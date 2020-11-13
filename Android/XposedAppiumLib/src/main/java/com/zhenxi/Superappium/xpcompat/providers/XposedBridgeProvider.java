package com.zhenxi.Superappium.xpcompat.providers;

import com.zhenxi.Superappium.xpcompat.CompatMethodHook;
import com.zhenxi.Superappium.xpcompat.HookProvider;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XposedBridgeProvider implements HookProvider {
    @Override
    public void hookMethod(Member method, CompatMethodHook compatMethodHook) {
        XposedBridge.hookMethod(method, new Compat2Xp_MethodHook(compatMethodHook));
    }

    @Override
    public void log(String text) {
        XposedBridge.log(text);
    }

    @Override
    public void log(Throwable t) {
        XposedBridge.log(t);
    }

    private static class Compat2Xp_MethodHook extends XC_MethodHook {
        private CompatMethodHook delegate;

        public Compat2Xp_MethodHook(CompatMethodHook delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);

            CompatMethodHook.MethodHookParam methodHookParam = new CompatMethodHook.MethodHookParam();
            methodHookParam.method = param.method;
            methodHookParam.thisObject = param.thisObject;
            methodHookParam.args = param.args;
            XposedHelpers.setObjectField(methodHookParam, "result", param.getResult());
            XposedHelpers.setObjectField(methodHookParam, "throwable", param.getThrowable());
            XposedHelpers.setBooleanField(methodHookParam, "returnEarly",
                    XposedHelpers.getBooleanField(param, "returnEarly"));
            delegate.beforeHookedMethod(methodHookParam);
            param.args = methodHookParam.args;
            XposedHelpers.setObjectField(param, "result", methodHookParam.getResult());
            XposedHelpers.setObjectField(param, "throwable", methodHookParam.getThrowable());
            XposedHelpers.setBooleanField(param, "returnEarly", XposedHelpers.getBooleanField(methodHookParam, "returnEarly"));

        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);

            CompatMethodHook.MethodHookParam methodHookParam = new CompatMethodHook.MethodHookParam();
            methodHookParam.method = param.method;
            methodHookParam.thisObject = param.thisObject;
            methodHookParam.args = param.args;
            XposedHelpers.setObjectField(methodHookParam, "result", param.getResult());
            XposedHelpers.setObjectField(methodHookParam, "throwable", param.getThrowable());
            XposedHelpers.setBooleanField(methodHookParam, "returnEarly",
                    XposedHelpers.getBooleanField(param, "returnEarly"));
            delegate.afterHookedMethod(methodHookParam);
            param.args = methodHookParam.args;
            XposedHelpers.setObjectField(param, "result", methodHookParam.getResult());
            XposedHelpers.setObjectField(param, "throwable", methodHookParam.getThrowable());
            XposedHelpers.setBooleanField(param, "returnEarly", XposedHelpers.getBooleanField(methodHookParam, "returnEarly"));

        }
    }
}
