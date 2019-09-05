package cn.xjiangwei.RobotHelper.GamePackage;

import cn.xjiangwei.RobotHelper.Tools.Image;
import cn.xjiangwei.RobotHelper.Tools.MLog;
import cn.xjiangwei.RobotHelper.Tools.OcrApi;
import cn.xjiangwei.RobotHelper.Tools.Point;
import cn.xjiangwei.RobotHelper.Tools.Robot;
import cn.xjiangwei.RobotHelper.Tools.ScreenCaptureUtil;
import cn.xjiangwei.RobotHelper.Tools.Toast;

import static android.os.SystemClock.sleep;

public class Main {


    /**
     * 在这个函数里面写你的业务逻辑
     */
    public void start() {
        sleep(5000);

        // 点击状态栏
        Robot.tap(0,0);

        Toast.show("运行结束！");
        Toast.notice();
    }

}
