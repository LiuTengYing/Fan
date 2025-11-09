package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.android.settings.R$array;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.ipc.IpcObj;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CommonPoweroffDelayDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "CommonPoweroffDelayDialogFragment";
    private static int currentTime;
    private ListView listView;
    private View mRootView;
    private ViewGroup.LayoutParams params;
    private PowerDelayTimeAdapter timeAdapter;
    private List<String> mTimeSelect = new ArrayList();
    private int mCurrentTime = 0;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    private int getCurrentPostion(int i) {
        if (i != 0) {
            if (i == 3) {
                return 1;
            }
            if (i == 10) {
                return 2;
            }
            if (i == 180) {
                return 3;
            }
            if (i == 300) {
                return 4;
            }
            if (i == 600) {
                return 5;
            }
            if (i == 900) {
                return 6;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getTime(int i) {
        switch (i) {
            case 0:
            default:
                return 0;
            case 1:
                return 3;
            case 2:
                return 10;
            case 3:
                return 180;
            case 4:
                return 300;
            case 5:
                return 600;
            case 6:
                return 900;
        }
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
            CommonPoweroffDelayDialogFragment commonPoweroffDelayDialogFragment = new CommonPoweroffDelayDialogFragment();
            currentTime = i;
            commonPoweroffDelayDialogFragment.setArguments(bundle);
            commonPoweroffDelayDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.common_powerdelay_dialog_layout, null);
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
        ((TextView) this.mRootView.findViewById(R$id.power_delay_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.listView = (ListView) this.mRootView.findViewById(R$id.power_delay_time_list);
        for (String str : getContext().getResources().getStringArray(R$array.power_delay_time)) {
            this.mTimeSelect.add(str);
        }
        PowerDelayTimeAdapter powerDelayTimeAdapter = new PowerDelayTimeAdapter(getContext(), this.mTimeSelect);
        this.timeAdapter = powerDelayTimeAdapter;
        this.listView.setAdapter((ListAdapter) powerDelayTimeAdapter);
        this.timeAdapter.setItemSelect(getCurrentPostion(currentTime));
        this.timeAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.common.dialog.CommonPoweroffDelayDialogFragment.1
            @Override // com.android.settings.common.dialog.CommonPoweroffDelayDialogFragment.OnItemClickListener
            public void onItemClick(int i) {
                CommonPoweroffDelayDialogFragment.this.timeAdapter.setItemSelect(i);
                CommonPoweroffDelayDialogFragment.this.mCurrentTime = i;
                CommonPoweroffDelayDialogFragment.this.timeAdapter.notifyDataSetChanged();
                IpcObj.getInstance().setCmd(0, 142, CommonPoweroffDelayDialogFragment.this.getTime(i));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class PowerDelayTimeAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public PowerDelayTimeAdapter(Context context, List<String> list) {
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
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_power_delay_time, viewGroup, false);
                holder.mTitle = (TextView) view2.findViewById(R$id.power_delay_time);
                holder.mCheck = (CheckBox) view2.findViewById(R$id.power_delay_time_select);
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
            String str = CommonPoweroffDelayDialogFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i));
            holder.mTitle.setText(this.mData.get(i));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.common.dialog.CommonPoweroffDelayDialogFragment.PowerDelayTimeAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (PowerDelayTimeAdapter.this.mListener != null) {
                        PowerDelayTimeAdapter.this.mListener.onItemClick(i);
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
