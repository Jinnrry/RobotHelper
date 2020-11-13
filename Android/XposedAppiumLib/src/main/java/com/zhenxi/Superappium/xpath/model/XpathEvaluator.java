package com.zhenxi.Superappium.xpath.model;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;
import com.zhenxi.Superappium.xpath.XpathUtil;
import com.zhenxi.Superappium.xpath.function.axis.AxisFunction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public abstract class XpathEvaluator {
    public abstract XNodes evaluate(XNodes elements);

    public XpathEvaluator wrap(XpathEvaluator newRule) {
        throw new UnsupportedOperationException();
    }

    public List<String> evaluateToString(XNodes xNodes) {
        return XpathUtil.transformToString(evaluate(xNodes));
    }


    public String evaluateToSingleString(XNodes xNodes) {
        List<String> strings = evaluateToString(xNodes);
        if (strings.size() == 0) {
            return null;
        }
        return strings.get(0);
    }

    public ViewImages evaluateToElement(XNodes xNodes) {
        return XpathUtil.transformToElement(evaluate(xNodes));
    }

    public static class AnanyseStartEvaluator extends XpathEvaluator {

        @Override
        public XNodes evaluate(XNodes elements) {
            throw new UnsupportedOperationException();
        }


        @Override
        public XpathEvaluator wrap(XpathEvaluator newRule) {
            return newRule;
        }
    }

    public static class ChainEvaluator extends XpathEvaluator {
        // @Getter
        // for xsoup
        private LinkedList<XpathNode> xpathNodeList = new LinkedList<>();

        public ChainEvaluator(LinkedList<XpathNode> xpathNodeList) {
            this.xpathNodeList = xpathNodeList;
        }

        private List<XNode> handleNode(List<XNode> input, final XpathNode xpathNode) {

            // 目前只支持对element元素进行抽取,如果中途抽取到了文本,则会断节
            ViewImages elements = XpathUtil.transformToElement(input);
            ViewImages contextElements;

            // 轴
            AxisFunction axis = xpathNode.getAxis();
            if (axis != null) {
                contextElements = new ViewImages();
                for (ViewImage element : elements) {
                    List<ViewImage> call = axis.call(element, xpathNode.getAxisParams());
                    if (call != null) {
                        contextElements.addAll(call);
                    }
                }
            } else {
                contextElements = elements;
            }

            // 调用抽取函数
            List<XNode> xNodesList = xpathNode.getSelectFunction().call(xpathNode.getScopeEm(),
                    contextElements, xpathNode.getSelectParams());
            // 谓语过滤
            if (xpathNode.getPredicates() == null || xpathNode.getPredicates().size() == 0) {
                return xNodesList;
            }

            List<XNode> finalResult = new LinkedList<>();
            //两层循环
            for (XNode xNode : xNodesList) {
                if (xNode.isText()) {
                    continue;
                }
                boolean valid = true;
                for (Predicate predicate : xpathNode.getPredicates()) {
                    if (!predicate.isValid(xNode.getElement())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    finalResult.add(xNode);
                }
            }
            return finalResult;
        }

        @Override
        public XNodes evaluate(XNodes elements) {
            for (XpathNode xpathNode : xpathNodeList) {// 对xpath语法树上面每个节点进行抽取
                // Log.i(SuperAppium.TAG, "before evaluate: " + xpathNode + ": " + StringUtils.join(elements));
                elements = new XNodes(handleNode(elements, xpathNode));
                // Log.i(SuperAppium.TAG, "after evaluate: " + xpathNode + ": " + StringUtils.join(elements));
                if (elements.isEmpty()) {// 如果已经为空,终止抽取链
                    return elements;
                }
            }
            return elements;
        }

    }

    public static class OrEvaluator extends XpathEvaluator {
        private List<XpathEvaluator> subEvaluators = new LinkedList<>();

        public OrEvaluator() {
        }

        @Override
        public XNodes evaluate(XNodes elements) {
            Iterator<XpathEvaluator> iterator = subEvaluators.iterator();
            XNodes evaluate = iterator.next().evaluate(elements);
            while (iterator.hasNext()) {
                evaluate.addAll(iterator.next().evaluate(elements));
            }
            return evaluate;
        }


        @Override
        public XpathEvaluator wrap(XpathEvaluator newRule) {
            subEvaluators.add(newRule);
            return this;
        }
    }

    public static class AndEvaluator extends XpathEvaluator {

        private List<XpathEvaluator> subEvaluators = new LinkedList<>();

        public AndEvaluator() {
        }

        @Override
        public XNodes evaluate(XNodes elements) {
            Iterator<XpathEvaluator> iterator = subEvaluators.iterator();
            XNodes evaluate = iterator.next().evaluate(elements);
            while (iterator.hasNext()) {
                evaluate.retainAll(iterator.next().evaluate(elements));
            }
            return evaluate;
        }


        @Override
        public XpathEvaluator wrap(XpathEvaluator newRule) {
            subEvaluators.add(newRule);
            return this;
        }
    }
}
