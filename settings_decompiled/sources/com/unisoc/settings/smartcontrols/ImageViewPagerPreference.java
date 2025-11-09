package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.DotsPageIndicator;
/* loaded from: classes2.dex */
public class ImageViewPagerPreference extends Preference {
    private int mCurrentItem;
    private ImageViewPagerAdapter mPreviewAdapter;

    ImageViewPagerPreference(Context context) {
        super(context);
        init();
    }

    public ImageViewPagerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    ImageViewPagerPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    ImageViewPagerPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ViewPager viewPager = (ViewPager) preferenceViewHolder.findViewById(R$id.preview_pager);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) preferenceViewHolder.findViewById(R$id.page_indicator);
        updateAdapterIfNeeded(viewPager, dotsPageIndicator, this.mPreviewAdapter);
        updatePagerAndIndicator(viewPager, dotsPageIndicator);
    }

    public void setPreviewAdapter(ImageViewPagerAdapter imageViewPagerAdapter) {
        if (imageViewPagerAdapter != this.mPreviewAdapter) {
            this.mPreviewAdapter = imageViewPagerAdapter;
            notifyChanged();
        }
    }

    private void updateAdapterIfNeeded(ViewPager viewPager, DotsPageIndicator dotsPageIndicator, ImageViewPagerAdapter imageViewPagerAdapter) {
        if (viewPager.getAdapter() == imageViewPagerAdapter) {
            return;
        }
        viewPager.setAdapter(imageViewPagerAdapter);
        if (imageViewPagerAdapter != null) {
            dotsPageIndicator.setViewPager(viewPager);
        } else {
            this.mCurrentItem = 0;
        }
    }

    private void updatePagerAndIndicator(ViewPager viewPager, DotsPageIndicator dotsPageIndicator) {
        if (viewPager.getAdapter() == null) {
            return;
        }
        int currentItem = viewPager.getCurrentItem();
        int i = this.mCurrentItem;
        if (currentItem != i) {
            viewPager.setCurrentItem(i);
        }
        dotsPageIndicator.setVisibility(viewPager.getAdapter().getCount() > 1 ? 0 : 8);
    }

    private void init() {
        setLayoutResource(R$layout.gesture_image_viewpager);
    }
}
