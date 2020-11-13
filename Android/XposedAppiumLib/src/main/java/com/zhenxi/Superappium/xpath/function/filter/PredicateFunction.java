package com.zhenxi.Superappium.xpath.function.filter;

import android.util.Log;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;



public class PredicateFunction implements FilterFunction {
    @Override
    public Object call(ViewImage element, List<SyntaxNode> params) {
        if (element == null) {
            return false;
        }
        Object ret = params.get(0).calc(element);
        if (ret == null) {
            return false;
        }

        if (ret instanceof Number) {
            int i = ((Number) ret).intValue();
            return element.index() == i;
        }

        if (ret instanceof Boolean) {
            return ret;
        }

        if (ret instanceof CharSequence) {
            String s = ret.toString();
            return Boolean.valueOf(s);
        }

        //log.warn("can not recognize predicate expression calc result:" + ret);
        Log.w(SuperAppium.TAG, "can not recognize predicate expression calc result:" + ret);
        return false;
    }

    @Override
    public String getName() {
        return "inner_predicate";
    }
}
