# 增加运行速度设置

## Goal

将导航默认速度从 30 调整为 20，并在主界面右侧增加“运行速度”入口，让现场人员可以查看和修改速度。修改后的值保存到本地配置，不需要重启机器人或 App，下一次导航出发时即可使用新值。

## What I already know

* `MainActivity.prepareNavigationRoute()` 当前在调用 Peanut SDK `prepare()` 前读取单点或多点速度，并通过 `NavManager.setSpeed()` 传给 SDK。
* 当前默认值为 30，`NavManager` 的注释说明 SDK 速度参数范围为 20～100。
* 现有 `/robot_task/single_point_speed` 和 `/robot_task/multiple_point_speed` 接口分别保存单点、多点速度。
* 主界面右侧操作区使用 `activity_main.xml`，现有按钮监听集中在 `MainActivity.initListener()` 和 `onClick()`。
* 本次确认：设置速度后从下一次出发开始生效，当前正在运行的路线不动态改速。

## Assumptions

* “运行速度”按钮作为一个统一的现场设置入口，保存同一个速度值到单点和多点导航配置，保持现有两种导航路线都使用界面设置的值。
* 速度输入继续使用 Peanut SDK 当前约定的整数范围 20～100。
* 已经通过上游接口保存的速度仍可被读取；用户通过界面保存后，单点和多点配置统一为新值。

## Requirements

* 将默认导航速度改为 20。
* 在主界面右侧增加“运行速度”按钮，并显示当前保存的速度。
* 点击按钮后弹出设置界面，展示当前速度并允许输入新的整数速度。
* 拒绝空值、非数字和不在 20～100 范围内的输入，并给出明确提示。
* 保存成功后立即刷新按钮上的当前速度显示。
* 保存成功后不重启机器人、不重启 App；下一次导航路线准备时读取新值。
* 当前正在执行的导航不因设置按钮而停止、重建或改变速度。
* 保持现有导航、回充、仓位绑定、上游速度接口和日志行为不变，除统一保存界面输入外不扩大范围。

## Acceptance Criteria

* [ ] 新安装或没有速度配置时，界面显示速度 20，导航准备时单点和多点均使用 20。
* [ ] 右侧显示“运行速度”入口，并能看到当前速度。
* [ ] 输入 20～100 的整数并保存后，按钮立即显示新值。
* [ ] 保存后直接开始下一次导航，无需重启机器人或 App，日志中的路线准备速度为新值。
* [ ] 输入空值、非数字、19 或 101 时不能保存，并显示校验提示。
* [ ] 导航运行期间打开设置并保存，不调用停止、释放或重建导航；当前路线继续按原速度运行。
* [ ] `:app:testDebugUnitTest` 和 `:app:assembleDebug` 通过。

## Definition of Done

* UI、配置读取和导航速度使用链路完成并保持可读。
* 针对速度边界或配置选择逻辑补充有价值的单元覆盖，避免只测试界面控件存在。
* Lint、单元测试和 Debug 构建通过。
* README 或现场文档同步说明默认速度、范围和生效时机。
* 进度日志记录改动文件、验证结果和回滚方式。

## Out of Scope

* 不在当前导航途中动态调整底盘速度。
* 不新增速度滑块、速度曲线、按路线单独配置或速度单位换算。
* 不修改 Peanut SDK、底盘参数或上游接口协议。
* 不处理与本次运行速度无关的既有换行符差异和现场文件。

## Technical Notes

* 主要文件：`app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`、`app/src/main/res/layout/activity_main.xml`、`README.md`、`progress.md`。
* 复用现有 `MmkvUtils` 持久化和 `AlertDialog`/`EditText` 配置对话框模式。
* 速度配置的实际生效点是 `prepareNavigationRoute()` 中的 `NavManager.setSpeed()`，因此保存配置即可支持无需重启的下一次出发。
