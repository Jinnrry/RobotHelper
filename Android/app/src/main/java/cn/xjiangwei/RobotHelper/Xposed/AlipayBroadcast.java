package cn.xjiangwei.RobotHelper.Xposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.robv.android.xposed.XposedHelpers;

public class AlipayBroadcast extends BroadcastReceiver {
    public static String INTENT_FILTER_ACTION = "com.xie.pay.alipay.info";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().contentEquals(INTENT_FILTER_ACTION)) {
            String qr_money = intent.getStringExtra("qr_money");
            String beiZhu = intent.getStringExtra("beiZhu");
            Log.d("arik", "AlipayBroadcast onReceive " + qr_money + " " + beiZhu + "\n");
            if (!qr_money.contentEquals("")) {
                Intent launcherIntent = new Intent(context, XposedHelpers.findClass("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", AlipayHook.classLoader));
                launcherIntent.putExtra("qr_money", qr_money);
                launcherIntent.putExtra("beiZhu", beiZhu);
                AlipayHook.launcherActivity.startActivity(launcherIntent);
            }
        }
    }
}