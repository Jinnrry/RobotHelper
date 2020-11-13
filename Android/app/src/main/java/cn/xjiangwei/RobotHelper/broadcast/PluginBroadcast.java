package cn.xjiangwei.RobotHelper.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PluginBroadcast extends BroadcastReceiver {
    public static String INTENT_FILTER_ACTION = "com.eg.android.AlipayGphone.info";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().contentEquals(INTENT_FILTER_ACTION)) {
            dealAlipayInfo(context, intent);
        }
    }

    public static void dealAlipayInfo(Context context, Intent intent) {
        String consultSetAmountResString = intent.getStringExtra("consultSetAmountResString");
        String toastString = consultSetAmountResString;
        Log.d("arik", "二维码链接:" + toastString);
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }
}