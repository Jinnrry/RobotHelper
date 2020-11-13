package cn.xjiangwei.RobotHelper.handlers;

import android.app.Activity;
import android.util.Log;

import com.zhenxi.Superappium.PageManager;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;

public class StockTradeHandler implements PageManager.ActivityFocusHandler {
    @Override
    public boolean handleActivity(Activity activity, ViewImage root) {
        Log.d("arik", "交易页处理 ");
        root.xpath2One("//android.widget.TextView[@text='卖出']").click();

        ViewImages codes = root.xpath("//android.widget.EditText[@id='com.foundersc.app.xf:id/trade_stock_business_stock_code']");
        Log.d("arik", "输入个数" + codes.size());
        for (int i = 0; i < codes.size(); i++) {
            Log.d("arik", (codes.get(i).toString()));
        }
        codes.get(0).setText("603026");


        ViewImages targets = root.xpath("//android.widget.EditText[@id='com.foundersc.app.xf:id/edit_trade_value']");
        if (targets.size() == 4) {
            targets.get(0).setText("48.2");
            targets.get(1).setText("100");
        }

        boolean isSucess = root.xpath2One("//android.widget.Button[@id='com.foundersc.app.xf:id/button_deal']").click();
        Log.d("arik", ("点击交易按钮完毕 " + isSucess));

        return true;
    }
}
