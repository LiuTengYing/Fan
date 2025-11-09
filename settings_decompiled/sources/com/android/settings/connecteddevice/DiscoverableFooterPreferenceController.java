package com.android.settings.connecteddevice;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.BidiFormatter;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.bluetooth.AlwaysDiscoverable;
import com.android.settings.bluetooth.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class DiscoverableFooterPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String KEY = "discoverable_footer_preference";
    private AlwaysDiscoverable mAlwaysDiscoverable;
    private BluetoothAdapter mBluetoothAdapter;
    BroadcastReceiver mBluetoothChangedReceiver;
    private boolean mIsAlwaysDiscoverable;
    LocalBluetoothManager mLocalManager;
    private FooterPreference mPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DiscoverableFooterPreferenceController(Context context, String str) {
        super(context, str);
        this.mLocalManager = Utils.getLocalBtManager(context);
        if (!context.getPackageManager().hasSystemFeature("android.hardware.bluetooth") || this.mLocalManager == null) {
            return;
        }
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mAlwaysDiscoverable = new AlwaysDiscoverable(context);
        initReceiver();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (FooterPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth") ? 1 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mLocalManager == null) {
            return;
        }
        this.mContext.registerReceiver(this.mBluetoothChangedReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
        if (this.mIsAlwaysDiscoverable) {
            this.mAlwaysDiscoverable.start();
        }
        updateFooterPreferenceTitle(this.mBluetoothAdapter.getState());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mLocalManager == null) {
            return;
        }
        this.mContext.unregisterReceiver(this.mBluetoothChangedReceiver);
        if (this.mIsAlwaysDiscoverable) {
            this.mAlwaysDiscoverable.stop();
        }
    }

    public void setAlwaysDiscoverable(boolean z) {
        this.mIsAlwaysDiscoverable = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFooterPreferenceTitle(int i) {
        if (i == 12) {
            this.mPreference.setTitle(getPreferenceTitle());
        } else {
            this.mPreference.setTitle(R$string.bluetooth_off_footer);
        }
    }

    private CharSequence getPreferenceTitle() {
        String name = this.mBluetoothAdapter.getName();
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return TextUtils.expandTemplate(this.mContext.getText(R$string.bluetooth_device_name_summary), BidiFormatter.getInstance().unicodeWrap(name));
    }

    private void initReceiver() {
        this.mBluetoothChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.connecteddevice.DiscoverableFooterPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                    DiscoverableFooterPreferenceController.this.updateFooterPreferenceTitle(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE));
                }
            }
        };
    }
}
