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
        // ���ʽ��ֳ�token��
        List<TokenHolder> tokenStream = tokenStream();

        // �����沨��ʽ
        Stack<TokenHolder> stack = new Stack<>();
        // RPN�����沨��ʽ�ĺ���
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

        // ����������
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
            throw new XpathSyntaxErrorException(0, "����ʶ����ʽ:" + expressionTokenQueue.getQueue(), e);
        }
    }

    private SyntaxNode buildAlgorithmUnit(TokenHolder tokenHolder, SyntaxNode left, SyntaxNode right) {
        // ���ڼ�����,�����ڲ��ڵ�,��Ҫ�������Ҳ�����,���ܵ�������token��Ϣ�����ڵ�
        // Preconditions.checkArgument(tokenHolder.type.equals(Token.OPERATOR));
        AlgorithmUnit algorithmUnit = OperatorEnv.createByName(tokenHolder.expression);
        algorithmUnit.setLeft(left);
        algorithmUnit.setRight(right);
        return algorithmUnit;
    }

    /**
     * �ǲ������Ľڵ㹹��,�纯��,xpath���ʽ,����,���ֵ�,���ǵĹ��췽���ͼ������޹�,�Ǳ��ʽ������ԭʼ�ļ���Ҷ�ڵ�
     *
     * @param tokenHolder token����
     * @return �������ڼ����������Ҷ�ڵ�
     */
    private SyntaxNode buildByTokenHolder(TokenHolder tokenHolder) throws XpathSyntaxErrorException {
        //Preconditions.checkArgument(!tokenHolder.type.equals(Token.OPERATOR));
        // return TokenNodeFactory.hintAndGen(tokenHolder);
        return TokenAnalysisRegistry.findHandler(tokenHolder.getType()).parseToken(tokenHolder.expression);
    }

    private int compareSymbolPripority(TokenHolder first, TokenHolder second) {
        // ����ֱ�Ӽ�,����������
        return Integer.valueOf(OperatorEnv.judgePriority(first.expression))
                .compareTo(OperatorEnv.judgePriority(second.expression));
    }

    private List<TokenHolder> tokenStream() throws XpathSyntaxErrorException {
        List<TokenHolder> tokenStream = new LinkedList<>();
        // java��֧�ֶ��ű��ʽ,��ô���ﵽ�˶��ű��ʽ��Ч��
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
                // ���ɹ�,����
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
