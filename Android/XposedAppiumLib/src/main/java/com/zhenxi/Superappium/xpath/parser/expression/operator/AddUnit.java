package com.zhenxi.Superappium.xpath.parser.expression.operator;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.XpathUtil;
import com.zhenxi.Superappium.xpath.parser.expression.node.AlgorithmUnit;

import java.math.BigDecimal;


/**
 * Created by virjar on 17/6/10.
 *
 * @author virjar
 * @since 0.0.1 �ӷ�����
 */
@OpKey(value = "+", priority = 20)
public class AddUnit extends AlgorithmUnit {

    @Override
    public Object calc(ViewImage element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
        if (leftValue == null || rightValue == null) {
            return XpathUtil.toPlainString(leftValue) + XpathUtil.toPlainString(rightValue);
        }
        // ���Ҷ���Ϊ��,��ʼ����
        // step one think as number
        if (leftValue instanceof Number && rightValue instanceof Number) {
            // ��������,��ִ�������ӷ�
            if (leftValue instanceof Integer && rightValue instanceof Integer) {
                return (Integer) leftValue + (Integer) rightValue;
            }

            // ����С��,תdoubleִ�мӷ�
            if (leftValue instanceof Double || rightValue instanceof Double || leftValue instanceof Float
                    || rightValue instanceof Float) {
                return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
            }

            // ����BigDecimal תbigDecimal
            if (leftValue instanceof BigDecimal || rightValue instanceof BigDecimal) {
                return XpathUtil.toBigDecimal((Number) leftValue).add(XpathUtil.toBigDecimal((Number) rightValue));
            }

            // ����������,�Ҳ�����С��,ȫ��ת��Ϊ����������
            if (leftValue instanceof Long || rightValue instanceof Long) {
                return ((Number) leftValue).longValue() + ((Number) rightValue).longValue();
            }

            // ����,��doubleִ�м���
            return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
        }

        // ��һ����������,ת��Ϊ�ַ�����������
        return XpathUtil.toPlainString(leftValue) + XpathUtil.toPlainString(rightValue);
    }
}
