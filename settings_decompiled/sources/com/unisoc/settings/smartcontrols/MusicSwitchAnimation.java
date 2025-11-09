package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class MusicSwitchAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static MusicSwitchAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        MusicSwitchAnimation musicSwitchAnimation = new MusicSwitchAnimation();
        mPreference = smartSwitchPreference;
        return musicSwitchAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.music_switch, R$id.music_switch_display, R$drawable.music_switch_anim, "music_switch");
    }
}
