package com.android.settings.widget;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PreferenceCategoryController extends BasePreferenceController {
    private final List<AbstractPreferenceController> mChildren;
    private final String mKey;

    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PreferenceCategoryController(Context context, String str) {
        super(context, str);
        this.mKey = str;
        this.mChildren = new ArrayList();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        List<AbstractPreferenceController> list = this.mChildren;
        if (list == null || list.isEmpty()) {
            return 3;
        }
        for (AbstractPreferenceController abstractPreferenceController : this.mChildren) {
            if (abstractPreferenceController.isAvailable()) {
                return 0;
            }
        }
        return 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mKey;
    }

    public PreferenceCategoryController setChildren(List<AbstractPreferenceController> list) {
        this.mChildren.clear();
        if (list != null) {
            this.mChildren.addAll(list);
        }
        return this;
    }
}
