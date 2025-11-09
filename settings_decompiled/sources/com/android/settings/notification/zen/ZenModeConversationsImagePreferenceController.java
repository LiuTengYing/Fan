package com.android.settings.notification.zen;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.service.notification.ConversationChannelWrapper;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenModeConversationsImagePreferenceController extends AbstractZenModePreferenceController {
    private final ArrayList<Drawable> mConversationDrawables;
    private final int mIconOffsetPx;
    private final int mIconSizePx;
    private final NotificationBackend mNotificationBackend;
    private LayoutPreference mPreference;
    private ViewGroup mViewGroup;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeConversationsImagePreferenceController(Context context, String str, Lifecycle lifecycle, NotificationBackend notificationBackend) {
        super(context, str, lifecycle);
        this.mConversationDrawables = new ArrayList<>();
        this.mNotificationBackend = notificationBackend;
        this.mIconSizePx = this.mContext.getResources().getDimensionPixelSize(R$dimen.zen_conversations_icon_size);
        this.mIconOffsetPx = this.mContext.getResources().getDimensionPixelSize(R$dimen.zen_conversations_icon_offset);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(this.KEY);
        this.mPreference = layoutPreference;
        this.mViewGroup = (ViewGroup) layoutPreference.findViewById(R$id.zen_mode_settings_senders_overlay_view);
        loadConversations();
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        loadConversations();
        this.mViewGroup.removeAllViews();
        int priorityConversationSenders = this.mBackend.getPriorityConversationSenders();
        if (priorityConversationSenders == 1) {
            this.mViewGroup.setContentDescription(this.mContext.getResources().getString(R$string.zen_mode_from_all_conversations));
        } else if (priorityConversationSenders == 2) {
            this.mViewGroup.setContentDescription(this.mContext.getResources().getString(R$string.zen_mode_from_important_conversations));
        } else {
            this.mViewGroup.setContentDescription(null);
            this.mViewGroup.setVisibility(8);
            return;
        }
        int min = Math.min(5, this.mConversationDrawables.size());
        for (int i = 0; i < min; i++) {
            ImageView imageView = new ImageView(this.mContext);
            imageView.setImageDrawable(this.mConversationDrawables.get(i));
            int i2 = this.mIconSizePx;
            imageView.setLayoutParams(new ViewGroup.LayoutParams(i2, i2));
            FrameLayout frameLayout = new FrameLayout(this.mContext);
            frameLayout.addView(imageView);
            frameLayout.setPadding(((min - i) - 1) * this.mIconOffsetPx, 0, 0, 0);
            this.mViewGroup.addView(frameLayout);
        }
        this.mViewGroup.setVisibility(min > 0 ? 0 : 8);
    }

    private void loadConversations() {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.zen.ZenModeConversationsImagePreferenceController.1
            private List<Drawable> mDrawables = new ArrayList();

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                Drawable conversationDrawable;
                this.mDrawables.clear();
                int priorityConversationSenders = ZenModeConversationsImagePreferenceController.this.mBackend.getPriorityConversationSenders();
                if (priorityConversationSenders == 3) {
                    return null;
                }
                ParceledListSlice<ConversationChannelWrapper> conversations = ZenModeConversationsImagePreferenceController.this.mNotificationBackend.getConversations(priorityConversationSenders == 2);
                if (conversations != null) {
                    for (ConversationChannelWrapper conversationChannelWrapper : conversations.getList()) {
                        if (!conversationChannelWrapper.getNotificationChannel().isDemoted() && (conversationDrawable = ZenModeConversationsImagePreferenceController.this.mNotificationBackend.getConversationDrawable(((AbstractPreferenceController) ZenModeConversationsImagePreferenceController.this).mContext, conversationChannelWrapper.getShortcutInfo(), conversationChannelWrapper.getPkg(), conversationChannelWrapper.getUid(), conversationChannelWrapper.getNotificationChannel().isImportantConversation())) != null) {
                            this.mDrawables.add(conversationDrawable);
                        }
                    }
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r2) {
                if (((AbstractPreferenceController) ZenModeConversationsImagePreferenceController.this).mContext == null) {
                    return;
                }
                ZenModeConversationsImagePreferenceController.this.mConversationDrawables.clear();
                ZenModeConversationsImagePreferenceController.this.mConversationDrawables.addAll(this.mDrawables);
                ZenModeConversationsImagePreferenceController zenModeConversationsImagePreferenceController = ZenModeConversationsImagePreferenceController.this;
                zenModeConversationsImagePreferenceController.updateState(zenModeConversationsImagePreferenceController.mPreference);
            }
        }.execute(new Void[0]);
    }
}
