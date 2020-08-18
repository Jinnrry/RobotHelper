package cn.xjiangwei.RobotHelper.Tools.InputImp;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;

import cn.xjiangwei.RobotHelper.Tools.Point;

import static android.os.SystemClock.sleep;

public class InstrumentationInput implements Input {

    private static InstrumentationInput instance;

    public static InstrumentationInput getInstance() {
        if (instance == null) {
            synchronized (InstrumentationInput.class) {
                if (instance == null) {
                    instance = new InstrumentationInput();
                }
            }
        }

        return instance;
    }


    private static Instrumentation mInst = new Instrumentation();

    /**
     * 点击操作
     *
     * @param x
     * @param y
     */
    @Override
    public void tap(final int x, final int y) {
        if (x < 0 || y < 0) {
            return;
        }
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));    //x,y 即是事件的坐标
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));

    }

    /**
     * 长按操作，可以自定义按下时间，单位为毫秒
     *
     * @param x
     * @param y
     * @param delay
     */
    @Override
    public void tap(final int x, final int y, final long delay) {
        if (x < 0 || y < 0) {
            return;
        }
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));    //x,y 即是事件的坐标
        sleep(delay);
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
    }

    @Override
    public void tap(Point p) {
        tap(p.getX(), p.getY());
    }

    @Override
    public void tap(Point p, long delay) {
        tap(p.getX(), p.getY(), delay);
    }

    @Override
    public void swipe(int x1, int y1, int x2, int y2, int duration) {
        swipe((float) x1, (float) y1, (float) x2, (float) y2, duration);
    }


    /**
     * 拖拽操作
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param duration //单位为毫秒
     */
    @Override
    public void swipe(float x1, float y1, float x2, float y2, float duration) {
        final int interval = 25;
        int steps = (int) (duration / interval + 1);
        float dx = (x2 - x1) / steps;
        float dy = (y2 - y1) / steps;
        down(x1, y1);
        for (int step = 0; step < steps; step++) {
            sleep(interval);
            moveTo(x1 + step * dx, y1 + step * dy, 0);
        }
        sleep(interval);
        up(x2, y2);
    }

    @Override
    public void input(String str) {

    }


    private void down(float x, float y) {
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));
    }


    private void up(float x, float y) {
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
    }


    private void moveTo(float x, float y, int contactId) {
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0));
    }


}
