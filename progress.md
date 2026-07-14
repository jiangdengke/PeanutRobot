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
