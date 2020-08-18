package cn.xjiangwei.RobotHelper;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;

import cn.xjiangwei.RobotHelper.Service.RunTime;
import cn.xjiangwei.RobotHelper.Tools.MLog;
import cn.xjiangwei.RobotHelper.Tools.ScreenCaptureUtilByMediaPro;
import cn.xjiangwei.RobotHelper.Tools.TessactOcr;
import cn.xjiangwei.RobotHelper.Tools.Toast;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1050;
    private String TAG = "Service";
    private MainApplication mainApplication;
    private int mResultCode;
    private Intent mResultData;
    private MediaProjection mMediaProjection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainApplication = (MainApplication) getApplication();

        if (MainApplication.sceenWidth == 0 || MainApplication.sceenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            MainApplication.sceenHeight = Math.min(dm.heightPixels, dm.widthPixels);
            MainApplication.sceenWidth = Math.max(dm.heightPixels, dm.widthPixels);
            MainApplication.dpi = dm.densityDpi;
        }


        ScreenCaptureUtilByMediaPro.mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        // init
        startActivityForResult(ScreenCaptureUtilByMediaPro.mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                //  获得sdcard写入权限后 初始化tessactocr
                if (!TessactOcr.checkInit()) {
                    TessactOcr.Init();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);


        // 初始化opencv
        if (!OpenCVLoader.initDebug()) {
            MLog.info("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            MLog.info("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && REQUEST_CODE == requestCode) {
            ScreenCaptureUtilByMediaPro.data = data;
            ScreenCaptureUtilByMediaPro.resultCode = resultCode;
        }
    }


    public void start(View view) {
        if (!TessactOcr.checkInit()) {
            Toast.show("初始化中，Please Wait!");
            return;
        }
        // 启动屏幕监控
        ScreenCaptureUtilByMediaPro.init();
        Intent intent = new Intent(this, RunTime.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        finish();

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }


    public void openLog(View view) {


        String filePath = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";
        File file = new File(filePath);
        Uri fileURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(fileURI, "text/plain");
            startActivity(intent);
            Intent.createChooser(intent, "请选择对应的软件打开该附件！");
        } catch (ActivityNotFoundException e) {

        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    MLog.info("OpenCV", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    private void updateStatus() {
        TextView xpStatus = (TextView) findViewById(R.id.xposed_status);
        TextView asStatus = (TextView) findViewById(R.id.accessibility_status);
        TextView hsStatus = (TextView) findViewById(R.id.httpserver_status);

        xpStatus.setText(mainApplication.checkXposedHook() ? "Xposed状态：已加载" : "Xposed状态：未加载");
        asStatus.setText(mainApplication.checkAccessibilityService() ? "Accessibility状态：已加载" : "Accessibility状态：未加载");
        hsStatus.setText(mainApplication.checkHttpServer() ? "HttpServer状态：已开启" : "HttpServer状态：未开启");
    }


}
