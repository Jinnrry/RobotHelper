package com.zhenxi.Superappium.xmodel.view;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xmodel.ValueGetter;

public class IndexGetter implements ValueGetter<Integer> {
    @Override
    public Integer get(ViewImage viewImage) {
        return viewImage.index();
    }

    @Override
    public boolean support(Class type) {
        return true;
    }

    @Override
    public String attr() {
        return SuperAppium.index;
    }
}
