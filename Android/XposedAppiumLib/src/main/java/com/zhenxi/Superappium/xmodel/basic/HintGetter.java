package com.zhenxi.Superappium.xmodel.basic;

import android.widget.TextView;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xmodel.ValueGetter;

public class HintGetter implements ValueGetter {
    @Override
    public Object get(ViewImage viewImage) {
        TextView textView = (TextView) viewImage.getOriginView();
        CharSequence hint = textView.getHint();
        if (hint == null) {
            return null;
        }
        return hint.toString();
    }

    @Override
    public String attr() {
        return SuperAppium.hint;
    }

    @Override
    public boolean support(Class type) {
        return TextView.class.isAssignableFrom(type);
    }
}