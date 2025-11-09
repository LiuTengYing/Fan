package com.android.settings.bluetooth;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$color;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatteryMeterView;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LeAudioProfile;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class LeAudioBluetoothDetailsHeaderController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, OnDestroy, CachedBluetoothDevice.Callback {
    static final int INVALID_RESOURCE_ID = -1;
    static final int LEFT_DEVICE_ID = 88413265;
    static final int RIGHT_DEVICE_ID = 176826530;
    private CachedBluetoothDevice mCachedDevice;
    Handler mHandler;
    boolean mIsRegisterCallback;
    LayoutPreference mLayoutPreference;
    private LocalBluetoothProfileManager mProfileManager;
    private static final String TAG = "LeAudioBtHeaderCtrl";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

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

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LeAudioBluetoothDetailsHeaderController(Context context, String str) {
        super(context, str);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mIsRegisterCallback = false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        if (cachedBluetoothDevice == null || this.mProfileManager == null) {
            return 2;
        }
        return (Utils.isAdvancedDetailsHeader(this.mCachedDevice.getDevice()) || !cachedBluetoothDevice.getConnectableProfiles().stream().anyMatch(new Predicate() { // from class: com.android.settings.bluetooth.LeAudioBluetoothDetailsHeaderController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getAvailabilityStatus$0;
                lambda$getAvailabilityStatus$0 = LeAudioBluetoothDetailsHeaderController.lambda$getAvailabilityStatus$0((LocalBluetoothProfile) obj);
                return lambda$getAvailabilityStatus$0;
            }
        })) ? 2 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getAvailabilityStatus$0(LocalBluetoothProfile localBluetoothProfile) {
        return localBluetoothProfile.getProfileId() == 22;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mLayoutPreference = layoutPreference;
        layoutPreference.setVisible(isAvailable());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (isAvailable()) {
            this.mIsRegisterCallback = true;
            this.mCachedDevice.registerCallback(this);
            refresh();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mIsRegisterCallback) {
            this.mCachedDevice.unregisterCallback(this);
            this.mIsRegisterCallback = false;
        }
    }

    public void init(CachedBluetoothDevice cachedBluetoothDevice, LocalBluetoothManager localBluetoothManager) {
        this.mCachedDevice = cachedBluetoothDevice;
        this.mProfileManager = localBluetoothManager.getProfileManager();
    }

    void refresh() {
        LayoutPreference layoutPreference = this.mLayoutPreference;
        if (layoutPreference == null || this.mCachedDevice == null) {
            return;
        }
        ImageView imageView = (ImageView) layoutPreference.findViewById(R$id.entity_header_icon);
        if (imageView != null) {
            Pair<Drawable, String> btRainbowDrawableWithDescription = BluetoothUtils.getBtRainbowDrawableWithDescription(this.mContext, this.mCachedDevice);
            imageView.setImageDrawable((Drawable) btRainbowDrawableWithDescription.first);
            imageView.setContentDescription((CharSequence) btRainbowDrawableWithDescription.second);
        }
        TextView textView = (TextView) this.mLayoutPreference.findViewById(R$id.entity_header_title);
        if (textView != null) {
            textView.setText(this.mCachedDevice.getName());
        }
        TextView textView2 = (TextView) this.mLayoutPreference.findViewById(R$id.entity_header_summary);
        if (textView2 != null) {
            textView2.setText(this.mCachedDevice.getConnectionSummary(true));
        }
        if (!this.mCachedDevice.isConnected() || this.mCachedDevice.isBusy()) {
            hideAllOfBatteryLayouts();
        } else {
            updateBatteryLayout();
        }
    }

    Drawable createBtBatteryIcon(Context context, int i) {
        BatteryMeterView.BatteryMeterDrawable batteryMeterDrawable = new BatteryMeterView.BatteryMeterDrawable(context, context.getColor(R$color.meter_background_color), context.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_meter_width), context.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_meter_height));
        batteryMeterDrawable.setBatteryLevel(i);
        batteryMeterDrawable.setColorFilter(new PorterDuffColorFilter(com.android.settingslib.Utils.getColorAttrDefaultColor(context, 16843817), PorterDuff.Mode.SRC));
        return batteryMeterDrawable;
    }

    private int getBatterySummaryResource(int i) {
        if (i == R$id.bt_battery_case) {
            return R$id.bt_battery_case_summary;
        }
        if (i == R$id.bt_battery_left) {
            return R$id.bt_battery_left_summary;
        }
        if (i == R$id.bt_battery_right) {
            return R$id.bt_battery_right_summary;
        }
        Log.d(TAG, "No summary resource id. The containerId is " + i);
        return INVALID_RESOURCE_ID;
    }

    private void hideAllOfBatteryLayouts() {
        updateBatteryLayout(R$id.bt_battery_case, INVALID_RESOURCE_ID);
        updateBatteryLayout(R$id.bt_battery_left, INVALID_RESOURCE_ID);
        updateBatteryLayout(R$id.bt_battery_right, INVALID_RESOURCE_ID);
    }

    private List<CachedBluetoothDevice> getAllOfLeAudioDevices() {
        if (this.mCachedDevice == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mCachedDevice);
        if (this.mCachedDevice.getGroupId() != INVALID_RESOURCE_ID) {
            for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevice.getMemberDevice()) {
                arrayList.add(cachedBluetoothDevice);
            }
        }
        return arrayList;
    }

    private void updateBatteryLayout() {
        hideAllOfBatteryLayouts();
        List<CachedBluetoothDevice> allOfLeAudioDevices = getAllOfLeAudioDevices();
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (allOfLeAudioDevices == null || allOfLeAudioDevices.isEmpty()) {
            Log.e(TAG, "There is no LeAudioProfile.");
        } else if (!leAudioProfile.isEnabled(this.mCachedDevice.getDevice())) {
            Log.d(TAG, "Show the legacy battery style if the LeAudio is not enabled.");
            TextView textView = (TextView) this.mLayoutPreference.findViewById(R$id.entity_header_summary);
            if (textView != null) {
                textView.setText(this.mCachedDevice.getConnectionSummary());
            }
        } else {
            for (CachedBluetoothDevice cachedBluetoothDevice : allOfLeAudioDevices) {
                int audioLocation = leAudioProfile.getAudioLocation(cachedBluetoothDevice.getDevice());
                Log.d(TAG, "LeAudioDevices:" + cachedBluetoothDevice.getDevice().getAnonymizedAddress() + ", deviceId:" + audioLocation);
                if (audioLocation == 0) {
                    Log.d(TAG, "The device does not support the AUDIO_LOCATION.");
                    return;
                }
                boolean z = true;
                boolean z2 = (LEFT_DEVICE_ID & audioLocation) != 0;
                boolean z3 = (audioLocation & RIGHT_DEVICE_ID) != 0;
                if ((z2 && z3) ? false : false) {
                    Log.d(TAG, "Show the legacy battery style if the device id is left+right.");
                    TextView textView2 = (TextView) this.mLayoutPreference.findViewById(R$id.entity_header_summary);
                    if (textView2 != null) {
                        textView2.setText(this.mCachedDevice.getConnectionSummary());
                    }
                } else if (z2) {
                    updateBatteryLayout(R$id.bt_battery_left, cachedBluetoothDevice.getBatteryLevel());
                } else if (z3) {
                    updateBatteryLayout(R$id.bt_battery_right, cachedBluetoothDevice.getBatteryLevel());
                } else {
                    Log.d(TAG, "The device id is other Audio Location. Do nothing.");
                }
            }
        }
    }

    private void updateBatteryLayout(int i, int i2) {
        View findViewById = this.mLayoutPreference.findViewById(i);
        if (findViewById == null) {
            Log.e(TAG, "updateBatteryLayout: No View");
        } else if (i2 != INVALID_RESOURCE_ID) {
            findViewById.setVisibility(0);
            TextView textView = (TextView) findViewById.requireViewById(getBatterySummaryResource(i));
            String formatPercentage = com.android.settingslib.Utils.formatPercentage(i2);
            textView.setText(formatPercentage);
            textView.setContentDescription(this.mContext.getString(R$string.bluetooth_battery_level, formatPercentage));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(createBtBatteryIcon(this.mContext, i2), (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            Log.d(TAG, "updateBatteryLayout: Hide it if it doesn't have battery information.");
            findViewById.setVisibility(8);
        }
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        if (this.mCachedDevice != null) {
            refresh();
        }
    }
}
