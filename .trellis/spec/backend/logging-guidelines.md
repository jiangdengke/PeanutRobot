# Logging Guidelines

## Scenario: In-App Robot Diagnostic Timeline

### 1. Scope / Trigger

Use this contract when adding logs intended for the operator-facing runtime log
window, especially around navigation, charging, upstream tasks, arrival waiting,
pickup completion, and Activity lifecycle events.

Ordinary development-only `android.util.Log` calls can remain separate. Do not
bulk-replace every Logcat call with diagnostic persistence.

### 2. Signatures

Initialize once from `Application.onCreate()`:

```java
DiagnosticLogRecorder.initialize(applicationContext);
```

Record business events without using their return values for control flow:

```java
DiagnosticLogRecorder.debug("NAV", message);
DiagnosticLogRecorder.info("CHARGER", message);
DiagnosticLogRecorder.warn("HTTP", message);
DiagnosticLogRecorder.error("PICKUP", message);
```

The operator UI may read or clear the retained snapshot:

```java
String snapshot = DiagnosticLogRecorder.snapshot();
DiagnosticLogRecorder.clear(success -> updateClearResult(success));
```

Manual file export must consume one immutable snapshot captured by the UI at
the moment the operator taps export:

```java
String snapshotToExport = DiagnosticLogRecorder.snapshot();
DiagnosticLogExporter.export(context, snapshotToExport, result -> showResult(result));
```

### 3. Contracts

- Each event is one line: `yyyy-MM-dd HH:mm:ss.SSS | LEVEL | MODULE | message`.
- Newlines in fields or messages must be escaped so one event cannot create
  ambiguous extra records.
- The in-memory snapshot is newest-retained data with a 256 KiB cap.
- Persistent logs live under private `files/diagnostic_logs/` storage.
- Keep at most four 256 KiB files, read oldest file first, and rotate out the
  oldest file when the current file reaches its cap.
- Submit persistence to one background writer with a bounded queue. Queue
  saturation drops file operations rather than blocking robot callback threads.
- Clearing empties memory immediately, discards older queued appends, then
  deletes files ahead of any later append. Report persistent deletion success
  through the callback.
- Storage failures are diagnostic-only: write them to Logcat and never throw
  them into navigation, charging, HTTP, or pickup behavior.
- Exclude the diagnostic directory from Android cloud backup and device transfer.
- Continue mirroring diagnostic events to Logcat for adb correlation.
- The manual export source is exactly the captured `snapshot()` string. Encode
  that string as UTF-8 without a header, trailing newline, Logcat merge, or a
  second recorder read after permission handling.
- Do not create an export for an empty snapshot. Name exports like
  `robot-runtime-yyyyMMdd-HHmmss-SSS.txt` and report the resulting path.
- On API 29+, insert into `MediaStore.Downloads` with
  `RELATIVE_PATH=Download/PeanutRobotLogs` and `IS_PENDING=1`, write the complete
  snapshot, then publish with `IS_PENDING=0`. Do not request storage permission.
- On API 28 and below, write under the public
  `Downloads/PeanutRobotLogs/` directory. Request `WRITE_EXTERNAL_STORAGE` only
  after the export tap, with a request code separate from robot-core startup.
  Retain the captured immutable snapshot until the permission result; clear it
  on denial.
- Run exports on a dedicated bounded single-thread executor. Queue saturation
  must fail immediately without blocking the main thread or consuming recorder
  persistence capacity.
- Disable the active export button while permission or I/O is pending. The
  permission callback must still be able to continue without a live button
  reference if the dialog was dismissed.
- Catch `IOException`, `RuntimeException`, and `SecurityException`, delete an
  incomplete MediaStore entry or legacy partial file, and return a result for
  main-thread operator feedback. Export failures must not reach robot control.

### 4. Validation & Error Matrix

| Condition | Required result |
|---|---|
| Recorder is not initialized | Logcat may still receive the event; robot behavior must continue |
| Message contains CR/LF | Escape it as literal `\\n` in the diagnostic line |
| One event exceeds the entry limit | UTF-8 truncate without leaving an unmatched surrogate |
| Memory cap is reached | Evict oldest lines before adding the newest line |
| Current file cap is reached | Rotate files before writing the next line |
| Writer queue is full | Keep the memory event, drop persistence work, and do not block the caller |
| Directory creation/read/write/delete fails | Log the failure to Logcat; do not propagate it to business code |
| Clear races with older queued appends | Remove older pending appends and execute deletion before later appends |
| Clipboard cannot accept the snapshot | Catch the runtime failure and show an operator error |
| Export snapshot is empty | Show an operator message and do not create a file or request permission |
| API 29+ export is requested | Use MediaStore pending publication and do not request storage permission |
| API 28 or below lacks write permission | Preserve the tap-time snapshot, request only `WRITE_EXTERNAL_STORAGE` with the export request code, then export that same snapshot after grant |
| Legacy export permission is denied | Clear the pending snapshot, restore a live button if available, show a message, and do not enter the robot-core permission branch |
| Export queue is full | Reject immediately, restore the button, and show a retry message without blocking the caller |
| MediaStore insert, stream, write, or publish fails | Delete the pending entry, report failure, and never expose a partial published file |
| Legacy directory or write fails | Delete any newly created partial file and report failure |
| Export dialog closes while permission is pending | Release the button reference but retain and export the captured snapshot after grant |

### 5. Good / Base / Bad Cases

- Good: `STATE_RUNNING`, `STATE_DESTINATION`, `readyGo(false)`, and arrival-wait
  start are visible in order, making an early SDK destination callback diagnosable.
- Base: The App restarts and reloads the newest retained file history before
  adding the new process initialization event.
- Bad: A callback performs `FileOutputStream.write()` directly and delays the SDK
  thread.
- Bad: An unbounded executor queue captures every event while storage is slow and
  grows until the process experiences memory pressure.
- Bad: A log includes SDK secrets, the lock-screen password, authentication
  headers, or complete request bodies that are not required for diagnosis.

### 6. Tests Required

- Assert timestamp, level, module, and message formatting stays on one line.
- Assert memory capacity evicts oldest records and clear returns an empty snapshot.
- Assert concurrent buffer writes do not lose or corrupt retained lines.
- Assert UTF-8 truncation respects byte limits and does not split surrogate pairs.
- Assert a fresh file-store instance reloads retained lines in chronological order.
- Assert rotation keeps only the configured newest files and oversized entries
  cannot exceed one file's limit.
- Assert invalid storage paths report I/O failure at the storage boundary; the
  Android recorder must catch that failure.
- Assert the pure-Java export writer produces byte-for-byte UTF-8 for Unicode
  and ordinary snapshots, writes zero bytes for an empty string, and never adds
  a header or trailing newline.
- Assert generated export names match
  `robot-runtime-yyyyMMdd-HHmmss-SSS.txt` without depending on Android runtime.
- Keep MediaStore and runtime-permission behavior out of fragile local JVM
  tests; verify those Android boundaries through compilation, static review,
  and target-device acceptance.
- Run `:app:testDebugUnitTest` and `:app:assembleDebug` after integration changes.

### 7. Wrong vs Correct

#### Wrong

```java
// SDK callback thread performs disk I/O and can block robot events.
try (FileOutputStream output = new FileOutputStream(logFile, true)) {
    output.write(line.getBytes(StandardCharsets.UTF_8));
}
```

#### Correct

```java
// The recorder updates a bounded in-memory snapshot and schedules bounded,
// single-threaded persistence without affecting the callback decision.
DiagnosticLogRecorder.info(
        "NAV",
        "accepted STATE_RUNNING task=" + taskGeneration + ", position=" + position
);
```

For operator export, capturing after a permission dialog is wrong because the
file can silently include later events. Capture first, retain the immutable
string across permission handling, and pass that exact value to the background
exporter.
