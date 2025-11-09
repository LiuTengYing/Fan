package com.android.settings.display.syudisplay;

import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.ipc.IpcObj;
import com.android.settings.widget.view.ColorPickerView;
import com.android.settingslib.widget.LayoutPreference;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SettingsThemeController extends BasePreferenceController {
    private static String TAG = "SettingsThemeController";
    private static int UPDATETHEME = 987;
    private static int UPDATETIME = 989;
    private int ColorSelect;
    private String KEY;
    private int allColor;
    private int bt;
    private int btColor;
    private ColorPickerView colorPickerView;
    private int dspColor;
    private int eq;
    private TextView mAppSelect;
    private Switch mAutoSwitch;
    private ImageView mBtnClassic;
    private ImageView mBtnCustom;
    private ImageView mBtnDay;
    private ImageView mBtnNight;
    private RelativeLayout mColorPickLayout;
    private View mContentView;
    private Context mContext;
    private int mDayNightState;
    private Handler mHandler;
    private LayoutPreference mPreference;
    private TextView mSelectAll;
    private CheckBox mSelectAllBtn;
    private LinearLayout mSelectAllLy;
    private LinearLayout mSelectAppLayout;
    private int mSelectAppPosition;
    private TextView mSelectBt;
    private CheckBox mSelectBtBtn;
    private LinearLayout mSelectBtLy;
    private TextView mSelectDsp;
    private CheckBox mSelectDspBtn;
    private LinearLayout mSelectDspLy;
    private LinearLayout mSelectSettingly;
    private TextView mSelectSettings;
    private CheckBox mSelectSettingsBtn;
    private TextView mTextOk;
    private RelativeLayout mThemeAutoLayout;
    private LinearLayout mThemeClassic;
    private LinearLayout mThemeCustom;
    private LinearLayout mThemeDay;
    private LinearLayout mThemeNight;
    private UiModeManager mUiModeManager;
    private PopupWindow popupWindow;
    private int settingColor;
    private int theme;
    private JSONObject themeColor;

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

    public SettingsThemeController(Context context, String str) {
        super(context, str);
        this.KEY = "display_theme";
        this.theme = -1;
        this.mSelectAppPosition = 0;
        this.themeColor = new JSONObject();
        this.btColor = 0;
        this.dspColor = 0;
        this.settingColor = 0;
        this.allColor = 0;
        this.mDayNightState = 0;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.display.syudisplay.SettingsThemeController.12
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i != 987) {
                    if (i == 989) {
                        SettingsThemeController.this.mHandler.removeMessages(SettingsThemeController.UPDATETIME);
                        String str2 = SettingsThemeController.TAG;
                        Log.d(str2, "handleMessage: " + SettingsThemeController.this.mUiModeManager.getNightMode());
                        if (SettingsThemeController.this.mAutoSwitch != null) {
                            SettingsThemeController.this.mAutoSwitch.setChecked(SettingsThemeController.this.theme == 0);
                        }
                    }
                } else if (SettingsApplication.mApplication.getIsAutoTheme() == 1) {
                    SettingsThemeController settingsThemeController = SettingsThemeController.this;
                    settingsThemeController.updateTheme(settingsThemeController.mDayNightState == 0 ? 0 : 1, false, -1);
                }
                super.handleMessage(message);
            }
        };
        this.mContext = context;
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(this.KEY);
        this.mPreference = layoutPreference;
        if (layoutPreference == null) {
            return;
        }
        Log.d(TAG, "displayPreference: ");
        initViews();
        initData();
        initListener();
        super.displayPreference(preferenceScreen);
    }

    private void getAppShow() {
        this.eq = SystemProperties.getInt("persist.syu.themeEqShow", 1);
        this.bt = SystemProperties.getInt("persist.syu.themeBtShow", 1);
    }

    private void initViews() {
        this.ColorSelect = getColor();
        getAppShow();
        this.mThemeDay = (LinearLayout) this.mPreference.findViewById(R$id.btn_theme_day_layout);
        this.mThemeNight = (LinearLayout) this.mPreference.findViewById(R$id.btn_theme_night_layout);
        this.mThemeClassic = (LinearLayout) this.mPreference.findViewById(R$id.btn_theme_classic_layout);
        this.mThemeCustom = (LinearLayout) this.mPreference.findViewById(R$id.btn_theme_custom_layout);
        this.mBtnDay = (ImageView) this.mPreference.findViewById(R$id.btn_theme_day);
        this.mBtnNight = (ImageView) this.mPreference.findViewById(R$id.btn_theme_night);
        this.mBtnClassic = (ImageView) this.mPreference.findViewById(R$id.btn_theme_classic);
        this.mBtnCustom = (ImageView) this.mPreference.findViewById(R$id.btn_theme_custom);
        this.colorPickerView = (ColorPickerView) this.mPreference.findViewById(R$id.color_pick_view);
        this.mThemeAutoLayout = (RelativeLayout) this.mPreference.findViewById(R$id.theme_auto_layout);
        this.mAutoSwitch = (Switch) this.mPreference.findViewById(R$id.theme_auto_switch);
        this.mColorPickLayout = (RelativeLayout) this.mPreference.findViewById(R$id.theme_color_pick_layout);
        this.mSelectAppLayout = (LinearLayout) this.mPreference.findViewById(R$id.app_select_ly);
        this.mTextOk = (TextView) this.mPreference.findViewById(R$id.color_pick_ok);
        this.mSelectSettingly = (LinearLayout) this.mPreference.findViewById(R$id.select_app_settings_ly);
        this.mSelectBtLy = (LinearLayout) this.mPreference.findViewById(R$id.select_app_bt_ly);
        this.mSelectDspLy = (LinearLayout) this.mPreference.findViewById(R$id.select_app_dsp_ly);
        this.mSelectAllLy = (LinearLayout) this.mPreference.findViewById(R$id.select_app_all_ly);
        this.mSelectSettingsBtn = (CheckBox) this.mPreference.findViewById(R$id.select_app_settings_btn);
        this.mSelectBtBtn = (CheckBox) this.mPreference.findViewById(R$id.select_app_bt_btn);
        this.mSelectDspBtn = (CheckBox) this.mPreference.findViewById(R$id.select_app_dsp_btn);
        this.mSelectAllBtn = (CheckBox) this.mPreference.findViewById(R$id.select_app_all_btn);
        this.mAutoSwitch.setChecked(SettingsApplication.mApplication.getIsAutoTheme() == 1);
        ColorPickerView colorPickerView = this.colorPickerView;
        if (colorPickerView != null) {
            colorPickerView.getParent().requestDisallowInterceptTouchEvent(true);
            this.colorPickerView.setmInitialColor(this.ColorSelect);
            updateThemePreview(this.ColorSelect);
        }
        if (SystemProperties.getInt("persist.fyt.settingversion", 1) == 2) {
            this.mThemeAutoLayout.setVisibility(8);
        }
        updateTheme(SettingsApplication.mApplication.getThemeMode(), true, -1);
    }

    private int getColor() {
        int optInt;
        String str = SystemProperties.get("persist.syu.themecolor", "");
        if (!TextUtils.isEmpty(str)) {
            try {
                optInt = new JSONObject(str).optInt("settings");
            } catch (JSONException e) {
                e.printStackTrace();
                String str2 = TAG;
                Log.d(str2, "initData: " + e.toString());
            }
            String str3 = TAG;
            Log.d(str3, "getColor: " + optInt);
            return optInt;
        }
        optInt = -16777216;
        String str32 = TAG;
        Log.d(str32, "getColor: " + optInt);
        return optInt;
    }

    private void initData() {
        String str = SystemProperties.get("persist.syu.themecolor", "");
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                this.themeColor = jSONObject;
                this.btColor = jSONObject.optInt("bt");
                this.dspColor = this.themeColor.optInt("dsp");
                this.settingColor = this.themeColor.optInt("settings");
                this.allColor = this.themeColor.optInt("all");
            } catch (JSONException e) {
                e.printStackTrace();
                String str2 = TAG;
                Log.d(str2, "initData: " + e.toString());
            }
        }
        if (SettingsApplication.mApplication.getIsAutoTheme() == 1) {
            updateTheme(this.mDayNightState == 0 ? 0 : 1, false, -1);
        }
        String str3 = TAG;
        Log.d(str3, "initData: " + this.mDayNightState);
        updateAppSelect(0);
    }

    private void initListener() {
        this.mTextOk.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingsThemeController settingsThemeController = SettingsThemeController.this;
                settingsThemeController.saveThemeColor(settingsThemeController.ColorSelect);
                SettingsThemeController settingsThemeController2 = SettingsThemeController.this;
                settingsThemeController2.updateTheme(3, true, settingsThemeController2.ColorSelect);
                SettingsThemeController settingsThemeController3 = SettingsThemeController.this;
                settingsThemeController3.updateThemePreview(settingsThemeController3.ColorSelect);
            }
        });
        this.mBtnDay.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SettingsThemeController.this.mAutoSwitch.isChecked()) {
                    SettingsThemeController.this.mAutoSwitch.setChecked(false);
                }
                IpcObj.getInstance().setCmd(0, 178, 1);
                SettingsThemeController.this.updateTheme(0, false, -1);
            }
        });
        this.mBtnNight.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SettingsThemeController.this.mAutoSwitch.isChecked()) {
                    SettingsThemeController.this.mAutoSwitch.setChecked(false);
                }
                IpcObj.getInstance().setCmd(0, 178, 2);
                SettingsThemeController.this.updateTheme(1, false, -1);
            }
        });
        this.mBtnClassic.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SettingsThemeController.this.mAutoSwitch.isChecked()) {
                    SettingsThemeController.this.mAutoSwitch.setChecked(false);
                }
                IpcObj.getInstance().setCmd(0, 178, 2);
                SettingsThemeController.this.updateTheme(2, false, -1);
            }
        });
        this.mBtnCustom.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SettingsThemeController.this.mAutoSwitch.isChecked()) {
                    SettingsThemeController.this.mAutoSwitch.setChecked(false);
                }
                IpcObj.getInstance().setCmd(0, 178, 2);
                SettingsThemeController.this.updateTheme(3, true, -1);
            }
        });
        this.colorPickerView.setmListener(new ColorPickerView.OnColorChangedListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.6
            @Override // com.android.settings.widget.view.ColorPickerView.OnColorChangedListener
            public void colorChanged(int i, boolean z) {
                String str = SettingsThemeController.TAG;
                Log.d(str, "colorChanged: " + i);
                if (z) {
                    SettingsThemeController.this.saveThemeColor(i);
                }
                SettingsThemeController.this.ColorSelect = i;
                IpcObj.getInstance().setCmd(0, 178, 2);
                SettingsThemeController.this.updateTheme(3, z, i);
                SettingsThemeController.this.updateThemePreview(i);
            }
        });
        this.mAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    IpcObj.getInstance().setCmd(0, 178, 0);
                    SettingsThemeController settingsThemeController = SettingsThemeController.this;
                    settingsThemeController.updateTheme(settingsThemeController.mDayNightState == 0 ? 0 : 1, false, -1);
                } else {
                    IpcObj ipcObj = IpcObj.getInstance();
                    int[] iArr = new int[1];
                    iArr[0] = SettingsThemeController.this.mDayNightState == 0 ? 1 : 2;
                    ipcObj.setCmd(0, 178, iArr);
                    SettingsThemeController settingsThemeController2 = SettingsThemeController.this;
                    settingsThemeController2.updateTheme(settingsThemeController2.mDayNightState == 0 ? 0 : 1, false, -1);
                }
                String str = SettingsThemeController.TAG;
                Log.d(str, "onCheckedChanged: " + SettingsThemeController.this.mDayNightState);
                SettingsApplication.mApplication.setIsAutoTheme(z ? 1 : 0);
            }
        });
        initPopClickListener();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveThemeColor(int i) {
        try {
            int i2 = this.mSelectAppPosition;
            if (i2 == 1) {
                this.btColor = i;
                JSONObject jSONObject = this.themeColor;
                jSONObject.put("bt", i + "");
            } else if (i2 == 2) {
                this.dspColor = i;
                JSONObject jSONObject2 = this.themeColor;
                jSONObject2.put("dsp", i + "");
            } else if (i2 == 0) {
                this.settingColor = i;
                JSONObject jSONObject3 = this.themeColor;
                jSONObject3.put("settings", i + "");
            } else if (i2 == 3) {
                this.btColor = i;
                this.dspColor = i;
                this.settingColor = i;
                this.allColor = i;
                JSONObject jSONObject4 = this.themeColor;
                jSONObject4.put("bt", i + "");
                JSONObject jSONObject5 = this.themeColor;
                jSONObject5.put("dsp", i + "");
                JSONObject jSONObject6 = this.themeColor;
                jSONObject6.put("settings", i + "");
                JSONObject jSONObject7 = this.themeColor;
                jSONObject7.put("all", i + "");
            }
        } catch (JSONException e) {
            e.fillInStackTrace();
            String str = TAG;
            Log.d(str, "setAppSelect: " + e.toString());
        }
        SystemProperties.set("persist.syu.themecolor", this.themeColor.toString());
    }

    private void initPopClickListener() {
        if (this.eq == 0) {
            this.mSelectDspLy.setVisibility(8);
        }
        if (this.bt == 0) {
            this.mSelectBtLy.setVisibility(8);
        }
        this.mSelectBtLy.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingsThemeController.this.updateAppSelect(1);
            }
        });
        this.mSelectDspLy.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingsThemeController.this.updateAppSelect(2);
            }
        });
        this.mSelectSettingly.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingsThemeController.this.updateAppSelect(0);
            }
        });
        this.mSelectAllLy.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.SettingsThemeController.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingsThemeController.this.updateAppSelect(3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppSelect(int i) {
        this.mSelectAppPosition = i;
        clearSelect();
        if (i == 0) {
            int i2 = this.settingColor;
            if (i2 != 0) {
                updateThemePreview(i2);
                this.colorPickerView.setmInitialColor(this.settingColor);
            } else {
                updateThemePreview(-16777216);
                this.colorPickerView.setmInitialColor(-16777216);
            }
            this.mSelectSettingsBtn.setChecked(true);
        } else if (i == 1) {
            int i3 = this.btColor;
            if (i3 != 0) {
                updateThemePreview(i3);
                this.colorPickerView.setmInitialColor(this.btColor);
            } else {
                updateThemePreview(-16777216);
                this.colorPickerView.setmInitialColor(-16777216);
            }
            this.mSelectBtBtn.setChecked(true);
        } else if (i == 2) {
            int i4 = this.dspColor;
            if (i4 != 0) {
                updateThemePreview(i4);
                this.colorPickerView.setmInitialColor(this.dspColor);
            } else {
                updateThemePreview(-16777216);
                this.colorPickerView.setmInitialColor(-16777216);
            }
            this.mSelectDspBtn.setChecked(true);
        } else if (i != 3) {
        } else {
            int i5 = this.allColor;
            if (i5 != 0) {
                updateThemePreview(i5);
                this.colorPickerView.setmInitialColor(this.allColor);
            } else {
                updateThemePreview(-16777216);
                this.colorPickerView.setmInitialColor(-16777216);
            }
            this.mSelectAllBtn.setChecked(true);
        }
    }

    private void clearSelect() {
        this.mSelectSettingsBtn.setChecked(false);
        this.mSelectBtBtn.setChecked(false);
        this.mSelectDspBtn.setChecked(false);
        this.mSelectAllBtn.setChecked(false);
    }

    private String getSelectAppName(int i) {
        Resources resources = this.mContext.getResources();
        int i2 = R$string.theme_custom_bt;
        String string = resources.getString(i2);
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return i != 3 ? string : this.mContext.getResources().getString(R$string.theme_custom_all);
                }
                return this.mContext.getResources().getString(R$string.theme_custom_eq);
            }
            return this.mContext.getResources().getString(i2);
        }
        return this.mContext.getResources().getString(R$string.theme_custom_settings);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTheme(int i, boolean z, int i2) {
        String str = TAG;
        Log.d(str, "updateTheme: " + i);
        setSelectBk(i);
        if (i == 3) {
            int i3 = this.mSelectAppPosition;
            if (i3 == 0 || i3 == 3) {
                SettingsApplication.mApplication.getHomeActivity().setHomeBackground(i, z, i2);
                if (SettingsApplication.mApplication.getBaseActivity() != null) {
                    SettingsApplication.mApplication.getBaseActivity().setHomeBackground(i, z, i2);
                }
            }
        } else {
            SettingsApplication.mApplication.getHomeActivity().setHomeBackground(i, z, i2);
            if (SettingsApplication.mApplication.getBaseActivity() != null) {
                SettingsApplication.mApplication.getBaseActivity().setHomeBackground(i, z, i2);
            }
        }
        SettingsApplication.mApplication.setThemeMode(i);
        RelativeLayout relativeLayout = this.mColorPickLayout;
        if (relativeLayout != null) {
            relativeLayout.setVisibility(i == 3 ? 0 : 8);
        }
        LinearLayout linearLayout = this.mSelectAppLayout;
        if (linearLayout != null) {
            linearLayout.setVisibility(i != 3 ? 8 : 0);
        }
    }

    private void clearSelectBk() {
        this.mThemeDay.setBackground(null);
        this.mThemeNight.setBackground(null);
        this.mThemeClassic.setBackground(null);
        this.mThemeCustom.setBackground(null);
    }

    private void setSelectBk(int i) {
        clearSelectBk();
        if (i == 0) {
            this.mThemeDay.setBackground(this.mContext.getResources().getDrawable(R$drawable.select_bk));
        } else if (i == 1) {
            this.mThemeNight.setBackground(this.mContext.getResources().getDrawable(R$drawable.select_bk));
        } else if (i == 2) {
            this.mThemeClassic.setBackground(this.mContext.getResources().getDrawable(R$drawable.select_bk));
        } else if (i != 3) {
        } else {
            this.mThemeCustom.setBackground(this.mContext.getResources().getDrawable(R$drawable.select_bk));
        }
    }

    public void updateTheme(int i) {
        this.theme = i;
        String str = TAG;
        Log.d(str, "updateTheme: " + this.theme);
        if (this.mHandler != null) {
            Log.d("fangli", "updateTheme: 1111");
            this.mHandler.sendEmptyMessageDelayed(UPDATETIME, 500L);
        }
    }

    public void setDayNightState(int i) {
        String str = TAG;
        Log.d(str, "setDayNightState: " + i);
        this.mDayNightState = i;
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.sendEmptyMessageDelayed(UPDATETHEME, 10L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateThemePreview(int i) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{-16777216, i});
        gradientDrawable.setGradientType(0);
        if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 160) {
            gradientDrawable.setSize(188, R.styleable.AppCompatTheme_toolbarNavigationButtonStyle);
        } else if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 120) {
            gradientDrawable.setSize(141, 84);
        } else {
            gradientDrawable.setSize(282, 168);
        }
        gradientDrawable.setCornerRadii(new float[]{20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f});
        this.mBtnCustom.setImageDrawable(gradientDrawable);
    }
}
