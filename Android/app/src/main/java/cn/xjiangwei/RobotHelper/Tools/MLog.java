package cn.xjiangwei.RobotHelper.Tools;

import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MLog {

    private static final boolean debug = false;

    private static String Tag = "RobotHelper";

    private static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());

        return formatter.format(curDate);
    }

    public static void error(String msg) {
        if (debug) {
            Log.e(Tag, MLog.GetDate() + ":" + msg);
        } else {
            String storageDir = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
            writeLog2file(storageDir, MLog.GetDate() + ":" + msg);
        }

    }

    public static void error(int[] msg) {
        if (debug) {
            Log.e(Tag, MLog.GetDate() + ":" + Arrays.toString(msg));
        } else {
            String storageDir = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
            writeLog2file(storageDir, MLog.GetDate() + ":" + Arrays.toString(msg));
        }
    }


    public static void error(String tag, String msg) {
        if (debug) {
            Log.e(tag, MLog.GetDate() + ":" + msg);
        } else {
            String storageDir = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
            writeLog2file(storageDir, MLog.GetDate() + ":" + tag + ":" + msg);
        }

    }

    public static void info(String msg) {
        if (debug) {
            Log.i(Tag, MLog.GetDate() + ":" + msg);
        } else {
            String storageDir = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
            writeLog2file(storageDir, MLog.GetDate() + ":" + msg);
        }
    }

    public static void info(String tag, String msg) {
        if (debug) {
            Log.i(tag, MLog.GetDate() + ":" + msg);
        } else {
            String storageDir = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
            writeLog2file(storageDir, tag + ":" + msg);
        }
    }

    /**
     * 写日志到本地
     *
     * @param msg
     */
    public static void LocalLog(String msg) {
        String storageDir = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
        System.out.println(storageDir);
        writeLog2file(storageDir, MLog.GetDate() + ":" + msg + "\n");
    }


    public static void writeLog2file(String path, String content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(path, true);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
