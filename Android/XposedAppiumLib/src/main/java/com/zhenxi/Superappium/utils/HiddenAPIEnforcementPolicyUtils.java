package com.zhenxi.Superappium.utils;

import android.os.Build;
import android.util.Log;

import com.zhenxi.Superappium.SuperAppium;

import java.lang.reflect.Method;

//
// Created by zhenxi  on 2020/3/15.
//

public class HiddenAPIEnforcementPolicyUtils {

    private static Method addWhiteListMethod;

    private static Object vmRuntime;

    private static boolean hasInit = false;

    private static void init() {
        try {
            //先反射forname方法利用系统级权限直接拿到 VMRuntime
            Method getMethodMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            Method forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            Class vmRuntimeClass = (Class) forNameMethod.invoke(null, "dalvik.system.VMRuntime");
            addWhiteListMethod = (Method) getMethodMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            Method getVMRuntimeMethod = (Method) getMethodMethod.invoke(vmRuntimeClass, "getRuntime", null);
            vmRuntime = getVMRuntimeMethod.invoke(null);
            hasInit = true;
        } catch (Throwable e) {
            Log.e(SuperAppium.TAG, "error get methods"+ e.getMessage());
        }
    }


    public static void passApiCheck() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        if (!hasInit) {
            init();
        }

        try {
            addReflectionWhiteList("Landroid/",
                    "Lcom/android/",
                    "Ljava/lang/",
                    "Ldalvik/system/",
                    "Llibcore/io/",
                    "Lsun/misc/"
            );
        } catch (Throwable throwable) {
            Log.e(SuperAppium.TAG, "pass Hidden API enforcement policy failed  " + throwable.getMessage());
        }
    }

    public static void reverseApiCheck() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        if (!hasInit) {
            init();
        }
        try {
            //只保留java.lang.* 避免apk通过hidden api policy进行检测
            addReflectionWhiteList("Ljava/lang/");
        } catch (Throwable throwable) {
            Log.e(SuperAppium.TAG, "reverseApiCheck failed", throwable);
        }
    }

    //methidSigs like Lcom/swift/sandhook/utils/ReflectionUtils;->vmRuntime:java/lang/Object; (from hidden policy list)
    private static void addReflectionWhiteList(String... memberSigs) throws Throwable {
        addWhiteListMethod.invoke(vmRuntime, new Object[]{memberSigs});
    }
}
