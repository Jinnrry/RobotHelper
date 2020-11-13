package com.zhenxi.Superappium.xpath.parser.expression.token.consumer;

import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.OperatorEnv;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenConsumer;

import java.util.List;

public class OperatorConsumer implements TokenConsumer {
    public String consume(TokenQueue tokenQueue) {
        List<OperatorEnv.AlgorithmHolder> algorithmHolders = OperatorEnv.allAlgorithmUnitList();
        for (OperatorEnv.AlgorithmHolder holder : algorithmHolders) {
            if (tokenQueue.matches(holder.getKey())) {
                tokenQueue.consume(holder.getKey());
                return holder.getKey();
            }
        }
        return null;
    }

    public int order() {
        return 40;
    }

    public String tokenType() {
        return "OPERATOR";
    }
}
