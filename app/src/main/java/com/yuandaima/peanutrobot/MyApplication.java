package com.yuandaima.peanutrobot;

import android.app.Application;
import android.view.Gravity;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.mmkv.MMKV;
import com.yuandaima.peanutrobot.util.MmkvUtils;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化mmkv
        MMKV.initialize(this);
        MmkvUtils.getInstance();
        Utils.init(this);

        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)//这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
                .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
                .errorActivity(DefaultErrorActivity.class) //程序崩溃后显示的页面
                .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置
        CustomActivityOnCrash.install(this);

    }
}
