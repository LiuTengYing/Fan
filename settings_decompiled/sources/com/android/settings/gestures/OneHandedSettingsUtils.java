package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.accessibility.AccessibilityShortcutController;
/* loaded from: classes.dex */
public class OneHandedSettingsUtils {
    private static int sCurrentUserId;
    private final Context mContext;
    private final SettingsObserver mSettingsObserver;
    static final String ONE_HANDED_MODE_TARGET_NAME = AccessibilityShortcutController.ONE_HANDED_COMPONENT_NAME.getShortClassName();
    static final Uri ONE_HANDED_MODE_ENABLED_URI = Settings.Secure.getUriFor("one_handed_mode_enabled");
    static final Uri SHOW_NOTIFICATION_ENABLED_URI = Settings.Secure.getUriFor("swipe_bottom_to_notification_enabled");
    static final Uri SOFTWARE_SHORTCUT_ENABLED_URI = Settings.Secure.getUriFor("accessibility_button_targets");
    static final Uri HARDWARE_SHORTCUT_ENABLED_URI = Settings.Secure.getUriFor("accessibility_shortcut_target_service");

    /* loaded from: classes.dex */
    public interface TogglesCallback {
        void onChange(Uri uri);
    }

    /* loaded from: classes.dex */
    public enum OneHandedTimeout {
        NEVER(0),
        SHORT(4),
        MEDIUM(8),
        LONG(12);
        
        private final int mValue;

        OneHandedTimeout(int i) {
            this.mValue = i;
        }

        public int getValue() {
            return this.mValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OneHandedSettingsUtils(Context context) {
        this.mContext = context;
        sCurrentUserId = UserHandle.myUserId();
        this.mSettingsObserver = new SettingsObserver(new Handler(Looper.getMainLooper()));
    }

    public static boolean isSupportOneHandedMode() {
        return SystemProperties.getBoolean("ro.support_one_handed_mode", false);
    }

    public static boolean isOneHandedModeEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "one_handed_mode_enabled", 0, sCurrentUserId) == 1;
    }

    public static void setOneHandedModeEnabled(Context context, boolean z) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "one_handed_mode_enabled", z ? 1 : 0, sCurrentUserId);
    }

    public static boolean setTapsAppToExitEnabled(Context context, boolean z) {
        return Settings.Secure.putIntForUser(context.getContentResolver(), "taps_app_to_exit", z ? 1 : 0, sCurrentUserId);
    }

    public static void setUserId(int i) {
        sCurrentUserId = i;
    }

    public static void setTimeoutValue(Context context, int i) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "one_handed_mode_timeout", i, sCurrentUserId);
    }

    public static boolean isSwipeDownNotificationEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "swipe_bottom_to_notification_enabled", 0, sCurrentUserId) == 1;
    }

    public static void setSwipeDownNotificationEnabled(Context context, boolean z) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "swipe_bottom_to_notification_enabled", z ? 1 : 0, sCurrentUserId);
    }

    public boolean setNavigationBarMode(Context context, String str) {
        return Settings.Secure.putStringForUser(context.getContentResolver(), "navigation_mode", str, UserHandle.myUserId());
    }

    public static int getNavigationBarMode(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "navigation_mode", 2, sCurrentUserId);
    }

    public static boolean canEnableController(Context context) {
        return (isOneHandedModeEnabled(context) && getNavigationBarMode(context) != 0) || getShortcutEnabled(context);
    }

    public static boolean getShortcutEnabled(Context context) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "accessibility_button_targets", sCurrentUserId);
        if (TextUtils.isEmpty(stringForUser) || !stringForUser.contains(ONE_HANDED_MODE_TARGET_NAME)) {
            String stringForUser2 = Settings.Secure.getStringForUser(context.getContentResolver(), "accessibility_shortcut_target_service", sCurrentUserId);
            return !TextUtils.isEmpty(stringForUser2) && stringForUser2.contains(ONE_HANDED_MODE_TARGET_NAME);
        }
        return true;
    }

    public void setShortcutEnabled(Context context, boolean z) {
        Settings.Secure.putStringForUser(context.getContentResolver(), "accessibility_button_targets", z ? ONE_HANDED_MODE_TARGET_NAME : "", sCurrentUserId);
    }

    public void registerToggleAwareObserver(TogglesCallback togglesCallback) {
        this.mSettingsObserver.observe();
        this.mSettingsObserver.setCallback(togglesCallback);
    }

    public void unregisterToggleAwareObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    }

    /* loaded from: classes.dex */
    private final class SettingsObserver extends ContentObserver {
        private TogglesCallback mCallback;

        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCallback(TogglesCallback togglesCallback) {
            this.mCallback = togglesCallback;
        }

        public void observe() {
            ContentResolver contentResolver = OneHandedSettingsUtils.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(OneHandedSettingsUtils.ONE_HANDED_MODE_ENABLED_URI, true, this);
            contentResolver.registerContentObserver(OneHandedSettingsUtils.SHOW_NOTIFICATION_ENABLED_URI, true, this);
            contentResolver.registerContentObserver(OneHandedSettingsUtils.SOFTWARE_SHORTCUT_ENABLED_URI, true, this);
            contentResolver.registerContentObserver(OneHandedSettingsUtils.HARDWARE_SHORTCUT_ENABLED_URI, true, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            TogglesCallback togglesCallback = this.mCallback;
            if (togglesCallback != null) {
                togglesCallback.onChange(uri);
            }
        }
    }
}
