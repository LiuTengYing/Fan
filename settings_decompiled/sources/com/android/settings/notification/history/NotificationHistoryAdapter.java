package com.android.settings.notification.history;

import android.app.INotificationManager;
import android.app.NotificationHistory;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.notification.history.NotificationHistoryActivity;
import com.android.settings.notification.history.NotificationHistoryRecyclerView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryViewHolder> implements NotificationHistoryRecyclerView.OnItemSwipeDeleteListener {
    private static String TAG = "NotiHistoryAdapter";
    private OnItemDeletedListener mListener;
    private INotificationManager mNm;
    private UiEventLogger mUiEventLogger;
    private List<NotificationHistory.HistoricalNotification> mValues = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnItemDeletedListener {
        void onItemDeleted(int i);
    }

    public NotificationHistoryAdapter(INotificationManager iNotificationManager, NotificationHistoryRecyclerView notificationHistoryRecyclerView, OnItemDeletedListener onItemDeletedListener, UiEventLogger uiEventLogger) {
        setHasStableIds(true);
        notificationHistoryRecyclerView.setOnItemSwipeDeleteListener(this);
        this.mNm = iNotificationManager;
        this.mListener = onItemDeletedListener;
        this.mUiEventLogger = uiEventLogger;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public NotificationHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new NotificationHistoryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.notification_history_log_row, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return this.mValues.get(i).hashCode();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final NotificationHistoryViewHolder notificationHistoryViewHolder, final int i) {
        final NotificationHistory.HistoricalNotification historicalNotification = this.mValues.get(i);
        notificationHistoryViewHolder.setTitle(historicalNotification.getTitle());
        notificationHistoryViewHolder.setSummary(historicalNotification.getText());
        notificationHistoryViewHolder.setPostedTime(historicalNotification.getPostedTimeMs());
        final View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.settings.notification.history.NotificationHistoryAdapter$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationHistoryAdapter.this.lambda$onBindViewHolder$0(historicalNotification, i, notificationHistoryViewHolder, view);
            }
        };
        notificationHistoryViewHolder.itemView.setOnClickListener(onClickListener);
        notificationHistoryViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.settings.notification.history.NotificationHistoryAdapter$$ExternalSyntheticLambda2
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                boolean lambda$onBindViewHolder$1;
                lambda$onBindViewHolder$1 = NotificationHistoryAdapter.lambda$onBindViewHolder$1(onClickListener, view);
                return lambda$onBindViewHolder$1;
            }
        });
        notificationHistoryViewHolder.itemView.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.notification.history.NotificationHistoryAdapter.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, view.getResources().getText(R$string.notification_history_view_settings)));
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
            }

            @Override // android.view.View.AccessibilityDelegate
            public boolean performAccessibilityAction(View view, int i2, Bundle bundle) {
                super.performAccessibilityAction(view, i2, bundle);
                if (i2 == AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS.getId()) {
                    NotificationHistoryAdapter.this.onItemSwipeDeleted(NotificationHistoryAdapter.this.mValues.indexOf(historicalNotification));
                    return true;
                }
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(NotificationHistory.HistoricalNotification historicalNotification, int i, NotificationHistoryViewHolder notificationHistoryViewHolder, View view) {
        this.mUiEventLogger.logWithPosition(NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_OLDER_ITEM_CLICK, historicalNotification.getUid(), historicalNotification.getPackage(), i);
        notificationHistoryViewHolder.itemView.getContext().startActivityAsUser(new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS").putExtra("android.provider.extra.APP_PACKAGE", historicalNotification.getPackage()).putExtra("android.provider.extra.CHANNEL_ID", historicalNotification.getChannelId()).putExtra("android.provider.extra.CONVERSATION_ID", historicalNotification.getConversationId()), UserHandle.of(historicalNotification.getUserId()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onBindViewHolder$1(View.OnClickListener onClickListener, View view) {
        onClickListener.onClick(view);
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mValues.size();
    }

    public void onRebuildComplete(List<NotificationHistory.HistoricalNotification> list) {
        this.mValues = list;
        list.sort(new Comparator() { // from class: com.android.settings.notification.history.NotificationHistoryAdapter$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$onRebuildComplete$2;
                lambda$onRebuildComplete$2 = NotificationHistoryAdapter.lambda$onRebuildComplete$2((NotificationHistory.HistoricalNotification) obj, (NotificationHistory.HistoricalNotification) obj2);
                return lambda$onRebuildComplete$2;
            }
        });
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$onRebuildComplete$2(NotificationHistory.HistoricalNotification historicalNotification, NotificationHistory.HistoricalNotification historicalNotification2) {
        return Long.compare(historicalNotification2.getPostedTimeMs(), historicalNotification.getPostedTimeMs());
    }

    @Override // com.android.settings.notification.history.NotificationHistoryRecyclerView.OnItemSwipeDeleteListener
    public void onItemSwipeDeleted(int i) {
        if (i > this.mValues.size() - 1) {
            String str = TAG;
            Slog.d(str, "Tried to swipe element out of list: position: " + i + " size? " + this.mValues.size());
            return;
        }
        NotificationHistory.HistoricalNotification remove = this.mValues.remove(i);
        if (remove != null) {
            try {
                this.mNm.deleteNotificationHistoryItem(remove.getPackage(), remove.getUid(), remove.getPostedTimeMs());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to delete item", e);
            }
            this.mUiEventLogger.logWithPosition(NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_OLDER_ITEM_DELETE, remove.getUid(), remove.getPackage(), i);
        }
        this.mListener.onItemDeleted(this.mValues.size());
        notifyItemRemoved(i);
    }
}
