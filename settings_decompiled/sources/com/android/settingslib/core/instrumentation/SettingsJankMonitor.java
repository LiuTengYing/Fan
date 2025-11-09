package com.android.settingslib.core.instrumentation;

import android.view.View;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.jank.InteractionJankMonitor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SettingsJankMonitor.kt */
/* loaded from: classes2.dex */
public final class SettingsJankMonitor {
    @NotNull
    public static final SettingsJankMonitor INSTANCE = new SettingsJankMonitor();
    private static final InteractionJankMonitor jankMonitor = InteractionJankMonitor.getInstance();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static /* synthetic */ void getMONITORED_ANIMATION_DURATION_MS$annotations() {
    }

    private SettingsJankMonitor() {
    }

    public static final void detectSwitchPreferenceClickJank(@NotNull RecyclerView recyclerView, @NotNull SwitchPreference preference) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        Intrinsics.checkNotNullParameter(recyclerView, "recyclerView");
        Intrinsics.checkNotNullParameter(preference, "preference");
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        PreferenceGroupAdapter preferenceGroupAdapter = adapter instanceof PreferenceGroupAdapter ? (PreferenceGroupAdapter) adapter : null;
        if (preferenceGroupAdapter == null || (findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(preferenceGroupAdapter.getPreferenceAdapterPosition(preference))) == null) {
            return;
        }
        String key = preference.getKey();
        View view = findViewHolderForAdapterPosition.itemView;
        Intrinsics.checkNotNullExpressionValue(view, "viewHolder.itemView");
        detectToggleJank(key, view);
    }

    public static final void detectToggleJank(@Nullable String str, @NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        InteractionJankMonitor.Configuration.Builder withView = InteractionJankMonitor.Configuration.Builder.withView(57, view);
        if (str != null) {
            withView.setTag(str);
        }
        if (jankMonitor.begin(withView)) {
            scheduledExecutorService.schedule(new Runnable() { // from class: com.android.settingslib.core.instrumentation.SettingsJankMonitor$detectToggleJank$1
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor interactionJankMonitor;
                    interactionJankMonitor = SettingsJankMonitor.jankMonitor;
                    interactionJankMonitor.end(57);
                }
            }, 300L, TimeUnit.MILLISECONDS);
        }
    }
}
