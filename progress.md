## 2026-06-22 - Task: 添加房间点位对照图预览
### What was done
- 在主界面左下角添加“展示图片”按钮，点击后显示房间点位对照图预览层。
- 预览层提供右上角 `X` 关闭入口，图片按居中适配方式展示。
- 预留图片资源名 `room_map`，未放置图片时提示应放到指定路径。
- 补充房间点位对照图图片放置和使用说明。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md"`：通过。
- `./gradlew :app:assembleDebug`：BUILD SUCCESSFUL。

### Notes
- `app/src/main/res/layout/activity_main.xml`：新增左下角“展示图片”按钮和全屏图片预览遮罩。
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：接入展示/关闭点位图逻辑，并在缺少图片资源时提示放置路径。
- `docs/room-map-preview.md`：记录图片应放置在 `app/src/main/res/drawable/room_map.png` 以及使用方式。
- `progress.md`：追加本轮任务记录。
- 回滚方式：执行 `git restore app/src/main/res/layout/activity_main.xml app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java docs/room-map-preview.md progress.md`，如只想撤销本轮记录可单独删除本节。

## 2026-06-22 - Task: 发布房间点位对照图预览预发布版
### What was done
- 将房间点位对照图图片资源纳入 App 打包。
- 更新应用版本到 `1.0.12-beta.4`，用于本次预发布。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/build.gradle" "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug`：BUILD SUCCESSFUL。

### Notes
- `app/build.gradle`：更新版本号到 `1.0.12-beta.4`，`versionCode` 更新到 `15`。
- `app/src/main/res/drawable/room_map.png`：新增房间点位对照图资源。
- `progress.md`：追加本轮发版记录。
- 回滚方式：执行 `git restore app/build.gradle progress.md && git rm -- app/src/main/res/drawable/room_map.png`，如已提交则使用 `git revert <commit>` 回滚。
