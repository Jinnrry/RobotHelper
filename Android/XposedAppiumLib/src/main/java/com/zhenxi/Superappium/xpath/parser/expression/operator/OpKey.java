package com.zhenxi.Superappium.xpath.parser.expression.operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by virjar on 17/6/10.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OpKey {
    /**
     * 操作符标志,xpath解析器会根据当前的字符是否是这个标记,决定使用对应的处理器进行运算
     *
     * @return 对应符号标记, 如"+","-","*","/","%",">","<",">=",">=","!="
     */
    String value();

    int priority();
}
