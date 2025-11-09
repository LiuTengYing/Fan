package com.android.settings.factory.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsApplication;
import com.android.settings.factory.dialog.ProtocolSelectDialogFragment;
import com.android.settings.factory.protocol.CanBox;
import com.android.settings.factory.protocol.CarProtocol;
import com.android.settings.factory.protocol.CarType;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class ProtocolCarTyleController extends AbstractPreferenceController implements LifecycleObserver {
    private static int mCanbusId;
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "cartype";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ProtocolCarTyleController(Context context, Lifecycle lifecycle) {
        super(context);
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
        return TextUtils.equals(preference.getKey(), getPreferenceKey());
    }

    public void showCarType(Fragment fragment, String str) {
        Log.d("ProtocolCarTyleController", "showCarType: " + isAppInstalled(this.mContext, "com.hiworld.customer.service"));
        if (isAppInstalled(this.mContext, "com.hiworld.customer.service")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.hiworld.customer.service", "com.hiworld.customer.activity.MainActivity"));
            intent.setFlags(268435456);
            this.mContext.startActivity(intent);
            return;
        }
        ProtocolSelectDialogFragment.show(fragment, str, mCanbusId);
    }

    public static boolean isAppInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (Exception unused) {
        }
        return packageInfo != null;
    }

    public void setCanbusId(int i) {
        mCanbusId = i;
    }

    public void initCarStr() {
        Log.d("ProtocolCarTyleController", "initCarStr: " + SettingsApplication.mApplication.cartypeStr + "  " + SettingsApplication.is_UpdatingCartype);
        if (!SettingsApplication.is_UpdatingCartype.booleanValue()) {
            if (TextUtils.isEmpty(SettingsApplication.mApplication.cartypeStr)) {
                if (CarProtocol.mCurCanBox == null) {
                    CarProtocol.mCurCanBox = CarProtocol.getCanBoxById(mCanbusId);
                }
                CanBox canBox = CarProtocol.mCurCanBox;
                if (canBox != null) {
                    CarType carType = canBox.carType;
                    CarProtocol.mCurCarType = carType;
                    CarProtocol.mCurCarSet = carType.carSet;
                    if (this.mPreference != null) {
                        String str = CarProtocol.mCurCarType.strType;
                        String[] split = str.split("##");
                        if (split != null && split.length > 1) {
                            str = split[0];
                        }
                        Log.d("fangli", "initCarStr: " + CarProtocol.mCurCarType.strType);
                        RestrictedPreference restrictedPreference = this.mPreference;
                        restrictedPreference.setState(CarProtocol.mCurCarType.carSet.company.strCompany + "-" + CarProtocol.mCurCarSet.strSet + "-" + str + "-" + CarProtocol.mCurCanBox.strCompany);
                        return;
                    }
                    return;
                }
                return;
            }
            this.mPreference.setState(SettingsApplication.mApplication.cartypeStr);
            return;
        }
        if (CarProtocol.mCurCanBox == null) {
            CarProtocol.mCurCanBox = CarProtocol.getCanBoxById(mCanbusId);
        }
        CanBox canBox2 = CarProtocol.mCurCanBox;
        if (canBox2 != null) {
            CarType carType2 = canBox2.carType;
            CarProtocol.mCurCarType = carType2;
            CarProtocol.mCurCarSet = carType2.carSet;
            if (this.mPreference != null) {
                String str2 = CarProtocol.mCurCarType.strType;
                String[] split2 = str2.split("##");
                if (split2 != null && split2.length > 1) {
                    str2 = split2[0];
                }
                RestrictedPreference restrictedPreference2 = this.mPreference;
                restrictedPreference2.setState(CarProtocol.mCurCarType.carSet.company.strCompany + "-" + CarProtocol.mCurCarSet.strSet + "-" + str2 + "-" + CarProtocol.mCurCanBox.strCompany);
            }
        }
    }
}
