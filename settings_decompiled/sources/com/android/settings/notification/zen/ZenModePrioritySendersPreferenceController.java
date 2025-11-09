package com.android.settings.notification.zen;

import android.content.Context;
import android.os.AsyncTask;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class ZenModePrioritySendersPreferenceController extends AbstractZenModePreferenceController {
    private ZenPrioritySendersHelper mHelper;
    private final boolean mIsMessages;
    private PreferenceCategory mPreferenceCategory;
    SelectorWithWidgetPreference.OnClickListener mSelectorClickListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModePrioritySendersPreferenceController(Context context, String str, Lifecycle lifecycle, boolean z, NotificationBackend notificationBackend) {
        super(context, str, lifecycle);
        this.mSelectorClickListener = new SelectorWithWidgetPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModePrioritySendersPreferenceController.2
            @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
            public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
                int[] iArr = ZenModePrioritySendersPreferenceController.this.mHelper.settingsToSaveOnClick(selectorWithWidgetPreference, ZenModePrioritySendersPreferenceController.this.getPrioritySenders(), ZenModePrioritySendersPreferenceController.this.getPriorityConversationSenders());
                int i = iArr[0];
                int i2 = iArr[1];
                if (i != -10) {
                    ZenModePrioritySendersPreferenceController zenModePrioritySendersPreferenceController = ZenModePrioritySendersPreferenceController.this;
                    zenModePrioritySendersPreferenceController.mBackend.saveSenders(zenModePrioritySendersPreferenceController.mIsMessages ? 4 : 8, i);
                }
                if (!ZenModePrioritySendersPreferenceController.this.mIsMessages || i2 == -10) {
                    return;
                }
                ZenModePrioritySendersPreferenceController.this.mBackend.saveConversationSenders(i2);
            }
        };
        this.mIsMessages = z;
        this.mHelper = new ZenPrioritySendersHelper(context, z, this.mBackend, notificationBackend, this.mSelectorClickListener);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        this.mHelper.displayPreference(preferenceCategory);
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mHelper.updateState(getPrioritySenders(), getPriorityConversationSenders());
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        if (this.mIsMessages) {
            updateChannelCounts();
        }
        this.mHelper.updateSummaries();
    }

    private void updateChannelCounts() {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.zen.ZenModePrioritySendersPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                ZenModePrioritySendersPreferenceController.this.mHelper.updateChannelCounts();
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r1) {
                if (((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext == null) {
                    return;
                }
                ZenModePrioritySendersPreferenceController zenModePrioritySendersPreferenceController = ZenModePrioritySendersPreferenceController.this;
                zenModePrioritySendersPreferenceController.updateState(zenModePrioritySendersPreferenceController.mPreferenceCategory);
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPrioritySenders() {
        if (this.mIsMessages) {
            return this.mBackend.getPriorityMessageSenders();
        }
        return this.mBackend.getPriorityCallSenders();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPriorityConversationSenders() {
        if (this.mIsMessages) {
            return this.mBackend.getPriorityConversationSenders();
        }
        return -10;
    }
}
