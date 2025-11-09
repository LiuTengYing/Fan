package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Switch;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.development.RebootConfirmationDialogFragment;
import com.android.settings.development.RebootConfirmationDialogHost;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.widget.VideoPreference;
import com.android.settings.widget.VideoPreferenceController;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class FreeformWindowsSettings extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, RebootConfirmationDialogHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.applications.FreeformWindowsSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.freeform_windows_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    final String KEY_FOOTER = "freeform_windows_footer";
    final String KEY_INTRO = "freeform_windows_intro";
    final String KEY_VIDEO = "freeform_windows_video";
    private FooterPreference mFooterPreference;
    private FreeformWindowsTopIntroPreferenceController mFreeformWindowsTopIntroPreferenceController;
    private SettingsMainSwitchBar mSwitchBar;
    private VideoPreferenceController mVideoPreferenceController;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1848;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        settingsActivity.setTitle(R$string.freeform_windows_title);
        addPreferencesFromResource(R$xml.freeform_windows_settings);
        this.mFreeformWindowsTopIntroPreferenceController = new FreeformWindowsTopIntroPreferenceController(settingsActivity, "freeform_windows_intro");
        this.mVideoPreferenceController = new VideoPreferenceController(settingsActivity, "freeform_windows_video");
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mFooterPreference = (FooterPreference) preferenceScreen.findPreference("freeform_windows_footer");
        this.mFreeformWindowsTopIntroPreferenceController.displayPreference(preferenceScreen);
        this.mVideoPreferenceController.displayPreference(preferenceScreen);
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R$string.freeform_windows_main_switch_title));
        this.mSwitchBar.show();
        this.mSwitchBar.setChecked(isFreeformWindowsEnabled(getActivity()));
        this.mSwitchBar.addOnSwitchChangeListener(this);
        updateUI();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.removeOnSwitchChangeListener(this);
        this.mSwitchBar.hide();
    }

    private static boolean isFreeformWindowsEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "enable_freeform_support", 0) != 0;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        Settings.Global.putInt(getContentResolver(), "enable_freeform_support", z ? 1 : 0);
        updateUI();
        if (z) {
            RebootConfirmationDialogFragment.show(this, R$string.reboot_dialog_enable_freeform_support, this);
        }
    }

    @Override // com.android.settings.development.RebootConfirmationDialogHost
    public void onRebootConfirmed() {
        getActivity().startActivity(new Intent("android.intent.action.REBOOT"));
    }

    private void updateUI() {
        this.mFreeformWindowsTopIntroPreferenceController.updateState(getPreferenceScreen().findPreference(this.mFreeformWindowsTopIntroPreferenceController.getPreferenceKey()));
        ((VideoPreference) getPreferenceScreen().findPreference(this.mVideoPreferenceController.getPreferenceKey())).setVisible(isFreeformWindowsEnabled(getActivity()));
        this.mFooterPreference.setVisible(isFreeformWindowsEnabled(getActivity()));
    }
}
