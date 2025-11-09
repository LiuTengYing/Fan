package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter2Manager;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class RemoteVolumeGroupController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnDestroy, LocalMediaManager.DeviceCallback {
    private static final String KEY_REMOTE_VOLUME_GROUP = "remote_media_group";
    static final String SWITCHER_PREFIX = "OUTPUT_SWITCHER";
    private static final String TAG = "RemoteVolumePrefCtr";
    LocalMediaManager mLocalMediaManager;
    private PreferenceCategory mPreferenceCategory;
    MediaRouter2Manager mRouterManager;
    private List<RoutingSessionInfo> mRoutingSessionInfos;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_REMOTE_VOLUME_GROUP;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public /* bridge */ /* synthetic */ void onAboutToConnectDeviceAdded(String str, String str2, Drawable drawable) {
        super.onAboutToConnectDeviceAdded(str, str2, drawable);
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public /* bridge */ /* synthetic */ void onAboutToConnectDeviceRemoved() {
        super.onAboutToConnectDeviceRemoved();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public /* bridge */ /* synthetic */ void onDeviceAttributesChanged() {
        super.onDeviceAttributesChanged();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public /* bridge */ /* synthetic */ void onRequestFailed(int i) {
        super.onRequestFailed(i);
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RemoteVolumeGroupController(Context context, String str) {
        super(context, str);
        this.mRoutingSessionInfos = new ArrayList();
        if (this.mLocalMediaManager == null) {
            LocalMediaManager localMediaManager = new LocalMediaManager(this.mContext, null, null);
            this.mLocalMediaManager = localMediaManager;
            localMediaManager.registerCallback(this);
            this.mLocalMediaManager.startScan();
        }
        this.mRouterManager = MediaRouter2Manager.getInstance(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mRoutingSessionInfos.isEmpty() ? 2 : 1;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        initRemoteMediaSession();
        refreshPreference();
    }

    private void initRemoteMediaSession() {
        this.mRoutingSessionInfos.clear();
        for (RoutingSessionInfo routingSessionInfo : this.mLocalMediaManager.getActiveMediaSession()) {
            if (!routingSessionInfo.isSystemSession()) {
                this.mRoutingSessionInfos.add(routingSessionInfo);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mLocalMediaManager.unregisterCallback(this);
        this.mLocalMediaManager.stopScan();
    }

    private synchronized void refreshPreference() {
        boolean z;
        Preference preference;
        if (!isAvailable()) {
            this.mPreferenceCategory.setVisible(false);
            return;
        }
        CharSequence text = this.mContext.getText(R$string.remote_media_volume_option_title);
        this.mPreferenceCategory.setVisible(true);
        for (RoutingSessionInfo routingSessionInfo : this.mRoutingSessionInfos) {
            CharSequence applicationLabel = Utils.getApplicationLabel(this.mContext, routingSessionInfo.getClientPackageName());
            RemoteVolumeSeekBarPreference remoteVolumeSeekBarPreference = (RemoteVolumeSeekBarPreference) this.mPreferenceCategory.findPreference(routingSessionInfo.getId());
            if (remoteVolumeSeekBarPreference != null) {
                if (remoteVolumeSeekBarPreference.getProgress() != routingSessionInfo.getVolume()) {
                    remoteVolumeSeekBarPreference.setProgress(routingSessionInfo.getVolume());
                }
                remoteVolumeSeekBarPreference.setEnabled(this.mLocalMediaManager.shouldEnableVolumeSeekBar(routingSessionInfo));
            } else {
                RemoteVolumeSeekBarPreference remoteVolumeSeekBarPreference2 = new RemoteVolumeSeekBarPreference(this.mContext);
                remoteVolumeSeekBarPreference2.setKey(routingSessionInfo.getId());
                remoteVolumeSeekBarPreference2.setTitle(text);
                remoteVolumeSeekBarPreference2.setMax(routingSessionInfo.getVolumeMax());
                remoteVolumeSeekBarPreference2.setProgress(routingSessionInfo.getVolume());
                remoteVolumeSeekBarPreference2.setMin(0);
                remoteVolumeSeekBarPreference2.setOnPreferenceChangeListener(this);
                remoteVolumeSeekBarPreference2.setIcon(R$drawable.ic_volume_remote);
                remoteVolumeSeekBarPreference2.setEnabled(this.mLocalMediaManager.shouldEnableVolumeSeekBar(routingSessionInfo));
                this.mPreferenceCategory.addPreference(remoteVolumeSeekBarPreference2);
            }
            Preference findPreference = this.mPreferenceCategory.findPreference(SWITCHER_PREFIX + routingSessionInfo.getId());
            boolean shouldDisableMediaOutput = this.mLocalMediaManager.shouldDisableMediaOutput(routingSessionInfo.getClientPackageName());
            String string = this.mContext.getString(R$string.media_output_label_title, applicationLabel);
            if (findPreference != null) {
                if (!shouldDisableMediaOutput) {
                    applicationLabel = string;
                }
                findPreference.setTitle(applicationLabel);
                findPreference.setSummary(routingSessionInfo.getName());
                findPreference.setEnabled(!shouldDisableMediaOutput);
            } else {
                Preference preference2 = new Preference(this.mContext);
                preference2.setKey(SWITCHER_PREFIX + routingSessionInfo.getId());
                if (!shouldDisableMediaOutput) {
                    applicationLabel = string;
                }
                preference2.setTitle(applicationLabel);
                preference2.setSummary(routingSessionInfo.getName());
                preference2.setEnabled(!shouldDisableMediaOutput);
                this.mPreferenceCategory.addPreference(preference2);
            }
        }
        for (int i = 0; i < this.mPreferenceCategory.getPreferenceCount(); i += 2) {
            Preference preference3 = this.mPreferenceCategory.getPreference(i);
            Iterator<RoutingSessionInfo> it = this.mRoutingSessionInfos.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (TextUtils.equals(preference3.getKey(), it.next().getId())) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            if (!z && (preference = this.mPreferenceCategory.getPreference(i + 1)) != null) {
                this.mPreferenceCategory.removePreference(preference3);
                this.mPreferenceCategory.removePreference(preference);
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(final Preference preference, final Object obj) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.notification.RemoteVolumeGroupController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RemoteVolumeGroupController.this.lambda$onPreferenceChange$0(preference, obj);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceChange$0(Preference preference, Object obj) {
        this.mLocalMediaManager.adjustSessionVolume(preference.getKey(), ((Integer) obj).intValue());
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.getKey().startsWith(SWITCHER_PREFIX)) {
            for (RoutingSessionInfo routingSessionInfo : this.mRoutingSessionInfos) {
                if (TextUtils.equals(routingSessionInfo.getId(), preference.getKey().substring(15))) {
                    this.mContext.sendBroadcast(new Intent().setAction("com.android.systemui.action.LAUNCH_MEDIA_OUTPUT_DIALOG").setPackage("com.android.systemui").putExtra("package_name", routingSessionInfo.getClientPackageName()));
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceListUpdate(List<MediaDevice> list) {
        if (this.mPreferenceCategory == null) {
            return;
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.notification.RemoteVolumeGroupController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RemoteVolumeGroupController.this.lambda$onDeviceListUpdate$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDeviceListUpdate$1() {
        initRemoteMediaSession();
        refreshPreference();
    }
}
