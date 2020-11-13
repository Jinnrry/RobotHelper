package com.zhenxi.Superappium.xmodel;

import com.zhenxi.Superappium.ViewImage;

/**
 * 提供了从ViewImage中获取内容的功能
 *
 * @param <T> 返回数据类型
 */
public interface ValueGetter<T> {
    T get(ViewImage viewImage);

    boolean support(Class type);

    String attr();
}
