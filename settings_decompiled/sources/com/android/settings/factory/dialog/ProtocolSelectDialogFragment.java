package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.factory.listener.ProtocolNotifyListener;
import com.android.settings.factory.protocol.CanBox;
import com.android.settings.factory.protocol.CarCompany;
import com.android.settings.factory.protocol.CarProtocol;
import com.android.settings.factory.protocol.CarSet;
import com.android.settings.factory.protocol.CarType;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.ipc.IpcObj;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ProtocolSelectDialogFragment extends InstrumentedDialogFragment {
    private static int PopChoiceItem_Name = 333;
    private static String TAG = "ProtocolSelectDialogFragment";
    private static int mCanbusId;
    private ProtocolListAdapter canboxAdapter;
    private ProtocolListAdapter cartypeAdapter;
    private ProtocolListAdapter companyAdapter;
    private CanBox mCanBox;
    private ListView mCanBoxList;
    private CarCompany mCarCompany;
    private CarSet mCarSet;
    private CarType mCarType;
    private ListView mCarTypeList;
    private ListView mCompanyList;
    private LinearLayout mLoading;
    private LinearLayout mProtocolList;
    private View mRootView;
    private ListView mVehicleList;
    private ViewGroup.LayoutParams params;
    private ProtocolNotifyListener protocolNotifyListener = new ProtocolNotifyListener() { // from class: com.android.settings.factory.dialog.ProtocolSelectDialogFragment.5
        @Override // com.android.settings.factory.listener.ProtocolNotifyListener
        public void notifyCarType() {
            Log.d(ProtocolSelectDialogFragment.TAG, "notifyCarType: ");
            ProtocolSelectDialogFragment.this.updateAllList();
        }
    };
    private ProtocolListAdapter vehicleAdapter;

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
            ProtocolSelectDialogFragment protocolSelectDialogFragment = new ProtocolSelectDialogFragment();
            mCanbusId = i;
            protocolSelectDialogFragment.setArguments(bundle);
            protocolSelectDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        getArguments().getString("arg_key_dialog_title");
        int i = SystemProperties.getInt("sys.lsec.status_bar_height", 0);
        String str = TAG;
        Log.d(str, "onCreateDialog: " + i);
        int[] windowManeger = getWindowManeger();
        if (SettingsApplication.mHeightFix > 1100) {
            this.params = new ViewGroup.LayoutParams(windowManeger[0], windowManeger[1] - 100);
        } else {
            this.params = new ViewGroup.LayoutParams(windowManeger[0], windowManeger[1] - 60);
        }
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.protocol_car_type_dialog_layout, null);
        int i2 = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i2 == 0 || i2 == 2 || i2 == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mLoading = (LinearLayout) this.mRootView.findViewById(R$id.protocol_loading);
        this.mProtocolList = (LinearLayout) this.mRootView.findViewById(R$id.protocol_list);
        this.mCompanyList = (ListView) this.mRootView.findViewById(R$id.can_box_company);
        this.mVehicleList = (ListView) this.mRootView.findViewById(R$id.vehicle_series);
        this.mCarTypeList = (ListView) this.mRootView.findViewById(R$id.car_type);
        this.mCanBoxList = (ListView) this.mRootView.findViewById(R$id.can_box);
        updateAllList();
        CarProtocol.getInstance(SettingsApplication.mApplication).setProtocolNotifyListener(this.protocolNotifyListener);
    }

    public void updateListCompany() {
        CarType carType;
        CarSet carSet;
        CarCompany carCompany;
        if (this.mCompanyList == null || CarProtocol.mListCarCompanys == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<CarCompany> it = CarProtocol.mListCarCompanys.iterator();
        int i = 0;
        int i2 = 0;
        while (it.hasNext()) {
            CarCompany next = it.next();
            CanBox canBox = CarProtocol.mCurCanBox;
            if (canBox != null && (carCompany = (carSet = (carType = canBox.carType).carSet).company) == next) {
                this.mCanBox = canBox;
                this.mCarType = carType;
                this.mCarSet = carSet;
                this.mCarCompany = carCompany;
                i = i2;
            }
            SparseArray sparseArray = new SparseArray();
            sparseArray.put(PopChoiceItem_Name, next.strCompany);
            arrayList.add(sparseArray);
            i2++;
        }
        ProtocolListAdapter protocolListAdapter = this.companyAdapter;
        if (protocolListAdapter == null) {
            this.companyAdapter = new ProtocolListAdapter(getContext(), arrayList);
        } else {
            protocolListAdapter.setData(arrayList);
            this.companyAdapter.notifyDataSetChanged();
        }
        this.mCompanyList.setAdapter((ListAdapter) this.companyAdapter);
        this.companyAdapter.setItemSelect(i);
        this.mCompanyList.setSelection(i);
        Log.d(TAG, "updateListCompany: " + i);
        this.companyAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.ProtocolSelectDialogFragment.1
            @Override // com.android.settings.factory.dialog.ProtocolSelectDialogFragment.OnItemClickListener
            public void onItemClick(int i3) {
                ProtocolSelectDialogFragment.this.companyAdapter.setItemSelect(i3);
                if (((SparseArray) ProtocolSelectDialogFragment.this.companyAdapter.getItem(i3)).size() > 0) {
                    ProtocolSelectDialogFragment.this.mCarCompany = CarProtocol.mListCarCompanys.get(i3);
                    ProtocolSelectDialogFragment.this.companyAdapter.notifyDataSetChanged();
                    ProtocolSelectDialogFragment.this.updateListCarSet();
                    if (ProtocolSelectDialogFragment.this.mCarCompany == CarProtocol.mCurCarcompany) {
                        ProtocolSelectDialogFragment.this.mCarSet = CarProtocol.mCurCarSet;
                    } else {
                        ProtocolSelectDialogFragment protocolSelectDialogFragment = ProtocolSelectDialogFragment.this;
                        protocolSelectDialogFragment.mCarSet = protocolSelectDialogFragment.mCarCompany.mListCarSet.get(0);
                    }
                    ProtocolSelectDialogFragment protocolSelectDialogFragment2 = ProtocolSelectDialogFragment.this;
                    protocolSelectDialogFragment2.mCarType = protocolSelectDialogFragment2.mCarSet.mListCarType.get(0);
                    ProtocolSelectDialogFragment.this.updateListCarType();
                    ProtocolSelectDialogFragment.this.updateListCanBox();
                    ProtocolSelectDialogFragment.this.updateMessage();
                }
            }
        });
    }

    public void updateListCarSet() {
        Log.d(TAG, "updateListCarSet: 111");
        if (this.mCarCompany != null) {
            Log.d(TAG, "updateListCarSet: 222");
            if (this.mCarCompany.mListCarSet != null) {
                Log.d(TAG, "updateListCarSet: 333");
                ArrayList arrayList = new ArrayList();
                Iterator<CarSet> it = this.mCarCompany.mListCarSet.iterator();
                int i = 0;
                int i2 = 0;
                while (it.hasNext()) {
                    CarSet next = it.next();
                    Log.d(TAG, "updateListCarSet: 444");
                    if (next == this.mCarSet) {
                        i2 = i;
                    }
                    Log.d(TAG, "updateListCarSet: 555");
                    String str = next.strSet;
                    String[] split = str.split("##");
                    if (split != null && split.length > 1) {
                        str = split[0];
                    }
                    SparseArray sparseArray = new SparseArray();
                    sparseArray.put(PopChoiceItem_Name, str);
                    arrayList.add(sparseArray);
                    i++;
                }
                ProtocolListAdapter protocolListAdapter = this.vehicleAdapter;
                if (protocolListAdapter == null) {
                    this.vehicleAdapter = new ProtocolListAdapter(getContext(), arrayList);
                } else {
                    protocolListAdapter.setData(arrayList);
                    this.vehicleAdapter.notifyDataSetChanged();
                }
                this.mVehicleList.setAdapter((ListAdapter) this.vehicleAdapter);
                this.vehicleAdapter.setItemSelect(i2);
                this.mVehicleList.setSelection(i2);
                Log.d(TAG, "updateListCarSet: " + i2);
                this.vehicleAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.ProtocolSelectDialogFragment.2
                    @Override // com.android.settings.factory.dialog.ProtocolSelectDialogFragment.OnItemClickListener
                    public void onItemClick(int i3) {
                        ProtocolSelectDialogFragment.this.vehicleAdapter.setItemSelect(i3);
                        if (((SparseArray) ProtocolSelectDialogFragment.this.vehicleAdapter.getItem(i3)).size() <= 0 || ProtocolSelectDialogFragment.this.mCarCompany == null) {
                            return;
                        }
                        ProtocolSelectDialogFragment protocolSelectDialogFragment = ProtocolSelectDialogFragment.this;
                        protocolSelectDialogFragment.mCarSet = protocolSelectDialogFragment.mCarCompany.mListCarSet.get(i3);
                        ProtocolSelectDialogFragment protocolSelectDialogFragment2 = ProtocolSelectDialogFragment.this;
                        protocolSelectDialogFragment2.mCarType = protocolSelectDialogFragment2.mCarSet.mListCarType.get(0);
                        ProtocolSelectDialogFragment.this.vehicleAdapter.notifyDataSetChanged();
                        ProtocolSelectDialogFragment.this.updateListCarType();
                        ProtocolSelectDialogFragment.this.updateListCanBox();
                        ProtocolSelectDialogFragment.this.updateMessage();
                    }
                });
            }
        }
    }

    public void updateListCarType() {
        CarSet carSet = this.mCarSet;
        if (carSet == null || carSet.mListCarType == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<CarType> it = this.mCarSet.mListCarType.iterator();
        int i = 0;
        int i2 = 0;
        while (it.hasNext()) {
            CarType next = it.next();
            if (next == this.mCarType) {
                i2 = i;
            }
            String str = next.strType;
            String[] split = str.split("##");
            if (split != null && split.length > 1) {
                str = split[0];
            }
            SparseArray sparseArray = new SparseArray();
            sparseArray.put(PopChoiceItem_Name, str);
            arrayList.add(sparseArray);
            i++;
        }
        ProtocolListAdapter protocolListAdapter = this.cartypeAdapter;
        if (protocolListAdapter == null) {
            this.cartypeAdapter = new ProtocolListAdapter(getContext(), arrayList);
        } else {
            protocolListAdapter.setData(arrayList);
            this.cartypeAdapter.notifyDataSetChanged();
        }
        this.mCarTypeList.setAdapter((ListAdapter) this.cartypeAdapter);
        this.cartypeAdapter.setItemSelect(i2);
        this.mCarTypeList.setSelection(i2);
        Log.d(TAG, "updateListCarType: " + i2);
        this.cartypeAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.ProtocolSelectDialogFragment.3
            @Override // com.android.settings.factory.dialog.ProtocolSelectDialogFragment.OnItemClickListener
            public void onItemClick(int i3) {
                ProtocolSelectDialogFragment.this.cartypeAdapter.setItemSelect(i3);
                if (((SparseArray) ProtocolSelectDialogFragment.this.cartypeAdapter.getItem(i3)).size() <= 0 || ProtocolSelectDialogFragment.this.mCarSet == null) {
                    return;
                }
                ProtocolSelectDialogFragment protocolSelectDialogFragment = ProtocolSelectDialogFragment.this;
                protocolSelectDialogFragment.mCarType = protocolSelectDialogFragment.mCarSet.mListCarType.get(i3);
                ProtocolSelectDialogFragment.this.cartypeAdapter.notifyDataSetChanged();
                ProtocolSelectDialogFragment.this.updateListCanBox();
                ProtocolSelectDialogFragment.this.updateMessage();
            }
        });
    }

    public void updateListCanBox() {
        try {
            CarType carType = this.mCarType;
            if (carType == null || carType.mListCanBox == null) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            Iterator<CanBox> it = this.mCarType.mListCanBox.iterator();
            int i = 0;
            int i2 = 0;
            boolean z = false;
            while (it.hasNext()) {
                CanBox next = it.next();
                if (next == this.mCanBox) {
                    z = true;
                    i2 = i;
                }
                SparseArray sparseArray = new SparseArray();
                sparseArray.put(PopChoiceItem_Name, next.strCompany);
                arrayList.add(sparseArray);
                i++;
            }
            ProtocolListAdapter protocolListAdapter = this.canboxAdapter;
            if (protocolListAdapter == null) {
                this.canboxAdapter = new ProtocolListAdapter(getContext(), arrayList);
            } else {
                protocolListAdapter.setData(arrayList);
                this.canboxAdapter.notifyDataSetChanged();
            }
            this.mCanBoxList.setAdapter((ListAdapter) this.canboxAdapter);
            this.mCanBoxList.setSelection(i2);
            Log.d(TAG, "updateListCanBox: " + i2);
            if (z) {
                this.canboxAdapter.setItemSelect(i2);
            } else {
                this.canboxAdapter.setItemSelect(-1);
            }
            this.canboxAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.factory.dialog.ProtocolSelectDialogFragment.4
                @Override // com.android.settings.factory.dialog.ProtocolSelectDialogFragment.OnItemClickListener
                public void onItemClick(int i3) {
                    ProtocolSelectDialogFragment.this.canboxAdapter.setItemSelect(i3);
                    ProtocolSelectDialogFragment protocolSelectDialogFragment = ProtocolSelectDialogFragment.this;
                    protocolSelectDialogFragment.mCanBox = protocolSelectDialogFragment.mCarType.mListCanBox.get(i3);
                    if (((SparseArray) ProtocolSelectDialogFragment.this.canboxAdapter.getItem(i3)).size() <= 0 || ProtocolSelectDialogFragment.this.mCarType == null) {
                        return;
                    }
                    if (53 == SettingsApplication.mCustomerid) {
                        if (ProtocolSelectDialogFragment.this.mCanBox.id / 10000 == 1008) {
                            ProtocolSelectDialogFragment protocolSelectDialogFragment2 = ProtocolSelectDialogFragment.this;
                            protocolSelectDialogFragment2.writeTxtToFile("" + ProtocolSelectDialogFragment.this.mCanBox.id, "/sdcard/canbus/", "CanId.txt");
                            ProtocolSelectDialogFragment.this.sendBroadcast(true);
                        } else {
                            ProtocolSelectDialogFragment.this.writeTxtToFile("", "/sdcard/canbus/", "CanId.txt");
                            ProtocolSelectDialogFragment.this.sendBroadcast(false);
                        }
                    }
                    IpcObj.getInstance().setCmd(7, AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP, ProtocolSelectDialogFragment.this.mCanBox.id);
                    SettingsApplication.mCanbusId = ProtocolSelectDialogFragment.this.mCanBox.id;
                    Intent intent = new Intent();
                    intent.setAction("com.fyt.changecanbusid");
                    intent.putExtra("canbusid", SettingsApplication.mCanbusId);
                    intent.addFlags(16777216);
                    Log.i("fangli", "canbusid = " + SettingsApplication.mCanbusId);
                    ProtocolSelectDialogFragment.this.getContext().sendBroadcast(intent);
                    ProtocolSelectDialogFragment.this.canboxAdapter.notifyDataSetChanged();
                    ProtocolSelectDialogFragment.this.updateCanbusStr();
                    ProtocolSelectDialogFragment.this.dismiss();
                }
            });
        } catch (Exception e) {
            Log.i("hzq", Log.getStackTraceString(e));
        }
    }

    public void writeTxtToFile(String str, String str2, String str3) {
        makeFilePath(str2, str3);
        String str4 = str2 + str3;
        String str5 = str + "\r\n";
        try {
            File file = new File(str4);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + str4);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(file.length());
            randomAccessFile.write(str5.getBytes());
            randomAccessFile.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    public static void makeRootDirectory(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                return;
            }
            file.mkdir();
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    public void updateAllList() {
        String str = TAG;
        Log.d(str, "updateAllList: " + SettingsApplication.is_UpdatingCartype);
        if (SettingsApplication.is_UpdatingCartype.booleanValue()) {
            return;
        }
        int i = mCanbusId;
        CanBox canBox = CarProtocol.mCurCanBox;
        if (canBox == null || i != canBox.id) {
            CarProtocol.mCurCanBox = CarProtocol.getCanBoxById(i);
        }
        this.mLoading.setVisibility(8);
        this.mProtocolList.setVisibility(0);
        updateListCompany();
        updateListCarSet();
        updateListCarType();
        updateListCanBox();
        updateMessage();
    }

    public File makeFilePath(String str, String str2) {
        File file;
        Exception e;
        makeRootDirectory(str);
        try {
            file = new File(str + str2);
        } catch (Exception e2) {
            file = null;
            e = e2;
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return file;
        }
        return file;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCanbusStr() {
        if (this.mCanBox != null) {
            String str = this.mCarType.strType;
            String[] split = str.split("##");
            if (split != null && split.length > 1) {
                str = split[0];
            }
            Log.d("fangli", "updateCanbusStr: " + str);
            SettingsApplication settingsApplication = SettingsApplication.mApplication;
            settingsApplication.cartypeStr = this.mCarType.carSet.company.strCompany + "-" + this.mCarSet.strSet + "-" + str + "-" + this.mCanBox.strCompany;
        }
    }

    public void sendBroadcast(boolean z) {
        Intent intent = new Intent();
        intent.setAction("com.syu.setcanbox");
        intent.putExtra("isZHcanbox", z);
        getContext().sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ProtocolListAdapter extends BaseAdapter {
        private Context mContext;
        private List<SparseArray<String>> mData;
        private OnItemClickListener mListener;
        private int mPosition = 0;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public ProtocolListAdapter(Context context, List<SparseArray<String>> list) {
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
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_protocol_select, viewGroup, false);
                holder.mTitle = (TextView) view2.findViewById(R$id.protocol_name);
                holder.mCheck = (CheckBox) view2.findViewById(R$id.protocol_select);
                holder.mItemLayout = (LinearLayout) view2.findViewById(R$id.protocol_item_layout);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            if (i == this.mPosition) {
                holder.mCheck.setChecked(true);
                holder.mItemLayout.setBackground(this.mContext.getResources().getDrawable(R$drawable.protocol_item_press));
            } else {
                holder.mCheck.setChecked(false);
                holder.mItemLayout.setBackground(null);
            }
            holder.mTitle.setText(this.mData.get(i).get(ProtocolSelectDialogFragment.PopChoiceItem_Name));
            holder.mTitle.setSelected(true);
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.dialog.ProtocolSelectDialogFragment.ProtocolListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (ProtocolListAdapter.this.mListener != null) {
                        ProtocolListAdapter.this.mListener.onItemClick(i);
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

        public void setData(List<SparseArray<String>> list) {
            this.mData = list;
        }

        /* loaded from: classes.dex */
        class Holder {
            CheckBox mCheck;
            LinearLayout mItemLayout;
            TextView mTitle;

            Holder() {
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        CarProtocol.getInstance(SettingsApplication.mApplication).removeProtocolNotifyListsner(this.protocolNotifyListener);
        super.onDestroy();
    }

    public void updateMessage() {
        String[] split;
        CarType carType = this.mCarType;
        if (carType == null || (split = carType.strType.split("##")) == null || split.length <= 1) {
            return;
        }
        String str = split[1];
    }
}
