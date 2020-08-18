package cn.xjiangwei.RobotHelper.Service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class Accessibility extends AccessibilityService {

    private static Accessibility instance;
    public static AccessibilityNodeInfo DOM;


    public Accessibility() {
        instance = this;
    }


    public static Accessibility getInstance(){
        return instance;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        DOM = getRootInActiveWindow();
    }


    @Override
    public void onInterrupt() {

    }




}
