package androidx.window.embedding;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import androidx.window.core.ExperimentalWindowApi;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: MatcherUtils.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class MatcherUtils {
    @NotNull
    public static final MatcherUtils INSTANCE = new MatcherUtils();
    public static final boolean sDebugMatchers = false;
    @NotNull
    public static final String sMatchersTag = "SplitRuleResolution";

    private MatcherUtils() {
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x006e  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x008d A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean areComponentsMatching$window_release(@org.jetbrains.annotations.Nullable android.content.ComponentName r7, @org.jetbrains.annotations.NotNull android.content.ComponentName r8) {
        /*
            r6 = this;
            java.lang.String r0 = "ruleComponent"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "*"
            r1 = 1
            r2 = 0
            if (r7 != 0) goto L23
            java.lang.String r6 = r8.getPackageName()
            boolean r6 = kotlin.jvm.internal.Intrinsics.areEqual(r6, r0)
            if (r6 == 0) goto L21
            java.lang.String r6 = r8.getClassName()
            boolean r6 = kotlin.jvm.internal.Intrinsics.areEqual(r6, r0)
            if (r6 == 0) goto L21
            goto L22
        L21:
            r1 = r2
        L22:
            return r1
        L23:
            java.lang.String r3 = r7.toString()
            java.lang.String r4 = "activityComponent.toString()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
            r4 = 2
            r5 = 0
            boolean r0 = kotlin.text.StringsKt.contains$default(r3, r0, r2, r4, r5)
            r0 = r0 ^ r1
            if (r0 == 0) goto L92
            java.lang.String r0 = r7.getPackageName()
            java.lang.String r3 = r8.getPackageName()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r3)
            if (r0 != 0) goto L5f
            java.lang.String r0 = r7.getPackageName()
            java.lang.String r3 = "activityComponent.packageName"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r3)
            java.lang.String r3 = r8.getPackageName()
            java.lang.String r4 = "ruleComponent.packageName"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
            boolean r0 = r6.wildcardMatch(r0, r3)
            if (r0 == 0) goto L5d
            goto L5f
        L5d:
            r0 = r2
            goto L60
        L5f:
            r0 = r1
        L60:
            java.lang.String r3 = r7.getClassName()
            java.lang.String r4 = r8.getClassName()
            boolean r3 = kotlin.jvm.internal.Intrinsics.areEqual(r3, r4)
            if (r3 != 0) goto L8a
            java.lang.String r7 = r7.getClassName()
            java.lang.String r3 = "activityComponent.className"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r7, r3)
            java.lang.String r8 = r8.getClassName()
            java.lang.String r3 = "ruleComponent.className"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r8, r3)
            boolean r6 = r6.wildcardMatch(r7, r8)
            if (r6 == 0) goto L88
            goto L8a
        L88:
            r6 = r2
            goto L8b
        L8a:
            r6 = r1
        L8b:
            if (r0 == 0) goto L90
            if (r6 == 0) goto L90
            goto L91
        L90:
            r1 = r2
        L91:
            return r1
        L92:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = "Wildcard can only be part of the rule."
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.embedding.MatcherUtils.areComponentsMatching$window_release(android.content.ComponentName, android.content.ComponentName):boolean");
    }

    public final boolean areActivityOrIntentComponentsMatching$window_release(@NotNull Activity activity, @NotNull ComponentName ruleComponent) {
        ComponentName component;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(ruleComponent, "ruleComponent");
        if (areComponentsMatching$window_release(activity.getComponentName(), ruleComponent)) {
            return true;
        }
        Intent intent = activity.getIntent();
        if (intent == null || (component = intent.getComponent()) == null) {
            return false;
        }
        return INSTANCE.areComponentsMatching$window_release(component, ruleComponent);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0032  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0046  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final boolean wildcardMatch(java.lang.String r12, java.lang.String r13) {
        /*
            r11 = this;
            java.lang.String r11 = "*"
            r0 = 0
            r1 = 2
            r2 = 0
            boolean r3 = kotlin.text.StringsKt.contains$default(r13, r11, r0, r1, r2)
            if (r3 != 0) goto Lc
            return r0
        Lc:
            boolean r3 = kotlin.jvm.internal.Intrinsics.areEqual(r13, r11)
            r4 = 1
            if (r3 == 0) goto L14
            return r4
        L14:
            r7 = 0
            r8 = 0
            r9 = 6
            r10 = 0
            java.lang.String r6 = "*"
            r5 = r13
            int r3 = kotlin.text.StringsKt.indexOf$default(r5, r6, r7, r8, r9, r10)
            java.lang.String r6 = "*"
            int r5 = kotlin.text.StringsKt.lastIndexOf$default(r5, r6, r7, r8, r9, r10)
            if (r3 != r5) goto L2f
            boolean r11 = kotlin.text.StringsKt.endsWith$default(r13, r11, r0, r1, r2)
            if (r11 == 0) goto L2f
            r11 = r4
            goto L30
        L2f:
            r11 = r0
        L30:
            if (r11 == 0) goto L46
            int r11 = r13.length()
            int r11 = r11 - r4
            java.lang.String r11 = r13.substring(r0, r11)
            java.lang.String r13 = "this as java.lang.String…ing(startIndex, endIndex)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r11, r13)
            boolean r11 = kotlin.text.StringsKt.startsWith$default(r12, r11, r0, r1, r2)
            return r11
        L46:
            java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
            java.lang.String r12 = "Name pattern with a wildcard must only contain a single wildcard in the end"
            java.lang.String r12 = r12.toString()
            r11.<init>(r12)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.embedding.MatcherUtils.wildcardMatch(java.lang.String, java.lang.String):boolean");
    }
}
