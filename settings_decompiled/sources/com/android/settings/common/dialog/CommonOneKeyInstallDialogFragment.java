package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$color;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.oneKey.InstallListener;
import com.android.settings.oneKey.InstallResult;
import com.android.settings.oneKey.SilencesInstall;
import com.android.settings.utils.FileUtil;
import com.syu.util.FuncUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class CommonOneKeyInstallDialogFragment extends InstrumentedDialogFragment {
    private TextView emptyList;
    private SilencesInstall install;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private LinearLayout mEmptyListLayout;
    private TextView mInstallProgress;
    private View mRootView;
    private Button mScan;
    private ViewGroup.LayoutParams params;
    public List<AppInfo> appList = new ArrayList();
    private String directoryPath = "/sdcard/onekey";
    private int position = 0;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, String str2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonOneKeyInstallDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonOneKeyInstallDialogFragment commonOneKeyInstallDialogFragment = new CommonOneKeyInstallDialogFragment();
            commonOneKeyInstallDialogFragment.setArguments(bundle);
            commonOneKeyInstallDialogFragment.show(childFragmentManager, "CommonOneKeyInstallDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams((windowManeger[0] / 4) * 3, (windowManeger[1] / 4) * 3);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_onekey_install_list_dialog, null);
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
        this.mBtnCancel = (Button) this.mRootView.findViewById(R$id.one_key_cancel_new);
        this.mBtnConfirm = (Button) this.mRootView.findViewById(R$id.one_key_confirm_new);
        this.mInstallProgress = (TextView) this.mRootView.findViewById(R$id.app_list_path);
        this.mEmptyListLayout = (LinearLayout) this.mRootView.findViewById(R$id.empty_layout);
        this.emptyList = (TextView) this.mRootView.findViewById(R$id.empty_list);
        this.mScan = (Button) this.mRootView.findViewById(R$id.one_key_scan);
        TextView textView = this.mInstallProgress;
        textView.setText(getContext().getResources().getString(R$string.common_scan_app) + " : 0 /" + this.appList.size());
        this.mBtnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonOneKeyInstallDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CommonOneKeyInstallDialogFragment.this.appList.size() > 0) {
                    CommonOneKeyInstallDialogFragment.this.position = 0;
                    CommonOneKeyInstallDialogFragment.this.startOneKey(0);
                    CommonOneKeyInstallDialogFragment.this.mBtnCancel.setClickable(false);
                    CommonOneKeyInstallDialogFragment.this.mBtnConfirm.setClickable(false);
                    return;
                }
                CommonOneKeyInstallDialogFragment.this.dismiss();
            }
        });
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonOneKeyInstallDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CommonOneKeyInstallDialogFragment.this.dismiss();
            }
        });
        InstallResult.getmInstance().setListener(new InstallListener() { // from class: com.android.settings.common.dialog.CommonOneKeyInstallDialogFragment.3
            @Override // com.android.settings.oneKey.InstallListener
            public void success() {
                CommonOneKeyInstallDialogFragment.this.position++;
                if (CommonOneKeyInstallDialogFragment.this.position < CommonOneKeyInstallDialogFragment.this.appList.size()) {
                    CommonOneKeyInstallDialogFragment commonOneKeyInstallDialogFragment = CommonOneKeyInstallDialogFragment.this;
                    commonOneKeyInstallDialogFragment.startOneKey(commonOneKeyInstallDialogFragment.position);
                    return;
                }
                CommonOneKeyInstallDialogFragment.this.mBtnCancel.setClickable(true);
                CommonOneKeyInstallDialogFragment.this.mBtnConfirm.setClickable(true);
                SettingsApplication settingsApplication = SettingsApplication.mApplication;
                Toast.makeText(settingsApplication, settingsApplication.getString(R$string.common_one_key_install_complete), 1).show();
                CommonOneKeyInstallDialogFragment.this.dismiss();
            }

            @Override // com.android.settings.oneKey.InstallListener
            public void failed(String str) {
                if (str != null) {
                    Log.d("CommonOneKeyInstallDialogFragment", "failed: " + str);
                }
                CommonOneKeyInstallDialogFragment.this.position++;
                if (CommonOneKeyInstallDialogFragment.this.position < CommonOneKeyInstallDialogFragment.this.appList.size()) {
                    CommonOneKeyInstallDialogFragment commonOneKeyInstallDialogFragment = CommonOneKeyInstallDialogFragment.this;
                    commonOneKeyInstallDialogFragment.startOneKey(commonOneKeyInstallDialogFragment.position);
                    return;
                }
                CommonOneKeyInstallDialogFragment.this.mBtnCancel.setClickable(true);
                CommonOneKeyInstallDialogFragment.this.mBtnConfirm.setClickable(true);
                SettingsApplication settingsApplication = SettingsApplication.mApplication;
                Toast.makeText(settingsApplication, settingsApplication.getString(R$string.common_one_key_install_complete), 1).show();
                CommonOneKeyInstallDialogFragment.this.dismiss();
            }
        });
        this.mScan.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonOneKeyInstallDialogFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d("CommonOneKeyInstallDialogFragment", "onClick: onScan onClick");
                CommonOneKeyInstallDialogFragment.this.mScan.setClickable(false);
                CommonOneKeyInstallDialogFragment.this.updateList();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateList() {
        if (!TextUtils.isEmpty(getUsbPath())) {
            this.directoryPath = getUsbPath() + "/onekey";
        }
        this.install = new SilencesInstall(getContext());
        Iterator<String> it = scanFiles(this.directoryPath).iterator();
        while (it.hasNext()) {
            Context context = getContext();
            AppInfo appInfo = getAppInfo(context, this.directoryPath + "/" + it.next());
            if (appInfo != null && !FuncUtils.isAppInstalled(getContext(), appInfo.packageName)) {
                this.appList.add(appInfo);
            }
        }
        TextView textView = this.mInstallProgress;
        textView.setText(getContext().getResources().getString(R$string.common_scan_app) + " : 0 /" + this.appList.size());
        GridView gridView = (GridView) this.mRootView.findViewById(R$id.oneinstall_list);
        if (this.appList.size() > 0) {
            this.mEmptyListLayout.setVisibility(8);
            gridView.setVisibility(0);
            gridView.setAdapter((ListAdapter) new AppListAdapter(SettingsApplication.mApplication, this.appList));
            return;
        }
        this.mEmptyListLayout.setVisibility(0);
        gridView.setVisibility(8);
        this.mScan.setClickable(true);
    }

    private String getUsbPath() {
        for (String str : getUsbPaths(getContext())) {
            if (isDirectoryExists(str)) {
                return str;
            }
        }
        return "";
    }

    public static List<String> getUsbPaths(Context context) {
        String[] strArr;
        ArrayList arrayList = new ArrayList();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService("storage");
            for (String str : (String[]) StorageManager.class.getMethod("getVolumePaths", new Class[0]).invoke(storageManager, new Object[0])) {
                Object invoke = StorageManager.class.getMethod("getVolumeState", String.class).invoke(storageManager, str);
                if (!str.contains("emulated") && "mounted".equals(invoke)) {
                    arrayList.add(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private boolean isDirectoryExists(String str) {
        Log.d("CommonOneKeyInstallDialogFragment", "isDirectoryExists: " + str);
        File file = new File(str + "/onekey");
        if (file.exists() && file.isDirectory()) {
            Log.d("CommonOneKeyInstallDialogFragment", "isDirectoryExists: true");
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startOneKey(int i) {
        SilencesInstall silencesInstall = this.install;
        if (silencesInstall == null) {
            Log.d("CommonOneKeyInstallDialogFragment", "startOneKey: install is null");
            return;
        }
        try {
            silencesInstall.installApp(this.appList.get(i).path);
            this.mInstallProgress.setTextColor(getContext().getResources().getColor(R$color.red));
            TextView textView = this.mInstallProgress;
            textView.setText(getContext().getResources().getString(R$string.common_one_key_installing) + " : " + (i + 1) + "/" + this.appList.size());
        } catch (Exception e) {
            Log.d("CommonOneKeyInstallDialogFragment", "startOneKey: " + e.toString());
        }
    }

    public List<String> scanFiles(String str) {
        File[] listFiles;
        File file = new File(str);
        ArrayList arrayList = new ArrayList();
        if (file.isDirectory() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
                if (file2.isFile()) {
                    String name = file2.getName();
                    Log.d("CommonOneKeyInstallDialogFragment", "scanFiles: " + name);
                    if (name.endsWith(".apk")) {
                        arrayList.add(name);
                    }
                }
            }
        }
        return arrayList;
    }

    public AppInfo getAppInfo(Context context, String str) {
        ApplicationInfo applicationInfo;
        Resources resource = FileUtil.getResource(context, str);
        PackageInfo packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(str, 1);
        if (packageArchiveInfo != null && (applicationInfo = packageArchiveInfo.applicationInfo) != null && applicationInfo.icon > 0) {
            try {
                AppInfo appInfo = new AppInfo();
                Drawable drawable = resource.getDrawable(applicationInfo.icon);
                resource.getAssets().close();
                appInfo.inco = drawable;
                appInfo.packageName = applicationInfo.packageName;
                appInfo.path = str;
                return appInfo;
            } catch (Exception e) {
                Log.d("CommonOneKeyInstallDialogFragment", "getAppInfo: " + e.toString());
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class AppListAdapter extends BaseAdapter {
        private Context mContext;
        private List<AppInfo> mData;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public AppListAdapter(Context context, List<AppInfo> list) {
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.app_list_item_one_key, viewGroup, false);
                holder.mTitle = (TextView) view2.findViewById(R$id.app_name);
                holder.mIcon = (ImageView) view2.findViewById(R$id.app_icon);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            holder.mTitle.setText(this.mData.get(i).packageName);
            holder.mIcon.setImageDrawable(this.mData.get(i).inco);
            return view2;
        }

        /* loaded from: classes.dex */
        class Holder {
            ImageView mIcon;
            TextView mTitle;

            Holder() {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class AppInfo {
        public Drawable inco;
        public String packageName;
        public String path;

        AppInfo() {
        }
    }
}
