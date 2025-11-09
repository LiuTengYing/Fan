package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.SearchIndexableResource;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.StorageDashboardFragment;
import com.android.settings.deviceinfo.storage.AutomaticStorageManagementSwitchPreferenceController;
import com.android.settings.deviceinfo.storage.ManageStoragePreferenceController;
import com.android.settings.deviceinfo.storage.SecondaryUserController;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.StorageCacheHelper;
import com.android.settings.deviceinfo.storage.StorageDataUseController;
import com.android.settings.deviceinfo.storage.StorageEntry;
import com.android.settings.deviceinfo.storage.StorageItemPreferenceController;
import com.android.settings.deviceinfo.storage.StorageUtils;
import com.android.settings.deviceinfo.storage.UserIconLoader;
import com.android.settings.deviceinfo.storage.VolumeSizesLoader;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class StorageDashboardFragment extends DashboardFragment implements LoaderManager.LoaderCallbacks<SparseArray<StorageAsyncLoader.StorageResult>> {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.storage_dashboard_fragment;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new StorageItemPreferenceController(context, null, null, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)), false));
            arrayList.addAll(SecondaryUserController.getSecondaryUserControllers(context, (UserManager) context.getSystemService(UserManager.class), false));
            return arrayList;
        }
    };
    private SparseArray<StorageAsyncLoader.StorageResult> mAppsResult;
    private boolean mIsLoadedFromCache;
    private boolean mIsWorkProfile;
    private VolumeOptionMenuController mOptionMenuController;
    private StorageItemPreferenceController mPreferenceController;
    private List<AbstractPreferenceController> mSecondaryUsers;
    private StorageEntry mSelectedStorageEntry;
    private StorageCacheHelper mStorageCacheHelper;
    private PrivateStorageInfo mStorageInfo;
    private StorageManager mStorageManager;
    private int mUserId;
    private UserManager mUserManager;
    private StorageDataUseController storageDataUseController;
    private final List<StorageEntry> mStorageEntries = new ArrayList();
    private boolean isInMultiWindow = false;
    private final StorageEventListener mStorageEventListener = new AnonymousClass1();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "StorageDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 745;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<SparseArray<StorageAsyncLoader.StorageResult>> loader) {
    }

    /* renamed from: com.android.settings.deviceinfo.StorageDashboardFragment$1  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass1 extends StorageEventListener {
        AnonymousClass1() {
        }

        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (StorageUtils.isStorageSettingsInterestedVolume(volumeInfo)) {
                final StorageEntry storageEntry = new StorageEntry(StorageDashboardFragment.this.getContext(), volumeInfo);
                int state = volumeInfo.getState();
                if (state == 0 || state == 2 || state == 3 || state == 5 || state == 6 || ((state == 7 || state == 8) && StorageDashboardFragment.this.mStorageEntries.remove(storageEntry))) {
                    StorageDashboardFragment.this.mStorageEntries.removeIf(new Predicate() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$1$$ExternalSyntheticLambda1
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$onVolumeStateChanged$0;
                            lambda$onVolumeStateChanged$0 = StorageDashboardFragment.AnonymousClass1.lambda$onVolumeStateChanged$0(StorageEntry.this, (StorageEntry) obj);
                            return lambda$onVolumeStateChanged$0;
                        }
                    });
                    if (state != 7 && state != 8) {
                        StorageDashboardFragment.this.mStorageEntries.add(storageEntry);
                    }
                    if (storageEntry.equals(StorageDashboardFragment.this.mSelectedStorageEntry)) {
                        StorageDashboardFragment.this.mSelectedStorageEntry = storageEntry;
                    }
                    if (state == 2) {
                        StorageDashboardFragment.this.setLoading(true, false);
                    }
                    if (state == 5) {
                        StorageDashboardFragment.this.getActivity().closeOptionsMenu();
                    }
                    StorageDashboardFragment.this.refreshUi();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onVolumeStateChanged$0(StorageEntry storageEntry, StorageEntry storageEntry2) {
            return storageEntry2.equals(storageEntry);
        }

        public void onVolumeRecordChanged(final VolumeRecord volumeRecord) {
            if (StorageUtils.isVolumeRecordMissed(StorageDashboardFragment.this.mStorageManager, volumeRecord)) {
                StorageEntry storageEntry = new StorageEntry(volumeRecord);
                if (StorageDashboardFragment.this.mStorageEntries.contains(storageEntry)) {
                    return;
                }
                StorageDashboardFragment.this.mStorageEntries.add(storageEntry);
                StorageDashboardFragment.this.refreshUi();
                return;
            }
            VolumeInfo findVolumeByUuid = StorageDashboardFragment.this.mStorageManager.findVolumeByUuid(volumeRecord.getFsUuid());
            if (findVolumeByUuid != null && StorageDashboardFragment.this.mStorageEntries.removeIf(new Predicate() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onVolumeRecordChanged$1;
                    lambda$onVolumeRecordChanged$1 = StorageDashboardFragment.AnonymousClass1.lambda$onVolumeRecordChanged$1(volumeRecord, (StorageEntry) obj);
                    return lambda$onVolumeRecordChanged$1;
                }
            })) {
                StorageDashboardFragment.this.mStorageEntries.add(new StorageEntry(StorageDashboardFragment.this.getContext(), findVolumeByUuid));
                StorageDashboardFragment.this.refreshUi();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onVolumeRecordChanged$1(VolumeRecord volumeRecord, StorageEntry storageEntry) {
            return storageEntry.isVolumeInfo() && TextUtils.equals(storageEntry.getFsUuid(), volumeRecord.getFsUuid());
        }

        public void onVolumeForgotten(String str) {
            StorageEntry storageEntry = new StorageEntry(new VolumeRecord(0, str));
            if (StorageDashboardFragment.this.mStorageEntries.remove(storageEntry)) {
                if (StorageDashboardFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    StorageDashboardFragment storageDashboardFragment = StorageDashboardFragment.this;
                    storageDashboardFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(storageDashboardFragment.getContext());
                }
                StorageDashboardFragment.this.refreshUi();
            }
        }

        public void onDiskScanned(DiskInfo diskInfo, int i) {
            if (StorageUtils.isDiskUnsupported(diskInfo)) {
                StorageEntry storageEntry = new StorageEntry(diskInfo);
                if (StorageDashboardFragment.this.mStorageEntries.contains(storageEntry)) {
                    return;
                }
                StorageDashboardFragment.this.mStorageEntries.add(storageEntry);
                StorageDashboardFragment.this.refreshUi();
            }
        }

        public void onDiskDestroyed(DiskInfo diskInfo) {
            StorageDashboardFragment.this.getActivity().closeOptionsMenu();
            StorageEntry storageEntry = new StorageEntry(diskInfo);
            if (StorageDashboardFragment.this.mStorageEntries.remove(storageEntry)) {
                if (StorageDashboardFragment.this.mSelectedStorageEntry.equals(storageEntry)) {
                    StorageDashboardFragment storageDashboardFragment = StorageDashboardFragment.this;
                    storageDashboardFragment.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(storageDashboardFragment.getContext());
                }
                StorageDashboardFragment.this.refreshUi();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshUi() {
        updateSelectedStorageEntry();
        this.mStorageEntries.clear();
        this.mStorageEntries.addAll(StorageUtils.getAllStorageEntries(getContext(), this.mStorageManager));
        this.mOptionMenuController.setSelectedStorageEntry(this.mSelectedStorageEntry);
        getActivity().invalidateOptionsMenu();
        if (this.mSelectedStorageEntry.isPrivate()) {
            getActivity().closeOptionsMenu();
        }
        if (!getSecondaryUsersVisibleStatus()) {
            setSecondaryUsersVisible(false);
        }
        if (!this.mSelectedStorageEntry.isMounted()) {
            this.mPreferenceController.setVolume(null);
            return;
        }
        if (this.mStorageCacheHelper.hasCachedSizeInfo() && this.mSelectedStorageEntry.isPrivate()) {
            StorageCacheHelper.StorageCache retrieveCachedSize = this.mStorageCacheHelper.retrieveCachedSize();
            this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
            this.mPreferenceController.setUsedSize(retrieveCachedSize.totalUsedSize);
            this.mPreferenceController.setTotalSize(retrieveCachedSize.totalSize);
            this.storageDataUseController.setUsedSize(retrieveCachedSize.totalUsedSize);
            this.storageDataUseController.setTotalSize(retrieveCachedSize.totalSize);
        }
        if (this.mSelectedStorageEntry.isPrivate()) {
            this.mStorageInfo = null;
            this.mAppsResult = null;
            if (this.mStorageCacheHelper.hasCachedSizeInfo()) {
                this.mPreferenceController.onLoadFinished(this.mAppsResult, this.mUserId);
                this.storageDataUseController.onLoadFinished(this.mAppsResult, this.mUserId);
            } else {
                maybeSetLoading(isQuotaSupported());
                this.mPreferenceController.setVolume(null);
            }
            LoaderManager loaderManager = getLoaderManager();
            Bundle bundle = Bundle.EMPTY;
            loaderManager.restartLoader(0, bundle, this);
            getLoaderManager().restartLoader(2, bundle, new VolumeSizeCallbacks());
            getLoaderManager().restartLoader(1, bundle, new IconLoaderCallbacks());
            return;
        }
        this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
        if (getView() != null) {
            View view = getView();
            int i = R$id.loading_container;
            if (view.findViewById(i) == null || getView().findViewById(i).getVisibility() != 0) {
                return;
            }
            setLoading(false, true);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        StorageEntry storageEntry;
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.isInMultiWindow = activity.isInMultiWindowMode();
        StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        if (bundle == null) {
            VolumeInfo maybeInitializeVolume = Utils.maybeInitializeVolume(storageManager, getArguments());
            if (maybeInitializeVolume == null) {
                storageEntry = StorageEntry.getDefaultInternalStorageEntry(getContext());
            } else {
                storageEntry = new StorageEntry(getContext(), maybeInitializeVolume);
            }
            this.mSelectedStorageEntry = storageEntry;
        } else {
            this.mSelectedStorageEntry = (StorageEntry) bundle.getParcelable("selected_storage_entry_key");
        }
        initializeOptionsMenu(activity);
        if (this.mStorageCacheHelper.hasCachedSizeInfo()) {
            this.mIsLoadedFromCache = true;
            this.mStorageEntries.clear();
            this.mStorageEntries.addAll(StorageUtils.getAllStorageEntries(getContext(), this.mStorageManager));
            refreshUi();
            updateSecondaryUserControllers(this.mSecondaryUsers, this.mAppsResult);
            setSecondaryUsersVisible(true);
        }
    }

    private void updateSelectedStorageEntry() {
        boolean z = false;
        int i = 0;
        while (true) {
            if (i < this.mStorageEntries.size()) {
                StorageEntry storageEntry = this.mStorageEntries.get(i);
                if (storageEntry != null && storageEntry.getPath() != null && storageEntry.getPath().equals(this.mSelectedStorageEntry.getPath())) {
                    z = true;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (z) {
            return;
        }
        this.mSelectedStorageEntry = StorageEntry.getDefaultInternalStorageEntry(getContext());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mIsWorkProfile = false;
        this.mUserId = UserHandle.myUserId();
        this.mStorageCacheHelper = new StorageCacheHelper(getContext(), this.mUserId);
        super.onAttach(context);
        ((AutomaticStorageManagementSwitchPreferenceController) use(AutomaticStorageManagementSwitchPreferenceController.class)).setFragmentManager(getFragmentManager());
        ((ManageStoragePreferenceController) use(ManageStoragePreferenceController.class)).setUserId(this.mUserId);
    }

    void initializeOptionsMenu(Activity activity) {
        this.mOptionMenuController = new VolumeOptionMenuController(activity, this, this.mSelectedStorageEntry);
        getSettingsLifecycle().addObserver(this.mOptionMenuController);
        setHasOptionsMenu(true);
        activity.invalidateOptionsMenu();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mIsLoadedFromCache && !this.isInMultiWindow) {
            this.mIsLoadedFromCache = false;
        } else {
            this.mStorageEntries.clear();
            this.mStorageEntries.addAll(StorageUtils.getAllStorageEntries(getContext(), this.mStorageManager));
            updateSelectedStorageEntry();
            refreshUi();
        }
        this.mStorageManager.registerListener(this.mStorageEventListener);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mStorageManager.unregisterListener(this.mStorageEventListener);
        getLoaderManager().destroyLoader(0);
        getLoaderManager().destroyLoader(1);
        getLoaderManager().destroyLoader(2);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable("selected_storage_entry_key", this.mSelectedStorageEntry);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_storage_dashboard;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReceivedSizes() {
        if (this.mStorageInfo == null || this.mAppsResult == null) {
            return;
        }
        if (getView().findViewById(R$id.loading_container).getVisibility() == 0) {
            setLoading(false, true);
            getListView().smoothScrollToPosition(0);
        }
        PrivateStorageInfo privateStorageInfo = this.mStorageInfo;
        long j = privateStorageInfo.totalBytes - privateStorageInfo.freeBytes;
        this.mPreferenceController.setVolume(this.mSelectedStorageEntry.getVolumeInfo());
        this.mPreferenceController.setUsedSize(j);
        this.mPreferenceController.setTotalSize(this.mStorageInfo.totalBytes);
        this.storageDataUseController.setUsedSize(j);
        this.storageDataUseController.setTotalSize(this.mStorageInfo.totalBytes);
        this.mStorageCacheHelper.cacheTotalSizeAndTotalUsedSize(this.mStorageInfo.totalBytes, j);
        int size = this.mSecondaryUsers.size();
        for (int i = 0; i < size; i++) {
            AbstractPreferenceController abstractPreferenceController = this.mSecondaryUsers.get(i);
            if (abstractPreferenceController instanceof SecondaryUserController) {
                ((SecondaryUserController) abstractPreferenceController).setTotalSize(this.mStorageInfo.totalBytes);
            }
        }
        if (this.mSelectedStorageEntry.isPrivate()) {
            this.mPreferenceController.onLoadFinished(this.mAppsResult, this.mUserId);
            this.storageDataUseController.onLoadFinished(this.mAppsResult, this.mUserId);
        }
        updateSecondaryUserControllers(this.mSecondaryUsers, this.mAppsResult);
        setSecondaryUsersVisible(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.storage_dashboard_fragment;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mPreferenceController = new StorageItemPreferenceController(context, this, null, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)), this.mIsWorkProfile);
        StorageDataUseController storageDataUseController = new StorageDataUseController(context, "storage_data_ring_view");
        this.storageDataUseController = storageDataUseController;
        arrayList.add(storageDataUseController);
        arrayList.add(this.mPreferenceController);
        List<AbstractPreferenceController> secondaryUserControllers = SecondaryUserController.getSecondaryUserControllers(context, this.mUserManager, this.mIsWorkProfile);
        this.mSecondaryUsers = secondaryUserControllers;
        arrayList.addAll(secondaryUserControllers);
        return arrayList;
    }

    private void updateSecondaryUserControllers(List<AbstractPreferenceController> list, SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AbstractPreferenceController abstractPreferenceController = list.get(i);
            if (abstractPreferenceController instanceof StorageAsyncLoader.ResultHandler) {
                ((StorageAsyncLoader.ResultHandler) abstractPreferenceController).handleResult(sparseArray);
            }
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<SparseArray<StorageAsyncLoader.StorageResult>> onCreateLoader(int i, Bundle bundle) {
        Context context = getContext();
        return new StorageAsyncLoader(context, this.mUserManager, this.mSelectedStorageEntry.getFsUuid(), new StorageStatsSource(context), context.getPackageManager());
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<SparseArray<StorageAsyncLoader.StorageResult>> loader, SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
        onReceivedSizes();
    }

    public PrivateStorageInfo getPrivateStorageInfo() {
        return this.mStorageInfo;
    }

    public void setPrivateStorageInfo(PrivateStorageInfo privateStorageInfo) {
        this.mStorageInfo = privateStorageInfo;
    }

    public SparseArray<StorageAsyncLoader.StorageResult> getStorageResult() {
        return this.mAppsResult;
    }

    public void setStorageResult(SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
    }

    public void maybeSetLoading(boolean z) {
        if (!(z && (this.mStorageInfo == null || this.mAppsResult == null)) && (z || this.mStorageInfo != null)) {
            return;
        }
        setLoading(true, false);
    }

    private boolean isQuotaSupported() {
        return this.mSelectedStorageEntry.isMounted() && ((StorageStatsManager) getActivity().getSystemService(StorageStatsManager.class)).isQuotaSupported(this.mSelectedStorageEntry.getFsUuid());
    }

    private void setSecondaryUsersVisible(boolean z) {
        Optional findAny = this.mSecondaryUsers.stream().filter(new Predicate() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setSecondaryUsersVisible$0;
                lambda$setSecondaryUsersVisible$0 = StorageDashboardFragment.lambda$setSecondaryUsersVisible$0((AbstractPreferenceController) obj);
                return lambda$setSecondaryUsersVisible$0;
            }
        }).map(new Function() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SecondaryUserController lambda$setSecondaryUsersVisible$1;
                lambda$setSecondaryUsersVisible$1 = StorageDashboardFragment.lambda$setSecondaryUsersVisible$1((AbstractPreferenceController) obj);
                return lambda$setSecondaryUsersVisible$1;
            }
        }).findAny();
        if (findAny.isPresent()) {
            ((SecondaryUserController) findAny.get()).setPreferenceGroupVisible(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setSecondaryUsersVisible$0(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof SecondaryUserController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ SecondaryUserController lambda$setSecondaryUsersVisible$1(AbstractPreferenceController abstractPreferenceController) {
        return (SecondaryUserController) abstractPreferenceController;
    }

    private boolean getSecondaryUsersVisibleStatus() {
        Optional findAny = this.mSecondaryUsers.stream().filter(new Predicate() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSecondaryUsersVisibleStatus$2;
                lambda$getSecondaryUsersVisibleStatus$2 = StorageDashboardFragment.lambda$getSecondaryUsersVisibleStatus$2((AbstractPreferenceController) obj);
                return lambda$getSecondaryUsersVisibleStatus$2;
            }
        }).map(new Function() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SecondaryUserController lambda$getSecondaryUsersVisibleStatus$3;
                lambda$getSecondaryUsersVisibleStatus$3 = StorageDashboardFragment.lambda$getSecondaryUsersVisibleStatus$3((AbstractPreferenceController) obj);
                return lambda$getSecondaryUsersVisibleStatus$3;
            }
        }).findAny();
        if (findAny.isPresent()) {
            return ((SecondaryUserController) findAny.get()).getPreferenceGroupVisible();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSecondaryUsersVisibleStatus$2(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof SecondaryUserController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ SecondaryUserController lambda$getSecondaryUsersVisibleStatus$3(AbstractPreferenceController abstractPreferenceController) {
        return (SecondaryUserController) abstractPreferenceController;
    }

    /* loaded from: classes.dex */
    public final class IconLoaderCallbacks implements LoaderManager.LoaderCallbacks<SparseArray<Drawable>> {
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<SparseArray<Drawable>> loader) {
        }

        public IconLoaderCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<SparseArray<Drawable>> onCreateLoader(int i, Bundle bundle) {
            return new UserIconLoader(StorageDashboardFragment.this.getContext(), new UserIconLoader.FetchUserIconTask() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$IconLoaderCallbacks$$ExternalSyntheticLambda2
                @Override // com.android.settings.deviceinfo.storage.UserIconLoader.FetchUserIconTask
                public final SparseArray getUserIcons() {
                    SparseArray lambda$onCreateLoader$0;
                    lambda$onCreateLoader$0 = StorageDashboardFragment.IconLoaderCallbacks.this.lambda$onCreateLoader$0();
                    return lambda$onCreateLoader$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ SparseArray lambda$onCreateLoader$0() {
            return UserIconLoader.loadUserIconsWithContext(StorageDashboardFragment.this.getContext());
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<SparseArray<Drawable>> loader, final SparseArray<Drawable> sparseArray) {
            StorageDashboardFragment.this.mSecondaryUsers.stream().filter(new Predicate() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$IconLoaderCallbacks$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onLoadFinished$1;
                    lambda$onLoadFinished$1 = StorageDashboardFragment.IconLoaderCallbacks.lambda$onLoadFinished$1((AbstractPreferenceController) obj);
                    return lambda$onLoadFinished$1;
                }
            }).forEach(new Consumer() { // from class: com.android.settings.deviceinfo.StorageDashboardFragment$IconLoaderCallbacks$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    StorageDashboardFragment.IconLoaderCallbacks.lambda$onLoadFinished$2(sparseArray, (AbstractPreferenceController) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onLoadFinished$1(AbstractPreferenceController abstractPreferenceController) {
            return abstractPreferenceController instanceof UserIconLoader.UserIconHandler;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onLoadFinished$2(SparseArray sparseArray, AbstractPreferenceController abstractPreferenceController) {
            ((UserIconLoader.UserIconHandler) abstractPreferenceController).handleUserIcons(sparseArray);
        }
    }

    /* loaded from: classes.dex */
    public final class VolumeSizeCallbacks implements LoaderManager.LoaderCallbacks<PrivateStorageInfo> {
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<PrivateStorageInfo> loader) {
        }

        public VolumeSizeCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<PrivateStorageInfo> onCreateLoader(int i, Bundle bundle) {
            Context context = StorageDashboardFragment.this.getContext();
            return new VolumeSizesLoader(context, new StorageManagerVolumeProvider(StorageDashboardFragment.this.mStorageManager), (StorageStatsManager) context.getSystemService(StorageStatsManager.class), StorageDashboardFragment.this.mSelectedStorageEntry.getVolumeInfo());
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<PrivateStorageInfo> loader, PrivateStorageInfo privateStorageInfo) {
            if (privateStorageInfo == null) {
                StorageDashboardFragment.this.getActivity().finish();
                return;
            }
            StorageDashboardFragment.this.mStorageInfo = privateStorageInfo;
            StorageDashboardFragment.this.onReceivedSizes();
        }
    }
}
