package com.zhenxi.Superappium.xpath.parser.expression.node;

import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

public abstract class AlgorithmUnit implements SyntaxNode {
    protected SyntaxNode left = null;
    protected SyntaxNode right = null;

    public void setLeft(SyntaxNode left) {
        this.left = left;
    }

    public void setRight(SyntaxNode right) {
        this.right = right;
    }
}
