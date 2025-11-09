package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.VpnManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import com.android.settings.R$string;
import com.android.settings.vpn2.VpnUtils;
import com.android.settingslib.utils.WorkPolicyUtils;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class EnterprisePrivacyFeatureProviderImpl implements EnterprisePrivacyFeatureProvider {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private final ConnectivityManager mCm;
    private final Context mContext;
    private final DevicePolicyManager mDpm;
    private final PackageManager mPm;
    private final Resources mResources;
    private final UserManager mUm;
    private final VpnManager mVm;
    private final WorkPolicyUtils mWorkPolicyUtils;

    public EnterprisePrivacyFeatureProviderImpl(Context context, DevicePolicyManager devicePolicyManager, PackageManager packageManager, UserManager userManager, ConnectivityManager connectivityManager, VpnManager vpnManager, Resources resources) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mDpm = devicePolicyManager;
        this.mPm = packageManager;
        this.mUm = userManager;
        this.mCm = connectivityManager;
        this.mVm = vpnManager;
        this.mResources = resources;
        this.mWorkPolicyUtils = new WorkPolicyUtils(applicationContext);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasDeviceOwner() {
        return getDeviceOwnerComponent() != null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isInCompMode() {
        return hasDeviceOwner() && getManagedProfileUserId() != -10000;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getDeviceOwnerOrganizationName() {
        CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName == null) {
            return null;
        }
        return deviceOwnerOrganizationName.toString();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public CharSequence getDeviceOwnerDisclosure() {
        if (hasDeviceOwner()) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            final CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
            if (deviceOwnerOrganizationName != null) {
                spannableStringBuilder.append((CharSequence) this.mDpm.getResources().getString("Settings.DEVICE_MANAGED_WITH_NAME", new Supplier() { // from class: com.android.settings.enterprise.EnterprisePrivacyFeatureProviderImpl$$ExternalSyntheticLambda0
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDeviceOwnerDisclosure$0;
                        lambda$getDeviceOwnerDisclosure$0 = EnterprisePrivacyFeatureProviderImpl.this.lambda$getDeviceOwnerDisclosure$0(deviceOwnerOrganizationName);
                        return lambda$getDeviceOwnerDisclosure$0;
                    }
                }, new Object[]{deviceOwnerOrganizationName}));
            } else {
                spannableStringBuilder.append((CharSequence) this.mDpm.getResources().getString("Settings.DEVICE_MANAGED_WITHOUT_NAME", new Supplier() { // from class: com.android.settings.enterprise.EnterprisePrivacyFeatureProviderImpl$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDeviceOwnerDisclosure$1;
                        lambda$getDeviceOwnerDisclosure$1 = EnterprisePrivacyFeatureProviderImpl.this.lambda$getDeviceOwnerDisclosure$1();
                        return lambda$getDeviceOwnerDisclosure$1;
                    }
                }));
            }
            return spannableStringBuilder;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDeviceOwnerDisclosure$0(CharSequence charSequence) {
        return this.mResources.getString(R$string.do_disclosure_with_name, charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDeviceOwnerDisclosure$1() {
        return this.mResources.getString(R$string.do_disclosure_generic);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastSecurityLogRetrievalTime() {
        long lastSecurityLogRetrievalTime = this.mDpm.getLastSecurityLogRetrievalTime();
        if (lastSecurityLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastSecurityLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastBugReportRequestTime() {
        long lastBugReportRequestTime = this.mDpm.getLastBugReportRequestTime();
        if (lastBugReportRequestTime < 0) {
            return null;
        }
        return new Date(lastBugReportRequestTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastNetworkLogRetrievalTime() {
        long lastNetworkLogRetrievalTime = this.mDpm.getLastNetworkLogRetrievalTime();
        if (lastNetworkLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastNetworkLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isSecurityLoggingEnabled() {
        return this.mDpm.isSecurityLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isNetworkLoggingEnabled() {
        return this.mDpm.isNetworkLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInCurrentUser() {
        return VpnUtils.isAlwaysOnVpnSet(this.mVm, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInManagedProfile() {
        int managedProfileUserId = getManagedProfileUserId();
        return managedProfileUserId != -10000 && VpnUtils.isAlwaysOnVpnSet(this.mVm, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInCurrentUser() {
        ComponentName deviceOwnerComponentOnCallingUser = this.mDpm.getDeviceOwnerComponentOnCallingUser();
        if (deviceOwnerComponentOnCallingUser == null) {
            deviceOwnerComponentOnCallingUser = this.mDpm.getProfileOwnerAsUser(MY_USER_ID);
        }
        if (deviceOwnerComponentOnCallingUser == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(deviceOwnerComponentOnCallingUser, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInManagedProfile() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(profileOwnerAsUser, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getImeLabelIfOwnerSet() {
        if (this.mDpm.isCurrentInputMethodSetByOwner()) {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            int i = MY_USER_ID;
            String stringForUser = Settings.Secure.getStringForUser(contentResolver, "default_input_method", i);
            if (stringForUser == null) {
                return null;
            }
            try {
                return this.mPm.getApplicationInfoAsUser(stringForUser, 0, i).loadLabel(this.mPm).toString();
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        return null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForCurrentUser() {
        List ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(MY_USER_ID));
        if (ownerInstalledCaCerts == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForManagedProfile() {
        List ownerInstalledCaCerts;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(managedProfileUserId))) == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfActiveDeviceAdminsForCurrentUserAndManagedProfile() {
        List activeAdminsAsUser;
        int i = 0;
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            if (!userInfo.isCloneProfile() && (activeAdminsAsUser = this.mDpm.getActiveAdminsAsUser(userInfo.id)) != null) {
                i += activeAdminsAsUser.size();
            }
        }
        return i;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasWorkPolicyInfo() {
        return this.mWorkPolicyUtils.hasWorkPolicy();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showWorkPolicyInfo(Context context) {
        return this.mWorkPolicyUtils.showWorkPolicyInfo(context);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showParentalControls() {
        Intent parentalControlsIntent = getParentalControlsIntent();
        if (parentalControlsIntent != null) {
            this.mContext.startActivity(parentalControlsIntent);
            return true;
        }
        return false;
    }

    private Intent getParentalControlsIntent() {
        DevicePolicyManager devicePolicyManager = this.mDpm;
        int i = MY_USER_ID;
        ComponentName profileOwnerOrDeviceOwnerSupervisionComponent = devicePolicyManager.getProfileOwnerOrDeviceOwnerSupervisionComponent(new UserHandle(i));
        if (profileOwnerOrDeviceOwnerSupervisionComponent == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_PARENTAL_CONTROLS").setPackage(profileOwnerOrDeviceOwnerSupervisionComponent.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivitiesAsUser(addFlags, 0, i).size() != 0) {
            return addFlags;
        }
        return null;
    }

    private ComponentName getDeviceOwnerComponent() {
        if (this.mPm.hasSystemFeature("android.software.device_admin")) {
            return this.mDpm.getDeviceOwnerComponentOnAnyUser();
        }
        return null;
    }

    private UserInfo getManagedProfileUserInfo() {
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            if (userInfo.isManagedProfile()) {
                return userInfo;
            }
        }
        return null;
    }

    private int getManagedProfileUserId() {
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (managedProfileUserInfo != null) {
            return managedProfileUserInfo.id;
        }
        return -10000;
    }
}
