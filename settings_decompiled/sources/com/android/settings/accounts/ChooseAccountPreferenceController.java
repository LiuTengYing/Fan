package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class ChooseAccountPreferenceController extends BasePreferenceController {
    private static final String TAG = "ChooseAccountPrefCtrler";
    private Map<String, List<String>> mAccountTypeToAuthorities;
    private Set<String> mAccountTypesFilter;
    private Activity mActivity;
    private AuthenticatorDescription[] mAuthDescs;
    private String[] mAuthorities;
    private final List<ProviderEntry> mProviderList;
    private PreferenceScreen mScreen;
    private final Map<String, AuthenticatorDescription> mTypeToAuthDescription;
    private UserHandle mUserHandle;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ChooseAccountPreferenceController(Context context, String str) {
        super(context, str);
        this.mProviderList = new ArrayList();
        this.mTypeToAuthDescription = new HashMap();
    }

    public void initialize(String[] strArr, String[] strArr2, UserHandle userHandle, Activity activity) {
        this.mActivity = activity;
        this.mAuthorities = strArr;
        this.mUserHandle = userHandle;
        if (strArr2 != null) {
            this.mAccountTypesFilter = new HashSet();
            for (String str : strArr2) {
                this.mAccountTypesFilter.add(str);
            }
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        updateAuthDescriptions();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference instanceof ProviderPreference) {
            ProviderPreference providerPreference = (ProviderPreference) preference;
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Attempting to add account of type " + providerPreference.getAccountType());
            }
            finishWithAccountType(providerPreference.getAccountType());
            return true;
        }
        return false;
    }

    private void updateAuthDescriptions() {
        ArrayList arrayList = new ArrayList();
        this.mAuthDescs = AccountManager.get(this.mContext).getAuthenticatorTypesAsUser(this.mUserHandle.getIdentifier());
        int i = 0;
        while (true) {
            AuthenticatorDescription[] authenticatorDescriptionArr = this.mAuthDescs;
            if (i < authenticatorDescriptionArr.length) {
                if (AccountPreferenceController.filterSprdAccount(AccountPreferenceController.SPRD_ACCOUNTS, authenticatorDescriptionArr[i].type)) {
                    Log.d(TAG, "mAuthDescs[i].type = " + this.mAuthDescs[i].type);
                } else {
                    arrayList.add(this.mAuthDescs[i]);
                    Map<String, AuthenticatorDescription> map = this.mTypeToAuthDescription;
                    AuthenticatorDescription authenticatorDescription = this.mAuthDescs[i];
                    map.put(authenticatorDescription.type, authenticatorDescription);
                }
                i++;
            } else {
                this.mAuthDescs = (AuthenticatorDescription[]) arrayList.toArray(new AuthenticatorDescription[0]);
                onAuthDescriptionsUpdated();
                return;
            }
        }
    }

    private void onAuthDescriptionsUpdated() {
        Set<String> set;
        int i = 0;
        while (true) {
            AuthenticatorDescription[] authenticatorDescriptionArr = this.mAuthDescs;
            boolean z = true;
            if (i >= authenticatorDescriptionArr.length) {
                break;
            }
            String str = authenticatorDescriptionArr[i].type;
            CharSequence labelForType = getLabelForType(str);
            List<String> authoritiesForAccountType = getAuthoritiesForAccountType(str);
            String[] strArr = this.mAuthorities;
            if (strArr != null && strArr.length > 0 && authoritiesForAccountType != null) {
                int i2 = 0;
                while (true) {
                    String[] strArr2 = this.mAuthorities;
                    if (i2 >= strArr2.length) {
                        z = false;
                        break;
                    } else if (authoritiesForAccountType.contains(strArr2[i2])) {
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            if (z && (set = this.mAccountTypesFilter) != null && !set.contains(str)) {
                z = false;
            }
            if (z) {
                this.mProviderList.add(new ProviderEntry(labelForType, str));
            } else if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Skipped pref " + ((Object) labelForType) + ": has no authority we need");
            }
            i++;
        }
        Context context = this.mScreen.getContext();
        if (this.mProviderList.size() == 1) {
            RestrictedLockUtils.EnforcedAdmin checkIfAccountManagementDisabled = RestrictedLockUtilsInternal.checkIfAccountManagementDisabled(context, this.mProviderList.get(0).getType(), this.mUserHandle.getIdentifier());
            if (checkIfAccountManagementDisabled != null) {
                this.mActivity.setResult(0, RestrictedLockUtils.getShowAdminSupportDetailsIntent(context, checkIfAccountManagementDisabled));
                this.mActivity.finish();
                return;
            }
            finishWithAccountType(this.mProviderList.get(0).getType());
        } else if (this.mProviderList.size() > 0) {
            Collections.sort(this.mProviderList);
            for (ProviderEntry providerEntry : this.mProviderList) {
                ProviderPreference providerPreference = new ProviderPreference(context, providerEntry.getType(), getDrawableForType(providerEntry.getType()), providerEntry.getName());
                providerPreference.setKey(providerEntry.getType().toString());
                providerPreference.checkAccountManagementAndSetDisabled(this.mUserHandle.getIdentifier());
                this.mScreen.addPreference(providerPreference);
            }
        } else {
            if (Log.isLoggable(TAG, 2)) {
                StringBuilder sb = new StringBuilder();
                for (String str2 : this.mAuthorities) {
                    sb.append(str2);
                    sb.append(' ');
                }
                Log.v(TAG, "No providers found for authorities: " + ((Object) sb));
            }
            this.mActivity.setResult(0);
            this.mActivity.finish();
        }
    }

    private List<String> getAuthoritiesForAccountType(String str) {
        SyncAdapterType[] syncAdapterTypesAsUser;
        if (this.mAccountTypeToAuthorities == null) {
            this.mAccountTypeToAuthorities = Maps.newHashMap();
            for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier())) {
                List<String> list = this.mAccountTypeToAuthorities.get(syncAdapterType.accountType);
                if (list == null) {
                    list = new ArrayList<>();
                    this.mAccountTypeToAuthorities.put(syncAdapterType.accountType, list);
                }
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, "added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
                }
                list.add(syncAdapterType.authority);
            }
        }
        return this.mAccountTypeToAuthorities.get(str);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x005c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:11:0x005d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    android.graphics.drawable.Drawable getDrawableForType(java.lang.String r7) {
        /*
            r6 = this;
            java.lang.String r0 = "ChooseAccountPrefCtrler"
            java.util.Map<java.lang.String, android.accounts.AuthenticatorDescription> r1 = r6.mTypeToAuthDescription
            boolean r1 = r1.containsKey(r7)
            if (r1 == 0) goto L59
            java.util.Map<java.lang.String, android.accounts.AuthenticatorDescription> r1 = r6.mTypeToAuthDescription     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            java.lang.Object r1 = r1.get(r7)     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.accounts.AuthenticatorDescription r1 = (android.accounts.AuthenticatorDescription) r1     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.app.Activity r2 = r6.mActivity     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            java.lang.String r3 = r1.packageName     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            r4 = 0
            android.os.UserHandle r5 = r6.mUserHandle     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.content.Context r2 = r2.createPackageContextAsUser(r3, r4, r5)     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.content.Context r3 = r6.mContext     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            int r1 = r1.iconId     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.graphics.drawable.Drawable r1 = r2.getDrawable(r1)     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.os.UserHandle r2 = r6.mUserHandle     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            android.graphics.drawable.Drawable r7 = r3.getUserBadgedIcon(r1, r2)     // Catch: android.content.res.Resources.NotFoundException -> L30 android.content.pm.PackageManager.NameNotFoundException -> L45
            goto L5a
        L30:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "No icon resource for account type "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r7 = r1.toString()
            android.util.Log.w(r0, r7)
            goto L59
        L45:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "No icon name for account type "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r7 = r1.toString()
            android.util.Log.w(r0, r7)
        L59:
            r7 = 0
        L5a:
            if (r7 == 0) goto L5d
            return r7
        L5d:
            android.content.Context r6 = r6.mContext
            android.content.pm.PackageManager r6 = r6.getPackageManager()
            android.graphics.drawable.Drawable r6 = r6.getDefaultActivityIcon()
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.accounts.ChooseAccountPreferenceController.getDrawableForType(java.lang.String):android.graphics.drawable.Drawable");
    }

    CharSequence getLabelForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                return this.mActivity.createPackageContextAsUser(authenticatorDescription.packageName, 0, this.mUserHandle).getResources().getText(authenticatorDescription.labelId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w(TAG, "No label name for account type " + str);
            } catch (Resources.NotFoundException unused2) {
                Log.w(TAG, "No label resource for account type " + str);
            }
        }
        return null;
    }

    private void finishWithAccountType(String str) {
        Intent intent = new Intent();
        intent.putExtra("selected_account", str);
        intent.putExtra("android.intent.extra.USER", this.mUserHandle);
        this.mActivity.setResult(-1, intent);
        this.mActivity.finish();
    }
}
