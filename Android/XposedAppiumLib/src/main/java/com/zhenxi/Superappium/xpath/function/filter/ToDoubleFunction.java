package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;



public class ToDoubleFunction implements FilterFunction {
    @Override
    public Object call(ViewImage element, List<SyntaxNode> params) {
        // Preconditions.checkArgument(params.size() > 0, getName() + " at last has one parameter");
        Object calc = params.get(0).calc(element);
        if (calc instanceof Double) {
            return calc;
        }
        if (calc == null) {
            return null;
        }

        if (params.size() > 1) {
            Object defaultValue = params.get(1).calc(element);

//            Preconditions.checkArgument(defaultValue != null && defaultValue instanceof Double,
//                    getName() + " parameter 2 must to be a Double now is:" + defaultValue);
            return toDouble(calc.toString(), (Double) defaultValue);
        }
        return toDouble(calc.toString());
    }

    private  double toDouble(final String str) {
        return toDouble(str, 0.0d);
    }

    private  double toDouble(final String str, final double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    @Override
    public String getName() {
        return "toDouble";
    }
}
