package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;

public class TryExeptionFunction implements FilterFunction {
    @Override
    public Object call(ViewImage element, List<SyntaxNode> params) {
        if (params.size() == 0) {
            return true;
        }
        try {
            return params.get(0).calc(element);
        } catch (Exception e) {
            // log.debug("exception occur", e);
            if (params.size() > 1) {
                return params.get(1).calc(element);
            } else {
                return false;
            }
        }
    }

    @Override
    public String getName() {
        return "try";
    }
}

