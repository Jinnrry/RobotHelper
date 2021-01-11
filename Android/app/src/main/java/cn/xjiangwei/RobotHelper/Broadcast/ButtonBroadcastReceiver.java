package cn.xjiangwei.RobotHelper.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Method;

import cn.xjiangwei.RobotHelper.Accessibility.HttpServer;
import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Service.RunTime;

import static cn.xjiangwei.RobotHelper.Service.Controller.ACTION_BUTTON;
import static cn.xjiangwei.RobotHelper.Service.Controller.EXIT;
import static cn.xjiangwei.RobotHelper.Service.Controller.INTENT_BUTTONID_TAG;
import static cn.xjiangwei.RobotHelper.Service.Controller.START;
import static cn.xjiangwei.RobotHelper.Service.Controller.END;
import static cn.xjiangwei.RobotHelper.Service.Controller.END_HTTPSERVER;
import static cn.xjiangwei.RobotHelper.Service.Controller.START_HTTPSERVER;


/**
 * 广播监听按钮点击事件
 */
public class ButtonBroadcastReceiver extends BroadcastReceiver {

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
                    if (!RunTime.getInstance().isRunning()) {
                        RunTime.getInstance().start();
                    } else {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(MainApplication.getInstance(), "运行中！", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    break;
                case END:
                    collapseStatusBar(context);
                    RunTime.getInstance().stop();
                    break;
                case EXIT:
                    System.exit(9);
                    break;
                case START_HTTPSERVER:
                    HttpServer.getInstance().start();
                    cn.xjiangwei.RobotHelper.Tools.Toast.show("HttpServer Start!");

                    break;
                case END_HTTPSERVER:
                    HttpServer.getInstance().stop();
                    cn.xjiangwei.RobotHelper.Tools.Toast.show("HttpServer Stop!");

                    break;
                default:
                    System.out.println("defatult : " + buttonId);
                    break;
            }
        }
    }
}