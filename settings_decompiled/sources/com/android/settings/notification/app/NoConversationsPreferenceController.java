package com.android.settings.notification.app;

import android.content.Context;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
class NoConversationsPreferenceController extends AbstractPreferenceController {
    private boolean mIsAvailable;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "no_conversations";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NoConversationsPreferenceController(Context context) {
        super(context);
        this.mIsAvailable = false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mIsAvailable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAvailable(boolean z) {
        this.mIsAvailable = z;
    }
}
