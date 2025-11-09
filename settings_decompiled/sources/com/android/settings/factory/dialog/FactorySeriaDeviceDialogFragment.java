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
public class FactorySeriaDeviceDialogFragment extends InstrumentedDialogFragment {
    private static int PopChoiceItem_Name = 235;
    private static String TAG = "FactorySeriaDeviceDialogFragment";
    public static String mPkgName = null;
    public static Resources mResources = null;
    private static String selectDevice = "";
    private View mRootView;
    private ListView mSeriaDeviceList;
    private ViewGroup.LayoutParams params;
    private SeriaDeviceAdapter seriaDeviceAdapter;
    public List<SparseArray<String>> mListSelectBackup = new ArrayList();
    public List<String> mSerialDevice = new ArrayList();
    public List<SparseArray<String>> mListSerialDevice = new ArrayList();
    private int currentPosition = 0;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str, String str2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FactorySeriaDeviceDialogFragment factorySeriaDeviceDialogFragment = new FactorySeriaDeviceDialogFragment();
            selectDevice = str2;
            factorySeriaDeviceDialogFragment.setArguments(bundle);
            factorySeriaDeviceDialogFragment.show(childFragmentManager, TAG);
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
        this.mSeriaDeviceList = (ListView) this.mRootView.findViewById(R$id.backup_camera_list);
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        initData();
    }

    private void initData() {
        addChoice2List("set_serial_device_null", this.mListSerialDevice);
        this.mSerialDevice.add("null");
        addChoice2List("set_serial_device_RadarXiaoFD_9600", this.mListSerialDevice);
        this.mSerialDevice.add("RadarXiaoFD#9600");
        addChoice2List("set_serial_device_accelerator_9600", this.mListSerialDevice);
        this.mSerialDevice.add("Accelerator#9600");
        addChoice2List("set_serial_device_accelerator3b_9600", this.mListSerialDevice);
        this.mSerialDevice.add("Accelerator3B#9600");
        addChoice2List("set_serial_device_orotpms_9600", this.mListSerialDevice);
        this.mSerialDevice.add("OroTpms#9600");
        addChoice2List("set_serial_device_radartiancheng_9600", this.mListSerialDevice);
        this.mSerialDevice.add("RadarTianCheng#9600");
        addChoice2List("set_serial_device_trackplugin_19200", this.mListSerialDevice);
        this.mSerialDevice.add("TrackPlugin#19200");
        addChoice2List("set_serial_device_pluginwlleds_19200", this.mListSerialDevice);
        this.mSerialDevice.add("PluginWLLeds#19200");
        addChoice2List("set_serial_device_xzguiji_38400", this.mListSerialDevice);
        this.mSerialDevice.add("Common_XZMGuiji#19200");
        addChoice2List("set_serial_device_wcguiji_38400", this.mListSerialDevice);
        this.mSerialDevice.add("Common_WCGuiji#38400");
        addChoice2List("set_serial_device_yltdsp_38400", this.mListSerialDevice);
        this.mSerialDevice.add("LeiShenDsp#38400");
        addChoice2List("set_serial_device_rzclight_38400", this.mListSerialDevice);
        this.mSerialDevice.add("RaiseLeds#38400");
        addChoice2List("set_serial_device_SnnavLed_115200", this.mListSerialDevice);
        this.mSerialDevice.add("SnnavLed#115200");
        SeriaDeviceAdapter seriaDeviceAdapter = new SeriaDeviceAdapter(getContext(), this.mListSerialDevice);
        this.seriaDeviceAdapter = seriaDeviceAdapter;
        this.mSeriaDeviceList.setAdapter((ListAdapter) seriaDeviceAdapter);
        for (int i = 0; i < this.mSerialDevice.size(); i++) {
            if (selectDevice.contains(this.mSerialDevice.get(i))) {
                this.currentPosition = i;
                String str = TAG;
                Log.d(str, "initData: " + this.currentPosition);
            }
        }
        this.seriaDeviceAdapter.setItemSelect(this.currentPosition);
        this.seriaDeviceAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.FactorySeriaDeviceDialogFragment.1
            @Override // com.android.settings.factory.dialog.FactorySeriaDeviceDialogFragment.OnItemClickListener
            public void onItemClick(int i2) {
                FactorySeriaDeviceDialogFragment.this.seriaDeviceAdapter.setItemSelect(i2);
                FactorySeriaDeviceDialogFragment.this.currentPosition = i2;
                FactorySeriaDeviceDialogFragment.this.seriaDeviceAdapter.notifyDataSetChanged();
                if (i2 == 0) {
                    IpcObj.getInstance().setCmd(0, 163, null, null, new String[]{"NULL"});
                } else {
                    IpcObj.getInstance().setCmd(0, 163, null, null, new String[]{FactorySeriaDeviceDialogFragment.this.mSerialDevice.get(i2)});
                }
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
    public class SeriaDeviceAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public SeriaDeviceAdapter(Context context, List<SparseArray<String>> list) {
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
            String str = FactorySeriaDeviceDialogFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i).toString());
            holder.mTitle.setText(this.mData.get(i).get(FactorySeriaDeviceDialogFragment.PopChoiceItem_Name));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.FactorySeriaDeviceDialogFragment.SeriaDeviceAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (SeriaDeviceAdapter.this.mListener != null) {
                        SeriaDeviceAdapter.this.mListener.onItemClick(i);
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
