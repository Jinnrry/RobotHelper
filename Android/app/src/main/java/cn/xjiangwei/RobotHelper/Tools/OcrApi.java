package cn.xjiangwei.RobotHelper.Tools;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xjiangwei.RobotHelper.MainApplication;


/**
 * 建议使用TessactOcr替代
 */
@Deprecated
public class OcrApi {

    private static String ocrApiUrl = MainApplication.getServerUrl() + "singleLineOcr";
    private static String multOcrApiUrl = MainApplication.getServerUrl() + "multLineOcr";
    private static String testApiUrl = MainApplication.getServerUrl() + "testImg";
    private static int timeOut = 5000;



    public static String singLineOcr(Bitmap img, int leftTopX, int leftTopY, int rightBottomX, int rightBottomY) {
        img = Image.cropBitmap(img, leftTopX, leftTopY, rightBottomX, rightBottomY);
        String base64 = Image.encodeImage(img);
        String res = Post(ocrApiUrl, "img=" + base64);
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(res);
        res = m.replaceAll("");
        return res;
    }


    public static String multLineOcr(Bitmap img, int leftTopX, int leftTopY, int rightBottomX, int rightBottomY) {
        img = Image.cropBitmap(img, leftTopX, leftTopY, rightBottomX, rightBottomY);
        String base64 = Image.encodeImage(img);
        return Post(multOcrApiUrl, "img=" + base64);
    }

    private static String Post(String path, String paras)//string POST参数,get 请求的URL地址,context 联系上下文
    {

        String html;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeOut);//超时时间
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
//            conn.setRequestProperty("Content-Type", "application/json");
//      conn.setRequestProperty("User-Agent", Other.getUserAgent(context));
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(paras);
            out.flush();
            out.close();
            InputStream inputStream = conn.getInputStream();
            byte[] data = read(inputStream);
            html = new String(data, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            return "";
        }

        return html;
    }

    private static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


    public static void testImage(Bitmap bitmap) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String base64 = Image.encodeImage(bitmap);
                Post(testApiUrl, "img=" + base64);
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
