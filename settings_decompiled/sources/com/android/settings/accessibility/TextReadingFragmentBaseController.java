package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class TextReadingFragmentBaseController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String MULTIDISPLAY_INFO = "multidisplay_info";
    private final Uri MULTIDISPLAY_INFO_URI;
    private int mEntryPoint;
    private Preference mPreference;
    private final ContentObserver mSettingObserver;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    private TextReadingFragmentBaseController(Context context, String str) {
        super(context, str);
        this.MULTIDISPLAY_INFO_URI = Settings.Global.getUriFor(MULTIDISPLAY_INFO);
        this.mSettingObserver = new ContentObserver(new Handler(true)) { // from class: com.android.settings.accessibility.TextReadingFragmentBaseController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                TextReadingFragmentBaseController.this.setPreferenceEnabled();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextReadingFragmentBaseController(Context context, String str, int i) {
        this(context, str);
        this.mEntryPoint = i;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            preference.getExtras().putInt("launched_from", this.mEntryPoint);
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(this.MULTIDISPLAY_INFO_URI, false, this.mSettingObserver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = preference;
        setPreferenceEnabled();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingObserver);
    }

    public void setPreferenceEnabled() {
        if (this.mPreference != null) {
            this.mPreference.setEnabled(TextUtils.isEmpty(Settings.Global.getString(this.mContext.getContentResolver(), MULTIDISPLAY_INFO)));
        }
    }
}
