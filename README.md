# DebugSDK
崩溃上报工具

### 说明
1) 本工具无需任何权限，仅需要进行简单配置即可使用。
2) 本工具提供崩溃信息记录功能，但不提供上传功能，崩溃信息将在软件下次启动时加载，请自行处理。
3) 本工具需要您提供的参数对照表如下：

需要的权限：
```
无
```

### 准备
1) 创建自定义 Application
范例：创建名为 MyApplication 的自定义 Application，并在 AndroidManifest.xml中完成配置
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="您的包名">

    <application
        ...
        android:name=".MyApplication"
        ...>
        
        ...
        
    </application>
</manifest>
```
接下来在 MyApplication 中对SDK进行初始化：
```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugSDK.initSDK(this);
    }
}
```

2) 创建崩溃日志回调函数：
```
DebugSDK.initSDK(this)
        .setOnBugReportListener(new DebugSDK.OnBugReportListener() {
            @Override
            public void result(String exceptionMessage, String phoneInfo) {
                //此处处理崩溃信息，例如上传服务器。
                Log.d("<<<", "exceptionMessage: " + exceptionMessage + "\nphoneInfo: " + phoneInfo);
            }
        });
```
您可以在此处上传日志，同时此方法还提供了一个 json 字符串 phoneInfo ，它包含触发崩溃的设备的基础信息，包含设备型号、厂商、androidId、软件版本等信息用来辅助判断原因和受影响用户数。

### 其他
可选的前后台判断方法
DebugSDK 提供了一个可选使用的前后台判断方法，可辅助判断应用程序是否在前台运行，调用方法如下：

```
.setApplicationStatusListener(new ApplicationStatusListener() {
    @Override
    public void isBackground(boolean isBackground) {
        Log.d("<<<", "isBackground: " + isBackground);
    }
})
```

### 引入KongzueUpdateSDK到您的项目
当前版本号：1.0.0
暂未提供 jcenter ，正在编译或审核

引入方法：
```

```
