package com.android.settings.sim;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.TaskStackBuilder;
import com.android.settings.R$color;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.network.SubscriptionUtil;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class SimActivationNotifier {
    private final Context mContext;
    private final NotificationManager mNotificationManager;

    public SimActivationNotifier(Context context) {
        this.mContext = context;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mNotificationManager = notificationManager;
        notificationManager.createNotificationChannel(new NotificationChannel("sim_setup", context.getString(R$string.sim_setup_channel_id), 4));
        notificationManager.createNotificationChannel(new NotificationChannel("carrier_switching", context.getString(R$string.sim_switch_channel_id), 4));
    }

    public static void setShowSimSettingsNotification(Context context, boolean z) {
        context.getSharedPreferences("sim_prefs", 0).edit().putBoolean("show_sim_settings_notification", z).apply();
    }

    public static boolean getShowSimSettingsNotification(Context context) {
        return context.getSharedPreferences("sim_prefs", 0).getBoolean("show_sim_settings_notification", false);
    }

    public void sendNetworkConfigNotification() {
        String charSequence;
        SubscriptionInfo activeRemovableSub = getActiveRemovableSub();
        if (activeRemovableSub == null) {
            Log.e("SimActivationNotifier", "No removable subscriptions found. Do not show notification.");
            return;
        }
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(activeRemovableSub, this.mContext);
        if (TextUtils.isEmpty(uniqueSubscriptionDisplayName)) {
            charSequence = this.mContext.getString(R$string.sim_card_label);
        } else {
            charSequence = uniqueSubscriptionDisplayName.toString();
        }
        String string = this.mContext.getString(R$string.post_dsds_reboot_notification_title_with_carrier, charSequence);
        this.mNotificationManager.notify(1, new Notification.Builder(this.mContext, "sim_setup").setContentTitle(string).setContentText(this.mContext.getString(R$string.post_dsds_reboot_notification_text)).setContentIntent(TaskStackBuilder.create(this.mContext).addNextIntent(new Intent(this.mContext, Settings.MobileNetworkListActivity.class)).getPendingIntent(0, 201326592)).setSmallIcon(R$drawable.ic_sim_alert).setAutoCancel(true).build());
    }

    public void sendSwitchedToRemovableSlotNotification() {
        String string;
        String activeCarrierName = getActiveCarrierName();
        PendingIntent pendingIntent = TaskStackBuilder.create(this.mContext).addNextIntent(new Intent(this.mContext, Settings.MobileNetworkListActivity.class)).getPendingIntent(0, 201326592);
        if (TextUtils.isEmpty(activeCarrierName)) {
            string = this.mContext.getString(R$string.switch_to_removable_notification_no_carrier_name);
        } else {
            string = this.mContext.getString(R$string.switch_to_removable_notification, activeCarrierName);
        }
        this.mNotificationManager.notify(2, new Notification.Builder(this.mContext, "carrier_switching").setContentTitle(string).setContentText(this.mContext.getString(R$string.network_changed_notification_text)).setContentIntent(pendingIntent).setSmallIcon(R$drawable.ic_sim_alert).setColor(this.mContext.getResources().getColor(R$color.homepage_generic_icon_background, null)).setAutoCancel(true).build());
    }

    public void sendEnableDsdsNotification() {
        Intent intent = new Intent(this.mContext, Settings.MobileNetworkListActivity.class);
        this.mNotificationManager.notify(1, new Notification.Builder(this.mContext, "sim_setup").setContentTitle(this.mContext.getString(R$string.dsds_notification_after_suw_title)).setContentText(this.mContext.getString(R$string.dsds_notification_after_suw_text)).setContentIntent(TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(intent).addNextIntent(new Intent(this.mContext, DsdsDialogActivity.class)).getPendingIntent(0, 201326592)).setSmallIcon(R$drawable.ic_sim_alert).setAutoCancel(true).build());
    }

    private SubscriptionInfo getActiveRemovableSub() {
        return SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).stream().filter(new Predicate() { // from class: com.android.settings.sim.SimActivationNotifier$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getActiveRemovableSub$0;
                lambda$getActiveRemovableSub$0 = SimActivationNotifier.lambda$getActiveRemovableSub$0((SubscriptionInfo) obj);
                return lambda$getActiveRemovableSub$0;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getActiveRemovableSub$0(SubscriptionInfo subscriptionInfo) {
        return !subscriptionInfo.isEmbedded();
    }

    private String getActiveCarrierName() {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        String simOperatorName = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).getSimOperatorName();
        if (carrierConfigManager == null || carrierConfigManager.getConfig() == null) {
            return simOperatorName;
        }
        return (carrierConfigManager.getConfig().getBoolean("carrier_name_override_bool") || TextUtils.isEmpty(simOperatorName)) ? carrierConfigManager.getConfig().getString("carrier_name_string") : simOperatorName;
    }
}
