package com.android.settings.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class SpatialAudioSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.spatial_audio_settings);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SpatialAudioSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1921;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        FooterPreference footerPreference = (FooterPreference) findPreference("spatial_audio_footer");
        if (footerPreference != null) {
            footerPreference.setLearnMoreText(getString(R$string.spatial_audio_footer_learn_more_text));
            footerPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.notification.SpatialAudioSettings$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SpatialAudioSettings.this.lambda$onCreatePreferences$0(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePreferences$0(View view) {
        startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.spatial_audio_settings;
    }
}
