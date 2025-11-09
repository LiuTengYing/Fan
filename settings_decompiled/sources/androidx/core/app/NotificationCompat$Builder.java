package androidx.core.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.widget.RemoteViews;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class NotificationCompat$Builder {
    boolean mAllowSystemGeneratedContextualActions;
    RemoteViews mBigContentView;
    String mCategory;
    String mChannelId;
    boolean mColorized;
    boolean mColorizedSet;
    CharSequence mContentInfo;
    PendingIntent mContentIntent;
    CharSequence mContentText;
    CharSequence mContentTitle;
    RemoteViews mContentView;
    public Context mContext;
    Bundle mExtras;
    PendingIntent mFullScreenIntent;
    String mGroupKey;
    boolean mGroupSummary;
    RemoteViews mHeadsUpContentView;
    Bitmap mLargeIcon;
    Notification mNotification;
    int mNumber;
    @Deprecated
    public ArrayList<String> mPeople;
    int mPriority;
    int mProgress;
    boolean mProgressIndeterminate;
    int mProgressMax;
    Notification mPublicVersion;
    CharSequence[] mRemoteInputHistory;
    CharSequence mSettingsText;
    String mShortcutId;
    boolean mSilent;
    Icon mSmallIcon;
    String mSortKey;
    CharSequence mSubText;
    RemoteViews mTickerView;
    long mTimeout;
    boolean mUseChronometer;
    public ArrayList<NotificationCompat$Action> mActions = new ArrayList<>();
    public ArrayList<Person> mPersonList = new ArrayList<>();
    ArrayList<NotificationCompat$Action> mInvisibleActions = new ArrayList<>();
    boolean mShowWhen = true;
    boolean mLocalOnly = false;
    int mColor = 0;
    int mVisibility = 0;
    int mBadgeIcon = 0;
    int mGroupAlertBehavior = 0;
    int mFgsDeferBehavior = 0;

    public NotificationCompat$Builder(Context context, String str) {
        Notification notification = new Notification();
        this.mNotification = notification;
        this.mContext = context;
        this.mChannelId = str;
        notification.when = System.currentTimeMillis();
        this.mNotification.audioStreamType = -1;
        this.mPriority = 0;
        this.mPeople = new ArrayList<>();
        this.mAllowSystemGeneratedContextualActions = true;
    }

    public NotificationCompat$Builder setSmallIcon(int i) {
        this.mNotification.icon = i;
        return this;
    }

    public NotificationCompat$Builder setContentTitle(CharSequence charSequence) {
        this.mContentTitle = limitCharSequenceLength(charSequence);
        return this;
    }

    public NotificationCompat$Builder setContentText(CharSequence charSequence) {
        this.mContentText = limitCharSequenceLength(charSequence);
        return this;
    }

    public NotificationCompat$Builder setContentIntent(PendingIntent pendingIntent) {
        this.mContentIntent = pendingIntent;
        return this;
    }

    public NotificationCompat$Builder setTicker(CharSequence charSequence) {
        this.mNotification.tickerText = limitCharSequenceLength(charSequence);
        return this;
    }

    public NotificationCompat$Builder setOngoing(boolean z) {
        setFlag(2, z);
        return this;
    }

    public NotificationCompat$Builder setLocalOnly(boolean z) {
        this.mLocalOnly = z;
        return this;
    }

    public NotificationCompat$Builder setDefaults(int i) {
        Notification notification = this.mNotification;
        notification.defaults = i;
        if ((i & 4) != 0) {
            notification.flags |= 1;
        }
        return this;
    }

    private void setFlag(int i, boolean z) {
        if (z) {
            Notification notification = this.mNotification;
            notification.flags = i | notification.flags;
            return;
        }
        Notification notification2 = this.mNotification;
        notification2.flags = (~i) & notification2.flags;
    }

    public Bundle getExtras() {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        return this.mExtras;
    }

    public NotificationCompat$Builder addAction(NotificationCompat$Action notificationCompat$Action) {
        if (notificationCompat$Action != null) {
            this.mActions.add(notificationCompat$Action);
        }
        return this;
    }

    public NotificationCompat$Builder setColor(int i) {
        this.mColor = i;
        return this;
    }

    public Notification build() {
        return new NotificationCompatBuilder(this).build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CharSequence limitCharSequenceLength(CharSequence charSequence) {
        return (charSequence != null && charSequence.length() > 5120) ? charSequence.subSequence(0, 5120) : charSequence;
    }
}
