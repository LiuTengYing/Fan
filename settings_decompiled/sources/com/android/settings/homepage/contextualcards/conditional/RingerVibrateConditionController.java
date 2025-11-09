package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;
/* loaded from: classes.dex */
public class RingerVibrateConditionController extends AbnormalRingerConditionController {
    static final int ID = Objects.hash("RingerVibrateConditionController");
    private final Context mAppContext;

    public RingerVibrateConditionController(Context context, ConditionManager conditionManager) {
        super(context, conditionManager);
        this.mAppContext = context;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mAudioManager.getRingerModeInternal() == 1;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(1369).setActionText(this.mAppContext.getText(R$string.condition_device_muted_action_turn_on_sound));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = R$string.condition_device_vibrate_title;
        sb.append((Object) context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(R$string.condition_device_vibrate_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(R$drawable.ic_volume_ringer_vibrate)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }
}
