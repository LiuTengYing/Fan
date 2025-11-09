package com.android.settings.wifi;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$layout;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.FastDoubleClickUtils;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiEntry;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.time.ZoneOffset;
/* loaded from: classes.dex */
public class ConfigureWifiEntryFragment extends InstrumentedFragment implements WifiConfigUiBase2 {
    private Button mCancelBtn;
    NetworkDetailsTracker mNetworkDetailsTracker;
    private Button mSubmitBtn;
    private WifiConfigController2 mUiController;
    private WifiEntry mWifiEntry;
    private HandlerThread mWorkerThread;

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public Button getForgetButton() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1800;
    }

    public int getMode() {
        return 1;
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setForgetButton(CharSequence charSequence) {
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (UniMobileNetworkUtils.isSubsidyShowing()) {
            getActivity().getWindow().addFlags(524288);
        }
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        setupNetworkDetailsTracker();
        this.mWifiEntry = this.mNetworkDetailsTracker.getWifiEntry();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        HandlerThread handlerThread = this.mWorkerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.wifi_add_network_view, viewGroup, false);
        Button button = (Button) inflate.findViewById(16908315);
        if (button != null) {
            button.setVisibility(8);
        }
        this.mSubmitBtn = (Button) inflate.findViewById(16908313);
        this.mCancelBtn = (Button) inflate.findViewById(16908314);
        this.mSubmitBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.ConfigureWifiEntryFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ConfigureWifiEntryFragment.this.lambda$onCreateView$0(view);
            }
        });
        this.mCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.ConfigureWifiEntryFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ConfigureWifiEntryFragment.this.lambda$onCreateView$1(view);
            }
        });
        this.mUiController = new WifiConfigController2(this, inflate, this.mWifiEntry, getMode());
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        getActivity().getWindow().setSoftInputMode(16);
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(View view) {
        handleSubmitAction();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(View view) {
        handleCancelAction();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mUiController.showSecurityFields(false, true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        this.mUiController.updatePassword();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void dispatchSubmit() {
        handleSubmitAction();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setTitle(int i) {
        getActivity().setTitle(i);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setTitle(CharSequence charSequence) {
        getActivity().setTitle(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setSubmitButton(CharSequence charSequence) {
        this.mSubmitBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setCancelButton(CharSequence charSequence) {
        this.mCancelBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public Button getSubmitButton() {
        return this.mSubmitBtn;
    }

    void handleSubmitAction() {
        Intent intent = new Intent();
        FragmentActivity activity = getActivity();
        Log.d("ConfigureWifiEntryFragment", "handleSubmitAction: " + activity.getComponentName());
        intent.putExtra("network_config_key", this.mUiController.getConfig());
        activity.setResult(-1, intent);
        if (FastDoubleClickUtils.isFastDoubleClick()) {
            return;
        }
        activity.finish();
    }

    void handleCancelAction() {
        if (FastDoubleClickUtils.isFastDoubleClick()) {
            return;
        }
        getActivity().finish();
    }

    private void setupNetworkDetailsTracker() {
        if (this.mNetworkDetailsTracker != null) {
            return;
        }
        Context context = getContext();
        HandlerThread handlerThread = new HandlerThread("ConfigureWifiEntryFragment{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mNetworkDetailsTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createNetworkDetailsTracker(getSettingsLifecycle(), context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.ConfigureWifiEntryFragment.1
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, getArguments().getString("key_chosen_wifientry_key"));
    }
}
