package cn.xjiangwei.RobotHelper.Service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class Accessibility extends AccessibilityService {


    public static AccessibilityNodeInfo DOM;



    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        DOM = getRootInActiveWindow();
    }

    public static void logViewHierarchy(AccessibilityNodeInfo nodeInfo, final int depth) {

        if (nodeInfo == null) return;

        String spacerString = "";

        for (int i = 0; i < depth; ++i) {
            spacerString += '-';
        }
        //Log the info you care about here... I choce classname and view resource name, because they are simple, but interesting.
        Log.d("TAG", spacerString + nodeInfo.getClassName() + " " + nodeInfo.getViewIdResourceName());

        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            logViewHierarchy(nodeInfo.getChild(i), depth + 1);
        }
    }


    @Override
    public void onInterrupt() {

    }


}
