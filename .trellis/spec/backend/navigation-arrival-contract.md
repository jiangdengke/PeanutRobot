# Navigation Arrival and Pickup Contract

## Scenario: Screen Compartment Delivery Arrival

### 1. Scope / Trigger

Use this contract when changing any of the following:

- Screen-selected compartment delivery navigation.
- Arrival reporting to the upstream system.
- The local pickup-completion HTTP endpoint.
- Navigation callback forwarding or stale-event protection.
- Task preemption, timeout handling, or Activity teardown during delivery.

This workflow applies only to screen compartment routes. Routes received through
`/robot_task/send_point` keep their existing full-route navigation behavior.

### 2. Signatures

Arrival report sent by the robot App:

```http
POST http://192.168.112.194:9088/nav_arrive
Content-Type: application/json
```

Pickup completion received by the robot App at `192.168.112.89`:

```http
POST /pickup_status HTTP/1.1
Host: 192.168.112.89:9088
Content-Type: application/json
```

Navigation callbacks that can affect route progression must carry their SDK
session generation:

```java
void onSessionStateChanged(int sessionGeneration, int state, int schedule);
void onSessionRoutePrepared(int sessionGeneration, RouteNode... routeNodes);
void onSessionError(int sessionGeneration, int code);
```

### 3. Contracts

`/nav_arrive` request body:

```json
{
  "point_name": "Restaurant Left Bottom",
  "bay": 1
}
```

- `point_name`: required string from the current route node.
- `bay`: required integer in the inclusive range `1..3`, read from the immutable
  point-to-compartment snapshot created at departure.
- The response body and status are diagnostic only. They must not advance or
  cancel navigation.

`/pickup_status` request body:

```json
{
  "pickup_status": true
}
```

- `pickup_status` is required and must be the JSON boolean `true`.
- Do not add point, bay, task, or generation fields unless the upstream contract
  is explicitly revised.
- A valid pickup can consume only the currently active wait generation once.
- Pickup and the local five-minute timeout converge on the same completion path.
- Completion advances an intermediate point and recalls after the final point.

Screen route navigation contract:

- Reuse the SDK navigation instance created during robot-core initialization.
  Do not release and rebuild `PeanutNavigation` at departure or between points;
  this robot SDK can fail to emit route-prepared after immediate reconstruction.
- Submit the complete screen route once. Pause at each destination, then call
  `pilotNext()` and `setPilotWhenReady(true)` after pickup or timeout.
- Accept `STATE_DESTINATION` only after the current SDK session has emitted
  `STATE_RUNNING` for the expected route position.
- Reject callbacks whose session generation or task generation is no longer
  active.
- If route preparation does not complete within ten seconds, invalidate the
  task, stop navigation, restore the departure action, and show a retry prompt.

Screen departure charger handoff contract:

- If charging control is already inactive, keep the direct screen-route
  `prepareNav()` path.
- If charging or an upstream charge task is active, immediately invalidate and
  stop any previous SDK navigation before waiting for charger release.
- Retain the screen route under a monotonically increasing handoff generation,
  send `CHARGE_ACTION_STOP`, and do not prepare the route until charger status
  `1` or `6` confirms release.
- Charger status, error, and timeout work posted to the main thread must carry
  the generation captured by the originating handoff. Work from a cancelled
  generation cannot consume or fail a replacement route.
- The first matching release consumes the retained route exactly once. Repeated
  release callbacks cannot prepare the route again.
- Charger absence, stop-action failure, charger error, or a ten-second release
  timeout cancels the screen task, restores the departure action, and prompts a
  retry without forcing navigation.
- Task preemption and Activity teardown clear the retained route and timeout so
  delayed callbacks cannot start navigation later.

Upstream route preemption contract:

- Parse and validate the complete `/robot_task/send_point` route before
  cancelling a screen wait.
- Reject malformed JSON, null or empty lists, and lists containing null nodes.
- Submit a valid upstream route to one SDK session as the complete route; do not
  split it into screen-style single-node legs.

### 4. Validation & Error Matrix

| Condition | Required result |
|---|---|
| Path is not `/pickup_status` | `404` JSON response; do not invoke callback |
| Method is not `POST` | `405` JSON response with `Allow: POST` |
| Content-Type is missing or not `application/json` | `415` JSON response |
| Body is unreadable, empty, or malformed JSON | `400` JSON response |
| `pickup_status` is missing or is not a JSON boolean | `400` JSON response |
| `pickup_status` is `false` | `400` JSON response |
| No active arrival wait exists | `409` JSON response |
| Valid `pickup_status: true` claims the active wait | `200` JSON response and exactly one completion |
| Duplicate pickup or timeout loses the claim race | No route progression |
| Arrival HTTP call belongs to a cancelled wait generation | Cancel or skip the call |
| Navigation callback carries a stale session generation | Ignore before reading or advancing route state |
| Route preparation does not complete within ten seconds | Cancel the task, restore editing/departure controls, and prompt retry |
| Screen departure begins while charger control is active | Stop prior navigation, request charger stop, and defer route preparation |
| Charger status is neither `1` nor `6` during a pending handoff | Keep waiting without preparing navigation |
| Matching charger release is reported more than once | Prepare the retained route exactly once |
| Charger release/error callback belongs to a cancelled handoff generation | Ignore it without changing the replacement handoff |
| Charger release is not confirmed within ten seconds | Cancel the screen task, restore departure, and prompt retry |
| Activity is destroyed before queued endpoint work runs | Return without touching UI or SDK objects |
| Incoming upstream route is invalid | Keep the current screen route and wait unchanged |

### 5. Good / Base / Bad Cases

- Good: Point A arrives in bay 2, `/nav_arrive` reports `bay: 2`, pickup is
  accepted once, and `pilotNext()` starts point B on the prepared route.
- Base: No pickup arrives. The five-minute timeout claims the wait and performs
  the same next-point or final-recall decision.
- Bad: A destination callback arrives before `STATE_RUNNING` arms the expected
  route position. It is ignored and cannot report arrival or advance the route.
- Bad: Departure releases the initialized navigation object and immediately
  rebuilds it. Route preparation never completes, so the robot does not move.
- Bad: `/robot_task/send_point` contains `[null]`. Validation rejects it before
  cancelling the active screen wait.
- Protocol limitation: A delayed boolean-only pickup from point A that arrives
  while point B is already waiting cannot be distinguished locally from a valid
  pickup for point B.

### 6. Tests Required

Static and build checks:

- Assert the arrival URL, POST method, JSON media type, `point_name`, and `bay`.
- Assert invalid pickup method, path, media type, JSON, field type, and inactive
  wait never invoke route completion.
- Assert pickup and timeout racing for one wait generation produce one completion.
- Assert task cancellation atomically detaches the pending arrival HTTP call.
- Assert stale task/session callbacks and unarmed destination callbacks cannot
  complete a route position.
- Assert route preparation timeout restores the departure action and cancels
  the active screen route.
- Assert repeated charger release consumes one retained screen route once.
- Assert a cancelled charger handoff generation cannot consume or clear its
  replacement generation.
- Assert charger release timeout and current-generation failure clear the
  retained route without forcing navigation.
- Assert malformed or null-containing upstream routes do not preempt a valid wait.
- Run `:app:testDebugUnitTest` and `:app:assembleDebug`.

Robot integration checks:

- Run a two- or three-point screen delivery and verify bay values and binding order.
- Verify pickup and five-minute timeout at an intermediate point both start the
  next route point through `pilotNext()`.
- Verify pickup and timeout at the final point both send the existing recall task.
- Preempt an active wait with a valid upstream route and verify the old timeout,
  pickup completion, arrival call, and SDK callbacks cannot advance the new route.
- Destroy and recreate the Activity and verify port `9088` binds to the new instance.
- While charger status is `4`, start a screen route and verify the event order is
  navigation stop, `CHARGE_ACTION_STOP`, status `6` or `1`, then route preparation.
- Cancel one pending charger handoff, start another, and verify delayed callbacks
  from the first handoff cannot start or cancel the second route.

### 7. Wrong vs Correct

#### Wrong

```java
navigation.release();
navigation = new PeanutNavigation.Builder().build();
navigation.setTargets(currentPointOnly);
navigation.prepare();
```

Releasing and immediately rebuilding the SDK object at departure regressed the
working start path on the target robot: route preparation did not complete and
`setPilotWhenReady(true)` was never called.

#### Correct

```java
navigation.stop();
navigation.setTargets(completeRouteSnapshot);
navigation.prepare();

// After an intermediate pickup or timeout:
navigation.pilotNext();
navigation.setPilotWhenReady(true);
```

The initialized SDK object is reused, matching the start path verified in
`v1.0.12-beta.10`. Task/session tokens and the expected-position
`STATE_RUNNING` gate remain responsible for rejecting invalid progression.
