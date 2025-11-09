package com.unisoc.settings.smartcontrols;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import com.android.settings.R$id;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class ImageViewPagerAdapter extends PagerAdapter {
    private List<View> listMode;

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public ImageViewPagerAdapter(List<ImageViewMode> list) {
        if (this.listMode == null) {
            this.listMode = new ArrayList();
        }
        this.listMode.clear();
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i).getView();
            ((ImageView) view.findViewById(R$id.image_view)).setImageResource(list.get(i).getImageId());
            ((TextView) view.findViewById(R$id.title)).setText(list.get(i).getTitle());
            ((TextView) view.findViewById(R$id.summary)).setText(list.get(i).getSummary());
            this.listMode.add(view);
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView(this.listMode.get(i));
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.listMode.size();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        viewGroup.addView(this.listMode.get(i));
        return this.listMode.get(i);
    }
}
