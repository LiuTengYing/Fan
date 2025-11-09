package com.android.settings.development.tare;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.settings.R$array;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settingslib.widget.SettingsSpinnerAdapter;
/* loaded from: classes.dex */
public class DropdownActivity extends Activity {
    private Fragment mAlarmManagerFragment;
    private Fragment mJobSchedulerFragment;
    private Spinner mSpinner;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.tare_dropdown_page);
        int intExtra = getIntent().getIntExtra("policy", 0);
        this.mSpinner = (Spinner) findViewById(R$id.spinner);
        this.mAlarmManagerFragment = new AlarmManagerFragment();
        this.mJobSchedulerFragment = new JobSchedulerFragment();
        String[] stringArray = getResources().getStringArray(R$array.tare_policies);
        SettingsSpinnerAdapter settingsSpinnerAdapter = new SettingsSpinnerAdapter(this);
        settingsSpinnerAdapter.addAll(stringArray);
        this.mSpinner.setAdapter((SpinnerAdapter) settingsSpinnerAdapter);
        this.mSpinner.setSelection(intExtra);
        selectFragment(intExtra);
        this.mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.development.tare.DropdownActivity.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                DropdownActivity.this.selectFragment(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectFragment(int i) {
        if (i == 0) {
            openFragment(this.mAlarmManagerFragment);
        } else if (i == 1) {
            openFragment(this.mJobSchedulerFragment);
        } else {
            openFragment(this.mAlarmManagerFragment);
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(R$id.frame_layout, fragment);
        beginTransaction.commit();
    }
}
