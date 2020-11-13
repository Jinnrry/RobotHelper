package com.zhenxi.Superappium;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;

import static android.view.MotionEvent.TOOL_TYPE_FINGER;
import static com.zhenxi.Superappium.SuperAppium.TAG;


/**
 * 事件滑动封装
 */
public class SwipeUtils {

    public static final int HIGH = 10;
    public static final int NORMAL = 100;
    public static final int LOW = 1000;

    private static final long DEFAULT_DURATION = 1500;

    /**
     * 模拟手势滑动
     *
     * @param view   滑动的 view
     * @param startX 起始位置 x
     * @param startY 起始位置 y
     * @param endX   终点位置 x
     * @param endY   终点位置 y
     */
    public static void simulateScroll(ViewImage view, int startX, int startY, int endX, int endY) {
        simulateScroll(view, startX, startY, endX, endY, DEFAULT_DURATION);
    }


    /**
     * 模拟手势滑动
     *
     * @param view     滑动的 view
     * @param startX   起始位置 x
     * @param startY   起始位置 y
     * @param endX     终点位置 x
     * @param endY     终点位置 y
     * @param duration 滑动时长 单位：ms
     */
    public static void simulateScroll(ViewImage view, int startX, int startY, int endX, int endY, long duration) {
        simulateScroll(view, startX, startY, endX, endY, duration, NORMAL);
    }


    /**
     * 模拟手势滑动
     *
     * @param view     滑动的 view
     * @param startX   起始位置 x
     * @param startY   起始位置 y
     * @param endX     终点位置 x
     * @param endY     终点位置 y
     * @param duration 滑动时长 单位：ms
     * @param period   滑动周期
     *                 {@link #LOW} 慢
     *                 {@link #NORMAL} 正常
     *                 {@link #HIGH} 高
     */
    public static void simulateScroll(ViewImage view, int startX, int startY, int endX, int endY, long duration, int period) {
        dealSimulateScroll(view, startX, startY, endX, endY, duration, period);
    }

    /**
     * 模拟手势滑动
     *
     * @param activity 当前的 activity
     * @param startX   起始位置 x
     * @param startY   起始位置 y
     * @param endX     终点位置 x
     * @param endY     终点位置 y
     * @param duration 滑动时长 单位 ms
     * @param period   滑动周期
     *                 {@link #LOW} 慢
     *                 <p>
     *                 {@link #NORMAL} 正常
     *                 {@link #HIGH} 高
     */
    public static void simulateScroll(ViewImage activity, float startX, float startY, float endX, float endY, long duration, int period) {
        dealSimulateScroll(activity, startX, startY, endX, endY, duration, period);
    }

    private static void dealSimulateScroll(ViewImage object, float startX, float startY, float endX, float endY, long duration, int period) {
        Log.i(TAG, "dealSimulateScroll:" + object);
        long downTime = SystemClock.uptimeMillis();
        Handler handler = new ViewHandler(object);
        object.getOriginView().dispatchTouchEvent(createFingerMotionEvent(downTime, downTime, MotionEvent.ACTION_DOWN, startX, startY, 0));
        GestureBean bean = new GestureBean(startX, startY, endX, endY, duration, period);
        Message.obtain(handler, 1, bean).sendToTarget();
    }


    static class ViewHandler extends Handler {
        WeakReference<ViewImage> mView;

        ViewHandler(ViewImage activity) {
            super(Looper.getMainLooper());
            mView = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ViewImage theView = mView.get();
            if (theView == null) {
                return;
            }
            long downTime = SystemClock.uptimeMillis();
            GestureBean bean = (GestureBean) msg.obj;
            long count = bean.count;
            if (count >= bean.totalCount) {
                theView.getOriginView().dispatchTouchEvent(createFingerMotionEvent(downTime, downTime, MotionEvent.ACTION_UP, bean.endX, bean.endY, 0));
            } else {
                theView.getOriginView().dispatchTouchEvent(createFingerMotionEvent(downTime, downTime, MotionEvent.ACTION_MOVE, bean.startX + bean.ratioX * count, bean.startY + bean.ratioY * count, 0));
                bean.count++;
                Message message = new Message();
                message.obj = bean;
                sendMessageDelayed(message, bean.period);
            }
        }
    }

    static class GestureBean {

        /**
         * 起始位置 X
         */
        float startX;
        /**
         * 起始位置 Y
         */
        float startY;
        /**
         * 终点位置 X
         */
        float endX;
        /**
         * 终点位置 Y
         */
        float endY;
        /**
         * 每个周期 x 移动的位置
         */
        float ratioX;
        /**
         * 每个周期 y 移动的位置
         */
        float ratioY;
        /**
         * 总共周期
         */
        long totalCount;
        /**
         * 当前周期
         */
        long count = 0;
        int period = NORMAL;

        GestureBean(float startX, float startY, float endX, float endY, long duration, int speed) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.period = speed;
            totalCount = duration / speed;
            ratioX = (endX - startX) / totalCount;
            ratioY = (endY - startY) / totalCount;
        }
    }


    /**
     * Create a new MotionEvent, filling in a subset of the basic motion
     * values.  Those not specified here are: device id (always 0), pressure
     * and size (always 1), x and y precision (always 1), and edgeFlags (always 0).
     *
     * @param downTime  The time (in ms) when the user originally pressed down to start
     *                  a stream of position events.  This must be obtained from {@link SystemClock#uptimeMillis()}.
     * @param eventTime The the time (in ms) when this specific event was generated.  This
     *                  must be obtained from {@link SystemClock#uptimeMillis()}.
     * @param action    The kind of action being performed, such as {@link MotionEvent#ACTION_DOWN}.
     * @param x         The X coordinate of this event.
     * @param y         The Y coordinate of this event.
     * @param metaState The state of any meta / modifier keys that were in effect when
     *                  the event was generated.
     */
    private static MotionEvent createFingerMotionEvent(long downTime, long eventTime, int action, float x, float y, int metaState) {
        MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
        pointerCoords.x = x;
        pointerCoords.y = y;
        MotionEvent.PointerProperties pointerProperties = new MotionEvent.PointerProperties();
        pointerProperties.id = 0;
        pointerProperties.toolType = TOOL_TYPE_FINGER;
        MotionEvent.PointerProperties[] pointerPropertiesArray = new MotionEvent.PointerProperties[]{pointerProperties};
        MotionEvent.PointerCoords[] pointerCoordsArray = new MotionEvent.PointerCoords[]{pointerCoords};
        // @param deviceId The id for the device that this event came from.  An id of zero indicates that the event didn't come from a physical device;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, 1, pointerPropertiesArray, pointerCoordsArray, 0, 0, 0, 0, 8, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        return motionEvent;
    }
}
