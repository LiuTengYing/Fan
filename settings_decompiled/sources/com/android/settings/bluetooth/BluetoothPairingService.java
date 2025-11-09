package com.android.settings.bluetooth;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat$Action;
import androidx.core.app.NotificationCompat$Builder;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public final class BluetoothPairingService extends Service {
    static final String ACTION_DISMISS_PAIRING = "com.android.settings.bluetooth.ACTION_DISMISS_PAIRING";
    static final String ACTION_PAIRING_DIALOG = "com.android.settings.bluetooth.ACTION_PAIRING_DIALOG";
    static final int NOTIFICATION_ID = 17301632;
    private BluetoothDevice mDevice;
    NotificationManager mNm;
    private boolean mRegistered = false;
    private final BroadcastReceiver mCancelReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.BluetoothPairingService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                Log.d("BluetoothPairingService", "onReceive() Bond state change : " + intExtra + ", device name : " + BluetoothPairingService.this.mDevice.getName());
                if (intExtra != 10 && intExtra != 12) {
                    return;
                }
            } else if (action.equals(BluetoothPairingService.ACTION_DISMISS_PAIRING)) {
                Log.d("BluetoothPairingService", "Notification cancel  (" + BluetoothPairingService.this.mDevice.getName() + ")");
                BluetoothPairingService.this.mDevice.cancelBondProcess();
            } else {
                int intExtra2 = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                Log.d("BluetoothPairingService", "Dismiss pairing for  (" + BluetoothPairingService.this.mDevice.getName() + "), BondState: " + intExtra2);
            }
            BluetoothPairingService.this.mNm.cancel(BluetoothPairingService.NOTIFICATION_ID);
            BluetoothPairingService.this.stopSelf();
        }
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent getPairingDialogIntent(Context context, Intent intent, int i) {
        int intExtra = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
        Intent intent2 = new Intent();
        intent2.setClass(context, BluetoothPairingDialog.class);
        intent2.putExtra("android.bluetooth.device.extra.DEVICE", (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
        intent2.putExtra("android.bluetooth.device.extra.PAIRING_VARIANT", intExtra);
        if (intExtra == 2 || intExtra == 4 || intExtra == 5) {
            intent2.putExtra("android.bluetooth.device.extra.PAIRING_KEY", intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE));
            intent2.putExtra("android.bluetooth.device.extra.PAIRING_INITIATOR", i);
        }
        intent2.setAction("android.bluetooth.device.action.PAIRING_REQUEST");
        intent2.setFlags(268435456);
        return intent2;
    }

    @Override // android.app.Service
    public void onCreate() {
        this.mNm = (NotificationManager) getSystemService(NotificationManager.class);
        this.mNm.createNotificationChannel(new NotificationChannel("bluetooth_notification_channel", getString(R$string.bluetooth), 4));
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            Log.e("BluetoothPairingService", "Can't start: null intent!");
            stopSelf();
            return 2;
        }
        String action = intent.getAction();
        Log.d("BluetoothPairingService", "onStartCommand() action : " + action);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        this.mDevice = bluetoothDevice;
        if (bluetoothDevice != null && bluetoothDevice.getBondState() != 11) {
            Log.w("BluetoothPairingService", "Device " + this.mDevice.getName() + " not bonding: " + this.mDevice.getBondState());
            this.mNm.cancel(NOTIFICATION_ID);
            stopSelf();
            return 2;
        }
        if (TextUtils.equals(action, "android.bluetooth.device.action.PAIRING_REQUEST")) {
            createPairingNotification(intent);
        } else if (TextUtils.equals(action, ACTION_DISMISS_PAIRING)) {
            Log.d("BluetoothPairingService", "Notification cancel  (" + this.mDevice.getName() + ")");
            this.mDevice.cancelBondProcess();
            this.mNm.cancel(NOTIFICATION_ID);
            stopSelf();
        } else if (TextUtils.equals(action, ACTION_PAIRING_DIALOG)) {
            Intent pairingDialogIntent = getPairingDialogIntent(this, intent, 2);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.PAIRING_CANCEL");
            intentFilter.addAction(ACTION_DISMISS_PAIRING);
            registerReceiver(this.mCancelReceiver, intentFilter);
            this.mRegistered = true;
            startActivity(pairingDialogIntent);
        }
        return 1;
    }

    private void createPairingNotification(Intent intent) {
        Resources resources = getResources();
        NotificationCompat$Builder localOnly = new NotificationCompat$Builder(this, "bluetooth_notification_channel").setSmallIcon(NOTIFICATION_ID).setTicker(resources.getString(R$string.bluetooth_notif_ticker)).setLocalOnly(true);
        int intExtra = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
        Intent intent2 = new Intent(ACTION_PAIRING_DIALOG);
        intent2.setClass(this, BluetoothPairingService.class);
        intent2.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
        intent2.putExtra("android.bluetooth.device.extra.PAIRING_VARIANT", intExtra);
        if (intExtra == 2 || intExtra == 4 || intExtra == 5) {
            intent2.putExtra("android.bluetooth.device.extra.PAIRING_KEY", intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE));
        }
        PendingIntent service = PendingIntent.getService(this, 0, intent2, 1275068416);
        Intent intent3 = new Intent(ACTION_DISMISS_PAIRING);
        intent3.setClass(this, BluetoothPairingService.class);
        intent3.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
        PendingIntent service2 = PendingIntent.getService(this, 0, intent3, 1140850688);
        String stringExtra = intent.getStringExtra("android.bluetooth.device.extra.NAME");
        if (TextUtils.isEmpty(stringExtra)) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            stringExtra = bluetoothDevice != null ? bluetoothDevice.getAlias() : resources.getString(17039374);
        }
        Log.d("BluetoothPairingService", "Show pairing notification for  (" + stringExtra + ")");
        localOnly.setContentTitle(resources.getString(R$string.bluetooth_notif_title)).setContentText(resources.getString(R$string.bluetooth_notif_message, stringExtra)).setContentIntent(service).setDefaults(1).setOngoing(true).setColor(getColor(17170460)).addAction(new NotificationCompat$Action.Builder(0, resources.getString(R$string.bluetooth_device_context_pair_connect), service).build()).addAction(new NotificationCompat$Action.Builder(0, resources.getString(17039360), service2).build());
        this.mNm.notify(NOTIFICATION_ID, localOnly.build());
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.mRegistered) {
            unregisterReceiver(this.mCancelReceiver);
            this.mRegistered = false;
        }
    }
}
