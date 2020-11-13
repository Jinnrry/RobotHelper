package com.zhenxi.Superappium.xpcompat;

import java.lang.reflect.Member;

/**
 * xp 兼容层，由于很多用户有xp的定制，导致api可能无法直接在xp上面使用<br>
 * 所以这里做一层抽象，通过这层路由直接对接各种xp like方案
 */
public interface HookProvider {
    void hookMethod(Member method, CompatMethodHook compatMethodHook);

    void log(String text);

    void log(Throwable t);
}
