package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.UpdateStateChange;
/* loaded from: classes.dex */
public class CommonUsbDeviceStartDialogFragment extends InstrumentedDialogFragment {
    public static String mUSB_Conn_Start_Carlink = "com.syu.carlink/com.syu.carlink.MainActivity";
    public static String mUSB_Conn_Start_Easyconn = "net.easyconn/net.easyconn.ui.Fy15MainActivity";
    public static String mUSB_Conn_Start_Hicar = "com.syu.hicar/com.syu.hicar.MainActivity";
    private CheckBox mCheckCarlink;
    private CheckBox mCheckEasyConnect;
    private CheckBox mCheckHicar;
    private CheckBox mCheckNone;
    private RelativeLayout mRlCarlink;
    private RelativeLayout mRlEasyConnect;
    private RelativeLayout mRlHicar;
    private RelativeLayout mRlNone;
    private View mRootView;
    private ViewGroup.LayoutParams params;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonDialog") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonUsbDeviceStartDialogFragment commonUsbDeviceStartDialogFragment = new CommonUsbDeviceStartDialogFragment();
            commonUsbDeviceStartDialogFragment.setArguments(bundle);
            commonUsbDeviceStartDialogFragment.show(childFragmentManager, "CommonDialog");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_usbdevice_start_dialog_layout, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setTitle(string);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        setTitle(string);
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mCheckNone = (CheckBox) this.mRootView.findViewById(R$id.usb_start_none);
        this.mCheckCarlink = (CheckBox) this.mRootView.findViewById(R$id.usb_start_carlink);
        this.mCheckEasyConnect = (CheckBox) this.mRootView.findViewById(R$id.usb_start_easyconnect);
        this.mCheckHicar = (CheckBox) this.mRootView.findViewById(R$id.usb_start_hicar);
        this.mRlNone = (RelativeLayout) this.mRootView.findViewById(R$id.usb_none_layout);
        this.mRlCarlink = (RelativeLayout) this.mRootView.findViewById(R$id.usb_carlink_layout);
        this.mRlEasyConnect = (RelativeLayout) this.mRootView.findViewById(R$id.usb_easyconnect_layout);
        this.mRlHicar = (RelativeLayout) this.mRootView.findViewById(R$id.usb_hicar_layout);
        String str = SystemProperties.get("persist.fyt.phonestartapp", "");
        getUsbDeviceStart();
        initListener();
        updateAutoStart(str);
    }

    private void updateAutoStart(String str) {
        if ("".equals(str)) {
            setItemCheck(0);
        } else if (mUSB_Conn_Start_Carlink.equals(str)) {
            setItemCheck(1);
        } else if (mUSB_Conn_Start_Easyconn.equals(str)) {
            setItemCheck(2);
        } else if (mUSB_Conn_Start_Hicar.equals(str)) {
            setItemCheck(3);
        }
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.usb_device_start_title)).setText(str);
    }

    private void initListener() {
        this.mRlNone.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonUsbDeviceStartDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemProperties.set("persist.fyt.phonestartapp", "");
                CommonUsbDeviceStartDialogFragment.this.setItemCheck(0);
            }
        });
        this.mRlCarlink.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonUsbDeviceStartDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemProperties.set("persist.fyt.phonestartapp", CommonUsbDeviceStartDialogFragment.mUSB_Conn_Start_Carlink);
                CommonUsbDeviceStartDialogFragment.this.setItemCheck(1);
            }
        });
        this.mRlEasyConnect.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonUsbDeviceStartDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemProperties.set("persist.fyt.phonestartapp", CommonUsbDeviceStartDialogFragment.mUSB_Conn_Start_Easyconn);
                CommonUsbDeviceStartDialogFragment.this.setItemCheck(2);
            }
        });
        this.mRlHicar.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonUsbDeviceStartDialogFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemProperties.set("persist.fyt.phonestartapp", CommonUsbDeviceStartDialogFragment.mUSB_Conn_Start_Hicar);
                CommonUsbDeviceStartDialogFragment.this.setItemCheck(3);
                IpcObj.getInstance().setCmd(0, 167, 0);
            }
        });
    }

    private void getUsbDeviceStart() {
        if (isAppInstalled(getContext(), "com.syu.carlink")) {
            this.mRlCarlink.setVisibility(0);
        } else {
            this.mRlCarlink.setVisibility(8);
        }
        if (isAppInstalled(getContext(), "net.easyconn") && !SystemProperties.getBoolean("persist.fyt.hideecforusbconn", false)) {
            this.mRlEasyConnect.setVisibility(0);
        } else {
            this.mRlEasyConnect.setVisibility(8);
        }
        if (getCustomId() != 112 || !is8541()) {
            if (isAppInstalled(getContext(), "com.syu.hicar")) {
                this.mRlHicar.setVisibility(0);
                return;
            } else {
                this.mRlHicar.setVisibility(8);
                return;
            }
        }
        this.mRlHicar.setVisibility(8);
    }

    private int getCustomId() {
        return SystemProperties.getInt("ro.build.fytmanufacturer", 2);
    }

    public static boolean isAppInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (Exception unused) {
        }
        return packageInfo != null;
    }

    private boolean is8541() {
        return "3003".equals(SystemProperties.get("ro.fyt.realplatform", "0"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setItemCheck(int i) {
        if (getCustomId() == 112) {
            if (i == 0) {
                IpcObj.getInstance().setCmd(0, 167, 1);
            } else {
                IpcObj.getInstance().setCmd(0, 167, 0);
            }
        }
        UpdateStateChange updateStateChange = UpdateStateChange.getInstance();
        updateStateChange.updateChoice("usb_device_start", i + "");
        if (i == 0) {
            this.mCheckNone.setChecked(true);
            this.mCheckCarlink.setChecked(false);
            this.mCheckEasyConnect.setChecked(false);
            this.mCheckHicar.setChecked(false);
        } else if (i == 1) {
            this.mCheckNone.setChecked(false);
            this.mCheckCarlink.setChecked(true);
            this.mCheckEasyConnect.setChecked(false);
            this.mCheckHicar.setChecked(false);
        } else if (i == 2) {
            this.mCheckNone.setChecked(false);
            this.mCheckCarlink.setChecked(false);
            this.mCheckEasyConnect.setChecked(true);
            this.mCheckHicar.setChecked(false);
        } else if (i != 3) {
        } else {
            this.mCheckNone.setChecked(false);
            this.mCheckCarlink.setChecked(false);
            this.mCheckEasyConnect.setChecked(false);
            this.mCheckHicar.setChecked(true);
        }
    }
}
