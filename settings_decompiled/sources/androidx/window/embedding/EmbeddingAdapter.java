package androidx.window.embedding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.WindowMetrics;
import androidx.window.core.ExperimentalWindowApi;
import androidx.window.core.PredicateAdapter;
import androidx.window.extensions.embedding.ActivityRule;
import androidx.window.extensions.embedding.SplitPairRule;
import androidx.window.extensions.embedding.SplitPlaceholderRule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt__IterablesKt;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.collections.SetsKt__SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import org.jetbrains.annotations.NotNull;
/* compiled from: EmbeddingAdapter.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class EmbeddingAdapter {
    @NotNull
    private final PredicateAdapter predicateAdapter;

    public EmbeddingAdapter(@NotNull PredicateAdapter predicateAdapter) {
        Intrinsics.checkNotNullParameter(predicateAdapter, "predicateAdapter");
        this.predicateAdapter = predicateAdapter;
    }

    @NotNull
    public final List<SplitInfo> translate(@NotNull List<? extends androidx.window.extensions.embedding.SplitInfo> splitInfoList) {
        int collectionSizeOrDefault;
        Intrinsics.checkNotNullParameter(splitInfoList, "splitInfoList");
        List<? extends androidx.window.extensions.embedding.SplitInfo> list = splitInfoList;
        collectionSizeOrDefault = CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10);
        ArrayList arrayList = new ArrayList(collectionSizeOrDefault);
        for (androidx.window.extensions.embedding.SplitInfo splitInfo : list) {
            arrayList.add(translate(splitInfo));
        }
        return arrayList;
    }

    private final SplitInfo translate(androidx.window.extensions.embedding.SplitInfo splitInfo) {
        boolean z;
        androidx.window.extensions.embedding.ActivityStack primaryActivityStack = splitInfo.getPrimaryActivityStack();
        Intrinsics.checkNotNullExpressionValue(primaryActivityStack, "splitInfo.primaryActivityStack");
        boolean z2 = false;
        try {
            z = primaryActivityStack.isEmpty();
        } catch (NoSuchMethodError unused) {
            z = false;
        }
        List activities = primaryActivityStack.getActivities();
        Intrinsics.checkNotNullExpressionValue(activities, "primaryActivityStack.activities");
        ActivityStack activityStack = new ActivityStack(activities, z);
        androidx.window.extensions.embedding.ActivityStack secondaryActivityStack = splitInfo.getSecondaryActivityStack();
        Intrinsics.checkNotNullExpressionValue(secondaryActivityStack, "splitInfo.secondaryActivityStack");
        try {
            z2 = secondaryActivityStack.isEmpty();
        } catch (NoSuchMethodError unused2) {
        }
        List activities2 = secondaryActivityStack.getActivities();
        Intrinsics.checkNotNullExpressionValue(activities2, "secondaryActivityStack.activities");
        return new SplitInfo(activityStack, new ActivityStack(activities2, z2), splitInfo.getSplitRatio());
    }

    @SuppressLint({"ClassVerificationFailure", "NewApi"})
    private final Object translateActivityPairPredicates(final Set<SplitPairFilter> set) {
        return this.predicateAdapter.buildPairPredicate(Reflection.getOrCreateKotlinClass(Activity.class), Reflection.getOrCreateKotlinClass(Activity.class), new Function2<Activity, Activity, Boolean>() { // from class: androidx.window.embedding.EmbeddingAdapter$translateActivityPairPredicates$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(2);
            }

            @Override // kotlin.jvm.functions.Function2
            @NotNull
            public final Boolean invoke(@NotNull Activity first, @NotNull Activity second) {
                Intrinsics.checkNotNullParameter(first, "first");
                Intrinsics.checkNotNullParameter(second, "second");
                Set<SplitPairFilter> set2 = set;
                boolean z = false;
                if (!(set2 instanceof Collection) || !set2.isEmpty()) {
                    Iterator<T> it = set2.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (((SplitPairFilter) it.next()).matchesActivityPair(first, second)) {
                            z = true;
                            break;
                        }
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    @SuppressLint({"ClassVerificationFailure", "NewApi"})
    private final Object translateActivityIntentPredicates(final Set<SplitPairFilter> set) {
        return this.predicateAdapter.buildPairPredicate(Reflection.getOrCreateKotlinClass(Activity.class), Reflection.getOrCreateKotlinClass(Intent.class), new Function2<Activity, Intent, Boolean>() { // from class: androidx.window.embedding.EmbeddingAdapter$translateActivityIntentPredicates$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(2);
            }

            @Override // kotlin.jvm.functions.Function2
            @NotNull
            public final Boolean invoke(@NotNull Activity first, @NotNull Intent second) {
                Intrinsics.checkNotNullParameter(first, "first");
                Intrinsics.checkNotNullParameter(second, "second");
                Set<SplitPairFilter> set2 = set;
                boolean z = false;
                if (!(set2 instanceof Collection) || !set2.isEmpty()) {
                    Iterator<T> it = set2.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (((SplitPairFilter) it.next()).matchesActivityIntentPair(first, second)) {
                            z = true;
                            break;
                        }
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    @SuppressLint({"ClassVerificationFailure", "NewApi"})
    private final Object translateParentMetricsPredicate(final SplitRule splitRule) {
        return this.predicateAdapter.buildPredicate(Reflection.getOrCreateKotlinClass(WindowMetrics.class), new Function1<WindowMetrics, Boolean>() { // from class: androidx.window.embedding.EmbeddingAdapter$translateParentMetricsPredicate$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            @NotNull
            public final Boolean invoke(@NotNull WindowMetrics windowMetrics) {
                Intrinsics.checkNotNullParameter(windowMetrics, "windowMetrics");
                return Boolean.valueOf(SplitRule.this.checkParentMetrics(windowMetrics));
            }
        });
    }

    @SuppressLint({"ClassVerificationFailure", "NewApi"})
    private final Object translateActivityPredicates(final Set<ActivityFilter> set) {
        return this.predicateAdapter.buildPredicate(Reflection.getOrCreateKotlinClass(Activity.class), new Function1<Activity, Boolean>() { // from class: androidx.window.embedding.EmbeddingAdapter$translateActivityPredicates$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            @NotNull
            public final Boolean invoke(@NotNull Activity activity) {
                Intrinsics.checkNotNullParameter(activity, "activity");
                Set<ActivityFilter> set2 = set;
                boolean z = false;
                if (!(set2 instanceof Collection) || !set2.isEmpty()) {
                    Iterator<T> it = set2.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (((ActivityFilter) it.next()).matchesActivity(activity)) {
                            z = true;
                            break;
                        }
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    @SuppressLint({"ClassVerificationFailure", "NewApi"})
    private final Object translateIntentPredicates(final Set<ActivityFilter> set) {
        return this.predicateAdapter.buildPredicate(Reflection.getOrCreateKotlinClass(Intent.class), new Function1<Intent, Boolean>() { // from class: androidx.window.embedding.EmbeddingAdapter$translateIntentPredicates$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            @NotNull
            public final Boolean invoke(@NotNull Intent intent) {
                Intrinsics.checkNotNullParameter(intent, "intent");
                Set<ActivityFilter> set2 = set;
                boolean z = false;
                if (!(set2 instanceof Collection) || !set2.isEmpty()) {
                    Iterator<T> it = set2.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (((ActivityFilter) it.next()).matchesIntent(intent)) {
                            z = true;
                            break;
                        }
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    @SuppressLint({"WrongConstant"})
    private final androidx.window.extensions.embedding.SplitPairRule translateSplitPairRule(SplitPairRule splitPairRule, Class<?> cls) {
        SplitPairRule.Builder finishSecondaryWithPrimary = ((SplitPairRule.Builder) SplitPairRule.Builder.class.getConstructor(cls, cls, cls).newInstance(translateActivityPairPredicates(splitPairRule.getFilters()), translateActivityIntentPredicates(splitPairRule.getFilters()), translateParentMetricsPredicate(splitPairRule))).setSplitRatio(splitPairRule.getSplitRatio()).setLayoutDirection(splitPairRule.getLayoutDirection()).setShouldClearTop(splitPairRule.getClearTop()).setFinishPrimaryWithSecondary(splitPairRule.getFinishPrimaryWithSecondary()).setFinishSecondaryWithPrimary(splitPairRule.getFinishSecondaryWithPrimary());
        Intrinsics.checkNotNullExpressionValue(finishSecondaryWithPrimary, "SplitPairRuleBuilder::cl…nishSecondaryWithPrimary)");
        androidx.window.extensions.embedding.SplitPairRule build = finishSecondaryWithPrimary.build();
        Intrinsics.checkNotNullExpressionValue(build, "builder.build()");
        return build;
    }

    @SuppressLint({"WrongConstant"})
    private final androidx.window.extensions.embedding.SplitPlaceholderRule translateSplitPlaceholderRule(SplitPlaceholderRule splitPlaceholderRule, Class<?> cls) {
        SplitPlaceholderRule.Builder finishPrimaryWithSecondary = ((SplitPlaceholderRule.Builder) SplitPlaceholderRule.Builder.class.getConstructor(Intent.class, cls, cls, cls).newInstance(splitPlaceholderRule.getPlaceholderIntent(), translateActivityPredicates(splitPlaceholderRule.getFilters()), translateIntentPredicates(splitPlaceholderRule.getFilters()), translateParentMetricsPredicate(splitPlaceholderRule))).setSplitRatio(splitPlaceholderRule.getSplitRatio()).setLayoutDirection(splitPlaceholderRule.getLayoutDirection()).setSticky(splitPlaceholderRule.isSticky()).setFinishPrimaryWithSecondary(splitPlaceholderRule.getFinishPrimaryWithSecondary());
        Intrinsics.checkNotNullExpressionValue(finishPrimaryWithSecondary, "SplitPlaceholderRuleBuil…nishPrimaryWithSecondary)");
        androidx.window.extensions.embedding.SplitPlaceholderRule build = finishPrimaryWithSecondary.build();
        Intrinsics.checkNotNullExpressionValue(build, "builder.build()");
        return build;
    }

    private final androidx.window.extensions.embedding.ActivityRule translateActivityRule(ActivityRule activityRule, Class<?> cls) {
        androidx.window.extensions.embedding.ActivityRule build = ((ActivityRule.Builder) ActivityRule.Builder.class.getConstructor(cls, cls).newInstance(translateActivityPredicates(activityRule.getFilters()), translateIntentPredicates(activityRule.getFilters()))).setShouldAlwaysExpand(activityRule.getAlwaysExpand()).build();
        Intrinsics.checkNotNullExpressionValue(build, "ActivityRuleBuilder::cla…and)\n            .build()");
        return build;
    }

    @NotNull
    public final Set<androidx.window.extensions.embedding.EmbeddingRule> translate(@NotNull Set<? extends EmbeddingRule> rules) {
        int collectionSizeOrDefault;
        Set<androidx.window.extensions.embedding.EmbeddingRule> set;
        androidx.window.extensions.embedding.SplitPairRule translateActivityRule;
        Set<androidx.window.extensions.embedding.EmbeddingRule> emptySet;
        Intrinsics.checkNotNullParameter(rules, "rules");
        Class<?> predicateClassOrNull$window_release = this.predicateAdapter.predicateClassOrNull$window_release();
        if (predicateClassOrNull$window_release == null) {
            emptySet = SetsKt__SetsKt.emptySet();
            return emptySet;
        }
        Set<? extends EmbeddingRule> set2 = rules;
        collectionSizeOrDefault = CollectionsKt__IterablesKt.collectionSizeOrDefault(set2, 10);
        ArrayList arrayList = new ArrayList(collectionSizeOrDefault);
        for (EmbeddingRule embeddingRule : set2) {
            if (embeddingRule instanceof SplitPairRule) {
                translateActivityRule = translateSplitPairRule((SplitPairRule) embeddingRule, predicateClassOrNull$window_release);
            } else if (embeddingRule instanceof SplitPlaceholderRule) {
                translateActivityRule = translateSplitPlaceholderRule((SplitPlaceholderRule) embeddingRule, predicateClassOrNull$window_release);
            } else if (!(embeddingRule instanceof ActivityRule)) {
                throw new IllegalArgumentException("Unsupported rule type");
            } else {
                translateActivityRule = translateActivityRule((ActivityRule) embeddingRule, predicateClassOrNull$window_release);
            }
            arrayList.add((androidx.window.extensions.embedding.EmbeddingRule) translateActivityRule);
        }
        set = CollectionsKt___CollectionsKt.toSet(arrayList);
        return set;
    }
}
