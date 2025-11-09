package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UniCarrierConfigManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.annotation.Keep;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.telephony.OperatorInfo;
import com.android.settings.R$bool;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.telephony.NetworkScanHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import com.unisoc.settings.network.ProxySimStateManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
@Keep
/* loaded from: classes.dex */
public class NetworkSelectSettings extends DashboardFragment {
    private static final int EVENT_NETWORK_SCAN_COMPLETED = 4;
    private static final int EVENT_NETWORK_SCAN_ERROR = 3;
    private static final int EVENT_NETWORK_SCAN_RESULTS = 2;
    private static final int EVENT_SET_NETWORK_SELECTION_MANUALLY_DONE = 1;
    private static final int MIN_NUMBER_OF_SCAN_REQUIRED = 2;
    private static final String PREF_KEY_NETWORK_OPERATORS = "network_operators_preference";
    private static final String TAG = "NetworkSelectSettings";
    private AsyncTask<Void, Void, List<String>> mAsyncTask;
    private CellIdentity mCellIdentity;
    List<CellInfo> mCellInfoList;
    private boolean mClickBeforeSearch;
    private boolean mCustomDisplayRequirements;
    private List<String> mForbiddenPlmns;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private String mNetworkOperator;
    private NetworkScanHelper mNetworkScanHelper;
    private boolean mOemPermanentAutoSelMode;
    private boolean mOemRestoreAutoMode;
    private ProxySimStateManager.OnSimStateChangedListener mOnSimStateChangeListener;
    private int mPhoneId;
    private PreferenceCategory mPreferenceCategory;
    private View mProgressHeader;
    private ProxySimStateManager mProxySimStateMgr;
    private long mRequestIdManualNetworkScan;
    private long mRequestIdManualNetworkSelect;
    NetworkOperatorPreference mSelectedPreference;
    private boolean mShowNetworkSelectionFailed;
    private Preference mStatusMessagePreference;
    private TelephonyManager mTelephonyManager;
    private UniCarrierConfigManager mUniCarrierConfigManager;
    private UniNetworkScanHelper mUniNetworkScanHelper;
    private boolean mUseNewApi;
    private long mWaitingForNumberOfScanResults;
    private int mSubId = -1;
    private boolean mShow4GForLTE = false;
    private final ExecutorService mNetworkScanExecutor = Executors.newFixedThreadPool(1);
    boolean mIsAggregationEnabled = false;
    private AlertDialog mAlertDialog = null;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i;
            int i2 = message.what;
            if (i2 == 1) {
                NetworkSelectSettings.this.mClickBeforeSearch = false;
                boolean booleanValue = ((Boolean) message.obj).booleanValue();
                NetworkSelectSettings.this.stopNetworkQuery();
                NetworkSelectSettings.this.setProgressBarVisible(false);
                NetworkSelectSettings.this.enablePreferenceScreen(true);
                NetworkOperatorPreference networkOperatorPreference = NetworkSelectSettings.this.mSelectedPreference;
                if (networkOperatorPreference != null) {
                    if (booleanValue) {
                        i = R$string.network_connected;
                    } else {
                        i = R$string.network_could_not_connect;
                    }
                    networkOperatorPreference.setSummary(i);
                } else {
                    Log.e(NetworkSelectSettings.TAG, "No preference to update!");
                }
                if (NetworkSelectSettings.this.mShowNetworkSelectionFailed) {
                    if (booleanValue) {
                        return;
                    }
                    NetworkSelectSettings.this.showNetworkSelectionFailed();
                    return;
                }
                Intent intent = new Intent();
                if (booleanValue) {
                    if (NetworkSelectSettings.this.mOemRestoreAutoMode) {
                        intent.putExtra("manual_select_success", false);
                        NetworkSelectSettings.this.setResult(-1, intent);
                        NetworkSelectSettings.this.finish();
                    }
                } else if (NetworkSelectSettings.this.mOemPermanentAutoSelMode) {
                    intent.putExtra("manual_select_success", false);
                    NetworkSelectSettings.this.setResult(-1, intent);
                    NetworkSelectSettings.this.finish();
                } else {
                    intent.putExtra("manual_select_success", true);
                    NetworkSelectSettings.this.setResult(-1, intent);
                }
            } else if (i2 == 2) {
                NetworkSelectSettings.this.scanResultHandler((List) message.obj);
            } else if (i2 == 3) {
                NetworkSelectSettings.this.mClickBeforeSearch = false;
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.i(NetworkSelectSettings.TAG, "Network scan failure " + message.arg1 + ": scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    return;
                }
                if (!NetworkSelectSettings.this.isPreferenceScreenEnabled()) {
                    NetworkSelectSettings.this.clearPreferenceSummary();
                    NetworkSelectSettings.this.enablePreferenceScreen(true);
                    return;
                }
                NetworkSelectSettings.this.addMessagePreference(R$string.network_query_error);
            } else if (i2 != 4) {
            } else {
                NetworkSelectSettings.this.mClickBeforeSearch = false;
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.d(NetworkSelectSettings.TAG, "Network scan complete: scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    return;
                }
                if (!NetworkSelectSettings.this.isPreferenceScreenEnabled()) {
                    NetworkSelectSettings.this.clearPreferenceSummary();
                    NetworkSelectSettings.this.enablePreferenceScreen(true);
                    return;
                }
                List<CellInfo> list = NetworkSelectSettings.this.mCellInfoList;
                if (list == null || list.isEmpty()) {
                    NetworkSelectSettings.this.addMessagePreference(R$string.empty_networks_list);
                }
            }
        }
    };
    private final NetworkScanHelper.NetworkScanCallback mCallback = new NetworkScanHelper.NetworkScanCallback() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.3
        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onResults(List<CellInfo> list) {
            NetworkSelectSettings.this.mHandler.obtainMessage(2, list).sendToTarget();
        }

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onComplete() {
            NetworkSelectSettings.this.mHandler.obtainMessage(4).sendToTarget();
        }

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onError(int i) {
            NetworkSelectSettings.this.mHandler.obtainMessage(3, i, 0).sendToTarget();
        }
    };
    LinkedHashMap<String, CellInfo> mCellInfoMap = new LinkedHashMap<>();
    LinkedHashMap<String, CellInfo> mCellInfoMapForPlmn = new LinkedHashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$scanResultHandler$2(CellInfo cellInfo) {
        return cellInfo == null;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public /* bridge */ /* synthetic */ int getHelpResource() {
        return super.getHelpResource();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1581;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onCreateInitialization();
    }

    @Keep
    protected void onCreateInitialization() {
        this.mUseNewApi = enableNewAutoSelectNetworkUI(getContext());
        int i = getArguments().getInt("android.provider.extra.SUB_ID");
        this.mSubId = i;
        this.mPhoneId = SubscriptionManager.getPhoneId(i);
        this.mPreferenceCategory = getPreferenceCategory(PREF_KEY_NETWORK_OPERATORS);
        Preference preference = new Preference(getContext());
        this.mStatusMessagePreference = preference;
        preference.setSelectable(false);
        this.mSelectedPreference = null;
        this.mClickBeforeSearch = true;
        TelephonyManager telephonyManager = getTelephonyManager(getContext(), this.mSubId);
        this.mTelephonyManager = telephonyManager;
        NetworkScanHelper networkScanHelper = new NetworkScanHelper(telephonyManager, this.mCallback, this.mNetworkScanExecutor, getContext());
        this.mNetworkScanHelper = networkScanHelper;
        this.mUniNetworkScanHelper = new UniNetworkScanHelper(this.mTelephonyManager, networkScanHelper, getContext());
        PersistableBundle configForSubId = getCarrierConfigManager(getContext()).getConfigForSubId(this.mSubId);
        if (configForSubId != null) {
            this.mShow4GForLTE = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
        UniCarrierConfigManager uniCarrierConfigManager = new UniCarrierConfigManager(getContext());
        this.mUniCarrierConfigManager = uniCarrierConfigManager;
        PersistableBundle configForSubId2 = uniCarrierConfigManager.getConfigForSubId(this.mSubId);
        if (configForSubId2 != null) {
            this.mShowNetworkSelectionFailed = configForSubId2.getBoolean("show_network_selection_fail");
            this.mOemRestoreAutoMode = configForSubId2.getBoolean("oem_key_restore_auto_mode");
            this.mOemPermanentAutoSelMode = configForSubId2.getBoolean("oem_key_permanent_auto_sel_mode_bool");
        }
        this.mCustomDisplayRequirements = getContext().getResources().getBoolean(R$bool.network_scan_custom_display_results);
        ProxySimStateManager proxySimStateManager = ProxySimStateManager.getInstance(getContext());
        this.mProxySimStateMgr = proxySimStateManager;
        proxySimStateManager.setLifecycle(getLifecycle());
        ProxySimStateManager.OnSimStateChangedListener onSimStateChangedListener = new ProxySimStateManager.OnSimStateChangedListener() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.1
            @Override // com.unisoc.settings.network.ProxySimStateManager.OnSimStateChangedListener
            public void onChanged() {
                String simState = NetworkSelectSettings.this.mProxySimStateMgr.getSimState(NetworkSelectSettings.this.mPhoneId);
                Log.d(NetworkSelectSettings.TAG, "onChange: [" + NetworkSelectSettings.this.mPhoneId + ", " + simState + "]");
                if ("ABSENT".equals(simState)) {
                    NetworkSelectSettings.this.finish();
                }
            }
        };
        this.mOnSimStateChangeListener = onSimStateChangedListener;
        this.mProxySimStateMgr.addSimStateChangeListener(onSimStateChangedListener);
        this.mMetricsFeatureProvider = getMetricsFeatureProvider(getContext());
        this.mIsAggregationEnabled = enableAggregation(getContext());
        Log.d(TAG, "init: mUseNewApi:" + this.mUseNewApi + " ,mIsAggregationEnabled:" + this.mIsAggregationEnabled + " ,mSubId:" + this.mSubId);
    }

    @Keep
    protected boolean enableNewAutoSelectNetworkUI(Context context) {
        return context.getResources().getBoolean(17891642);
    }

    @Keep
    protected boolean enableAggregation(Context context) {
        return context.getResources().getBoolean(R$bool.config_network_selection_list_aggregation_enabled);
    }

    @Keep
    protected PreferenceCategory getPreferenceCategory(String str) {
        return (PreferenceCategory) findPreference(str);
    }

    @Keep
    protected TelephonyManager getTelephonyManager(Context context, int i) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }

    @Keep
    protected CarrierConfigManager getCarrierConfigManager(Context context) {
        return (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    @Keep
    protected MetricsFeatureProvider getMetricsFeatureProvider(Context context) {
        return FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Keep
    protected boolean isPreferenceScreenEnabled() {
        return getPreferenceScreen().isEnabled();
    }

    @Keep
    protected void enablePreferenceScreen(boolean z) {
        getPreferenceScreen().setEnabled(z);
    }

    @Keep
    protected int getSubId() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            return intent.getIntExtra("android.provider.extra.SUB_ID", -1);
        }
        return -1;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity() != null) {
            this.mProgressHeader = setPinnedHeaderView(R$layout.progress_header).findViewById(R$id.progress_bar_animation);
            setProgressBarVisible(false);
        }
        startNetworkQuery();
        forceUpdateConnectedPreferenceCategory();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Keep
    protected void updateForbiddenPlmns() {
        List<String> arrayList;
        String[] forbiddenPlmns = this.mTelephonyManager.getForbiddenPlmns();
        if (forbiddenPlmns != null) {
            arrayList = Arrays.asList(forbiddenPlmns);
        } else {
            arrayList = new ArrayList<>();
        }
        this.mForbiddenPlmns = arrayList;
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            Log.d(TAG, "fplmn = " + it.next());
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        final OperatorInfo operatorInfoFromCellInfo;
        if (preference == this.mSelectedPreference || !MobileNetworkUtils.isNetworkSelectEnabled(getContext())) {
            return true;
        }
        stopNetworkQuery();
        clearPreferenceSummary();
        NetworkOperatorPreference networkOperatorPreference = this.mSelectedPreference;
        if (networkOperatorPreference != null) {
            networkOperatorPreference.setSummary(R$string.network_disconnected);
        }
        NetworkOperatorPreference networkOperatorPreference2 = (NetworkOperatorPreference) preference;
        this.mSelectedPreference = networkOperatorPreference2;
        networkOperatorPreference2.setSummary(R$string.network_connecting);
        this.mMetricsFeatureProvider.action(getContext(), 1210, new Pair[0]);
        setProgressBarVisible(true);
        enablePreferenceScreen(false);
        this.mRequestIdManualNetworkSelect = getNewRequestId();
        this.mWaitingForNumberOfScanResults = 2L;
        CellInfo cellInfo = this.mSelectedPreference.getCellInfo();
        if (this.mClickBeforeSearch) {
            operatorInfoFromCellInfo = CellInfoUtil.getOperatorInfoFromCellInfo(this.mCellIdentity, this.mNetworkOperator);
        } else {
            operatorInfoFromCellInfo = CellInfoUtil.getOperatorInfoFromCellInfo(cellInfo);
        }
        Log.d(TAG, "manually selected network:" + operatorInfoFromCellInfo.toString());
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.NetworkSelectSettings$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                NetworkSelectSettings.this.lambda$onPreferenceTreeClick$0(operatorInfoFromCellInfo);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceTreeClick$0(OperatorInfo operatorInfo) {
        Message obtainMessage = this.mHandler.obtainMessage(1);
        obtainMessage.obj = Boolean.valueOf(this.mTelephonyManager.setNetworkSelectionModeManual(operatorInfo, true));
        obtainMessage.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.choose_network;
    }

    List<CellInfo> doAggregation(List<CellInfo> list) {
        if (!this.mIsAggregationEnabled) {
            Log.d(TAG, "no aggregation");
            return new ArrayList(list);
        }
        ArrayList arrayList = new ArrayList();
        for (CellInfo cellInfo : list) {
            final String networkTitle = CellInfoUtil.getNetworkTitle(cellInfo.getCellIdentity(), CellInfoUtil.getCellIdentityMccMnc(cellInfo.getCellIdentity()));
            final Class<?> cls = cellInfo.getClass();
            Optional findFirst = arrayList.stream().filter(new Predicate() { // from class: com.android.settings.network.telephony.NetworkSelectSettings$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$doAggregation$1;
                    lambda$doAggregation$1 = NetworkSelectSettings.lambda$doAggregation$1(networkTitle, cls, (CellInfo) obj);
                    return lambda$doAggregation$1;
                }
            }).findFirst();
            if (findFirst.isPresent()) {
                if (cellInfo.isRegistered() && !((CellInfo) findFirst.get()).isRegistered()) {
                    arrayList.set(arrayList.indexOf(findFirst.get()), cellInfo);
                }
            } else {
                arrayList.add(cellInfo);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$doAggregation$1(String str, Class cls, CellInfo cellInfo) {
        return CellInfoUtil.getNetworkTitle(cellInfo.getCellIdentity(), CellInfoUtil.getCellIdentityMccMnc(cellInfo.getCellIdentity())).equals(str) && cellInfo.getClass().equals(cls);
    }

    @Keep
    protected void scanResultHandler(List<CellInfo> list) {
        this.mClickBeforeSearch = false;
        if (this.mRequestIdManualNetworkScan < this.mRequestIdManualNetworkSelect) {
            Log.d(TAG, "CellInfoList (drop): " + CellInfoUtil.cellInfoListToString(new ArrayList(list)));
            return;
        }
        this.mWaitingForNumberOfScanResults--;
        list.removeIf(new Predicate() { // from class: com.android.settings.network.telephony.NetworkSelectSettings$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$scanResultHandler$2;
                lambda$scanResultHandler$2 = NetworkSelectSettings.lambda$scanResultHandler$2((CellInfo) obj);
                return lambda$scanResultHandler$2;
            }
        });
        List<CellInfo> aggregateCellInfoList = aggregateCellInfoList(list);
        if (this.mCustomDisplayRequirements) {
            aggregateCellInfoList = aggregateCellInfoListEx(aggregateCellInfoList);
        }
        Log.d(TAG, "CellInfoList after aggregation: " + CellInfoUtil.cellInfoListToString(aggregateCellInfoList));
        ArrayList arrayList = new ArrayList(aggregateCellInfoList);
        this.mCellInfoList = arrayList;
        if (arrayList.size() != 0) {
            NetworkOperatorPreference updateAllPreferenceCategory = updateAllPreferenceCategory();
            if (updateAllPreferenceCategory != null) {
                if (this.mSelectedPreference != null) {
                    this.mSelectedPreference = updateAllPreferenceCategory;
                }
            } else if (isPreferenceScreenEnabled() || updateAllPreferenceCategory != null) {
            } else {
                this.mSelectedPreference.setSummary(R$string.network_connecting);
            }
        }
    }

    @Keep
    protected NetworkOperatorPreference createNetworkOperatorPreference(CellInfo cellInfo) {
        return new NetworkOperatorPreference(getPrefContext(), cellInfo, this.mForbiddenPlmns, this.mShow4GForLTE);
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0109  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.android.settings.network.telephony.NetworkOperatorPreference updateAllPreferenceCategory() {
        /*
            Method dump skipped, instructions count: 273
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.NetworkSelectSettings.updateAllPreferenceCategory():com.android.settings.network.telephony.NetworkOperatorPreference");
    }

    private void forceUpdateConnectedPreferenceCategory() {
        Log.d(TAG, "forceUpdateConnectedPreferenceCategory");
        if (this.mTelephonyManager.getDataState() == 2) {
            Log.d(TAG, "forceUpdateConnectedPreferenceCategory: data connected");
            ServiceState serviceState = this.mTelephonyManager.getServiceState();
            if (serviceState == null) {
                return;
            }
            List<NetworkRegistrationInfo> networkRegistrationInfoListForTransportType = serviceState.getNetworkRegistrationInfoListForTransportType(1);
            if (networkRegistrationInfoListForTransportType == null || networkRegistrationInfoListForTransportType.size() == 0) {
                Log.d(TAG, "forceUpdateConnectedPreferenceCategory: networkList = " + networkRegistrationInfoListForTransportType);
                return;
            }
            if (this.mForbiddenPlmns == null) {
                updateForbiddenPlmns();
            }
            for (NetworkRegistrationInfo networkRegistrationInfo : networkRegistrationInfoListForTransportType) {
                Log.d(TAG, "forceUpdateConnectedPreferenceCategory: regInfo = " + networkRegistrationInfo);
                CellIdentity cellIdentity = networkRegistrationInfo.getCellIdentity();
                if (cellIdentity != null) {
                    NetworkOperatorPreference networkOperatorPreference = new NetworkOperatorPreference(getPrefContext(), cellIdentity, this.mForbiddenPlmns, this.mShow4GForLTE);
                    if (!networkOperatorPreference.isForbiddenNetwork()) {
                        networkOperatorPreference.setSummary(R$string.network_connected);
                        if (this.mClickBeforeSearch) {
                            setOperatorInfo(cellIdentity, serviceState);
                            networkOperatorPreference.setIcon(4, cellIdentity);
                        } else {
                            networkOperatorPreference.setIcon(4);
                        }
                        Log.d(TAG, "add a connected preference");
                        this.mPreferenceCategory.addPreference(networkOperatorPreference);
                        return;
                    }
                }
            }
            return;
        }
        Log.d(TAG, "forceUpdateConnectedPreferenceCategory: data not connected");
    }

    private void setOperatorInfo(CellIdentity cellIdentity, ServiceState serviceState) {
        this.mCellIdentity = cellIdentity;
        this.mNetworkOperator = serviceState.getOperatorNumeric();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearPreferenceSummary() {
        int preferenceCount = this.mPreferenceCategory.getPreferenceCount();
        while (preferenceCount > 0) {
            preferenceCount--;
            ((NetworkOperatorPreference) this.mPreferenceCategory.getPreference(preferenceCount)).setSummary((CharSequence) null);
        }
    }

    private long getNewRequestId() {
        return Math.max(this.mRequestIdManualNetworkSelect, this.mRequestIdManualNetworkScan) + 1;
    }

    private boolean isProgressBarVisible() {
        View view = this.mProgressHeader;
        return view != null && view.getVisibility() == 0;
    }

    protected void setProgressBarVisible(boolean z) {
        View view = this.mProgressHeader;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addMessagePreference(int i) {
        setProgressBarVisible(false);
        this.mStatusMessagePreference.setTitle(i);
        this.mPreferenceCategory.removeAll();
        this.mPreferenceCategory.addPreference(this.mStatusMessagePreference);
    }

    private void startNetworkQuery() {
        setProgressBarVisible(true);
        this.mAsyncTask = new AsyncTask<Void, Void, List<String>>() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<String> doInBackground(Void... voidArr) {
                NetworkSelectSettings.this.updateForbiddenPlmns();
                return NetworkSelectSettings.this.mForbiddenPlmns;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<String> list) {
                if (NetworkSelectSettings.this.mUniNetworkScanHelper == null || isCancelled()) {
                    return;
                }
                NetworkSelectSettings.this.mUniNetworkScanHelper.startNetworkScan();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        if (this.mNetworkScanHelper != null) {
            this.mRequestIdManualNetworkScan = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopNetworkQuery() {
        setProgressBarVisible(false);
        NetworkScanHelper networkScanHelper = this.mNetworkScanHelper;
        if (networkScanHelper != null) {
            this.mWaitingForNumberOfScanResults = 0L;
            networkScanHelper.stopNetworkQuery();
        }
        UniNetworkScanHelper uniNetworkScanHelper = this.mUniNetworkScanHelper;
        if (uniNetworkScanHelper != null) {
            uniNetworkScanHelper.enableData();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        AsyncTask<Void, Void, List<String>> asyncTask = this.mAsyncTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mAsyncTask = null;
        }
        stopNetworkQuery();
        this.mNetworkScanExecutor.shutdown();
        UniNetworkScanHelper uniNetworkScanHelper = this.mUniNetworkScanHelper;
        if (uniNetworkScanHelper != null) {
            uniNetworkScanHelper.onDestroy();
        }
        Handler handler = this.mHandler;
        if (handler != null && handler.hasMessagesOrCallbacks()) {
            this.mHandler.removeCallbacksAndMessages(null);
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
        }
        this.mProxySimStateMgr.removeSimStateChangeListener(this.mOnSimStateChangeListener);
        super.onDestroy();
    }

    private List<CellInfo> aggregateCellInfoList(List<CellInfo> list) {
        for (CellInfo cellInfo : list) {
            String str = CellInfoUtil.getOperatorInfoFromCellInfo(cellInfo).getOperatorNumeric() + " " + String.valueOf(CellInfoUtil.getCellType(cellInfo));
            if (!this.mCellInfoMap.containsKey(str)) {
                this.mCellInfoMap.put(str, cellInfo);
            } else {
                if (this.mCellInfoMap.get(str).getCellSignalStrength().getLevel() < cellInfo.getCellSignalStrength().getLevel()) {
                    this.mCellInfoMap.put(str, cellInfo);
                }
            }
        }
        return new ArrayList(this.mCellInfoMap.values());
    }

    private List<CellInfo> aggregateCellInfoListEx(List<CellInfo> list) {
        for (CellInfo cellInfo : list) {
            String str = CellInfoUtil.getOperatorInfoFromCellInfo(cellInfo).getOperatorNumeric().split(" ")[0] + " " + ((String) cellInfo.getCellIdentity().getOperatorAlphaLong());
            if (cellInfo.isRegistered() || !this.mCellInfoMapForPlmn.containsKey(str)) {
                this.mCellInfoMapForPlmn.put(str, cellInfo);
            } else if (CellInfoUtil.checkCellType(this.mCellInfoMapForPlmn.get(str)) < CellInfoUtil.checkCellType(cellInfo) && !this.mCellInfoMapForPlmn.get(str).isRegistered()) {
                this.mCellInfoMapForPlmn.put(str, cellInfo);
            }
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, CellInfo> entry : this.mCellInfoMapForPlmn.entrySet()) {
            CellInfo value = entry.getValue();
            if (value.isRegistered()) {
                arrayList.add(0, value);
            } else {
                arrayList.add(value);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNetworkSelectionFailed() {
        Log.d(TAG, "showNetworkSelectionFailed");
        if (getContext() == null) {
            return;
        }
        final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R$string.network_registration_fail_title).setMessage(R$string.network_registration_fail).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (MobileNetworkUtils.isNetworkSelectEnabled(NetworkSelectSettings.this.getContext())) {
                    intent.putExtra("manual_select_success", false);
                    NetworkSelectSettings.this.setResult(-1, intent);
                    NetworkSelectSettings.this.finish();
                }
            }
        }).setNegativeButton(17039369, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                intent.putExtra("manual_select_success", true);
                NetworkSelectSettings.this.setResult(-1, intent);
                dialogInterface.cancel();
            }
        }).setCancelable(false);
        AlertDialog create = builder.create();
        this.mAlertDialog = create;
        create.show();
    }
}
