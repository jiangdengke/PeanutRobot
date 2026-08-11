# Fix Screen Departure After Charging

## Background

Target-robot diagnostics show that a screen-selected delivery can remain blocked
after charging because the screen departure path prepares navigation without
first releasing `PeanutCharger` control. The upstream `send_point` path sends
`CHARGE_ACTION_STOP` and subsequently receives charger status `6` / `1` before
navigation enters `STATE_RUNNING`.

## Goal

Make screen "立即出发" hand off control from the charger to navigation before
preparing the selected route.

## Requirements

1. Build and activate the existing immutable screen compartment route as today.
2. If `isCharging` or `upstreamChargeTaskActive` is true:
   - invalidate and stop any previous SDK navigation immediately;
   - retain a private snapshot of the selected route;
   - assign a monotonically increasing handoff generation;
   - send `PeanutCharger.CHARGE_ACTION_STOP`;
   - do not call `prepareNav()` yet;
   - start a bounded handoff timeout.
3. Continue the retained route only after charger status `1` or `6` is received.
4. If the charger is unavailable, reports an error, or does not release before
   the timeout:
   - clear the pending route and timeout;
   - cancel the screen compartment task;
   - restore the departure button;
   - show a retryable failure message;
   - do not force navigation.
5. Existing task preemption and Activity destruction must clear the pending
   departure so a stale charger callback cannot start navigation later.
6. Timeout, status, and error work must carry the generation captured for its
   handoff so queued work from a cancelled handoff cannot affect a replacement.
7. Record request, status confirmation, continuation, cancellation, and failure
   events in the bounded diagnostic log.

## Non-goals

- Do not add retries for navigation SDK state `6`.
- Do not rebuild the `PeanutNavigation` session.
- Do not change upstream `send_point`, arrival, pickup, warehouse, HTTP, or
  WebSocket contracts.
- Do not change navigation speed or route preparation behavior after handoff.

## Acceptance Criteria

- A screen departure while charging sends `CHARGE_ACTION_STOP` and does not call
  `prepareNav()` until charger status `1` or `6` arrives.
- Repeated release statuses start the retained route at most once.
- A cancelled handoff generation cannot consume or fail a later handoff.
- Timeout, charger error, manual recall, manual charge, patrol, upstream task
  preemption, and Activity destruction cannot leave a stale pending departure.
- A screen departure when charger control is already inactive keeps the current
  immediate `prepareNav()` behavior.
- `:app:testDebugUnitTest` and `:app:assembleDebug` pass.

## Robot Verification

1. While charger status is `4`, select a point and tap "立即出发".
2. Verify logs show stop request, status `6` or `1`, then route preparation.
3. Verify the robot enters navigation and the departure is not started twice.
4. Repeat with the same point after returning to charge.
5. Verify timeout/error restores the UI without forcing navigation.
