package com.zhenxi.Superappium.xpath.model;

import com.zhenxi.Superappium.ViewImage;

public class XNode {
    public enum NodeType {
        NODE, TEXT
    }

    private ViewImage element;
    private boolean isText;
    private String textVal;

    public ViewImage getElement() {
        return element;
    }

    public XNode setElement(ViewImage element) {
        this.element = element;
        return this;
    }

    public String getTextVal() {
        return textVal;
    }

    public XNode setTextVal(String textVal) {
        this.textVal = textVal;
        return this;
    }

    public boolean isText() {
        return isText;
    }

    public XNode setText(boolean text) {
        isText = text;
        return this;
    }

    public static XNode e(ViewImage viewImage) {
        XNode xNode = new XNode();
        xNode.setElement(viewImage).setText(false);
        return xNode;
    }

    public static XNode t(String text) {
        XNode xNode = new XNode();
        xNode.setTextVal(text).setText(true);
        return xNode;
    }


    @Override
    public String toString() {
        if (isText) {
            return textVal;
        } else {
            return element.toString();
        }
    }
}
