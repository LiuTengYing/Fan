package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Switch;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class SmartMotionFragment extends DashboardFragment implements OnMainSwitchChangeListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.unisoc.settings.smartcontrols.SmartMotionFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (UserHandle.myUserId() == 0 && Utils.isSupportSmartControl(context)) {
                SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
                searchIndexableResource.xmlResId = R$xml.smart_motion;
                arrayList.add(searchIndexableResource);
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> getPreferenceControllers(Context context) {
            return SmartMotionFragment.buildPreferenceControllers(context, null);
        }
    };
    private static EasyAnswerPreferenceController mEasyAnswerPreferenceController;
    private static EasyBellPreferenceController mEasyBellPreferenceController;
    private static EasyDialPreferenceController mEasyDialPreferenceController;
    private static HandsfreeSwitchPreferenceController mHandsfreeSwitchPreferenceController;
    private static LockMusicSwitchPreferenceController mLockMusicSwitchPreferenceController;
    private static MusicSwitchPreferenceController mMusicSwitchPreferenceController;
    private static MuteAlarmsPreferenceController mMuteAlarmsPreferenceController;
    private static MuteIncomingCallsPreferenceController mMuteIncomingCallsPreferenceController;
    private static PlayControlPreferenceController mPlayControlPreferenceController;
    private static QuickBrowsePreferenceController mQuickBrowsePreferenceController;
    private static SmartCallRecorderPreferenceController mSmartCallRecorderPreferenceController;
    private PreferenceGroup mMoreCategory;
    private PreferenceGroup mSmartCallCategory;
    private PreferenceGroup mSmartPlayCategory;
    private SettingsMainSwitchBar mSwitchBar;
    private boolean mValidListener = false;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SmartMotionFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50952;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsMainSwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R$string.smart_motion_switch_title));
        this.mSwitchBar.show();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!this.mValidListener) {
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
        this.mSwitchBar.setChecked(isSmartMotionEnabled(getActivity()));
        getPreferenceScreen().setEnabled(isSmartMotionEnabled(getActivity()));
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mValidListener) {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mValidListener = false;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.hide();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.smart_motion;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        EasyDialPreferenceController easyDialPreferenceController = new EasyDialPreferenceController(context, fragment);
        mEasyDialPreferenceController = easyDialPreferenceController;
        arrayList.add(easyDialPreferenceController);
        EasyAnswerPreferenceController easyAnswerPreferenceController = new EasyAnswerPreferenceController(context, fragment);
        mEasyAnswerPreferenceController = easyAnswerPreferenceController;
        arrayList.add(easyAnswerPreferenceController);
        HandsfreeSwitchPreferenceController handsfreeSwitchPreferenceController = new HandsfreeSwitchPreferenceController(context, fragment);
        mHandsfreeSwitchPreferenceController = handsfreeSwitchPreferenceController;
        arrayList.add(handsfreeSwitchPreferenceController);
        SmartCallRecorderPreferenceController smartCallRecorderPreferenceController = new SmartCallRecorderPreferenceController(context, fragment);
        mSmartCallRecorderPreferenceController = smartCallRecorderPreferenceController;
        arrayList.add(smartCallRecorderPreferenceController);
        EasyBellPreferenceController easyBellPreferenceController = new EasyBellPreferenceController(context, fragment);
        mEasyBellPreferenceController = easyBellPreferenceController;
        arrayList.add(easyBellPreferenceController);
        MuteIncomingCallsPreferenceController muteIncomingCallsPreferenceController = new MuteIncomingCallsPreferenceController(context, fragment);
        mMuteIncomingCallsPreferenceController = muteIncomingCallsPreferenceController;
        arrayList.add(muteIncomingCallsPreferenceController);
        PlayControlPreferenceController playControlPreferenceController = new PlayControlPreferenceController(context, fragment);
        mPlayControlPreferenceController = playControlPreferenceController;
        arrayList.add(playControlPreferenceController);
        MusicSwitchPreferenceController musicSwitchPreferenceController = new MusicSwitchPreferenceController(context, fragment);
        mMusicSwitchPreferenceController = musicSwitchPreferenceController;
        arrayList.add(musicSwitchPreferenceController);
        LockMusicSwitchPreferenceController lockMusicSwitchPreferenceController = new LockMusicSwitchPreferenceController(context, fragment);
        mLockMusicSwitchPreferenceController = lockMusicSwitchPreferenceController;
        arrayList.add(lockMusicSwitchPreferenceController);
        MuteAlarmsPreferenceController muteAlarmsPreferenceController = new MuteAlarmsPreferenceController(context, fragment);
        mMuteAlarmsPreferenceController = muteAlarmsPreferenceController;
        arrayList.add(muteAlarmsPreferenceController);
        QuickBrowsePreferenceController quickBrowsePreferenceController = new QuickBrowsePreferenceController(context, fragment);
        mQuickBrowsePreferenceController = quickBrowsePreferenceController;
        arrayList.add(quickBrowsePreferenceController);
        return arrayList;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        Settings.Global.putInt(getContentResolver(), "smart_motion_enabled", z ? 1 : 0);
        getPreferenceScreen().setEnabled(z);
        mEasyDialPreferenceController.updateOnSmartMotionChange(z);
        mEasyAnswerPreferenceController.updateOnSmartMotionChange(z);
        mHandsfreeSwitchPreferenceController.updateOnSmartMotionChange(z);
        mSmartCallRecorderPreferenceController.updateOnSmartMotionChange(z);
        mEasyBellPreferenceController.updateOnSmartMotionChange(z);
        mMuteIncomingCallsPreferenceController.updateOnSmartMotionChange(z);
        mPlayControlPreferenceController.updateOnSmartMotionChange(z);
        mMusicSwitchPreferenceController.updateOnSmartMotionChange(z);
        mLockMusicSwitchPreferenceController.updateOnSmartMotionChange(z);
        mMuteAlarmsPreferenceController.updateOnSmartMotionChange(z);
        mQuickBrowsePreferenceController.updateOnSmartMotionChange(z);
    }

    private void initializeAllPreferences() {
        this.mSmartCallCategory = (PreferenceGroup) findPreference("smart_call");
        this.mSmartPlayCategory = (PreferenceGroup) findPreference("smart_play");
        this.mMoreCategory = (PreferenceGroup) findPreference("more");
        if (!mEasyDialPreferenceController.isAvailable() && !mEasyAnswerPreferenceController.isAvailable() && !mHandsfreeSwitchPreferenceController.isAvailable() && !mSmartCallRecorderPreferenceController.isAvailable() && !mEasyBellPreferenceController.isAvailable() && !mMuteIncomingCallsPreferenceController.isAvailable() && this.mSmartCallCategory != null) {
            getPreferenceScreen().removePreference(this.mSmartCallCategory);
        }
        if (!mPlayControlPreferenceController.isAvailable() && !mMusicSwitchPreferenceController.isAvailable() && !mLockMusicSwitchPreferenceController.isAvailable() && this.mSmartPlayCategory != null) {
            getPreferenceScreen().removePreference(this.mSmartPlayCategory);
        }
        if (mMuteAlarmsPreferenceController.isAvailable() || mQuickBrowsePreferenceController.isAvailable() || this.mMoreCategory == null) {
            return;
        }
        getPreferenceScreen().removePreference(this.mMoreCategory);
    }

    public static final boolean isSmartMotionEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "smart_motion_enabled", 0) == 1;
    }
}
