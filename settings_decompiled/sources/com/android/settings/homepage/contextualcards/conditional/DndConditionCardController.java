package com.android.settings.homepage.contextualcards.conditional;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.ZenModeConfig;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settings.notification.zen.ZenModeSettings;
import java.util.Objects;
/* loaded from: classes.dex */
public class DndConditionCardController implements ConditionalCardController {
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final NotificationManager mNotificationManager;
    private final Receiver mReceiver = new Receiver();
    static final int ID = Objects.hash("DndConditionCardController");
    static final IntentFilter DND_FILTER = new IntentFilter("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL");

    public DndConditionCardController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mNotificationManager.getZenMode() != 0;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, DND_FILTER);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        new SubSettingLauncher(context).setDestination(ZenModeSettings.class.getName()).setSourceMetricsCategory(1502).setTitleRes(R$string.zen_mode_settings_title).launch();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mNotificationManager.setZenMode(0, null, "DndCondition");
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(381).setActionText(this.mAppContext.getText(R$string.condition_turn_off));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = R$string.condition_zen_title;
        sb.append((Object) context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(getSummary()).setIconDrawable(this.mAppContext.getDrawable(R$drawable.ic_do_not_disturb_on_24dp)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }

    /* loaded from: classes.dex */
    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL".equals(intent.getAction())) {
                DndConditionCardController.this.mConditionManager.onConditionChanged();
            }
        }
    }

    private String getSummary() {
        if (ZenModeConfig.areAllZenBehaviorSoundsMuted(this.mNotificationManager.getZenModeConfig())) {
            return this.mAppContext.getText(R$string.condition_zen_summary_phone_muted).toString();
        }
        return this.mAppContext.getText(R$string.condition_zen_summary_with_exceptions).toString();
    }
}
