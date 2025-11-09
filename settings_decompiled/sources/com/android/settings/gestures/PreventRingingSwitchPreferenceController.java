package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class PreventRingingSwitchPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, OnMainSwitchChangeListener {
    private final Context mContext;
    MainSwitchPreference mSwitch;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "gesture_prevent_ringing_switch";
    }

    public PreventRingingSwitchPreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference findPreference;
        super.displayPreference(preferenceScreen);
        if (!isAvailable() || (findPreference = preferenceScreen.findPreference(getPreferenceKey())) == null) {
            return;
        }
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.gestures.PreventRingingSwitchPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$displayPreference$0;
                lambda$displayPreference$0 = PreventRingingSwitchPreferenceController.this.lambda$displayPreference$0(preference);
                return lambda$displayPreference$0;
            }
        });
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) findPreference;
        this.mSwitch = mainSwitchPreference;
        mainSwitchPreference.setTitle(this.mContext.getString(R$string.prevent_ringing_main_switch_title));
        this.mSwitch.addOnSwitchChangeListener(this);
        updateState(this.mSwitch);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Preference preference) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "volume_hush_gesture", (Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1) != 0 ? 1 : 0) ^ 1);
        return true;
    }

    public void setChecked(boolean z) {
        MainSwitchPreference mainSwitchPreference = this.mSwitch;
        if (mainSwitchPreference != null) {
            mainSwitchPreference.updateStatus(z);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        setChecked(Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1) != 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(17891828);
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1);
        int i2 = i != 0 ? i : 1;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (!z) {
            i2 = 0;
        }
        Settings.Secure.putInt(contentResolver, "volume_hush_gesture", i2);
    }
}
