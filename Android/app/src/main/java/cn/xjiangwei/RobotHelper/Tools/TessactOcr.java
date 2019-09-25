package cn.xjiangwei.RobotHelper.Tools;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cn.xjiangwei.RobotHelper.MainApplication;

import static android.os.SystemClock.sleep;

public class TessactOcr {

    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    public static void Init() {

        MainApplication application = MainApplication.getInstance();
        String[] list = new String[0];
        try {
            list = application.getAssets().list("tessdata");
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileUtils.getInstance(application).copyAssetsToSD("tessdata", "tessdata");
        FileUtils.getInstance(application).setFileOperateCallback(new FileUtils.FileOperateCallback() {
            @Override
            public void onSuccess() {
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(SD_PATH + "/tessdata/flag");
                    byte[] bytes = "version:1.0".getBytes();

                    fout.write(bytes);
                    fout.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String error) {

            }
        });


    }


    public static boolean checkInit() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(SD_PATH + "/tessdata/flag");
            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);
            String res = new String(buffer, StandardCharsets.UTF_8);
            fin.close();
            return "version:1.0".equals(res);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * @param img       //需要识别的图片
     * @param lang      // 文字语言，默认只支持了eng和chi_sim
     * @param whitelist //白名单
     * @param blacklist //黑名单
     * @return String
     */
    public static String img2string(Bitmap img, String lang, String whitelist, String blacklist) {
        if (img == null) {
            return "";
        }
        if (!checkInit()) {
            sleep(500);
            return img2string(img, lang, whitelist, blacklist);
        }


        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        // 使用默认语言初始化BaseApi
        tessBaseAPI.init(SD_PATH + "/", lang);
        if (whitelist != null && !whitelist.equals("")) {
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whitelist); // 识别白名单
        }

        if (blacklist != null && !"".equals(blacklist)) {
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blacklist);  //识别黑名单
        }

        tessBaseAPI.setImage(img);
        // 获取返回值
        String recognizedText = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();

        return recognizedText;
    }


}
