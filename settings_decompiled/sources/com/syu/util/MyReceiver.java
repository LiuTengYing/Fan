package com.syu.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityManager;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.List;
/* loaded from: classes2.dex */
public class MyReceiver extends BroadcastReceiver {
    private String oldPakcage = "";
    private String oldClass = "";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.i("fangli", "call onReceive === action = " + intent.getAction());
        if (intent.getAction().equals("com.android.settings.action.requestforimei")) {
            Intent intent2 = new Intent("com.android.settings.action.resultforimei");
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            int phoneCount = telephonyManager.getPhoneCount();
            intent2.putExtra("imei_count", phoneCount);
            for (int i = 0; i < phoneCount; i++) {
                intent2.putExtra("imei_" + i, telephonyManager.getDeviceId(i));
            }
            SettingsApplication.mApplication.sendBroadcast(intent2);
        } else if (intent.getAction().equals("com.android.settings.action.resultforimei")) {
            Bundle extras = intent.getExtras();
            if (extras == null || !extras.containsKey("imei_count")) {
                return;
            }
            for (int i2 = 0; i2 < extras.getInt("imei_count", 0); i2++) {
                StringBuilder sb = new StringBuilder();
                sb.append("imei_");
                sb.append(i2);
                sb.append(" = ");
                sb.append(extras.getString("imei_" + i2));
                Log.i("fangli", sb.toString());
            }
        } else if (intent.getAction().equals("com.android.settings.action.resizefont")) {
            Bundle extras2 = intent.getExtras();
            if (extras2 == null || !extras2.containsKey("font_size")) {
                return;
            }
            float f = extras2.getFloat("font_size");
            Log.i("fangli", "font_size = " + f);
            Settings.System.putFloat(context.getContentResolver(), "font_scale", f);
        } else if (intent.getAction().equals("com.syu.accessibility.servicestate")) {
            Bundle extras3 = intent.getExtras();
            String string = extras3.getString("package_name", "");
            String string2 = extras3.getString("class_name", "");
            boolean z = extras3.getBoolean(ToggleSubscriptionDialogActivity.ARG_enable, false);
            Log.i("fangli", "packageName = " + string + ", className = " + string2 + ", enable = " + z);
            if ("".equals(string) || "".equals(string2)) {
                return;
            }
            ComponentName componentName = new ComponentName(string, string2);
            if (getAccessibilityServiceInfo(context, componentName) != null) {
                try {
                    AccessibilityUtils.setAccessibilityServiceState(context, componentName, z);
                    return;
                } catch (Exception e) {
                    Log.i("fangli", Log.getStackTraceString(e));
                    return;
                }
            }
            Log.i("fangli", "getAccessibilityServiceInfo is null");
        } else if (intent.getAction().contains("FOURCAMERA2_BROADCAST_RECV")) {
            Log.d("fangli", "onReceive: " + intent.getBooleanExtra("Show", true));
            if (intent.getBooleanExtra("Show", true)) {
                dismissAllDialogs();
            }
        } else if (intent.getAction().contains("com.syu.settings.wiredscreenTest")) {
            getDisplay(context);
        }
    }

    private void getDisplay(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        int i = activityManager.getRunningTasks(1).get(0).taskId;
        final String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        final String className = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        Display[] displays = ((DisplayManager) SettingsApplication.mApplication.getSystemService(DisplayManager.class)).getDisplays();
        if (displays == null || displays.length <= 1) {
            return;
        }
        final int displayId = displays[1].getDisplayId();
        Log.d("fangli", "getDisplay: " + displayId + "  " + displays[1].getName() + displays.length);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.syu.util.MyReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                MyReceiver.this.moveTaskToVdisplay(displayId, packageName, className);
            }
        }, 1500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveTaskToVdisplay(int i, String str, String str2) {
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchDisplayId(i);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setFlags(4194304);
        intent.setComponent(new ComponentName(str, str2));
        SettingsApplication.mApplication.startActivity(intent, makeBasic.toBundle());
    }

    private AccessibilityServiceInfo getAccessibilityServiceInfo(Context context, ComponentName componentName) {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) context.getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if (componentName.equals(accessibilityServiceInfo.getComponentName())) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    public void dismissAllDialogs() {
        FragmentManager childFragmentManager;
        List<Fragment> fragments;
        SettingsBaseActivity baseActivity = SettingsApplication.mApplication.getBaseActivity();
        if (baseActivity == null) {
            return;
        }
        Log.d("fangli", "dismissAllDialogs: ");
        List<Fragment> fragments2 = baseActivity.getSupportFragmentManager().getFragments();
        if (fragments2 == null) {
            return;
        }
        for (Fragment fragment : fragments2) {
            if (fragment.isVisible() && (childFragmentManager = fragment.getChildFragmentManager()) != null && (fragments = childFragmentManager.getFragments()) != null) {
                for (Fragment fragment2 : fragments) {
                    if (fragment2 instanceof DialogFragment) {
                        ((DialogFragment) fragment2).dismissAllowingStateLoss();
                    }
                }
            }
        }
    }
}
