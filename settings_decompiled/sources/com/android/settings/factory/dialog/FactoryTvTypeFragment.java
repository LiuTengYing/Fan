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
import com.android.settings.ipc.IpcObj;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FactoryTvTypeFragment extends InstrumentedDialogFragment {
    private static int PopChoiceItem_Name = 237;
    private static String TAG = "FactoryTvTypeFragment";
    private static int itemSelect = -1;
    public static String mPkgName;
    public static Resources mResources;
    private View mRootView;
    private TvTypeAdapter mTvTypeAdapter;
    private ListView mTvTypeList;
    private ViewGroup.LayoutParams params;
    public List<SparseArray<String>> mListTv = new ArrayList();
    public int mCurSelectBackup = 0;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, int i) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FactoryTvTypeFragment factoryTvTypeFragment = new FactoryTvTypeFragment();
            itemSelect = i;
            factoryTvTypeFragment.setArguments(bundle);
            factoryTvTypeFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.factory_tv_type_dialog_layout, null);
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
        ((TextView) this.mRootView.findViewById(R$id.tv_type_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mTvTypeList = (ListView) this.mRootView.findViewById(R$id.tv_type_list);
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        initData();
    }

    private void initData() {
        addChoice2List("factory_settings_tv_type_0", this.mListTv);
        addChoice2List("factory_settings_tv_type_2", this.mListTv);
        addChoice2List("factory_settings_tv_type_3", this.mListTv);
        TvTypeAdapter tvTypeAdapter = new TvTypeAdapter(getContext(), this.mListTv);
        this.mTvTypeAdapter = tvTypeAdapter;
        this.mTvTypeList.setAdapter((ListAdapter) tvTypeAdapter);
        int i = itemSelect;
        if (i == 0) {
            this.mTvTypeAdapter.setItemSelect(0);
        } else {
            this.mTvTypeAdapter.setItemSelect(i - 1);
        }
        this.mTvTypeAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.FactoryTvTypeFragment.1
            @Override // com.android.settings.factory.dialog.FactoryTvTypeFragment.OnItemClickListener
            public void onItemClick(int i2) {
                FactoryTvTypeFragment.this.mTvTypeAdapter.setItemSelect(i2);
                FactoryTvTypeFragment.this.mCurSelectBackup = i2;
                if (i2 == 0) {
                    IpcObj.getInstance().setCmd(6, 4, i2);
                } else {
                    IpcObj.getInstance().setCmd(6, 4, i2 + 1);
                }
                FactoryTvTypeFragment.this.mTvTypeAdapter.notifyDataSetChanged();
            }
        });
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
    public class TvTypeAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public TvTypeAdapter(Context context, List<SparseArray<String>> list) {
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
            String str = FactoryTvTypeFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i).toString());
            holder.mTitle.setText(this.mData.get(i).get(FactoryTvTypeFragment.PopChoiceItem_Name));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryTvTypeFragment.TvTypeAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (TvTypeAdapter.this.mListener != null) {
                        TvTypeAdapter.this.mListener.onItemClick(i);
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
