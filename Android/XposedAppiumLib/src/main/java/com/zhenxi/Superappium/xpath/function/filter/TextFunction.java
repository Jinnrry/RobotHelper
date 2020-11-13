package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;

public class TextFunction implements FilterFunction {
    @Override
    public Object call(ViewImage element, List<SyntaxNode> params) {
        //return element.ownText();
        return element.getText();
    }

    @Override
    public String getName() {
        return "text";
    }
}
