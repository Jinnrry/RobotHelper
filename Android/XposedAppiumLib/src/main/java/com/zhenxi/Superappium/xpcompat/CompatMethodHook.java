package com.zhenxi.Superappium.xpcompat;

import java.lang.reflect.Member;

public abstract class CompatMethodHook {


    /**
     * Called before the invocation of the method.
     * <p>
     * <p>You can use {@link MethodHookParam#setResult} and {@link MethodHookParam#setThrowable}
     * to prevent the original method from being called.
     * <p>
     * <p>Note that implementations shouldn't call {@code super(param)}, it's not necessary.
     *
     * @param param Information about the method call.
     * @throws Throwable Everything the callback throws is caught and logged.
     */
    public void beforeHookedMethod(MethodHookParam param) throws Throwable {
    }

    /**
     * Called after the invocation of the method.
     * <p>
     * <p>You can use {@link MethodHookParam#setResult} and {@link MethodHookParam#setThrowable}
     * to modify the return value of the original method.
     * <p>
     * <p>Note that implementations shouldn't call {@code super(param)}, it's not necessary.
     *
     * @param param Information about the method call.
     * @throws Throwable Everything the callback throws is caught and logged.
     */
    public void afterHookedMethod(MethodHookParam param) throws Throwable {
    }


    public static class MethodHookParam {
        /**
         * The hooked method/constructor.
         */
        public Member method;

        /**
         * The {@code this} reference for an instance method, or {@code null} for static methods.
         */
        public Object thisObject;

        /**
         * Arguments to the method call.
         */
        public Object[] args;

        private Object result = null;
        private Throwable throwable = null;
        public boolean returnEarly = false;

        public Class<?>[] parameterTypes = null;

        /**
         * Returns the result of the method call.
         */
        public Object getResult() {
            return result;
        }

        /**
         * Modify the result of the method call.
         * <p>
         * <p>If called from {@link #beforeHookedMethod}, it prevents the call to the original method.
         */
        public void setResult(Object result) {
            this.result = result;
            this.throwable = null;
            this.returnEarly = true;
        }

        /**
         * Returns the {@link Throwable} thrown by the method, or {@code null}.
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Returns true if an exception was thrown by the method.
         */
        public boolean hasThrowable() {
            return throwable != null;
        }

        /**
         * Modify the exception thrown of the method call.
         * <p>
         * <p>If called from {@link #beforeHookedMethod}, it prevents the call to the original method.
         */
        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
            this.result = null;
            this.returnEarly = true;
        }

        /**
         * Returns the result of the method call, or throws the Throwable caused by it.
         */
        public Object getResultOrThrowable() throws Throwable {
            if (throwable != null)
                throw throwable;
            return result;
        }
    }
}
