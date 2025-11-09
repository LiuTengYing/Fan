package com.android.settings.applications.specialaccess.interactacrossprofiles;

import android.app.ActionBar;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.PermissionChecker;
import android.content.pm.CrossProfileApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppStoreUtil;
import com.android.settings.widget.CardPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.unisoc.settings.utils.ToastManager;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class InteractAcrossProfilesDetails extends AppInfoBase implements Preference.OnPreferenceClickListener {
    private String mAppLabel;
    private Context mContext;
    private CrossProfileApps mCrossProfileApps;
    private LayoutPreference mHeader;
    private Intent mInstallAppIntent;
    private CardPreference mInstallBanner;
    private boolean mInstalledInPersonal;
    private boolean mInstalledInWork;
    private boolean mIsPageLaunchedByApp;
    private PackageManager mPackageManager;
    private UserHandle mPersonalProfile;
    private RestrictedSwitchPreference mSwitchPref;
    private UserManager mUserManager;
    private UserHandle mWorkProfile;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1829;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mContext = context;
        this.mCrossProfileApps = (CrossProfileApps) context.getSystemService(CrossProfileApps.class);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mPackageManager = this.mContext.getPackageManager();
        UserHandle workProfile = InteractAcrossProfilesSettings.getWorkProfile(this.mUserManager);
        this.mWorkProfile = workProfile;
        if (workProfile == null) {
            Toast makeText = Toast.makeText(this.mContext, R$string.switch_profile_failed, 0);
            ToastManager.setToast(makeText);
            makeText.show();
            setIntentAndFinish(true);
            return;
        }
        this.mPersonalProfile = this.mUserManager.getProfileParent(workProfile);
        this.mInstalledInWork = isPackageInstalled(this.mPackageName, this.mWorkProfile.getIdentifier());
        this.mInstalledInPersonal = isPackageInstalled(this.mPackageName, this.mPersonalProfile.getIdentifier());
        this.mAppLabel = this.mPackageInfo.applicationInfo.loadLabel(this.mPackageManager).toString();
        this.mInstallAppIntent = AppStoreUtil.getAppStoreLink(this.mContext, this.mPackageName);
        addPreferencesFromResource(R$xml.interact_across_profiles_permissions_details);
        replaceEnterprisePreferenceScreenTitle("Settings.CONNECTED_WORK_AND_PERSONAL_APPS_TITLE", R$string.interact_across_profiles_title);
        replaceEnterpriseStringSummary("interact_across_profiles_summary_1", "Settings.CONNECTED_APPS_SHARE_PERMISSIONS_AND_DATA", R$string.interact_across_profiles_summary_1);
        replaceEnterpriseStringSummary("interact_across_profiles_summary_2", "Settings.ONLY_CONNECT_TRUSTED_APPS", R$string.interact_across_profiles_summary_2);
        replaceEnterpriseStringSummary("interact_across_profiles_extra_summary", "Settings.HOW_TO_DISCONNECT_APPS", R$string.interact_across_profiles_summary_3);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("interact_across_profiles_settings_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceClickListener(this);
        this.mHeader = (LayoutPreference) findPreference("interact_across_profiles_header");
        CardPreference cardPreference = (CardPreference) findPreference("install_app_banner");
        this.mInstallBanner = cardPreference;
        cardPreference.setOnPreferenceClickListener(this);
        this.mIsPageLaunchedByApp = launchedByApp();
        if (!refreshUi()) {
            setIntentAndFinish(true);
        }
        addAppTitleAndIcons(this.mPersonalProfile, this.mWorkProfile);
        styleActionBar();
        maybeShowExtraSummary();
        logPageLaunchMetrics();
    }

    private void maybeShowExtraSummary() {
        Preference findPreference = findPreference("interact_across_profiles_extra_summary");
        if (findPreference == null) {
            return;
        }
        findPreference.setVisible(this.mIsPageLaunchedByApp);
    }

    private void logPageLaunchMetrics() {
        if (!this.mCrossProfileApps.canConfigureInteractAcrossProfiles(this.mPackageName)) {
            logNonConfigurableAppMetrics();
        }
        if (this.mIsPageLaunchedByApp) {
            logEvent(162);
        } else {
            logEvent(163);
        }
    }

    private void logNonConfigurableAppMetrics() {
        if (!isCrossProfilePackageAllowlisted(this.mPackageName)) {
            logEvent(164);
            return;
        }
        if (this.mInstallBanner == null) {
            logEvent(167);
        }
        if (!this.mInstalledInPersonal) {
            logEvent(166);
        } else if (this.mInstalledInWork) {
        } else {
            logEvent(165);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logEvent(int i) {
        DevicePolicyEventLogger.createEvent(i).setStrings(new String[]{this.mPackageName}).setInt(UserHandle.myUserId()).setAdmin(RestrictedLockUtils.getProfileOrDeviceOwner(this.mContext, this.mWorkProfile).component).write();
    }

    private void addAppTitleAndIcons(UserHandle userHandle, UserHandle userHandle2) {
        TextView textView = (TextView) this.mHeader.findViewById(R$id.entity_header_title);
        if (textView != null) {
            textView.setText(this.mPackageInfo.applicationInfo.loadLabel(this.mPackageManager).toString());
        }
        ImageView imageView = (ImageView) this.mHeader.findViewById(R$id.entity_header_icon_personal);
        if (imageView != null) {
            Drawable mutate = IconDrawableFactory.newInstance(this.mContext).getBadgedIcon(this.mPackageInfo.applicationInfo, userHandle.getIdentifier()).mutate();
            if (!this.mInstalledInPersonal) {
                mutate.setColorFilter(createSuspendedColorMatrix());
            }
            imageView.setImageDrawable(mutate);
        }
        ImageView imageView2 = (ImageView) this.mHeader.findViewById(R$id.entity_header_icon_work);
        if (imageView2 != null) {
            Drawable mutate2 = IconDrawableFactory.newInstance(this.mContext).getBadgedIcon(this.mPackageInfo.applicationInfo, userHandle2.getIdentifier()).mutate();
            if (!this.mInstalledInWork) {
                mutate2.setColorFilter(createSuspendedColorMatrix());
            }
            imageView2.setImageDrawable(mutate2);
        }
    }

    private void styleActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0.0f);
        }
    }

    private ColorMatrixColorFilter createSuspendedColorMatrix() {
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] array = colorMatrix.getArray();
        array[0] = 0.5f;
        array[6] = 0.5f;
        array[12] = 0.5f;
        float f = 127;
        array[4] = f;
        array[9] = f;
        array[14] = f;
        ColorMatrix colorMatrix2 = new ColorMatrix();
        colorMatrix2.setSaturation(0.0f);
        colorMatrix2.preConcat(colorMatrix);
        return new ColorMatrixColorFilter(colorMatrix2);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!refreshUi()) {
            setIntentAndFinish(true);
        }
        if (preference == this.mSwitchPref) {
            handleSwitchPreferenceClick();
            return true;
        } else if (preference == this.mInstallBanner) {
            handleInstallBannerClick();
            return true;
        } else {
            return false;
        }
    }

    private void handleSwitchPreferenceClick() {
        if (isInteractAcrossProfilesEnabled()) {
            logEvent(172);
            enableInteractAcrossProfiles(false);
            refreshUi();
            return;
        }
        showConsentDialog();
    }

    private void showConsentDialog() {
        View inflate = getLayoutInflater().inflate(R$layout.interact_across_profiles_consent_dialog, (ViewGroup) null);
        ((TextView) inflate.findViewById(R$id.interact_across_profiles_consent_dialog_title)).setText(this.mDpm.getResources().getString("Settings.CONNECT_APPS_DIALOG_TITLE", new Supplier() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$showConsentDialog$0;
                lambda$showConsentDialog$0 = InteractAcrossProfilesDetails.this.lambda$showConsentDialog$0();
                return lambda$showConsentDialog$0;
            }
        }, new Object[]{this.mAppLabel}));
        ((TextView) inflate.findViewById(R$id.app_data_summary)).setText(this.mDpm.getResources().getString("Settings.APP_CAN_ACCESS_PERSONAL_DATA", new Supplier() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$showConsentDialog$1;
                lambda$showConsentDialog$1 = InteractAcrossProfilesDetails.this.lambda$showConsentDialog$1();
                return lambda$showConsentDialog$1;
            }
        }, new Object[]{this.mAppLabel}));
        ((TextView) inflate.findViewById(R$id.permissions_summary)).setText(this.mDpm.getResources().getString("Settings.APP_CAN_ACCESS_PERSONAL_PERMISSIONS", new Supplier() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$showConsentDialog$2;
                lambda$showConsentDialog$2 = InteractAcrossProfilesDetails.this.lambda$showConsentDialog$2();
                return lambda$showConsentDialog$2;
            }
        }, new Object[]{this.mAppLabel}));
        ((TextView) inflate.findViewById(R$id.interact_across_profiles_consent_dialog_summary)).setText(this.mDpm.getResources().getString("Settings.CONNECT_APPS_DIALOG_SUMMARY", new Supplier() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$showConsentDialog$3;
                lambda$showConsentDialog$3 = InteractAcrossProfilesDetails.this.lambda$showConsentDialog$3();
                return lambda$showConsentDialog$3;
            }
        }));
        new AlertDialog.Builder(getActivity()).setView(inflate).setPositiveButton(R$string.allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                InteractAcrossProfilesDetails.this.logEvent(170);
                InteractAcrossProfilesDetails.this.enableInteractAcrossProfiles(true);
                InteractAcrossProfilesDetails.this.refreshUi();
                if (InteractAcrossProfilesDetails.this.mIsPageLaunchedByApp) {
                    InteractAcrossProfilesDetails.this.setIntentAndFinish(true);
                }
            }
        }).setNegativeButton(R$string.deny, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                InteractAcrossProfilesDetails.this.logEvent(171);
                InteractAcrossProfilesDetails.this.refreshUi();
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$showConsentDialog$0() {
        return getString(R$string.interact_across_profiles_consent_dialog_title, this.mAppLabel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$showConsentDialog$1() {
        return getString(R$string.interact_across_profiles_consent_dialog_app_data_summary, this.mAppLabel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$showConsentDialog$2() {
        return getString(R$string.interact_across_profiles_consent_dialog_permissions_summary, this.mAppLabel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$showConsentDialog$3() {
        return getString(R$string.interact_across_profiles_consent_dialog_summary);
    }

    private boolean isInteractAcrossProfilesEnabled() {
        return isInteractAcrossProfilesEnabled(this.mContext, this.mPackageName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isInteractAcrossProfilesEnabled(Context context, String str) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        UserHandle workProfile = InteractAcrossProfilesSettings.getWorkProfile(userManager);
        if (workProfile == null) {
            return false;
        }
        return ((CrossProfileApps) context.getSystemService(CrossProfileApps.class)).canConfigureInteractAcrossProfiles(str) && isInteractAcrossProfilesEnabledInProfile(context, str, userManager.getProfileParent(workProfile)) && isInteractAcrossProfilesEnabledInProfile(context, str, workProfile);
    }

    private static boolean isInteractAcrossProfilesEnabledInProfile(Context context, String str, UserHandle userHandle) {
        try {
            return PermissionChecker.checkPermissionForPreflight(context, "android.permission.INTERACT_ACROSS_PROFILES", -1, context.getPackageManager().getApplicationInfoAsUser(str, 0, userHandle).uid, str) == 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableInteractAcrossProfiles(boolean z) {
        this.mCrossProfileApps.setInteractAcrossProfilesAppOp(this.mPackageName, !z ? 1 : 0);
    }

    private void handleInstallBannerClick() {
        if (this.mInstallAppIntent == null) {
            logEvent(169);
        } else if (!this.mInstalledInWork) {
            logEvent(168);
            this.mContext.startActivityAsUser(this.mInstallAppIntent, this.mWorkProfile);
        } else if (this.mInstalledInPersonal) {
        } else {
            logEvent(168);
            this.mContext.startActivityAsUser(this.mInstallAppIntent, this.mPersonalProfile);
        }
    }

    public static CharSequence getPreferenceSummary(Context context, String str) {
        int i;
        if (isInteractAcrossProfilesEnabled(context, str)) {
            i = R$string.interact_across_profiles_summary_allowed;
        } else {
            i = R$string.interact_across_profiles_summary_not_allowed;
        }
        return context.getString(i);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return false;
        }
        if (!this.mCrossProfileApps.canUserAttemptToConfigureInteractAcrossProfiles(this.mPackageName)) {
            this.mSwitchPref.setEnabled(false);
            return false;
        } else if (!this.mCrossProfileApps.canConfigureInteractAcrossProfiles(this.mPackageName)) {
            return refreshUiForNonConfigurableApps();
        } else {
            refreshUiForConfigurableApps();
            return true;
        }
    }

    private boolean refreshUiForNonConfigurableApps() {
        this.mSwitchPref.setChecked(false);
        this.mSwitchPref.setTitle(R$string.interact_across_profiles_switch_disabled);
        if (!isCrossProfilePackageAllowlisted(this.mPackageName)) {
            this.mInstallBanner.setVisible(false);
            this.mSwitchPref.setDisabledByAdmin(RestrictedLockUtils.getProfileOrDeviceOwner(this.mContext, this.mWorkProfile));
            return true;
        }
        this.mSwitchPref.setEnabled(false);
        boolean z = this.mInstalledInPersonal;
        if (z || this.mInstalledInWork) {
            if (!z) {
                this.mInstallBanner.setTitle(this.mDpm.getResources().getString("Settings.INSTALL_IN_PERSONAL_PROFILE_TO_CONNECT_PROMPT", new Supplier() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails$$ExternalSyntheticLambda0
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$refreshUiForNonConfigurableApps$4;
                        lambda$refreshUiForNonConfigurableApps$4 = InteractAcrossProfilesDetails.this.lambda$refreshUiForNonConfigurableApps$4();
                        return lambda$refreshUiForNonConfigurableApps$4;
                    }
                }, new Object[]{this.mAppLabel}));
                if (this.mInstallAppIntent != null) {
                    this.mInstallBanner.setSummary(R$string.interact_across_profiles_install_app_summary);
                }
                this.mInstallBanner.setVisible(true);
                return true;
            } else if (this.mInstalledInWork) {
                return false;
            } else {
                this.mInstallBanner.setTitle(this.mDpm.getResources().getString("Settings.INSTALL_IN_WORK_PROFILE_TO_CONNECT_PROMPT", new Supplier() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetails$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$refreshUiForNonConfigurableApps$5;
                        lambda$refreshUiForNonConfigurableApps$5 = InteractAcrossProfilesDetails.this.lambda$refreshUiForNonConfigurableApps$5();
                        return lambda$refreshUiForNonConfigurableApps$5;
                    }
                }, new Object[]{this.mAppLabel}));
                if (this.mInstallAppIntent != null) {
                    this.mInstallBanner.setSummary(R$string.interact_across_profiles_install_app_summary);
                }
                this.mInstallBanner.setVisible(true);
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$refreshUiForNonConfigurableApps$4() {
        return getString(R$string.interact_across_profiles_install_personal_app_title, this.mAppLabel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$refreshUiForNonConfigurableApps$5() {
        return getString(R$string.interact_across_profiles_install_work_app_title, this.mAppLabel);
    }

    private boolean isCrossProfilePackageAllowlisted(String str) {
        return ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getAllCrossProfilePackages().contains(str);
    }

    private boolean isPackageInstalled(String str, int i) {
        try {
            return this.mContext.createContextAsUser(UserHandle.of(i), 0).getPackageManager().getPackageInfo(str, 786432) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private void refreshUiForConfigurableApps() {
        this.mInstallBanner.setVisible(false);
        this.mSwitchPref.setEnabled(true);
        if (isInteractAcrossProfilesEnabled()) {
            enableSwitchPref();
        } else {
            disableSwitchPref();
        }
    }

    private void enableSwitchPref() {
        this.mSwitchPref.setChecked(true);
        this.mSwitchPref.setTitle(R$string.interact_across_profiles_switch_enabled);
        ImageView imageView = (ImageView) this.mHeader.findViewById(R$id.entity_header_swap_horiz);
        if (imageView != null) {
            imageView.setImageDrawable(this.mContext.getDrawable(R$drawable.ic_swap_horiz_blue));
        }
    }

    private void disableSwitchPref() {
        this.mSwitchPref.setChecked(false);
        this.mSwitchPref.setTitle(R$string.interact_across_profiles_switch_disabled);
        ImageView imageView = (ImageView) this.mHeader.findViewById(R$id.entity_header_swap_horiz);
        if (imageView != null) {
            imageView.setImageDrawable(this.mContext.getDrawable(R$drawable.ic_swap_horiz_grey));
        }
    }

    private boolean launchedByApp() {
        Intent intent;
        Bundle bundleExtra = getIntent().getBundleExtra(":settings:show_fragment_args");
        if (bundleExtra == null || (intent = (Intent) bundleExtra.get("intent")) == null) {
            return false;
        }
        return "android.settings.MANAGE_CROSS_PROFILE_ACCESS".equals(intent.getAction());
    }
}
