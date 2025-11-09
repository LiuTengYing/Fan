package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import com.google.common.primitives.Ints;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class AccessibilityTimeoutController extends AbstractPreferenceController implements LifecycleObserver, SelectorWithWidgetPreference.OnClickListener, PreferenceControllerMixin {
    private final Map<String, Integer> mAccessibilityTimeoutKeyToValueMap;
    private int mAccessibilityUiTimeoutValue;
    private final ContentResolver mContentResolver;
    private OnChangeListener mOnChangeListener;
    private SelectorWithWidgetPreference mPreference;
    private final String mPreferenceKey;
    private final Resources mResources;

    /* loaded from: classes.dex */
    public interface OnChangeListener {
        void onCheckedChanged(Preference preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AccessibilityTimeoutController(Context context, Lifecycle lifecycle, String str) {
        super(context);
        this.mAccessibilityTimeoutKeyToValueMap = new HashMap();
        this.mContentResolver = context.getContentResolver();
        this.mResources = context.getResources();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mPreferenceKey = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getSecureAccessibilityTimeoutValue(ContentResolver contentResolver, String str) {
        Integer tryParse;
        String string = Settings.Secure.getString(contentResolver, str);
        if (string == null || (tryParse = Ints.tryParse(string)) == null) {
            return 0;
        }
        return tryParse.intValue();
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    private Map<String, Integer> getTimeoutValueToKeyMap() {
        if (this.mAccessibilityTimeoutKeyToValueMap.size() == 0) {
            String[] stringArray = this.mResources.getStringArray(R$array.accessibility_timeout_control_selector_keys);
            int[] intArray = this.mResources.getIntArray(R$array.accessibility_timeout_selector_values);
            int length = intArray.length;
            for (int i = 0; i < length; i++) {
                this.mAccessibilityTimeoutKeyToValueMap.put(stringArray[i], Integer.valueOf(intArray[i]));
            }
        }
        return this.mAccessibilityTimeoutKeyToValueMap;
    }

    private void putSecureString(String str, String str2) {
        Settings.Secure.putString(this.mContentResolver, str, str2);
    }

    private void handlePreferenceChange(String str) {
        putSecureString("accessibility_non_interactive_ui_timeout_ms", str);
        putSecureString("accessibility_interactive_ui_timeout_ms", str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SelectorWithWidgetPreference selectorWithWidgetPreference = (SelectorWithWidgetPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = selectorWithWidgetPreference;
        selectorWithWidgetPreference.setOnClickListener(this);
    }

    @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
    public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        handlePreferenceChange(String.valueOf(getTimeoutValueToKeyMap().get(this.mPreferenceKey).intValue()));
        OnChangeListener onChangeListener = this.mOnChangeListener;
        if (onChangeListener != null) {
            onChangeListener.onCheckedChanged(this.mPreference);
        }
    }

    private int getAccessibilityTimeoutValue() {
        return getSecureAccessibilityTimeoutValue(this.mContentResolver, "accessibility_interactive_ui_timeout_ms");
    }

    protected void updatePreferenceCheckedState(int i) {
        if (this.mAccessibilityUiTimeoutValue == i) {
            this.mPreference.setChecked(true);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mAccessibilityUiTimeoutValue = getAccessibilityTimeoutValue();
        this.mPreference.setChecked(false);
        updatePreferenceCheckedState(getTimeoutValueToKeyMap().get(this.mPreference.getKey()).intValue());
    }
}
