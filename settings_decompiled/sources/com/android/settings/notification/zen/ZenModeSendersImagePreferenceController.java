package com.android.settings.notification.zen;

import android.content.Context;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class ZenModeSendersImagePreferenceController extends AbstractZenModePreferenceController {
    private ImageView mImageView;
    private final boolean mIsMessages;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mImageView = (ImageView) ((LayoutPreference) preferenceScreen.findPreference(this.KEY)).findViewById(R$id.zen_mode_settings_senders_image);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        int i2;
        String string;
        int i3;
        int prioritySenders = getPrioritySenders();
        if (prioritySenders == 0) {
            if (this.mIsMessages) {
                i3 = R$drawable.zen_messages_any;
            } else {
                i3 = R$drawable.zen_calls_any;
            }
            string = this.mContext.getString(R$string.zen_mode_from_anyone);
        } else if (1 == prioritySenders) {
            if (this.mIsMessages) {
                i3 = R$drawable.zen_messages_contacts;
            } else {
                i3 = R$drawable.zen_calls_contacts;
            }
            string = this.mContext.getString(R$string.zen_mode_from_contacts);
        } else if (2 == prioritySenders) {
            if (this.mIsMessages) {
                i3 = R$drawable.zen_messages_starred;
            } else {
                i3 = R$drawable.zen_calls_starred;
            }
            string = this.mContext.getString(R$string.zen_mode_from_starred);
        } else {
            boolean z = this.mIsMessages;
            if (z) {
                i = R$drawable.zen_messages_none;
            } else {
                i = R$drawable.zen_calls_none;
            }
            Context context = this.mContext;
            if (z) {
                i2 = R$string.zen_mode_none_messages;
            } else {
                i2 = R$string.zen_mode_none_calls;
            }
            int i4 = i;
            string = context.getString(i2);
            i3 = i4;
        }
        this.mImageView.setImageResource(i3);
        this.mImageView.setContentDescription(string);
    }

    private int getPrioritySenders() {
        if (this.mIsMessages) {
            return this.mBackend.getPriorityMessageSenders();
        }
        return this.mBackend.getPriorityCallSenders();
    }
}
