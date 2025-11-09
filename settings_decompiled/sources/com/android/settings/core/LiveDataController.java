package com.android.settings.core;

import android.content.Context;
import android.content.IntentFilter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public abstract class LiveDataController extends BasePreferenceController {
    private MutableLiveData<CharSequence> mData;
    private Preference mPreference;
    protected CharSequence mSummary;

    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    protected abstract CharSequence getSummaryTextInBackground();

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

    public LiveDataController(Context context, String str) {
        super(context, str);
        this.mData = new MutableLiveData<>();
        this.mSummary = context.getText(R$string.summary_placeholder);
    }

    public void initLifeCycleOwner(Fragment fragment) {
        this.mData.observe(fragment, new Observer() { // from class: com.android.settings.core.LiveDataController$$ExternalSyntheticLambda0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                LiveDataController.this.lambda$initLifeCycleOwner$0((CharSequence) obj);
            }
        });
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.core.LiveDataController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LiveDataController.this.lambda$initLifeCycleOwner$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initLifeCycleOwner$0(CharSequence charSequence) {
        this.mSummary = charSequence;
        refreshSummary(this.mPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initLifeCycleOwner$1() {
        this.mData.postValue(getSummaryTextInBackground());
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mSummary;
    }
}
