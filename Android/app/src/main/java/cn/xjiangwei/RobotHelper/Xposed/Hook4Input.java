package cn.xjiangwei.RobotHelper.Xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook4Input {


    public static void run(XC_LoadPackage.LoadPackageParam lpparam) {

        //            //Hook安卓的输入相关类，为程序提权，使其能够点击其他应用
        try {
            XposedHelpers.findAndHookMethod("com.android.server.input.InputManagerService", lpparam.classLoader,
                    "nativeInjectInputEvent",
                    long.class, lpparam.classLoader.loadClass("android.view.InputEvent"), int.class, int.class, int.class, int.class, int.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[4] = 0;
                            XposedBridge.log("拦截启动！long");
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log("~~~~~~~~~~~类没找到~~~~~~~~~~~~~");
        }
//            //Hook安卓的输入相关类，为程序提权，使其能够点击其他应用
//            //网上都是这么写的，但是这样写并不能工作，或许某个安卓版本需要这样写吧？？？？
//        try {
//            XposedHelpers.findAndHookMethod("com.android.server.input.InputManagerService", lpparam.classLoader,
//                    "nativeInjectInputEvent",
//                    int.class, lpparam.classLoader.loadClass("android.view.InputEvent"), int.class, int.class, int.class, int.class, int.class, int.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            param.args[3] = 0;
//                            XposedBridge.log("拦截启动！int");
//                        }
//                    });
//        } catch (Exception e) {
//            XposedBridge.log("~~~~~~~~~~~类没找到~~~~~~~~~~~~~");
//        }


    }


}
