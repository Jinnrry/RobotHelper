package com.zhenxi.Superappium.xpath.parser.expression.operator;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.node.AlgorithmUnit;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "||", priority = 0)
public class OrUnit extends AlgorithmUnit {
    @Override
    public Object calc(ViewImage element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
        // ���Ϊtrue,�ұ߲�����ɶ,��Ϊ��
        if (leftValue != null && leftValue instanceof Boolean && (Boolean) leftValue) {
            return true;
        }

        // ��߲�Ϊ��,���ұ�Ϊ��
        if (rightValue != null && rightValue instanceof Boolean && (Boolean) rightValue) {
            return true;
        }
        return Boolean.FALSE;
    }
}
