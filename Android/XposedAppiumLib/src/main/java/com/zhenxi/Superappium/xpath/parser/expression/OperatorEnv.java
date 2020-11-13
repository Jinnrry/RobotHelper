package com.zhenxi.Superappium.xpath.parser.expression;

import com.zhenxi.Superappium.utils.Lists;
import com.zhenxi.Superappium.utils.StringUtils;
import com.zhenxi.Superappium.xpath.parser.expression.node.AlgorithmUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.AddUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.And2Unit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.AndUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.ContainUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.DivideUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.EndWithUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.EqualUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.GreaterEqualUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.GreaterThanUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.LessEqualUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.LessThanUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.MinusUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.MultiUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.NotEqualUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.NotMatchUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.OpKey;
import com.zhenxi.Superappium.xpath.parser.expression.operator.Or2Unit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.OrUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.RegexUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.RemainderUnit;
import com.zhenxi.Superappium.xpath.parser.expression.operator.StartWithUnit;
import de.robv.android.xposed.XposedHelpers;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;



public class OperatorEnv {

    private static TreeSet<AlgorithmHolder> allOperators = new TreeSet<>();
    private static HashMap<String, AlgorithmHolder> operatorMaps = new HashMap<>();

    static {
        registerDefault();
    }

    private static void registerDefault() {
        addOperator(AddUnit.class);
        addOperator(AndUnit.class);
        addOperator(And2Unit.class);
        addOperator(ContainUnit.class);
        addOperator(DivideUnit.class);
        addOperator(EndWithUnit.class);
        addOperator(EqualUnit.class);
        addOperator(GreaterEqualUnit.class);
        addOperator(GreaterThanUnit.class);
        addOperator(LessEqualUnit.class);
        addOperator(LessThanUnit.class);
        addOperator(MinusUnit.class);
        addOperator(MultiUnit.class);
        addOperator(NotEqualUnit.class);
        addOperator(NotMatchUnit.class);
        addOperator(Or2Unit.class);
        addOperator(OrUnit.class);
        addOperator(RegexUnit.class);
        addOperator(RemainderUnit.class);
        addOperator(StartWithUnit.class);
    }

    public static void addOperator(String key, int priority, Class<? extends AlgorithmUnit> algorithmUnit) {
        if ("#".equals(key)) {
            throw new IllegalStateException("\"#\" can not to be register a operator,please contact author virjar@virjar.com");
        }

        AlgorithmHolder holder = new AlgorithmHolder(algorithmUnit, key, priority);
        allOperators.add(holder);
        operatorMaps.put(key, holder);
    }

    public static void addOperator(Class<? extends AlgorithmUnit> algorithmUnitClass) {
        OpKey annotation = algorithmUnitClass.getAnnotation(OpKey.class);
        if (annotation == null) {
            throw new IllegalStateException("can not register operator for class " + algorithmUnitClass
                    + ",such can not resolve operator name");
        }
        String key = annotation.value();
        if (StringUtils.isBlank(key)) {
            throw new IllegalStateException(
                    "can not register operator for class " + algorithmUnitClass + " ,such operator name is empty");
        }
        addOperator(key, annotation.priority(), algorithmUnitClass);
    }

    public static AlgorithmUnit createByName(String operatorName) {
        AlgorithmHolder holder = operatorMaps.get(operatorName);
        return (AlgorithmUnit) XposedHelpers.newInstance(holder.aClass);
    }

    public static int judgePriority(String operatorName) {
        if ("#".equals(operatorName)) {
            // 这是一个特殊逻辑,运算栈需要有一个兜底判断的操作符,所以#是RPN默认的一个运算符,但是他不会表现在编译好的语法树里面
            return Integer.MIN_VALUE;
        }
        AlgorithmHolder holder = operatorMaps.get(operatorName);
        return holder.priority;
    }

    public static synchronized List<AlgorithmHolder> allAlgorithmUnitList() {
        return Lists.newCopyOnWriteArrayList(allOperators);
    }

    public static class AlgorithmHolder implements Comparable<AlgorithmHolder> {

        private String key;
        private Class<? extends AlgorithmUnit> aClass;

        private int priority;

        public String getKey() {
            return key;
        }

        public Class<? extends AlgorithmUnit> getaClass() {
            return aClass;
        }

        public AlgorithmHolder(Class<? extends AlgorithmUnit> aClass, String key, int priority) {
            this.aClass = aClass;
            this.key = key;
            this.priority = priority;
        }

        @Override
        public int compareTo(AlgorithmHolder o) {
            String thisKey = key;
            String otherKey = o.key;
            if (thisKey.length() != otherKey.length()) {
                return otherKey.length() - thisKey.length();
            }
            return thisKey.compareTo(otherKey);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AlgorithmHolder that = (AlgorithmHolder) o;

            return Objects.equals(key, that.key);

        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }
    }
}
