package com.unisoc.settings.timerpower;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.unisoc.internal.app.ShutdownCountdownActivity;
/* loaded from: classes2.dex */
public class AlarmKlaxon extends Service {
    private Alarm mCurrentAlarm;
    private int mInitialCallState;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.unisoc.settings.timerpower.AlarmKlaxon.1
        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int i, String str) {
            Log.v("TimerPower AlarmKlaxon :::: state = " + i + ",ignored = " + str);
            if (i != 0 || i == AlarmKlaxon.this.mInitialCallState) {
                return;
            }
            AlarmKlaxon.this.startActivityForShutdown();
            AlarmKlaxon.this.stopSelf();
        }
    };
    private long mStartTime;
    private TelephonyManager mTelephonyManager;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.v("service onCreate");
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        telephonyManager.listen(this.mPhoneStateListener, 32);
        AlarmAlertWakeLock.acquireCpuWakeLock(this);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            Log.v("AlarmKlaxon intent is null");
            stopSelf();
            return 2;
        }
        Alarm alarm = (Alarm) intent.getParcelableExtra("intent.extra.alarm");
        if (alarm == null) {
            Log.v("AlarmKlaxon failed to parse the alarm from the intent");
            stopSelf();
            return 2;
        }
        play(alarm);
        this.mCurrentAlarm = alarm;
        this.mInitialCallState = this.mTelephonyManager.getCallState();
        return 1;
    }

    private void play(Alarm alarm) {
        if (checkCallIsUsing()) {
            Log.v("in-call , AlarmKlaxon don't play alarm");
            return;
        }
        if (this.mTelephonyManager.getCallState() == 0) {
            startActivityForShutdown();
            stopSelf();
        }
        this.mStartTime = System.currentTimeMillis();
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.v("AlarmKlaxon onDestroy");
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        AlarmAlertWakeLock.releaseCpuLock();
    }

    public boolean checkCallIsUsing() {
        return this.mTelephonyManager.getCallState() != 0;
    }

    public void startActivityForShutdown() {
        Intent intent = new Intent("sprd.intent.action.ACTION_SHUTDOWN_COUNTDOWN");
        intent.setClassName("com.unisoc", ShutdownCountdownActivity.class.getName());
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.addFlags(268435456);
        intent.putExtra("shutdown_mode", "timer");
        startActivity(intent);
    }
}
