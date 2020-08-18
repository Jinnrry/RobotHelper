package cn.xjiangwei.RobotHelper.Tools;


import android.os.Build;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Tools.InputImp.AccessibilityInput;
import cn.xjiangwei.RobotHelper.Tools.InputImp.Input;
import cn.xjiangwei.RobotHelper.Tools.InputImp.InstrumentationInput;
import cn.xjiangwei.RobotHelper.Tools.InputImp.NullInput;


/**
 * 模拟操作的实现类
 * <p>
 * 目前只使用了xposed提权实现
 * <p>
 * 未来可能考虑加入root权限的实现
 */
public class Robot {


    private static int execType;

    public static final int ExecTypeXposed = 389;
    public static final int ExecTypeAccessibillty = 18;
    public static final int ExecTypeNull = 115;

    private static Input getInput() {
        MainApplication mainApplication = MainApplication.getInstance();

        // 没有手动设置模式的时候
        if (execType == 0) {
            // 有xposed权限优先使用xposed提权方式
            if (mainApplication.checkXposedHook()) {
                execType = ExecTypeXposed;
                return InstrumentationInput.getInstance();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mainApplication.checkAccessibilityService()) {
                execType = ExecTypeAccessibillty;
                return AccessibilityInput.getInstance();
            } else {
                return NullInput.getInstance();
            }
        } else {
            switch (execType) {
                case ExecTypeAccessibillty:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mainApplication.checkAccessibilityService()) {
                        return AccessibilityInput.getInstance();
                    }
                case ExecTypeXposed:
                    if (mainApplication.checkXposedHook()) {
                        return InstrumentationInput.getInstance();
                    }
                case ExecTypeNull:
                    return NullInput.getInstance();
                default:
                    return NullInput.getInstance();
            }

        }
    }

    /**
     * 设置以什么方式执行模拟操作
     * <p>
     * 目前支持无障碍和xposed提权操作
     *
     * @param execType
     */
    public static void setExecType(int execType) {
        Robot.execType = execType;
    }


    /**
     * 点击操作
     *
     * @param x
     * @param y
     */
    public static void tap(final int x, final int y) {
        getInput().tap(x, y);
    }

    /**
     * 长按操作，可以自定义按下时间，单位为毫秒
     *
     * @param x
     * @param y
     * @param delay
     */
    public static void tap(final int x, final int y, final long delay) {
        getInput().tap(x, y, delay);
    }


    public static void tap(Point p) {
        getInput().tap(p);
    }


    public static void tap(Point p, long delay) {
        getInput().tap(p, delay);
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
    public static void swipe(int x1, int y1, int x2, int y2, int duration) {
        getInput().swipe(x1, y1, x2, y2, duration);
    }

    public static void swipe(float x1, float y1, float x2, float y2, float duration) {
        getInput().swipe(x1, y1, x2, y2, duration);
    }


    /**
     * 往输入框输入文字
     *
     * @param str
     */
    public static void input(String str) {
        getInput().input(str);
    }

}
