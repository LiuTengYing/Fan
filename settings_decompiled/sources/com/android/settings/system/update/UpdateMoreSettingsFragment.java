package com.android.settings.system.update;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class UpdateMoreSettingsFragment extends InstrumentedDialogFragment {
    private static String TAG = "UpdateMoreSettingsFragment";
    private static UpdateSettingListener updateSettingListener;
    private TextView mClearData;
    private View mContentView;
    private View mRootView;
    private TextView mSelectTime;
    private Switch mSlientDownload;
    private TextView mUpdateOneWeek;
    private TextView mUpdateOneday;
    private TextView mUpdateThreeday;
    private Switch mWifiDownload;
    private ViewGroup.LayoutParams params;
    private PopupWindow popupWindow;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            UpdateMoreSettingsFragment updateMoreSettingsFragment = new UpdateMoreSettingsFragment();
            updateMoreSettingsFragment.setArguments(bundle);
            updateMoreSettingsFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.update_more_settings_layout, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mClearData = (TextView) this.mRootView.findViewById(R$id.clear_update_data);
        this.mWifiDownload = (Switch) this.mRootView.findViewById(R$id.only_wifi_download);
        this.mSlientDownload = (Switch) this.mRootView.findViewById(R$id.slience_update);
        this.mSelectTime = (TextView) this.mRootView.findViewById(R$id.check_time);
        UpdateSettingListener updateSettingListener2 = updateSettingListener;
        if (updateSettingListener2 != null) {
            this.mWifiDownload.setChecked(updateSettingListener2.getWifiDownload());
        }
        UpdateSettingListener updateSettingListener3 = updateSettingListener;
        if (updateSettingListener3 != null) {
            this.mSlientDownload.setChecked(updateSettingListener3.getSilentDownload());
        }
        UpdateSettingListener updateSettingListener4 = updateSettingListener;
        if (updateSettingListener4 != null) {
            setSelectTime(updateSettingListener4.getCheckTime());
        }
        this.mClearData.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UpdateMoreSettingsFragment.updateSettingListener != null) {
                    UpdateMoreSettingsFragment.updateSettingListener.clearData();
                }
                UpdateMoreSettingsFragment.this.dismiss();
            }
        });
        this.mWifiDownload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (UpdateMoreSettingsFragment.updateSettingListener != null) {
                    UpdateMoreSettingsFragment.updateSettingListener.onWifiDownload(z);
                }
            }
        });
        this.mSlientDownload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (UpdateMoreSettingsFragment.updateSettingListener != null) {
                    UpdateMoreSettingsFragment.updateSettingListener.slientDownload(z);
                }
            }
        });
        this.mSelectTime.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UpdateMoreSettingsFragment.this.popupWindow != null && UpdateMoreSettingsFragment.this.popupWindow.isShowing()) {
                    UpdateMoreSettingsFragment.this.popupWindow.dismiss();
                } else {
                    UpdateMoreSettingsFragment.this.showTimeSelectPop();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTimeSelectPop() {
        this.mContentView = LayoutInflater.from(getContext()).inflate(R$layout.update_select_time_layout, (ViewGroup) null);
        if (this.popupWindow == null) {
            this.popupWindow = new PopupWindow(this.mContentView, 200, 320, false);
        }
        this.popupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.popupWindow.setFocusable(true);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.update();
        this.popupWindow.showAsDropDown(this.mSelectTime);
        initPopClickListener();
    }

    private void initPopClickListener() {
        this.mUpdateOneday = (TextView) this.mContentView.findViewById(R$id.update_time_one_day);
        this.mUpdateThreeday = (TextView) this.mContentView.findViewById(R$id.update_time_three_day);
        this.mUpdateOneWeek = (TextView) this.mContentView.findViewById(R$id.update_time_one_week);
        this.mUpdateOneday.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UpdateMoreSettingsFragment.updateSettingListener != null) {
                    UpdateMoreSettingsFragment.updateSettingListener.setCheckTime(0);
                }
                UpdateMoreSettingsFragment.this.setSelectTime(0);
            }
        });
        this.mUpdateThreeday.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UpdateMoreSettingsFragment.updateSettingListener != null) {
                    UpdateMoreSettingsFragment.updateSettingListener.setCheckTime(1);
                }
                UpdateMoreSettingsFragment.this.setSelectTime(1);
            }
        });
        this.mUpdateOneWeek.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateMoreSettingsFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UpdateMoreSettingsFragment.updateSettingListener != null) {
                    UpdateMoreSettingsFragment.updateSettingListener.setCheckTime(2);
                }
                UpdateMoreSettingsFragment.this.setSelectTime(2);
            }
        });
    }

    public static void setUpdateListener(UpdateSettingListener updateSettingListener2) {
        updateSettingListener = updateSettingListener2;
    }

    public static void removeListener() {
        updateSettingListener = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSelectTime(int i) {
        TextView textView = this.mSelectTime;
        if (textView != null) {
            if (i == 0) {
                textView.setText(getContext().getResources().getString(R$string.update_select_time_one_day));
            } else if (i == 1) {
                textView.setText(getContext().getResources().getString(R$string.update_select_time_three_day));
            } else if (i == 2) {
                textView.setText(getContext().getResources().getString(R$string.update_select_time_one_week));
            } else {
                textView.setText(getContext().getResources().getString(R$string.update_select_time_one_day));
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        removeListener();
        super.onStop();
    }
}
