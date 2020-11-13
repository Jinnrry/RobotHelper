package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.exception.EvaluateException;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;

public abstract class AbstractStringFunction implements FilterFunction {
    protected String firstParamToString(ViewImage element, List<SyntaxNode> params) {
        Object string = ((SyntaxNode) params.get(0)).calc(element);
        if (!(string instanceof String)) {
            throw new EvaluateException(getName() + " first parameter is not a string :" + string);
        }
        return string.toString();
    }
}
