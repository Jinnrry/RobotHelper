package com.zhenxi.Superappium.xpath.parser.expression.operator;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.XpathUtil;
import com.zhenxi.Superappium.xpath.exception.EvaluateException;
import com.zhenxi.Superappium.xpath.parser.expression.node.AlgorithmUnit;

import java.math.BigDecimal;


/**
 * Created by virjar on 17/6/10.
 *
 * @author virjar
 * @since 0.0.1 ģ������
 */
@OpKey(value = "%", priority = 30)
public class RemainderUnit extends AlgorithmUnit {

    @Override
    public Object calc(ViewImage element) {

        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
        if (leftValue == null || rightValue == null) {
            throw new EvaluateException("operate is null,left: " + leftValue + "  right:" + rightValue);
        }
        // ���Ҷ���Ϊ��,��ʼ����
        // step one think as number
        if (leftValue instanceof Number && rightValue instanceof Number) {
            // ��������,��ִ����������
            if (leftValue instanceof Integer && rightValue instanceof Integer) {
                return (Integer) leftValue % (Integer) rightValue;
            }

            // ����С��,תdoubleִ�г���
            if (leftValue instanceof Double || rightValue instanceof Double || leftValue instanceof Float
                    || rightValue instanceof Float) {
                return ((Number) leftValue).doubleValue() % ((Number) rightValue).doubleValue();
            }

            // ����BigDecimal תbigDecimal
            if (leftValue instanceof BigDecimal || rightValue instanceof BigDecimal) {
                if (leftValue instanceof BigDecimal && rightValue instanceof BigDecimal) {
                    return ((BigDecimal) leftValue).remainder((BigDecimal) rightValue);
                }

                BigDecimal newLeft = XpathUtil.toBigDecimal((Number) leftValue);
                BigDecimal newRight = XpathUtil.toBigDecimal((Number) rightValue);
                return newLeft.remainder(newRight);
            }

            // ����������,�Ҳ�����С��,ȫ��ת��Ϊ����������
            if (leftValue instanceof Long || rightValue instanceof Long) {
                return ((Number) leftValue).longValue() % ((Number) rightValue).longValue();
            }

            // ����,��doubleִ�м���
            return ((Number) leftValue).doubleValue() % ((Number) rightValue).doubleValue();
        }

        throw new EvaluateException(
                "remainder operate must with number parameter left:" + leftValue + " right:" + rightValue);
    }
}
