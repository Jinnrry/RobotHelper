package cn.xjiangwei.RobotHelper.GamePackage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Tools.Image;
import cn.xjiangwei.RobotHelper.Tools.MLog;
import cn.xjiangwei.RobotHelper.Tools.Point;
import cn.xjiangwei.RobotHelper.Tools.Robot;
import cn.xjiangwei.RobotHelper.Tools.ScreenCaptureUtil;
import cn.xjiangwei.RobotHelper.Tools.TessactOcr;
import cn.xjiangwei.RobotHelper.Tools.Toast;

import static android.os.SystemClock.sleep;

public class Main {
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    /**
     * 在这个函数里面写你的业务逻辑
     */
    public void start() {
        sleep(5000); //点击开始后等待5秒后再执行，因为状态栏收起有动画时间，建议保留这行代码

        //Robot.setExecType(Robot.ExecTypeXposed);         //使用xposed权限执行模拟操作，建议优先使用此方式
        //Robot.setExecType(Robot.ExecTypeAccessibillty);  //使用安卓无障碍接口执行模拟操作


        /****************************  模板匹配demo  *******************************/
        InputStream is = null;
        try {
            is = MainApplication.getInstance().getAssets().open("ImgMatch.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        //在当前屏幕中查找模板图片
        Point point = Image.matchTemplate(ScreenCaptureUtil.getScreenCap(), bitmap, 0.1);
        MLog.info("找到模板", point.toString());
        // 点击找到的这个图
        Robot.tap(point);


        /**************************** 文字识别demo  **********************************/
        try {
            //识别素材文件中的ocrTest.png图片中的文字
            is = MainApplication.getInstance().getAssets().open("ocrTest.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(is);
        String res = TessactOcr.img2string(bitmap, "chi_sim", "", "");
        MLog.info("文字识别结果：" + res);


        /*****************************  特征点找图  ************************************/
        //当前屏幕中查找chrome图标（特征点是3120X1440分辨率手机制作）
        point = Image.findPointByMulColor(ScreenCaptureUtil.getScreenCap(), "434FD7,65|0|414DDB,90|55|46CDFF,5|86|5FA119");
        //点击chrome图标
        Robot.tap(point);


        /***** 提示  *****/
        Toast.show("运行结束！");
        //声音提示
        Toast.notice();

    }

}
