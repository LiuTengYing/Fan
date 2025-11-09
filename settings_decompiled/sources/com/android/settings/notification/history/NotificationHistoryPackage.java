package com.android.settings.notification.history;

import android.app.NotificationHistory;
import android.graphics.drawable.Drawable;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
/* loaded from: classes.dex */
public class NotificationHistoryPackage {
    Drawable icon;
    CharSequence label;
    TreeSet<NotificationHistory.HistoricalNotification> notifications = new TreeSet<>(new Comparator() { // from class: com.android.settings.notification.history.NotificationHistoryPackage$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$new$0;
            lambda$new$0 = NotificationHistoryPackage.lambda$new$0((NotificationHistory.HistoricalNotification) obj, (NotificationHistory.HistoricalNotification) obj2);
            return lambda$new$0;
        }
    });
    String pkgName;
    int uid;

    public NotificationHistoryPackage(String str, int i) {
        this.pkgName = str;
        this.uid = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$0(NotificationHistory.HistoricalNotification historicalNotification, NotificationHistory.HistoricalNotification historicalNotification2) {
        return Long.compare(historicalNotification2.getPostedTimeMs(), historicalNotification.getPostedTimeMs());
    }

    public long getMostRecent() {
        if (this.notifications.isEmpty()) {
            return 0L;
        }
        return this.notifications.first().getPostedTimeMs();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NotificationHistoryPackage notificationHistoryPackage = (NotificationHistoryPackage) obj;
        return this.uid == notificationHistoryPackage.uid && Objects.equals(this.pkgName, notificationHistoryPackage.pkgName) && Objects.equals(this.notifications, notificationHistoryPackage.notifications) && Objects.equals(this.label, notificationHistoryPackage.label) && Objects.equals(this.icon, notificationHistoryPackage.icon);
    }

    public int hashCode() {
        return Objects.hash(this.pkgName, Integer.valueOf(this.uid), this.notifications, this.label, this.icon);
    }
}
