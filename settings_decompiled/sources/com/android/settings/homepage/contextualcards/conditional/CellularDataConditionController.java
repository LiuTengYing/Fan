package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settings.network.GlobalSettingsChangeListener;
import java.util.Objects;
/* loaded from: classes.dex */
public class CellularDataConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("CellularDataConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final GlobalSettingsChangeListener mDefaultDataSubscriptionIdListener;
    private boolean mIsListeningConnectionChange;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.settings.homepage.contextualcards.conditional.CellularDataConditionController.2
        @Override // android.telephony.PhoneStateListener
        public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
            CellularDataConditionController.this.mConditionManager.onConditionChanged();
        }
    };
    private int mSubId;
    private TelephonyManager mTelephonyManager;

    public CellularDataConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        int defaultDataSubscriptionId = getDefaultDataSubscriptionId(context);
        this.mSubId = defaultDataSubscriptionId;
        this.mTelephonyManager = getTelephonyManager(context, defaultDataSubscriptionId);
        this.mDefaultDataSubscriptionIdListener = new GlobalSettingsChangeListener(context, "multi_sim_data_call") { // from class: com.android.settings.homepage.contextualcards.conditional.CellularDataConditionController.1
            @Override // com.android.settings.network.GlobalSettingsChangeListener
            public void onChanged(String str) {
                CellularDataConditionController cellularDataConditionController = CellularDataConditionController.this;
                int defaultDataSubscriptionId2 = cellularDataConditionController.getDefaultDataSubscriptionId(cellularDataConditionController.mAppContext);
                if (defaultDataSubscriptionId2 == CellularDataConditionController.this.mSubId) {
                    return;
                }
                CellularDataConditionController.this.mSubId = defaultDataSubscriptionId2;
                if (CellularDataConditionController.this.mIsListeningConnectionChange) {
                    CellularDataConditionController cellularDataConditionController2 = CellularDataConditionController.this;
                    cellularDataConditionController2.restartPhoneStateListener(cellularDataConditionController2.mAppContext, defaultDataSubscriptionId2);
                }
            }
        };
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        if (this.mTelephonyManager.isDataCapable() && this.mTelephonyManager.getSimState() == 5) {
            return !this.mTelephonyManager.isDataEnabled();
        }
        return false;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent(context, Settings.DataUsageSummaryActivity.class));
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mTelephonyManager.setDataEnabled(true);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(380).setActionText(this.mAppContext.getText(R$string.condition_turn_on));
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAppContext.getPackageName());
        sb.append("/");
        Context context = this.mAppContext;
        int i = R$string.condition_cellular_title;
        sb.append((Object) context.getText(i));
        return actionText.setName(sb.toString()).setTitleText(this.mAppContext.getText(i).toString()).setSummaryText(this.mAppContext.getText(R$string.condition_cellular_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(R$drawable.ic_cellular_off)).setViewType(ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH).build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        restartPhoneStateListener(this.mAppContext, this.mSubId);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        stopPhoneStateListener();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDefaultDataSubscriptionId(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }

    private TelephonyManager getTelephonyManager(Context context, int i) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }

    private void stopPhoneStateListener() {
        this.mIsListeningConnectionChange = false;
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartPhoneStateListener(Context context, int i) {
        stopPhoneStateListener();
        this.mIsListeningConnectionChange = true;
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            this.mTelephonyManager = getTelephonyManager(context, i);
        }
        this.mTelephonyManager.listen(this.mPhoneStateListener, 4096);
    }
}
