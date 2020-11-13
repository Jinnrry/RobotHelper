package com.zhenxi.Superappium.xpath.function.select;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;
import com.zhenxi.Superappium.xpath.model.XNode;
import com.zhenxi.Superappium.xpath.model.XNodes;
import com.zhenxi.Superappium.xpath.model.XpathNode;

import java.util.List;


public class SelfFunction implements SelectFunction {
    @Override
    public XNodes call(XpathNode.ScopeEm scopeEm, ViewImages elements, List<String> args) {
        XNodes xNodes = new XNodes();
        for (ViewImage viewImage : elements) {
            xNodes.add(XNode.e(viewImage));
        }
        return xNodes;
    }

    @Override
    public String getName() {
        return "self";
    }
}
