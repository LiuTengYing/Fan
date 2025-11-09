package com.unisoc.settings.network;

import android.content.Context;
import android.os.Looper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class ProxySimStateManager implements LifecycleObserver {
    private static ProxySimStateManager sSingleton;
    private Lifecycle mLifecycle;
    private List<OnSimStateChangedListener> mSimStateChangeListeners;
    private SimStateChangeListener mSimStateMonitor;

    /* loaded from: classes2.dex */
    public interface OnSimStateChangedListener {
        default Lifecycle getLifecycle() {
            return null;
        }

        void onChanged();
    }

    public static ProxySimStateManager getInstance(Context context) {
        ProxySimStateManager proxySimStateManager = sSingleton;
        if (proxySimStateManager != null) {
            return proxySimStateManager;
        }
        ProxySimStateManager proxySimStateManager2 = new ProxySimStateManager(context.getApplicationContext());
        sSingleton = proxySimStateManager2;
        return proxySimStateManager2;
    }

    private ProxySimStateManager(Context context) {
        Looper mainLooper = context.getMainLooper();
        this.mSimStateChangeListeners = new ArrayList();
        SimStateChangeListener simStateChangeListener = new SimStateChangeListener(mainLooper, context) { // from class: com.unisoc.settings.network.ProxySimStateManager.1
            @Override // com.unisoc.settings.network.SimStateChangeListener
            public void onChanged() {
                ProxySimStateManager.this.notifyAllListeners();
            }
        };
        this.mSimStateMonitor = simStateChangeListener;
        simStateChangeListener.start();
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
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        this.mSimStateMonitor.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        this.mSimStateMonitor.stop();
        Lifecycle lifecycle = this.mLifecycle;
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
            this.mLifecycle = null;
            sSingleton = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAllListeners() {
        for (OnSimStateChangedListener onSimStateChangedListener : this.mSimStateChangeListeners) {
            Lifecycle lifecycle = onSimStateChangedListener.getLifecycle();
            if (lifecycle == null || lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                onSimStateChangedListener.onChanged();
            }
        }
    }

    public void addSimStateChangeListener(OnSimStateChangedListener onSimStateChangedListener) {
        if (this.mSimStateChangeListeners.contains(onSimStateChangedListener)) {
            return;
        }
        this.mSimStateChangeListeners.add(onSimStateChangedListener);
    }

    public void removeSimStateChangeListener(OnSimStateChangedListener onSimStateChangedListener) {
        this.mSimStateChangeListeners.remove(onSimStateChangedListener);
    }

    public String getSimState(int i) {
        return this.mSimStateMonitor.getSimState(i);
    }
}
