package cn.xjiangwei.RobotHelper.Tools;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;


public class Robot {
    private static Instrumentation mInst = null;


    public static void tap(final int x, final int y) {
        if (x < 0 || y < 0) {
            return;
        }

        if (Robot.mInst == null) {
            mInst = new Instrumentation();

        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));    //x,y 即是事件的坐标
                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public static void tap(final int x, final int y, final long delay) {
        if (Robot.mInst == null) {
            mInst = new Instrumentation();
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));    //x,y 即是事件的坐标
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void tap(Point p) {
        tap(p.getX(), p.getY());
    }


    public static void tap(Point p, long delay) {
        tap(p.getX(), p.getY(), delay);
    }

    public static void swipe(float x1, float y1, float x2, float y2, float duration) {
        final int interval = 25;
        int steps = (int) (duration * 1000 / interval + 1);
        float dx = (x2 - x1) / steps;
        float dy = (y2 - y1) / steps;
        down(x1, y1);
        for (int step = 0; step < steps; step++) {
            SystemClock.sleep(interval);
            moveTo(x1 + step * dx, y1 + step * dy, 0);
        }
        SystemClock.sleep(interval);
        up(x2, y2);
    }


    private static void down(float x, float y) {
        if (Robot.mInst == null) {
            mInst = new Instrumentation();
        }
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));
    }


    private static void up(float x, float y) {
        if (Robot.mInst == null) {
            mInst = new Instrumentation();
        }
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
    }


    private static void moveTo(float x, float y, int contactId) {
        if (Robot.mInst == null) {
            mInst = new Instrumentation();
        }
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0));
    }


}
