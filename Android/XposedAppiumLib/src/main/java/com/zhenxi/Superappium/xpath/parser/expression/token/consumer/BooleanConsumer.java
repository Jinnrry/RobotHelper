package com.zhenxi.Superappium.xpath.parser.expression.token.consumer;

import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenConsumer;

public class BooleanConsumer implements TokenConsumer {
    public String consume(TokenQueue tokenQueue) {
        if (tokenQueue.matchesBoolean()) {
            return tokenQueue.consumeWord();
        }
        return null;
    }

    public int order() {
        return 70;
    }

    public String tokenType() {
        return "BOOLEAN";
    }
}
