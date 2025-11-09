package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.Bundle;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
abstract class AbstractZenCustomRulePreferenceController extends AbstractZenModePreferenceController {
    String mId;
    AutomaticZenRule mRule;

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractZenCustomRulePreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mRule != null;
    }

    public void setIdAndRule(String str, AutomaticZenRule automaticZenRule) {
        this.mId = str;
        this.mRule = automaticZenRule;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("RULE_ID", this.mId);
        return bundle;
    }
}
