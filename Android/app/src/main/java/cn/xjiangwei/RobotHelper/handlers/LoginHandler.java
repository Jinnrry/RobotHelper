package cn.xjiangwei.RobotHelper.handlers;

import android.app.Activity;
import android.util.Log;

import com.zhenxi.Superappium.PageManager;
import com.zhenxi.Superappium.ViewImage;

/**
 * @author Zhenxi on 2020-07-03
 */
public class LoginHandler implements PageManager.ActivityFocusHandler {


    @Override
    public boolean handleActivity(Activity activity, ViewImage root) {

        Log.d("arik", "进入交易页面 ");
        ViewImage username = root.xpath2One("//android.widget.LinearLayout[@id='com.foundersc.app.xf:id/account_view']/android.widget.EditText[@id='com.foundersc.app.xf:id/internal_edit']");
        username.setText("75203155");

        ViewImage password = root.xpath2One("//android.widget.LinearLayout[@id='com.foundersc.app.xf:id/password_view']/android.widget.EditText[@id='com.foundersc.app.xf:id/internal_edit']");
        password.setText("159357");

        boolean isSucess = root.xpath2One("//android.widget.TextView[@id='com.foundersc.app.xf:id/normal_status_text']").parents().get(0).click();
        Log.d("arik", "点击登陆按钮完毕 " + isSucess);

        return true;
    }
}
