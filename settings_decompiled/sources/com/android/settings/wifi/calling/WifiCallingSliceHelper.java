package com.android.settings.wifi.calling;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsMmTelManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settingslib.Utils;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
public class WifiCallingSliceHelper {
    private final Context mContext;

    public WifiCallingSliceHelper(Context context) {
        this.mContext = context;
    }

    public Slice createWifiCallingSlice(Uri uri) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            Log.d("WifiCallingSliceHelper", "Invalid subscription Id");
            return null;
        } else if (!queryImsState(defaultVoiceSubId).isWifiCallingProvisioned()) {
            Log.d("WifiCallingSliceHelper", "Wifi calling is either not provisioned or not enabled by Platform");
            return null;
        } else {
            boolean isWifiCallingEnabled = isWifiCallingEnabled();
            if (getWifiCallingCarrierActivityIntent(defaultVoiceSubId) != null && !isWifiCallingEnabled) {
                Log.d("WifiCallingSliceHelper", "Needs Activation");
                Resources resourcesForSubId = getResourcesForSubId(defaultVoiceSubId);
                return getNonActionableWifiCallingSlice(resourcesForSubId.getText(R$string.wifi_calling_settings_title), resourcesForSubId.getText(R$string.wifi_calling_settings_activation_instructions), uri, getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"));
            }
            return getWifiCallingSlice(uri, isWifiCallingEnabled, defaultVoiceSubId);
        }
    }

    private boolean isWifiCallingEnabled() {
        WifiCallingQueryImsState queryImsState = queryImsState(getDefaultVoiceSubId());
        return queryImsState.isEnabledByUser() && queryImsState.isAllowUserControl();
    }

    private Slice getWifiCallingSlice(Uri uri, boolean z, int i) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.wifi_signal);
        Resources resourcesForSubId = getResourcesForSubId(i);
        ListBuilder accentColor = new ListBuilder(this.mContext, uri, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        int i2 = R$string.wifi_calling_settings_title;
        return accentColor.addRow(rowBuilder.setTitle(resourcesForSubId.getText(i2)).addEndItem(SliceAction.createToggle(getBroadcastIntent("com.android.settings.wifi.calling.action.WIFI_CALLING_CHANGED", z), null, z)).setPrimaryAction(SliceAction.createDeeplink(getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"), createWithResource, 0, resourcesForSubId.getText(i2)))).build();
    }

    public Slice createWifiCallingPreferenceSlice(Uri uri) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            Log.d("WifiCallingSliceHelper", "Invalid Subscription Id");
            return null;
        }
        boolean isCarrierConfigManagerKeyEnabled = isCarrierConfigManagerKeyEnabled("editable_wfc_mode_bool", defaultVoiceSubId, false);
        boolean isCarrierConfigManagerKeyEnabled2 = isCarrierConfigManagerKeyEnabled("carrier_wfc_supports_wifi_only_bool", defaultVoiceSubId, true);
        if (!isCarrierConfigManagerKeyEnabled) {
            Log.d("WifiCallingSliceHelper", "Wifi calling preference is not editable");
            return null;
        } else if (!queryImsState(defaultVoiceSubId).isWifiCallingProvisioned()) {
            Log.d("WifiCallingSliceHelper", "Wifi calling is either not provisioned or not enabled by platform");
            return null;
        } else {
            try {
                ImsMmTelManager imsMmTelManager = getImsMmTelManager(defaultVoiceSubId);
                boolean isWifiCallingEnabled = isWifiCallingEnabled();
                int wfcMode = getWfcMode(imsMmTelManager);
                if (!isWifiCallingEnabled) {
                    Resources resourcesForSubId = getResourcesForSubId(defaultVoiceSubId);
                    return getNonActionableWifiCallingSlice(resourcesForSubId.getText(R$string.wifi_calling_mode_title), resourcesForSubId.getText(R$string.wifi_calling_turn_on), uri, getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"));
                }
                return getWifiCallingPreferenceSlice(isCarrierConfigManagerKeyEnabled2, wfcMode, uri, defaultVoiceSubId);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.e("WifiCallingSliceHelper", "Unable to get wifi calling preferred mode", e);
                return null;
            }
        }
    }

    private Slice getWifiCallingPreferenceSlice(boolean z, int i, Uri uri, int i2) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.wifi_signal);
        Resources resourcesForSubId = getResourcesForSubId(i2);
        ListBuilder accentColor = new ListBuilder(this.mContext, uri, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        ListBuilder.HeaderBuilder headerBuilder = new ListBuilder.HeaderBuilder();
        int i3 = R$string.wifi_calling_mode_title;
        ListBuilder.HeaderBuilder primaryAction = headerBuilder.setTitle(resourcesForSubId.getText(i3)).setPrimaryAction(SliceAction.createDeeplink(getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"), createWithResource, 0, resourcesForSubId.getText(i3)));
        if (!com.android.settings.Utils.isSettingsIntelligence(this.mContext)) {
            primaryAction.setSubtitle(getWifiCallingPreferenceSummary(i, i2));
        }
        accentColor.setHeader(primaryAction);
        if (z) {
            accentColor.addRow(wifiPreferenceRowBuilder(accentColor, 17041738, "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_ONLY", i == 0, i2));
        }
        accentColor.addRow(wifiPreferenceRowBuilder(accentColor, 17041739, "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_PREFERRED", i == 2, i2));
        accentColor.addRow(wifiPreferenceRowBuilder(accentColor, 17041737, "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_CELLULAR_PREFERRED", i == 1, i2));
        return accentColor.build();
    }

    private ListBuilder.RowBuilder wifiPreferenceRowBuilder(ListBuilder listBuilder, int i, String str, boolean z, int i2) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.radio_button_check);
        Resources resourcesForSubId = getResourcesForSubId(i2);
        return new ListBuilder.RowBuilder().setTitle(resourcesForSubId.getText(i)).setTitleItem(SliceAction.createToggle(getBroadcastIntent(str, z), createWithResource, resourcesForSubId.getText(i), z));
    }

    private CharSequence getWifiCallingPreferenceSummary(int i, int i2) {
        Resources resourcesForSubId = getResourcesForSubId(i2);
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return null;
                }
                return resourcesForSubId.getText(17041739);
            }
            return resourcesForSubId.getText(17041737);
        }
        return resourcesForSubId.getText(17041738);
    }

    protected ImsMmTelManager getImsMmTelManager(int i) {
        return ImsMmTelManager.createForSubscriptionId(i);
    }

    private int getWfcMode(final ImsMmTelManager imsMmTelManager) throws InterruptedException, ExecutionException, TimeoutException {
        FutureTask futureTask = new FutureTask(new Callable<Integer>() { // from class: com.android.settings.wifi.calling.WifiCallingSliceHelper.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Integer call() {
                return Integer.valueOf(imsMmTelManager.getVoWiFiModeSetting());
            }
        });
        Executors.newSingleThreadExecutor().execute(futureTask);
        return ((Integer) futureTask.get(2000L, TimeUnit.MILLISECONDS)).intValue();
    }

    public void handleWifiCallingChanged(Intent intent) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId) && intent.hasExtra("android.app.slice.extra.TOGGLE_STATE")) {
            if (queryImsState(defaultVoiceSubId).isWifiCallingProvisioned()) {
                boolean isWifiCallingEnabled = isWifiCallingEnabled();
                boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", isWifiCallingEnabled);
                Intent wifiCallingCarrierActivityIntent = getWifiCallingCarrierActivityIntent(defaultVoiceSubId);
                if (booleanExtra == isWifiCallingEnabled && wifiCallingCarrierActivityIntent == null) {
                    getImsMmTelManager(defaultVoiceSubId).setVoWiFiSettingEnabled(!booleanExtra);
                } else {
                    Log.w("WifiCallingSliceHelper", "action not taken: subId " + defaultVoiceSubId + " from " + isWifiCallingEnabled + " to " + booleanExtra);
                }
            } else {
                Log.w("WifiCallingSliceHelper", "action not taken: subId " + defaultVoiceSubId + " needs provision");
            }
        } else {
            Log.w("WifiCallingSliceHelper", "action not taken: subId " + defaultVoiceSubId);
        }
        this.mContext.getContentResolver().notifyChange(CustomSliceRegistry.WIFI_CALLING_URI, null);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0070, code lost:
        if (r3 != false) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void handleWifiCallingPreferenceChanged(android.content.Intent r9) {
        /*
            r8 = this;
            int r0 = r8.getDefaultVoiceSubId()
            boolean r1 = android.telephony.SubscriptionManager.isValidSubscriptionId(r0)
            if (r1 == 0) goto L7d
            java.lang.String r1 = "editable_wfc_mode_bool"
            r2 = 0
            boolean r1 = r8.isCarrierConfigManagerKeyEnabled(r1, r0, r2)
            java.lang.String r3 = "carrier_wfc_supports_wifi_only_bool"
            r4 = 1
            boolean r3 = r8.isCarrierConfigManagerKeyEnabled(r3, r0, r4)
            com.android.settings.network.ims.WifiCallingQueryImsState r5 = r8.queryImsState(r0)
            if (r1 == 0) goto L7d
            boolean r1 = r5.isWifiCallingProvisioned()
            if (r1 == 0) goto L7d
            boolean r1 = r5.isEnabledByUser()
            if (r1 == 0) goto L7d
            boolean r1 = r5.isAllowUserControl()
            if (r1 == 0) goto L7d
            android.telephony.ims.ImsMmTelManager r0 = r8.getImsMmTelManager(r0)
            int r1 = r0.getVoWiFiModeSetting()
            java.lang.String r9 = r9.getAction()
            r9.hashCode()
            int r5 = r9.hashCode()
            r6 = 2
            r7 = -1
            switch(r5) {
                case -86230637: goto L60;
                case 176882490: goto L55;
                case 495970216: goto L4a;
                default: goto L48;
            }
        L48:
            r9 = r7
            goto L6a
        L4a:
            java.lang.String r5 = "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_CELLULAR_PREFERRED"
            boolean r9 = r9.equals(r5)
            if (r9 != 0) goto L53
            goto L48
        L53:
            r9 = r6
            goto L6a
        L55:
            java.lang.String r5 = "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_ONLY"
            boolean r9 = r9.equals(r5)
            if (r9 != 0) goto L5e
            goto L48
        L5e:
            r9 = r4
            goto L6a
        L60:
            java.lang.String r5 = "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_PREFERRED"
            boolean r9 = r9.equals(r5)
            if (r9 != 0) goto L69
            goto L48
        L69:
            r9 = r2
        L6a:
            switch(r9) {
                case 0: goto L75;
                case 1: goto L70;
                case 2: goto L6e;
                default: goto L6d;
            }
        L6d:
            goto L73
        L6e:
            r2 = r4
            goto L76
        L70:
            if (r3 == 0) goto L73
            goto L76
        L73:
            r2 = r7
            goto L76
        L75:
            r2 = r6
        L76:
            if (r2 == r7) goto L7d
            if (r2 == r1) goto L7d
            r0.setVoWiFiModeSetting(r2)
        L7d:
            android.content.Context r8 = r8.mContext
            android.content.ContentResolver r8 = r8.getContentResolver()
            android.net.Uri r9 = com.android.settings.slices.CustomSliceRegistry.WIFI_CALLING_PREFERENCE_URI
            r0 = 0
            r8.notifyChange(r9, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.calling.WifiCallingSliceHelper.handleWifiCallingPreferenceChanged(android.content.Intent):void");
    }

    private Slice getNonActionableWifiCallingSlice(CharSequence charSequence, CharSequence charSequence2, Uri uri, PendingIntent pendingIntent) {
        ListBuilder.RowBuilder primaryAction = new ListBuilder.RowBuilder().setTitle(charSequence).setPrimaryAction(SliceAction.createDeeplink(pendingIntent, IconCompat.createWithResource(this.mContext, R$drawable.wifi_signal), 1, charSequence));
        if (!com.android.settings.Utils.isSettingsIntelligence(this.mContext)) {
            primaryAction.setSubtitle(charSequence2);
        }
        return new ListBuilder(this.mContext, uri, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext)).addRow(primaryAction).build();
    }

    protected boolean isCarrierConfigManagerKeyEnabled(String str, int i, boolean z) {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = getCarrierConfigManager(this.mContext);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            return false;
        }
        return configForSubId.getBoolean(str, z);
    }

    protected CarrierConfigManager getCarrierConfigManager(Context context) {
        return (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    protected int getDefaultVoiceSubId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    protected Intent getWifiCallingCarrierActivityIntent(int i) {
        PersistableBundle configForSubId;
        ComponentName unflattenFromString;
        CarrierConfigManager carrierConfigManager = getCarrierConfigManager(this.mContext);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            return null;
        }
        String string = configForSubId.getString("wfc_emergency_address_carrier_app_string");
        if (TextUtils.isEmpty(string) || (unflattenFromString = ComponentName.unflattenFromString(string)) == null) {
            return null;
        }
        Intent intent = new Intent();
        intent.setComponent(unflattenFromString);
        return intent;
    }

    private PendingIntent getBroadcastIntent(String str, boolean z) {
        Intent intent = new Intent(str);
        intent.setClass(this.mContext, SliceBroadcastReceiver.class);
        intent.addFlags(268435456);
        intent.putExtra("android.app.slice.extra.TOGGLE_STATE", z);
        return PendingIntent.getBroadcast(this.mContext, 0, intent, 335544320);
    }

    private PendingIntent getActivityIntent(String str) {
        Intent intent = new Intent(str);
        intent.setPackage("com.android.settings");
        intent.addFlags(268435456);
        return PendingIntent.getActivity(this.mContext, 0, intent, 67108864);
    }

    private Resources getResourcesForSubId(int i) {
        return SubscriptionManager.getResourcesForSubId(this.mContext, i);
    }

    WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }
}
