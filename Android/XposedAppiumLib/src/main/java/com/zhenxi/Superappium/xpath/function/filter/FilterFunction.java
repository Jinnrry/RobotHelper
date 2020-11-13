package com.zhenxi.Superappium.xpath.function.filter;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.function.NameAware;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;

public abstract interface FilterFunction extends NameAware {
    public abstract Object call(ViewImage paramViewImage, List<SyntaxNode> paramList);
}