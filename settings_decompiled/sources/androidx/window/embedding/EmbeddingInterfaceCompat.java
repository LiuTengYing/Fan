package androidx.window.embedding;

import android.app.Activity;
import androidx.window.core.ExperimentalWindowApi;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
/* compiled from: EmbeddingInterfaceCompat.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public interface EmbeddingInterfaceCompat {

    /* compiled from: EmbeddingInterfaceCompat.kt */
    /* loaded from: classes.dex */
    public interface EmbeddingCallbackInterface {
        void onSplitInfoChanged(@NotNull List<SplitInfo> list);
    }

    boolean isActivityEmbedded(@NotNull Activity activity);

    void setEmbeddingCallback(@NotNull EmbeddingCallbackInterface embeddingCallbackInterface);

    void setSplitRules(@NotNull Set<? extends EmbeddingRule> set);
}
