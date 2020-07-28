package cn.xjiangwei.RobotHelper.Accessibility.exceptions;

/**
 * Created by adolli on 2017/7/30.
 */

public class NodeHasBeenRemovedException extends RuntimeException {
    public NodeHasBeenRemovedException(String attrName, Object node) {
        super(String.format("Node was no longer alive when query attribute \"%s\". Please re-select.", attrName));
    }
}
