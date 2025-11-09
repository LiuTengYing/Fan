package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.widget.view.TextSeekBarCenter;
/* loaded from: classes.dex */
public class ProtocolTrackMaxAngelDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "ProtocolTrackMaxAngelDialogFragment";
    private TextSeekBarCenter angelSeekbar;
    private TextView mCurrentValue;
    private View mRootView;
    private ViewGroup.LayoutParams params;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 3);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.settings_track_max_angel_layout, null);
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
        ((TextView) this.mRootView.findViewById(R$id.track_max_angel_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        int i = SystemProperties.getInt("persist.fyt.track_angle_max", 40);
        String str = TAG;
        Log.d(str, "init: " + i);
        this.angelSeekbar = (TextSeekBarCenter) this.mRootView.findViewById(R$id.track_max_angel_seek);
        this.mCurrentValue = (TextView) this.mRootView.findViewById(R$id.track_max_angel_max);
        this.angelSeekbar.setMin(20);
        this.angelSeekbar.setMax(50);
        this.angelSeekbar.setProgress(i - 20);
        TextView textView = this.mCurrentValue;
        textView.setText(i + "");
        this.angelSeekbar.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.factory.dialog.ProtocolTrackMaxAngelDialogFragment.1
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i2, boolean z) {
                StringBuilder sb = new StringBuilder();
                int i3 = i2 + 20;
                sb.append(i3);
                sb.append("");
                SystemProperties.set("persist.fyt.track_angle_max", sb.toString());
                TextView textView2 = ProtocolTrackMaxAngelDialogFragment.this.mCurrentValue;
                textView2.setText(i3 + "");
                String str2 = ProtocolTrackMaxAngelDialogFragment.TAG;
                Log.d(str2, "onProgressChanged: " + i2);
            }
        });
    }
}
