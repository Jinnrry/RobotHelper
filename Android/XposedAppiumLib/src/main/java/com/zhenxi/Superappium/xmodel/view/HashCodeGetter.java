package com.zhenxi.Superappium.xmodel.view;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xmodel.ValueGetter;

public class HashCodeGetter implements ValueGetter<String> {
    @Override
    public String get(ViewImage viewImage) {
        return String.valueOf(viewImage.getOriginView().hashCode());
    }

    @Override
    public boolean support(Class type) {
        return true;
    }

    @Override
    public String attr() {
        return SuperAppium.hash;
    }
}
