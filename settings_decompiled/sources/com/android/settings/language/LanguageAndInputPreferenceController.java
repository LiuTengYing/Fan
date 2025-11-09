package com.android.settings.language;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class LanguageAndInputPreferenceController extends BasePreferenceController {
    private InputMethodManager mInputMethodManager;
    private PackageManager mPackageManager;

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

    public LanguageAndInputPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = this.mContext.getPackageManager();
        this.mInputMethodManager = (InputMethodManager) this.mContext.getSystemService(InputMethodManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "default_input_method");
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        String packageName = ComponentName.unflattenFromString(string).getPackageName();
        for (InputMethodInfo inputMethodInfo : this.mInputMethodManager.getInputMethodList()) {
            if (TextUtils.equals(inputMethodInfo.getPackageName(), packageName)) {
                return inputMethodInfo.loadLabel(this.mPackageManager);
            }
        }
        return "";
    }
}
