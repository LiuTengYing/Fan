package com.android.settingslib.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.accessibility.AccessibilityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes2.dex */
public class AccessibilityUtils {
    public static Set<ComponentName> getEnabledServicesFromSettings(Context context) {
        return getEnabledServicesFromSettings(context, UserHandle.myUserId());
    }

    public static Set<ComponentName> getEnabledServicesFromSettings(Context context, int i) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "enabled_accessibility_services", i);
        if (TextUtils.isEmpty(stringForUser)) {
            return Collections.emptySet();
        }
        HashSet hashSet = new HashSet();
        TextUtils.SimpleStringSplitter<String> simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(stringForUser);
        for (String str : simpleStringSplitter) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null) {
                hashSet.add(unflattenFromString);
            }
        }
        return hashSet;
    }

    public static CharSequence getTextForLocale(Context context, Locale locale, int i) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration).getText(i);
    }

    public static void setAccessibilityServiceState(Context context, ComponentName componentName, boolean z) {
        setAccessibilityServiceState(context, componentName, z, UserHandle.myUserId());
    }

    public static void setAccessibilityServiceState(Context context, ComponentName componentName, boolean z, int i) {
        Set<ComponentName> enabledServicesFromSettings = getEnabledServicesFromSettings(context, i);
        if (enabledServicesFromSettings.isEmpty()) {
            enabledServicesFromSettings = new ArraySet(1);
        }
        if (z) {
            enabledServicesFromSettings.add(componentName);
        } else {
            enabledServicesFromSettings.remove(componentName);
            Set<ComponentName> installedServices = getInstalledServices(context);
            Iterator it = enabledServicesFromSettings.iterator();
            while (it.hasNext() && !installedServices.contains((ComponentName) it.next())) {
            }
        }
        StringBuilder sb = new StringBuilder();
        for (ComponentName componentName2 : enabledServicesFromSettings) {
            sb.append(componentName2.flattenToString());
            sb.append(':');
        }
        int length = sb.length();
        if (length > 0) {
            sb.deleteCharAt(length - 1);
        }
        Settings.Secure.putStringForUser(context.getContentResolver(), "enabled_accessibility_services", sb.toString(), i);
    }

    public static String getShortcutTargetServiceComponentNameString(Context context, int i) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "accessibility_shortcut_target_service", i);
        return stringForUser != null ? stringForUser : context.getString(17039915);
    }

    private static Set<ComponentName> getInstalledServices(Context context) {
        HashSet hashSet = new HashSet();
        hashSet.clear();
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = AccessibilityManager.getInstance(context).getInstalledAccessibilityServiceList();
        if (installedAccessibilityServiceList == null) {
            return hashSet;
        }
        for (AccessibilityServiceInfo accessibilityServiceInfo : installedAccessibilityServiceList) {
            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
            hashSet.add(new ComponentName(serviceInfo.packageName, serviceInfo.name));
        }
        return hashSet;
    }
}
