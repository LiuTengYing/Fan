package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableData;
import android.text.BidiFormatter;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.AccessiblePreferenceCategory;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class AccountPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, AuthenticatorHelper.OnAccountsUpdateListener, Preference.OnPreferenceClickListener, LifecycleObserver, OnPause, OnResume {
    public static final String[] SPRD_ACCOUNTS = {"USIM Account", "SIM Account", "SDN Account", "sprd.com.android.account.phone", "Preload Account"};
    private int mAccountProfileOrder;
    private String[] mAuthorities;
    private int mAuthoritiesCount;
    private DevicePolicyManager mDpm;
    private SettingsPreferenceFragment mFragment;
    private AccountRestrictionHelper mHelper;
    private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private Preference mProfileNotAvailablePreference;
    private SparseArray<ProfileData> mProfiles;
    private int mType;
    private UserManager mUm;

    /* loaded from: classes.dex */
    public static class ProfileData {
        public ArrayMap<String, AccountTypePreference> accountPreferences = new ArrayMap<>();
        public RestrictedPreference addAccountPreference;
        public AuthenticatorHelper authenticatorHelper;
        public Preference managedProfilePreference;
        public boolean pendingRemoval;
        public PreferenceGroup preferenceGroup;
        public RestrictedPreference removeWorkProfilePreference;
        public UserInfo userInfo;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    public AccountPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, int i) {
        this(context, settingsPreferenceFragment, strArr, new AccountRestrictionHelper(context), i);
    }

    AccountPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, AccountRestrictionHelper accountRestrictionHelper, int i) {
        super(context);
        this.mProfiles = new SparseArray<>();
        this.mManagedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver();
        this.mAuthoritiesCount = 0;
        this.mAccountProfileOrder = 101;
        this.mUm = (UserManager) context.getSystemService("user");
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        this.mAuthorities = strArr;
        this.mFragment = settingsPreferenceFragment;
        if (strArr != null) {
            this.mAuthoritiesCount = strArr.length;
        }
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        this.mHelper = accountRestrictionHelper;
        this.mType = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mUm.isManagedProfile();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        updateUi();
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateDynamicRawDataToIndex(List<SearchIndexableRaw> list) {
        if (isAvailable()) {
            final Resources resources = this.mContext.getResources();
            String string = resources.getString(R$string.account_settings_title);
            for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
                if (userInfo.isEnabled() && userInfo.isManagedProfile()) {
                    if (!this.mHelper.hasBaseUserRestriction("no_remove_managed_profile", UserHandle.myUserId())) {
                        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(this.mContext);
                        ((SearchIndexableData) searchIndexableRaw).key = "remove_profile";
                        searchIndexableRaw.title = this.mDpm.getResources().getString("Settings.REMOVE_WORK_PROFILE", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda1
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                String lambda$updateDynamicRawDataToIndex$0;
                                lambda$updateDynamicRawDataToIndex$0 = AccountPreferenceController.lambda$updateDynamicRawDataToIndex$0(resources);
                                return lambda$updateDynamicRawDataToIndex$0;
                            }
                        });
                        searchIndexableRaw.screenTitle = string;
                        list.add(searchIndexableRaw);
                    }
                    SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(this.mContext);
                    ((SearchIndexableData) searchIndexableRaw2).key = "work_profile_setting";
                    searchIndexableRaw2.title = this.mDpm.getResources().getString("Settings.MANAGED_PROFILE_SETTINGS_TITLE", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda2
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateDynamicRawDataToIndex$1;
                            lambda$updateDynamicRawDataToIndex$1 = AccountPreferenceController.lambda$updateDynamicRawDataToIndex$1(resources);
                            return lambda$updateDynamicRawDataToIndex$1;
                        }
                    });
                    searchIndexableRaw2.screenTitle = string;
                    list.add(searchIndexableRaw2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$updateDynamicRawDataToIndex$0(Resources resources) {
        return resources.getString(R$string.remove_managed_profile_label);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$updateDynamicRawDataToIndex$1(Resources resources) {
        return resources.getString(R$string.managed_profile_settings_title);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateUi();
        this.mManagedProfileBroadcastReceiver.register(this.mContext);
        listenToAccountUpdates();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        stopListeningToAccountUpdates();
        this.mManagedProfileBroadcastReceiver.unregister(this.mContext);
    }

    @Override // com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
        ProfileData profileData = this.mProfiles.get(userHandle.getIdentifier());
        if (profileData != null) {
            updateAccountTypes(profileData);
            return;
        }
        Log.w("AccountPrefController", "Missing Settings screen for: " + userHandle.getIdentifier());
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int metricsCategory = this.mFragment.getMetricsCategory();
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            ProfileData valueAt = this.mProfiles.valueAt(i);
            if (preference == valueAt.addAccountPreference) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, metricsCategory);
                Intent intent = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
                intent.putExtra("android.intent.extra.USER", valueAt.userInfo.getUserHandle());
                intent.putExtra("authorities", this.mAuthorities);
                this.mContext.startActivity(intent);
                return true;
            } else if (preference == valueAt.removeWorkProfilePreference) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, metricsCategory);
                RemoveUserFragment.newInstance(valueAt.userInfo.id).show(this.mFragment.getFragmentManager(), "removeUser");
                return true;
            } else if (preference == valueAt.managedProfilePreference) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, metricsCategory);
                Bundle bundle = new Bundle();
                bundle.putParcelable("android.intent.extra.USER", valueAt.userInfo.getUserHandle());
                new SubSettingLauncher(this.mContext).setSourceMetricsCategory(metricsCategory).setDestination(ManagedProfileSettings.class.getName()).setTitleText(this.mDpm.getResources().getString("Settings.MANAGED_PROFILE_SETTINGS_TITLE", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$onPreferenceClick$2;
                        lambda$onPreferenceClick$2 = AccountPreferenceController.this.lambda$onPreferenceClick$2();
                        return lambda$onPreferenceClick$2;
                    }
                })).setArguments(bundle).launch();
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onPreferenceClick$2() {
        return this.mContext.getString(R$string.managed_profile_settings_title);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUi() {
        if (!isAvailable()) {
            Log.e("AccountPrefController", "We should not be showing settings for a managed profile");
            return;
        }
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            this.mProfiles.valueAt(i).pendingRemoval = true;
        }
        if (this.mUm.isRestrictedProfile()) {
            updateProfileUi(this.mUm.getUserInfo(UserHandle.myUserId()));
        } else {
            List profiles = this.mUm.getProfiles(UserHandle.myUserId());
            int size2 = profiles.size();
            for (int i2 = 0; i2 < size2; i2++) {
                if (!((UserInfo) profiles.get(i2)).isCloneProfile()) {
                    if (((UserInfo) profiles.get(i2)).isManagedProfile() && (this.mType & 2) != 0) {
                        updateProfileUi((UserInfo) profiles.get(i2));
                    } else if (!((UserInfo) profiles.get(i2)).isManagedProfile() && (this.mType & 1) != 0) {
                        updateProfileUi((UserInfo) profiles.get(i2));
                    }
                }
            }
        }
        cleanUpPreferences();
        int size3 = this.mProfiles.size();
        for (int i3 = 0; i3 < size3; i3++) {
            updateAccountTypes(this.mProfiles.valueAt(i3));
        }
    }

    private void updateProfileUi(UserInfo userInfo) {
        if (this.mFragment.getPreferenceManager() == null) {
            return;
        }
        ProfileData profileData = this.mProfiles.get(userInfo.id);
        if (profileData != null) {
            profileData.pendingRemoval = false;
            profileData.userInfo = userInfo;
            if (userInfo.isEnabled()) {
                profileData.authenticatorHelper = new AuthenticatorHelper(this.mContext, userInfo.getUserHandle(), this);
                return;
            }
            return;
        }
        Context context = this.mContext;
        ProfileData profileData2 = new ProfileData();
        profileData2.userInfo = userInfo;
        AccessiblePreferenceCategory createAccessiblePreferenceCategory = this.mHelper.createAccessiblePreferenceCategory(this.mFragment.getPreferenceManager().getContext());
        int i = this.mAccountProfileOrder;
        this.mAccountProfileOrder = i + 1;
        createAccessiblePreferenceCategory.setOrder(i);
        createAccessiblePreferenceCategory.setTitle(R$string.account_settings);
        if (isSingleProfile()) {
            String string = context.getString(R$string.account_for_section_header, BidiFormatter.getInstance().unicodeWrap(userInfo.name));
            createAccessiblePreferenceCategory.setTitle(string);
            createAccessiblePreferenceCategory.setContentDescription(string);
        } else if (userInfo.isManagedProfile()) {
            if (this.mType == 3) {
                createAccessiblePreferenceCategory.setTitle(this.mDpm.getResources().getString("Settings.WORK_CATEGORY_HEADER", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda4
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateProfileUi$3;
                        lambda$updateProfileUi$3 = AccountPreferenceController.this.lambda$updateProfileUi$3();
                        return lambda$updateProfileUi$3;
                    }
                }));
                final String workGroupSummary = getWorkGroupSummary(context, userInfo);
                createAccessiblePreferenceCategory.setSummary(workGroupSummary);
                createAccessiblePreferenceCategory.setContentDescription(this.mDpm.getResources().getString("Settings.ACCESSIBILITY_CATEGORY_WORK", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda5
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateProfileUi$4;
                        lambda$updateProfileUi$4 = AccountPreferenceController.this.lambda$updateProfileUi$4(workGroupSummary);
                        return lambda$updateProfileUi$4;
                    }
                }));
            }
            RestrictedPreference newRemoveWorkProfilePreference = newRemoveWorkProfilePreference();
            profileData2.removeWorkProfilePreference = newRemoveWorkProfilePreference;
            this.mHelper.enforceRestrictionOnPreference(newRemoveWorkProfilePreference, "no_remove_managed_profile", UserHandle.myUserId());
            profileData2.managedProfilePreference = newManagedProfileSettings();
        } else if (this.mType == 3) {
            createAccessiblePreferenceCategory.setTitle(this.mDpm.getResources().getString("Settings.PERSONAL_CATEGORY_HEADER", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda6
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateProfileUi$5;
                    lambda$updateProfileUi$5 = AccountPreferenceController.this.lambda$updateProfileUi$5();
                    return lambda$updateProfileUi$5;
                }
            }));
            createAccessiblePreferenceCategory.setContentDescription(this.mDpm.getResources().getString("Settings.ACCESSIBILITY_CATEGORY_PERSONAL", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda7
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateProfileUi$6;
                    lambda$updateProfileUi$6 = AccountPreferenceController.this.lambda$updateProfileUi$6();
                    return lambda$updateProfileUi$6;
                }
            }));
        }
        PreferenceScreen preferenceScreen = this.mFragment.getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.addPreference(createAccessiblePreferenceCategory);
        }
        profileData2.preferenceGroup = createAccessiblePreferenceCategory;
        if (userInfo.isEnabled()) {
            profileData2.authenticatorHelper = new AuthenticatorHelper(context, userInfo.getUserHandle(), this);
            RestrictedPreference newAddAccountPreference = newAddAccountPreference();
            profileData2.addAccountPreference = newAddAccountPreference;
            this.mHelper.enforceRestrictionOnPreference(newAddAccountPreference, "no_modify_accounts", userInfo.id);
        }
        this.mProfiles.put(userInfo.id, profileData2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateProfileUi$3() {
        return this.mContext.getString(R$string.category_work);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateProfileUi$4(String str) {
        return this.mContext.getString(R$string.accessibility_category_work, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateProfileUi$5() {
        return this.mContext.getString(R$string.category_personal);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateProfileUi$6() {
        return this.mContext.getString(R$string.accessibility_category_personal);
    }

    private RestrictedPreference newAddAccountPreference() {
        RestrictedPreference restrictedPreference = new RestrictedPreference(this.mFragment.getPreferenceManager().getContext());
        restrictedPreference.setKey("add_account");
        restrictedPreference.setTitle(R$string.add_account_label);
        restrictedPreference.setIcon(R$drawable.ic_add_24dp);
        restrictedPreference.setOnPreferenceClickListener(this);
        restrictedPreference.setOrder(AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP);
        return restrictedPreference;
    }

    private RestrictedPreference newRemoveWorkProfilePreference() {
        RestrictedPreference restrictedPreference = new RestrictedPreference(this.mFragment.getPreferenceManager().getContext());
        restrictedPreference.setKey("remove_profile");
        restrictedPreference.setTitle(this.mDpm.getResources().getString("Settings.REMOVE_WORK_PROFILE", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda10
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$newRemoveWorkProfilePreference$7;
                lambda$newRemoveWorkProfilePreference$7 = AccountPreferenceController.this.lambda$newRemoveWorkProfilePreference$7();
                return lambda$newRemoveWorkProfilePreference$7;
            }
        }));
        restrictedPreference.setIcon(R$drawable.ic_delete);
        restrictedPreference.setOnPreferenceClickListener(this);
        restrictedPreference.setOrder(1002);
        return restrictedPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$newRemoveWorkProfilePreference$7() {
        return this.mContext.getString(R$string.remove_managed_profile_label);
    }

    private Preference newManagedProfileSettings() {
        Preference preference = new Preference(this.mFragment.getPreferenceManager().getContext());
        preference.setKey("work_profile_setting");
        preference.setTitle(this.mDpm.getResources().getString("Settings.MANAGED_PROFILE_SETTINGS_TITLE", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda9
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$newManagedProfileSettings$8;
                lambda$newManagedProfileSettings$8 = AccountPreferenceController.this.lambda$newManagedProfileSettings$8();
                return lambda$newManagedProfileSettings$8;
            }
        }));
        preference.setIcon(R$drawable.ic_settings_24dp);
        preference.setOnPreferenceClickListener(this);
        preference.setOrder(1001);
        return preference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$newManagedProfileSettings$8() {
        return this.mContext.getString(R$string.managed_profile_settings_title);
    }

    private String getWorkGroupSummary(Context context, UserInfo userInfo) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo adminApplicationInfo = Utils.getAdminApplicationInfo(context, userInfo.id);
        if (adminApplicationInfo == null) {
            return null;
        }
        final CharSequence applicationLabel = packageManager.getApplicationLabel(adminApplicationInfo);
        return this.mDpm.getResources().getString("Settings.MANAGED_BY", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda8
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getWorkGroupSummary$9;
                lambda$getWorkGroupSummary$9 = AccountPreferenceController.this.lambda$getWorkGroupSummary$9(applicationLabel);
                return lambda$getWorkGroupSummary$9;
            }
        }, new Object[]{applicationLabel});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getWorkGroupSummary$9(CharSequence charSequence) {
        return this.mContext.getString(R$string.managing_admin, charSequence);
    }

    void cleanUpPreferences() {
        PreferenceScreen preferenceScreen = this.mFragment.getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }
        for (int size = this.mProfiles.size() - 1; size >= 0; size--) {
            ProfileData valueAt = this.mProfiles.valueAt(size);
            if (valueAt.pendingRemoval) {
                preferenceScreen.removePreference(valueAt.preferenceGroup);
                this.mProfiles.removeAt(size);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listenToAccountUpdates() {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            AuthenticatorHelper authenticatorHelper = this.mProfiles.valueAt(i).authenticatorHelper;
            if (authenticatorHelper != null) {
                authenticatorHelper.listenToAccountUpdates();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopListeningToAccountUpdates() {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            AuthenticatorHelper authenticatorHelper = this.mProfiles.valueAt(i).authenticatorHelper;
            if (authenticatorHelper != null) {
                authenticatorHelper.stopListeningToAccountUpdates();
            }
        }
    }

    private void updateAccountTypes(ProfileData profileData) {
        if (this.mFragment.getPreferenceManager() == null || profileData.preferenceGroup.getPreferenceManager() == null) {
            return;
        }
        if (profileData.userInfo.isEnabled()) {
            ArrayMap<String, AccountTypePreference> arrayMap = new ArrayMap<>(profileData.accountPreferences);
            ArrayList<AccountTypePreference> accountTypePreferences = getAccountTypePreferences(profileData.authenticatorHelper, profileData.userInfo.getUserHandle(), arrayMap);
            int size = accountTypePreferences.size();
            for (int i = 0; i < size; i++) {
                AccountTypePreference accountTypePreference = accountTypePreferences.get(i);
                accountTypePreference.setOrder(i);
                String key = accountTypePreference.getKey();
                if (!profileData.accountPreferences.containsKey(key)) {
                    profileData.preferenceGroup.addPreference(accountTypePreference);
                    profileData.accountPreferences.put(key, accountTypePreference);
                }
            }
            RestrictedPreference restrictedPreference = profileData.addAccountPreference;
            if (restrictedPreference != null) {
                profileData.preferenceGroup.addPreference(restrictedPreference);
            }
            for (String str : arrayMap.keySet()) {
                profileData.preferenceGroup.removePreference(profileData.accountPreferences.get(str));
                profileData.accountPreferences.remove(str);
            }
        } else {
            profileData.preferenceGroup.removeAll();
            if (this.mProfileNotAvailablePreference == null) {
                this.mProfileNotAvailablePreference = new Preference(this.mFragment.getPreferenceManager().getContext());
            }
            this.mProfileNotAvailablePreference.setEnabled(false);
            this.mProfileNotAvailablePreference.setIcon(R$drawable.empty_icon);
            this.mProfileNotAvailablePreference.setTitle((CharSequence) null);
            this.mProfileNotAvailablePreference.setSummary(this.mDpm.getResources().getString("Settings.WORK_PROFILE_NOT_AVAILABLE", new Supplier() { // from class: com.android.settings.accounts.AccountPreferenceController$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateAccountTypes$10;
                    lambda$updateAccountTypes$10 = AccountPreferenceController.this.lambda$updateAccountTypes$10();
                    return lambda$updateAccountTypes$10;
                }
            }));
            profileData.preferenceGroup.addPreference(this.mProfileNotAvailablePreference);
        }
        RestrictedPreference restrictedPreference2 = profileData.removeWorkProfilePreference;
        if (restrictedPreference2 != null) {
            profileData.preferenceGroup.addPreference(restrictedPreference2);
        }
        Preference preference = profileData.managedProfilePreference;
        if (preference != null) {
            profileData.preferenceGroup.addPreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateAccountTypes$10() {
        return this.mContext.getString(R$string.managed_profile_not_available_label);
    }

    public static boolean filterSprdAccount(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (str != null && str.equals(str2)) {
                Log.d("AccountPrefController", " type = " + str);
                return true;
            }
        }
        return false;
    }

    private ArrayList<AccountTypePreference> getAccountTypePreferences(AuthenticatorHelper authenticatorHelper, UserHandle userHandle, ArrayMap<String, AccountTypePreference> arrayMap) {
        CharSequence labelForType;
        String[] strArr;
        String[] strArr2;
        int i;
        int i2;
        Account[] accountArr;
        int i3;
        UserHandle userHandle2 = userHandle;
        String[] enabledAccountTypes = authenticatorHelper.getEnabledAccountTypes();
        ArrayList<AccountTypePreference> arrayList = new ArrayList<>(enabledAccountTypes.length);
        int i4 = 0;
        while (i4 < enabledAccountTypes.length) {
            String str = enabledAccountTypes[i4];
            if (filterSprdAccount(SPRD_ACCOUNTS, str) || !accountTypeHasAnyRequestedAuthorities(authenticatorHelper, str) || (labelForType = authenticatorHelper.getLabelForType(this.mContext, str)) == null) {
                strArr = enabledAccountTypes;
            } else {
                String packageForType = authenticatorHelper.getPackageForType(str);
                int labelIdForType = authenticatorHelper.getLabelIdForType(str);
                Account[] accountsByTypeAsUser = AccountManager.get(this.mContext).getAccountsByTypeAsUser(str, userHandle2);
                Drawable drawableForType = authenticatorHelper.getDrawableForType(this.mContext, str);
                Context context = this.mFragment.getPreferenceManager().getContext();
                int length = accountsByTypeAsUser.length;
                int i5 = 0;
                while (i5 < length) {
                    Account account = accountsByTypeAsUser[i5];
                    AccountTypePreference remove = arrayMap.remove(AccountTypePreference.buildKey(account));
                    if (remove != null) {
                        arrayList.add(remove);
                    } else if (AccountRestrictionHelper.showAccount(this.mAuthorities, authenticatorHelper.getAuthoritiesForAccountType(account.type))) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("account", account);
                        bundle.putParcelable("user_handle", userHandle2);
                        bundle.putString("account_type", str);
                        strArr2 = enabledAccountTypes;
                        bundle.putString("account_label", labelForType.toString());
                        bundle.putInt("account_title_res", labelIdForType);
                        bundle.putParcelable("android.intent.extra.USER", userHandle2);
                        i = i5;
                        i2 = length;
                        accountArr = accountsByTypeAsUser;
                        i3 = labelIdForType;
                        arrayList.add(new AccountTypePreference(context, this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment), account, packageForType, labelIdForType, labelForType, AccountDetailDashboardFragment.class.getName(), bundle, drawableForType));
                        i5 = i + 1;
                        userHandle2 = userHandle;
                        enabledAccountTypes = strArr2;
                        length = i2;
                        accountsByTypeAsUser = accountArr;
                        labelIdForType = i3;
                    }
                    strArr2 = enabledAccountTypes;
                    i = i5;
                    i2 = length;
                    accountArr = accountsByTypeAsUser;
                    i3 = labelIdForType;
                    i5 = i + 1;
                    userHandle2 = userHandle;
                    enabledAccountTypes = strArr2;
                    length = i2;
                    accountsByTypeAsUser = accountArr;
                    labelIdForType = i3;
                }
                strArr = enabledAccountTypes;
                authenticatorHelper.preloadDrawableForType(this.mContext, str);
            }
            i4++;
            userHandle2 = userHandle;
            enabledAccountTypes = strArr;
        }
        Collections.sort(arrayList, new Comparator<AccountTypePreference>() { // from class: com.android.settings.accounts.AccountPreferenceController.1
            @Override // java.util.Comparator
            public int compare(AccountTypePreference accountTypePreference, AccountTypePreference accountTypePreference2) {
                int compareTo = accountTypePreference.getSummary().toString().compareTo(accountTypePreference2.getSummary().toString());
                return compareTo != 0 ? compareTo : accountTypePreference.getTitle().toString().compareTo(accountTypePreference2.getTitle().toString());
            }
        });
        return arrayList;
    }

    private boolean accountTypeHasAnyRequestedAuthorities(AuthenticatorHelper authenticatorHelper, String str) {
        if (this.mAuthoritiesCount == 0) {
            return true;
        }
        ArrayList<String> authoritiesForAccountType = authenticatorHelper.getAuthoritiesForAccountType(str);
        if (authoritiesForAccountType == null) {
            Log.d("AccountPrefController", "No sync authorities for account type: " + str);
            return false;
        }
        for (int i = 0; i < this.mAuthoritiesCount; i++) {
            if (authoritiesForAccountType.contains(this.mAuthorities[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isSingleProfile() {
        return this.mUm.isLinkedUser() || this.mUm.getProfiles(UserHandle.myUserId()).size() == 1;
    }

    /* loaded from: classes.dex */
    private class ManagedProfileBroadcastReceiver extends BroadcastReceiver {
        private boolean mListeningToManagedProfileEvents;

        private ManagedProfileBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("AccountPrefController", "Received broadcast: " + action);
            if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED") || action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                if (AccountPreferenceController.this.mFragment instanceof AccountWorkProfileDashboardFragment) {
                    new SubSettingLauncher(context).setDestination(AccountDashboardFragment.class.getName()).setSourceMetricsCategory(AccountPreferenceController.this.mFragment.getMetricsCategory()).setTitleRes(-1).setIsSecondLayerPage(true).launch();
                    AccountPreferenceController.this.mFragment.getActivity().finish();
                    return;
                }
                AccountPreferenceController.this.stopListeningToAccountUpdates();
                AccountPreferenceController.this.updateUi();
                AccountPreferenceController.this.listenToAccountUpdates();
                return;
            }
            Log.w("AccountPrefController", "Cannot handle received broadcast: " + intent.getAction());
        }

        public void register(Context context) {
            if (this.mListeningToManagedProfileEvents) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            context.registerReceiver(this, intentFilter);
            this.mListeningToManagedProfileEvents = true;
        }

        public void unregister(Context context) {
            if (this.mListeningToManagedProfileEvents) {
                context.unregisterReceiver(this);
                this.mListeningToManagedProfileEvents = false;
            }
        }
    }
}
