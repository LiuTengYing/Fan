package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.preference.Preference;
/* loaded from: classes.dex */
public class BubbleCategoryPreferenceController extends NotificationPreferenceController {
    static final int ON = 1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bubbles";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    public BubbleCategoryPreferenceController(Context context) {
        super(context, null);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable()) {
            return areBubblesEnabled();
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mAppRow != null) {
            Intent intent = new Intent("android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", this.mAppRow.pkg);
            intent.putExtra("app_uid", this.mAppRow.uid);
            preference.setIntent(intent);
        }
    }

    private boolean areBubblesEnabled() {
        return Settings.Secure.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_bubbles", 1) == 1;
    }
}
