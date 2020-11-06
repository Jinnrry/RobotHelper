package cn.xjiangwei.RobotHelper.Tools;


import android.content.res.Configuration;
import android.graphics.Bitmap;

import cn.xjiangwei.RobotHelper.MainApplication;


/**
 * 截图工具类
 */
public class ScreenCaptureUtil {

    private static int screenOrientation;   // 0 未设置  ，1竖屏    2横批


    /**
     * 强制设置为横屏模式
     */
    public static void setHorizontalScreen() {
        ScreenCaptureUtil.screenOrientation = 2;
    }


    /**
     * 强制设置为竖屏模式
     */
    public static void setVerticalScreen() {
        ScreenCaptureUtil.screenOrientation = 1;
    }


    /**
     * 获取屏幕图像
     *
     * @return
     */
    public static Bitmap getScreenCap() {
        Bitmap bitmap;

        if (ScreenCaptureUtil.screenOrientation == 0) {
            if (MainApplication.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                bitmap = Bitmap.createBitmap(ScreenCaptureUtilByMediaPro.getScreenCapVertical());

            } else {
                //横屏
                bitmap = Bitmap.createBitmap(ScreenCaptureUtilByMediaPro.getScreenCapHorizontal());
            }
        } else if (ScreenCaptureUtil.screenOrientation == 1) {
            bitmap = Bitmap.createBitmap(ScreenCaptureUtilByMediaPro.getScreenCapVertical());
        } else {
            bitmap = Bitmap.createBitmap(ScreenCaptureUtilByMediaPro.getScreenCapHorizontal());
        }


        return bitmap;


//         使用adb方式截图，性能低下，已废弃
//        if (System.currentTimeMillis() - getScreenTime < 1000) {
//            return screenCache;
//        }
//        byte[] tempBuffer = new byte[100 * 1024 * 1024];
//        StringBuilder buffer = new StringBuilder(100 * 1024 * 1024);
//
//        Process exec = null;
//        try {
//            exec = Runtime.getRuntime().exec("su -c /system/bin/screencap -p");
//
//
//            final InputStream inputStream = exec.getInputStream();
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//            //清空缓存内容
//            buffer.setLength(0);
//            int count;
//            while ((count = bufferedInputStream.read(tempBuffer)) > 0) {
//                buffer.append(new String(tempBuffer, 0, count, StandardCharsets.ISO_8859_1));
//            }
//            bufferedInputStream.close();
//            final int retCode = exec.waitFor();
//            exec.destroy();
//            tempBuffer = buffer.toString().getBytes(StandardCharsets.ISO_8859_1);
//            screenCache = BitmapFactory.decodeByteArray(tempBuffer, 0, tempBuffer.length);
//            getScreenTime = System.currentTimeMillis();
//            return screenCache;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        throw new NullPointerException("截图失败");


    }

    /**
     * 指定范围截图
     *
     * @param leftX
     * @param leftY
     * @param rigthX
     * @param rightY
     * @return
     */
    public static Bitmap getScreenCap(int leftX, int leftY, int rigthX, int rightY) {
        Bitmap bitmap = getScreenCap();
        return Image.cropBitmap(bitmap, leftX, leftY, rigthX, rightY);
    }


}