package cn.xjiangwei.RobotHelper.Tools.InputImp;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Tools.Point;

import static android.os.SystemClock.sleep;

public class InstrumentationInput implements Input {

    private static volatile InstrumentationInput instance;

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

    /**
     * https://github.com/Jinnrry/RobotHelper/issues/13
     *
     * @param distance // 距离  // 缩放距离，0到100
     */
    @Override
    public void pinchOpen(int distance) {

        if (distance > 100) {
            distance = 100;
        }

        if (distance < 0) {
            distance = 0;
        }


        final int center_X = MainApplication.sceenWidth / 2;
        final int center_Y = MainApplication.sceenHeight / 2;

        int point_x1 = 100;  // 这里最好不要硬编码，小屏幕会出问题
        int point_x2 = 700;  // 这里最好不要硬编码，小屏幕会出问题


        MotionEvent.PointerCoords pOneStart = new MotionEvent.PointerCoords();
        pOneStart.pressure = 1;
        pOneStart.x = (point_x2 + point_x1) / 2;
        pOneStart.y = center_Y;
        pOneStart.size = 1;

        MotionEvent.PointerCoords pTwoStart = new MotionEvent.PointerCoords();
        pTwoStart.pressure = 1;
        pTwoStart.x = (point_x2 + point_x1) / 2;
        pTwoStart.y = center_Y;
        pTwoStart.size = 1;


        MotionEvent.PointerProperties pProp1 = new MotionEvent.PointerProperties();
        pProp1.id = 0;
        pProp1.toolType = MotionEvent.TOOL_TYPE_FINGER;

        MotionEvent.PointerProperties pProp2 = new MotionEvent.PointerProperties();
        pProp2.id = 1;
        pProp2.toolType = MotionEvent.TOOL_TYPE_FINGER;


        MotionEvent.PointerCoords[] pCordStart = new MotionEvent.PointerCoords[]{pOneStart, pTwoStart};
        MotionEvent.PointerProperties[] pProp = new MotionEvent.PointerProperties[]{pProp1, pProp2};


        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_DOWN, 1, pProp, pCordStart, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);

        event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_POINTER_2_DOWN, 2, pProp, pCordStart, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);


        // 一共一百步
        for (int i = 0; i < distance; i++) {
            MotionEvent.PointerCoords pOneTemp = new MotionEvent.PointerCoords();
            pOneTemp.pressure = 1;
            pOneTemp.x = (point_x2 + point_x1) / 2 + (i * ((float) point_x2 - point_x1) / 200);

            pOneTemp.y = center_Y;
            pOneTemp.size = 1;
            MotionEvent.PointerCoords pTwoTemp = new MotionEvent.PointerCoords();
            pTwoTemp.pressure = 1;
            pTwoTemp.x = (point_x2 + point_x1) / 2 - (i * ((float) point_x2 - point_x1) / 200);

            pTwoTemp.y = center_Y;
            pTwoTemp.size = 1;
            MotionEvent.PointerCoords[] pCordTemp = new MotionEvent.PointerCoords[]{pOneTemp, pTwoTemp};


            event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE, 2, pProp, pCordTemp, 0, 0, 1, 1, 0, 0, 0, 0);
            mInst.sendPointerSync(event);
            sleep(25);
        }


        MotionEvent.PointerCoords pOneEnd = new MotionEvent.PointerCoords();
        pOneEnd.pressure = 1;
        pOneEnd.x = point_x2;
        pOneEnd.y = center_Y;
        pOneEnd.size = 1;

        MotionEvent.PointerCoords pTwoEnd = new MotionEvent.PointerCoords();
        pTwoEnd.pressure = 1;
        pTwoEnd.x = point_x1;
        pTwoEnd.y = center_Y;
        pTwoEnd.size = 1;
        MotionEvent.PointerCoords[] pCordEnd = new MotionEvent.PointerCoords[]{pOneEnd, pTwoEnd};


        event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_POINTER_2_UP, 2, pProp, pCordEnd, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);


        event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_UP, 1, pProp, pCordEnd, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);


    }

    /**
     * https://github.com/Jinnrry/RobotHelper/issues/13
     *
     * @param distance // 距离 // 缩放距离，0到100
     */
    @Override
    public void pinchClose(int distance) {

        if (distance > 100) {
            distance = 100;
        }

        if (distance < 0) {
            distance = 0;
        }


        final int center_X = MainApplication.sceenWidth / 2;
        final int center_Y = MainApplication.sceenHeight / 2;

        int point_x1 = 100;  // 这里最好不要硬编码，小屏幕会出问题
        int point_x2 = 700;  // 这里最好不要硬编码，小屏幕会出问题


        MotionEvent.PointerCoords pOneStart = new MotionEvent.PointerCoords();
        pOneStart.pressure = 1;
        pOneStart.x = point_x1;
        pOneStart.y = center_Y;
        pOneStart.size = 1;

        MotionEvent.PointerCoords pTwoStart = new MotionEvent.PointerCoords();
        pTwoStart.pressure = 1;
        pTwoStart.x = point_x2;
        pTwoStart.y = center_Y;
        pTwoStart.size = 1;


        MotionEvent.PointerProperties pProp1 = new MotionEvent.PointerProperties();
        pProp1.id = 0;
        pProp1.toolType = MotionEvent.TOOL_TYPE_FINGER;

        MotionEvent.PointerProperties pProp2 = new MotionEvent.PointerProperties();
        pProp2.id = 1;
        pProp2.toolType = MotionEvent.TOOL_TYPE_FINGER;


        MotionEvent.PointerCoords[] pCordStart = new MotionEvent.PointerCoords[]{pOneStart, pTwoStart};
        MotionEvent.PointerProperties[] pProp = new MotionEvent.PointerProperties[]{pProp1, pProp2};


        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_DOWN, 1, pProp, pCordStart, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);

        event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_POINTER_2_DOWN, 2, pProp, pCordStart, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);


        for (int i = 0; i < distance; i++) {
            MotionEvent.PointerCoords pOneTemp = new MotionEvent.PointerCoords();
            pOneTemp.pressure = 1;
            pOneTemp.x = point_x1 + (i * ((float) point_x2 - point_x1) / 200);

            pOneTemp.y = center_Y;
            pOneTemp.size = 1;
            MotionEvent.PointerCoords pTwoTemp = new MotionEvent.PointerCoords();
            pTwoTemp.pressure = 1;
            pTwoTemp.x = point_x2 - (i * ((float) point_x2 - point_x1) / 200);

            pTwoTemp.y = center_Y;
            pTwoTemp.size = 1;
            MotionEvent.PointerCoords[] pCordTemp = new MotionEvent.PointerCoords[]{pOneTemp, pTwoTemp};


            event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE, 2, pProp, pCordTemp, 0, 0, 1, 1, 0, 0, 0, 0);
            mInst.sendPointerSync(event);
            sleep(25);
        }


        MotionEvent.PointerCoords pOneEnd = new MotionEvent.PointerCoords();
        pOneEnd.pressure = 1;
        pOneEnd.x = (point_x2 + point_x1) / 2;
        pOneEnd.y = center_Y;
        pOneEnd.size = 1;

        MotionEvent.PointerCoords pTwoEnd = new MotionEvent.PointerCoords();
        pTwoEnd.pressure = 1;
        pTwoEnd.x = (point_x2 + point_x1) / 2;
        pTwoEnd.y = center_Y;
        pTwoEnd.size = 1;
        MotionEvent.PointerCoords[] pCordEnd = new MotionEvent.PointerCoords[]{pOneEnd, pTwoEnd};


        event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_POINTER_2_UP, 2, pProp, pCordEnd, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);


        event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 25,
                MotionEvent.ACTION_UP, 1, pProp, pCordEnd, 0, 0, 1, 1, 0, 0, 0, 0);
        mInst.sendPointerSync(event);


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
