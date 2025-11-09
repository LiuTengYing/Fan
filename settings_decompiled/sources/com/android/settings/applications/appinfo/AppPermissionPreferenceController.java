package com.android.settings.applications.appinfo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.icu.text.ListFormatter;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settingslib.applications.PermissionsSummaryHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/* loaded from: classes.dex */
public class AppPermissionPreferenceController extends AppInfoPreferenceControllerBase implements LifecycleObserver, OnStart, OnStop {
    private static final String EXTRA_HIDE_INFO_BUTTON = "hideInfoButton";
    private static final long INVALID_SESSION_ID = 0;
    private static final String TAG = "PermissionPrefControl";
    private final PackageManager.OnPermissionsChangedListener mOnPermissionsChangedListener;
    private final PackageManager mPackageManager;
    private String mPackageName;
    final PermissionsSummaryHelper.PermissionsResultCallback mPermissionCallback;

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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        updateState(this.mPreference);
    }

    public AppPermissionPreferenceController(Context context, String str) {
        super(context, str);
        this.mPermissionCallback = new PermissionsSummaryHelper.PermissionsResultCallback() { // from class: com.android.settings.applications.appinfo.AppPermissionPreferenceController.1
            @Override // com.android.settingslib.applications.PermissionsSummaryHelper.PermissionsResultCallback
            public void onPermissionSummaryResult(int i, int i2, int i3, List<CharSequence> list) {
                String format;
                Resources resources = ((AbstractPreferenceController) AppPermissionPreferenceController.this).mContext.getResources();
                if (i2 == 0) {
                    format = resources.getString(R$string.runtime_permissions_summary_no_permissions_requested);
                    AppPermissionPreferenceController.this.mPreference.setEnabled(false);
                } else {
                    ArrayList arrayList = new ArrayList(list);
                    if (i3 > 0) {
                        arrayList.add(resources.getQuantityString(R$plurals.runtime_permissions_additional_count, i3, Integer.valueOf(i3)));
                    }
                    if (arrayList.size() == 0) {
                        format = resources.getString(R$string.runtime_permissions_summary_no_permissions_granted);
                    } else {
                        format = ListFormatter.getInstance().format(arrayList);
                    }
                    AppPermissionPreferenceController.this.mPreference.setEnabled(true);
                }
                AppPermissionPreferenceController.this.mPreference.setSummary(format);
            }
        };
        this.mOnPermissionsChangedListener = new PackageManager.OnPermissionsChangedListener() { // from class: com.android.settings.applications.appinfo.AppPermissionPreferenceController$$ExternalSyntheticLambda0
            public final void onPermissionsChanged(int i) {
                AppPermissionPreferenceController.this.lambda$new$0(i);
            }
        };
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mPackageManager.addOnPermissionsChangeListener(this.mOnPermissionsChangedListener);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mPackageManager.removeOnPermissionsChangeListener(this.mOnPermissionsChangedListener);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PermissionsSummaryHelper.getPermissionSummary(this.mContext, this.mPackageName, this.mPermissionCallback);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            startManagePermissionsActivity();
            return true;
        }
        return false;
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    private void startManagePermissionsActivity() {
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mParent.getAppEntry().info.packageName);
        intent.putExtra(EXTRA_HIDE_INFO_BUTTON, true);
        FragmentActivity activity = this.mParent.getActivity();
        Intent intent2 = activity != null ? activity.getIntent() : null;
        if (intent2 != null) {
            String action = intent2.getAction();
            long longExtra = intent2.getLongExtra("android.intent.action.AUTO_REVOKE_PERMISSIONS", INVALID_SESSION_ID);
            if ((action != null && action.equals("android.intent.action.AUTO_REVOKE_PERMISSIONS")) || longExtra != INVALID_SESSION_ID) {
                while (longExtra == INVALID_SESSION_ID) {
                    longExtra = new Random().nextLong();
                }
                intent.putExtra("android.intent.action.AUTO_REVOKE_PERMISSIONS", longExtra);
            }
        }
        if (activity != null) {
            try {
                activity.startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException unused) {
                Log.w(TAG, "No app can handle android.intent.action.MANAGE_APP_PERMISSIONS");
            }
        }
    }
}
