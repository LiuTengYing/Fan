package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.BidiFormatter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.RelativeSizeSpan;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.datausage.BillingCycleSettings;
import com.android.settings.datausage.lib.DataUsageLib;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.network.SubscriptionUtil;
import com.android.settingslib.NetworkPolicyEditor;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DataUsageSummary extends DataUsageBaseFragment implements DataUsageEditController {
    private BillingCyclePreference mBillingCyclePreferences;
    private NetworkTemplate mDefaultTemplate;
    private ProxySubscriptionManager mProxySubscriptionMgr;
    private DataUsageSummaryPreferenceController mSummaryController;
    private DataUsageSummaryPreference mSummaryPreference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DataUsageSummary";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 37;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_data_usage;
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        enableProxySubscriptionManager(context);
        boolean hasMobileData = DataUsageUtils.hasMobileData(context);
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (defaultDataSubscriptionId == -1) {
            hasMobileData = false;
        }
        this.mDefaultTemplate = DataUsageUtils.getDefaultTemplate(context, defaultDataSubscriptionId);
        this.mSummaryPreference = (DataUsageSummaryPreference) findPreference("status_header");
        if (!hasMobileData || !isAdmin()) {
            removePreference("restrict_background");
        }
        boolean hasWifiRadio = DataUsageUtils.hasWifiRadio(context);
        if (hasMobileData) {
            addMobileSection(defaultDataSubscriptionId);
            if (hasActiveSubscription() && hasWifiRadio) {
                addWifiSection();
            }
        } else if (hasWifiRadio) {
            addWifiSection();
        }
        if (DataUsageUtils.hasEthernet(context)) {
            addEthernetSection();
        }
        setHasOptionsMenu(true);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == findPreference("status_header")) {
            BillingCycleSettings.BytesEditorFragment.show((DataUsageEditController) this, false);
            return false;
        }
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.data_usage;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        ArrayList arrayList = new ArrayList();
        DataUsageSummaryPreferenceController dataUsageSummaryPreferenceController = new DataUsageSummaryPreferenceController(activity, getSettingsLifecycle(), this, DataUsageUtils.getDefaultSubscriptionId(activity));
        this.mSummaryController = dataUsageSummaryPreferenceController;
        arrayList.add(dataUsageSummaryPreferenceController);
        getSettingsLifecycle().addObserver(this.mSummaryController);
        return arrayList;
    }

    void addMobileSection(int i) {
        addMobileSection(i, null);
    }

    void enableProxySubscriptionManager(Context context) {
        ProxySubscriptionManager proxySubscriptionManager = ProxySubscriptionManager.getInstance(context);
        this.mProxySubscriptionMgr = proxySubscriptionManager;
        proxySubscriptionManager.setLifecycle(getLifecycle());
    }

    boolean hasActiveSubscription() {
        List<SubscriptionInfo> activeSubscriptionsInfo = this.mProxySubscriptionMgr.getActiveSubscriptionsInfo();
        return activeSubscriptionsInfo != null && activeSubscriptionsInfo.size() > 0;
    }

    private void addMobileSection(int i, SubscriptionInfo subscriptionInfo) {
        TemplatePreferenceCategory templatePreferenceCategory = (TemplatePreferenceCategory) inflatePreferences(R$xml.data_usage_cellular);
        templatePreferenceCategory.setTemplate(DataUsageLib.getMobileTemplate(getContext(), i), i, this.services);
        templatePreferenceCategory.pushTemplates(this.services);
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, getContext());
        if (subscriptionInfo != null && !TextUtils.isEmpty(uniqueSubscriptionDisplayName)) {
            templatePreferenceCategory.findPreference("mobile_category").setTitle(uniqueSubscriptionDisplayName);
        }
        this.mBillingCyclePreferences = (BillingCyclePreference) templatePreferenceCategory.findPreference("billing_preference");
    }

    void addWifiSection() {
        ((TemplatePreferenceCategory) inflatePreferences(R$xml.data_usage_wifi)).setTemplate(new NetworkTemplate.Builder(4).build(), 0, this.services);
    }

    private void addEthernetSection() {
        ((TemplatePreferenceCategory) inflatePreferences(R$xml.data_usage_ethernet)).setTemplate(new NetworkTemplate.Builder(5).build(), 0, this.services);
    }

    private Preference inflatePreferences(int i) {
        PreferenceScreen inflateFromResource = getPreferenceManager().inflateFromResource(getPrefContext(), i, null);
        Preference preference = inflateFromResource.getPreference(0);
        inflateFromResource.removeAll();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preference.setOrder(preferenceScreen.getPreferenceCount());
        preferenceScreen.addPreference(preference);
        return preference;
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateState();
        this.mBillingCyclePreferences.onMobileDataEnabledChange();
    }

    static CharSequence formatUsage(Context context, String str, long j) {
        return formatUsage(context, str, j, 1.5625f, 0.64f);
    }

    static CharSequence formatUsage(Context context, String str, long j, float f, float f2) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 10);
        SpannableString spannableString = new SpannableString(formatBytes.value);
        spannableString.setSpan(new RelativeSizeSpan(f), 0, spannableString.length(), 18);
        CharSequence expandTemplate = TextUtils.expandTemplate(new SpannableString(context.getString(17040321).replace("%1$s", "^1").replace("%2$s", "^2")), spannableString, formatBytes.units);
        SpannableString spannableString2 = new SpannableString(str);
        spannableString2.setSpan(new RelativeSizeSpan(f2), 0, spannableString2.length(), 18);
        return TextUtils.expandTemplate(spannableString2, BidiFormatter.getInstance().unicodeWrap(expandTemplate.toString()));
    }

    private void updateState() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (int i = 1; i < preferenceScreen.getPreferenceCount(); i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (preference instanceof TemplatePreferenceCategory) {
                ((TemplatePreferenceCategory) preference).pushTemplates(this.services);
            }
        }
    }

    @Override // com.android.settings.datausage.DataUsageEditController
    public NetworkPolicyEditor getNetworkPolicyEditor() {
        return this.services.mPolicyEditor;
    }

    @Override // com.android.settings.datausage.DataUsageEditController
    public NetworkTemplate getNetworkTemplate() {
        return this.mDefaultTemplate;
    }

    @Override // com.android.settings.datausage.DataUsageEditController
    public void updateDataUsage() {
        updateState();
        this.mSummaryController.updateState(this.mSummaryPreference);
    }
}
