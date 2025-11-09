package com.android.settings.sim;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SimListDialogFragment extends SimDialogFragment {
    protected SelectSubscriptionAdapter mAdapter;
    List<SubscriptionInfo> mSubscriptions;

    public int getMetricsCategory() {
        return 1707;
    }

    public static SimListDialogFragment newInstance(int i, int i2, boolean z, boolean z2) {
        SimListDialogFragment simListDialogFragment = new SimListDialogFragment();
        Bundle initArguments = SimDialogFragment.initArguments(i, i2);
        initArguments.putBoolean("include_ask_every_time", z);
        initArguments.putBoolean("show_cancel_item", z2);
        simListDialogFragment.setArguments(initArguments);
        return simListDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mSubscriptions = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R$layout.sim_confirm_dialog_title_multiple_enabled_profiles_supported, (ViewGroup) null).findViewById(R$id.title);
        textView.setText(getContext().getString(getTitleResId()));
        builder.setCustomTitle(textView);
        this.mAdapter = new SelectSubscriptionAdapter(builder.getContext(), this.mSubscriptions);
        AlertDialog create = builder.create();
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.sim_confirm_dialog_multiple_enabled_profiles_supported, (ViewGroup) null);
        ListView listView = inflate != null ? (ListView) inflate.findViewById(R$id.carrier_list) : null;
        if (listView != null) {
            setAdapter(listView);
            listView.setVisibility(0);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.sim.SimListDialogFragment.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    SimListDialogFragment.this.onClick(i);
                }
            });
        }
        create.setView(inflate);
        updateDialog();
        return create;
    }

    public void onClick(int i) {
        if (i >= 0 && i < this.mSubscriptions.size()) {
            SubscriptionInfo subscriptionInfo = this.mSubscriptions.get(i);
            ((SimDialogActivity) getActivity()).onSubscriptionSelected(getDialogType(), subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1);
        }
        dismiss();
    }

    protected List<SubscriptionInfo> getCurrentSubscriptions() {
        if (getContext() == null) {
            return null;
        }
        return ((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
    }

    @Override // com.android.settings.sim.SimDialogFragment
    public void updateDialog() {
        Log.d("SimListDialogFragment", "Dialog updated, dismiss status: " + this.mWasDismissed);
        if (this.mWasDismissed) {
            return;
        }
        List<SubscriptionInfo> currentSubscriptions = getCurrentSubscriptions();
        if (currentSubscriptions == null || currentSubscriptions.size() <= 1) {
            if (getContext() == null) {
                return;
            }
            dismiss();
            return;
        }
        boolean z = getArguments().getBoolean("include_ask_every_time");
        boolean z2 = getArguments().getBoolean("show_cancel_item");
        if (z || z2) {
            ArrayList arrayList = new ArrayList(currentSubscriptions.size() + (z ? 1 : 0) + (z2 ? 1 : 0));
            if (z) {
                arrayList.add(null);
            }
            arrayList.addAll(currentSubscriptions);
            if (z2) {
                arrayList.add(null);
            }
            currentSubscriptions = arrayList;
        }
        if (currentSubscriptions.equals(this.mSubscriptions)) {
            return;
        }
        this.mSubscriptions.clear();
        this.mSubscriptions.addAll(currentSubscriptions);
        this.mAdapter.notifyDataSetChanged();
    }

    void setAdapter(ListView listView) {
        listView.setAdapter((ListAdapter) this.mAdapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SelectSubscriptionAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        List<SubscriptionInfo> mSubscriptions;

        public SelectSubscriptionAdapter(Context context, List<SubscriptionInfo> list) {
            this.mSubscriptions = list;
            this.mContext = context;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mSubscriptions.size();
        }

        @Override // android.widget.Adapter
        public SubscriptionInfo getItem(int i) {
            return this.mSubscriptions.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            SubscriptionInfo subscriptionInfo = this.mSubscriptions.get(i);
            if (subscriptionInfo == null) {
                return -1L;
            }
            return subscriptionInfo.getSubscriptionId();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.from(viewGroup.getContext());
                }
                view = this.mInflater.inflate(R$layout.select_account_list_item, viewGroup, false);
            }
            SubscriptionInfo item = getItem(i);
            TextView textView = (TextView) view.findViewById(R$id.title);
            TextView textView2 = (TextView) view.findViewById(R$id.summary);
            if (item == null) {
                if (i == 0) {
                    textView.setText(R$string.sim_calls_ask_first_prefs_title);
                } else {
                    textView.setText(R$string.sim_action_cancel);
                }
                textView2.setVisibility(8);
            } else {
                textView.setText(SubscriptionUtil.getUniqueSubscriptionDisplayName(item, this.mContext));
                String number = isMdnProvisioned(item.getNumber()) ? item.getNumber() : "";
                if (!TextUtils.isEmpty(number)) {
                    textView2.setVisibility(0);
                    textView2.setText(number);
                } else {
                    textView2.setVisibility(8);
                }
            }
            return view;
        }

        private boolean isMdnProvisioned(String str) {
            return (TextUtils.isEmpty(str) || str.matches("[\\D0]+")) ? false : true;
        }
    }
}
