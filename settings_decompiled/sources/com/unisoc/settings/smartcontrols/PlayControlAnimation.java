package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class PlayControlAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static PlayControlAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        PlayControlAnimation playControlAnimation = new PlayControlAnimation();
        mPreference = smartSwitchPreference;
        return playControlAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.play_control, R$id.play_control_display, R$drawable.play_control_anim, "play_control");
    }
}
