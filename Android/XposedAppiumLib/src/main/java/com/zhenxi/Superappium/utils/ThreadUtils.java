package com.zhenxi.Superappium.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Zhenxi on 2019/12/09.
 */

public class ThreadUtils {
    //ʹ�����̵߳�looper����handler ���handlerһ��ִ�������̵߳�
    public static Handler handler = new Handler(Looper.getMainLooper());

    //���̵߳��̳߳�
    private static Executor executor = Executors.newSingleThreadExecutor();

    /**
     * �����߳���ִ�д���
     *
     * @param r
     */
    public static void runOnNonUIThread(final Runnable r) {
        executor.execute(r);
    }

    /**
     * �����߳���ִ�д���
     *
     * @param r
     */
    public static void runOnMainThread(Runnable r) {
        handler.post(r);
    }
}
