package com.android.settingslib.applications;

import android.content.Context;
import android.os.Handler;
import android.permission.PermissionControllerManager;
import android.permission.RuntimePermissionPresentationInfo;
import com.android.settingslib.applications.PermissionsSummaryHelper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes2.dex */
public class PermissionsSummaryHelper {

    /* loaded from: classes2.dex */
    public static abstract class PermissionsResultCallback {
        public abstract void onPermissionSummaryResult(int i, int i2, int i3, List<CharSequence> list);
    }

    public static void getPermissionSummary(Context context, String str, final PermissionsResultCallback permissionsResultCallback) {
        ((PermissionControllerManager) context.getSystemService(PermissionControllerManager.class)).getAppPermissions(str, new PermissionControllerManager.OnGetAppPermissionResultCallback() { // from class: com.android.settingslib.applications.PermissionsSummaryHelper$$ExternalSyntheticLambda0
            public final void onGetAppPermissions(List list) {
                PermissionsSummaryHelper.lambda$getPermissionSummary$0(PermissionsSummaryHelper.PermissionsResultCallback.this, list);
            }
        }, (Handler) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getPermissionSummary$0(PermissionsResultCallback permissionsResultCallback, List list) {
        int size = list.size();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            RuntimePermissionPresentationInfo runtimePermissionPresentationInfo = (RuntimePermissionPresentationInfo) list.get(i4);
            i2++;
            if (runtimePermissionPresentationInfo.isGranted()) {
                if (runtimePermissionPresentationInfo.isStandard()) {
                    arrayList.add(runtimePermissionPresentationInfo.getLabel());
                    i++;
                } else {
                    i3++;
                }
            }
        }
        Collator collator = Collator.getInstance();
        collator.setStrength(0);
        Collections.sort(arrayList, collator);
        permissionsResultCallback.onPermissionSummaryResult(i, i2, i3, arrayList);
    }
}
