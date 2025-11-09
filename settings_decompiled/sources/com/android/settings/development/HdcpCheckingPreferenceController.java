package com.android.settings.development;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.SystemPropPoker;
/* loaded from: classes.dex */
public class HdcpCheckingPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String HDCP_CHECKING_PROPERTY = "persist.sys.hdcp_checking";
    static final String USER_BUILD_TYPE = "user";
    private final String[] mListSummaries;
    private final String[] mListValues;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "hdcp_checking";
    }

    public HdcpCheckingPreferenceController(Context context) {
        super(context);
        this.mListValues = this.mContext.getResources().getStringArray(R$array.hdcp_checking_values);
        this.mListSummaries = this.mContext.getResources().getStringArray(R$array.hdcp_checking_summaries);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !TextUtils.equals(USER_BUILD_TYPE, getBuildType());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SystemProperties.set(HDCP_CHECKING_PROPERTY, obj.toString());
        updateHdcpValues((ListPreference) this.mPreference);
        SystemPropPoker.getInstance().poke();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateHdcpValues((ListPreference) this.mPreference);
    }

    private void updateHdcpValues(ListPreference listPreference) {
        String str = SystemProperties.get(HDCP_CHECKING_PROPERTY);
        int i = 0;
        while (true) {
            String[] strArr = this.mListValues;
            if (i >= strArr.length) {
                i = 1;
                break;
            } else if (TextUtils.equals(str, strArr[i])) {
                break;
            } else {
                i++;
            }
        }
        listPreference.setValue(this.mListValues[i]);
        listPreference.setSummary(this.mListSummaries[i]);
    }

    public String getBuildType() {
        return Build.TYPE;
    }
}
