package cn.xjiangwei.RobotHelper.Tools;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import cn.xjiangwei.RobotHelper.MainApplication;

import static android.content.Context.VIBRATOR_SERVICE;

public class Toast {

    /**
     * Toast提示
     * @param msg
     */
    public static void show(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                android.widget.Toast.makeText(MainApplication.getInstance(), msg, android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 声音提示
     */
    public static void notice() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(MainApplication.getInstance(), notification);
        r.play();
        Vibrator vibrator = (Vibrator) MainApplication.getInstance().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }
}
