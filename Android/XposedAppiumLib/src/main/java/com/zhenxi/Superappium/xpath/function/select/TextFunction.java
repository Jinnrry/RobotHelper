package com.zhenxi.Superappium.xpath.function.select;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;
import com.zhenxi.Superappium.xpath.model.XNode;
import com.zhenxi.Superappium.xpath.model.XNodes;
import com.zhenxi.Superappium.xpath.model.XpathNode;

import java.util.List;

public class TextFunction implements SelectFunction {
    /**
     * 只获取节点自身的子文本
     */
    @Override
    public XNodes call(XpathNode.ScopeEm scopeEm, ViewImages elements, List<String> args) {
        XNodes res = new XNodes();
        if (elements != null && elements.size() > 0) {
            for (ViewImage e : elements) {
                res.add(XNode.t(e.getText()));
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return "text";
    }
}

