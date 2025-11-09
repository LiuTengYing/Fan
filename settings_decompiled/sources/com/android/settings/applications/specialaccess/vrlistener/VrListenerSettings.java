package com.android.settings.applications.specialaccess.vrlistener;

import android.content.ComponentName;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class VrListenerSettings extends ManagedServiceSettings {
    private static final ManagedServiceSettings.Config CONFIG;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER;
    private static final String TAG;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 334;
    }

    static {
        String simpleName = VrListenerSettings.class.getSimpleName();
        TAG = simpleName;
        CONFIG = new ManagedServiceSettings.Config.Builder().setTag(simpleName).setSetting("enabled_vr_listeners").setIntentAction("android.service.vr.VrListenerService").setPermission("android.permission.BIND_VR_LISTENER_SERVICE").setNoun("vr listener").setWarningDialogTitle(R$string.vr_listener_security_warning_title).setWarningDialogSummary(R$string.vr_listener_security_warning_summary).setEmptyText(R$string.no_vr_listeners).build();
        SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.vr_listeners_settings);
    }

    @Override // com.android.settings.utils.ManagedServiceSettings
    protected ManagedServiceSettings.Config getConfig() {
        return CONFIG;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.utils.ManagedServiceSettings
    public boolean setEnabled(ComponentName componentName, String str, boolean z) {
        logSpecialPermissionChange(z, componentName.getPackageName());
        return super.setEnabled(componentName, str, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.vr_listeners_settings;
    }

    void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 772 : 773;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }
}
