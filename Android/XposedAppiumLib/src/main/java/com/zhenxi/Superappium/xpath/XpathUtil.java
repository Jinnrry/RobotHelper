package com.zhenxi.Superappium.xpath;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;
import com.zhenxi.Superappium.xpath.exception.EvaluateException;
import com.zhenxi.Superappium.xpath.model.XNode;
import com.zhenxi.Superappium.xpath.model.XNodes;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class XpathUtil {
    public static ViewImages transformToElement(List<XNode> input) {
        ViewImages ret = new ViewImages();
        for (XNode xNode : input) {
            if (!xNode.isText()) {
                ret.add(xNode.getElement());
            }
        }
        return ret;
    }

    public static String toPlainString(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.toString();
    }

    public static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if (number instanceof Integer) {
            return new BigDecimal(number.intValue());
        }
        if (number instanceof Double || number instanceof Float) {// BigDecimal float 也是转double
            return new BigDecimal(number.doubleValue());
        }
        if (number instanceof Long) {
            return BigDecimal.valueOf(number.longValue());
        }
        return new BigDecimal(number.toString());
    }

    public static Integer firstParamToInt(List<SyntaxNode> params, ViewImage element, String functionName) {
        if (params.size() > 0) {
            Object calc = params.get(0).calc(element);
            if (calc != null && !(calc instanceof Integer)) {
                throw new EvaluateException(functionName + " parameter must be integer");
            } else {
                if (calc != null) {
                    return (Integer) calc;
                }
            }
        }
        return null;
    }

    public static ViewImage root(ViewImage element) {
        while (element.parentNode() != null) {
            element = element.parentNode();
        }
        return element;
    }

    public static XNodes transform(Collection<ViewImage> viewImages) {
        XNodes xNodes = new XNodes();
        for (ViewImage viewImage : viewImages) {
            xNodes.add(XNode.e(viewImage));
        }
        return xNodes;
    }

    public static List<String> transformToString(XNodes evaluate) {
        LinkedList<String> ret = new LinkedList<>();
        for (XNode xNode : evaluate) {
            if (xNode.isText()) {
                ret.add(xNode.getTextVal());
            }
        }
        return ret;
    }
}
