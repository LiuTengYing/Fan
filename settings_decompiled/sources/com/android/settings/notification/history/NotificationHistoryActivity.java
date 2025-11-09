package com.android.settings.notification.history;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.INotificationManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.widget.NotificationExpandButton;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.history.HistoryLoader;
import com.android.settings.notification.history.NotificationHistoryAdapter;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.MainSwitchBar;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class NotificationHistoryActivity extends CollapsingToolbarBaseActivity {
    private static String TAG = "NotifHistory";
    private Future mCountdownFuture;
    private CountDownLatch mCountdownLatch;
    private ViewGroup mDismissView;
    private ViewGroup mHistoryEmpty;
    private HistoryLoader mHistoryLoader;
    private ViewGroup mHistoryOff;
    private ViewGroup mHistoryOn;
    private INotificationManager mNm;
    private PackageManager mPm;
    private ViewGroup mSnoozeView;
    private MainSwitchBar mSwitchBar;
    private ViewGroup mTodayView;
    private UserManager mUm;
    private final ViewOutlineProvider mOutlineProvider = new ViewOutlineProvider() { // from class: com.android.settings.notification.history.NotificationHistoryActivity.1
        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            TypedArray obtainStyledAttributes = NotificationHistoryActivity.this.obtainStyledAttributes(new int[]{16844145});
            float dimension = obtainStyledAttributes.getDimension(0, 0.0f);
            obtainStyledAttributes.recycle();
            TypedValue typedValue = new TypedValue();
            NotificationHistoryActivity.this.getTheme().resolveAttribute(16843284, typedValue, true);
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() - NotificationHistoryActivity.this.getDrawable(typedValue.resourceId).getIntrinsicHeight(), dimension);
        }
    };
    private UiEventLogger mUiEventLogger = new UiEventLoggerImpl();
    private HistoryLoader.OnHistoryLoaderListener mOnHistoryLoaderListener = new HistoryLoader.OnHistoryLoaderListener() { // from class: com.android.settings.notification.history.NotificationHistoryActivity$$ExternalSyntheticLambda1
        @Override // com.android.settings.notification.history.HistoryLoader.OnHistoryLoaderListener
        public final void onHistoryLoaded(List list) {
            NotificationHistoryActivity.this.lambda$new$2(list);
        }
    };
    private final OnMainSwitchChangeListener mOnSwitchClickListener = new OnMainSwitchChangeListener() { // from class: com.android.settings.notification.history.NotificationHistoryActivity$$ExternalSyntheticLambda2
        @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
        public final void onSwitchChanged(Switch r1, boolean z) {
            NotificationHistoryActivity.this.lambda$new$5(r1, z);
        }
    };
    private final NotificationListenerService mListener = new NotificationListenerService() { // from class: com.android.settings.notification.history.NotificationHistoryActivity.2
        private RecyclerView mDismissedRv;
        private RecyclerView mSnoozedRv;

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        }

        @Override // android.service.notification.NotificationListenerService
        public void onListenerConnected() {
            StatusBarNotification[] statusBarNotificationArr;
            StatusBarNotification[] statusBarNotificationArr2 = null;
            try {
                statusBarNotificationArr = getSnoozedNotifications();
            } catch (RemoteException | SecurityException unused) {
                statusBarNotificationArr = null;
            }
            try {
                statusBarNotificationArr2 = NotificationHistoryActivity.this.mNm.getHistoricalNotificationsWithAttribution(NotificationHistoryActivity.this.getPackageName(), NotificationHistoryActivity.this.getAttributionTag(), 6, false);
            } catch (RemoteException | SecurityException unused2) {
                Log.d(NotificationHistoryActivity.TAG, "OnPaused called while trying to retrieve notifications");
                ViewGroup viewGroup = NotificationHistoryActivity.this.mSnoozeView;
                int i = R$id.notification_list;
                this.mSnoozedRv = (RecyclerView) viewGroup.findViewById(i);
                this.mSnoozedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
                RecyclerView recyclerView = this.mSnoozedRv;
                NotificationHistoryActivity notificationHistoryActivity = NotificationHistoryActivity.this;
                recyclerView.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity, notificationHistoryActivity.mPm, NotificationHistoryActivity.this.mUm, true, NotificationHistoryActivity.this.mUiEventLogger));
                this.mSnoozedRv.setNestedScrollingEnabled(false);
                if (statusBarNotificationArr != null) {
                }
                NotificationHistoryActivity.this.mSnoozeView.setVisibility(8);
                this.mDismissedRv = (RecyclerView) NotificationHistoryActivity.this.mDismissView.findViewById(i);
                this.mDismissedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
                RecyclerView recyclerView2 = this.mDismissedRv;
                NotificationHistoryActivity notificationHistoryActivity2 = NotificationHistoryActivity.this;
                recyclerView2.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity2, notificationHistoryActivity2.mPm, NotificationHistoryActivity.this.mUm, false, NotificationHistoryActivity.this.mUiEventLogger));
                this.mDismissedRv.setNestedScrollingEnabled(false);
                if (statusBarNotificationArr2 != null) {
                }
                NotificationHistoryActivity.this.mDismissView.setVisibility(8);
                NotificationHistoryActivity.this.mCountdownLatch.countDown();
            }
            ViewGroup viewGroup2 = NotificationHistoryActivity.this.mSnoozeView;
            int i2 = R$id.notification_list;
            this.mSnoozedRv = (RecyclerView) viewGroup2.findViewById(i2);
            this.mSnoozedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
            RecyclerView recyclerView3 = this.mSnoozedRv;
            NotificationHistoryActivity notificationHistoryActivity3 = NotificationHistoryActivity.this;
            recyclerView3.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity3, notificationHistoryActivity3.mPm, NotificationHistoryActivity.this.mUm, true, NotificationHistoryActivity.this.mUiEventLogger));
            this.mSnoozedRv.setNestedScrollingEnabled(false);
            if (statusBarNotificationArr != null || statusBarNotificationArr.length == 0) {
                NotificationHistoryActivity.this.mSnoozeView.setVisibility(8);
            } else {
                ((NotificationSbnAdapter) this.mSnoozedRv.getAdapter()).onRebuildComplete(new ArrayList(Arrays.asList(statusBarNotificationArr)));
            }
            this.mDismissedRv = (RecyclerView) NotificationHistoryActivity.this.mDismissView.findViewById(i2);
            this.mDismissedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
            RecyclerView recyclerView22 = this.mDismissedRv;
            NotificationHistoryActivity notificationHistoryActivity22 = NotificationHistoryActivity.this;
            recyclerView22.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity22, notificationHistoryActivity22.mPm, NotificationHistoryActivity.this.mUm, false, NotificationHistoryActivity.this.mUiEventLogger));
            this.mDismissedRv.setNestedScrollingEnabled(false);
            if (statusBarNotificationArr2 != null || statusBarNotificationArr2.length == 0) {
                NotificationHistoryActivity.this.mDismissView.setVisibility(8);
            } else {
                NotificationHistoryActivity.this.mDismissView.setVisibility(0);
                ((NotificationSbnAdapter) this.mDismissedRv.getAdapter()).onRebuildComplete(new ArrayList(Arrays.asList(statusBarNotificationArr2)));
            }
            NotificationHistoryActivity.this.mCountdownLatch.countDown();
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
            if (i == 18) {
                ((NotificationSbnAdapter) this.mSnoozedRv.getAdapter()).addSbn(statusBarNotification);
                NotificationHistoryActivity.this.mSnoozeView.setVisibility(0);
                return;
            }
            ((NotificationSbnAdapter) this.mDismissedRv.getAdapter()).addSbn(statusBarNotification);
            NotificationHistoryActivity.this.mDismissView.setVisibility(0);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum NotificationHistoryEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_HISTORY_ON(504),
        NOTIFICATION_HISTORY_OFF(505),
        NOTIFICATION_HISTORY_OPEN(506),
        NOTIFICATION_HISTORY_CLOSE(507),
        NOTIFICATION_HISTORY_RECENT_ITEM_CLICK(508),
        NOTIFICATION_HISTORY_SNOOZED_ITEM_CLICK(509),
        NOTIFICATION_HISTORY_PACKAGE_HISTORY_OPEN(510),
        NOTIFICATION_HISTORY_PACKAGE_HISTORY_CLOSE(511),
        NOTIFICATION_HISTORY_OLDER_ITEM_CLICK(512),
        NOTIFICATION_HISTORY_OLDER_ITEM_DELETE(513);
        
        private int mId;

        NotificationHistoryEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(List list) {
        String string;
        int i = 8;
        boolean z = false;
        findViewById(R$id.today_list).setVisibility(list.isEmpty() ? 8 : 0);
        this.mCountdownLatch.countDown();
        this.mTodayView.findViewById(R$id.apps).setClipToOutline(true);
        this.mTodayView.setOutlineProvider(this.mOutlineProvider);
        this.mSnoozeView.setOutlineProvider(this.mOutlineProvider);
        int size = list.size();
        int i2 = 0;
        while (i2 < size) {
            final NotificationHistoryPackage notificationHistoryPackage = (NotificationHistoryPackage) list.get(i2);
            final View inflate = LayoutInflater.from(this).inflate(R$layout.notification_history_app_layout, (ViewGroup) null);
            final View findViewById = inflate.findViewById(R$id.notification_list_wrapper);
            findViewById.setVisibility(i);
            final View findViewById2 = inflate.findViewById(R$id.app_header);
            final NotificationExpandButton findViewById3 = inflate.findViewById(16908982);
            int obtainThemeColor = obtainThemeColor(16842806);
            findViewById3.setDefaultPillColor(obtainThemeColor(16844002));
            findViewById3.setDefaultTextColor(obtainThemeColor);
            findViewById3.setExpanded(z);
            if (findViewById.getVisibility() == 0) {
                string = getString(R$string.condition_expand_hide);
            } else {
                string = getString(R$string.condition_expand_show);
            }
            findViewById2.setStateDescription(string);
            final int i3 = i2;
            findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.history.NotificationHistoryActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    NotificationHistoryActivity.this.lambda$new$0(findViewById, findViewById3, findViewById2, notificationHistoryPackage, i3, view);
                }
            });
            TextView textView = (TextView) inflate.findViewById(R$id.label);
            CharSequence charSequence = notificationHistoryPackage.label;
            if (charSequence == null) {
                charSequence = notificationHistoryPackage.pkgName;
            }
            textView.setText(charSequence);
            textView.setContentDescription(this.mUm.getBadgedLabelForUser(textView.getText(), UserHandle.getUserHandleForUid(notificationHistoryPackage.uid)));
            ((ImageView) inflate.findViewById(R$id.icon)).setImageDrawable(notificationHistoryPackage.icon);
            final TextView textView2 = (TextView) inflate.findViewById(R$id.count);
            textView2.setText(getResources().getQuantityString(R$plurals.notification_history_count, notificationHistoryPackage.notifications.size(), Integer.valueOf(notificationHistoryPackage.notifications.size())));
            NotificationHistoryRecyclerView notificationHistoryRecyclerView = (NotificationHistoryRecyclerView) inflate.findViewById(R$id.notification_list);
            notificationHistoryRecyclerView.setAdapter(new NotificationHistoryAdapter(this.mNm, notificationHistoryRecyclerView, new NotificationHistoryAdapter.OnItemDeletedListener() { // from class: com.android.settings.notification.history.NotificationHistoryActivity$$ExternalSyntheticLambda5
                @Override // com.android.settings.notification.history.NotificationHistoryAdapter.OnItemDeletedListener
                public final void onItemDeleted(int i4) {
                    NotificationHistoryActivity.this.lambda$new$1(textView2, inflate, i4);
                }
            }, this.mUiEventLogger));
            ((NotificationHistoryAdapter) notificationHistoryRecyclerView.getAdapter()).onRebuildComplete(new ArrayList(notificationHistoryPackage.notifications));
            this.mTodayView.addView(inflate);
            i2++;
            z = false;
            i = 8;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, NotificationExpandButton notificationExpandButton, View view2, NotificationHistoryPackage notificationHistoryPackage, int i, View view3) {
        String string;
        NotificationHistoryEvent notificationHistoryEvent;
        view.setVisibility(view.getVisibility() == 0 ? 8 : 0);
        notificationExpandButton.setExpanded(view.getVisibility() == 0);
        if (view.getVisibility() == 0) {
            string = getString(R$string.condition_expand_hide);
        } else {
            string = getString(R$string.condition_expand_show);
        }
        view2.setStateDescription(string);
        view2.sendAccessibilityEvent(32768);
        UiEventLogger uiEventLogger = this.mUiEventLogger;
        if (view.getVisibility() == 0) {
            notificationHistoryEvent = NotificationHistoryEvent.NOTIFICATION_HISTORY_PACKAGE_HISTORY_OPEN;
        } else {
            notificationHistoryEvent = NotificationHistoryEvent.NOTIFICATION_HISTORY_PACKAGE_HISTORY_CLOSE;
        }
        uiEventLogger.logWithPosition(notificationHistoryEvent, notificationHistoryPackage.uid, notificationHistoryPackage.pkgName, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(TextView textView, View view, int i) {
        textView.setText(getResources().getQuantityString(R$plurals.notification_history_count, i, Integer.valueOf(i)));
        if (i == 0) {
            view.setVisibility(8);
        }
    }

    private void configureNotificationList(View view) {
        view.setClipToOutline(true);
        view.setOutlineProvider(this.mOutlineProvider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R$string.notification_history);
        setContentView(R$layout.notification_history);
        this.mTodayView = (ViewGroup) findViewById(R$id.apps);
        this.mSnoozeView = (ViewGroup) findViewById(R$id.snoozed_list);
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.recently_dismissed_list);
        this.mDismissView = viewGroup;
        int i = R$id.notification_list;
        configureNotificationList(viewGroup.findViewById(i));
        configureNotificationList(this.mSnoozeView.findViewById(i));
        this.mHistoryOff = (ViewGroup) findViewById(R$id.history_off);
        this.mHistoryOn = (ViewGroup) findViewById(R$id.history_on);
        this.mHistoryEmpty = (ViewGroup) findViewById(R$id.history_on_empty);
        this.mSwitchBar = (MainSwitchBar) findViewById(R$id.main_switch_bar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mPm = getPackageManager();
        this.mUm = (UserManager) getSystemService(UserManager.class);
        this.mCountdownLatch = new CountDownLatch(2);
        this.mTodayView.removeAllViews();
        HistoryLoader historyLoader = new HistoryLoader(this, new NotificationBackend(), this.mPm);
        this.mHistoryLoader = historyLoader;
        historyLoader.load(this.mOnHistoryLoaderListener);
        this.mNm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        try {
            this.mListener.registerAsSystemService(this, new ComponentName(getPackageName(), getClass().getCanonicalName()), ActivityManager.getCurrentUser());
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot register listener", e);
        }
        bindSwitch();
        this.mCountdownFuture = ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.notification.history.NotificationHistoryActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NotificationHistoryActivity.this.lambda$onResume$4();
            }
        });
        this.mUiEventLogger.log(NotificationHistoryEvent.NOTIFICATION_HISTORY_OPEN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$4() {
        try {
            this.mCountdownLatch.await(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Slog.e(TAG, "timed out waiting for loading", e);
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.notification.history.NotificationHistoryActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                NotificationHistoryActivity.this.lambda$onResume$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$3() {
        if (this.mSwitchBar.isChecked() && findViewById(R$id.today_list).getVisibility() == 8 && this.mSnoozeView.getVisibility() == 8 && this.mDismissView.getVisibility() == 8) {
            this.mHistoryOn.setVisibility(8);
            this.mHistoryEmpty.setVisibility(0);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        try {
            this.mListener.unregisterAsSystemService();
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot unregister listener", e);
        }
        this.mUiEventLogger.log(NotificationHistoryEvent.NOTIFICATION_HISTORY_CLOSE);
        super.onPause();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Future future = this.mCountdownFuture;
        if (future != null) {
            future.cancel(true);
        }
        super.onDestroy();
    }

    private int obtainThemeColor(int i) {
        int i2 = 0;
        TypedArray obtainStyledAttributes = new ContextThemeWrapper(this, 16974563).getTheme().obtainStyledAttributes(new int[]{i});
        if (obtainStyledAttributes != null) {
            try {
                i2 = obtainStyledAttributes.getColor(0, 0);
            } catch (Throwable th) {
                try {
                    obtainStyledAttributes.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (obtainStyledAttributes != null) {
            obtainStyledAttributes.close();
        }
        return i2;
    }

    private void bindSwitch() {
        MainSwitchBar mainSwitchBar = this.mSwitchBar;
        if (mainSwitchBar != null) {
            mainSwitchBar.show();
            this.mSwitchBar.setTitle(getString(R$string.notification_history_toggle));
            try {
                this.mSwitchBar.addOnSwitchChangeListener(this.mOnSwitchClickListener);
            } catch (IllegalStateException unused) {
            }
            this.mSwitchBar.setChecked(Settings.Secure.getInt(getContentResolver(), "notification_history_enabled", 0) == 1);
            toggleViews(this.mSwitchBar.isChecked());
        }
    }

    private void toggleViews(boolean z) {
        if (z) {
            this.mHistoryOff.setVisibility(8);
            this.mHistoryOn.setVisibility(0);
        } else {
            this.mHistoryOn.setVisibility(8);
            this.mHistoryOff.setVisibility(0);
            this.mTodayView.removeAllViews();
        }
        this.mHistoryEmpty.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(Switch r7, boolean z) {
        int i;
        try {
            i = Settings.Secure.getInt(getContentResolver(), "notification_history_enabled");
        } catch (Settings.SettingNotFoundException unused) {
            i = 0;
        }
        if (i != z) {
            for (int i2 : this.mUm.getProfileIds(ActivityManager.getCurrentUser(), false)) {
                Settings.Secure.putIntForUser(getContentResolver(), "notification_history_enabled", z ? 1 : 0, i2);
            }
            this.mUiEventLogger.log(z ? NotificationHistoryEvent.NOTIFICATION_HISTORY_ON : NotificationHistoryEvent.NOTIFICATION_HISTORY_OFF);
            Log.d(TAG, "onSwitchChange history to " + z);
        }
        this.mHistoryOn.setVisibility(8);
        if (z) {
            this.mHistoryEmpty.setVisibility(0);
            this.mHistoryOff.setVisibility(8);
        } else {
            this.mHistoryOff.setVisibility(0);
            this.mHistoryEmpty.setVisibility(8);
        }
        this.mTodayView.removeAllViews();
    }
}
