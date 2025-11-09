package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.VolumeInfo;
import android.util.DataUnit;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.deviceinfo.StorageItemPreference;
import com.android.settings.deviceinfo.storage.EmptyTrashFragment;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.StorageCacheHelper;
import com.android.settings.deviceinfo.storage.StorageUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.deviceinfo.StorageVolumeProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToLongFunction;
/* loaded from: classes.dex */
public class StorageItemPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, EmptyTrashFragment.OnEmptyTrashCompleteListener {
    static final String APPS_KEY = "pref_apps";
    static final String AUDIO_KEY = "pref_audio";
    static final String DOCUMENTS_AND_OTHER_KEY = "pref_documents_and_other";
    static final String GAMES_KEY = "pref_games";
    static final String IMAGES_KEY = "pref_images";
    static final String PUBLIC_STORAGE_KEY = "pref_public_storage";
    static final String SYSTEM_KEY = "pref_system";
    static final String TRASH_KEY = "pref_trash";
    static final String VIDEOS_KEY = "pref_videos";
    StorageItemPreference mAppsPreference;
    StorageItemPreference mAudioPreference;
    final Uri mAudioUri;
    StorageItemPreference mDocumentsAndOtherPreference;
    final Uri mDocumentsAndOtherUri;
    private final Fragment mFragment;
    StorageItemPreference mGamesPreference;
    StorageItemPreference mImagesPreference;
    final Uri mImagesUri;
    private boolean mIsDocumentsPrefShown;
    private boolean mIsPreferenceOrderedBySize;
    private boolean mIsWorkProfile;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private PackageManager mPackageManager;
    private List<StorageItemPreference> mPrivateStorageItemPreferences;
    Preference mPublicStoragePreference;
    private PreferenceScreen mScreen;
    private StorageCacheHelper mStorageCacheHelper;
    private final StorageVolumeProvider mSvp;
    StorageItemPreference mSystemPreference;
    private long mTotalSize;
    StorageItemPreference mTrashPreference;
    private long mUsedBytes;
    private int mUserId;
    private UserManager mUserManager;
    StorageItemPreference mVideosPreference;
    final Uri mVideosUri;
    private VolumeInfo mVolume;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public StorageItemPreferenceController(Context context, Fragment fragment, VolumeInfo volumeInfo, StorageVolumeProvider storageVolumeProvider, boolean z) {
        super(context);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mFragment = fragment;
        this.mVolume = volumeInfo;
        this.mSvp = storageVolumeProvider;
        this.mIsWorkProfile = z;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mUserId = getCurrentUserId();
        this.mIsDocumentsPrefShown = isDocumentsPrefShown();
        this.mStorageCacheHelper = new StorageCacheHelper(this.mContext, this.mUserId);
        this.mImagesUri = Uri.parse(context.getResources().getString(R$string.config_images_storage_category_uri));
        this.mVideosUri = Uri.parse(context.getResources().getString(R$string.config_videos_storage_category_uri));
        this.mAudioUri = Uri.parse(context.getResources().getString(R$string.config_audio_storage_category_uri));
        this.mDocumentsAndOtherUri = Uri.parse(context.getResources().getString(R$string.config_documents_and_other_storage_category_uri));
    }

    int getCurrentUserId() {
        return Utils.getCurrentUserId(this.mUserManager, this.mIsWorkProfile);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.getKey() == null) {
            return false;
        }
        String key = preference.getKey();
        key.hashCode();
        char c = 65535;
        switch (key.hashCode()) {
            case -1646839110:
                if (key.equals(AUDIO_KEY)) {
                    c = 0;
                    break;
                }
                break;
            case -1641885275:
                if (key.equals(GAMES_KEY)) {
                    c = 1;
                    break;
                }
                break;
            case -1629384164:
                if (key.equals(TRASH_KEY)) {
                    c = 2;
                    break;
                }
                break;
            case -1300054258:
                if (key.equals(APPS_KEY)) {
                    c = 3;
                    break;
                }
                break;
            case -917633983:
                if (key.equals(PUBLIC_STORAGE_KEY)) {
                    c = 4;
                    break;
                }
                break;
            case 709148692:
                if (key.equals(IMAGES_KEY)) {
                    c = 5;
                    break;
                }
                break;
            case 930440645:
                if (key.equals(DOCUMENTS_AND_OTHER_KEY)) {
                    c = 6;
                    break;
                }
                break;
            case 1007071179:
                if (key.equals(SYSTEM_KEY)) {
                    c = 7;
                    break;
                }
                break;
            case 1077721332:
                if (key.equals(VIDEOS_KEY)) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                launchPublicStorageIntent();
                return true;
            case 1:
                launchGamesIntent();
                return true;
            case 2:
                launchTrashIntent();
                return true;
            case 3:
                launchAppsIntent();
                return true;
            case 4:
                launchPublicStorageIntent();
                return true;
            case 5:
                launchPublicStorageIntent();
                return true;
            case 6:
                launchPublicStorageIntent();
                return true;
            case 7:
                StorageUtils.SystemInfoFragment systemInfoFragment = new StorageUtils.SystemInfoFragment();
                systemInfoFragment.setTargetFragment(this.mFragment, 0);
                systemInfoFragment.show(this.mFragment.getFragmentManager(), "SystemInfo");
                return true;
            case '\b':
                launchPublicStorageIntent();
                return true;
            default:
                return super.handlePreferenceTreeClick(preference);
        }
    }

    public void setVolume(VolumeInfo volumeInfo) {
        this.mVolume = volumeInfo;
        Preference preference = this.mPublicStoragePreference;
        if (preference != null) {
            preference.setVisible(isValidPublicVolume());
        }
        if (isValidPrivateVolume()) {
            this.mIsDocumentsPrefShown = isDocumentsPrefShown();
        } else {
            setPrivateStorageCategoryPreferencesVisibility(false);
        }
    }

    private boolean isValidPrivateVolume() {
        VolumeInfo volumeInfo = this.mVolume;
        return volumeInfo != null && volumeInfo.getType() == 1 && (this.mVolume.getState() == 2 || this.mVolume.getState() == 3);
    }

    private boolean isValidPublicVolume() {
        VolumeInfo volumeInfo = this.mVolume;
        return volumeInfo != null && (volumeInfo.getType() == 0 || this.mVolume.getType() == 5) && (this.mVolume.getState() == 2 || this.mVolume.getState() == 3);
    }

    void setPrivateStorageCategoryPreferencesVisibility(boolean z) {
        if (this.mScreen == null) {
            return;
        }
        this.mImagesPreference.setVisible(z);
        this.mVideosPreference.setVisible(z);
        this.mAudioPreference.setVisible(z);
        this.mAppsPreference.setVisible(z);
        this.mGamesPreference.setVisible(z);
        this.mSystemPreference.setVisible(z);
        this.mTrashPreference.setVisible(z);
        if (z) {
            this.mDocumentsAndOtherPreference.setVisible(this.mIsDocumentsPrefShown);
        } else {
            this.mDocumentsAndOtherPreference.setVisible(false);
        }
    }

    private boolean isDocumentsPrefShown() {
        VolumeInfo findEmulatedForPrivate = this.mSvp.findEmulatedForPrivate(this.mVolume);
        return findEmulatedForPrivate != null && findEmulatedForPrivate.isMountedReadable();
    }

    private void updatePrivateStorageCategoryPreferencesOrder() {
        if (this.mScreen == null || !isValidPrivateVolume()) {
            return;
        }
        if (this.mPrivateStorageItemPreferences == null) {
            ArrayList arrayList = new ArrayList();
            this.mPrivateStorageItemPreferences = arrayList;
            arrayList.add(this.mImagesPreference);
            this.mPrivateStorageItemPreferences.add(this.mVideosPreference);
            this.mPrivateStorageItemPreferences.add(this.mAudioPreference);
            this.mPrivateStorageItemPreferences.add(this.mAppsPreference);
            this.mPrivateStorageItemPreferences.add(this.mGamesPreference);
            this.mPrivateStorageItemPreferences.add(this.mDocumentsAndOtherPreference);
            this.mPrivateStorageItemPreferences.add(this.mSystemPreference);
            this.mPrivateStorageItemPreferences.add(this.mTrashPreference);
        }
        this.mScreen.removePreference(this.mImagesPreference);
        this.mScreen.removePreference(this.mVideosPreference);
        this.mScreen.removePreference(this.mAudioPreference);
        this.mScreen.removePreference(this.mAppsPreference);
        this.mScreen.removePreference(this.mGamesPreference);
        this.mScreen.removePreference(this.mDocumentsAndOtherPreference);
        this.mScreen.removePreference(this.mSystemPreference);
        this.mScreen.removePreference(this.mTrashPreference);
        Collections.sort(this.mPrivateStorageItemPreferences, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.settings.deviceinfo.storage.StorageItemPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                return ((StorageItemPreference) obj).getStorageSize();
            }
        }));
        int i = 200;
        for (StorageItemPreference storageItemPreference : this.mPrivateStorageItemPreferences) {
            storageItemPreference.setOrder(i);
            this.mScreen.addPreference(storageItemPreference);
            i--;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        this.mPublicStoragePreference = preferenceScreen.findPreference(PUBLIC_STORAGE_KEY);
        this.mImagesPreference = (StorageItemPreference) preferenceScreen.findPreference(IMAGES_KEY);
        this.mVideosPreference = (StorageItemPreference) preferenceScreen.findPreference(VIDEOS_KEY);
        this.mAudioPreference = (StorageItemPreference) preferenceScreen.findPreference(AUDIO_KEY);
        this.mAppsPreference = (StorageItemPreference) preferenceScreen.findPreference(APPS_KEY);
        this.mGamesPreference = (StorageItemPreference) preferenceScreen.findPreference(GAMES_KEY);
        this.mDocumentsAndOtherPreference = (StorageItemPreference) preferenceScreen.findPreference(DOCUMENTS_AND_OTHER_KEY);
        this.mSystemPreference = (StorageItemPreference) preferenceScreen.findPreference(SYSTEM_KEY);
        this.mTrashPreference = (StorageItemPreference) preferenceScreen.findPreference(TRASH_KEY);
    }

    public void onLoadFinished(SparseArray<StorageAsyncLoader.StorageResult> sparseArray, int i) {
        boolean z = sparseArray != null && this.mIsPreferenceOrderedBySize;
        StorageCacheHelper.StorageCache sizeInfo = getSizeInfo(sparseArray, i);
        boolean z2 = z;
        this.mImagesPreference.setStorageSize(sizeInfo.imagesSize, this.mTotalSize, z2);
        this.mVideosPreference.setStorageSize(sizeInfo.videosSize, this.mTotalSize, z2);
        this.mAudioPreference.setStorageSize(sizeInfo.audioSize, this.mTotalSize, z2);
        this.mAppsPreference.setStorageSize(sizeInfo.allAppsExceptGamesSize, this.mTotalSize, z2);
        this.mGamesPreference.setStorageSize(sizeInfo.gamesSize, this.mTotalSize, z2);
        this.mDocumentsAndOtherPreference.setStorageSize(sizeInfo.documentsAndOtherSize, this.mTotalSize, z2);
        this.mTrashPreference.setStorageSize(sizeInfo.trashSize, this.mTotalSize, z2);
        StorageItemPreference storageItemPreference = this.mSystemPreference;
        if (storageItemPreference != null) {
            storageItemPreference.setStorageSize(sizeInfo.systemSize, this.mTotalSize, z);
        }
        if (sparseArray != null) {
            this.mStorageCacheHelper.cacheSizeInfo(sizeInfo);
        }
        if (!this.mIsPreferenceOrderedBySize) {
            updatePrivateStorageCategoryPreferencesOrder();
            this.mIsPreferenceOrderedBySize = true;
        }
        setPrivateStorageCategoryPreferencesVisibility(true);
    }

    private StorageCacheHelper.StorageCache getSizeInfo(SparseArray<StorageAsyncLoader.StorageResult> sparseArray, int i) {
        if (sparseArray == null) {
            return this.mStorageCacheHelper.retrieveCachedSize();
        }
        StorageAsyncLoader.StorageResult storageResult = sparseArray.get(i);
        StorageCacheHelper.StorageCache storageCache = new StorageCacheHelper.StorageCache();
        storageCache.imagesSize = storageResult.imagesSize;
        storageCache.videosSize = storageResult.videosSize;
        storageCache.audioSize = storageResult.audioSize;
        storageCache.allAppsExceptGamesSize = storageResult.allAppsExceptGamesSize;
        storageCache.gamesSize = storageResult.gamesSize;
        storageCache.documentsAndOtherSize = storageResult.documentsAndOtherSize;
        storageCache.trashSize = storageResult.trashSize;
        long j = 0;
        for (int i2 = 0; i2 < sparseArray.size(); i2++) {
            StorageAsyncLoader.StorageResult valueAt = sparseArray.valueAt(i2);
            j = (j + ((((((valueAt.gamesSize + valueAt.audioSize) + valueAt.videosSize) + valueAt.imagesSize) + valueAt.documentsAndOtherSize) + valueAt.trashSize) + valueAt.allAppsExceptGamesSize)) - valueAt.duplicateCodeSize;
        }
        storageCache.systemSize = Math.max(DataUnit.GIBIBYTES.toBytes(1L), this.mUsedBytes - j);
        return storageCache;
    }

    public void setUsedSize(long j) {
        this.mUsedBytes = j;
    }

    public void setTotalSize(long j) {
        this.mTotalSize = j;
    }

    private void launchPublicStorageIntent() {
        Intent buildBrowseIntent = this.mVolume.buildBrowseIntent();
        if (buildBrowseIntent == null) {
            return;
        }
        this.mContext.startActivityAsUser(buildBrowseIntent, new UserHandle(this.mUserId));
    }

    private void launchAppsIntent() {
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(3);
        workAnnotatedBundle.putString("classname", Settings.StorageUseActivity.class.getName());
        workAnnotatedBundle.putString("volumeUuid", this.mVolume.getFsUuid());
        workAnnotatedBundle.putString("volumeName", this.mVolume.getDescription());
        Intent intent = new SubSettingLauncher(this.mContext).setDestination(ManageApplications.class.getName()).setTitleRes(R$string.apps_storage).setArguments(workAnnotatedBundle).setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment)).toIntent();
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        Utils.launchIntent(this.mFragment, intent);
    }

    private void launchGamesIntent() {
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(1);
        workAnnotatedBundle.putString("classname", Settings.GamesStorageActivity.class.getName());
        Intent intent = new SubSettingLauncher(this.mContext).setDestination(ManageApplications.class.getName()).setTitleRes(R$string.game_storage_settings).setArguments(workAnnotatedBundle).setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment)).toIntent();
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        Utils.launchIntent(this.mFragment, intent);
    }

    private Bundle getWorkAnnotatedBundle(int i) {
        Bundle bundle = new Bundle(i + 1);
        bundle.putInt(":settings:show_fragment_tab", this.mIsWorkProfile ? 1 : 0);
        return bundle;
    }

    private void launchTrashIntent() {
        Intent intent = new Intent("android.settings.VIEW_TRASH");
        if (this.mPackageManager.resolveActivityAsUser(intent, 0, this.mUserId) == null) {
            long storageSize = this.mTrashPreference.getStorageSize();
            if (storageSize > 0) {
                new EmptyTrashFragment(this.mFragment, this.mUserId, storageSize, this).show();
                return;
            } else {
                Toast.makeText(this.mContext, R$string.storage_trash_dialog_empty_message, 0).show();
                return;
            }
        }
        this.mContext.startActivityAsUser(intent, new UserHandle(this.mUserId));
    }

    @Override // com.android.settings.deviceinfo.storage.EmptyTrashFragment.OnEmptyTrashCompleteListener
    public void onEmptyTrashComplete() {
        StorageItemPreference storageItemPreference = this.mTrashPreference;
        if (storageItemPreference == null) {
            return;
        }
        storageItemPreference.setStorageSize(0L, this.mTotalSize, true);
        updatePrivateStorageCategoryPreferencesOrder();
    }
}
