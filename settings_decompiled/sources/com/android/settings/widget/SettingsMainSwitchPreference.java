package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$styleable;
import androidx.preference.TwoStatePreference;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.core.instrumentation.SettingsJankMonitor;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.google.android.setupdesign.util.LayoutStyler;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SettingsMainSwitchPreference extends TwoStatePreference implements OnMainSwitchChangeListener {
    private boolean mApplyPartnerCustomizationPaddingStyle;
    private final List<SettingsMainSwitchBar.OnBeforeCheckedChangeListener> mBeforeCheckedChangeListeners;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private SettingsMainSwitchBar mMainSwitchBar;
    private RestrictedPreferenceHelper mRestrictedHelper;
    private final List<OnMainSwitchChangeListener> mSwitchChangeListeners;
    private CharSequence mTitle;

    public SettingsMainSwitchPreference(Context context) {
        super(context);
        this.mBeforeCheckedChangeListeners = new ArrayList();
        this.mSwitchChangeListeners = new ArrayList();
        init(context, null);
    }

    public SettingsMainSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBeforeCheckedChangeListeners = new ArrayList();
        this.mSwitchChangeListeners = new ArrayList();
        init(context, attributeSet);
    }

    public SettingsMainSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBeforeCheckedChangeListeners = new ArrayList();
        this.mSwitchChangeListeners = new ArrayList();
        init(context, attributeSet);
    }

    public SettingsMainSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mBeforeCheckedChangeListeners = new ArrayList();
        this.mSwitchChangeListeners = new ArrayList();
        init(context, attributeSet);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(false);
        preferenceViewHolder.setDividerAllowedBelow(false);
        RestrictedPreferenceHelper restrictedPreferenceHelper = this.mRestrictedHelper;
        if (restrictedPreferenceHelper != null) {
            this.mEnforcedAdmin = restrictedPreferenceHelper.checkRestrictionEnforced();
        }
        this.mMainSwitchBar = (SettingsMainSwitchBar) preferenceViewHolder.findViewById(R$id.main_switch_bar);
        initMainSwitchBar();
        if (isVisible()) {
            this.mMainSwitchBar.show();
            if (this.mMainSwitchBar.isChecked() != isChecked()) {
                setChecked(isChecked());
            }
            registerListenerToSwitchBar();
        } else {
            this.mMainSwitchBar.hide();
        }
        if (this.mApplyPartnerCustomizationPaddingStyle) {
            LayoutStyler.applyPartnerCustomizationLayoutPaddingStyle((ViewGroup) this.mMainSwitchBar.getParent());
        }
    }

    private void init(Context context, AttributeSet attributeSet) {
        setLayoutResource(R$layout.preference_widget_main_switch);
        this.mSwitchChangeListeners.add(this);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.Preference, 0, 0);
            CharSequence text = TypedArrayUtils.getText(obtainStyledAttributes, R$styleable.Preference_title, R$styleable.Preference_android_title);
            if (!TextUtils.isEmpty(text)) {
                setTitle(text.toString());
            }
            obtainStyledAttributes.recycle();
            this.mRestrictedHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
        }
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        super.setChecked(z);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setTitle(charSequence);
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        super.setChecked(z);
        SettingsJankMonitor.detectToggleJank(getKey(), r1);
    }

    public void show() {
        setVisible(true);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.show();
        }
    }

    public void hide() {
        setVisible(false);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.hide();
        }
    }

    public void setCheckedInternal(boolean z) {
        super.setChecked(z);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setCheckedInternal(z);
        }
    }

    public void setSwitchBarEnabled(boolean z) {
        setEnabled(z);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setEnabled(z);
        }
    }

    public void setOnBeforeCheckedChangeListener(SettingsMainSwitchBar.OnBeforeCheckedChangeListener onBeforeCheckedChangeListener) {
        if (!this.mBeforeCheckedChangeListeners.contains(onBeforeCheckedChangeListener)) {
            this.mBeforeCheckedChangeListeners.add(onBeforeCheckedChangeListener);
        }
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setOnBeforeCheckedChangeListener(onBeforeCheckedChangeListener);
        }
    }

    public void addOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        if (!this.mSwitchChangeListeners.contains(onMainSwitchChangeListener)) {
            this.mSwitchChangeListeners.add(onMainSwitchChangeListener);
        }
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.addOnSwitchChangeListener(onMainSwitchChangeListener);
        }
    }

    public void removeOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        this.mSwitchChangeListeners.remove(onMainSwitchChangeListener);
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.removeOnSwitchChangeListener(onMainSwitchChangeListener);
        }
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mEnforcedAdmin = enforcedAdmin;
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setDisabledByAdmin(enforcedAdmin);
        }
    }

    public void applyPartnerCustomizationPaddingStyle() {
        this.mApplyPartnerCustomizationPaddingStyle = true;
        notifyChanged();
    }

    private void initMainSwitchBar() {
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setTitle(this.mTitle);
            this.mMainSwitchBar.setDisabledByAdmin(this.mEnforcedAdmin);
        }
    }

    private void registerListenerToSwitchBar() {
        for (SettingsMainSwitchBar.OnBeforeCheckedChangeListener onBeforeCheckedChangeListener : this.mBeforeCheckedChangeListeners) {
            this.mMainSwitchBar.setOnBeforeCheckedChangeListener(onBeforeCheckedChangeListener);
        }
        for (OnMainSwitchChangeListener onMainSwitchChangeListener : this.mSwitchChangeListeners) {
            this.mMainSwitchBar.addOnSwitchChangeListener(onMainSwitchChangeListener);
        }
    }
}
