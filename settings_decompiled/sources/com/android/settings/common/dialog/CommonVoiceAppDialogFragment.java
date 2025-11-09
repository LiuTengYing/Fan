package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemProperties;
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
public class CommonVoiceAppDialogFragment extends InstrumentedDialogFragment {
    public static boolean hasFilter = false;
    private NaviAppAdapter appAdapter;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    public static List<String> NaviFilterList = new ArrayList();
    public static String packName = "";
    public static String mCurrentPkg = "";
    public static String[] hideApkPackage = {"com.android.browser", "com.android.calculator2", "cn.kuwo.kwmusiccar", "net.easyconn", "com.edog.car", "cn.yunovo.nxos.traffic", "com.abupdate.fota_demo_iot", "com.williexing.android.apps.xcdvr1", "com.teyes.TeyesYahu", "com.sprd.logmanager", "cn.kuwo.tingshucar", "com.android.settings", "com.kugou.android.auto", "com.qiyi.video.pad", "com.baidu.browser.apps", "com.autonavi.amapauto", "mark.via"};

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, String str2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonVoiceAppDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonVoiceAppDialogFragment commonVoiceAppDialogFragment = new CommonVoiceAppDialogFragment();
            packName = str2;
            commonVoiceAppDialogFragment.setArguments(bundle);
            commonVoiceAppDialogFragment.show(childFragmentManager, "CommonVoiceAppDialogFragment");
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
        Log.d("CommonVoiceAppDialogFragment", "init111: " + packName);
        NaviAppAdapter naviAppAdapter = new NaviAppAdapter(getContext(), allInstalledApps, packageManager);
        this.appAdapter = naviAppAdapter;
        gridView.setAdapter((ListAdapter) naviAppAdapter);
        this.appAdapter.setSelectPackage(packName);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.common.dialog.CommonVoiceAppDialogFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ResolveInfo resolveInfo = (ResolveInfo) allInstalledApps.get(i);
                Log.d("CommonVoiceAppDialogFragment", "onItemClick: " + resolveInfo.activityInfo.packageName);
                CommonVoiceAppDialogFragment.this.appAdapter.setSelectPackage(resolveInfo.activityInfo.packageName);
                CommonVoiceAppDialogFragment.mCurrentPkg = resolveInfo.activityInfo.packageName;
                CommonVoiceAppDialogFragment.this.appAdapter.notifyDataSetChanged();
            }
        });
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonVoiceAppDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpcObj.getInstance().setCmd(0, 164, CommonVoiceAppDialogFragment.mCurrentPkg);
                CommonVoiceAppDialogFragment.packName = CommonVoiceAppDialogFragment.mCurrentPkg;
                CommonVoiceAppDialogFragment.this.dismiss();
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonVoiceAppDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonVoiceAppDialogFragment.this.dismiss();
            }
        });
    }

    private int getCustomId() {
        int i = SystemProperties.getInt("ro.build.fytmanufacturer", 2);
        Log.d("CommonVoiceAppDialogFragment", "onCreateDialog: " + i);
        return i;
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x00e0, code lost:
        if (r7.equals("com.syu.voice") != false) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00e3, code lost:
        r8 = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<android.content.pm.ResolveInfo> getAllInstalledApps(android.content.pm.PackageManager r11) {
        /*
            Method dump skipped, instructions count: 243
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.common.dialog.CommonVoiceAppDialogFragment.getAllInstalledApps(android.content.pm.PackageManager):java.util.ArrayList");
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
        String str = "/oem/app/VoiceFilter";
        File file = new File("/oem/app/VoiceFilter");
        NaviFilterList.clear();
        ?? exists = file.exists();
        if (exists != 0 && file.canRead()) {
            FileReader fileReader = null;
            try {
                try {
                    try {
                        exists = new FileReader("/oem/app/VoiceFilter");
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
