package cn.xjiangwei.RobotHelper.Xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook4Input {


    public static void run(XC_LoadPackage.LoadPackageParam lpparam) {

        /**
         * android 10输入参数改了
         */
        if ("10".equals(android.os.Build.VERSION.RELEASE)) {
            Hook4Input.hook4android10(lpparam);
        } else {
            Hook4Input.hook4LTandroid10(lpparam);
        }


    }


    /**
     * 处理android10的hook
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
