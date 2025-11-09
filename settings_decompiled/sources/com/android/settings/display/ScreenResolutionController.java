package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.view.Display;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class ScreenResolutionController extends BasePreferenceController {
    static final int FHD_WIDTH = 1080;
    static final int QHD_WIDTH = 1440;
    private Display mDisplay;

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

    public ScreenResolutionController(Context context, String str) {
        super(context, str);
        this.mDisplay = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(0);
    }

    private boolean isSupportedMode(int i) {
        for (Display.Mode mode : getSupportedModes()) {
            if (mode.getPhysicalWidth() == i) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkSupportedResolutions() {
        return isSupportedMode(FHD_WIDTH) && isSupportedMode(QHD_WIDTH);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return checkSupportedResolutions() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int displayWidth = getDisplayWidth();
        if (displayWidth != FHD_WIDTH) {
            if (displayWidth == QHD_WIDTH) {
                return this.mContext.getString(R$string.screen_resolution_summary_highest);
            }
            return this.mContext.getString(R$string.screen_resolution_title);
        }
        return this.mContext.getString(R$string.screen_resolution_summary_high);
    }

    public int getDisplayWidth() {
        return this.mDisplay.getMode().getPhysicalWidth();
    }

    public Display.Mode[] getSupportedModes() {
        return this.mDisplay.getSupportedModes();
    }
}
