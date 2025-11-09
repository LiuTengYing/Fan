package com.android.settings.notification;

import android.content.Context;
import android.content.res.Resources;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class DockAudioMediaPreferenceController extends SettingPrefController {
    public DockAudioMediaPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(1, "dock_audio_media", "dock_audio_media_enabled", 0, 0, 1) { // from class: com.android.settings.notification.DockAudioMediaPreferenceController.1
            @Override // com.android.settings.notification.SettingPref
            public boolean isApplicable(Context context2) {
                return context2.getResources().getBoolean(R$bool.has_dock_settings);
            }

            @Override // com.android.settings.notification.SettingPref
            protected String getCaption(Resources resources, int i) {
                if (i != 0) {
                    if (i == 1) {
                        return resources.getString(R$string.dock_audio_media_enabled);
                    }
                    throw new IllegalArgumentException();
                }
                return resources.getString(R$string.dock_audio_media_disabled);
            }
        };
    }
}
