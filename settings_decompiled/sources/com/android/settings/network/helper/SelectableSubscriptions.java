package com.android.settings.network.helper;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.Keep;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class SelectableSubscriptions implements Callable<List<SubscriptionAnnotation>> {
    private Context mContext;
    private Predicate<SubscriptionAnnotation> mFilter;
    private Function<List<SubscriptionAnnotation>, List<SubscriptionAnnotation>> mFinisher;
    private Supplier<List<SubscriptionInfo>> mSubscriptions;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ List lambda$new$4(List list) {
        return list;
    }

    public SelectableSubscriptions(final Context context, boolean z) {
        Supplier<List<SubscriptionInfo>> supplier;
        this.mContext = context;
        if (z) {
            supplier = new Supplier() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    List lambda$new$0;
                    lambda$new$0 = SelectableSubscriptions.this.lambda$new$0(context);
                    return lambda$new$0;
                }
            };
        } else {
            supplier = new Supplier() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda4
                @Override // java.util.function.Supplier
                public final Object get() {
                    List lambda$new$1;
                    lambda$new$1 = SelectableSubscriptions.this.lambda$new$1(context);
                    return lambda$new$1;
                }
            };
        }
        this.mSubscriptions = supplier;
        if (z) {
            this.mFilter = new Predicate() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$new$2;
                    lambda$new$2 = SelectableSubscriptions.lambda$new$2((SubscriptionAnnotation) obj);
                    return lambda$new$2;
                }
            };
        } else {
            this.mFilter = new Predicate() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean isActive;
                    isActive = ((SubscriptionAnnotation) obj).isActive();
                    return isActive;
                }
            };
        }
        this.mFinisher = new Function() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                List lambda$new$4;
                lambda$new$4 = SelectableSubscriptions.lambda$new$4((List) obj);
                return lambda$new$4;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$2(SubscriptionAnnotation subscriptionAnnotation) {
        if (subscriptionAnnotation.isExisted()) {
            return true;
        }
        return subscriptionAnnotation.getType() == 2 && subscriptionAnnotation.isDisplayAllowed();
    }

    public SelectableSubscriptions addFinisher(UnaryOperator<List<SubscriptionAnnotation>> unaryOperator) {
        this.mFinisher = this.mFinisher.andThen(unaryOperator);
        return this;
    }

    @Override // java.util.concurrent.Callable
    public List<SubscriptionAnnotation> call() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        try {
            Future postOnBackgroundThread = ThreadUtils.postOnBackgroundThread(new QueryEsimCardId(telephonyManager));
            Future postOnBackgroundThread2 = ThreadUtils.postOnBackgroundThread(new QuerySimSlotIndex(telephonyManager, true, true));
            Future postOnBackgroundThread3 = ThreadUtils.postOnBackgroundThread(new QuerySimSlotIndex(telephonyManager, false, true));
            final List<SubscriptionInfo> list = this.mSubscriptions.get();
            final List<Integer> atomicToList = atomicToList((AtomicIntegerArray) postOnBackgroundThread.get());
            final List<Integer> atomicToList2 = atomicToList((AtomicIntegerArray) postOnBackgroundThread2.get());
            final List<Integer> atomicToList3 = atomicToList((AtomicIntegerArray) postOnBackgroundThread3.get());
            return (List) IntStream.range(0, list.size()).mapToObj(new IntFunction() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    SubscriptionAnnotation.Builder lambda$call$5;
                    lambda$call$5 = SelectableSubscriptions.lambda$call$5(list, i);
                    return lambda$call$5;
                }
            }).map(new Function() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    SubscriptionAnnotation lambda$call$6;
                    lambda$call$6 = SelectableSubscriptions.this.lambda$call$6(atomicToList, atomicToList2, atomicToList3, (SubscriptionAnnotation.Builder) obj);
                    return lambda$call$6;
                }
            }).filter(this.mFilter).collect(Collectors.collectingAndThen(Collectors.toList(), this.mFinisher));
        } catch (Exception e) {
            Log.w("SelectableSubscriptions", "Fail to request subIdList", e);
            return Collections.emptyList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ SubscriptionAnnotation.Builder lambda$call$5(List list, int i) {
        return new SubscriptionAnnotation.Builder(list, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionAnnotation lambda$call$6(List list, List list2, List list3, SubscriptionAnnotation.Builder builder) {
        return builder.build(this.mContext, list, list2, list3);
    }

    protected List<SubscriptionInfo> getSubInfoList(Context context, Function<SubscriptionManager, List<SubscriptionInfo>> function) {
        SubscriptionManager subscriptionManager = getSubscriptionManager(context);
        List<SubscriptionInfo> apply = subscriptionManager == null ? null : function.apply(subscriptionManager);
        return apply == null ? Collections.emptyList() : apply;
    }

    protected SubscriptionManager getSubscriptionManager(Context context) {
        return (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: getAvailableSubInfoList */
    public List<SubscriptionInfo> lambda$new$0(Context context) {
        return getSubInfoList(context, new Function() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((SubscriptionManager) obj).getAvailableSubscriptionInfoList();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: getActiveSubInfoList */
    public List<SubscriptionInfo> lambda$new$1(Context context) {
        return getSubInfoList(context, new Function() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((SubscriptionManager) obj).getActiveSubscriptionInfoList();
            }
        });
    }

    @Keep
    protected static List<Integer> atomicToList(final AtomicIntegerArray atomicIntegerArray) {
        if (atomicIntegerArray == null) {
            return Collections.emptyList();
        }
        return (List) IntStream.range(0, atomicIntegerArray.length()).map(new IntUnaryOperator() { // from class: com.android.settings.network.helper.SelectableSubscriptions$$ExternalSyntheticLambda2
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i) {
                int i2;
                i2 = atomicIntegerArray.get(i);
                return i2;
            }
        }).boxed().collect(Collectors.toList());
    }
}
