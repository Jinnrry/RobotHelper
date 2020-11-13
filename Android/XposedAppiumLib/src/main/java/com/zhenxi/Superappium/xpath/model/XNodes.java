package com.zhenxi.Superappium.xpath.model;

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;
import com.zhenxi.Superappium.xpath.XpathParser;

import java.util.LinkedList;
import java.util.List;

public class XNodes extends LinkedList<XNode> {

    public XNodes(List<XNode> handleNode) {
        super(handleNode);
    }

    public XNodes(XNode e) {
        add(e);
    }

    public XNodes() {
    }

    public XNodes evaluate(String xpathQuery) {
        return XpathParser.compileNoError(xpathQuery).evaluate(this);
    }

    public List<String> evaluateToString(String xpathQuery) {
        return XpathParser.compileNoError(xpathQuery).evaluateToString(this);
    }

    public List<ViewImage> evaluateToElement(String xpathQuery) {
        return XpathParser.compileNoError(xpathQuery).evaluateToElement(this);
    }

    public ViewImages evaluateToElements(String xpathQuery) {
        return new ViewImages(evaluateToElement(xpathQuery));
    }

    public String evaluateToSingleStr(String xpathQuery) {
        List<String> strings = evaluateToString(xpathQuery);
        if (strings.size() == 0) {
            return null;
        }
        return strings.get(0);
    }

    public ViewImage evaluateToSingleElement(String xpathQuery) {
        List<ViewImage> viewImages = evaluateToElement(xpathQuery);
        if (viewImages.size() == 0) {
            return null;
        }
        return viewImages.get(0);
    }
}
