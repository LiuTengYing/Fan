package com.android.settings.dream;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.widget.SettingsMainSwitchPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.dream.DreamBackend;
/* loaded from: classes.dex */
public class DreamMainSwitchPreferenceController extends SettingsMainSwitchPreferenceController implements LifecycleObserver {
    static final String MAIN_SWITCH_PREF_KEY = "dream_main_settings_switch";
    private final DreamBackend mBackend;
    private final ContentObserver mObserver;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DreamMainSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.dream.DreamMainSwitchPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                DreamMainSwitchPreferenceController dreamMainSwitchPreferenceController = DreamMainSwitchPreferenceController.this;
                dreamMainSwitchPreferenceController.updateState(((SettingsMainSwitchPreferenceController) dreamMainSwitchPreferenceController).mSwitchPreference);
            }
        };
        this.mBackend = DreamBackend.getInstance(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mBackend.isEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mBackend.setEnabled(z);
        return true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("screensaver_enabled"), false, this.mObserver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }
}
