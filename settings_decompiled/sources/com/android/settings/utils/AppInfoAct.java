package com.android.settings.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AppInfoAct extends Fragment {
    private myAdapter adapter;
    private ListView appListView;
    private ArrayList<Info> list;
    private Bundle mBundle;
    private View mContainerView;
    private LinearLayout mListLayout;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String verTime;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mContainerView = layoutInflater.inflate(R$layout.device_info_appinfo, viewGroup, false);
        initviews();
        return this.mContainerView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        Toolbar toolbar;
        FragmentActivity activity = getActivity();
        if ((activity instanceof SettingsBaseActivity) && (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) != null) {
            if ("zsender".equals(SystemProperties.get("persist.lsec.usbtype", ""))) {
                toolbar.setTitle(getResources().getString(R$string.about_carapp_vision_box));
            } else {
                toolbar.setTitle(getResources().getString(R$string.about_carapp_vision));
            }
        }
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.list.clear();
    }

    private void initviews() {
        int size;
        this.mListLayout = (LinearLayout) this.mContainerView.findViewById(R$id.appinfo_ly);
        this.appListView = (ListView) this.mContainerView.findViewById(R$id.appinfo_list);
        getAppInfo();
        if (SettingsApplication.mHeightFix != 1200) {
            if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 120) {
                size = (int) ((this.list.size() * 47.6d) + 20.0d);
            } else {
                size = (this.list.size() * 70) + 20;
            }
            Log.d("AppInfoAct", "initviews: " + size + "  " + this.list.size());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, size);
            layoutParams.setMargins(60, 10, 10, 10);
            this.appListView.setLayoutParams(layoutParams);
        } else {
            int size2 = (this.list.size() * 80) + 20;
            Log.d("AppInfoAct", "initviews: " + size2);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, size2);
            layoutParams2.setMargins(60, 10, 10, 10);
            this.appListView.setLayoutParams(layoutParams2);
        }
        myAdapter myadapter = new myAdapter(this.list, getActivity());
        this.adapter = myadapter;
        this.appListView.setAdapter((ListAdapter) myadapter);
    }

    private void getAppInfo() {
        this.list = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        ApplicationInfo applicationInfo = null;
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            Info info = new Info();
            info.setAppIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            info.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            info.setPackageName(packageInfo.applicationInfo.packageName);
            try {
                applicationInfo = packageManager.getApplicationInfo(packageInfo.applicationInfo.packageName, 128);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.verTime = "";
            if (applicationInfo != null) {
                Bundle bundle = applicationInfo.metaData;
                this.mBundle = bundle;
                if (bundle != null) {
                    this.verTime = bundle.getString("compile");
                }
            }
            String str = packageInfo.versionName;
            if (str != null && str.length() > 0) {
                if (packageInfo.packageName.equals("com.android.settings")) {
                    info.setAppVersion("v1.0");
                } else {
                    info.setAppVersion("v" + packageInfo.versionName);
                }
            }
            String str2 = this.verTime;
            if (str2 != null && str2.length() > 0) {
                if (this.verTime.length() > 0 && this.verTime.contains("/")) {
                    info.setAppTime(this.verTime.split("/")[0]);
                } else {
                    info.setAppTime(this.verTime);
                }
            }
            Log.d("fangli", "packageInfo.applicationInfo.packageName ===" + packageInfo.applicationInfo.packageName);
            if (checkByPkg(packageInfo.applicationInfo.packageName) && !packageInfo.applicationInfo.packageName.equals("com.syu.uinfo")) {
                this.list.add(info);
            }
        }
    }

    public boolean checkByPkg(String str) {
        return str.startsWith("com.syu") || str.startsWith("com.fyt") || str.startsWith("com.android.launcher") || str.equals("com.android.systemui") || str.equals("com.android.settings") || str.equals("com.vipercn.viper4android_v2") || str.equals("com.android.calculator2");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class myAdapter extends BaseAdapter {
        private ArrayList<Info> appList;
        private Context context;
        private LayoutInflater inflater;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public myAdapter(ArrayList<Info> arrayList, Context context) {
            this.appList = arrayList;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.appList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.appList.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view2 = this.inflater.inflate(R$layout.device_info_item, (ViewGroup) null);
                holder.imageView = (ImageView) view2.findViewById(R$id.app_icon);
                holder.appName = (TextView) view2.findViewById(R$id.app_name);
                holder.appVersion = (TextView) view2.findViewById(R$id.app_version);
                holder.appTime = (TextView) view2.findViewById(R$id.app_time);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            Info info = this.appList.get(i);
            holder.imageView.setImageDrawable(info.getAppIcon());
            holder.appName.setText(info.getAppName());
            holder.appVersion.setText(info.getAppVersion());
            holder.appTime.setText(info.getAppTime());
            holder.appTime.requestFocus();
            if (SettingsApplication.mHeightFix > SettingsApplication.mWidthFix) {
                holder.appName.setTextSize(18.0f);
                holder.appVersion.setTextSize(18.0f);
                holder.appTime.setTextSize(18.0f);
            }
            return view2;
        }

        /* loaded from: classes.dex */
        private class Holder {
            TextView appName;
            TextView appTime;
            TextView appVersion;
            ImageView imageView;

            private Holder() {
            }
        }
    }
}
