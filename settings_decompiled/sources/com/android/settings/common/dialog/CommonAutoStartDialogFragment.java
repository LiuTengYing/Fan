package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.common.data.AppData;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class CommonAutoStartDialogFragment extends InstrumentedDialogFragment {
    public static boolean hasFilter = false;
    private GridView gridView;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    private static Uri uri_autostart = Uri.parse("content://com.syu.settingsProvider/autostartlist");
    private static Uri uri_filter = Uri.parse("content://com.android.systemUIProvider/filterlist");
    public static HashMap<String, Drawable> mapIcon = new HashMap<>();
    private static Uri uri = uri_autostart;
    private static boolean isLocalAPKVisible = true;
    private Uri uri_autostart_old = Uri.parse("content://com.syu.settingsProvider/autostartlist");
    private JSONObject Data = new JSONObject();
    private int count = 0;
    String[] hide_For_Projections = {"com.android.messaging", "com.google.android.googlequicksearchbox", "com.google.android.apps.googleassistant", "com.android.settings"};
    private List<AppData> mAppList = new ArrayList();

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onCheckChanged(boolean z, int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("CommonAutoStartDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            CommonAutoStartDialogFragment commonAutoStartDialogFragment = new CommonAutoStartDialogFragment();
            commonAutoStartDialogFragment.setArguments(bundle);
            commonAutoStartDialogFragment.show(childFragmentManager, "CommonAutoStartDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        if (SettingsApplication.mHeightFix > 1000) {
            this.params = new ViewGroup.LayoutParams(windowManeger[0], windowManeger[1] - 100);
        } else {
            this.params = new ViewGroup.LayoutParams(windowManeger[0], windowManeger[1] - 60);
        }
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_auto_start_dialog, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2008);
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
        uri = uri_autostart;
        if (!SystemProperties.getBoolean("persist.lsec.hassystemuidataprovider", false)) {
            uri = this.uri_autostart_old;
        }
        this.gridView = (GridView) this.mRootView.findViewById(R$id.navi_app_gridview);
        readData();
        updateData();
    }

    public void updateData() {
        this.mAppList.clear();
        getContext().getContentResolver().update(uri, new ContentValues(), this.Data.toString(), null);
        this.mAppList = getAllInstalledApps(getContext().getPackageManager());
        AutoStartAppAdapter autoStartAppAdapter = new AutoStartAppAdapter(getContext(), this.mAppList);
        this.gridView.setAdapter((ListAdapter) autoStartAppAdapter);
        autoStartAppAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.common.dialog.CommonAutoStartDialogFragment.1
            @Override // com.android.settings.common.dialog.CommonAutoStartDialogFragment.OnItemClickListener
            public void onCheckChanged(boolean z, int i) {
                AppData appData = (AppData) CommonAutoStartDialogFragment.this.mAppList.get(i);
                Log.d("CommonAutoStartDialogFragment", "onCheckChanged: " + z + i);
                String str = appData.getmName();
                if (!z) {
                    if (CommonAutoStartDialogFragment.this.Data.length() < 3) {
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("packageName", appData.getmName());
                            jSONObject.put("className", appData.getClassName());
                            jSONObject.put("labelName", appData.getLabelName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            CommonAutoStartDialogFragment.this.Data.put(str, jSONObject);
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CommonAutoStartDialogFragment.this.getContext(), CommonAutoStartDialogFragment.this.getContext().getResources().getString(R$string.autostart_toomuch), 0).show();
                    }
                } else {
                    CommonAutoStartDialogFragment.this.Data.remove(str);
                }
                Log.d("CommonAutoStartDialogFragment", "onCheckChanged: count ===" + CommonAutoStartDialogFragment.this.count);
                CommonAutoStartDialogFragment.this.updateData();
            }
        });
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.select_app_title)).setText(str);
    }

    public List<AppData> getAllInstalledApps(PackageManager packageManager) {
        boolean z;
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        ArrayList arrayList = new ArrayList();
        arrayList.clear();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null) {
            return arrayList;
        }
        Cursor query = getContext().getContentResolver().query(uri_filter, null, null, null, null);
        ArrayList arrayList2 = new ArrayList();
        if (query != null) {
            while (query.moveToNext()) {
                arrayList2.add(query.getString(0));
            }
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            AppData appData = new AppData();
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String str = activityInfo.packageName;
            String str2 = activityInfo.name;
            String charSequence = activityInfo.loadLabel(packageManager).toString();
            String str3 = resolveInfo.activityInfo.packageName;
            if (!arrayList2.contains(str) && (!"BT-2".equals(charSequence) || ("com.android.launcher6.BT2Activity".equals(str2) && "com.android.launcher6".equals(str)))) {
                if ((!str.startsWith("com.syu.") && !str.startsWith("com.fyt.") && !str.startsWith("com.android.calculator")) || isLocalAPKVisible) {
                    int i = 0;
                    while (true) {
                        String[] strArr = this.hide_For_Projections;
                        if (i < strArr.length) {
                            if (str.equals(strArr[i])) {
                                break;
                            }
                            i++;
                        } else {
                            appData.setmName(str);
                            appData.setClassName(str2);
                            appData.setLabelName(charSequence);
                            if (this.Data != null) {
                                z = false;
                                for (int i2 = 0; i2 < this.Data.length(); i2++) {
                                    try {
                                        if (this.Data.has(str)) {
                                            JSONObject jSONObject = (JSONObject) this.Data.get(str);
                                            if (str.equals(jSONObject.getString("packageName")) && str2.equals(jSONObject.getString("className"))) {
                                                z = true;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                z = false;
                            }
                            appData.setAutoStart(z);
                            appData.setmIcon(resolveInfo.activityInfo.loadIcon(packageManager));
                            arrayList.add(appData);
                        }
                    }
                }
            }
        }
        if (hasFilter) {
            arrayList.add(null);
        }
        return arrayList;
    }

    public void readData() {
        JSONObject jSONObject = new JSONObject();
        Cursor query = getContext().getContentResolver().query(uri, null, null, null, null);
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
                    Log.d("CommonAutoStartDialogFragment", "readData: " + e);
                    e.printStackTrace();
                }
            }
        }
        this.Data = jSONObject;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class AutoStartAppAdapter extends BaseAdapter {
        private Context mContext;
        private List<AppData> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public AutoStartAppAdapter(Context context, List<AppData> list) {
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
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_auto_start, viewGroup, false);
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
            Log.d("CommonAutoStartDialogFragment", "getView: " + CommonAutoStartDialogFragment.this.Data.length() + "   " + holder.mAutoStart.isChecked());
            holder.mAutoStart.setClickable(false);
            holder.mLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonAutoStartDialogFragment.AutoStartAppAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    Log.d("CommonAutoStartDialogFragment", "mLayout onClick: ");
                    if (AutoStartAppAdapter.this.mListener != null) {
                        AutoStartAppAdapter.this.mListener.onCheckChanged(isAutoStart, i);
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
