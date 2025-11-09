package com.android.settings.security.screenlock;

import android.content.Context;
import android.os.UserHandle;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.OwnerInfoPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ScreenLockSettings extends DashboardFragment implements OwnerInfoPreferenceController.OwnerInfoCallback {
    private static final int MY_USER_ID = UserHandle.myUserId();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.screen_lock_settings) { // from class: com.android.settings.security.screenlock.ScreenLockSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ScreenLockSettings.buildPreferenceControllers(context, null, new LockPatternUtils(context));
        }
    };
    private LockPatternUtils mLockPatternUtils;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ScreenLockSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1265;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.screen_lock_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        LockPatternUtils lockPatternUtils = new LockPatternUtils(context);
        this.mLockPatternUtils = lockPatternUtils;
        return buildPreferenceControllers(context, this, lockPatternUtils);
    }

    @Override // com.android.settings.security.OwnerInfoPreferenceController.OwnerInfoCallback
    public void onOwnerInfoUpdated() {
        ((OwnerInfoPreferenceController) use(OwnerInfoPreferenceController.class)).updateSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, DashboardFragment dashboardFragment, LockPatternUtils lockPatternUtils) {
        ArrayList arrayList = new ArrayList();
        int i = MY_USER_ID;
        arrayList.add(new PatternVisiblePreferenceController(context, i, lockPatternUtils));
        arrayList.add(new PowerButtonInstantLockPreferenceController(context, i, lockPatternUtils));
        arrayList.add(new LockAfterTimeoutPreferenceController(context, i, lockPatternUtils));
        arrayList.add(new OwnerInfoPreferenceController(context, dashboardFragment));
        return arrayList;
    }
}
