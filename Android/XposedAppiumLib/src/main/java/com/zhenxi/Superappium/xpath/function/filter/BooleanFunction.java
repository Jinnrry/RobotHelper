package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;


public class BooleanFunction
        implements FilterFunction {
    public Object call(ViewImage element, List<SyntaxNode> params) {
        if (params.size() == 0) {
            return Boolean.FALSE;
        }
        Object calc = ((SyntaxNode) params.get(0)).calc(element);
        if (calc == null) {
            return Boolean.FALSE;
        }
        if ((calc instanceof Boolean)) {
            return calc;
        }
        if ((calc instanceof String)) {
            return Boolean.valueOf(calc.toString());
        }
        if ((calc instanceof Integer)) {
            return (Integer) calc != 0;
        }
        if ((calc instanceof Number)) {
            return ((Number) calc).doubleValue() > 0.0D;
        }
        return Boolean.FALSE;
    }

    public String getName() {
        return "boolean";
    }
}
