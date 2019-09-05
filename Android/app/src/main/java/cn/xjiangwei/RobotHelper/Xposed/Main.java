package cn.xjiangwei.RobotHelper.Xposed;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log(lpparam.packageName);
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

            //网上都是这么写的，但是这样写并不能工作，或许某个安卓版本需要这样写吧？？？？
            XposedHelpers.findAndHookMethod("com.android.server.input.InputManagerService", lpparam.classLoader,
                    "nativeInjectInputEvent",
                    int.class, lpparam.classLoader.loadClass("android.view.InputEvent"), int.class, int.class, int.class, int.class, int.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[3] = 0;
                            XposedBridge.log("拦截启动！int");
                        }
                    });
        } catch (Exception ignored) {
        } catch (Error ignored) {
        }


    }
}
