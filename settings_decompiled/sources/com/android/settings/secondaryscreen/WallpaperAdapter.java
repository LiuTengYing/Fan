package com.android.settings.secondaryscreen;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.SettingsApplication;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class WallpaperAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Drawable> mData;
    private int wallpaperWidth = getWallpaperWidth();
    private int wallpaperHeight = (int) (getWallpaperWidth() * 0.65d);

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public WallpaperAdapter(Context context, ArrayList<Drawable> arrayList) {
        this.mContext = context;
        this.mData = arrayList;
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
            view2 = LayoutInflater.from(this.mContext).inflate(R$layout.wallpaper_list_item, (ViewGroup) null);
            holder.mWallpaper = (ImageView) view2.findViewById(R$id.wallpaper);
            view2.setTag(holder);
        } else {
            view2 = view;
            holder = (Holder) view.getTag();
        }
        holder.mWallpaper.setMaxWidth(this.wallpaperWidth);
        holder.mWallpaper.setMaxHeight(this.wallpaperHeight);
        holder.mWallpaper.setImageDrawable(this.mData.get(i));
        return view2;
    }

    /* loaded from: classes.dex */
    class Holder {
        ImageView mWallpaper;

        Holder() {
        }
    }

    private int getWallpaperWidth() {
        double d;
        double d2;
        int i = SettingsApplication.mWidthFix;
        int i2 = SettingsApplication.mHeightFix;
        if (i > i2) {
            int i3 = SettingsApplication.mWidthFix;
            if (i3 > 1100) {
                d = (i3 * 0.6d) - 160.0d;
                d2 = 0.33d;
            } else {
                d = i3;
                d2 = 0.21d;
            }
        } else {
            d = i2;
            d2 = 0.25d;
        }
        return (int) (d * d2);
    }
}
