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

## 2026-06-22 - Task: 添加房间点位图旋转
### What was done
- 在房间点位对照图预览层新增“旋转”按钮。
- 点击“旋转”会将图片顺时针旋转 90 度，并保存当前角度，方便现场调整图片方向。
- 同步更新房间点位对照图使用说明。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md"`：通过。
- `./gradlew :app:assembleDebug`：BUILD SUCCESSFUL。

### Notes
- `app/src/main/res/layout/activity_main.xml`：在点位图预览层新增“旋转”按钮。
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：新增点位图旋转、角度归一化和角度持久化逻辑。
- `docs/room-map-preview.md`：补充旋转按钮使用说明。
- `progress.md`：追加本轮任务记录。
- 回滚方式：执行 `git restore app/src/main/res/layout/activity_main.xml app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java docs/room-map-preview.md progress.md`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-06-22 - Task: 发布房间点位图旋转预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.5`，用于发布房间点位图旋转功能。
- 准备将旋转按钮、角度保存和使用说明纳入本次预发布。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/build.gradle" "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug`：BUILD SUCCESSFUL。

### Notes
- `app/build.gradle`：更新版本号到 `1.0.12-beta.5`，`versionCode` 更新到 `16`。
- `progress.md`：追加本轮发版记录。
- 回滚方式：执行 `git restore app/build.gradle progress.md`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-06-22 - Task: 写死单点送餐到位语音地址
### What was done
- 将屏幕触发单点送餐到位后的默认播报音频固定为 `http://192.168.112.194:9089/delivery.wav`。
- 到位后不再依赖提前调用 `/robot_task/save_voice_address` 保存语音地址。
- 补充单点送餐到位语音说明文档。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/build.gradle" "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "docs/delivery-voice.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：新增默认到位播报音频地址，并在屏幕单点任务到达后直接播放该地址。
- `docs/delivery-voice.md`：记录默认语音地址、触发条件和注意事项。
- `progress.md`：追加本轮任务记录。
- 回滚方式：执行 `git restore app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java progress.md && rm -f docs/delivery-voice.md`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-06-22 - Task: 发布单点送餐到位语音预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.6`，用于发布单点送餐到位默认语音功能。
- 准备将固定到位语音地址和说明文档纳入本次预发布。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/build.gradle" "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "docs/delivery-voice.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。

### Notes
- `app/build.gradle`：更新版本号到 `1.0.12-beta.6`，`versionCode` 更新到 `17`。
- `progress.md`：追加本轮发版记录。
- 回滚方式：执行 `git restore app/build.gradle progress.md`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-07-05 - Task: 修复 Android Release 依赖源过滤并发布预发布版
### What was done
- 修复 Android Release 在 CI 中解析 Android Gradle Plugin 传递依赖失败的问题。
- 允许 `com.google.testing.platform` 从 Google Maven 仓库解析，避免 `core-proto` 在配置阶段找不到。
- 更新应用版本到 `1.0.12-beta.7`，用于重新触发预发布构建。
- 补充 Android 发布构建依赖源过滤说明。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "build.gradle" "app/build.gradle" "docs/android-release-build.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug --no-daemon --refresh-dependencies`：BUILD SUCCESSFUL。

### Notes
- `build.gradle`：补充 Google Maven 仓库 content 过滤规则，允许 `com.google.testing.platform` 依赖组。
- `app/build.gradle`：更新版本号到 `1.0.12-beta.7`，`versionCode` 更新到 `18`。
- `docs/android-release-build.md`：记录发布构建仓库过滤规则和刷新依赖验证方式。
- `progress.md`：追加本轮 CI 修复和发版记录。
- 回滚方式：执行 `git restore build.gradle app/build.gradle progress.md && rm -f docs/android-release-build.md`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-07-14 - Task: 绘制室内地形底图草稿
### What was done
- 根据机器人地图截图手工描绘简化室内地形底图，保留主要房间、通道和桌椅区域的相对布局。
- 移除蓝色网格、红色扫描噪点和截图界面元素，为后续叠加可点击点位按钮提供干净底图。
- 同时提供可继续编辑的 SVG 源文件和便于预览的 PNG 图片。

### Testing
- `xmllint --noout "docs/room-map-draft.svg"`：通过，SVG 语法有效。
- `qlmanage -t -s 1600 -o "docs" "docs/room-map-draft.svg"`：成功生成 PNG 预览。
- `ReadLints`：未发现新增 IDE 诊断。

### Notes
- `docs/room-map-draft.svg`：新增可编辑的室内地形矢量草稿。
- `docs/room-map-draft.png`：新增地形草稿 PNG 预览图。
- `progress.md`：追加本轮地图草稿交付记录。
- 回滚方式：执行 `git restore progress.md && rm -f docs/room-map-draft.svg docs/room-map-draft.png`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-07-14 - Task: 使用 Android UI 绘制简化室内地形
### What was done
- 使用自定义 Android View 和 `Canvas` 绘制简化室内地形，不再依赖房间图片资源。
- 保留建筑外轮廓、顶部异形区域、中间主通道、主要房间、房门和桌区，作为后续点位按钮的 UI 底层。
- 将原“展示图片”入口调整为“查看地图”，保留全屏打开和关闭能力，移除图片旋转与资源检查逻辑。
- 删除旧房间图片资源，并清理已被 UI 方案取代的 SVG/PNG 临时草稿。
- 同步更新室内地形图使用说明。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- 搜索旧图片资源、图片旋转和旧绑定引用：无残留匹配。
- `git diff --check -- "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/view/TerrainMapView.java`：新增简化室内地形自定义绘制控件。
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：将地图预览切换为纯 UI 展示，并移除图片加载和旋转逻辑。
- `app/src/main/res/layout/activity_main.xml`：使用地形控件替换图片控件，入口文案改为“查看地图”。
- `app/src/main/res/drawable/room_map.png`：删除不再使用的旧房间图片资源。
- `docs/room-map-preview.md`：更新为 Canvas 地形实现和当前能力说明。
- `docs/room-map-draft.svg`、`docs/room-map-draft.png`：删除被 UI 方案取代的临时草稿。
- `progress.md`：追加本轮 UI 地形实现记录。
- 回滚方式：执行 `git restore app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java app/src/main/res/layout/activity_main.xml app/src/main/res/drawable/room_map.png docs/room-map-preview.md progress.md && rm -f app/src/main/java/com/yuandaima/peanutrobot/view/TerrainMapView.java`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-07-14 - Task: 调整室内地形为横屏朝右布局
### What was done
- 将简化室内地形整体顺时针旋转 90 度，使原地图顶部异形区域朝机器人屏幕右侧。
- 按旋转后的横向设计尺寸重新计算缩放和居中位置，提升横屏空间利用率。
- 允许地图遮罩仅在 Android Studio Design 模式中默认可见，实际 App 启动时仍保持隐藏。
- 同步更新室内地形图横屏方向说明。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check -- "app/src/main/java/com/yuandaima/peanutrobot/view/TerrainMapView.java" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md" "progress.md"`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/view/TerrainMapView.java`：新增横屏尺寸计算和顺时针 90 度绘制变换。
- `app/src/main/res/layout/activity_main.xml`：增加仅用于 Android Studio 的地图预览可见配置。
- `docs/room-map-preview.md`：补充地形在机器人横屏中朝右展示的说明。
- `progress.md`：追加本轮横屏适配记录。
- 回滚方式：执行 `git restore app/src/main/java/com/yuandaima/peanutrobot/view/TerrainMapView.java app/src/main/res/layout/activity_main.xml docs/room-map-preview.md progress.md`，如已提交则使用 `git revert <commit>` 回滚。

## 2026-07-14 - Task: 调整地图与操作按钮为横屏三栏布局
### What was done
- 将主界面调整为左侧点位、中间地图、右侧操作的横屏三栏常驻布局。
- 中间地图区域约占屏幕宽度的三分之二，使用黑白平面图按原比例完整居中展示。
- 左侧保留全部点位和已选点位队列，并将全部点位调整为适合窄栏的单列展示。
- 右侧纵向保留立即出发、巡仓、回充、召回、刷新点位和锁屏等原有操作。
- 移除不再需要的地图弹层、关闭入口和 Canvas 地形控件，不修改机器人通信和导航业务逻辑。
- 同步更新室内地图布局说明和 Trellis 任务记录。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- 搜索旧地图弹层绑定和 `TerrainMapView` 源码引用：当前源码无残留匹配。
- `git diff --check -- "app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java" "app/src/main/res/drawable/room_map.png" "app/src/main/res/layout/activity_main.xml" "docs/room-map-preview.md" ".trellis/tasks/07-14-map-control-layout/prd.md" ".trellis/tasks/07-14-map-control-layout/implement.jsonl" ".trellis/tasks/07-14-map-control-layout/check.jsonl"`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 未连接真实机器人进行视觉和触控验收，左右窄栏在目标设备上的文字与触控尺寸仍需现场确认。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：将全部点位改为单列，并清理旧地图弹层绑定和交互。
- `app/src/main/res/drawable/room_map.png`：使用用户提供的黑白横版平面图作为常驻地图。
- `app/src/main/res/layout/activity_main.xml`：重排为 `1/6 + 2/3 + 1/6` 三栏布局，并保留原业务控件 ID。
- `app/src/main/java/com/yuandaima/peanutrobot/view/TerrainMapView.java`：删除被图片方案取代的未提交 Canvas 地形控件。
- `docs/room-map-preview.md`：更新为图片常驻三栏布局和现有交互说明。
- `.trellis/tasks/07-14-map-control-layout/prd.md`：记录本轮范围、决策和验收标准。
- `.trellis/tasks/07-14-map-control-layout/implement.jsonl`：记录实施阶段需要遵守的复用规范。
- `.trellis/tasks/07-14-map-control-layout/check.jsonl`：记录检查阶段需要遵守的复用规范。
- `.trellis/tasks/07-14-map-control-layout/task.json`：记录任务状态和范围。
- `progress.md`：追加本轮三栏地图布局记录。
- 回滚点：本轮修改前的 IDE Local History；当前改动与前序未提交地图工作共享文件，不应直接执行整文件 `git restore`。如本轮后续单独提交，使用 `git revert <commit>` 回滚该提交。

## 2026-07-14 - Task: 发布横屏三栏地图布局预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.8`，用于发布横屏三栏常驻地图布局。
- 将黑白室内地图、点位选择区和右侧操作区纳入本次预发布。

### Testing
- `ReadLints`：未发现新增 IDE 诊断。
- `git diff --check`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。

### Notes
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.8`，`versionCode` 更新为 `19`。
- `progress.md`：追加本轮预发布记录。
- 回滚方式：如本轮已提交，执行 `git revert <commit>`；如仅撤销版本号且尚未提交，恢复 `app/build.gradle` 中的 `versionCode 18` 和 `versionName "1.0.12-beta.7"`，并删除本节记录。

## 2026-07-14 - Task: 发布带点位标注的新室内地图预发布版
### What was done
- 替换主界面常驻室内地图，加入小屏右、小包右、大屏左、大屏后、吧台左、吧台右和补餐桌等点位标注。
- 更新应用版本到 `1.0.12-beta.9`，用于在机器人横屏上验证新地图的清晰度和标注位置。

### Testing
- 检查图片元数据：PNG 格式，尺寸为 `1448 × 1086`。
- `git diff --check`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。

### Notes
- `app/src/main/res/drawable/room_map.png`：替换为带红色点位标注的新室内地图。
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.9`，`versionCode` 更新为 `20`。
- `progress.md`：追加本轮地图替换和预发布记录。
- 回滚方式：如本轮已提交，执行 `git revert <commit>`；如尚未提交，恢复上一版地图，并将版本恢复为 `versionCode 19` 和 `versionName "1.0.12-beta.8"`。

## 2026-07-14 - Task: 增加三仓位点位绑定并记录到位等待方案
### What was done
- 在主界面增加仓位 1、仓位 2、仓位 3，并实现“先选点位、再点仓位”的绑定交互。
- 限制一次最多绑定三个不同点位，禁止点位重复绑定和已占用仓位直接覆盖。
- 屏幕导航只使用已绑定点位，并按绑定先后生成路线；无绑定或仍有待绑定点位时禁止立即出发。
- 清空仓位时同步删除对应路线点位；成功获取非空点位快照后，按点位 ID 更新或清理绑定。
- 临时未获取到点位、解析失败或返回空列表时保留当前点位、仓位、路线和待绑定状态，避免自动重试期间丢失任务配置。
- 记录后续逐点到位 HTTP 上报、等待五分钟、继续下一点或最终召回的业务方案，本轮未修改相关导航和通信逻辑。

### Testing
- `ReadLints`：`MainActivity.java` 和 `activity_main.xml` 未发现新增 IDE 诊断。
- `git diff --check`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 静态复核点位刷新分支：仅非空新快照执行绑定对账，空结果保留上一份成功快照并继续现有重试。
- 未连接真实机器人，仓位触控、点位刷新及实际导航顺序仍需在目标设备上联调确认。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：增加三仓位绑定、待绑定状态、绑定顺序路线、出发校验和空刷新状态保护。
- `app/src/main/res/layout/activity_main.xml`：将左侧下半区调整为待绑定提示和三个固定仓位卡片。
- `docs/compartment-point-binding.md`：记录三仓位绑定交互、约束、刷新语义和导航顺序。
- `docs/navigation-arrival-waiting.md`：记录后续逐点到位上报、五分钟等待、继续下一点和最终召回方案。
- `.trellis/tasks/07-14-compartment-point-binding/task.json`：记录 Trellis 任务状态和基础分支。
- `.trellis/tasks/07-14-compartment-point-binding/prd.md`：记录需求决策、边界和验收标准。
- `.trellis/tasks/07-14-compartment-point-binding/implement.jsonl`：记录实施阶段使用的复用和跨层规范。
- `.trellis/tasks/07-14-compartment-point-binding/check.jsonl`：记录检查阶段使用的复用和跨层规范。
- `progress.md`：追加本轮实现和验证记录。
- 回滚点：本轮开始前的 `v1.0.12-beta.9` 提交 `837221c`；如本轮后续单独提交，使用 `git revert <commit>` 回滚该提交。

## 2026-07-14 - Task: 发布三仓位点位绑定预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.10`，用于发布三仓位点位绑定、最多三点限制和绑定顺序导航能力。
- 将待绑定点位出发拦截、临时空刷新状态保护和相关使用说明纳入本次预发布。

### Testing
- `ReadLints`：`MainActivity.java` 和 `activity_main.xml` 未发现新增 IDE 诊断。
- `git diff --check`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 未连接真实机器人，三仓位触控、点位刷新及实际导航顺序仍需在目标设备上验收。

### Notes
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.10`，`versionCode` 更新为 `21`。
- `progress.md`：追加本轮预发布记录。
- 回滚方式：如本轮已提交，执行 `git revert <commit>`；如尚未提交，仅恢复 `app/build.gradle` 中的 `versionCode 20` 和 `versionName "1.0.12-beta.9"`，并删除本节记录。

## 2026-07-15 - Task: 实现到位上报与取餐等待推进
### What was done
- 屏幕仓位任务到达每个点位后，异步向固定地址 `http://192.168.112.194:9088/nav_arrive` 上报点位名称和仓位号。
- 新增独立的 `9088/pickup_status` HTTP 服务，仅接受 `POST application/json` 和布尔值 `pickup_status: true`。
- 每个点位到达后原地等待，pickup 成功或五分钟超时只消费当前等待一次；中间点继续下一点，最后一点调用现有 WebSocket 召回。
- 将屏幕仓位路线拆成逐点 SDK 导航会话，并使用任务代次、会话代次和 `STATE_RUNNING` 导航腿门控隔离停止、抢占或上一点产生的迟到回调。
- 到位 HTTP Call 的登记、清理和取消与等待代次使用同一同步边界，避免任务取消后继续发送旧到位请求。
- 有效上游 `/robot_task/send_point` 保持完整路线语义；无效 JSON、空路线或包含空节点的路线在抢占前被拒绝。
- 移除原屏幕单点到达后等待 20 秒再追加固定“出餐口”的旧逻辑，并补充 Activity 销毁后的服务、回调和网络资源清理。

### Testing
- `ReadLints`：`MainActivity.java`、`NavManager.java` 和 `PickupStatusServer.java` 未发现新增 IDE 诊断。
- `git -c core.whitespace=cr-at-eol diff --check`：通过；`NavManager.java` 保留仓库原有 CRLF 行尾。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 4 项有效。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- `./gradlew :app:lintDebug :app:testDebugUnitTest :app:assembleDebug --no-daemon`：测试与构建任务完成，但项目级 `lintDebug` 被既有基线阻断，报告为 5 个错误和 156 个警告，包含 API 23/24 `StorageVolume` 兼容、Android 13 通知权限和既有 URI flag 问题；这些位置不属于本轮到位等待改动。
- 独立竞态复核：未发现实现级阻塞；任务/会话归属、导航腿门控、pickup 与超时一次性消费、HTTP Call 取消和服务生命周期逻辑一致。
- 未连接真实机器人；多点 pickup、五分钟超时、上游抢占和 Activity 销毁时序仍需在目标设备上完成集成验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：增加到位等待状态机、任务与会话校验、逐腿导航、到位 HTTP 上报、任务抢占和生命周期清理。
- `app/src/main/java/com/yuandaima/peanutrobot/manager/NavManager.java`：增加 SDK 导航会话代次和带会话标识的回调转发，丢弃已释放会话的迟到事件。
- `app/src/main/java/com/yuandaima/peanutrobot/server/PickupStatusServer.java`：新增 `9088/pickup_status` 服务及方法、路径、媒体类型和 JSON 字段校验。
- `docs/navigation-arrival-waiting.md`：记录 HTTP 契约、五分钟推进、导航事件隔离、任务抢占和协议限制。
- `docs/compartment-point-binding.md`：关联仓位绑定与到位等待流程。
- `.trellis/spec/backend/navigation-arrival-contract.md`：沉淀固定 HTTP 契约、校验矩阵、等待归属和 SDK 会话隔离规范。
- `.trellis/tasks/07-15-arrival-pickup-waiting/`：记录需求、上下文、验收标准和任务状态。
- 已知限制：`pickup_status` 不携带任务、点位或仓位标识，App 无法识别恰好落入下一等待阶段的上一点迟到回调；需要上游协议增加关联字段才能彻底消除。
- 回滚方式：尚未提交时恢复 `MainActivity.java`、`NavManager.java`、两份流程文档和 `progress.md`，删除新增的 `PickupStatusServer.java` 与本 Trellis 任务目录；如后续形成独立提交，使用 `git revert <commit>` 回滚。

## 2026-07-15 - Task: 增加左栏配送进度界面
### What was done
- 在主界面左栏增加“点位选择”和“配送进度”切换，保留中间地图和右侧操作区原布局职责。
- 屏幕仓位任务成功出发后自动切换到配送进度，并使用出发时的点位名称、仓位和绑定顺序生成独立展示快照。
- 展示全部路线点位的等待配送、正在前往、等待取餐、已完成取餐、等待超时和配送已取消状态。
- 当前点到达后显示五分钟倒计时；pickup 和超时继续复用现有一次性等待完成路径，未修改 HTTP、导航会话或召回协议。
- 中间点完成等待后将下一点切换为正在前往；最后一点完成后保留最终结果并显示已开始召回。
- 配送期间拦截点位选择、仓位绑定和手动点位刷新，并用只读提示和半透明状态区分；完成或取消后恢复编辑。
- 手动回充、巡仓、召回或有效上游任务抢占时保留本次快照，并将未完成点位标记为配送已取消。

### Testing
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 静态复核：配送进度只读取出发快照；倒计时在 pickup、超时、抢占和 Activity 销毁路径停止；状态展示未改变既有任务/会话代次校验和路线推进条件。
- 未连接真实机器人；目标横屏上的文字密度、标签触控、五分钟实时时序和多点配送状态仍需现场验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：增加配送快照、状态卡片、倒计时、标签切换、只读拦截及完成/取消结果保留。
- `app/src/main/res/layout/activity_main.xml`：将左栏重组为双标签内容区，并增加配送进度滚动容器。
- `docs/compartment-point-binding.md`：补充配送进度界面、状态语义、快照和只读规则。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：增加配送进度 UI 扩展需求与验收标准。
- `progress.md`：追加本轮实现和验证记录。
- 回滚点：使用 IDE Local History 恢复到本轮首次修改 `activity_main.xml` 之前；`MainActivity.java` 与未提交的到位等待实现共享文件，禁止直接执行整文件 `git restore`。如本轮后续形成独立提交，执行 `git revert <commit>` 回滚该提交。

## 2026-07-15 - Task: 发布到位等待与配送进度预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.11`，`versionCode` 更新为 `22`。
- 将逐点到位上报、取餐等待、五分钟超时推进、导航会话隔离和左栏配送进度界面纳入本次预发布。
- 准备通过 annotated tag `v1.0.12-beta.11` 触发 Android Release 工作流并生成 GitHub 预发布版本。

### Testing
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease --no-daemon`：BUILD SUCCESSFUL。
- 构建仅报告项目既有的 Android Gradle Plugin、SDK XML 和重复权限声明警告，没有新增编译错误。
- 未连接真实机器人；多点配送、pickup、完整五分钟超时和目标横屏视觉效果仍需现场验收。

### Notes
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.11`，`versionCode` 更新为 `22`。
- `progress.md`：追加本轮预发布记录。
- 回滚方式：如已推送，执行 `git revert <release-commit>` 后发布修复版本；删除远端发布标签属于破坏性操作，不作为默认回滚方式。

## 2026-07-16 - Task: 修复立即出发后机器人不移动
### What was done
- 对比 `v1.0.12-beta.9`、`v1.0.12-beta.10` 和 `v1.0.12-beta.11` 的完整出发链路，确认回归来自 `beta.11` 新增的 SDK 导航实例逐点释放重建。
- 恢复 `beta.10` 已在机器人上验证的导航启动方式：复用机器人核心初始化时创建的 `PeanutNavigation`，整条路线一次执行 `setTargets()` 和 `prepare()`。
- 保留屏幕配送的任务代次、当前 SDK 会话代次、预期路线位置和 `STATE_RUNNING` 到达门控，避免未进入运行状态的到达事件推进路线。
- 中间点 pickup 或五分钟超时后改用既有 `pilotNext()` 和 `readyGo(true)` 继续下一点，不再释放并重建 SDK 导航对象。
- 增加十秒路线准备超时；未收到 route-prepared 时取消假启动任务、恢复“立即出发”和点位编辑，并提示用户重试。
- 导航错误回调现在会清理路线准备超时、取消当前屏幕配送并恢复操作状态，不再让按钮永久保持禁用。

### Testing
- `ReadLints`：`MainActivity.java` 未发现新增 IDE 诊断。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 历史静态对比：`beta.9` 和 `beta.10` 均复用已初始化导航实例，只有 `beta.11` 在出发和点位切换时执行 SDK 对象释放重建。
- 未连接真实机器人；首次立即出发、中间点 pickup/超时继续和十秒失败提示仍需在目标设备上验证。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：恢复稳定导航实例复用、整路线准备、`pilotNext()` 推进，并增加路线准备超时和错误恢复。
- `.trellis/spec/backend/navigation-arrival-contract.md`：将屏幕导航契约修正为整路线复用，并记录禁止立即重建 SDK 对象及十秒超时要求。
- `docs/navigation-arrival-waiting.md`：更新现场流程和导航隔离说明。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录本次启动回归修复与验收标准。
- `progress.md`：追加本轮修复和验证记录。
- 回滚方式：执行 `git restore -- app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java .trellis/spec/backend/navigation-arrival-contract.md docs/navigation-arrival-waiting.md .trellis/tasks/07-15-arrival-pickup-waiting/prd.md progress.md`；该命令不会触碰当前 Manifest、NavManager 行尾变化和未跟踪 JPG。

## 2026-07-16 - Task: 发布立即出发导航修复预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.12`，`versionCode` 更新为 `23`。
- 将导航实例复用、整路线准备、`pilotNext()` 中间点推进和十秒路线准备失败恢复纳入本次预发布。
- 准备通过 annotated tag `v1.0.12-beta.12` 触发 Android Release 工作流并生成 GitHub 预发布版本。

### Testing
- `ReadLints`：`MainActivity.java` 未发现新增 IDE 诊断。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease --no-daemon`：BUILD SUCCESSFUL。
- 未连接真实机器人；首次立即出发、中间点 pickup/超时继续和十秒失败恢复仍需在目标设备上验证。

### Notes
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.12`，`versionCode` 更新为 `23`。
- `progress.md`：追加本轮预发布记录。
- 回滚方式：如已推送，执行 `git revert <release-commit>` 后发布修复版本；删除远端发布标签属于破坏性操作，不作为默认回滚方式。
