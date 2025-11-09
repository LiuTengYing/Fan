package androidx.window.core;

import androidx.window.core.SpecificationComputer;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: SpecificationComputer.kt */
/* loaded from: classes.dex */
final class ValidSpecification<T> extends SpecificationComputer<T> {
    @NotNull
    private final Logger logger;
    @NotNull
    private final String tag;
    @NotNull
    private final T value;
    @NotNull
    private final SpecificationComputer.VerificationMode verificationMode;

    @NotNull
    public final T getValue() {
        return this.value;
    }

    @NotNull
    public final String getTag() {
        return this.tag;
    }

    @NotNull
    public final SpecificationComputer.VerificationMode getVerificationMode() {
        return this.verificationMode;
    }

    @NotNull
    public final Logger getLogger() {
        return this.logger;
    }

    public ValidSpecification(@NotNull T value, @NotNull String tag, @NotNull SpecificationComputer.VerificationMode verificationMode, @NotNull Logger logger) {
        Intrinsics.checkNotNullParameter(value, "value");
        Intrinsics.checkNotNullParameter(tag, "tag");
        Intrinsics.checkNotNullParameter(verificationMode, "verificationMode");
        Intrinsics.checkNotNullParameter(logger, "logger");
        this.value = value;
        this.tag = tag;
        this.verificationMode = verificationMode;
        this.logger = logger;
    }

    @Override // androidx.window.core.SpecificationComputer
    @NotNull
    public SpecificationComputer<T> require(@NotNull String message, @NotNull Function1<? super T, Boolean> condition) {
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(condition, "condition");
        return condition.invoke((T) this.value).booleanValue() ? this : new FailedSpecification(this.value, this.tag, message, this.logger, this.verificationMode);
    }

    @Override // androidx.window.core.SpecificationComputer
    @NotNull
    public T compute() {
        return this.value;
    }
}
