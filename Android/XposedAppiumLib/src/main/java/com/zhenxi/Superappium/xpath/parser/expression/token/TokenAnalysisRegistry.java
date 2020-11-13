package com.zhenxi.Superappium.xpath.parser.expression.token;

import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.AttributeActionConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.BooleanConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.DefaultWordConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.DefaultXpathConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.DigitConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.ExpressionConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.FunctionConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.OperatorConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.StringConstantConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.consumer.XpathConsumer;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.AttributeHandler;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.BooleanHandler;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.ConstantHandler;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.ExpressionHandler;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.FunctionHandler;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.NumberHandler;
import com.zhenxi.Superappium.xpath.parser.expression.token.handler.XpathHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class TokenAnalysisRegistry {
    /*  27 */   private static TreeSet<TokenConsumerWrapper> allConsumer = new TreeSet();
    /*  28 */   private static Map<String, TokenHandler> allHandler = new HashMap();

    static {
        /*  31 */
        registerHandler(new AttributeHandler());
        /*  32 */
        registerHandler(new BooleanHandler());
        /*  33 */
        registerHandler(new ConstantHandler());
        /*  34 */
        registerHandler(new FunctionHandler());
        /*  35 */
        registerHandler(new NumberHandler());
        /*  36 */
        registerHandler(new XpathHandler());
        /*  37 */
        registerHandler(new ExpressionHandler());

        /*  39 */
        registerConsumer(new OperatorConsumer());
        /*  40 */
        registerConsumer(new DigitConsumer());
        /*  41 */
        registerConsumer(new FunctionConsumer());
        /*  42 */
        registerConsumer(new BooleanConsumer());


        /*  45 */
        registerConsumer(new AttributeActionConsumer());
        /*  46 */
        registerConsumer(new StringConstantConsumer());
        /*  47 */
        registerConsumer(new XpathConsumer());
        /*  48 */
        registerConsumer(new ExpressionConsumer());


        /*  51 */
        registerConsumer(new DefaultWordConsumer());
        /*  52 */
        registerConsumer(new DefaultXpathConsumer());
    }

    public static void registerHandler(TokenHandler tokenHandler) {
        /*  56 */
        if (("OPERATOR".equals(tokenHandler.typeName())) && (allHandler.containsKey("OPERATOR"))) {
            /*  57 */
            throw new IllegalStateException("can not register operator handler,operator handler must hold by framework");
        }

        /*  60 */
        allHandler.put(tokenHandler.typeName(), tokenHandler);
    }


    public static void registerConsumer(TokenConsumer tokenConsumer) {
        /*  70 */
        if ((!"OPERATOR".equals(tokenConsumer.tokenType())) && (!allHandler.containsKey(tokenConsumer.tokenType()))) {
            /*  71 */
            throw new IllegalStateException("can not register token consumer ,not token handler available");
        }
        /*  73 */
        allConsumer.add(new TokenConsumerWrapper(tokenConsumer));
    }

    public static TokenHandler findHandler(String tokenType) {
        /*  77 */
        return (TokenHandler) allHandler.get(tokenType);
    }

    public static Iterable<? extends TokenConsumer> consumerIterable() {
        /*  81 */
        return allConsumer;
    }

    private static class TokenConsumerWrapper implements Comparable<TokenConsumer>, TokenConsumer {
        private TokenConsumer delegate;

        TokenConsumerWrapper(TokenConsumer delegate) {
            /*  88 */
            this.delegate = delegate;
        }

        public String consume(TokenQueue tokenQueue) {
            /*  93 */
            return this.delegate.consume(tokenQueue);
        }

        public int order() {
            /*  98 */
            return this.delegate.order();
        }

        public String tokenType() {
            /* 103 */
            return this.delegate.tokenType();
        }

        public int compareTo(TokenConsumer o) {
            /* 108 */
            if (this == o) {
                /* 109 */
                return 0;
            }
            /* 111 */
            return Integer.valueOf(this.delegate.order()).compareTo(Integer.valueOf(o.order()));
        }
    }
}


/* Location:              /Users/alienhe/.gradle/caches/modules-2/files-2.1/com.virjar/ratel-extersion/1.0.4/c3247d9a6d2e125b04726c3b311ee721ef979ad2/ratel-extersion-1.0.4.jar!/com/virjar/ratel/api/extension/superappium/xpath/parser/expression/token/TokenAnalysisRegistry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */