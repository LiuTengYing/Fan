package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.preference.SeekBarVolumizer;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.VolumeSeekBarPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.sound.AudioSwitchPreferenceController;
import com.android.settings.sound.HandsFreeProfileOutputPreferenceController;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.UpdatableListPreferenceDialogFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class SoundSettings extends DashboardFragment implements OnActivityResultListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.sound_settings) { // from class: com.android.settings.notification.SoundSettings.2
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SoundSettings.buildPreferenceControllers(context, null, null);
        }
    };
    static final int STOP_SAMPLE = 1;
    private UpdatableListPreferenceDialogFragment mDialogFragment;
    private String mHfpOutputControllerKey;
    private RingtonePreference mRequestPreference;
    final VolumePreferenceCallback mVolumeCallback = new VolumePreferenceCallback();
    final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.notification.SoundSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            SoundSettings.this.mVolumeCallback.stopSample();
        }
    };
    private String mVibrationPreferencesKey = "vibration_preference_screen";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SoundSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 336;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            String string = bundle.getString("selected_preference", null);
            if (!TextUtils.isEmpty(string)) {
                this.mRequestPreference = (RingtonePreference) findPreference(string);
            }
            this.mDialogFragment = (UpdatableListPreferenceDialogFragment) getFragmentManager().findFragmentByTag("SoundSettings");
        }
        replaceEnterpriseStringTitle("sound_work_settings", "Settings.WORK_PROFILE_SOUND_SETTINGS_SECTION_HEADER", R$string.sound_work_settings);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_sound;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mVolumeCallback.stopSample();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RingtonePreference) {
            writePreferenceClickMetric(preference);
            RingtonePreference ringtonePreference = (RingtonePreference) preference;
            this.mRequestPreference = ringtonePreference;
            ringtonePreference.onPrepareRingtonePickerIntent(ringtonePreference.getIntent());
            getActivity().startActivityForResultAsUser(this.mRequestPreference.getIntent(), 200, null, UserHandle.of(this.mRequestPreference.getUserId()));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        if (TextUtils.equals(this.mVibrationPreferencesKey, preference.getKey())) {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        UpdatableListPreferenceDialogFragment newInstance = UpdatableListPreferenceDialogFragment.newInstance(preference.getKey(), this.mHfpOutputControllerKey.equals(preference.getKey()) ? 1416 : 0);
        this.mDialogFragment = newInstance;
        newInstance.setTargetFragment(this, 0);
        this.mDialogFragment.show(getFragmentManager(), "SoundSettings");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.sound_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            ringtonePreference.onActivityResult(i, i2, intent);
            this.mRequestPreference = null;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            bundle.putString("selected_preference", ringtonePreference.getKey());
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ArrayList arrayList = new ArrayList();
        arrayList.add((VolumeSeekBarPreferenceController) use(AlarmVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(MediaVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(RingVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(NotificationVolumePreferenceController.class));
        arrayList.add((VolumeSeekBarPreferenceController) use(CallVolumePreferenceController.class));
        ((HandsFreeProfileOutputPreferenceController) use(HandsFreeProfileOutputPreferenceController.class)).setCallback(new AudioSwitchPreferenceController.AudioSwitchCallback() { // from class: com.android.settings.notification.SoundSettings$$ExternalSyntheticLambda0
            @Override // com.android.settings.sound.AudioSwitchPreferenceController.AudioSwitchCallback
            public final void onPreferenceDataChanged(ListPreference listPreference) {
                SoundSettings.this.lambda$onAttach$0(listPreference);
            }
        });
        this.mHfpOutputControllerKey = ((HandsFreeProfileOutputPreferenceController) use(HandsFreeProfileOutputPreferenceController.class)).getPreferenceKey();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            VolumeSeekBarPreferenceController volumeSeekBarPreferenceController = (VolumeSeekBarPreferenceController) it.next();
            volumeSeekBarPreferenceController.setCallback(this.mVolumeCallback);
            getSettingsLifecycle().addObserver(volumeSeekBarPreferenceController);
        }
    }

    /* loaded from: classes.dex */
    final class VolumePreferenceCallback implements VolumeSeekBarPreference.Callback {
        private SeekBarVolumizer mCurrent;

        VolumePreferenceCallback() {
        }

        @Override // com.android.settings.notification.VolumeSeekBarPreference.Callback
        public void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
            if (this.mCurrent != null) {
                SoundSettings.this.mHandler.removeMessages(1);
                SoundSettings.this.mHandler.sendEmptyMessageDelayed(1, 2000L);
            }
        }

        @Override // com.android.settings.notification.VolumeSeekBarPreference.Callback
        public void onStreamValueChanged(int i, int i2) {
            if (this.mCurrent != null) {
                SoundSettings.this.mHandler.removeMessages(1);
                SoundSettings.this.mHandler.sendEmptyMessageDelayed(1, 2000L);
            }
        }

        @Override // com.android.settings.notification.VolumeSeekBarPreference.Callback
        public void onStartTrackingTouch(SeekBarVolumizer seekBarVolumizer) {
            SeekBarVolumizer seekBarVolumizer2 = this.mCurrent;
            if (seekBarVolumizer2 != null && seekBarVolumizer2 != seekBarVolumizer) {
                seekBarVolumizer2.stopSample();
            }
            this.mCurrent = seekBarVolumizer;
        }

        public void stopSample() {
            SeekBarVolumizer seekBarVolumizer = this.mCurrent;
            if (seekBarVolumizer != null) {
                seekBarVolumizer.stopSample();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SoundSettings soundSettings, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AlarmRingtonePreferenceController(context));
        arrayList.add(new NotificationRingtonePreferenceController(context));
        arrayList.add(new PhoneRingtonePreferenceController(context, "phone_ringtone"));
        arrayList.add(new PhoneRingtonePreferenceController(context, "phone_ringtone1"));
        arrayList.add(new PhoneRingtonePreferenceController(context, "phone_ringtone2"));
        DialPadTonePreferenceController dialPadTonePreferenceController = new DialPadTonePreferenceController(context, soundSettings, lifecycle);
        ScreenLockSoundPreferenceController screenLockSoundPreferenceController = new ScreenLockSoundPreferenceController(context, soundSettings, lifecycle);
        ChargingSoundPreferenceController chargingSoundPreferenceController = new ChargingSoundPreferenceController(context, soundSettings, lifecycle);
        DockingSoundPreferenceController dockingSoundPreferenceController = new DockingSoundPreferenceController(context, soundSettings, lifecycle);
        TouchSoundPreferenceController touchSoundPreferenceController = new TouchSoundPreferenceController(context, soundSettings, lifecycle);
        DockAudioMediaPreferenceController dockAudioMediaPreferenceController = new DockAudioMediaPreferenceController(context, soundSettings, lifecycle);
        BootSoundPreferenceController bootSoundPreferenceController = new BootSoundPreferenceController(context);
        EmergencyTonePreferenceController emergencyTonePreferenceController = new EmergencyTonePreferenceController(context, soundSettings, lifecycle);
        VibrateIconPreferenceController vibrateIconPreferenceController = new VibrateIconPreferenceController(context, soundSettings, lifecycle);
        arrayList.add(dialPadTonePreferenceController);
        arrayList.add(screenLockSoundPreferenceController);
        arrayList.add(chargingSoundPreferenceController);
        arrayList.add(dockingSoundPreferenceController);
        arrayList.add(touchSoundPreferenceController);
        arrayList.add(vibrateIconPreferenceController);
        arrayList.add(dockAudioMediaPreferenceController);
        arrayList.add(bootSoundPreferenceController);
        arrayList.add(emergencyTonePreferenceController);
        arrayList.add(new PreferenceCategoryController(context, "other_sounds_and_vibrations_category").setChildren(Arrays.asList(dialPadTonePreferenceController, screenLockSoundPreferenceController, chargingSoundPreferenceController, dockingSoundPreferenceController, touchSoundPreferenceController, vibrateIconPreferenceController, dockAudioMediaPreferenceController, bootSoundPreferenceController, emergencyTonePreferenceController)));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onPreferenceDataChanged */
    public void lambda$onAttach$0(ListPreference listPreference) {
        UpdatableListPreferenceDialogFragment updatableListPreferenceDialogFragment = this.mDialogFragment;
        if (updatableListPreferenceDialogFragment != null) {
            updatableListPreferenceDialogFragment.onListPreferenceUpdated(listPreference);
        }
    }
}
