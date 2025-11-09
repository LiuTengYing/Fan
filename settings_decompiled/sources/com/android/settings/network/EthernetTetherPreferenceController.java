package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class EthernetTetherPreferenceController extends TetherBasePreferenceController {
    private final HashSet<String> mAvailableInterfaces;
    @VisibleForTesting
    EthernetManager.InterfaceStateListener mEthernetListener;
    private final EthernetManager mEthernetManager;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 5;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EthernetTetherPreferenceController(Context context, String str) {
        super(context, str);
        this.mAvailableInterfaces = new HashSet<>();
        this.mEthernetManager = (EthernetManager) context.getSystemService(EthernetManager.class);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mEthernetListener = new EthernetManager.InterfaceStateListener() { // from class: com.android.settings.network.EthernetTetherPreferenceController$$ExternalSyntheticLambda0
            public final void onInterfaceStateChanged(String str, int i, int i2, IpConfiguration ipConfiguration) {
                EthernetTetherPreferenceController.this.lambda$onStart$0(str, i, i2, ipConfiguration);
            }
        };
        final Handler handler = new Handler(Looper.getMainLooper());
        EthernetManager ethernetManager = this.mEthernetManager;
        if (ethernetManager != null) {
            ethernetManager.addInterfaceStateListener(new Executor() { // from class: com.android.settings.network.EthernetTetherPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    handler.post(runnable);
                }
            }, this.mEthernetListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(String str, int i, int i2, IpConfiguration ipConfiguration) {
        if (i == 2) {
            this.mAvailableInterfaces.add(str);
        } else {
            this.mAvailableInterfaces.remove(str);
        }
        updateState(this.mPreference);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        EthernetManager ethernetManager = this.mEthernetManager;
        if (ethernetManager != null) {
            ethernetManager.removeInterfaceStateListener(this.mEthernetListener);
        }
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        ensureRunningOnMainLoopThread();
        for (String str : this.mTm.getTetherableIfaces()) {
            if (this.mAvailableInterfaces.contains(str)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        return this.mEthernetManager != null;
    }

    private void ensureRunningOnMainLoopThread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            return;
        }
        throw new IllegalStateException("Not running on main loop thread: " + Thread.currentThread().getName());
    }
}
