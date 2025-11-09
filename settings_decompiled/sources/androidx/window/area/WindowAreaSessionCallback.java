package androidx.window.area;

import androidx.window.core.ExperimentalWindowApi;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowAreaSessionCallback.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public interface WindowAreaSessionCallback {
    void onSessionEnded();

    void onSessionStarted(@NotNull WindowAreaSession windowAreaSession);
}
