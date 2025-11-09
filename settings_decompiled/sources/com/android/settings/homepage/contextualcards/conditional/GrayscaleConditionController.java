package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.net.URISyntaxException;
import java.util.Objects;
/* loaded from: classes.dex */
public class GrayscaleConditionController implements ConditionalCardController {
    private final Context mAppContext;
    private final ColorDisplayManager mColorDisplayManager;
    private final ConditionManager mConditionManager;
    private Intent mIntent;
    private final Receiver mReceiver = new Receiver();
    static final int ID = Objects.hash("GrayscaleConditionController");
    private static final IntentFilter GRAYSCALE_CHANGED_FILTER = new IntentFilter("android.settings.action.GRAYSCALE_CHANGED");

    public GrayscaleConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        try {
            this.mIntent = Intent.parseUri(this.mAppContext.getString(R$string.config_grayscale_settings_intent), 1);
            return this.mColorDisplayManager.isSaturationActivated();
        } catch (URISyntaxException e) {
            Log.w("GrayscaleCondition", "Failure parsing grayscale settings intent, skipping", e);
            return false;
        }
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        this.mAppContext.startActivity(this.mIntent);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mColorDisplayManager.setSaturationLevel(100);
        sendBroadcast();
        this.mConditionManager.onConditionChanged();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(1683).setActionText(this.mAppContext.getText(R$string.condition_turn_off));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = R$string.condition_grayscale_title;
        sb.append((Object) context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(R$string.condition_grayscale_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(R$drawable.ic_gray_scale_24dp)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, GRAYSCALE_CHANGED_FILTER, "android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", null);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    private void sendBroadcast() {
        Intent intent = new Intent("android.settings.action.GRAYSCALE_CHANGED");
        intent.addFlags(16777216);
        this.mAppContext.sendBroadcastAsUser(intent, UserHandle.CURRENT, "android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS");
    }

    /* loaded from: classes.dex */
    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.settings.action.GRAYSCALE_CHANGED".equals(intent.getAction())) {
                GrayscaleConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }
}
