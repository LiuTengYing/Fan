package com.android.settings.bluetooth;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public final class DevicePickerActivity extends FragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        setContentView(R$layout.bluetooth_device_picker);
    }
}
