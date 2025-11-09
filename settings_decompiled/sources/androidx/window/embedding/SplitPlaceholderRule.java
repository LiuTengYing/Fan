package androidx.window.embedding;

import android.content.Intent;
import androidx.window.core.ExperimentalWindowApi;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SplitPlaceholderRule.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class SplitPlaceholderRule extends SplitRule {
    @NotNull
    private final Set<ActivityFilter> filters;
    private final int finishPrimaryWithSecondary;
    private final boolean isSticky;
    @NotNull
    private final Intent placeholderIntent;

    public /* synthetic */ SplitPlaceholderRule(Set set, Intent intent, boolean z, int i, int i2, int i3, float f, int i4, int i5, DefaultConstructorMarker defaultConstructorMarker) {
        this(set, intent, z, (i5 & 8) != 0 ? 1 : i, (i5 & 16) != 0 ? 0 : i2, (i5 & 32) != 0 ? 0 : i3, (i5 & 64) != 0 ? 0.5f : f, (i5 & 128) != 0 ? 3 : i4);
    }

    @NotNull
    public final Intent getPlaceholderIntent() {
        return this.placeholderIntent;
    }

    public final boolean isSticky() {
        return this.isSticky;
    }

    public final int getFinishPrimaryWithSecondary() {
        return this.finishPrimaryWithSecondary;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SplitPlaceholderRule(@NotNull Set<ActivityFilter> filters, @NotNull Intent placeholderIntent, boolean z, int i, int i2, int i3, float f, int i4) {
        super(i2, i3, f, i4);
        Set<ActivityFilter> set;
        Intrinsics.checkNotNullParameter(filters, "filters");
        Intrinsics.checkNotNullParameter(placeholderIntent, "placeholderIntent");
        this.placeholderIntent = placeholderIntent;
        this.isSticky = z;
        this.finishPrimaryWithSecondary = i;
        set = CollectionsKt___CollectionsKt.toSet(filters);
        this.filters = set;
    }

    @NotNull
    public final Set<ActivityFilter> getFilters() {
        return this.filters;
    }

    @NotNull
    public final SplitPlaceholderRule plus$window_release(@NotNull ActivityFilter filter) {
        Set set;
        Intrinsics.checkNotNullParameter(filter, "filter");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.addAll(this.filters);
        linkedHashSet.add(filter);
        set = CollectionsKt___CollectionsKt.toSet(linkedHashSet);
        return new SplitPlaceholderRule(set, this.placeholderIntent, this.isSticky, this.finishPrimaryWithSecondary, getMinWidth(), getMinSmallestWidth(), getSplitRatio(), getLayoutDirection());
    }

    @Override // androidx.window.embedding.SplitRule
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof SplitPlaceholderRule) && super.equals(obj)) {
            SplitPlaceholderRule splitPlaceholderRule = (SplitPlaceholderRule) obj;
            return Intrinsics.areEqual(this.placeholderIntent, splitPlaceholderRule.placeholderIntent) && this.isSticky == splitPlaceholderRule.isSticky && this.finishPrimaryWithSecondary == splitPlaceholderRule.finishPrimaryWithSecondary && Intrinsics.areEqual(this.filters, splitPlaceholderRule.filters);
        }
        return false;
    }

    @Override // androidx.window.embedding.SplitRule
    public int hashCode() {
        return (((((((super.hashCode() * 31) + this.placeholderIntent.hashCode()) * 31) + Boolean.hashCode(this.isSticky)) * 31) + Integer.hashCode(this.finishPrimaryWithSecondary)) * 31) + this.filters.hashCode();
    }
}
