package androidx.window.embedding;

import android.app.Activity;
import androidx.window.core.ExperimentalWindowApi;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ActivityStack.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class ActivityStack {
    @NotNull
    private final List<Activity> activities;
    private final boolean isEmpty;

    /* JADX WARN: Multi-variable type inference failed */
    public ActivityStack(@NotNull List<? extends Activity> activities, boolean z) {
        Intrinsics.checkNotNullParameter(activities, "activities");
        this.activities = activities;
        this.isEmpty = z;
    }

    public /* synthetic */ ActivityStack(List list, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(list, (i & 2) != 0 ? false : z);
    }

    @NotNull
    public final List<Activity> getActivities$window_release() {
        return this.activities;
    }

    public final boolean contains(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        return this.activities.contains(activity);
    }

    public final boolean isEmpty() {
        return this.isEmpty;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ActivityStack) {
            ActivityStack activityStack = (ActivityStack) obj;
            return (Intrinsics.areEqual(this.activities, activityStack.activities) || this.isEmpty == activityStack.isEmpty) ? false : true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.isEmpty ? 1 : 0) * 31) + this.activities.hashCode();
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActivityStack{");
        sb.append("activities=" + this.activities);
        sb.append("isEmpty=" + this.isEmpty + '}');
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "StringBuilder().apply(builderAction).toString()");
        return sb2;
    }
}
