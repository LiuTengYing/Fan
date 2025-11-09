package com.android.settings.notification;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public abstract class RingtonePreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    public abstract int getRingtoneType();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public RingtonePreferenceControllerBase(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.notification.RingtonePreferenceControllerBase$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RingtonePreferenceControllerBase.this.lambda$updateState$0(preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateSummary */
    public void lambda$updateState$0(final Preference preference) {
        try {
            final String title = Ringtone.getTitle(this.mContext, RingtoneManager.getActualDefaultRingtoneUri(this.mContext, getRingtoneType()), false, true);
            if (title != null) {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.notification.RingtonePreferenceControllerBase$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Preference.this.setSummary(title);
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            Log.w("PrefControllerMixin", "Error getting ringtone summary.", e);
        }
    }
}
