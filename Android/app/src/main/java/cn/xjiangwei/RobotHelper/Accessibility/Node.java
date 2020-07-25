package cn.xjiangwei.RobotHelper.Accessibility;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.xjiangwei.RobotHelper.Accessibility.exceptions.NodeHasBeenRemovedException;
import cn.xjiangwei.RobotHelper.Accessibility.exceptions.UnableToSetAttributeException;

/**
 * Created by adolli on 2017/7/19.
 */

public class Node extends AbstractNode {

    public AccessibilityNodeInfo node;
    protected int screenWidth_ = 0;
    protected int screenHeight_ = 0;

    public Node(AccessibilityNodeInfo node, int screenW, int screenH) {
        super();
        this.node = node;
        this.screenWidth_ = screenW;
        this.screenHeight_ = screenH;
    }

    @Override
    @Nullable
    public AbstractNode getParent() {
        AccessibilityNodeInfo parent = this.node.getParent();
        if (parent == null) {
            return null;
        }
        return new Node(this.node.getParent(), this.screenWidth_, this.screenHeight_);
    }

    @Override
    public Iterable<AbstractNode> getChildren() {
        List<AbstractNode> ret = new LinkedList<>();
        int childCount = this.node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ret.add(new Node(node.getChild(i), this.screenWidth_, this.screenHeight_));
        }
        return ret;
    }

    @Override
    public void setAttr(String attrName, Object attrVal) throws UnableToSetAttributeException, NodeHasBeenRemovedException {
        if (this.node == null) {
            throw new NodeHasBeenRemovedException(attrName, null);
        }

        switch (attrName) {
            case "text":
                if (!this.node.isEditable()) {
                    throw new UnableToSetAttributeException(attrName, this.node, "this node is not editable");
                }
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, attrVal.toString());
                this.node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
                break;
            default:
                throw new UnableToSetAttributeException(attrName, this.node);
        }
    }

    @Override
    public Object getAttr(String attrName) throws NodeHasBeenRemovedException {
        if (this.node == null) {
            throw new NodeHasBeenRemovedException(attrName, null);
        }

        Object ret = null;
        switch (attrName) {
            case "name":
                ret = node.getViewIdResourceName();
                if (ret == null || ret.equals("")) {
                    ret = node.getContentDescription();
                }
                if (ret == null || ret.toString().equals("")) {
                    ret = node.getClassName();
                }
                if (ret != null) {
                    ret = ret.toString();
                } else {
                    ret = "<empty>";
                }
                break;
            case "type":
                ret = node.getClassName().toString();
                break;
            case "visible":
                boolean visible = node.isVisibleToUser();
                if (!visible) {
                    ret = false;
                } else {
                    ret = true;
                    AccessibilityNodeInfo parent = node.getParent();
                    while (parent != null) {
                        boolean parentVisible = parent.isVisibleToUser();
                        if (!parentVisible) {
                            ret = false;
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
                break;
            case "pos":
                Rect bound = new Rect();
                node.getBoundsInScreen(bound);
                JSONArray pos = new JSONArray();
                try {
                    pos.put(1.0 * bound.centerX() / this.screenWidth_);
                    pos.put(1.0 * bound.centerY() / this.screenHeight_);
                } catch (JSONException e) {}
                ret = pos;
                break;
            case "size":
                Rect bound1 = new Rect();
                node.getBoundsInScreen(bound1);
                JSONArray size = new JSONArray();
                try {
                    size.put(1.0 * bound1.width() / this.screenWidth_);
                    size.put(1.0 * bound1.height() / this.screenHeight_);
                } catch (JSONException e) {}
                ret = size;
                break;
            case "boundsInParent":
                Rect boundP = new Rect();
                node.getBoundsInParent(boundP);
                JSONArray sizeP = new JSONArray();
                try {
                    sizeP.put(1.0 * boundP.width() / this.screenWidth_);
                    sizeP.put(1.0 * boundP.height() / this.screenHeight_);
                } catch (JSONException e) {}
                ret = sizeP;
                break;
            case "scale":
                JSONArray scale = new JSONArray();
                scale.put(1);
                scale.put(1);
                ret = scale;
                break;
            case "anchorPoint":
                JSONArray anchor = new JSONArray();
                try {
                    anchor.put(0.5);
                    anchor.put(0.5);
                } catch (JSONException e) {}
                ret = anchor;
                break;
            case "zOrders":
                JSONObject zOrders = new JSONObject();
                int localOrder = 0;
                try {
                    localOrder = node.getDrawingOrder();
                } catch (NoSuchMethodError e) { }
                try {
                    zOrders.put("global", 0);
                    zOrders.put("local", localOrder);
                } catch (JSONException e) {}
                ret = zOrders;
                break;
            case "resourceId":
                CharSequence resid = node.getViewIdResourceName();
                if (resid != null) {
                    ret = resid.toString();
                }
                break;
            case "package":
                CharSequence pkgName = node.getPackageName();
                if (pkgName != null) {
                    ret = pkgName.toString();
                }
                break;
            case "desc":
                CharSequence desc = node.getContentDescription();
                if (desc != null) {
                    ret = desc.toString();
                }
                break;
            case "text":
                CharSequence text = node.getText();
                if (text != null) {
                    ret = text.toString();
                }
                break;
            case "enabled":
                ret = node.isEnabled();
                break;
            case "checkable":
                ret = node.isCheckable();
                break;
            case "checked":
                ret = node.isChecked();
                break;
            case "focusable":
                ret = node.isFocusable();
                break;
            case "focused":
                ret = node.isFocused();
                break;
            case "editalbe":
                ret = node.isEditable();
                break;
            case "selected":
                ret = node.isSelected();
                break;
            case "touchable":
                ret = node.isClickable();
                break;
            case "longClickable":
                ret = node.isLongClickable();
                break;
            case "scrollable":
                ret = node.isScrollable();
                break;
            case "dismissable":
                ret = node.isDismissable();
                break;
            default:
                ret = super.getAttr(attrName);
        }
        return ret;
    }

    public List<String> getAvailableAttributeNames() {
        List<String> ret = super.getAvailableAttributeNames();
        String[] a = new String[] {
                "resourceId",
                "package",
                "desc",
                "text",
                "enabled",
                "checkable",
                "checked",
                "focusable",
                "focused",
                "editalbe",
                "selected",
                "touchable",
                "longClickable",
                "boundsInParent",
                "scrollable",
                "dismissable",
        };

        for (String n : a) {
            ret.add(n);
        }
        return ret;
    }
}
