package com.android.settings.datausage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datausage.AppStateDataUsageBridge;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AppSwitchPreference;
/* loaded from: classes.dex */
public class UnrestrictedDataAccessPreference extends AppSwitchPreference implements DataSaverBackend.Listener {
    private final ApplicationsState mApplicationsState;
    private Drawable mCacheIcon;
    private final DataSaverBackend mDataSaverBackend;
    private final AppStateDataUsageBridge.DataUsageState mDataUsageState;
    private final ApplicationsState.AppEntry mEntry;
    private final RestrictedPreferenceHelper mHelper;
    private final DashboardFragment mParentFragment;

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
    }

    public UnrestrictedDataAccessPreference(Context context, ApplicationsState.AppEntry appEntry, ApplicationsState applicationsState, DataSaverBackend dataSaverBackend, DashboardFragment dashboardFragment) {
        super(context);
        this.mHelper = new RestrictedPreferenceHelper(context, this, null);
        this.mEntry = appEntry;
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateDataUsageBridge.DataUsageState) {
            this.mDataUsageState = (AppStateDataUsageBridge.DataUsageState) obj;
        } else {
            this.mDataUsageState = null;
        }
        appEntry.ensureLabel(context);
        this.mApplicationsState = applicationsState;
        this.mDataSaverBackend = dataSaverBackend;
        this.mParentFragment = dashboardFragment;
        ApplicationInfo applicationInfo = appEntry.info;
        setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(context, applicationInfo.packageName, UserHandle.getUserId(applicationInfo.uid)));
        updateState();
        setKey(generateKey(appEntry));
        Drawable iconFromCache = AppUtils.getIconFromCache(appEntry);
        this.mCacheIcon = iconFromCache;
        if (iconFromCache != null) {
            setIcon(iconFromCache);
        } else {
            setIcon(R$drawable.empty_icon);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateKey(ApplicationsState.AppEntry appEntry) {
        return appEntry.info.packageName + "|" + appEntry.info.uid;
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDataSaverBackend.addListener(this);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        this.mDataSaverBackend.remListener(this);
        super.onDetached();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState != null && dataUsageState.isDataSaverDenylisted) {
            AppInfoDashboardFragment.startAppInfoFragment(AppDataUsage.class, R$string.data_usage_app_summary_title, null, this.mParentFragment, this.mEntry);
        } else {
            super.onClick();
        }
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (this.mHelper.performClick()) {
            return;
        }
        super.performClick();
    }

    @Override // com.android.settingslib.widget.AppSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mCacheIcon == null) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.datausage.UnrestrictedDataAccessPreference$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UnrestrictedDataAccessPreference.this.lambda$onBindViewHolder$1();
                }
            });
        }
        boolean isDisabledByAdmin = isDisabledByAdmin();
        View findViewById = preferenceViewHolder.findViewById(16908312);
        int i = 0;
        if (isDisabledByAdmin) {
            findViewById.setVisibility(0);
        } else {
            AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
            if (dataUsageState != null && dataUsageState.isDataSaverDenylisted) {
                i = 4;
            }
            findViewById.setVisibility(i);
        }
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1() {
        final Drawable icon = AppUtils.getIcon(getContext(), this.mEntry);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.datausage.UnrestrictedDataAccessPreference$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                UnrestrictedDataAccessPreference.this.lambda$onBindViewHolder$0(icon);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(Drawable drawable) {
        setIcon(drawable);
        this.mCacheIcon = drawable;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState == null || this.mEntry.info.uid != i) {
            return;
        }
        dataUsageState.isDataSaverAllowlisted = z;
        updateState();
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState == null || this.mEntry.info.uid != i) {
            return;
        }
        dataUsageState.isDataSaverDenylisted = z;
        updateState();
    }

    public AppStateDataUsageBridge.DataUsageState getDataUsageState() {
        return this.mDataUsageState;
    }

    public ApplicationsState.AppEntry getEntry() {
        return this.mEntry;
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mHelper.setDisabledByAdmin(enforcedAdmin);
    }

    public void updateState() {
        setTitle(this.mEntry.label);
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState != null) {
            setChecked(dataUsageState.isDataSaverAllowlisted);
            if (isDisabledByAdmin()) {
                setSummary(R$string.disabled_by_admin);
            } else if (this.mDataUsageState.isDataSaverDenylisted) {
                setSummary(R$string.restrict_background_blocklisted);
            } else {
                setSummary("");
            }
        }
        notifyChanged();
    }
}
