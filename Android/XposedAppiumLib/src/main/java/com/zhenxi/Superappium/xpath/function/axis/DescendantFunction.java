package com.zhenxi.Superappium.xpath.function.axis;

import com.zhenxi.Superappium.ViewImage;

import java.util.List;

/**
 * 全部子代节点 儿子，孙子，孙子的儿子...
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
