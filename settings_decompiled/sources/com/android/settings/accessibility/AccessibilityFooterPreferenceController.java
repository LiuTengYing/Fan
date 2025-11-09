package com.android.settings.accessibility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.HelpUtils;
/* loaded from: classes.dex */
public class AccessibilityFooterPreferenceController extends BasePreferenceController {
    private int mHelpResource;
    private String mIntroductionTitle;
    private String mLearnMoreText;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public AccessibilityFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        updateFooterPreferences((AccessibilityFooterPreference) preferenceScreen.findPreference(getPreferenceKey()));
    }

    public void setupHelpLink(int i, String str) {
        this.mHelpResource = i;
        this.mLearnMoreText = str;
    }

    protected int getHelpResource() {
        return this.mHelpResource;
    }

    protected String getLearnMoreText() {
        return this.mLearnMoreText;
    }

    public void setIntroductionTitle(String str) {
        this.mIntroductionTitle = str;
    }

    protected String getIntroductionTitle() {
        return this.mIntroductionTitle;
    }

    private void updateFooterPreferences(AccessibilityFooterPreference accessibilityFooterPreference) {
        final Intent intent;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getIntroductionTitle());
        stringBuffer.append("\n\n");
        stringBuffer.append(accessibilityFooterPreference.getTitle());
        accessibilityFooterPreference.setContentDescription(stringBuffer);
        if (getHelpResource() != 0) {
            Context context = this.mContext;
            intent = HelpUtils.getHelpIntent(context, context.getString(getHelpResource()), this.mContext.getClass().getName());
        } else {
            intent = null;
        }
        if (intent != null) {
            accessibilityFooterPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityFooterPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    view.startActivityForResult(intent, 0);
                }
            });
            accessibilityFooterPreference.setLearnMoreText(getLearnMoreText());
            accessibilityFooterPreference.setLinkEnabled(true);
        } else {
            accessibilityFooterPreference.setLinkEnabled(false);
        }
        accessibilityFooterPreference.setSelectable(false);
    }
}
