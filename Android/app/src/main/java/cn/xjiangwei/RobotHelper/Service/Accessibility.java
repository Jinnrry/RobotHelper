package cn.xjiangwei.RobotHelper.Service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


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
