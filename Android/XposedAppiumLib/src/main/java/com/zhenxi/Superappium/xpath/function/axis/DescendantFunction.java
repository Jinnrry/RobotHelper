package com.zhenxi.Superappium.xpath.function.axis;

import com.zhenxi.Superappium.ViewImage;

import java.util.List;

/**
 * ȫ���Ӵ��ڵ� ���ӣ����ӣ����ӵĶ���...
 */
public class DescendantFunction implements AxisFunction {
    @Override
    public List<ViewImage> call(ViewImage e, List<String> args) {
        return e.getAllElements();
    }

    @Override
    public String getName() {
        return "descendant";
    }
}
