package com.android.settings.users;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.R$array;
import com.android.settings.R$xml;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class TimeoutToUserZeroSettings extends RadioButtonPickerFragment {
    private String[] mEntries;
    private String[] mValues;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1916;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.user_timeout_to_user_zero_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mEntries = getContext().getResources().getStringArray(R$array.switch_to_user_zero_when_docked_timeout_entries);
        this.mValues = getContext().getResources().getStringArray(R$array.switch_to_user_zero_when_docked_timeout_values);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        if (this.mEntries != null && this.mValues != null) {
            int i = 0;
            while (true) {
                String[] strArr = this.mValues;
                if (i >= strArr.length) {
                    break;
                }
                arrayList.add(new TimeoutCandidateInfo(this.mEntries[i], strArr[i], true));
                i++;
            }
        }
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        String stringForUser = Settings.Secure.getStringForUser(getContext().getContentResolver(), "timeout_to_user_zero", UserHandle.myUserId());
        return stringForUser != null ? stringForUser : this.mValues[0];
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        Settings.Secure.putStringForUser(getContext().getContentResolver(), "timeout_to_user_zero", str, UserHandle.myUserId());
        return true;
    }

    /* loaded from: classes.dex */
    private static class TimeoutCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        TimeoutCandidateInfo(CharSequence charSequence, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mKey = str;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mLabel;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }
    }
}
