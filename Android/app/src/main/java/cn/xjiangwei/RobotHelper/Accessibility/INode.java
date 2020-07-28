package cn.xjiangwei.RobotHelper.Accessibility;


import cn.xjiangwei.RobotHelper.Accessibility.exceptions.NodeHasBeenRemovedException;
import cn.xjiangwei.RobotHelper.Accessibility.exceptions.UnableToSetAttributeException;

/**
 * Created by adolli on 2017/7/19.
 */

public interface INode {
    Object getAttr(String attrName) throws NodeHasBeenRemovedException;
    void setAttr(String attrName, Object value) throws UnableToSetAttributeException, NodeHasBeenRemovedException;
}
