package com.android.settings.notification.app;

import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.Bundle;
import android.os.ServiceManager;
import android.service.notification.ConversationChannelWrapper;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ConversationListSettings extends DashboardFragment {
    private AllConversationsPreferenceController mAllConversationsController;
    private NoConversationsPreferenceController mNoConversationsController;
    private PriorityConversationsPreferenceController mPriorityConversationsController;
    private RecentConversationsPreferenceController mRecentConversationsController;
    NotificationBackend mBackend = new NotificationBackend();
    protected List<AbstractPreferenceController> mControllers = new ArrayList();
    private boolean mUpdatedInOnCreate = false;
    IPeopleManager mPs = IPeopleManager.Stub.asInterface(ServiceManager.getService("people"));

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConvoListSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1834;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.conversation_list_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mControllers = new ArrayList();
        NoConversationsPreferenceController noConversationsPreferenceController = new NoConversationsPreferenceController(context);
        this.mNoConversationsController = noConversationsPreferenceController;
        this.mControllers.add(noConversationsPreferenceController);
        PriorityConversationsPreferenceController priorityConversationsPreferenceController = new PriorityConversationsPreferenceController(context, this.mBackend);
        this.mPriorityConversationsController = priorityConversationsPreferenceController;
        this.mControllers.add(priorityConversationsPreferenceController);
        AllConversationsPreferenceController allConversationsPreferenceController = new AllConversationsPreferenceController(context, this.mBackend);
        this.mAllConversationsController = allConversationsPreferenceController;
        this.mControllers.add(allConversationsPreferenceController);
        RecentConversationsPreferenceController recentConversationsPreferenceController = new RecentConversationsPreferenceController(context, this.mBackend, this.mPs);
        this.mRecentConversationsController = recentConversationsPreferenceController;
        this.mControllers.add(recentConversationsPreferenceController);
        return new ArrayList(this.mControllers);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        update();
        this.mUpdatedInOnCreate = true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mUpdatedInOnCreate) {
            this.mUpdatedInOnCreate = false;
        } else {
            update();
        }
    }

    private void update() {
        List<ConversationChannelWrapper> list = this.mBackend.getConversations(false).getList();
        this.mNoConversationsController.setAvailable(!(this.mAllConversationsController.updateList(list) | this.mPriorityConversationsController.updateList(list) | this.mRecentConversationsController.updateList()));
        this.mNoConversationsController.displayPreference(getPreferenceScreen());
    }
}
