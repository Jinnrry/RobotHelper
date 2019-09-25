package cn.xjiangwei.RobotHelper.Xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook4Env {

    public static void run(XC_LoadPackage.LoadPackageParam lpparam) {
//          // 未完成
//        if (lpparam.packageName.equals("cn.xjiangwei.RobotHelper")) {
//            //Hook 系统变量相关类，使游戏无法检测模拟器运行环境
//            XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader,
//                    "get", String.class, String.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            XposedBridge.log("~~~~~~~~~~~~222~~~~~~~~~~~~");
//                            XposedBridge.log("after");
//                            XposedBridge.log(String.valueOf(param.args[0]));
//                            XposedBridge.log(String.valueOf(param.args[1]));
//                            XposedBridge.log(String.valueOf(param.getResult()));
//                            XposedBridge.log("~~~~~~~~~~~222~~~~~~~~~~~~~");
//                            super.afterHookedMethod(param);
//
//                            if (param.args[0].equals("gsm.version.baseband")) {
//                                param.setResult("2.1.2");
//                            }
//
//                        }
//                    }
//            );
//
//        }


    }

}
