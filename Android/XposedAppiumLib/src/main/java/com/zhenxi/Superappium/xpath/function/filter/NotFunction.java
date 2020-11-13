package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;

public class NotFunction extends BooleanFunction {
    @Override
    public Object call(ViewImage element, List<SyntaxNode> params) {
        return !((Boolean) super.call(element, params));
    }

    @Override
    public String getName() {
        return "not";
    }
}
