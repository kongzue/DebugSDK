package com.kongzue.debugsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.kongzue.debugsdk.listener.ApplicationStatusListener;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugSDK {
    private static Application application;
    private static DebugSDK debugSDK;

    private static ApplicationStatusListener applicationStatusListener;
    private static boolean isBackground = false;
    private static int count = 0;

    private DebugSDK() {
    }

    public static DebugSDK initSDK(Application applicationContext) {
        application = applicationContext;

        //检查之前的Bug信息
        checkErrorInfo();

        //获取状态信息
        applicationContext.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    isBackground = false;
                    if (applicationStatusListener != null)
                        applicationStatusListener.isBackground(false);
                }
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    isBackground = true;
                    if (applicationStatusListener != null)
                        applicationStatusListener.isBackground(true);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

        });

        //崩溃拦截
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (isBackground) {

                } else {

                }
                catchException(t, e);
            }
        });

        if (debugSDK == null) {
            synchronized (DebugSDK.class) {
                if (debugSDK == null) {
                    debugSDK = new DebugSDK();
                }
            }
        }
        return debugSDK;
    }

    private static void checkErrorInfo() {
        String errorInfo = getPreferencesToString(application, "error", "msg");
        if (errorInfo != null && !errorInfo.isEmpty()) {
            Log.i(">>>", "发现异常信息：");

            JSONObject phone = new JSONObject();
            try {
                phone.put("model", android.os.Build.MODEL);
                phone.put("carrier", android.os.Build.MANUFACTURER);
                phone.put("os-ver", android.os.Build.VERSION.RELEASE);
                phone.put("android-id", Settings.System.getString(application.getContentResolver(), Settings.System.ANDROID_ID));
                phone.put("app-ver", application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionCode);
                phone.put("app-package", application.getPackageName());
            } catch (Exception e) {

            }

            String phoneInfo = phone.toString();

            if (onBugReportListener != null) {
                onBugReportListener.result(errorInfo, phoneInfo);
                setPreferences(application, "error", "msg", "");
            }
        } else {
            Log.i(">>>", "没发现异常信息：");
        }
    }

    private static void catchException(Thread t, Throwable e) {
        Log.i(">>>", "Exception: " + e.getMessage());
        setPreferences(application, "error", "msg", getExceptionInfo(e));
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private static String getExceptionInfo(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    private static String getPreferencesToString(Context context, String path, String preferencesName) {
        SharedPreferences preferences = context.getSharedPreferences(path, Context.MODE_PRIVATE);
        String value = preferences.getString(preferencesName, "");
        return value;
    }

    private static void setPreferences(Context context, String path, String preferencesName, String value) {
        SharedPreferences preferences = context.getSharedPreferences(path, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferencesName, value);
        editor.commit();
    }

    public DebugSDK setApplicationStatusListener(ApplicationStatusListener applicationStatusListener) {
        this.applicationStatusListener = applicationStatusListener;
        return this;
    }

    private static OnBugReportListener onBugReportListener;

    public DebugSDK setOnBugReportListener(OnBugReportListener onBugReportListener) {
        this.onBugReportListener = onBugReportListener;
        checkErrorInfo();
        return this;
    }

    public interface OnBugReportListener {
        void result(String exceptionMessage, String phoneInfo);
    }
}
