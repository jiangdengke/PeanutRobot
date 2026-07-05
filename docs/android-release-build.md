# Android 发布构建说明

## Maven 仓库过滤规则

项目在根目录 `build.gradle` 中对 Maven 仓库配置了 `content` 过滤规则。

GitHub Actions 构建时优先使用官方 `google()` 和 `mavenCentral()`，镜像仓库只作为 fallback。修改 Android Gradle Plugin 或相关构建依赖时，需要确认对应依赖 group 已被允许进入正确仓库。

当前 Android Gradle Plugin `com.android.tools.build:gradle:7.4.2` 会依赖以下 Google Maven group：

```text
com.google.testing.platform
```

该 group 必须允许从 `google()` 仓库解析，否则 CI 会在配置 root project 阶段报错：

```text
Could not find com.google.testing.platform:core-proto:0.0.8-alpha08
```

## 验证方式

发布前建议执行：

```text
./gradlew :app:assembleDebug --no-daemon --refresh-dependencies
```

`--refresh-dependencies` 可以减少本地缓存掩盖远端依赖解析问题的风险。
