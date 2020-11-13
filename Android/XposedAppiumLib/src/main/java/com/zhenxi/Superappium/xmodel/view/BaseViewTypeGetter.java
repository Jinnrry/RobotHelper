package com.zhenxi.Superappium.xmodel.view;

import android.text.Layout;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xmodel.ValueGetter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 获取当前View的基类类型
 */
public class BaseViewTypeGetter implements ValueGetter {
    @Override
    public Object get(ViewImage viewImage) {
        for (Class<?> testClass : viewClassList) {
            if (testClass.isAssignableFrom(viewImage.getOriginView().getClass())) {
                return testClass.getName();
            }
        }
        return viewImage.getOriginView().getClass().getName();
    }

    @Override
    public boolean support(Class type) {
        return true;
    }

    @Override
    public String attr() {
        return SuperAppium.baseClassName;
    }

    private static List<Class<?>> viewClassList = new ArrayList<>();

    /**
     * this comparator make sure child class type process first by BaseViewTypeGetter
     */
    private static class BaseViewTypeComparator implements Comparator<Class<?>> {

        @Override
        public int compare(Class<?> aClass, Class<?> t1) {
            if (aClass.isAssignableFrom(t1)) {
                return 1;
            }
            if (t1.isAssignableFrom(aClass)) {
                return -1;
            }
            return aClass.getName().compareTo(t1.getName());
        }
    }

    private static BaseViewTypeComparator theComparator = new BaseViewTypeComparator();

    public static void addPrintClass(Class<?> clazz) {
        viewClassList.add(clazz);
        Collections.sort(viewClassList, theComparator);
    }

    private static void addDefaultPrintType() {
        addPrintClass(View.class);

        addPrintClass(WebView.class);
        addPrintClass(TextView.class);
        addPrintClass(Button.class);
        addPrintClass(EditText.class);
        addPrintClass(ImageView.class);
        addPrintClass(CheckBox.class);
        addPrintClass(CalendarView.class);
        addPrintClass(GridView.class);

        addPrintClass(ListView.class);

        addPrintClass(Layout.class);
        addPrintClass(LinearLayout.class);
        addPrintClass(GridLayout.class);
        addPrintClass(RelativeLayout.class);
        addPrintClass(TableLayout.class);
        addPrintClass(FrameLayout.class);
    }

    static {
        addDefaultPrintType();
    }

}

