package cn.xjiangwei.RobotHelper.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import cn.xjiangwei.RobotHelper.Broadcast.ButtonBroadcastReceiver;
import cn.xjiangwei.RobotHelper.R;


public class Controller extends Service {
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int START = 1;
    public final static int END = 2;
    public final static int START_HTTPSERVER = 3;
    public final static int END_HTTPSERVER = 4;
    public final static int EXIT = 5;
    private static final String CHANNEL_ID = "cn.xjiangwei.RobotHelper.channel";
    public final static String ACTION_BUTTON = "com.notification.intent.action.ButtonClick";



    public Controller() {
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
        PendingIntent end = PendingIntent.getBroadcast(this, END, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn2, end);


        //设置点击的事件
        Intent startHttpserverIntent = new Intent(ACTION_BUTTON);
        startHttpserverIntent.putExtra(INTENT_BUTTONID_TAG, START_HTTPSERVER);
        PendingIntent startHttpserver = PendingIntent.getBroadcast(this, START_HTTPSERVER, startHttpserverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.start_http, startHttpserver);


        Intent endHttpServerIntent = new Intent(ACTION_BUTTON);
        endHttpServerIntent.putExtra(INTENT_BUTTONID_TAG, END_HTTPSERVER);
        PendingIntent endHttpServer = PendingIntent.getBroadcast(this, END_HTTPSERVER, endHttpServerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.end_http, endHttpServer);


        Intent exitIntent = new Intent(ACTION_BUTTON);
        exitIntent.putExtra(INTENT_BUTTONID_TAG, EXIT);
        PendingIntent exit = PendingIntent.getBroadcast(this, EXIT, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.exit, exit);


        startForeground(101, notification);

    }




}

