package cn.xjiangwei.RobotHelper.Tools.InputImp;

import android.Manifest;
import android.support.annotation.NonNull;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Tools.FileUtils;
import cn.xjiangwei.RobotHelper.Tools.Point;
import cn.xjiangwei.RobotHelper.Tools.Toast;

public class RootInput implements Input {


    private static RootInput instance;

    public static boolean BinFileStatus = false;

    public static RootInput getInstance() {
        if (instance == null) {
            synchronized (RootInput.class) {
                if (instance == null) {
                    instance = new RootInput();
                }
            }
        }
        return instance;
    }


    // 将sendevent移动到可执行位置
    private void initBinFile() {
        PermissionsUtil.requestPermission(MainApplication.getInstance(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                MainApplication application = MainApplication.getInstance();

                FileUtils.getInstance(application).copyAssetsToSD("sendevent", "sendevent");
                FileUtils.getInstance(application).setFileOperateCallback(new FileUtils.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        RootInput.BinFileStatus = true;
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Toast.show("授权失败");
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);


    }


    @Override
    public void tap(int x, int y) {

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
