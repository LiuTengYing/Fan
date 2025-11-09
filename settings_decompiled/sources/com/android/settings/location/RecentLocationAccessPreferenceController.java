package com.android.settings.location;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.applications.RecentAppOpsAccess;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RecentLocationAccessPreferenceController extends LocationBasePreferenceController {
    public static final int MAX_APPS = 3;
    private PreferenceCategory mCategoryRecentLocationRequests;
    RecentAppOpsAccess mRecentLocationApps;
    private boolean mShowSystem;
    private boolean mSystemSettingChanged;
    private int mType;

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PackageEntryClickedListener implements Preference.OnPreferenceClickListener {
        private final Context mContext;
        private final String mPackage;
        private final UserHandle mUserHandle;

        PackageEntryClickedListener(Context context, String str, UserHandle userHandle) {
            this.mContext = context;
            this.mPackage = str;
            this.mUserHandle = userHandle;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSION");
            intent.putExtra("android.intent.extra.PERMISSION_GROUP_NAME", "android.permission-group.LOCATION");
            intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mPackage);
            intent.putExtra("android.intent.extra.USER", this.mUserHandle);
            this.mContext.startActivity(intent);
            return true;
        }
    }

    public RecentLocationAccessPreferenceController(Context context, String str) {
        this(context, str, RecentAppOpsAccess.createForLocation(context));
    }

    public RecentLocationAccessPreferenceController(Context context, String str, RecentAppOpsAccess recentAppOpsAccess) {
        super(context, str);
        this.mType = 3;
        boolean z = false;
        this.mShowSystem = false;
        this.mSystemSettingChanged = false;
        this.mRecentLocationApps = recentAppOpsAccess;
        if (DeviceConfig.getBoolean("privacy", "location_indicators_small_enabled", false) && Settings.Secure.getInt(this.mContext.getContentResolver(), "locationShowSystemOps", 0) == 1) {
            z = true;
        }
        this.mShowSystem = z;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mCategoryRecentLocationRequests = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mLocationEnabler.refreshLocationMode();
        loadRecentAccesses();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mSystemSettingChanged) {
            loadRecentAccesses();
            this.mSystemSettingChanged = false;
        }
    }

    private void loadRecentAccesses() {
        this.mCategoryRecentLocationRequests.removeAll();
        Context context = this.mCategoryRecentLocationRequests.getContext();
        ArrayList<RecentAppOpsAccess.Access> arrayList = new ArrayList();
        UserManager userManager = UserManager.get(this.mContext);
        for (RecentAppOpsAccess.Access access : this.mRecentLocationApps.getAppListSorted(this.mShowSystem)) {
            if (isRequestMatchesProfileType(userManager, access, this.mType)) {
                arrayList.add(access);
                if (arrayList.size() == 3) {
                    break;
                }
            }
        }
        if (arrayList.size() > 0) {
            for (RecentAppOpsAccess.Access access2 : arrayList) {
                this.mCategoryRecentLocationRequests.addPreference(createAppPreference(context, access2, this.mFragment));
            }
            return;
        }
        AppPreference appPreference = new AppPreference(context);
        appPreference.setTitle(R$string.location_no_recent_accesses);
        appPreference.setSelectable(false);
        this.mCategoryRecentLocationRequests.addPreference(appPreference);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mCategoryRecentLocationRequests.setVisible(this.mLocationEnabler.isEnabled(i));
    }

    public void clearPreferenceList() {
        PreferenceCategory preferenceCategory = this.mCategoryRecentLocationRequests;
        if (preferenceCategory != null) {
            preferenceCategory.removeAll();
        }
    }

    public void setProfileType(int i) {
        this.mType = i;
    }

    public static AppPreference createAppPreference(Context context, RecentAppOpsAccess.Access access, DashboardFragment dashboardFragment) {
        AppPreference appPreference = new AppPreference(context);
        appPreference.setIcon(access.icon);
        appPreference.setTitle(access.label);
        appPreference.setSummary(StringUtil.formatRelativeTime(context, System.currentTimeMillis() - access.accessFinishTime, false, RelativeDateTimeFormatter.Style.SHORT));
        appPreference.setOnPreferenceClickListener(new PackageEntryClickedListener(dashboardFragment.getContext(), access.packageName, access.userHandle));
        return appPreference;
    }

    public static boolean isRequestMatchesProfileType(UserManager userManager, RecentAppOpsAccess.Access access, int i) {
        boolean isManagedProfile = userManager.isManagedProfile(access.userHandle.getIdentifier());
        if (!isManagedProfile || (i & 2) == 0) {
            return (isManagedProfile || (i & 1) == 0) ? false : true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateShowSystem() {
        this.mSystemSettingChanged = true;
        this.mShowSystem = true ^ this.mShowSystem;
        clearPreferenceList();
        loadRecentAccesses();
    }
}
