package com.android.settings.display;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.display.PreviewSeekBarPreferenceFragment;
import com.android.settings.widget.DotsPageIndicator;
import com.android.settings.widget.LabeledSeekBar;
/* loaded from: classes.dex */
public abstract class PreviewSeekBarPreferenceFragment extends SettingsPreferenceFragment {
    protected int mCurrentIndex;
    protected String[] mEntries;
    protected int mInitialIndex;
    private TextView mLabel;
    private View mLarger;
    private long mLastCommitTime;
    private DotsPageIndicator mPageIndicator;
    private ViewPager mPreviewPager;
    private PreviewPagerAdapter mPreviewPagerAdapter;
    private LabeledSeekBar mSeekBar;
    private View mSmaller;
    private ViewPager.OnPageChangeListener mPreviewPageChangeListener = new ViewPager.OnPageChangeListener() { // from class: com.android.settings.display.PreviewSeekBarPreferenceFragment.1
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            PreviewSeekBarPreferenceFragment.this.mPreviewPager.sendAccessibilityEvent(16384);
        }
    };
    private ViewPager.OnPageChangeListener mPageIndicatorPageChangeListener = new ViewPager.OnPageChangeListener() { // from class: com.android.settings.display.PreviewSeekBarPreferenceFragment.2
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            PreviewSeekBarPreferenceFragment.this.setPagerIndicatorContentDescription(i);
        }
    };

    protected abstract void commit();

    protected abstract Configuration createConfig(Configuration configuration, int i);

    protected abstract int getActivityLayoutResId();

    protected abstract int[] getPreviewSampleResIds();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class onPreviewSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final Choreographer.FrameCallback mCommit;
        private long mCommitDelayMs;
        private boolean mIsChanged;
        private boolean mSeekByTouch;

        private onPreviewSeekBarChangeListener() {
            this.mCommit = new Choreographer.FrameCallback() { // from class: com.android.settings.display.PreviewSeekBarPreferenceFragment$onPreviewSeekBarChangeListener$$ExternalSyntheticLambda1
                @Override // android.view.Choreographer.FrameCallback
                public final void doFrame(long j) {
                    PreviewSeekBarPreferenceFragment.onPreviewSeekBarChangeListener.this.lambda$new$0(j);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(long j) {
            PreviewSeekBarPreferenceFragment.this.commit();
            PreviewSeekBarPreferenceFragment.this.mLastCommitTime = SystemClock.elapsedRealtime();
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            PreviewSeekBarPreferenceFragment previewSeekBarPreferenceFragment = PreviewSeekBarPreferenceFragment.this;
            if (previewSeekBarPreferenceFragment.mCurrentIndex == i) {
                this.mIsChanged = false;
                return;
            }
            this.mIsChanged = true;
            previewSeekBarPreferenceFragment.setPreviewLayer(i, false);
            if (this.mSeekByTouch) {
                this.mCommitDelayMs = 100L;
                return;
            }
            this.mCommitDelayMs = 300L;
            commitOnNextFrame();
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.mSeekByTouch = true;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.mSeekByTouch = false;
            if (this.mIsChanged) {
                if (PreviewSeekBarPreferenceFragment.this.mPreviewPagerAdapter.isAnimating()) {
                    PreviewSeekBarPreferenceFragment.this.mPreviewPagerAdapter.setAnimationEndAction(new Runnable() { // from class: com.android.settings.display.PreviewSeekBarPreferenceFragment$onPreviewSeekBarChangeListener$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PreviewSeekBarPreferenceFragment.onPreviewSeekBarChangeListener.this.commitOnNextFrame();
                        }
                    });
                } else {
                    commitOnNextFrame();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void commitOnNextFrame() {
            if (SystemClock.elapsedRealtime() - PreviewSeekBarPreferenceFragment.this.mLastCommitTime < 800) {
                this.mCommitDelayMs += 800;
            }
            Choreographer choreographer = Choreographer.getInstance();
            choreographer.removeFrameCallback(this.mCommit);
            choreographer.postFrameCallbackDelayed(this.mCommit, this.mCommitDelayMs);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong("mLastCommitTime", this.mLastCommitTime);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (bundle != null) {
            this.mLastCommitTime = bundle.getLong("mLastCommitTime");
        }
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ViewGroup viewGroup2 = (ViewGroup) onCreateView.findViewById(16908351);
        viewGroup2.removeAllViews();
        View inflate = layoutInflater.inflate(getActivityLayoutResId(), viewGroup2, false);
        viewGroup2.addView(inflate);
        this.mLabel = (TextView) inflate.findViewById(R$id.current_label);
        int max = Math.max(1, this.mEntries.length - 1);
        LabeledSeekBar labeledSeekBar = (LabeledSeekBar) inflate.findViewById(R$id.seek_bar);
        this.mSeekBar = labeledSeekBar;
        labeledSeekBar.setLabels(this.mEntries);
        this.mSeekBar.setMax(max);
        View findViewById = inflate.findViewById(R$id.smaller);
        this.mSmaller = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.PreviewSeekBarPreferenceFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PreviewSeekBarPreferenceFragment.this.lambda$onCreateView$0(view);
            }
        });
        View findViewById2 = inflate.findViewById(R$id.larger);
        this.mLarger = findViewById2;
        findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.PreviewSeekBarPreferenceFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PreviewSeekBarPreferenceFragment.this.lambda$onCreateView$1(view);
            }
        });
        if (this.mEntries.length == 1) {
            this.mSeekBar.setEnabled(false);
        }
        Context context = getContext();
        Configuration configuration = context.getResources().getConfiguration();
        boolean z = configuration.getLayoutDirection() == 1;
        Configuration[] configurationArr = new Configuration[this.mEntries.length];
        for (int i = 0; i < this.mEntries.length; i++) {
            configurationArr[i] = createConfig(configuration, i);
        }
        int[] previewSampleResIds = getPreviewSampleResIds();
        this.mPreviewPager = (ViewPager) inflate.findViewById(R$id.preview_pager);
        PreviewPagerAdapter previewPagerAdapter = new PreviewPagerAdapter(context, z, previewSampleResIds, configurationArr);
        this.mPreviewPagerAdapter = previewPagerAdapter;
        this.mPreviewPager.setAdapter(previewPagerAdapter);
        this.mPreviewPager.setCurrentItem(z ? previewSampleResIds.length - 1 : 0);
        this.mPreviewPager.addOnPageChangeListener(this.mPreviewPageChangeListener);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) inflate.findViewById(R$id.page_indicator);
        this.mPageIndicator = dotsPageIndicator;
        if (previewSampleResIds.length > 1) {
            dotsPageIndicator.setViewPager(this.mPreviewPager);
            this.mPageIndicator.setVisibility(0);
            this.mPageIndicator.setOnPageChangeListener(this.mPageIndicatorPageChangeListener);
        } else {
            dotsPageIndicator.setVisibility(8);
        }
        setPreviewLayer(this.mInitialIndex, false);
        return onCreateView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(View view) {
        int progress = this.mSeekBar.getProgress();
        if (progress > 0) {
            this.mSeekBar.setProgress(progress - 1, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(View view) {
        int progress = this.mSeekBar.getProgress();
        if (progress < this.mSeekBar.getMax()) {
            this.mSeekBar.setProgress(progress + 1, true);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mSeekBar.setProgress(this.mCurrentIndex);
        this.mSeekBar.setOnSeekBarChangeListener(new onPreviewSeekBarChangeListener());
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mSeekBar.setOnSeekBarChangeListener(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreviewLayer(int i, boolean z) {
        this.mLabel.setText(this.mEntries[i]);
        this.mSmaller.setEnabled(i > 0);
        this.mLarger.setEnabled(i < this.mEntries.length - 1);
        setPagerIndicatorContentDescription(this.mPreviewPager.getCurrentItem());
        this.mPreviewPagerAdapter.setPreviewLayer(i, this.mCurrentIndex, this.mPreviewPager.getCurrentItem(), z);
        this.mCurrentIndex = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPagerIndicatorContentDescription(int i) {
        this.mPageIndicator.setContentDescription(getString(R$string.preview_page_indicator_content_description, Integer.valueOf(i + 1), Integer.valueOf(getPreviewSampleResIds().length)));
    }
}
