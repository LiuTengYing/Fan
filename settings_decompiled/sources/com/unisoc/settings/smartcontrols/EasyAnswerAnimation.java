package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class EasyAnswerAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static EasyAnswerAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        EasyAnswerAnimation easyAnswerAnimation = new EasyAnswerAnimation();
        mPreference = smartSwitchPreference;
        return easyAnswerAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.easy_answer, R$id.easy_answer_display, R$drawable.easy_answer_anim, "easy_answer");
    }
}
