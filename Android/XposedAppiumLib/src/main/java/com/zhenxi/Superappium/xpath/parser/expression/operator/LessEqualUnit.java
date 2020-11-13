package com.zhenxi.Superappium.xpath.parser.expression.operator;

import com.zhenxi.Superappium.xpath.parser.expression.node.BooleanRevertUnit;


/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "<=", priority = 10)
public class LessEqualUnit extends BooleanRevertUnit {
    @Override
    protected String targetName() {
        return ">";
    }
}
