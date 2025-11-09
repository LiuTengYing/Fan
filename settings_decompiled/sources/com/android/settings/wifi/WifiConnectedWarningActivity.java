package com.android.settings.wifi;

import android.app.Activity;
import android.net.wifi.UniWifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class WifiConnectedWarningActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.wifi_connected_waring_dialog);
        getWindow().setCloseOnTouchOutside(false);
        setTitle(R$string.network_disconnect_title);
        ((CheckBox) findViewById(R$id.do_not_prompt)).setOnCheckedChangeListener(this);
        new UpdateHandler().sendEmptyMessageDelayed(0, 5000L);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        UniWifiManager.getInstance().setShowNotificationEnabled(!compoundButton.isChecked());
    }

    /* loaded from: classes.dex */
    private class UpdateHandler extends Handler {
        private UpdateHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 0) {
                return;
            }
            WifiConnectedWarningActivity.this.finish();
        }
    }
}
