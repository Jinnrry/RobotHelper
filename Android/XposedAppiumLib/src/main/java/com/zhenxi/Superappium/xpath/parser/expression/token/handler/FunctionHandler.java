package com.zhenxi.Superappium.xpath.parser.expression.token.handler;

import com.zhenxi.Superappium.xpath.exception.XpathSyntaxErrorException;
import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.FunctionParser;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;
import com.zhenxi.Superappium.xpath.parser.expression.token.Token;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenHandler;

public class FunctionHandler implements TokenHandler {
    @Override
    public SyntaxNode parseToken(String tokenStr) throws XpathSyntaxErrorException {
        return new FunctionParser(new TokenQueue(tokenStr)).parse();
    }

    @Override
    public String typeName() {
        return Token.FUNCTION;
    }
}
