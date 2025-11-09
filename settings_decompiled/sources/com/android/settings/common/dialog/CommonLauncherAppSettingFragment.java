package com.android.settings.common.dialog;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import com.android.settings.common.adapter.LauncherAppAdapter;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class CommonLauncherAppSettingFragment extends InstrumentedDialogFragment {
    public static String mPackageName = "";
    private LauncherAppAdapter appAdapter;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private ResolveInfo mCurrentApp;
    private TextView mReset;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    private static String[] showApkPackage = {"com.teyes.carkit", "com.autonavi.amapauto", "ru.yandex.yandexnavi", "com.syu.camera360", "com.syu.frontvideo", "com.syu.rearcamera", "com.apkpure.aegon", "ru.dublgis.dgismobile", "com.google.android.apps.maps", "com.waze", "com.macsoftex.antiradar.free", "com.sygic.aura", "vn.autoai.android", "com.mgoogle.android.gms", "com.navitel", "com.vanced.android.youtube", "com.zing.mp3", "com.autonavi.minimap", "com.baidu.BaiduMap", "com.tencent.map", "ru.yandex.yandexmaps", "com.williexing.android.apps.xcdvr1", "com.vietmap.s2OBU", "com.ankai.cardvr", "com.skt.tmap.ku", "com.sygic.trackingworld", "com.google.android.youtube", "com.iloen.melon", "com.locnall.KimGiSa", "com.kingwaytek.naviking3d", "com.nng.igo.primong.igoworld", "com.basarsoft.igonextgen.javaclient", "com.nng.igoprimoisr2013march24.javaclient", "com.nng.igoprimoisr.sdjavaclient", "com.navngo.igo.javaclient", "com.nng.igoprimoisrael.javaclient", "com.basarsoft.igo.javaclient", "com.nng.igo.primong", "com.nng.igo.primong.palestine", "com.basarsoft.igo.javaclient", "com.vietmap.s1OBU", "com.here.app.maps", "OziExplorer.Main", "com.papago.M11SGMY", "com.kingwaytek.navikinggi", "com.google.android.tts", "com.papago.M11_Int", "jp.co.datawest.android.navi", "com.ankai.edog", "com.tencent.wecarnavi", "com.mxnavi.mxnavi", "cld.navi.c2198.mainframe,", "cld.navi.c3525.mainframe", "cld.navi.c2802.mainframe", "title.navi", "com.mapbar.android.mapbarmap", "cld.navi.mobile.mainframe", "com.baidu.navi", "com.uu.uunavi", "com.raxtone.flynavi", "com.pdager", "com.mapbar.android.mapnavi", "com.autonavi.xmgd.navigator", "cn.com.tiros.android.navidog4x", "cn.com.tiros.android.navidog", "com.netflix.mediaclient", "com.nhn.android.nmap", "com.mnsoft.mappyobn", "com.ktmusic.geniemusic", "com.frograms.wplay", "kr.mappers.AtlanSmart", "kr.co.captv.pooqV2", "com.syu.fourcamera2", "com.fvsm.camera", "com.kingwaytek.naviking4g", "kr.co.kbs.kong", "com.imbc.mini", "com.google.android.apps.youtube.kids", "com.google.android.apps.youtube.music", "com.appgate.gorealra", "com.disneyplus.mea", "com.kingwaytek", "com.kingwaytek.NaviKing"};
    private static String[] showApkPackage_xingyuan = {"com.autonavi.amapauto"};
    public static String[] filterForLauncher8 = {"com.teyes.carkit", "com.syu.camera360"};

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonDialog") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonLauncherAppSettingFragment commonLauncherAppSettingFragment = new CommonLauncherAppSettingFragment();
            commonLauncherAppSettingFragment.setArguments(bundle);
            commonLauncherAppSettingFragment.show(childFragmentManager, "CommonDialog");
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

    private void init() {
        PackageManager packageManager = getContext().getPackageManager();
        GridView gridView = (GridView) this.mRootView.findViewById(R$id.navi_app_gridview);
        this.mBtnConfirm = (Button) this.mRootView.findViewById(R$id.navi_app_confirm);
        this.mBtnCancel = (Button) this.mRootView.findViewById(R$id.navi_app_cancel);
        this.mReset = (TextView) this.mRootView.findViewById(R$id.select_app_reset);
        final ArrayList<ResolveInfo> allInstalledApps = getAllInstalledApps(packageManager);
        LauncherAppAdapter launcherAppAdapter = new LauncherAppAdapter(getContext(), allInstalledApps, packageManager);
        this.appAdapter = launcherAppAdapter;
        gridView.setAdapter((ListAdapter) launcherAppAdapter);
        this.appAdapter.setSelectPackage(SystemProperties.get("persist.launcher.packagename", "null"));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.common.dialog.CommonLauncherAppSettingFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ResolveInfo resolveInfo = (ResolveInfo) allInstalledApps.get(i);
                CommonLauncherAppSettingFragment.this.mCurrentApp = resolveInfo;
                CommonLauncherAppSettingFragment.this.appAdapter.setSelectPackage(resolveInfo.activityInfo.packageName);
                CommonLauncherAppSettingFragment.this.appAdapter.notifyDataSetChanged();
            }
        });
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonLauncherAppSettingFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CommonLauncherAppSettingFragment.this.mCurrentApp != null) {
                    CommonLauncherAppSettingFragment commonLauncherAppSettingFragment = CommonLauncherAppSettingFragment.this;
                    commonLauncherAppSettingFragment.setLauncher(commonLauncherAppSettingFragment.mCurrentApp);
                }
                CommonLauncherAppSettingFragment.this.dismiss();
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonLauncherAppSettingFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonLauncherAppSettingFragment.this.dismiss();
            }
        });
        this.mReset.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonLauncherAppSettingFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonLauncherAppSettingFragment.this.resetLauncherApp();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetLauncherApp() {
        SystemProperties.set("persist.launcher.packagename", "null");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLauncher(ResolveInfo resolveInfo) {
        String str;
        Log.d("CommonDialog", "setLauncher: ");
        if (resolveInfo != null) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && (str = activityInfo.packageName) != null) {
                mPackageName = str.toString();
            } else {
                mPackageName = "";
            }
            String str2 = SystemProperties.get("persist.launcher.packagename", "null");
            SystemProperties.set("persist.launcher.packagename", mPackageName);
            if (str2 == null || "null".equals(str2)) {
                return;
            }
            ((ActivityManager) getContext().getSystemService("activity")).forceStopPackage(str2);
        }
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.select_app_title)).setText(str);
    }

    public ArrayList<ResolveInfo> getAllInstalledApps(PackageManager packageManager) {
        ActivityInfo activityInfo;
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        ArrayList<ResolveInfo> arrayList = new ArrayList<>();
        arrayList.clear();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null) {
            return arrayList;
        }
        boolean z = SystemProperties.getBoolean("persist.fyt.hidecam360lauset", false);
        Iterator<ResolveInfo> it = queryIntentActivities.iterator();
        while (it.hasNext()) {
            ResolveInfo next = it.next();
            String str = (next == null || (activityInfo = next.activityInfo) == null || (str = activityInfo.packageName) == null) ? "" : "";
            String[] strArr = showApkPackage;
            if (SystemProperties.getBoolean("persist.fyt.xyfortzy", false)) {
                strArr = showApkPackage_xingyuan;
            }
            if (str != null) {
                if ("pic".equals(str)) {
                    arrayList.add(next);
                } else if (!z || !str.equalsIgnoreCase("com.syu.fourcamera2")) {
                    if (strArr != null && strArr.length > 0) {
                        for (String str2 : strArr) {
                            if (str.equalsIgnoreCase(str2)) {
                                arrayList.add(next);
                            }
                        }
                    } else {
                        arrayList.add(next);
                    }
                }
            }
        }
        return arrayList;
    }
}
