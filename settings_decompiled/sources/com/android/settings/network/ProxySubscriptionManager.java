package com.android.settings.network;

import android.content.Context;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.network.ProxySubscriptionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ProxySubscriptionManager implements LifecycleObserver {
    private static ProxySubscriptionManager sSingleton;
    private List<OnActiveSubscriptionChangedListener> mActiveSubscriptionsListeners;
    private GlobalSettingsChangeListener mAirplaneModeMonitor;
    private Lifecycle mLifecycle;
    private List<OnActiveSubscriptionChangedListener> mPendingNotifyListeners;
    private ActiveSubscriptionsListener mSubscriptionMonitor;

    /* loaded from: classes.dex */
    public interface OnActiveSubscriptionChangedListener {
        default Lifecycle getLifecycle() {
            return null;
        }

        void onChanged();
    }

    public static ProxySubscriptionManager getInstance(Context context) {
        ProxySubscriptionManager proxySubscriptionManager = sSingleton;
        if (proxySubscriptionManager != null) {
            return proxySubscriptionManager;
        }
        ProxySubscriptionManager proxySubscriptionManager2 = new ProxySubscriptionManager(context.getApplicationContext());
        sSingleton = proxySubscriptionManager2;
        return proxySubscriptionManager2;
    }

    private ProxySubscriptionManager(Context context) {
        Looper mainLooper = context.getMainLooper();
        final ActiveSubscriptionsListener activeSubscriptionsListener = new ActiveSubscriptionsListener(mainLooper, context) { // from class: com.android.settings.network.ProxySubscriptionManager.1
            @Override // com.android.settings.network.ActiveSubscriptionsListener
            public void onChanged() {
                ProxySubscriptionManager.this.notifySubscriptionInfoMightChanged();
            }
        };
        init(context, activeSubscriptionsListener, new GlobalSettingsChangeListener(mainLooper, context, "airplane_mode_on") { // from class: com.android.settings.network.ProxySubscriptionManager.2
            @Override // com.android.settings.network.GlobalSettingsChangeListener
            public void onChanged(String str) {
                activeSubscriptionsListener.clearCache();
                ProxySubscriptionManager.this.notifySubscriptionInfoMightChanged();
            }
        });
    }

    @Keep
    protected void init(Context context, ActiveSubscriptionsListener activeSubscriptionsListener, GlobalSettingsChangeListener globalSettingsChangeListener) {
        this.mActiveSubscriptionsListeners = new ArrayList();
        this.mPendingNotifyListeners = new ArrayList();
        this.mSubscriptionMonitor = activeSubscriptionsListener;
        this.mAirplaneModeMonitor = globalSettingsChangeListener;
        activeSubscriptionsListener.start();
    }

    @Keep
    protected void notifySubscriptionInfoMightChanged() {
        ArrayList arrayList = new ArrayList(this.mPendingNotifyListeners);
        arrayList.addAll(this.mActiveSubscriptionsListeners);
        this.mActiveSubscriptionsListeners.clear();
        this.mPendingNotifyListeners.clear();
        processStatusChangeOnListeners(arrayList);
    }

    public void setLifecycle(Lifecycle lifecycle) {
        Lifecycle lifecycle2 = this.mLifecycle;
        if (lifecycle2 == lifecycle) {
            return;
        }
        if (lifecycle2 != null) {
            lifecycle2.removeObserver(this);
        }
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mLifecycle = lifecycle;
        this.mAirplaneModeMonitor.notifyChangeBasedOn(lifecycle);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        this.mSubscriptionMonitor.start();
        List<OnActiveSubscriptionChangedListener> list = this.mPendingNotifyListeners;
        this.mPendingNotifyListeners = new ArrayList();
        processStatusChangeOnListeners(list);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        this.mSubscriptionMonitor.stop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        this.mSubscriptionMonitor.close();
        this.mAirplaneModeMonitor.close();
        Lifecycle lifecycle = this.mLifecycle;
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
            this.mLifecycle = null;
            sSingleton = null;
        }
    }

    public SubscriptionManager get() {
        return this.mSubscriptionMonitor.getSubscriptionManager();
    }

    public int getActiveSubscriptionInfoCountMax() {
        return this.mSubscriptionMonitor.getActiveSubscriptionInfoCountMax();
    }

    public List<SubscriptionInfo> getActiveSubscriptionsInfo() {
        return this.mSubscriptionMonitor.getActiveSubscriptionsInfo();
    }

    public SubscriptionInfo getActiveSubscriptionInfo(int i) {
        return this.mSubscriptionMonitor.getActiveSubscriptionInfo(i);
    }

    public List<SubscriptionInfo> getAccessibleSubscriptionsInfo() {
        return this.mSubscriptionMonitor.getAccessibleSubscriptionsInfo();
    }

    public SubscriptionInfo getAccessibleSubscriptionInfo(int i) {
        return this.mSubscriptionMonitor.getAccessibleSubscriptionInfo(i);
    }

    @Keep
    public void addActiveSubscriptionsListener(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mPendingNotifyListeners);
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mActiveSubscriptionsListeners);
        if (onActiveSubscriptionChangedListener == null || getListenerState(onActiveSubscriptionChangedListener) == -1) {
            return;
        }
        this.mActiveSubscriptionsListeners.add(onActiveSubscriptionChangedListener);
    }

    @Keep
    public void removeActiveSubscriptionsListener(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mPendingNotifyListeners);
        removeSpecificListenerAndCleanList(onActiveSubscriptionChangedListener, this.mActiveSubscriptionsListeners);
    }

    private int getListenerState(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        Lifecycle lifecycle = onActiveSubscriptionChangedListener.getLifecycle();
        if (lifecycle == null) {
            return 1;
        }
        Lifecycle.State currentState = lifecycle.getCurrentState();
        if (currentState == Lifecycle.State.DESTROYED) {
            Log.d("ProxySubscriptionManager", "Listener dead detected - " + onActiveSubscriptionChangedListener);
            return -1;
        }
        return currentState.isAtLeast(Lifecycle.State.STARTED) ? 1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$removeSpecificListenerAndCleanList$0(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener, OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener2) {
        return onActiveSubscriptionChangedListener2 == onActiveSubscriptionChangedListener || getListenerState(onActiveSubscriptionChangedListener2) == -1;
    }

    private void removeSpecificListenerAndCleanList(final OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener, List<OnActiveSubscriptionChangedListener> list) {
        list.removeIf(new Predicate() { // from class: com.android.settings.network.ProxySubscriptionManager$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeSpecificListenerAndCleanList$0;
                lambda$removeSpecificListenerAndCleanList$0 = ProxySubscriptionManager.this.lambda$removeSpecificListenerAndCleanList$0(onActiveSubscriptionChangedListener, (ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj);
                return lambda$removeSpecificListenerAndCleanList$0;
            }
        });
    }

    private void processStatusChangeOnListeners(List<OnActiveSubscriptionChangedListener> list) {
        Map map = (Map) list.stream().collect(Collectors.groupingBy(new Function() { // from class: com.android.settings.network.ProxySubscriptionManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$processStatusChangeOnListeners$1;
                lambda$processStatusChangeOnListeners$1 = ProxySubscriptionManager.this.lambda$processStatusChangeOnListeners$1((ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj);
                return lambda$processStatusChangeOnListeners$1;
            }
        }));
        map.computeIfPresent(0, new BiFunction() { // from class: com.android.settings.network.ProxySubscriptionManager$$ExternalSyntheticLambda1
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                List lambda$processStatusChangeOnListeners$2;
                lambda$processStatusChangeOnListeners$2 = ProxySubscriptionManager.this.lambda$processStatusChangeOnListeners$2((Integer) obj, (List) obj2);
                return lambda$processStatusChangeOnListeners$2;
            }
        });
        map.computeIfPresent(1, new BiFunction() { // from class: com.android.settings.network.ProxySubscriptionManager$$ExternalSyntheticLambda2
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                List lambda$processStatusChangeOnListeners$4;
                lambda$processStatusChangeOnListeners$4 = ProxySubscriptionManager.this.lambda$processStatusChangeOnListeners$4((Integer) obj, (List) obj2);
                return lambda$processStatusChangeOnListeners$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$processStatusChangeOnListeners$1(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        return Integer.valueOf(getListenerState(onActiveSubscriptionChangedListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$processStatusChangeOnListeners$2(Integer num, List list) {
        this.mPendingNotifyListeners.addAll(list);
        return list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$processStatusChangeOnListeners$4(Integer num, List list) {
        this.mActiveSubscriptionsListeners.addAll(list);
        list.stream().forEach(new Consumer() { // from class: com.android.settings.network.ProxySubscriptionManager$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj).onChanged();
            }
        });
        return list;
    }
}
