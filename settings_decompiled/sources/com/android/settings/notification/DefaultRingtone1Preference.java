package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import com.android.settings.RingtonePreference;
/* loaded from: classes.dex */
public class DefaultRingtone1Preference extends RingtonePreference {
    public DefaultRingtone1Preference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
    }

    @Override // com.android.settings.RingtonePreference
    protected void onSaveRingtone(Uri uri) {
        RingtoneManager.setActualDefaultRingtoneUri(this.mUserContext, 8, uri);
    }

    @Override // com.android.settings.RingtonePreference
    protected Uri onRestoreRingtone() {
        return RingtoneManager.getActualDefaultRingtoneUri(this.mUserContext, 8);
    }
}
