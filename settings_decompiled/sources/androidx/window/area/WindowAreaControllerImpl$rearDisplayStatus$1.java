package androidx.window.area;

import androidx.window.area.WindowAreaStatus;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.Channel;
import kotlinx.coroutines.flow.FlowCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: WindowAreaControllerImpl.kt */
@DebugMetadata(c = "androidx.window.area.WindowAreaControllerImpl$rearDisplayStatus$1", f = "WindowAreaControllerImpl.kt", l = {65, 66}, m = "invokeSuspend")
/* loaded from: classes.dex */
final class WindowAreaControllerImpl$rearDisplayStatus$1 extends SuspendLambda implements Function2<FlowCollector<? super WindowAreaStatus>, Continuation<? super Unit>, Object> {
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    int label;
    final /* synthetic */ WindowAreaControllerImpl this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public WindowAreaControllerImpl$rearDisplayStatus$1(WindowAreaControllerImpl windowAreaControllerImpl, Continuation<? super WindowAreaControllerImpl$rearDisplayStatus$1> continuation) {
        super(2, continuation);
        this.this$0 = windowAreaControllerImpl;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    @NotNull
    public final Continuation<Unit> create(@Nullable Object obj, @NotNull Continuation<?> continuation) {
        WindowAreaControllerImpl$rearDisplayStatus$1 windowAreaControllerImpl$rearDisplayStatus$1 = new WindowAreaControllerImpl$rearDisplayStatus$1(this.this$0, continuation);
        windowAreaControllerImpl$rearDisplayStatus$1.L$0 = obj;
        return windowAreaControllerImpl$rearDisplayStatus$1;
    }

    @Override // kotlin.jvm.functions.Function2
    @Nullable
    public final Object invoke(@NotNull FlowCollector<? super WindowAreaStatus> flowCollector, @Nullable Continuation<? super Unit> continuation) {
        return ((WindowAreaControllerImpl$rearDisplayStatus$1) create(flowCollector, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x006d A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x006e  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0079 A[Catch: all -> 0x0037, TRY_LEAVE, TryCatch #0 {all -> 0x0037, blocks: (B:7:0x001a, B:19:0x005f, B:23:0x0071, B:25:0x0079, B:12:0x0033), top: B:34:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x008e  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:26:0x008b -> B:8:0x001d). Please submit an issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object invokeSuspend(@org.jetbrains.annotations.NotNull java.lang.Object r9) {
        /*
            r8 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r8.label
            r2 = 2
            r3 = 1
            if (r1 == 0) goto L39
            if (r1 == r3) goto L27
            if (r1 != r2) goto L1f
            java.lang.Object r1 = r8.L$2
            kotlinx.coroutines.channels.ChannelIterator r1 = (kotlinx.coroutines.channels.ChannelIterator) r1
            java.lang.Object r4 = r8.L$1
            java.util.function.Consumer r4 = (java.util.function.Consumer) r4
            java.lang.Object r5 = r8.L$0
            kotlinx.coroutines.flow.FlowCollector r5 = (kotlinx.coroutines.flow.FlowCollector) r5
            kotlin.ResultKt.throwOnFailure(r9)     // Catch: java.lang.Throwable -> L37
        L1d:
            r9 = r5
            goto L5f
        L1f:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r9 = "call to 'resume' before 'invoke' with coroutine"
            r8.<init>(r9)
            throw r8
        L27:
            java.lang.Object r1 = r8.L$2
            kotlinx.coroutines.channels.ChannelIterator r1 = (kotlinx.coroutines.channels.ChannelIterator) r1
            java.lang.Object r4 = r8.L$1
            java.util.function.Consumer r4 = (java.util.function.Consumer) r4
            java.lang.Object r5 = r8.L$0
            kotlinx.coroutines.flow.FlowCollector r5 = (kotlinx.coroutines.flow.FlowCollector) r5
            kotlin.ResultKt.throwOnFailure(r9)     // Catch: java.lang.Throwable -> L37
            goto L71
        L37:
            r9 = move-exception
            goto L9c
        L39:
            kotlin.ResultKt.throwOnFailure(r9)
            java.lang.Object r9 = r8.L$0
            kotlinx.coroutines.flow.FlowCollector r9 = (kotlinx.coroutines.flow.FlowCollector) r9
            r1 = 10
            kotlinx.coroutines.channels.BufferOverflow r4 = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
            r5 = 4
            r6 = 0
            kotlinx.coroutines.channels.Channel r1 = kotlinx.coroutines.channels.ChannelKt.Channel$default(r1, r4, r6, r5, r6)
            androidx.window.area.WindowAreaControllerImpl r4 = r8.this$0
            androidx.window.area.WindowAreaControllerImpl$rearDisplayStatus$1$$ExternalSyntheticLambda0 r5 = new androidx.window.area.WindowAreaControllerImpl$rearDisplayStatus$1$$ExternalSyntheticLambda0
            r5.<init>()
            androidx.window.area.WindowAreaControllerImpl r4 = r8.this$0
            androidx.window.extensions.area.WindowAreaComponent r4 = androidx.window.area.WindowAreaControllerImpl.access$getWindowAreaComponent$p(r4)
            r4.addRearDisplayStatusListener(r5)
            kotlinx.coroutines.channels.ChannelIterator r1 = r1.iterator()     // Catch: java.lang.Throwable -> L9a
            r4 = r5
        L5f:
            r8.L$0 = r9     // Catch: java.lang.Throwable -> L37
            r8.L$1 = r4     // Catch: java.lang.Throwable -> L37
            r8.L$2 = r1     // Catch: java.lang.Throwable -> L37
            r8.label = r3     // Catch: java.lang.Throwable -> L37
            java.lang.Object r5 = r1.hasNext(r8)     // Catch: java.lang.Throwable -> L37
            if (r5 != r0) goto L6e
            return r0
        L6e:
            r7 = r5
            r5 = r9
            r9 = r7
        L71:
            java.lang.Boolean r9 = (java.lang.Boolean) r9     // Catch: java.lang.Throwable -> L37
            boolean r9 = r9.booleanValue()     // Catch: java.lang.Throwable -> L37
            if (r9 == 0) goto L8e
            java.lang.Object r9 = r1.next()     // Catch: java.lang.Throwable -> L37
            androidx.window.area.WindowAreaStatus r9 = (androidx.window.area.WindowAreaStatus) r9     // Catch: java.lang.Throwable -> L37
            r8.L$0 = r5     // Catch: java.lang.Throwable -> L37
            r8.L$1 = r4     // Catch: java.lang.Throwable -> L37
            r8.L$2 = r1     // Catch: java.lang.Throwable -> L37
            r8.label = r2     // Catch: java.lang.Throwable -> L37
            java.lang.Object r9 = r5.emit(r9, r8)     // Catch: java.lang.Throwable -> L37
            if (r9 != r0) goto L1d
            return r0
        L8e:
            androidx.window.area.WindowAreaControllerImpl r8 = r8.this$0
            androidx.window.extensions.area.WindowAreaComponent r8 = androidx.window.area.WindowAreaControllerImpl.access$getWindowAreaComponent$p(r8)
            r8.removeRearDisplayStatusListener(r4)
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            return r8
        L9a:
            r9 = move-exception
            r4 = r5
        L9c:
            androidx.window.area.WindowAreaControllerImpl r8 = r8.this$0
            androidx.window.extensions.area.WindowAreaComponent r8 = androidx.window.area.WindowAreaControllerImpl.access$getWindowAreaComponent$p(r8)
            r8.removeRearDisplayStatusListener(r4)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.area.WindowAreaControllerImpl$rearDisplayStatus$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: invokeSuspend$lambda-0  reason: not valid java name */
    public static final void m11invokeSuspend$lambda0(WindowAreaControllerImpl windowAreaControllerImpl, Channel channel, Integer status) {
        WindowAreaStatus windowAreaStatus;
        WindowAreaStatus.Companion companion = WindowAreaStatus.Companion;
        Intrinsics.checkNotNullExpressionValue(status, "status");
        windowAreaControllerImpl.currentStatus = companion.translate$window_release(status.intValue());
        windowAreaStatus = windowAreaControllerImpl.currentStatus;
        if (windowAreaStatus == null) {
            windowAreaStatus = WindowAreaStatus.UNSUPPORTED;
        }
        channel.trySend-JP2dKIU(windowAreaStatus);
    }
}
