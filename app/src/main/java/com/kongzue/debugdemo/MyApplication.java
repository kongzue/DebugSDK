package com.kongzue.debugdemo;

import android.app.Application;
import android.util.Log;

import com.kongzue.debugsdk.DebugSDK;
import com.kongzue.debugsdk.listener.ApplicationStatusListener;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugSDK.initSDK(this)
                //以下选配
                .setApplicationStatusListener(new ApplicationStatusListener() {
                    @Override
                    public void isBackground(boolean isBackground) {
                        Log.d("<<<", "isBackground: " + isBackground);
                    }
                })
                .setOnBugReportListener(new DebugSDK.OnBugReportListener() {
                    @Override
                    public void result(String exceptionMessage, String phoneInfo) {
                        //此处处理崩溃信息，例如上传服务器。phoneInfo提供了一个json字符串，包含设备型号、厂商、androidId、软件版本等信息用来辅助判断原因和受影响用户数
                        Log.d("<<<", "exceptionMessage: " + exceptionMessage + "\nphoneInfo: " + phoneInfo);
                    }
                });
    }
}
