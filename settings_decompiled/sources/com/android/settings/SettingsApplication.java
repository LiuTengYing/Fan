package com.android.settings;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.factory.protocol.CarProtocol;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.MyList;
import com.android.settings.utils.NetworkUtils;
import com.android.settingslib.applications.AppIconCacheManager;
import com.syu.crash.CrashHandler;
import com.syu.remote.Message;
import com.syu.systemupdate.aidl.CheckStatusCallback;
import com.syu.systemupdate.aidl.NewVersionInfo;
import com.syu.systemupdate.aidl.UpdateStatusListener;
import com.syu.util.DataProvider;
import com.syu.util.MyReceiver;
import com.syu.util.MySharePreference;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SettingsApplication extends Application implements IpcNotify {
    public static String OneKey_Folder = null;
    public static String Path_Native = null;
    public static String[] Path_SD = null;
    public static String[] Path_USB = null;
    public static SettingsApplication mApplication = null;
    public static int mCanbusId = 0;
    public static String[] mConf_SD = null;
    public static String[] mConf_USB = null;
    public static ContentResolver mContentResolver = null;
    public static int mCustomerid = 1;
    public static int mHeightFix = 0;
    public static MyList[] mListUSBNavi = null;
    public static LocationManager mLocationManager = null;
    private static String mPkgName = null;
    public static Resources mResources = null;
    public static int mUi = -1;
    public static int mWidthFix = 1;
    public String bIsShowVerticalAndHorizontal;
    private CheckStatusCallback checkStatusCallback;
    private ConnectivityManager mConnectivityManager;
    public static Boolean is_UpdatingCartype = Boolean.FALSE;
    public static boolean bStartActFromCarProtocol = false;
    public static int orientation = 2;
    public static String sunrise = "";
    public static String sunset = "";
    public static String month = "";
    public static Date sunriseT = null;
    public static Date sunsetT = null;
    public static boolean isRecentApp = false;
    public static int SD_CNT = 2;
    public static int USB_CNT = 21;
    public static int riseH = 7;
    public static int riseM = 0;
    public static int downH = 19;
    public static int downM = 0;
    public static boolean initCanbus = false;
    public static MyList[] mListSDNavi = new MyList[2];
    private WeakReference<SettingsHomepageActivity> mHomeActivity = new WeakReference<>(null);
    private WeakReference<SettingsBaseActivity> mBaseActivity = new WeakReference<>(null);
    public String cartypeStr = "";
    private int timeZoneIndex = 0;
    private List<UpdateStatusListener> updateStatusListeners = new ArrayList();
    private int oldUiMode = -1;
    final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.settings.SettingsApplication.1
        public void onTetheringFailed() {
            super.onTetheringFailed();
            Log.e("SettingsApplication", "Failed to start Wi-Fi Tethering.");
        }
    };
    public RequestLightTime requestLightTime = new RequestLightTime();
    public Runnable postProtocolUpdate = new Runnable() { // from class: com.android.settings.SettingsApplication.2
        /* JADX WARN: Code restructure failed: missing block: B:18:0x0060, code lost:
            if (android.os.SystemProperties.getBoolean("com.android.systemui.is.switch", false) != false) goto L3;
         */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                r5 = this;
                java.lang.String r0 = "fangli"
                java.lang.String r1 = "run: 111"
                android.util.Log.d(r0, r1)
                com.android.settings.SettingsApplication r1 = com.android.settings.SettingsApplication.this
                java.lang.String r2 = "persist.syu.showvhprotocol"
                java.lang.String r3 = "default"
                java.lang.String r2 = android.os.SystemProperties.get(r2, r3)
                r1.bIsShowVerticalAndHorizontal = r2
                com.android.settings.SettingsApplication r1 = com.android.settings.SettingsApplication.this
                java.lang.String r1 = r1.bIsShowVerticalAndHorizontal
                java.lang.String r2 = "h"
                boolean r1 = r2.equalsIgnoreCase(r1)
                java.lang.String r2 = "canbus_sp.db"
                java.lang.String r3 = "canbus.db"
                if (r1 == 0) goto L26
            L24:
                r2 = r3
                goto L65
            L26:
                com.android.settings.SettingsApplication r1 = com.android.settings.SettingsApplication.this
                java.lang.String r1 = r1.bIsShowVerticalAndHorizontal
                java.lang.String r4 = "v"
                boolean r1 = r4.equalsIgnoreCase(r1)
                if (r1 == 0) goto L34
                goto L65
            L34:
                com.android.settings.SettingsApplication r1 = com.android.settings.SettingsApplication.this
                java.lang.String r1 = r1.bIsShowVerticalAndHorizontal
                java.lang.String r4 = "hv"
                boolean r1 = r4.equalsIgnoreCase(r1)
                if (r1 != 0) goto L63
                com.android.settings.SettingsApplication r1 = com.android.settings.SettingsApplication.this
                java.lang.String r1 = r1.bIsShowVerticalAndHorizontal
                java.lang.String r4 = "vh"
                boolean r1 = r4.equalsIgnoreCase(r1)
                if (r1 == 0) goto L4e
                goto L63
            L4e:
                int r1 = com.android.settings.SettingsApplication.mHeightFix
                int r4 = com.android.settings.SettingsApplication.mWidthFix
                if (r1 > r4) goto L65
                int r1 = com.android.settings.SettingsApplication.orientation
                r4 = 1
                if (r1 != r4) goto L24
                r1 = 0
                java.lang.String r4 = "com.android.systemui.is.switch"
                boolean r1 = android.os.SystemProperties.getBoolean(r4, r1)
                if (r1 != 0) goto L24
                goto L65
            L63:
                java.lang.String r2 = "canbus_hsp.db"
            L65:
                java.lang.String r1 = "run: 222"
                android.util.Log.d(r0, r1)
                java.lang.String r0 = "com.fyt.canbusdb"
                android.os.SystemProperties.set(r0, r2)
                java.lang.Boolean r0 = java.lang.Boolean.TRUE
                com.android.settings.SettingsApplication.is_UpdatingCartype = r0
                com.android.settings.SettingsApplication r5 = com.android.settings.SettingsApplication.this
                r5.initCanbus()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.SettingsApplication.AnonymousClass2.run():void");
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.android.settings.SettingsApplication.3
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SettingsApplication.this.checkStatusCallback = CheckStatusCallback.Stub.asInterface(iBinder);
        }
    };

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvr(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGesture(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGsensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyIpod(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(Message message) {
    }

    static {
        for (int i = 0; i < SD_CNT; i++) {
            mListSDNavi[i] = new MyList();
        }
        mListUSBNavi = new MyList[USB_CNT];
        for (int i2 = 0; i2 < USB_CNT; i2++) {
            mListUSBNavi[i2] = new MyList();
        }
        OneKey_Folder = "/onekey";
        mConf_SD = new String[SD_CNT];
        for (int i3 = 0; i3 < SD_CNT; i3++) {
            mConf_SD[i3] = new String();
            mConf_SD[i3] = "/xxxx";
        }
        mConf_USB = new String[USB_CNT];
        for (int i4 = 0; i4 < USB_CNT; i4++) {
            mConf_USB[i4] = new String();
            mConf_USB[i4] = "/xxxx";
        }
        Path_Native = "/xxxx";
        Path_SD = new String[SD_CNT];
        for (int i5 = 0; i5 < SD_CNT; i5++) {
            Path_SD[i5] = new String();
            Path_SD[i5] = "/xxxx";
        }
        Path_USB = new String[USB_CNT];
        for (int i6 = 0; i6 < USB_CNT; i6++) {
            Path_USB[i6] = new String();
            Path_USB[i6] = "/xxxx";
        }
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mHeightFix = getWindowManeger()[1];
        mWidthFix = getWindowManeger()[0];
        this.mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService("connectivity");
        new ActivityEmbeddingRulesController(this).initRules();
        Log.d("SettingsApplication", "onCreate: ");
        mCustomerid = SystemProperties.getInt("ro.build.fytmanufacturer", 1);
        int i = SystemProperties.getInt("persist.hzq.systemuitestid", -1);
        if (i != -1) {
            mCustomerid = i;
        }
        mPkgName = getPackageName();
        CrashHandler.getInstance(mApplication);
        MyReceiver myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.settings.action.requestforimei");
        intentFilter.addAction("com.android.settings.action.resultforimei");
        intentFilter.addAction("com.android.settings.action.resizefont");
        intentFilter.addAction("com.syu.settings.wiredscreenTest");
        intentFilter.addAction("FOURCAMERA2_BROADCAST_RECV");
        intentFilter.addAction("android.app.action.NIGHT_MODE_CHANGED");
        registerReceiver(myReceiver, intentFilter);
        initData();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.uiMode;
        Log.d("SettingsApplications", "onConfigurationChanged: " + i + "oldUiMode =====" + this.oldUiMode);
        if (getHomeActivity() != null) {
            getHomeActivity().onConfigurationChanged(configuration);
        }
        if (getBaseActivity() != null) {
            getBaseActivity().onConfigurationChanged(configuration);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Resources resources = super.getResources();
        int i = mWidthFix;
        if (i == 2000 || ((i == 1920 && mHeightFix > 1000) || (i == 2880 && mHeightFix == 1080))) {
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            if (displayMetrics.densityDpi != 240 || displayMetrics.density != 1.5f || displayMetrics.scaledDensity != 1.75f) {
                displayMetrics.density = 1.5f;
                displayMetrics.scaledDensity = 1.75f;
                displayMetrics.densityDpi = 240;
            }
            return resources;
        } else if (i <= 768) {
            DisplayMetrics displayMetrics2 = resources.getDisplayMetrics();
            if (displayMetrics2.densityDpi != 120 || displayMetrics2.density != 0.75f || displayMetrics2.scaledDensity != 0.95f) {
                Log.d("SettingsApplication", "getResources: " + mWidthFix);
                displayMetrics2.density = 0.75f;
                displayMetrics2.scaledDensity = 0.95f;
                displayMetrics2.densityDpi = 120;
            }
            return resources;
        } else {
            DisplayMetrics displayMetrics3 = resources.getDisplayMetrics();
            if (displayMetrics3.densityDpi != 160 || displayMetrics3.density != 1.0f || displayMetrics3.scaledDensity != 1.25f) {
                displayMetrics3.density = 1.0f;
                displayMetrics3.scaledDensity = 1.25f;
                displayMetrics3.densityDpi = 160;
            }
            return resources;
        }
    }

    public void setHomeActivity(SettingsHomepageActivity settingsHomepageActivity) {
        this.mHomeActivity = new WeakReference<>(settingsHomepageActivity);
    }

    public int getIdDrawable(String str) {
        return mResources.getIdentifier(str, "drawable", mPkgName);
    }

    public SettingsHomepageActivity getHomeActivity() {
        return this.mHomeActivity.get();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        AppIconCacheManager.getInstance();
        AppIconCacheManager.release();
    }

    private void initData() {
        mUi = SystemProperties.getInt("ro.fyt.uiid", 0);
        mResources = getResources();
        mContentResolver = getContentResolver();
        DataProvider.openDatabase();
        MySharePreference.init(this, "Settings");
        if (!IpcObj.getInstance().isConnect()) {
            IpcObj.getInstance().setManager(this);
        }
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(this);
        IpcObj.getInstance().setObserverMoudle(0, 4);
        new Thread(this.requestLightTime).start();
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public void setBaseActivity(SettingsBaseActivity settingsBaseActivity) {
        this.mBaseActivity = new WeakReference<>(settingsBaseActivity);
    }

    public SettingsBaseActivity getBaseActivity() {
        return this.mBaseActivity.get();
    }

    public void setThemeMode(int i) {
        SystemProperties.set("persist.syu.thememode", i + "");
    }

    public int getThemeMode() {
        return SystemProperties.getInt("persist.syu.thememode", 2);
    }

    public void setIsAutoTheme(int i) {
        SystemProperties.set("persist.syu.autotheme", i + "");
    }

    public int getIsAutoTheme() {
        return SystemProperties.getInt("persist.syu.autotheme", 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class RequestLightTime implements Runnable {
        private boolean requestWeekData = false;

        RequestLightTime() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                Looper.prepare();
                Log.i("hzqok", "333 = " + NetworkUtils.isNetWorkAvailable(SettingsApplication.this.getApplicationContext()));
                if (NetworkUtils.isNetWorkAvailable(SettingsApplication.this.getApplicationContext())) {
                    String format = new SimpleDateFormat("yyyyMM").format(new Date());
                    Log.i("hzqok", "444 = " + format);
                    if (SettingsApplication.mLocationManager == null) {
                        SettingsApplication.mLocationManager = (LocationManager) SettingsApplication.this.getSystemService("location");
                    }
                    Location lastKnownLocation = SettingsApplication.mLocationManager.getLastKnownLocation("gps");
                    if (lastKnownLocation == null) {
                        lastKnownLocation = SettingsApplication.mLocationManager.getLastKnownLocation("network");
                    }
                    if (lastKnownLocation != null) {
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();
                        TimeZone timeZone = TimeZone.getDefault();
                        SettingsApplication.this.timeZoneIndex = (timeZone.getRawOffset() / 3600) / AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP;
                        SettingsApplication settingsApplication = SettingsApplication.this;
                        settingsApplication.calculateSunshineTime(longitude, latitude, settingsApplication.timeZoneIndex);
                    }
                } else {
                    String str = SystemProperties.get("persist.syu.sunTime", "");
                    if (!TextUtils.isEmpty(str)) {
                        JSONObject jSONObject = new JSONObject(str);
                        SettingsApplication.riseH = jSONObject.optInt("riseH");
                        SettingsApplication.riseM = jSONObject.optInt("riseM");
                        SettingsApplication.downH = jSONObject.optInt("downH");
                        SettingsApplication.downM = jSONObject.optInt("downM");
                    }
                }
            } catch (Exception e) {
                Log.i("hzq", Log.getStackTraceString(e));
            }
            Looper.loop();
        }
    }

    public String getAppName(String str) {
        String str2 = "";
        try {
            PackageManager packageManager = getPackageManager();
            str2 = packageManager.getPackageInfo(str, 0).applicationInfo.loadLabel(packageManager).toString();
            Log.d("SettingsApplication", "Application Name: " + str2);
            return str2;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SettingsApplication", "Package name not found", e);
            return str2;
        }
    }

    public void initCanbus() {
        new ParserTask().execute(new Void[0]);
    }

    /* loaded from: classes.dex */
    public class ParserTask extends AsyncTask<Void, Void, Void> {
        public ParserTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            int i;
            try {
                Log.d("fangli", "doInBackground: 111");
                int i2 = SettingsApplication.mCustomerid;
                boolean z = false;
                if ((i2 != 63 && i2 != 94) || ((i = SettingsApplication.mUi) != 101 && i != 6 && i != 4 && (i != 2 || SystemProperties.getInt("persist.syu.DZSJCanbus", 0) != 1))) {
                    z = true;
                }
                if (z) {
                    CarProtocol.getInstance(SettingsApplication.mApplication).getTestDataByProvider();
                } else {
                    CarProtocol.getInstance(SettingsApplication.mApplication).parseXml("canbus_syu");
                }
                Log.d("fangli", "doInBackground: 222");
                return null;
            } catch (Exception e) {
                Log.i("fangli", Log.getStackTraceString(e));
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Void... voidArr) {
            super.onProgressUpdate((Object[]) voidArr);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((ParserTask) r1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateSunshineTime(double d, double d2, int i) {
        Log.e("fangli", "------>> calculateSunshineTime longitude : " + d + " latitude: " + d2);
        try {
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = (Calendar) calendar.clone();
            double acos = (((((i * 15) + 180) - d) - ((Math.acos((-Math.tan(((Math.cos(((calendar.get(6) + 9) * 6.283185307179586d) / calendar.getActualMaximum(6)) * (-23.4d)) * 3.141592653589793d) / 180.0d)) * Math.tan((d2 * 3.141592653589793d) / 180.0d)) * 180.0d) / 3.141592653589793d)) * 24.0d) / 360.0d;
            calendar2.set(11, (int) acos);
            calendar2.set(12, (int) ((acos * 60.0d) % 60.0d));
            calendar2.set(13, 0);
            sunriseT = calendar2.getTime();
            sunsetT = sunsetTime(acos, d, i);
            Log.d("fangli", "calculateSunshineTime: " + sunriseT.toString() + "  " + sunsetT.toString() + "  " + i);
            riseH = sunriseT.getHours();
            riseM = sunriseT.getMinutes();
            downH = sunsetT.getHours();
            downM = sunsetT.getMinutes();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("riseH", riseH);
            jSONObject.put("riseM", riseM);
            jSONObject.put("downH", downH);
            jSONObject.put("downM", downM);
            Log.d("fangli", "calculateSunshineTime: " + jSONObject.toString());
            SystemProperties.set("persist.syu.sunTime", jSONObject.toString());
        } catch (Exception e) {
            Log.e("Qin", "------>> calculateSunshineTime longitude : " + d + " latitude: " + d2 + " == >>  Error!!");
            e.printStackTrace();
        }
    }

    Date sunsetTime(double d, double d2, int i) {
        double d3 = (((((i * 15) - d2) / 180.0d) + 1.0d) * 24.0d) - d;
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(11, (int) d3);
        calendar.set(12, (int) ((d3 * 60.0d) % 60.0d));
        calendar.set(13, 0);
        return calendar.getTime();
    }

    public void connectUpdateservice() {
        Intent intent = new Intent();
        intent.setClassName("com.syu.systemupdate", "com.syu.systemupdate.MainActivity");
        mApplication.bindService(intent, this.serviceConnection, 1);
    }

    public boolean isUpdateServiceConnect() {
        return (this.serviceConnection == null || this.checkStatusCallback == null) ? false : true;
    }

    public void registerUpdateListener(UpdateStatusListener updateStatusListener) {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.registerCallBack(updateStatusListener);
                this.updateStatusListeners.add(updateStatusListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "registerUpdateListener: " + e.toString());
        }
    }

    public void unregisterUpdateListener(UpdateStatusListener updateStatusListener) {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.removeListener(updateStatusListener);
            }
            this.updateStatusListeners.remove(updateStatusListener);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "removeStatusListener: " + e.toString());
        }
    }

    public void removeAllListener() {
        List<UpdateStatusListener> list;
        try {
            if (this.checkStatusCallback != null && (list = this.updateStatusListeners) != null && list.size() > 0) {
                for (UpdateStatusListener updateStatusListener : this.updateStatusListeners) {
                    this.checkStatusCallback.removeListener(updateStatusListener);
                }
            }
            this.updateStatusListeners.clear();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "removeStatusListener: " + e.toString());
        }
    }

    public void startCheck() {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.startCheck();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "startCheck: " + e.toString());
        }
    }

    public void setProductId(int i) {
        try {
            this.checkStatusCallback.setProductId(i);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "setProductId: " + e.toString());
        }
    }

    public void startDownload() {
        try {
            this.checkStatusCallback.startDownload();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "startDownload onClick: " + e.toString());
        }
    }

    public NewVersionInfo getNewVersionInfo() {
        try {
            return this.checkStatusCallback.getNewVersionInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "getNewVersionInfo: " + e.toString());
            return null;
        }
    }

    public String getStatus() {
        String str;
        try {
            str = this.checkStatusCallback.getStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
            str = "";
        }
        Log.d("SettingsApplication", "getStatus: " + str);
        return str;
    }

    public void startUpgrade() {
        try {
            this.checkStatusCallback.startUpgrade();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "startInstallSystem: " + e.toString());
        }
    }

    public void setWifiDownload(boolean z) {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.setWifiDownload(z);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "onWifiDownload: " + e.toString());
        }
    }

    public void setCheckTime(int i) {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.setCheckTime(i);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "setCheckTime: " + e.toString());
        }
    }

    public void setSlientDownload(boolean z) {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.setSlientDownload(z);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "slientDownload: " + e.toString());
        }
    }

    public void clearData() {
        try {
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.clearData();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "clearData: " + e.toString());
        }
    }

    public boolean getWifiDownload() {
        try {
            Log.d("SettingsApplication", "getWifiDownload: ");
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                return checkStatusCallback.getWifiDownload();
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "getWifiDownload: " + e.toString());
            return false;
        }
    }

    public boolean getSilentUpdate() {
        try {
            Log.d("SettingsApplication", "getSilentDownload: ");
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                return checkStatusCallback.getSilentUpdate();
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "getSilentDownload: " + e.toString());
            return false;
        }
    }

    public int getCheckTime() {
        try {
            Log.d("SettingsApplication", "getCheckTime: ");
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                return checkStatusCallback.getCheckTime();
            }
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "getCheckTime: " + e.toString());
            return 0;
        }
    }

    public void cancelDownload() {
        try {
            Log.d("SettingsApplication", "cancelDownload: ");
            CheckStatusCallback checkStatusCallback = this.checkStatusCallback;
            if (checkStatusCallback != null) {
                checkStatusCallback.cancelDownload();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("SettingsApplication", "cancelDownload: " + e.toString());
        }
    }

    private void updateMainMsg(Message message) {
        Log.d("SettingsApplication", "message.code ===" + message.code);
        if (message.code == 4 && getIsAutoTheme() == 1) {
            if (message.ints[0] == 0) {
                setThemeMode(0);
            } else {
                setThemeMode(1);
            }
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(Message message) {
        updateMainMsg(message);
    }
}
