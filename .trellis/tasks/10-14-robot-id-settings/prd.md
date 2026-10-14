# 增加机器人 ID 设置

## Goal

把仓库任务指令中写死的机器人 ID `3` 改为可在“设置”内修改，现场换机器人时不需要改代码重新打包。保存后无需重启机器人或 App 即可生效。

## What I already know

机器人 ID 目前是 `MainActivity.WAREHOUSE_TASK_ROBOT_ID = 3`，只用于发往上游 `ws://<上游IP>:9098` 的 3 种仓库任务指令的 `robot_id` 字段：

| 指令 | 触发位置 |
| --- | --- |
| 回充 | 主界面“回充”按钮、启动后自动回充 |
| 巡仓 | 主界面“巡仓”按钮 |
| 召回 | 主界面“召回”按钮、屏幕配送最后一个点位取餐完成或超时后自动召回 |

每次发送都会新建 JSON 和 WebSocket，因此发送时读取配置即可生效。

`WebServer` 中解析告警请求的 `robot_id` 只是接收示例，不是本机机器人 ID，不纳入本次修改。

## Requirements

* 在设置弹窗中增加“机器人 ID：<当前值>”入口。
* 点击后弹出设置框，显示当前 ID 和受影响的指令，允许输入新 ID。
* 只接受正整数（1-999999999），拒绝空值、0、负数、非数字和超长输入，并给出明确提示。
* 保存后回充、巡仓、召回（含自动回充和配送完成后的自动召回）的下一次发送使用新 ID，不需要重启。
* 提供“恢复默认”入口，回到 `3`。
* 未保存过 ID 时使用默认值 `3`；升级后保留已保存的值。
* 修改写入运行日志，模块为 `CONFIG`。

## Acceptance Criteria

* [ ] 未保存过时，设置中显示“机器人 ID：3”，3 种指令发送 `"robot_id": 3`。
* [ ] 输入合法 ID 保存后，设置入口显示新值，下一次回充/巡仓/召回发送新 ID。
* [ ] 输入空值、`0`、`-1`、`abc`、`1.5`、`1234567890` 时不能保存，并显示校验提示。
* [ ] 点击“恢复默认”后恢复为 `3` 并立即生效。
* [ ] `:app:testDebugUnitTest` 和 `:app:assembleDebug` 通过。

## Definition of Done

* 机器人 ID 的默认值、校验、解析和持久化集中在 `RobotIdConfig`，`MainActivity` 不再保留硬编码常量。
* 为解析与校验补充 JVM 单元测试。
* README、设置入口文档和新增的现场文档说明可配置 ID 与生效时机。
* `progress.md` 记录改动文件、验证结果和回滚方式。

## Out of Scope

* 不把 `task_id`、`robot_task_id` 等任务 ID 做成可配置。
* 不修改状态上报 WebSocket 字段（当前不含机器人 ID）。
* 不修改 `WebServer` 告警示例接口。
* 不处理仓库中既有的换行符差异文件和现场 JPG/TXT 文件。

## Technical Notes

* 新增 `util/RobotIdConfig`，参照 `UpstreamServerConfig` / `NavigationSpeedConfig`：纯逻辑的 `parseRobotId` / `isValidRobotId` 可单元测试，读写通过 `MmkvUtils`。
* 读取时若 MMKV 中的值非法，回退到默认值 `3`。
* 设置弹窗复用 `AlertDialog` + `EditText` 模式，与“上游地址”入口一致。
