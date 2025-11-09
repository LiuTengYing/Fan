package com.android.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.internal.PreferenceImageView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class NetworkRequestDialogFragment extends NetworkRequestDialogBaseFragment implements DialogInterface.OnClickListener, WifiPickerTracker.WifiPickerTrackerCallback {
    private WifiEntryAdapter mDialogAdapter;
    private WifiManager.NetworkRequestUserSelectionCallback mUserSelectionCallback;
    WifiPickerTracker mWifiPickerTracker;
    private HandlerThread mWorkerThread;
    private boolean mShowLimitedItem = true;
    List<WifiEntry> mFilteredWifiEntries = new ArrayList();
    List<ScanResult> mMatchedScanResults = new ArrayList();

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    public static NetworkRequestDialogFragment newInstance() {
        return new NetworkRequestDialogFragment();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        HandlerThread handlerThread = new HandlerThread("NetworkRequestDialogFragment{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        Clock clock = new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.NetworkRequestDialogFragment.1
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        };
        Context context = getContext();
        this.mWifiPickerTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createWifiPickerTracker(getSettingsLifecycle(), context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), clock, 15000L, 10000L, this);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(R$layout.network_request_dialog_title, (ViewGroup) null);
        ((TextView) inflate.findViewById(R$id.network_request_title_text)).setText(getTitle());
        ((TextView) inflate.findViewById(R$id.network_request_summary_text)).setText(getSummary());
        ((ProgressBar) inflate.findViewById(R$id.network_request_title_progress)).setVisibility(0);
        this.mDialogAdapter = new WifiEntryAdapter(context, R$layout.preference_access_point, this.mFilteredWifiEntries);
        final AlertDialog create = new AlertDialog.Builder(context).setCustomTitle(inflate).setAdapter(this.mDialogAdapter, this).setNegativeButton(R$string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNeutralButton(R$string.network_connection_request_dialog_showall, null).create();
        create.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.wifi.NetworkRequestDialogFragment$$ExternalSyntheticLambda1
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$1(create, adapterView, view, i, j);
            }
        });
        setCancelable(false);
        create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.settings.wifi.NetworkRequestDialogFragment$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$3(create, dialogInterface);
            }
        });
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        onCancel(dialogInterface);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(AlertDialog alertDialog, AdapterView adapterView, View view, int i, long j) {
        onClick(alertDialog, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$3(AlertDialog alertDialog, DialogInterface dialogInterface) {
        final Button button = alertDialog.getButton(-3);
        button.setVisibility(8);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestDialogFragment$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$2(button, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$2(Button button, View view) {
        this.mShowLimitedItem = false;
        updateWifiEntries();
        updateUi();
        button.setVisibility(8);
    }

    private BaseAdapter getDialogAdapter() {
        return this.mDialogAdapter;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.mFilteredWifiEntries.size() == 0 || i >= this.mFilteredWifiEntries.size() || this.mUserSelectionCallback == null) {
            return;
        }
        WifiEntry wifiEntry = this.mFilteredWifiEntries.get(i);
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        if (wifiConfiguration == null) {
            wifiConfiguration = WifiUtils.getWifiConfig(wifiEntry, null);
        }
        this.mUserSelectionCallback.select(wifiConfiguration);
    }

    @Override // com.android.settings.wifi.NetworkRequestDialogBaseFragment, androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback = this.mUserSelectionCallback;
        if (networkRequestUserSelectionCallback != null) {
            networkRequestUserSelectionCallback.reject();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mWorkerThread.quit();
        super.onDestroy();
    }

    private void showAllButton() {
        Button button;
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog == null || (button = alertDialog.getButton(-3)) == null) {
            return;
        }
        button.setVisibility(0);
    }

    private void hideProgressIcon() {
        View findViewById;
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog == null || (findViewById = alertDialog.findViewById(R$id.network_request_title_progress)) == null) {
            return;
        }
        findViewById.setVisibility(8);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        if (this.mMatchedScanResults.size() == 0) {
            return;
        }
        updateWifiEntries();
        updateUi();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        if (this.mMatchedScanResults.size() == 0) {
            return;
        }
        updateWifiEntries();
        updateUi();
    }

    void updateWifiEntries() {
        ArrayList arrayList = new ArrayList();
        if (this.mWifiPickerTracker.getConnectedWifiEntry() != null) {
            arrayList.add(this.mWifiPickerTracker.getConnectedWifiEntry());
        }
        arrayList.addAll(this.mWifiPickerTracker.getWifiEntries());
        this.mFilteredWifiEntries.clear();
        this.mFilteredWifiEntries.addAll((Collection) arrayList.stream().filter(new Predicate() { // from class: com.android.settings.wifi.NetworkRequestDialogFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateWifiEntries$4;
                lambda$updateWifiEntries$4 = NetworkRequestDialogFragment.this.lambda$updateWifiEntries$4((WifiEntry) obj);
                return lambda$updateWifiEntries$4;
            }
        }).limit(this.mShowLimitedItem ? 5L : Long.MAX_VALUE).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiEntries$4(WifiEntry wifiEntry) {
        for (ScanResult scanResult : this.mMatchedScanResults) {
            if (TextUtils.equals(wifiEntry.getSsid(), scanResult.SSID) && wifiEntry.getSecurity() == WifiUtils.getWifiEntrySecurity(scanResult)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WifiEntryAdapter extends ArrayAdapter<WifiEntry> {
        private final LayoutInflater mInflater;
        private final int mResourceId;

        WifiEntryAdapter(Context context, int i, List<WifiEntry> list) {
            super(context, i, list);
            this.mResourceId = i;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(this.mResourceId, viewGroup, false);
                view.findViewById(com.android.settingslib.R$id.two_target_divider).setVisibility(8);
            }
            WifiEntry item = getItem(i);
            TextView textView = (TextView) view.findViewById(16908310);
            if (textView != null) {
                textView.setSingleLine(false);
                textView.setText(item.getTitle());
            }
            TextView textView2 = (TextView) view.findViewById(16908304);
            if (textView2 != null) {
                String summary = item.getSummary();
                if (TextUtils.isEmpty(summary)) {
                    textView2.setVisibility(8);
                } else {
                    textView2.setVisibility(0);
                    textView2.setText(summary);
                }
            }
            PreferenceImageView preferenceImageView = (PreferenceImageView) view.findViewById(16908294);
            int level = item.getLevel();
            if (preferenceImageView != null && level != -1) {
                Drawable drawable = getContext().getDrawable(Utils.getWifiIconResource(level));
                drawable.setTintList(Utils.getColorAttr(getContext(), 16843817));
                preferenceImageView.setImageDrawable(drawable);
            }
            return view;
        }
    }

    @Override // com.android.settings.wifi.NetworkRequestDialogBaseFragment
    public void onUserSelectionCallbackRegistration(WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback) {
        this.mUserSelectionCallback = networkRequestUserSelectionCallback;
    }

    @Override // com.android.settings.wifi.NetworkRequestDialogBaseFragment
    public void onMatch(List<ScanResult> list) {
        this.mMatchedScanResults = list;
        updateWifiEntries();
        updateUi();
    }

    void updateUi() {
        if (this.mShowLimitedItem && this.mFilteredWifiEntries.size() >= 5) {
            showAllButton();
        }
        if (this.mFilteredWifiEntries.size() > 0) {
            hideProgressIcon();
        }
        if (getDialogAdapter() != null) {
            getDialogAdapter().notifyDataSetChanged();
        }
    }
}
