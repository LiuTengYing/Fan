package com.android.settings.applications;

import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.widget.CompoundButton;
import com.android.settings.R$id;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.StringUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class AppStateNotificationBridge extends AppStateBaseBridge {
    private final boolean DEBUG;
    private final String TAG;
    private NotificationBackend mBackend;
    private final Context mContext;
    private IUsageStatsManager mUsageStatsManager;
    protected List<Integer> mUserIds;
    public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_RECENCY = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateNotificationBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            return (notificationsSentState == null || notificationsSentState.lastSent == 0) ? false : true;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_FREQUENCY = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateNotificationBridge.2
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            return (notificationsSentState == null || notificationsSentState.sentCount == 0) ? false : true;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_BLOCKED = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateNotificationBridge.3
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            if (notificationsSentState != null) {
                return notificationsSentState.blocked;
            }
            return false;
        }
    };
    public static final Comparator<ApplicationsState.AppEntry> RECENT_NOTIFICATION_COMPARATOR = new Comparator<ApplicationsState.AppEntry>() { // from class: com.android.settings.applications.AppStateNotificationBridge.4
        @Override // java.util.Comparator
        public int compare(ApplicationsState.AppEntry appEntry, ApplicationsState.AppEntry appEntry2) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            NotificationsSentState notificationsSentState2 = AppStateNotificationBridge.getNotificationsSentState(appEntry2);
            if (notificationsSentState != null || notificationsSentState2 == null) {
                if (notificationsSentState == null || notificationsSentState2 != null) {
                    if (notificationsSentState != null && notificationsSentState2 != null) {
                        long j = notificationsSentState.lastSent;
                        long j2 = notificationsSentState2.lastSent;
                        if (j < j2) {
                            return 1;
                        }
                        if (j > j2) {
                            return -1;
                        }
                    }
                    return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
                }
                return 1;
            }
            return -1;
        }
    };
    public static final Comparator<ApplicationsState.AppEntry> FREQUENCY_NOTIFICATION_COMPARATOR = new Comparator<ApplicationsState.AppEntry>() { // from class: com.android.settings.applications.AppStateNotificationBridge.5
        @Override // java.util.Comparator
        public int compare(ApplicationsState.AppEntry appEntry, ApplicationsState.AppEntry appEntry2) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            NotificationsSentState notificationsSentState2 = AppStateNotificationBridge.getNotificationsSentState(appEntry2);
            if (notificationsSentState != null || notificationsSentState2 == null) {
                if (notificationsSentState == null || notificationsSentState2 != null) {
                    if (notificationsSentState != null && notificationsSentState2 != null) {
                        int i = notificationsSentState.sentCount;
                        int i2 = notificationsSentState2.sentCount;
                        if (i < i2) {
                            return 1;
                        }
                        if (i > i2) {
                            return -1;
                        }
                    }
                    return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
                }
                return 1;
            }
            return -1;
        }
    };

    /* loaded from: classes.dex */
    public static class NotificationsSentState {
        public boolean blockable;
        public boolean blocked;
        public int avgSentDaily = 0;
        public int avgSentWeekly = 0;
        public long lastSent = 0;
        public int sentCount = 0;
    }

    public AppStateNotificationBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback, IUsageStatsManager iUsageStatsManager, UserManager userManager, NotificationBackend notificationBackend) {
        super(applicationsState, callback);
        this.TAG = "AppStateNotificationBridge";
        this.DEBUG = false;
        this.mContext = context;
        this.mUsageStatsManager = iUsageStatsManager;
        this.mBackend = notificationBackend;
        ArrayList arrayList = new ArrayList();
        this.mUserIds = arrayList;
        arrayList.add(Integer.valueOf(context.getUserId()));
        int managedProfileId = Utils.getManagedProfileId(userManager, context.getUserId());
        if (managedProfileId != -10000) {
            this.mUserIds.add(Integer.valueOf(managedProfileId));
        }
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        if (allApps == null) {
            return;
        }
        Map<String, NotificationsSentState> aggregatedUsageEvents = getAggregatedUsageEvents();
        Iterator<ApplicationsState.AppEntry> it = allApps.iterator();
        while (it.hasNext()) {
            ApplicationsState.AppEntry next = it.next();
            NotificationsSentState notificationsSentState = aggregatedUsageEvents.get(getKey(UserHandle.getUserId(next.info.uid), next.info.packageName));
            if (notificationsSentState == null) {
                notificationsSentState = new NotificationsSentState();
            }
            calculateAvgSentCounts(notificationsSentState);
            addBlockStatus(next, notificationsSentState);
            next.extraInfo = notificationsSentState;
        }
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        NotificationsSentState aggregatedUsageEvents = getAggregatedUsageEvents(UserHandle.getUserId(appEntry.info.uid), appEntry.info.packageName);
        calculateAvgSentCounts(aggregatedUsageEvents);
        addBlockStatus(appEntry, aggregatedUsageEvents);
        appEntry.extraInfo = aggregatedUsageEvents;
    }

    public static CharSequence getSummary(Context context, NotificationsSentState notificationsSentState, int i) {
        if (i == R$id.sort_order_recent_notification) {
            if (notificationsSentState.lastSent == 0) {
                return context.getString(R$string.notifications_sent_never);
            }
            return StringUtil.formatRelativeTime(context, System.currentTimeMillis() - notificationsSentState.lastSent, true);
        } else if (i == R$id.sort_order_frequent_notification) {
            if (notificationsSentState.avgSentDaily > 0) {
                Resources resources = context.getResources();
                int i2 = R$plurals.notifications_sent_daily;
                int i3 = notificationsSentState.avgSentDaily;
                return resources.getQuantityString(i2, i3, Integer.valueOf(i3));
            }
            Resources resources2 = context.getResources();
            int i4 = R$plurals.notifications_sent_weekly;
            int i5 = notificationsSentState.avgSentWeekly;
            return resources2.getQuantityString(i4, i5, Integer.valueOf(i5));
        } else {
            return "";
        }
    }

    private void addBlockStatus(ApplicationsState.AppEntry appEntry, NotificationsSentState notificationsSentState) {
        if (notificationsSentState != null) {
            NotificationBackend notificationBackend = this.mBackend;
            ApplicationInfo applicationInfo = appEntry.info;
            notificationsSentState.blocked = notificationBackend.getNotificationsBanned(applicationInfo.packageName, applicationInfo.uid);
            notificationsSentState.blockable = this.mBackend.enableSwitch(this.mContext, appEntry.info);
        }
    }

    private void calculateAvgSentCounts(NotificationsSentState notificationsSentState) {
        if (notificationsSentState != null) {
            notificationsSentState.avgSentDaily = Math.round(notificationsSentState.sentCount / 7.0f);
            int i = notificationsSentState.sentCount;
            if (i < 7) {
                notificationsSentState.avgSentWeekly = i;
            }
        }
    }

    protected Map<String, NotificationsSentState> getAggregatedUsageEvents() {
        ArrayMap arrayMap = new ArrayMap();
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - 604800000;
        for (Integer num : this.mUserIds) {
            int intValue = num.intValue();
            UsageEvents usageEvents = null;
            try {
                usageEvents = this.mUsageStatsManager.queryEventsForUser(j, currentTimeMillis, intValue, this.mContext.getPackageName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (usageEvents != null) {
                UsageEvents.Event event = new UsageEvents.Event();
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);
                    NotificationsSentState notificationsSentState = (NotificationsSentState) arrayMap.get(getKey(intValue, event.getPackageName()));
                    if (notificationsSentState == null) {
                        notificationsSentState = new NotificationsSentState();
                        arrayMap.put(getKey(intValue, event.getPackageName()), notificationsSentState);
                    }
                    if (event.getEventType() == 12) {
                        if (event.getTimeStamp() > notificationsSentState.lastSent) {
                            notificationsSentState.lastSent = event.getTimeStamp();
                        }
                        notificationsSentState.sentCount++;
                    }
                }
            }
        }
        return arrayMap;
    }

    protected NotificationsSentState getAggregatedUsageEvents(int i, String str) {
        UsageEvents usageEvents;
        long currentTimeMillis = System.currentTimeMillis();
        NotificationsSentState notificationsSentState = null;
        try {
            usageEvents = this.mUsageStatsManager.queryEventsForPackageForUser(currentTimeMillis - 604800000, currentTimeMillis, i, str, this.mContext.getPackageName());
        } catch (RemoteException e) {
            e.printStackTrace();
            usageEvents = null;
        }
        if (usageEvents != null) {
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == 12) {
                    if (notificationsSentState == null) {
                        notificationsSentState = new NotificationsSentState();
                    }
                    if (event.getTimeStamp() > notificationsSentState.lastSent) {
                        notificationsSentState.lastSent = event.getTimeStamp();
                    }
                    notificationsSentState.sentCount++;
                }
            }
        }
        return notificationsSentState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NotificationsSentState getNotificationsSentState(ApplicationsState.AppEntry appEntry) {
        Object obj;
        if (appEntry == null || (obj = appEntry.extraInfo) == null || !(obj instanceof NotificationsSentState)) {
            return null;
        }
        return (NotificationsSentState) obj;
    }

    protected static String getKey(int i, String str) {
        return i + "|" + str;
    }

    public CompoundButton.OnCheckedChangeListener getSwitchOnCheckedListener(final ApplicationsState.AppEntry appEntry) {
        if (appEntry == null) {
            return null;
        }
        return new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.applications.AppStateNotificationBridge$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AppStateNotificationBridge.this.lambda$getSwitchOnCheckedListener$0(appEntry, compoundButton, z);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getSwitchOnCheckedListener$0(ApplicationsState.AppEntry appEntry, CompoundButton compoundButton, boolean z) {
        NotificationsSentState notificationsSentState = getNotificationsSentState(appEntry);
        if (notificationsSentState == null || notificationsSentState.blocked != z) {
            return;
        }
        NotificationBackend notificationBackend = this.mBackend;
        ApplicationInfo applicationInfo = appEntry.info;
        notificationBackend.setNotificationsEnabledForPackage(applicationInfo.packageName, applicationInfo.uid, z);
        notificationsSentState.blocked = !z;
    }

    public static final boolean enableSwitch(ApplicationsState.AppEntry appEntry) {
        NotificationsSentState notificationsSentState = getNotificationsSentState(appEntry);
        if (notificationsSentState == null) {
            return false;
        }
        return notificationsSentState.blockable;
    }

    public static final boolean checkSwitch(ApplicationsState.AppEntry appEntry) {
        NotificationsSentState notificationsSentState = getNotificationsSentState(appEntry);
        if (notificationsSentState == null) {
            return false;
        }
        return !notificationsSentState.blocked;
    }
}
