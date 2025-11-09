package com.android.settings.connecteddevice.usb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.common.data.AppData;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.view.TextSeekBarCenter;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import qin.x_core.client.java.XCoreSdk;
/* loaded from: classes.dex */
public class UsbVideoOutputFragment extends DashboardFragment implements XCoreSdk.OnInitListener {
    private static String TAG = "UsbVideoOutputFragment";
    public static boolean hasFilter = false;
    public static String mPkgName;
    public static Resources mResources;
    TextSeekBarCenter Bar_Contrast;
    TextSeekBarCenter Bar_Deviation_H;
    TextSeekBarCenter Bar_Deviation_V;
    TextSeekBarCenter Bar_Hue;
    TextSeekBarCenter Bar_Light;
    TextSeekBarCenter Bar_Saturation;
    TextSeekBarCenter Bar_Stretch_H;
    TextSeekBarCenter Bar_Stretch_V;
    private UsbOutputAdapter appAdapter;
    private GridView gridView;
    private View mContentView;
    private TextView mDeviationH;
    private TextView mDeviationV;
    private ListView mResolutionList;
    private RelativeLayout mResolutionLy;
    private TextView mResolutionTv;
    private View mRootView;
    private TextView mStretchH;
    private TextView mStretchV;
    private PopupWindow popupWindow;
    private static Uri uri = Uri.parse("content://com.syu.settingsProvider/projectionlist");
    private static Uri uri_filter = Uri.parse("content://com.syu.settingsProvider/filterlist");
    private static boolean isLocalAPKVisible = false;
    private static JSONObject Data = new JSONObject();
    public static String refresh_projections_action = "com.fyt.action.refreshprojections";
    private static int PopChoiceItem_CheckMark = 255;
    private static int PopChoiceItem_Name = 256;
    private XCoreSdk remote = null;
    int middle = 50;
    String[] hide_For_Projections = {"com.android.messaging", "com.google.android.googlequicksearchbox", "com.google.android.apps.googleassistant", "com.android.settings"};
    public List<SparseArray<String>> mListUSBOutputSize = new ArrayList();
    public List<SparseArray<String>> mListUSBOutputSizeCheckMap = new ArrayList();
    public int mCurUSBOutputSize = 0;
    public int mCurUSBOutputSizeValue = 0;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onCheckChanged(boolean z, int i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R$layout.common_usb_video_output_layout, viewGroup, false);
        initViews();
        initListener();
        initData();
        return this.mRootView;
    }

    private void initViews() {
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        this.Bar_Light = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_light);
        this.Bar_Contrast = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_contrast);
        this.Bar_Saturation = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_saturation);
        this.Bar_Hue = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_hue);
        this.Bar_Deviation_H = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_deviation_h);
        this.Bar_Deviation_V = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_deviation_v);
        this.Bar_Stretch_H = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_stretch_h);
        this.Bar_Stretch_V = (TextSeekBarCenter) this.mRootView.findViewById(R$id.usb_seekbar_stretch_v);
        this.gridView = (GridView) this.mRootView.findViewById(R$id.usb_output_app_grid);
        this.mResolutionLy = (RelativeLayout) this.mRootView.findViewById(R$id.output_resolution_layout);
        this.mResolutionTv = (TextView) this.mRootView.findViewById(R$id.output_resolution);
        this.mDeviationV = (TextView) this.mRootView.findViewById(R$id.usb_seekbar_deviation_v_value);
        this.mDeviationH = (TextView) this.mRootView.findViewById(R$id.usb_seekbar_deviation_h_value);
        this.mStretchH = (TextView) this.mRootView.findViewById(R$id.usb_seekbar_stretch_h_value);
        this.mStretchV = (TextView) this.mRootView.findViewById(R$id.usb_seekbar_stretch_v_value);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R$string.fix_connectivity);
        add.setIcon(R$drawable.ic_repair_24dp);
        add.setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            resetSettings();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void resetSettings() {
        XCoreSdk xCoreSdk = this.remote;
        if (xCoreSdk == null || !xCoreSdk.isBound()) {
            return;
        }
        updateVideo();
    }

    private void initListener() {
        this.Bar_Light.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.1
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                String str = UsbVideoOutputFragment.TAG;
                Log.d(str, "onProgressChanged: " + i);
                if (z) {
                    UsbVideoOutputFragment.this.remote.commandValue(0, 3, i - 50);
                }
            }
        });
        this.Bar_Contrast.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.2
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                UsbVideoOutputFragment.this.remote.commandValue(0, 4, i - 50);
            }
        });
        this.Bar_Saturation.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.3
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                UsbVideoOutputFragment.this.remote.commandValue(0, 5, i - 50);
            }
        });
        this.Bar_Hue.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.4
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                UsbVideoOutputFragment.this.remote.commandValue(0, 6, i - 50);
            }
        });
        this.Bar_Deviation_H.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.5
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                if (z) {
                    UsbVideoOutputFragment.this.remote.commandValue(0, 8, i - 50);
                }
                TextView textView = UsbVideoOutputFragment.this.mDeviationH;
                StringBuilder sb = new StringBuilder();
                sb.append(i - 50);
                sb.append("");
                textView.setText(sb.toString());
            }
        });
        this.Bar_Deviation_V.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.6
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                if (z) {
                    UsbVideoOutputFragment.this.remote.commandValue(0, 9, i - 50);
                }
                TextView textView = UsbVideoOutputFragment.this.mDeviationV;
                StringBuilder sb = new StringBuilder();
                sb.append(i - 50);
                sb.append("");
                textView.setText(sb.toString());
            }
        });
        this.Bar_Stretch_H.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.7
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                if (z) {
                    UsbVideoOutputFragment.this.remote.commandValue(0, 10, i - 50);
                }
                TextView textView = UsbVideoOutputFragment.this.mStretchH;
                StringBuilder sb = new StringBuilder();
                sb.append(i - 50);
                sb.append("");
                textView.setText(sb.toString());
            }
        });
        this.Bar_Stretch_V.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.8
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                if (z) {
                    UsbVideoOutputFragment.this.remote.commandValue(0, 11, i - 50);
                }
                TextView textView = UsbVideoOutputFragment.this.mStretchV;
                StringBuilder sb = new StringBuilder();
                sb.append(i - 50);
                sb.append("");
                textView.setText(sb.toString());
            }
        });
        this.mResolutionLy.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d("fangli", "mScreenSaverTimeLy onClick: ");
                if (UsbVideoOutputFragment.this.popupWindow != null && UsbVideoOutputFragment.this.popupWindow.isShowing()) {
                    UsbVideoOutputFragment.this.popupWindow.dismiss();
                } else {
                    UsbVideoOutputFragment.this.showResolutionPop();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showResolutionPop() {
        this.mContentView = LayoutInflater.from(getContext()).inflate(R$layout.usb_resoltion_select_dialog, (ViewGroup) null);
        if (this.popupWindow == null) {
            this.popupWindow = new PopupWindow(this.mContentView, 240, 320, false);
        }
        this.popupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.popupWindow.setFocusable(true);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.update();
        this.popupWindow.showAsDropDown(this.mResolutionTv, -90, 30);
        initPopClickListener();
    }

    private void initPopClickListener() {
        View view = this.mContentView;
        if (view != null) {
            this.mResolutionList = (ListView) view.findViewById(R$id.resoletion_list);
            ResolutionAdapter resolutionAdapter = new ResolutionAdapter(getContext(), this.mListUSBOutputSize);
            this.mResolutionList.setAdapter((ListAdapter) resolutionAdapter);
            resolutionAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.10
                @Override // com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.OnItemClickListener
                public void onCheckChanged(boolean z, int i) {
                    if (UsbVideoOutputFragment.this.mResolutionTv != null) {
                        UsbVideoOutputFragment.this.mResolutionTv.setText(UsbVideoOutputFragment.this.mListUSBOutputSize.get(i).get(UsbVideoOutputFragment.PopChoiceItem_Name));
                    }
                    if (UsbVideoOutputFragment.this.popupWindow != null && UsbVideoOutputFragment.this.popupWindow.isShowing()) {
                        UsbVideoOutputFragment.this.popupWindow.dismiss();
                    }
                    int parseInt = Integer.parseInt(UsbVideoOutputFragment.this.mListUSBOutputSizeCheckMap.get(i).get(UsbVideoOutputFragment.PopChoiceItem_CheckMark, "-1"));
                    String str = UsbVideoOutputFragment.TAG;
                    Log.d(str, "onCheckChanged: " + parseInt);
                    UsbVideoOutputFragment.this.remote.commandValue(0, 1, parseInt);
                }
            });
        }
    }

    private void initData() {
        EventBus.getDefault().register(this);
        XCoreSdk xCoreSdk = new XCoreSdk(getContext(), "com.syu.cs", "com.syu.cs", "com.syu.cs", this);
        this.remote = xCoreSdk;
        xCoreSdk.initSdk();
        readData();
        List<AppData> allInstalledApps = getAllInstalledApps(getContext().getPackageManager());
        int round = (Math.round(allInstalledApps.size() / 2.0f) * 70) + 20;
        int i = SettingsApplication.mWidthFix;
        if (i == 2000 || (i == 1920 && SettingsApplication.mHeightFix == 1200)) {
            round = (int) (round * 1.5d);
        }
        String str = TAG;
        Log.d(str, "initData: " + round + allInstalledApps.size());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, round);
        layoutParams.setMargins(0, 20, 0, 0);
        this.gridView.setLayoutParams(layoutParams);
        UsbOutputAdapter usbOutputAdapter = new UsbOutputAdapter(getContext(), allInstalledApps);
        this.appAdapter = usbOutputAdapter;
        this.gridView.setAdapter((ListAdapter) usbOutputAdapter);
        this.appAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.11
            @Override // com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.OnItemClickListener
            public void onCheckChanged(boolean z, int i2) {
                String str2 = UsbVideoOutputFragment.TAG;
                Log.d(str2, "onCheckChanged: " + z + i2);
                AppData appData = (AppData) UsbVideoOutputFragment.this.appAdapter.getItem(i2);
                String str3 = appData.getmName();
                if (z) {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("packageName", appData.getmName());
                        jSONObject.put("className", appData.getClassName());
                        jSONObject.put("labelName", appData.getLabelName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        UsbVideoOutputFragment.Data.put(str3, jSONObject);
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                } else {
                    UsbVideoOutputFragment.Data.remove(str3);
                }
                UsbVideoOutputFragment.this.update();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        getContext().getContentResolver().update(uri, new ContentValues(), Data.toString(), null);
        Intent intent = new Intent(refresh_projections_action);
        intent.addFlags(16777216);
        getContext().sendBroadcast(intent);
    }

    public void addChoice2List(String str, List<SparseArray<String>> list) {
        if (list != null) {
            SparseArray<String> sparseArray = new SparseArray<>();
            sparseArray.put(PopChoiceItem_Name, getString(str));
            list.add(sparseArray);
        }
    }

    public String getString(String str) {
        int identifier = mResources.getIdentifier(str, "string", mPkgName);
        return identifier == 0 ? str : myString(identifier);
    }

    public static String myString(int i) {
        Resources resources = mResources;
        if (resources != null) {
            try {
                return resources.getString(i);
            } catch (Exception unused) {
                return "";
            }
        }
        return "";
    }

    public void addChoice2ListCkeckMark(int i, List<SparseArray<String>> list) {
        if (list != null) {
            SparseArray<String> sparseArray = new SparseArray<>();
            int i2 = PopChoiceItem_CheckMark;
            sparseArray.put(i2, i + "");
            list.add(sparseArray);
        }
    }

    private void updateVideo() {
        int i;
        int i2;
        int i3;
        int i4;
        String str = SystemProperties.get("ro.syu.castscreen.offset_zoom", "");
        if (str == null || "".equals(str)) {
            i = 0;
            i2 = 0;
            i3 = 0;
            i4 = 0;
        } else {
            try {
                JSONArray jSONArray = new JSONArray(str);
                i = jSONArray.getInt(0);
                try {
                    i3 = jSONArray.getInt(1);
                    try {
                        i4 = jSONArray.getInt(2);
                    } catch (Exception e) {
                        e = e;
                        i4 = 0;
                    }
                } catch (Exception e2) {
                    e = e2;
                    i3 = 0;
                    i4 = i3;
                    e.printStackTrace();
                    i2 = 0;
                    this.remote.commandValue(0, 8, i);
                    this.remote.commandValue(0, 9, i3);
                    this.remote.commandValue(0, 10, i4);
                    this.remote.commandValue(0, 11, i2);
                    this.remote.commandValue(0, 3, this.middle);
                    this.remote.commandValue(0, 4, this.middle);
                    this.remote.commandValue(0, 5, this.middle);
                    this.remote.commandValue(0, 6, this.middle);
                    this.Bar_Light.setProgress(this.middle);
                    this.Bar_Contrast.setProgress(this.middle);
                    this.Bar_Saturation.setProgress(this.middle);
                    this.Bar_Hue.setProgress(this.middle);
                    this.Bar_Deviation_H.setProgress(i + 50);
                    this.Bar_Deviation_V.setProgress(i3 + 50);
                    this.Bar_Stretch_H.setProgress(i4 + 50);
                    this.Bar_Stretch_V.setProgress(i2 + 50);
                }
                try {
                    i2 = jSONArray.getInt(3);
                } catch (Exception e3) {
                    e = e3;
                    e.printStackTrace();
                    i2 = 0;
                    this.remote.commandValue(0, 8, i);
                    this.remote.commandValue(0, 9, i3);
                    this.remote.commandValue(0, 10, i4);
                    this.remote.commandValue(0, 11, i2);
                    this.remote.commandValue(0, 3, this.middle);
                    this.remote.commandValue(0, 4, this.middle);
                    this.remote.commandValue(0, 5, this.middle);
                    this.remote.commandValue(0, 6, this.middle);
                    this.Bar_Light.setProgress(this.middle);
                    this.Bar_Contrast.setProgress(this.middle);
                    this.Bar_Saturation.setProgress(this.middle);
                    this.Bar_Hue.setProgress(this.middle);
                    this.Bar_Deviation_H.setProgress(i + 50);
                    this.Bar_Deviation_V.setProgress(i3 + 50);
                    this.Bar_Stretch_H.setProgress(i4 + 50);
                    this.Bar_Stretch_V.setProgress(i2 + 50);
                }
            } catch (Exception e4) {
                e = e4;
                i = 0;
                i3 = 0;
            }
        }
        this.remote.commandValue(0, 8, i);
        this.remote.commandValue(0, 9, i3);
        this.remote.commandValue(0, 10, i4);
        this.remote.commandValue(0, 11, i2);
        this.remote.commandValue(0, 3, this.middle);
        this.remote.commandValue(0, 4, this.middle);
        this.remote.commandValue(0, 5, this.middle);
        this.remote.commandValue(0, 6, this.middle);
        this.Bar_Light.setProgress(this.middle);
        this.Bar_Contrast.setProgress(this.middle);
        this.Bar_Saturation.setProgress(this.middle);
        this.Bar_Hue.setProgress(this.middle);
        this.Bar_Deviation_H.setProgress(i + 50);
        this.Bar_Deviation_V.setProgress(i3 + 50);
        this.Bar_Stretch_H.setProgress(i4 + 50);
        this.Bar_Stretch_V.setProgress(i2 + 50);
    }

    @Override // qin.x_core.client.java.XCoreSdk.OnInitListener
    public void onInitFinish(boolean z) {
        this.remote.register(0, 1, 0, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    }

    @Override // qin.x_core.client.java.XCoreSdk.OnInitListener
    public void onError(int i) {
        String str = TAG;
        Log.e(str, "==================>>>  onError errorCode: " + i);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        if (this.remote.isBound()) {
            this.remote.deinitSdk();
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataChanged(Bundle bundle) {
        int cmdidOf = this.remote.cmdidOf(bundle);
        int intOf = this.remote.intOf(bundle, -1);
        String str = TAG;
        Log.d(str, "onDataChanged: " + cmdidOf + "   " + intOf);
        int i = 0;
        switch (cmdidOf) {
            case 1:
                this.mCurUSBOutputSizeValue = intOf;
                while (i < this.mListUSBOutputSizeCheckMap.size()) {
                    if (this.mListUSBOutputSizeCheckMap.get(i).get(PopChoiceItem_CheckMark, "-1").equals(this.mCurUSBOutputSizeValue + "")) {
                        this.mCurUSBOutputSize = i;
                    }
                    i++;
                }
                if (this.mResolutionTv == null || this.mListUSBOutputSize.size() <= 0) {
                    return;
                }
                this.mResolutionTv.setText(this.mListUSBOutputSize.get(this.mCurUSBOutputSize).get(PopChoiceItem_Name));
                return;
            case 2:
            default:
                return;
            case 3:
                this.Bar_Light.setProgress(intOf);
                return;
            case 4:
                this.Bar_Contrast.setProgress(intOf);
                return;
            case 5:
                this.Bar_Saturation.setProgress(intOf);
                return;
            case 6:
                this.Bar_Hue.setProgress(intOf);
                return;
            case 7:
                this.mListUSBOutputSize.clear();
                this.mListUSBOutputSizeCheckMap.clear();
                if (intOf == 0) {
                    addChoice2List("set_usb_output_size_720X480", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(0, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_720X576", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(1, this.mListUSBOutputSizeCheckMap);
                } else if (intOf == 1) {
                    addChoice2List("set_usb_output_size_720X480", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(0, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_720X576", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(1, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_800X600_800X480", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(18, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_800X600", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(2, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_1280X720", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(3, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_1920X1080", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(4, this.mListUSBOutputSizeCheckMap);
                } else if (intOf == 2) {
                    addChoice2List("set_usb_output_size_720X480", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(0, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_720X576", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(1, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_1280X720", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(3, this.mListUSBOutputSizeCheckMap);
                    addChoice2List("set_usb_output_size_1920X1080", this.mListUSBOutputSize);
                    addChoice2ListCkeckMark(4, this.mListUSBOutputSizeCheckMap);
                }
                PopupWindow popupWindow = this.popupWindow;
                if (popupWindow != null && popupWindow.isShowing()) {
                    this.popupWindow.dismiss();
                    showResolutionPop();
                }
                while (i < this.mListUSBOutputSizeCheckMap.size()) {
                    if (this.mListUSBOutputSizeCheckMap.get(i).get(PopChoiceItem_CheckMark, "-1").equals(this.mCurUSBOutputSizeValue + "")) {
                        this.mCurUSBOutputSize = i;
                    }
                    i++;
                }
                TextView textView = this.mResolutionTv;
                if (textView != null) {
                    textView.setText(this.mListUSBOutputSize.get(this.mCurUSBOutputSize).get(PopChoiceItem_Name));
                    return;
                }
                return;
            case 8:
                this.Bar_Deviation_H.setProgress(this.middle + intOf);
                TextView textView2 = this.mDeviationH;
                textView2.setText(intOf + "");
                return;
            case 9:
                this.Bar_Deviation_V.setProgress(this.middle + intOf);
                TextView textView3 = this.mDeviationV;
                textView3.setText(intOf + "");
                return;
            case 10:
                this.Bar_Stretch_H.setProgress(this.middle + intOf);
                TextView textView4 = this.mStretchH;
                textView4.setText(intOf + "");
                return;
            case 11:
                this.Bar_Stretch_V.setProgress(this.middle + intOf);
                TextView textView5 = this.mStretchV;
                textView5.setText(intOf + "");
                return;
        }
    }

    public List<AppData> getAllInstalledApps(PackageManager packageManager) {
        boolean z;
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        ArrayList arrayList = new ArrayList();
        arrayList.clear();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null) {
            return arrayList;
        }
        Cursor query = getContext().getContentResolver().query(uri_filter, null, null, null, null);
        ArrayList arrayList2 = new ArrayList();
        if (query != null) {
            while (query.moveToNext()) {
                arrayList2.add(query.getString(0));
            }
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            AppData appData = new AppData();
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String str = activityInfo.packageName;
            String str2 = activityInfo.name;
            String charSequence = activityInfo.loadLabel(packageManager).toString();
            Log.d(TAG, "getAllInstalledApps: " + charSequence);
            String str3 = resolveInfo.activityInfo.packageName;
            if (!arrayList2.contains(str) && (!"BT-2".equals(charSequence) || ("com.android.launcher6.BT2Activity".equals(str2) && "com.android.launcher6".equals(str)))) {
                if ((!str.startsWith("com.syu.") && !str.startsWith("com.fyt.") && !str.startsWith("com.android.calculator")) || isLocalAPKVisible) {
                    int i = 0;
                    while (true) {
                        String[] strArr = this.hide_For_Projections;
                        if (i < strArr.length) {
                            if (str.equals(strArr[i])) {
                                break;
                            }
                            i++;
                        } else {
                            appData.setmName(str);
                            appData.setClassName(str2);
                            appData.setLabelName(charSequence);
                            if (Data != null) {
                                z = false;
                                for (int i2 = 0; i2 < Data.length(); i2++) {
                                    try {
                                        if (Data.has(str)) {
                                            JSONObject jSONObject = (JSONObject) Data.get(str);
                                            if (str.equals(jSONObject.getString("packageName")) && str2.equals(jSONObject.getString("className"))) {
                                                z = true;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                z = false;
                            }
                            appData.setAutoStart(z);
                            appData.setmIcon(resolveInfo.activityInfo.loadIcon(packageManager));
                            arrayList.add(appData);
                        }
                    }
                }
            }
        }
        if (hasFilter) {
            arrayList.add(null);
        }
        return arrayList;
    }

    public void readData() {
        JSONObject jSONObject = new JSONObject();
        Cursor query = getContext().getContentResolver().query(uri, null, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                String string = query.getString(0);
                String string2 = query.getString(1);
                String string3 = query.getString(2);
                JSONObject jSONObject2 = new JSONObject();
                try {
                    jSONObject2.put("packageName", string);
                    jSONObject2.put("className", string2);
                    jSONObject2.put("labelName", string3);
                    jSONObject.put(string, jSONObject2);
                } catch (JSONException e) {
                    String str = TAG;
                    Log.d(str, "readData: " + e);
                    e.printStackTrace();
                }
            }
        }
        Data = jSONObject;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class UsbOutputAdapter extends BaseAdapter {
        private Context mContext;
        private List<AppData> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public UsbOutputAdapter(Context context, List<AppData> list) {
            this.mContext = context;
            this.mData = list;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mData.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mData.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View view2;
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_cast_screen, viewGroup, false);
                holder.mIcon = (ImageView) view2.findViewById(R$id.app_icon);
                holder.mTitle = (TextView) view2.findViewById(R$id.app_name);
                holder.mAutoStart = (Switch) view2.findViewById(R$id.app_auto_start_switch);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            String str = UsbVideoOutputFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i).toString());
            holder.mTitle.setText(this.mData.get(i).getLabelName());
            holder.mIcon.setImageDrawable(this.mData.get(i).getmIcon());
            holder.mAutoStart.setChecked(this.mData.get(i).isAutoStart());
            holder.mAutoStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.UsbOutputAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    if (UsbOutputAdapter.this.mListener != null) {
                        UsbOutputAdapter.this.mListener.onCheckChanged(z, i);
                    }
                }
            });
            return view2;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mListener = onItemClickListener;
        }

        /* loaded from: classes.dex */
        class Holder {
            Switch mAutoStart;
            ImageView mIcon;
            TextView mTitle;

            Holder() {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ResolutionAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public ResolutionAdapter(Context context, List<SparseArray<String>> list) {
            this.mContext = context;
            this.mData = list;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mData.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mData.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View view2;
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_usb_resolution, viewGroup, false);
                holder.sizeLy = (RelativeLayout) view2.findViewById(R$id.resolution_ly);
                holder.size = (TextView) view2.findViewById(R$id.resolution_tv);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            holder.size.setText(this.mData.get(i).get(UsbVideoOutputFragment.PopChoiceItem_Name));
            holder.sizeLy.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbVideoOutputFragment.ResolutionAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (ResolutionAdapter.this.mListener != null) {
                        ResolutionAdapter.this.mListener.onCheckChanged(true, i);
                    }
                }
            });
            String str = UsbVideoOutputFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i).toString());
            return view2;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mListener = onItemClickListener;
        }

        /* loaded from: classes.dex */
        class Holder {
            TextView size;
            RelativeLayout sizeLy;

            Holder() {
            }
        }
    }
}
