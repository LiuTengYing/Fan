package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class UpdateProgressDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "UpdateProgressDialogFragment";
    private Context mContext;
    private View mRootView;
    private TextView mSelectPath;
    private ImageView mSelectPathBtn;
    private Button mStartBtn;
    private TextView mUpgradeProgress;
    private TextView mUpgradeResult;
    String matchName = "";
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
            UpdateProgressDialogFragment updateProgressDialogFragment = new UpdateProgressDialogFragment();
            updateProgressDialogFragment.setArguments(bundle);
            updateProgressDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0], windowManeger[1]);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.settings_update_progress_layout, null);
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
        ((TextView) this.mRootView.findViewById(R$id.upgrade_progress_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mContext = getContext();
        this.mSelectPath = (TextView) this.mRootView.findViewById(R$id.select_file_path);
        this.mUpgradeProgress = (TextView) this.mRootView.findViewById(R$id.upgrade_progress);
        this.mUpgradeResult = (TextView) this.mRootView.findViewById(R$id.upgrade_result);
        this.mStartBtn = (Button) this.mRootView.findViewById(R$id.start_upgrade);
        this.mSelectPathBtn = (ImageView) this.mRootView.findViewById(R$id.select_file_btn);
        initListener();
    }

    private void initListener() {
        this.mSelectPathBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.UpdateProgressDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateProgressDialogFragment.this.startFileManager();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFileManager() {
        Intent intent = new Intent();
        intent.setAction("com.syu.filemanager");
        intent.setFlags(268435456);
        this.mContext.startActivity(intent);
    }
}
