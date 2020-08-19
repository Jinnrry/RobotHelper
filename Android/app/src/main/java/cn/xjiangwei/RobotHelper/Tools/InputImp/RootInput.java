package cn.xjiangwei.RobotHelper.Tools.InputImp;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.InputDevice;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Tools.FileUtils;
import cn.xjiangwei.RobotHelper.Tools.Point;
import cn.xjiangwei.RobotHelper.Tools.ShellUtils;

public class RootInput implements Input {


    private static RootInput instance;
    private Process mProcess;
    private String sendeventPath;
    public static boolean NEED_ROOT = true;
    private String event;

    private final static int EV_SYN = 0;
    private final static int EV_ABS = 3;
    private final static int SYN_REPORT = 0;
    private final static int ABS_MT_SLOT = 47;
    private final static int ABS_MT_TOUCH_MAJOR = 48;
    private final static int ABS_MT_POSITION_X = 53;
    private final static int ABS_MT_POSITION_Y = 54;
    private final static int ABS_MT_TRACKING_ID = 57;
    private final static int ABS_MT_PRESSURE = 58;


    public static RootInput getInstance() {
        if (instance == null) {
            synchronized (RootInput.class) {
                if (instance == null) {
                    instance = new RootInput();
                    if (NEED_ROOT) {
                        instance.initBinFile();
                    }
                    instance.initShell();
                }
            }
        }
        return instance;
    }


    // 将sendevent移动到可执行位置
    private void initBinFile() {
        File tmp = new File(MainApplication.getInstance().getCacheDir(), "sendevent");
        FileUtils.copyAsset(MainApplication.getInstance(), "sendevent/arm64/sendevent", tmp.getAbsolutePath());
        ShellUtils.execCommand("chmod 777 " + tmp.getAbsolutePath(), true);
        sendeventPath = tmp.getAbsolutePath();
    }


    /**
     * 获取触摸屏对应的event文件
     *
     * @return
     */
    private String getTouchFile() {

        SharedPreferences sharedPreferences = MainApplication.getInstance().getSharedPreferences("touchEvent", Context.MODE_PRIVATE);

        String ret = sharedPreferences.getString("event", "");

        if (!"".equals(ret)) {
            event = ret;
            return ret;
        }


        String touchDeviceName = getTouchDevice().getName();

        ShellUtils.CommandResult result = ShellUtils.execCommand("cat /proc/bus/input/devices", true);
        String[] res = result.successMsg.split("\n");

        Pattern event_pattern = Pattern.compile("event\\d+");
        Pattern name_pattern = Pattern.compile("\".*\"");


        boolean findName = false;

        for (String l : res) {
            if (l.contains("N: Name=")) {
                Matcher matcher = name_pattern.matcher(l);
                matcher.find();
                if (("\"" + touchDeviceName + "\"").equals(matcher.group(0))) {
                    findName = true;
                }


            } else if (l.contains("H: Handlers=")) {
                Matcher matcher = event_pattern.matcher(l);
                matcher.find();

                if (findName) {

                    ret = matcher.group(0);

                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("event", ret);
                    edit.apply();
                    event = ret;
                    return ret;
                }
            }
        }


        return "";
    }

    private InputDevice getTouchDevice() {
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (supportSource(device, InputDevice.SOURCE_TOUCHSCREEN) || supportSource(device, InputDevice.SOURCE_TOUCHPAD)) {
                return device;
            }
        }
        return null;
    }

    private boolean supportSource(InputDevice device, int source) {
        return (device.getSources() & source) == source;
    }


    private void executeCommands(Process process, String[] commands) throws IOException {

        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        for (String command : commands) {
            if (command != null) {
                os.write(command.getBytes());
                os.writeBytes("\n");
            }
        }
        os.flush();

    }


    private void initShell() {

        if (event == null) {
            getTouchFile();
        }

        try {
            if (NEED_ROOT) {
                mProcess = Runtime.getRuntime().exec("su");
                String[] commands = {
                        sendeventPath + " /dev/input/" + event
                };
                executeCommands(mProcess, commands);
            } else {
                mProcess = Runtime.getRuntime().exec(sendeventPath + " /dev/input/" + event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void tap(int x, int y) {

        System.out.println("click");
        try {
            executeCommands(mProcess, new String[]{
                    "3 47 0",
                    "3 57 0",
                    "3 53 " + x,
                    "3 54 " + y,
                    "0 0 0",
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void tap(int x, int y, long delay) {

    }

    @Override
    public void tap(Point p) {

    }

    @Override
    public void tap(Point p, long delay) {

    }

    @Override
    public void swipe(int x1, int y1, int x2, int y2, int duration) {

    }

    @Override
    public void swipe(float x1, float y1, float x2, float y2, float duration) {

    }

    @Override
    public void input(String str) {

    }
}
