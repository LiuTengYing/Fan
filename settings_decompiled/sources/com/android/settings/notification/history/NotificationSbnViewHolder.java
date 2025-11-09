package com.android.settings.notification.history;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import android.view.View;
import android.widget.DateTimeView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.notification.history.NotificationHistoryActivity;
/* loaded from: classes.dex */
public class NotificationSbnViewHolder extends RecyclerView.ViewHolder {
    private final View mDivider;
    private final ImageView mIcon;
    private final TextView mPkgName;
    private final ImageView mProfileBadge;
    private final TextView mSummary;
    private final DateTimeView mTime;
    private final TextView mTitle;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationSbnViewHolder(View view) {
        super(view);
        this.mPkgName = (TextView) view.findViewById(R$id.pkgname);
        this.mIcon = (ImageView) view.findViewById(R$id.icon);
        this.mTime = view.findViewById(R$id.timestamp);
        this.mTitle = (TextView) view.findViewById(R$id.title);
        this.mSummary = (TextView) view.findViewById(R$id.text);
        this.mProfileBadge = (ImageView) view.findViewById(R$id.profile_badge);
        this.mDivider = view.findViewById(R$id.divider);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSummary(CharSequence charSequence) {
        this.mSummary.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mSummary.setText(charSequence);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTitle(CharSequence charSequence) {
        this.mTitle.setText(charSequence);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIcon(Drawable drawable) {
        this.mIcon.setImageDrawable(drawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIconBackground(Drawable drawable) {
        this.mIcon.setBackground(drawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPackageLabel(String str) {
        this.mPkgName.setText(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPostedTime(long j) {
        this.mTime.setTime(j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileBadge(Drawable drawable) {
        this.mProfileBadge.setImageDrawable(drawable);
        this.mProfileBadge.setVisibility(drawable != null ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDividerVisible(boolean z) {
        this.mDivider.setVisibility(z ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addOnClick(final int i, final String str, final int i2, final int i3, final PendingIntent pendingIntent, final InstanceId instanceId, final boolean z, final UiEventLogger uiEventLogger) {
        final Intent launchIntentForPackage = this.itemView.getContext().getPackageManager().getLaunchIntentForPackage(str);
        final boolean z2 = pendingIntent != null && pendingIntent.isActivity();
        if (z2 || launchIntentForPackage != null) {
            this.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.history.NotificationSbnViewHolder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    NotificationSbnViewHolder.this.lambda$addOnClick$0(uiEventLogger, z, i2, str, instanceId, i, pendingIntent, z2, launchIntentForPackage, i3, view);
                }
            });
            ViewCompat.setAccessibilityDelegate(this.itemView, new AccessibilityDelegateCompat() { // from class: com.android.settings.notification.history.NotificationSbnViewHolder.1
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(16, view.getResources().getText(R$string.notification_history_open_notification)));
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addOnClick$0(UiEventLogger uiEventLogger, boolean z, int i, String str, InstanceId instanceId, int i2, PendingIntent pendingIntent, boolean z2, Intent intent, int i3, View view) {
        NotificationHistoryActivity.NotificationHistoryEvent notificationHistoryEvent;
        if (z) {
            notificationHistoryEvent = NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_SNOOZED_ITEM_CLICK;
        } else {
            notificationHistoryEvent = NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_RECENT_ITEM_CLICK;
        }
        uiEventLogger.logWithInstanceIdAndPosition(notificationHistoryEvent, i, str, instanceId, i2);
        if (pendingIntent != null && z2) {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Slog.e("SbnViewHolder", "Could not launch", e);
            }
        } else if (intent != null) {
            intent.addFlags(268435456);
            try {
                this.itemView.getContext().startActivityAsUser(intent, UserHandle.of(i3));
            } catch (ActivityNotFoundException e2) {
                Slog.e("SbnViewHolder", "no launch activity", e2);
            }
        }
    }
}
