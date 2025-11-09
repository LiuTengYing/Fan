package com.android.settings.wifidialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import java.util.List;
/* loaded from: classes2.dex */
public class WifiAdapter extends BaseAdapter {
    private Context mContext;
    private List<ScanResult> mData;
    private WifiInfo wifiInfo;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public WifiAdapter(Context context, List<ScanResult> list) {
        this.mContext = context;
        this.mData = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mData.size();
    }

    @Override // android.widget.Adapter
    public ScanResult getItem(int i) {
        return this.mData.get(i);
    }

    @Override // android.widget.Adapter
    @SuppressLint({"ViewHolder"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(R$layout.wifi_list_item, (ViewGroup) null);
            viewHolder.mNameTv = (TextView) view.findViewById(R$id.wifi_name);
            viewHolder.wifiLock = (ImageView) view.findViewById(R$id.wifi_lock);
            viewHolder.wifiSign = (ImageView) view.findViewById(R$id.wifi_sign);
            viewHolder.mWifiState = (ImageView) view.findViewById(R$id.wifi_connect_state);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        WifiInfo wifiInfo = this.wifiInfo;
        if (wifiInfo != null && i == 0) {
            viewHolder.mNameTv.setText(wifiInfo.getSSID().replace("\"", ""));
            viewHolder.mWifiState.setVisibility(0);
        } else {
            viewHolder.mWifiState.setVisibility(4);
            viewHolder.mNameTv.setText(this.mData.get(i).SSID);
        }
        return view;
    }

    /* loaded from: classes2.dex */
    class ViewHolder {
        TextView mNameTv;
        ImageView mWifiState;
        ImageView wifiLock;
        ImageView wifiSign;

        ViewHolder() {
        }
    }

    public void setConnectInfo(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    public WifiInfo getConnectInfo() {
        return this.wifiInfo;
    }
}
