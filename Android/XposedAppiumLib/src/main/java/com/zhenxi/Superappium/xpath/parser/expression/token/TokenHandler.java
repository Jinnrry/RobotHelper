package com.zhenxi.Superappium.xpath.parser.expression.token;

import com.zhenxi.Superappium.xpath.exception.XpathSyntaxErrorException;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

public abstract interface TokenHandler {
    public abstract SyntaxNode parseToken(String paramString)
            throws XpathSyntaxErrorException;

    public abstract String typeName();
}


/* Location:              /Users/alienhe/.gradle/caches/modules-2/files-2.1/com.virjar/ratel-extersion/1.0.4/c3247d9a6d2e125b04726c3b311ee721ef979ad2/ratel-extersion-1.0.4.jar!/com/virjar/ratel/api/extension/superappium/xpath/parser/expression/token/TokenHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */