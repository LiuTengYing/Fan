package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.SeekBar;
import androidx.preference.PreferenceScreen;
import com.android.settings.accessibility.TextReadingResetController;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.widget.LabeledSeekBarPreference;
/* loaded from: classes.dex */
class PreviewSizeSeekBarController extends BasePreferenceController implements TextReadingResetController.ResetStateListener {
    private ProgressInteractionListener mInteractionListener;
    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private LabeledSeekBarPreference mSeekBarPreference;
    private boolean mSeekByTouch;
    private final PreviewSizeData<? extends Number> mSizeData;

    /* loaded from: classes.dex */
    interface ProgressInteractionListener {
        void notifyPreferenceChanged();

        void onEndTrackingTouch();

        void onProgressChanged();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreviewSizeSeekBarController(Context context, String str, PreviewSizeData<? extends Number> previewSizeData) {
        super(context, str);
        this.mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.accessibility.PreviewSizeSeekBarController.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                PreviewSizeSeekBarController.this.mInteractionListener.notifyPreferenceChanged();
                if (PreviewSizeSeekBarController.this.mSeekByTouch || PreviewSizeSeekBarController.this.mInteractionListener == null) {
                    return;
                }
                PreviewSizeSeekBarController.this.mInteractionListener.onProgressChanged();
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                PreviewSizeSeekBarController.this.mSeekByTouch = true;
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                PreviewSizeSeekBarController.this.mSeekByTouch = false;
                if (PreviewSizeSeekBarController.this.mInteractionListener != null) {
                    PreviewSizeSeekBarController.this.mInteractionListener.onEndTrackingTouch();
                }
            }
        };
        this.mSizeData = previewSizeData;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInteractionListener(ProgressInteractionListener progressInteractionListener) {
        this.mInteractionListener = progressInteractionListener;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        int size = this.mSizeData.getValues().size();
        int initialIndex = this.mSizeData.getInitialIndex();
        LabeledSeekBarPreference labeledSeekBarPreference = (LabeledSeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSeekBarPreference = labeledSeekBarPreference;
        labeledSeekBarPreference.setMax(size - 1);
        this.mSeekBarPreference.setProgress(initialIndex);
        this.mSeekBarPreference.setContinuousUpdates(true);
        this.mSeekBarPreference.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
    }

    @Override // com.android.settings.accessibility.TextReadingResetController.ResetStateListener
    public void resetState() {
        this.mSeekBarPreference.setProgress(this.mSizeData.getValues().indexOf(this.mSizeData.getDefaultValue()));
    }
}
