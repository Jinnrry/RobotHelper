package com.zhenxi.Superappium.xmodel.view;


import android.content.res.Resources;
import android.view.View;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xmodel.ValueGetter;

public class IdGetter implements ValueGetter<String> {
    @Override
    public String get(ViewImage viewImage) {
        View originView = viewImage.getOriginView();
        int id = originView.getId();
        if (id <= 0) {
            return null;
        }
        try {
            return originView.getResources().getResourceName(id);
        } catch (Resources.NotFoundException e) {
            //这里可能报错
            return null;
        }
    }

    @Override
    public boolean support(Class type) {
        return true;
    }

    @Override
    public String attr() {
        return SuperAppium.id;
    }
}
