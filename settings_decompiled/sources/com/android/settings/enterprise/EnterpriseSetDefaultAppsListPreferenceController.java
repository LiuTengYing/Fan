package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.EnterpriseDefaultApps;
import com.android.settings.applications.UserAppInfo;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.users.UserFeatureProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class EnterpriseSetDefaultAppsListPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final ApplicationFeatureProvider mApplicationFeatureProvider;
    private List<EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>>> mApps;
    private final EnterprisePrivacyFeatureProvider mEnterprisePrivacyFeatureProvider;
    private final SettingsPreferenceFragment mParent;
    private final PackageManager mPm;
    private final UserFeatureProvider mUserFeatureProvider;
    private final UserManager mUserManager;
    private List<UserInfo> mUsers;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public EnterpriseSetDefaultAppsListPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, PackageManager packageManager) {
        super(context);
        this.mUsers = Collections.emptyList();
        this.mApps = Collections.emptyList();
        this.mPm = packageManager;
        this.mParent = settingsPreferenceFragment;
        FeatureFactory factory = FeatureFactory.getFactory(context);
        this.mApplicationFeatureProvider = factory.getApplicationFeatureProvider(context);
        this.mEnterprisePrivacyFeatureProvider = factory.getEnterprisePrivacyFeatureProvider(context);
        this.mUserFeatureProvider = factory.getUserFeatureProvider(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        buildAppList();
    }

    private void buildAppList() {
        EnterpriseDefaultApps[] values;
        this.mUsers = new ArrayList();
        this.mApps = new ArrayList();
        for (UserHandle userHandle : this.mUserFeatureProvider.getUserProfiles()) {
            if (!Utils.isCloneProfile(userHandle, this.mUserManager)) {
                EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>> enumMap = null;
                boolean z = false;
                for (EnterpriseDefaultApps enterpriseDefaultApps : EnterpriseDefaultApps.values()) {
                    List<UserAppInfo> findPersistentPreferredActivities = this.mApplicationFeatureProvider.findPersistentPreferredActivities(userHandle.getIdentifier(), enterpriseDefaultApps.getIntents());
                    if (!findPersistentPreferredActivities.isEmpty()) {
                        if (!z) {
                            this.mUsers.add(findPersistentPreferredActivities.get(0).userInfo);
                            enumMap = new EnumMap<>(EnterpriseDefaultApps.class);
                            this.mApps.add(enumMap);
                            z = true;
                        }
                        ArrayList arrayList = new ArrayList();
                        for (UserAppInfo userAppInfo : findPersistentPreferredActivities) {
                            arrayList.add(userAppInfo.appInfo);
                        }
                        enumMap.put((EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>>) enterpriseDefaultApps, (EnterpriseDefaultApps) arrayList);
                    }
                }
            }
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.enterprise.EnterpriseSetDefaultAppsListPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EnterpriseSetDefaultAppsListPreferenceController.this.lambda$buildAppList$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateUi */
    public void lambda$buildAppList$0() {
        Context context = this.mParent.getPreferenceManager().getContext();
        PreferenceGroup preferenceScreen = this.mParent.getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }
        if (!this.mEnterprisePrivacyFeatureProvider.isInCompMode() && this.mUsers.size() == 1) {
            createPreferences(context, preferenceScreen, this.mApps.get(0));
            return;
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
        for (int i = 0; i < this.mUsers.size(); i++) {
            PreferenceGroup preferenceCategory = new PreferenceCategory(context);
            preferenceScreen.addPreference(preferenceCategory);
            if (this.mUsers.get(i).isManagedProfile()) {
                preferenceCategory.setTitle(devicePolicyManager.getResources().getString("Settings.WORK_CATEGORY_HEADER", new Supplier() { // from class: com.android.settings.enterprise.EnterpriseSetDefaultAppsListPreferenceController$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateUi$1;
                        lambda$updateUi$1 = EnterpriseSetDefaultAppsListPreferenceController.this.lambda$updateUi$1();
                        return lambda$updateUi$1;
                    }
                }));
            } else {
                preferenceCategory.setTitle(devicePolicyManager.getResources().getString("Settings.PERSONAL_CATEGORY_HEADER", new Supplier() { // from class: com.android.settings.enterprise.EnterpriseSetDefaultAppsListPreferenceController$$ExternalSyntheticLambda2
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateUi$2;
                        lambda$updateUi$2 = EnterpriseSetDefaultAppsListPreferenceController.this.lambda$updateUi$2();
                        return lambda$updateUi$2;
                    }
                }));
            }
            preferenceCategory.setOrder(i);
            createPreferences(context, preferenceCategory, this.mApps.get(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateUi$1() {
        return this.mContext.getString(R$string.category_work);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateUi$2() {
        return this.mContext.getString(R$string.category_personal);
    }

    private void createPreferences(Context context, PreferenceGroup preferenceGroup, EnumMap<EnterpriseDefaultApps, List<ApplicationInfo>> enumMap) {
        EnterpriseDefaultApps[] values;
        if (preferenceGroup == null) {
            return;
        }
        for (EnterpriseDefaultApps enterpriseDefaultApps : EnterpriseDefaultApps.values()) {
            List<ApplicationInfo> list = enumMap.get(enterpriseDefaultApps);
            if (list != null && !list.isEmpty()) {
                Preference preference = new Preference(context);
                preference.setTitle(getTitle(context, enterpriseDefaultApps, list.size()));
                preference.setSummary(buildSummaryString(context, list));
                preference.setOrder(enterpriseDefaultApps.ordinal());
                preference.setSelectable(false);
                preferenceGroup.addPreference(preference);
            }
        }
    }

    private CharSequence buildSummaryString(Context context, List<ApplicationInfo> list) {
        Object[] objArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            objArr[i] = list.get(i).loadLabel(this.mPm);
        }
        if (list.size() == 1) {
            return objArr[0];
        }
        return list.size() == 2 ? context.getString(R$string.app_names_concatenation_template_2, objArr[0], objArr[1]) : context.getString(R$string.app_names_concatenation_template_3, objArr[0], objArr[1], objArr[2]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.enterprise.EnterpriseSetDefaultAppsListPreferenceController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps;

        static {
            int[] iArr = new int[EnterpriseDefaultApps.values().length];
            $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps = iArr;
            try {
                iArr[EnterpriseDefaultApps.BROWSER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.CALENDAR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.CONTACTS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.PHONE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.MAP.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.EMAIL.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[EnterpriseDefaultApps.CAMERA.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    private String getTitle(Context context, EnterpriseDefaultApps enterpriseDefaultApps, int i) {
        switch (AnonymousClass1.$SwitchMap$com$android$settings$applications$EnterpriseDefaultApps[enterpriseDefaultApps.ordinal()]) {
            case 1:
                return context.getString(R$string.default_browser_title);
            case 2:
                return context.getString(R$string.default_calendar_app_title);
            case 3:
                return context.getString(R$string.default_contacts_app_title);
            case 4:
                return context.getResources().getQuantityString(R$plurals.default_phone_app_title, i);
            case 5:
                return context.getString(R$string.default_map_app_title);
            case 6:
                return context.getResources().getQuantityString(R$plurals.default_email_app_title, i);
            case 7:
                return context.getResources().getQuantityString(R$plurals.default_camera_app_title, i);
            default:
                throw new IllegalStateException("Unknown type of default " + enterpriseDefaultApps);
        }
    }
}
