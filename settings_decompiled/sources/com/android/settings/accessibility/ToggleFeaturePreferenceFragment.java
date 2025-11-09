package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.accessibility.ShortcutPreference;
import com.android.settings.utils.LocaleUtils;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.android.settingslib.widget.TopIntroPreference;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public abstract class ToggleFeaturePreferenceFragment extends SettingsPreferenceFragment implements ShortcutPreference.OnClickCallback, OnMainSwitchChangeListener {
    protected ComponentName mComponentName;
    private CharSequence mDescription;
    protected Dialog mDialog;
    protected AccessibilityFooterPreferenceController mFooterPreferenceController;
    private CheckBox mHardwareTypeCheckBox;
    protected CharSequence mHtmlDescription;
    private ImageView mImageGetterCacheView;
    protected Uri mImageUri;
    protected CharSequence mPackageName;
    protected String mPreferenceKey;
    private boolean mSavedAccessibilityFloatingMenuEnabled;
    private AccessibilitySettingsContentObserver mSettingsContentObserver;
    protected Intent mSettingsIntent;
    protected Preference mSettingsPreference;
    protected CharSequence mSettingsTitle;
    protected ShortcutPreference mShortcutPreference;
    private CheckBox mSoftwareTypeCheckBox;
    protected SettingsMainSwitchPreference mToggleServiceSwitchPreference;
    private AccessibilityQuickSettingsTooltipWindow mTooltipWindow;
    protected TopIntroPreference mTopIntroPreference;
    protected CharSequence mTopIntroTitle;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    private boolean mNeedsQSTooltipReshow = false;
    private int mNeedsQSTooltipType = 0;
    protected int mSavedCheckBoxValue = -1;
    private final Html.ImageGetter mImageGetter = new Html.ImageGetter() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda3
        @Override // android.text.Html.ImageGetter
        public final Drawable getDrawable(String str) {
            Drawable lambda$new$0;
            lambda$new$0 = ToggleFeaturePreferenceFragment.this.lambda$new$0(str);
            return lambda$new$0;
        }
    };

    private boolean hasShortcutType(int i, int i2) {
        return (i & i2) == i2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 1) {
            return i != 1008 ? 0 : 1810;
        }
        return 1812;
    }

    public int getHelpResource() {
        return 0;
    }

    public int getMetricsCategory() {
        return 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getShortcutPreferenceKey() {
        return "shortcut_preference";
    }

    abstract ComponentName getTileComponentName();

    abstract CharSequence getTileTooltipContent(int i);

    abstract int getUserShortcutTypes();

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRemoveSwitchPreferenceToggleSwitch() {
    }

    protected void updateSwitchBarToggleSwitch() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Drawable lambda$new$0(String str) {
        if (str == null || !str.startsWith("R.drawable.")) {
            return null;
        }
        String substring = str.substring(11);
        return getDrawableFromUri(Uri.parse("android.resource://" + this.mComponentName.getPackageName() + "/drawable/" + substring));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
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
        AccessibilitySettingsContentObserver accessibilitySettingsContentObserver = new AccessibilitySettingsContentObserver(new Handler());
        this.mSettingsContentObserver = accessibilitySettingsContentObserver;
        registerKeysToObserverCallback(accessibilitySettingsContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerKeysToObserverCallback(AccessibilitySettingsContentObserver accessibilitySettingsContentObserver) {
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(getShortcutFeatureSettingsKeys(), new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda2
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                ToggleFeaturePreferenceFragment.this.lambda$registerKeysToObserverCallback$1(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerKeysToObserverCallback$1(String str) {
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<String> getShortcutFeatureSettingsKeys() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        onProcessArguments(getArguments());
        initTopIntroPreference();
        initAnimatedImagePreference();
        initToggleServiceSwitchPreference();
        initGeneralCategory();
        initShortcutPreference();
        initSettingsPreference();
        initHtmlTextPreference();
        initFooterPreference();
        installActionBarToggleSwitch();
        updateToggleServiceTitle(this.mToggleServiceSwitchPreference);
        this.mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda1
            @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
            public final void onTouchExplorationStateChanged(boolean z) {
                ToggleFeaturePreferenceFragment.this.lambda$onCreateView$2(z);
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$2(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((SettingsActivity) getActivity()).getSwitchBar().hide();
        updatePreferenceOrder();
        if (this.mNeedsQSTooltipReshow) {
            getView().post(new Runnable() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ToggleFeaturePreferenceFragment.this.showQuickSettingsTooltipIfNeeded();
                }
            });
        }
        writeDefaultShortcutTargetServiceToSettingsIfNeeded(getPrefContext());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.register(getContentResolver());
        updateShortcutPreferenceData();
        updateShortcutPreference();
        updateEditShortcutDialogIfNeeded();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.unregister(getContentResolver());
        this.mSavedAccessibilityFloatingMenuEnabled = AccessibilityUtil.isFloatingMenuEnabled(getContext());
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
            AlertDialog showEditShortcutDialog = AccessibilityDialogUtils.showEditShortcutDialog(getPrefContext(), isAnySetupWizard ? 1 : 0, getShortcutTitle(), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ToggleFeaturePreferenceFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i2);
                }
            });
            this.mDialog = showEditShortcutDialog;
            setupEditShortcutDialog(showEditShortcutDialog);
            return this.mDialog;
        } else if (i == 1008) {
            AlertDialog createAccessibilityTutorialDialog = AccessibilityGestureNavigationTutorial.createAccessibilityTutorialDialog(getPrefContext(), getUserShortcutTypes(), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ToggleFeaturePreferenceFragment.this.callOnTutorialDialogButtonClicked(dialogInterface, i2);
                }
            });
            this.mDialog = createAccessibilityTutorialDialog;
            createAccessibilityTutorialDialog.setCanceledOnTouchOutside(false);
            return this.mDialog;
        } else {
            throw new IllegalArgumentException("Unsupported dialogId " + i);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        removeActionBarToggleSwitch();
    }

    public void onSwitchChanged(Switch r1, boolean z) {
        onPreferenceToggled(this.mPreferenceKey, z);
    }

    protected void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        settingsMainSwitchPreference.setTitle(getString(R$string.accessibility_service_primary_switch_title, this.mPackageName));
    }

    protected CharSequence getShortcutTitle() {
        return getString(R$string.accessibility_shortcut_title, this.mPackageName);
    }

    protected void onPreferenceToggled(String str, boolean z) {
        if (z) {
            showQuickSettingsTooltipIfNeeded();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onInstallSwitchPreferenceToggleSwitch() {
        updateSwitchBarToggleSwitch();
        this.mToggleServiceSwitchPreference.addOnSwitchChangeListener(this);
    }

    private void installActionBarToggleSwitch() {
        onInstallSwitchPreferenceToggleSwitch();
    }

    private void removeActionBarToggleSwitch() {
        this.mToggleServiceSwitchPreference.setOnPreferenceClickListener(null);
        onRemoveSwitchPreferenceToggleSwitch();
    }

    public void setTitle(String str) {
        getActivity().setTitle(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onProcessArguments(Bundle bundle) {
        this.mPreferenceKey = bundle.getString("preference_key");
        if (bundle.containsKey("resolve_info")) {
            getActivity().setTitle(((ResolveInfo) bundle.getParcelable("resolve_info")).loadLabel(getPackageManager()).toString());
        } else if (bundle.containsKey("title")) {
            setTitle(bundle.getString("title"));
        }
        if (bundle.containsKey("summary")) {
            this.mDescription = bundle.getCharSequence("summary");
        }
        if (bundle.containsKey("html_description")) {
            this.mHtmlDescription = bundle.getCharSequence("html_description");
        }
        if (bundle.containsKey("intro")) {
            this.mTopIntroTitle = bundle.getCharSequence("intro");
        }
    }

    protected List<String> getPreferenceOrderList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("top_intro");
        arrayList.add("animated_image");
        arrayList.add("use_service");
        arrayList.add("general_categories");
        arrayList.add("html_description");
        return arrayList;
    }

    private void updatePreferenceOrder() {
        List<String> preferenceOrderList = getPreferenceOrderList();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.setOrderingAsAdded(false);
        int size = preferenceOrderList.size();
        for (int i = 0; i < size; i++) {
            Preference findPreference = preferenceScreen.findPreference(preferenceOrderList.get(i));
            if (findPreference != null) {
                findPreference.setOrder(i);
            }
        }
    }

    private Drawable getDrawableFromUri(Uri uri) {
        if (this.mImageGetterCacheView == null) {
            this.mImageGetterCacheView = new ImageView(getPrefContext());
        }
        this.mImageGetterCacheView.setAdjustViewBounds(true);
        this.mImageGetterCacheView.setImageURI(uri);
        if (this.mImageGetterCacheView.getDrawable() == null) {
            return null;
        }
        Drawable newDrawable = this.mImageGetterCacheView.getDrawable().mutate().getConstantState().newDrawable();
        this.mImageGetterCacheView.setImageURI(null);
        int intrinsicWidth = newDrawable.getIntrinsicWidth();
        int intrinsicHeight = newDrawable.getIntrinsicHeight();
        int screenHeightPixels = AccessibilityUtil.getScreenHeightPixels(getPrefContext()) / 2;
        if (intrinsicWidth > AccessibilityUtil.getScreenWidthPixels(getPrefContext()) || intrinsicHeight > screenHeightPixels) {
            return null;
        }
        newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
        return newDrawable;
    }

    private void initAnimatedImagePreference() {
        if (this.mImageUri == null) {
            return;
        }
        IllustrationPreference illustrationPreference = new IllustrationPreference(getPrefContext());
        illustrationPreference.setImageUri(this.mImageUri);
        illustrationPreference.setSelectable(false);
        illustrationPreference.setMaxHeight(AccessibilityUtil.getDisplayBounds(getPrefContext()).height() / 2);
        illustrationPreference.setKey("animated_image");
        getPreferenceScreen().addPreference(illustrationPreference);
    }

    void initTopIntroPreference() {
        if (TextUtils.isEmpty(this.mTopIntroTitle)) {
            return;
        }
        TopIntroPreference topIntroPreference = new TopIntroPreference(getPrefContext());
        this.mTopIntroPreference = topIntroPreference;
        topIntroPreference.setKey("top_intro");
        this.mTopIntroPreference.setTitle(this.mTopIntroTitle);
        getPreferenceScreen().addPreference(this.mTopIntroPreference);
    }

    private void initToggleServiceSwitchPreference() {
        SettingsMainSwitchPreference settingsMainSwitchPreference = new SettingsMainSwitchPreference(getPrefContext());
        this.mToggleServiceSwitchPreference = settingsMainSwitchPreference;
        settingsMainSwitchPreference.setKey("use_service");
        if (getArguments().containsKey("checked")) {
            this.mToggleServiceSwitchPreference.setChecked(getArguments().getBoolean("checked"));
        }
        getPreferenceScreen().addPreference(this.mToggleServiceSwitchPreference);
    }

    private void initGeneralCategory() {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey("general_categories");
        preferenceCategory.setTitle(R$string.accessibility_screen_option);
        getPreferenceScreen().addPreference(preferenceCategory);
    }

    protected void initShortcutPreference() {
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey(getShortcutPreferenceKey());
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle(getShortcutTitle());
        ((PreferenceCategory) findPreference("general_categories")).addPreference(this.mShortcutPreference);
    }

    protected void initSettingsPreference() {
        if (this.mSettingsTitle == null || this.mSettingsIntent == null) {
            return;
        }
        Preference preference = new Preference(getPrefContext());
        this.mSettingsPreference = preference;
        preference.setTitle(this.mSettingsTitle);
        this.mSettingsPreference.setIconSpaceReserved(false);
        this.mSettingsPreference.setIntent(this.mSettingsIntent);
        ((PreferenceCategory) findPreference("general_categories")).addPreference(this.mSettingsPreference);
    }

    private void initHtmlTextPreference() {
        if (TextUtils.isEmpty(this.mHtmlDescription)) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Spanned fromHtml = Html.fromHtml(this.mHtmlDescription.toString(), 63, this.mImageGetter, null);
        AccessibilityFooterPreference accessibilityFooterPreference = new AccessibilityFooterPreference(preferenceScreen.getContext());
        accessibilityFooterPreference.setKey("html_description");
        accessibilityFooterPreference.setSummary(fromHtml);
        preferenceScreen.addPreference(accessibilityFooterPreference);
        String string = getString(R$string.accessibility_introduction_title, this.mPackageName);
        AccessibilityFooterPreferenceController accessibilityFooterPreferenceController = new AccessibilityFooterPreferenceController(preferenceScreen.getContext(), accessibilityFooterPreference.getKey());
        this.mFooterPreferenceController = accessibilityFooterPreferenceController;
        accessibilityFooterPreferenceController.setIntroductionTitle(string);
        this.mFooterPreferenceController.displayPreference(preferenceScreen);
    }

    private void initFooterPreference() {
        if (TextUtils.isEmpty(this.mDescription)) {
            return;
        }
        createFooterPreference(getPreferenceScreen(), this.mDescription, getString(R$string.accessibility_introduction_title, this.mPackageName));
    }

    void createFooterPreference(PreferenceScreen preferenceScreen, CharSequence charSequence, String str) {
        AccessibilityFooterPreference accessibilityFooterPreference = new AccessibilityFooterPreference(preferenceScreen.getContext());
        accessibilityFooterPreference.setSummary(charSequence);
        preferenceScreen.addPreference(accessibilityFooterPreference);
        AccessibilityFooterPreferenceController accessibilityFooterPreferenceController = new AccessibilityFooterPreferenceController(preferenceScreen.getContext(), accessibilityFooterPreference.getKey());
        this.mFooterPreferenceController = accessibilityFooterPreferenceController;
        accessibilityFooterPreferenceController.setIntroductionTitle(str);
        this.mFooterPreferenceController.displayPreference(preferenceScreen);
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

    private void setDialogTextAreaClickListener(View view, final CheckBox checkBox) {
        view.findViewById(R$id.container).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                checkBox.toggle();
            }
        });
    }

    private void updateEditShortcutDialogCheckBox() {
        int restoreOnConfigChangedValue = restoreOnConfigChangedValue();
        if (restoreOnConfigChangedValue == -1) {
            restoreOnConfigChangedValue = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1);
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

    protected int getShortcutTypeCheckBoxValue() {
        CheckBox checkBox = this.mSoftwareTypeCheckBox;
        if (checkBox == null || this.mHardwareTypeCheckBox == null) {
            return -1;
        }
        boolean isChecked = checkBox.isChecked();
        return this.mHardwareTypeCheckBox.isChecked() ? (isChecked ? 1 : 0) | 2 : isChecked ? 1 : 0;
    }

    private static CharSequence getSoftwareShortcutTypeSummary(Context context) {
        int i;
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            i = R$string.accessibility_shortcut_edit_summary_software;
        } else if (AccessibilityUtil.isGestureNavigateEnabled(context)) {
            i = R$string.accessibility_shortcut_edit_summary_software_gesture;
        } else {
            i = R$string.accessibility_shortcut_edit_summary_software;
        }
        return context.getText(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CharSequence getShortcutTypeSummary(Context context) {
        if (!this.mShortcutPreference.isSettingsEditable()) {
            return context.getText(R$string.accessibility_shortcut_edit_dialog_title_hardware);
        }
        if (!this.mShortcutPreference.isChecked()) {
            return context.getText(R$string.switch_off_text);
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(context, this.mComponentName.flattenToString(), 1);
        ArrayList arrayList = new ArrayList();
        if (hasShortcutType(retrieveUserShortcutType, 1)) {
            arrayList.add(getSoftwareShortcutTypeSummary(context));
        }
        if (hasShortcutType(retrieveUserShortcutType, 2)) {
            arrayList.add(context.getText(R$string.accessibility_shortcut_hardware_keyword));
        }
        if (arrayList.isEmpty()) {
            arrayList.add(getSoftwareShortcutTypeSummary(context));
        }
        return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), null, LocaleUtils.getConcatenatedString(arrayList));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callOnTutorialDialogButtonClicked(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        showQuickSettingsTooltipIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        if (this.mComponentName == null) {
            return;
        }
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        saveNonEmptyUserShortcutType(shortcutTypeCheckBoxValue);
        AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), shortcutTypeCheckBoxValue, this.mComponentName);
        AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), ~shortcutTypeCheckBoxValue, this.mComponentName);
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

    protected void updateShortcutPreferenceData() {
        int userShortcutTypesFromSettings;
        if (this.mComponentName == null || (userShortcutTypesFromSettings = AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName)) == 0) {
            return;
        }
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(this.mComponentName.flattenToString(), userShortcutTypesFromSettings));
    }

    protected void updateShortcutPreference() {
        if (this.mComponentName == null) {
            return;
        }
        this.mShortcutPreference.setChecked(AccessibilityUtil.hasValuesInSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1), this.mComponentName));
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        if (this.mComponentName == null) {
            return;
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1);
        if (shortcutPreference.isChecked()) {
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
            showDialog(1008);
        } else {
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        showDialog(1);
    }

    private void writeDefaultShortcutTargetServiceToSettingsIfNeeded(Context context) {
        ComponentName unflattenFromString;
        if (this.mComponentName == null) {
            return;
        }
        if (this.mComponentName.equals(ComponentName.unflattenFromString(context.getString(17039915))) && TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "accessibility_shortcut_target_service"))) {
            String shortcutTargetServiceComponentNameString = AccessibilityUtils.getShortcutTargetServiceComponentNameString(context, UserHandle.myUserId());
            if (TextUtils.isEmpty(shortcutTargetServiceComponentNameString) || (unflattenFromString = ComponentName.unflattenFromString(shortcutTargetServiceComponentNameString)) == null) {
                return;
            }
            Settings.Secure.putString(context.getContentResolver(), "accessibility_shortcut_target_service", unflattenFromString.flattenToString());
        }
    }

    private void updateEditShortcutDialogIfNeeded() {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            return;
        }
        if (this.mSavedAccessibilityFloatingMenuEnabled != AccessibilityUtil.isFloatingMenuEnabled(getContext())) {
            AccessibilityDialogUtils.updateSoftwareShortcutInDialog(getContext(), this.mDialog);
        }
    }

    void saveNonEmptyUserShortcutType(int i) {
        if (i == 0) {
            return;
        }
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(this.mComponentName.flattenToString(), i));
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

    /* JADX INFO: Access modifiers changed from: protected */
    public CharSequence loadTileLabel(Context context, ComponentName componentName) {
        PackageManager packageManager = context.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryIntentServices(new Intent("android.service.quicksettings.action.QS_TILE"), 128)) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (TextUtils.equals(componentName.getPackageName(), serviceInfo.packageName) && TextUtils.equals(componentName.getClassName(), serviceInfo.name)) {
                return serviceInfo.loadLabel(packageManager);
            }
        }
        return null;
    }
}
