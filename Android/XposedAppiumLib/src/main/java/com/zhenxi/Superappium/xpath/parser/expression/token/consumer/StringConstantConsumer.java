package com.zhenxi.Superappium.xpath.parser.expression.token.consumer;

import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenConsumer;

public class StringConstantConsumer implements TokenConsumer {
    public String consume(TokenQueue tokenQueue) {
        if (tokenQueue.matches("'"))
            return TokenQueue.unescape(tokenQueue.chompBalanced('\'', '\''));
        if (tokenQueue.matches("\"")) {
            return TokenQueue.unescape(tokenQueue.chompBalanced('"', '"'));
        }
        return null;
    }

    public int order() {
        return 30;
    }

    public String tokenType() {
        return "CONSTANT";
    }
}