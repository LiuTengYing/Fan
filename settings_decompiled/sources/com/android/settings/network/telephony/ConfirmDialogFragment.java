package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ConfirmDialogFragment extends BaseDialogFragment implements DialogInterface.OnClickListener {

    /* loaded from: classes.dex */
    public interface OnConfirmListener {
        void onConfirm(int i, boolean z, int i2);
    }

    public static <T> void show(FragmentActivity fragmentActivity, Class<T> cls, int i, String str, String str2, String str3, String str4) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putCharSequence("msg", str2);
        bundle.putString("pos_button_string", str3);
        bundle.putString("neg_button_string", str4);
        BaseDialogFragment.setListener(fragmentActivity, null, cls, i, bundle);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "ConfirmDialogFragment");
    }

    public static <T> void show(FragmentActivity fragmentActivity, Class<T> cls, int i, String str, String str2, String str3, String str4, ArrayList<String> arrayList) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putCharSequence("msg", str2);
        bundle.putString("pos_button_string", str3);
        bundle.putString("neg_button_string", str4);
        bundle.putStringArrayList("list", arrayList);
        BaseDialogFragment.setListener(fragmentActivity, null, cls, i, bundle);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "ConfirmDialogFragment");
    }

    @Override // androidx.fragment.app.DialogFragment
    public final Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("title");
        String string2 = getArguments().getString("msg");
        String string3 = getArguments().getString("pos_button_string");
        String string4 = getArguments().getString("neg_button_string");
        final ArrayList<String> stringArrayList = getArguments().getStringArrayList("list");
        Log.i("ConfirmDialogFragment", "Showing dialog with title =" + string);
        AlertDialog.Builder negativeButton = new AlertDialog.Builder(getContext()).setPositiveButton(string3, this).setNegativeButton(string4, this);
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.sim_confirm_dialog_multiple_enabled_profiles_supported, (ViewGroup) null);
        if (stringArrayList != null && !stringArrayList.isEmpty() && inflate != null) {
            Log.i("ConfirmDialogFragment", "list =" + stringArrayList.toString());
            if (!TextUtils.isEmpty(string)) {
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R$layout.sim_confirm_dialog_title_multiple_enabled_profiles_supported, (ViewGroup) null).findViewById(R$id.title);
                textView.setText(string);
                negativeButton.setCustomTitle(textView);
            }
            TextView textView2 = (TextView) inflate.findViewById(R$id.msg);
            if (!TextUtils.isEmpty(string2) && textView2 != null) {
                textView2.setText(string2);
                textView2.setVisibility(0);
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R$layout.sim_confirm_dialog_item_multiple_enabled_profiles_supported, stringArrayList);
            ListView listView = (ListView) inflate.findViewById(R$id.carrier_list);
            if (listView != null) {
                listView.setVisibility(0);
                listView.setAdapter((ListAdapter) arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.network.telephony.ConfirmDialogFragment.1
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        Log.i("ConfirmDialogFragment", "list onClick =" + i);
                        Log.i("ConfirmDialogFragment", "list item =" + ((String) stringArrayList.get(i)));
                        if (i == stringArrayList.size() - 1) {
                            ConfirmDialogFragment.this.informCaller(false, -1);
                        } else {
                            ConfirmDialogFragment.this.informCaller(true, i);
                        }
                    }
                });
            }
            LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R$id.info_outline_layout);
            if (linearLayout != null) {
                linearLayout.setVisibility(0);
            }
            negativeButton.setView(inflate);
        } else {
            if (!TextUtils.isEmpty(string)) {
                negativeButton.setTitle(string);
            }
            if (!TextUtils.isEmpty(string2)) {
                negativeButton.setMessage(string2);
            }
        }
        AlertDialog create = negativeButton.create();
        create.setCanceledOnTouchOutside(false);
        return create;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("ConfirmDialogFragment", "dialog onClick =" + i);
        informCaller(i == -1, -1);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        informCaller(false, -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void informCaller(boolean z, int i) {
        OnConfirmListener onConfirmListener = (OnConfirmListener) getListener(OnConfirmListener.class);
        if (onConfirmListener == null) {
            return;
        }
        onConfirmListener.onConfirm(getTagInCaller(), z, i);
    }
}
