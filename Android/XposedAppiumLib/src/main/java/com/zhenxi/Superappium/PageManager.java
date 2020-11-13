package com.zhenxi.Superappium;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import com.zhenxi.Superappium.xpcompat.CompatHelpers;
import com.zhenxi.Superappium.xpcompat.CompatMethodHook;
import com.zhenxi.Superappium.xpcompat.XpCompatEngine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class PageManager {

    static {
        //�ϵ�9.0+ Hide Api���

        // ����ֱ�ӷϵ�������app���ڶ����flag�ļ�⣬�����ķ��䱻��ϣ���ʹ�� { @link com.zhenxi.Superappium.xpcompat.FreeReflection}
        // com.zhenxi.Superappium.xpcompat.CompatHelpers ��Ĭ��ʹ�� FreeReflection���з������
        // HiddenAPIEnforcementPolicyUtils.passApiCheck();

        //init();
        // ����ҳ����
        enablePageMonitor();
    }

    private static Context mContext = null;

    private static ClassLoader mClassLoader = null;

    private static HashMap<String, ActivityFocusHandler> activityFocusHandlerMap = new HashMap<>();

    private static HashMap<String, FragmentFocusHandler> fragmentFocusHandlerHashMap = new HashMap<>();

    private static Handler mainLooperHandler = new Handler(Looper.getMainLooper());

    private static Activity topActivity = null;

    private static Map<String, Object> topFragmentMaps = new ConcurrentHashMap<>();


    /**
     * ���ص�ǰ���̵�Context
     */
    public static Context getContext() {
        return mContext;
    }

    public static ClassLoader getClassloader() {
        return mClassLoader;
    }

    /**
     * ��Ҫ��Ϊ���õ��Է����̵�classloader �������
     *
     * @deprecated ���ڣ��˷��������������⡣���ƻ�����£�hook�����ڵ������attach֮�󡣴�ʱ���ز��������̡�<br>������dispatchActivityResumed��ʱ��ֱ������Ҳ�����õ��������ֶ�
     */
    private static void init() {
        try {
            CompatHelpers.findAndHookMethod(Application.class, "attach", Context.class, new CompatMethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = (Context) param.args[0];
                    mClassLoader = ((Context) param.args[0]).getClassLoader();
                }
            });
        } catch (Exception e) {
            Log.i(SuperAppium.TAG, "Hook Application->attach error " + e.getMessage());
            e.printStackTrace();
        }
    }


    public interface ActivityFocusHandler {
        boolean handleActivity(Activity activity, ViewImage root);
    }

    public interface FragmentFocusHandler {
        boolean handleFragmentPage(Object fragment, Activity activity, ViewImage root);
    }


    public interface AlertDialogShowListener {
        void DialogShow(Dialog dialogObject);
    }


    /**
     * ������
     */
    private static int taskDuration = 200;

    private static boolean hasPendingActivityTask = false;

    private static boolean disable = false;

    //private static Set<WeakReference<LocalActivityManager>> localActivityManagers = new CopyOnWriteArraySet<>();

    private static Set<WeakReference<Window>> dialogWindowsSets = new CopyOnWriteArraySet<>();

    private static Set<WeakReference<PopupWindow>> popupWindowSets = new CopyOnWriteArraySet<>();


    public static Set<WeakReference<Window>> getWindos() {
        return dialogWindowsSets;
    }

    public static Set<WeakReference<PopupWindow>> getPopWindos() {
        return popupWindowSets;
    }


    /**
     * �Ի��򵯴��Ļص�
     */
    private static AlertDialogShowListener mAfterAlertDialogShowListener = null;

    /**
     * ��������ʱ���������Ӱ��caseִ���ٶ�
     *
     * @param taskDuration case���ʱ�䣬Ĭ��200���룬Ҳ����0.2��
     */
    public static void setTaskDuration(int taskDuration) {
        PageManager.taskDuration = taskDuration;
    }

    public static void setDisable(boolean disable) {
        PageManager.disable = disable;
    }

    public static Handler getMainLooperHandler() {
        return mainLooperHandler;
    }

    /**
     * ����show�����Ļص�
     *
     * @param listener ��Ӧ��Listener
     * @deprecated
     */
    public static void SetDialogShowListener(AlertDialogShowListener listener) {
        if (listener != null) {
            mAfterAlertDialogShowListener = listener;
        }
    }

    public static void setDialogShowListener(AlertDialogShowListener listener) {
        SetDialogShowListener(listener);
    }

    public static void addHandler(String activityClassName, ActivityFocusHandler activityFocusHandler) {
        activityFocusHandlerMap.put(activityClassName, activityFocusHandler);
    }

    public static void addHandler(String activityClassName, FragmentFocusHandler fragmentFocusHandler) {
        fragmentFocusHandlerHashMap.put(activityClassName, fragmentFocusHandler);
    }

    public static Activity getTopActivity() {
        return topActivity;
    }


    public static Window getTopDialogWindow() {
        for (WeakReference<Window> windowWeakReference : dialogWindowsSets) {
            Window window = windowWeakReference.get();
            if (window == null) {
                dialogWindowsSets.remove(windowWeakReference);
                continue;
            }
            if (window.getDecorView().getVisibility() != View.VISIBLE) {
                continue;
            }
            if (!window.getDecorView().hasWindowFocus()) {
                continue;
            }
            Log.i(SuperAppium.TAG, "get getTopDialogWindow: " + window.peekDecorView().hasWindowFocus());
            return window;
        }
        return null;
    }

    public static View getTopPupWindowView() {
        for (WeakReference<PopupWindow> popupWindowWeakReference : popupWindowSets) {
            PopupWindow popupWindow = popupWindowWeakReference.get();
            if (popupWindow == null) {
                popupWindowSets.remove(popupWindowWeakReference);
                continue;
            }
            View mDecorView = (View) CompatHelpers.getObjectField(popupWindow, "mDecorView");
            if (mDecorView == null) {
                continue;
            }
            if (mDecorView.getVisibility() != View.VISIBLE) {
                continue;
            }
            return mDecorView;
        }
        return null;
    }


    /**
     * ����xpath���ʽ
     * �ӿ��ܳ��������ϲ��view�Ͻ��б�������
     * ���ܴ���������+�ͶԻ�ͬʱ���ڵ��������ʱ����ж��Window
     * <p>
     * ʹ���߲�Ӧ�ù�ע��Щ���ѿ������ϲ���ֵĴ��ڵ�������б���
     */
    public static ViewImage tryGetTopView(String xpath) {
        //���ԴӶԻ��������ȡ
        for (WeakReference<Window> windowWeakReference : PageManager.getWindos()) {
            Window window = windowWeakReference.get();
            if (window == null) {
                PageManager.getWindos().remove(windowWeakReference);
                continue;
            }
            if (window.getDecorView().getVisibility() != View.VISIBLE) {
                continue;
            }
            if (!window.getDecorView().hasWindowFocus()) {
                continue;
            }
            ViewImages DialogWindowXpath = new ViewImage(window.getDecorView()).xpath(xpath);
            if (DialogWindowXpath.size() >= 1) {
                return DialogWindowXpath.get(0);
            }
        }
        //���Դ�pupwindow��ȡ
        for (WeakReference<PopupWindow> popupWindowWeakReference : PageManager.getPopWindos()) {
            PopupWindow popupWindow = popupWindowWeakReference.get();
            if (popupWindow == null) {
                PageManager.getPopWindos().remove(popupWindowWeakReference);
                continue;
            }
            View mDecorView = (View) CompatHelpers.getObjectField(popupWindow, "mDecorView");
            if (mDecorView == null) {
                continue;
            }
            if (mDecorView.getVisibility() != View.VISIBLE) {
                continue;
            }
            ViewImages DialogWindowXpath = new ViewImage(mDecorView).xpath(xpath);
            if (DialogWindowXpath.size() >= 1) {
                return DialogWindowXpath.get(0);
            }
        }
        return null;
    }

    /**
     * ��ȡ���ϲ�Ĳ�����ʾ��View
     * ����Ի��򣬿��ܴ���Null������
     */
    public static View getTopRootView() {
        Activity topActivity = PageManager.getTopActivity();
        if (topActivity != null) {
            View rootView = topActivity.getWindow().getDecorView();
            if (rootView.getVisibility() == View.VISIBLE) {
                return rootView;
            }
            Log.w(SuperAppium.TAG, "target activity : " + topActivity + " not visible!!");
        }

        Window dialogWindow = PageManager.getTopDialogWindow();
        if (dialogWindow != null) {
            View rootView = dialogWindow.peekDecorView();
            if (rootView.getVisibility() == View.VISIBLE) {
                return rootView;
            }
        }
        return getTopPupWindowView();

    }


    public static List<Object> getTopFragment() {
        List<Object> ret = new ArrayList<>();
        for (String theFragmentClassName : topFragmentMaps.keySet()) {
            Object topFragment = getTopFragment(theFragmentClassName);
            if (topFragment == null) {
                continue;
            }
            ret.add(topFragment);
        }
        return ret;
    }

    public static Object getTopFragment(String fragmentClassName) {
        Object fragmentObject = topFragmentMaps.get(fragmentClassName);
        if (fragmentObject == null) {
            return null;
        }
        boolean isVisible = (boolean) CompatHelpers.callMethod(fragmentObject, "isVisible");
        if (isVisible) {
            return fragmentObject;
        } else {
            topFragmentMaps.remove(fragmentClassName);
        }
        return null;
    }

    /**
     * Hook ���ܳ��������ϲ��View
     * <p>
     * Activity,Dialog,PopupWindow
     */
    private static void enablePageMonitor() {
        try {
//            Xpo
//            sedHelpers.findAndHookMethod(Activity.class, "onResume", new CompatMethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) {
//
//                    final Activity activity = (Activity) param.thisObject;
//
//                    topActivity = activity;
//                    Log.i(SuperAppium.TAG, "onWindow resume: " + activity.getClass().getName());
//                    //ִ�ж�Ӧ������
//                    trigger();
//                }
//            });

            CompatHelpers.findAndHookMethod(Application.class, "dispatchActivityResumed",
                    Activity.class, new CompatMethodHook() {
                        @Override
                        public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            topActivity = (Activity) param.args[0];
                            if (mContext == null) {
                                mContext = topActivity.getApplicationContext();
                                mClassLoader = topActivity.getClassLoader();
                            }
                            triggerActivity();
                        }
                    });
        } catch (Exception e) {
            Log.e(SuperAppium.TAG, "Hook Activity->dispatchActivityResumed error " + e.getMessage());
        }

        Class<?> fragmentClass = null;
        try {

            CompatMethodHook rc_methodHook = new CompatMethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam param) {
                    Log.i(SuperAppium.TAG, "onFragment resume: " + param.thisObject.getClass().getName());
                    topFragmentMaps.put(param.thisObject.getClass().getName(), param.thisObject);

                    triggerFragment();
                }
            };
            //����Hook
            CompatHelpers.findAndHookMethod(Fragment.class, "onResume", rc_methodHook);

            fragmentClass = mClassLoader.loadClass("android.support.v4.app.Fragment");

            if (fragmentClass != null) {
                CompatHelpers.findAndHookMethod(fragmentClass, "onResume", rc_methodHook);
            }

            Class<?> AndroidXFragmentClass = mClassLoader.loadClass("androidx.fragment.app.Fragment");

            if (AndroidXFragmentClass != null) {
                CompatHelpers.findAndHookMethod(AndroidXFragmentClass, "onResume", rc_methodHook);
            }

        } catch (Throwable e) {
            //ignore
        }

        //android.app.LocalActivityManager.LocalActivityManager
//        CompatHelpers.findAndHookConstructor(LocalActivityManager.class, Activity.class, boolean.class, new RC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                localActivityManagers.add(new WeakReference<>((LocalActivityManager) param.thisObject));
//            }
//        });


        //�������� activity����
        try {
            XpCompatEngine.hookAllConstructors(Dialog.class, new CompatMethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Window mWindow = (Window) CompatHelpers.getObjectField(param.thisObject, "mWindow");
                    if (mWindow == null) {
                        Log.w(SuperAppium.TAG, "can not get windows object for dialog: " + param.thisObject.getClass().getName());
                        return;
                    }
                    Log.i(SuperAppium.TAG, "create dialog: " + param.thisObject.getClass().getName());
                    dialogWindowsSets.add(new WeakReference<>(mWindow));
                }
            });

        } catch (Exception e) {
            Log.i(SuperAppium.TAG, "Hook  Dialog error  : " + e.getMessage());

            e.printStackTrace();
        }

        try {
            CompatHelpers.findAndHookMethod(Dialog.class, "show", new CompatMethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (mAfterAlertDialogShowListener != null) {
                        Dialog DialogObject = (Dialog) param.thisObject;
                        mAfterAlertDialogShowListener.DialogShow(DialogObject);
                    }
                }
            });
        } catch (Exception e) {
            Log.i(SuperAppium.TAG, "Hook show error  : " + e.getMessage());
            e.printStackTrace();
        }

        //popupWindow����activity����
        try {
            CompatHelpers.findAndHookConstructor(PopupWindow.class, View.class, int.class, int.class, boolean.class, new CompatMethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(SuperAppium.TAG, "create PopupWindow: " + param.thisObject.getClass().getName());
                    popupWindowSets.add(new WeakReference<>((PopupWindow) param.thisObject));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void triggerActivity() {

        final Activity activity = topActivity;
        if (activity == null) {
            Log.i(SuperAppium.TAG, "no top activity found");
            return;
        }
        //
        final ActivityFocusHandler iActivityHandler = activityFocusHandlerMap.get(activity.getClass().getName());
        if (iActivityHandler != null) {
            //��ֹ���ҳ�����Flag
            if (hasPendingActivityTask) {
                return;
            }
            hasPendingActivityTask = true;
            //��һ������CountΪ0
            triggerActivityActive(activity, iActivityHandler, 0);
        }

    }


    public static void triggerFragment() {
        for (String theFragmentClassName : topFragmentMaps.keySet()) {
            Object topFragment = getTopFragment(theFragmentClassName);
            if (topFragment == null) {
                continue;
            }
            FragmentFocusHandler fragmentFocusHandler = fragmentFocusHandlerHashMap.get(theFragmentClassName);
            if (fragmentFocusHandler == null) {
                continue;
            }
            triggerFragmentActive(getTopActivity(), topFragment, fragmentFocusHandler, 0);
        }
    }

    private static void triggerFragmentActive(final Activity activity, final Object fragment, final FragmentFocusHandler fragmentFocusHandler, final int triggerCount) {
        if (disable) {
            Log.i(SuperAppium.TAG, "Page Trigger manager disabled");
            return;
        }
        mainLooperHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!activity.hasWindowFocus()) {
                    return;
                }
                try {
                    if (fragmentFocusHandler.handleFragmentPage(fragment, activity, new ViewImage((View) CompatHelpers.callMethod(fragment, "getView")))) {
                        return;
                    }
                } catch (Throwable throwable) {
                    Log.e(SuperAppium.TAG, "error to handle fragment: " + fragment.getClass().getName(), throwable);
                }
                if (triggerCount > 1) {
                    Log.w(SuperAppium.TAG, "the activity event trigger failed too many times: " + fragmentFocusHandler.getClass());
                    return;
                }
                triggerFragmentActive(activity, fragment, fragmentFocusHandler, triggerCount + 1);
            }
        }, taskDuration);
    }

    private static void triggerActivityActive(final Activity activity, final ActivityFocusHandler activityFocusHandler, final int triggerCount) {
        if (disable) {
            Log.i(SuperAppium.TAG, "Page Trigger manager disabled");
            return;
        }
        mainLooperHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(SuperAppium.TAG, "triggerActivityActive activity: " + activity.getClass().getName() + " for ActivityFocusHandler:" + activityFocusHandler.getClass().getName());
                    hasPendingActivityTask = false;
                    //������Ӧ���¼�trueΪ�����¼� ������ִ��  �������false���ӳ�200�������ִ��
                    if (activityFocusHandler.handleActivity(activity, new ViewImage(activity.getWindow().getDecorView()))) {
                        return;
                    }
                } catch (Throwable throwable) {
                    Log.e(SuperAppium.TAG, "error to handle activity:" + activity.getClass().getName(), throwable);
                }
                if (triggerCount > 1) {
                    Log.w(SuperAppium.TAG, "the activity event trigger failed too many times: " + activityFocusHandler.getClass());
                    return;
                }
                triggerActivityActive(activity, activityFocusHandler, triggerCount + 1);
            }
        }, taskDuration);
    }

}
