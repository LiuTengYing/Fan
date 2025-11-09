package com.android.settings.deviceinfo.syudevice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.syu.jni.ControlNative;
import com.syu.jni.DataSetting;
import com.syu.remote.Message;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
/* loaded from: classes.dex */
public class SyuSysDialog extends InstrumentedDialogFragment implements IpcNotify {
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private Dialog mFormatsdDialog;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    private String mAppVersion = "";
    private String old_resolution1 = "";
    private String old_resolution2 = "";
    private String old_resolution3 = "";
    private String old_resolution4 = "";
    private String old_resolution5 = "";
    private String new_resolution = "";
    private String mUiAppTime = "";
    private int isAudioType = 0;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvr(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGesture(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGsensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyIpod(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(Message message) {
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("SyuSysDialog") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            SyuSysDialog syuSysDialog = new SyuSysDialog();
            syuSysDialog.setArguments(bundle);
            syuSysDialog.show(childFragmentManager, "SyuSysDialog");
        }
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams((windowManeger[0] / 4) * 3, (windowManeger[1] / 4) * 3);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.dialog_syu_system, null);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, R$style.add_dialog);
        this.mFormatsdDialog = dialog;
        dialog.getWindow().setType(2003);
        this.mFormatsdDialog.requestWindowFeature(1);
        this.mFormatsdDialog.setContentView(this.mRootView, this.params);
        this.mFormatsdDialog.show();
        setTitle(string);
        setNotify(this.mContext);
        setSysmsg();
        return this.mFormatsdDialog;
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(4, 87);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        super.onStop();
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.syu_sys_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public void setSysmsg() {
        this.mDisplayMetrics = new DisplayMetrics();
        this.mAppVersion = getSystemInfoApp();
        TextView textView = (TextView) this.mRootView.findViewById(R$id.system_syu_msg);
        if (textView != null) {
            if (SettingsApplication.mHeightFix == 1200) {
                textView.setLineSpacing(15.0f, 1.0f);
            } else {
                textView.setLineSpacing(5.0f, 1.0f);
            }
            textView.setText(readSystemVision("/sys/fytver/fyt_bin_version"));
        }
    }

    private String readSystemVision(String str) {
        String str2;
        String str3 = "unknow";
        this.mDisplayMetrics = new DisplayMetrics();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(str)));
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                Log.d("SyuSysDialog", "readSystemVision: " + readLine);
                if (readLine.startsWith(" kernel")) {
                    Log.d("SyuSysDialog", "readSystemVision: kernel");
                    readLine = readLine.replace(" kernel:", "Kernel :  ");
                }
                if (readLine.startsWith(" platform")) {
                    readLine = readLine.replace(" platform:", "Platform :   ");
                }
                if (readLine.startsWith(" touch")) {
                    readLine = readLine.replace(" touch:", "Touch :   ");
                }
                if (readLine.startsWith(" cust")) {
                    readLine.replace(" cust:", "Cust :   ");
                }
                if (readLine.startsWith(" panel")) {
                    readLine = readLine.replace(" panel:", "Panel :   ");
                }
                if (readLine.startsWith(" bdcfg")) {
                    readLine = readLine.replace(" bdcfg:", "Bdcfg :   ");
                }
                if (readLine.startsWith("radiotype")) {
                    readLine = readLine.replace("radiotype:", "Radiotype :   ");
                }
                if (readLine.startsWith("audiotype")) {
                    readLine = readLine.replace("audiotype:", "Audiotype :   ");
                    if (this.isAudioType != 0) {
                        readLine = readLine + this.isAudioType;
                    }
                }
                if (readLine.startsWith("cs0mr")) {
                    readLine = readLine.replace("cs0mr:", "Cs0mr :   ");
                }
                if (readLine.startsWith("emif")) {
                    readLine = readLine.replace("emif:", "Emif :   ");
                }
                if (!readLine.startsWith("video") && !readLine.startsWith("t132") && !readLine.startsWith("T132")) {
                    stringBuffer.append(readLine + "\n");
                }
                if (!SystemProperties.get("sys.fyt.videoic_version", "null").equals("null")) {
                    stringBuffer.append("video:" + SystemProperties.get("sys.fyt.videoic_version", "null") + "\n");
                }
            }
            bufferedReader.close();
            if (!this.mAppVersion.isEmpty()) {
                stringBuffer.append(this.mAppVersion + "\n");
            }
            stringBuffer.append(getSystemTime());
            if ("zsender".equals(SystemProperties.get("persist.lsec.usbtype", ""))) {
                str2 = "Car: " + SystemProperties.get("zj.zsender.display", "1024x768") + ", \"" + SystemProperties.get("zj.zsender.carname", "unknow") + "\", <" + SystemProperties.get("zj.zsender.modelid", "unknow") + ">\n";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    String str4 = SystemProperties.get("zj.zsender.version", "unknow");
                    if (!"unknow".equals(str4) && !"".equals(str4)) {
                        str3 = simpleDateFormat2.format(simpleDateFormat.parse(str4));
                    }
                    str2 = str2 + "zsender: " + str3 + "\n";
                } catch (Exception e) {
                    Log.i("carset", Log.getStackTraceString(e));
                    e.printStackTrace();
                }
            } else {
                str2 = "";
            }
            String str5 = str2 + stringBuffer.toString();
            if (!"".equals(this.new_resolution) && !"".equals(this.old_resolution1) && !"".equals(this.old_resolution2) && !"".equals(this.old_resolution3) && !"".equals(this.old_resolution4) && !"".equals(this.old_resolution5)) {
                str5 = str5.replace(this.old_resolution1, this.new_resolution).replace(this.old_resolution2, this.new_resolution).replace(this.old_resolution3, this.new_resolution).replace(this.old_resolution4, this.new_resolution).replace(this.old_resolution5, "_");
            }
            if ("zsender".equals(SystemProperties.get("persist.lsec.usbtype", ""))) {
                str5.replace("audiotype:unknow\n", "");
            }
            return str5;
        } catch (Exception e2) {
            e2.printStackTrace();
            return "null";
        }
    }

    private String getDisplayMetrics() {
        int i = SystemProperties.getInt("sys.lsec.navi_bar_height", 0);
        String str = " ";
        if (this.mDisplayMetrics != null) {
            ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(this.mDisplayMetrics);
            DisplayMetrics displayMetrics = this.mDisplayMetrics;
            int i2 = displayMetrics.widthPixels;
            int i3 = displayMetrics.heightPixels;
            StringBuilder sb = new StringBuilder();
            sb.append(" ");
            sb.append(i2);
            sb.append("x");
            int i4 = i3 + i;
            sb.append(i4);
            sb.append(" ");
            str = sb.toString();
            this.old_resolution1 = i2 + "x" + i4;
            this.old_resolution2 = i2 + "X" + i4;
            this.old_resolution3 = i4 + "x" + i2;
            this.old_resolution4 = i4 + "X" + i2;
            this.old_resolution5 = "-_";
        }
        String str2 = SystemProperties.get("persist.sys.private_systeminfo", "");
        if ("".equals(str2)) {
            return str;
        }
        if ("null".equals(str2)) {
            this.new_resolution = "";
        } else {
            this.new_resolution = str2;
        }
        return str.replace(this.old_resolution1, this.new_resolution).replace(this.old_resolution2, this.new_resolution).replace(this.old_resolution3, this.new_resolution).replace(this.old_resolution4, this.new_resolution).replace(this.old_resolution5, "_");
    }

    private String getSystemTime() {
        String str = "System " + SystemProperties.get("ro.build.date", "");
        Log.d("fangli", "getSystemTime ==" + str);
        return str;
    }

    private String getSystemInfoApp() {
        byte[] fyt_get_ui_time;
        String string = getContext().getResources().getString(R$string.device_info_default);
        try {
            if (ControlNative.INSTANCE.fyt_get_ui_time().length > 5) {
                this.mUiAppTime = String.valueOf(Math.abs((int) fyt_get_ui_time[5]) + 1900) + "-" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[4])) + "-" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[3])) + " " + String.format("%02d", Byte.valueOf(fyt_get_ui_time[2])) + ":" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[1])) + ":" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[0]));
                string = DataSetting.ANDROID_COMPANY + getDisplayMetrics() + this.mUiAppTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("fangli", "App info===" + string);
        return string;
    }

    private void updateSound(Message message) {
        if (message.code != 87) {
            return;
        }
        this.isAudioType = message.ints[0];
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(Message message) {
        updateSound(message);
    }
}
