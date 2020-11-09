package com.uroica.drinkmachine
 
import androidx.multidex.MultiDex
import com.tencent.bugly.crashreport.CrashReport
import com.uroica.drinkmachine.db.DaoManager
import me.goldze.mvvmhabit.base.BaseApplication
import me.goldze.mvvmhabit.crash.CaocConfig

class MyApplication : BaseApplication() {


    companion object instance {
        var machineFault:Boolean = false

    }
        override fun onCreate() {
        super.onCreate()
        // 主要是添加下面这句代码
        CrashReport.initCrashReport(getApplicationContext(), "0599a446c8", true);
        MultiDex.install(this);
        //初始化全局异常崩溃
        initGreenDao()
        initCrash()
    }



    private fun initCrash() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
            .enabled(true) //是否启动全局异常捕获
            .showErrorDetails(true) //是否显示错误详细信息
            .showRestartButton(true) //是否显示重启按钮
            .trackActivities(true) //是否跟踪Activity
            .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
            .errorDrawable(R.mipmap.ic_launcher) //错误图标
            //                .restartActivity(TabActivity.class) //重新启动后的activity
            //                .errorActiv
            //                ity(YourCustomErrorActivity.class) //崩溃后的错误activity
            //                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
            .apply()
    }

    private fun initGreenDao() {
        val mManager: DaoManager = DaoManager.getInstance()
        mManager.init(this)
    }

}