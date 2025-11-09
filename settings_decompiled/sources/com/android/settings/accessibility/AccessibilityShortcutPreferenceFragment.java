package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.accessibility.ShortcutPreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.utils.LocaleUtils;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.Locale;
/* loaded from: classes.dex */
public abstract class AccessibilityShortcutPreferenceFragment extends DashboardFragment implements ShortcutPreference.OnClickCallback {
    private CheckBox mHardwareTypeCheckBox;
    private AccessibilitySettingsContentObserver mSettingsContentObserver;
    protected ShortcutPreference mShortcutPreference;
    private CheckBox mSoftwareTypeCheckBox;
    private AccessibilityQuickSettingsTooltipWindow mTooltipWindow;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    protected int mSavedCheckBoxValue = -1;
    private boolean mNeedsQSTooltipReshow = false;
    private int mNeedsQSTooltipType = 0;

    private boolean hasShortcutType(int i, int i2) {
        return (i & i2) == i2;
    }

    protected abstract ComponentName getComponentName();

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 1) {
            return i != 1008 ? 0 : 1810;
        }
        return 1812;
    }

    protected abstract CharSequence getLabelName();

    protected String getShortcutPreferenceKey() {
        return "shortcut_preference";
    }

    protected abstract ComponentName getTileComponentName();

    protected abstract CharSequence getTileTooltipContent(int i);

    protected boolean showGeneralCategory() {
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            if (bundle.containsKey("shortcut_type")) {
                this.mSavedCheckBoxValue = bundle.getInt("shortcut_type", -1);
            }
            if (bundle.containsKey("qs_tooltip_reshow")) {
                this.mNeedsQSTooltipReshow = bundle.getBoolean("qs_tooltip_reshow");
            }
            if (bundle.containsKey("qs_tooltip_type")) {
                this.mNeedsQSTooltipType = bundle.getInt("qs_tooltip_type");
            }
        }
        if (getPreferenceScreenResId() <= 0) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getPrefContext()));
        }
        if (showGeneralCategory()) {
            initGeneralCategory();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        AccessibilitySettingsContentObserver accessibilitySettingsContentObserver = new AccessibilitySettingsContentObserver(new Handler());
        this.mSettingsContentObserver = accessibilitySettingsContentObserver;
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(arrayList, new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                AccessibilityShortcutPreferenceFragment.this.lambda$onCreate$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(String str) {
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey(getShortcutPreferenceKey());
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle(getShortcutTitle());
        getPreferenceScreen().addPreference(this.mShortcutPreference);
        this.mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda3
            @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
            public final void onTouchExplorationStateChanged(boolean z) {
                AccessibilityShortcutPreferenceFragment.this.lambda$onCreateView$1(z);
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.mNeedsQSTooltipReshow) {
            getView().post(new Runnable() { // from class: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityShortcutPreferenceFragment.this.showQuickSettingsTooltipIfNeeded();
                }
            });
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.register(getContentResolver());
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.unregister(getContentResolver());
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        if (shortcutTypeCheckBoxValue != -1) {
            bundle.putInt("shortcut_type", shortcutTypeCheckBoxValue);
        }
        AccessibilityQuickSettingsTooltipWindow accessibilityQuickSettingsTooltipWindow = this.mTooltipWindow;
        if (accessibilityQuickSettingsTooltipWindow != null) {
            bundle.putBoolean("qs_tooltip_reshow", accessibilityQuickSettingsTooltipWindow.isShowing());
            bundle.putInt("qs_tooltip_type", this.mNeedsQSTooltipType);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
            AlertDialog showEditShortcutDialog = AccessibilityDialogUtils.showEditShortcutDialog(getPrefContext(), isAnySetupWizard ? 1 : 0, getShortcutTitle(), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    AccessibilityShortcutPreferenceFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i2);
                }
            });
            setupEditShortcutDialog(showEditShortcutDialog);
            return showEditShortcutDialog;
        } else if (i == 1008) {
            AlertDialog createAccessibilityTutorialDialog = AccessibilityGestureNavigationTutorial.createAccessibilityTutorialDialog(getPrefContext(), getUserShortcutTypes(), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    AccessibilityShortcutPreferenceFragment.this.callOnTutorialDialogButtonClicked(dialogInterface, i2);
                }
            });
            createAccessibilityTutorialDialog.setCanceledOnTouchOutside(false);
            return createAccessibilityTutorialDialog;
        } else {
            throw new IllegalArgumentException("Unsupported dialogId " + i);
        }
    }

    protected CharSequence getShortcutTitle() {
        return getString(R$string.accessibility_shortcut_title, getLabelName());
    }

    @Override // com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        showDialog(1);
    }

    @Override // com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        if (getComponentName() == null) {
            return;
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), getComponentName().flattenToString(), 1);
        if (shortcutPreference.isChecked()) {
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), retrieveUserShortcutType, getComponentName());
            showDialog(1008);
        } else {
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), retrieveUserShortcutType, getComponentName());
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    void setupEditShortcutDialog(Dialog dialog) {
        View findViewById = dialog.findViewById(R$id.software_shortcut);
        int i = R$id.checkbox;
        CheckBox checkBox = (CheckBox) findViewById.findViewById(i);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = dialog.findViewById(R$id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(i);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        updateEditShortcutDialogCheckBox();
    }

    protected int getShortcutTypeCheckBoxValue() {
        CheckBox checkBox = this.mSoftwareTypeCheckBox;
        if (checkBox == null || this.mHardwareTypeCheckBox == null) {
            return -1;
        }
        boolean isChecked = checkBox.isChecked();
        return this.mHardwareTypeCheckBox.isChecked() ? (isChecked ? 1 : 0) | 2 : isChecked ? 1 : 0;
    }

    protected int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), getComponentName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callOnTutorialDialogButtonClicked(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        showQuickSettingsTooltipIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        if (getComponentName() == null) {
            return;
        }
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        saveNonEmptyUserShortcutType(shortcutTypeCheckBoxValue);
        AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), shortcutTypeCheckBoxValue, getComponentName());
        AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), ~shortcutTypeCheckBoxValue, getComponentName());
        boolean z = shortcutTypeCheckBoxValue != 0;
        this.mShortcutPreference.setChecked(z);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        if (this.mHardwareTypeCheckBox.isChecked()) {
            AccessibilityUtil.skipVolumeShortcutDialogTimeoutRestriction(getPrefContext());
        }
        if (z) {
            showQuickSettingsTooltipIfNeeded();
        }
    }

    void initGeneralCategory() {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey("general_categories");
        preferenceCategory.setTitle(getGeneralCategoryDescription(null));
        getPreferenceScreen().addPreference(preferenceCategory);
    }

    void saveNonEmptyUserShortcutType(int i) {
        if (i == 0) {
            return;
        }
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(getComponentName().flattenToString(), i));
    }

    protected CharSequence getGeneralCategoryDescription(CharSequence charSequence) {
        return (charSequence == null || charSequence.toString().isEmpty()) ? getContext().getString(R$string.accessibility_screen_option) : charSequence;
    }

    private void setDialogTextAreaClickListener(View view, final CheckBox checkBox) {
        view.findViewById(R$id.container).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                checkBox.toggle();
            }
        });
    }

    protected CharSequence getShortcutTypeSummary(Context context) {
        if (!this.mShortcutPreference.isSettingsEditable()) {
            return context.getText(R$string.accessibility_shortcut_edit_dialog_title_hardware);
        }
        if (!this.mShortcutPreference.isChecked()) {
            return context.getText(R$string.switch_off_text);
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(context, getComponentName().flattenToString(), 1);
        ArrayList arrayList = new ArrayList();
        CharSequence text = context.getText(R$string.accessibility_shortcut_edit_summary_software);
        if (hasShortcutType(retrieveUserShortcutType, 1)) {
            arrayList.add(text);
        }
        if (hasShortcutType(retrieveUserShortcutType, 2)) {
            arrayList.add(context.getText(R$string.accessibility_shortcut_hardware_keyword));
        }
        if (arrayList.isEmpty()) {
            arrayList.add(text);
        }
        return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), null, LocaleUtils.getConcatenatedString(arrayList));
    }

    private void updateEditShortcutDialogCheckBox() {
        int restoreOnConfigChangedValue = restoreOnConfigChangedValue();
        if (restoreOnConfigChangedValue == -1) {
            restoreOnConfigChangedValue = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), getComponentName().flattenToString(), 1);
            if (!this.mShortcutPreference.isChecked()) {
                restoreOnConfigChangedValue = 0;
            }
        }
        this.mSoftwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 1));
        this.mHardwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 2));
    }

    private int restoreOnConfigChangedValue() {
        int i = this.mSavedCheckBoxValue;
        this.mSavedCheckBoxValue = -1;
        return i;
    }

    protected void updateShortcutPreferenceData() {
        int userShortcutTypesFromSettings;
        if (getComponentName() == null || (userShortcutTypesFromSettings = AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), getComponentName())) == 0) {
            return;
        }
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(getComponentName().flattenToString(), userShortcutTypesFromSettings));
    }

    protected void updateShortcutPreference() {
        if (getComponentName() == null) {
            return;
        }
        this.mShortcutPreference.setChecked(AccessibilityUtil.hasValuesInSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), getComponentName().flattenToString(), 1), getComponentName()));
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showQuickSettingsTooltipIfNeeded(int i) {
        this.mNeedsQSTooltipType = i;
        showQuickSettingsTooltipIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showQuickSettingsTooltipIfNeeded() {
        int i;
        ComponentName tileComponentName = getTileComponentName();
        if (tileComponentName == null) {
            return;
        }
        if (this.mNeedsQSTooltipReshow || !AccessibilityQuickSettingUtils.hasValueInSharedPreferences(getContext(), tileComponentName)) {
            CharSequence tileTooltipContent = getTileTooltipContent(this.mNeedsQSTooltipType);
            if (TextUtils.isEmpty(tileTooltipContent)) {
                return;
            }
            if (this.mNeedsQSTooltipType == 0) {
                i = R$drawable.accessibility_qs_tooltip_illustration;
            } else {
                i = R$drawable.accessibility_auto_added_qs_tooltip_illustration;
            }
            AccessibilityQuickSettingsTooltipWindow accessibilityQuickSettingsTooltipWindow = new AccessibilityQuickSettingsTooltipWindow(getContext());
            this.mTooltipWindow = accessibilityQuickSettingsTooltipWindow;
            accessibilityQuickSettingsTooltipWindow.setup(tileTooltipContent, i);
            this.mTooltipWindow.showAtTopCenter(getView());
            AccessibilityQuickSettingUtils.optInValueToSharedPreferences(getContext(), tileComponentName);
            this.mNeedsQSTooltipReshow = false;
        }
    }
}
