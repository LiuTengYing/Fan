package androidx.window.embedding;

import androidx.window.core.ExperimentalWindowApi;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SplitPairRule.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class SplitPairRule extends SplitRule {
    private final boolean clearTop;
    @NotNull
    private final Set<SplitPairFilter> filters;
    private final int finishPrimaryWithSecondary;
    private final int finishSecondaryWithPrimary;

    public /* synthetic */ SplitPairRule(Set set, int i, int i2, boolean z, int i3, int i4, float f, int i5, int i6, DefaultConstructorMarker defaultConstructorMarker) {
        this(set, (i6 & 2) != 0 ? 0 : i, (i6 & 4) != 0 ? 1 : i2, (i6 & 8) != 0 ? false : z, (i6 & 16) != 0 ? 0 : i3, (i6 & 32) == 0 ? i4 : 0, (i6 & 64) != 0 ? 0.5f : f, (i6 & 128) != 0 ? 3 : i5);
    }

    public final int getFinishPrimaryWithSecondary() {
        return this.finishPrimaryWithSecondary;
    }

    public final int getFinishSecondaryWithPrimary() {
        return this.finishSecondaryWithPrimary;
    }

    public final boolean getClearTop() {
        return this.clearTop;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SplitPairRule(@NotNull Set<SplitPairFilter> filters, int i, int i2, boolean z, int i3, int i4, float f, int i5) {
        super(i3, i4, f, i5);
        Set<SplitPairFilter> set;
        Intrinsics.checkNotNullParameter(filters, "filters");
        this.finishPrimaryWithSecondary = i;
        this.finishSecondaryWithPrimary = i2;
        this.clearTop = z;
        set = CollectionsKt___CollectionsKt.toSet(filters);
        this.filters = set;
    }

    @NotNull
    public final Set<SplitPairFilter> getFilters() {
        return this.filters;
    }

    @NotNull
    public final SplitPairRule plus$window_release(@NotNull SplitPairFilter filter) {
        Set set;
        Intrinsics.checkNotNullParameter(filter, "filter");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.addAll(this.filters);
        linkedHashSet.add(filter);
        set = CollectionsKt___CollectionsKt.toSet(linkedHashSet);
        return new SplitPairRule(set, this.finishPrimaryWithSecondary, this.finishSecondaryWithPrimary, this.clearTop, getMinWidth(), getMinSmallestWidth(), getSplitRatio(), getLayoutDirection());
    }

    @Override // androidx.window.embedding.SplitRule
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof SplitPairRule) && super.equals(obj)) {
            SplitPairRule splitPairRule = (SplitPairRule) obj;
            return Intrinsics.areEqual(this.filters, splitPairRule.filters) && this.finishPrimaryWithSecondary == splitPairRule.finishPrimaryWithSecondary && this.finishSecondaryWithPrimary == splitPairRule.finishSecondaryWithPrimary && this.clearTop == splitPairRule.clearTop;
        }
        return false;
    }

    @Override // androidx.window.embedding.SplitRule
    public int hashCode() {
        return (((((((super.hashCode() * 31) + this.filters.hashCode()) * 31) + Integer.hashCode(this.finishPrimaryWithSecondary)) * 31) + Integer.hashCode(this.finishSecondaryWithPrimary)) * 31) + Boolean.hashCode(this.clearTop);
    }
}
