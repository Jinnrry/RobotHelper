package com.zhenxi.Superappium.xpcompat.providers;

import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.zhenxi.Superappium.xpcompat.CompatMethodHook;
import com.zhenxi.Superappium.xpcompat.HookProvider;

import java.lang.reflect.Member;


public class RatelBridgeProvider implements HookProvider {
    @Override
    public void hookMethod(Member method, CompatMethodHook compatMethodHook) {
        RposedBridge.hookMethod(method, new Compat2Ratel_MethodHook(compatMethodHook));
    }

    @Override
    public void log(String text) {
        RposedBridge.log(text);
    }

    @Override
    public void log(Throwable t) {
        RposedBridge.log(t);
    }

    private static class Compat2Ratel_MethodHook extends RC_MethodHook {
        private CompatMethodHook delegate;

        public Compat2Ratel_MethodHook(CompatMethodHook delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);

            CompatMethodHook.MethodHookParam methodHookParam = new CompatMethodHook.MethodHookParam();
            methodHookParam.method = param.method;
            methodHookParam.thisObject = param.thisObject;
            methodHookParam.args = param.args;
            RposedHelpers.setObjectField(methodHookParam, "result", param.getResult());
            RposedHelpers.setObjectField(methodHookParam, "throwable", param.getThrowable());
            RposedHelpers.setBooleanField(methodHookParam, "returnEarly",
                    RposedHelpers.getBooleanField(param, "returnEarly"));
            delegate.beforeHookedMethod(methodHookParam);
            param.args = methodHookParam.args;
            RposedHelpers.setObjectField(param, "result", methodHookParam.getResult());
            RposedHelpers.setObjectField(param, "throwable", methodHookParam.getThrowable());
            RposedHelpers.setBooleanField(param, "returnEarly", RposedHelpers.getBooleanField(methodHookParam, "returnEarly"));

        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);

            CompatMethodHook.MethodHookParam methodHookParam = new CompatMethodHook.MethodHookParam();
            methodHookParam.method = param.method;
            methodHookParam.thisObject = param.thisObject;
            methodHookParam.args = param.args;
            RposedHelpers.setObjectField(methodHookParam, "result", param.getResult());
            RposedHelpers.setObjectField(methodHookParam, "throwable", param.getThrowable());
            RposedHelpers.setBooleanField(methodHookParam, "returnEarly",
                    RposedHelpers.getBooleanField(param, "returnEarly"));
            delegate.afterHookedMethod(methodHookParam);
            param.args = methodHookParam.args;
            RposedHelpers.setObjectField(param, "result", methodHookParam.getResult());
            RposedHelpers.setObjectField(param, "throwable", methodHookParam.getThrowable());
            RposedHelpers.setBooleanField(param, "returnEarly", RposedHelpers.getBooleanField(methodHookParam, "returnEarly"));

        }
    }
}
