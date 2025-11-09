package com.android.settings.connecteddevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$bool;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsApplication;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.LayoutPreferences;
import com.android.settings.widget.RadioPreference;
import com.android.settings.widget.WiredCastPreference;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class WiredScreenCastSettings extends SettingsPreferenceFragment {
    private static final boolean IS_SUPPORT_WIRED_SCREEN = SystemProperties.getBoolean("ro.vendor.display.multidisplay", false);
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.wired_screen_cast_settings) { // from class: com.android.settings.connecteddevice.WiredScreenCastSettings.3
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return WiredScreenCastSettings.IS_SUPPORT_WIRED_SCREEN && context.getResources().getBoolean(R$bool.config_multi_screen_cast);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (((UserManager) context.getSystemService(UserManager.class)).isGuestUser()) {
                nonIndexableKeys.add("wired_screen_case_mode_different");
            }
            return nonIndexableKeys;
        }
    };
    private AudioReceiver mAudioReceiver;
    private LayoutPreferences mAudioTitle;
    private Context mContext;
    private RadioPreference mDefaultDisplayPref;
    private PreferenceCategory mDeviceListCategory;
    private RadioPreference mHdmiAudioPref;
    private Preference mHdmiOutputPreference;
    private RadioPreference mMirrorDisplayPref;
    private RadioPreference mPhoneAudioPref;
    private Preference mTouchActivity;
    private Preference mUsbOutputPreference;
    private WiredCastPreference mWiredCastPreference;
    private Handler mHandler = new Handler() { // from class: com.android.settings.connecteddevice.WiredScreenCastSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            WiredScreenCastSettings.this.updatePreviewVideo();
        }
    };
    private ContentObserver mDeviceListObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.connecteddevice.WiredScreenCastSettings.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            WiredScreenCastSettings.this.updateDeviceList();
            WiredScreenCastSettings.this.updatePreviewVideo();
            if (WiredScreenCastSettings.this.mUsbOutputPreference != null) {
                if (WiredScreenCastSettings.this.getUSBConnectForProjection()) {
                    WiredScreenCastSettings.this.mUsbOutputPreference.setVisible(true);
                } else {
                    WiredScreenCastSettings.this.mUsbOutputPreference.setVisible(false);
                }
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1264;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        addPreferencesFromResource(R$xml.wired_screen_cast_settings);
        initAllPreference();
        updateDeviceList();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.action.AUDIO_OUTPUT_SWITCH");
        AudioReceiver audioReceiver = new AudioReceiver();
        this.mAudioReceiver = audioReceiver;
        this.mContext.registerReceiver(audioReceiver, intentFilter);
    }

    private void initAllPreference() {
        this.mWiredCastPreference = (WiredCastPreference) findPreference("wired_screen_cast_image");
        this.mDefaultDisplayPref = (RadioPreference) findPreference("wired_screen_case_mode_different");
        this.mUsbOutputPreference = findPreference("usb_video_output");
        this.mHdmiOutputPreference = findPreference("hdmi_video_output");
        this.mTouchActivity = findPreference("touch_activity");
        this.mHdmiAudioPref = (RadioPreference) findPreference("audio_hdmi");
        this.mPhoneAudioPref = (RadioPreference) findPreference("audio_phone");
        this.mAudioTitle = (LayoutPreferences) findPreference("audio_title");
        if (SystemProperties.getBoolean("persist.syu.showCastAudio", false)) {
            this.mAudioTitle.setVisible(true);
            this.mHdmiAudioPref.setVisible(true);
            this.mPhoneAudioPref.setVisible(true);
        } else {
            this.mAudioTitle.setVisible(false);
            this.mHdmiAudioPref.setVisible(false);
            this.mPhoneAudioPref.setVisible(false);
        }
        if (((UserManager) this.mContext.getSystemService(UserManager.class)).isGuestUser()) {
            this.mDefaultDisplayPref.setVisible(false);
        }
        if (getUSBConnectForProjection()) {
            this.mUsbOutputPreference.setVisible(true);
        } else {
            this.mUsbOutputPreference.setVisible(false);
        }
        this.mMirrorDisplayPref = (RadioPreference) findPreference("wired_screen_case_mode_same");
        this.mTouchActivity.setVisible(SystemProperties.getBoolean("persist.syu.showTouchPad", false));
        updatePreviewVideo();
        updatePreViewAudio();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreViewAudio() {
        this.mHdmiAudioPref.setChecked(getCurrentAudioOutput() == 1);
        this.mPhoneAudioPref.setChecked(getCurrentAudioOutput() != 1);
    }

    private int getCurrentAudioOutput() {
        return AudioSystem.getForceUse(1) == 0 ? 1 : 0;
    }

    private void switchAudioDevices(int i) {
        if (i == 1) {
            AudioSystem.setForceUse(1, 0);
            AudioSystem.setForceUse(5, 0);
            AudioSystem.setForceUse(4, 0);
            AudioSystem.setForceUse(0, 0);
        } else {
            AudioSystem.setForceUse(1, 1);
            AudioSystem.setForceUse(4, 1);
            AudioSystem.setForceUse(0, 1);
            AudioSystem.setForceUse(5, 0);
        }
        this.mContext.sendBroadcast(new Intent("android.action.AUDIO_OUTPUT_SWITCH"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeviceList() {
        this.mDeviceListCategory = (PreferenceCategory) findPreference("wired_screen_cast_device_list");
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "multidisplay_info");
        if (TextUtils.isEmpty(string)) {
            this.mDeviceListCategory.setVisible(false);
            this.mHdmiOutputPreference.setVisible(false);
            return;
        }
        String[] split = string.split(",");
        this.mDeviceListCategory.removeAll();
        this.mDeviceListCategory.setVisible(true);
        this.mHdmiOutputPreference.setVisible(true);
        for (String str : split) {
            Preference preference = new Preference(this.mContext);
            preference.setTitle(str);
            preference.setSummary(R$string.wired_screen_cast_connected);
            this.mDeviceListCategory.addPreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreviewVideo() {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "cast_mode");
        boolean z = TextUtils.equals(string, "default_display") || TextUtils.isEmpty(string);
        this.mDefaultDisplayPref.setChecked(z);
        this.mMirrorDisplayPref.setChecked(!z);
        setIllustrationVideo(this.mWiredCastPreference, z);
    }

    private void updateCastMode(String str) {
        Settings.Global.putString(this.mContext.getContentResolver(), "cast_mode", str);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return super.onPreferenceTreeClick(preference);
        }
        if (preference instanceof RadioPreference) {
            ((RadioPreference) preference).setChecked(true);
            if (TextUtils.equals(key, "wired_screen_case_mode_different")) {
                this.mMirrorDisplayPref.setChecked(false);
                setIllustrationVideo(this.mWiredCastPreference, true);
                updateCastMode("default_display");
            } else if (TextUtils.equals(key, "wired_screen_case_mode_same")) {
                this.mDefaultDisplayPref.setChecked(false);
                setIllustrationVideo(this.mWiredCastPreference, false);
                updateCastMode("mirror_display");
            } else if (TextUtils.equals(key, "audio_phone")) {
                this.mHdmiAudioPref.setChecked(false);
                switchAudioDevices(0);
            } else if (TextUtils.equals(key, "audio_hdmi")) {
                this.mPhoneAudioPref.setChecked(false);
                switchAudioDevices(1);
            }
        } else if (TextUtils.equals(key, "wired_screen_app_select")) {
            Log.d("fangli", "onPreferenceTreeClick: wired screen click");
        } else if (TextUtils.equals(key, "touch_activity")) {
            startTouchActivity();
        }
        return super.onPreferenceTreeClick(preference);
    }

    public void startTouchActivity() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.systemui", "com.unisoc.systemui.touchboard.TouchActivity"));
        intent.addFlags(335544320);
        SettingsApplication.mApplication.startActivity(intent);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("multidisplay_info"), false, this.mDeviceListObserver);
        updateDeviceList();
        this.mHandler.sendEmptyMessageDelayed(0, 100L);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        getContentResolver().unregisterContentObserver(this.mDeviceListObserver);
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mContext.unregisterReceiver(this.mAudioReceiver);
        super.onDetach();
    }

    private void setIllustrationVideo(WiredCastPreference wiredCastPreference, boolean z) {
        wiredCastPreference.setVideo(0);
        if (z) {
            wiredCastPreference.setVideo(R$raw.multi_screen_different);
        } else {
            wiredCastPreference.setVideo(R$raw.multi_screen_same);
        }
    }

    public boolean getUSBConnectForProjection() {
        boolean z;
        Iterator<UsbDevice> it = ((UsbManager) SettingsApplication.mApplication.getSystemService(UsbManager.class)).getDeviceList().values().iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            }
            UsbDevice next = it.next();
            int vendorId = next.getVendorId();
            int productId = next.getProductId();
            Log.i("fangli", "VendorId = " + vendorId);
            Log.i("fangli", "ProductId = " + productId);
            if (vendorId == 21325 && productId == 24609) {
                z = true;
                break;
            }
        }
        if (TextUtils.isEmpty(Settings.Global.getString(this.mContext.getContentResolver(), "multidisplay_info"))) {
            return z;
        }
        return true;
    }

    /* loaded from: classes.dex */
    private final class AudioReceiver extends BroadcastReceiver {
        private AudioReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.action.AUDIO_OUTPUT_SWITCH")) {
                WiredScreenCastSettings.this.updatePreViewAudio();
            }
        }
    }
}
