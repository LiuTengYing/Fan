package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppLocaleUtil;
import com.android.settings.localepicker.AppLocalePickerActivity;
import java.util.List;
/* loaded from: classes.dex */
public class AppLocalePreferenceController extends AppInfoPreferenceControllerBase {
    private static final String TAG = "AppLocalePreferenceController";
    private final List<ResolveInfo> mListInfos;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppLocalePreferenceController(Context context, String str) {
        super(context, str);
        this.mListInfos = context.getPackageManager().queryIntentActivities(AppLocaleUtil.LAUNCHER_ENTRY_INTENT, 128);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (FeatureFlagUtils.isEnabled(this.mContext, "settings_app_language_selection") && canDisplayLocaleUi()) ? 0 : 2;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    protected Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppLocaleDetails.class;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return AppLocaleDetails.getSummary(this.mContext, this.mParent.getAppEntry());
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), this.mPreferenceKey)) {
            if (this.mParent != null) {
                Intent intent = new Intent(this.mContext, AppLocalePickerActivity.class);
                intent.setData(Uri.parse("package:" + this.mParent.getAppEntry().info.packageName));
                intent.putExtra("uid", this.mParent.getAppEntry().info.uid);
                this.mContext.startActivity(intent);
                return true;
            }
            Log.d(TAG, "mParent is null");
            return false;
        }
        return false;
    }

    boolean canDisplayLocaleUi() {
        return AppLocaleUtil.canDisplayLocaleUi(this.mContext, this.mParent.getAppEntry().info.packageName, this.mListInfos);
    }
}
