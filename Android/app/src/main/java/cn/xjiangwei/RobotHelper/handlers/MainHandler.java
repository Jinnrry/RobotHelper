package cn.xjiangwei.RobotHelper.handlers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.zhenxi.Superappium.PageManager;
import com.zhenxi.Superappium.ViewImage;

/**
 * @author Zhenxi on 2020-07-03
 */
public class MainHandler implements PageManager.ActivityFocusHandler {

    @Override
    public boolean handleActivity(Activity activity, ViewImage root) {

        Log.d("arik", "首页面处理 ");

        Intent intent = new Intent();
        intent.setClassName(activity.getApplicationContext(), "com.foundersc.trade.login.TradeLoginActivity");
        activity.startActivity(intent);


//        root.clickByXpath("//android.widget.Button[@id='com.foundersc.app.xf:id/login']");

        //判断是否消费此次事件，返回false则再次执行
        return true;
    }
}
