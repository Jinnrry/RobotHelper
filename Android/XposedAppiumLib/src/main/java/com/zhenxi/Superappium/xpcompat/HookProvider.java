package com.zhenxi.Superappium.xpcompat;

import java.lang.reflect.Member;

/**
 * xp ���ݲ㣬���ںܶ��û���xp�Ķ��ƣ�����api�����޷�ֱ����xp����ʹ��<br>
 * ����������һ�����ͨ�����·��ֱ�ӶԽӸ���xp like����
 */
public interface HookProvider {
    void hookMethod(Member method, CompatMethodHook compatMethodHook);

    void log(String text);

    void log(Throwable t);
}
