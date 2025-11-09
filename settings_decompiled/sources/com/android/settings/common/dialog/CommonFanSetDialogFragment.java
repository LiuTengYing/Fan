package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$color;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.widget.view.TextSeekBarCenter;
import java.util.Arrays;
/* loaded from: classes.dex */
public class CommonFanSetDialogFragment extends InstrumentedDialogFragment implements IpcNotify {
    private static int AUTOTEMP = 899;
    private static int UPDATETEMP = 898;
    private static int fanMode = 1;
    private static int mTemp;
    private AnimationDrawable animationDrawable;
    private ImageView imageView;
    private boolean isAnimStart;
    private TextSeekBarCenter mAutoTemp;
    private Context mContext;
    private TextView mCurrentTemp;
    private LinearLayout mFanAutoLayout;
    private CheckBox mFanAutoSelect;
    private LinearLayout mFanCloseLayout;
    private CheckBox mFanCloseSelect;
    private LinearLayout mFanOpenLayout;
    private CheckBox mFanOpenSelect;
    private View mRootView;
    private TextView mValue;
    private ViewGroup.LayoutParams params;
    private int mCpuTemp = 54;
    private int mCurrentAutoTemp = 55;
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.common.dialog.CommonFanSetDialogFragment.5
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == CommonFanSetDialogFragment.UPDATETEMP) {
                CommonFanSetDialogFragment commonFanSetDialogFragment = CommonFanSetDialogFragment.this;
                commonFanSetDialogFragment.setTemp(commonFanSetDialogFragment.mCpuTemp);
            } else if (message.what == CommonFanSetDialogFragment.AUTOTEMP) {
                CommonFanSetDialogFragment.this.updateAutoTemp();
            }
            super.handleMessage(message);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvr(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGesture(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGsensor(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyIpod(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(com.syu.remote.Message message) {
    }

    public static void show(Fragment fragment, String str, int i, int i2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonFanSetDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonFanSetDialogFragment commonFanSetDialogFragment = new CommonFanSetDialogFragment();
            commonFanSetDialogFragment.setArguments(bundle);
            mTemp = i;
            fanMode = i2;
            Log.d("CommonFanSetDialogFragment", "show: " + fanMode);
            commonFanSetDialogFragment.show(childFragmentManager, "CommonFanSetDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        if (SettingsApplication.mHeightFix > SettingsApplication.mWidthFix) {
            this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_fan_set_dialog_layout_h, null);
            this.params = new ViewGroup.LayoutParams((windowManeger[0] / 5) * 4, (windowManeger[1] / 5) * 2);
        } else {
            this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_fan_set_dialog_layout, null);
            this.params = new ViewGroup.LayoutParams((windowManeger[0] / 5) * 3, (windowManeger[1] / 5) * 3);
        }
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setTitle(string);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        setTitle(string);
        setNotify(this.mContext);
        return dialog;
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 145, 146);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        super.onStop();
    }

    private void init() {
        RelativeLayout.LayoutParams layoutParams;
        this.mFanOpenLayout = (LinearLayout) this.mRootView.findViewById(R$id.fan_set_open_layout);
        this.mFanCloseLayout = (LinearLayout) this.mRootView.findViewById(R$id.fan_set_close_layout);
        this.mFanAutoLayout = (LinearLayout) this.mRootView.findViewById(R$id.fan_set_auto_layout);
        this.mFanOpenSelect = (CheckBox) this.mRootView.findViewById(R$id.fan_set_open);
        this.mFanCloseSelect = (CheckBox) this.mRootView.findViewById(R$id.fan_set_close);
        this.mFanAutoSelect = (CheckBox) this.mRootView.findViewById(R$id.fan_set_auto);
        this.mAutoTemp = (TextSeekBarCenter) this.mRootView.findViewById(R$id.fan_set_temp_seek);
        this.mValue = (TextView) this.mRootView.findViewById(R$id.fan_set_auto_temp);
        this.mCurrentTemp = (TextView) this.mRootView.findViewById(R$id.fan_set_current_temp);
        this.imageView = (ImageView) this.mRootView.findViewById(R$id.fan_set_anim);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        if (i == 0) {
            this.imageView.setBackground(this.mContext.getResources().getDrawable(R$drawable.fan_set_anim));
        } else if (i == 1) {
            this.imageView.setBackground(this.mContext.getResources().getDrawable(R$drawable.fan_set_anim));
        } else if (i == 2 || i == 3) {
            this.imageView.setBackground(this.mContext.getResources().getDrawable(R$drawable.fan_set_anim_classic));
        }
        if (SettingsApplication.mHeightFix > SettingsApplication.mWidthFix) {
            int i2 = SettingsApplication.mWidthFix;
            layoutParams = new RelativeLayout.LayoutParams(i2 / 4, i2 / 4);
            layoutParams.addRule(11);
            layoutParams.setMargins(10, 20, 10, 10);
        } else {
            int i3 = SettingsApplication.mHeightFix;
            layoutParams = new RelativeLayout.LayoutParams(i3 / 3, i3 / 3);
            layoutParams.addRule(11);
            layoutParams.setMargins(10, 20, 40, 10);
        }
        this.imageView.setLayoutParams(layoutParams);
        this.animationDrawable = (AnimationDrawable) this.imageView.getBackground();
        this.mAutoTemp.setMax(55);
        this.mAutoTemp.setMin(0);
        setItemClick(fanMode);
        this.mAutoTemp.setProgress(mTemp);
        this.mFanOpenLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonFanSetDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonFanSetDialogFragment.this.setItemClick(0);
                IpcObj.getInstance().setCmd(0, 144, 0);
                IpcObj.getInstance().setCmd(0, 34, 1);
            }
        });
        this.mFanCloseLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonFanSetDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonFanSetDialogFragment.this.setItemClick(1);
                IpcObj.getInstance().setCmd(0, 34, 0);
                IpcObj.getInstance().setCmd(0, 144, 0);
            }
        });
        this.mFanAutoLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonFanSetDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonFanSetDialogFragment.this.setItemClick(2);
                IpcObj.getInstance().setCmd(0, 144, 1);
            }
        });
        this.mAutoTemp.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.common.dialog.CommonFanSetDialogFragment.4
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i4, boolean z) {
                int i5 = i4 + 40;
                IpcObj.getInstance().setCmd(0, 145, new int[]{i4 + 35, i5}, null, null);
                CommonFanSetDialogFragment.this.mCurrentAutoTemp = i5;
                if (CommonFanSetDialogFragment.this.mValue != null) {
                    TextView textView = CommonFanSetDialogFragment.this.mValue;
                    textView.setText(i5 + "℃");
                }
            }
        });
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.fan_set_title)).setText(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setItemClick(int i) {
        fanMode = i;
        if (i == 0) {
            this.mFanOpenSelect.setChecked(true);
            this.mFanCloseSelect.setChecked(false);
            this.mFanAutoSelect.setChecked(false);
            AnimationDrawable animationDrawable = this.animationDrawable;
            if (animationDrawable != null) {
                this.isAnimStart = true;
                animationDrawable.start();
            }
        } else if (i == 1) {
            this.mFanOpenSelect.setChecked(false);
            this.mFanCloseSelect.setChecked(true);
            this.mFanAutoSelect.setChecked(false);
            AnimationDrawable animationDrawable2 = this.animationDrawable;
            if (animationDrawable2 != null) {
                animationDrawable2.stop();
                this.isAnimStart = false;
            }
        } else if (i != 2) {
        } else {
            this.mFanOpenSelect.setChecked(false);
            this.mFanCloseSelect.setChecked(false);
            this.mFanAutoSelect.setChecked(true);
            AnimationDrawable animationDrawable3 = this.animationDrawable;
            if (animationDrawable3 != null) {
                if (this.mCpuTemp >= this.mCurrentAutoTemp) {
                    if (this.isAnimStart) {
                        return;
                    }
                    animationDrawable3.start();
                    this.isAnimStart = true;
                    return;
                }
                animationDrawable3.stop();
                this.isAnimStart = false;
            }
        }
    }

    private void notifyMainMsg(com.syu.remote.Message message) {
        Log.d("CommonFanSetDialogFragment", "notifyMainMsg: " + message.code + "  " + Arrays.toString(message.ints));
        int i = message.code;
        if (i == 145) {
            this.mCurrentAutoTemp = message.ints[1];
            this.mHandler.sendEmptyMessage(AUTOTEMP);
        } else if (i != 146) {
        } else {
            this.mCpuTemp = message.ints[0];
            this.mHandler.sendEmptyMessage(UPDATETEMP);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAutoTemp() {
        TextView textView = this.mValue;
        if (textView != null) {
            textView.setText(this.mCurrentAutoTemp + "℃");
        }
        TextSeekBarCenter textSeekBarCenter = this.mAutoTemp;
        if (textSeekBarCenter != null) {
            textSeekBarCenter.setProgress(this.mCurrentAutoTemp - 40);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTemp(int i) {
        TextView textView = this.mCurrentTemp;
        if (textView != null) {
            if (i < 70) {
                textView.setTextColor(this.mContext.getResources().getColor(R$color.cpu_temp_low));
            } else if (i > 70 && i < 95) {
                textView.setTextColor(this.mContext.getResources().getColor(R$color.cpu_temp_middle));
            } else if (i > 95) {
                textView.setTextColor(this.mContext.getResources().getColor(R$color.cpu_temp_high));
            }
            TextView textView2 = this.mCurrentTemp;
            textView2.setText(i + "");
        }
        AnimationDrawable animationDrawable = this.animationDrawable;
        if (animationDrawable == null || fanMode != 2) {
            return;
        }
        if (this.mCpuTemp >= this.mCurrentAutoTemp) {
            if (this.isAnimStart) {
                return;
            }
            animationDrawable.start();
            this.isAnimStart = true;
            return;
        }
        animationDrawable.stop();
        this.isAnimStart = false;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(com.syu.remote.Message message) {
        notifyMainMsg(message);
    }
}
