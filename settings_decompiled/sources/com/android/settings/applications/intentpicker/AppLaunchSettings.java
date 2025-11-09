package com.android.settings.applications.intentpicker;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.ClearDefaultsPreference;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.List;
import java.util.Set;
import java.util.UUID;
/* loaded from: classes.dex */
public class AppLaunchSettings extends AppInfoBase implements Preference.OnPreferenceChangeListener, OnMainSwitchChangeListener {
    private boolean mActivityCreated;
    private Preference mAddLinkPreference;
    private ClearDefaultsPreference mClearDefaultsPreference;
    Context mContext;
    DomainVerificationManager mDomainVerificationManager;
    private PreferenceCategory mMainPreferenceCategory;
    private MainSwitchPreference mMainSwitchPreference;
    private PreferenceCategory mOtherDefaultsPreferenceCategory;
    private PreferenceCategory mSelectedLinksPreferenceCategory;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 17;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivityCreated = false;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mAppEntry == null) {
            Log.w("AppLaunchSettings", "onCreate: mAppEntry is null, please check the reason!!!");
            getActivity().finish();
            return;
        }
        addPreferencesFromResource(R$xml.installed_app_launch_settings);
        this.mDomainVerificationManager = (DomainVerificationManager) this.mContext.getSystemService(DomainVerificationManager.class);
        initUIComponents();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        createHeaderPreference();
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        if (i == 1) {
            return createVerifiedLinksDialog();
        }
        return null;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        this.mClearDefaultsPreference.setPackageName(this.mPackageName);
        this.mClearDefaultsPreference.setAppEntry(this.mAppEntry);
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        IntentPickerUtils.logd("onPreferenceChange: " + ((Object) preference.getTitle()) + " isChecked: " + booleanValue);
        if (!(preference instanceof LeftSideCheckBoxPreference) || booleanValue) {
            return true;
        }
        ArraySet arraySet = new ArraySet();
        arraySet.add(preference.getTitle().toString());
        removePreference(preference.getKey());
        DomainVerificationUserState domainVerificationUserState = IntentPickerUtils.getDomainVerificationUserState(this.mDomainVerificationManager, this.mPackageName);
        if (domainVerificationUserState == null) {
            return false;
        }
        setDomainVerificationUserSelection(domainVerificationUserState.getIdentifier(), arraySet, false);
        this.mAddLinkPreference.setEnabled(isAddLinksNotEmpty());
        return true;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        IntentPickerUtils.logd("onSwitchChanged: isChecked=" + z);
        MainSwitchPreference mainSwitchPreference = this.mMainSwitchPreference;
        if (mainSwitchPreference != null) {
            mainSwitchPreference.setChecked(z);
        }
        PreferenceCategory preferenceCategory = this.mMainPreferenceCategory;
        if (preferenceCategory != null) {
            preferenceCategory.setVisible(z);
        }
        DomainVerificationManager domainVerificationManager = this.mDomainVerificationManager;
        if (domainVerificationManager != null) {
            try {
                domainVerificationManager.setDomainVerificationLinkHandlingAllowed(this.mPackageName, z);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("AppLaunchSettings", "onSwitchChanged: " + e.getMessage());
            }
        }
    }

    private void createHeaderPreference() {
        if (this.mActivityCreated) {
            Log.w("AppLaunchSettings", "onParentActivityCreated: ignoring duplicate call.");
            return;
        }
        this.mActivityCreated = true;
        if (this.mPackageInfo == null) {
            Log.w("AppLaunchSettings", "onParentActivityCreated: PakcageInfo is null.");
            return;
        }
        FragmentActivity activity = getActivity();
        getPreferenceScreen().addPreference(EntityHeaderController.newInstance(activity, this, null).setRecyclerView(getListView(), getSettingsLifecycle()).setIcon(Utils.getBadgedIcon(this.mContext, this.mPackageInfo.applicationInfo)).setLabel(this.mPackageInfo.applicationInfo.loadLabel(this.mPm)).setSummary(activity.getString(R$string.app_launch_top_intro_message)).setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo)).setPackageName(this.mPackageName).setUid(this.mPackageInfo.applicationInfo.uid).setHasAppInfoLink(true).setButtonActions(0, 0).done(activity, getPrefContext()));
    }

    private void initUIComponents() {
        initMainSwitchAndCategories();
        if (canUpdateMainSwitchAndCategories()) {
            initVerifiedLinksPreference();
            initAddLinkPreference();
            addSelectedLinksPreference();
            initFooter();
        }
    }

    private void initMainSwitchAndCategories() {
        this.mMainSwitchPreference = (MainSwitchPreference) findPreference("open_by_default_supported_links");
        this.mMainPreferenceCategory = (PreferenceCategory) findPreference("open_by_default_main_category");
        this.mSelectedLinksPreferenceCategory = (PreferenceCategory) findPreference("open_by_default_selected_links_category");
        initOtherDefaultsSection();
    }

    private boolean canUpdateMainSwitchAndCategories() {
        DomainVerificationUserState domainVerificationUserState = IntentPickerUtils.getDomainVerificationUserState(this.mDomainVerificationManager, this.mPackageName);
        if (domainVerificationUserState == null) {
            disabledPreference();
            return false;
        }
        IntentPickerUtils.logd("isLinkHandlingAllowed() : " + domainVerificationUserState.isLinkHandlingAllowed());
        this.mMainSwitchPreference.updateStatus(domainVerificationUserState.isLinkHandlingAllowed());
        this.mMainSwitchPreference.addOnSwitchChangeListener(this);
        this.mMainPreferenceCategory.setVisible(domainVerificationUserState.isLinkHandlingAllowed());
        return true;
    }

    private void initVerifiedLinksPreference() {
        VerifiedLinksPreference verifiedLinksPreference = (VerifiedLinksPreference) this.mMainPreferenceCategory.findPreference("open_by_default_verified_links");
        verifiedLinksPreference.setWidgetFrameClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.intentpicker.AppLaunchSettings$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppLaunchSettings.this.lambda$initVerifiedLinksPreference$0(view);
            }
        });
        int linksNumber = getLinksNumber(2);
        verifiedLinksPreference.setTitle(getVerifiedLinksTitle(linksNumber));
        verifiedLinksPreference.setCheckBoxVisible(linksNumber > 0);
        verifiedLinksPreference.setEnabled(linksNumber > 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initVerifiedLinksPreference$0(View view) {
        showVerifiedLinksDialog();
    }

    private void showVerifiedLinksDialog() {
        if (getLinksNumber(2) == 0) {
            return;
        }
        showDialogInner(1, 0);
    }

    private AlertDialog createVerifiedLinksDialog() {
        int linksNumber = getLinksNumber(2);
        View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.app_launch_verified_links_title, (ViewGroup) null);
        ((TextView) inflate.findViewById(R$id.dialog_title)).setText(getVerifiedLinksTitle(linksNumber));
        ((TextView) inflate.findViewById(R$id.dialog_message)).setText(getVerifiedLinksMessage(linksNumber));
        return new AlertDialog.Builder(this.mContext).setCustomTitle(inflate).setCancelable(true).setItems((CharSequence[]) IntentPickerUtils.getLinksList(this.mDomainVerificationManager, this.mPackageName, 2).toArray(new String[0]), null).setPositiveButton(R$string.app_launch_dialog_ok, (DialogInterface.OnClickListener) null).create();
    }

    String getVerifiedLinksTitle(int i) {
        return getResources().getQuantityString(R$plurals.app_launch_verified_links_title, i, Integer.valueOf(i));
    }

    private String getVerifiedLinksMessage(int i) {
        return getResources().getQuantityString(R$plurals.app_launch_verified_links_message, i, Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addSelectedLinksPreference() {
        if (getLinksNumber(1) == 0) {
            return;
        }
        this.mSelectedLinksPreferenceCategory.removeAll();
        for (String str : IntentPickerUtils.getLinksList(this.mDomainVerificationManager, this.mPackageName, 1)) {
            generateCheckBoxPreference(this.mSelectedLinksPreferenceCategory, str);
        }
        this.mAddLinkPreference.setEnabled(isAddLinksNotEmpty());
    }

    private void initAddLinkPreference() {
        Preference findPreference = findPreference("open_by_default_add_link");
        this.mAddLinkPreference = findPreference;
        findPreference.setVisible(isAddLinksShown());
        this.mAddLinkPreference.setEnabled(isAddLinksNotEmpty());
        this.mAddLinkPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.intentpicker.AppLaunchSettings$$ExternalSyntheticLambda2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$initAddLinkPreference$1;
                lambda$initAddLinkPreference$1 = AppLaunchSettings.this.lambda$initAddLinkPreference$1(preference);
                return lambda$initAddLinkPreference$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initAddLinkPreference$1(Preference preference) {
        int linksNumber = getLinksNumber(0);
        IntentPickerUtils.logd("The number of the state none links: " + linksNumber);
        if (linksNumber > 0) {
            showProgressDialogFragment();
            return true;
        }
        return true;
    }

    private boolean isAddLinksNotEmpty() {
        return getLinksNumber(0) > 0;
    }

    private boolean isAddLinksShown() {
        return isAddLinksNotEmpty() || getLinksNumber(1) > 0;
    }

    private void showProgressDialogFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("app_package", this.mPackageName);
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setArguments(bundle);
        progressDialogFragment.showDialog(getActivity().getSupportFragmentManager());
    }

    private void disabledPreference() {
        this.mMainSwitchPreference.updateStatus(false);
        this.mMainSwitchPreference.setSelectable(false);
        this.mMainSwitchPreference.setEnabled(false);
        this.mMainPreferenceCategory.setVisible(false);
    }

    private void initOtherDefaultsSection() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("app_launch_other_defaults");
        this.mOtherDefaultsPreferenceCategory = preferenceCategory;
        preferenceCategory.setVisible(isClearDefaultsEnabled());
        this.mClearDefaultsPreference = (ClearDefaultsPreference) findPreference("app_launch_clear_defaults");
    }

    private void initFooter() {
        CharSequence text = this.mContext.getText(R$string.app_launch_footer);
        FooterPreference footerPreference = (FooterPreference) findPreference("open_by_default_footer");
        footerPreference.setTitle(text);
        footerPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.applications.intentpicker.AppLaunchSettings$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppLaunchSettings.this.lambda$initFooter$2(view);
            }
        });
        footerPreference.setLearnMoreText(this.mContext.getString(R$string.footer_learn_more_content_description, getLabelName()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initFooter$2(View view) {
        this.mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://developer.android.com/training/app-links/verify-site-associations")));
    }

    private String getLabelName() {
        return this.mContext.getString(R$string.launch_by_default);
    }

    private boolean isClearDefaultsEnabled() {
        boolean hasBindAppWidgetPermission = AppWidgetManager.getInstance(this.mContext).hasBindAppWidgetPermission(this.mAppEntry.info.packageName);
        boolean z = AppUtils.hasPreferredActivities(this.mPm, this.mPackageName) || AppUtils.isDefaultBrowser(this.mContext, this.mPackageName) || AppUtils.hasUsbDefaults(this.mUsbManager, this.mPackageName);
        IntentPickerUtils.logd("isClearDefaultsEnabled hasBindAppWidgetPermission : " + hasBindAppWidgetPermission);
        IntentPickerUtils.logd("isClearDefaultsEnabled isAutoLaunchEnabled : " + z);
        return z || hasBindAppWidgetPermission;
    }

    private void setDomainVerificationUserSelection(UUID uuid, Set<String> set, boolean z) {
        try {
            this.mDomainVerificationManager.setDomainVerificationUserSelection(uuid, set, z);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("AppLaunchSettings", "addSelectedItems : " + e.getMessage());
        }
    }

    private void generateCheckBoxPreference(PreferenceCategory preferenceCategory, String str) {
        LeftSideCheckBoxPreference leftSideCheckBoxPreference = new LeftSideCheckBoxPreference(preferenceCategory.getContext(), true);
        leftSideCheckBoxPreference.setTitle(str);
        leftSideCheckBoxPreference.setOnPreferenceChangeListener(this);
        leftSideCheckBoxPreference.setKey(UUID.randomUUID().toString());
        preferenceCategory.addPreference(leftSideCheckBoxPreference);
    }

    private int getLinksNumber(int i) {
        List<String> linksList = IntentPickerUtils.getLinksList(this.mDomainVerificationManager, this.mPackageName, i);
        if (linksList == null) {
            return 0;
        }
        return linksList.size();
    }
}
