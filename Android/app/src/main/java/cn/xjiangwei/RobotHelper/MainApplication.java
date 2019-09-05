package cn.xjiangwei.RobotHelper;

import android.app.Application;



public class MainApplication extends Application {
    public static int sceenWidth = 0;
    public static int sceenHeight = 0;
    public static int dpi;
    private static String serverUrl = "http://192.168.199.241:8001/";
    private static MainApplication instance;


    public static MainApplication getInstance() {
        return instance;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static void setServerUrl(String serverUrl) {
        MainApplication.serverUrl = serverUrl;
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

}
