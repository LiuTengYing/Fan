package com.android.settings.notification.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
/* loaded from: classes.dex */
public class ChannelPanelActivity extends FragmentActivity {
    final Bundle mBundle = new Bundle();
    NotificationSettings mPanelFragment;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!getIntent().hasExtra("android.provider.extra.CHANNEL_FILTER_LIST")) {
            launchFullSettings();
        }
        getApplicationContext().getTheme().rebase();
        createOrUpdatePanel();
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        createOrUpdatePanel();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    private void launchFullSettings() {
        Bundle extras = getIntent().getExtras();
        extras.remove("android.provider.extra.CHANNEL_FILTER_LIST");
        startActivity(new SubSettingLauncher(this).setDestination(ChannelNotificationSettings.class.getName()).setExtras(extras).setSourceMetricsCategory(265).toIntent());
        finish();
    }

    private void createOrUpdatePanel() {
        NotificationSettings channelNotificationSettings;
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("ChannelPanelActivity", "Null intent, closing Panel Activity");
            finish();
            return;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        setContentView(R$layout.notification_channel_panel);
        Window window = getWindow();
        window.setGravity(80);
        window.setLayout(-1, -2);
        findViewById(R$id.done).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.app.ChannelPanelActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChannelPanelActivity.this.lambda$createOrUpdatePanel$0(view);
            }
        });
        findViewById(R$id.see_more).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.app.ChannelPanelActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChannelPanelActivity.this.lambda$createOrUpdatePanel$1(view);
            }
        });
        if (intent.hasExtra("android.provider.extra.CONVERSATION_ID")) {
            channelNotificationSettings = new ConversationNotificationSettings();
        } else {
            channelNotificationSettings = new ChannelNotificationSettings();
        }
        this.mPanelFragment = channelNotificationSettings;
        channelNotificationSettings.setArguments(new Bundle(this.mBundle));
        supportFragmentManager.beginTransaction().replace(16908351, this.mPanelFragment).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOrUpdatePanel$0(View view) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOrUpdatePanel$1(View view) {
        launchFullSettings();
    }
}
