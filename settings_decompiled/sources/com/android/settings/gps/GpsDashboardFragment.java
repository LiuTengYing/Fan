package com.android.settings.gps;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.gps.adapter.SatelliteListAdapter;
import com.android.settings.gps.data.Gps_Data;
import com.android.settings.gps.data.NmeaParser;
import com.android.settings.gps.data.SatelliteInfo;
import com.android.settings.gps.data.Updatable;
import com.android.settings.gps.view.JSatelliteView;
import com.android.settings.utils.SatelliteProgressUtils;
import com.syu.ipcself.module.main.Main;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes.dex */
public class GpsDashboardFragment extends Fragment {
    private static String TAG = "GpsDashboardFragment";
    private static int UPDATELOCATION = 850;
    private static int UPDATETIME = 851;
    private SatelliteListAdapter listAdapter;
    private LinearLayout mBDlayout;
    private LinearLayout mGalLayout;
    private LinearLayout mGloLayout;
    private TextView mGpsLat;
    private TextView mGpsLon;
    private TextView mGpsSign;
    private TextView mGpsTime;
    private AnimationDrawable mLoading;
    private ImageView mLoadingView;
    private View mRootView;
    private TextView mSatelliteBdNum;
    private TextView mSatelliteGalNum;
    private TextView mSatelliteGlnNum;
    private TextView mSatelliteGpsNum;
    private ImageView mSatelliteImgBd;
    private ImageView mSatelliteImgGps;
    private ImageView mSatelliteImgOther;
    private TextView mSatellitrOtherNum;
    private JSatelliteView mStatellitrView;
    private ImageView mTotalNum1;
    private ImageView mTotalNum2;
    private ImageView mUsefulNum1;
    private ImageView mUsefulNum2;
    private RecyclerView satelliteList;
    private String mCurrData = "";
    ArrayList<SatelliteInfo> GPSStates = new ArrayList<>();
    private boolean bFixSuccess = false;
    private LocationManager mLocationManager = (LocationManager) SettingsApplication.mApplication.getSystemService("location");
    Handler addListener = new Handler() { // from class: com.android.settings.gps.GpsDashboardFragment.1
        @Override // android.os.Handler
        public void dispatchMessage(Message message) {
            if (GpsDashboardFragment.this.mLocationManager.isProviderEnabled("gps")) {
                Log.d(GpsDashboardFragment.TAG, "dispatchMessage: ProviderEnabled");
                Criteria criteria = new Criteria();
                criteria.setAccuracy(1);
                int i = Main.mConf_PlatForm;
                if (i == 2 || i == 7) {
                    criteria.setBearingRequired(false);
                    criteria.setAltitudeRequired(false);
                } else {
                    criteria.setSpeedRequired(true);
                    criteria.setBearingRequired(true);
                    criteria.setAltitudeRequired(true);
                }
                criteria.setCostAllowed(false);
                criteria.setPowerRequirement(1);
                GpsDashboardFragment.this.mLocationManager.getBestProvider(criteria, true);
                GpsDashboardFragment.this.mLocationManager.registerGnssStatusCallback(GpsDashboardFragment.this.StatusListener);
                GpsDashboardFragment.this.mLocationManager.requestLocationUpdates("gps", 500L, 0.0f, GpsDashboardFragment.this.locationListener);
                Log.d(GpsDashboardFragment.TAG, "dispatchMessage: requestLocation");
                return;
            }
            Log.d(GpsDashboardFragment.TAG, "dispatchMessage: ProviderDisabled");
            GpsDashboardFragment.this.addListener.sendEmptyMessageDelayed(0, 500L);
        }
    };
    private GnssStatus.Callback StatusListener = new GnssStatus.Callback() { // from class: com.android.settings.gps.GpsDashboardFragment.2
        @Override // android.location.GnssStatus.Callback
        public void onStarted() {
            super.onStarted();
        }

        @Override // android.location.GnssStatus.Callback
        public void onStopped() {
            super.onStopped();
        }

        @Override // android.location.GnssStatus.Callback
        public void onFirstFix(int i) {
            super.onFirstFix(i);
        }

        @Override // android.location.GnssStatus.Callback
        public void onSatelliteStatusChanged(GnssStatus gnssStatus) {
            int i;
            int i2;
            GnssStatus gnssStatus2 = gnssStatus;
            super.onSatelliteStatusChanged(gnssStatus);
            if (gnssStatus2 == null || gnssStatus.getSatelliteCount() <= 0) {
                return;
            }
            Gps_Data.mGPSSate.clear();
            Gps_Data.mBDSate.clear();
            Gps_Data.mGLNSate.clear();
            Gps_Data.mUNKNOWSate.clear();
            Gps_Data.mGALSate.clear();
            GpsDashboardFragment.this.GPSStates.clear();
            int i3 = 0;
            boolean z = false;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            while (i3 < gnssStatus.getSatelliteCount()) {
                int svid = gnssStatus2.getSvid(i3);
                if (svid > 0) {
                    int cn0DbHz = (int) gnssStatus2.getCn0DbHz(i3);
                    int azimuthDegrees = (int) gnssStatus2.getAzimuthDegrees(i3);
                    int elevationDegrees = (int) gnssStatus2.getElevationDegrees(i3);
                    int locationType = NmeaParser.getLocationType(gnssStatus2.getConstellationType(i3));
                    boolean usedInFix = cn0DbHz <= 0 ? false : gnssStatus2.usedInFix(i3);
                    if (usedInFix) {
                        z = true;
                    }
                    if (usedInFix) {
                        i2 = 1;
                        i = locationType;
                    } else {
                        i = locationType;
                        i2 = 0;
                    }
                    SatelliteInfo satelliteInfo = new SatelliteInfo(svid, elevationDegrees, azimuthDegrees, cn0DbHz, i2, i);
                    if (i == 0) {
                        Gps_Data.mGPSSate.add(satelliteInfo);
                        if (satelliteInfo.getIsUse() == 1) {
                            i4++;
                        }
                    } else if (i == 2) {
                        Gps_Data.mGLNSate.add(satelliteInfo);
                        if (satelliteInfo.getIsUse() == 1) {
                            i5++;
                        }
                    } else if (i == 1) {
                        Gps_Data.mBDSate.add(satelliteInfo);
                        if (satelliteInfo.getIsUse() == 1) {
                            i6++;
                        }
                    } else if (i == 3) {
                        Gps_Data.mGALSate.add(satelliteInfo);
                        if (satelliteInfo.getIsUse() == 1) {
                            i7++;
                        }
                    } else if (i == 4) {
                        Gps_Data.mUNKNOWSate.add(satelliteInfo);
                        if (satelliteInfo.getIsUse() == 1) {
                            i8++;
                        }
                    }
                    GpsDashboardFragment.this.GPSStates.add(satelliteInfo);
                }
                i3++;
                gnssStatus2 = gnssStatus;
            }
            Gps_Data.UPDATABLE_GPS_STATAS.set(new int[]{z ? 1 : 0}, null, null);
            Gps_Data.UPDATABLE_GLN_STATAS.set(new int[]{z ? 1 : 0}, null, null);
            Gps_Data.UPDATABLE_BD_STATAS.set(new int[]{z ? 1 : 0}, null, null);
            Gps_Data.UPDATABLE_GAL_STATAS.set(new int[]{z ? 1 : 0}, null, null);
            Gps_Data.UPDATABLE_UNKNOW_STATAS.set(new int[]{z ? 1 : 0}, null, null);
            Gps_Data.UPDATABLE_GPS_STATAS_CHANGE.set(null, null, null);
            Gps_Data.UPDATABLE_GLN_STATAS_CHANGE.set(null, null, null);
            Gps_Data.UPDATABLE_BD_STATAS_CHANGE.set(null, null, null);
            Gps_Data.UPDATABLE_GAL_STATAS_CHANGE.set(null, null, null);
            Gps_Data.UPDATABLE_UNKNOW_STATAS_CHANGE.set(null, null, null);
            Gps_Data.UPDATABLE_BD_STATAS_CNT.set(new int[]{i6}, null, null);
            Gps_Data.UPDATABLE_GLN_STATAS_CNT.set(new int[]{i5}, null, null);
            Gps_Data.UPDATABLE_GPS_STATAS_CNT.set(new int[]{i4}, null, null);
            Gps_Data.UPDATABLE_GAL_STATAS_CNT.set(new int[]{i7}, null, null);
            Gps_Data.UPDATABLE_UNKNOW_STATAS_CNT.set(new int[]{i8}, null, null);
            GpsDashboardFragment gpsDashboardFragment = GpsDashboardFragment.this;
            gpsDashboardFragment.updateGpsCount(gpsDashboardFragment.GPSStates.size());
            GpsDashboardFragment.this.updateGpsCountMsg();
            Log.d(GpsDashboardFragment.TAG, "onSatelliteStatusChanged: " + z);
        }
    };
    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() { // from class: com.android.settings.gps.GpsDashboardFragment.3
        @Override // android.location.GpsStatus.Listener
        public void onGpsStatusChanged(int i) {
            String str = GpsDashboardFragment.TAG;
            Log.d(str, "onGpsStatusChanged: " + i);
        }
    };
    private LocationListener locationListener = new LocationListener() { // from class: com.android.settings.gps.GpsDashboardFragment.4
        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            GpsDashboardFragment.this.updateView(location);
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String str, int i, Bundle bundle) {
            String str2 = GpsDashboardFragment.TAG;
            Log.d(str2, "onStatusChanged: " + i);
            if (i != 2) {
                GpsDashboardFragment.this.updateView(null);
            }
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String str) {
            GpsDashboardFragment gpsDashboardFragment = GpsDashboardFragment.this;
            gpsDashboardFragment.updateView(gpsDashboardFragment.mLocationManager.getLastKnownLocation(str));
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String str) {
            GpsDashboardFragment.this.updateView(null);
        }
    };
    private Updatable.IUpdater update_Statu = new Updatable.IUpdater() { // from class: com.android.settings.gps.GpsDashboardFragment.5
        @Override // com.android.settings.gps.data.Updatable.IUpdater
        public void onUpdate() {
            int i = Gps_Data.mGpsStatus;
            int i2 = Gps_Data.mBDStatus;
            int i3 = Gps_Data.mGLNStatus;
            int i4 = Gps_Data.mGALStatus;
            int i5 = Gps_Data.mUNKNOWStatus;
            if (i == 1 || i2 == 1 || i3 == 1 || i5 == 1 || i4 == 1) {
                GpsDashboardFragment.this.mGpsSign.setText(GpsDashboardFragment.this.getContext().getResources().getString(R$string.gps_event_success));
                if (GpsDashboardFragment.this.mLoading != null) {
                    GpsDashboardFragment.this.mLoading.stop();
                }
                GpsDashboardFragment.this.mLoadingView.setImageResource(R$drawable.gps_location_success);
                return;
            }
            GpsDashboardFragment.this.mGpsSign.setText(GpsDashboardFragment.this.getContext().getResources().getString(R$string.gps_no_signal));
            GpsDashboardFragment.this.mLoadingView.setImageResource(R$drawable.gps_loading_anim);
            if (GpsDashboardFragment.this.mLoading != null) {
                GpsDashboardFragment.this.mLoading.start();
            }
        }
    };
    public Updatable.IUpdater update_GPSBDSig = new Updatable.IUpdater() { // from class: com.android.settings.gps.GpsDashboardFragment.6
        @Override // com.android.settings.gps.data.Updatable.IUpdater
        public void onUpdate() {
            if (GpsDashboardFragment.this.mStatellitrView != null) {
                if (Gps_Data.mBDSate.size() > 0) {
                    GpsDashboardFragment.this.mStatellitrView.setBdSatellites(Gps_Data.mBDSate);
                }
                if (Gps_Data.mGPSSate.size() > 0) {
                    GpsDashboardFragment.this.mStatellitrView.setGpsSatellites(Gps_Data.mGPSSate);
                }
                if (Gps_Data.mGLNSate.size() > 0) {
                    GpsDashboardFragment.this.mStatellitrView.setGLNSatellites(Gps_Data.mGLNSate);
                }
                if (Gps_Data.mUNKNOWSate.size() > 0) {
                    GpsDashboardFragment.this.mStatellitrView.setUNKNOWSatellites(Gps_Data.mUNKNOWSate);
                }
            }
        }
    };
    private Updatable.IUpdater update_GPSCnt = new Updatable.IUpdater() { // from class: com.android.settings.gps.GpsDashboardFragment.7
        @Override // com.android.settings.gps.data.Updatable.IUpdater
        public void onUpdate() {
            Log.d("fangli", "onUpdate: " + GpsDashboardFragment.this.GPSStates.size());
            GpsDashboardFragment gpsDashboardFragment = GpsDashboardFragment.this;
            gpsDashboardFragment.updateGpsCount(gpsDashboardFragment.GPSStates.size());
        }
    };
    private Updatable.IUpdater update_GPSEnable = new Updatable.IUpdater() { // from class: com.android.settings.gps.GpsDashboardFragment.8
        @Override // com.android.settings.gps.data.Updatable.IUpdater
        public void onUpdate() {
            Iterator<SatelliteInfo> it = GpsDashboardFragment.this.GPSStates.iterator();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (it.hasNext()) {
                SatelliteInfo next = it.next();
                if (next.getIsUse() == 1) {
                    i++;
                }
                int isBD = next.getIsBD();
                if (isBD == 0) {
                    i2++;
                } else if (isBD == 2) {
                    i4++;
                } else if (isBD == 1) {
                    i3++;
                } else if (isBD == 3) {
                    i6++;
                } else if (isBD == 4) {
                    i5++;
                }
            }
            GpsDashboardFragment.this.updateGpsNumState(i2, i3, i4, i5, i6);
            GpsDashboardFragment.this.updateUsefulCount(i);
        }
    };
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.gps.GpsDashboardFragment.9
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == GpsDashboardFragment.UPDATELOCATION) {
                GpsDashboardFragment.this.updateEmpSate();
            } else if (message.what == GpsDashboardFragment.UPDATETIME) {
                GpsDashboardFragment.this.updateView(null);
            }
        }
    };
    private Updatable.IUpdater update_Change = new Updatable.IUpdater() { // from class: com.android.settings.gps.GpsDashboardFragment.10
        @Override // com.android.settings.gps.data.Updatable.IUpdater
        public void onUpdate() {
            try {
                String str = GpsDashboardFragment.TAG;
                Log.d(str, "onUpdate: " + Gps_Data.isFixed());
                if (GpsDashboardFragment.this.GPSStates.isEmpty()) {
                    GpsDashboardFragment.this.updateEmpSate();
                    return;
                }
                int satelliteNum = GpsDashboardFragment.this.getSatelliteNum();
                if (GpsDashboardFragment.this.GPSStates.size() < satelliteNum) {
                    for (int i = 0; i < satelliteNum - GpsDashboardFragment.this.GPSStates.size(); i++) {
                        GpsDashboardFragment.this.GPSStates.add(new SatelliteInfo(0, 0, 0, 0, 0, 0));
                    }
                }
                if (GpsDashboardFragment.this.listAdapter == null) {
                    GpsDashboardFragment gpsDashboardFragment = GpsDashboardFragment.this;
                    gpsDashboardFragment.listAdapter = new SatelliteListAdapter(gpsDashboardFragment.getActivity(), GpsDashboardFragment.this.GPSStates);
                    GpsDashboardFragment.this.satelliteList.setAdapter(GpsDashboardFragment.this.listAdapter);
                } else {
                    GpsDashboardFragment.this.listAdapter.setData(GpsDashboardFragment.this.GPSStates);
                    GpsDashboardFragment.this.listAdapter.notifyDataSetChanged();
                }
                if (GpsDashboardFragment.this.mHandler != null && GpsDashboardFragment.this.mHandler.hasMessages(GpsDashboardFragment.UPDATELOCATION)) {
                    GpsDashboardFragment.this.mHandler.removeMessages(GpsDashboardFragment.UPDATELOCATION);
                }
                if (GpsDashboardFragment.this.mHandler == null || Gps_Data.isFixed()) {
                    return;
                }
                GpsDashboardFragment.this.mHandler.sendEmptyMessageDelayed(GpsDashboardFragment.UPDATELOCATION, 5000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (SettingsApplication.mWidthFix > SettingsApplication.mHeightFix) {
            this.mRootView = layoutInflater.inflate(R$layout.settings_gps_info_layout_new, viewGroup, false);
        } else {
            this.mRootView = layoutInflater.inflate(R$layout.settings_gps_info_layout_new_h, viewGroup, false);
        }
        initViews();
        return this.mRootView;
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        View view = this.mRootView;
        int i = R$id.gps_total_1;
        this.mTotalNum1 = (ImageView) view.findViewById(i);
        View view2 = this.mRootView;
        int i2 = R$id.gps_total_2;
        this.mTotalNum2 = (ImageView) view2.findViewById(i2);
        View view3 = this.mRootView;
        int i3 = R$id.gps_useful_1;
        this.mUsefulNum1 = (ImageView) view3.findViewById(i3);
        View view4 = this.mRootView;
        int i4 = R$id.gps_useful_2;
        this.mUsefulNum2 = (ImageView) view4.findViewById(i4);
        this.mGpsLat = (TextView) this.mRootView.findViewById(R$id.gps_lat);
        this.mGpsLon = (TextView) this.mRootView.findViewById(R$id.gps_lon);
        this.mGpsTime = (TextView) this.mRootView.findViewById(R$id.gps_time);
        this.mGpsSign = (TextView) this.mRootView.findViewById(R$id.gps_location_state);
        this.mLoadingView = (ImageView) this.mRootView.findViewById(R$id.gps_location_loading);
        this.mTotalNum1 = (ImageView) this.mRootView.findViewById(i);
        this.mTotalNum2 = (ImageView) this.mRootView.findViewById(i2);
        this.mUsefulNum1 = (ImageView) this.mRootView.findViewById(i3);
        this.mUsefulNum2 = (ImageView) this.mRootView.findViewById(i4);
        this.mBDlayout = (LinearLayout) this.mRootView.findViewById(R$id.satellite_bd_ly);
        this.mGalLayout = (LinearLayout) this.mRootView.findViewById(R$id.satellite_gal_ly);
        this.mGloLayout = (LinearLayout) this.mRootView.findViewById(R$id.satellite_glo_ly);
        this.mSatelliteGalNum = (TextView) this.mRootView.findViewById(R$id.satellite_gal_num);
        this.mSatelliteGpsNum = (TextView) this.mRootView.findViewById(R$id.satellite_gps_num);
        this.mSatelliteBdNum = (TextView) this.mRootView.findViewById(R$id.satellite_bd_num);
        this.mSatellitrOtherNum = (TextView) this.mRootView.findViewById(R$id.satellite_unknow_num);
        this.mSatelliteImgGps = (ImageView) this.mRootView.findViewById(R$id.set_satellite_img_gps);
        this.mSatelliteImgBd = (ImageView) this.mRootView.findViewById(R$id.set_satellite_img_bd);
        this.mSatelliteImgOther = (ImageView) this.mRootView.findViewById(R$id.set_satellite_img_other);
        this.mSatelliteGlnNum = (TextView) this.mRootView.findViewById(R$id.satellite_gln_num);
        this.satelliteList = (RecyclerView) this.mRootView.findViewById(R$id.gps_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(0);
        this.satelliteList.setLayoutManager(linearLayoutManager);
        AnimationDrawable animationDrawable = (AnimationDrawable) this.mLoadingView.getBackground();
        this.mLoading = animationDrawable;
        animationDrawable.start();
        if (!SystemProperties.getBoolean("ro.client.foreign", false)) {
            this.mBDlayout.setVisibility(0);
            this.mGloLayout.setVisibility(8);
        } else {
            this.mBDlayout.setVisibility(8);
            this.mGloLayout.setVisibility(0);
        }
        initData();
    }

    private void initData() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 12; i++) {
            arrayList.add(new SatelliteInfo(0, 0, 0, 0, 0, 0));
        }
        SatelliteListAdapter satelliteListAdapter = new SatelliteListAdapter(getActivity(), arrayList);
        this.listAdapter = satelliteListAdapter;
        this.satelliteList.setAdapter(satelliteListAdapter);
        updatejingdu("000°00'00");
        updateWeidu("000°00'00");
        TextView textView = this.mGpsTime;
        textView.setText(getContext().getResources().getString(R$string.gps_time) + "0000-00-00 00:00:00");
    }

    private void unregisterListener() {
        this.addListener.removeMessages(0);
        LocationManager locationManager = this.mLocationManager;
        if (locationManager != null) {
            locationManager.unregisterGnssStatusCallback(this.StatusListener);
            this.mLocationManager.removeUpdates(this.locationListener);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateView(Location location) {
        if (location != null) {
            String str = TAG;
            Log.d(str, "updateView: " + location.toString());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            long time = location.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String convertJingWeiDuToStr = convertJingWeiDuToStr(latitude);
            updatejingdu(convertJingWeiDuToStr(longitude));
            updateWeidu(convertJingWeiDuToStr);
            updateTime(simpleDateFormat.format(Long.valueOf(time)));
        } else {
            Log.d(TAG, "updateView:  null");
            updatejingdu("000°00'00");
            updateWeidu("000°00'00");
            updateTime("0000-00-00 00:00:00");
        }
        Handler handler = this.mHandler;
        if (handler != null && handler.hasMessages(UPDATETIME)) {
            this.mHandler.removeMessages(UPDATETIME);
        }
        this.mHandler.sendEmptyMessageDelayed(UPDATETIME, 5000L);
    }

    public String convertJingWeiDuToStr(double d) {
        int floor = (int) Math.floor(Math.abs(d));
        double d2 = getdPoint(Math.abs(d)) * 60.0d;
        int floor2 = (int) Math.floor(d2);
        int floor3 = (int) Math.floor(getdPoint(d2) * 60.0d);
        StringBuilder sb = new StringBuilder();
        sb.append(d < 0.0d ? "-" : "");
        sb.append(String.format(Locale.US, "%03d°%02d′%02d″", Integer.valueOf(floor), Integer.valueOf(floor2), Integer.valueOf(floor3)));
        return sb.toString();
    }

    public double getdPoint(double d) {
        return new BigDecimal(Double.toString(d)).subtract(new BigDecimal(Integer.toString((int) d))).floatValue();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        Toolbar toolbar;
        this.addListener.sendEmptyMessage(0);
        Gps_Data.UPDATABLE_GPS_STATAS.addUpdater(this.update_Statu, true);
        Gps_Data.UPDATABLE_BD_STATAS.addUpdater(this.update_Statu, true);
        Gps_Data.UPDATABLE_GLN_STATAS.addUpdater(this.update_Statu, true);
        Gps_Data.UPDATABLE_GAL_STATAS.addUpdater(this.update_Statu, true);
        Gps_Data.UPDATABLE_UNKNOW_STATAS.addUpdater(this.update_Statu, true);
        Gps_Data.UPDATABLE_GPS_STATAS_CHANGE.addUpdater(this.update_GPSBDSig, true);
        Gps_Data.UPDATABLE_BD_STATAS_CHANGE.addUpdater(this.update_GPSBDSig, true);
        Gps_Data.UPDATABLE_GLN_STATAS_CHANGE.addUpdater(this.update_GPSBDSig, true);
        Gps_Data.UPDATABLE_GAL_STATAS_CHANGE.addUpdater(this.update_GPSBDSig, true);
        Gps_Data.UPDATABLE_UNKNOW_STATAS_CHANGE.addUpdater(this.update_GPSBDSig, true);
        Gps_Data.UPDATABLE_BD_STATAS.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_BD_STATAS_CHANGE.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_GLN_STATAS.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_GLN_STATAS_CHANGE.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_GPS_STATAS.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_GPS_STATAS_CHANGE.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_GAL_STATAS.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_GAL_STATAS_CHANGE.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_UNKNOW_STATAS.addUpdater(this.update_Change, true);
        Gps_Data.UPDATABLE_UNKNOW_STATAS_CHANGE.addUpdater(this.update_Change, true);
        FragmentActivity activity = getActivity();
        if ((activity instanceof SettingsBaseActivity) && (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) != null) {
            toolbar.setTitle(getResources().getString(R$string.top_level_gps_message));
        }
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        Gps_Data.UPDATABLE_GPS_STATAS.removeUpdater(this.update_Statu);
        Gps_Data.UPDATABLE_BD_STATAS.removeUpdater(this.update_Statu);
        Gps_Data.UPDATABLE_GLN_STATAS.removeUpdater(this.update_Statu);
        Gps_Data.UPDATABLE_GAL_STATAS.removeUpdater(this.update_Statu);
        Gps_Data.UPDATABLE_UNKNOW_STATAS.removeUpdater(this.update_Statu);
        updateGpsCount(0);
        updateUsefulCount(0);
        Gps_Data.UPDATABLE_GPS_STATAS_CHANGE.removeUpdater(this.update_GPSBDSig);
        Gps_Data.UPDATABLE_BD_STATAS_CHANGE.removeUpdater(this.update_GPSBDSig);
        Gps_Data.UPDATABLE_GLN_STATAS_CHANGE.removeUpdater(this.update_GPSBDSig);
        Gps_Data.UPDATABLE_GAL_STATAS_CHANGE.removeUpdater(this.update_GPSBDSig);
        Gps_Data.UPDATABLE_UNKNOW_STATAS_CHANGE.removeUpdater(this.update_GPSBDSig);
        Gps_Data.UPDATABLE_BD_STATAS.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_BD_STATAS_CHANGE.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_GLN_STATAS.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_GLN_STATAS_CHANGE.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_GPS_STATAS.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_GPS_STATAS_CHANGE.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_GAL_STATAS.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_GAL_STATAS_CHANGE.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_UNKNOW_STATAS.removeUpdater(this.update_Change);
        Gps_Data.UPDATABLE_UNKNOW_STATAS_CHANGE.removeUpdater(this.update_Change);
        updateView(null);
        unregisterListener();
        this.mHandler.removeMessages(UPDATELOCATION);
        this.mHandler.removeMessages(UPDATETIME);
        super.onPause();
    }

    private void updatejingdu(String str) {
        TextView textView = this.mGpsLat;
        if (textView != null) {
            textView.setText(getContext().getResources().getString(R$string.gps_longitude) + str);
        }
    }

    private void updateWeidu(String str) {
        TextView textView = this.mGpsLon;
        if (textView != null) {
            textView.setText(getContext().getResources().getString(R$string.gps_latitude) + str);
        }
    }

    private void updateTime(String str) {
        if (this.mGpsTime != null) {
            String language = Locale.getDefault().getLanguage();
            if (language != null && !language.contains("zh") && !language.contains("ko")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(simpleDateFormat.parse(str));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            TextView textView = this.mGpsTime;
            textView.setText(getContext().getResources().getString(R$string.gps_time) + str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGpsCount(int i) {
        if (this.mTotalNum1 == null || this.mTotalNum2 == null) {
            return;
        }
        String valueOf = String.valueOf(i);
        if (valueOf.length() > 1) {
            int intValue = Integer.valueOf(valueOf.substring(0, 1)).intValue();
            int intValue2 = Integer.valueOf(valueOf.substring(1, 2)).intValue();
            this.mTotalNum1.setVisibility(0);
            this.mTotalNum1.setImageDrawable(getNumber(intValue));
            this.mTotalNum2.setImageDrawable(getNumber(intValue2));
            return;
        }
        this.mTotalNum1.setVisibility(4);
        this.mTotalNum2.setImageDrawable(getNumber(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUsefulCount(int i) {
        if (this.mUsefulNum1 == null || this.mUsefulNum2 == null) {
            return;
        }
        String valueOf = String.valueOf(i);
        if (valueOf.length() > 1) {
            int intValue = Integer.valueOf(valueOf.substring(0, 1)).intValue();
            int intValue2 = Integer.valueOf(valueOf.substring(1, 2)).intValue();
            this.mUsefulNum1.setVisibility(0);
            this.mUsefulNum1.setImageDrawable(getNumber(intValue));
            this.mUsefulNum2.setImageDrawable(getNumber(intValue2));
            return;
        }
        this.mUsefulNum1.setVisibility(4);
        this.mUsefulNum2.setImageDrawable(getNumber(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGpsCountMsg() {
        Iterator<SatelliteInfo> it = this.GPSStates.iterator();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (it.hasNext()) {
            SatelliteInfo next = it.next();
            if (next.getIsUse() == 1) {
                i++;
            }
            int isBD = next.getIsBD();
            if (isBD == 0) {
                i2++;
            } else if (isBD == 2) {
                i4++;
            } else if (isBD == 1) {
                i3++;
            } else if (isBD == 3) {
                i6++;
            } else if (isBD == 4) {
                i5++;
            }
        }
        updateGpsNumState(i2, i3, i4, i5, i6);
        updateUsefulCount(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGpsNumState(int i, int i2, int i3, int i4, int i5) {
        if (!SystemProperties.getBoolean("ro.client.foreign", false)) {
            this.mBDlayout.setVisibility(0);
            this.mGloLayout.setVisibility(8);
        } else {
            this.mBDlayout.setVisibility(8);
            this.mGloLayout.setVisibility(0);
        }
        TextView textView = this.mSatelliteGpsNum;
        if (textView != null) {
            textView.setText("  " + i);
        }
        TextView textView2 = this.mSatellitrOtherNum;
        if (textView2 != null) {
            textView2.setText("  " + i4);
        }
        TextView textView3 = this.mSatelliteBdNum;
        if (textView3 != null) {
            textView3.setText("  " + i2);
        }
        TextView textView4 = this.mSatelliteGlnNum;
        if (textView4 != null) {
            textView4.setText("  " + i3);
        }
        TextView textView5 = this.mSatelliteGalNum;
        if (textView5 != null) {
            textView5.setText("  " + i5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmpSate() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 12; i++) {
            arrayList.add(new SatelliteInfo(0, 0, 0, 0, 0, 0));
        }
        SatelliteListAdapter satelliteListAdapter = this.listAdapter;
        if (satelliteListAdapter == null) {
            SatelliteListAdapter satelliteListAdapter2 = new SatelliteListAdapter(getActivity(), arrayList);
            this.listAdapter = satelliteListAdapter2;
            this.satelliteList.setAdapter(satelliteListAdapter2);
            return;
        }
        satelliteListAdapter.setData(arrayList);
        this.listAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSatelliteNum() {
        int progressWidth = (int) (((SettingsApplication.mWidthFix * 0.6d) - 90.0d) / (SatelliteProgressUtils.getProgressWidth() + 50));
        String str = TAG;
        Log.d(str, "getSatelliteNum: " + progressWidth);
        return progressWidth - 1;
    }

    private Drawable getNumber(int i) {
        int i2 = R$drawable.gps_num_0;
        switch (i) {
            case 1:
                i2 = R$drawable.gps_num_1;
                break;
            case 2:
                i2 = R$drawable.gps_num_2;
                break;
            case 3:
                i2 = R$drawable.gps_num_3;
                break;
            case 4:
                i2 = R$drawable.gps_num_4;
                break;
            case 5:
                i2 = R$drawable.gps_num_5;
                break;
            case 6:
                i2 = R$drawable.gps_num_6;
                break;
            case 7:
                i2 = R$drawable.gps_num_7;
                break;
            case 8:
                i2 = R$drawable.gps_num_8;
                break;
            case 9:
                i2 = R$drawable.gps_num_9;
                break;
        }
        return getContext().getResources().getDrawable(i2);
    }
}
