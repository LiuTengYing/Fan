package com.android.settings.applications.specialaccess.deviceadmin;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class DeviceAdminAdd extends CollapsingToolbarBaseActivity {
    Button mActionButton;
    TextView mAddMsg;
    ImageView mAddMsgExpander;
    String mAddMsgText;
    boolean mAdding;
    boolean mAddingProfileOwner;
    TextView mAdminDescription;
    ImageView mAdminIcon;
    TextView mAdminName;
    ViewGroup mAdminPolicies;
    boolean mAdminPoliciesInitialized;
    TextView mAdminWarning;
    AppOpsManager mAppOps;
    Button mCancelButton;
    DevicePolicyManager mDPM;
    DeviceAdminInfo mDeviceAdmin;
    Handler mHandler;
    private LayoutInflater mLayoutInflaternflater;
    String mProfileOwnerName;
    TextView mProfileOwnerWarning;
    boolean mRefreshing;
    TextView mSupportMessage;
    Button mUninstallButton;
    boolean mWaitingForRemoveMsg;
    private final IBinder mToken = new Binder();
    boolean mAddMsgEllipsized = true;
    boolean mUninstalling = false;
    boolean mIsCalledFromSupportDialog = false;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x014d, code lost:
        r9.activityInfo = r2;
        new android.app.admin.DeviceAdminInfo(r12, r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0154, code lost:
        r13 = true;
     */
    @Override // com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onCreate(android.os.Bundle r13) {
        /*
            Method dump skipped, instructions count: 1049
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd.onCreate(android.os.Bundle):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$0() {
        return getString(R$string.profile_owner_add_title_simplified);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$1() {
        return getString(R$string.device_admin_warning_simplified, new Object[]{this.mProfileOwnerName});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$2() {
        return getString(R$string.adding_profile_owner_warning);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$3() {
        return getString(R$string.uninstall_device_admin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPolicyTransparencyDialogIfRequired() {
        RestrictedLockUtils.EnforcedAdmin adminEnforcingCantRemoveProfile;
        if (isManagedProfile(this.mDeviceAdmin) && this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner())) {
            ComponentName profileOwnerAsUser = this.mDPM.getProfileOwnerAsUser(getUserId());
            if (profileOwnerAsUser != null && this.mDPM.isOrganizationOwnedDeviceWithManagedProfile()) {
                adminEnforcingCantRemoveProfile = new RestrictedLockUtils.EnforcedAdmin(profileOwnerAsUser, "no_remove_managed_profile", UserHandle.of(getUserId()));
            } else if (hasBaseCantRemoveProfileRestriction()) {
                return;
            } else {
                adminEnforcingCantRemoveProfile = getAdminEnforcingCantRemoveProfile();
            }
            if (adminEnforcingCantRemoveProfile != null) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this, adminEnforcingCantRemoveProfile);
            }
        }
    }

    void addAndFinish() {
        try {
            logSpecialPermissionChange(true, this.mDeviceAdmin.getComponent().getPackageName());
            this.mDPM.setActiveAdmin(this.mDeviceAdmin.getComponent(), this.mRefreshing);
            EventLog.writeEvent(90201, this.mDeviceAdmin.getActivityInfo().applicationInfo.uid);
            unrestrictAppIfPossible(BatteryUtils.getInstance(this));
            setResult(-1);
        } catch (RuntimeException e) {
            Log.w("DeviceAdminAdd", "Exception trying to activate admin " + this.mDeviceAdmin.getComponent(), e);
            if (this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
                setResult(-1);
            }
        }
        if (this.mAddingProfileOwner) {
            try {
                this.mDPM.setProfileOwner(this.mDeviceAdmin.getComponent(), this.mProfileOwnerName, UserHandle.myUserId());
            } catch (RuntimeException unused) {
                setResult(0);
            }
        }
        finish();
    }

    void unrestrictAppIfPossible(BatteryUtils batteryUtils) {
        batteryUtils.clearForceAppStandby(this.mDeviceAdmin.getComponent().getPackageName());
    }

    void continueRemoveAction(CharSequence charSequence) {
        if (this.mWaitingForRemoveMsg) {
            this.mWaitingForRemoveMsg = false;
            if (charSequence == null) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                logSpecialPermissionChange(false, this.mDeviceAdmin.getComponent().getPackageName());
                this.mDPM.removeActiveAdmin(this.mDeviceAdmin.getComponent());
                finish();
                return;
            }
            try {
                ActivityManager.getService().stopAppSwitches();
            } catch (RemoteException unused2) {
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequence("android.app.extra.DISABLE_WARNING", charSequence);
            showDialog(1, bundle);
        }
    }

    void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(0, z ? 766 : 767, 0, str, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mActionButton.setEnabled(true);
        if (!this.mAddingProfileOwner) {
            updateInterface();
        }
        this.mAppOps.setUserRestriction(24, true, this.mToken);
        this.mAppOps.setUserRestriction(45, true, this.mToken);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.mActionButton.setEnabled(false);
        this.mAppOps.setUserRestriction(24, false, this.mToken);
        this.mAppOps.setUserRestriction(45, false, this.mToken);
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
    }

    @Override // android.app.Activity
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (this.mIsCalledFromSupportDialog) {
            finish();
        }
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int i, Bundle bundle) {
        if (i == 1) {
            CharSequence charSequence = bundle.getCharSequence("android.app.extra.DISABLE_WARNING");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(charSequence);
            builder.setPositiveButton(R$string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    try {
                        ActivityManager.getService().resumeAppSwitches();
                    } catch (RemoteException unused) {
                    }
                    DeviceAdminAdd deviceAdminAdd = DeviceAdminAdd.this;
                    deviceAdminAdd.mDPM.removeActiveAdmin(deviceAdminAdd.mDeviceAdmin.getComponent());
                    DeviceAdminAdd.this.finish();
                }
            });
            builder.setNegativeButton(R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
        return super.onCreateDialog(i, bundle);
    }

    void updateInterface() {
        findViewById(R$id.restricted_icon).setVisibility(8);
        this.mAdminIcon.setImageDrawable(this.mDeviceAdmin.loadIcon(getPackageManager()));
        this.mAdminName.setText(this.mDeviceAdmin.loadLabel(getPackageManager()));
        try {
            this.mAdminDescription.setText(this.mDeviceAdmin.loadDescription(getPackageManager()));
            this.mAdminDescription.setVisibility(0);
        } catch (Resources.NotFoundException unused) {
            this.mAdminDescription.setVisibility(8);
        }
        if (!TextUtils.isEmpty(this.mAddMsgText)) {
            this.mAddMsg.setText(this.mAddMsgText);
            this.mAddMsg.setVisibility(0);
        } else {
            this.mAddMsg.setVisibility(8);
            this.mAddMsgExpander.setVisibility(8);
        }
        boolean z = true;
        if (!this.mRefreshing && !this.mAddingProfileOwner && this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
            this.mAdding = false;
            boolean equals = this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner());
            boolean isManagedProfile = isManagedProfile(this.mDeviceAdmin);
            if (equals && isManagedProfile) {
                this.mAdminWarning.setText(this.mDPM.getResources().getString("Settings.WORK_PROFILE_ADMIN_POLICIES_WARNING", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda4
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateInterface$4;
                        lambda$updateInterface$4 = DeviceAdminAdd.this.lambda$updateInterface$4();
                        return lambda$updateInterface$4;
                    }
                }));
                this.mActionButton.setText(this.mDPM.getResources().getString("Settings.REMOVE_WORK_PROFILE", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda6
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateInterface$5;
                        lambda$updateInterface$5 = DeviceAdminAdd.this.lambda$updateInterface$5();
                        return lambda$updateInterface$5;
                    }
                }));
                RestrictedLockUtils.EnforcedAdmin adminEnforcingCantRemoveProfile = getAdminEnforcingCantRemoveProfile();
                boolean hasBaseCantRemoveProfileRestriction = hasBaseCantRemoveProfileRestriction();
                if ((hasBaseCantRemoveProfileRestriction && this.mDPM.isOrganizationOwnedDeviceWithManagedProfile()) || (adminEnforcingCantRemoveProfile != null && !hasBaseCantRemoveProfileRestriction)) {
                    findViewById(R$id.restricted_icon).setVisibility(0);
                }
                Button button = this.mActionButton;
                if (adminEnforcingCantRemoveProfile != null || hasBaseCantRemoveProfileRestriction) {
                    z = false;
                }
                button.setEnabled(z);
            } else if (equals || this.mDeviceAdmin.getComponent().equals(this.mDPM.getDeviceOwnerComponentOnCallingUser())) {
                if (equals) {
                    this.mAdminWarning.setText(this.mDPM.getResources().getString("Settings.USER_ADMIN_POLICIES_WARNING", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda7
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateInterface$6;
                            lambda$updateInterface$6 = DeviceAdminAdd.this.lambda$updateInterface$6();
                            return lambda$updateInterface$6;
                        }
                    }));
                } else if (isFinancedDevice()) {
                    this.mAdminWarning.setText(R$string.admin_financed_message);
                } else {
                    this.mAdminWarning.setText(this.mDPM.getResources().getString("Settings.DEVICE_ADMIN_POLICIES_WARNING", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda8
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateInterface$7;
                            lambda$updateInterface$7 = DeviceAdminAdd.this.lambda$updateInterface$7();
                            return lambda$updateInterface$7;
                        }
                    }));
                }
                this.mActionButton.setText(this.mDPM.getResources().getString("Settings.REMOVE_DEVICE_ADMIN", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda9
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateInterface$8;
                        lambda$updateInterface$8 = DeviceAdminAdd.this.lambda$updateInterface$8();
                        return lambda$updateInterface$8;
                    }
                }));
                this.mActionButton.setEnabled(false);
            } else {
                addDeviceAdminPolicies(false);
                final CharSequence loadLabel = this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager());
                this.mAdminWarning.setText(this.mDPM.getResources().getString("Settings.ACTIVE_DEVICE_ADMIN_WARNING", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda10
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateInterface$9;
                        lambda$updateInterface$9 = DeviceAdminAdd.this.lambda$updateInterface$9(loadLabel);
                        return lambda$updateInterface$9;
                    }
                }, new Object[]{loadLabel}));
                setTitle(R$string.active_device_admin_msg);
                if (this.mUninstalling) {
                    this.mActionButton.setText(this.mDPM.getResources().getString("Settings.REMOVE_AND_UNINSTALL_DEVICE_ADMIN", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda11
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateInterface$10;
                            lambda$updateInterface$10 = DeviceAdminAdd.this.lambda$updateInterface$10();
                            return lambda$updateInterface$10;
                        }
                    }));
                } else {
                    this.mActionButton.setText(this.mDPM.getResources().getString("Settings.REMOVE_DEVICE_ADMIN", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda12
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateInterface$11;
                            lambda$updateInterface$11 = DeviceAdminAdd.this.lambda$updateInterface$11();
                            return lambda$updateInterface$11;
                        }
                    }));
                }
            }
            CharSequence longSupportMessageForUser = this.mDPM.getLongSupportMessageForUser(this.mDeviceAdmin.getComponent(), UserHandle.myUserId());
            if (!TextUtils.isEmpty(longSupportMessageForUser)) {
                this.mSupportMessage.setText(longSupportMessageForUser);
                this.mSupportMessage.setVisibility(0);
                return;
            }
            this.mSupportMessage.setVisibility(8);
            return;
        }
        addDeviceAdminPolicies(true);
        final CharSequence loadLabel2 = this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager());
        this.mAdminWarning.setText(this.mDPM.getResources().getString("Settings.NEW_DEVICE_ADMIN_WARNING", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda13
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$updateInterface$12;
                lambda$updateInterface$12 = DeviceAdminAdd.this.lambda$updateInterface$12(loadLabel2);
                return lambda$updateInterface$12;
            }
        }, new Object[]{loadLabel2}));
        setTitle(this.mDPM.getResources().getString("Settings.ACTIVATE_DEVICE_ADMIN_APP", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda14
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$updateInterface$13;
                lambda$updateInterface$13 = DeviceAdminAdd.this.lambda$updateInterface$13();
                return lambda$updateInterface$13;
            }
        }));
        this.mActionButton.setText(this.mDPM.getResources().getString("Settings.ACTIVATE_THIS_DEVICE_ADMIN_APP", new Supplier() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$updateInterface$14;
                lambda$updateInterface$14 = DeviceAdminAdd.this.lambda$updateInterface$14();
                return lambda$updateInterface$14;
            }
        }));
        if (isAdminUninstallable()) {
            this.mUninstallButton.setVisibility(0);
        }
        this.mSupportMessage.setVisibility(8);
        this.mAdding = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$4() {
        return getString(R$string.admin_profile_owner_message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$5() {
        return getString(R$string.remove_managed_profile_label);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$6() {
        return getString(R$string.admin_profile_owner_user_message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$7() {
        return getString(R$string.admin_device_owner_message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$8() {
        return getString(R$string.remove_device_admin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$9(CharSequence charSequence) {
        return getString(R$string.device_admin_status, new Object[]{charSequence});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$10() {
        return getString(R$string.remove_and_uninstall_device_admin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$11() {
        return getString(R$string.remove_device_admin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$12(CharSequence charSequence) {
        return getString(R$string.device_admin_warning, new Object[]{charSequence});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$13() {
        return getString(R$string.add_device_admin_msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateInterface$14() {
        return getString(R$string.add_device_admin);
    }

    private RestrictedLockUtils.EnforcedAdmin getAdminEnforcingCantRemoveProfile() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this, "no_remove_managed_profile", getParentUserId());
    }

    private boolean hasBaseCantRemoveProfileRestriction() {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(this, "no_remove_managed_profile", getParentUserId());
    }

    private int getParentUserId() {
        return UserManager.get(this).getProfileParent(UserHandle.myUserId()).id;
    }

    private void addDeviceAdminPolicies(boolean z) {
        if (this.mAdminPoliciesInitialized) {
            return;
        }
        boolean isAdminUser = UserManager.get(this).isAdminUser();
        Iterator it = this.mDeviceAdmin.getUsedPolicies().iterator();
        while (it.hasNext()) {
            DeviceAdminInfo.PolicyInfo policyInfo = (DeviceAdminInfo.PolicyInfo) it.next();
            this.mAdminPolicies.addView(getPermissionItemView(getText(isAdminUser ? policyInfo.label : policyInfo.labelForSecondaryUsers), z ? getText(isAdminUser ? policyInfo.description : policyInfo.descriptionForSecondaryUsers) : ""));
        }
        this.mAdminPoliciesInitialized = true;
    }

    private View getPermissionItemView(CharSequence charSequence, CharSequence charSequence2) {
        Drawable drawable = getDrawable(17302881);
        View inflate = this.mLayoutInflaternflater.inflate(R$layout.app_permission_item, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R$id.permission_group);
        TextView textView2 = (TextView) inflate.findViewById(R$id.permission_list);
        ((ImageView) inflate.findViewById(R$id.perm_icon)).setImageDrawable(drawable);
        if (charSequence != null) {
            textView.setText(charSequence);
            textView2.setText(charSequence2);
        } else {
            textView.setText(charSequence2);
            textView2.setVisibility(8);
        }
        return inflate;
    }

    void toggleMessageEllipsis(View view) {
        TextView textView = (TextView) view;
        boolean z = !this.mAddMsgEllipsized;
        this.mAddMsgEllipsized = z;
        textView.setEllipsize(z ? TextUtils.TruncateAt.END : null);
        textView.setMaxLines(this.mAddMsgEllipsized ? getEllipsizedLines() : 15);
        this.mAddMsgExpander.setImageResource(this.mAddMsgEllipsized ? 17302240 : 17302239);
    }

    int getEllipsizedLines() {
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        return defaultDisplay.getHeight() > defaultDisplay.getWidth() ? 5 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isManagedProfile(DeviceAdminInfo deviceAdminInfo) {
        UserInfo userInfo = UserManager.get(this).getUserInfo(UserHandle.getUserId(deviceAdminInfo.getActivityInfo().applicationInfo.uid));
        if (userInfo != null) {
            return userInfo.isManagedProfile();
        }
        return false;
    }

    private boolean isFinancedDevice() {
        if (this.mDPM.isDeviceManaged()) {
            DevicePolicyManager devicePolicyManager = this.mDPM;
            if (devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1) {
                return true;
            }
        }
        return false;
    }

    private Optional<ComponentName> findAdminWithPackageName(final String str) {
        List<ComponentName> activeAdmins = this.mDPM.getActiveAdmins();
        if (activeAdmins == null) {
            return Optional.empty();
        }
        return activeAdmins.stream().filter(new Predicate() { // from class: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findAdminWithPackageName$15;
                lambda$findAdminWithPackageName$15 = DeviceAdminAdd.lambda$findAdminWithPackageName$15(str, (ComponentName) obj);
                return lambda$findAdminWithPackageName$15;
            }
        }).findAny();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$findAdminWithPackageName$15(String str, ComponentName componentName) {
        return componentName.getPackageName().equals(str);
    }

    private boolean isAdminUninstallable() {
        return !this.mDeviceAdmin.getActivityInfo().applicationInfo.isSystemApp();
    }
}
