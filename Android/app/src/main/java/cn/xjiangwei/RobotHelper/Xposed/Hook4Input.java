package cn.xjiangwei.RobotHelper.Xposed;

import android.os.Build;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook4Input {


    public static void run(XC_LoadPackage.LoadPackageParam lpparam) {

        /**
         * android 10输入参数改了
         */
        if (Build.VERSION.SDK_INT >= 29) {
            Hook4Input.hook4android10(lpparam);
        } else {
            Hook4Input.hook4LTandroid10(lpparam);
        }


    }


    /**
     * 处理android10的hook
     * android源码文档
     * <p>
     * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/services/core/java/com/android/server/input/InputManagerService.java
     *
     * @param lpparam
     */
    private static void hook4android10(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod("com.android.server.input.InputManagerService", lpparam.classLoader,
                    "nativeInjectInputEvent",
                    long.class, lpparam.classLoader.loadClass("android.view.InputEvent"), int.class, int.class, int.class, int.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[3] = 0;
                            XposedBridge.log("拦截启动！long");
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log("~~~~~~~~~~~类没找到~~~~~~~~~~~~~");
        }
    }


    /**
     * 处理android9及其以下版本的的hook
     * <p>
     * android 源码位置
     * <p>
     * http://androidxref.com/9.0.0_r3/xref/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
     *
     * @param lpparam
     */
    private static void hook4LTandroid10(XC_LoadPackage.LoadPackageParam lpparam) {
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
    }


}
