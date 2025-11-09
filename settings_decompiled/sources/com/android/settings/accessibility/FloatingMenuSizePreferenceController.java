package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.google.common.primitives.Ints;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class FloatingMenuSizePreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private int mDefaultSize;
    ListPreference mPreference;
    private final ArrayMap<String, String> mValueTitleMap;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface Size {
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

    public FloatingMenuSizePreferenceController(Context context, String str) {
        super(context, str);
        this.mValueTitleMap = new ArrayMap<>();
        this.mContentResolver = context.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.accessibility.FloatingMenuSizePreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                FloatingMenuSizePreferenceController.this.updateAvailabilityStatus();
            }
        };
        initValueTitleMap();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return AccessibilityUtil.isFloatingMenuEnabled(this.mContext) ? 0 : 5;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ListPreference listPreference = (ListPreference) preference;
        Integer tryParse = Ints.tryParse((String) obj);
        if (tryParse != null) {
            putAccessibilityFloatingMenuSize(tryParse.intValue());
            updateState(listPreference);
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ((ListPreference) preference).setValue(String.valueOf(getAccessibilityFloatingMenuSize(this.mDefaultSize)));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_button_mode"), false, this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAvailabilityStatus() {
        this.mPreference.setEnabled(AccessibilityUtil.isFloatingMenuEnabled(this.mContext));
    }

    private void initValueTitleMap() {
        if (this.mValueTitleMap.size() == 0) {
            String[] stringArray = this.mContext.getResources().getStringArray(R$array.accessibility_button_size_selector_values);
            String[] stringArray2 = this.mContext.getResources().getStringArray(R$array.accessibility_button_size_selector_titles);
            int length = stringArray.length;
            this.mDefaultSize = Integer.parseInt(stringArray[0]);
            for (int i = 0; i < length; i++) {
                this.mValueTitleMap.put(stringArray[i], stringArray2[i]);
            }
        }
    }

    private int getAccessibilityFloatingMenuSize(int i) {
        return Settings.Secure.getInt(this.mContentResolver, "accessibility_floating_menu_size", i);
    }

    private void putAccessibilityFloatingMenuSize(int i) {
        Settings.Secure.putInt(this.mContentResolver, "accessibility_floating_menu_size", i);
    }
}
