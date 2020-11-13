package com.zhenxi.Superappium.xpath.function.axis;


import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.function.NameAware;

import java.util.List;

public abstract interface AxisFunction extends NameAware {
    public abstract List<ViewImage> call(ViewImage paramViewImage, List<String> paramList);
}
