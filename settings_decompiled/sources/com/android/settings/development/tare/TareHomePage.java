package com.android.settings.development.tare;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class TareHomePage extends Activity {
    private TextView mAlarmManagerView;
    private ConfigObserver mConfigObserver;
    private TextView mJobSchedulerView;
    private Switch mOnSwitch;
    private Button mRevButton;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.tare_homepage);
        this.mOnSwitch = (Switch) findViewById(R$id.on_switch);
        this.mRevButton = (Button) findViewById(R$id.revert_button);
        this.mAlarmManagerView = (TextView) findViewById(R$id.alarmmanager);
        this.mJobSchedulerView = (TextView) findViewById(R$id.jobscheduler);
        this.mConfigObserver = new ConfigObserver(new Handler(Looper.getMainLooper()));
        this.mOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.development.tare.TareHomePage.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (TareHomePage.this.mConfigObserver.mEnableTareSetting != -1 || z) {
                    Settings.Global.putInt(TareHomePage.this.getContentResolver(), "enable_tare", z ? 1 : 0);
                }
            }
        });
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mConfigObserver.start();
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.mConfigObserver.stop();
        super.onPause();
    }

    public void revertSettings(View view) {
        Toast.makeText(this, R$string.tare_settings_reverted_toast, 1).show();
        Settings.Global.putString(getApplicationContext().getContentResolver(), "enable_tare", null);
        Settings.Global.putString(getApplicationContext().getContentResolver(), "tare_alarm_manager_constants", null);
        Settings.Global.putString(getApplicationContext().getContentResolver(), "tare_job_scheduler_constants", null);
    }

    public void launchAlarmManagerPage(View view) {
        Intent intent = new Intent(getApplicationContext(), DropdownActivity.class);
        intent.putExtra("policy", 0);
        startActivity(intent);
    }

    public void launchJobSchedulerPage(View view) {
        Intent intent = new Intent(getApplicationContext(), DropdownActivity.class);
        intent.putExtra("policy", 1);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnabled(boolean z) {
        this.mRevButton.setEnabled(z);
        this.mAlarmManagerView.setEnabled(z);
        this.mJobSchedulerView.setEnabled(z);
        this.mOnSwitch.setChecked(z);
    }

    /* loaded from: classes.dex */
    private class ConfigObserver extends ContentObserver {
        private int mEnableTareSetting;

        ConfigObserver(Handler handler) {
            super(handler);
        }

        public void start() {
            TareHomePage.this.getContentResolver().registerContentObserver(Settings.Global.getUriFor("enable_tare"), false, this);
            processEnableTareChange();
        }

        public void stop() {
            TareHomePage.this.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            processEnableTareChange();
        }

        private void processEnableTareChange() {
            String string = Settings.Global.getString(TareHomePage.this.getContentResolver(), "enable_tare");
            if (string == null) {
                this.mEnableTareSetting = -1;
            } else {
                try {
                    this.mEnableTareSetting = Integer.parseInt(string);
                } catch (NumberFormatException unused) {
                    this.mEnableTareSetting = 0;
                }
            }
            TareHomePage.this.setEnabled(this.mEnableTareSetting == 1);
        }
    }
}
