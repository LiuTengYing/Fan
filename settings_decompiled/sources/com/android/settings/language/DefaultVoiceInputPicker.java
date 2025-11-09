package com.android.settings.language;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settings.language.VoiceInputHelper;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class DefaultVoiceInputPicker extends DefaultAppPickerFragment {
    private VoiceInputHelper mHelper;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 844;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        VoiceInputHelper voiceInputHelper = new VoiceInputHelper(context);
        this.mHelper = voiceInputHelper;
        voiceInputHelper.buildUi();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.default_voice_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<VoiceInputDefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = getContext();
        Iterator<VoiceInputHelper.RecognizerInfo> it = this.mHelper.mAvailableRecognizerInfos.iterator();
        while (it.hasNext()) {
            arrayList.add(new VoiceInputDefaultAppInfo(context, this.mPm, this.mUserId, it.next(), true));
        }
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        ComponentName currentService = getCurrentService(this.mHelper);
        if (currentService == null) {
            return null;
        }
        return currentService.flattenToShortString();
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        Iterator<VoiceInputHelper.RecognizerInfo> it = this.mHelper.mAvailableRecognizerInfos.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (TextUtils.equals(str, it.next().mKey)) {
                Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", str);
                break;
            }
        }
        return true;
    }

    public static ComponentName getCurrentService(VoiceInputHelper voiceInputHelper) {
        return voiceInputHelper.mCurrentRecognizer;
    }

    /* loaded from: classes.dex */
    public static class VoiceInputDefaultAppInfo extends DefaultAppInfo {
        public VoiceInputHelper.BaseInfo mInfo;

        public VoiceInputDefaultAppInfo(Context context, PackageManager packageManager, int i, VoiceInputHelper.BaseInfo baseInfo, boolean z) {
            super(context, packageManager, i, baseInfo.mComponentName, (String) null, z);
            this.mInfo = baseInfo;
        }

        @Override // com.android.settingslib.applications.DefaultAppInfo, com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mInfo.mKey;
        }

        @Override // com.android.settingslib.applications.DefaultAppInfo, com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mInfo.mLabel;
        }

        public Intent getSettingIntent() {
            if (this.mInfo.mSettings == null) {
                return null;
            }
            return new Intent("android.intent.action.MAIN").setComponent(this.mInfo.mSettings);
        }
    }
}
