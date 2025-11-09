package com.android.settings.secondaryscreen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.common.data.AppData;
import com.android.settings.core.SettingsBaseActivity;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SecondaryScreenDashboardFragment extends Fragment {
    private static String TAG = "SecondaryScreenDashboardFragment";
    private static Uri secondary_app = Uri.parse("content://com.syu.settingsProvider/secondaryscreen");
    private ListView mAppList;
    private View mRootView;
    private List<AppData> allApp = new ArrayList();
    private JSONObject Data = new JSONObject();

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onCheckChanged(boolean z, int i);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R$layout.secondary_screen_layout, viewGroup, false);
        initViews();
        return this.mRootView;
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        this.mAppList = (ListView) this.mRootView.findViewById(R$id.secondary_screen_app_list);
        readData();
        initData();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initData() {
        int size;
        PackageManager packageManager = getContext().getPackageManager();
        this.allApp.clear();
        getContext().getContentResolver().update(secondary_app, new ContentValues(), this.Data.toString(), null);
        List<AppData> allInstalledApps = getAllInstalledApps(packageManager);
        this.allApp = allInstalledApps;
        if (SettingsApplication.mHeightFix < 1000) {
            if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 120) {
                size = (int) ((this.allApp.size() * 47.6d) + 20.0d);
            } else {
                size = (this.allApp.size() * 70) + 20;
            }
            String str = TAG;
            Log.d(str, "initviews: " + size + "  " + this.allApp.size());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, size);
            layoutParams.setMargins(90, 10, 10, 10);
            this.mAppList.setLayoutParams(layoutParams);
        } else {
            int size2 = (allInstalledApps.size() * 105) + 20;
            String str2 = TAG;
            Log.d(str2, "initviews: " + size2);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, size2);
            layoutParams2.setMargins(90, 10, 10, 10);
            this.mAppList.setLayoutParams(layoutParams2);
        }
        SecondaryScreenAppAdapter secondaryScreenAppAdapter = new SecondaryScreenAppAdapter(getContext(), this.allApp);
        this.mAppList.setAdapter((ListAdapter) secondaryScreenAppAdapter);
        secondaryScreenAppAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.secondaryscreen.SecondaryScreenDashboardFragment.1
            @Override // com.android.settings.secondaryscreen.SecondaryScreenDashboardFragment.OnItemClickListener
            public void onCheckChanged(boolean z, int i) {
                AppData appData = (AppData) SecondaryScreenDashboardFragment.this.allApp.get(i);
                String str3 = appData.getmName();
                String str4 = SecondaryScreenDashboardFragment.TAG;
                Log.d(str4, "onCheckChanged: " + str3);
                SecondaryScreenDashboardFragment.this.sendAppMsg(str3, z ^ true);
                if (z) {
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("packageName", appData.getmName());
                        jSONObject.put("className", appData.getClassName());
                        jSONObject.put("labelName", appData.getLabelName());
                        SecondaryScreenDashboardFragment.this.Data.put(str3, jSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    SecondaryScreenDashboardFragment.this.Data.remove(str3);
                }
                SecondaryScreenDashboardFragment.this.initData();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAppMsg(String str, boolean z) {
        Intent intent = new Intent();
        intent.setAction("com.syu.secondary_app_close");
        intent.putExtra("package", str);
        intent.putExtra("isOpen", z);
        getContext().sendBroadcast(intent);
    }

    public List<AppData> getAllInstalledApps(PackageManager packageManager) {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        ArrayList arrayList = new ArrayList();
        arrayList.clear();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null) {
            return arrayList;
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            AppData appData = new AppData();
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String str = activityInfo.packageName;
            String str2 = activityInfo.name;
            String charSequence = activityInfo.loadLabel(packageManager).toString();
            String str3 = resolveInfo.activityInfo.packageName;
            if (!isHideApp(str)) {
                appData.setmName(str);
                appData.setClassName(str2);
                appData.setLabelName(charSequence);
                boolean z = true;
                if (this.Data != null) {
                    for (int i = 0; i < this.Data.length(); i++) {
                        try {
                            if (this.Data.has(str)) {
                                JSONObject jSONObject = (JSONObject) this.Data.get(str);
                                if (str.equals(jSONObject.getString("packageName")) && str2.equals(jSONObject.getString("className"))) {
                                    z = false;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                appData.setAutoStart(z);
                appData.setmIcon(resolveInfo.activityInfo.loadIcon(packageManager));
                arrayList.add(appData);
            }
        }
        return arrayList;
    }

    public void readData() {
        JSONObject jSONObject = new JSONObject();
        Cursor query = getContext().getContentResolver().query(secondary_app, null, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                String string = query.getString(0);
                String string2 = query.getString(1);
                String string3 = query.getString(2);
                JSONObject jSONObject2 = new JSONObject();
                try {
                    jSONObject2.put("packageName", string);
                    jSONObject2.put("className", string2);
                    jSONObject2.put("labelName", string3);
                    jSONObject.put(string, jSONObject2);
                } catch (JSONException e) {
                    String str = TAG;
                    Log.d(str, "readData: " + e);
                    e.printStackTrace();
                }
            }
        }
        this.Data = jSONObject;
    }

    private boolean isHideApp(String str) {
        return (str.contains("com.syu") && !str.equals("com.syu.radio")) || str.contains("com.lsec") || str.startsWith("com.android.set") || str.startsWith("com.fyt") || str.startsWith("net.easyconn") || str.equals("com.sprd.logmanager") || str.equals("com.android.calculator2") || str.equals(SystemProperties.get("persist.launcher.embedded_pkg", "")) || str.equals(SystemProperties.get("persist.launcher.packagename", ""));
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        Toolbar toolbar;
        FragmentActivity activity = getActivity();
        if ((activity instanceof SettingsBaseActivity) && (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) != null) {
            toolbar.setTitle(getResources().getString(R$string.secondary_screen_app_setting));
        }
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SecondaryScreenAppAdapter extends BaseAdapter {
        private Context mContext;
        private List<AppData> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public SecondaryScreenAppAdapter(Context context, List<AppData> list) {
            this.mContext = context;
            this.mData = list;
            String str = SecondaryScreenDashboardFragment.TAG;
            Log.d(str, "SecondaryScreenAppAdapter: " + list.size());
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
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_secondary_screen, viewGroup, false);
                holder.mLayout = (RelativeLayout) view2.findViewById(R$id.item_auto_start_ly);
                holder.mIcon = (ImageView) view2.findViewById(R$id.app_icon);
                holder.mTitle = (TextView) view2.findViewById(R$id.app_name);
                holder.mAutoStart = (Switch) view2.findViewById(R$id.app_auto_start_switch);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            final boolean isAutoStart = this.mData.get(i).isAutoStart();
            holder.mTitle.setText(this.mData.get(i).getLabelName());
            holder.mIcon.setImageDrawable(this.mData.get(i).getmIcon());
            holder.mAutoStart.setChecked(isAutoStart);
            holder.mAutoStart.setClickable(false);
            holder.mLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.secondaryscreen.SecondaryScreenDashboardFragment.SecondaryScreenAppAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    String str = SecondaryScreenDashboardFragment.TAG;
                    Log.d(str, "mLayout onClick: " + i);
                    if (SecondaryScreenAppAdapter.this.mListener != null) {
                        SecondaryScreenAppAdapter.this.mListener.onCheckChanged(isAutoStart, i);
                    }
                }
            });
            return view2;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mListener = onItemClickListener;
        }

        /* loaded from: classes.dex */
        class Holder {
            Switch mAutoStart;
            ImageView mIcon;
            RelativeLayout mLayout;
            TextView mTitle;

            Holder() {
            }
        }
    }
}
