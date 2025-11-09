package com.google.android.setupcompat.internal;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.setupcompat.ISetupCompatService;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.util.Logger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes2.dex */
public class SetupCompatServiceProvider {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile SetupCompatServiceProvider instance;
    private final Context context;
    private static final Logger LOG = new Logger("SetupCompatServiceProvider");
    static final Intent COMPAT_SERVICE_INTENT = new Intent().setPackage(PartnerConfigHelper.SUW_PACKAGE_NAME).setAction("com.google.android.setupcompat.SetupCompatService.BIND");
    static boolean disableLooperCheckForTesting = false;
    final ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.google.android.setupcompat.internal.SetupCompatServiceProvider.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            State state = State.CONNECTED;
            if (iBinder == null) {
                state = State.DISCONNECTED;
                SetupCompatServiceProvider.LOG.w("Binder is null when onServiceConnected was called!");
            }
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(state, ISetupCompatService.Stub.asInterface(iBinder)));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(State.DISCONNECTED));
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(State.REBIND_REQUIRED));
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(State.SERVICE_NOT_USABLE));
        }
    };
    private volatile ServiceContext serviceContext = new ServiceContext(State.NOT_STARTED);
    private final AtomicReference<CountDownLatch> connectedConditionRef = new AtomicReference<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public enum State {
        NOT_STARTED,
        BIND_FAILED,
        BINDING,
        CONNECTED,
        DISCONNECTED,
        SERVICE_NOT_USABLE,
        REBIND_REQUIRED
    }

    public static ISetupCompatService get(Context context, long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        return getInstance(context).getService(j, timeUnit);
    }

    public ISetupCompatService getService(long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        Preconditions.checkState(disableLooperCheckForTesting || Looper.getMainLooper() != Looper.myLooper(), "getService blocks and should not be called from the main thread.");
        ServiceContext currentServiceState = getCurrentServiceState();
        switch (AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[currentServiceState.state.ordinal()]) {
            case 1:
                return currentServiceState.compatService;
            case 2:
            case 3:
                return null;
            case 4:
            case 5:
                return waitForConnection(j, timeUnit);
            case 6:
                requestServiceBind();
                return waitForConnection(j, timeUnit);
            case 7:
                LOG.w("NOT_STARTED state only possible before instance is created.");
                return null;
            default:
                throw new IllegalStateException("Unknown state = " + currentServiceState.state);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.android.setupcompat.internal.SetupCompatServiceProvider$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State = iArr;
            try {
                iArr[State.CONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[State.SERVICE_NOT_USABLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[State.BIND_FAILED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[State.DISCONNECTED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[State.BINDING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[State.REBIND_REQUIRED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[State.NOT_STARTED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    private ISetupCompatService waitForConnection(long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        ServiceContext currentServiceState = getCurrentServiceState();
        if (currentServiceState.state == State.CONNECTED) {
            return currentServiceState.compatService;
        }
        CountDownLatch connectedCondition = getConnectedCondition();
        Logger logger = LOG;
        logger.atInfo("Waiting for service to get connected");
        if (!connectedCondition.await(j, timeUnit)) {
            requestServiceBind();
            throw new TimeoutException(String.format("Failed to acquire connection after [%s %s]", Long.valueOf(j), timeUnit));
        }
        ServiceContext currentServiceState2 = getCurrentServiceState();
        logger.atInfo(String.format("Finished waiting for service to get connected. Current state = %s", currentServiceState2.state));
        return currentServiceState2.compatService;
    }

    protected CountDownLatch createCountDownLatch() {
        return new CountDownLatch(1);
    }

    private synchronized void requestServiceBind() {
        boolean z;
        State state = getCurrentServiceState().state;
        if (state == State.CONNECTED) {
            LOG.atInfo("Refusing to rebind since current state is already connected");
            return;
        }
        if (state != State.NOT_STARTED) {
            LOG.atInfo("Unbinding existing service connection.");
            this.context.unbindService(this.serviceConnection);
        }
        try {
            z = this.context.bindService(COMPAT_SERVICE_INTENT, this.serviceConnection, 1);
        } catch (SecurityException e) {
            Logger logger = LOG;
            logger.e("Unable to bind to compat service. " + e);
            z = false;
        }
        if (z) {
            if (getCurrentState() != State.CONNECTED) {
                swapServiceContextAndNotify(new ServiceContext(State.BINDING));
                LOG.atInfo("Context#bindService went through, now waiting for service connection");
            }
        } else {
            swapServiceContextAndNotify(new ServiceContext(State.BIND_FAILED));
            LOG.e("Context#bindService did not succeed.");
        }
    }

    State getCurrentState() {
        return this.serviceContext.state;
    }

    private synchronized ServiceContext getCurrentServiceState() {
        return this.serviceContext;
    }

    void swapServiceContextAndNotify(ServiceContext serviceContext) {
        LOG.atInfo(String.format("State changed: %s -> %s", this.serviceContext.state, serviceContext.state));
        this.serviceContext = serviceContext;
        CountDownLatch andClearConnectedCondition = getAndClearConnectedCondition();
        if (andClearConnectedCondition != null) {
            andClearConnectedCondition.countDown();
        }
    }

    private CountDownLatch getAndClearConnectedCondition() {
        return this.connectedConditionRef.getAndSet(null);
    }

    private CountDownLatch getConnectedCondition() {
        CountDownLatch createCountDownLatch;
        do {
            CountDownLatch countDownLatch = this.connectedConditionRef.get();
            if (countDownLatch != null) {
                return countDownLatch;
            }
            createCountDownLatch = createCountDownLatch();
        } while (!this.connectedConditionRef.compareAndSet(null, createCountDownLatch));
        return createCountDownLatch;
    }

    SetupCompatServiceProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class ServiceContext {
        final ISetupCompatService compatService;
        final State state;

        private ServiceContext(State state, ISetupCompatService iSetupCompatService) {
            this.state = state;
            this.compatService = iSetupCompatService;
            if (state == State.CONNECTED) {
                Preconditions.checkNotNull(iSetupCompatService, "CompatService cannot be null when state is connected");
            }
        }

        ServiceContext(State state) {
            this(state, null);
        }
    }

    static SetupCompatServiceProvider getInstance(Context context) {
        Preconditions.checkNotNull(context, "Context object cannot be null.");
        SetupCompatServiceProvider setupCompatServiceProvider = instance;
        if (setupCompatServiceProvider == null) {
            synchronized (SetupCompatServiceProvider.class) {
                setupCompatServiceProvider = instance;
                if (setupCompatServiceProvider == null) {
                    setupCompatServiceProvider = new SetupCompatServiceProvider(context.getApplicationContext());
                    instance = setupCompatServiceProvider;
                    instance.requestServiceBind();
                }
            }
        }
        return setupCompatServiceProvider;
    }

    public static void setInstanceForTesting(SetupCompatServiceProvider setupCompatServiceProvider) {
        instance = setupCompatServiceProvider;
    }
}
