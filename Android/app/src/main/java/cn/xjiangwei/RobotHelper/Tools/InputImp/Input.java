package cn.xjiangwei.RobotHelper.Tools.InputImp;


import cn.xjiangwei.RobotHelper.Tools.Point;

public interface Input {


    /**
     * 点击操作
     *
     * @param x
     * @param y
     */
    void tap(final int x, final int y);

    /**
     * 长按操作，可以自定义按下时间，单位为毫秒
     *
     * @param x
     * @param y
     * @param delay
     */
    void tap(final int x, final int y, final long delay);


    void tap(Point p);


    void tap(Point p, long delay);


    /**
     * 拖拽操作
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param duration //单位为毫秒
     */
    void swipe(int x1, int y1, int x2, int y2, int duration);

    void swipe(float x1, float y1, float x2, float y2, float duration);


    /**
     * 往输入框输入文字
     *
     * @param str
     */
    void input(String str);


    /**
     * 放大屏幕（双指捏开）
     *
     * @param distance // 距离  // 缩放距离，0到100
     */
    void pinchOpen(int distance);


    /**
     * 缩小屏幕（双指捏合）
     *
     * @param distance // 距离 // 缩放距离，0到100
     */
    void pinchClose(int distance);

}
