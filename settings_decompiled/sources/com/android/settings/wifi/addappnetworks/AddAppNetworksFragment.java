package com.android.settings.wifi.addappnetworks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.internal.PreferenceImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.addappnetworks.AddAppNetworksFragment;
import com.android.settingslib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class AddAppNetworksFragment extends InstrumentedFragment implements WifiPickerTracker.WifiPickerTrackerCallback {
    @VisibleForTesting
    static final int INITIAL_RSSI_SIGNAL_LEVEL = 0;
    @VisibleForTesting
    static final int MESSAGE_SHOW_SAVED_AND_CONNECT_NETWORK = 2;
    @VisibleForTesting
    static final int MESSAGE_SHOW_SAVE_FAILED = 3;
    @VisibleForTesting
    static final int MESSAGE_START_SAVING_NETWORK = 1;
    @VisibleForTesting
    static final int RESULT_NETWORK_ALREADY_EXISTS = 2;
    @VisibleForTesting
    static final int RESULT_NETWORK_SUCCESS = 0;
    @VisibleForTesting
    FragmentActivity mActivity;
    @VisibleForTesting
    List<WifiNetworkSuggestion> mAllSpecifiedNetworksList;
    private boolean mAnyNetworkSavedSuccess;
    @VisibleForTesting
    String mCallingPackageName;
    @VisibleForTesting
    Button mCancelButton;
    @VisibleForTesting
    final Handler mHandler = new Handler() { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AddAppNetworksFragment.this.showSaveStatusByState(message.what);
            int i = message.what;
            if (i == 1) {
                AddAppNetworksFragment.this.mSaveButton.setEnabled(false);
                AddAppNetworksFragment.this.mSavingIndex = 0;
                AddAppNetworksFragment addAppNetworksFragment = AddAppNetworksFragment.this;
                addAppNetworksFragment.saveNetwork(addAppNetworksFragment.mSavingIndex);
            } else if (i == 2) {
                if (AddAppNetworksFragment.this.mIsSingleNetwork) {
                    AddAppNetworksFragment.this.connectNetwork(0);
                }
                sendEmptyMessageDelayed(4, 1000L);
            } else if (i == 3) {
                AddAppNetworksFragment.this.mSaveButton.setEnabled(true);
            } else if (i != 4) {
            } else {
                AddAppNetworksFragment addAppNetworksFragment2 = AddAppNetworksFragment.this;
                addAppNetworksFragment2.finishWithResult(-1, addAppNetworksFragment2.mResultCodeArrayList);
            }
        }
    };
    private boolean mIsSingleNetwork;
    @VisibleForTesting
    View mLayoutView;
    @VisibleForTesting
    List<Integer> mResultCodeArrayList;
    @VisibleForTesting
    Button mSaveButton;
    private WifiManager.ActionListener mSaveListener;
    private int mSavingIndex;
    private TextView mSingleNetworkProcessingStatusView;
    private TextView mSummaryView;
    private UiConfigurationItemAdapter mUiConfigurationItemAdapter;
    @VisibleForTesting
    List<UiConfigurationItem> mUiToRequestedList;
    private WifiManager mWifiManager;
    @VisibleForTesting
    WifiPickerTracker mWifiPickerTracker;
    @VisibleForTesting
    HandlerThread mWorkerThread;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1809;
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentActivity activity = getActivity();
        this.mActivity = activity;
        this.mWifiManager = (WifiManager) activity.getSystemService(WifiManager.class);
        HandlerThread handlerThread = new HandlerThread("AddAppNetworksFragment{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWifiPickerTracker = FeatureFactory.getFactory(this.mActivity.getApplicationContext()).getWifiTrackerLibProvider().createWifiPickerTracker(getSettingsLifecycle(), this.mActivity, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment.2
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, this);
        return layoutInflater.inflate(R$layout.wifi_add_app_networks, viewGroup, false);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mWorkerThread.quit();
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mLayoutView = view;
        this.mCancelButton = (Button) view.findViewById(R$id.cancel);
        this.mSaveButton = (Button) view.findViewById(R$id.save);
        this.mSummaryView = (TextView) view.findViewById(R$id.app_summary);
        this.mSingleNetworkProcessingStatusView = (TextView) view.findViewById(R$id.single_status);
        this.mCancelButton.setOnClickListener(getCancelClickListener());
        this.mSaveButton.setOnClickListener(getSaveClickListener());
        prepareSaveResultListener();
        createContent(getArguments());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void createContent(Bundle bundle) {
        Button button = this.mSaveButton;
        if (button != null && !button.isEnabled()) {
            Log.d("AddAppNetworksFragment", "Network saving, ignore new intent");
            return;
        }
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("android.provider.extra.WIFI_NETWORK_LIST");
        this.mAllSpecifiedNetworksList = parcelableArrayList;
        if (parcelableArrayList == null || parcelableArrayList.isEmpty() || this.mAllSpecifiedNetworksList.size() > 5) {
            finishWithResult(0, null);
            return;
        }
        initializeResultCodeArray();
        filterSavedNetworks(this.mWifiManager.getPrivilegedConfiguredNetworks());
        if (this.mUiToRequestedList.size() == 0) {
            finishWithResult(-1, this.mResultCodeArrayList);
            return;
        }
        if (this.mAllSpecifiedNetworksList.size() == 1) {
            this.mIsSingleNetwork = true;
            this.mLayoutView.findViewById(R$id.multiple_networks).setVisibility(8);
            this.mLayoutView.findViewById(R$id.single_network).setVisibility(0);
            updateSingleNetworkSignalIcon(0);
            ((TextView) this.mLayoutView.findViewById(R$id.single_ssid)).setText(this.mUiToRequestedList.get(0).mDisplayedSsid);
            this.mSingleNetworkProcessingStatusView.setVisibility(8);
        } else {
            this.mIsSingleNetwork = false;
            this.mLayoutView.findViewById(R$id.single_network).setVisibility(8);
            this.mLayoutView.findViewById(R$id.multiple_networks).setVisibility(0);
            UiConfigurationItemAdapter uiConfigurationItemAdapter = this.mUiConfigurationItemAdapter;
            if (uiConfigurationItemAdapter == null) {
                UiConfigurationItemAdapter uiConfigurationItemAdapter2 = new UiConfigurationItemAdapter(this.mActivity, com.android.settingslib.R$layout.preference_access_point, this.mUiToRequestedList);
                this.mUiConfigurationItemAdapter = uiConfigurationItemAdapter2;
                ((ListView) this.mLayoutView.findViewById(R$id.config_list)).setAdapter((ListAdapter) uiConfigurationItemAdapter2);
            } else {
                uiConfigurationItemAdapter.notifyDataSetChanged();
            }
        }
        String string = bundle.getString("panel_calling_package_name");
        this.mCallingPackageName = string;
        assignAppIcon(this.mActivity, string);
        assignTitleAndSummary(this.mActivity, this.mCallingPackageName);
    }

    private void initializeResultCodeArray() {
        int size = this.mAllSpecifiedNetworksList.size();
        this.mResultCodeArrayList = new ArrayList();
        for (int i = 0; i < size; i++) {
            this.mResultCodeArrayList.add(0);
        }
    }

    private String getWepKey(WifiConfiguration wifiConfiguration) {
        int i = wifiConfiguration.wepTxKeyIndex;
        if (i >= 0) {
            String[] strArr = wifiConfiguration.wepKeys;
            if (i < strArr.length) {
                return strArr[i];
            }
        }
        return null;
    }

    private boolean isSavedPasspointConfiguration(final PasspointConfiguration passpointConfiguration) {
        return this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate() { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isSavedPasspointConfiguration$0;
                lambda$isSavedPasspointConfiguration$0 = AddAppNetworksFragment.lambda$isSavedPasspointConfiguration$0(passpointConfiguration, (PasspointConfiguration) obj);
                return lambda$isSavedPasspointConfiguration$0;
            }
        }).findFirst().isPresent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isSavedPasspointConfiguration$0(PasspointConfiguration passpointConfiguration, PasspointConfiguration passpointConfiguration2) {
        return passpointConfiguration2.equals(passpointConfiguration);
    }

    private boolean isSavedWifiConfiguration(WifiConfiguration wifiConfiguration, List<WifiConfiguration> list) {
        String addQuotationIfNeeded = addQuotationIfNeeded(wifiConfiguration.SSID);
        int authType = wifiConfiguration.getAuthType();
        for (WifiConfiguration wifiConfiguration2 : list) {
            if (addQuotationIfNeeded.equals(wifiConfiguration2.SSID) && authType == wifiConfiguration2.getAuthType()) {
                if (authType == 0) {
                    return TextUtils.equals(getWepKey(wifiConfiguration), getWepKey(wifiConfiguration2));
                }
                if (authType == 1 || authType == 4 || authType == 8) {
                    if (wifiConfiguration.preSharedKey.equals(wifiConfiguration2.preSharedKey)) {
                        return true;
                    }
                } else if (authType == 9) {
                    return true;
                }
            }
        }
        return false;
    }

    @VisibleForTesting
    void filterSavedNetworks(List<WifiConfiguration> list) {
        boolean isSavedWifiConfiguration;
        String str;
        List<UiConfigurationItem> list2 = this.mUiToRequestedList;
        if (list2 == null) {
            this.mUiToRequestedList = new ArrayList();
        } else {
            list2.clear();
        }
        int i = 0;
        for (WifiNetworkSuggestion wifiNetworkSuggestion : this.mAllSpecifiedNetworksList) {
            PasspointConfiguration passpointConfig = wifiNetworkSuggestion.getPasspointConfig();
            if (passpointConfig != null) {
                isSavedWifiConfiguration = isSavedPasspointConfiguration(passpointConfig);
                str = passpointConfig.getHomeSp().getFriendlyName();
            } else {
                WifiConfiguration wifiConfiguration = wifiNetworkSuggestion.getWifiConfiguration();
                String removeDoubleQuotes = removeDoubleQuotes(wifiConfiguration.SSID);
                isSavedWifiConfiguration = isSavedWifiConfiguration(wifiConfiguration, list);
                str = removeDoubleQuotes;
            }
            if (isSavedWifiConfiguration) {
                this.mResultCodeArrayList.set(i, 2);
            } else {
                this.mUiToRequestedList.add(new UiConfigurationItem(str, wifiNetworkSuggestion, i, 0));
            }
            i++;
        }
    }

    private void updateSingleNetworkSignalIcon(int i) {
        if (i == -1) {
            return;
        }
        Drawable mutate = this.mActivity.getDrawable(Utils.getWifiIconResource(i)).mutate().getConstantState().newDrawable().mutate();
        mutate.setTintList(Utils.getColorAttr(this.mActivity, 16843817));
        ((ImageView) this.mLayoutView.findViewById(R$id.signal_strength)).setImageDrawable(mutate);
    }

    private String addQuotationIfNeeded(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str;
        }
        return "\"" + str + "\"";
    }

    static String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    private void assignAppIcon(Context context, String str) {
        ((ImageView) this.mLayoutView.findViewById(R$id.app_icon)).setImageDrawable(loadPackageIconDrawable(context, str));
    }

    private Drawable loadPackageIconDrawable(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationIcon(str);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("AddAppNetworksFragment", "Cannot get application icon", e);
            return null;
        }
    }

    private void assignTitleAndSummary(Context context, String str) {
        ((TextView) this.mLayoutView.findViewById(R$id.app_title)).setText(getTitle());
        this.mSummaryView.setText(getAddNetworkRequesterSummary(com.android.settings.Utils.getApplicationLabel(context, str)));
    }

    private CharSequence getAddNetworkRequesterSummary(CharSequence charSequence) {
        return getString(this.mIsSingleNetwork ? R$string.wifi_add_app_single_network_summary : R$string.wifi_add_app_networks_summary, charSequence);
    }

    private CharSequence getTitle() {
        return getString(this.mIsSingleNetwork ? R$string.wifi_add_app_single_network_title : R$string.wifi_add_app_networks_title);
    }

    View.OnClickListener getCancelClickListener() {
        return new View.OnClickListener() { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AddAppNetworksFragment.this.lambda$getCancelClickListener$1(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getCancelClickListener$1(View view) {
        Log.d("AddAppNetworksFragment", "User rejected to add network");
        finishWithResult(0, null);
    }

    View.OnClickListener getSaveClickListener() {
        return new View.OnClickListener() { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AddAppNetworksFragment.this.lambda$getSaveClickListener$2(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getSaveClickListener$2(View view) {
        Log.d("AddAppNetworksFragment", "User agree to add networks");
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class UiConfigurationItem {
        public final String mDisplayedSsid;
        public final int mIndex;
        public int mLevel;
        public final WifiNetworkSuggestion mWifiNetworkSuggestion;

        UiConfigurationItem(String str, WifiNetworkSuggestion wifiNetworkSuggestion, int i, int i2) {
            if (str.contains("\n") || str.contains("\r")) {
                this.mDisplayedSsid = str.replaceAll("\\r|\\n", "");
                Log.e("AddAppNetworksFragment", "Ignore CRLF strings in display SSIDs to avoid display errors!");
                EventLog.writeEvent(1397638484, "224545390", -1, "CRLF injection");
            } else {
                this.mDisplayedSsid = str;
            }
            this.mWifiNetworkSuggestion = wifiNetworkSuggestion;
            this.mIndex = i;
            this.mLevel = i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UiConfigurationItemAdapter extends ArrayAdapter<UiConfigurationItem> {
        private final LayoutInflater mInflater;
        private final int mResourceId;

        UiConfigurationItemAdapter(Context context, int i, List<UiConfigurationItem> list) {
            super(context, i, list);
            this.mResourceId = i;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(this.mResourceId, viewGroup, false);
            }
            View findViewById = view.findViewById(com.android.settingslib.R$id.two_target_divider);
            if (findViewById != null) {
                findViewById.setVisibility(8);
            }
            UiConfigurationItem item = getItem(i);
            TextView textView = (TextView) view.findViewById(16908310);
            if (textView != null) {
                textView.setSingleLine(false);
                textView.setText(item.mDisplayedSsid);
            }
            PreferenceImageView preferenceImageView = (PreferenceImageView) view.findViewById(16908294);
            if (preferenceImageView != null) {
                Drawable drawable = getContext().getDrawable(Utils.getWifiIconResource(item.mLevel));
                drawable.setTintList(Utils.getColorAttr(getContext(), 16843817));
                preferenceImageView.setImageDrawable(drawable);
            }
            TextView textView2 = (TextView) view.findViewById(16908304);
            if (textView2 != null) {
                textView2.setVisibility(8);
            }
            return view;
        }
    }

    private void prepareSaveResultListener() {
        this.mSaveListener = new WifiManager.ActionListener() { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment.3
            public void onSuccess() {
                AddAppNetworksFragment.this.mAnyNetworkSavedSuccess = true;
                if (AddAppNetworksFragment.this.saveNextNetwork()) {
                    return;
                }
                AddAppNetworksFragment.this.showSavedOrFail();
            }

            public void onFailure(int i) {
                AddAppNetworksFragment addAppNetworksFragment = AddAppNetworksFragment.this;
                addAppNetworksFragment.mResultCodeArrayList.set(addAppNetworksFragment.mUiToRequestedList.get(addAppNetworksFragment.mSavingIndex).mIndex, 1);
                if (AddAppNetworksFragment.this.saveNextNetwork()) {
                    return;
                }
                AddAppNetworksFragment.this.showSavedOrFail();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean saveNextNetwork() {
        if (this.mIsSingleNetwork || this.mSavingIndex >= this.mUiToRequestedList.size() - 1) {
            return false;
        }
        int i = this.mSavingIndex + 1;
        this.mSavingIndex = i;
        saveNetwork(i);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSavedOrFail() {
        Message obtainMessage;
        if (this.mAnyNetworkSavedSuccess) {
            obtainMessage = this.mHandler.obtainMessage(2);
        } else {
            obtainMessage = this.mHandler.obtainMessage(3);
        }
        this.mHandler.sendMessageDelayed(obtainMessage, 500L);
    }

    @VisibleForTesting
    void saveNetwork(int i) {
        PasspointConfiguration passpointConfig = this.mUiToRequestedList.get(i).mWifiNetworkSuggestion.getPasspointConfig();
        if (passpointConfig != null) {
            try {
                this.mWifiManager.addOrUpdatePasspointConfiguration(passpointConfig);
                this.mAnyNetworkSavedSuccess = true;
            } catch (IllegalArgumentException unused) {
                this.mResultCodeArrayList.set(this.mUiToRequestedList.get(i).mIndex, 1);
            }
            if (saveNextNetwork()) {
                return;
            }
            showSavedOrFail();
            return;
        }
        WifiConfiguration wifiConfiguration = this.mUiToRequestedList.get(i).mWifiNetworkSuggestion.getWifiConfiguration();
        wifiConfiguration.SSID = addQuotationIfNeeded(wifiConfiguration.SSID);
        this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectNetwork(int i) {
        this.mWifiManager.connect(this.mUiToRequestedList.get(i).mWifiNetworkSuggestion.getWifiConfiguration(), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishWithResult(int i, List<Integer> list) {
        if (this.mActivity == null) {
            return;
        }
        if (list != null) {
            Intent intent = new Intent();
            intent.putIntegerArrayListExtra("android.provider.extra.WIFI_NETWORK_RESULT_LIST", (ArrayList) list);
            this.mActivity.setResult(i, intent);
        }
        this.mActivity.finish();
    }

    @VisibleForTesting
    void showSaveStatusByState(int i) {
        if (i == 1) {
            if (this.mIsSingleNetwork) {
                this.mSingleNetworkProcessingStatusView.setTextColor(Utils.getColorAttr(this.mActivity, 16842808));
                this.mSingleNetworkProcessingStatusView.setText(getString(R$string.wifi_add_app_single_network_saving_summary));
                this.mSingleNetworkProcessingStatusView.setVisibility(0);
                return;
            }
            this.mSummaryView.setTextColor(Utils.getColorAttr(this.mActivity, 16842808));
            this.mSummaryView.setText(getString(R$string.wifi_add_app_networks_saving_summary, Integer.valueOf(this.mUiToRequestedList.size())));
        } else if (i == 2) {
            if (this.mIsSingleNetwork) {
                this.mSingleNetworkProcessingStatusView.setText(getString(R$string.wifi_add_app_single_network_saved_summary));
            } else {
                this.mSummaryView.setText(getString(R$string.wifi_add_app_networks_saved_summary));
            }
        } else if (i != 3) {
        } else {
            if (this.mIsSingleNetwork) {
                this.mSingleNetworkProcessingStatusView.setTextColor(Utils.getColorAttr(this.mActivity, 16844099));
                this.mSingleNetworkProcessingStatusView.setText(getString(R$string.wifi_add_app_network_save_failed_summary));
                return;
            }
            this.mSummaryView.setTextColor(Utils.getColorAttr(this.mActivity, 16844099));
            this.mSummaryView.setText(getString(R$string.wifi_add_app_network_save_failed_summary));
        }
    }

    @VisibleForTesting
    void updateScanResultsToUi() {
        if (this.mUiToRequestedList == null) {
            return;
        }
        List<WifiEntry> list = null;
        if (this.mWifiPickerTracker.getWifiState() == 3) {
            list = this.mWifiPickerTracker.getWifiEntries();
            WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
            if (connectedWifiEntry != null) {
                list.add(connectedWifiEntry);
            }
        }
        Iterator<UiConfigurationItem> it = this.mUiToRequestedList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            final UiConfigurationItem next = it.next();
            next.mLevel = 0;
            if (list != null) {
                Optional<WifiEntry> findFirst = list.stream().filter(new Predicate() { // from class: com.android.settings.wifi.addappnetworks.AddAppNetworksFragment$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$updateScanResultsToUi$3;
                        lambda$updateScanResultsToUi$3 = AddAppNetworksFragment.lambda$updateScanResultsToUi$3(AddAppNetworksFragment.UiConfigurationItem.this, (WifiEntry) obj);
                        return lambda$updateScanResultsToUi$3;
                    }
                }).findFirst();
                next.mLevel = findFirst.isPresent() ? findFirst.get().getLevel() : 0;
            }
        }
        if (this.mIsSingleNetwork) {
            updateSingleNetworkSignalIcon(this.mUiToRequestedList.get(0).mLevel);
            return;
        }
        UiConfigurationItemAdapter uiConfigurationItemAdapter = this.mUiConfigurationItemAdapter;
        if (uiConfigurationItemAdapter != null) {
            uiConfigurationItemAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateScanResultsToUi$3(UiConfigurationItem uiConfigurationItem, WifiEntry wifiEntry) {
        return TextUtils.equals(uiConfigurationItem.mWifiNetworkSuggestion.getSsid(), wifiEntry.getSsid());
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        onWifiEntriesChanged();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        onWifiEntriesChanged();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        updateScanResultsToUi();
    }
}
