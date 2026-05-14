# SalaryIsland Android

macOS 工资计算应用「工资岛」的 Android 移植版。基于 Kotlin + Jetpack Compose 实现，使用 WindowManager 悬浮窗模拟 Dynamic Island 效果。

## 功能

- 设置月薪、上班时间、工作时长、工作天数
- 顶部悬浮窗实时显示今日已赚金额
- 金币掉落动画效果
- 可配置的刷新频率
- 常驻通知栏显示运行状态

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material3
- **架构**: 单 Activity + Service + WindowManager 悬浮窗
- **存储**: DataStore Preferences
- **后台**: Foreground Service

## 构建

### 前提条件
- Android Studio Hedgehog (2023.1.1+) 或更高版本
- JDK 17
- Android SDK 34+

### 本地构建
```bash
./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

### GitHub Actions 自动构建
推送代码到 GitHub 仓库的 `main` 分支，或手动触发 workflow，即可在 [Actions](https://github.com/你的用户名/仓库名/actions) 页面下载 APK。

## 权限说明

- **SYSTEM_ALERT_WINDOW**: 显示顶部悬浮窗（灵动岛）
- **POST_NOTIFICATIONS** (Android 13+): 后台运行通知
- **FOREGROUND_SERVICE**: 后台定时刷新

## 项目结构

```
app/src/main/java/com/salaryisland/app/
├── SalaryIslandApp.kt           # Application 入口
├── MainActivity.kt              # 主 Activity（设置页面）
├── data/
│   └── SettingsDataStore.kt     # DataStore 存储
├── domain/
│   └── SalaryCalculator.kt      # 工资计算逻辑
├── service/
│   └── IslandService.kt         # 前台 Service
├── overlay/
│   └── IslandOverlayManager.kt  # WindowManager 悬浮窗管理
└── ui/
    ├── SettingsScreen.kt        # 设置界面
    ├── IslandContent.kt         # 悬浮窗内容
    └── CoinRainAnimation.kt     # 金币掉落动画
```
