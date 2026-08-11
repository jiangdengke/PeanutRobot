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

## 2026-07-17 - Task: 美化主界面三栏 UI
### What was done
- 参考紫色圆角操作卡片，将主界面统一为克制的浅紫主题，保留原有左栏、地图和右侧操作三栏结构。
- 左栏标签、点位列表、仓位绑定和配送进度统一使用圆角卡片、紧凑间距及清晰的选中、按下、禁用和只读反馈。
- 中间地图增加独立卡片容器，并按用户确认切换到新的 `root_map.jpg`，使用 `fitCenter` 保留完整地图内容。
- 右侧操作区增加稳定的 Android Vector 图标和箭头，将“立即出发”设为深紫主操作，其余入口使用白色或浅紫次级卡片。
- 保留配送前往、等待、完成和取消状态的语义色；导航、HTTP、召回、锁屏、倒计时和仓位绑定业务逻辑均未改变。
- 独立质量复核补充了页签和点位移除按钮按压反馈，并提高“立即出发”禁用状态的文字与图标对比度。

### Testing
- `ReadLints`：本轮修改的 Java、布局、颜色和 drawable 资源未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 独立静态复核：View ID、View 类型、点击入口、启用状态和业务文案保持兼容；`MainActivity` 仅调整标签及动态配送卡片的展示代码。
- 未连接真实机器人；目标横屏文字换行、触控面积、地图留白及系统字体缩放仍需实机验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：将标签和动态配送进度卡片切换为主题化圆角资源。
- `app/src/main/res/layout/activity_main.xml`：统一三栏卡片布局、操作层级、图标、间距和新地图引用。
- `app/src/main/res/layout/item_point.xml`：移除固定点位宽度和过大内边距，适配窄左栏卡片布局。
- `app/src/main/res/values/colors.xml`：增加浅紫主题及配送状态语义色令牌。
- `app/src/main/res/drawable/circle_cross.xml`：更新点位移除图标并提供清晰危险色语义。
- `app/src/main/res/drawable/text_view_selector.xml`：更新普通操作卡片的默认、按下、聚焦和禁用状态。
- `app/src/main/res/drawable/textview_bg_selector.xml`：更新点位与仓位卡片的默认、按下、选中和禁用状态。
- `app/src/main/res/drawable/panel_surface.xml`：新增三栏通用圆角面板背景。
- `app/src/main/res/drawable/primary_action_selector.xml`：新增“立即出发”主操作背景状态。
- `app/src/main/res/drawable/progress_summary_background.xml`：新增摘要与任务状态的浅紫背景。
- `app/src/main/res/drawable/status_header_background.xml`：新增副屏状态标题背景。
- `app/src/main/res/drawable/tab_group_background.xml`：新增左栏标签组容器背景。
- `app/src/main/res/drawable/tab_selected_background.xml`：新增已选标签及按压状态背景。
- `app/src/main/res/drawable/tab_unselected_background.xml`：新增未选标签及按压状态背景。
- `app/src/main/res/drawable/delivery_progress_queued.xml`：新增等待配送圆角状态背景。
- `app/src/main/res/drawable/delivery_progress_traveling.xml`：新增正在前往圆角状态背景。
- `app/src/main/res/drawable/delivery_progress_waiting.xml`：新增等待取餐圆角状态背景。
- `app/src/main/res/drawable/delivery_progress_completed.xml`：新增配送完成圆角状态背景。
- `app/src/main/res/drawable/delivery_progress_problem.xml`：新增超时和取消圆角状态背景。
- `app/src/main/res/drawable/ic_arrow_right.xml`：新增操作入口右箭头图标。
- `app/src/main/res/drawable/ic_navigation_start.xml`：新增立即出发图标。
- `app/src/main/res/drawable/ic_refresh.xml`：新增刷新点位图标。
- `app/src/main/res/drawable/ic_charge.xml`：新增回充图标。
- `app/src/main/res/drawable/ic_patrol.xml`：新增巡仓图标。
- `app/src/main/res/drawable/ic_recall.xml`：新增召回图标。
- `app/src/main/res/drawable/ic_lock.xml`：新增立即锁屏图标。
- `app/src/main/res/drawable/ic_settings.xml`：新增锁屏设置图标。
- `app/src/main/res/drawable/ic_monitor.xml`：新增副屏显示状态图标。
- `app/src/main/res/drawable/room_map.png`：按用户确认移除旧地图资源。
- `app/src/main/res/drawable/root_map.jpg`：纳入并展示用户提供的新地图资源。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录整屏 UI 主题、适配边界、地图决策和验收状态。
- `progress.md`：追加本轮实现、验证、文件清单和回滚点。
- 回滚点：使用 IDE Local History 恢复到本轮首次修改 UI 文件之前；若只回滚主题但保留已确认的新地图，恢复布局后继续保留 `@drawable/root_map` 引用且不要删除 `root_map.jpg`。如后续形成独立提交，执行 `git revert <commit>` 回滚该提交。

## 2026-07-20 - Task: 精简仓位绑定卡片文案
### What was done
- 隐藏仓位区顶部长期显示的“待绑定”教学提示，不再占用左栏视觉空间。
- 未绑定仓位只显示仓位编号和加号；选中待绑定点位后，所有空仓位使用紫色描边和浅紫底色表达可绑定状态。
- 已绑定仓位只显示仓位编号、点位名称和勾选图标，移除“未绑定”“点击绑定”和“点击清空”等重复说明。
- 为未绑定、可绑定、已绑定、可清空和配送只读状态保留动态无障碍描述；现有临时提示、绑定、清空和配送只读行为均未改变。

### Testing
- `ReadLints`：`MainActivity.java`、`activity_main.xml` 和两个新增 Vector 图标未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 静态复核：待绑定时只高亮空仓位，配送只读状态不会显示可绑定高亮；点击绑定、点击清空和错误顺序提示继续复用原有路径。
- 未连接真实机器人；加号、勾选图标和点位名称在目标横屏上的最终比例仍需实机确认。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：将仓位卡片展示改为图标化紧凑状态，并增加动态无障碍描述。
- `app/src/main/res/layout/activity_main.xml`：隐藏顶部教学提示，并精简三个仓位卡片的初始内容、字号和内边距。
- `app/src/main/res/drawable/ic_compartment_add.xml`：新增未绑定仓位的加号 Vector 图标。
- `app/src/main/res/drawable/ic_compartment_check.xml`：新增已绑定仓位的勾选 Vector 图标。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录并完成紧凑仓位卡片的视觉验收标准。
- `progress.md`：追加本轮实现、验证、文件清单和回滚点。
- 回滚点：使用 IDE Local History 恢复到本轮首次修改 `updateCompartmentCard` 之前；当前文件还包含未提交的整屏 UI 改造，禁止直接整文件 `git restore`。如后续形成独立提交，执行 `git revert <commit>` 回滚该提交。

## 2026-07-20 - Task: 调整为先选仓位再选点位
### What was done
- 将仓位绑定流程从“先选点位、再点仓位”调整为“先选仓位、再选点位”，当前编辑仓位使用紫色高亮且始终只有一个。
- 空仓位选择点位后自动完成绑定；已绑定仓位选择其他未占用点位后直接替换，并保留原点位在配送路线中的顺序位置。
- 已绑定仓位进入编辑后，点击当前绑定点位可解除绑定并保留该空仓位选中状态，便于立即选择替代点位。
- 未选择仓位时点击点位只显示临时提示；点位已被其他仓位占用时拒绝重复绑定并提示对应仓位。
- 删除旧的待绑定点位中间状态、点位预选高亮和出发前待绑定检查；开始屏幕配送时清除仓位编辑状态，避免配送结束后恢复陈旧高亮。
- 配送期间的仓位与点位只读、路线生成、到位上报、等待取餐和召回逻辑均保持不变。

### Testing
- `ReadLints`：`MainActivity.java` 未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 搜索 `pendingPoint` 及旧的点位优先提示文本：无残留匹配。
- 静态复核：替换绑定复用旧点位在 `selectedPointList` 中的索引；解绑移除对应点位；跨仓位重复点位不会移动或复制。
- 未连接真实机器人；仓位切换、替换、解绑和配送顺序仍需在目标设备上完成触控验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：增加当前编辑仓位状态，并重写仓位选择、点位绑定、替换、解绑、高亮和无障碍描述流程。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录仓位优先绑定规则、配送顺序约束和验收结果。
- `progress.md`：追加本轮实现、验证、文件清单和回滚点。
- 回滚点：使用 IDE Local History 恢复到本轮将 `pendingPoint` 替换为 `activeCompartmentIndex` 之前；当前 `MainActivity.java` 还包含未提交的整屏 UI 改造，禁止直接整文件 `git restore`。如后续形成独立提交，执行 `git revert <commit>` 回滚该提交。

## 2026-07-20 - Task: 重排右侧操作分组
### What was done
- 将“立即出发”移动为右侧操作区首项，并与“刷新点位”组成配送操作组。
- 将“回充、巡仓、召回”连续放置为机器人操作组。
- 将“副屏显示”移动到下方，与“立即锁屏、锁屏设置”连续组成屏幕操作组。
- 三组分别按 `2 : 3 : 3` 分配高度，底部任务状态区域保持原有位置和权重。
- 所有 TextView ID、图标、背景、启用状态、点击事件和业务行为保持不变；“副屏显示”继续保持禁用状态。

### Testing
- IDE 诊断：`activity_main.xml` 未发现新增诊断。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL。
- 静态复核：右栏顺序为“立即出发、刷新点位、回充、巡仓、召回、副屏显示、立即锁屏、锁屏设置”，任务状态区仍位于末尾。
- 未连接真实机器人；三个分组在目标横屏上的间距和整体高度仍需实机确认。

### Notes
- `app/src/main/res/layout/activity_main.xml`：将右侧操作重组为配送、机器人和屏幕三个连续分组。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录并完成右侧操作分组验收标准。
- `progress.md`：追加本轮实现、验证、文件清单和回滚点。
- 回滚点：使用 IDE Local History 恢复到本轮移动 `tv_secondary_screen_display` 之前；当前布局还包含未提交的整屏 UI 改造，禁止直接整文件 `git restore`。如后续形成独立提交，执行 `git revert <commit>` 回滚该提交。

## 2026-07-20 - Task: 发布主界面 UI 优化预发布版
### What was done
- 更新应用版本到 `1.0.12-beta.13`，`versionCode` 更新为 `24`。
- 将浅紫三栏主题、新室内地图、紧凑仓位卡片、仓位优先绑定和右侧操作分组纳入本次预发布。
- 同步仓位绑定和三栏地图使用文档，准备通过 annotated tag `v1.0.12-beta.13` 触发 Android Release 工作流并生成 GitHub Pre-release。

### Testing
- `ReadLints`：本次发布涉及的 Java、布局、drawable、颜色和 Gradle 文件未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease --no-daemon`：BUILD SUCCESSFUL。
- 构建仅报告项目既有的 Android Gradle Plugin、SDK XML、重复权限及废弃 API 警告，没有新增编译错误。
- 未连接真实机器人；三栏文字密度、仓位触控、地图留白和实际配送流程仍需使用本次预发布 APK 验收。

### Notes
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.13`，`versionCode` 更新为 `24`。
- `docs/compartment-point-binding.md`：同步仓位优先绑定、替换、解绑、重复占用和配送顺序规则。
- `docs/room-map-preview.md`：同步新地图资源和右侧三组操作顺序。
- `progress.md`：追加本轮预发布范围、验证证据、文件清单和回滚点。
- 回滚方式：如已推送，执行 `git revert <release-commit>` 后发布修复版本；删除远端发布标签属于破坏性操作，不作为默认回滚方式。

## 2026-07-20 - Task: 修复横屏点位仓位分区与操作图标并发布预发布版
### What was done
- 在左侧点位列表和仓位卡片之间增加克制的分隔线及上下留白，避免目标横屏中两个区域视觉重叠或粘连。
- 将普通右侧操作图标及右箭头改为资源内置深紫灰颜色，不再依赖目标机器人系统对 `TextView` `drawableTint` 的支持。
- 为“立即出发”保留独立白色右箭头，继续维持主操作与普通操作的视觉层级。
- 更新应用版本到 `1.0.12-beta.14`，`versionCode` 更新为 `25`，准备通过 annotated tag `v1.0.12-beta.14` 发布 GitHub Pre-release。

### Testing
- `ReadLints`：本轮涉及的 Gradle、布局和 Vector Drawable 文件未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- 静态检查：普通右侧操作区域无残留 `android:drawableTint`；普通操作图标无残留白色填充或描边；“立即出发”引用独立白色右箭头。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease --no-daemon`：BUILD SUCCESSFUL。
- 构建仅报告项目既有的 Android Gradle Plugin、重复权限及废弃 API 警告，没有新增编译错误。
- 未连接真实机器人；左侧分区间距和普通图标在目标屏幕上的最终显示效果仍需使用本次预发布 APK 验收。

### Notes
- `app/src/main/res/layout/activity_main.xml`：分隔点位与仓位区域，移除普通操作图标对 tint 的依赖，并为主操作引用独立白色箭头。
- `app/src/main/res/drawable/ic_arrow_right.xml`：将普通操作右箭头改为深紫灰颜色。
- `app/src/main/res/drawable/ic_arrow_right_primary.xml`：新增“立即出发”专用白色右箭头。
- `app/src/main/res/drawable/ic_refresh.xml`、`ic_charge.xml`、`ic_patrol.xml`、`ic_recall.xml`、`ic_monitor.xml`、`ic_lock.xml`、`ic_settings.xml`：将普通操作图标改为资源内置深紫灰颜色。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录目标机器人实机问题及对应视觉验收结果。
- `app/build.gradle`：将应用版本更新为 `1.0.12-beta.14`，`versionCode` 更新为 `25`。
- `progress.md`：追加本轮修复、验证、发布范围和回滚点。
- 回滚方式：如已推送，执行 `git revert <release-commit>` 后发布后续修复版本；删除远端发布标签属于破坏性操作，不作为默认回滚方式。

## 2026-07-20 - Task: 移除主界面手动刷新点位入口
### What was done
- 移除主界面右侧“刷新点位”入口、点击处理和仅供该入口使用的刷新图标；配送操作组只保留“立即出发”，并调整为标准单行高度。
- 保留启动自动读取、读取失败重试、成功更新点位和点位绑定对账行为，自动读取流程改为使用无参刷新方法。
- 同步 README、地图布局说明和当前任务 PRD，记录移除手动入口、自动流程不变及“立即出发”高度验收结果。

### Testing
- `ReadLints`：`MainActivity.java`、`activity_main.xml`、`README.md`、`docs/room-map-preview.md` 和当前任务 PRD 未发现 IDE 诊断。
- 搜索 `tvRefreshPoints`、`tv_refresh_points`、`refreshPointData(true)`、`手动刷新点位`：源码和当前文档无残留；`ic_refresh.xml` 在 `app/src` 中无引用且已删除。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；仅报告项目既有的 Android Gradle Plugin、SDK XML 和废弃 API 警告。

### Notes
- `app/src/main/res/layout/activity_main.xml`：删除 `tv_refresh_points`，配送操作组权重由 `2` 调整为 `1`。
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：删除刷新入口监听和点击分支，保留自动读取与重试并将刷新方法改为无参。
- `app/src/main/res/drawable/ic_refresh.xml`：删除已无引用的刷新图标资源。
- `README.md`：改为说明启动自动读取点位及失败重试。
- `docs/room-map-preview.md`：删除右侧操作顺序中的“刷新点位”。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：追加移除手动入口、自动流程和标准单行高度的决策及已完成验收项。
- `progress.md`：追加本轮实现、验证、文件清单和回滚点。
- 回滚方式：在当前目标文件未含其他未提交改动的前提下，执行 `git restore -- README.md docs/room-map-preview.md .trellis/tasks/07-15-arrival-pickup-waiting/prd.md app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java app/src/main/res/layout/activity_main.xml app/src/main/res/drawable/ic_refresh.xml progress.md`，不会触及本轮无关的 `AndroidManifest.xml`、`NavManager.java` 或根目录 JPG 文件。

## 2026-07-20 - Task: 恢复主界面手动刷新点位入口
### What was done
- 根据最终使用需求撤销上一轮未提交的“移除刷新点位”改动，恢复右侧“刷新点位”按钮、深色刷新图标和原有点击处理。
- 恢复手动刷新过程中的状态重置、结果提示及配送期间刷新拦截；启动自动读取和失败重试逻辑继续保留。
- 配送操作组恢复为“立即出发、刷新点位”两项，并恢复原有高度分配。
- 更新当前任务决策，明确最终保留手动刷新入口。

### Testing
- `ReadLints`：`MainActivity.java`、`activity_main.xml`、`ic_refresh.xml` 和当前任务 PRD 未发现 IDE 诊断。
- 静态检查：布局包含 `tv_refresh_points` 并引用 `ic_refresh`；Java 同时包含手动 `refreshPointData(true)` 和自动重试 `refreshPointData(false)` 路径。
- `git diff --exit-code HEAD -- README.md docs/room-map-preview.md app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java app/src/main/res/layout/activity_main.xml app/src/main/res/drawable/ic_refresh.xml`：通过，业务实现与已发布的 `0017a79` 状态一致。
- `python3 ./.trellis/scripts/task.py validate 07-15-arrival-pickup-waiting`：通过，实施与检查上下文各 5 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；仅报告项目既有的 Android Gradle Plugin、SDK XML、废弃 API 和未检查操作警告。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：恢复刷新按钮监听、手动刷新分支、结果提示和自动重试参数。
- `app/src/main/res/layout/activity_main.xml`：恢复“刷新点位”按钮及配送操作组原有高度分配。
- `app/src/main/res/drawable/ic_refresh.xml`：恢复深紫灰刷新 Vector 图标。
- `README.md`：恢复手动刷新及启动自动重试能力说明。
- `docs/room-map-preview.md`：恢复右侧“立即出发、刷新点位”操作顺序说明。
- `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`：记录最终保留手动刷新入口及配送期间拦截规则。
- `progress.md`：追加本轮回退、验证证据、文件清单和回滚点。
- 回滚点：业务实现已精确恢复到提交 `0017a79`；如本轮后续形成独立提交，执行 `git revert <rollback-commit>` 可重新撤销本轮恢复。

## 2026-07-21 - Task: 规划地图点位标注与联动
### What was done
- 确认中间地图采用等比例铺满并允许少量上下裁切，不使用拉伸变形。
- 确认使用独立透明图层绘制地图点位，以机器人点位 ID、地图比例坐标和自定义名称建立映射。
- 确认通过长按地图并验证现有锁屏密码进入本地编辑模式，支持新增、拖动、改名、重新关联、删除、保存和取消。
- 确认正常模式显示全部小圆点并突出当前点位；未选择仓位时点击左侧点位只聚焦地图，选择仓位后同时执行原有绑定流程。
- 将第一版收敛为地图铺满、点位编辑和左侧联动，暂不加入地图反向选择、配送状态或配置导入导出。

### Testing
- 只完成方案讨论和代码结构检查，未修改 Android 业务代码，因此未执行运行时构建验收。
- 检查当前地图布局：中间区域为 `FrameLayout + ImageView`，可以叠加独立标注层。
- 检查当前点位模型：机器人点位包含稳定 ID 和名称，可以使用 ID 建立地图映射。
- `sips -g pixelWidth -g pixelHeight app/src/main/res/drawable/root_map.jpg`：确认地图尺寸为 `1202 x 1026`，与目标横屏中间区域比例不同，等比例完整显示会产生留白。

### Notes
- `.trellis/tasks/07-21-map-point-overlay/task.json`：新增地图点位标注与联动规划任务元数据。
- `.trellis/tasks/07-21-map-point-overlay/implement.jsonl`：创建后续实施上下文占位文件，尚未进入实施阶段。
- `.trellis/tasks/07-21-map-point-overlay/check.jsonl`：创建后续检查上下文占位文件，尚未进入实施阶段。
- `.trellis/tasks/07-21-map-point-overlay/prd.md`：记录目标、方案比较、已确认决策、MVP 范围和验收标准。
- `progress.md`：追加本轮方案讨论、检查依据、文件清单和回滚点。
- 回滚点：删除 `.trellis/tasks/07-21-map-point-overlay/` 并移除本节，可完整撤销本轮规划文档；本轮未改 Android 业务代码。

## 2026-07-21 - Task: 实现地图点位标注与联动
### What was done
- 中间地图移除内部白边并改为等比例铺满，新增与底图共用 `centerCrop` 缩放和裁切偏移的独立标注层，以原图归一化坐标绘制和反算触摸位置。
- 新增带地图版本、机器人点位 ID、自定义名称、归一化坐标和启用状态的本地配置，通过现有 MMKV 和 Gson 保存；版本不匹配、JSON 损坏、坐标越界、重复映射和机器人点位缺失均安全忽略并记录日志。
- 左侧点位点击先更新地图聚焦；未选择仓位时不再提示先选仓位且不修改路线，已选择仓位时继续原有绑定、替换、解绑和重复占用校验，未映射点位不阻断配送。
- 实现长按地图约三秒并验证现有锁屏密码的编辑入口，配送中禁止进入；编辑工具条支持新增、拖动、点击移动、改名、重新关联、删除、保存和取消，取消使用深拷贝工作副本恢复已保存配置。
- 同步地图使用文档和任务验收状态，保留目标机器人铺满效果与关键区域裁切的实机视觉验收未勾选。

### Testing
- `ReadLints`：新增及修改的 Java、单元测试和 `activity_main.xml` 未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-21-map-point-overlay`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- 搜索 `app/src/main` 中的“请先选择仓位”：无匹配，新点位点击路径不再以仓位选择阻断地图浏览。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；新增单元测试覆盖 Gson 配置往返、取消编辑所依赖的深拷贝隔离，以及 `centerCrop` 正反坐标变换和边界约束。
- 静态复核：保存配置强制 `root_map_v1`、机器人点位 ID 唯一和有效坐标；取消仅丢弃编辑工作副本；重新关联和保存均拒绝重复机器人点位；长按入口及密码确认后再次检查配送状态；绘制和触摸反算统一使用同一 `scale/translation`。
- 构建仅报告项目既有的 Android Gradle Plugin、SDK XML、废弃 API 和未检查操作警告；未连接目标机器人，地图关键区域裁切、工具条密度、标注拖动手感和完整配送流程仍需实机验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/bean/MapPointConfig.java`：新增地图版本和机器人点位标注持久化模型及深拷贝工作副本能力。
- `app/src/main/java/com/yuandaima/peanutrobot/util/CenterCropCoordinateMapper.java`：新增纯 Java 的 `centerCrop` 缩放、偏移、正向绘制和反向触摸坐标换算。
- `app/src/main/java/com/yuandaima/peanutrobot/view/MapPointOverlayView.java`：新增正常标注绘制、聚焦定位标记、三秒长按入口和编辑点击/拖动交互层。
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：集成 MMKV/Gson 配置保护、左侧点位聚焦、密码验证、编辑工作副本、工具条操作及配送状态隔离。
- `app/src/main/res/layout/activity_main.xml`：移除地图内部白边，改为 `centerCrop`，叠加标注层和默认隐藏的编辑工具条。
- `app/src/test/java/com/yuandaima/peanutrobot/bean/MapPointConfigTest.java`：新增配置 JSON 往返和编辑副本隔离测试。
- `app/src/test/java/com/yuandaima/peanutrobot/util/CenterCropCoordinateMapperTest.java`：新增宽屏裁切、坐标往返和触摸边界测试。
- `docs/room-map-preview.md`：说明地图铺满、标注显示、长按密码编辑、保存位置和当前限制。
- `.trellis/tasks/07-21-map-point-overlay/prd.md`：勾选代码和自动验证已支持的 MVP 验收项，保留目标机器人视觉验收未完成。
- `progress.md`：追加本轮实现、验证、文件清单和回滚点。
- 回滚方式：确认上述目标文件未叠加后续改动后，执行 `git restore -- app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java app/src/main/res/layout/activity_main.xml docs/room-map-preview.md`，再执行 `rm -f app/src/main/java/com/yuandaima/peanutrobot/bean/MapPointConfig.java app/src/main/java/com/yuandaima/peanutrobot/util/CenterCropCoordinateMapper.java app/src/main/java/com/yuandaima/peanutrobot/view/MapPointOverlayView.java app/src/test/java/com/yuandaima/peanutrobot/bean/MapPointConfigTest.java app/src/test/java/com/yuandaima/peanutrobot/util/CenterCropCoordinateMapperTest.java`；由于任务 PRD 和 `progress.md` 含本轮开始前的本地记录，仅移除本轮 PRD 勾选变更和本节，禁止整文件还原。若后续形成独立提交，优先执行 `git revert <commit>`。

## 2026-07-21 - Task: 修复地图点位审查问题
### What was done
- 建立统一机器人任务忙碌判断，补齐长按和密码确认双重检查、编辑模式五个右侧任务入口拦截，以及有效上游点位/回充任务抢占前丢弃未保存工作副本。
- 地图配置保存改为使用 MMKV boolean 写入结果，只有成功才替换已保存状态并退出；失败或异常保留当前编辑工作副本和工具栏。
- 修正 `centerCrop` 触摸反算为 View 可见区域边界，抽取纯 Java 配置清洗器以过滤 disabled、无效坐标和重复 ID，并补齐边界单元测试。
- 收紧全部点位已映射时的新建状态，并为地图标签增加顶部不足下移、纵向限制和超长名称省略。
- 同步任务验收和地图使用文档，目标机器人视觉验收继续保持未完成。

### Testing
- `ReadLints`：本轮涉及的 Java、单元测试及 `activity_main.xml` 未发现 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-21-map-point-overlay`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；测试覆盖宽 View 上下可见边界和越界、窄高 View 左右可见边界和越界、非中心点往返，以及配置版本、null、NaN/Infinity、越界、disabled 和重复 ID 清洗。
- 静态搜索与复核：五个 UI 入口均调用编辑拦截；两个有效 HTTP 任务入口均在执行前调用工作副本丢弃；长按、密码确认和最终进入共用统一忙碌判断；MMKV false 分支在替换 saved config 和退出编辑之前返回；后续映射查找使用启用且坐标有效判断；触摸坐标先 clamp 到 View 宽高再反算。
- 保护文件复核：`AndroidManifest.xml`、`NavManager.java`、前序任务 PRD 和根目录 JPG 的 SHA-256 与施工前一致，未被本轮修改。
- 未连接目标机器人；上游任务抢占时序、MMKV 真实写盘失败反馈、标签在目标屏幕边缘的视觉效果和地图关键区域裁切仍需实机验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：补齐任务与编辑互斥、上游抢占、保存真实性、配置清洗复用和全部点位已配置状态。
- `app/src/main/java/com/yuandaima/peanutrobot/util/MmkvUtils.java`：新增直接返回 MMKV 写入结果的字符串保存接口，不改变现有 `encode` 调用。
- `app/src/main/java/com/yuandaima/peanutrobot/util/CenterCropCoordinateMapper.java`：保存 View 尺寸并将反向触摸坐标限制到当前可见区域。
- `app/src/main/java/com/yuandaima/peanutrobot/util/MapPointConfigSanitizer.java`：新增不依赖 Android 的地图配置清洗和有效标注判断。
- `app/src/main/java/com/yuandaima/peanutrobot/view/MapPointOverlayView.java`：限制标签宽高、顶部不足时下移并省略超长显示名。
- `app/src/test/java/com/yuandaima/peanutrobot/util/CenterCropCoordinateMapperTest.java`：更新可见裁切边界、越界和非中心点往返测试。
- `app/src/test/java/com/yuandaima/peanutrobot/util/MapPointConfigSanitizerTest.java`：新增版本、空值、非有限数、越界、disabled 和重复 ID 清洗测试。
- `docs/room-map-preview.md`：同步任务互斥、上游抢占和 MMKV 保存失败行为。
- `.trellis/tasks/07-21-map-point-overlay/prd.md`：补充并勾选代码和测试可证明的审查修复项，保留目标机器人视觉项未勾选。
- `progress.md`：在末尾追加本轮实现、验证证据、精确文件清单和回滚点。
- 回滚点：本节之前的工作区状态。由于 `MainActivity.java`、地图视图/坐标文件、任务 PRD、文档和 `progress.md` 已包含前序未提交工作，回滚本轮时应通过 IDE 本地历史仅恢复上述文件到本轮开始时版本，禁止整文件 `git restore`；确认没有后续改动后，可执行 `git restore -- app/src/main/java/com/yuandaima/peanutrobot/util/MmkvUtils.java` 并执行 `rm -f app/src/main/java/com/yuandaima/peanutrobot/util/MapPointConfigSanitizer.java app/src/test/java/com/yuandaima/peanutrobot/util/MapPointConfigSanitizerTest.java` 回退本轮独立新增项。

## 2026-07-21 - Task: 闭环地图点位任务状态与输入校验
### What was done
- 将上游回充请求解析抽取为不依赖 Android 的正整数充电桩 ID 解析器，在进入 UI 线程和抢占地图编辑前完成 Gson 解析及完整结构校验；无效输入和充电模块未就绪均保留编辑状态，只有合法请求才激活并执行回充。
- 上游 `send_point` 最后一点在完成消息和任务标记可用后结束统一导航活动状态；状态上报 WebSocket 不可用或发送异常时记录日志，但仍清理本地导航状态、标记并恢复按钮。
- Activity 销毁时终止待确认仓库 WebSocket，清除 `warehouseTaskPending`，取消并关闭该 Activity 自有 OkHttp dispatcher、executor 和连接池；仓库任务成功、失败、状态、加载和清理 Runnable 在访问 binding 前统一检查销毁状态。
- 地图编辑标注改名时同步刷新标注 Spinner 完整名称；新建标注仅列出未映射点位，所有点位均已映射时清空陈旧选择、禁用输入并显示明确状态，已有标注仍可选择全部点位重新关联。
- 收紧 PRD 和使用文档的可验证边界：`9098` 巡仓/回充/召回互斥仅覆盖请求发送和等待确认期间，不承诺无法权威检测的外部执行期，也不新增永久锁、猜测超时或未知 SDK code 解锁。

### Testing
- `ReadLints`：`MainActivity.java`、新增解析器及单元测试和现有 `activity_main.xml` 未发现 IDE 诊断。
- `./gradlew :app:testDebugUnitTest --tests com.yuandaima.peanutrobot.util.UpstreamChargeTaskParserTest --no-daemon`：BUILD SUCCESSFUL；覆盖合法正整数及首尾空格、malformed JSON、空对象、null/空 data、null 首项、空白 ID、非数字、0 和负数，解析异常不向调用方抛出。
- `python3 ./.trellis/scripts/task.py validate 07-21-map-point-overlay`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；仅报告项目既有的 Android Gradle Plugin 与 SDK XML 版本警告。
- 静态复核：无效回充在设置 active、丢弃编辑和进入 UI 线程前返回，charger null 同样在抢占前返回；合法回充才激活并执行；`send_point` 最终点保持屏幕配送早返回和中间点 `nextDes()` 不变，并调用统一导航失效；仓库任务 UI Runnable 均有销毁检查且 `onDestroy()` 不在 destroyed 状态下更新配送 binding；Spinner 改名即时同步，新建无可用点时不再显示旧点位。
- 保护范围复核：本轮未写入、还原或删除 `AndroidManifest.xml`、`NavManager.java`、根目录 JPG 和 `.trellis/tasks/07-15-arrival-pickup-waiting/prd.md`，这些文件保留施工前已有工作区状态。
- 未连接目标机器人；仍需实机验证上游 HTTP 抢占与 charger 未就绪提示、最终点 WebSocket 回传、销毁中的 `9098` 请求、Spinner 视觉与输入光标、配置保存后重启、目标横屏裁切和完整屏幕配送流程。`9098` 外部任务执行期互斥不在当前可验证 MVP 内。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：复用上游回充解析器，闭环导航完成状态和仓库网络销毁，修复地图编辑 Spinner 陈旧显示。
- `app/src/main/java/com/yuandaima/peanutrobot/util/UpstreamChargeTaskParser.java`：新增无 Android 依赖且不向调用方抛异常的正整数充电桩 ID 解析器。
- `app/src/test/java/com/yuandaima/peanutrobot/util/UpstreamChargeTaskParserTest.java`：新增上游回充合法及完整无效输入矩阵单元测试。
- `docs/room-map-preview.md`：同步有效上游任务抢占规则、Spinner 状态及 `9098` 请求确认阶段互斥边界。
- `.trellis/tasks/07-21-map-point-overlay/prd.md`：保留历史需求并按现有证据撤销实机项勾选，记录第二轮修复和 `9098` 明确边界。
- `progress.md`：仅在末尾追加本轮实现、验证、文件清单和回滚点。
- 回滚点：本节追加前的未提交工作区状态。回滚本轮时，仅通过 IDE 本地历史反向恢复上述 `MainActivity.java`、PRD、文档和本节，禁止整文件 `git restore` 以免覆盖第一轮及用户改动；新增解析器和测试可在确认无后续依赖后执行 `rm -f app/src/main/java/com/yuandaima/peanutrobot/util/UpstreamChargeTaskParser.java app/src/test/java/com/yuandaima/peanutrobot/util/UpstreamChargeTaskParserTest.java` 删除。

## 2026-07-21 - Task: 发布 v1.0.12-beta.15
### What was done
- 将 Android 应用版本更新为 `1.0.12-beta.15`，`versionCode` 单调递增为 `26`。
- 准备通过 annotated tag `v1.0.12-beta.15` 触发 Android Release 工作流，发布地图点位标注、配送进度和任务安全处理能力。

### Testing
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL，单元测试和 Debug APK 构建通过。
- `python3 ./.trellis/scripts/task.py validate 07-21-map-point-overlay`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `ReadLints`：`app/build.gradle` 未发现 IDE 诊断。
- 构建仅报告项目既有的 Android Gradle Plugin、SDK XML 版本和 Manifest 重复权限警告；本轮未修改对应配置。

### Notes
- `app/build.gradle`：更新应用版本为 `1.0.12-beta.15`，`versionCode` 更新为 `26`。
- `progress.md`：追加本次预发布准备、验证证据、文件清单和回滚点。
- 回滚点：功能提交 `1e4d847`；发布提交完成后优先执行 `git revert <release-commit>` 回退版本号，不回退已完成的地图和配送功能。

## 2026-07-21 - Task: 固定主界面横屏方向
### What was done
- 将 `.MainActivity` 的 Manifest 屏幕方向从固定竖屏改为固定横屏，避免横屏平板或模拟器以竖屏窗口运行并挤压三栏布局。
- 同步地图使用文档和当前任务 PRD，明确 Activity 方向由 Manifest 控制，`centerCrop` 只负责地图缩放与裁切；目标设备视觉验收继续保持未完成。

### Testing
- `git diff --ignore-space-at-eol -- app/src/main/AndroidManifest.xml`：退出成功，未发现既有行尾差异之外的额外语义变化；结合提交版本与工作树配置检查，确认目标属性仅由 `portrait` 改为 `landscape`。
- `ReadLints`：检查 Manifest、地图文档和任务 PRD，未返回 IDE 诊断。
- `python3 ./.trellis/scripts/task.py validate 07-21-map-point-overlay`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；仅报告项目既有的 Android Gradle Plugin、SDK XML 版本和 Manifest 重复权限警告。
- 未连接目标横屏平板或模拟器；仍需启动 App 复查窗口是否占满横屏、两侧黑边是否消失，以及左中右三栏是否恢复预期宽度。

### Notes
- `app/src/main/AndroidManifest.xml`：仅将 `.MainActivity` 的 `android:screenOrientation` 从 `portrait` 改为 `landscape`，保留现有编码、行尾和其他工作区差异。
- `docs/room-map-preview.md`：说明主界面固定横屏，以及 `centerCrop` 不控制 Activity 方向。
- `.trellis/tasks/07-21-map-point-overlay/prd.md`：补充固定横屏要求和可自动证明的配置验收项，保留目标设备视觉验收未勾选。
- `progress.md`：仅在末尾追加本轮改动、验证证据、文件清单和回滚点。
- 回滚点：本节追加前的未提交工作区状态。回滚时仅将 Manifest 目标属性精确改回 `portrait`，删除地图文档新增段落、PRD 新增的要求与配置验收项及本节；禁止整文件 `git restore`，以免覆盖 Manifest 既有行尾差异或其他未提交工作。

## 2026-07-21 - Task: 取消固定主界面横屏方向
### What was done
- 将 `.MainActivity` 的 Manifest 屏幕方向从上一轮未提交的 `landscape` 精确恢复为 HEAD 的 `portrait`，保留该文件原有未提交行尾差异和其他内容。
- 删除地图使用文档中上一轮新增的固定横屏说明，并删除当前任务 PRD 中上一轮新增的固定横屏 requirement 和静态 acceptance 条目；其他内容保持不变。
- 本轮回退未暂存、未提交、未推送、未发布，也未修改 `MainActivity` 导航逻辑或处理“编辑地图”按钮。

### Testing
- `git diff --ignore-space-at-eol -- app/src/main/AndroidManifest.xml`：无输出，确认忽略行尾后 HEAD 与工作树不再有 `screenOrientation` 语义差异；工作树属性读取为 `portrait`。
- 静态搜索：`docs/room-map-preview.md` 和任务 PRD 不再包含上一轮固定横屏说明、requirement 或 `landscape` acceptance 条目。
- `python3 ./.trellis/scripts/task.py validate 07-21-map-point-overlay`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；仅报告项目既有的 Android Gradle Plugin、SDK XML 版本和 Manifest 重复权限警告。
- `git diff --cached --name-only`：无输出，暂存区为空。

### Notes
- `progress.md` 按仓库规则仅在末尾追加本节，保留既有“固定主界面横屏方向”历史记录，不删除或修改已有记录。
- 保留施工前已有工作区内容和既有回滚点；未修改或还原 `NavManager.java`、`MmkvUtils.java` 和根目录 JPG。
- 回滚点：本节追加前、已精确取消固定横屏但尚未记录本节的未提交工作区状态；本轮回退明确不形成提交或发布。

## 2026-08-06 - Task: 规划应用内诊断日志窗口
### What was done
- 暂停对导航几秒停止和回充后不移动问题的直接修复，规划在主界面增加可滚动、可清空、可复制的应用内诊断日志窗口。
- 确认普通 App 不依赖读取完整系统 Logcat，第一版以有容量上限的线程安全缓冲区记录导航、回充、上游任务和到位等待等关键业务时间线。
- 明确诊断窗口只用于观察和复制，不改变机器人导航、回充、HTTP、WebSocket 或到位推进行为。

### Testing
- 检查当前右侧操作区布局，确认可增加与现有操作卡片一致的“运行日志”入口。
- 检查当前 `MainActivity` 日志和弹窗模式，确认可复用 Android `AlertDialog`、可滚动文本和系统剪贴板，不需要增加第三方依赖。
- 本轮仅创建任务和 PRD，尚未修改 Android 业务代码，因此未执行构建验证。

### Notes
- `.trellis/tasks/08-06-in-app-diagnostic-logs/task.json`：新增应用内诊断日志窗口任务元数据。
- `.trellis/tasks/08-06-in-app-diagnostic-logs/prd.md`：记录目标、可靠日志来源、可选持久化方案、验收标准和明确排除项。
- `.trellis/tasks/08-06-in-app-diagnostic-logs/implement.jsonl`：保留后续实施上下文文件，确认方案后再填充有效规范。
- `.trellis/tasks/08-06-in-app-diagnostic-logs/check.jsonl`：保留后续检查上下文文件，确认方案后再填充有效规范。
- `progress.md`：仅在末尾追加本轮规划记录。
- 回滚点：删除 `.trellis/tasks/08-06-in-app-diagnostic-logs/`，并按项目历史记录规则追加取消说明；本轮未修改 Android 业务代码。

## 2026-08-06 - Task: 实现应用内诊断日志窗口
### What was done
- 新增应用级诊断记录器，在 `MyApplication` 启动时加载 App 私有目录中的历史日志；记录格式统一为时间、级别、模块和单行消息，并继续镜像到 Android Logcat。
- 使用 256 KiB 线程安全内存快照、最多 4 个 256 KiB 轮转文件和最多 512 个待写操作的有界单线程队列；磁盘缓慢或失败时丢弃额外文件操作，不阻塞导航、回充或主线程回调。
- 右侧屏幕操作组新增“运行日志”卡片，弹窗支持滚动查看、手动刷新、复制全部、清空和关闭；清空先立即清除内存，再按队列顺序删除私有文件，并在实际结果返回后提示。
- 为导航任务创建、路线准备、SDK 状态、`STATE_RUNNING`、`STATE_DESTINATION`、`readyGo(false)`、导航停止/失效、回充状态、上游 HTTP、仓库 WebSocket、到位等待、pickup、超时和 Activity 生命周期增加只观察不参与控制分支的诊断事件。
- 将诊断目录从 Android 云备份和设备迁移中排除；没有记录 SDK secret、锁屏密码、认证信息或完整上游请求体。
- 新增日志缓冲与轮转文件测试，覆盖格式、容量淘汰、清空、并发写入、UTF-8 和代理对截断、跨实例读取、轮转顺序、超大记录及无效目录失败。
- 新增 `docs/in-app-diagnostic-logs.md` 使用和现场时间线判断文档，并将有界诊断记录约束写入 Trellis logging code-spec。

### Testing
- 聚焦测试 `DiagnosticLogBufferTest` 和 `RotatingLogFileStoreTest` 两次执行均 BUILD SUCCESSFUL。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；全部单元测试和 Debug APK 构建通过，产物为 `app/build/outputs/apk/debug/app-debug.apk`。
- `./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --no-daemon`：单元测试和 `assembleDebug` 已执行，但项目级 `lintDebug` 被 6 个既有错误阻断，包括 `item_point.xml` 缺少 ConstraintLayout 约束、既有 StorageVolume API 23 兼容问题、Manifest 通知权限和既有 URI flag 常量问题；新诊断日志类、图标、备份规则和按钮没有出现在 Lint 问题报告中。
- `python3 ./.trellis/scripts/task.py validate 08-06-in-app-diagnostic-logs`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `git diff --ignore-cr-at-eol -- AndroidManifest.xml NavManager.java MmkvUtils.java`：无输出，确认三个保护文件仍只有施工前行尾差异；暂存区保持为空。
- 未连接目标机器人；仍需实机复现“立即出发后停止”，打开运行日志并验证查看、刷新、复制、清空、跨重启保留和右侧触控布局。

### Notes
- `DiagnosticLogRecorder.java`、`DiagnosticLogBuffer.java`、`RotatingLogFileStore.java`：新增有界内存、异步队列和私有轮转文件实现，文件失败不向业务调用方抛出。
- `MainActivity.java`、`activity_main.xml`、`ic_runtime_logs.xml`：新增日志入口、弹窗和关键故障时间线观察点；未改导航、回充、HTTP、WebSocket 或到位等待的既有控制决策。
- `MyApplication.java`：在其他 App 初始化前启动诊断记录器，以保留尽可能完整的进程时间线。
- `backup_rules.xml`、`data_extraction_rules.xml`：排除 `diagnostic_logs/` 云备份和设备迁移。
- `.trellis/spec/backend/logging-guidelines.md` 和 index：记录可复用的诊断日志实现合同；任务 PRD 保留目标设备验收未勾选。
- 未修改、还原、删除、暂存或提交施工前已有的 `AndroidManifest.xml`、`NavManager.java`、`MmkvUtils.java` 行尾差异和根目录 JPG。
- 回滚点：本节追加前的未提交工作区状态。回滚本轮时仅删除新增诊断 Java 类、测试、图标和文档，精确移除 `MainActivity.java`、`MyApplication.java`、`activity_main.xml`、两个备份规则、Trellis spec/index 和任务文件中的本轮增量，并追加回滚记录；禁止整文件 `git restore`，以免覆盖施工前已有改动。

## 2026-08-06 - Task: 发布 v1.0.12-beta.16
### What was done
- 将 Android 应用版本更新为 `1.0.12-beta.16`，`versionCode` 单调递增为 `27`。
- 准备通过 annotated tag `v1.0.12-beta.16` 触发 Android Release 工作流，发布应用内运行日志、异步轮转存储和关键机器人业务时间线能力。

### Testing
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon --refresh-dependencies`：BUILD SUCCESSFUL；全部单元测试和 Debug APK 构建通过。
- 构建仅报告项目既有的 Android Gradle Plugin、SDK XML 版本及 Manifest 重复权限警告，本次版本更新未新增构建错误。
- 功能提交前已通过 Trellis 任务校验、whitespace 检查和诊断日志聚焦测试；项目级 Lint 仍存在与本功能无关的既有错误，已在上一轮实现记录中列明。

### Notes
- `app/build.gradle`：更新应用版本为 `1.0.12-beta.16`，`versionCode` 更新为 `27`。
- `progress.md`：追加本次预发布准备、验证证据和回滚点。
- 回滚点：功能提交 `d930ee7`；发布提交完成后优先执行 `git revert <release-commit>` 回退版本号，不直接改写已推送历史，也不回退诊断日志功能提交。

## 2026-08-07 - Task: 扩展应用内诊断日志手动导出
### What was done
- 将运行日志弹窗操作扩展为“刷新、复制全部、导出文件、清空、关闭”；点击导出时立即保留不可变 `DiagnosticLogRecorder.snapshot()`，空快照只提示且不创建文件。
- 新增独立 `DiagnosticLogExporter`，通过最多等待 4 个任务的有界单线程队列执行磁盘 I/O；队列饱和、调度失败、I/O、运行时或安全异常均返回结果，不阻塞主线程或改变记录器持久化队列。
- Android 10/API 29 及以上使用 `MediaStore.Downloads` 写入 `Download/PeanutRobotLogs/`，通过 `RELATIVE_PATH` 和 `IS_PENDING` 安全发布且不申请存储权限；Android 9/API 28 及以下写入公共 Downloads，并只在手动导出时用独立 request code 申请 `WRITE_EXTERNAL_STORAGE`。
- 旧版权限请求前保留点击时快照，授权后导出同一字符串；拒绝后清理 pending snapshot 并提示。权限和写盘期间禁用导出按钮，弹窗关闭后释放按钮引用但不丢失待导出的快照。
- 提取纯 Java 文件名和 UTF-8 写入 helper；导出字节与 snapshot 精确一致，不追加 header、尾换行或系统 Logcat，失败时清理 MediaStore pending 条目或旧版半成品。
- 更新任务 PRD、现场文档和 logging spec 的导出合同、系统版本差异、错误矩阵、测试要求及未完成实机验收项；任务状态继续保持 `in_progress`，未归档。

### Testing
- `./gradlew :app:testDebugUnitTest --tests com.yuandaima.peanutrobot.util.DiagnosticLogExportFileTest --tests com.yuandaima.peanutrobot.util.DiagnosticLogBufferTest --tests com.yuandaima.peanutrobot.util.RotatingLogFileStoreTest --no-daemon`：BUILD SUCCESSFUL；覆盖 Unicode 精确 UTF-8、普通/空内容、无额外字节、文件名格式，以及既有缓冲和轮转行为。
- `./gradlew :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；Debug APK 构建通过，产物为 `app/build/outputs/apk/debug/app-debug.apk`。仅出现项目既有的 Android Gradle Plugin compileSdk 兼容和 SDK XML 版本警告。
- `python3 ./.trellis/scripts/task.py validate 08-06-in-app-diagnostic-logs`：通过，`implement.jsonl` 和 `check.jsonl` 各 4 项有效。
- `git -c core.whitespace=cr-at-eol diff --check`：通过。
- `git diff --ignore-cr-at-eol -- app/src/main/AndroidManifest.xml app/src/main/java/com/yuandaima/peanutrobot/manager/NavManager.java app/src/main/java/com/yuandaima/peanutrobot/util/MmkvUtils.java`：无输出；三个保护文件仍只有施工前行尾差异。`git diff --cached --name-only` 无输出，暂存区保持为空。
- 未运行项目级 lint；用户已明确无需修复项目既有 lint 错误。本轮未暂存、提交、推送或发布。
- 未连接目标机器人；仍需实机验证 API 29+ MediaStore 可见性和精确文件字节、API 28 及以下权限授权/拒绝路径、系统下载路径提示、按钮状态，以及原有日志采集、复制、清空和导航故障复现。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：增加独立导出权限和按钮状态流程；从通用启动权限中移除存储权限，确保只在 API 28 及以下的手动导出点击后申请写权限，不改变导航、回充、HTTP、WebSocket、到位等待或 pickup 控制分支。
- `app/src/main/java/com/yuandaima/peanutrobot/util/DiagnosticLogExporter.java`：新增版本化共享下载写入、有界后台执行、半成品清理和结果模型。
- `app/src/main/java/com/yuandaima/peanutrobot/util/DiagnosticLogExportFile.java`、`app/src/test/java/com/yuandaima/peanutrobot/util/DiagnosticLogExportFileTest.java`：新增纯 Java 文件名和 UTF-8 写入逻辑及聚焦测试。
- `.trellis/tasks/08-06-in-app-diagnostic-logs/prd.md`、`docs/in-app-diagnostic-logs.md`、`.trellis/spec/backend/logging-guidelines.md`：同步导出要求、验收、决策、DoD、路径、内容边界和错误矩阵。
- 明确保留并未修改、还原、删除、格式化或暂存施工前路径 `app/src/main/AndroidManifest.xml`、`app/src/main/java/com/yuandaima/peanutrobot/manager/NavManager.java`、`app/src/main/java/com/yuandaima/peanutrobot/util/MmkvUtils.java` 和根目录 `acb04aae21ce9e365ccbd9596e7d1a3d.jpg`。
- 回滚点：本节追加前的未提交工作区状态。回滚本轮时删除两个新增导出类和新增单元测试，通过 IDE 本地历史精确移除 `MainActivity.java`、任务 PRD、现场文档、logging spec 和本节的导出增量；禁止整文件 `git restore`，以免覆盖施工前保护内容或既有诊断日志实现。

## 2026-08-07 - Task: 发布 v1.0.12-beta.17
### What was done
- 将 Android 应用版本更新为 `1.0.12-beta.17`，`versionCode` 单调递增为 `28`。
- 准备通过 annotated tag `v1.0.12-beta.17` 触发 Android Release 工作流，发布运行日志手动导出到系统下载目录的能力。

### Testing
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon --refresh-dependencies`：BUILD SUCCESSFUL；全部单元测试和 Debug APK 构建通过。
- 构建仅报告项目既有的 Android Gradle Plugin、SDK XML 版本及 Manifest 重复权限警告，本次版本更新未新增构建错误。
- 日志导出功能提交前已通过 Trellis 校验、whitespace 检查、IDE linter 检查及日志导出聚焦测试。

### Notes
- `app/build.gradle`：更新应用版本为 `1.0.12-beta.17`，`versionCode` 更新为 `28`。
- `progress.md`：追加本次预发布准备、验证证据和回滚点。
- 本版本只发布诊断日志导出能力，不包含尚未实施的回充后同点再次出发修复。
- 未修改、还原、删除或暂存施工前已有的 `AndroidManifest.xml`、`NavManager.java`、`MmkvUtils.java` 行尾差异和根目录 JPG。
- 回滚点：日志导出功能提交 `d8883aa`；发布提交完成后使用 `git revert <release-commit>` 回退版本号，不改写已推送历史。

## 2026-08-11 - Task: 修复充电状态下屏幕立即出发
### What was done
- 保留屏幕仓位任务原有的路线构造、任务激活和导航会话复用行为；充电控制已停止时仍立即进入现有路线准备流程。
- 当正在充电或上游回充任务仍活动时，保存本次路线快照、安装十秒有界超时，再发送 `CHARGE_ACTION_STOP`；只有充电状态 `1` 或 `6` 能一次性继续该路线。
- 将充电释放确认和错误处理切回主线程；充电模块不可用、停止指令运行时异常、模块错误、超时、任务抢占和 Activity 销毁都会清除待出发路线及超时，阻止迟到回调强制启动导航。
- 用户可见失败会取消当前屏幕配送、恢复“立即出发”并显示可重试中文提示；新增请求、确认、继续、取消和失败诊断事件，未改变上游 `send_point` 或导航状态处理。

### Testing
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；全部 Debug 单元测试和 APK 构建通过，仅保留项目既有 Android Gradle Plugin、SDK XML、过时 API 和 unchecked 警告。
- `python3 ./.trellis/scripts/task.py validate 08-11-screen-departure-after-charge`：通过，`implement.jsonl` 和 `check.jsonl` 各 3 项有效。
- `git diff --check -- app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：通过。
- IDE linter 检查 `MainActivity.java`：未报告诊断。
- 静态复核确认屏幕“立即出发”改走充电交接入口，上游 `send_point` 仍直接调用原有 `prepareNav(routeNodes)`；未连接目标机器人，充电状态 `4 -> 6/1`、超时和模块错误仍需按 PRD 执行实机验收。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：新增屏幕出发充电控制交接、十秒超时、一次性状态确认、失败恢复及全取消路径清理。
- `docs/compartment-point-binding.md`：补充屏幕出发在充电状态下的等待、确认、失败和抢占行为说明。
- `progress.md`：按仓库规则追加本轮实现、验证证据、文件清单和回滚方式。
- 保留并未修改、还原、删除或暂存施工前已有的 `app/src/main/AndroidManifest.xml`、`NavManager.java`、`MmkvUtils.java` 和根目录 JPG；本轮未提交、推送或发布。
- 回滚方式：在没有后续同文件改动的前提下执行 `git restore -- app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java docs/compartment-point-binding.md progress.md`，仅回退本轮三个已跟踪文件；不要处理施工前保护文件或任务目录。

## 2026-08-11 - Task: 修正屏幕出发充电交接阻塞项
### What was done
- 屏幕出发需要等待充电控制释放时，先立即让旧导航任务失效并调用现有 `NavManager.stop()`，记录诊断事件后才安装交接等待和发送停止充电指令；充电控制未活动时仍直接进入原有路线准备流程。
- 新增屏幕出发交接状态持有器，为每次交接分配单调递增代次，并让超时、充电释放状态、充电模块错误和同步停止指令异常只处理各自代次；取消或抢占会使旧代次失效，重复释放只消费路线一次。
- 移除屏幕配送通用取消流程中“只因存在待交接路线就恢复立即出发按钮”的共享副作用；仅用户可见交接失败显式恢复按钮，手动 HTTP/UI 路径继续保留各自原有按钮处理。
- 新增纯 Java 聚焦单元测试，覆盖重复释放一次性消费、取消代次不能消费或清除后继代次、当前失败可清除当前交接；同步更新现场文档中的立即停止旧导航和代次隔离说明。

### Testing
- `./gradlew :app:testDebugUnitTest --tests com.yuandaima.peanutrobot.ScreenDepartureHandoffStateTest --no-daemon`：BUILD SUCCESSFUL，3 个交接代次聚焦场景通过。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon`：BUILD SUCCESSFUL；全部 Debug 单元测试和 APK 构建通过，仅保留项目既有 Android Gradle Plugin compileSdk 与 SDK XML 版本警告。
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --rerun-tasks --no-daemon`：BUILD SUCCESSFUL；最终复核时强制重新执行 42 个任务，确认修改后的 Java 编译、完整单元测试和 Debug APK 打包均通过。
- IDE linter 检查 `MainActivity.java`、`ScreenDepartureHandoffState.java` 和 `ScreenDepartureHandoffStateTest.java`：未报告诊断。
- `git diff --check -- app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java docs/compartment-point-binding.md progress.md`：通过；两个新增 Java 文件分别执行 `git diff --no-index --check /dev/null <file>`，无空白错误诊断，命令仅因存在新增内容返回预期的 no-index 差异状态 `1`。
- `python3 ./.trellis/scripts/task.py validate 08-11-screen-departure-after-charge`：通过，`implement.jsonl` 和 `check.jsonl` 各 3 项有效。未连接目标机器人，仍需实机验证充电状态 `4 -> 6/1`、交接超时、模块错误和连续两次交接。

### Notes
- `app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java`：恢复等待充电释放前的旧导航立即抢占，接入交接代次，并移除通用取消中的按钮恢复副作用。
- `app/src/main/java/com/yuandaima/peanutrobot/ScreenDepartureHandoffState.java`：新增屏幕出发专用的路线快照、单调代次、一次性消费和按代次清除状态。
- `app/src/test/java/com/yuandaima/peanutrobot/ScreenDepartureHandoffStateTest.java`：新增 3 个纯 Java 交接代次回归测试。
- `docs/compartment-point-binding.md`：补充等待交接前立即停止旧导航和迟到回调代次隔离行为。
- `.trellis/spec/backend/navigation-arrival-contract.md`：固化充电控制交接、十秒超时、一次性释放消费、代次隔离和实机验证合同。
- `progress.md`：追加本轮修正、验证证据、文件清单和回滚点。
- 保留并未修改、还原、删除、格式化或暂存施工前已有的 `app/src/main/AndroidManifest.xml`、`app/src/main/java/com/yuandaima/peanutrobot/manager/NavManager.java`、`app/src/main/java/com/yuandaima/peanutrobot/util/MmkvUtils.java` 和根目录 JPG。
- 完整回滚本次屏幕出发充电交接任务时，可执行 `git restore -- app/src/main/java/com/yuandaima/peanutrobot/MainActivity.java docs/compartment-point-binding.md progress.md .trellis/spec/backend/navigation-arrival-contract.md`，删除新增的 `ScreenDepartureHandoffState.java`、对应测试和 `.trellis/tasks/08-11-screen-departure-after-charge/`；不得处理上述保护文件。

## 2026-08-11 - Task: 发布 v1.0.12-beta.18
### What was done
- 将 Android 应用版本更新为 `1.0.12-beta.18`，`versionCode` 单调递增为 `29`。
- 准备通过 annotated tag `v1.0.12-beta.18` 触发 Android Release 工作流，发布回充后屏幕立即出发的 Charger 控制权交接修复。

### Testing
- `./gradlew :app:testDebugUnitTest :app:assembleDebug --rerun-tasks --no-daemon`：BUILD SUCCESSFUL；强制执行 42 个 Gradle 任务，完整单元测试和 Debug APK 构建通过。
- 构建仅报告项目既有的 Android Gradle Plugin compileSdk、SDK XML、Manifest 重复权限、过时 API 和 unchecked 警告，本次版本更新未新增构建错误。
- `git diff --check -- app/build.gradle`：通过。

### Notes
- `app/build.gradle`：更新应用版本为 `1.0.12-beta.18`，`versionCode` 更新为 `29`。
- `progress.md`：追加本次预发布准备、验证证据和回滚点。
- 本版本修复回充控制仍活跃时屏幕路线过早准备的问题；导航进入运行后仍出现的 SDK 数字状态 `6` 不在本版本加入自动重试。
- 未修改、还原、删除或暂存施工前已有的 `AndroidManifest.xml`、`NavManager.java`、`MmkvUtils.java` 行尾差异和根目录 JPG。
- 回滚点：功能提交 `d94c4ab`；发布提交完成后使用 `git revert <release-commit>` 回退版本号，使用 `git revert d94c4ab` 回退功能，不改写已推送历史。
