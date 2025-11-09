package com.android.settings.fuelgauge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class UnisocAppViewHolder {
    public ImageView appIcon;
    public TextView appName;
    public TextView appState;
    public Switch app_switch;
    public TextView high_usage;
    public TextView high_usage_type;
    public View rootView;

    public static UnisocAppViewHolder createOrRecycle(LayoutInflater layoutInflater, View view) {
        if (view == null) {
            View inflate = layoutInflater.inflate(R$layout.app_battery_item, (ViewGroup) null);
            UnisocAppViewHolder unisocAppViewHolder = new UnisocAppViewHolder();
            unisocAppViewHolder.rootView = inflate;
            unisocAppViewHolder.appName = (TextView) inflate.findViewById(16908310);
            unisocAppViewHolder.appIcon = (ImageView) inflate.findViewById(16908294);
            unisocAppViewHolder.high_usage = (TextView) inflate.findViewById(R$id.high_usage);
            inflate.setTag(unisocAppViewHolder);
            return unisocAppViewHolder;
        }
        return (UnisocAppViewHolder) view.getTag();
    }

    public static UnisocAppViewHolder createForCloseApp(LayoutInflater layoutInflater, View view) {
        if (view == null) {
            View inflate = layoutInflater.inflate(R$layout.unisoc_preference_app, (ViewGroup) null);
            UnisocAppViewHolder unisocAppViewHolder = new UnisocAppViewHolder();
            unisocAppViewHolder.rootView = inflate;
            unisocAppViewHolder.appName = (TextView) inflate.findViewById(16908310);
            unisocAppViewHolder.appIcon = (ImageView) inflate.findViewById(16908294);
            unisocAppViewHolder.app_switch = (Switch) inflate.findViewById(R$id.app_switch);
            unisocAppViewHolder.appState = (TextView) inflate.findViewById(R$id.app_state);
            unisocAppViewHolder.high_usage = (TextView) inflate.findViewById(R$id.high_usage);
            inflate.setTag(unisocAppViewHolder);
            return unisocAppViewHolder;
        }
        return (UnisocAppViewHolder) view.getTag();
    }

    public static UnisocAppViewHolder createForPowerIntensiveApps(LayoutInflater layoutInflater, View view) {
        if (view == null) {
            View inflate = layoutInflater.inflate(R$layout.app_power_intensive_item, (ViewGroup) null);
            UnisocAppViewHolder unisocAppViewHolder = new UnisocAppViewHolder();
            unisocAppViewHolder.rootView = inflate;
            unisocAppViewHolder.appName = (TextView) inflate.findViewById(16908310);
            unisocAppViewHolder.appIcon = (ImageView) inflate.findViewById(16908294);
            unisocAppViewHolder.high_usage = (TextView) inflate.findViewById(R$id.high_usage);
            unisocAppViewHolder.high_usage_type = (TextView) inflate.findViewById(R$id.high_usage_type);
            inflate.setTag(unisocAppViewHolder);
            return unisocAppViewHolder;
        }
        return (UnisocAppViewHolder) view.getTag();
    }
}
