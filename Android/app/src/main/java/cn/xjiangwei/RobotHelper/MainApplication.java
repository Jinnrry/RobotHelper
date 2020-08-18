package cn.xjiangwei.RobotHelper;

import android.app.Application;

import cn.xjiangwei.RobotHelper.Service.Accessibility;
import cn.xjiangwei.RobotHelper.Service.RunTime;


public class MainApplication extends Application {
    public static int sceenWidth = 0;
    public static int sceenHeight = 0;
    public static int dpi;
    private static MainApplication instance;

    public static MainApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static int sceenSize() {
        if (MainApplication.sceenWidth == 3120 && MainApplication.sceenHeight == 1440) {
            return 1;
        }
        return 2;
    }


    public boolean checkXposedHook() {
        return false;
    }

    public boolean checkAccessibilityService() {
        return Accessibility.DOM != null;
    }

    public boolean checkHttpServer() {
        return RunTime.httpServer != null && RunTime.httpServer.runing;
    }
}
