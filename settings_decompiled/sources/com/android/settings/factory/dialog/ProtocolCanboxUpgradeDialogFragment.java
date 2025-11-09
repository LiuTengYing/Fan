package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.ipc.IpcObj;
/* loaded from: classes.dex */
public class ProtocolCanboxUpgradeDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "ProtocolTrackMaxAngelDialogFragment";
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
            ProtocolCanboxUpgradeDialogFragment protocolCanboxUpgradeDialogFragment = new ProtocolCanboxUpgradeDialogFragment();
            protocolCanboxUpgradeDialogFragment.setArguments(bundle);
            protocolCanboxUpgradeDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.settings_canbox_upgrade_layout, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        setTitle(string);
        init();
        return dialog;
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.canbox_upgrade_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mBtnConfirm = (Button) this.mRootView.findViewById(R$id.canbox_confirm);
        this.mBtnCancel = (Button) this.mRootView.findViewById(R$id.canbox_cancel);
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.ProtocolCanboxUpgradeDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ProtocolCanboxUpgradeDialogFragment.this.dismiss();
                IpcObj.getInstance().setCmd(14, AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP, new int[0]);
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.ProtocolCanboxUpgradeDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ProtocolCanboxUpgradeDialogFragment.this.dismiss();
            }
        });
    }
}
