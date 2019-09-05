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
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import java.io.File;
import cn.xjiangwei.RobotHelper.Service.RunTime;
import cn.xjiangwei.RobotHelper.Tools.Robot;
import cn.xjiangwei.RobotHelper.Tools.ScreenCaptureUtilByMediaPro;


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
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

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

        EditText editText = findViewById(R.id.serverUrl);
        String serverUrl = editText.getText().toString();
        MainApplication.setServerUrl(serverUrl);

        ScreenCaptureUtilByMediaPro.init();
        Intent intent = new Intent(this, RunTime.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        finish();
    }


    public void test(View view) {
        Robot.tap(0, 0);

    }


    public void openLog(View view) {

        String file = Environment.getExternalStorageDirectory().toString() + "/RobotHelper.log";

        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(file)), "text/plain");
            startActivity(intent);
            Intent.createChooser(intent, "请选择对应的软件打开该附件！");
        } catch (ActivityNotFoundException e) {

        }
    }


}
