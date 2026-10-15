# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A single-module Android app (mostly Java, JDK 17, AGP 7.4.2, Gradle 8.0) that runs on a Keenon (擎朗) delivery robot and drives it through the Peanut SDK (`app/libs/peanut-sdk-release.aar`). Code namespace is `com.yuandaima.peanutrobot`, but the `applicationId` is `com.keenon.zealq`. The SDK authorization is bound to that package, the signature and the `appId`/`secret` in `MainActivity.initSDK()`, so don't change any of them without confirmation.

Most real behavior (SDK init, navigation, charging, secondary display) only runs on an authorized robot. You can't verify it on an emulator. For any change you can only run unit tests and `assembleDebug`. When you report, say which on-robot behavior is still unverified.

## Commands

```bash
./gradlew :app:assembleDebug                      # build → app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:testDebugUnitTest                  # JVM unit tests (app/src/test)
./gradlew :app:testDebugUnitTest --tests "com.yuandaima.peanutrobot.util.UpstreamServerConfigTest"   # single class
./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon    # standard pre-commit check used in progress.md
./gradlew :app:assembleDebug --no-daemon --refresh-dependencies    # before release, to catch repo resolution issues
git diff --check -- <changed files>               # whitespace check run on every change
```

There is no lint or formatter config. The instrumented test (`androidTest`) is only the template placeholder.

## Architecture

**`MainActivity` (~4.8k lines) is the app.** It holds SDK init, every UI surface (point list, 3-bay compartment binding, map point overlay and editor, unified settings dialog, idle lock screen, diagnostic log dialog), the navigation state machine, charger handling, warehouse-task WebSocket calls and the routing of inbound HTTP commands. `MainActivity2`, the camera fragments/activities, `USBCameraManager` and `WebSocketManager2` are legacy or dormant code (their wiring is commented out). Don't extend them.

Startup: `MyApplication` initializes `DiagnosticLogRecorder`, MMKV, utilcode and the crash handler. `MainActivity.startRobotCore()` then runs `initSubscribe` → `initSDK` → TTS → `initWebSocket` (which starts both local HTTP servers and binds `WebSocketService`) → media player → charger.

**Network surfaces.** The README has the full `/robot_task/*` table and payloads.

| Direction | Port | Class | Purpose |
|---|---|---|---|
| inbound | 9095 | `server/WebServer` (NanoHTTPD) | `/robot_task/*` commands from upstream, forwarded to `MainActivity` via `WebCallback` |
| inbound | 9088 | `server/PickupStatusServer` | `/pickup_status` pickup confirmation during an arrival wait |
| outbound | 9088 | `MainActivity.sendArrivalReport` | `POST /nav_arrive` (`point_name`, `bay`) |
| outbound | 9089 | `MainActivity` (screen-route arrival) | plays `delivery.wav` as the arrival voice |
| outbound | 9096 | `server/WebSocketService` | periodic robot status report |
| outbound | 9098 | `MainActivity.sendWarehouseTask` | 回充 / 巡仓 / 召回 warehouse task buttons |

Build every outbound address through `util/UpstreamServerConfig`. It reads the upstream IP from MMKV on each call, so changes made in Settings apply without a restart. Ports are fixed. Never hard-code the IP.

**Navigation.** `manager/NavManager` is a singleton that wraps `PeanutNavigation`. Each SDK session gets a generation number, and callbacks from old sessions are dropped. `MainActivity` adds its own task, arrival-wait and charger-handoff generations on top of that. There are two route modes:
- **Screen compartment route** (points picked in the UI and bound to bays 1–3): the whole route is submitted once and pauses at each destination. On arrival the app reports `/nav_arrive` and waits for `/pickup_status` or a 5-minute timeout, then calls `pilotNext()`. After the last point it sends the recall task.
- **Upstream route** (`/robot_task/send_point`): the full route is validated first, then submitted as one session. It may preempt a screen wait. A 巡仓 started from the local button adds a 60-second wait for the next route, then the robot returns to point ID 2.

Hard-won invariants from `.trellis/spec/backend/navigation-arrival-contract.md` (read it before touching navigation, arrival, pickup or charger code):
- Reuse the initialized `PeanutNavigation`. Releasing and rebuilding it at departure stops route-prepared from firing, and the robot doesn't move.
- Accept `STATE_DESTINATION` only after `STATE_RUNNING` for the expected route position. Ignore stale-generation callbacks before reading any state.
- Departure while charging: stop navigation, send `CHARGE_ACTION_STOP`, and wait for charger status `1` or `6` (10 s timeout) before preparing the route. Route preparation also has a 10 s timeout.

**Diagnostics.** Use `DiagnosticLogRecorder.info/warn/error(module, msg)` for events operators need to see. It writes to a bounded in-memory buffer plus rotating files on one background writer. Never do disk I/O on SDK callback threads, and never log secrets or passwords. Export rules are in `.trellis/spec/backend/logging-guidelines.md`.

**Persistence and testability.** All settings live in MMKV via `util/MmkvUtils`: speed (20–100, default 20), upstream host, warehouse-task robot ID (`RobotIdConfig`, default 3), idle-lock config, map point config and the 进入密码 (default `123456`). The Settings entry password (`0`, `SettingsAccessConfig`) is a separate password. Logic that can be tested is pulled out of `MainActivity` into pure-Java classes (`util/*Config`, `*Parser`, `*Sanitizer`, `DiagnosticLogBuffer`, `RotatingLogFileStore`, `ScreenDepartureHandoffState`, `bean/MapPointConfig`) with JVM tests. Follow that pattern for new logic. Android, SDK and MediaStore behavior is checked by compiling and by acceptance on the robot.

## Repository conventions

- **Trellis workflow** (`AGENTS.md`, `.trellis/workflow.md`): code changes normally go through a task created with `python3 ./.trellis/scripts/task.py create "<title>"` (PRD plus jsonl context under `.trellis/tasks/`). Skip this only when the user explicitly opts out (e.g. "skip trellis" / "直接改" / "小修一下"). Specs under `.trellis/spec/backend/` are mostly empty templates. The two filled ones are linked above.
- **Every task** also updates the matching `docs/<feature>.md` (Chinese) and appends a `## YYYY-MM-DD - Task: …` entry to `progress.md` with *What was done / Testing / Notes*, including rollback steps and what still needs on-robot verification. `plan.md` and `task.md` are stale snapshots from 2026-06-01.
- **Commits** use a gitmoji plus a conventional type and a Chinese subject: `:sparkles: feat(scope): …`, `:bug: fix(scope): …`, `:recycle: refactor`, `:lipstick:`.
- **Releases**:
  1. Bump `versionCode` (+1) and `versionName` (e.g. `1.0.12-beta.N`) in `app/build.gradle` and add a `progress.md` entry.
  2. Commit as `:bookmark: chore(release): 发布 vX`.
  3. Push an annotated tag `vX` whose message is `发布 vX`, a blank line, then bullet notes. `.github/workflows/android-release.yml` drops that first line and publishes the rest as release notes with a debug APK `PeanutRobot-<tag>-debug.apk`. Tags with `-alpha`/`-beta`/`-rc` are marked prerelease.
- **Line endings**: there is no `.gitattributes`. 45 files are committed with CRLF, and `MmkvUtils.java` has mixed endings. When editing, keep each file's existing line endings. If `git status` shows a file as modified but `git diff --ignore-cr-at-eol` is empty, only its line endings changed. Don't commit that.
- **On-site files**: screenshots (`/*.jpg`) and exported runtime logs (`/robot-runtime-*.txt`) in the repo root are gitignored and stay local. Don't stage, move or delete them.
- **Gradle repositories** in the root `build.gradle` use `content { includeGroupByRegex … }` filters, with official repos first and Aliyun mirrors as fallback. A dependency from a new group may need its filter updated, or CI resolution breaks (see `docs/android-release-build.md`).
