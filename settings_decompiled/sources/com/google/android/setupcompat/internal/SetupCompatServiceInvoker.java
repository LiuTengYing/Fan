package com.google.android.setupcompat.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.setupcompat.ISetupCompatService;
import com.google.android.setupcompat.util.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes2.dex */
public class SetupCompatServiceInvoker {
    private static final Logger LOG = new Logger("SetupCompatServiceInvoker");
    private static final long MAX_WAIT_TIME_FOR_CONNECTION_MS = TimeUnit.SECONDS.toMillis(10);
    @SuppressLint({"StaticFieldLeak"})
    private static SetupCompatServiceInvoker instance;
    private final Context context;
    private final ExecutorService loggingExecutor = ExecutorProvider.setupCompatServiceInvoker.get();
    private final long waitTimeInMillisForServiceConnection = MAX_WAIT_TIME_FOR_CONNECTION_MS;

    @SuppressLint({"DefaultLocale"})
    public void logMetricEvent(final int i, final Bundle bundle) {
        try {
            this.loggingExecutor.execute(new Runnable() { // from class: com.google.android.setupcompat.internal.SetupCompatServiceInvoker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SetupCompatServiceInvoker.this.lambda$logMetricEvent$0(i, bundle);
                }
            });
        } catch (RejectedExecutionException e) {
            LOG.e(String.format("Metric of type %d dropped since queue is full.", Integer.valueOf(i)), e);
        }
    }

    public void bindBack(final String str, final Bundle bundle) {
        try {
            this.loggingExecutor.execute(new Runnable() { // from class: com.google.android.setupcompat.internal.SetupCompatServiceInvoker$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SetupCompatServiceInvoker.this.lambda$bindBack$1(str, bundle);
                }
            });
        } catch (RejectedExecutionException e) {
            LOG.e(String.format("Screen %s bind back fail.", str), e);
        }
    }

    public void onFocusStatusChanged(final String str, final Bundle bundle) {
        try {
            this.loggingExecutor.execute(new Runnable() { // from class: com.google.android.setupcompat.internal.SetupCompatServiceInvoker$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SetupCompatServiceInvoker.this.lambda$onFocusStatusChanged$2(str, bundle);
                }
            });
        } catch (RejectedExecutionException e) {
            LOG.e(String.format("Screen %s report focus changed failed.", str), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: invokeLogMetric */
    public void lambda$logMetricEvent$0(int i, Bundle bundle) {
        try {
            ISetupCompatService iSetupCompatService = SetupCompatServiceProvider.get(this.context, this.waitTimeInMillisForServiceConnection, TimeUnit.MILLISECONDS);
            if (iSetupCompatService != null) {
                iSetupCompatService.logMetric(i, bundle, Bundle.EMPTY);
            } else {
                LOG.w("logMetric failed since service reference is null. Are the permissions valid?");
            }
        } catch (RemoteException | IllegalStateException | InterruptedException | TimeoutException e) {
            LOG.e(String.format("Exception occurred while trying to log metric = [%s]", bundle), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: invokeOnWindowFocusChanged */
    public void lambda$onFocusStatusChanged$2(String str, Bundle bundle) {
        try {
            ISetupCompatService iSetupCompatService = SetupCompatServiceProvider.get(this.context, this.waitTimeInMillisForServiceConnection, TimeUnit.MILLISECONDS);
            if (iSetupCompatService != null) {
                iSetupCompatService.onFocusStatusChanged(bundle);
            } else {
                LOG.w("Report focusChange failed since service reference is null. Are the permission valid?");
            }
        } catch (RemoteException | InterruptedException | UnsupportedOperationException | TimeoutException e) {
            LOG.e(String.format("Exception occurred while %s trying report windowFocusChange to SetupWizard.", str), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: invokeBindBack */
    public void lambda$bindBack$1(String str, Bundle bundle) {
        try {
            ISetupCompatService iSetupCompatService = SetupCompatServiceProvider.get(this.context, this.waitTimeInMillisForServiceConnection, TimeUnit.MILLISECONDS);
            if (iSetupCompatService != null) {
                iSetupCompatService.validateActivity(str, bundle);
            } else {
                LOG.w("BindBack failed since service reference is null. Are the permissions valid?");
            }
        } catch (RemoteException | InterruptedException | TimeoutException e) {
            LOG.e(String.format("Exception occurred while %s trying bind back to SetupWizard.", str), e);
        }
    }

    private SetupCompatServiceInvoker(Context context) {
        this.context = context;
    }

    public static synchronized SetupCompatServiceInvoker get(Context context) {
        SetupCompatServiceInvoker setupCompatServiceInvoker;
        synchronized (SetupCompatServiceInvoker.class) {
            if (instance == null) {
                instance = new SetupCompatServiceInvoker(context.getApplicationContext());
            }
            setupCompatServiceInvoker = instance;
        }
        return setupCompatServiceInvoker;
    }

    static void setInstanceForTesting(SetupCompatServiceInvoker setupCompatServiceInvoker) {
        instance = setupCompatServiceInvoker;
    }
}
