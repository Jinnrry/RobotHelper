package cn.xjiangwei.RobotHelper.wework;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyGetPostListCallBack implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("onResult")) {
            Log.d("arik", "invoke: +++++hiahiahiahia onNegativeTest");
        } else if (method.getName().equals("onNegativeClick")) {
            Log.d("arik", "invoke: -----hiahiahiahia onNegativeClick");
        }
        return null;
    }
}
