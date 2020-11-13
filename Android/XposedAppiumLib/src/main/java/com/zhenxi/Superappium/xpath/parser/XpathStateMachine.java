package com.zhenxi.Superappium.xpath.parser;

import com.zhenxi.Superappium.utils.Lists;
import com.zhenxi.Superappium.utils.StringUtils;
import com.zhenxi.Superappium.xpath.XpathParser;
import com.zhenxi.Superappium.xpath.exception.NoSuchAxisException;
import com.zhenxi.Superappium.xpath.exception.XpathSyntaxErrorException;
import com.zhenxi.Superappium.xpath.function.FunctionEnv;
import com.zhenxi.Superappium.xpath.function.axis.AxisFunction;
import com.zhenxi.Superappium.xpath.model.Predicate;
import com.zhenxi.Superappium.xpath.model.XpathEvaluator;
import com.zhenxi.Superappium.xpath.model.XpathNode;
import com.zhenxi.Superappium.xpath.parser.expression.ExpressionParser;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class XpathStateMachine {
    private static Map<String, XpathNode.ScopeEm> scopeEmMap = new HashMap<>();


    // 注意顺序,这顺序不能乱
    private static List<String> scopeList = new ArrayList<>(); //Lists.newArrayList("//", "/", ".//", "./");

    static {
        scopeEmMap.put("/", XpathNode.ScopeEm.INCHILREN);
        scopeEmMap.put("//", XpathNode.ScopeEm.RECURSIVE);
        scopeEmMap.put("./", XpathNode.ScopeEm.CUR);
        scopeEmMap.put(".//", XpathNode.ScopeEm.CURREC);

        scopeList.add("//");
        scopeList.add("/");
        scopeList.add(".//");
        scopeList.add("./");
    }

    public BuilderState getState() {
        return state;
    }

    private BuilderState state = BuilderState.SCOPE;
    private TokenQueue tokenQueue;

    public XpathEvaluator getEvaluator() {
        return evaluator;
    }

    private XpathEvaluator evaluator = new XpathEvaluator.AnanyseStartEvaluator();

    private LinkedList<XpathNode> xpathChain = new LinkedList<>();

    public XpathStateMachine(TokenQueue tokenQueue) {
        this.tokenQueue = tokenQueue;
    }


    public enum BuilderState {

        // 解析起始
        SCOPE {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                stateMachine.tokenQueue.consumeWhitespace();// 消除空白字符
                char xpathFlag = '`';
                if (stateMachine.tokenQueue.matchesAny(xpathFlag, '(')) {
                    // 获取一个子串,处理递归,转义,引号问题

                    String subXpath;
                    if (stateMachine.tokenQueue.matchesAny(xpathFlag)) {
                        subXpath = stateMachine.tokenQueue.chompBalanced(xpathFlag, xpathFlag);
                    } else {
                        subXpath = stateMachine.tokenQueue.chompBalanced('(', ')');
                    }
                    if (StringUtils.isBlank(subXpath)) {
                        throw new XpathSyntaxErrorException(0, "\"()\" empty sub xpath fond");
                    }
                    // subXpath = TokenQueue.unescape(subXpath);
                    // TODO 考虑是否抹掉转义
                    XpathEvaluator subTree = new XpathParser(subXpath).parse();
                    stateMachine.evaluator = stateMachine.evaluator.wrap(subTree);
                    return;
                }

                if (stateMachine.tokenQueue.matchesAny("and", "&")) {
                    if (stateMachine.tokenQueue.matches("&")) {
                        stateMachine.tokenQueue.consume("&");
                    } else {
                        stateMachine.tokenQueue.advance("and".length());
                    }
                    XpathEvaluator tempEvaluator = stateMachine.evaluator;
                    if (!(tempEvaluator instanceof XpathEvaluator.AndEvaluator)) {
                        XpathEvaluator newEvaluator = new XpathEvaluator.AndEvaluator();
                        stateMachine.evaluator = tempEvaluator.wrap(newEvaluator);
                    }
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain));
                    stateMachine.xpathChain = new LinkedList<>();
                    return;
                }

                if (stateMachine.tokenQueue.matchesAny("or", "|")) {
                    if (stateMachine.tokenQueue.matches("|")) {
                        stateMachine.tokenQueue.consume("|");
                    } else {
                        stateMachine.tokenQueue.advance("or".length());
                    }
                    XpathEvaluator tempEvaluator = stateMachine.evaluator;
                    if (!(tempEvaluator instanceof XpathEvaluator.OrEvaluator)) {
                        XpathEvaluator newEvaluator = new XpathEvaluator.OrEvaluator();
                        stateMachine.evaluator = tempEvaluator.wrap(newEvaluator);
                    }
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain));
                    stateMachine.xpathChain = new LinkedList<>();
                    return;
                }

                for (String scope : scopeList) {
                    if (stateMachine.tokenQueue.matches(scope)) {
                        stateMachine.tokenQueue.consume(scope);
                        XpathNode xpathNode = new XpathNode();
                        xpathNode.setScopeEm(scopeEmMap.get(scope));
                        stateMachine.xpathChain.add(xpathNode);
                        stateMachine.state = AXIS;
                        return;
                    }
                }

                throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                        "can not recognize token:" + stateMachine.tokenQueue.remainder());
            }
        },
        AXIS {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                // 轴解析
                if (!stateMachine.tokenQueue.hasAxis()) {
                    if (stateMachine.tokenQueue.matchesAny("..", ".")) {
                        //特殊逻辑,求当前节点和父节点的话,转化为一个轴函数
                        String axisFunctionName;
                        if (stateMachine.tokenQueue.matches("..")) {
                            stateMachine.tokenQueue.consume("..");
                            axisFunctionName = "parent";
                        } else {
                            stateMachine.tokenQueue.consume(".");
                            axisFunctionName = "self";
                        }
                        stateMachine.xpathChain.getLast().setAxis(FunctionEnv.getAxisFunction(axisFunctionName));
                        stateMachine.xpathChain.getLast().setSelectFunction(FunctionEnv.getSelectFunction("self"));
                        stateMachine.state = SCOPE;
                    } else {
                        stateMachine.state = TAG;
                    }
                    return;
                }

                String axisFunctionStr = stateMachine.tokenQueue.consumeTo("::");
                stateMachine.tokenQueue.consume("::");
                TokenQueue functionTokenQueue = new TokenQueue(axisFunctionStr);
                String functionName = functionTokenQueue.consumeCssIdentifier().trim();
                functionTokenQueue.consumeWhitespace();

                AxisFunction axisFunction = FunctionEnv.getAxisFunction(functionName);
                if (axisFunction == null) {
                    throw new NoSuchAxisException(stateMachine.tokenQueue.nowPosition(),
                            "not such axis " + functionName);
                }
                stateMachine.xpathChain.getLast().setAxis(axisFunction);

                if (functionTokenQueue.isEmpty()) {
                    stateMachine.state = TAG;
                    return;
                }

                // 带有参数的轴函数
                if (!functionTokenQueue.matches("(")) {// 必须以括号开头
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "expression is not a function:\"" + axisFunctionStr + "\"");
                }
                String paramList = StringUtils.trimToEmpty(functionTokenQueue.chompBalanced('(', ')'));
                functionTokenQueue.consumeWhitespace();
                if (!functionTokenQueue.isEmpty()) {
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "expression is not a function: \"" + axisFunctionStr + "\" can not recognize token:"
                                    + functionTokenQueue.remainder());
                }

                // 解析参数列表
                TokenQueue paramTokenQueue = new TokenQueue(paramList);
                LinkedList<String> params = new LinkedList<>();
                while (!paramTokenQueue.isEmpty()) {
                    paramTokenQueue.consumeWhitespace();
                    if (!paramTokenQueue.isEmpty() && paramTokenQueue.peek() == ',') {
                        paramTokenQueue.advance();
                        paramTokenQueue.consumeWhitespace();
                    }
                    String param;
                    if (paramTokenQueue.matches("\"")) {
                        param = paramTokenQueue.chompBalanced('\"', '\"');
                        if (paramTokenQueue.peek() == ',') {
                            paramTokenQueue.consume();
                        }
                    } else if (paramTokenQueue.matches("\'")) {
                        param = paramTokenQueue.chompBalanced('\'', '\'');
                        if (paramTokenQueue.peek() == ',') {
                            paramTokenQueue.consume();
                        }
                    } else {
                        param = paramTokenQueue.consumeTo(",");
                        if (StringUtils.isEmpty(param)) {
                            continue;
                        }
                    }
                    params.add(TokenQueue.unescape(StringUtils.trimToEmpty(param)));
                }
                stateMachine.xpathChain.getLast().setAxisParams(params);
                stateMachine.state = TAG;
            }
        },
        TAG {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                stateMachine.tokenQueue.consumeWhitespace();
                if (stateMachine.tokenQueue.peek() == '*') {
                    stateMachine.tokenQueue.advance();
                    stateMachine.tokenQueue.consumeWhitespace();
                    stateMachine.xpathChain.getLast()
                            .setSelectFunction(FunctionEnv.getSelectFunction("tag"));
                    stateMachine.xpathChain.getLast().setSelectParams(Lists.newArrayList("*"));
                    stateMachine.state = PREDICATE;
                    return;
                }

                if (stateMachine.tokenQueue.matchesFunction()) {// 遇到主干抽取函数,后面不能有谓语
                    String function = stateMachine.tokenQueue.consumeFunction();
                    TokenQueue functionTokenQueue = new TokenQueue(function);
                    String functionName = functionTokenQueue.consumeTo("(");
                    LinkedList<String> params = new LinkedList<>();
                    TokenQueue paramTokenQueue = new TokenQueue(StringUtils.trimToEmpty(functionTokenQueue.chompBalanced('(', ')')));
                    while ((paramTokenQueue.consumeWhitespace() && !paramTokenQueue.consumeWhitespace())
                            || !paramTokenQueue.isEmpty()) {
                        String param;
                        if (paramTokenQueue.matches("\"")) {
                            param = paramTokenQueue.chompBalanced('\"', '\"');
                            if (paramTokenQueue.peek() == ',') {
                                paramTokenQueue.advance();
                            }
                        } else if (paramTokenQueue.matches("\'")) {
                            param = paramTokenQueue.chompBalanced('\'', '\'');
                            if (paramTokenQueue.peek() == ',') {
                                paramTokenQueue.advance();
                            }
                        } else {
                            param = paramTokenQueue.consumeTo(",");
                        }
                        params.add(TokenQueue.unescape(StringUtils.trimToEmpty(param)));
                    }
                    stateMachine.xpathChain.getLast()
                            .setSelectFunction(FunctionEnv.getSelectFunction(functionName));
                    stateMachine.xpathChain.getLast().setSelectParams(params);
                    stateMachine.state = PREDICATE;// TODO 后面是否支持谓语,如果支持的话,谓语数据结构需要修改
                    return;
                }
                if (stateMachine.tokenQueue.matches("@")) {// 遇到属性抽取动作
                    stateMachine.tokenQueue.advance();
                    stateMachine.tokenQueue.consumeWhitespace();
                    stateMachine.xpathChain.getLast()
                            .setSelectFunction(FunctionEnv.getSelectFunction("@"));
                    if (stateMachine.tokenQueue.peek() == '*') {
                        stateMachine.tokenQueue.advance();
                        stateMachine.xpathChain.getLast().setSelectParams(Lists.newArrayList("*"));
                    } else {
                        String attributeKey = stateMachine.tokenQueue.consumeAttributeKey();
                        stateMachine.xpathChain.getLast()
                                .setSelectParams(Lists.newArrayList(attributeKey));
                    }
                    stateMachine.state = PREDICATE;
                    stateMachine.tokenQueue.consumeWhitespace();
                    return;
                }

                String tagName = stateMachine.tokenQueue.consumeTagName();
                if (StringUtils.isEmpty(tagName)) {
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "can not recognize token,expected start with a tagName,actually is:"
                                    + stateMachine.tokenQueue.remainder());
                }
                stateMachine.xpathChain.getLast()
                        .setSelectFunction(FunctionEnv.getSelectFunction("tag"));
                stateMachine.xpathChain.getLast().setSelectParams(Lists.newArrayList(tagName));
                stateMachine.state = PREDICATE;
            }
        },
        PREDICATE {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                stateMachine.tokenQueue.consumeWhitespace();

                if (stateMachine.tokenQueue.matches("[")) {
                    // 谓语串
                    String predicate = stateMachine.tokenQueue.chompBalanced('[', ']');
                    SyntaxNode predicateTree = new ExpressionParser(new TokenQueue(predicate)).parse();
                    stateMachine.xpathChain.getLast()
                            .getPredicates().add(new Predicate(StringUtils.trimToEmpty(predicate), predicateTree));
                }
                // check
                stateMachine.tokenQueue.consumeWhitespace();
                if (stateMachine.tokenQueue.isEmpty()) {
                    stateMachine.state = END;
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain));
                    stateMachine.xpathChain = new LinkedList<>();
                } else {
                    stateMachine.state = SCOPE;
                }
            }
        },
        END {
            @Override
            public void parse(XpathStateMachine stateMachine) {
            }
        };

        public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
        }
    }
}
