# PeanutRobot

PeanutRobot 是一个用于花生机器人现场控制的 Android 应用，主界面提供点位刷新、点位导航、副屏显示、锁屏配置，以及仓库任务快捷指令。

## 功能

- 读取机器人点位并在主界面展示
- 选择一个或多个点位后立即导航
- 空闲自动锁屏，支持自定义屏保图片、显示模式、旋转角度和解锁密码
- 通过本机 HTTP 服务接收机器人任务控制请求
- 通过 WebSocket 上报机器人状态
- 通过底部按钮发送“回充”和“巡仓”任务指令

## 环境

- JDK 17
- Android Gradle Plugin 7.4.2
- Gradle Wrapper 8.0
- `compileSdk` 34
- 当前运行包名：`com.keenon.zealq`

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

## 运行接口

本机 HTTP 服务监听端口：

```text
9095
```

常用任务路径：

```text
/robot_task/get_point_list
/robot_task/go_to_charge
/robot_task/send_point
/robot_task/send_stop
/robot_task/screen_control
/robot_task/screen_control_img_display
/robot_task/single_point_speed
/robot_task/multiple_point_speed
/robot_task/save_voice_address
```

状态上报 WebSocket：

```text
ws://192.168.112.194:9096
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

`robot_task_id` 使用 10 位固定字符串，避免 JSON 数字丢失前导 0。

## 注意事项

- Peanut SDK 鉴权配置在应用代码中，调整包名或证书前需要同步确认 SDK 授权。
- 默认导航速度为 `30`。
- 默认锁屏密码为 `123456`。
