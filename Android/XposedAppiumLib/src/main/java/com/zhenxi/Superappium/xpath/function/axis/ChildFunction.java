package com.zhenxi.Superappium.xpath.function.axis;

import com.zhenxi.Superappium.ViewImage;

import java.util.List;


public class ChildFunction implements AxisFunction {
    @Override
    public List<ViewImage> call(ViewImage e, List<String> args) {
        return e.children();
    }

    @Override
    public String getName() {
        return "child";
    }
}
