package com.android.settings.safetycenter;

import android.content.Context;
import android.safetycenter.SafetyCenterManager;
import android.safetycenter.SafetyEvent;
import android.safetycenter.SafetySourceData;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class SafetyCenterManagerWrapper {
    @VisibleForTesting
    public static SafetyCenterManagerWrapper sInstance;

    private SafetyCenterManagerWrapper() {
    }

    public static SafetyCenterManagerWrapper get() {
        if (sInstance == null) {
            sInstance = new SafetyCenterManagerWrapper();
        }
        return sInstance;
    }

    public void setSafetySourceData(Context context, String str, SafetySourceData safetySourceData, SafetyEvent safetyEvent) {
        SafetyCenterManager safetyCenterManager = (SafetyCenterManager) context.getSystemService(SafetyCenterManager.class);
        if (safetyCenterManager == null) {
            Log.e("SafetyCenterManagerWrap", "System service SAFETY_CENTER_SERVICE (SafetyCenterManager) is null");
            return;
        }
        try {
            safetyCenterManager.setSafetySourceData(str, safetySourceData, safetyEvent);
        } catch (Exception e) {
            Log.e("SafetyCenterManagerWrap", "Failed to send SafetySourceData", e);
        }
    }

    public boolean isEnabled(Context context) {
        if (context == null) {
            Log.e("SafetyCenterManagerWrap", "Context is null at SafetyCenterManagerWrapper#isEnabled");
            return false;
        }
        SafetyCenterManager safetyCenterManager = (SafetyCenterManager) context.getSystemService(SafetyCenterManager.class);
        if (safetyCenterManager == null) {
            Log.w("SafetyCenterManagerWrap", "System service SAFETY_CENTER_SERVICE (SafetyCenterManager) is null");
            return false;
        }
        try {
            return safetyCenterManager.isSafetyCenterEnabled();
        } catch (RuntimeException e) {
            Log.e("SafetyCenterManagerWrap", "Calling isSafetyCenterEnabled failed.", e);
            return false;
        }
    }
}
