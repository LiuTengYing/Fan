package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.util.SparseArray;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.deviceinfo.StorageItemPreference;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.UserIconLoader;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SecondaryUserController extends AbstractPreferenceController implements PreferenceControllerMixin, StorageAsyncLoader.ResultHandler, UserIconLoader.UserIconHandler {
    private boolean mIsVisible;
    private PreferenceGroup mPreferenceGroup;
    private long mSize;
    private StorageCacheHelper mStorageCacheHelper;
    private StorageItemPreference mStoragePreference;
    private long mTotalSizeBytes;
    private UserInfo mUser;
    private Drawable mUserIcon;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public static List<AbstractPreferenceController> getSecondaryUserControllers(Context context, UserManager userManager, boolean z) {
        ArrayList arrayList = new ArrayList();
        UserInfo primaryUser = userManager.getPrimaryUser();
        List users = userManager.getUsers();
        int size = users.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            UserInfo userInfo = (UserInfo) users.get(i);
            if (!userInfo.isPrimary() && !Utils.isProfileOf(primaryUser, userInfo) && (!z || userInfo.isManagedProfile())) {
                arrayList.add(new SecondaryUserController(context, userInfo));
                z2 = true;
            }
        }
        if (!z2) {
            arrayList.add(new NoSecondaryUserController(context));
        }
        return arrayList;
    }

    SecondaryUserController(Context context, UserInfo userInfo) {
        super(context);
        this.mUser = userInfo;
        this.mSize = -1L;
        this.mStorageCacheHelper = new StorageCacheHelper(context, userInfo.id);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        if (this.mStoragePreference == null) {
            this.mStoragePreference = new StorageItemPreference(preferenceScreen.getContext());
            this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference("pref_secondary_users");
            this.mStoragePreference.setTitle(this.mUser.name);
            StorageItemPreference storageItemPreference = this.mStoragePreference;
            storageItemPreference.setKey("pref_user_" + this.mUser.id);
            setSize(this.mStorageCacheHelper.retrieveUsedSize(), false);
            this.mPreferenceGroup.setVisible(this.mIsVisible);
            this.mPreferenceGroup.addPreference(this.mStoragePreference);
            maybeSetIcon();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        StorageItemPreference storageItemPreference = this.mStoragePreference;
        if (storageItemPreference != null) {
            return storageItemPreference.getKey();
        }
        return null;
    }

    public UserInfo getUser() {
        return this.mUser;
    }

    public void setSize(long j, boolean z) {
        this.mSize = j;
        StorageItemPreference storageItemPreference = this.mStoragePreference;
        if (storageItemPreference != null) {
            storageItemPreference.setStorageSize(j, this.mTotalSizeBytes, z);
        }
    }

    public void setTotalSize(long j) {
        this.mTotalSizeBytes = j;
    }

    public void setPreferenceGroupVisible(boolean z) {
        this.mIsVisible = z;
        PreferenceGroup preferenceGroup = this.mPreferenceGroup;
        if (preferenceGroup != null) {
            preferenceGroup.setVisible(z);
        }
    }

    public boolean getPreferenceGroupVisible() {
        PreferenceGroup preferenceGroup = this.mPreferenceGroup;
        if (preferenceGroup != null) {
            return preferenceGroup.isVisible();
        }
        return false;
    }

    @Override // com.android.settings.deviceinfo.storage.StorageAsyncLoader.ResultHandler
    public void handleResult(SparseArray<StorageAsyncLoader.StorageResult> sparseArray) {
        if (sparseArray == null) {
            setSize(this.mStorageCacheHelper.retrieveUsedSize(), false);
            return;
        }
        StorageAsyncLoader.StorageResult storageResult = sparseArray.get(getUser().id);
        if (storageResult != null) {
            setSize(storageResult.externalStats.totalBytes, true);
            this.mStorageCacheHelper.cacheUsedSize(storageResult.externalStats.totalBytes);
        }
    }

    @Override // com.android.settings.deviceinfo.storage.UserIconLoader.UserIconHandler
    public void handleUserIcons(SparseArray<Drawable> sparseArray) {
        this.mUserIcon = sparseArray.get(this.mUser.id);
        maybeSetIcon();
    }

    private void maybeSetIcon() {
        StorageItemPreference storageItemPreference;
        Drawable drawable = this.mUserIcon;
        if (drawable == null || (storageItemPreference = this.mStoragePreference) == null) {
            return;
        }
        storageItemPreference.setIcon(drawable);
    }

    /* loaded from: classes.dex */
    static class NoSecondaryUserController extends AbstractPreferenceController implements PreferenceControllerMixin {
        @Override // com.android.settingslib.core.AbstractPreferenceController
        public String getPreferenceKey() {
            return null;
        }

        @Override // com.android.settingslib.core.AbstractPreferenceController
        public boolean isAvailable() {
            return true;
        }

        public NoSecondaryUserController(Context context) {
            super(context);
        }

        @Override // com.android.settingslib.core.AbstractPreferenceController
        public void displayPreference(PreferenceScreen preferenceScreen) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference("pref_secondary_users");
            if (preferenceGroup == null) {
                return;
            }
            preferenceScreen.removePreference(preferenceGroup);
        }
    }
}
