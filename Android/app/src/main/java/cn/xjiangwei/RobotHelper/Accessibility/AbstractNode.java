package cn.xjiangwei.RobotHelper.Accessibility;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.xjiangwei.RobotHelper.Accessibility.exceptions.UnableToSetAttributeException;

/**
 * Created by adolli on 2017/7/19.
 */

public abstract class AbstractNode implements INode {
    // tree node interface
    public AbstractNode getParent() {
        return null;
    }

    public abstract Iterable<AbstractNode> getChildren();

    // node interface
    public void setAttr(String attrName, Object attrVal) {
        throw new UnableToSetAttributeException(String.format("%s=%s", attrName, attrVal), null, "Method not implemented.");
    }

    public Object getAttr(String attrName) {
        Map<String, Object> defaultAttrs = new HashMap<>();
        defaultAttrs.put("name", "<Root>");
        defaultAttrs.put("type", "Root");
        defaultAttrs.put("visible", true);
        defaultAttrs.put("pos", new float[] {0, 0});
        defaultAttrs.put("size", new float[] {0, 0});
        defaultAttrs.put("scale", new float[] {0, 0});
        defaultAttrs.put("anchorPoint", new float[] {0.5f, 0.5f});
        Map<String, Object> zOrders = new HashMap<>();
        zOrders.put("global", 0);
        zOrders.put("local", 0);
        defaultAttrs.put("zOrders", zOrders);

        if (defaultAttrs.containsKey(attrName)) {
            return defaultAttrs.get(attrName);
        } else {
            return null;
        }
    }

    public List<String> getAvailableAttributeNames() {
        List<String> ret = new LinkedList<>();
        String[] a = new String[] {
            "name",
            "type",
            "visible",
            "pos",
            "size",
            "scale",
            "anchorPoint",
            "zOrders",
        };
        for (String n : a) {
            ret.add(n);
        }
        return ret;
    }

    // method for dumper only
    public Map<String, Object> enumerateAttrs() {
        Map<String, Object> ret = new HashMap<>();
        for (String attrName : getAvailableAttributeNames()) {
            ret.put(attrName, getAttr(attrName));
        }
        return ret;
    }
}
