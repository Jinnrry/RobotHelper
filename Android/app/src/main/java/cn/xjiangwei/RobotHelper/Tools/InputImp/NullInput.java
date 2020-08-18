package cn.xjiangwei.RobotHelper.Tools.InputImp;

import cn.xjiangwei.RobotHelper.Tools.MLog;
import cn.xjiangwei.RobotHelper.Tools.Point;
import cn.xjiangwei.RobotHelper.Tools.Toast;

public class NullInput implements Input {

    private static NullInput instance;

    public static NullInput getInstance() {
        if (instance == null) {
            synchronized (NullInput.class) {
                if (instance == null) {
                    instance = new NullInput();
                }
            }
        }
        return instance;
    }


    @Override
    public void tap(int x, int y) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }

    @Override
    public void tap(int x, int y, long delay) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }

    @Override
    public void tap(Point p) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }

    @Override
    public void tap(Point p, long delay) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }

    @Override
    public void swipe(int x1, int y1, int x2, int y2, int duration) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }

    @Override
    public void swipe(float x1, float y1, float x2, float y2, float duration) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }

    @Override
    public void input(String str) {
        MLog.error("没有权限执行操作！请检查xposed或者无障碍权限！");
        Toast.show("没有权限执行操作！请检查xposed或者无障碍权限！");
    }
}
