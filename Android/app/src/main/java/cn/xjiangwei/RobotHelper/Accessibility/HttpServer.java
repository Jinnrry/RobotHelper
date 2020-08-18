package cn.xjiangwei.RobotHelper.Accessibility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Service.Accessibility;
import cn.xjiangwei.RobotHelper.Tools.Robot;
import fi.iki.elonen.NanoHTTPD;


public class HttpServer extends NanoHTTPD {


    public boolean runing;

    public HttpServer() {
        super("0.0.0.0", 1082);
        try {
            start(5000, true);
            runing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        super.stop();
        runing = false;
    }


    public void start() {
        try {
            start(5000, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runing = true;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String path = session.getUri();
        Map<String, String> parms = session.getParms();
        String ret = "[]";
        String mimeType = "application/json; charset=utf-8";
        switch (path) {
            case "/dom":
                if (Accessibility.DOM == null) {
                    ret = "{\"tip\":\"无障碍服务未开启！\"}";
                } else if (parms.containsKey("id")) {
                    ret = dumpHierarchyImpl(Accessibility.DOM.findAccessibilityNodeInfosByViewId(parms.get("id")), false).toString();
                } else {
                    ret = dumpHierarchyImpl(new Node(Accessibility.DOM, MainApplication.sceenWidth, MainApplication.sceenHeight), false).toString();
                }
                break;
            case "/sceenSize":
                ret = "{\"w\": " + MainApplication.sceenWidth + " , \"h\":" + MainApplication.sceenHeight + "  }";
                break;
            case "/swipe":
                try {
                    Robot.swipe(Float.parseFloat(parms.get("start_x")), Float.parseFloat(parms.get("start_y")), Float.parseFloat(parms.get("end_x")), Float.parseFloat(parms.get("end_y")), Float.parseFloat(parms.get("duration")) * 1000);
                    ret = "{\"code\":200,\"msg\":\"success\"}";
                } catch (Exception e) {
                    ret = "{\"code\":500,\"msg\":\"检查是否开启xposed\"}";
                }
                break;
            case "/tap":
                try {
                    Robot.tap(Integer.parseInt(parms.get("x")), Integer.parseInt(parms.get("y")), (long) (Float.parseFloat(parms.get("delay")) * 1000));
                    ret = "{\"code\":200,\"msg\":\"success\"}";
                } catch (Exception e) {
                    ret = "{\"code\":500,\"msg\":\"检查是否开启xposed\"}";
                }
                break;
            case "/input":
                try {
                    Robot.input(parms.get("input"));
                    ret = "{\"code\":200,\"msg\":\"success\"}";
                } catch (Exception e) {
                    System.out.println(e.toString());
                    ret = "{\"code\":500,\"msg\":\"检查是否开启xposed\"}";
                }
                break;
            case "/clipboard":
                ClipboardManager cm = (ClipboardManager) MainApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("RobotHelp", parms.get("str"));
                cm.setPrimaryClip(mClipData);
                ret = "{\"code\":200,\"msg\":\"success\"}";
                break;
        }


        return newFixedLengthResponse(Response.Status.OK, mimeType, ret);
    }


    public JSONArray dumpHierarchyImpl(List<AccessibilityNodeInfo> nodes, boolean onlyVisibleNode) {
        JSONArray jsonArray = new JSONArray();
        for (AccessibilityNodeInfo node : nodes) {
            jsonArray.put(dumpHierarchyImpl(new Node(node, MainApplication.sceenWidth, MainApplication.sceenHeight), onlyVisibleNode));
        }

        return jsonArray;
    }


    public JSONObject dumpHierarchyImpl(AbstractNode node, boolean onlyVisibleNode) {
        if (node == null) {
            // return if still null
            return null;
        }

        JSONObject payload = new JSONObject();
        for (Map.Entry<String, Object> attr : node.enumerateAttrs().entrySet()) {
            try {
                payload.put(attr.getKey(), attr.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject result = new JSONObject();
        JSONArray children = new JSONArray();

        for (AbstractNode child : node.getChildren()) {
            if (!onlyVisibleNode || (boolean) child.getAttr("visible")) {
                children.put(this.dumpHierarchyImpl(child, onlyVisibleNode));
            }
        }
        if (children.length() > 0) {
            try {
                result.put("children", children);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            result.put("name", node.getAttr("name"));
            result.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}
