package androidx.window.core;

import androidx.window.core.SpecificationComputer;
import org.jetbrains.annotations.NotNull;
/* compiled from: BuildConfig.kt */
/* loaded from: classes.dex */
public final class BuildConfig {
    @NotNull
    public static final BuildConfig INSTANCE = new BuildConfig();
    @NotNull
    private static final SpecificationComputer.VerificationMode verificationMode = SpecificationComputer.VerificationMode.QUIET;

    private BuildConfig() {
    }

    @NotNull
    public final SpecificationComputer.VerificationMode getVerificationMode() {
        return verificationMode;
    }
}
