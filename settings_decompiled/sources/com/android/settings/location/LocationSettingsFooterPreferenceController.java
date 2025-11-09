package com.android.settings.location;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class LocationSettingsFooterPreferenceController extends LocationBasePreferenceController {
    private static final Intent INJECT_INTENT = new Intent("com.android.settings.location.DISPLAYED_FOOTER");
    private static final String PARAGRAPH_SEPARATOR = "<br><br>";
    private static final String TAG = "LocationFooter";
    private FooterPreference mFooterPreference;
    private String mInjectedFooterString;
    private boolean mLocationEnabled;
    private final PackageManager mPackageManager;

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public LocationSettingsFooterPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mFooterPreference = (FooterPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mLocationEnabled = this.mLocationEnabler.isEnabled(i);
        updateFooterPreference();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        for (FooterData footerData : getFooterData()) {
            try {
                this.mInjectedFooterString = this.mPackageManager.getResourcesForApplication(footerData.applicationInfo).getString(footerData.footerStringRes);
                updateFooterPreference();
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w(TAG, "Resources not found for application " + footerData.applicationInfo.packageName);
            }
        }
    }

    private void updateFooterPreference() {
        String string = this.mContext.getString(R$string.location_settings_footer_general);
        if (this.mLocationEnabled) {
            if (!TextUtils.isEmpty(this.mInjectedFooterString)) {
                string = Html.escapeHtml(this.mInjectedFooterString) + PARAGRAPH_SEPARATOR + string;
            }
        } else {
            string = this.mContext.getString(R$string.location_settings_footer_location_off) + PARAGRAPH_SEPARATOR + string;
        }
        FooterPreference footerPreference = this.mFooterPreference;
        if (footerPreference != null) {
            footerPreference.setTitle(Html.fromHtml(string));
            this.mFooterPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.location.LocationSettingsFooterPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LocationSettingsFooterPreferenceController.this.lambda$updateFooterPreference$0(view);
                }
            });
            this.mFooterPreference.setLearnMoreText(this.mContext.getString(R$string.location_settings_footer_learn_more_content_description));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFooterPreference$0(View view) {
        openLocationLearnMoreLink();
    }

    private void openLocationLearnMoreLink() {
        DashboardFragment dashboardFragment = this.mFragment;
        Context context = this.mContext;
        dashboardFragment.startActivityForResult(HelpUtils.getHelpIntent(context, context.getString(R$string.location_settings_footer_learn_more_link), ""), 0);
    }

    private List<FooterData> getFooterData() {
        PackageManager packageManager = this.mPackageManager;
        Intent intent = INJECT_INTENT;
        List<ResolveInfo> queryBroadcastReceivers = packageManager.queryBroadcastReceivers(intent, 128);
        if (queryBroadcastReceivers == null) {
            Log.e(TAG, "Unable to resolve intent " + intent);
            return Collections.emptyList();
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Found broadcast receivers: " + queryBroadcastReceivers);
        }
        ArrayList arrayList = new ArrayList(queryBroadcastReceivers.size());
        for (ResolveInfo resolveInfo : queryBroadcastReceivers) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            if ((applicationInfo.flags & 1) == 0) {
                Log.w(TAG, "Ignoring attempt to inject footer from app not in system image: " + resolveInfo);
            } else {
                Bundle bundle = activityInfo.metaData;
                if (bundle == null) {
                    if (Log.isLoggable(TAG, 3)) {
                        Log.d(TAG, "No METADATA in broadcast receiver " + activityInfo.name);
                    }
                } else {
                    int i = bundle.getInt("com.android.settings.location.FOOTER_STRING");
                    if (i == 0) {
                        Log.w(TAG, "No mapping of integer exists for com.android.settings.location.FOOTER_STRING");
                    } else {
                        arrayList.add(new FooterData(i, applicationInfo));
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FooterData {
        public final ApplicationInfo applicationInfo;
        public final int footerStringRes;

        FooterData(int i, ApplicationInfo applicationInfo) {
            this.footerStringRes = i;
            this.applicationInfo = applicationInfo;
        }
    }
}
