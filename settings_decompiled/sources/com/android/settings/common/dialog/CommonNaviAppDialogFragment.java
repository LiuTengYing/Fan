package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.common.adapter.NaviAppAdapter;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class CommonNaviAppDialogFragment extends InstrumentedDialogFragment {
    public static boolean hasFilter = false;
    private NaviAppAdapter appAdapter;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    public static List<String> NaviFilterList = new ArrayList();
    public static String packName = "";
    public static String mCurrentPkg = "";
    public static String[] hideApkPackage = {"com.android.browser", "com.android.calculator2", "cn.kuwo.kwmusiccar", "net.easyconn", "com.edog.car", "cn.yunovo.nxos.traffic", "com.abupdate.fota_demo_iot", "com.williexing.android.apps.xcdvr1", "com.teyes.TeyesYahu", "com.android.settings"};

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, String str2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonNaviAppDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonNaviAppDialogFragment commonNaviAppDialogFragment = new CommonNaviAppDialogFragment();
            packName = str2;
            commonNaviAppDialogFragment.setArguments(bundle);
            commonNaviAppDialogFragment.show(childFragmentManager, "CommonNaviAppDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams((windowManeger[0] / 4) * 3, (windowManeger[1] / 4) * 3);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_navi_app_dialog, null);
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

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.select_app_title)).setText(str);
    }

    private void init() {
        readNaviFilter();
        PackageManager packageManager = getContext().getPackageManager();
        GridView gridView = (GridView) this.mRootView.findViewById(R$id.navi_app_gridview);
        this.mBtnConfirm = (Button) this.mRootView.findViewById(R$id.navi_app_confirm);
        this.mBtnCancel = (Button) this.mRootView.findViewById(R$id.navi_app_cancel);
        final ArrayList<ResolveInfo> allInstalledApps = getAllInstalledApps(packageManager);
        Log.d("CommonNaviAppDialogFragment", "init111: " + packName);
        if (TextUtils.isEmpty(packName)) {
            packName = SystemProperties.get("persist.sys.navi.packagename", "");
        }
        Log.d("CommonNaviAppDialogFragment", "init333: " + packName);
        NaviAppAdapter naviAppAdapter = new NaviAppAdapter(getContext(), allInstalledApps, packageManager);
        this.appAdapter = naviAppAdapter;
        gridView.setAdapter((ListAdapter) naviAppAdapter);
        this.appAdapter.setSelectPackage(packName);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.common.dialog.CommonNaviAppDialogFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ResolveInfo resolveInfo = (ResolveInfo) allInstalledApps.get(i);
                Log.d("CommonNaviAppDialogFragment", "onItemClick: " + resolveInfo.activityInfo.packageName);
                CommonNaviAppDialogFragment.this.appAdapter.setSelectPackage(resolveInfo.activityInfo.packageName);
                CommonNaviAppDialogFragment.mCurrentPkg = resolveInfo.activityInfo.packageName;
                CommonNaviAppDialogFragment.this.appAdapter.notifyDataSetChanged();
            }
        });
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonNaviAppDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SystemProperties.set("persist.sys.navi.packagename", CommonNaviAppDialogFragment.mCurrentPkg);
                IpcObj.getInstance().setCmd(0, 9, CommonNaviAppDialogFragment.mCurrentPkg);
                CommonNaviAppDialogFragment.packName = CommonNaviAppDialogFragment.mCurrentPkg;
                CommonNaviAppDialogFragment.this.dismiss();
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonNaviAppDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonNaviAppDialogFragment.this.dismiss();
            }
        });
    }

    private int getCustomId() {
        int i = SystemProperties.getInt("ro.build.fytmanufacturer", 2);
        Log.d("CommonNaviAppDialogFragment", "onCreateDialog: " + i);
        return i;
    }

    /* JADX WARN: Removed duplicated region for block: B:63:0x00f0 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0053 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<android.content.pm.ResolveInfo> getAllInstalledApps(android.content.pm.PackageManager r10) {
        /*
            Method dump skipped, instructions count: 253
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.common.dialog.CommonNaviAppDialogFragment.getAllInstalledApps(android.content.pm.PackageManager):java.util.ArrayList");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v12 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r2v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.io.FileReader, java.io.Reader] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:33:0x007e -> B:52:0x0090). Please submit an issue!!! */
    public void readNaviFilter() {
        List<String> list;
        ?? r1;
        Exception e;
        BufferedReader bufferedReader;
        String str = "/oem/app/NaviFilter";
        File file = new File("/oem/app/NaviFilter");
        NaviFilterList.clear();
        ?? exists = file.exists();
        if (exists != 0 && file.canRead()) {
            FileReader fileReader = null;
            try {
                try {
                    try {
                        exists = new FileReader("/oem/app/NaviFilter");
                    } catch (Throwable th) {
                        th = th;
                    }
                } catch (Exception e2) {
                    exists = 0;
                    e = e2;
                    bufferedReader = null;
                } catch (Throwable th2) {
                    th = th2;
                    r1 = 0;
                    try {
                        fileReader.close();
                        r1.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                    throw th;
                }
                try {
                    bufferedReader = new BufferedReader(exists);
                    String str2 = "";
                    while (true) {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            str2 = str2 + readLine;
                        } catch (Exception e4) {
                            e = e4;
                            e.printStackTrace();
                            exists.close();
                            bufferedReader.close();
                            list = NaviFilterList;
                            if (list == null) {
                            }
                            hasFilter = false;
                        }
                    }
                    if (str2 != null && !str2.equals("")) {
                        NaviFilterList = new ArrayList(Arrays.asList(str2.replaceAll("\\s*|\t|\r|\n", "").split(",")));
                    }
                    exists.close();
                    bufferedReader.close();
                } catch (Exception e5) {
                    bufferedReader = null;
                    e = e5;
                } catch (Throwable th3) {
                    th = th3;
                    str = null;
                    fileReader = exists;
                    r1 = str;
                    fileReader.close();
                    r1.close();
                    throw th;
                }
            } catch (IOException e6) {
                e6.printStackTrace();
            }
        }
        list = NaviFilterList;
        if (list == null && list.size() > 0) {
            if (getCustomId() == 112) {
                hasFilter = true;
                return;
            } else {
                hasFilter = false;
                return;
            }
        }
        hasFilter = false;
    }
}
