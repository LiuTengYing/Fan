package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$color;
import com.android.settings.R$dimen;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatteryMeterView;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.LayoutPreference;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AdvancedBluetoothDetailsHeaderController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, OnDestroy, CachedBluetoothDevice.Callback {
    private static final String BATTERY_ESTIMATE = "battery_estimate";
    private static final int CASE_DEVICE_ID = 3;
    private static final int CASE_LOW_BATTERY_LEVEL = 19;
    private static final String DATABASE_BLUETOOTH = "Bluetooth";
    private static final String DATABASE_ID = "id";
    private static final String ESTIMATE_READY = "estimate_ready";
    private static final float HALF_ALPHA = 0.5f;
    private static final int LEFT_DEVICE_ID = 1;
    private static final int LOW_BATTERY_LEVEL = 15;
    private static final int MAIN_DEVICE_ID = 4;
    private static final String PATH = "time_remaining";
    private static final String QUERY_PARAMETER_ADDRESS = "address";
    private static final String QUERY_PARAMETER_BATTERY_ID = "battery_id";
    private static final String QUERY_PARAMETER_BATTERY_LEVEL = "battery_level";
    private static final String QUERY_PARAMETER_TIMESTAMP = "timestamp";
    private static final int RIGHT_DEVICE_ID = 2;
    private static final long TIME_OF_HOUR;
    private static final long TIME_OF_MINUTE;
    BluetoothAdapter mBluetoothAdapter;
    private CachedBluetoothDevice mCachedDevice;
    Handler mHandler;
    final Map<String, Bitmap> mIconCache;
    boolean mIsLeftDeviceEstimateReady;
    boolean mIsRegisterCallback;
    boolean mIsRightDeviceEstimateReady;
    LayoutPreference mLayoutPreference;
    final BluetoothAdapter.OnMetadataChangedListener mMetadataListener;
    private static final String TAG = "AdvancedBtHeaderCtrl";
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    static {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        TIME_OF_HOUR = timeUnit.toMillis(3600L);
        TIME_OF_MINUTE = timeUnit.toMillis(60L);
    }

    public AdvancedBluetoothDetailsHeaderController(Context context, String str) {
        super(context, str);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mIsRegisterCallback = false;
        this.mMetadataListener = new BluetoothAdapter.OnMetadataChangedListener() { // from class: com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController.1
            public void onMetadataChanged(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                if (AdvancedBluetoothDetailsHeaderController.DEBUG) {
                    Object[] objArr = new Object[3];
                    objArr[0] = bluetoothDevice;
                    objArr[1] = Integer.valueOf(i);
                    objArr[2] = bArr == null ? null : new String(bArr);
                    Log.d(AdvancedBluetoothDetailsHeaderController.TAG, String.format("Metadata updated in Device %s: %d = %s.", objArr));
                }
                AdvancedBluetoothDetailsHeaderController.this.refresh();
            }
        };
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mIconCache = new HashMap();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        return (cachedBluetoothDevice != null && Utils.isAdvancedDetailsHeader(cachedBluetoothDevice.getDevice())) ? 0 : 2;
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
            this.mBluetoothAdapter.addOnMetadataChangedListener(this.mCachedDevice.getDevice(), this.mContext.getMainExecutor(), this.mMetadataListener);
            refresh();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mIsRegisterCallback) {
            this.mCachedDevice.unregisterCallback(this);
            this.mBluetoothAdapter.removeOnMetadataChangedListener(this.mCachedDevice.getDevice(), this.mMetadataListener);
            this.mIsRegisterCallback = false;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        for (Bitmap bitmap : this.mIconCache.values()) {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.mIconCache.clear();
    }

    public void init(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mCachedDevice = cachedBluetoothDevice;
    }

    void refresh() {
        LayoutPreference layoutPreference = this.mLayoutPreference;
        if (layoutPreference == null || this.mCachedDevice == null) {
            return;
        }
        ((TextView) layoutPreference.findViewById(R$id.entity_header_title)).setText(this.mCachedDevice.getName());
        ((TextView) this.mLayoutPreference.findViewById(R$id.entity_header_summary)).setText(this.mCachedDevice.getConnectionSummary(true));
        if (!this.mCachedDevice.isConnected() || this.mCachedDevice.isBusy()) {
            updateDisconnectLayout();
            return;
        }
        BluetoothDevice device = this.mCachedDevice.getDevice();
        String stringMetaData = BluetoothUtils.getStringMetaData(device, 17);
        if (TextUtils.equals(stringMetaData, "Watch") || TextUtils.equals(stringMetaData, "Default")) {
            this.mLayoutPreference.findViewById(R$id.layout_left).setVisibility(8);
            this.mLayoutPreference.findViewById(R$id.layout_right).setVisibility(8);
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(R$id.layout_middle), 5, 18, 20, 19, 0, 4);
        } else if (TextUtils.equals(stringMetaData, "Untethered Headset") || BluetoothUtils.getBooleanMetaData(device, 6)) {
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(R$id.layout_left), 7, 10, 21, 13, R$string.bluetooth_left_name, 1);
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(R$id.layout_middle), 9, 12, 23, 15, R$string.bluetooth_middle_name, 3);
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(R$id.layout_right), 8, 11, 22, 14, R$string.bluetooth_right_name, 2);
            showBothDevicesBatteryPredictionIfNecessary();
        }
    }

    Drawable createBtBatteryIcon(Context context, int i, boolean z) {
        BatteryMeterView.BatteryMeterDrawable batteryMeterDrawable = new BatteryMeterView.BatteryMeterDrawable(context, context.getColor(R$color.meter_background_color), context.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_meter_width), context.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_meter_height));
        batteryMeterDrawable.setBatteryLevel(i);
        batteryMeterDrawable.setColorFilter(new PorterDuffColorFilter(com.android.settingslib.Utils.getColorAttrDefaultColor(context, 16843817), PorterDuff.Mode.SRC));
        batteryMeterDrawable.setCharging(z);
        return batteryMeterDrawable;
    }

    private void updateSubLayout(LinearLayout linearLayout, int i, int i2, int i3, int i4, int i5, int i6) {
        if (linearLayout == null) {
            return;
        }
        BluetoothDevice device = this.mCachedDevice.getDevice();
        String stringMetaData = BluetoothUtils.getStringMetaData(device, i);
        ImageView imageView = (ImageView) linearLayout.findViewById(R$id.header_icon);
        if (stringMetaData != null) {
            updateIcon(imageView, stringMetaData);
        } else {
            Pair<Drawable, String> btRainbowDrawableWithDescription = BluetoothUtils.getBtRainbowDrawableWithDescription(this.mContext, this.mCachedDevice);
            imageView.setImageDrawable((Drawable) btRainbowDrawableWithDescription.first);
            imageView.setContentDescription((CharSequence) btRainbowDrawableWithDescription.second);
        }
        int intMetaData = BluetoothUtils.getIntMetaData(device, i2);
        boolean booleanMetaData = BluetoothUtils.getBooleanMetaData(device, i4);
        if (DEBUG) {
            Log.d(TAG, "updateSubLayout() icon : " + i + ", battery : " + i2 + ", charge : " + i4 + ", batteryLevel : " + intMetaData + ", charging : " + booleanMetaData + ", iconUri : " + stringMetaData);
        }
        if (i6 == 1 || i6 == 2) {
            showBatteryPredictionIfNecessary(linearLayout, i6, intMetaData);
        }
        TextView textView = (TextView) linearLayout.findViewById(R$id.bt_battery_summary);
        if (!isUntetheredHeadset(device)) {
            textView.setVisibility(8);
        } else if (intMetaData != -1) {
            linearLayout.setVisibility(0);
            textView.setText(com.android.settingslib.Utils.formatPercentage(intMetaData));
            textView.setVisibility(0);
            int intMetaData2 = BluetoothUtils.getIntMetaData(device, i3);
            if (intMetaData2 == -1) {
                intMetaData2 = i2 == 12 ? 19 : 15;
            }
            showBatteryIcon(linearLayout, intMetaData, intMetaData2, booleanMetaData);
        } else if (i6 == 4) {
            linearLayout.setVisibility(0);
            linearLayout.findViewById(R$id.bt_battery_icon).setVisibility(8);
            int batteryLevel = device.getBatteryLevel();
            if (batteryLevel != -1 && batteryLevel != -100) {
                textView.setText(com.android.settingslib.Utils.formatPercentage(batteryLevel));
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        } else {
            linearLayout.setVisibility(8);
        }
        TextView textView2 = (TextView) linearLayout.findViewById(R$id.header_title);
        if (i6 == 4) {
            textView2.setVisibility(8);
            return;
        }
        textView2.setText(i5);
        textView2.setVisibility(0);
    }

    private boolean isUntetheredHeadset(BluetoothDevice bluetoothDevice) {
        return BluetoothUtils.getBooleanMetaData(bluetoothDevice, 6) || TextUtils.equals(BluetoothUtils.getStringMetaData(bluetoothDevice, 17), "Untethered Headset");
    }

    private void showBatteryPredictionIfNecessary(final LinearLayout linearLayout, final int i, final int i2) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AdvancedBluetoothDetailsHeaderController.this.lambda$showBatteryPredictionIfNecessary$0(i, i2, linearLayout);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showBatteryPredictionIfNecessary$0(int i, int i2, LinearLayout linearLayout) {
        Cursor query = this.mContext.getContentResolver().query(new Uri.Builder().scheme("content").authority(this.mContext.getString(R$string.config_battery_prediction_authority)).appendPath(PATH).appendPath(DATABASE_ID).appendPath(DATABASE_BLUETOOTH).appendQueryParameter(QUERY_PARAMETER_ADDRESS, this.mCachedDevice.getAddress()).appendQueryParameter(QUERY_PARAMETER_BATTERY_ID, String.valueOf(i)).appendQueryParameter(QUERY_PARAMETER_BATTERY_LEVEL, String.valueOf(i2)).appendQueryParameter(QUERY_PARAMETER_TIMESTAMP, String.valueOf(System.currentTimeMillis())).build(), new String[]{BATTERY_ESTIMATE, ESTIMATE_READY}, null, null, null);
        if (query == null) {
            Log.w(TAG, "showBatteryPredictionIfNecessary() cursor is null!");
            return;
        }
        try {
            query.moveToFirst();
            while (!query.isAfterLast()) {
                int i3 = query.getInt(query.getColumnIndex(ESTIMATE_READY));
                long j = query.getLong(query.getColumnIndex(BATTERY_ESTIMATE));
                if (DEBUG) {
                    Log.d(TAG, "showBatteryTimeIfNecessary() batteryId : " + i + ", ESTIMATE_READY : " + i3 + ", BATTERY_ESTIMATE : " + j);
                }
                showBatteryPredictionIfNecessary(i3, j, linearLayout);
                if (i == 1) {
                    this.mIsLeftDeviceEstimateReady = i3 == 1;
                } else if (i == 2) {
                    this.mIsRightDeviceEstimateReady = i3 == 1;
                }
                query.moveToNext();
            }
        } finally {
            query.close();
        }
    }

    void showBatteryPredictionIfNecessary(final int i, final long j, final LinearLayout linearLayout) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AdvancedBluetoothDetailsHeaderController.this.lambda$showBatteryPredictionIfNecessary$1(linearLayout, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showBatteryPredictionIfNecessary$1(LinearLayout linearLayout, int i, long j) {
        TextView textView = (TextView) linearLayout.findViewById(R$id.bt_battery_prediction);
        if (i == 1) {
            textView.setText(StringUtil.formatElapsedTime(this.mContext, j, false, false));
        } else {
            textView.setVisibility(8);
        }
    }

    void showBothDevicesBatteryPredictionIfNecessary() {
        View findViewById = this.mLayoutPreference.findViewById(R$id.layout_left);
        int i = R$id.bt_battery_prediction;
        final TextView textView = (TextView) findViewById.findViewById(i);
        final TextView textView2 = (TextView) this.mLayoutPreference.findViewById(R$id.layout_right).findViewById(i);
        final int i2 = this.mIsLeftDeviceEstimateReady && this.mIsRightDeviceEstimateReady ? 0 : 8;
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AdvancedBluetoothDetailsHeaderController.lambda$showBothDevicesBatteryPredictionIfNecessary$2(textView, i2, textView2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBothDevicesBatteryPredictionIfNecessary$2(TextView textView, int i, TextView textView2) {
        textView.setVisibility(i);
        textView2.setVisibility(i);
    }

    private void showBatteryIcon(LinearLayout linearLayout, int i, int i2, boolean z) {
        boolean z2 = i <= i2 && !z;
        ImageView imageView = (ImageView) linearLayout.findViewById(R$id.bt_battery_icon);
        if (z2) {
            imageView.setImageDrawable(this.mContext.getDrawable(R$drawable.ic_battery_alert_24dp));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.mContext.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_width), this.mContext.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_height));
            layoutParams.rightMargin = this.mContext.getResources().getDimensionPixelSize(R$dimen.advanced_bluetooth_battery_right_margin);
            imageView.setLayoutParams(layoutParams);
        } else {
            imageView.setImageDrawable(createBtBatteryIcon(this.mContext, i, z));
            imageView.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        }
        imageView.setVisibility(0);
    }

    private void updateDisconnectLayout() {
        this.mLayoutPreference.findViewById(R$id.layout_left).setVisibility(8);
        this.mLayoutPreference.findViewById(R$id.layout_right).setVisibility(8);
        LinearLayout linearLayout = (LinearLayout) this.mLayoutPreference.findViewById(R$id.layout_middle);
        linearLayout.setVisibility(0);
        linearLayout.findViewById(R$id.header_title).setVisibility(8);
        linearLayout.findViewById(R$id.bt_battery_summary).setVisibility(8);
        linearLayout.findViewById(R$id.bt_battery_icon).setVisibility(8);
        String stringMetaData = BluetoothUtils.getStringMetaData(this.mCachedDevice.getDevice(), 5);
        if (DEBUG) {
            Log.d(TAG, "updateDisconnectLayout() iconUri : " + stringMetaData);
        }
        if (stringMetaData != null) {
            updateIcon((ImageView) linearLayout.findViewById(R$id.header_icon), stringMetaData);
        }
    }

    void updateIcon(final ImageView imageView, final String str) {
        if (this.mIconCache.containsKey(str)) {
            imageView.setAlpha(1.0f);
            imageView.setImageBitmap(this.mIconCache.get(str));
            return;
        }
        imageView.setAlpha(HALF_ALPHA);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AdvancedBluetoothDetailsHeaderController.this.lambda$updateIcon$4(str, imageView);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIcon$4(final String str, final ImageView imageView) {
        Uri parse = Uri.parse(str);
        try {
            this.mContext.getContentResolver().takePersistableUriPermission(parse, 1);
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.mContext.getContentResolver(), parse);
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    AdvancedBluetoothDetailsHeaderController.this.lambda$updateIcon$3(str, bitmap, imageView);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Failed to get bitmap for: " + str, e);
        } catch (SecurityException e2) {
            Log.e(TAG, "Failed to take persistable permission for: " + parse, e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIcon$3(String str, Bitmap bitmap, ImageView imageView) {
        this.mIconCache.put(str, bitmap);
        imageView.setAlpha(1.0f);
        imageView.setImageBitmap(bitmap);
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        if (this.mCachedDevice != null) {
            refresh();
        }
    }
}
