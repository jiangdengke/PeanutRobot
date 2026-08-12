# Hide Runtime Log Button

## Goal

Temporarily remove the operator-facing runtime log entry from the main screen
without deleting or disabling diagnostic recording, persistence, viewing,
copying, clearing, or export implementation.

## Requirements

- Hide the `tv_diagnostic_logs` view from the runtime layout.
- Keep diagnostic recording and private rotating storage active.
- Keep the existing dialog and export code intact for later restoration.
- Do not change navigation, charging, warehouse, arrival, pickup, HTTP, or
  WebSocket behavior.
- Document that the operator entry is temporarily hidden.

## Acceptance Criteria

- The main screen does not display the "运行日志" button.
- The hidden view does not consume layout space.
- Diagnostic logging code still compiles and runs unchanged.
- `:app:assembleDebug` succeeds.

## Out of Scope

- Deleting diagnostic logs or their UI implementation.
- Adding another hidden gesture or password entry.
- Publishing a new application version.
