package com.android.settings.common.controller;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.common.dialog.CommonFanSetDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class CommonFanSetController extends AbstractPreferenceController implements LifecycleObserver {
    private static int fanAutoTemp = 0;
    private static int mode = 1;
    private boolean cycle;
    private boolean mAuto;
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "fan_set";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public CommonFanSetController(Context context, Lifecycle lifecycle) {
        super(context);
        this.cycle = true;
        this.mAuto = false;
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

    public void showDialog(Fragment fragment, String str) {
        CommonFanSetDialogFragment.show(fragment, str, fanAutoTemp, mode);
    }

    public void setTemp(int i) {
        fanAutoTemp = i;
    }

    public void setMode(boolean z) {
        this.cycle = z;
        Log.d("fangli", "setMode: " + this.mAuto);
        if (this.mAuto) {
            mode = 2;
        } else {
            mode = !this.cycle ? 1 : 0;
        }
        updateState();
    }

    public void setIsAuto(boolean z) {
        this.mAuto = z;
        Log.d("fangli", "setIsAuto: " + this.mAuto);
        if (z) {
            mode = 2;
        } else {
            mode = !this.cycle ? 1 : 0;
        }
        updateState();
    }

    public void updateState() {
        Resources resources = this.mContext.getResources();
        int i = R$string.fan_set_close;
        String string = resources.getString(i);
        int i2 = mode;
        if (i2 == 0) {
            string = this.mContext.getResources().getString(R$string.fan_set_open);
        } else if (i2 == 1) {
            string = this.mContext.getResources().getString(i);
        } else if (i2 == 2) {
            string = this.mContext.getResources().getString(R$string.fan_set_auto) + " : " + fanAutoTemp + "℃";
        }
        this.mPreference.setState(string);
    }
}
