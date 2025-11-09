package com.android.settings.fuelgauge.batterytip;

import android.app.StatsManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.settings.R$integer;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AnomalyConfigJobService extends JobService {
    static final long CONFIG_UPDATE_FREQUENCY_MS = TimeUnit.DAYS.toMillis(1);

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleConfigUpdate(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        ComponentName componentName = new ComponentName(context, AnomalyConfigJobService.class);
        int i = R$integer.job_anomaly_config_update;
        JobInfo.Builder persisted = new JobInfo.Builder(i, componentName).setPeriodic(CONFIG_UPDATE_FREQUENCY_MS).setRequiresDeviceIdle(true).setRequiresCharging(true).setPersisted(true);
        if (jobScheduler.getPendingJob(i) != null || jobScheduler.schedule(persisted.build()) == 1) {
            return;
        }
        Log.i("AnomalyConfigJobService", "Anomaly config update job service schedule failed.");
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.batterytip.AnomalyConfigJobService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnomalyConfigJobService.this.lambda$onStartJob$0(jobParameters);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(JobParameters jobParameters) {
        StatsManager statsManager = (StatsManager) getSystemService(StatsManager.class);
        checkAnomalyConfig(statsManager);
        try {
            BatteryTipUtils.uploadAnomalyPendingIntent(this, statsManager);
        } catch (StatsManager.StatsUnavailableException e) {
            Log.w("AnomalyConfigJobService", "Failed to uploadAnomalyPendingIntent.", e);
        }
        jobFinished(jobParameters, false);
    }

    synchronized void checkAnomalyConfig(StatsManager statsManager) {
        SharedPreferences sharedPreferences = getSharedPreferences("anomaly_pref", 0);
        int i = sharedPreferences.getInt("anomaly_config_version", 0);
        int i2 = Settings.Global.getInt(getContentResolver(), "anomaly_config_version", 0);
        String string = Settings.Global.getString(getContentResolver(), "anomaly_config");
        Log.i("AnomalyConfigJobService", "CurrentVersion: " + i + " new version: " + i2);
        if (i2 > i) {
            try {
                statsManager.removeConfig(1L);
            } catch (StatsManager.StatsUnavailableException e) {
                Log.i("AnomalyConfigJobService", "When updating anomaly config, failed to first remove the old config 1", e);
            }
            if (!TextUtils.isEmpty(string)) {
                try {
                    statsManager.addConfig(1L, Base64.decode(string, 0));
                    Log.i("AnomalyConfigJobService", "Upload the anomaly config. configKey: 1");
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("anomaly_config_version", i2);
                    edit.commit();
                } catch (StatsManager.StatsUnavailableException e2) {
                    Log.i("AnomalyConfigJobService", "Upload of anomaly config failed for configKey 1", e2);
                } catch (IllegalArgumentException e3) {
                    Log.e("AnomalyConfigJobService", "Anomaly raw config is in wrong format", e3);
                }
            }
        }
    }
}
