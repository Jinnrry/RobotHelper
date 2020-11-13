package com.zhenxi.Superappium.xpcompat;

import com.zhenxi.Superappium.xpcompat.providers.RatelBridgeProvider;
import com.zhenxi.Superappium.xpcompat.providers.XposedBridgeProvider;

import java.lang.reflect.Member;

public class XpCompatEngine {
    private static HookProvider sHookProvider = null;

    public static void setupProvider(HookProvider hookProvider) {
        sHookProvider = hookProvider;
    }

    public static void hookMethod(Member method, CompatMethodHook compatMethodHook) {
        getsHookProvider().hookMethod(method, compatMethodHook);
    }

    public static void hookAllConstructors(Class<?> hookClass, CompatMethodHook callback) {
        for (Member constructor : hookClass.getDeclaredConstructors())
            hookMethod(constructor, callback);
    }


    public static void log(String text) {
        getsHookProvider().log(text);
    }

    public static void log(Throwable t) {
        getsHookProvider().log(t);
    }

    public static HookProvider getsHookProvider() {
        if (sHookProvider != null) {
            return sHookProvider;
        }

        try {
            Class.forName("de.robv.android.xposed.XposedBridge");
            // 默认支持在 xp环境下运行，如果有自定义hook框架，那么需要实现 com.zhenxi.Superappium.xpcompat.HookProvider
            sHookProvider = new XposedBridgeProvider();
            return sHookProvider;
        } catch (Throwable throwable) {
            //ignore
        }

        //com.virjar.ratel.api.rposed.RposedBridge
        try {
            Class.forName("com.virjar.ratel.api.rposed.RposedBridge");
            //如果运行在平头哥框架下，那么自动路由到平头哥
            sHookProvider = new RatelBridgeProvider();
            return sHookProvider;
        } catch (Throwable throwable) {
            //ignore
        }
        throw new IllegalStateException("no hook provider setup, and not running xposed environment");
    }
}
