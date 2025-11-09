package androidx.window.embedding;

import android.app.Activity;
import android.content.Context;
import androidx.core.util.Consumer;
import androidx.window.core.ExperimentalWindowApi;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Unit;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.collections.SetsKt__SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SplitController.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class SplitController {
    @Nullable
    private static volatile SplitController globalInstance = null;
    public static final boolean sDebug = false;
    @NotNull
    private final EmbeddingBackend embeddingBackend;
    @NotNull
    private Set<? extends EmbeddingRule> staticSplitRules;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final ReentrantLock globalLock = new ReentrantLock();

    public /* synthetic */ SplitController(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    @NotNull
    public static final SplitController getInstance() {
        return Companion.getInstance();
    }

    public static final void initialize(@NotNull Context context, int i) {
        Companion.initialize(context, i);
    }

    private SplitController() {
        Set<? extends EmbeddingRule> emptySet;
        this.embeddingBackend = ExtensionEmbeddingBackend.Companion.getInstance();
        emptySet = SetsKt__SetsKt.emptySet();
        this.staticSplitRules = emptySet;
    }

    @NotNull
    public final Set<EmbeddingRule> getSplitRules() {
        Set<EmbeddingRule> set;
        set = CollectionsKt___CollectionsKt.toSet(this.embeddingBackend.getSplitRules());
        return set;
    }

    public final void registerRule(@NotNull EmbeddingRule rule) {
        Intrinsics.checkNotNullParameter(rule, "rule");
        this.embeddingBackend.registerRule(rule);
    }

    public final void unregisterRule(@NotNull EmbeddingRule rule) {
        Intrinsics.checkNotNullParameter(rule, "rule");
        this.embeddingBackend.unregisterRule(rule);
    }

    public final void clearRegisteredRules() {
        this.embeddingBackend.setSplitRules(this.staticSplitRules);
    }

    public final void addSplitListener(@NotNull Activity activity, @NotNull Executor executor, @NotNull Consumer<List<SplitInfo>> consumer) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(consumer, "consumer");
        this.embeddingBackend.registerSplitListenerForActivity(activity, executor, consumer);
    }

    public final void removeSplitListener(@NotNull Consumer<List<SplitInfo>> consumer) {
        Intrinsics.checkNotNullParameter(consumer, "consumer");
        this.embeddingBackend.unregisterSplitListenerForActivity(consumer);
    }

    public final boolean isSplitSupported() {
        return this.embeddingBackend.isSplitSupported();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void setStaticSplitRules(Set<? extends EmbeddingRule> set) {
        this.staticSplitRules = set;
        this.embeddingBackend.setSplitRules(set);
    }

    public final boolean isActivityEmbedded(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        return this.embeddingBackend.isActivityEmbedded(activity);
    }

    /* compiled from: SplitController.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final SplitController getInstance() {
            if (SplitController.globalInstance == null) {
                ReentrantLock reentrantLock = SplitController.globalLock;
                reentrantLock.lock();
                try {
                    if (SplitController.globalInstance == null) {
                        Companion companion = SplitController.Companion;
                        SplitController.globalInstance = new SplitController(null);
                    }
                    Unit unit = Unit.INSTANCE;
                } finally {
                    reentrantLock.unlock();
                }
            }
            SplitController splitController = SplitController.globalInstance;
            Intrinsics.checkNotNull(splitController);
            return splitController;
        }

        public final void initialize(@NotNull Context context, int i) {
            Intrinsics.checkNotNullParameter(context, "context");
            Set<EmbeddingRule> parseSplitRules$window_release = new SplitRuleParser().parseSplitRules$window_release(context, i);
            SplitController companion = getInstance();
            if (parseSplitRules$window_release == null) {
                parseSplitRules$window_release = SetsKt__SetsKt.emptySet();
            }
            companion.setStaticSplitRules(parseSplitRules$window_release);
        }
    }
}
