package com.android.settings.applications.intentpicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import com.android.settings.R$layout;
import java.util.List;
/* loaded from: classes.dex */
public class SupportedLinksAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<SupportedLinkWrapper> mWrapperList;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public SupportedLinksAdapter(Context context, List<SupportedLinkWrapper> list) {
        this.mContext = context;
        this.mWrapperList = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mWrapperList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        if (i < this.mWrapperList.size()) {
            return this.mWrapperList.get(i);
        }
        return null;
    }

    @Override // android.widget.Adapter
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(R$layout.supported_links_dialog_item, (ViewGroup) null);
        }
        final CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908308);
        checkedTextView.setText(this.mWrapperList.get(i).getDisplayTitle(this.mContext));
        checkedTextView.setEnabled(this.mWrapperList.get(i).isEnabled());
        checkedTextView.setChecked(this.mWrapperList.get(i).isChecked());
        checkedTextView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.intentpicker.SupportedLinksAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                SupportedLinksAdapter.this.lambda$getView$0(checkedTextView, i, view2);
            }
        });
        return view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getView$0(CheckedTextView checkedTextView, int i, View view) {
        checkedTextView.toggle();
        this.mWrapperList.get(i).setChecked(checkedTextView.isChecked());
    }
}
