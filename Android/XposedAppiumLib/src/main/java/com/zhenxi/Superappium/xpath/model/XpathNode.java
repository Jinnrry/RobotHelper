package com.zhenxi.Superappium.xpath.model;

import com.zhenxi.Superappium.utils.StringUtils;
import com.zhenxi.Superappium.xpath.function.axis.AxisFunction;
import com.zhenxi.Superappium.xpath.function.select.SelectFunction;

import java.util.LinkedList;
import java.util.List;


public class XpathNode {
    public enum ScopeEm {
        INCHILREN("/"), // 默认只在子代中筛选,有轴时由轴定义筛选域
        RECURSIVE("//"), // 向下递归查找
        CUR("./"), // 当前节点下
        CURREC(".//"); // 当前节点向下递归
        private String val;

        ScopeEm(String type) {
            this.val = type;
        }

        public String val() {
            return this.val;
        }
    }

    /**
     * 查找方向,
     */
    private ScopeEm scopeEm;

    public ScopeEm getScopeEm() {
        return scopeEm;
    }

    public void setScopeEm(ScopeEm scopeEm) {
        this.scopeEm = scopeEm;
    }

    /**
     * 轴
     */
    private AxisFunction axis;

    public AxisFunction getAxis() {
        return axis;
    }

    public void setAxis(AxisFunction axis) {
        this.axis = axis;
    }

    private List<String> axisParams;

    public List<String> getAxisParams() {
        return axisParams;
    }

    public void setAxisParams(List<String> axisParams) {
        this.axisParams = axisParams;
    }

    /**
     * 谓语
     */

    private LinkedList<Predicate> predicates = new LinkedList<>();

    public LinkedList<Predicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(LinkedList<Predicate> predicates) {
        this.predicates = predicates;
    }

    private SelectFunction selectFunction;

    public SelectFunction getSelectFunction() {
        return selectFunction;
    }

    public void setSelectFunction(SelectFunction selectFunction) {
        this.selectFunction = selectFunction;
    }

    private List<String> selectParams;

    public List<String> getSelectParams() {
        return selectParams;
    }

    public void setSelectParams(List<String> selectParams) {
        this.selectParams = selectParams;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(scopeEm.val);
        if (axis != null) {
            sb.append(axis.getName());
            if (axisParams != null && axisParams.size() > 0) {
                sb.append("(").append(StringUtils.join(axisParams, ",")).append(")");
            }
            sb.append("::");
        }
        if (selectFunction != null) {
            String name = selectFunction.getName();
            if ("tag".equalsIgnoreCase(name)) {
                sb.append(selectParams.get(0));
            } else {
                sb.append(name).append("(").append(StringUtils.join(selectParams, ",")).append(")");
            }
        }
        if (predicates != null && predicates.size() > 0) {
            for (Predicate predicate : predicates) {
                sb.append("[");
                sb.append(predicate.getPredicateStr());
                sb.append("]");
            }
        }

        return sb.toString();
    }
}
