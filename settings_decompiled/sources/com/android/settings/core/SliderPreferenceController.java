package com.android.settings.core;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.slice.builders.SliceAction;
import com.android.settings.widget.SeekBarPreference;
/* loaded from: classes.dex */
public abstract class SliderPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public abstract int getMax();

    public abstract int getMin();

    public SliceAction getSliceEndItem(Context context) {
        return null;
    }

    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getSliceType() {
        return 2;
    }

    public abstract int getSliderPosition();

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public abstract boolean setSliderPosition(int i);

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SliderPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return setSliderPosition(((Integer) obj).intValue());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SeekBarPreference) {
            ((SeekBarPreference) preference).setProgress(getSliderPosition());
        } else if (preference instanceof androidx.preference.SeekBarPreference) {
            ((androidx.preference.SeekBarPreference) preference).setValue(getSliderPosition());
        }
    }
}
