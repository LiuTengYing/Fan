package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.syu.util.FuncUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FctoryBackcarCameraDialogFragment extends InstrumentedDialogFragment {
    private static int PopChoiceItem_Name = 234;
    private static String TAG = "FctoryBackcarCameraDialogFragment";
    private static Fragment fragment;
    public static String mPkgName;
    public static Resources mResources;
    private BackCarCameraAdapter cameraAdapter;
    private ListView mCameraList;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    public List<SparseArray<String>> mListSelectBackup = new ArrayList();
    public int mCurSelectBackup = 0;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment2, String str) {
        FragmentManager childFragmentManager = fragment2.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FctoryBackcarCameraDialogFragment fctoryBackcarCameraDialogFragment = new FctoryBackcarCameraDialogFragment();
            fragment = fragment2;
            fctoryBackcarCameraDialogFragment.setArguments(bundle);
            fctoryBackcarCameraDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.factory_backcar_camera_dialog_layout, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        setTitle(string);
        init();
        return dialog;
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.backup_camera_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mCameraList = (ListView) this.mRootView.findViewById(R$id.backup_camera_list);
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        initData();
    }

    private void initData() {
        String readFile = readFile("/sys/fytver/fyt_bin_version");
        if (SettingsApplication.mCustomerid == 112) {
            if (FuncUtils.isAppInstalled(getContext(), "com.syu.us") && FuncUtils.isAppInstalled(getContext(), "com.syu.doublecamera") && readFile.contains("m2_v:Rn54") && (readFile.contains("s_v:Ls15") || readFile.contains("s_v:Ls15"))) {
                addChoice2List("sevenlight_default", this.mListSelectBackup);
                addChoice2List("str_text_doublecamera", this.mListSelectBackup);
            }
        } else {
            if (FuncUtils.isAppInstalled(getContext(), "com.syu.us") && readFile.contains("m2_v:Rn54")) {
                addChoice2List("sevenlight_default", this.mListSelectBackup);
            }
            if (FuncUtils.isAppInstalled(getContext(), "com.syu.fourcamera2") && readFile.contains("m2_v:Rn54") && readFile.contains("s_v:Ls15")) {
                addChoice2List("str_text_inner360", this.mListSelectBackup);
            }
        }
        int i = SystemProperties.getInt("persist.syu.camera360", 0);
        BackCarCameraAdapter backCarCameraAdapter = new BackCarCameraAdapter(getContext(), this.mListSelectBackup);
        this.cameraAdapter = backCarCameraAdapter;
        this.mCameraList.setAdapter((ListAdapter) backCarCameraAdapter);
        this.cameraAdapter.setItemSelect(i);
        this.cameraAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.FctoryBackcarCameraDialogFragment.1
            @Override // com.android.settings.factory.dialog.FctoryBackcarCameraDialogFragment.OnItemClickListener
            public void onItemClick(int i2) {
                FctoryBackcarCameraDialogFragment.this.cameraAdapter.setItemSelect(i2);
                FctoryBackcarCameraDialogFragment fctoryBackcarCameraDialogFragment = FctoryBackcarCameraDialogFragment.this;
                fctoryBackcarCameraDialogFragment.mCurSelectBackup = i2;
                boolean equals = fctoryBackcarCameraDialogFragment.mListSelectBackup.get(i2).get(FctoryBackcarCameraDialogFragment.PopChoiceItem_Name).equals(FctoryBackcarCameraDialogFragment.this.getString("str_text_inner360"));
                boolean z = SystemProperties.getInt("persist.syu.camera360", 0) != equals;
                SystemProperties.set("persist.syu.camera360", (equals ? 1 : 0) + "");
                SystemProperties.reportSyspropChanged();
                String str = FctoryBackcarCameraDialogFragment.TAG;
                Log.d(str, "onItemClick: " + z + (equals ? 1 : 0));
                if (z) {
                    FctoryBackcarCameraDialogFragment.this.showConfirmDialog();
                }
                FctoryBackcarCameraDialogFragment.this.cameraAdapter.notifyDataSetChanged();
            }
        });
    }

    public static String readFile(String str) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(str)));
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    stringBuffer.append(readLine + "\n");
                } else {
                    bufferedReader.close();
                    return stringBuffer.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConfirmDialog() {
        Fragment fragment2 = fragment;
        if (fragment2 != null) {
            ProtocolBackCarConfirmFragment.show(fragment2, "");
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BackCarCameraAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public BackCarCameraAdapter(Context context, List<SparseArray<String>> list) {
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
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_camera_select, viewGroup, false);
                holder.mTitle = (TextView) view2.findViewById(R$id.camera_name);
                holder.mCheck = (CheckBox) view2.findViewById(R$id.camera_select);
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
            String str = FctoryBackcarCameraDialogFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i).toString());
            holder.mTitle.setText(this.mData.get(i).get(FctoryBackcarCameraDialogFragment.PopChoiceItem_Name));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FctoryBackcarCameraDialogFragment.BackCarCameraAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (BackCarCameraAdapter.this.mListener != null) {
                        BackCarCameraAdapter.this.mListener.onItemClick(i);
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
