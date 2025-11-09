package com.android.settings.volume.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
import com.android.settings.widget.view.TextSeekBarCenter;
/* loaded from: classes.dex */
public class SoundBalanceSettingsDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "FactoryLauncherSetDialogFragment";
    private static int[] soundValues = new int[48];
    private TextSeekBarCenter mAux;
    private TextView mAuxValue;
    private TextSeekBarCenter mBtMusic;
    private TextView mBtMusicValue;
    private TextView mBtPhoneValue;
    private TextSeekBarCenter mBtphone;
    private TextSeekBarCenter mIpod;
    private TextSeekBarCenter mMusic;
    private TextView mMusicValue;
    private TextSeekBarCenter mNavi;
    private TextView mNaviValue;
    private TextSeekBarCenter mRadio;
    private TextView mRadioValue;
    private View mRootView;
    private TextSeekBarCenter mTv;
    private TextView mTvValue;
    private ViewGroup.LayoutParams params;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, int[] iArr) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            SoundBalanceSettingsDialogFragment soundBalanceSettingsDialogFragment = new SoundBalanceSettingsDialogFragment();
            soundValues = iArr;
            soundBalanceSettingsDialogFragment.setArguments(bundle);
            soundBalanceSettingsDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams((windowManeger[0] / 4) * 3, (windowManeger[1] / 4) * 3);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.sound_balance_settings_layout, null);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, SystemProperties.getInt("persist.syu.thememode", 2) == 2 ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        setTitle(string);
        return dialog;
    }

    private void init() {
        getContext().getPackageManager();
        this.mRadio = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_radio);
        this.mAux = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_aux);
        this.mTv = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_tv);
        this.mBtMusic = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_bt_music);
        this.mIpod = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_ipod);
        this.mNavi = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_navi);
        this.mMusic = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_music);
        this.mBtphone = (TextSeekBarCenter) this.mRootView.findViewById(R$id.sound_balance_bt_phone);
        this.mRadioValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_radio);
        this.mAuxValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_aux);
        this.mTvValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_tv);
        this.mBtMusicValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_bt_music);
        this.mNaviValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_navi);
        this.mMusicValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_music);
        this.mBtPhoneValue = (TextView) this.mRootView.findViewById(R$id.sound_balance_value_bt_phone);
        int[] iArr = soundValues;
        if (iArr != null && iArr.length > 0) {
            this.mRadio.setProgress(iArr[0]);
            this.mAux.setProgress(soundValues[5]);
            this.mTv.setProgress(soundValues[8]);
            this.mBtMusic.setProgress(soundValues[3]);
            this.mNavi.setProgress(soundValues[7]);
            this.mMusic.setProgress(soundValues[6]);
            this.mBtphone.setProgress(soundValues[2]);
            TextView textView = this.mRadioValue;
            textView.setText(soundValues[0] + "");
            TextView textView2 = this.mAuxValue;
            textView2.setText(soundValues[5] + "");
            TextView textView3 = this.mTvValue;
            textView3.setText(soundValues[8] + "");
            TextView textView4 = this.mBtMusicValue;
            textView4.setText(soundValues[3] + "");
            TextView textView5 = this.mNaviValue;
            textView5.setText(soundValues[7] + "");
            TextView textView6 = this.mMusicValue;
            textView6.setText(soundValues[6] + "");
            TextView textView7 = this.mBtPhoneValue;
            textView7.setText(soundValues[2] + "");
        }
        initListener();
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.sound_balance_title)).setText(str);
    }

    private void initListener() {
        this.mRadio.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.1
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{0, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mRadioValue;
                textView.setText(i + "");
            }
        });
        this.mAux.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.2
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{5, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mAuxValue;
                textView.setText(i + "");
            }
        });
        this.mTv.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.3
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{8, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mTvValue;
                textView.setText(i + "");
            }
        });
        this.mBtMusic.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.4
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{3, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mBtMusicValue;
                textView.setText(i + "");
            }
        });
        this.mNavi.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.5
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{7, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mNaviValue;
                textView.setText(i + "");
            }
        });
        this.mMusic.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.6
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{6, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mMusicValue;
                textView.setText(i + "");
            }
        });
        this.mBtphone.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment.7
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(4, 6, new int[]{2, i}, null, null);
                TextView textView = SoundBalanceSettingsDialogFragment.this.mBtPhoneValue;
                textView.setText(i + "");
            }
        });
    }
}
