package com.android.settings.notification.history;

import android.app.NotificationHistory;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Slog;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.history.HistoryLoader;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public class HistoryLoader {
    private final NotificationBackend mBackend;
    private final Context mContext;
    private final PackageManager mPm;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnHistoryLoaderListener {
        void onHistoryLoaded(List<NotificationHistoryPackage> list);
    }

    public HistoryLoader(Context context, NotificationBackend notificationBackend, PackageManager packageManager) {
        this.mContext = context;
        this.mBackend = notificationBackend;
        this.mPm = packageManager;
    }

    public void load(final OnHistoryLoaderListener onHistoryLoaderListener) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.notification.history.HistoryLoader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HistoryLoader.this.lambda$load$2(onHistoryLoaderListener);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$2(final OnHistoryLoaderListener onHistoryLoaderListener) {
        try {
            HashMap hashMap = new HashMap();
            NotificationHistory notificationHistory = this.mBackend.getNotificationHistory(this.mContext.getPackageName(), this.mContext.getAttributionTag());
            while (notificationHistory.hasNextNotification()) {
                NotificationHistory.HistoricalNotification nextNotification = notificationHistory.getNextNotification();
                String str = nextNotification.getPackage() + "|" + nextNotification.getUid();
                NotificationHistoryPackage notificationHistoryPackage = (NotificationHistoryPackage) hashMap.getOrDefault(str, new NotificationHistoryPackage(nextNotification.getPackage(), nextNotification.getUid()));
                notificationHistoryPackage.notifications.add(nextNotification);
                hashMap.put(str, notificationHistoryPackage);
            }
            final ArrayList<NotificationHistoryPackage> arrayList = new ArrayList(hashMap.values());
            Collections.sort(arrayList, new Comparator() { // from class: com.android.settings.notification.history.HistoryLoader$$ExternalSyntheticLambda1
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$load$0;
                    lambda$load$0 = HistoryLoader.lambda$load$0((NotificationHistoryPackage) obj, (NotificationHistoryPackage) obj2);
                    return lambda$load$0;
                }
            });
            for (NotificationHistoryPackage notificationHistoryPackage2 : arrayList) {
                try {
                    ApplicationInfo applicationInfoAsUser = this.mPm.getApplicationInfoAsUser(notificationHistoryPackage2.pkgName, 795136, UserHandle.getUserId(notificationHistoryPackage2.uid));
                    if (applicationInfoAsUser != null) {
                        notificationHistoryPackage2.label = String.valueOf(this.mPm.getApplicationLabel(applicationInfoAsUser));
                        PackageManager packageManager = this.mPm;
                        notificationHistoryPackage2.icon = packageManager.getUserBadgedIcon(packageManager.getApplicationIcon(applicationInfoAsUser), UserHandle.of(UserHandle.getUserId(notificationHistoryPackage2.uid)));
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    notificationHistoryPackage2.icon = this.mPm.getDefaultActivityIcon();
                }
            }
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.notification.history.HistoryLoader$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    HistoryLoader.OnHistoryLoaderListener.this.onHistoryLoaded(arrayList);
                }
            });
        } catch (Exception e) {
            Slog.e("HistoryLoader", "Error loading history", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$load$0(NotificationHistoryPackage notificationHistoryPackage, NotificationHistoryPackage notificationHistoryPackage2) {
        return Long.compare(notificationHistoryPackage.getMostRecent(), notificationHistoryPackage2.getMostRecent()) * (-1);
    }
}
