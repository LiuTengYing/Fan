package com.android.settings.applications.appinfo;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public abstract class DefaultAppShortcutPreferenceControllerBase extends BasePreferenceController {
    private boolean mAppVisible;
    protected final String mPackageName;
    private PreferenceScreen mPreferenceScreen;
    private final RoleManager mRoleManager;
    private final String mRoleName;
    private boolean mRoleVisible;

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

    public DefaultAppShortcutPreferenceControllerBase(Context context, String str, String str2, String str3) {
        super(context, str);
        this.mRoleName = str2;
        this.mPackageName = str3;
        RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
        this.mRoleManager = roleManager;
        Executor mainExecutor = this.mContext.getMainExecutor();
        roleManager.isRoleVisible(str2, mainExecutor, new Consumer() { // from class: com.android.settings.applications.appinfo.DefaultAppShortcutPreferenceControllerBase$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAppShortcutPreferenceControllerBase.this.lambda$new$0((Boolean) obj);
            }
        });
        roleManager.isApplicationVisibleForRole(str2, str3, mainExecutor, new Consumer() { // from class: com.android.settings.applications.appinfo.DefaultAppShortcutPreferenceControllerBase$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAppShortcutPreferenceControllerBase.this.lambda$new$1((Boolean) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Boolean bool) {
        this.mRoleVisible = bool.booleanValue();
        refreshAvailability();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Boolean bool) {
        this.mAppVisible = bool.booleanValue();
        refreshAvailability();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
    }

    private void refreshAvailability() {
        Preference findPreference;
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen == null || (findPreference = preferenceScreen.findPreference(getPreferenceKey())) == null) {
            return;
        }
        findPreference.setVisible(isAvailable());
        updateState(findPreference);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (((UserManager) this.mContext.getSystemService(UserManager.class)).isManagedProfile()) {
            return 4;
        }
        return (this.mRoleVisible && this.mAppVisible) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getText(isDefaultApp() ? R$string.yes : R$string.no);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(this.mPreferenceKey, preference.getKey())) {
            this.mContext.startActivity(new Intent("android.intent.action.MANAGE_DEFAULT_APP").putExtra("android.intent.extra.ROLE_NAME", this.mRoleName));
            return true;
        }
        return false;
    }

    private boolean isDefaultApp() {
        return TextUtils.equals(this.mPackageName, (String) CollectionUtils.firstOrNull(this.mRoleManager.getRoleHolders(this.mRoleName)));
    }
}
