package cn.xjiangwei.RobotHelper.Xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook4XposedCheck {


    public static void run(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("cn.xjiangwei.RobotHelper")) {
            // 检查xp框架加载是否成功
            XposedHelpers.findAndHookMethod("cn.xjiangwei.RobotHelper.MainApplication", lpparam.classLoader, "checkXposedHook",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(true);
                        }
                    });


        }
    }
}
