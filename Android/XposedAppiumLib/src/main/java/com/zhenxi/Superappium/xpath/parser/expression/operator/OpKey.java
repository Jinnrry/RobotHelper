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
     * ��������־,xpath����������ݵ�ǰ���ַ��Ƿ���������,����ʹ�ö�Ӧ�Ĵ�������������
     *
     * @return ��Ӧ���ű��, ��"+","-","*","/","%",">","<",">=",">=","!="
     */
    String value();

    int priority();
}
