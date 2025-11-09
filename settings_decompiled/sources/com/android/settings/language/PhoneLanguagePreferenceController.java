package com.android.settings.language;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.localepicker.LocaleListEditor;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;
/* loaded from: classes.dex */
public class PhoneLanguagePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "phone_language";
    }

    public PhoneLanguagePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_phone_language) && this.mContext.getAssets().getLocales().length > 1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        preference.setSummary(FeatureFactory.getFactory(this.mContext).getLocaleFeatureProvider().getLocaleNames());
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateNonIndexableKeys(List<String> list) {
        list.add(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ("phone_language".equals(preference.getKey())) {
            new SubSettingLauncher(this.mContext).setDestination(LocaleListEditor.class.getName()).setSourceMetricsCategory(750).setTitleRes(R$string.language_picker_title).launch();
            return true;
        }
        return false;
    }
}
