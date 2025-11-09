package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
/* loaded from: classes.dex */
public class DefaultRingtonePreference extends RingtonePreference {
    public DefaultRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
    }

    @Override // com.android.settings.RingtonePreference
    protected void onSaveRingtone(Uri uri) {
        if (uri == null) {
            setActualDefaultRingtoneUri(uri);
            return;
        }
        String type = this.mUserContext.getContentResolver().getType(uri);
        if (type == null) {
            Log.e("DefaultRingtonePreference", "onSaveRingtone for URI:" + uri + " ignored: failure to find mimeType (no access from this context?)");
        } else if (!type.startsWith("audio/") && !type.equals("application/ogg")) {
            Log.e("DefaultRingtonePreference", "onSaveRingtone for URI:" + uri + " ignored: associated mimeType:" + type + " is not an audio type");
        } else {
            setActualDefaultRingtoneUri(uri);
        }
    }

    void setActualDefaultRingtoneUri(Uri uri) {
        RingtoneManager.setActualDefaultRingtoneUri(this.mUserContext, getRingtoneType(), uri);
    }

    @Override // com.android.settings.RingtonePreference
    protected Uri onRestoreRingtone() {
        return RingtoneManager.getActualDefaultRingtoneUri(this.mUserContext, getRingtoneType());
    }
}
