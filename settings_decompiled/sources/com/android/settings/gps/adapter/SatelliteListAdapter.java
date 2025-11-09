package com.android.settings.gps.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.SettingsApplication;
import com.android.settings.gps.data.SatelliteInfo;
import com.android.settings.gps.view.JSatelliteProgBar;
import com.android.settings.utils.SatelliteProgressUtils;
import java.util.List;
/* loaded from: classes.dex */
public class SatelliteListAdapter extends RecyclerView.Adapter<Holder> {
    private Context mContext;
    private List<SatelliteInfo> mDatas;
    private LayoutInflater mInflater;

    public SatelliteListAdapter(Context context, List<SatelliteInfo> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = list;
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate;
        if (SettingsApplication.mWidthFix > SettingsApplication.mHeightFix) {
            inflate = this.mInflater.inflate(R$layout.item_satellite_progress, viewGroup, false);
        } else {
            inflate = this.mInflater.inflate(R$layout.item_satellite_progress_h, viewGroup, false);
        }
        Holder holder = new Holder(inflate);
        holder.mSign = (TextView) inflate.findViewById(R$id.gps_sign_strength);
        holder.mId = (TextView) inflate.findViewById(R$id.gps_sign_progress);
        holder.progBar = (JSatelliteProgBar) inflate.findViewById(R$id.gps_progress);
        holder.mType = (ImageView) inflate.findViewById(R$id.gps_type_view);
        holder.progBar.setBkWidthAndHeight(SatelliteProgressUtils.getProgressWidth(), SatelliteProgressUtils.getProgressHeight());
        Log.d("SatelliteListAdapter", "onCreateViewHolder: " + SatelliteProgressUtils.getProgressWidth() + "   " + SatelliteProgressUtils.getProgressHeight());
        return holder;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(Holder holder, int i) {
        Log.d("SatelliteListAdapter", "holder.mSign ===" + this.mDatas.get(i).getPrn() + " holder.id " + this.mDatas.get(i).getSnr());
        TextView textView = holder.mSign;
        StringBuilder sb = new StringBuilder();
        sb.append(this.mDatas.get(i).getPrn());
        sb.append("");
        textView.setText(sb.toString());
        TextView textView2 = holder.mId;
        textView2.setText("" + this.mDatas.get(i).getSnr());
        holder.progBar.setProgressNew(this.mDatas.get(i).getPrn());
        holder.progBar.setIsUse(this.mDatas.get(i).getIsUse());
        if (this.mDatas.get(i).getPrn() == 0 && this.mDatas.get(i).getSnr() == 0) {
            holder.mType.setVisibility(4);
        } else {
            holder.mType.setVisibility(0);
        }
        if (this.mDatas.get(i).getIsBD() == 0) {
            holder.mType.setImageDrawable(this.mContext.getResources().getDrawable(R$drawable.set_satellite_gps));
        } else if (this.mDatas.get(i).getIsBD() == 2) {
            holder.mType.setImageDrawable(this.mContext.getResources().getDrawable(R$drawable.set_satellite_gln));
        } else if (this.mDatas.get(i).getIsBD() == 1) {
            holder.mType.setImageDrawable(this.mContext.getResources().getDrawable(R$drawable.set_satellite_bd));
        } else if (this.mDatas.get(i).getIsBD() == 3) {
            holder.mType.setImageDrawable(this.mContext.getResources().getDrawable(R$drawable.set_satellite_gal));
        } else {
            holder.mType.setImageDrawable(this.mContext.getResources().getDrawable(R$drawable.set_satellite_ufo));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mDatas.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Holder extends RecyclerView.ViewHolder {
        TextView mId;
        TextView mSign;
        ImageView mType;
        JSatelliteProgBar progBar;

        public Holder(View view) {
            super(view);
        }
    }

    public void setData(List<SatelliteInfo> list) {
        this.mDatas = list;
    }
}
