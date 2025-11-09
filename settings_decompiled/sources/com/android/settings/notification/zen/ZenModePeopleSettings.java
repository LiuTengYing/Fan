package com.android.settings.notification.zen;

import android.app.Application;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenModePeopleSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.zen_mode_people_settings) { // from class: com.android.settings.notification.zen.ZenModePeopleSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModePeopleSettings.buildPreferenceControllers(context, null, null, null, null, null);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1823;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        return buildPreferenceControllers(context, getSettingsLifecycle(), activity != null ? activity.getApplication() : null, this, getFragmentManager(), new NotificationBackend());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, Application application, Fragment fragment, FragmentManager fragmentManager, NotificationBackend notificationBackend) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeCallsPreferenceController(context, lifecycle, "zen_mode_people_calls"));
        arrayList.add(new ZenModeMessagesPreferenceController(context, lifecycle, "zen_mode_people_messages"));
        arrayList.add(new ZenModeBehaviorFooterPreferenceController(context, lifecycle, R$string.zen_mode_people_footer));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_people_settings;
    }
}
