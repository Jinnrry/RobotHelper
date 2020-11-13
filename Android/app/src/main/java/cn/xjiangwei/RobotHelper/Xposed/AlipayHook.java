package cn.xjiangwei.RobotHelper.Xposed;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

import cn.xjiangwei.RobotHelper.broadcast.PluginBroadcast;
import cn.xjiangwei.RobotHelper.utils.Constants;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AlipayHook implements IXposedHookLoadPackage {
    public static Activity launcherActivity = null;
    public static ClassLoader classLoader = null;
    public static ClassLoader wework_classLoader = null;
    public static ClassLoader chem99_classLoader = null;
    public static AlipayBroadcast alipayBroadcast;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if ((lpparam.appInfo == null) || ((lpparam.appInfo.flags & 0x81) != 0)) {
            return;
        }
        final String packageName = lpparam.packageName;
        if (Constants.ALIPAY_PACKAGE.equals(packageName)) {
            classLoader = lpparam.classLoader;
            Log.d("arik", "com.eg.android.AlipayGphone onCreated" + "\n");
            XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("arik", "com.alipay.mobile.quinox.LauncherActivity onCreated" + "\n");
                    launcherActivity = (Activity) param.thisObject;
                    alipayBroadcast = new AlipayBroadcast();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(AlipayBroadcast.INTENT_FILTER_ACTION);
                    launcherActivity.registerReceiver(alipayBroadcast, intentFilter);
                }
            });

            // hook 支付宝的主界面的onDestory方法，销毁广播
            XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onDestroy", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("arik", "com.alipay.mobile.quinox.LauncherActivity onDestroy" + "\n");
                    if (alipayBroadcast != null) {
                        ((Activity) param.thisObject).unregisterReceiver(alipayBroadcast);
                    }
                    launcherActivity = null;
                }
            });

            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    Intent intent = activity.getIntent();
                    String money = intent.getStringExtra("qr_money");
                    String beiZhu = intent.getStringExtra("beiZhu");
                    Log.d("arik", "设置：" + money + " beizhu : " + beiZhu + "\n");
                    XposedHelpers.setObjectField(activity, "g", money);
                    XposedHelpers.callMethod(XposedHelpers.getObjectField(activity, "c"), "setText", beiZhu);
                }
            });

            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", classLoader, "a", String.class,
                    XposedHelpers.findClass("com.alipay.transferprod.rpc.result.ConsultSetAmountRes", classLoader), new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.d("arik", "com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity a" + "\n");
                            String message = (String) param.args[0];
                            Object consultSetAmountRes = param.args[1];
                            String consultSetAmountResString = "";
                            if (consultSetAmountRes != null) {
                                consultSetAmountResString = (String) XposedHelpers.callMethod(consultSetAmountRes, "toString");
                            }
                            Intent broadCastIntent = new Intent();
                            broadCastIntent.putExtra("consultSetAmountResString", consultSetAmountResString);
                            broadCastIntent.setAction(PluginBroadcast.INTENT_FILTER_ACTION);
                            Activity activity = (Activity) param.thisObject;
                            activity.sendBroadcast(broadCastIntent);
                        }
                    });

            XposedHelpers.findAndHookConstructor("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity.a", classLoader, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("arik", "设置金额" + "\n");
                    String message = (String) param.args[0];
                    Log.d("arik", "设置金额  -> :" + message + "\n");
                }
            });
        } else if (Constants.WEWORK_PACKAGE.equals(packageName)) {
            wework_classLoader = lpparam.classLoader;
            Log.d("arik", "wework  hook " + "\n");
            XposedHelpers.findAndHookMethod("com.tencent.wework.colleague.controller.ColleagueBbsManager", wework_classLoader, "getPostListCache", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object container = param.getResult();
                    List list = (List) XposedHelpers.callMethod(container, "bKH");
                    for (int i = 0; i < list.size(); i++) {
                        String title = (String) XposedHelpers.callMethod(list.get(i), "getTitle");
                        String content = (String) XposedHelpers.callMethod(list.get(i), "getContent");
                        String toDebugString = (String) XposedHelpers.callMethod(list.get(i), "toDebugString");
                        Log.d("arik", "title:  " + title + "\n");
                        Log.d("arik", "content: " + content + "\n");
                        Log.d("arik", "=======================================================================================================================================  \n");
                        Log.d("arik", "toDebugString: " + toDebugString + "\n");
                        Log.d("arik", "=======================================================================================================================================  \n");
                    }
                }
            });
        } else if (Constants.CHEM99_PACKAGE.equals(packageName)) {
            chem99_classLoader = lpparam.classLoader;
            Log.d("arik", "chem99  hook " + "\n");
            XposedHelpers.findAndHookMethod("com.chem99.composite.init.InitApp", chem99_classLoader, "getSig", Map.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Map<String, String> map = (Map<String, String>) param.args[0];
                    Log.d("arik", "chem99 sign map  -> :" + JSON.toJSONString(map) + " \n");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String message = (String) param.getResult();
                    Log.d("arik", "chem99 sign result  -> :" + message + "\n");
                }
            });
        }
    }
}
