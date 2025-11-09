package com.android.settings.notification;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class RedactNotificationPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    static final String KEY_LOCKSCREEN_REDACT = "lock_screen_redact";
    static final String KEY_LOCKSCREEN_WORK_PROFILE_REDACT = "lock_screen_work_redact";
    private static final String TAG = "LockScreenNotifPref";
    private ContentObserver mContentObserver;
    private KeyguardManager mKm;
    private RestrictedSwitchPreference mPreference;
    int mProfileUserId;
    private UserManager mUm;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RedactNotificationPreferenceController(Context context, String str) {
        super(context, str);
        int[] profileIdsWithDisabled;
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.notification.RedactNotificationPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (RedactNotificationPreferenceController.this.mPreference != null) {
                    RedactNotificationPreferenceController.this.mPreference.setEnabled(RedactNotificationPreferenceController.this.getAvailabilityStatus() != 5);
                }
            }
        };
        this.mUm = (UserManager) context.getSystemService(UserManager.class);
        this.mKm = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mProfileUserId = UserHandle.myUserId();
        for (int i : this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId())) {
            if (i != UserHandle.myUserId()) {
                this.mProfileUserId = i;
            }
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        int myUserId = KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId;
        if (myUserId != -10000) {
            this.mPreference.setDisabledByAdmin(getEnforcedAdmin(myUserId));
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return getAllowPrivateNotifications(KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", z ? 1 : 0, KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (KEY_LOCKSCREEN_WORK_PROFILE_REDACT.equals(getPreferenceKey()) && this.mProfileUserId == UserHandle.myUserId()) {
            return 2;
        }
        int myUserId = KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId;
        if (FeatureFactory.getFactory(this.mContext).getSecurityFeatureProvider().getLockPatternUtils(this.mContext).isSecure(myUserId)) {
            if (getLockscreenNotificationsEnabled(myUserId)) {
                return (KEY_LOCKSCREEN_WORK_PROFILE_REDACT.equals(getPreferenceKey()) && this.mKm.isDeviceLocked(this.mProfileUserId)) ? 5 : 0;
            }
            return 5;
        }
        return 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_notifications;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_notifications"), false, this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    private RestrictedLockUtils.EnforcedAdmin getEnforcedAdmin(int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this.mContext, 4, i);
        return checkIfKeyguardFeaturesDisabled != null ? checkIfKeyguardFeaturesDisabled : RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this.mContext, 8, i);
    }

    private boolean getAllowPrivateNotifications(int i) {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 1, i) != 0 && getEnforcedAdmin(i) == null;
    }

    private boolean getLockscreenNotificationsEnabled(int i) {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, i) != 0;
    }
}
