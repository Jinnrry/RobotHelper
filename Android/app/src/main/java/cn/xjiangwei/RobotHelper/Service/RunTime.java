package cn.xjiangwei.RobotHelper.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Method;

import cn.xjiangwei.RobotHelper.GamePackage.Main;
import cn.xjiangwei.RobotHelper.R;
import cn.xjiangwei.RobotHelper.Tools.MLog;
import cn.xjiangwei.RobotHelper.Tools.ScreenCaptureUtil;

import static android.os.SystemClock.sleep;


public class RunTime extends Service {
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int START = 1;
    public final static int END = 2;
    private static final String CHANNEL_ID = "cn.xjiangwei.RobotHelper.channel";
    public final static String ACTION_BUTTON = "com.notification.intent.action.ButtonClick";

    public RunTime() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        //处理任务
        return START_STICKY;
    }


    @Override
    public void onCreate() {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return;

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "xxx", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContent(remoteViews)
                .setSmallIcon(getApplicationInfo().icon)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .build();


        //注册广播
        ButtonBroadcastReceiver receiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        this.registerReceiver(receiver, intentFilter);

        //设置点击的事件
        Intent startIntent = new Intent(ACTION_BUTTON);
        startIntent.putExtra(INTENT_BUTTONID_TAG, START);
        PendingIntent start = PendingIntent.getBroadcast(this, START, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn1, start);


        Intent endIntent = new Intent(ACTION_BUTTON);
        endIntent.putExtra(INTENT_BUTTONID_TAG, END);
        PendingIntent end = PendingIntent.getBroadcast(this, END, endIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn2, end);


        startForeground(101, notification);

    }


    /**
     *  广播监听按钮点击事件
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        private Thread thread;


        public void collapseStatusBar(Context context) {
            try {
                Object statusBarManager = context.getSystemService("statusbar");

                Method collapse;
                collapse = statusBarManager.getClass().getMethod("collapsePanels");

                collapse.invoke(statusBarManager);
            } catch (Exception localException) {
                localException.printStackTrace();
            }

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_BUTTON)) {
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case START:
                        collapseStatusBar(context);
                        if (thread == null || !thread.isAlive()) {
                            thread = new Thread() {
                                @Override
                                public void run() {
                                    new Main().start();
                                }
                            };
                            thread.start();
                        } else {
                            MLog.info("运行中！");
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "运行中！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        break;
                    case END:
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "还未实现暂停功能。", Toast.LENGTH_LONG).show();
                            }
                        });



                        break;
                    default:
                        break;
                }
            }
        }
    }

}

