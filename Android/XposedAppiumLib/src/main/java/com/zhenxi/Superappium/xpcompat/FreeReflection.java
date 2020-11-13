package com.zhenxi.Superappium.xpcompat;

import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ratelEngine 1.3.2֮�󣬽���ȡ�� hidden policy bypass����ʱRposedHelper�ķ���������ܻᱻ��ϡ�
 * �ṩ��������࣬����ʵ��hidden policy���ƹ�
 * <p>
 * ���������android9(api level = 28)֮��Ż����
 */
public class FreeReflection {
    private static Method getMethodMethod = null;
    // private static Method forNameMethod = null;
    private static Method getFiledMethod = null;

    private static Method getDeclaredConstructorMethod = null;

    private static Method getDeclaredConstructorsMethod = null;

    private static Method getDeclaredMethodsMethod = null;

    private static Method getDeclaredFieldsMethod = null;

    static {
        init();
    }

    private static void init() {
        try {
            getMethodMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            // forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            getFiledMethod = Class.class.getDeclaredMethod("getDeclaredField", String.class);
            getDeclaredConstructorMethod = Class.class.getDeclaredMethod("getDeclaredConstructor", Class[].class);
            getDeclaredFieldsMethod = Class.class.getDeclaredMethod("getDeclaredFields");

            //java.lang.Class.getDeclaredConstructors
            getDeclaredConstructorsMethod = Class.class.getDeclaredMethod("getDeclaredConstructors");

            getDeclaredMethodsMethod = Class.class.getDeclaredMethod("getDeclaredMethods");

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static boolean use() {
        return Build.VERSION.SDK_INT >= 28;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (use()) {
            try {
                return (Method) getMethodMethod.invoke(clazz, methodName, parameterTypes);
            } catch (Throwable e) {
                //ignore
            }
        }
        return clazz.getDeclaredMethod(methodName, parameterTypes);
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) throws SecurityException {
        if (use()) {
            try {
                return (Method[]) getDeclaredMethodsMethod.invoke(clazz);
            } catch (Throwable e) {
                //ignore
            }
        }
        return clazz.getDeclaredMethods();
    }

    public static Constructor<?> getDeclaredConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (use()) {
            try {
                return (Constructor) getDeclaredConstructorMethod.invoke(clazz, (Object) parameterTypes);
            } catch (Throwable e) {
                //ignore
            }
        }
        return clazz.getDeclaredConstructor(parameterTypes);
    }

    public static Constructor<?>[] getDeclaredConstructors(Class<?> clazz) throws SecurityException {
        if (use()) {
            try {
                return (Constructor<?>[]) getDeclaredConstructorsMethod.invoke(clazz);
            } catch (Throwable e) {
                //ignore
                e.printStackTrace();
            }
        }
        return clazz.getDeclaredConstructors();
    }


    public static Field getDeclaredField(Class<?> clazz, String field) throws NoSuchFieldException {
        if (use()) {
            try {
                return (Field) getFiledMethod.invoke(clazz, field);
            } catch (Throwable e) {
                //ignore
            }
        }
        return clazz.getDeclaredField(field);
    }

    public static Field[] getDeclaredField(Class<?> clazz) {
        if (use()) {
            try {
                return (Field[]) getDeclaredFieldsMethod.invoke(clazz);
            } catch (Throwable e) {
                //ignore
            }
        }
        return clazz.getDeclaredFields();
    }
}
