package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.exception.EvaluateException;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.math.BigDecimal;
import java.util.List;

public class AbsFunction implements FilterFunction {
    public Object call(ViewImage element, List<SyntaxNode> params) {
        Object calc = ((SyntaxNode) params.get(0)).calc(element);
        if (!(calc instanceof Number)) {
            throw new EvaluateException(getName() + " must have one number parameter, now it is: " + calc);
        }
        Number number = (Number) calc;
        if ((calc instanceof Integer)) {
            return Integer.valueOf(Math.abs(number.intValue()));
        }
        if ((calc instanceof Double)) {
            return Double.valueOf(Math.abs(number.doubleValue()));
        }
        if ((calc instanceof Long)) {
            return Long.valueOf(Math.abs(number.longValue()));
        }
        if ((calc instanceof Float)) {
            return Float.valueOf(Math.abs(number.floatValue()));
        }
        if ((calc instanceof BigDecimal)) {
            return ((BigDecimal) calc).abs();
        }


        return Double.valueOf(Math.abs(number.doubleValue()));
    }

    public String getName() {
        return "abs";
    }
}

