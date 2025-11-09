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
import com.syu.jni.ToolsJni;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FactoryUsbProtocolDialogFragment extends InstrumentedDialogFragment {
    private static int PopChoiceItem_Name = 236;
    private static String TAG = "FactoryUsbProtocolDialogFragment";
    public static String mPkgName;
    public static Resources mResources;
    private View mRootView;
    private ListView mUsbProtocolList;
    private ViewGroup.LayoutParams params;
    private UsbProtocolAdapter usbProtocolAdapter;
    public List<SparseArray<String>> mListUsbProtocol = new ArrayList();
    public int mCurSelectBackup = 0;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FactoryUsbProtocolDialogFragment factoryUsbProtocolDialogFragment = new FactoryUsbProtocolDialogFragment();
            factoryUsbProtocolDialogFragment.setArguments(bundle);
            factoryUsbProtocolDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.factory_usb_protocol_dialog_layout, null);
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
        ((TextView) this.mRootView.findViewById(R$id.usb_protocol_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mUsbProtocolList = (ListView) this.mRootView.findViewById(R$id.usb_protocol_list);
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        this.mCurSelectBackup = ToolsJni.cmd_12_get_usb_speed();
        String str = TAG;
        Log.d(str, "init: " + this.mCurSelectBackup);
        initData();
    }

    private void initData() {
        addChoice2List("str_usbprotocol1", this.mListUsbProtocol);
        addChoice2List("str_usbprotocol2", this.mListUsbProtocol);
        UsbProtocolAdapter usbProtocolAdapter = new UsbProtocolAdapter(getContext(), this.mListUsbProtocol);
        this.usbProtocolAdapter = usbProtocolAdapter;
        this.mUsbProtocolList.setAdapter((ListAdapter) usbProtocolAdapter);
        this.usbProtocolAdapter.setItemSelect(this.mCurSelectBackup);
        this.usbProtocolAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.FactoryUsbProtocolDialogFragment.1
            @Override // com.android.settings.factory.dialog.FactoryUsbProtocolDialogFragment.OnItemClickListener
            public void onItemClick(int i) {
                FactoryUsbProtocolDialogFragment.this.usbProtocolAdapter.setItemSelect(i);
                FactoryUsbProtocolDialogFragment.this.mCurSelectBackup = i;
                ToolsJni.cmd_13_write_usb_speed(i);
                FactoryUsbProtocolDialogFragment.this.usbProtocolAdapter.notifyDataSetChanged();
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
    public class UsbProtocolAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public UsbProtocolAdapter(Context context, List<SparseArray<String>> list) {
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
            String str = FactoryUsbProtocolDialogFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i).toString());
            holder.mTitle.setText(this.mData.get(i).get(FactoryUsbProtocolDialogFragment.PopChoiceItem_Name));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactoryUsbProtocolDialogFragment.UsbProtocolAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (UsbProtocolAdapter.this.mListener != null) {
                        UsbProtocolAdapter.this.mListener.onItemClick(i);
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
