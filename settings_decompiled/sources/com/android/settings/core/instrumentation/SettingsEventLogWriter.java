package com.android.settings.core.instrumentation;

import android.content.Context;
import android.provider.DeviceConfig;
import com.android.settingslib.core.instrumentation.EventLogWriter;
/* loaded from: classes.dex */
public class SettingsEventLogWriter extends EventLogWriter {
    @Override // com.android.settingslib.core.instrumentation.EventLogWriter, com.android.settingslib.core.instrumentation.LogWriter
    public void visible(Context context, int i, int i2, int i3) {
        if (shouldDisableGenericEventLogging()) {
            return;
        }
        super.visible(context, i, i2, i3);
    }

    @Override // com.android.settingslib.core.instrumentation.EventLogWriter, com.android.settingslib.core.instrumentation.LogWriter
    public void hidden(Context context, int i, int i2) {
        if (shouldDisableGenericEventLogging()) {
            return;
        }
        super.hidden(context, i, i2);
    }

    @Override // com.android.settingslib.core.instrumentation.EventLogWriter, com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, String str) {
        if (shouldDisableGenericEventLogging()) {
            return;
        }
        super.action(context, i, str);
    }

    @Override // com.android.settingslib.core.instrumentation.EventLogWriter, com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, int i2) {
        if (shouldDisableGenericEventLogging()) {
            return;
        }
        super.action(context, i, i2);
    }

    @Override // com.android.settingslib.core.instrumentation.EventLogWriter, com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, boolean z) {
        if (shouldDisableGenericEventLogging()) {
            return;
        }
        super.action(context, i, z);
    }

    private static boolean shouldDisableGenericEventLogging() {
        return !DeviceConfig.getBoolean("settings_ui", "event_logging_enabled", true);
    }
}
