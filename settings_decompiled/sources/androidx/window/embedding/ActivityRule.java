package androidx.window.embedding;

import androidx.window.core.ExperimentalWindowApi;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ActivityRule.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class ActivityRule extends EmbeddingRule {
    private final boolean alwaysExpand;
    @NotNull
    private final Set<ActivityFilter> filters;

    public /* synthetic */ ActivityRule(Set set, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(set, (i & 2) != 0 ? false : z);
    }

    public final boolean getAlwaysExpand() {
        return this.alwaysExpand;
    }

    public ActivityRule(@NotNull Set<ActivityFilter> filters, boolean z) {
        Set<ActivityFilter> set;
        Intrinsics.checkNotNullParameter(filters, "filters");
        this.alwaysExpand = z;
        set = CollectionsKt___CollectionsKt.toSet(filters);
        this.filters = set;
    }

    @NotNull
    public final Set<ActivityFilter> getFilters() {
        return this.filters;
    }

    @NotNull
    public final ActivityRule plus$window_release(@NotNull ActivityFilter filter) {
        Set set;
        Intrinsics.checkNotNullParameter(filter, "filter");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.addAll(this.filters);
        linkedHashSet.add(filter);
        set = CollectionsKt___CollectionsKt.toSet(linkedHashSet);
        return new ActivityRule(set, this.alwaysExpand);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ActivityRule) {
            ActivityRule activityRule = (ActivityRule) obj;
            return Intrinsics.areEqual(this.filters, activityRule.filters) && this.alwaysExpand == activityRule.alwaysExpand;
        }
        return false;
    }

    public int hashCode() {
        return (this.filters.hashCode() * 31) + Boolean.hashCode(this.alwaysExpand);
    }
}
