package com.android.settings.volume.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
/* loaded from: classes.dex */
public class SoundSpeedVolumeDialogFragment extends InstrumentedDialogFragment {
    private static int speedSound;
    private RelativeLayout mRlClose;
    private RelativeLayout mRlSpeedHigh;
    private RelativeLayout mRlSpeedLow;
    private RelativeLayout mRlSpeedMid;
    private View mRootView;
    private CheckBox mSpeedClose;
    private CheckBox mSpeedHigh;
    private CheckBox mSpeedLow;
    private CheckBox mSpeedMiddle;
    private ViewGroup.LayoutParams params;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, int i) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("SoundSpeedVolumeDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            SoundSpeedVolumeDialogFragment soundSpeedVolumeDialogFragment = new SoundSpeedVolumeDialogFragment();
            speedSound = i;
            soundSpeedVolumeDialogFragment.setArguments(bundle);
            soundSpeedVolumeDialogFragment.show(childFragmentManager, "SoundSpeedVolumeDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.sound_speed_volume_dialog_layout, null);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, SystemProperties.getInt("persist.syu.thememode", 2) == 2 ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setTitle(string);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        setTitle(string);
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mSpeedClose = (CheckBox) this.mRootView.findViewById(R$id.sound_speed_close);
        this.mSpeedLow = (CheckBox) this.mRootView.findViewById(R$id.sound_speed_low);
        this.mSpeedMiddle = (CheckBox) this.mRootView.findViewById(R$id.sound_speed_middle);
        this.mSpeedHigh = (CheckBox) this.mRootView.findViewById(R$id.sound_speed_high);
        this.mRlClose = (RelativeLayout) this.mRootView.findViewById(R$id.sound_speed_close_layout);
        this.mRlSpeedLow = (RelativeLayout) this.mRootView.findViewById(R$id.sound_speed_low_layout);
        this.mRlSpeedMid = (RelativeLayout) this.mRootView.findViewById(R$id.sound_speed_middle_layout);
        this.mRlSpeedHigh = (RelativeLayout) this.mRootView.findViewById(R$id.sound_speed_high_layout);
        initData();
        initListener();
    }

    private void initData() {
        int i = speedSound;
        if (i == 3) {
            setItemCheck(1);
        } else if (i == 5) {
            setItemCheck(2);
        } else if (i == 7) {
            setItemCheck(3);
        } else {
            setItemCheck(0);
        }
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.sound_speed_volume_title)).setText(str);
    }

    private void initListener() {
        this.mRlClose.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.volume.dialog.SoundSpeedVolumeDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SoundSpeedVolumeDialogFragment.this.setItemCheck(0);
                SoundSpeedVolumeDialogFragment.this.setCmd(0);
            }
        });
        this.mRlSpeedLow.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.volume.dialog.SoundSpeedVolumeDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SoundSpeedVolumeDialogFragment.this.setItemCheck(1);
                SoundSpeedVolumeDialogFragment.this.setCmd(3);
            }
        });
        this.mRlSpeedMid.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.volume.dialog.SoundSpeedVolumeDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SoundSpeedVolumeDialogFragment.this.setItemCheck(2);
                SoundSpeedVolumeDialogFragment.this.setCmd(5);
            }
        });
        this.mRlSpeedHigh.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.volume.dialog.SoundSpeedVolumeDialogFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SoundSpeedVolumeDialogFragment.this.setItemCheck(3);
                SoundSpeedVolumeDialogFragment.this.setCmd(7);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCmd(int i) {
        IpcObj.getInstance().setCmd(4, 57, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setItemCheck(int i) {
        clearCheck();
        if (i == 0) {
            this.mSpeedClose.setChecked(true);
            this.mSpeedLow.setChecked(false);
            this.mSpeedMiddle.setChecked(false);
            this.mSpeedHigh.setChecked(false);
        } else if (i == 1) {
            this.mSpeedClose.setChecked(false);
            this.mSpeedLow.setChecked(true);
            this.mSpeedMiddle.setChecked(false);
            this.mSpeedHigh.setChecked(false);
        } else if (i == 2) {
            this.mSpeedClose.setChecked(false);
            this.mSpeedLow.setChecked(false);
            this.mSpeedMiddle.setChecked(true);
            this.mSpeedHigh.setChecked(false);
        } else if (i != 3) {
        } else {
            this.mSpeedClose.setChecked(false);
            this.mSpeedLow.setChecked(false);
            this.mSpeedMiddle.setChecked(false);
            this.mSpeedHigh.setChecked(true);
        }
    }

    private void clearCheck() {
        this.mSpeedClose.setChecked(false);
        this.mSpeedLow.setChecked(false);
        this.mSpeedMiddle.setChecked(false);
        this.mSpeedHigh.setChecked(false);
    }
}
