package com.android.settings.display.syudisplay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
public class DisplayScreenSaverTimeDialogFragment extends InstrumentedDialogFragment {
    private static String TAG = "DisplayScreenSaverTimeDialogFragment";
    public static String mPkgName;
    private static int mPosition;
    public static Resources mResources;
    private View mRootView;
    private ListView mScreenSaverTimeList;
    private ScreenSaverTimeAdapter mTimeAdapter;
    private List<String> mTimeSelect = new ArrayList();
    private ViewGroup.LayoutParams params;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAutoTime(int i) {
        if (i != 0) {
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 5;
            }
            if (i == 3) {
                return 15;
            }
            if (i == 4) {
                return 30;
            }
        }
        return 1;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.display_screensaver_time_dialog_layout, null);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        setTitle(string);
        init();
        return dialog;
    }

    private void setTitle(String str) {
        ((TextView) this.mRootView.findViewById(R$id.screen_saver_time_title)).setText(str);
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        this.mScreenSaverTimeList = (ListView) this.mRootView.findViewById(R$id.screen_saver_time_list);
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        initData();
    }

    private void initData() {
        for (String str : getContext().getResources().getStringArray(R$array.screen_saver_auto_time)) {
            this.mTimeSelect.add(str);
        }
        ScreenSaverTimeAdapter screenSaverTimeAdapter = new ScreenSaverTimeAdapter(getContext(), this.mTimeSelect);
        this.mTimeAdapter = screenSaverTimeAdapter;
        this.mScreenSaverTimeList.setAdapter((ListAdapter) screenSaverTimeAdapter);
        this.mTimeAdapter.setItemSelect(mPosition);
        this.mTimeAdapter.setOnItemClickListener(new OnItemClickListener() { // from class: com.android.settings.display.syudisplay.dialog.DisplayScreenSaverTimeDialogFragment.1
            @Override // com.android.settings.display.syudisplay.dialog.DisplayScreenSaverTimeDialogFragment.OnItemClickListener
            public void onItemClick(int i) {
                DisplayScreenSaverTimeDialogFragment.this.mTimeAdapter.setItemSelect(i);
                DisplayScreenSaverTimeDialogFragment.mPosition = i;
                IpcObj.getInstance().setCmd(0, 177, 1, DisplayScreenSaverTimeDialogFragment.this.getAutoTime(i));
                DisplayScreenSaverTimeDialogFragment.this.mTimeAdapter.notifyDataSetChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ScreenSaverTimeAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mData;
        private OnItemClickListener mListener;
        private int mPosition = -1;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return 0L;
        }

        public ScreenSaverTimeAdapter(Context context, List<String> list) {
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
                view2 = LayoutInflater.from(this.mContext).inflate(R$layout.item_screensaver_time_select, viewGroup, false);
                holder.mTitle = (TextView) view2.findViewById(R$id.screen_saver_time);
                holder.mCheck = (CheckBox) view2.findViewById(R$id.screen_saver_time_select);
                view2.setTag(holder);
            } else {
                view2 = view;
                holder = (Holder) view.getTag();
            }
            holder.mTitle.setText(this.mData.get(i));
            if (i == this.mPosition) {
                holder.mCheck.setChecked(true);
            } else {
                holder.mCheck.setChecked(false);
            }
            String str = DisplayScreenSaverTimeDialogFragment.TAG;
            Log.d(str, "getView: " + this.mData.get(i));
            view2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.dialog.DisplayScreenSaverTimeDialogFragment.ScreenSaverTimeAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (ScreenSaverTimeAdapter.this.mListener != null) {
                        ScreenSaverTimeAdapter.this.mListener.onItemClick(i);
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
