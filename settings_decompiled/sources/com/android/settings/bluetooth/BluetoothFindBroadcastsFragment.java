package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothLeBroadcastAssistant;
import android.bluetooth.BluetoothLeBroadcastMetadata;
import android.bluetooth.BluetoothLeBroadcastReceiveState;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.bluetooth.BluetoothFindBroadcastsFragment;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothLeBroadcastAssistant;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.core.AbstractPreferenceController;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class BluetoothFindBroadcastsFragment extends RestrictedDashboardFragment {
    BluetoothFindBroadcastsHeaderController mBluetoothFindBroadcastsHeaderController;
    private BluetoothLeBroadcastAssistant.Callback mBroadcastAssistantCallback;
    PreferenceCategory mBroadcastSourceListCategory;
    CachedBluetoothDevice mCachedDevice;
    String mDeviceAddress;
    private Executor mExecutor;
    private LocalBluetoothLeBroadcastAssistant mLeBroadcastAssistant;
    LocalBluetoothManager mManager;
    private BluetoothBroadcastSourcePreference mSelectedPreference;
    private int mSourceId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BtFindBroadcastsFrg";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements BluetoothLeBroadcastAssistant.Callback {
        public void onSourceModified(BluetoothDevice bluetoothDevice, int i, int i2) {
        }

        public void onSourceModifyFailed(BluetoothDevice bluetoothDevice, int i, int i2) {
        }

        AnonymousClass1() {
        }

        public void onSearchStarted(int i) {
            Log.d("BtFindBroadcastsFrg", "onSearchStarted: " + i);
            BluetoothFindBroadcastsFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BluetoothFindBroadcastsFragment.AnonymousClass1.this.lambda$onSearchStarted$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSearchStarted$0() {
            BluetoothFindBroadcastsFragment.this.handleSearchStarted();
        }

        public void onSearchStartFailed(int i) {
            Log.d("BtFindBroadcastsFrg", "onSearchStartFailed: " + i);
        }

        public void onSearchStopped(int i) {
            Log.d("BtFindBroadcastsFrg", "onSearchStopped: " + i);
        }

        public void onSearchStopFailed(int i) {
            Log.d("BtFindBroadcastsFrg", "onSearchStopFailed: " + i);
        }

        public void onSourceFound(final BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata) {
            Log.d("BtFindBroadcastsFrg", "onSourceFound:");
            BluetoothFindBroadcastsFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BluetoothFindBroadcastsFragment.AnonymousClass1.this.lambda$onSourceFound$1(bluetoothLeBroadcastMetadata);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSourceFound$1(BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata) {
            BluetoothFindBroadcastsFragment.this.updateListCategoryFromBroadcastMetadata(bluetoothLeBroadcastMetadata, false);
        }

        public void onSourceAdded(BluetoothDevice bluetoothDevice, int i, int i2) {
            BluetoothFindBroadcastsFragment.this.setSourceId(i);
            if (BluetoothFindBroadcastsFragment.this.mSelectedPreference == null) {
                Log.w("BtFindBroadcastsFrg", "onSourceAdded: mSelectedPreference == null!");
            } else {
                BluetoothFindBroadcastsFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        BluetoothFindBroadcastsFragment.AnonymousClass1.this.lambda$onSourceAdded$2();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSourceAdded$2() {
            BluetoothFindBroadcastsFragment bluetoothFindBroadcastsFragment = BluetoothFindBroadcastsFragment.this;
            bluetoothFindBroadcastsFragment.updateListCategoryFromBroadcastMetadata(bluetoothFindBroadcastsFragment.mSelectedPreference.getBluetoothLeBroadcastMetadata(), true);
        }

        public void onSourceAddFailed(BluetoothDevice bluetoothDevice, BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata, int i) {
            BluetoothFindBroadcastsFragment.this.mSelectedPreference = null;
            Log.d("BtFindBroadcastsFrg", "onSourceAddFailed: clear the mSelectedPreference.");
        }

        public void onSourceRemoved(BluetoothDevice bluetoothDevice, int i, int i2) {
            Log.d("BtFindBroadcastsFrg", "onSourceRemoved:");
            BluetoothFindBroadcastsFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    BluetoothFindBroadcastsFragment.AnonymousClass1.this.lambda$onSourceRemoved$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSourceRemoved$3() {
            BluetoothFindBroadcastsFragment.this.handleSourceRemoved();
        }

        public void onSourceRemoveFailed(BluetoothDevice bluetoothDevice, int i, int i2) {
            Log.d("BtFindBroadcastsFrg", "onSourceRemoveFailed:");
        }

        public void onReceiveStateChanged(BluetoothDevice bluetoothDevice, int i, BluetoothLeBroadcastReceiveState bluetoothLeBroadcastReceiveState) {
            Log.d("BtFindBroadcastsFrg", "onReceiveStateChanged:");
        }
    }

    public BluetoothFindBroadcastsFragment() {
        super("no_config_bluetooth");
        this.mBroadcastAssistantCallback = new AnonymousClass1();
    }

    LocalBluetoothManager getLocalBluetoothManager(Context context) {
        return Utils.getLocalBtManager(context);
    }

    CachedBluetoothDevice getCachedDevice(String str) {
        return this.mManager.getCachedDeviceManager().findDevice(this.mManager.getBluetoothAdapter().getRemoteDevice(str));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mDeviceAddress = getArguments().getString("device_address");
        this.mManager = getLocalBluetoothManager(context);
        this.mCachedDevice = getCachedDevice(this.mDeviceAddress);
        this.mLeBroadcastAssistant = getLeBroadcastAssistant();
        this.mExecutor = Executors.newSingleThreadExecutor();
        super.onAttach(context);
        if (this.mCachedDevice == null || this.mLeBroadcastAssistant == null) {
            Log.w("BtFindBroadcastsFrg", "onAttach() CachedDevice or LeBroadcastAssistant is null!");
            finish();
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBroadcastSourceListCategory = (PreferenceCategory) findPreference("broadcast_source_list");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        LocalBluetoothLeBroadcastAssistant localBluetoothLeBroadcastAssistant = this.mLeBroadcastAssistant;
        if (localBluetoothLeBroadcastAssistant != null) {
            localBluetoothLeBroadcastAssistant.registerServiceCallBack(this.mExecutor, this.mBroadcastAssistantCallback);
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        finishFragmentIfNecessary();
        LocalBluetoothLeBroadcastAssistant localBluetoothLeBroadcastAssistant = this.mLeBroadcastAssistant;
        if (localBluetoothLeBroadcastAssistant != null && !localBluetoothLeBroadcastAssistant.isSearchInProgress()) {
            this.mLeBroadcastAssistant.startSearchingForSources(getScanFilter());
        } else {
            addConnectedSourcePreference();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        LocalBluetoothLeBroadcastAssistant localBluetoothLeBroadcastAssistant = this.mLeBroadcastAssistant;
        if (localBluetoothLeBroadcastAssistant != null) {
            localBluetoothLeBroadcastAssistant.unregisterServiceCallBack(this.mBroadcastAssistantCallback);
        }
    }

    void finishFragmentIfNecessary() {
        if (this.mCachedDevice.getBondState() == 10) {
            finish();
        }
    }

    public void scanBroadcastSource() {
        LocalBluetoothLeBroadcastAssistant localBluetoothLeBroadcastAssistant = this.mLeBroadcastAssistant;
        if (localBluetoothLeBroadcastAssistant == null) {
            Log.w("BtFindBroadcastsFrg", "scanBroadcastSource: LeBroadcastAssistant is null!");
        } else {
            localBluetoothLeBroadcastAssistant.startSearchingForSources(getScanFilter());
        }
    }

    public void leaveBroadcastSession() {
        CachedBluetoothDevice cachedBluetoothDevice;
        LocalBluetoothLeBroadcastAssistant localBluetoothLeBroadcastAssistant = this.mLeBroadcastAssistant;
        if (localBluetoothLeBroadcastAssistant == null || (cachedBluetoothDevice = this.mCachedDevice) == null) {
            Log.w("BtFindBroadcastsFrg", "leaveBroadcastSession: LeBroadcastAssistant or CachedDevice is null!");
        } else {
            localBluetoothLeBroadcastAssistant.removeSource(cachedBluetoothDevice.getDevice(), getSourceId());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.bluetooth_find_broadcasts_fragment;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        if (this.mCachedDevice != null) {
            BluetoothFindBroadcastsHeaderController bluetoothFindBroadcastsHeaderController = new BluetoothFindBroadcastsHeaderController(context, this, this.mCachedDevice, getSettingsLifecycle(), this.mManager);
            this.mBluetoothFindBroadcastsHeaderController = bluetoothFindBroadcastsHeaderController;
            arrayList.add(bluetoothFindBroadcastsHeaderController);
        }
        return arrayList;
    }

    public LocalBluetoothLeBroadcastAssistant getLeBroadcastAssistant() {
        LocalBluetoothManager localBluetoothManager = this.mManager;
        if (localBluetoothManager == null) {
            Log.w("BtFindBroadcastsFrg", "getLeBroadcastAssistant: LocalBluetoothManager is null!");
            return null;
        }
        LocalBluetoothProfileManager profileManager = localBluetoothManager.getProfileManager();
        if (profileManager == null) {
            Log.w("BtFindBroadcastsFrg", "getLeBroadcastAssistant: LocalBluetoothProfileManager is null!");
            return null;
        }
        return profileManager.getLeAudioBroadcastAssistantProfile();
    }

    private List<ScanFilter> getScanFilter() {
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateListCategoryFromBroadcastMetadata(BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata, boolean z) {
        BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference = (BluetoothBroadcastSourcePreference) this.mBroadcastSourceListCategory.findPreference(Integer.toString(bluetoothLeBroadcastMetadata.getBroadcastId()));
        if (bluetoothBroadcastSourcePreference == null) {
            bluetoothBroadcastSourcePreference = createBluetoothBroadcastSourcePreference();
            bluetoothBroadcastSourcePreference.setKey(Integer.toString(bluetoothLeBroadcastMetadata.getBroadcastId()));
            this.mBroadcastSourceListCategory.addPreference(bluetoothBroadcastSourcePreference);
        }
        bluetoothBroadcastSourcePreference.updateMetadataAndRefreshUi(bluetoothLeBroadcastMetadata, z);
        bluetoothBroadcastSourcePreference.setOrder(!z ? 1 : 0);
        BluetoothFindBroadcastsHeaderController bluetoothFindBroadcastsHeaderController = this.mBluetoothFindBroadcastsHeaderController;
        if (bluetoothFindBroadcastsHeaderController != null) {
            bluetoothFindBroadcastsHeaderController.refreshUi();
        }
    }

    private void updateListCategoryFromBroadcastReceiveState(BluetoothLeBroadcastReceiveState bluetoothLeBroadcastReceiveState) {
        BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference = (BluetoothBroadcastSourcePreference) this.mBroadcastSourceListCategory.findPreference(Integer.toString(bluetoothLeBroadcastReceiveState.getBroadcastId()));
        if (bluetoothBroadcastSourcePreference == null) {
            bluetoothBroadcastSourcePreference = createBluetoothBroadcastSourcePreference();
            bluetoothBroadcastSourcePreference.setKey(Integer.toString(bluetoothLeBroadcastReceiveState.getBroadcastId()));
            this.mBroadcastSourceListCategory.addPreference(bluetoothBroadcastSourcePreference);
        }
        bluetoothBroadcastSourcePreference.updateReceiveStateAndRefreshUi(bluetoothLeBroadcastReceiveState);
        bluetoothBroadcastSourcePreference.setOrder(0);
        setSourceId(bluetoothLeBroadcastReceiveState.getSourceId());
        this.mSelectedPreference = bluetoothBroadcastSourcePreference;
        BluetoothFindBroadcastsHeaderController bluetoothFindBroadcastsHeaderController = this.mBluetoothFindBroadcastsHeaderController;
        if (bluetoothFindBroadcastsHeaderController != null) {
            bluetoothFindBroadcastsHeaderController.refreshUi();
        }
    }

    private BluetoothBroadcastSourcePreference createBluetoothBroadcastSourcePreference() {
        final BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference = new BluetoothBroadcastSourcePreference(getContext());
        bluetoothBroadcastSourcePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$createBluetoothBroadcastSourcePreference$0;
                lambda$createBluetoothBroadcastSourcePreference$0 = BluetoothFindBroadcastsFragment.this.lambda$createBluetoothBroadcastSourcePreference$0(bluetoothBroadcastSourcePreference, preference);
                return lambda$createBluetoothBroadcastSourcePreference$0;
            }
        });
        return bluetoothBroadcastSourcePreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createBluetoothBroadcastSourcePreference$0(BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference, Preference preference) {
        if (bluetoothBroadcastSourcePreference.getBluetoothLeBroadcastMetadata() == null) {
            Log.d("BtFindBroadcastsFrg", "BluetoothLeBroadcastMetadata is null, do nothing.");
            return false;
        } else if (bluetoothBroadcastSourcePreference.isEncrypted()) {
            launchBroadcastCodeDialog(bluetoothBroadcastSourcePreference);
            return true;
        } else {
            addSource(bluetoothBroadcastSourcePreference);
            return true;
        }
    }

    private void addSource(BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference) {
        if (this.mLeBroadcastAssistant == null || this.mCachedDevice == null) {
            Log.w("BtFindBroadcastsFrg", "addSource: LeBroadcastAssistant or CachedDevice is null!");
            return;
        }
        if (this.mSelectedPreference != null) {
            getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BluetoothFindBroadcastsFragment.this.lambda$addSource$1();
                }
            });
        }
        this.mSelectedPreference = bluetoothBroadcastSourcePreference;
        this.mLeBroadcastAssistant.addSource(this.mCachedDevice.getDevice(), bluetoothBroadcastSourcePreference.getBluetoothLeBroadcastMetadata(), true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addSource$1() {
        BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference = this.mSelectedPreference;
        bluetoothBroadcastSourcePreference.updateMetadataAndRefreshUi(bluetoothBroadcastSourcePreference.getBluetoothLeBroadcastMetadata(), false);
        this.mSelectedPreference.setOrder(1);
    }

    private void addBroadcastCodeIntoPreference(BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference, String str) {
        bluetoothBroadcastSourcePreference.updateMetadataAndRefreshUi(new BluetoothLeBroadcastMetadata.Builder(bluetoothBroadcastSourcePreference.getBluetoothLeBroadcastMetadata()).setBroadcastCode(str.getBytes(StandardCharsets.UTF_8)).build(), false);
    }

    private void launchBroadcastCodeDialog(final BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference) {
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.bluetooth_find_broadcast_password_dialog, (ViewGroup) null);
        final EditText editText = (EditText) inflate.requireViewById(R$id.broadcast_edit_text);
        ((TextView) inflate.requireViewById(R$id.broadcast_name_text)).setText(bluetoothBroadcastSourcePreference.getTitle());
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(R$string.find_broadcast_password_dialog_title).setView(inflate).setNeutralButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R$string.bluetooth_connect_access_dialog_positive, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BluetoothFindBroadcastsFragment.this.lambda$launchBroadcastCodeDialog$2(bluetoothBroadcastSourcePreference, editText, dialogInterface, i);
            }
        }).create();
        create.getWindow().setType(2009);
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$launchBroadcastCodeDialog$2(BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference, EditText editText, DialogInterface dialogInterface, int i) {
        Log.d("BtFindBroadcastsFrg", "setPositiveButton: clicked");
        if (bluetoothBroadcastSourcePreference.getBluetoothLeBroadcastMetadata() == null) {
            Log.d("BtFindBroadcastsFrg", "BluetoothLeBroadcastMetadata is null, do nothing.");
            return;
        }
        addBroadcastCodeIntoPreference(bluetoothBroadcastSourcePreference, editText.getText().toString());
        addSource(bluetoothBroadcastSourcePreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSearchStarted() {
        cacheRemoveAllPrefs(this.mBroadcastSourceListCategory);
        addConnectedSourcePreference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSourceRemoved() {
        BluetoothBroadcastSourcePreference bluetoothBroadcastSourcePreference = this.mSelectedPreference;
        if (bluetoothBroadcastSourcePreference != null) {
            if (bluetoothBroadcastSourcePreference.getBluetoothLeBroadcastMetadata() == null) {
                this.mBroadcastSourceListCategory.removePreference(this.mSelectedPreference);
            } else {
                this.mSelectedPreference.clearReceiveState();
            }
        }
        this.mSelectedPreference = null;
    }

    private void addConnectedSourcePreference() {
        List<BluetoothLeBroadcastReceiveState> allSources = this.mLeBroadcastAssistant.getAllSources(this.mCachedDevice.getDevice());
        if (allSources.isEmpty()) {
            return;
        }
        updateListCategoryFromBroadcastReceiveState(allSources.get(0));
    }

    public int getSourceId() {
        return this.mSourceId;
    }

    public void setSourceId(int i) {
        this.mSourceId = i;
    }
}
