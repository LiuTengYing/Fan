package com.android.settings.development;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.SystemProperties;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class BluetoothMaxConnectedAudioDevicesPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String MAX_CONNECTED_AUDIO_DEVICES_PROPERTY = "persist.bluetooth.maxconnectedaudiodevices";
    private boolean entryValuesChanged;
    private int mDefaultMaxConnectedAudioDevices;
    private Map<Integer, String> mMaxConnectedNumberMap;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_max_connected_audio_devices";
    }

    public BluetoothMaxConnectedAudioDevicesPreferenceController(Context context) {
        super(context);
        this.mDefaultMaxConnectedAudioDevices = 0;
        this.mMaxConnectedNumberMap = new LinkedHashMap();
        this.entryValuesChanged = false;
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication("com.android.bluetooth");
            this.mDefaultMaxConnectedAudioDevices = resourcesForApplication.getInteger(resourcesForApplication.getIdentifier("config_bluetooth_max_connected_audio_devices", "integer", "com.android.bluetooth"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] stringArray = this.mContext.getResources().getStringArray(R$array.bluetooth_max_connected_audio_devices);
        String[] stringArray2 = this.mContext.getResources().getStringArray(R$array.bluetooth_max_connected_audio_devices_values);
        for (int i = 0; i < stringArray.length; i++) {
            this.mMaxConnectedNumberMap.put(Integer.valueOf(stringArray2[i].isEmpty() ? 0 : Integer.parseInt(stringArray2[i])), stringArray[i]);
        }
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ListPreference listPreference = (ListPreference) this.mPreference;
        if (this.mMaxConnectedNumberMap.keySet().removeIf(new Predicate() { // from class: com.android.settings.development.BluetoothMaxConnectedAudioDevicesPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$displayPreference$0;
                lambda$displayPreference$0 = BluetoothMaxConnectedAudioDevicesPreferenceController.this.lambda$displayPreference$0((Integer) obj);
                return lambda$displayPreference$0;
            }
        })) {
            CharSequence[] charSequenceArr = (CharSequence[]) this.mMaxConnectedNumberMap.values().stream().toArray(new IntFunction() { // from class: com.android.settings.development.BluetoothMaxConnectedAudioDevicesPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence[] lambda$displayPreference$1;
                    lambda$displayPreference$1 = BluetoothMaxConnectedAudioDevicesPreferenceController.lambda$displayPreference$1(i);
                    return lambda$displayPreference$1;
                }
            });
            charSequenceArr[0] = String.format(charSequenceArr[0].toString(), Integer.valueOf(this.mDefaultMaxConnectedAudioDevices));
            listPreference.setEntries(charSequenceArr);
            listPreference.setEntryValues((CharSequence[]) this.mMaxConnectedNumberMap.keySet().stream().map(new BluetoothMaxConnectedAudioDevicesPreferenceController$$ExternalSyntheticLambda2()).toArray(new IntFunction() { // from class: com.android.settings.development.BluetoothMaxConnectedAudioDevicesPreferenceController$$ExternalSyntheticLambda3
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence[] lambda$displayPreference$2;
                    lambda$displayPreference$2 = BluetoothMaxConnectedAudioDevicesPreferenceController.lambda$displayPreference$2(i);
                    return lambda$displayPreference$2;
                }
            }));
            this.entryValuesChanged = true;
            return;
        }
        CharSequence[] entries = listPreference.getEntries();
        entries[0] = String.format(entries[0].toString(), Integer.valueOf(this.mDefaultMaxConnectedAudioDevices));
        listPreference.setEntries(entries);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Integer num) {
        return num.intValue() > this.mDefaultMaxConnectedAudioDevices;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$displayPreference$1(int i) {
        return new CharSequence[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$displayPreference$2(int i) {
        return new CharSequence[i];
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String obj2 = obj.toString();
        if (((ListPreference) preference).findIndexOfValue(obj2) <= 0) {
            obj2 = "";
        }
        if (!obj2.isEmpty() && this.entryValuesChanged) {
            obj2 = String.valueOf(Integer.parseInt(obj2, 2));
        }
        SystemProperties.set(MAX_CONNECTED_AUDIO_DEVICES_PROPERTY, obj2);
        updateState(preference);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ListPreference listPreference = (ListPreference) preference;
        CharSequence[] entries = listPreference.getEntries();
        String str = SystemProperties.get(MAX_CONNECTED_AUDIO_DEVICES_PROPERTY);
        int i = 0;
        if (!str.isEmpty()) {
            int findIndexOfValue = listPreference.findIndexOfValue(String.valueOf(Integer.toBinaryString(Integer.valueOf(str).intValue())));
            if (findIndexOfValue < 0) {
                SystemProperties.set(MAX_CONNECTED_AUDIO_DEVICES_PROPERTY, "");
            } else {
                i = findIndexOfValue;
            }
        }
        listPreference.setValueIndex(i);
        listPreference.setSummary(entries[i]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchEnabled() {
        super.onDeveloperOptionsSwitchEnabled();
        updateState(this.mPreference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SystemProperties.set(MAX_CONNECTED_AUDIO_DEVICES_PROPERTY, "");
        updateState(this.mPreference);
    }
}
