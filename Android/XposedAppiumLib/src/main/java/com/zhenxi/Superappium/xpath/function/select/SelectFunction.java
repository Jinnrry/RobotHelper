package com.zhenxi.Superappium.xpath.function.select;

import com.zhenxi.Superappium.ViewImages;
import com.zhenxi.Superappium.xpath.function.NameAware;
import com.zhenxi.Superappium.xpath.model.XNodes;
import com.zhenxi.Superappium.xpath.model.XpathNode;

import java.util.List;

public interface SelectFunction extends NameAware {
    XNodes call(XpathNode.ScopeEm scopeEm, ViewImages elements, List<String> args);
}
