package cn.xjiangwei.RobotHelper.Xposed;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.zhenxi.Superappium.PageManager;

import java.net.Socket;

import cn.hutool.core.codec.Base64;
import cn.xjiangwei.RobotHelper.handlers.LoginHandler;
import cn.xjiangwei.RobotHelper.handlers.MainHandler;
import cn.xjiangwei.RobotHelper.handlers.StockTradeHandler;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Zhenxi on 2020-07-03
 */
public class LHook implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.processName.equals("com.foundersc.app.xf")) {
            ClassLoader classLoader = lpparam.classLoader;
            Log.d("arik", "发现匹配的App");
            //设置页面两秒后开始执行
            PageManager.setTaskDuration(2000);
            //添加需要处理的Activity
            AddHandleActivity();

            XposedHelpers.findAndHookMethod("com.hundsun.armo.sdk.common.net.MacsEventFactory", classLoader, "sendEvent", Socket.class, XposedHelpers.findClass("com.hundsun.armo.sdk.a.c.a", classLoader), XposedHelpers.findClass("com.foundersc.network.tasks.send.SendFluxListener", classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //Log.d("arik 发送事件", param.args[1].toString());
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                }
            });

            XposedHelpers.findAndHookMethod("com.hundsun.armo.sdk.common.net.MacsEventFactory", classLoader, "recvDataForLength", Socket.class, int.class, XposedHelpers.findClass("com.foundersc.network.tasks.receive.ReceiveFluxListener", classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //int length = (int) param.args[1];
                    //Log.d("arik 收到事件", String.valueOf(length));
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] result = (byte[]) param.getResult();
                    Log.d("arik 收到结果", Base64.encode(result));
                }
            });

            XposedHelpers.findAndHookMethod("com.foundersc.network.GroupLogger", classLoader, "log", String.class, String.class, int.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String log = (String) param.args[3];
                    Log.d("arik 收到日志", log);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                }
            });
        }
    }

    private void AddHandleActivity() {
//        PageManager.addHandler("com.foundersc.app.module.main.MainActivity", new MainHandler());
//        PageManager.addHandler("com.foundersc.trade.login.TradeLoginActivity", new LoginHandler());
//        PageManager.addHandler("com.hundsun.winner.trade.HsTradeMainActivity", new StockTradeHandler());
    }
}
