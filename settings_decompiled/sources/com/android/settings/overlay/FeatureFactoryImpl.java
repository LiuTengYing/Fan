package com.android.settings.overlay;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.VpnManager;
import android.os.UserManager;
import androidx.annotation.Keep;
import com.android.settings.accessibility.AccessibilityMetricsFeatureProvider;
import com.android.settings.accessibility.AccessibilityMetricsFeatureProviderImpl;
import com.android.settings.accessibility.AccessibilitySearchFeatureProvider;
import com.android.settings.accessibility.AccessibilitySearchFeatureProviderImpl;
import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.accounts.AccountFeatureProviderImpl;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.ApplicationFeatureProviderImpl;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.aware.AwareFeatureProviderImpl;
import com.android.settings.biometrics.face.FaceFeatureProvider;
import com.android.settings.biometrics.face.FaceFeatureProviderImpl;
import com.android.settings.bluetooth.BluetoothFeatureProvider;
import com.android.settings.bluetooth.BluetoothFeatureProviderImpl;
import com.android.settings.connecteddevice.dock.DockUpdaterFeatureProviderImpl;
import com.android.settings.core.instrumentation.SettingsMetricsFeatureProvider;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.dashboard.DashboardFeatureProviderImpl;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProvider;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl;
import com.android.settings.enterprise.EnterprisePrivacyFeatureProvider;
import com.android.settings.enterprise.EnterprisePrivacyFeatureProviderImpl;
import com.android.settings.fuelgauge.BatterySettingsFeatureProvider;
import com.android.settings.fuelgauge.BatterySettingsFeatureProviderImpl;
import com.android.settings.fuelgauge.BatteryStatusFeatureProvider;
import com.android.settings.fuelgauge.BatteryStatusFeatureProviderImpl;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.fuelgauge.PowerUsageFeatureProviderImpl;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.gestures.AssistGestureFeatureProviderImpl;
import com.android.settings.homepage.contextualcards.ContextualCardFeatureProvider;
import com.android.settings.homepage.contextualcards.ContextualCardFeatureProviderImpl;
import com.android.settings.localepicker.LocaleFeatureProvider;
import com.android.settings.localepicker.LocaleFeatureProviderImpl;
import com.android.settings.panel.PanelFeatureProvider;
import com.android.settings.panel.PanelFeatureProviderImpl;
import com.android.settings.search.SearchFeatureProvider;
import com.android.settings.search.SearchFeatureProviderImpl;
import com.android.settings.security.SecurityFeatureProvider;
import com.android.settings.security.SecurityFeatureProviderImpl;
import com.android.settings.security.SecuritySettingsFeatureProvider;
import com.android.settings.security.SecuritySettingsFeatureProviderImpl;
import com.android.settings.slices.SlicesFeatureProvider;
import com.android.settings.slices.SlicesFeatureProviderImpl;
import com.android.settings.users.UserFeatureProvider;
import com.android.settings.users.UserFeatureProviderImpl;
import com.android.settings.wifi.WifiTrackerLibProvider;
import com.android.settings.wifi.WifiTrackerLibProviderImpl;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
@Keep
/* loaded from: classes.dex */
public class FeatureFactoryImpl extends FeatureFactory {
    private AccessibilityMetricsFeatureProvider mAccessibilityMetricsFeatureProvider;
    private AccessibilitySearchFeatureProvider mAccessibilitySearchFeatureProvider;
    private AccountFeatureProvider mAccountFeatureProvider;
    private ApplicationFeatureProvider mApplicationFeatureProvider;
    private AssistGestureFeatureProvider mAssistGestureFeatureProvider;
    private AwareFeatureProvider mAwareFeatureProvider;
    private BatterySettingsFeatureProvider mBatterySettingsFeatureProvider;
    private BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;
    private BluetoothFeatureProvider mBluetoothFeatureProvider;
    private ContextualCardFeatureProvider mContextualCardFeatureProvider;
    private DashboardFeatureProviderImpl mDashboardFeatureProvider;
    private DockUpdaterFeatureProvider mDockUpdaterFeatureProvider;
    private EnterprisePrivacyFeatureProvider mEnterprisePrivacyFeatureProvider;
    private FaceFeatureProvider mFaceFeatureProvider;
    private LocaleFeatureProvider mLocaleFeatureProvider;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private PanelFeatureProvider mPanelFeatureProvider;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    private SearchFeatureProvider mSearchFeatureProvider;
    private SecurityFeatureProvider mSecurityFeatureProvider;
    private SecuritySettingsFeatureProvider mSecuritySettingsFeatureProvider;
    private SlicesFeatureProvider mSlicesFeatureProvider;
    private SuggestionFeatureProvider mSuggestionFeatureProvider;
    private UserFeatureProvider mUserFeatureProvider;
    private WifiTrackerLibProvider mWifiTrackerLibProvider;

    @Override // com.android.settings.overlay.FeatureFactory
    public SupportFeatureProvider getSupportFeatureProvider(Context context) {
        return null;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public SurveyFeatureProvider getSurveyFeatureProvider(Context context) {
        return null;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public MetricsFeatureProvider getMetricsFeatureProvider() {
        if (this.mMetricsFeatureProvider == null) {
            this.mMetricsFeatureProvider = new SettingsMetricsFeatureProvider();
        }
        return this.mMetricsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public PowerUsageFeatureProvider getPowerUsageFeatureProvider(Context context) {
        if (this.mPowerUsageFeatureProvider == null) {
            this.mPowerUsageFeatureProvider = new PowerUsageFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mPowerUsageFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public BatteryStatusFeatureProvider getBatteryStatusFeatureProvider(Context context) {
        if (this.mBatteryStatusFeatureProvider == null) {
            this.mBatteryStatusFeatureProvider = new BatteryStatusFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mBatteryStatusFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public BatterySettingsFeatureProvider getBatterySettingsFeatureProvider(Context context) {
        if (this.mBatterySettingsFeatureProvider == null) {
            this.mBatterySettingsFeatureProvider = new BatterySettingsFeatureProviderImpl(context);
        }
        return this.mBatterySettingsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public DashboardFeatureProvider getDashboardFeatureProvider(Context context) {
        if (this.mDashboardFeatureProvider == null) {
            this.mDashboardFeatureProvider = new DashboardFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mDashboardFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public DockUpdaterFeatureProvider getDockUpdaterFeatureProvider() {
        if (this.mDockUpdaterFeatureProvider == null) {
            this.mDockUpdaterFeatureProvider = new DockUpdaterFeatureProviderImpl();
        }
        return this.mDockUpdaterFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public ApplicationFeatureProvider getApplicationFeatureProvider(Context context) {
        if (this.mApplicationFeatureProvider == null) {
            Context applicationContext = context.getApplicationContext();
            this.mApplicationFeatureProvider = new ApplicationFeatureProviderImpl(applicationContext, applicationContext.getPackageManager(), AppGlobals.getPackageManager(), (DevicePolicyManager) applicationContext.getSystemService("device_policy"));
        }
        return this.mApplicationFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public LocaleFeatureProvider getLocaleFeatureProvider() {
        if (this.mLocaleFeatureProvider == null) {
            this.mLocaleFeatureProvider = new LocaleFeatureProviderImpl();
        }
        return this.mLocaleFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public EnterprisePrivacyFeatureProvider getEnterprisePrivacyFeatureProvider(Context context) {
        if (this.mEnterprisePrivacyFeatureProvider == null) {
            Context applicationContext = context.getApplicationContext();
            this.mEnterprisePrivacyFeatureProvider = new EnterprisePrivacyFeatureProviderImpl(applicationContext, (DevicePolicyManager) applicationContext.getSystemService("device_policy"), applicationContext.getPackageManager(), UserManager.get(applicationContext), (ConnectivityManager) applicationContext.getSystemService(ConnectivityManager.class), (VpnManager) applicationContext.getSystemService(VpnManager.class), applicationContext.getResources());
        }
        return this.mEnterprisePrivacyFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public SearchFeatureProvider getSearchFeatureProvider() {
        if (this.mSearchFeatureProvider == null) {
            this.mSearchFeatureProvider = new SearchFeatureProviderImpl();
        }
        return this.mSearchFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public SecurityFeatureProvider getSecurityFeatureProvider() {
        if (this.mSecurityFeatureProvider == null) {
            this.mSecurityFeatureProvider = new SecurityFeatureProviderImpl();
        }
        return this.mSecurityFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public SuggestionFeatureProvider getSuggestionFeatureProvider(Context context) {
        if (this.mSuggestionFeatureProvider == null) {
            this.mSuggestionFeatureProvider = new SuggestionFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mSuggestionFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public UserFeatureProvider getUserFeatureProvider(Context context) {
        if (this.mUserFeatureProvider == null) {
            this.mUserFeatureProvider = new UserFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mUserFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public AssistGestureFeatureProvider getAssistGestureFeatureProvider() {
        if (this.mAssistGestureFeatureProvider == null) {
            this.mAssistGestureFeatureProvider = new AssistGestureFeatureProviderImpl();
        }
        return this.mAssistGestureFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public SlicesFeatureProvider getSlicesFeatureProvider() {
        if (this.mSlicesFeatureProvider == null) {
            this.mSlicesFeatureProvider = new SlicesFeatureProviderImpl();
        }
        return this.mSlicesFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public AccountFeatureProvider getAccountFeatureProvider() {
        if (this.mAccountFeatureProvider == null) {
            this.mAccountFeatureProvider = new AccountFeatureProviderImpl();
        }
        return this.mAccountFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public PanelFeatureProvider getPanelFeatureProvider() {
        if (this.mPanelFeatureProvider == null) {
            this.mPanelFeatureProvider = new PanelFeatureProviderImpl();
        }
        return this.mPanelFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public ContextualCardFeatureProvider getContextualCardFeatureProvider(Context context) {
        if (this.mContextualCardFeatureProvider == null) {
            this.mContextualCardFeatureProvider = new ContextualCardFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mContextualCardFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public BluetoothFeatureProvider getBluetoothFeatureProvider() {
        if (this.mBluetoothFeatureProvider == null) {
            this.mBluetoothFeatureProvider = new BluetoothFeatureProviderImpl(FeatureFactory.getAppContext());
        }
        return this.mBluetoothFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public AwareFeatureProvider getAwareFeatureProvider() {
        if (this.mAwareFeatureProvider == null) {
            this.mAwareFeatureProvider = new AwareFeatureProviderImpl();
        }
        return this.mAwareFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public FaceFeatureProvider getFaceFeatureProvider() {
        if (this.mFaceFeatureProvider == null) {
            this.mFaceFeatureProvider = new FaceFeatureProviderImpl();
        }
        return this.mFaceFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public WifiTrackerLibProvider getWifiTrackerLibProvider() {
        if (this.mWifiTrackerLibProvider == null) {
            this.mWifiTrackerLibProvider = new WifiTrackerLibProviderImpl();
        }
        return this.mWifiTrackerLibProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public SecuritySettingsFeatureProvider getSecuritySettingsFeatureProvider() {
        if (this.mSecuritySettingsFeatureProvider == null) {
            this.mSecuritySettingsFeatureProvider = new SecuritySettingsFeatureProviderImpl();
        }
        return this.mSecuritySettingsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public AccessibilitySearchFeatureProvider getAccessibilitySearchFeatureProvider() {
        if (this.mAccessibilitySearchFeatureProvider == null) {
            this.mAccessibilitySearchFeatureProvider = new AccessibilitySearchFeatureProviderImpl();
        }
        return this.mAccessibilitySearchFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactory
    public AccessibilityMetricsFeatureProvider getAccessibilityMetricsFeatureProvider() {
        if (this.mAccessibilityMetricsFeatureProvider == null) {
            this.mAccessibilityMetricsFeatureProvider = new AccessibilityMetricsFeatureProviderImpl();
        }
        return this.mAccessibilityMetricsFeatureProvider;
    }
}
