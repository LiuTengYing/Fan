package com.android.settings.fuelgauge.batterytip;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.StatsDimensionsValue;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.R$integer;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AnomalyDetectionJobService extends JobService {
    static final long MAX_DELAY_MS = TimeUnit.MINUTES.toMillis(30);
    static final int STATSD_UID_FILED = 1;
    static final int UID_NULL = -1;
    private final Object mLock = new Object();
    boolean mIsJobCanceled = false;

    public static void scheduleAnomalyDetection(Context context, Intent intent) {
        if (((JobScheduler) context.getSystemService(JobScheduler.class)).enqueue(new JobInfo.Builder(R$integer.job_anomaly_detection, new ComponentName(context, AnomalyDetectionJobService.class)).setOverrideDeadline(MAX_DELAY_MS).build(), new JobWorkItem(intent)) != 1) {
            Log.i("AnomalyDetectionService", "Anomaly detection job service enqueue failed.");
        }
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        synchronized (this.mLock) {
            this.mIsJobCanceled = false;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.batterytip.AnomalyDetectionJobService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnomalyDetectionJobService.this.lambda$onStartJob$0(jobParameters);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(JobParameters jobParameters) {
        BatteryDatabaseManager batteryDatabaseManager = BatteryDatabaseManager.getInstance(this);
        BatteryTipPolicy batteryTipPolicy = new BatteryTipPolicy(this);
        BatteryUtils batteryUtils = BatteryUtils.getInstance(this);
        ContentResolver contentResolver = getContentResolver();
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        PowerAllowlistBackend powerAllowlistBackend = PowerAllowlistBackend.getInstance(this);
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(this).getPowerUsageFeatureProvider(this);
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this).getMetricsFeatureProvider();
        JobWorkItem dequeueWork = dequeueWork(jobParameters);
        while (dequeueWork != null) {
            saveAnomalyToDatabase(this, userManager, batteryDatabaseManager, batteryUtils, batteryTipPolicy, powerAllowlistBackend, contentResolver, powerUsageFeatureProvider, metricsFeatureProvider, dequeueWork.getIntent().getExtras());
            completeWork(jobParameters, dequeueWork);
            dequeueWork = dequeueWork(jobParameters);
            batteryDatabaseManager = batteryDatabaseManager;
        }
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        synchronized (this.mLock) {
            this.mIsJobCanceled = true;
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0068, code lost:
        if (android.provider.Settings.Global.getInt(r20, "app_auto_restriction_enabled", 1) == 1) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void saveAnomalyToDatabase(android.content.Context r14, android.os.UserManager r15, com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager r16, com.android.settings.fuelgauge.BatteryUtils r17, com.android.settings.fuelgauge.batterytip.BatteryTipPolicy r18, com.android.settingslib.fuelgauge.PowerAllowlistBackend r19, android.content.ContentResolver r20, com.android.settings.fuelgauge.PowerUsageFeatureProvider r21, com.android.settingslib.core.instrumentation.MetricsFeatureProvider r22, android.os.Bundle r23) {
        /*
            Method dump skipped, instructions count: 248
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.batterytip.AnomalyDetectionJobService.saveAnomalyToDatabase(android.content.Context, android.os.UserManager, com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager, com.android.settings.fuelgauge.BatteryUtils, com.android.settings.fuelgauge.batterytip.BatteryTipPolicy, com.android.settingslib.fuelgauge.PowerAllowlistBackend, android.content.ContentResolver, com.android.settings.fuelgauge.PowerUsageFeatureProvider, com.android.settingslib.core.instrumentation.MetricsFeatureProvider, android.os.Bundle):void");
    }

    int extractUidFromStatsDimensionsValue(StatsDimensionsValue statsDimensionsValue) {
        if (statsDimensionsValue == null) {
            return UID_NULL;
        }
        if (statsDimensionsValue.isValueType(3) && statsDimensionsValue.getField() == 1) {
            return statsDimensionsValue.getIntValue();
        }
        if (statsDimensionsValue.isValueType(7)) {
            List tupleValueList = statsDimensionsValue.getTupleValueList();
            int size = tupleValueList.size();
            for (int i = 0; i < size; i++) {
                int extractUidFromStatsDimensionsValue = extractUidFromStatsDimensionsValue((StatsDimensionsValue) tupleValueList.get(i));
                if (extractUidFromStatsDimensionsValue != UID_NULL) {
                    return extractUidFromStatsDimensionsValue;
                }
            }
        }
        return UID_NULL;
    }

    JobWorkItem dequeueWork(JobParameters jobParameters) {
        synchronized (this.mLock) {
            if (this.mIsJobCanceled) {
                return null;
            }
            return jobParameters.dequeueWork();
        }
    }

    void completeWork(JobParameters jobParameters, JobWorkItem jobWorkItem) {
        synchronized (this.mLock) {
            if (this.mIsJobCanceled) {
                return;
            }
            jobParameters.completeWork(jobWorkItem);
        }
    }
}
