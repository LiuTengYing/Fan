package com.android.settings.safetycenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.safetycenter.SafetyEvent;
import com.android.settings.security.ScreenLockPreferenceDetailsUtils;
import com.google.common.collect.ImmutableList;
import java.util.List;
/* loaded from: classes.dex */
public class SafetySourceBroadcastReceiver extends BroadcastReceiver {
    private static final SafetyEvent EVENT_DEVICE_REBOOTED = new SafetyEvent.Builder(600).build();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (SafetyCenterManagerWrapper.get().isEnabled(context)) {
            if ("android.safetycenter.action.REFRESH_SAFETY_SOURCES".equals(intent.getAction())) {
                String[] stringArrayExtra = intent.getStringArrayExtra("android.safetycenter.extra.REFRESH_SAFETY_SOURCE_IDS");
                String stringExtra = intent.getStringExtra("android.safetycenter.extra.REFRESH_SAFETY_SOURCES_BROADCAST_ID");
                if (stringArrayExtra == null || stringArrayExtra.length <= 0 || stringExtra == null) {
                    return;
                }
                refreshSafetySources(context, ImmutableList.copyOf(stringArrayExtra), new SafetyEvent.Builder(200).setRefreshBroadcastId(stringExtra).build());
            } else if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                refreshAllSafetySources(context, EVENT_DEVICE_REBOOTED);
            }
        }
    }

    private static void refreshSafetySources(Context context, List<String> list, SafetyEvent safetyEvent) {
        if (list.contains("AndroidLockScreen")) {
            LockScreenSafetySource.setSafetySourceData(context, new ScreenLockPreferenceDetailsUtils(context), safetyEvent);
        }
        if (list.contains("AndroidBiometrics")) {
            BiometricsSafetySource.setSafetySourceData(context, safetyEvent);
        }
    }

    private static void refreshAllSafetySources(Context context, SafetyEvent safetyEvent) {
        LockScreenSafetySource.setSafetySourceData(context, new ScreenLockPreferenceDetailsUtils(context), safetyEvent);
        BiometricsSafetySource.setSafetySourceData(context, safetyEvent);
    }
}
