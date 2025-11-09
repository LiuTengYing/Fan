package com.android.settings.security.trustagent;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class TrustAgentManager {
    static final String PERMISSION_PROVIDE_AGENT = "android.permission.PROVIDE_TRUST_AGENT";
    private static final Intent TRUST_AGENT_INTENT = new Intent("android.service.trust.TrustAgentService");

    /* loaded from: classes.dex */
    public static class TrustAgentComponentInfo {
        public RestrictedLockUtils.EnforcedAdmin admin = null;
        public ComponentName componentName;
        public String summary;
        public String title;
    }

    public boolean shouldProvideTrust(ResolveInfo resolveInfo, PackageManager packageManager) {
        String str = resolveInfo.serviceInfo.packageName;
        if (packageManager.checkPermission(PERMISSION_PROVIDE_AGENT, str) != 0) {
            Log.w("TrustAgentManager", "Skipping agent because package " + str + " does not have permission " + PERMISSION_PROVIDE_AGENT + ".");
            return false;
        }
        return true;
    }

    public CharSequence getActiveTrustAgentLabel(Context context, LockPatternUtils lockPatternUtils) {
        List<TrustAgentComponentInfo> activeTrustAgents = getActiveTrustAgents(context, lockPatternUtils);
        if (activeTrustAgents.isEmpty()) {
            return null;
        }
        return activeTrustAgents.get(0).title;
    }

    public List<TrustAgentComponentInfo> getActiveTrustAgents(Context context, LockPatternUtils lockPatternUtils) {
        int myUserId = UserHandle.myUserId();
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        PackageManager packageManager = context.getPackageManager();
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(TRUST_AGENT_INTENT, 128);
        List enabledTrustAgents = lockPatternUtils.getEnabledTrustAgents(myUserId);
        RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(context, 16, myUserId);
        if (enabledTrustAgents != null && !enabledTrustAgents.isEmpty()) {
            for (ResolveInfo resolveInfo : queryIntentServices) {
                if (resolveInfo.serviceInfo != null && shouldProvideTrust(resolveInfo, packageManager)) {
                    TrustAgentComponentInfo settingsComponent = getSettingsComponent(packageManager, resolveInfo);
                    if (settingsComponent.componentName != null && enabledTrustAgents.contains(getComponentName(resolveInfo)) && !TextUtils.isEmpty(settingsComponent.title)) {
                        if (checkIfKeyguardFeaturesDisabled != null && devicePolicyManager.getTrustAgentConfiguration(null, getComponentName(resolveInfo)) == null) {
                            settingsComponent.admin = checkIfKeyguardFeaturesDisabled;
                        }
                        arrayList.add(settingsComponent);
                    }
                }
            }
        }
        return arrayList;
    }

    public ComponentName getComponentName(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            return null;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    /* JADX WARN: Code restructure failed: missing block: B:49:0x0091, code lost:
        if (r2 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0093, code lost:
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x009a, code lost:
        if (r2 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00a0, code lost:
        if (r2 == null) goto L32;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.android.settings.security.trustagent.TrustAgentManager.TrustAgentComponentInfo getSettingsComponent(android.content.pm.PackageManager r9, android.content.pm.ResolveInfo r10) {
        /*
            Method dump skipped, instructions count: 235
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.security.trustagent.TrustAgentManager.getSettingsComponent(android.content.pm.PackageManager, android.content.pm.ResolveInfo):com.android.settings.security.trustagent.TrustAgentManager$TrustAgentComponentInfo");
    }
}
