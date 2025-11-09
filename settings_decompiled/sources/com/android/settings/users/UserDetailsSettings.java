package com.android.settings.users;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class UserDetailsSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = UserDetailsSettings.class.getSimpleName();
    Preference mAppAndContentAccessPref;
    Preference mAppCopyingPref;
    private Bundle mDefaultGuestRestrictions;
    private boolean mGuestUserAutoCreated;
    private SwitchPreference mPhonePref;
    Preference mRemoveUserPref;
    RestrictedPreference mSwitchUserPref;
    private UserCapabilities mUserCaps;
    UserInfo mUserInfo;
    private UserManager mUserManager;
    private final AtomicBoolean mGuestCreationScheduled = new AtomicBoolean();
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private void openAppCopyingScreen() {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        return i != 5 ? 0 : 591;
                    }
                    return 596;
                }
                return 593;
            }
            return 592;
        }
        return 591;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 98;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService("user");
        this.mUserCaps = UserCapabilities.create(activity);
        addPreferencesFromResource(R$xml.user_details_settings);
        this.mGuestUserAutoCreated = getPrefContext().getResources().getBoolean(17891673);
        initialize(activity, getArguments());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSwitchUserPref.setEnabled(canSwitchUserNow());
        if (this.mGuestUserAutoCreated) {
            this.mRemoveUserPref.setEnabled((this.mUserInfo.flags & 16) != 0);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mRemoveUserPref) {
            if (canDeleteUser()) {
                if (this.mUserInfo.isGuest()) {
                    showDialog(5);
                } else {
                    showDialog(1);
                }
                return true;
            }
        } else if (preference == this.mSwitchUserPref) {
            if (canSwitchUserNow()) {
                if (shouldShowSetupPromptDialog()) {
                    showDialog(4);
                } else {
                    switchUser();
                }
                return true;
            }
        } else if (preference == this.mAppAndContentAccessPref) {
            openAppAndContentAccessScreen(false);
            return true;
        } else if (preference == this.mAppCopyingPref) {
            openAppCopyingScreen();
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (Boolean.TRUE.equals(obj)) {
            showDialog(this.mUserInfo.isGuest() ? 2 : 3);
            return false;
        }
        enableCallsAndSms(false);
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (getActivity() == null) {
            return null;
        }
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            if (this.mGuestUserAutoCreated) {
                                return UserDialogs.createResetGuestDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda4
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public final void onClick(DialogInterface dialogInterface, int i2) {
                                        UserDetailsSettings.this.lambda$onCreateDialog$4(dialogInterface, i2);
                                    }
                                });
                            }
                            return UserDialogs.createRemoveGuestDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda5
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i2) {
                                    UserDetailsSettings.this.lambda$onCreateDialog$5(dialogInterface, i2);
                                }
                            });
                        }
                        throw new IllegalArgumentException("Unsupported dialogId " + i);
                    }
                    return UserDialogs.createSetupUserDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda3
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            UserDetailsSettings.this.lambda$onCreateDialog$3(dialogInterface, i2);
                        }
                    });
                }
                return UserDialogs.createEnablePhoneCallsAndSmsDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        UserDetailsSettings.this.lambda$onCreateDialog$2(dialogInterface, i2);
                    }
                });
            }
            return UserDialogs.createEnablePhoneCallsDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    UserDetailsSettings.this.lambda$onCreateDialog$1(dialogInterface, i2);
                }
            });
        }
        return UserDialogs.createRemoveDialog(getActivity(), this.mUserInfo.id, new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                UserDetailsSettings.this.lambda$onCreateDialog$0(dialogInterface, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        removeUser();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        enableCallsAndSms(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$2(DialogInterface dialogInterface, int i) {
        enableCallsAndSms(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$3(DialogInterface dialogInterface, int i) {
        if (canSwitchUserNow()) {
            switchUser();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$4(DialogInterface dialogInterface, int i) {
        resetGuest();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$5(DialogInterface dialogInterface, int i) {
        resetGuest();
    }

    private void resetGuest() {
        if (this.mUserInfo.isGuest()) {
            this.mMetricsFeatureProvider.action(getActivity(), 1763, new Pair[0]);
            this.mUserManager.removeUser(this.mUserInfo.id);
            setResult(100);
            finishFragment();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.MagnificationModePreferenceController.DialogHelper
    public void showDialog(int i) {
        super.showDialog(i);
    }

    void initialize(Context context, Bundle bundle) {
        int i;
        int i2 = bundle != null ? bundle.getInt("user_id", -10000) : -10000;
        if (i2 == -10000) {
            throw new IllegalStateException("Arguments to this fragment must contain the user id");
        }
        boolean z = bundle.getBoolean("new_user", false);
        this.mUserInfo = this.mUserManager.getUserInfo(i2);
        this.mSwitchUserPref = (RestrictedPreference) findPreference("switch_user");
        this.mPhonePref = (SwitchPreference) findPreference("enable_calling");
        this.mRemoveUserPref = findPreference("remove_user");
        this.mAppAndContentAccessPref = findPreference("app_and_content_access");
        this.mAppCopyingPref = findPreference("app_copying");
        this.mSwitchUserPref.setTitle(context.getString(R$string.user_switch_to_user, this.mUserInfo.name));
        if (this.mUserCaps.mDisallowSwitchUser) {
            this.mSwitchUserPref.setDisabledByAdmin(RestrictedLockUtilsInternal.getDeviceOwner(context));
        } else {
            this.mSwitchUserPref.setDisabledByAdmin(null);
            this.mSwitchUserPref.setSelectable(true);
            this.mSwitchUserPref.setOnPreferenceClickListener(this);
        }
        if (!this.mUserManager.isAdminUser()) {
            removePreference("enable_calling");
            removePreference("remove_user");
            removePreference("app_and_content_access");
            removePreference("app_copying");
            return;
        }
        if (!Utils.isVoiceCapable(context)) {
            removePreference("enable_calling");
        }
        if (this.mUserInfo.isRestricted()) {
            removePreference("enable_calling");
            if (z) {
                openAppAndContentAccessScreen(true);
            }
        } else {
            removePreference("app_and_content_access");
        }
        if (this.mUserInfo.isGuest()) {
            this.mPhonePref.setTitle(com.android.settings.R$string.user_enable_calling);
            Bundle defaultGuestRestrictions = this.mUserManager.getDefaultGuestRestrictions();
            this.mDefaultGuestRestrictions = defaultGuestRestrictions;
            this.mPhonePref.setChecked(!defaultGuestRestrictions.getBoolean("no_outgoing_calls"));
            Preference preference = this.mRemoveUserPref;
            if (this.mGuestUserAutoCreated) {
                i = R$string.guest_reset_guest;
            } else {
                i = R$string.guest_exit_guest;
            }
            preference.setTitle(i);
            if (this.mGuestUserAutoCreated) {
                this.mRemoveUserPref.setEnabled((this.mUserInfo.flags & 16) != 0);
            }
            removePreference("app_copying");
        } else {
            this.mPhonePref.setChecked(!this.mUserManager.hasUserRestriction("no_outgoing_calls", new UserHandle(i2)));
            this.mRemoveUserPref.setTitle(com.android.settings.R$string.user_remove_user);
            removePreference("app_copying");
        }
        if (RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_remove_user", UserHandle.myUserId())) {
            removePreference("remove_user");
        }
        this.mRemoveUserPref.setOnPreferenceClickListener(this);
        this.mPhonePref.setOnPreferenceChangeListener(this);
        this.mAppAndContentAccessPref.setOnPreferenceClickListener(this);
        this.mAppCopyingPref.setOnPreferenceClickListener(this);
    }

    boolean canDeleteUser() {
        FragmentActivity activity;
        if (this.mUserManager.isAdminUser() && (activity = getActivity()) != null) {
            RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(activity, "no_remove_user", UserHandle.myUserId());
            if (checkIfRestrictionEnforced != null) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(activity, checkIfRestrictionEnforced);
                return false;
            }
            return true;
        }
        return false;
    }

    boolean canSwitchUserNow() {
        return this.mUserManager.getUserSwitchability() == 0;
    }

    void switchUser() {
        Trace.beginSection("UserDetailSettings.switchUser");
        try {
            try {
                if (this.mUserInfo.isGuest()) {
                    this.mMetricsFeatureProvider.action(getActivity(), 1765, new Pair[0]);
                }
                ActivityManager.getService().switchUser(this.mUserInfo.id);
            } catch (RemoteException unused) {
                Log.e(TAG, "Error while switching to other user.");
            }
        } finally {
            Trace.endSection();
            finishFragment();
        }
    }

    private void enableCallsAndSms(boolean z) {
        this.mPhonePref.setChecked(z);
        if (this.mUserInfo.isGuest()) {
            this.mDefaultGuestRestrictions.putBoolean("no_outgoing_calls", !z);
            this.mDefaultGuestRestrictions.putBoolean("no_sms", true);
            this.mUserManager.setDefaultGuestRestrictions(this.mDefaultGuestRestrictions);
            for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
                if (userInfo.isGuest()) {
                    UserHandle of = UserHandle.of(userInfo.id);
                    for (String str : this.mDefaultGuestRestrictions.keySet()) {
                        this.mUserManager.setUserRestriction(str, this.mDefaultGuestRestrictions.getBoolean(str), of);
                    }
                }
            }
            return;
        }
        UserHandle of2 = UserHandle.of(this.mUserInfo.id);
        this.mUserManager.setUserRestriction("no_outgoing_calls", !z, of2);
        this.mUserManager.setUserRestriction("no_sms", !z, of2);
    }

    private void removeUser() {
        this.mUserManager.removeUser(this.mUserInfo.id);
        finishFragment();
    }

    private void openAppAndContentAccessScreen(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", this.mUserInfo.id);
        bundle.putBoolean("new_user", z);
        new SubSettingLauncher(getContext()).setDestination(AppRestrictionsFragment.class.getName()).setArguments(bundle).setTitleRes(com.android.settings.R$string.user_restrictions_title).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private boolean isSecondaryUser(UserInfo userInfo) {
        return "android.os.usertype.full.SECONDARY".equals(userInfo.userType);
    }

    private boolean shouldShowSetupPromptDialog() {
        return isSecondaryUser(this.mUserInfo) && !this.mUserInfo.isInitialized();
    }
}
