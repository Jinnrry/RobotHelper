package com.zhenxi.Superappium;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.robv.android.xposed.XposedHelpers;

public class WebViewHelper {
    private WebView webView;

    public static final String superAppiumDataLocalVariable = "super_appium_data";

    public WebViewHelper(WebView webView) {
        this.webView = webView;
    }

    public JsCallFuture clickByXpath(String xpath) {
        return clickByXpath(xpath, null);
    }

    public JsCallFuture clickByXpath(String xpath, JsCallFuture clickEndFuture) {
        final JsCallFuture jsCallFuture = executeJsCode(genXpathFindJsCode(xpath));
        if (clickEndFuture == null) {
            clickEndFuture = new JsCallFuture(jsCallFuture.domCookie);
        }
        final JsCallFuture finalClickEndFuture = clickEndFuture;
        jsCallFuture.success().executeJsCode(superAppiumDataLocalVariable + ".click()")
                .addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                    @Override
                    public void onJsCallFinished(String callResultId) {
                        finalClickEndFuture.triggerCallFinish();
                    }
                });

        jsCallFuture.failed().addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
            @Override
            public void onJsCallFinished(String callResultId) {
                finalClickEndFuture.triggerCallFinish();
            }
        });

        return clickEndFuture;
    }


    public JsCallFuture typeByXpath(String xpath, final String value) {
        return typeByXpath(xpath, value, null);
    }

    public JsCallFuture typeByXpath(String xpath, final String value, JsCallFuture typeEndFuture) {

        final JsCallFuture jsCallFuture = executeJsCode(genXpathFindJsCode(xpath));
        if (typeEndFuture == null) {
            typeEndFuture = new JsCallFuture(jsCallFuture.domCookie);
        }
        //通过xpath寻找到元素

        //先获取焦点
        final JsCallFuture finalTypeEndFuture = typeEndFuture;
        jsCallFuture.success().executeJsCode(superAppiumDataLocalVariable + ".dispatchEvent(new Event('focus'));")
                .addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                    @Override
                    public void onJsCallFinished(String callResultId) {
                        //输入数据
                        jsCallFuture.executeJsCode(superAppiumDataLocalVariable + ".value=" + JSON.toJSONString(value) + ";").addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                            @Override
                            public void onJsCallFinished(String callResultId) {
                                //让自己失去焦点
                                jsCallFuture.executeJsCode(superAppiumDataLocalVariable + ".dispatchEvent(new Event('blur'));").addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                                    @Override
                                    public void onJsCallFinished(String callResultId) {
                                        finalTypeEndFuture.triggerCallFinish();
                                    }
                                });
                            }
                        });
                    }
                });
        jsCallFuture.failed().addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
            @Override
            public void onJsCallFinished(String callResultId) {
                finalTypeEndFuture.triggerCallFinish();
            }
        });
        return typeEndFuture;
    }

    public JsCallFuture xpath(String xpath, JsCallFuture jsCallFuture) {
        return executeJsCode(genXpathFindJsCode(xpath), jsCallFuture);
    }

    public JsCallFuture xpath(String xpath) {
        return executeJsCode(genXpathFindJsCode(xpath));
    }

    private Map<String, String> jsResultMapping = new HashMap<>();

    private String genXpathFindJsCode(String xpath) {
        return "document.evaluate("
                + JSON.toJSONString(xpath) +
                ",document.body,null,XPathResult.ANY_TYPE,null).iterateNext();";
    }


    public JsCallFuture executeJsCode(String jsCode) {
        return executeJsCode(jsCode, (JsCallFuture) null);
    }

    public JsCallFuture executeJsCode(String jsCode, JsCallFuture jsCallFuture) {
        if (jsCallFuture == null) {
            jsCallFuture = new JsCallFuture(UUID.randomUUID().toString());
        }

        jsCode = "(function(){if(!document.__super_appium_data){document.__super_appium_data={};}})(),"
                + "document.__super_appium_data['" + jsCallFuture.domCookie + "']=(function(){return " + jsCode +
                "})()," +
                "document.__super_appium_data['" + jsCallFuture.domCookie + "'];";
        final JsCallFuture finalJsCallFuture = jsCallFuture;
        executeJsCode(jsCode, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.i(SuperAppium.TAG, "ReceiveValue: " + s);
                jsResultMapping.put(finalJsCallFuture.domCookie, s);
                finalJsCallFuture.triggerCallFinish();
            }
        });
        return jsCallFuture;
    }


    private String wrapCallResultVariable(String cookie, String jsCode) {
        return "(function(" + superAppiumDataLocalVariable + "){" +
                "return " + jsCode +
                "})(document.__super_appium_data['" + cookie + "']);";
    }

    public void executeJsCode(final String jsCode, ValueCallback<String> valueCallback) {
        if (valueCallback == null) {
            valueCallback = new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {

                }
            };
        }
        Log.i(SuperAppium.TAG, "execute js code: " + jsCode);
        //android.webkit.WebView#evaluateJavascript(String script, @RecentlyNullable ValueCallback<String> resultCallback) {
        XposedHelpers.callMethod(webView, "evaluateJavascript", jsCode, valueCallback);
    }

    public class JsCallFuture {
        private String domCookie;

        private Set<OnJsCallFinishEvent> onJsCallFinishEventSet = new HashSet<>();
        private boolean hasCalled = false;

        public void addOnJsCallFinishEvent(OnJsCallFinishEvent onJsCallFinishEvent) {
            if (hasCalled) {
                onJsCallFinishEvent.onJsCallFinished(domCookie);
            } else {
                onJsCallFinishEventSet.add(onJsCallFinishEvent);
            }
        }

        JsCallFuture(String domCookie) {
            this.domCookie = domCookie;
        }

        void triggerCallFinish() {
            hasCalled = true;
            for (OnJsCallFinishEvent onJsCallFinishEvent : onJsCallFinishEventSet) {
                onJsCallFinishEvent.onJsCallFinished(domCookie);
            }
        }

        public JsCallFuture success() {
            final JsCallFuture successFuture = new JsCallFuture(domCookie);
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    if (isCallSuccess(callResultId)) {
                        successFuture.triggerCallFinish();
                    }
                }
            });
            return successFuture;
        }

        public JsCallFuture success(OnJsCallFinishEvent onJsCallFinishEvent) {
            JsCallFuture success = success();
            success.addOnJsCallFinishEvent(onJsCallFinishEvent);
            return success;
        }

        private boolean isCallSuccess(String callResultId) {
            if (!jsResultMapping.containsKey(callResultId)) {
                return false;
            }
            String callResult = jsResultMapping.get(callResultId);
            if (callResult == null) {
                return false;
            }
            return !"null".equalsIgnoreCase(callResult);
        }

        public JsCallFuture failed() {
            final JsCallFuture failedFuture = new JsCallFuture(domCookie);
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    if (!isCallSuccess(callResultId)) {
                        failedFuture.triggerCallFinish();
                    }
                }
            });
            return failedFuture;
        }

        public JsCallFuture failed(OnJsCallFinishEvent onJsCallFinishEvent) {
            JsCallFuture failed = failed();
            failed.addOnJsCallFinishEvent(onJsCallFinishEvent);
            return failed;
        }

        public JsCallFuture delay(final long delay) {
            final JsCallFuture delayFuture = new JsCallFuture(domCookie);
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            delayFuture.triggerCallFinish();
                        }
                    }, delay);
                }
            });
            return delayFuture;
        }


        public JsCallFuture executeJsCode(final String jsCode) {
            final JsCallFuture jsCallFuture = new JsCallFuture(UUID.randomUUID().toString());
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    WebViewHelper.this.executeJsCode(wrapCallResultVariable(callResultId, jsCode), jsCallFuture);
                }
            });
            return jsCallFuture;
        }

        public JsCallFuture clickByXpath(final String xpath) {
            final JsCallFuture ret = new JsCallFuture(UUID.randomUUID().toString());
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    WebViewHelper.this.clickByXpath(xpath, ret);
                }
            });
            return ret;
        }

        public JsCallFuture typeByXpath(final String xpath, final String value) {
            final JsCallFuture ret = new JsCallFuture(UUID.randomUUID().toString());
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    WebViewHelper.this.typeByXpath(xpath, value, ret);
                }
            });
            return ret;
        }

        public String getExecuteResult() {
            return jsResultMapping.get(domCookie);
        }

        public JsCallFuture xpath(final String xpath) {
            final JsCallFuture ret = new JsCallFuture(UUID.randomUUID().toString());
            addOnJsCallFinishEvent(new OnJsCallFinishEvent() {
                @Override
                public void onJsCallFinished(String callResultId) {
                    WebViewHelper.this.xpath(xpath, ret);
                }
            });
            return ret;
        }

    }

    public interface OnJsCallFinishEvent {
        void onJsCallFinished(String callResultId);
    }
}


