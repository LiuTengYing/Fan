package com.android.settings.common.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class NaviAppAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ResolveInfo> mData;
    private PackageManager manager;
    private String name = "";

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public NaviAppAdapter(Context context, ArrayList<ResolveInfo> arrayList, PackageManager packageManager) {
        this.mContext = context;
        this.mData = arrayList;
        this.manager = packageManager;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view2 = LayoutInflater.from(this.mContext).inflate(R$layout.app_list_item, (ViewGroup) null);
            view2.setTag(holder);
        } else {
            view2 = view;
            holder = (Holder) view.getTag();
        }
        holder.mTitle = (TextView) view2.findViewById(R$id.app_name);
        holder.mIcon = (ImageView) view2.findViewById(R$id.app_icon);
        holder.mSelectIcon = (LinearLayout) view2.findViewById(R$id.app_icon_select);
        holder.mTitle.setText(this.mData.get(i).loadLabel(this.manager).toString());
        holder.mIcon.setImageDrawable(this.mData.get(i).activityInfo.loadIcon(this.manager));
        if (this.mData.get(i).activityInfo.packageName.equals(this.name)) {
            holder.mSelectIcon.setBackground(this.mContext.getResources().getDrawable(R$drawable.select_bk_10));
        } else {
            holder.mSelectIcon.setBackground(null);
        }
        return view2;
    }

    /* loaded from: classes.dex */
    class Holder {
        ImageView mIcon;
        LinearLayout mSelectIcon;
        TextView mTitle;

        Holder() {
        }
    }

    public void setSelectPackage(String str) {
        this.name = str;
    }
}
