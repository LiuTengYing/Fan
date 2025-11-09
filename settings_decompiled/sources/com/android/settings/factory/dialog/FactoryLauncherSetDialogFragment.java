package com.android.settings.factory.dialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.utils.UpdateStateChange;
import com.syu.ipcself.module.main.Main;
import com.syu.util.FuncUtils;
import com.syu.util.Markup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FactoryLauncherSetDialogFragment extends InstrumentedDialogFragment {
    public static String App_name = "";
    public static String[] Path_USB = null;
    public static String Platform_str = "";
    private static String TAG = "FactoryLauncherSetDialogFragment";
    public static boolean bEnableFocusChange = false;
    public static String[] compatibleWidth = null;
    public static String[] mConf_SD = null;
    public static String[] mConf_USB = null;
    public static boolean mIsOldService = false;
    public static String mPkgName = null;
    public static Resources mResources = null;
    public static String realplatform = "";
    private String defLaun;
    Method getExternalSd1Path;
    Method getExternalSd2Path;
    Method getUsb1Path;
    Method getUsb2Path;
    Method getUsb3Path;
    Method getUsb4Path;
    private launcherListAdapter launcherListAdapter;
    public IntentFilter mFilterLauncher;
    private ListView mListView;
    private PackageManager mPkgManager;
    private List<ResolveInfo> mResolveList;
    private View mRootView;
    public ComponentName[] mSetLauncer;
    public ComponentName[] mSetUserLauncer;
    public Method method_removeTask;
    private ViewGroup.LayoutParams params;
    private SharedPreferences sp_Laun;
    public static Boolean is_Zhihangcanbus_Enable = Boolean.FALSE;
    public static Boolean is_gpsinfoneedtypeface = Boolean.TRUE;
    public static boolean UI_Color_Select_Flag = false;
    public static String MCU_FOLDER_PATH = "/system/mcu";
    public static String MCU_ALL_FLIE_PATH_8 = "/system/mcu/McuUpdateAll.bin";
    public static String MCU_ALL_FLIE_PATH_32 = "/system/mcu/McuUpdateAllM32.bin";
    public static String MCU_ALL_FLIE_PATH_ID8 = "/system/mcu/McuUpdateAll.bin";
    public static String MCU_ALL_FLIE_PATH_ID32 = "/system/mcu/McuUpdateAllM32.bin";
    public static String str_system_path = "/system/";
    public static String McuFileName = "";
    private static int PopChoiceItem_Name = 233;
    public static String Path_Native = "/xxxx";
    private static int SET_LAUNCHER = 585;
    public static String[] Path_SD = new String[2];
    public int mCurLauncher = 0;
    public int mCurUserLauncher = 0;
    public int Launcherselect = 0;
    private int mIdCustomer = -1;
    private int mUi = -1;
    public int mAnimationLeft = 0;
    private String oldLauncher = "";
    public Method setStatusBarColor = null;
    public Method isInMultiWindowMode = null;
    private String pakcagename = "";
    public List<SparseArray<String>> mListUserLauncher = new ArrayList();
    public List<SparseArray<String>> mListLauncher = new ArrayList();
    Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == FactoryLauncherSetDialogFragment.SET_LAUNCHER) {
                FactoryLauncherSetDialogFragment.this.setLauncher(((Integer) message.obj).intValue());
            }
            super.handleMessage(message);
        }
    };
    Handler sendmsg_handler = new Handler() { // from class: com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment.3
        @Override // android.os.Handler
        public void handleMessage(Message message) {
        }
    };

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    static {
        for (int i = 0; i < 2; i++) {
            Path_SD[i] = new String();
            Path_SD[i] = "/xxxx";
        }
        Path_USB = new String[21];
        for (int i2 = 0; i2 < 21; i2++) {
            Path_USB[i2] = new String();
            Path_USB[i2] = "/xxxx";
        }
        mConf_SD = new String[2];
        for (int i3 = 0; i3 < 2; i3++) {
            mConf_SD[i3] = new String();
            mConf_SD[i3] = "/xxxx";
        }
        mConf_USB = new String[21];
        for (int i4 = 0; i4 < 21; i4++) {
            mConf_USB[i4] = new String();
            mConf_USB[i4] = "/xxxx";
        }
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FactoryLauncherSetDialogFragment factoryLauncherSetDialogFragment = new FactoryLauncherSetDialogFragment();
            factoryLauncherSetDialogFragment.setArguments(bundle);
            factoryLauncherSetDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.factory_launcher_select_dialog_layout, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        setTitle(string);
        return dialog;
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.launcher_select_title)).setText(str);
    }

    private void init() {
        this.sp_Laun = getContext().getSharedPreferences("LAUNCHER_CHOOSE", 0);
        this.defLaun = SystemProperties.get("ro.fyt.launcher");
        this.mIdCustomer = SystemProperties.getInt("ro.build.fytmanufacturer", 2);
        this.mUi = SystemProperties.getInt("ro.fyt.uiid", 0);
        mPkgName = getContext().getPackageName();
        realplatform = SystemProperties.get("ro.fyt.realplatform", "0");
        this.mPkgManager = getContext().getPackageManager();
        mResources = getContext().getResources();
        IntentFilter intentFilter = new IntentFilter();
        this.mFilterLauncher = intentFilter;
        intentFilter.addAction("android.intent.action.MAIN");
        this.mFilterLauncher.addCategory("android.intent.category.HOME");
        this.mFilterLauncher.addCategory("android.intent.category.DEFAULT");
        ReadConf();
        getMethod();
        getLauncherSetApp();
        initData();
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void initData() {
        this.mListView = (ListView) this.mRootView.findViewById(R$id.launcher_list);
        String str = SystemProperties.get("persist.lsec.launcher", "");
        if (!TextUtils.isEmpty(str)) {
            int size = this.mListUserLauncher.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                } else if (this.mListUserLauncher.get(i).get(PopChoiceItem_Name).equalsIgnoreCase(str)) {
                    this.mCurLauncher = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        launcherListAdapter launcherlistadapter = new launcherListAdapter(getContext(), this.mListUserLauncher);
        this.launcherListAdapter = launcherlistadapter;
        this.mListView.setAdapter((ListAdapter) launcherlistadapter);
        this.launcherListAdapter.setItemSelect(this.mCurLauncher);
        this.launcherListAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment.1
            @Override // com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment.OnItemClickListener
            public void onItemClick(int i2) {
                FactoryLauncherSetDialogFragment factoryLauncherSetDialogFragment = FactoryLauncherSetDialogFragment.this;
                if (factoryLauncherSetDialogFragment.mCurLauncher == i2) {
                    String str2 = FactoryLauncherSetDialogFragment.TAG;
                    Log.d(str2, "onItemClick: " + i2 + "mCurLauncher ===" + FactoryLauncherSetDialogFragment.this.mCurLauncher);
                    return;
                }
                if (factoryLauncherSetDialogFragment.launcherListAdapter != null) {
                    FactoryLauncherSetDialogFragment.this.launcherListAdapter.setItemSelect(i2);
                    FactoryLauncherSetDialogFragment.this.launcherListAdapter.notifyDataSetChanged();
                }
                FactoryLauncherSetDialogFragment.this.pakcagename = SystemProperties.get("persist.launcher.packagename", "");
                SystemProperties.set("persist.launcher.embedded_pkg", "");
                SystemProperties.set("persist.launcher.packagename", "");
                SystemProperties.set("persist.launcher.packagename", "");
                String str3 = FactoryLauncherSetDialogFragment.TAG;
                Log.d(str3, "onItemClick: ");
                SystemProperties.reportSyspropChanged();
                UpdateStateChange.getInstance().updateChoice("launch_sel", FactoryLauncherSetDialogFragment.this.mListUserLauncher.get(i2).get(FactoryLauncherSetDialogFragment.PopChoiceItem_Name));
                if (FactoryLauncherSetDialogFragment.this.handler != null) {
                    Message message = new Message();
                    message.what = FactoryLauncherSetDialogFragment.SET_LAUNCHER;
                    message.obj = Integer.valueOf(i2);
                    FactoryLauncherSetDialogFragment.this.handler.sendMessageDelayed(message, 500L);
                }
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x008c, code lost:
        if (android.text.TextUtils.isEmpty(r6) == false) goto L74;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void getLauncherSetApp() {
        /*
            Method dump skipped, instructions count: 396
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment.getLauncherSetApp():void");
    }

    public String obtainName(String str) {
        if (str.equalsIgnoreCase("com.android.launcher1")) {
            this.Launcherselect = 0;
            if (this.mIdCustomer == 42 && "Sophia".equals(Platform_str)) {
                return getContext().getResources().getString(R$string.common_launcher_type_1);
            }
            return getContext().getResources().getString(R$string.common_launcher_type_0);
        } else if (str.equalsIgnoreCase("com.android.launcher2")) {
            this.Launcherselect = 1;
            return getContext().getResources().getString(R$string.common_launcher_type_1);
        } else if (str.equalsIgnoreCase("com.android.launcher3")) {
            this.Launcherselect = 2;
            return getContext().getResources().getString(R$string.common_launcher_type_2);
        } else if (str.equalsIgnoreCase("com.android.launcher4")) {
            this.Launcherselect = 3;
            return getContext().getResources().getString(R$string.common_launcher_type_3);
        } else if (str.equalsIgnoreCase("com.android.launcher5")) {
            this.Launcherselect = 4;
            return getContext().getResources().getString(R$string.common_launcher_type_4);
        } else if (str.equalsIgnoreCase("com.android.launcher6")) {
            this.Launcherselect = 5;
            return getContext().getResources().getString(R$string.common_launcher_type_5);
        } else if (str.equalsIgnoreCase("com.android.launcher7")) {
            this.Launcherselect = 6;
            return getContext().getResources().getString(R$string.common_launcher_type_6);
        } else if (str.equalsIgnoreCase("com.android.launcher8")) {
            this.Launcherselect = 7;
            return getContext().getResources().getString(R$string.common_launcher_type_7);
        } else if (str.equalsIgnoreCase("com.android.launcher10")) {
            this.Launcherselect = 9;
            return getContext().getResources().getString(R$string.common_launcher_type_9);
        } else if (str.equalsIgnoreCase("com.android.launcher13")) {
            this.Launcherselect = 12;
            return getContext().getResources().getString(R$string.common_launcher_type_12);
        } else if (str.equalsIgnoreCase("com.ava.car")) {
            this.Launcherselect = 10;
            return getContext().getResources().getString(R$string.common_launcher_type_ava);
        } else if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_0))) {
            return "com.android.launcher1";
        } else {
            if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_1))) {
                return this.mIdCustomer == 42 ? "com.android.launcher1" : "com.android.launcher2";
            } else if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_2))) {
                return "com.android.launcher3";
            } else {
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_3))) {
                    return "com.android.launcher4";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_4))) {
                    return "com.android.launcher5";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_5))) {
                    return "com.android.launcher6";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_6))) {
                    return "com.android.launcher7";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_7))) {
                    return "com.android.launcher8";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_9))) {
                    return "com.android.launcher10";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_12))) {
                    return "com.android.launcher13";
                }
                if (str.equalsIgnoreCase(getContext().getResources().getString(R$string.common_launcher_type_ava))) {
                    return "com.ava.car";
                }
                if (str.startsWith("com.android.launcher")) {
                    try {
                        int intValue = Integer.valueOf(str.substring(20, str.length())).intValue();
                        this.Launcherselect = intValue - 1;
                        return "UI" + intValue;
                    } catch (Exception unused) {
                        return "Third-party";
                    }
                } else if (!str.startsWith("UI")) {
                    return "com.syu.launcher".contains(str) ? "NEW UI" : "Third-party";
                } else {
                    return "com.android.launcher" + str.substring(2, str.length());
                }
            }
        }
    }

    public void addChoice2List(String str, List<SparseArray<String>> list) {
        if (list != null) {
            SparseArray<String> sparseArray = new SparseArray<>();
            sparseArray.put(PopChoiceItem_Name, getString(str));
            list.add(sparseArray);
        }
    }

    public String getString(String str) {
        int identifier = mResources.getIdentifier(str, "string", mPkgName);
        return identifier == 0 ? str : myString(identifier);
    }

    public static String myString(int i) {
        Resources resources = mResources;
        if (resources != null) {
            try {
                return resources.getString(i);
            } catch (Exception unused) {
                return "";
            }
        }
        return "";
    }

    public void setLauncher(int i) {
        this.oldLauncher = this.mListUserLauncher.get(this.mCurLauncher).get(PopChoiceItem_Name);
        Log.d(TAG, "setLauncher 111 " + i);
        if (i >= this.mListUserLauncher.size()) {
            return;
        }
        Log.d(TAG, "setLauncher 222 " + i);
        this.mCurLauncher = i;
        String str = this.mListUserLauncher.get(i).get(PopChoiceItem_Name);
        String str2 = "";
        int i2 = 0;
        boolean z = false;
        while (true) {
            ComponentName[] componentNameArr = this.mSetUserLauncer;
            if (i2 >= componentNameArr.length) {
                break;
            }
            if (str.equalsIgnoreCase(componentNameArr[i2].getPackageName())) {
                ComponentName componentName = this.mSetUserLauncer[i2];
                str2 = componentName.getPackageName();
                ResolveInfo resolveInfo = this.mResolveList.get(i2);
                int i3 = resolveInfo.match;
                if (i3 <= 0) {
                    i3 = 0;
                }
                this.mPkgManager.clearPackagePreferredActivities(resolveInfo.activityInfo.packageName);
                if (componentName != null) {
                    this.mPkgManager.replacePreferredActivity(this.mFilterLauncher, i3, this.mSetUserLauncer, componentName);
                    this.mPkgManager.addPreferredActivity(this.mFilterLauncher, i3, this.mSetUserLauncer, componentName);
                }
                String str3 = this.mListUserLauncher.get(this.mCurLauncher).get(PopChoiceItem_Name);
                Log.d(TAG, "setLauncher: " + str3);
                SystemProperties.set("persist.lsec.launcher", str3);
                ActivityManager activityManager = (ActivityManager) SettingsApplication.mApplication.getSystemService("activity");
                List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(10);
                if (this.method_removeTask != null) {
                    for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                        int i4 = 0;
                        while (true) {
                            if (i4 >= this.mListUserLauncher.size()) {
                                break;
                            } else if (this.mListUserLauncher.get(i4).equals(runningTaskInfo.baseActivity.getPackageName())) {
                                try {
                                    this.method_removeTask.invoke(activityManager, Integer.valueOf(runningTaskInfo.id));
                                    break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                i4++;
                            }
                        }
                    }
                }
                z = true;
            }
            i2++;
        }
        ActivityManager activityManager2 = (ActivityManager) SettingsApplication.mApplication.getSystemService("activity");
        if (z) {
            Message message = new Message();
            message.obj = str2;
            this.sendmsg_handler.sendMessageDelayed(message, 500L);
        }
        Log.d(TAG, "setLauncher: " + this.oldLauncher);
        if (!"Third-party".equals(this.oldLauncher)) {
            activityManager2.forceStopPackage(this.oldLauncher);
            if (!SystemProperties.getBoolean("persist.lsec.pip_rect_clear", false)) {
                SystemProperties.set("sys.lsec.pip_rect", "");
            }
        }
        if (!TextUtils.isEmpty(this.pakcagename)) {
            activityManager2.forceStopPackage(this.pakcagename);
        }
        dismiss();
        goHome();
        SystemProperties.set("persist.syu.launcher.haspip", "false");
        String str4 = SystemProperties.get("persist.launcher.packagename", (String) null);
        if (str4 == null || "".equals(str4)) {
            return;
        }
        activityManager2.forceStopPackage(str4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class PInfo {
        private String appPackageName = "";

        PInfo() {
        }
    }

    public void ReadConf() {
        String GetAttr;
        try {
            int identifier = getContext().getResources().getIdentifier("conf_platform", "raw", mPkgName);
            if (identifier <= 0) {
                return;
            }
            String readStrFromStream = FuncUtils.readStrFromStream(getContext().getResources().openRawResource(identifier));
            Markup markup = new Markup();
            markup.ReadXML(readStrFromStream);
            if (markup.IntoItem()) {
                do {
                    String GetAttr2 = markup.GetAttr("name");
                    if (GetAttr2 != null) {
                        if (GetAttr2.equals("apkid")) {
                            String GetAttr3 = markup.GetAttr("value");
                            if (GetAttr3 != null) {
                                App_name = GetAttr3;
                            }
                        } else if (GetAttr2.equals("platform")) {
                            String GetAttr4 = markup.GetAttr("value");
                            if (GetAttr4 != null) {
                                Main.initPlatForm(GetAttr4);
                                Platform_str = GetAttr4;
                            }
                        } else if (GetAttr2.equals("old_service")) {
                            String GetAttr5 = markup.GetAttr("value");
                            if (GetAttr5 != null && GetAttr5.equals("1")) {
                                mIsOldService = true;
                            }
                        } else if (GetAttr2.equals("animation")) {
                            String GetAttr6 = markup.GetAttr("value");
                            if (GetAttr6 != null) {
                                if (GetAttr6.equals("1")) {
                                    this.mAnimationLeft = 1;
                                } else if (GetAttr6.equals("2")) {
                                    this.mAnimationLeft = 2;
                                }
                            }
                        } else if (GetAttr2.equals("EnableFocusChange")) {
                            String GetAttr7 = markup.GetAttr("value");
                            if (GetAttr7 != null && GetAttr7.equals("1")) {
                                bEnableFocusChange = true;
                            }
                        } else if (GetAttr2.equals("isZhihangcanbusEnable")) {
                            String GetAttr8 = markup.GetAttr("value");
                            if (GetAttr8 != null) {
                                if (GetAttr8.equals("1")) {
                                    is_Zhihangcanbus_Enable = Boolean.TRUE;
                                } else if (GetAttr8.equals("0")) {
                                    is_Zhihangcanbus_Enable = Boolean.FALSE;
                                }
                            }
                        } else if (GetAttr2.equals("GpsinfoNeedTypeface")) {
                            String GetAttr9 = markup.GetAttr("value");
                            if (GetAttr9 != null) {
                                if (GetAttr9.equals("0")) {
                                    is_gpsinfoneedtypeface = Boolean.FALSE;
                                } else if (GetAttr9.equals("1")) {
                                    is_gpsinfoneedtypeface = Boolean.TRUE;
                                }
                            }
                        } else if (GetAttr2.equals("compatibleWidth") && (GetAttr = markup.GetAttr("value")) != null) {
                            compatibleWidth = GetAttr.split(",");
                        }
                    }
                } while (markup.NextItem());
                markup.ExitItem();
                switch (Main.mConf_PlatForm) {
                    case 1:
                        ReadPath("path_e7");
                        return;
                    case 2:
                        ReadPath("path_3188");
                        return;
                    case 3:
                        ReadPath("path_8700");
                        return;
                    case 4:
                        ReadPath("path_786");
                        return;
                    case 5:
                        ReadPath("path_sophia");
                        return;
                    case 6:
                    case 7:
                        ReadPath("path_6025");
                        return;
                    case 8:
                        ReadPath("path_6025");
                        MCU_FOLDER_PATH = "/oem/mcu";
                        MCU_ALL_FLIE_PATH_8 = "/oem/mcu/McuUpdateAll.bin";
                        MCU_ALL_FLIE_PATH_32 = "/oem/mcu/McuUpdateAllM32.bin";
                        MCU_ALL_FLIE_PATH_ID8 = "/oem/mcu/McuUpdateAll.bin";
                        MCU_ALL_FLIE_PATH_ID32 = "/oem/mcu/McuUpdateAllM32.bin";
                        str_system_path = "/oem/";
                        return;
                    default:
                        return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReadPath(String str) {
        String GetAttr;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            int identifier = getContext().getResources().getIdentifier(str, "raw", mPkgName);
            if (identifier <= 0) {
                return;
            }
            String readStrFromStream = FuncUtils.readStrFromStream(getContext().getResources().openRawResource(identifier));
            Markup markup = new Markup();
            markup.ReadXML(readStrFromStream);
            if (markup.IntoItem()) {
                do {
                    Markup.XmlItem xmlItem = markup.mItemCur;
                    String FindToken = markup.FindToken(xmlItem.mStrBuff, xmlItem.mPosEnd - xmlItem.mPosStart, 0, null);
                    if (FindToken.equals("path_Native")) {
                        String GetAttr2 = markup.GetAttr("value");
                        if (GetAttr2 != null) {
                            Path_Native = GetAttr2;
                        }
                    } else if (FindToken.equals("path_SD1")) {
                        String GetAttr3 = markup.GetAttr("value");
                        if (GetAttr3 != null) {
                            mConf_SD[0] = GetAttr3;
                        }
                    } else if (FindToken.equals("path_SD2")) {
                        String GetAttr4 = markup.GetAttr("value");
                        if (GetAttr4 != null) {
                            mConf_SD[1] = GetAttr4;
                        }
                    } else if (FindToken.equals("path_USB1")) {
                        String GetAttr5 = markup.GetAttr("value");
                        if (GetAttr5 != null) {
                            if ("3002".equals(realplatform)) {
                                mConf_USB[4] = GetAttr5;
                            } else {
                                mConf_USB[0] = GetAttr5;
                            }
                        }
                    } else if (FindToken.equals("path_USB2")) {
                        String GetAttr6 = markup.GetAttr("value");
                        if (GetAttr6 != null) {
                            mConf_USB[1] = GetAttr6;
                        }
                    } else if (FindToken.equals("path_USB3")) {
                        String GetAttr7 = markup.GetAttr("value");
                        if (GetAttr7 != null) {
                            mConf_USB[2] = GetAttr7;
                        }
                    } else if (FindToken.equals("path_USB4")) {
                        String GetAttr8 = markup.GetAttr("value");
                        if (GetAttr8 != null) {
                            mConf_USB[3] = GetAttr8;
                        }
                    } else if (FindToken.equals("path_USB5")) {
                        String GetAttr9 = markup.GetAttr("value");
                        if (GetAttr9 != null) {
                            if ("3002".equals(realplatform)) {
                                mConf_USB[0] = GetAttr9;
                            } else {
                                mConf_USB[4] = GetAttr9;
                            }
                        }
                    } else if (FindToken.equals("path_USB6")) {
                        String GetAttr10 = markup.GetAttr("value");
                        if (GetAttr10 != null) {
                            mConf_USB[5] = GetAttr10;
                        }
                    } else if (FindToken.equals("path_USB7")) {
                        String GetAttr11 = markup.GetAttr("value");
                        if (GetAttr11 != null) {
                            mConf_USB[6] = GetAttr11;
                        }
                    } else if (FindToken.equals("path_USB8")) {
                        String GetAttr12 = markup.GetAttr("value");
                        if (GetAttr12 != null) {
                            mConf_USB[7] = GetAttr12;
                        }
                    } else if (FindToken.equals("path_USB9")) {
                        String GetAttr13 = markup.GetAttr("value");
                        if (GetAttr13 != null) {
                            mConf_USB[8] = GetAttr13;
                        }
                    } else if (FindToken.equals("path_USB10")) {
                        String GetAttr14 = markup.GetAttr("value");
                        if (GetAttr14 != null) {
                            mConf_USB[9] = GetAttr14;
                        }
                    } else if (FindToken.equals("path_USB11")) {
                        String GetAttr15 = markup.GetAttr("value");
                        if (GetAttr15 != null) {
                            mConf_USB[10] = GetAttr15;
                        }
                    } else if (FindToken.equals("path_USB12")) {
                        String GetAttr16 = markup.GetAttr("value");
                        if (GetAttr16 != null) {
                            mConf_USB[11] = GetAttr16;
                        }
                    } else if (FindToken.equals("path_USB13")) {
                        String GetAttr17 = markup.GetAttr("value");
                        if (GetAttr17 != null) {
                            mConf_USB[12] = GetAttr17;
                        }
                    } else if (FindToken.equals("path_USB14")) {
                        String GetAttr18 = markup.GetAttr("value");
                        if (GetAttr18 != null) {
                            mConf_USB[13] = GetAttr18;
                        }
                    } else if (FindToken.equals("path_USB15")) {
                        String GetAttr19 = markup.GetAttr("value");
                        if (GetAttr19 != null) {
                            mConf_USB[14] = GetAttr19;
                        }
                    } else if (FindToken.equals("path_USB16")) {
                        String GetAttr20 = markup.GetAttr("value");
                        if (GetAttr20 != null) {
                            mConf_USB[15] = GetAttr20;
                        }
                    } else if (FindToken.equals("path_USB17")) {
                        String GetAttr21 = markup.GetAttr("value");
                        if (GetAttr21 != null) {
                            mConf_USB[16] = GetAttr21;
                        }
                    } else if (FindToken.equals("path_USB18")) {
                        String GetAttr22 = markup.GetAttr("value");
                        if (GetAttr22 != null) {
                            mConf_USB[17] = GetAttr22;
                        }
                    } else if (FindToken.equals("path_USB19")) {
                        String GetAttr23 = markup.GetAttr("value");
                        if (GetAttr23 != null) {
                            mConf_USB[18] = GetAttr23;
                        }
                    } else if (FindToken.equals("path_USB20")) {
                        String GetAttr24 = markup.GetAttr("value");
                        if (GetAttr24 != null) {
                            mConf_USB[19] = GetAttr24;
                        }
                    } else if (FindToken.equals("path_USB21") && (GetAttr = markup.GetAttr("value")) != null) {
                        mConf_USB[20] = GetAttr;
                    }
                } while (markup.NextItem());
                markup.ExitItem();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMethod() {
        Method[] declaredMethods;
        Method[] declaredMethods2;
        Method[] declaredMethods3;
        for (Method method : Environment.class.getDeclaredMethods()) {
            if (method.getName().equals("getExternalSd1Path")) {
                this.getExternalSd1Path = method;
            } else if (method.getName().equals("getExternalSd2Path")) {
                this.getExternalSd2Path = method;
            } else if (method.getName().equals("getUsb1Path")) {
                this.getUsb1Path = method;
            } else if (method.getName().equals("getUsb2Path")) {
                this.getUsb2Path = method;
            } else if (method.getName().equals("getUsb3Path")) {
                this.getUsb3Path = method;
            } else if (method.getName().equals("getUsb4Path")) {
                this.getUsb4Path = method;
            }
        }
        Method[] declaredMethods4 = ActivityManager.class.getDeclaredMethods();
        int length = declaredMethods4.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Method method2 = declaredMethods4[i];
            if (method2.getName().equals("removeTask")) {
                this.method_removeTask = method2;
                break;
            }
            i++;
        }
        for (Method method3 : Window.class.getDeclaredMethods()) {
            if (method3.getName().equals("setStatusBarColor")) {
                this.setStatusBarColor = method3;
            }
        }
        for (Method method4 : Activity.class.getDeclaredMethods()) {
            if (method4.getName().equals("isInMultiWindowMode")) {
                this.isInMultiWindowMode = method4;
            }
        }
    }

    public void goHome() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        getContext().startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class launcherListAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public launcherListAdapter(Context context, List<SparseArray<String>> list) {
            this.mContext = context;
            this.mData = list;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mData.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mData.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View view2;
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_launcher_select, viewGroup, false);
                holder.mTitle = (TextView) view2.findViewById(R$id.launcher_name);
                holder.mCheck = (CheckBox) view2.findViewById(R$id.launcher_select);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            if (i == this.mPosition) {
                holder.mCheck.setChecked(true);
            } else {
                holder.mCheck.setChecked(false);
            }
            holder.mTitle.setText(this.mData.get(i).get(FactoryLauncherSetDialogFragment.PopChoiceItem_Name));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment.launcherListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (launcherListAdapter.this.mListener != null) {
                        launcherListAdapter.this.mListener.onItemClick(i);
                    }
                }
            });
            return view2;
        }

        public void setItemSelect(int i) {
            this.mPosition = i;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mListener = onItemClickListener;
        }

        /* loaded from: classes.dex */
        class Holder {
            CheckBox mCheck;
            TextView mTitle;

            Holder() {
            }
        }
    }
}
