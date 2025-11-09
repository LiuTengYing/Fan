package com.android.settings.display.syudisplay;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.display.syudisplay.TimePicker;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.widget.LayoutPreference;
import com.syu.util.MySharePreference;
import java.text.SimpleDateFormat;
/* loaded from: classes.dex */
public class TimePickerContrlooer extends BasePreferenceController {
    private static String TAG = "TimePickerContrlooer";
    private static int UPDATESUNDOWN = 977;
    private static int UPDATESUNRISE = 978;
    private static int updateUI = 998;
    private String KEY;
    private CheckBox autoCheckBox;
    private TextView backState;
    private Handler handler;
    private RelativeLayout layout;
    private Context mContext;
    private LayoutPreference mPreference;
    private LinearLayout mSunTimeLy;
    private TextView mSundownTime;
    private TextView mSunriseTime;
    private RelativeLayout mTimeAutoLayout;
    private LinearLayout mTimeAutoSeek;
    private TextView mTimeEnd;
    private LinearLayout mTimePicker;
    private RelativeLayout mTimePickerLayout;
    private TextView mTimeStart;
    SimpleDateFormat sdf;
    private ColorArcProgressBar sunDownPro;
    private ColorArcProgressBar sunRisePro;
    private TimePicker sundownPicker;
    private TimePicker sunrisePicker;
    private int timePickerState;
    private CheckBox userCheckBox;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public TimePickerContrlooer(Context context, String str) {
        super(context, str);
        this.timePickerState = -1;
        this.KEY = "backlight_time_control";
        this.sdf = new SimpleDateFormat("hh:mm");
        this.handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.display.syudisplay.TimePickerContrlooer.6
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                String str2 = TimePickerContrlooer.TAG;
                Log.d(str2, "handleMessage: " + message.what);
                if (message.what == TimePickerContrlooer.updateUI) {
                    TimePickerContrlooer.this.updateTimePickerLayout();
                } else if (message.what == TimePickerContrlooer.UPDATESUNRISE) {
                    int i = message.arg1;
                    int i2 = message.arg2;
                    IpcObj.getInstance().setCmd(0, 136, new int[]{1, i, i2}, null, null);
                    MySharePreference.saveIntValue("sunRiseH", i);
                    MySharePreference.saveIntValue("sunRiseM", i2);
                } else if (message.what == TimePickerContrlooer.UPDATESUNDOWN) {
                    int i3 = message.arg1;
                    int i4 = message.arg2;
                    IpcObj.getInstance().setCmd(0, 136, new int[]{0, i3, i4}, null, null);
                    MySharePreference.saveIntValue("sunDownH", i3);
                    MySharePreference.saveIntValue("sunDownM", i4);
                }
                super.handleMessage(message);
            }
        };
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(this.KEY);
        this.mPreference = layoutPreference;
        if (layoutPreference == null) {
            return;
        }
        initViews();
        super.displayPreference(preferenceScreen);
    }

    private void initViews() {
        this.sunrisePicker = (TimePicker) this.mPreference.findViewById(R$id.backlight_time_picker_sunrise);
        this.sundownPicker = (TimePicker) this.mPreference.findViewById(R$id.backlight_time_picker_sundown);
        this.autoCheckBox = (CheckBox) this.mPreference.findViewById(R$id.backlight_time_auto);
        this.userCheckBox = (CheckBox) this.mPreference.findViewById(R$id.backlight_time_custom);
        this.layout = (RelativeLayout) this.mPreference.findViewById(R$id.backlight_title_layout);
        this.backState = (TextView) this.mPreference.findViewById(R$id.backlight_state);
        this.mTimeAutoSeek = (LinearLayout) this.mPreference.findViewById(R$id.backlight_time_auto_seek);
        this.mTimePickerLayout = (RelativeLayout) this.mPreference.findViewById(R$id.backlight_control_layout);
        this.mTimeAutoLayout = (RelativeLayout) this.mPreference.findViewById(R$id.backlight_time_auto_layout);
        LinearLayout linearLayout = (LinearLayout) this.mPreference.findViewById(R$id.time_auto_layout);
        this.mTimePicker = linearLayout;
        linearLayout.setVisibility(this.timePickerState == 1 ? 0 : 8);
        this.mTimeStart = (TextView) this.mPreference.findViewById(R$id.time_start_tv);
        this.mTimeEnd = (TextView) this.mPreference.findViewById(R$id.time_end_tv);
        this.sunDownPro = (ColorArcProgressBar) this.mPreference.findViewById(R$id.seek_sundown);
        this.sunRisePro = (ColorArcProgressBar) this.mPreference.findViewById(R$id.seek_sunrise);
        this.mSunTimeLy = (LinearLayout) this.mPreference.findViewById(R$id.backlight_suntime_ly);
        this.mSundownTime = (TextView) this.mPreference.findViewById(R$id.backlight_sundown_time_tv);
        this.mSunriseTime = (TextView) this.mPreference.findViewById(R$id.backlight_sunrise_time_tv);
        TimePicker timePicker = this.sunrisePicker;
        Boolean bool = Boolean.TRUE;
        timePicker.setIs24HourView(bool);
        this.sundownPicker.setIs24HourView(bool);
        this.autoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.display.syudisplay.TimePickerContrlooer.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    TimePickerContrlooer.this.setTimeOrSmallLightOnCheck(false);
                    TimePickerContrlooer.this.setAutoTime();
                }
            }
        });
        this.userCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.display.syudisplay.TimePickerContrlooer.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    TimePickerContrlooer.this.setTimeOrSmallLightOnCheck(true);
                }
            }
        });
        this.layout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.TimePickerContrlooer.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpcObj ipcObj = IpcObj.getInstance();
                int[] iArr = new int[1];
                iArr[0] = TimePickerContrlooer.this.timePickerState == 1 ? 0 : 1;
                ipcObj.setCmd(0, 1, iArr);
                if (TimePickerContrlooer.this.handler != null) {
                    TimePickerContrlooer.this.handler.sendEmptyMessage(TimePickerContrlooer.updateUI);
                }
            }
        });
        this.sunrisePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() { // from class: com.android.settings.display.syudisplay.TimePickerContrlooer.4
            @Override // com.android.settings.display.syudisplay.TimePicker.OnTimeChangedListener
            public void onTimeChanged(TimePicker timePicker2, int i, int i2, int i3) {
                String str;
                String str2;
                Log.d(TimePickerContrlooer.TAG, "sunrise onTimeChanged: " + i + "   " + i2);
                if (i < 10) {
                    str = "0" + i;
                } else {
                    str = "" + i;
                }
                if (i2 < 10) {
                    str2 = "0" + i2;
                } else {
                    str2 = "" + i2;
                }
                TimePickerContrlooer.this.mTimeEnd.setText(TimePickerContrlooer.this.mContext.getResources().getString(R$string.backlight_end_time) + " " + str + " : " + str2);
                if (TimePickerContrlooer.this.handler != null) {
                    if (TimePickerContrlooer.this.handler.hasMessages(TimePickerContrlooer.UPDATESUNRISE)) {
                        TimePickerContrlooer.this.handler.removeMessages(TimePickerContrlooer.UPDATESUNRISE);
                    }
                    Message message = new Message();
                    message.what = TimePickerContrlooer.UPDATESUNRISE;
                    message.arg1 = i;
                    message.arg2 = i2;
                    TimePickerContrlooer.this.handler.sendMessageDelayed(message, 500L);
                }
            }
        });
        this.sundownPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() { // from class: com.android.settings.display.syudisplay.TimePickerContrlooer.5
            @Override // com.android.settings.display.syudisplay.TimePicker.OnTimeChangedListener
            public void onTimeChanged(TimePicker timePicker2, int i, int i2, int i3) {
                String str;
                String str2;
                Log.d(TimePickerContrlooer.TAG, "sundown onTimeChanged: " + i + "   " + i2);
                if (i < 10) {
                    str = "0" + i;
                } else {
                    str = "" + i;
                }
                if (i2 < 10) {
                    str2 = "0" + i2;
                } else {
                    str2 = "" + i2;
                }
                TimePickerContrlooer.this.mTimeStart.setText(TimePickerContrlooer.this.mContext.getResources().getString(R$string.backlight_start_time) + " " + str + " : " + str2 + "  -  ");
                if (TimePickerContrlooer.this.handler != null) {
                    if (TimePickerContrlooer.this.handler.hasMessages(TimePickerContrlooer.UPDATESUNDOWN)) {
                        TimePickerContrlooer.this.handler.removeMessages(TimePickerContrlooer.UPDATESUNDOWN);
                    }
                    Message obtain = Message.obtain();
                    obtain.what = TimePickerContrlooer.UPDATESUNDOWN;
                    obtain.arg1 = i;
                    obtain.arg2 = i2;
                    TimePickerContrlooer.this.handler.sendMessageDelayed(obtain, 500L);
                }
            }
        });
        getAutoTime();
    }

    public void setTimePickerState(int i) {
        this.timePickerState = i;
        Log.d("fangli", "setTimePickerState: " + this.timePickerState);
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendEmptyMessage(updateUI);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimePickerLayout() {
        this.backState.setText(this.timePickerState == 1 ? R$string.backlight_control_time : R$string.backlight_control_lamp);
        this.mTimePickerLayout.setVisibility(this.timePickerState == 1 ? 0 : 8);
        this.mTimeAutoLayout.setVisibility(this.timePickerState == 1 ? 0 : 8);
        boolean z = SystemProperties.getBoolean("persist.fyt.lighttimeautoenable", false);
        if (this.timePickerState == 1) {
            setTimeOrSmallLightOnCheck(!z);
        }
    }

    public void setTimeOrSmallLightOnCheck(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(!z);
        sb.append("");
        SystemProperties.set("persist.fyt.lighttimeautoenable", sb.toString());
        if (z) {
            this.autoCheckBox.setClickable(true);
            this.userCheckBox.setClickable(false);
            this.autoCheckBox.setChecked(false);
            this.userCheckBox.setChecked(true);
            this.mTimeAutoSeek.setVisibility(8);
            this.mSunTimeLy.setVisibility(8);
            this.mTimePicker.setVisibility(0);
            int intValue = MySharePreference.getIntValue("sunRiseH", 7);
            int intValue2 = MySharePreference.getIntValue("sunRiseM", 0);
            int intValue3 = MySharePreference.getIntValue("sunDownH", 19);
            int intValue4 = MySharePreference.getIntValue("sunDownM", 0);
            setTimePicker(0, intValue3, intValue4);
            setTimePicker(1, intValue, intValue2);
            IpcObj.getInstance().setCmd(0, 136, new int[]{1, intValue, intValue2}, null, null);
            IpcObj.getInstance().setCmd(0, 136, new int[]{0, intValue3, intValue4}, null, null);
            return;
        }
        this.autoCheckBox.setClickable(false);
        this.userCheckBox.setClickable(true);
        this.autoCheckBox.setChecked(true);
        this.userCheckBox.setChecked(false);
        this.mTimeAutoSeek.setVisibility(0);
        this.mSunTimeLy.setVisibility(0);
        this.mTimePicker.setVisibility(8);
        getAutoTime();
    }

    private void getAutoTime() {
        String str;
        try {
            SettingsApplication settingsApplication = SettingsApplication.mApplication;
            SettingsApplication settingsApplication2 = SettingsApplication.mApplication;
            float f = (((SettingsApplication.riseH * 60) + SettingsApplication.riseM) * 100) / 1440;
            SettingsApplication settingsApplication3 = SettingsApplication.mApplication;
            SettingsApplication settingsApplication4 = SettingsApplication.mApplication;
            float f2 = (((SettingsApplication.downH * 60) + SettingsApplication.downM) * 100) / 1440;
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getAutoTime: ");
            sb.append(f);
            sb.append("  ");
            sb.append(f2);
            sb.append("   ");
            SettingsApplication settingsApplication5 = SettingsApplication.mApplication;
            sb.append(SettingsApplication.riseH);
            Log.d(str2, sb.toString());
            this.sunDownPro.setCurrentValues(f2);
            this.sunRisePro.setCurrentValues(f);
            SettingsApplication settingsApplication6 = SettingsApplication.mApplication;
            String str3 = "00";
            if (SettingsApplication.riseM < 10) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("0");
                SettingsApplication settingsApplication7 = SettingsApplication.mApplication;
                sb2.append(SettingsApplication.riseM);
                str = sb2.toString();
            } else {
                str = "00";
            }
            SettingsApplication settingsApplication8 = SettingsApplication.mApplication;
            if (SettingsApplication.downM < 10) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("0");
                SettingsApplication settingsApplication9 = SettingsApplication.mApplication;
                sb3.append(SettingsApplication.downM);
                str3 = sb3.toString();
            }
            TextView textView = this.mSundownTime;
            StringBuilder sb4 = new StringBuilder();
            SettingsApplication settingsApplication10 = SettingsApplication.mApplication;
            sb4.append(SettingsApplication.downH);
            sb4.append(":");
            sb4.append(str3);
            sb4.append("   --   ");
            SettingsApplication settingsApplication11 = SettingsApplication.mApplication;
            sb4.append(SettingsApplication.riseH);
            sb4.append(":");
            sb4.append(str);
            textView.setText(sb4.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getAutoTime: " + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAutoTime() {
        IpcObj ipcObj = IpcObj.getInstance();
        SettingsApplication settingsApplication = SettingsApplication.mApplication;
        SettingsApplication settingsApplication2 = SettingsApplication.mApplication;
        ipcObj.setCmd(0, 136, new int[]{1, SettingsApplication.riseH, SettingsApplication.riseM}, null, null);
        IpcObj ipcObj2 = IpcObj.getInstance();
        SettingsApplication settingsApplication3 = SettingsApplication.mApplication;
        SettingsApplication settingsApplication4 = SettingsApplication.mApplication;
        ipcObj2.setCmd(0, 136, new int[]{0, SettingsApplication.downH, SettingsApplication.downM}, null, null);
    }

    public void setTimePicker(int i, int i2, int i3) {
        String str;
        String str2;
        if (i2 < 10) {
            str = "0" + i2;
        } else {
            str = "" + i2;
        }
        if (i3 < 10) {
            str2 = "0" + i3;
        } else {
            str2 = "" + i3;
        }
        Log.d(TAG, "setTimePicker: " + str + "  " + str2);
        if (i == 0) {
            this.sundownPicker.setHour(Integer.valueOf(i2));
            this.sundownPicker.setMinute(Integer.valueOf(i3));
            this.mTimeStart.setText(this.mContext.getResources().getString(R$string.backlight_start_time) + " " + str + " : " + str2 + "  -  ");
        } else if (i == 1) {
            this.sunrisePicker.setHour(Integer.valueOf(i2));
            this.sunrisePicker.setMinute(Integer.valueOf(i3));
            this.mTimeEnd.setText(this.mContext.getResources().getString(R$string.backlight_end_time) + " " + str + " : " + str2);
        }
    }
}
