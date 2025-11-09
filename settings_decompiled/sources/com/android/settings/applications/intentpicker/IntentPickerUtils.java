package com.android.settings.applications.intentpicker;

import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class IntentPickerUtils {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;

    public static SpannableString getCentralizedDialogTitle(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, str.length(), 0);
        return spannableString;
    }

    public static DomainVerificationUserState getDomainVerificationUserState(DomainVerificationManager domainVerificationManager, String str) {
        try {
            return domainVerificationManager.getDomainVerificationUserState(str);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("IntentPickerUtils", e.getMessage());
            return null;
        }
    }

    public static List<String> getLinksList(DomainVerificationManager domainVerificationManager, String str, final int i) {
        DomainVerificationUserState domainVerificationUserState = getDomainVerificationUserState(domainVerificationManager, str);
        if (domainVerificationUserState == null) {
            return null;
        }
        return (List) domainVerificationUserState.getHostToStateMap().entrySet().stream().filter(new Predicate() { // from class: com.android.settings.applications.intentpicker.IntentPickerUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getLinksList$0;
                lambda$getLinksList$0 = IntentPickerUtils.lambda$getLinksList$0(i, (Map.Entry) obj);
                return lambda$getLinksList$0;
            }
        }).map(new Function() { // from class: com.android.settings.applications.intentpicker.IntentPickerUtils$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$getLinksList$1;
                lambda$getLinksList$1 = IntentPickerUtils.lambda$getLinksList$1((Map.Entry) obj);
                return lambda$getLinksList$1;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getLinksList$0(int i, Map.Entry entry) {
        return ((Integer) entry.getValue()).intValue() == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getLinksList$1(Map.Entry entry) {
        return (String) entry.getKey();
    }

    public static void logd(String str) {
        if (DEBUG) {
            Log.d("IntentPickerUtils", str);
        }
    }
}
