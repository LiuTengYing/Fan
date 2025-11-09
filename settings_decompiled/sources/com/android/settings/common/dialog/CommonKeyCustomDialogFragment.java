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
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CommonKeyCustomDialogFragment extends InstrumentedDialogFragment {
    public static boolean hasFilter = false;
    private NaviAppAdapter appAdapter;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    public static List<String> NaviFilterList = new ArrayList();
    public static String packName = "";
    public static String mCurrentPkg = "";
    public static String[] hideApkPackage = {"com.android.browser", "com.android.settings"};

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, String str2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonKeyCustomDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonKeyCustomDialogFragment commonKeyCustomDialogFragment = new CommonKeyCustomDialogFragment();
            packName = str2;
            commonKeyCustomDialogFragment.setArguments(bundle);
            commonKeyCustomDialogFragment.show(childFragmentManager, "CommonKeyCustomDialogFragment");
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
        PackageManager packageManager = getContext().getPackageManager();
        GridView gridView = (GridView) this.mRootView.findViewById(R$id.navi_app_gridview);
        this.mBtnConfirm = (Button) this.mRootView.findViewById(R$id.navi_app_confirm);
        this.mBtnCancel = (Button) this.mRootView.findViewById(R$id.navi_app_cancel);
        final ArrayList<ResolveInfo> allInstalledApps = getAllInstalledApps(packageManager);
        NaviAppAdapter naviAppAdapter = new NaviAppAdapter(getContext(), allInstalledApps, packageManager);
        this.appAdapter = naviAppAdapter;
        gridView.setAdapter((ListAdapter) naviAppAdapter);
        this.appAdapter.setSelectPackage(packName);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.common.dialog.CommonKeyCustomDialogFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ResolveInfo resolveInfo = (ResolveInfo) allInstalledApps.get(i);
                Log.d("CommonKeyCustomDialogFragment", "onItemClick: " + resolveInfo.activityInfo.packageName);
                CommonKeyCustomDialogFragment.this.appAdapter.setSelectPackage(resolveInfo.activityInfo.packageName);
                CommonKeyCustomDialogFragment.mCurrentPkg = resolveInfo.activityInfo.packageName;
                CommonKeyCustomDialogFragment.this.appAdapter.notifyDataSetChanged();
            }
        });
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonKeyCustomDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                IpcObj.getInstance().setCmd(0, 185, CommonKeyCustomDialogFragment.mCurrentPkg);
                CommonKeyCustomDialogFragment.packName = CommonKeyCustomDialogFragment.mCurrentPkg;
                CommonKeyCustomDialogFragment.this.dismiss();
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonKeyCustomDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonKeyCustomDialogFragment.this.dismiss();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x009d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0021 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<android.content.pm.ResolveInfo> getAllInstalledApps(android.content.pm.PackageManager r9) {
        /*
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.MAIN"
            r2 = 0
            r0.<init>(r1, r2)
            java.lang.String r1 = "android.intent.category.LAUNCHER"
            r0.addCategory(r1)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.clear()
            r3 = 0
            java.util.List r0 = r9.queryIntentActivities(r0, r3)
            if (r0 != 0) goto L1d
            return r1
        L1d:
            java.util.Iterator r0 = r0.iterator()
        L21:
            boolean r4 = r0.hasNext()
            if (r4 == 0) goto La1
            java.lang.Object r4 = r0.next()
            android.content.pm.ResolveInfo r4 = (android.content.pm.ResolveInfo) r4
            android.content.pm.ActivityInfo r5 = r4.activityInfo
            java.lang.String r6 = r5.packageName
            if (r6 == 0) goto L21
            java.lang.CharSequence r5 = r5.loadLabel(r9)
            java.lang.String r5 = r5.toString()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "getAllInstalledApps: "
            r7.append(r8)
            r7.append(r5)
            java.lang.String r7 = r7.toString()
            java.lang.String r8 = "CommonKeyCustomDialogFragment"
            android.util.Log.d(r8, r7)
            java.lang.String r7 = "BT-2"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L5f
            android.content.pm.ActivityInfo r5 = r4.activityInfo
            java.lang.String r7 = r5.packageName
            java.lang.String r5 = r5.name
        L5f:
            java.util.List<java.lang.String> r5 = com.android.settings.common.dialog.CommonKeyCustomDialogFragment.NaviFilterList
            if (r5 == 0) goto L75
            int r5 = r5.size()
            if (r5 <= 0) goto L75
            java.util.List<java.lang.String> r5 = com.android.settings.common.dialog.CommonKeyCustomDialogFragment.NaviFilterList
            boolean r5 = r5.contains(r6)
            if (r5 == 0) goto L21
            r1.add(r4)
            goto L21
        L75:
            java.lang.String r5 = "com.syu."
            boolean r5 = r6.startsWith(r5)
            if (r5 != 0) goto L9a
            java.lang.String r5 = "com.fyt."
            boolean r5 = r6.startsWith(r5)
            if (r5 == 0) goto L86
            goto L9a
        L86:
            r5 = r3
        L87:
            java.lang.String[] r7 = com.android.settings.common.dialog.CommonKeyCustomDialogFragment.hideApkPackage
            int r8 = r7.length
            if (r5 >= r8) goto L98
            r7 = r7[r5]
            boolean r7 = r6.equals(r7)
            if (r7 == 0) goto L95
            goto L9a
        L95:
            int r5 = r5 + 1
            goto L87
        L98:
            r5 = 1
            goto L9b
        L9a:
            r5 = r3
        L9b:
            if (r5 == 0) goto L21
            r1.add(r4)
            goto L21
        La1:
            boolean r9 = com.android.settings.common.dialog.CommonKeyCustomDialogFragment.hasFilter
            if (r9 == 0) goto La8
            r1.add(r2)
        La8:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.common.dialog.CommonKeyCustomDialogFragment.getAllInstalledApps(android.content.pm.PackageManager):java.util.ArrayList");
    }
}
