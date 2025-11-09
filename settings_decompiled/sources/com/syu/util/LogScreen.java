package com.syu.util;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
/* loaded from: classes2.dex */
public class LogScreen {
    static LogScreen instance = null;
    public static int mColor = -2147418113;
    public static Context mContext = null;
    public static int mGravity = 85;
    public static int mTypeLayoutParams = 2003;
    TextView logPreview;
    WindowManager.LayoutParams mParams;
    FrameLayout rootView;
    WindowManager wm;
    StringBuffer logs = new StringBuffer();
    Runnable checkLogPreview = new Runnable() { // from class: com.syu.util.LogScreen.1
        @Override // java.lang.Runnable
        public void run() {
            HandlerUI.getInstance().post(new Runnable() { // from class: com.syu.util.LogScreen.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (LogScreen.this.rootView.getParent() == null) {
                        LogScreen.this.wm.addView(LogScreen.this.rootView, LogScreen.this.mParams);
                    }
                }
            });
            Handler handler = ThreadMap.mHashMapHandler.get("LogPreview_Work");
            if (handler != null) {
                handler.removeCallbacks(this);
                handler.postDelayed(this, 25000L);
            }
        }
    };

    LogScreen(Context context) {
        Context applicationContext = context.getApplicationContext();
        mContext = applicationContext;
        this.wm = (WindowManager) applicationContext.getSystemService("window");
        init();
        ThreadMap.startThread("LogPreview_Work", this.checkLogPreview, true, 1);
    }

    public void init() {
        if (mTypeLayoutParams == 2003) {
            mTypeLayoutParams = 2038;
        }
        this.mParams = new WindowManager.LayoutParams(-1, -1, 0, 0, mTypeLayoutParams, 56, 1);
        this.rootView = new FrameLayout(mContext);
        TextView textView = new TextView(mContext);
        this.logPreview = textView;
        textView.setGravity(mGravity);
        this.logPreview.setTextColor(mColor);
        this.logPreview.setTextSize(16.0f);
        this.logPreview.setPadding(0, 0, 60, 0);
        this.rootView.addView(this.logPreview, new FrameLayout.LayoutParams(-1, -2));
    }

    public static void show(String str) {
        Context context = mContext;
        if (context != null) {
            if (instance == null) {
                instance = new LogScreen(context);
            }
            if (instance.logs.length() >= 20480) {
                instance.logs.replace(0, 4096, "");
            }
            StringBuffer stringBuffer = instance.logs;
            stringBuffer.append(String.valueOf(mContext.getPackageName()) + "-" + SystemClock.uptimeMillis() + ":" + str + "\n");
            HandlerUI.getInstance().post(new Runnable() { // from class: com.syu.util.LogScreen.2
                @Override // java.lang.Runnable
                public void run() {
                    LogScreen logScreen = LogScreen.instance;
                    logScreen.logPreview.setText(logScreen.logs.toString());
                }
            });
        }
    }
}
