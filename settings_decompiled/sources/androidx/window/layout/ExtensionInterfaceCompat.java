package androidx.window.layout;

import android.app.Activity;
import org.jetbrains.annotations.NotNull;
/* compiled from: ExtensionInterfaceCompat.kt */
/* loaded from: classes.dex */
public interface ExtensionInterfaceCompat {

    /* compiled from: ExtensionInterfaceCompat.kt */
    /* loaded from: classes.dex */
    public interface ExtensionCallbackInterface {
        void onWindowLayoutChanged(@NotNull Activity activity, @NotNull WindowLayoutInfo windowLayoutInfo);
    }

    void onWindowLayoutChangeListenerAdded(@NotNull Activity activity);

    void onWindowLayoutChangeListenerRemoved(@NotNull Activity activity);

    void setExtensionCallback(@NotNull ExtensionCallbackInterface extensionCallbackInterface);

    boolean validateExtensionInterface();
}
