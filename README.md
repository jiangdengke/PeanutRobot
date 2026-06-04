# PeanutRobot

PeanutRobot 是一个用于擎朗机器人现场控制的 Android 应用。主界面提供点位刷新、点位选择导航、副屏媒体展示、空闲锁屏配置，以及“回充”“巡仓”“召回”仓库任务快捷指令。

当前应用是单模块 Android 工程，主要代码在 `app/src/main/java/com/yuandaima/peanutrobot`。运行包名是 `com.keenon.zealq`，代码 namespace 是 `com.yuandaima.peanutrobot`。

## 当前状态

- 主入口：`MainActivity`
- Application：`MyApplication`
- 本机 HTTP 服务：`WebServer`，监听 `9095`
- 状态上报 WebSocket：`WebSocketService`
- 导航封装：`NavManager`
- 副屏展示：`PresentationCoucou`
- 本地配置存储：MMKV
- 擎朗 SDK AAR：`app/libs/peanut-sdk-release.aar`

## 功能

- 读取机器人点位并在主界面展示
- 支持手动刷新点位，启动后会自动重试获取点位
- 选择一个或多个点位后立即导航
- 根据单点或多点任务使用不同导航速度配置
- 支持本机 HTTP 任务控制
- 支持 WebSocket 上报机器人状态
- 支持副屏播放视频或图片轮播
- 支持空闲自动锁屏、手动锁屏、屏保图片、显示模式、旋转角度和解锁密码
- 支持底部按钮发送“回充”“巡仓”和“召回”仓库任务指令

## 环境

- JDK 17
- Android Gradle Plugin 7.4.2
- Gradle Wrapper 8.0
- `minSdk` 23
- `targetSdk` 33
- `compileSdk` 34
- `versionName` 1.0.1
- `versionCode` 2

## 构建

```bash
./gradlew :app:assembleDebug
```

构建产物：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 发布

推送 `v*` tag 会触发 GitHub Actions，自动构建 Debug APK，并创建或更新 GitHub Release。

```bash
git tag vX.Y.Z
git push origin vX.Y.Z
```

发布产物命名格式：

```text
PeanutRobot-<tag>-debug.apk
```

## 运行依赖

- 需要安装在已完成擎朗 Peanut SDK 授权的机器人或 Android 设备上。
- 应用需要网络、存储、设备状态等运行权限。
- `AndroidManifest.xml` 已启用明文流量，用于局域网 HTTP 和 WebSocket 通信。
- 视频上传默认保存到外部存储目录 `/peanut/media/ad/`。

## HTTP 接口

本机 HTTP 服务监听端口：

```text
9095
```

所有业务接口当前按 POST 处理。

| 路径 | 用途 |
| --- | --- |
| `/robot_task/get_point_list` | 返回当前点位列表 |
| `/robot_task/go_to_charge` | 执行机器人回充 |
| `/robot_task/send_point` | 接收点位列表并开始导航 |
| `/robot_task/send_stop` | 停止当前导航 |
| `/robot_task/screen_control` | 副屏播放视频 |
| `/robot_task/screen_control_img_display` | 副屏图片轮播 |
| `/robot_task/single_point_speed` | 保存单点导航速度 |
| `/robot_task/multiple_point_speed` | 保存多点导航速度 |
| `/robot_task/save_voice_address` | 保存到达后的语音或音频地址 |
| `/robot_task/send_video` | 上传视频文件到本机存储 |

速度配置请求示例：

```json
{
  "speed": 30
}
```

语音地址配置示例：

```json
{
  "video": "file:///sdcard/peanut/media/ad/voice.mp3"
}
```

## WebSocket

状态上报 WebSocket：

```text
ws://192.168.112.194:9096
```

应用会定时上报机器人状态，主要字段包括：

```json
{
  "workMode": 0,
  "power": 80,
  "motorStatus": 0,
  "destList": "...",
  "charging": "正在充电",
  "working": "空闲"
}
```

仓库任务按钮 WebSocket：

```text
ws://192.168.112.194:9098
```

“回充”按钮发送：

```json
{
  "robot_id": 3,
  "task_id": 789115,
  "robot_task_id": "0000004529"
}
```

“巡仓”按钮发送：

```json
{
  "robot_id": 3,
  "task_id": 789110,
  "is_return": true,
  "robot_task_id": "0000004528"
}
```

“召回”按钮发送：

```json
{
  "robot_id": 3,
  "task_id": 789116,
  "is_return": true,
  "robot_task_id": 4528
}
```

“回充”和“巡仓”的 `robot_task_id` 使用 10 位固定字符串，避免 JSON 数字丢失前导 0。“召回”的 `robot_task_id` 按接口约定使用数值 `4528`。

## 注意事项

- 擎朗 Peanut SDK 的 `appId`、`secret` 和通信方式目前在代码中配置，调整包名、签名或 SDK 授权前需要同步确认。
- 机器人服务地址、仓库任务地址、机器人 ID 和任务 ID 目前是硬编码配置，后续应迁移到环境配置或运行时设置。
- 默认导航速度为 `30`。
- 默认锁屏密码为 `123456`。
- 当前依赖中存在重复依赖和历史仓库配置，后续整理时需要兼顾 Peanut SDK AAR 和旧依赖解析。
