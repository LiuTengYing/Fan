package com.android.settingslib.dream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.network.telephony.EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda0;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes2.dex */
public class DreamBackend {
    private static DreamBackend sInstance;
    private final DreamInfoComparator mComparator;
    private final Context mContext;
    private final Set<Integer> mDefaultEnabledComplications;
    private final Set<ComponentName> mDisabledDreams;
    private final IDreamManager mDreamManager;
    private final boolean mDreamsActivatedOnDockByDefault;
    private final boolean mDreamsActivatedOnSleepByDefault;
    private final boolean mDreamsEnabledByDefault;
    private final Set<Integer> mSupportedComplications;

    private static void logd(String str, Object... objArr) {
    }

    /* loaded from: classes2.dex */
    public static class DreamInfo {
        public CharSequence caption;
        public ComponentName componentName;
        public CharSequence description;
        public Drawable icon;
        public boolean isActive;
        public Drawable previewImage;
        public ComponentName settingsComponentName;

        public String toString() {
            StringBuilder sb = new StringBuilder(DreamInfo.class.getSimpleName());
            sb.append('[');
            sb.append(this.caption);
            if (this.isActive) {
                sb.append(",active");
            }
            sb.append(',');
            sb.append(this.componentName);
            if (this.settingsComponentName != null) {
                sb.append("settings=");
                sb.append(this.settingsComponentName);
            }
            sb.append(']');
            return sb.toString();
        }
    }

    public static DreamBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DreamBackend(context);
        }
        return sInstance;
    }

    public DreamBackend(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        Resources resources = applicationContext.getResources();
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        this.mComparator = new DreamInfoComparator(getDefaultDream());
        this.mDreamsEnabledByDefault = resources.getBoolean(17891617);
        this.mDreamsActivatedOnSleepByDefault = resources.getBoolean(17891616);
        this.mDreamsActivatedOnDockByDefault = resources.getBoolean(17891615);
        this.mDisabledDreams = (Set) Arrays.stream(resources.getStringArray(17236032)).map(new Function() { // from class: com.android.settingslib.dream.DreamBackend$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ComponentName.unflattenFromString((String) obj);
            }
        }).collect(Collectors.toSet());
        final Set<Integer> set = (Set) Arrays.stream(resources.getIntArray(17236132)).boxed().collect(Collectors.toSet());
        this.mSupportedComplications = set;
        Stream<Integer> boxed = Arrays.stream(resources.getIntArray(17236056)).boxed();
        Objects.requireNonNull(set);
        this.mDefaultEnabledComplications = (Set) boxed.filter(new Predicate() { // from class: com.android.settingslib.dream.DreamBackend$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return set.contains((Integer) obj);
            }
        }).collect(Collectors.toSet());
    }

    public List<DreamInfo> getDreamInfos() {
        logd("getDreamInfos()", new Object[0]);
        ComponentName activeDream = getActiveDream();
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.service.dreams.DreamService"), 128);
        ArrayList arrayList = new ArrayList(queryIntentServices.size());
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ComponentName dreamComponentName = getDreamComponentName(resolveInfo);
            if (dreamComponentName != null && !this.mDisabledDreams.contains(dreamComponentName)) {
                DreamInfo dreamInfo = new DreamInfo();
                dreamInfo.caption = resolveInfo.loadLabel(packageManager);
                dreamInfo.icon = resolveInfo.loadIcon(packageManager);
                dreamInfo.description = getDescription(resolveInfo, packageManager);
                dreamInfo.componentName = dreamComponentName;
                dreamInfo.isActive = dreamComponentName.equals(activeDream);
                DreamService.DreamMetadata dreamMetadata = DreamService.getDreamMetadata(this.mContext, resolveInfo.serviceInfo);
                if (dreamMetadata != null) {
                    dreamInfo.settingsComponentName = dreamMetadata.settingsActivity;
                    dreamInfo.previewImage = dreamMetadata.previewImage;
                }
                arrayList.add(dreamInfo);
            }
        }
        arrayList.sort(this.mComparator);
        return arrayList;
    }

    private static CharSequence getDescription(ResolveInfo resolveInfo, PackageManager packageManager) {
        ApplicationInfo applicationInfo;
        String str = resolveInfo.resolvePackageName;
        if (str == null) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            String str2 = serviceInfo.packageName;
            applicationInfo = serviceInfo.applicationInfo;
            str = str2;
        } else {
            applicationInfo = null;
        }
        int i = resolveInfo.serviceInfo.descriptionRes;
        if (i != 0) {
            return packageManager.getText(str, i, applicationInfo);
        }
        return null;
    }

    public ComponentName getDefaultDream() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return null;
        }
        try {
            return iDreamManager.getDefaultDreamComponentForUser(this.mContext.getUserId());
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to get default dream", e);
            return null;
        }
    }

    public CharSequence getActiveDreamName() {
        ComponentName activeDream = getActiveDream();
        if (activeDream != null) {
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                ServiceInfo serviceInfo = packageManager.getServiceInfo(activeDream, 0);
                if (serviceInfo != null) {
                    return serviceInfo.loadLabel(packageManager);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return null;
    }

    public int getWhenToDreamSetting() {
        if (isActivatedOnDock() && isActivatedOnSleep()) {
            return 2;
        }
        if (isActivatedOnDock()) {
            return 1;
        }
        return isActivatedOnSleep() ? 0 : 3;
    }

    public void setWhenToDream(int i) {
        setEnabled(i != 3);
        if (i == 0) {
            setActivatedOnDock(false);
            setActivatedOnSleep(true);
        } else if (i == 1) {
            setActivatedOnDock(true);
            setActivatedOnSleep(false);
        } else if (i != 2) {
        } else {
            setActivatedOnDock(true);
            setActivatedOnSleep(true);
        }
    }

    public Set<Integer> getEnabledComplications() {
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "screensaver_enabled_complications");
        if (string == null) {
            return this.mDefaultEnabledComplications;
        }
        return parseFromString(string);
    }

    public Set<Integer> getSupportedComplications() {
        return this.mSupportedComplications;
    }

    public void setComplicationEnabled(int i, boolean z) {
        if (this.mSupportedComplications.contains(Integer.valueOf(i))) {
            Set<Integer> enabledComplications = getEnabledComplications();
            if (z) {
                enabledComplications.add(Integer.valueOf(i));
            } else {
                enabledComplications.remove(Integer.valueOf(i));
            }
            Settings.Secure.putString(this.mContext.getContentResolver(), "screensaver_enabled_complications", convertToString(enabledComplications));
        }
    }

    private static String convertToString(Set<Integer> set) {
        return (String) set.stream().map(new EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda0()).collect(Collectors.joining(","));
    }

    private static Set<Integer> parseFromString(String str) {
        if (TextUtils.isEmpty(str)) {
            return new HashSet();
        }
        return (Set) Arrays.stream(str.split(",")).map(new Function() { // from class: com.android.settingslib.dream.DreamBackend$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(Integer.parseInt((String) obj));
            }
        }).collect(Collectors.toSet());
    }

    public boolean isEnabled() {
        return getBoolean("screensaver_enabled", this.mDreamsEnabledByDefault);
    }

    public void setEnabled(boolean z) {
        logd("setEnabled(%s)", Boolean.valueOf(z));
        setBoolean("screensaver_enabled", z);
    }

    public boolean isActivatedOnDock() {
        return getBoolean("screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefault);
    }

    public void setActivatedOnDock(boolean z) {
        logd("setActivatedOnDock(%s)", Boolean.valueOf(z));
        setBoolean("screensaver_activate_on_dock", z);
    }

    public boolean isActivatedOnSleep() {
        return getBoolean("screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefault);
    }

    public void setActivatedOnSleep(boolean z) {
        logd("setActivatedOnSleep(%s)", Boolean.valueOf(z));
        setBoolean("screensaver_activate_on_sleep", z);
    }

    private boolean getBoolean(String str, boolean z) {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), str, z ? 1 : 0) == 1;
    }

    private void setBoolean(String str, boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), str, z ? 1 : 0);
    }

    public void setActiveDream(ComponentName componentName) {
        logd("setActiveDream(%s)", componentName);
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return;
        }
        try {
            ComponentName[] componentNameArr = {componentName};
            if (componentName == null) {
                componentNameArr = null;
            }
            iDreamManager.setDreamComponents(componentNameArr);
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to set active dream to " + componentName, e);
        }
    }

    public ComponentName getActiveDream() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return null;
        }
        try {
            ComponentName[] dreamComponents = iDreamManager.getDreamComponents();
            if (dreamComponents == null || dreamComponents.length <= 0) {
                return null;
            }
            return dreamComponents[0];
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to get active dream", e);
            return null;
        }
    }

    public void launchSettings(Context context, DreamInfo dreamInfo) {
        logd("launchSettings(%s)", dreamInfo);
        if (dreamInfo == null || dreamInfo.settingsComponentName == null) {
            return;
        }
        context.startActivity(new Intent().setComponent(dreamInfo.settingsComponentName).addFlags(276824064));
    }

    public void preview(ComponentName componentName) {
        logd("preview(%s)", componentName);
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null || componentName == null) {
            return;
        }
        try {
            iDreamManager.testDream(this.mContext.getUserId(), componentName);
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to preview " + componentName, e);
        }
    }

    private static ComponentName getDreamComponentName(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            return null;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    /* loaded from: classes2.dex */
    private static class DreamInfoComparator implements Comparator<DreamInfo> {
        private final ComponentName mDefaultDream;

        public DreamInfoComparator(ComponentName componentName) {
            this.mDefaultDream = componentName;
        }

        @Override // java.util.Comparator
        public int compare(DreamInfo dreamInfo, DreamInfo dreamInfo2) {
            return sortKey(dreamInfo).compareTo(sortKey(dreamInfo2));
        }

        private String sortKey(DreamInfo dreamInfo) {
            StringBuilder sb = new StringBuilder();
            sb.append(dreamInfo.componentName.equals(this.mDefaultDream) ? '0' : '1');
            sb.append(dreamInfo.caption);
            return sb.toString();
        }
    }
}
