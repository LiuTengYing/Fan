package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.debug.PairDevice;
import android.os.Parcelable;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.ActionButtonsPreference;
/* loaded from: classes.dex */
public class AdbDeviceDetailsActionController extends AbstractPreferenceController {
    static final String KEY_BUTTONS_PREF = "buttons";
    private ActionButtonsPreference mButtonsPref;
    private final Fragment mFragment;
    private PairDevice mPairedDevice;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_BUTTONS_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdbDeviceDetailsActionController(PairDevice pairDevice, Context context, Fragment fragment) {
        super(context);
        this.mPairedDevice = pairDevice;
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mButtonsPref = ((ActionButtonsPreference) preferenceScreen.findPreference(getPreferenceKey())).setButton1Visible(false).setButton2Icon(R$drawable.ic_settings_delete).setButton2Text(R$string.adb_device_forget).setButton2OnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.AdbDeviceDetailsActionController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdbDeviceDetailsActionController.this.lambda$displayPreference$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        forgetDevice();
    }

    private void forgetDevice() {
        Intent intent = new Intent();
        intent.putExtra("request_type", 0);
        intent.putExtra("paired_device", (Parcelable) this.mPairedDevice);
        this.mFragment.getActivity().setResult(-1, intent);
        this.mFragment.getActivity().finish();
    }
}
