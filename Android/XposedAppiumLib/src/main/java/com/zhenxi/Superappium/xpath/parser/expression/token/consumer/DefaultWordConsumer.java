package com.zhenxi.Superappium.xpath.parser.expression.token.consumer;

import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenConsumer;

public class DefaultWordConsumer implements TokenConsumer {
    public String consume(TokenQueue tokenQueue) {
        if (tokenQueue.matchesWord()) {
            return tokenQueue.consumeWord();
        }
        return null;
    }

    public int order() {
        return 90;
    }

    public String tokenType() {
        return "CONSTANT";
    }
}
