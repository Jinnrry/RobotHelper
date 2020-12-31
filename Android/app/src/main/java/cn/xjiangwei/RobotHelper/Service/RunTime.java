package cn.xjiangwei.RobotHelper.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Random;

import cn.xjiangwei.RobotHelper.GamePackage.Main;

import static android.os.SystemClock.sleep;

/**
 * 脚本执行进程的实现
 *
 * 这个service将在新的进程中运行，通过kill进程的方式实现停止脚本的功能
 *
 * 如果你需要安全退出脚本，你可以把stop改成interrupt
 */
public class RunTime extends Service {

    static final int MSG_STOP = 1;
    static final int MSG_START = 2;

    private Thread runtime;

    static class IncomingHandler extends Handler {
        private Context applicationContext;
        private RunTime runTime;


        IncomingHandler(Context context, RunTime runTime) {
            this.runTime = runTime;
            applicationContext = context.getApplicationContext();
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STOP:
                    runTime.stop();  // 这里替换成interrupt可以实现安全退出
                    break;
                case MSG_START:
                    runTime.start();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    Messenger mMessenger;

    public RunTime() {
        runtime = new Thread(() -> {
            new Main().start();
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(new IncomingHandler(this, this));
        return mMessenger.getBinder();
    }


    public void start() {
        runtime.start();
    }

    // 强行退出RunTime进程
    public void stop() {
        System.exit(9);
    }


    // 发送中断信号
    public void interrupt() {
        runtime.interrupt();
    }
}
