package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.GrantedUriPermission;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.util.MutableInt;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.applications.AppStorageSizesController;
import com.android.settings.deviceinfo.StorageWizardMoveConfirm;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.widget.ActionButtonsPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class AppStorageSettings extends AppInfoWithHeader implements View.OnClickListener, DialogInterface.OnClickListener, LoaderManager.LoaderCallbacks<StorageStatsSource.AppStorageStats> {
    private static final String TAG = AppStorageSettings.class.getSimpleName();
    ActionButtonsPreference mButtonsPref;
    private boolean mCacheCleared;
    private VolumeInfo[] mCandidates;
    private Button mChangeStorageButton;
    private ClearCacheObserver mClearCacheObserver;
    private ClearUserDataObserver mClearDataObserver;
    private LayoutPreference mClearUri;
    private Button mClearUriButton;
    private boolean mDataCleared;
    private AlertDialog.Builder mDialogBuilder;
    private ApplicationInfo mInfo;
    AppStorageSizesController mSizeController;
    private Preference mStorageUsed;
    private PreferenceCategory mUri;
    private boolean mCanClearData = true;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.applications.AppStorageSettings.3
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (AppStorageSettings.this.getView() == null) {
                return;
            }
            int i = message.what;
            if (i == 1) {
                AppStorageSettings.this.mDataCleared = true;
                AppStorageSettings.this.mCacheCleared = true;
                AppStorageSettings.this.processClearMsg(message);
            } else if (i != 3) {
            } else {
                AppStorageSettings.this.mCacheCleared = true;
                AppStorageSettings.this.updateSize();
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 19;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<StorageStatsSource.AppStorageStats> loader) {
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            boolean z = false;
            this.mCacheCleared = bundle.getBoolean("cache_cleared", false);
            boolean z2 = bundle.getBoolean("data_cleared", false);
            this.mDataCleared = z2;
            this.mCacheCleared = (this.mCacheCleared || z2) ? true : true;
        }
        addPreferencesFromResource(R$xml.app_storage_settings);
        setupViews();
        initMoveDialog();
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateSize();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("cache_cleared", this.mCacheCleared);
        bundle.putBoolean("data_cleared", this.mDataCleared);
    }

    private void setupViews() {
        this.mSizeController = new AppStorageSizesController.Builder().setTotalSizePreference(findPreference("total_size")).setAppSizePreference(findPreference("app_size")).setDataSizePreference(findPreference("data_size")).setCacheSizePreference(findPreference("cache_size")).setComputingString(R$string.computing_size).setErrorString(R$string.invalid_size_value).build();
        this.mButtonsPref = (ActionButtonsPreference) findPreference("header_view");
        this.mStorageUsed = findPreference("storage_used");
        int i = R$id.button;
        Button button = (Button) ((LayoutPreference) findPreference("change_storage_button")).findViewById(i);
        this.mChangeStorageButton = button;
        button.setText(R$string.change);
        this.mChangeStorageButton.setOnClickListener(this);
        this.mButtonsPref.setButton2Text(R$string.clear_cache_btn_text).setButton2Icon(R$drawable.ic_settings_delete);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("uri_category");
        this.mUri = preferenceCategory;
        LayoutPreference layoutPreference = (LayoutPreference) preferenceCategory.findPreference("clear_uri_button");
        this.mClearUri = layoutPreference;
        Button button2 = (Button) layoutPreference.findViewById(i);
        this.mClearUriButton = button2;
        button2.setText(R$string.clear_uri_btn_text);
        this.mClearUriButton.setOnClickListener(this);
    }

    void handleClearCacheClick() {
        if (this.mAppsControlDisallowedAdmin != null && !this.mAppsControlDisallowedBySystem) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
            return;
        }
        if (this.mClearCacheObserver == null) {
            this.mClearCacheObserver = new ClearCacheObserver();
        }
        this.mMetricsFeatureProvider.action(getContext(), 877, new Pair[0]);
        this.mPm.deleteApplicationCacheFiles(this.mPackageName, this.mClearCacheObserver);
    }

    void handleClearDataClick() {
        if (this.mAppsControlDisallowedAdmin != null && !this.mAppsControlDisallowedBySystem) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
        } else if (this.mAppEntry.info.manageSpaceActivityName != null) {
            if (Utils.isMonkeyRunning()) {
                return;
            }
            Intent intent = new Intent("android.intent.action.VIEW");
            ApplicationInfo applicationInfo = this.mAppEntry.info;
            intent.setClassName(applicationInfo.packageName, applicationInfo.manageSpaceActivityName);
            startActivityForResult(intent, 2);
        } else {
            showDialogInner(1, 0);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mChangeStorageButton && this.mDialogBuilder != null && !isMoveInProgress()) {
            this.mDialogBuilder.show();
        } else if (view == this.mClearUriButton) {
            if (this.mAppsControlDisallowedAdmin != null && !this.mAppsControlDisallowedBySystem) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
            } else {
                clearUriPermissions();
            }
        }
    }

    private boolean isMoveInProgress() {
        try {
            AppGlobals.getPackageManager().checkPackageStartable(this.mPackageName, UserHandle.myUserId());
            return false;
        } catch (RemoteException | SecurityException unused) {
            return true;
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        FragmentActivity activity = getActivity();
        VolumeInfo volumeInfo = this.mCandidates[i];
        if (!Objects.equals(volumeInfo, activity.getPackageManager().getPackageCurrentVolume(this.mAppEntry.info))) {
            Intent intent = new Intent(activity, StorageWizardMoveConfirm.class);
            intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
            intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mAppEntry.info.packageName);
            startActivity(intent);
        }
        dialogInterface.dismiss();
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        retrieveAppEntry();
        if (this.mAppEntry == null) {
            return false;
        }
        updateUiWithSize(this.mSizeController.getLastResult());
        refreshGrantedUriPermissions();
        this.mStorageUsed.setSummary(((StorageManager) getContext().getSystemService(StorageManager.class)).getBestVolumeDescription(getActivity().getPackageManager().getPackageCurrentVolume(this.mAppEntry.info)));
        refreshButtons();
        return true;
    }

    private void refreshButtons() {
        initMoveDialog();
        initDataButtons();
    }

    private void initDataButtons() {
        boolean z = this.mAppEntry.info.manageSpaceActivityName != null;
        boolean z2 = ((this.mAppEntry.info.flags & 65) == 1) || this.mDpm.packageHasActiveAdmins(this.mPackageName);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (z) {
            ApplicationInfo applicationInfo = this.mAppEntry.info;
            intent.setClassName(applicationInfo.packageName, applicationInfo.manageSpaceActivityName);
        }
        boolean z3 = getPackageManager().resolveActivity(intent, 0) != null;
        if ((!z && z2) || !z3) {
            this.mButtonsPref.setButton1Text(R$string.clear_user_data_text).setButton1Icon(R$drawable.ic_settings_delete).setButton1Enabled(false);
            this.mCanClearData = false;
        } else {
            if (z) {
                this.mButtonsPref.setButton1Text(R$string.manage_space_text);
            } else {
                this.mButtonsPref.setButton1Text(R$string.clear_user_data_text);
            }
            this.mButtonsPref.setButton1Icon(R$drawable.ic_settings_delete).setButton1OnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.AppStorageSettings$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AppStorageSettings.this.lambda$initDataButtons$0(view);
                }
            });
        }
        if (this.mAppsControlDisallowedBySystem || AppUtils.isMainlineModule(this.mPm, this.mPackageName)) {
            this.mButtonsPref.setButton1Enabled(false);
        }
        if (UniMobileNetworkUtils.isAppDisableAllowed(getContext(), this.mAppEntry.info.packageName)) {
            return;
        }
        this.mButtonsPref.setButton1Enabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initDataButtons$0(View view) {
        handleClearDataClick();
    }

    private void initMoveDialog() {
        FragmentActivity activity = getActivity();
        StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        List packageCandidateVolumes = activity.getPackageManager().getPackageCandidateVolumes(this.mAppEntry.info);
        if (packageCandidateVolumes.size() > 1) {
            Collections.sort(packageCandidateVolumes, VolumeInfo.getDescriptionComparator());
            CharSequence[] charSequenceArr = new CharSequence[packageCandidateVolumes.size()];
            int i = -1;
            for (int i2 = 0; i2 < packageCandidateVolumes.size(); i2++) {
                String bestVolumeDescription = storageManager.getBestVolumeDescription((VolumeInfo) packageCandidateVolumes.get(i2));
                if (Objects.equals(bestVolumeDescription, this.mStorageUsed.getSummary())) {
                    i = i2;
                }
                charSequenceArr[i2] = bestVolumeDescription;
            }
            this.mCandidates = (VolumeInfo[]) packageCandidateVolumes.toArray(new VolumeInfo[packageCandidateVolumes.size()]);
            this.mDialogBuilder = new AlertDialog.Builder(getContext()).setTitle(R$string.change_storage).setSingleChoiceItems(charSequenceArr, i, this).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null);
            return;
        }
        removePreference("storage_used");
        removePreference("change_storage_button");
        removePreference("storage_space");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initiateClearUserData() {
        boolean z;
        this.mMetricsFeatureProvider.action(getContext(), 876, new Pair[0]);
        this.mButtonsPref.setButton1Enabled(false);
        String str = this.mAppEntry.info.packageName;
        String str2 = TAG;
        Log.i(str2, "Clearing user data for package : " + str);
        if (this.mClearDataObserver == null) {
            this.mClearDataObserver = new ClearUserDataObserver();
        }
        try {
            z = ((ActivityManager) getActivity().getSystemService("activity")).clearApplicationUserData(str, this.mClearDataObserver);
        } catch (SecurityException e) {
            String str3 = TAG;
            Log.i(str3, "Failed to clear application user data: " + e);
            z = false;
        }
        if (!z) {
            String str4 = TAG;
            Log.i(str4, "Couldn't clear application user data for package:" + str);
            showDialogInner(2, 0);
            return;
        }
        this.mButtonsPref.setButton1Text(R$string.recompute_size);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processClearMsg(Message message) {
        int i = message.arg1;
        String str = this.mAppEntry.info.packageName;
        this.mButtonsPref.setButton1Text(R$string.clear_user_data_text).setButton1Icon(R$drawable.ic_settings_delete);
        if (i == 1) {
            String str2 = TAG;
            Log.i(str2, "Cleared user data for package : " + str);
            updateSize();
            return;
        }
        this.mButtonsPref.setButton1Enabled(true);
    }

    private void refreshGrantedUriPermissions() {
        removeUriPermissionsFromUi();
        List<GrantedUriPermission> list = ((ActivityManager) getActivity().getSystemService("activity")).getGrantedUriPermissions(this.mAppEntry.info.packageName).getList();
        if (list.isEmpty()) {
            this.mClearUriButton.setVisibility(8);
            return;
        }
        PackageManager packageManager = getActivity().getPackageManager();
        TreeMap treeMap = new TreeMap();
        for (GrantedUriPermission grantedUriPermission : list) {
            ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(grantedUriPermission.uri.getAuthority(), 0);
            if (resolveContentProvider != null) {
                CharSequence loadLabel = resolveContentProvider.applicationInfo.loadLabel(packageManager);
                MutableInt mutableInt = (MutableInt) treeMap.get(loadLabel);
                if (mutableInt == null) {
                    treeMap.put(loadLabel, new MutableInt(1));
                } else {
                    mutableInt.value++;
                }
            }
        }
        for (Map.Entry entry : treeMap.entrySet()) {
            int i = ((MutableInt) entry.getValue()).value;
            Preference preference = new Preference(getPrefContext());
            preference.setTitle((CharSequence) entry.getKey());
            preference.setSummary(getPrefContext().getResources().getQuantityString(R$plurals.uri_permissions_text, i, Integer.valueOf(i)));
            preference.setSelectable(false);
            preference.setLayoutResource(R$layout.horizontal_preference);
            preference.setOrder(0);
            Log.v(TAG, "Adding preference '" + preference + "' at order 0");
            this.mUri.addPreference(preference);
        }
        if (this.mAppsControlDisallowedBySystem) {
            this.mClearUriButton.setEnabled(false);
        }
        this.mClearUri.setOrder(0);
        this.mClearUriButton.setVisibility(0);
    }

    private void clearUriPermissions() {
        FragmentActivity activity = getActivity();
        ((ActivityManager) activity.getSystemService("activity")).clearGrantedUriPermissions(this.mAppEntry.info.packageName);
        refreshGrantedUriPermissions();
    }

    private void removeUriPermissionsFromUi() {
        for (int preferenceCount = this.mUri.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
            Preference preference = this.mUri.getPreference(preferenceCount);
            if (preference != this.mClearUri) {
                this.mUri.removePreference(preference);
            }
        }
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        if (i != 1) {
            if (i != 2) {
                return null;
            }
            return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R$string.clear_user_data_text)).setMessage(getActivity().getText(R$string.clear_failed_dlg_text)).setNeutralButton(R$string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.AppStorageSettings.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i3) {
                    AppStorageSettings.this.mButtonsPref.setButton1Enabled(false);
                    AppStorageSettings.this.setIntentAndFinish(false);
                }
            }).create();
        }
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R$string.clear_data_dlg_title)).setMessage(getActivity().getText(R$string.clear_data_dlg_text)).setPositiveButton(R$string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.AppStorageSettings.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                AppStorageSettings.this.initiateClearUserData();
            }
        }).setNegativeButton(R$string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<StorageStatsSource.AppStorageStats> onCreateLoader(int i, Bundle bundle) {
        Context context = getContext();
        return new FetchPackageStorageAsyncLoader(context, new StorageStatsSource(context), this.mInfo, UserHandle.of(this.mUserId));
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<StorageStatsSource.AppStorageStats> loader, StorageStatsSource.AppStorageStats appStorageStats) {
        this.mSizeController.setResult(appStorageStats);
        updateUiWithSize(appStorageStats);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSize() {
        try {
            this.mInfo = getPackageManager().getApplicationInfo(this.mPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not find package", e);
        }
        if (this.mInfo == null) {
            return;
        }
        getLoaderManager().restartLoader(1, Bundle.EMPTY, this);
    }

    void updateUiWithSize(StorageStatsSource.AppStorageStats appStorageStats) {
        if (this.mCacheCleared) {
            this.mSizeController.setCacheCleared(true);
        }
        if (this.mDataCleared) {
            this.mSizeController.setDataCleared(true);
        }
        this.mSizeController.updateUi(getContext());
        if (appStorageStats == null) {
            this.mButtonsPref.setButton1Enabled(false).setButton2Enabled(false);
        } else {
            long cacheBytes = appStorageStats.getCacheBytes();
            if (appStorageStats.getDataBytes() - cacheBytes <= 0 || !this.mCanClearData || this.mDataCleared) {
                this.mButtonsPref.setButton1Enabled(false);
            } else {
                this.mButtonsPref.setButton1Enabled(true).setButton1OnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.AppStorageSettings$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AppStorageSettings.this.lambda$updateUiWithSize$1(view);
                    }
                });
            }
            if (cacheBytes <= 0 || this.mCacheCleared) {
                this.mButtonsPref.setButton2Enabled(false);
            } else {
                this.mButtonsPref.setButton2Enabled(true).setButton2OnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.AppStorageSettings$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AppStorageSettings.this.lambda$updateUiWithSize$2(view);
                    }
                });
            }
        }
        if (this.mAppsControlDisallowedBySystem || AppUtils.isMainlineModule(this.mPm, this.mPackageName)) {
            this.mButtonsPref.setButton1Enabled(false).setButton2Enabled(false);
        }
        if (UniMobileNetworkUtils.isAppDisableAllowed(getContext(), this.mAppEntry.info.packageName)) {
            return;
        }
        this.mButtonsPref.setButton1Enabled(false).setButton2Enabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateUiWithSize$1(View view) {
        handleClearDataClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateUiWithSize$2(View view) {
        handleClearCacheClick();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ClearCacheObserver extends IPackageDataObserver.Stub {
        ClearCacheObserver() {
        }

        public void onRemoveCompleted(String str, boolean z) {
            Message obtainMessage = AppStorageSettings.this.mHandler.obtainMessage(3);
            obtainMessage.arg1 = z ? 1 : 2;
            AppStorageSettings.this.mHandler.sendMessage(obtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ClearUserDataObserver extends IPackageDataObserver.Stub {
        ClearUserDataObserver() {
        }

        public void onRemoveCompleted(String str, boolean z) {
            Message obtainMessage = AppStorageSettings.this.mHandler.obtainMessage(1);
            obtainMessage.arg1 = z ? 1 : 2;
            AppStorageSettings.this.mHandler.sendMessage(obtainMessage);
        }
    }
}
