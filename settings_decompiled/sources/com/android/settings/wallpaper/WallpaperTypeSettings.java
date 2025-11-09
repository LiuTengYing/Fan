package com.android.settings.wallpaper;

import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
/* loaded from: classes.dex */
public class WallpaperTypeSettings extends DashboardFragment {
    @Override // com.android.settings.dashboard.DashboardFragment
    protected String getLogTag() {
        return "WallpaperTypeSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 101;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_wallpaper;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    protected int getPreferenceScreenResId() {
        return R$xml.wallpaper_settings;
    }
}
