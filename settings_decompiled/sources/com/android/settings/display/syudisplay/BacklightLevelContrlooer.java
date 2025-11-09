package com.android.settings.display.syudisplay;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.ipc.IpcObj;
import com.android.settings.widget.view.TextSeekBarCenter;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class BacklightLevelContrlooer extends BasePreferenceController {
    private static int mSelect;
    private String KEY;
    private TextSeekBarCenter mBrghtnessDay;
    private TextSeekBarCenter mBrghtnessNight;
    private TextView mBrightdayValue;
    private TextView mBrightnightValue;
    private ImageView mDay;
    private ImageView mNight;
    private LayoutPreference mPreference;

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

    public BacklightLevelContrlooer(Context context, String str) {
        super(context, str);
        this.KEY = "brightness_day_night";
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(this.KEY);
        this.mPreference = layoutPreference;
        this.mBrghtnessDay = (TextSeekBarCenter) layoutPreference.findViewById(R$id.seek_progress_day);
        this.mBrghtnessNight = (TextSeekBarCenter) this.mPreference.findViewById(R$id.seek_progress_night);
        this.mBrightdayValue = (TextView) this.mPreference.findViewById(R$id.brightness_day_value);
        this.mBrightnightValue = (TextView) this.mPreference.findViewById(R$id.brightness_night_value);
        this.mDay = (ImageView) this.mPreference.findViewById(R$id.img_day);
        this.mNight = (ImageView) this.mPreference.findViewById(R$id.img_night);
        this.mBrghtnessDay.getParent().requestDisallowInterceptTouchEvent(true);
        this.mBrghtnessNight.getParent().requestDisallowInterceptTouchEvent(true);
        initViews();
        super.displayPreference(preferenceScreen);
    }

    private void initViews() {
        this.mBrghtnessDay.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.display.syudisplay.BacklightLevelContrlooer.1
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(0, 11, i);
            }
        });
        this.mBrghtnessNight.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.display.syudisplay.BacklightLevelContrlooer.2
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                IpcObj.getInstance().setCmd(0, 12, i);
            }
        });
    }

    public void setBrightnessDay(int i) {
        TextSeekBarCenter textSeekBarCenter = this.mBrghtnessDay;
        if (textSeekBarCenter != null) {
            textSeekBarCenter.setProgress(i);
            TextView textView = this.mBrightdayValue;
            textView.setText(i + "");
        }
    }

    public void setBrightnessNight(int i) {
        TextSeekBarCenter textSeekBarCenter = this.mBrghtnessNight;
        if (textSeekBarCenter != null) {
            textSeekBarCenter.setProgress(i);
            TextView textView = this.mBrightnightValue;
            textView.setText(i + "");
        }
    }

    public void setDayNightSelect(int i) {
        if (i == 0) {
            this.mDay.setAlpha(1.0f);
            this.mNight.setAlpha(0.3f);
            return;
        }
        this.mDay.setAlpha(0.3f);
        this.mNight.setAlpha(1.0f);
    }
}
