package com.android.settings.development;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.settings.R$string;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class AppPicker extends ListActivity {
    private static final Comparator<MyApplicationInfo> sDisplayNameComparator = new Comparator<MyApplicationInfo>() { // from class: com.android.settings.development.AppPicker.1
        private final Collator collator = Collator.getInstance();

        @Override // java.util.Comparator
        public final int compare(MyApplicationInfo myApplicationInfo, MyApplicationInfo myApplicationInfo2) {
            return this.collator.compare(myApplicationInfo.label, myApplicationInfo2.label);
        }
    };
    private AppListAdapter mAdapter;
    private boolean mDebuggableOnly;
    private boolean mIncludeNothing;
    private boolean mNonSystemOnly;
    private String mPermissionName;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.mPermissionName = getIntent().getStringExtra("com.android.settings.extra.REQUESTIING_PERMISSION");
        this.mDebuggableOnly = getIntent().getBooleanExtra("com.android.settings.extra.DEBUGGABLE", false);
        this.mNonSystemOnly = getIntent().getBooleanExtra("com.android.settings.extra.NON_SYSTEM", false);
        this.mIncludeNothing = getIntent().getBooleanExtra("com.android.settings.extra.INCLUDE_NOTHING", true);
        AppListAdapter appListAdapter = new AppListAdapter(this);
        this.mAdapter = appListAdapter;
        if (appListAdapter.getCount() <= 0) {
            setResult(-2);
            finish();
            return;
        }
        setListAdapter(this.mAdapter);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            handleBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // android.app.ListActivity
    protected void onListItemClick(ListView listView, View view, int i, long j) {
        Intent intent = new Intent();
        ApplicationInfo applicationInfo = this.mAdapter.getItem(i).info;
        if (applicationInfo != null) {
            intent.setAction(applicationInfo.packageName);
        }
        setResult(-1, intent);
        finish();
    }

    private void handleBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
            return;
        }
        setResult(0);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyApplicationInfo {
        ApplicationInfo info;
        CharSequence label;

        MyApplicationInfo() {
        }
    }

    /* loaded from: classes.dex */
    public class AppListAdapter extends ArrayAdapter<MyApplicationInfo> {
        private final LayoutInflater mInflater;
        private final List<MyApplicationInfo> mPackageInfoList;

        public AppListAdapter(Context context) {
            super(context, 0);
            boolean z;
            this.mPackageInfoList = new ArrayList();
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            List<ApplicationInfo> installedApplications = context.getPackageManager().getInstalledApplications(0);
            for (int i = 0; i < installedApplications.size(); i++) {
                ApplicationInfo applicationInfo = installedApplications.get(i);
                if (!isHideAppFromList(applicationInfo) && (!AppPicker.this.mNonSystemOnly || !applicationInfo.isSystemApp())) {
                    if (AppPicker.this.mPermissionName != null) {
                        try {
                            String[] strArr = AppPicker.this.getPackageManager().getPackageInfo(applicationInfo.packageName, 4096).requestedPermissions;
                            if (strArr != null) {
                                int length = strArr.length;
                                int i2 = 0;
                                while (true) {
                                    if (i2 >= length) {
                                        z = false;
                                        break;
                                    } else if (strArr[i2].equals(AppPicker.this.mPermissionName)) {
                                        z = true;
                                        break;
                                    } else {
                                        i2++;
                                    }
                                }
                                if (!z) {
                                }
                            }
                        } catch (PackageManager.NameNotFoundException unused) {
                        }
                    }
                    MyApplicationInfo myApplicationInfo = new MyApplicationInfo();
                    myApplicationInfo.info = applicationInfo;
                    myApplicationInfo.label = applicationInfo.loadLabel(AppPicker.this.getPackageManager()).toString();
                    this.mPackageInfoList.add(myApplicationInfo);
                }
            }
            Collections.sort(this.mPackageInfoList, AppPicker.sDisplayNameComparator);
            if (AppPicker.this.mIncludeNothing) {
                MyApplicationInfo myApplicationInfo2 = new MyApplicationInfo();
                myApplicationInfo2.label = context.getText(R$string.no_application);
                this.mPackageInfoList.add(0, myApplicationInfo2);
            }
            addAll(this.mPackageInfoList);
        }

        private boolean isHideAppFromList(ApplicationInfo applicationInfo) {
            if (applicationInfo.uid == 1000 || "com.android.systemui".equals(applicationInfo.packageName) || "com.google.android.networkstack".equals(applicationInfo.packageName) || "com.google.android.cellbroadcastservice".equals(applicationInfo.packageName) || "com.google.android.networkstack.tethering".equals(applicationInfo.packageName)) {
                return true;
            }
            return AppPicker.this.mDebuggableOnly && (applicationInfo.flags & 2) == 0 && "user".equals(Build.TYPE);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            AppViewHolder createOrRecycle = AppViewHolder.createOrRecycle(this.mInflater, view);
            View view2 = createOrRecycle.rootView;
            MyApplicationInfo item = getItem(i);
            createOrRecycle.appName.setText(item.label);
            ApplicationInfo applicationInfo = item.info;
            if (applicationInfo != null) {
                createOrRecycle.appIcon.setImageDrawable(applicationInfo.loadIcon(AppPicker.this.getPackageManager()));
                createOrRecycle.summary.setText(item.info.packageName);
            } else {
                createOrRecycle.appIcon.setImageDrawable(null);
                createOrRecycle.summary.setText("");
            }
            createOrRecycle.disabled.setVisibility(8);
            createOrRecycle.widget.setVisibility(8);
            return view2;
        }
    }
}
