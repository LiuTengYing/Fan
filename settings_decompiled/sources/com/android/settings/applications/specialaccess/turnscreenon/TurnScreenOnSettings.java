package com.android.settings.applications.specialaccess.turnscreenon;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.widget.AppPreference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class TurnScreenOnSettings extends EmptyTextSettings {
    static final List<String> IGNORE_PACKAGE_LIST;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private IconDrawableFactory mIconDrawableFactory;
    private PackageManager mPackageManager;
    private UserManager mUserManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1922;
    }

    static {
        ArrayList arrayList = new ArrayList();
        IGNORE_PACKAGE_LIST = arrayList;
        arrayList.add("com.android.systemui");
        SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.turn_screen_on_settings);
    }

    /* loaded from: classes.dex */
    static class AppComparator implements Comparator<Pair<ApplicationInfo, Integer>> {
        private final Collator mCollator = Collator.getInstance();
        private final PackageManager mPm;

        AppComparator(PackageManager packageManager) {
            this.mPm = packageManager;
        }

        @Override // java.util.Comparator
        public final int compare(Pair<ApplicationInfo, Integer> pair, Pair<ApplicationInfo, Integer> pair2) {
            CharSequence loadLabel = ((ApplicationInfo) pair.first).loadLabel(this.mPm);
            if (loadLabel == null) {
                loadLabel = ((ApplicationInfo) pair.first).name;
            }
            CharSequence loadLabel2 = ((ApplicationInfo) pair2.first).loadLabel(this.mPm);
            if (loadLabel2 == null) {
                loadLabel2 = ((ApplicationInfo) pair2.first).name;
            }
            int compare = this.mCollator.compare(loadLabel.toString(), loadLabel2.toString());
            return compare != 0 ? compare : ((Integer) pair.second).intValue() - ((Integer) pair2.second).intValue();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPackageManager = activity.getPackageManager();
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        ArrayList<Pair<ApplicationInfo, Integer>> collectTurnScreenOnApps = collectTurnScreenOnApps(UserHandle.myUserId());
        collectTurnScreenOnApps.sort(new AppComparator(this.mPackageManager));
        Context prefContext = getPrefContext();
        Iterator<Pair<ApplicationInfo, Integer>> it = collectTurnScreenOnApps.iterator();
        while (it.hasNext()) {
            Pair<ApplicationInfo, Integer> next = it.next();
            final ApplicationInfo applicationInfo = (ApplicationInfo) next.first;
            int intValue = ((Integer) next.second).intValue();
            UserHandle of = UserHandle.of(intValue);
            final String str = applicationInfo.packageName;
            CharSequence loadLabel = applicationInfo.loadLabel(this.mPackageManager);
            AppPreference appPreference = new AppPreference(prefContext);
            appPreference.setIcon(this.mIconDrawableFactory.getBadgedIcon(applicationInfo, intValue));
            appPreference.setTitle(this.mPackageManager.getUserBadgedLabel(loadLabel, of));
            appPreference.setSummary(TurnScreenOnDetails.getPreferenceSummary(this.mAppOpsManager, applicationInfo.uid, str));
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.specialaccess.turnscreenon.TurnScreenOnSettings.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    String string = TurnScreenOnSettings.this.getString(R$string.turn_screen_on_title);
                    String str2 = str;
                    int i = applicationInfo.uid;
                    TurnScreenOnSettings turnScreenOnSettings = TurnScreenOnSettings.this;
                    AppInfoBase.startAppInfoFragment(TurnScreenOnDetails.class, string, str2, i, turnScreenOnSettings, -1, turnScreenOnSettings.getMetricsCategory());
                    return true;
                }
            });
            preferenceScreen.addPreference(appPreference);
        }
    }

    @Override // com.android.settings.widget.EmptyTextSettings, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(R$string.no_applications);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.turn_screen_on_settings;
    }

    ArrayList<Pair<ApplicationInfo, Integer>> collectTurnScreenOnApps(int i) {
        ArrayList<Pair<ApplicationInfo, Integer>> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            arrayList2.add(Integer.valueOf(userInfo.id));
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            for (PackageInfo packageInfo : this.mPackageManager.getInstalledPackagesAsUser(0, intValue)) {
                if (hasTurnScreenOnPermission(this.mPackageManager, packageInfo.packageName)) {
                    arrayList.add(new Pair<>(packageInfo.applicationInfo, Integer.valueOf(intValue)));
                }
            }
        }
        return arrayList;
    }

    static boolean hasTurnScreenOnPermission(PackageManager packageManager, String str) {
        return !IGNORE_PACKAGE_LIST.contains(str) && packageManager.checkPermission("android.permission.WAKE_LOCK", str) == 0;
    }
}
