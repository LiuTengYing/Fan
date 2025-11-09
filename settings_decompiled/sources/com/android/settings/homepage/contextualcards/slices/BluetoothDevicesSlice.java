package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.bluetooth.AvailableMediaBluetoothDeviceUpdater;
import com.android.settings.bluetooth.BluetoothDeviceDetailsFragment;
import com.android.settings.bluetooth.BluetoothPairingDetail;
import com.android.settings.bluetooth.SavedBluetoothDeviceUpdater;
import com.android.settings.connecteddevice.ConnectedDeviceDashboardFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class BluetoothDevicesSlice implements CustomSliceable {
    @VisibleForTesting
    static final String BLUETOOTH_DEVICE_HASH_CODE = "bluetooth_device_hash_code";
    private static final Comparator<CachedBluetoothDevice> COMPARATOR = Comparator.naturalOrder();
    @VisibleForTesting
    static final int DEFAULT_EXPANDED_ROW_COUNT = 2;
    @VisibleForTesting
    static final String EXTRA_ENABLE_BLUETOOTH = "enable_bluetooth";
    private static boolean sBluetoothEnabling;
    private AvailableMediaBluetoothDeviceUpdater mAvailableMediaBtDeviceUpdater;
    private final Context mContext;
    private SavedBluetoothDeviceUpdater mSavedBtDeviceUpdater;

    public BluetoothDevicesSlice(Context context) {
        this.mContext = context;
        BluetoothUpdateWorker.initLocalBtManager(context);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.BLUETOOTH_DEVICES_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            Log.i("BluetoothDevicesSlice", "Bluetooth is not supported on this hardware platform");
            return null;
        }
        final ListBuilder accentColor = new ListBuilder(this.mContext, getUri(), -1L).setAccentColor(-1);
        if (!isBluetoothEnabled(defaultAdapter) && !sBluetoothEnabling) {
            return accentColor.addRow(getBluetoothOffHeader()).build();
        }
        sBluetoothEnabling = false;
        accentColor.addRow(getBluetoothOnHeader());
        getBluetoothRowBuilders().forEach(new Consumer() { // from class: com.android.settings.homepage.contextualcards.slices.BluetoothDevicesSlice$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ListBuilder.this.addRow((ListBuilder.RowBuilder) obj);
            }
        });
        return accentColor.build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, ConnectedDeviceDashboardFragment.class.getName(), "", this.mContext.getText(R$string.connected_devices_dashboard_title).toString(), 1401, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(getUri());
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_connected_devices;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        if (intent.getBooleanExtra(EXTRA_ENABLE_BLUETOOTH, false)) {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (isBluetoothEnabled(defaultAdapter)) {
                return;
            }
            sBluetoothEnabling = true;
            defaultAdapter.enable();
            this.mContext.getContentResolver().notifyChange(getUri(), null);
            return;
        }
        int intExtra = intent.getIntExtra(BLUETOOTH_DEVICE_HASH_CODE, -1);
        for (CachedBluetoothDevice cachedBluetoothDevice : getPairedBluetoothDevices()) {
            if (cachedBluetoothDevice.hashCode() == intExtra) {
                if (cachedBluetoothDevice.isConnected()) {
                    cachedBluetoothDevice.setActive();
                    return;
                } else if (cachedBluetoothDevice.isBusy()) {
                    return;
                } else {
                    cachedBluetoothDevice.connect();
                    return;
                }
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return BluetoothUpdateWorker.class;
    }

    @VisibleForTesting
    List<CachedBluetoothDevice> getPairedBluetoothDevices() {
        ArrayList arrayList = new ArrayList();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Log.i("BluetoothDevicesSlice", "Cannot get Bluetooth devices, Bluetooth is disabled.");
            return arrayList;
        }
        LocalBluetoothManager localBtManager = BluetoothUpdateWorker.getLocalBtManager();
        if (localBtManager == null) {
            Log.i("BluetoothDevicesSlice", "Cannot get Bluetooth devices, Bluetooth is not ready.");
            return arrayList;
        }
        return (List) localBtManager.getCachedDeviceManager().getCachedDevicesCopy().stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.slices.BluetoothDevicesSlice$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPairedBluetoothDevices$1;
                lambda$getPairedBluetoothDevices$1 = BluetoothDevicesSlice.lambda$getPairedBluetoothDevices$1((CachedBluetoothDevice) obj);
                return lambda$getPairedBluetoothDevices$1;
            }
        }).sorted(COMPARATOR).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getPairedBluetoothDevices$1(CachedBluetoothDevice cachedBluetoothDevice) {
        return cachedBluetoothDevice.getDevice().getBondState() == 12;
    }

    @VisibleForTesting
    PendingIntent getBluetoothDetailIntent(CachedBluetoothDevice cachedBluetoothDevice) {
        Bundle bundle = new Bundle();
        bundle.putString("device_address", cachedBluetoothDevice.getDevice().getAddress());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(BluetoothDeviceDetailsFragment.class.getName()).setArguments(bundle).setTitleRes(R$string.device_details_title).setSourceMetricsCategory(1009);
        return PendingIntent.getActivity(this.mContext, cachedBluetoothDevice.hashCode(), subSettingLauncher.toIntent(), 67108864);
    }

    @VisibleForTesting
    IconCompat getBluetoothDeviceIcon(CachedBluetoothDevice cachedBluetoothDevice) {
        Drawable drawable = (Drawable) BluetoothUtils.getBtRainbowDrawableWithDescription(this.mContext, cachedBluetoothDevice).first;
        if (drawable == null) {
            return IconCompat.createWithResource(this.mContext, 17302847);
        }
        return Utils.createIconWithDrawable(drawable);
    }

    private ListBuilder.RowBuilder getBluetoothOffHeader() {
        Drawable drawable = this.mContext.getDrawable(R$drawable.ic_bluetooth_disabled);
        Context context = this.mContext;
        drawable.setTint(com.android.settingslib.Utils.getDisabled(context, com.android.settingslib.Utils.getColorAttrDefaultColor(context, 16843817)));
        IconCompat createIconWithDrawable = Utils.createIconWithDrawable(drawable);
        CharSequence text = this.mContext.getText(R$string.bluetooth_devices_card_off_title);
        CharSequence text2 = this.mContext.getText(R$string.bluetooth_devices_card_off_summary);
        return new ListBuilder.RowBuilder().setTitleItem(createIconWithDrawable, 0).setTitle(text).setSubtitle(text2).setPrimaryAction(SliceAction.create(PendingIntent.getBroadcast(this.mContext, 0, new Intent(getUri().toString()).setClass(this.mContext, SliceBroadcastReceiver.class).putExtra(EXTRA_ENABLE_BLUETOOTH, true), 67108864), createIconWithDrawable, 0, text));
    }

    private ListBuilder.RowBuilder getBluetoothOnHeader() {
        Drawable drawable = this.mContext.getDrawable(17302847);
        drawable.setTint(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext));
        IconCompat createIconWithDrawable = Utils.createIconWithDrawable(drawable);
        CharSequence text = this.mContext.getText(R$string.bluetooth_devices);
        return new ListBuilder.RowBuilder().setTitleItem(createIconWithDrawable, 0).setTitle(text).setPrimaryAction(SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864), createIconWithDrawable, 0, text)).addEndItem(getPairNewDeviceAction());
    }

    private SliceAction getPairNewDeviceAction() {
        Drawable drawable = this.mContext.getDrawable(R$drawable.ic_add_24dp);
        drawable.setTint(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext));
        IconCompat createIconWithDrawable = Utils.createIconWithDrawable(drawable);
        String string = this.mContext.getString(R$string.bluetooth_pairing_pref_title);
        Intent intent = new SubSettingLauncher(this.mContext).setDestination(BluetoothPairingDetail.class.getName()).setTitleRes(R$string.bluetooth_pairing_page_title).setSourceMetricsCategory(1018).toIntent();
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, intent.hashCode(), intent, 67108864), createIconWithDrawable, 0, string);
    }

    private List<ListBuilder.RowBuilder> getBluetoothRowBuilders() {
        ArrayList arrayList = new ArrayList();
        List<CachedBluetoothDevice> pairedBluetoothDevices = getPairedBluetoothDevices();
        if (pairedBluetoothDevices.isEmpty()) {
            return arrayList;
        }
        lazyInitUpdaters();
        for (CachedBluetoothDevice cachedBluetoothDevice : pairedBluetoothDevices) {
            if (arrayList.size() >= 2) {
                break;
            }
            String connectionSummary = cachedBluetoothDevice.getConnectionSummary();
            if (connectionSummary == null) {
                connectionSummary = this.mContext.getString(R$string.connected_device_previously_connected_screen_title);
            }
            ListBuilder.RowBuilder subtitle = new ListBuilder.RowBuilder().setTitleItem(getBluetoothDeviceIcon(cachedBluetoothDevice), 0).setTitle(cachedBluetoothDevice.getName()).setSubtitle(connectionSummary);
            if (this.mAvailableMediaBtDeviceUpdater.isFilterMatched(cachedBluetoothDevice) || this.mSavedBtDeviceUpdater.isFilterMatched(cachedBluetoothDevice)) {
                subtitle.setPrimaryAction(buildPrimaryBluetoothAction(cachedBluetoothDevice));
                subtitle.addEndItem(buildBluetoothDetailDeepLinkAction(cachedBluetoothDevice));
            } else {
                subtitle.setPrimaryAction(buildBluetoothDetailDeepLinkAction(cachedBluetoothDevice));
            }
            arrayList.add(subtitle);
        }
        return arrayList;
    }

    private void lazyInitUpdaters() {
        if (this.mAvailableMediaBtDeviceUpdater == null) {
            this.mAvailableMediaBtDeviceUpdater = new AvailableMediaBluetoothDeviceUpdater(this.mContext, null, null);
        }
        if (this.mSavedBtDeviceUpdater == null) {
            this.mSavedBtDeviceUpdater = new SavedBluetoothDeviceUpdater(this.mContext, null, null);
        }
    }

    @VisibleForTesting
    SliceAction buildPrimaryBluetoothAction(CachedBluetoothDevice cachedBluetoothDevice) {
        return SliceAction.create(PendingIntent.getBroadcast(this.mContext, cachedBluetoothDevice.hashCode(), new Intent(getUri().toString()).setClass(this.mContext, SliceBroadcastReceiver.class).putExtra(BLUETOOTH_DEVICE_HASH_CODE, cachedBluetoothDevice.hashCode()), 67108864), getBluetoothDeviceIcon(cachedBluetoothDevice), 0, cachedBluetoothDevice.getName());
    }

    @VisibleForTesting
    SliceAction buildBluetoothDetailDeepLinkAction(CachedBluetoothDevice cachedBluetoothDevice) {
        return SliceAction.createDeeplink(getBluetoothDetailIntent(cachedBluetoothDevice), IconCompat.createWithResource(this.mContext, R$drawable.ic_settings_accent), 0, cachedBluetoothDevice.getName());
    }

    private boolean isBluetoothEnabled(BluetoothAdapter bluetoothAdapter) {
        int state = bluetoothAdapter.getState();
        return state == 11 || state == 12;
    }
}
