package com.zhenxi.Superappium.traversor;

import android.util.Log;
import android.util.Xml;

import com.zhenxi.Superappium.SuperAppium;
import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.ViewImages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class SuperAppiumDumper {
    public static String dumpToXml(ViewImage viewImage) {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();
            serializer.setOutput(stringWriter);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "hierarchy");
            serializer.attribute("", "comment", "dumped by super-appium, notice this not compatible with uiautomator");
            dumpNodeRec(viewImage, serializer);
            serializer.endTag("", "hierarchy");
            serializer.endDocument();
            return stringWriter.toString();
        } catch (IOException e) {
            Log.e(SuperAppium.TAG, "failed to dump window to file", e);
        }
        return null;
    }

    private static void dumpNodeRec(ViewImage node, XmlSerializer serializer) throws IOException {
        String tag = String.valueOf(node.attribute(SuperAppium.baseClassName));
        serializer.startTag("", tag);
        for (String attrKey : node.attributeKeys()) {
            if (attrKey.equals(SuperAppium.baseClassName)) {
                continue;
            }
            Object value = node.attribute(attrKey);
            if (value == null) {
                continue;
            }
            serializer.attribute("", attrKey, String.valueOf(value));
        }

        int count = node.childCount();
        for (int i = 0; i < count; i++) {
            ViewImage child = node.childAt(i);
            if (child != null) {
                dumpNodeRec(child, serializer);
            } else {
                Log.i(SuperAppium.TAG, String.format("Null child %d/%d, parent: %s",
                        i, count, node.toString()));
            }
        }
        serializer.endTag("", tag);
    }

    public static String dumpToJson(ViewImage viewImage) {
        return dumpToJsonObject(viewImage).toString();
    }

    public static String dumpToJson(ViewImages viewImages) {
        JSONArray jsonArray = new JSONArray();
        for (ViewImage viewImage : viewImages) {
            jsonArray.put(dumpToJsonObject(viewImage));
        }
        return jsonArray.toString();
    }

    public static JSONObject dumpToJsonObject(ViewImage viewImage) {
        JSONObject jsonObject = new JSONObject();
        dumpNodeRec(viewImage, jsonObject);
        return jsonObject;
    }

    private static void dumpNodeRec(ViewImage node, JSONObject container) {
        for (String attrKey : node.attributeKeys()) {
            Object value = node.attribute(attrKey);
            if (value == null) {
                continue;
            }
            try {
                container.putOpt(attrKey, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int count = node.childCount();
        if (count <= 0) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < count; i++) {
            ViewImage child = node.childAt(i);
            if (child == null) {
                jsonArray.put((Object) null);
            } else {
                JSONObject childContainer = new JSONObject();
                dumpNodeRec(child, childContainer);
                jsonArray.put(childContainer);
            }
        }
        try {
            container.put("children", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}