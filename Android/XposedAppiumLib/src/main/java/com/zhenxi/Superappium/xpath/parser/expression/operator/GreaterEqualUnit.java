package com.zhenxi.Superappium.xpath.parser.expression.operator;

import com.zhenxi.Superappium.xpath.parser.expression.node.BooleanRevertUnit;


/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = ">=", priority = 10)
public class GreaterEqualUnit extends BooleanRevertUnit {
    @Override
    protected String targetName() {
        return "<";
    }
}
