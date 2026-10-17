# 锁屏显示配送中状态

## Goal

点击“立即出发”开始屏幕送餐后，如果锁屏出现，在锁屏之上显示“配送中...”，并让末尾的 3 个点循环跳动，让周围的人知道机器人正在配送。配送结束后不再显示。

## What I already know

* “立即出发”调用 `activateScreenCompartmentRoute()`，把 `screenCompartmentRouteActive` 设为 `true`。
* 配送结束有两条路径，都会把 `screenCompartmentRouteActive` 设为 `false`：
  * 最后一个点位取餐完成或超时：`completeArrivalWait()` 末点分支，随后发送召回。
  * 任务取消：`cancelScreenCompartmentRoute()`（新任务、上游路线抢占、停止、出发失败、Activity 销毁等）。
* 锁屏是 `activity_main.xml` 中覆盖全屏的 `fl_idle_lock`，由 `showIdleLock()` / `hideIdleLock()` 控制，60 秒无操作或点击“锁屏”时出现。
* 上游 `/robot_task/send_point` 路线和巡仓不是屏幕送餐，不在本次范围。

## Requirements

* 锁屏显示且屏幕送餐进行中时，在锁屏底部居中显示“配送中...”。
* 末尾的点按 `.` → `..` → `...` 每 500 毫秒循环；隐藏的点占位透明，文字不左右抖动。
* 配送进行中的整个过程都显示：前往点位时为“配送中...”，到点等待取餐时为“请取餐...”，取餐完成或超时后切回“配送中...”。
* 配送完成或取消后立即隐藏；锁屏解除后隐藏并停止动画。
* 急停按下期间隐藏提示，锁屏保留；松开后若配送仍在进行则恢复。急停不取消配送、不改变导航。急停状态变化写入 `ESTOP` 运行日志。
* 锁屏在配送开始前已出现、或配送中途才出现，都能正确显示。
* Activity 暂停或销毁时停止动画，恢复时按当前状态重新显示。
* 不改变锁屏触发时机、解锁方式、屏保图片和配送流程。

## Acceptance Criteria

* [ ] 点击“立即出发”后锁屏出现时，锁屏底部显示“配送中”，点在 1-3 个之间循环。
* [ ] 到点等待取餐期间锁屏显示“请取餐...”，前往下一个点位后切回“配送中...”。
* [ ] 最后一个点位取餐完成或超时后，锁屏上的“配送中...”消失。
* [ ] 停止任务、上游路线抢占或出发失败后，锁屏上的“配送中...”消失。
* [ ] 锁屏显示提示时按下急停，提示消失、锁屏保留；松开急停后提示恢复。
* [ ] 没有屏幕送餐时，锁屏与原来一致，不显示该文字。
* [ ] `:app:testDebugUnitTest` 和 `:app:assembleDebug` 通过。

## Out of Scope

* 不为上游路线、巡仓、回充、召回显示配送状态。
* 不在锁屏上显示具体点位、仓位或倒计时。
* 不修改锁屏触发时间和解锁密码逻辑。
* 急停时不自动解锁、不取消配送。

## Technical Notes

* 在 `fl_idle_lock` 中新增 `tv_idle_delivery_status`，样式参照现有 `tv_idle_unlock_hint`。
* `MainActivity` 统一用 `refreshIdleDeliveryStatus()` 根据 `idleLocked && screenCompartmentRouteActive` 显示或隐藏，并启动或停止点动画。
* 在锁屏显示/隐藏、屏幕送餐开始/完成/取消、`onResume`/`onPause`/`onDestroy` 处调用或清理。
* 急停状态来自 `PeanutRuntime.Listener.onEvent` 的 `PeanutEvent.EVENT_EMERGENCY_STATUS_CHANGED`（SDK 订阅 `ButtonStatusApi`，变化时在主线程回调 `Boolean`）。按 SDK 字段名 `emergencyOpen`，`true` 视为急停按下，需现场确认。
