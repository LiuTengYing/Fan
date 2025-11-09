package com.android.settings.common.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.FastDoubleClickUtils;
import com.android.settings.utils.UpdateStateChange;
import com.android.settings.widget.view.TextSeekBarCenter;
import com.syu.util.MySharePreference;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class CommonSevenLightDialogFragment extends InstrumentedDialogFragment {
    private static String SAVECOLOR = "RGBColors";
    private static int currentColor;
    private Switch mAutoSync;
    private ImageView mLightBlue;
    private ImageView mLightGreen;
    private ImageView mLightOrange;
    private ImageView mLightPink;
    private ImageView mLightRed;
    private ImageView mLightSkyBlue;
    private ImageView mLightWhite;
    private TextSeekBarCenter mProgressBlue;
    private TextSeekBarCenter mProgressGreen;
    private TextSeekBarCenter mProgressRed;
    private ImageView mReset;
    private View mRootView;
    private TextView mTextB;
    private TextView mTextG;
    private TextView mTextR;
    private ViewGroup.LayoutParams params;
    public static int COLOR_RED = 1;
    public static int COLOR_ORANGE = 2;
    public static int COLOR_WHITE = 3;
    public static int COLOR_GREEN = 4;
    public static int COLOR_BLUE = 5;
    public static int COLOR_IODIGE = 6;
    public static int COLOR_PURPLE = 7;
    public static String defaultColors = "{\"" + COLOR_RED + "\":{\"color\":-65536},\"" + COLOR_ORANGE + "\":{\"color\":-433384},\"" + COLOR_WHITE + "\":{\"color\":-1},\"" + COLOR_GREEN + "\":{\"color\":-16711936},\"" + COLOR_BLUE + "\":{\"color\":-16776961},\"" + COLOR_IODIGE + "\":{\"color\":-15551233},\"" + COLOR_PURPLE + "\":{\"color\":-59706},\"current\":" + COLOR_WHITE + ",\"auto\":\"true\"}";
    private JSONObject thisColor = new JSONObject();
    private int currentSelectColor = 0;
    private boolean isUseDefault = false;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonSevenLightDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonSevenLightDialogFragment commonSevenLightDialogFragment = new CommonSevenLightDialogFragment();
            commonSevenLightDialogFragment.setArguments(bundle);
            commonSevenLightDialogFragment.show(childFragmentManager, "CommonSevenLightDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        if (SettingsApplication.mHeightFix > 1100) {
            this.params = new ViewGroup.LayoutParams((windowManeger[0] / 5) * 3, (windowManeger[1] / 5) * 3);
        } else {
            this.params = new ViewGroup.LayoutParams((windowManeger[0] / 5) * 3, (windowManeger[1] / 4) * 3);
        }
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_seven_light_dialog, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        setTitle(string);
        initViews();
        initData();
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.select_app_title)).setText(str);
    }

    private void initData() {
        int selectColor = getSelectColor();
        this.currentSelectColor = selectColor;
        initColor(selectColor);
        updateBtnSelect(this.currentSelectColor);
    }

    private void initViews() {
        this.mLightRed = (ImageView) this.mRootView.findViewById(R$id.seven_light_red);
        this.mLightOrange = (ImageView) this.mRootView.findViewById(R$id.seven_light_orange);
        this.mLightWhite = (ImageView) this.mRootView.findViewById(R$id.seven_light_white);
        this.mLightGreen = (ImageView) this.mRootView.findViewById(R$id.seven_light_green);
        this.mLightBlue = (ImageView) this.mRootView.findViewById(R$id.seven_light_blue);
        this.mLightSkyBlue = (ImageView) this.mRootView.findViewById(R$id.seven_light_sky_blue);
        this.mLightPink = (ImageView) this.mRootView.findViewById(R$id.seven_light_pink);
        this.mProgressRed = (TextSeekBarCenter) this.mRootView.findViewById(R$id.seek_progress_red);
        this.mProgressGreen = (TextSeekBarCenter) this.mRootView.findViewById(R$id.seek_progress_green);
        this.mProgressBlue = (TextSeekBarCenter) this.mRootView.findViewById(R$id.seek_progress_blue);
        this.mTextR = (TextView) this.mRootView.findViewById(R$id.seven_light_rgb_r);
        this.mTextG = (TextView) this.mRootView.findViewById(R$id.seven_light_rgb_g);
        this.mTextB = (TextView) this.mRootView.findViewById(R$id.seven_light_rgb_b);
        this.mAutoSync = (Switch) this.mRootView.findViewById(R$id.light_auto_sync);
        this.mReset = (ImageView) this.mRootView.findViewById(R$id.light_reset);
        Log.d("CommonSevenLightDialogFragment", "initViews: " + getAuto());
        this.mAutoSync.setChecked(getAuto());
        initListener();
        setClickEnable(getAuto() ^ true);
    }

    private void initListener() {
        this.mLightRed.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CommonSevenLightDialogFragment.this.currentSelectColor == CommonSevenLightDialogFragment.COLOR_RED) {
                    return;
                }
                if (CommonSevenLightDialogFragment.this.isUseDefault) {
                    CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_RED;
                    CommonSevenLightDialogFragment.this.setColor(255, 0, 0);
                    CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_RED);
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(CommonSevenLightDialogFragment.COLOR_RED);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_RED;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_RED);
            }
        });
        this.mLightOrange.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i = CommonSevenLightDialogFragment.this.currentSelectColor;
                int i2 = CommonSevenLightDialogFragment.COLOR_ORANGE;
                if (i == i2) {
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(i2);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_ORANGE;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_ORANGE);
            }
        });
        this.mLightWhite.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CommonSevenLightDialogFragment.this.currentSelectColor == CommonSevenLightDialogFragment.COLOR_WHITE) {
                    return;
                }
                if (CommonSevenLightDialogFragment.this.isUseDefault) {
                    CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_WHITE;
                    CommonSevenLightDialogFragment.this.setColor(255, 255, 255);
                    CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_WHITE);
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(CommonSevenLightDialogFragment.COLOR_WHITE);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_WHITE;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_WHITE);
            }
        });
        this.mLightGreen.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CommonSevenLightDialogFragment.this.currentSelectColor == CommonSevenLightDialogFragment.COLOR_GREEN) {
                    return;
                }
                if (CommonSevenLightDialogFragment.this.isUseDefault) {
                    CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_GREEN;
                    CommonSevenLightDialogFragment.this.setColor(0, 255, 0);
                    CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_GREEN);
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(CommonSevenLightDialogFragment.COLOR_GREEN);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_GREEN;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_GREEN);
            }
        });
        this.mLightBlue.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CommonSevenLightDialogFragment.this.currentSelectColor == CommonSevenLightDialogFragment.COLOR_BLUE) {
                    return;
                }
                if (CommonSevenLightDialogFragment.this.isUseDefault) {
                    CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_BLUE;
                    CommonSevenLightDialogFragment.this.setColor(0, 0, 255);
                    CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_BLUE);
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(CommonSevenLightDialogFragment.COLOR_BLUE);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_BLUE;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_BLUE);
            }
        });
        this.mLightSkyBlue.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i = CommonSevenLightDialogFragment.this.currentSelectColor;
                int i2 = CommonSevenLightDialogFragment.COLOR_IODIGE;
                if (i == i2) {
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(i2);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_IODIGE;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_IODIGE);
            }
        });
        this.mLightPink.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i = CommonSevenLightDialogFragment.this.currentSelectColor;
                int i2 = CommonSevenLightDialogFragment.COLOR_PURPLE;
                if (i == i2) {
                    return;
                }
                int color = CommonSevenLightDialogFragment.this.getColor(i2);
                CommonSevenLightDialogFragment.this.currentSelectColor = CommonSevenLightDialogFragment.COLOR_PURPLE;
                CommonSevenLightDialogFragment.this.setColor(Color.red(color), Color.green(color), Color.blue(color));
                CommonSevenLightDialogFragment.this.updateBtnSelect(CommonSevenLightDialogFragment.COLOR_PURPLE);
            }
        });
        this.mAutoSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.8
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    IpcObj.getInstance().setCmd(0, 123, new int[]{2, 0}, null, null);
                    CommonSevenLightDialogFragment.this.setClickEnable(false);
                } else {
                    IpcObj.getInstance().setCmd(0, 123, new int[]{0, CommonSevenLightDialogFragment.currentColor}, null, null);
                    CommonSevenLightDialogFragment.this.setClickEnable(true);
                }
                CommonSevenLightDialogFragment.this.saveAuto(z);
            }
        });
        this.mReset.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FastDoubleClickUtils.isFastDoubleClick()) {
                    return;
                }
                CommonSevenLightDialogFragment.this.resetColors();
            }
        });
        this.mProgressRed.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.10
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                Color.red(CommonSevenLightDialogFragment.currentColor);
                CommonSevenLightDialogFragment.this.setColor(i, Color.green(CommonSevenLightDialogFragment.currentColor), Color.blue(CommonSevenLightDialogFragment.currentColor));
            }
        });
        this.mProgressGreen.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.11
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                int red = Color.red(CommonSevenLightDialogFragment.currentColor);
                Color.green(CommonSevenLightDialogFragment.currentColor);
                CommonSevenLightDialogFragment.this.setColor(red, i, Color.blue(CommonSevenLightDialogFragment.currentColor));
            }
        });
        this.mProgressBlue.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.common.dialog.CommonSevenLightDialogFragment.12
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                int red = Color.red(CommonSevenLightDialogFragment.currentColor);
                int green = Color.green(CommonSevenLightDialogFragment.currentColor);
                Color.blue(CommonSevenLightDialogFragment.currentColor);
                CommonSevenLightDialogFragment.this.setColor(red, green, i);
            }
        });
    }

    private void setRGB(int i, int i2, int i3) {
        this.mProgressRed.setMin(0);
        this.mProgressBlue.setMin(0);
        this.mProgressGreen.setMin(0);
        this.mProgressRed.setMax(255);
        this.mProgressBlue.setMax(255);
        this.mProgressGreen.setMax(255);
        this.mProgressRed.setProgress(i);
        this.mProgressGreen.setProgress(i2);
        this.mProgressBlue.setProgress(i3);
        TextView textView = this.mTextR;
        textView.setText(i + "");
        TextView textView2 = this.mTextG;
        textView2.setText(i2 + "");
        TextView textView3 = this.mTextB;
        textView3.setText(i3 + "");
        currentColor = (i << 16) + (i2 << 8) + i3;
        IpcObj.getInstance().setCmd(0, 123, new int[]{0, currentColor}, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getColor(int i) {
        int i2;
        String stringValue;
        int i3 = 0;
        try {
            stringValue = MySharePreference.getStringValue(SAVECOLOR);
        } catch (Exception e) {
            e = e;
        }
        if (!TextUtils.isEmpty(stringValue)) {
            JSONObject jSONObject = new JSONObject(stringValue);
            String optString = jSONObject.optString(i + "");
            Log.d("CommonSevenLightDialogFragment", "getColor1111: " + optString);
            if (!TextUtils.isEmpty(optString)) {
                i2 = new JSONObject(optString).optInt("color");
                try {
                    this.isUseDefault = false;
                } catch (Exception e2) {
                    e = e2;
                    i3 = i2;
                    e.printStackTrace();
                    Log.d("CommonSevenLightDialogFragment", "getRgb: " + e.toString());
                    i2 = i3;
                    Log.d("CommonSevenLightDialogFragment", "getColor: " + i2);
                    return i2;
                }
                Log.d("CommonSevenLightDialogFragment", "getColor: " + i2);
                return i2;
            }
            JSONObject jSONObject2 = new JSONObject(defaultColors);
            i3 = new JSONObject(jSONObject2.optString(i + "")).optInt("color");
            this.isUseDefault = true;
        } else {
            JSONObject jSONObject3 = new JSONObject(defaultColors);
            i3 = new JSONObject(jSONObject3.optString(i + "")).optInt("color");
            this.isUseDefault = true;
        }
        i2 = i3;
        Log.d("CommonSevenLightDialogFragment", "getColor: " + i2);
        return i2;
    }

    private void saveColor(int i) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("color", i);
            JSONObject jSONObject2 = this.thisColor;
            jSONObject2.put("" + this.currentSelectColor, jSONObject);
            this.thisColor.put("current", this.currentSelectColor);
            this.thisColor.put("auto", false);
            MySharePreference.saveStringValue("RGBColors", this.thisColor.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CommonSevenLightDialogFragment", "saveColor: " + e.toString());
        }
        UpdateStateChange.getInstance().updateChoice("seven_light", "Color");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setColor(int i, int i2, int i3) {
        setRGB(i, i2, i3);
        saveColor(Color.rgb(i, i2, i3));
    }

    private int getSelectColor() {
        int i = 0;
        try {
            String stringValue = MySharePreference.getStringValue(SAVECOLOR);
            if (!TextUtils.isEmpty(stringValue)) {
                JSONObject jSONObject = new JSONObject(stringValue);
                this.thisColor = jSONObject;
                i = jSONObject.optInt("current");
            }
            if (i == 0) {
                i = new JSONObject(defaultColors).optInt("current");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CommonSevenLightDialogFragment", "getSelectColor: " + e.toString());
        }
        Log.d("CommonSevenLightDialogFragment", "getSelectColor: " + i);
        return i;
    }

    private void initColor(int i) {
        int color = getColor(i);
        currentColor = (Color.red(getColor(i)) << 16) + (Color.green(getColor(i)) << 8) + Color.blue(getColor(i));
        this.mProgressRed.setProgress(Color.red(color));
        this.mProgressGreen.setProgress(Color.green(color));
        this.mProgressBlue.setProgress(Color.blue(color));
        TextView textView = this.mTextR;
        textView.setText(Color.red(color) + "");
        TextView textView2 = this.mTextG;
        textView2.setText(Color.green(color) + "");
        TextView textView3 = this.mTextB;
        textView3.setText(Color.blue(color) + "");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBtnSelect(int i) {
        clearSelect();
        switch (i) {
            case 1:
                this.mLightRed.setSelected(true);
                return;
            case 2:
                this.mLightOrange.setSelected(true);
                return;
            case 3:
                this.mLightWhite.setSelected(true);
                return;
            case 4:
                this.mLightGreen.setSelected(true);
                return;
            case 5:
                this.mLightBlue.setSelected(true);
                return;
            case 6:
                this.mLightSkyBlue.setSelected(true);
                return;
            case 7:
                this.mLightPink.setSelected(true);
                return;
            default:
                this.mLightRed.setSelected(true);
                return;
        }
    }

    private void clearSelect() {
        this.mLightRed.setSelected(false);
        this.mLightOrange.setSelected(false);
        this.mLightWhite.setSelected(false);
        this.mLightGreen.setSelected(false);
        this.mLightBlue.setSelected(false);
        this.mLightSkyBlue.setSelected(false);
        this.mLightPink.setSelected(false);
    }

    public void resetColors() {
        try {
            JSONObject jSONObject = new JSONObject(defaultColors);
            int optInt = jSONObject.getJSONObject("" + this.currentSelectColor).optInt("color");
            Log.d("CommonSevenLightDialogFragment", "resetColors: " + optInt);
            setColor(Color.red(optInt), Color.green(optInt), Color.blue(optInt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveAuto(boolean z) {
        try {
            this.thisColor.put("auto", z);
            MySharePreference.saveStringValue("RGBColors", this.thisColor.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CommonSevenLightDialogFragment", "saveColor: " + e.toString());
        }
        UpdateStateChange.getInstance().updateChoice("seven_light", "Auto");
    }

    private boolean getAuto() {
        try {
            String stringValue = MySharePreference.getStringValue(SAVECOLOR);
            if (TextUtils.isEmpty(stringValue)) {
                return true;
            }
            return new JSONObject(stringValue).optBoolean("auto");
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClickEnable(boolean z) {
        this.mLightRed.setClickable(z);
        this.mLightOrange.setClickable(z);
        this.mLightWhite.setClickable(z);
        this.mLightGreen.setClickable(z);
        this.mLightBlue.setClickable(z);
        this.mLightSkyBlue.setClickable(z);
        this.mLightPink.setClickable(z);
        this.mReset.setClickable(z);
    }
}
