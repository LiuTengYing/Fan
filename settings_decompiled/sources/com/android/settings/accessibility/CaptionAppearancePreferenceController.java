package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.view.accessibility.CaptioningManager;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
/* loaded from: classes.dex */
public class CaptionAppearancePreferenceController extends BasePreferenceController {
    private final CaptioningManager mCaptioningManager;

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

    public CaptionAppearancePreferenceController(Context context, String str) {
        super(context, str);
        this.mCaptioningManager = (CaptioningManager) context.getSystemService(CaptioningManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getString(R$string.preference_summary_default_combination, geFontScaleSummary(), getPresetSummary());
    }

    private float[] getFontScaleValuesArray() {
        String[] stringArray = this.mContext.getResources().getStringArray(R$array.captioning_font_size_selector_values);
        int length = stringArray.length;
        float[] fArr = new float[length];
        for (int i = 0; i < length; i++) {
            fArr[i] = Float.parseFloat(stringArray[i]);
        }
        return fArr;
    }

    private CharSequence geFontScaleSummary() {
        float[] fontScaleValuesArray = getFontScaleValuesArray();
        String[] stringArray = this.mContext.getResources().getStringArray(R$array.captioning_font_size_selector_titles);
        int indexOf = Floats.indexOf(fontScaleValuesArray, this.mCaptioningManager.getFontScale());
        if (indexOf == -1) {
            indexOf = 0;
        }
        return stringArray[indexOf];
    }

    private CharSequence getPresetSummary() {
        return this.mContext.getResources().getStringArray(R$array.captioning_preset_selector_titles)[Ints.indexOf(this.mContext.getResources().getIntArray(R$array.captioning_preset_selector_values), this.mCaptioningManager.getRawUserStyle())];
    }
}
