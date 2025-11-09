package com.android.settings.applications.specialaccess.notificationaccess;

import android.companion.ICompanionDeviceManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.IconDrawableFactory;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class HeaderPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private LocalBluetoothManager mBm;
    private ICompanionDeviceManager mCdm;
    private ComponentName mCn;
    private DashboardFragment mFragment;
    private EntityHeaderController mHeaderController;
    private PackageInfo mPackageInfo;
    private PackageManager mPm;
    private CharSequence mServiceName;
    private int mUserId;

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

    public HeaderPreferenceController(Context context, String str) {
        super(context, str);
    }

    public HeaderPreferenceController setFragment(DashboardFragment dashboardFragment) {
        this.mFragment = dashboardFragment;
        return this;
    }

    public HeaderPreferenceController setPackageInfo(PackageInfo packageInfo) {
        this.mPackageInfo = packageInfo;
        return this;
    }

    public HeaderPreferenceController setPm(PackageManager packageManager) {
        this.mPm = packageManager;
        return this;
    }

    public HeaderPreferenceController setServiceName(CharSequence charSequence) {
        this.mServiceName = charSequence;
        return this;
    }

    public HeaderPreferenceController setCdm(ICompanionDeviceManager iCompanionDeviceManager) {
        this.mCdm = iCompanionDeviceManager;
        return this;
    }

    public HeaderPreferenceController setBluetoothManager(LocalBluetoothManager localBluetoothManager) {
        this.mBm = localBluetoothManager;
        return this;
    }

    public HeaderPreferenceController setCn(ComponentName componentName) {
        this.mCn = componentName;
        return this;
    }

    public HeaderPreferenceController setUserId(int i) {
        this.mUserId = i;
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (this.mFragment == null) {
            return;
        }
        FragmentActivity activity = this.mFragment.getActivity();
        DashboardFragment dashboardFragment = this.mFragment;
        int i = R$id.entity_header;
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, dashboardFragment, ((LayoutPreference) preferenceScreen.findPreference(getPreferenceKey())).findViewById(i));
        this.mHeaderController = newInstance;
        EntityHeaderController summary = newInstance.setRecyclerView(this.mFragment.getListView(), this.mFragment.getSettingsLifecycle()).setIcon(IconDrawableFactory.newInstance(this.mFragment.getActivity()).getBadgedIcon(this.mPackageInfo.applicationInfo)).setLabel(this.mPackageInfo.applicationInfo.loadLabel(this.mPm)).setSummary(this.mServiceName);
        new NotificationBackend();
        summary.setSecondSummary(NotificationBackend.getDeviceList(this.mCdm, this.mBm, this.mCn.getPackageName(), this.mUserId)).setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo)).setPackageName(this.mPackageInfo.packageName).setUid(this.mPackageInfo.applicationInfo.uid).setHasAppInfoLink(true).setButtonActions(0, 0).done(this.mFragment.getActivity(), this.mContext).findViewById(i).setVisibility(0);
    }
}
