package com.android.settings.network.telephony;

import android.util.Log;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class TelephonyStatusControlSession implements AutoCloseable {
    private Collection<AbstractPreferenceController> mControllers;
    private Collection<Future<Boolean>> mResult;

    /* loaded from: classes.dex */
    public static class Builder {
        private Collection<AbstractPreferenceController> mControllers;

        public Builder(Collection<AbstractPreferenceController> collection) {
            this.mControllers = collection;
        }

        public TelephonyStatusControlSession build() {
            return new TelephonyStatusControlSession(this.mControllers);
        }
    }

    private TelephonyStatusControlSession(Collection<AbstractPreferenceController> collection) {
        this.mResult = new ArrayList();
        this.mControllers = collection;
        collection.forEach(new Consumer() { // from class: com.android.settings.network.telephony.TelephonyStatusControlSession$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TelephonyStatusControlSession.this.lambda$new$1((AbstractPreferenceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(final AbstractPreferenceController abstractPreferenceController) {
        this.mResult.add(ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.telephony.TelephonyStatusControlSession$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$new$0;
                lambda$new$0 = TelephonyStatusControlSession.this.lambda$new$0(abstractPreferenceController);
                return lambda$new$0;
            }
        }));
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        for (Future<Boolean> future : this.mResult) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e("TelephonyStatusControlSS", "setup availability status failed!", e);
            }
        }
        unsetAvailabilityStatus(this.mControllers);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: setupAvailabilityStatus */
    public Boolean lambda$new$0(AbstractPreferenceController abstractPreferenceController) {
        try {
            if (abstractPreferenceController instanceof TelephonyAvailabilityHandler) {
                ((TelephonyAvailabilityHandler) abstractPreferenceController).setAvailabilityStatus(((BasePreferenceController) abstractPreferenceController).getAvailabilityStatus());
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            Log.e("TelephonyStatusControlSS", "Setup availability status failed!", e);
            return Boolean.FALSE;
        }
    }

    private void unsetAvailabilityStatus(Collection<AbstractPreferenceController> collection) {
        collection.stream().filter(new Predicate() { // from class: com.android.settings.network.telephony.TelephonyStatusControlSession$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$unsetAvailabilityStatus$2;
                lambda$unsetAvailabilityStatus$2 = TelephonyStatusControlSession.lambda$unsetAvailabilityStatus$2((AbstractPreferenceController) obj);
                return lambda$unsetAvailabilityStatus$2;
            }
        }).map(new Function() { // from class: com.android.settings.network.telephony.TelephonyStatusControlSession$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (TelephonyAvailabilityHandler) r1.cast((AbstractPreferenceController) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.settings.network.telephony.TelephonyStatusControlSession$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TelephonyAvailabilityHandler) obj).unsetAvailabilityStatus();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$unsetAvailabilityStatus$2(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof TelephonyAvailabilityHandler;
    }
}
