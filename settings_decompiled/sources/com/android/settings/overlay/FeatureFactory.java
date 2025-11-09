package com.android.settings.overlay;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.accessibility.AccessibilityMetricsFeatureProvider;
import com.android.settings.accessibility.AccessibilitySearchFeatureProvider;
import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.biometrics.face.FaceFeatureProvider;
import com.android.settings.bluetooth.BluetoothFeatureProvider;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProvider;
import com.android.settings.enterprise.EnterprisePrivacyFeatureProvider;
import com.android.settings.fuelgauge.BatterySettingsFeatureProvider;
import com.android.settings.fuelgauge.BatteryStatusFeatureProvider;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.homepage.contextualcards.ContextualCardFeatureProvider;
import com.android.settings.localepicker.LocaleFeatureProvider;
import com.android.settings.panel.PanelFeatureProvider;
import com.android.settings.search.SearchFeatureProvider;
import com.android.settings.security.SecurityFeatureProvider;
import com.android.settings.security.SecuritySettingsFeatureProvider;
import com.android.settings.slices.SlicesFeatureProvider;
import com.android.settings.users.UserFeatureProvider;
import com.android.settings.wifi.WifiTrackerLibProvider;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public abstract class FeatureFactory {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "FeatureFactory";
    protected static Context sAppContext;
    protected static FeatureFactory sFactory;

    public abstract AccessibilityMetricsFeatureProvider getAccessibilityMetricsFeatureProvider();

    public abstract AccessibilitySearchFeatureProvider getAccessibilitySearchFeatureProvider();

    public abstract AccountFeatureProvider getAccountFeatureProvider();

    public abstract ApplicationFeatureProvider getApplicationFeatureProvider(Context context);

    public abstract AssistGestureFeatureProvider getAssistGestureFeatureProvider();

    public abstract AwareFeatureProvider getAwareFeatureProvider();

    public abstract BatterySettingsFeatureProvider getBatterySettingsFeatureProvider(Context context);

    public abstract BatteryStatusFeatureProvider getBatteryStatusFeatureProvider(Context context);

    public abstract BluetoothFeatureProvider getBluetoothFeatureProvider();

    public abstract ContextualCardFeatureProvider getContextualCardFeatureProvider(Context context);

    public abstract DashboardFeatureProvider getDashboardFeatureProvider(Context context);

    public abstract DockUpdaterFeatureProvider getDockUpdaterFeatureProvider();

    public abstract EnterprisePrivacyFeatureProvider getEnterprisePrivacyFeatureProvider(Context context);

    public abstract FaceFeatureProvider getFaceFeatureProvider();

    public abstract LocaleFeatureProvider getLocaleFeatureProvider();

    public abstract MetricsFeatureProvider getMetricsFeatureProvider();

    public abstract PanelFeatureProvider getPanelFeatureProvider();

    public abstract PowerUsageFeatureProvider getPowerUsageFeatureProvider(Context context);

    public abstract SearchFeatureProvider getSearchFeatureProvider();

    public abstract SecurityFeatureProvider getSecurityFeatureProvider();

    public abstract SecuritySettingsFeatureProvider getSecuritySettingsFeatureProvider();

    public abstract SlicesFeatureProvider getSlicesFeatureProvider();

    public abstract SuggestionFeatureProvider getSuggestionFeatureProvider(Context context);

    public abstract SupportFeatureProvider getSupportFeatureProvider(Context context);

    public abstract SurveyFeatureProvider getSurveyFeatureProvider(Context context);

    public abstract UserFeatureProvider getUserFeatureProvider(Context context);

    public abstract WifiTrackerLibProvider getWifiTrackerLibProvider();

    public static FeatureFactory getFactory(Context context) {
        FeatureFactory featureFactory = sFactory;
        if (featureFactory != null) {
            return featureFactory;
        }
        if (sAppContext == null) {
            sAppContext = context.getApplicationContext();
        }
        String string = context.getString(R$string.config_featureFactory);
        Log.d("fangli", "clsName ===" + string);
        if (TextUtils.isEmpty(string)) {
            throw new UnsupportedOperationException("No feature factory configured");
        }
        try {
            FeatureFactory featureFactory2 = (FeatureFactory) context.getClassLoader().loadClass(string).newInstance();
            sFactory = featureFactory2;
            return featureFactory2;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new FactoryNotFoundException(e);
        }
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    /* loaded from: classes.dex */
    public static final class FactoryNotFoundException extends RuntimeException {
        public FactoryNotFoundException(Throwable th) {
            super("Unable to create factory. Did you misconfigure Proguard?", th);
        }
    }
}
