package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.android.settings.ipc.IpcObj;
import com.android.settings.widget.view.TextSeekBarCenter;
import java.io.File;
import java.io.RandomAccessFile;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class FactoryImageSettingsDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "FactoryImageSettingsDialogFragment";
    private static int mAvdd;
    private static int mVcom;
    private TextView mAvddAdd;
    private TextView mAvddExit;
    private TextView mAvddReduce;
    private TextView mAvddSaveConfig;
    private TextSeekBarCenter mAvddSeek;
    private TextView mAvddValue;
    private View mRootView;
    private TextView mVcomAdd;
    private TextView mVcomReduce;
    private TextSeekBarCenter mVcomSeek;
    private TextView mVcomValue;
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
            FactoryImageSettingsDialogFragment factoryImageSettingsDialogFragment = new FactoryImageSettingsDialogFragment();
            factoryImageSettingsDialogFragment.setArguments(bundle);
            mAvdd = iArr[0];
            mVcom = iArr[1];
            factoryImageSettingsDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0], windowManeger[1]);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.factory_image_settings_dialog_layout, null);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        initListener();
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mAvddAdd = (TextView) this.mRootView.findViewById(R$id.avdd_add);
        this.mAvddReduce = (TextView) this.mRootView.findViewById(R$id.avdd_reduce);
        this.mVcomAdd = (TextView) this.mRootView.findViewById(R$id.vcom_add);
        this.mVcomReduce = (TextView) this.mRootView.findViewById(R$id.vcom_reduce);
        this.mAvddSeek = (TextSeekBarCenter) this.mRootView.findViewById(R$id.avdd_seekbar);
        this.mVcomSeek = (TextSeekBarCenter) this.mRootView.findViewById(R$id.vcom_seekbar);
        this.mAvddValue = (TextView) this.mRootView.findViewById(R$id.avdd_value);
        this.mVcomValue = (TextView) this.mRootView.findViewById(R$id.vcom_value);
        this.mAvddExit = (TextView) this.mRootView.findViewById(R$id.avdd_exit);
        this.mAvddSaveConfig = (TextView) this.mRootView.findViewById(R$id.avdd_save_config);
        this.mAvddSeek.setMin(0);
        this.mAvddSeek.setMax(3);
        this.mVcomSeek.setMin(0);
        this.mVcomSeek.setMax(100);
        this.mAvddSeek.setProgress(mAvdd);
        this.mVcomSeek.setProgress(mVcom);
        initAVDD();
        initVcom();
        String str = TAG;
        Log.d(str, "init: mAvdd ===" + mAvdd + "  mVcom ====" + mVcom);
    }

    private void initListener() {
        this.mAvddAdd.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryImageSettingsDialogFragment.mAvdd < 3) {
                    FactoryImageSettingsDialogFragment.mAvdd++;
                    FactoryImageSettingsDialogFragment.this.mAvddSeek.setProgress(FactoryImageSettingsDialogFragment.mAvdd);
                }
                FactoryImageSettingsDialogFragment.this.sendAVDD();
            }
        });
        this.mAvddReduce.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryImageSettingsDialogFragment.mAvdd > 0) {
                    FactoryImageSettingsDialogFragment.mAvdd--;
                    FactoryImageSettingsDialogFragment.this.mAvddSeek.setProgress(FactoryImageSettingsDialogFragment.mAvdd);
                }
                FactoryImageSettingsDialogFragment.this.sendAVDD();
            }
        });
        this.mVcomAdd.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryImageSettingsDialogFragment.mVcom < 100) {
                    FactoryImageSettingsDialogFragment.mVcom++;
                    FactoryImageSettingsDialogFragment.this.mVcomSeek.setProgress(FactoryImageSettingsDialogFragment.mVcom);
                }
                FactoryImageSettingsDialogFragment.this.sendVcom();
            }
        });
        this.mVcomReduce.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryImageSettingsDialogFragment.mVcom > 0) {
                    FactoryImageSettingsDialogFragment.mVcom--;
                    FactoryImageSettingsDialogFragment.this.mVcomSeek.setProgress(FactoryImageSettingsDialogFragment.mVcom);
                }
                FactoryImageSettingsDialogFragment.this.sendVcom();
            }
        });
        this.mAvddExit.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryImageSettingsDialogFragment.this.dismiss();
            }
        });
        this.mAvddSaveConfig.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryImageSettingsDialogFragment.this.saveConfigForAVDDACOM();
                FactoryImageSettingsDialogFragment.this.dismiss();
            }
        });
        this.mAvddSeek.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.7
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                FactoryImageSettingsDialogFragment.mAvdd = i;
                FactoryImageSettingsDialogFragment.this.sendAVDD();
            }
        });
        this.mVcomSeek.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment.8
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                FactoryImageSettingsDialogFragment.mVcom = i;
                FactoryImageSettingsDialogFragment.this.sendVcom();
            }
        });
    }

    public void sendVcom() {
        TextView textView = this.mVcomValue;
        textView.setText("" + mVcom);
        IpcObj.getInstance().setCmd(0, 73, mVcom);
    }

    public void sendAVDD() {
        int i = mAvdd;
        String str = "10V";
        if (i != 0) {
            if (i == 1) {
                str = "10.8V";
            } else if (i == 2) {
                str = "11.5V";
            } else if (i == 3) {
                str = "12V";
            }
        }
        this.mAvddValue.setText(str);
        IpcObj.getInstance().setCmd(0, 79, mAvdd);
    }

    public void initAVDD() {
        int i = mAvdd;
        String str = "10V";
        if (i != 0) {
            if (i == 1) {
                str = "10.8V";
            } else if (i == 2) {
                str = "11.5V";
            } else if (i == 3) {
                str = "12V";
            }
        }
        this.mAvddValue.setText(str);
    }

    public void initVcom() {
        TextView textView = this.mVcomValue;
        textView.setText("" + mVcom);
    }

    public void saveConfigForAVDDACOM() {
        try {
            File file = new File("/mnt/sdcard/SettingsConfig");
            if (file.exists()) {
                file.delete();
            }
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("73", mVcom);
            jSONObject2.put("79", mAvdd);
            jSONObject.put("Main", jSONObject2);
            RandomAccessFile randomAccessFile = new RandomAccessFile("/mnt/sdcard/SettingsConfig", "rw");
            randomAccessFile.write(jSONObject.toString().getBytes("utf-8"));
            randomAccessFile.close();
            SettingsApplication settingsApplication = SettingsApplication.mApplication;
            Toast.makeText(settingsApplication, SettingsApplication.mResources.getString(R$string.avdd_toast_success) + "/mnt/sdcard/SettingsConfig", 1).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
