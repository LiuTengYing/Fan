package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class LockMusicSwitchAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static LockMusicSwitchAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        LockMusicSwitchAnimation lockMusicSwitchAnimation = new LockMusicSwitchAnimation();
        mPreference = smartSwitchPreference;
        return lockMusicSwitchAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.lock_music_switch, R$id.lock_music_switch_display, R$drawable.lock_music_switch_anim, "lock_music_switch");
    }
}
