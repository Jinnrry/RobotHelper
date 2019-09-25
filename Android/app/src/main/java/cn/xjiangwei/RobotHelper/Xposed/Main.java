package cn.xjiangwei.RobotHelper.Xposed;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        //这个hook 是为了检查xp框架是否加载成功
        Hook4XposedCheck.run(lpparam);

        //这个hook是为了获取点击权限
        Hook4Input.run(lpparam);

        //这个hook是为了让游戏程序无法检测模拟器
        Hook4Env.run(lpparam);
    }
}
