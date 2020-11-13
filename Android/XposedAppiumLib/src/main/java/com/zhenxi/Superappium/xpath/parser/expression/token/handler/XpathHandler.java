package com.zhenxi.Superappium.xpath.parser.expression.token.handler;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.XpathParser;
import com.zhenxi.Superappium.xpath.exception.XpathSyntaxErrorException;
import com.zhenxi.Superappium.xpath.model.XNode;
import com.zhenxi.Superappium.xpath.model.XNodes;
import com.zhenxi.Superappium.xpath.model.XpathEvaluator;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;
import com.zhenxi.Superappium.xpath.parser.expression.token.Token;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenHandler;

public class XpathHandler implements TokenHandler {
    @Override
    public SyntaxNode parseToken(String tokenStr) throws XpathSyntaxErrorException {
        final XpathEvaluator xpathEvaluator = new XpathParser(tokenStr).parse();
        return new SyntaxNode() {
            @Override
            public Object calc(ViewImage element) {
                return xpathEvaluator.evaluate(new XNodes(XNode.e(element)));
            }
        };
    }

    @Override
    public String typeName() {
        return Token.XPATH;
    }
}
