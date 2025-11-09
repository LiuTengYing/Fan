package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ChannelNotificationSettings extends NotificationSettings {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ChannelSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 265;
    }

    @Override // com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Bundle arguments = getArguments();
        if (preferenceScreen == null || arguments == null || arguments.getBoolean("fromSettings", false)) {
            return;
        }
        preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
    }

    @Override // com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || this.mChannel == null) {
            Log.w("ChannelSettings", "Missing package or uid or packageinfo or channel");
            finish();
            return;
        }
        getActivity().setTitle(this.mChannel.getName());
        if (!TextUtils.isEmpty(this.mChannel.getConversationId()) && !this.mChannel.isDemoted()) {
            Intent intent = new SubSettingLauncher(this.mContext).setDestination(ConversationNotificationSettings.class.getName()).setArguments(getArguments()).setExtras(getIntent() != null ? getIntent().getExtras() : null).setSourceMetricsCategory(265).toIntent();
            if (this.mPreferenceFilter != null) {
                intent.setClass(this.mContext, ChannelPanelActivity.class);
            }
            startActivity(intent);
            finish();
            return;
        }
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            notificationPreferenceController.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, null, null, this.mSuspendedAppsAdmin, this.mPreferenceFilter);
            notificationPreferenceController.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
        animatePanel();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            if (notificationPreferenceController instanceof PreferenceManager.OnActivityResultListener) {
                ((PreferenceManager.OnActivityResultListener) notificationPreferenceController).onActivityResult(i, i2, intent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.channel_notification_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((NotificationSettings) this).mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        ((NotificationSettings) this).mControllers.add(new BlockPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new ImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new MinImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new HighImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AllowSoundPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new SoundPreferenceController(context, this, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new VibrationPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AppLinkPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new VisibilityPreferenceController(context, new LockPatternUtils(context), this.mBackend));
        ((NotificationSettings) this).mControllers.add(new LightsPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new BadgePreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new DndPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new NotificationsOffPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new ConversationPromotePreferenceController(context, this, this.mBackend));
        return new ArrayList(((NotificationSettings) this).mControllers);
    }
}
