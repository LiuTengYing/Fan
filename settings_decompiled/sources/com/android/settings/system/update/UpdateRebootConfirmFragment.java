package com.android.settings.system.update;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
/* loaded from: classes.dex */
public class UpdateRebootConfirmFragment extends InstrumentedDialogFragment {
    private static String TAG = "ProtocolBackCarConfirmFragment";
    private static UpdateRebootListener updateRebootListener;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private View mRootView;
    private ViewGroup.LayoutParams params;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            UpdateRebootConfirmFragment updateRebootConfirmFragment = new UpdateRebootConfirmFragment();
            updateRebootConfirmFragment.setArguments(bundle);
            updateRebootConfirmFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 3, windowManeger[1] / 3);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.settings_update_reboot_dialog_layout, null);
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
        this.mBtnConfirm = (Button) this.mRootView.findViewById(R$id.backcar_camera_confirm);
        this.mBtnCancel = (Button) this.mRootView.findViewById(R$id.backcar_camera_cancel);
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateRebootConfirmFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateRebootConfirmFragment.removeBtnClickListener();
                UpdateRebootConfirmFragment.this.dismiss();
                IpcObj.getInstance().setCmd(0, 19, 1);
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateRebootConfirmFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UpdateRebootConfirmFragment.updateRebootListener != null) {
                    UpdateRebootConfirmFragment.updateRebootListener.onCancelClick();
                }
                UpdateRebootConfirmFragment.this.dismiss();
            }
        });
    }

    public static void setBtnClickListener(UpdateRebootListener updateRebootListener2) {
        updateRebootListener = updateRebootListener2;
    }

    public static void removeBtnClickListener() {
        updateRebootListener = null;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        removeBtnClickListener();
        super.onStop();
    }
}
