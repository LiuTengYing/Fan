package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.widget.view.TextSeekBarCurrent;
import com.syu.ipcself.module.main.Main;
import com.syu.jni.ToolsJni;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
/* loaded from: classes.dex */
public class FactoryBlCurrentAdjustDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "FactoryBlCurrentAdjustDialogFragment";
    private static int iBrightNessPre = 40;
    int[] iParam;
    private Button mBtnSave;
    private TextView mMax;
    private TextView mMin;
    private View mRootView;
    private TextSeekBarCurrent maxAdjust;
    private TextSeekBarCurrent minAdjust;
    private ViewGroup.LayoutParams params;
    private boolean isSave = false;
    int iMin = 1;
    int iMax = 40;
    Runnable runnableHandleSetBrightNess = new Runnable() { // from class: com.android.settings.factory.dialog.FactoryBlCurrentAdjustDialogFragment.4
        @Override // java.lang.Runnable
        public void run() {
            FactoryBlCurrentAdjustDialogFragment factoryBlCurrentAdjustDialogFragment;
            int[] iArr;
            if (!FactoryBlCurrentAdjustDialogFragment.this.isSave && (iArr = (factoryBlCurrentAdjustDialogFragment = FactoryBlCurrentAdjustDialogFragment.this).iParam) != null && iArr.length > 0) {
                factoryBlCurrentAdjustDialogFragment.setBrightness((iArr[0] * 255) / 100);
            }
            FactoryBlCurrentAdjustDialogFragment.this.setBrightness((FactoryBlCurrentAdjustDialogFragment.iBrightNessPre * 255) / 100);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, int i) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FactoryBlCurrentAdjustDialogFragment factoryBlCurrentAdjustDialogFragment = new FactoryBlCurrentAdjustDialogFragment();
            iBrightNessPre = i;
            factoryBlCurrentAdjustDialogFragment.setArguments(bundle);
            factoryBlCurrentAdjustDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.settings_bl_current_adjust_layout, null);
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
        ToolsJni.cmd_105_set_bl_adj(1);
        Log.d(TAG, "init: ");
        TextSeekBarCurrent textSeekBarCurrent = (TextSeekBarCurrent) this.mRootView.findViewById(R$id.bl_adjust_max_seekbar);
        this.maxAdjust = textSeekBarCurrent;
        textSeekBarCurrent.setMin(40);
        this.maxAdjust.setMax(100);
        TextSeekBarCurrent textSeekBarCurrent2 = (TextSeekBarCurrent) this.mRootView.findViewById(R$id.bl_adjust_min_seekbar);
        this.minAdjust = textSeekBarCurrent2;
        textSeekBarCurrent2.setMin(1);
        this.minAdjust.setMax(70);
        this.mMin = (TextView) this.mRootView.findViewById(R$id.bl_adjust_min_max);
        this.mMax = (TextView) this.mRootView.findViewById(R$id.bl_adjust_max_max);
        readConf();
        Button button = (Button) this.mRootView.findViewById(R$id.bl_adjust_save);
        this.mBtnSave = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryBlCurrentAdjustDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustDialogFragment.this.save();
            }
        });
        this.maxAdjust.setOnSeekBarChangeListener(new TextSeekBarCurrent.OnSeekBarChangeListener() { // from class: com.android.settings.factory.dialog.FactoryBlCurrentAdjustDialogFragment.2
            @Override // com.android.settings.widget.view.TextSeekBarCurrent.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCurrent textSeekBarCurrent3) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCurrent.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCurrent textSeekBarCurrent3) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCurrent.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCurrent textSeekBarCurrent3, int i, boolean z) {
                String str = FactoryBlCurrentAdjustDialogFragment.TAG;
                Log.d(str, "max onProgressChanged: " + i);
                FactoryBlCurrentAdjustDialogFragment factoryBlCurrentAdjustDialogFragment = FactoryBlCurrentAdjustDialogFragment.this;
                factoryBlCurrentAdjustDialogFragment.iMax = i + 40;
                factoryBlCurrentAdjustDialogFragment.updateMax();
            }
        });
        this.minAdjust.setOnSeekBarChangeListener(new TextSeekBarCurrent.OnSeekBarChangeListener() { // from class: com.android.settings.factory.dialog.FactoryBlCurrentAdjustDialogFragment.3
            @Override // com.android.settings.widget.view.TextSeekBarCurrent.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCurrent textSeekBarCurrent3) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCurrent.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCurrent textSeekBarCurrent3) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCurrent.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCurrent textSeekBarCurrent3, int i, boolean z) {
                String str = FactoryBlCurrentAdjustDialogFragment.TAG;
                Log.d(str, "min onProgressChanged: " + i);
                FactoryBlCurrentAdjustDialogFragment factoryBlCurrentAdjustDialogFragment = FactoryBlCurrentAdjustDialogFragment.this;
                factoryBlCurrentAdjustDialogFragment.iMin = i + 1;
                factoryBlCurrentAdjustDialogFragment.updateMin();
            }
        });
    }

    public void updateMin() {
        TextSeekBarCurrent textSeekBarCurrent = this.minAdjust;
        if (textSeekBarCurrent != null) {
            textSeekBarCurrent.setProgress(this.iMin - 1);
        }
        setBrightness((this.iMin * 255) / 100);
        TextView textView = this.mMin;
        textView.setText(this.iMin + "");
    }

    public void updateMax() {
        TextSeekBarCurrent textSeekBarCurrent = this.maxAdjust;
        if (textSeekBarCurrent != null) {
            textSeekBarCurrent.setProgress(this.iMax - 40);
        }
        setBrightness((this.iMax * 255) / 100);
        TextView textView = this.mMax;
        textView.setText(this.iMax + "");
    }

    public void readConf() {
        this.iParam = ToolsJni.cmd_109_get_bl_limit();
        String str = TAG;
        Log.d(str, "readConf: " + Arrays.toString(this.iParam));
        int[] iArr = this.iParam;
        int i = iArr[0];
        if (i >= 40 && i <= 100) {
            this.iMax = i;
        }
        int i2 = iArr[1];
        if (i2 >= 1 && i2 <= 70) {
            this.iMin = i2;
        }
        TextSeekBarCurrent textSeekBarCurrent = this.minAdjust;
        if (textSeekBarCurrent != null) {
            textSeekBarCurrent.setProgress(this.iMin - 1);
        }
        TextSeekBarCurrent textSeekBarCurrent2 = this.maxAdjust;
        if (textSeekBarCurrent2 != null) {
            textSeekBarCurrent2.setProgress(this.iMax - 40);
        }
        TextView textView = this.mMin;
        if (textView != null) {
            textView.setText(this.iMin + "");
        }
        TextView textView2 = this.mMax;
        if (textView2 != null) {
            textView2.setText(this.iMax + "");
        }
    }

    private void save2file(int i, int i2) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/customercfg");
        if (!file.exists()) {
            file.mkdirs();
        }
        String str = file.getPath() + "/customercfg.txt";
        File file2 = new File(str);
        if (file2.exists()) {
            file2.delete();
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(str, "rw");
            randomAccessFile.write(("set-blduty-min=" + i + "\r\n").getBytes("utf-8"));
            randomAccessFile.write(("set-blduty-max=" + i2).getBytes("utf-8"));
            randomAccessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        int[] iArr = this.iParam;
        int i = iArr[0];
        int i2 = this.iMax;
        if (i == i2 && iArr[1] == this.iMin) {
            return;
        }
        this.isSave = true;
        int i3 = this.iMin;
        if (i3 >= i2) {
            this.iMax = i3 + 10;
        }
        ToolsJni.cmd_110_set_bl_limit(this.iMax, i3);
        save2file(this.iMin, this.iMax);
        Toast.makeText(getContext(), getContext().getResources().getString(R$string.str_sure), 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBrightness(int i) {
        setScrennManualMode();
        try {
            Settings.System.putInt(SettingsApplication.mApplication.getContentResolver(), "screen_brightness", i);
        } catch (Exception unused) {
        }
    }

    public void setScrennManualMode() {
        ContentResolver contentResolver = SettingsApplication.mApplication.getContentResolver();
        try {
            if (Settings.System.getInt(contentResolver, "screen_brightness_mode") == 1) {
                Settings.System.putInt(contentResolver, "screen_brightness_mode", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onPause() {
        Log.d(TAG, "onPause: ");
        ToolsJni.cmd_105_set_bl_adj(0);
        Main.postRunnable_Ui(true, this.runnableHandleSetBrightNess, 3000L);
        super.onPause();
    }
}
