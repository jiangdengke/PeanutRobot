# Patrol arrival timeout return

## Goal

Prevent a patrol started from this App from remaining indefinitely at a waypoint when the upstream dispatcher does not provide the next route.

## Confirmed behavior and scope

- The App sends the patrol task to upstream port 9098. A captured patrol trace shows a one-point `/robot_task/send_point` route dispatched back to the App.
- At the end of an upstream route, the App reports `send_point已完成` through the status WebSocket on port 9096.
- The user confirmed the food pickup / departure point has robot map ID 2 and that the fallback must use local Peanut navigation, not the upstream recall WebSocket.
- Only patrol initiated by the on-screen patrol button is eligible; screen delivery and other upstream routes keep their current behavior.

## Acceptance criteria

- After the last point of an eligible patrol route (except point ID 2), wait 60 seconds for a new valid `/robot_task/send_point` route.
- A valid new route cancels the previous timer and executes normally. Invalid routes do not reset the wait.
- If no route arrives by the deadline, navigate directly to point ID 2 using the existing SDK navigation session; do not send an upstream recall command.
- Manual route departure, charge, recall, upstream stop/charge, navigation error and Activity destruction prevent a stale timer from starting another navigation task.
- Log wait start, cancellation, and fallback for field diagnostics.

## Constraints and limitations

- The upstream `send_point` protocol has no patrol task identifier. Local patrol-button initiation is the available scope marker; unrelated valid upstream routes arriving while this marker is active can only be treated as the next instruction.
- Port 9096 already attempts reconnection after failures. Lack of the next route, not the state of a single socket, is the fallback trigger.
- Device behavior needs validation on the robot; unit/build checks cannot prove its movement.
