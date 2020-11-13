package com.zhenxi.Superappium.xpath.parser.expression.token.consumer;

import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenConsumer;

public class AttributeActionConsumer implements TokenConsumer {
    public String consume(TokenQueue tokenQueue) {
        if (tokenQueue.matches("@")) {
            tokenQueue.advance();
            return tokenQueue.consumeAttributeKey();
        }
        return null;
    }

    public int order() {
        return 10;
    }

    public String tokenType() {
        return "ATTRIBUTE_ACTION";
    }
}


