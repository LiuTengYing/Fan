package com.android.settings.common.controller;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.common.dialog.CommonSevenLightDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.syu.util.MySharePreference;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class CommonLanternController extends AbstractPreferenceController implements LifecycleObserver {
    private static String SAVECOLOR = "RGBColors";
    private boolean isUseDefault;
    private Context mContext;
    private RestrictedPreference mPreference;
    private JSONObject thisColor;
    public static int COLOR_RED = 1;
    public static int COLOR_ORANGE = 2;
    public static int COLOR_WHITE = 3;
    public static int COLOR_GREEN = 4;
    public static int COLOR_BLUE = 5;
    public static int COLOR_IODIGE = 6;
    public static int COLOR_PURPLE = 7;
    public static String defaultColors = "{\"" + COLOR_RED + "\":{\"color\":-258029},\"" + COLOR_ORANGE + "\":{\"color\":-433384},\"" + COLOR_WHITE + "\":{\"color\":-66563},\"" + COLOR_GREEN + "\":{\"color\":-14811307},\"" + COLOR_BLUE + "\":{\"color\":-12577028},\"" + COLOR_IODIGE + "\":{\"color\":-15551233},\"" + COLOR_PURPLE + "\":{\"color\":-59706},\"current\":" + COLOR_WHITE + ",\"auto\":\"true\"}";

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "seven_light";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public CommonLanternController(Context context, Lifecycle lifecycle) {
        super(context);
        this.thisColor = new JSONObject();
        this.isUseDefault = false;
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Log.d("fangli", "handlePreferenceTreeClick: ");
        return true;
    }

    public void showDialog(Fragment fragment, String str) {
        CommonSevenLightDialogFragment.show(fragment, str);
    }

    public void updateColor() {
        if (getAuto()) {
            this.mPreference.setState(this.mContext.getResources().getString(R$string.light_auto));
        } else {
            initColor(getSelectColor());
        }
    }

    private int getSelectColor() {
        int i = 0;
        try {
            String stringValue = MySharePreference.getStringValue(SAVECOLOR);
            if (stringValue != null && !TextUtils.isEmpty(stringValue)) {
                JSONObject jSONObject = new JSONObject(stringValue);
                this.thisColor = jSONObject;
                i = jSONObject.optInt("current");
            }
            if (i == 0) {
                i = new JSONObject(defaultColors).optInt("current");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CommonLanternController", "getSelectColor: " + e.toString());
        }
        Log.d("CommonLanternController", "getSelectColor: " + i);
        return i;
    }

    private void initColor(int i) {
        int color = getColor(i);
        if (!this.isUseDefault) {
            RestrictedPreference restrictedPreference = this.mPreference;
            restrictedPreference.setState("RGB(" + Color.red(color) + "," + Color.green(color) + "," + Color.blue(color) + ")");
        } else if (i == 1) {
            this.mPreference.setState("RGB(255,0,0)");
        } else if (i == 3) {
            this.mPreference.setState("RGB(255,255,255)");
        } else if (i == 4) {
            this.mPreference.setState("RGB(0,255,0)");
        } else if (i == 5) {
            this.mPreference.setState("RGB(0,0,255)");
        } else {
            RestrictedPreference restrictedPreference2 = this.mPreference;
            restrictedPreference2.setState("RGB(" + Color.red(color) + "," + Color.green(color) + "," + Color.blue(color) + ")");
        }
    }

    private int getColor(int i) {
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
            Log.d("CommonLanternController", "getColor1111: " + optString);
            if (!TextUtils.isEmpty(optString)) {
                i2 = new JSONObject(optString).optInt("color");
                try {
                    this.isUseDefault = false;
                } catch (Exception e2) {
                    e = e2;
                    i3 = i2;
                    e.printStackTrace();
                    Log.d("CommonLanternController", "getRgb: " + e.toString());
                    i2 = i3;
                    Log.d("CommonLanternController", "getColor: " + i2);
                    return i2;
                }
            } else {
                JSONObject jSONObject2 = new JSONObject(defaultColors);
                i3 = new JSONObject(jSONObject2.optString(i + "")).optInt("color");
                this.isUseDefault = true;
                i2 = i3;
            }
        } else {
            JSONObject jSONObject3 = new JSONObject(defaultColors);
            i2 = new JSONObject(jSONObject3.optString(i + "")).optInt("color");
        }
        Log.d("CommonLanternController", "getColor: " + i2);
        return i2;
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
}
