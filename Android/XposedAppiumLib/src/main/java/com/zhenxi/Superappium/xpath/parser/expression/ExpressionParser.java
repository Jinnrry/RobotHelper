package com.zhenxi.Superappium.xpath.parser.expression;

import com.zhenxi.Superappium.xpath.exception.XpathSyntaxErrorException;
import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.expression.node.AlgorithmUnit;
import com.zhenxi.Superappium.xpath.parser.expression.token.Token;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenAnalysisRegistry;
import com.zhenxi.Superappium.xpath.parser.expression.token.TokenConsumer;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class ExpressionParser {
    private TokenQueue expressionTokenQueue;

    public ExpressionParser(TokenQueue expressionTokenQueue) {
        this.expressionTokenQueue = expressionTokenQueue;
    }

    private SyntaxNode innerParse() throws XpathSyntaxErrorException {
        // 表达式拆分成token流
        List<TokenHolder> tokenStream = tokenStream();

        // 构建逆波兰式
        Stack<TokenHolder> stack = new Stack<>();
        // RPN就是逆波兰式的含义
        List<TokenHolder> RPN = new LinkedList<>();
        TokenHolder bottom = new TokenHolder("#", null);
        stack.push(bottom);

        for (TokenHolder tokenHolder : tokenStream) {
            if (tokenHolder.type.equals(Token.OPERATOR)) {
                TokenHolder preSymbol = stack.peek();
                while (compareSymbolPripority(tokenHolder, preSymbol) <= 0) {
                    RPN.add(preSymbol);
                    stack.pop();
                    preSymbol = stack.peek();
                }
                stack.push(tokenHolder);
            } else {
                RPN.add(tokenHolder);

            }
        }
        while (!stack.peek().expression.equals("#")) {
            RPN.add(stack.pop());
        }

        // 构建计算树
        Stack<SyntaxNode> computeStack = new Stack<>();

        for (TokenHolder tokenHolder : RPN) {
            if (tokenHolder.type.equals(Token.OPERATOR)) {
                SyntaxNode right = computeStack.pop();
                SyntaxNode left = computeStack.pop();
                computeStack.push(buildAlgorithmUnit(tokenHolder, left, right));
            } else {
                computeStack.push(buildByTokenHolder(tokenHolder));
            }
        }
        return computeStack.pop();
    }

    public SyntaxNode parse() throws XpathSyntaxErrorException {
        try {
            return innerParse();
        } catch (EmptyStackException e) {
            throw new XpathSyntaxErrorException(0, "不能识别表达式:" + expressionTokenQueue.getQueue(), e);
        }
    }

    private SyntaxNode buildAlgorithmUnit(TokenHolder tokenHolder, SyntaxNode left, SyntaxNode right) {
        // 对于计算树,属于内部节点,需要附加左右操作树,不能单纯根据token信息产生节点
        // Preconditions.checkArgument(tokenHolder.type.equals(Token.OPERATOR));
        AlgorithmUnit algorithmUnit = OperatorEnv.createByName(tokenHolder.expression);
        algorithmUnit.setLeft(left);
        algorithmUnit.setRight(right);
        return algorithmUnit;
    }

    /**
     * 非操作符的节点构建,如函数,xpath表达式,常量,数字等,他们的构造方法和计算树无关,是表达式里面最原始的计算叶节点
     *
     * @param tokenHolder token数据
     * @return 用来挂在计算树上面的叶节点
     */
    private SyntaxNode buildByTokenHolder(TokenHolder tokenHolder) throws XpathSyntaxErrorException {
        //Preconditions.checkArgument(!tokenHolder.type.equals(Token.OPERATOR));
        // return TokenNodeFactory.hintAndGen(tokenHolder);
        return TokenAnalysisRegistry.findHandler(tokenHolder.getType()).parseToken(tokenHolder.expression);
    }

    private int compareSymbolPripority(TokenHolder first, TokenHolder second) {
        // 不能直接减,否则可能溢出
        return Integer.valueOf(OperatorEnv.judgePriority(first.expression))
                .compareTo(OperatorEnv.judgePriority(second.expression));
    }

    private List<TokenHolder> tokenStream() throws XpathSyntaxErrorException {
        List<TokenHolder> tokenStream = new LinkedList<>();
        // java不支持逗号表达式,这么做达到了逗号表达式的效果
        while ((expressionTokenQueue.consumeWhitespace() || !expressionTokenQueue.consumeWhitespace())
                && !expressionTokenQueue.isEmpty()) {

            boolean hint = false;
            for (TokenConsumer tokenConsumer : TokenAnalysisRegistry.consumerIterable()) {
                String consume = tokenConsumer.consume(expressionTokenQueue);
                if (consume == null) {
                    continue;
                }
                hint = true;
                tokenStream.add(new TokenHolder(consume, tokenConsumer.tokenType()));
                break;
            }
            if (!hint) {
                // 不成功,报错
                throw new XpathSyntaxErrorException(expressionTokenQueue.nowPosition(), "can not parse predicate"
                        + expressionTokenQueue.getQueue() + "  for token " + expressionTokenQueue.remainder());
            }
        }

        return tokenStream;
    }

    public static class TokenHolder {

        public TokenHolder(String expression, String type) {
            this.expression = expression;
            this.type = type;
        }

        private String type;
        private String expression;

        public String getType() {
            return type;
        }

        public String getExpression() {
            return expression;
        }

        @Override
        public String toString() {
            return expression;
        }
    }
}
