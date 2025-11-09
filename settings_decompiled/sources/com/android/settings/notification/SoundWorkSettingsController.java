package com.android.settings.notification;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settings.DefaultRingtonePreference;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.notification.SoundWorkSettingsController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class SoundWorkSettingsController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    private final AudioHelper mHelper;
    private int mManagedProfileId;
    private final BroadcastReceiver mManagedProfileReceiver;
    private final SoundWorkSettings mParent;
    private PreferenceScreen mScreen;
    private final UserManager mUserManager;
    private final boolean mVoiceCapable;
    private Preference mWorkAlarmRingtonePreference;
    private Preference mWorkNotificationRingtonePreference;
    private Preference mWorkPhoneRingtonePreference;
    private TwoStatePreference mWorkUsePersonalSounds;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    public SoundWorkSettingsController(Context context, SoundWorkSettings soundWorkSettings, Lifecycle lifecycle) {
        this(context, soundWorkSettings, lifecycle, new AudioHelper(context));
    }

    SoundWorkSettingsController(Context context, SoundWorkSettings soundWorkSettings, Lifecycle lifecycle, AudioHelper audioHelper) {
        super(context);
        this.mManagedProfileReceiver = new BroadcastReceiver() { // from class: com.android.settings.notification.SoundWorkSettingsController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int identifier = ((UserHandle) intent.getExtra("android.intent.extra.USER")).getIdentifier();
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                    SoundWorkSettingsController.this.onManagedProfileAdded(identifier);
                } else if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                    SoundWorkSettingsController.this.onManagedProfileRemoved(identifier);
                }
            }
        };
        this.mUserManager = UserManager.get(context);
        this.mVoiceCapable = Utils.isVoiceCapable(this.mContext);
        this.mParent = soundWorkSettings;
        this.mHelper = audioHelper;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        this.mContext.registerReceiver(this.mManagedProfileReceiver, intentFilter);
        this.mManagedProfileId = this.mHelper.getManagedProfileId(this.mUserManager);
        updateWorkPreferences();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mManagedProfileReceiver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mHelper.getManagedProfileId(this.mUserManager) != -10000 && shouldShowRingtoneSettings();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int i;
        if ("work_ringtone".equals(preference.getKey())) {
            i = 1;
        } else if (!"work_notification_ringtone".equals(preference.getKey())) {
            if ("work_alarm_ringtone".equals(preference.getKey())) {
                i = 4;
            }
            return true;
        } else {
            i = 2;
        }
        preference.setSummary(updateRingtoneName(getManagedProfileContext(), i));
        return true;
    }

    private boolean shouldShowRingtoneSettings() {
        return !this.mHelper.isSingleVolume();
    }

    private CharSequence updateRingtoneName(Context context, int i) {
        if (context == null || !this.mHelper.isUserUnlocked(this.mUserManager, context.getUserId())) {
            return this.mContext.getString(R$string.managed_profile_not_available_label);
        }
        return Ringtone.getTitle(context, RingtoneManager.getActualDefaultRingtoneUri(context, i), false, true);
    }

    private Context getManagedProfileContext() {
        int i = this.mManagedProfileId;
        if (i == -10000) {
            return null;
        }
        return this.mHelper.createPackageContextAsUser(i);
    }

    private DefaultRingtonePreference initWorkPreference(PreferenceGroup preferenceGroup, String str) {
        DefaultRingtonePreference defaultRingtonePreference = (DefaultRingtonePreference) preferenceGroup.findPreference(str);
        defaultRingtonePreference.setOnPreferenceChangeListener(this);
        defaultRingtonePreference.setUserId(this.mManagedProfileId);
        return defaultRingtonePreference;
    }

    private void updateWorkPreferences() {
        if (isAvailable()) {
            if (this.mWorkUsePersonalSounds == null) {
                TwoStatePreference twoStatePreference = (TwoStatePreference) this.mScreen.findPreference("work_use_personal_sounds");
                this.mWorkUsePersonalSounds = twoStatePreference;
                twoStatePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.SoundWorkSettingsController$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$updateWorkPreferences$0;
                        lambda$updateWorkPreferences$0 = SoundWorkSettingsController.this.lambda$updateWorkPreferences$0(preference, obj);
                        return lambda$updateWorkPreferences$0;
                    }
                });
            }
            if (this.mWorkPhoneRingtonePreference == null) {
                this.mWorkPhoneRingtonePreference = initWorkPreference(this.mScreen, "work_ringtone");
            }
            if (this.mWorkNotificationRingtonePreference == null) {
                this.mWorkNotificationRingtonePreference = initWorkPreference(this.mScreen, "work_notification_ringtone");
            }
            if (this.mWorkAlarmRingtonePreference == null) {
                this.mWorkAlarmRingtonePreference = initWorkPreference(this.mScreen, "work_alarm_ringtone");
            }
            if (!this.mVoiceCapable) {
                this.mWorkPhoneRingtonePreference.setVisible(false);
                this.mWorkPhoneRingtonePreference = null;
            }
            if (Settings.Secure.getIntForUser(getManagedProfileContext().getContentResolver(), "sync_parent_sounds", 0, this.mManagedProfileId) == 1) {
                enableWorkSyncSettings();
            } else {
                disableWorkSyncSettings();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWorkPreferences$0(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            UnifyWorkDialogFragment.show(this.mParent);
            return false;
        }
        disableWorkSync();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableWorkSync() {
        Settings.Secure.putIntForUser(getManagedProfileContext().getContentResolver(), "sync_parent_sounds", 1, this.mManagedProfileId);
        enableWorkSyncSettings();
    }

    private void enableWorkSyncSettings() {
        this.mWorkUsePersonalSounds.setChecked(true);
        String string = ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.WORK_PROFILE_SYNC_WITH_PERSONAL_SOUNDS_ACTIVE_SUMMARY", new Supplier() { // from class: com.android.settings.notification.SoundWorkSettingsController$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$enableWorkSyncSettings$1;
                lambda$enableWorkSyncSettings$1 = SoundWorkSettingsController.this.lambda$enableWorkSyncSettings$1();
                return lambda$enableWorkSyncSettings$1;
            }
        });
        Preference preference = this.mWorkPhoneRingtonePreference;
        if (preference != null) {
            preference.setSummary(string);
        }
        this.mWorkNotificationRingtonePreference.setSummary(string);
        this.mWorkAlarmRingtonePreference.setSummary(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$enableWorkSyncSettings$1() {
        return this.mContext.getString(R$string.work_sound_same_as_personal);
    }

    private void disableWorkSync() {
        Settings.Secure.putIntForUser(getManagedProfileContext().getContentResolver(), "sync_parent_sounds", 0, this.mManagedProfileId);
        disableWorkSyncSettings();
    }

    private void disableWorkSyncSettings() {
        Preference preference = this.mWorkPhoneRingtonePreference;
        if (preference != null) {
            preference.setEnabled(true);
        }
        this.mWorkNotificationRingtonePreference.setEnabled(true);
        this.mWorkAlarmRingtonePreference.setEnabled(true);
        updateWorkRingtoneSummaries();
    }

    private void updateWorkRingtoneSummaries() {
        Context managedProfileContext = getManagedProfileContext();
        Preference preference = this.mWorkPhoneRingtonePreference;
        if (preference != null) {
            preference.setSummary(updateRingtoneName(managedProfileContext, 1));
        }
        this.mWorkNotificationRingtonePreference.setSummary(updateRingtoneName(managedProfileContext, 2));
        this.mWorkAlarmRingtonePreference.setSummary(updateRingtoneName(managedProfileContext, 4));
    }

    public void onManagedProfileAdded(int i) {
        if (this.mManagedProfileId == -10000) {
            this.mManagedProfileId = i;
            updateWorkPreferences();
        }
    }

    public void onManagedProfileRemoved(int i) {
        if (this.mManagedProfileId == i) {
            this.mManagedProfileId = this.mHelper.getManagedProfileId(this.mUserManager);
            updateWorkPreferences();
        }
    }

    /* loaded from: classes.dex */
    public static class UnifyWorkDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 553;
        }

        public static void show(SoundWorkSettings soundWorkSettings) {
            FragmentManager fragmentManager = soundWorkSettings.getFragmentManager();
            if (fragmentManager.findFragmentByTag("UnifyWorkDialogFragment") == null) {
                UnifyWorkDialogFragment unifyWorkDialogFragment = new UnifyWorkDialogFragment();
                unifyWorkDialogFragment.setTargetFragment(soundWorkSettings, 200);
                unifyWorkDialogFragment.show(fragmentManager, "UnifyWorkDialogFragment");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final Context applicationContext = getActivity().getApplicationContext();
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) applicationContext.getSystemService(DevicePolicyManager.class);
            return new AlertDialog.Builder(getActivity()).setTitle(devicePolicyManager.getResources().getString("Settings.ENABLE_WORK_PROFILE_SYNC_WITH_PERSONAL_SOUNDS_DIALOG_TITLE", new Supplier() { // from class: com.android.settings.notification.SoundWorkSettingsController$UnifyWorkDialogFragment$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreateDialog$0;
                    lambda$onCreateDialog$0 = SoundWorkSettingsController.UnifyWorkDialogFragment.lambda$onCreateDialog$0(applicationContext);
                    return lambda$onCreateDialog$0;
                }
            })).setMessage(devicePolicyManager.getResources().getString("Settings.ENABLE_WORK_PROFILE_SYNC_WITH_PERSONAL_SOUNDS_DIALOG_MESSAGE", new Supplier() { // from class: com.android.settings.notification.SoundWorkSettingsController$UnifyWorkDialogFragment$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreateDialog$1;
                    lambda$onCreateDialog$1 = SoundWorkSettingsController.UnifyWorkDialogFragment.lambda$onCreateDialog$1(applicationContext);
                    return lambda$onCreateDialog$1;
                }
            })).setPositiveButton(R$string.work_sync_dialog_yes, this).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).create();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ String lambda$onCreateDialog$0(Context context) {
            return context.getString(R$string.work_sync_dialog_title);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ String lambda$onCreateDialog$1(Context context) {
            return context.getString(R$string.work_sync_dialog_message);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            SoundWorkSettings soundWorkSettings = (SoundWorkSettings) getTargetFragment();
            if (soundWorkSettings.isAdded()) {
                soundWorkSettings.enableWorkSync();
            }
        }
    }
}
