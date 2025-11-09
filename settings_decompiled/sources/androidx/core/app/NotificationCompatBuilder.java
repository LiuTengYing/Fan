package androidx.core.app;

import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import androidx.core.graphics.drawable.IconCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
class NotificationCompatBuilder {
    private RemoteViews mBigContentView;
    private final Notification.Builder mBuilder;
    private final NotificationCompat$Builder mBuilderCompat;
    private RemoteViews mContentView;
    private final Context mContext;
    private int mGroupAlertBehavior;
    private RemoteViews mHeadsUpContentView;
    private final List<Bundle> mActionExtrasList = new ArrayList();
    private final Bundle mExtras = new Bundle();

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationCompatBuilder(NotificationCompat$Builder notificationCompat$Builder) {
        this.mBuilderCompat = notificationCompat$Builder;
        this.mContext = notificationCompat$Builder.mContext;
        Notification.Builder builder = new Notification.Builder(notificationCompat$Builder.mContext, notificationCompat$Builder.mChannelId);
        this.mBuilder = builder;
        Notification notification = notificationCompat$Builder.mNotification;
        builder.setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, notificationCompat$Builder.mTickerView).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS).setOngoing((notification.flags & 2) != 0).setOnlyAlertOnce((notification.flags & 8) != 0).setAutoCancel((notification.flags & 16) != 0).setDefaults(notification.defaults).setContentTitle(notificationCompat$Builder.mContentTitle).setContentText(notificationCompat$Builder.mContentText).setContentInfo(notificationCompat$Builder.mContentInfo).setContentIntent(notificationCompat$Builder.mContentIntent).setDeleteIntent(notification.deleteIntent).setFullScreenIntent(notificationCompat$Builder.mFullScreenIntent, (notification.flags & 128) != 0).setLargeIcon(notificationCompat$Builder.mLargeIcon).setNumber(notificationCompat$Builder.mNumber).setProgress(notificationCompat$Builder.mProgressMax, notificationCompat$Builder.mProgress, notificationCompat$Builder.mProgressIndeterminate);
        builder.setSubText(notificationCompat$Builder.mSubText).setUsesChronometer(notificationCompat$Builder.mUseChronometer).setPriority(notificationCompat$Builder.mPriority);
        Iterator<NotificationCompat$Action> it = notificationCompat$Builder.mActions.iterator();
        while (it.hasNext()) {
            addAction(it.next());
        }
        Bundle bundle = notificationCompat$Builder.mExtras;
        if (bundle != null) {
            this.mExtras.putAll(bundle);
        }
        this.mContentView = notificationCompat$Builder.mContentView;
        this.mBigContentView = notificationCompat$Builder.mBigContentView;
        this.mBuilder.setShowWhen(notificationCompat$Builder.mShowWhen);
        this.mBuilder.setLocalOnly(notificationCompat$Builder.mLocalOnly).setGroup(notificationCompat$Builder.mGroupKey).setGroupSummary(notificationCompat$Builder.mGroupSummary).setSortKey(notificationCompat$Builder.mSortKey);
        this.mGroupAlertBehavior = notificationCompat$Builder.mGroupAlertBehavior;
        this.mBuilder.setCategory(notificationCompat$Builder.mCategory).setColor(notificationCompat$Builder.mColor).setVisibility(notificationCompat$Builder.mVisibility).setPublicVersion(notificationCompat$Builder.mPublicVersion).setSound(notification.sound, notification.audioAttributes);
        ArrayList<String> arrayList = notificationCompat$Builder.mPeople;
        if (arrayList != null && !arrayList.isEmpty()) {
            for (String str : arrayList) {
                this.mBuilder.addPerson(str);
            }
        }
        this.mHeadsUpContentView = notificationCompat$Builder.mHeadsUpContentView;
        if (notificationCompat$Builder.mInvisibleActions.size() > 0) {
            Bundle bundle2 = notificationCompat$Builder.getExtras().getBundle("android.car.EXTENSIONS");
            bundle2 = bundle2 == null ? new Bundle() : bundle2;
            Bundle bundle3 = new Bundle(bundle2);
            Bundle bundle4 = new Bundle();
            for (int i = 0; i < notificationCompat$Builder.mInvisibleActions.size(); i++) {
                bundle4.putBundle(Integer.toString(i), NotificationCompatJellybean.getBundleForAction(notificationCompat$Builder.mInvisibleActions.get(i)));
            }
            bundle2.putBundle("invisible_actions", bundle4);
            bundle3.putBundle("invisible_actions", bundle4);
            notificationCompat$Builder.getExtras().putBundle("android.car.EXTENSIONS", bundle2);
            this.mExtras.putBundle("android.car.EXTENSIONS", bundle3);
        }
        Icon icon = notificationCompat$Builder.mSmallIcon;
        if (icon != null) {
            this.mBuilder.setSmallIcon(icon);
        }
        this.mBuilder.setExtras(notificationCompat$Builder.mExtras).setRemoteInputHistory(notificationCompat$Builder.mRemoteInputHistory);
        RemoteViews remoteViews = notificationCompat$Builder.mContentView;
        if (remoteViews != null) {
            this.mBuilder.setCustomContentView(remoteViews);
        }
        RemoteViews remoteViews2 = notificationCompat$Builder.mBigContentView;
        if (remoteViews2 != null) {
            this.mBuilder.setCustomBigContentView(remoteViews2);
        }
        RemoteViews remoteViews3 = notificationCompat$Builder.mHeadsUpContentView;
        if (remoteViews3 != null) {
            this.mBuilder.setCustomHeadsUpContentView(remoteViews3);
        }
        this.mBuilder.setBadgeIconType(notificationCompat$Builder.mBadgeIcon).setSettingsText(notificationCompat$Builder.mSettingsText).setShortcutId(notificationCompat$Builder.mShortcutId).setTimeoutAfter(notificationCompat$Builder.mTimeout).setGroupAlertBehavior(notificationCompat$Builder.mGroupAlertBehavior);
        if (notificationCompat$Builder.mColorizedSet) {
            this.mBuilder.setColorized(notificationCompat$Builder.mColorized);
        }
        if (!TextUtils.isEmpty(notificationCompat$Builder.mChannelId)) {
            this.mBuilder.setSound(null).setDefaults(0).setLights(0, 0, 0).setVibrate(null);
        }
        Iterator<Person> it2 = notificationCompat$Builder.mPersonList.iterator();
        while (it2.hasNext()) {
            this.mBuilder.addPerson(it2.next().toAndroidPerson());
        }
        this.mBuilder.setAllowSystemGeneratedContextualActions(notificationCompat$Builder.mAllowSystemGeneratedContextualActions);
        this.mBuilder.setBubbleMetadata(NotificationCompat$BubbleMetadata.toPlatform(null));
        int i2 = notificationCompat$Builder.mFgsDeferBehavior;
        if (i2 != 0) {
            this.mBuilder.setForegroundServiceBehavior(i2);
        }
        if (notificationCompat$Builder.mSilent) {
            if (this.mBuilderCompat.mGroupSummary) {
                this.mGroupAlertBehavior = 2;
            } else {
                this.mGroupAlertBehavior = 1;
            }
            this.mBuilder.setVibrate(null);
            this.mBuilder.setSound(null);
            int i3 = notification.defaults & (-2) & (-3);
            notification.defaults = i3;
            this.mBuilder.setDefaults(i3);
            if (TextUtils.isEmpty(this.mBuilderCompat.mGroupKey)) {
                this.mBuilder.setGroup("silent");
            }
            this.mBuilder.setGroupAlertBehavior(this.mGroupAlertBehavior);
        }
    }

    public Notification build() {
        this.mBuilderCompat.getClass();
        Notification buildInternal = buildInternal();
        RemoteViews remoteViews = this.mBuilderCompat.mContentView;
        if (remoteViews != null) {
            buildInternal.contentView = remoteViews;
        }
        return buildInternal;
    }

    private void addAction(NotificationCompat$Action notificationCompat$Action) {
        Bundle bundle;
        IconCompat iconCompat = notificationCompat$Action.getIconCompat();
        Notification.Action.Builder builder = new Notification.Action.Builder(iconCompat != null ? iconCompat.toIcon() : null, notificationCompat$Action.getTitle(), notificationCompat$Action.getActionIntent());
        if (notificationCompat$Action.getRemoteInputs() != null) {
            for (android.app.RemoteInput remoteInput : RemoteInput.fromCompat(notificationCompat$Action.getRemoteInputs())) {
                builder.addRemoteInput(remoteInput);
            }
        }
        if (notificationCompat$Action.getExtras() != null) {
            bundle = new Bundle(notificationCompat$Action.getExtras());
        } else {
            bundle = new Bundle();
        }
        bundle.putBoolean("android.support.allowGeneratedReplies", notificationCompat$Action.getAllowGeneratedReplies());
        builder.setAllowGeneratedReplies(notificationCompat$Action.getAllowGeneratedReplies());
        bundle.putInt("android.support.action.semanticAction", notificationCompat$Action.getSemanticAction());
        builder.setSemanticAction(notificationCompat$Action.getSemanticAction());
        builder.setContextual(notificationCompat$Action.isContextual());
        builder.setAuthenticationRequired(notificationCompat$Action.isAuthenticationRequired());
        bundle.putBoolean("android.support.action.showsUserInterface", notificationCompat$Action.getShowsUserInterface());
        builder.addExtras(bundle);
        this.mBuilder.addAction(builder.build());
    }

    protected Notification buildInternal() {
        return this.mBuilder.build();
    }
}
