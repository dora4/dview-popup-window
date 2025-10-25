dview-popup-window

![Release](https://jitpack.io/v/dora4/dview-popup-window.svg)
--------------------------------

#### Gradle依赖配置

```groovy
// 添加以下代码到项目根目录下的build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// 添加以下代码到app模块的build.gradle
dependencies {
    implementation 'com.github.dora4:dview-popup-window:1.2'
}
```

#### 使用方式
```kt
val popup = DoraPopupWindow.create(context)
.contentView(R.layout.popup_custom)
.cornerRadius(12f)
.backgroundColor(Color.WHITE)
.build()
popup.show(anchorView)
```groovy
