package com.zhenxi.Superappium.xpath.parser.expression.node;

import com.zhenxi.Superappium.xpath.parser.expression.OperatorEnv;


public abstract class WrapperUnit extends AlgorithmUnit {
    private AlgorithmUnit delegate = null;

    protected abstract String targetName();

    protected AlgorithmUnit wrap() {
        if (delegate == null) {
            delegate = OperatorEnv.createByName(targetName());
            delegate.setLeft(left);
            delegate.setRight(right);
        }
        return delegate;
    }
}
