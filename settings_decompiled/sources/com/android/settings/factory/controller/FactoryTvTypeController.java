package com.android.settings.factory.controller;

import android.content.Context;
import android.content.res.Resources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.factory.dialog.FactoryTvTypeFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryTvTypeController extends AbstractPreferenceController implements LifecycleObserver {
    private static int itemSelect = -1;
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "tvtype";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryTvTypeController(Context context, Lifecycle lifecycle) {
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

    public void showDialog(Fragment fragment, String str) {
        FactoryTvTypeFragment.show(fragment, str, itemSelect);
    }

    public void setItemSelect(int i) {
        itemSelect = i;
        this.mPreference.setState(getTvType(i));
    }

    private String getTvType(int i) {
        Resources resources = this.mContext.getResources();
        int i2 = R$string.factory_settings_tv_type_0;
        String string = resources.getString(i2);
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return i != 3 ? string : this.mContext.getResources().getString(R$string.factory_settings_tv_type_3);
                }
                return this.mContext.getResources().getString(R$string.factory_settings_tv_type_2);
            }
            return this.mContext.getResources().getString(R$string.factory_settings_tv_type_1);
        }
        return this.mContext.getResources().getString(i2);
    }
}
