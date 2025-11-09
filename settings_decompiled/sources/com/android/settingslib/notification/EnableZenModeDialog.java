package com.android.settingslib.notification;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.PhoneWindow;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
/* loaded from: classes2.dex */
public class EnableZenModeDialog {
    @VisibleForTesting
    protected static final int COUNTDOWN_ALARM_CONDITION_INDEX = 2;
    @VisibleForTesting
    protected static final int COUNTDOWN_CONDITION_INDEX = 1;
    private static final boolean DEBUG = Log.isLoggable("EnableZenModeDialog", 3);
    private static final int DEFAULT_BUCKET_INDEX;
    @VisibleForTesting
    protected static final int FOREVER_CONDITION_INDEX = 0;
    private static final int MAX_BUCKET_MINUTES;
    private static final int[] MINUTE_BUCKETS;
    private static final int MIN_BUCKET_MINUTES;
    private int MAX_MANUAL_DND_OPTIONS;
    private AlarmManager mAlarmManager;
    private boolean mAttached;
    private int mBucketIndex;
    private final boolean mCancelIsNeutral;
    @VisibleForTesting
    protected Context mContext;
    @VisibleForTesting
    protected Uri mForeverId;
    @VisibleForTesting
    protected LayoutInflater mLayoutInflater;
    private final ZenModeDialogMetricsLogger mMetricsLogger;
    @VisibleForTesting
    protected NotificationManager mNotificationManager;
    private final int mThemeResId;
    private int mUserId;
    @VisibleForTesting
    protected TextView mZenAlarmWarning;
    private RadioGroup mZenRadioGroup;
    @VisibleForTesting
    protected LinearLayout mZenRadioGroupContent;

    static {
        int[] iArr = ZenModeConfig.MINUTE_BUCKETS;
        MINUTE_BUCKETS = iArr;
        MIN_BUCKET_MINUTES = iArr[0];
        MAX_BUCKET_MINUTES = iArr[iArr.length - 1];
        DEFAULT_BUCKET_INDEX = Arrays.binarySearch(iArr, 60);
    }

    public EnableZenModeDialog(Context context) {
        this(context, 0);
    }

    public EnableZenModeDialog(Context context, int i) {
        this(context, i, false, new ZenModeDialogMetricsLogger(context));
    }

    public EnableZenModeDialog(Context context, int i, boolean z, ZenModeDialogMetricsLogger zenModeDialogMetricsLogger) {
        this.mBucketIndex = -1;
        this.MAX_MANUAL_DND_OPTIONS = 3;
        this.mContext = context;
        this.mThemeResId = i;
        this.mCancelIsNeutral = z;
        this.mMetricsLogger = zenModeDialogMetricsLogger;
    }

    public AlertDialog createDialog() {
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        this.mForeverId = Condition.newId(this.mContext).appendPath("forever").build();
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mUserId = this.mContext.getUserId();
        this.mAttached = false;
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(this.mContext, this.mThemeResId).setTitle(R$string.zen_mode_settings_turn_on_dialog_title).setPositiveButton(R$string.zen_mode_enable_dialog_turn_on, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ConditionTag conditionTagAt = EnableZenModeDialog.this.getConditionTagAt(EnableZenModeDialog.this.mZenRadioGroup.getCheckedRadioButtonId());
                if (EnableZenModeDialog.this.isForever(conditionTagAt.condition)) {
                    EnableZenModeDialog.this.mMetricsLogger.logOnEnableZenModeForever();
                } else if (EnableZenModeDialog.this.isAlarm(conditionTagAt.condition)) {
                    EnableZenModeDialog.this.mMetricsLogger.logOnEnableZenModeUntilAlarm();
                } else if (EnableZenModeDialog.this.isCountdown(conditionTagAt.condition)) {
                    EnableZenModeDialog.this.mMetricsLogger.logOnEnableZenModeUntilCountdown();
                } else {
                    Slog.d("EnableZenModeDialog", "Invalid manual condition: " + conditionTagAt.condition);
                }
                EnableZenModeDialog enableZenModeDialog = EnableZenModeDialog.this;
                enableZenModeDialog.mNotificationManager.setZenMode(1, enableZenModeDialog.getRealConditionId(conditionTagAt.condition), "EnableZenModeDialog");
            }
        });
        if (this.mCancelIsNeutral) {
            positiveButton.setNeutralButton(R$string.cancel, (DialogInterface.OnClickListener) null);
        } else {
            positiveButton.setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null);
        }
        View contentView = getContentView();
        bindConditions(forever());
        positiveButton.setView(contentView);
        return positiveButton.create();
    }

    private void hideAllConditions() {
        int childCount = this.mZenRadioGroupContent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mZenRadioGroupContent.getChildAt(i).setVisibility(8);
        }
        this.mZenAlarmWarning.setVisibility(8);
    }

    protected View getContentView() {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = new PhoneWindow(this.mContext).getLayoutInflater();
        }
        View inflate = this.mLayoutInflater.inflate(R$layout.zen_mode_turn_on_dialog_container, (ViewGroup) null);
        ScrollView scrollView = (ScrollView) inflate.findViewById(R$id.container);
        this.mZenRadioGroup = (RadioGroup) scrollView.findViewById(R$id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout) scrollView.findViewById(R$id.zen_radio_buttons_content);
        this.mZenAlarmWarning = (TextView) scrollView.findViewById(R$id.zen_alarm_warning);
        for (int i = 0; i < this.MAX_MANUAL_DND_OPTIONS; i++) {
            View inflate2 = this.mLayoutInflater.inflate(R$layout.zen_mode_radio_button, (ViewGroup) this.mZenRadioGroup, false);
            this.mZenRadioGroup.addView(inflate2);
            inflate2.setId(i);
            View inflate3 = this.mLayoutInflater.inflate(R$layout.zen_mode_condition, (ViewGroup) this.mZenRadioGroupContent, false);
            inflate3.setId(this.MAX_MANUAL_DND_OPTIONS + i);
            this.mZenRadioGroupContent.addView(inflate3);
        }
        hideAllConditions();
        return inflate;
    }

    @VisibleForTesting
    protected void bind(Condition condition, View view, int i) {
        if (condition == null) {
            throw new IllegalArgumentException("condition must not be null");
        }
        boolean z = condition.state == 1;
        final ConditionTag conditionTag = view.getTag() != null ? (ConditionTag) view.getTag() : new ConditionTag();
        view.setTag(conditionTag);
        RadioButton radioButton = conditionTag.rb;
        boolean z2 = radioButton == null;
        if (radioButton == null) {
            conditionTag.rb = (RadioButton) this.mZenRadioGroup.getChildAt(i);
        }
        conditionTag.condition = condition;
        final Uri conditionId = getConditionId(condition);
        if (DEBUG) {
            Log.d("EnableZenModeDialog", "bind i=" + this.mZenRadioGroupContent.indexOfChild(view) + " first=" + z2 + " condition=" + conditionId);
        }
        conditionTag.rb.setEnabled(z);
        conditionTag.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z3) {
                if (z3) {
                    conditionTag.rb.setChecked(true);
                    if (EnableZenModeDialog.DEBUG) {
                        Log.d("EnableZenModeDialog", "onCheckedChanged " + conditionId);
                    }
                    EnableZenModeDialog.this.mMetricsLogger.logOnConditionSelected();
                    EnableZenModeDialog.this.updateAlarmWarningText(conditionTag.condition);
                }
            }
        });
        updateUi(conditionTag, view, condition, z, i, conditionId);
        view.setVisibility(0);
    }

    @VisibleForTesting
    protected ConditionTag getConditionTagAt(int i) {
        return (ConditionTag) this.mZenRadioGroupContent.getChildAt(i).getTag();
    }

    @VisibleForTesting
    protected void bindConditions(Condition condition) {
        bind(forever(), this.mZenRadioGroupContent.getChildAt(0), 0);
        if (condition == null) {
            bindGenericCountdown();
            bindNextAlarm(getTimeUntilNextAlarmCondition());
        } else if (isForever(condition)) {
            getConditionTagAt(0).rb.setChecked(true);
            bindGenericCountdown();
            bindNextAlarm(getTimeUntilNextAlarmCondition());
        } else if (isAlarm(condition)) {
            bindGenericCountdown();
            bindNextAlarm(condition);
            getConditionTagAt(2).rb.setChecked(true);
        } else if (isCountdown(condition)) {
            bindNextAlarm(getTimeUntilNextAlarmCondition());
            bind(condition, this.mZenRadioGroupContent.getChildAt(1), 1);
            getConditionTagAt(1).rb.setChecked(true);
        } else {
            Slog.d("EnableZenModeDialog", "Invalid manual condition: " + condition);
        }
    }

    public static Uri getConditionId(Condition condition) {
        if (condition != null) {
            return condition.id;
        }
        return null;
    }

    public Condition forever() {
        return new Condition(Condition.newId(this.mContext).appendPath("forever").build(), foreverSummary(this.mContext), "", "", 0, 1, 0);
    }

    public long getNextAlarm() {
        AlarmManager.AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        if (nextAlarmClock != null) {
            return nextAlarmClock.getTriggerTime();
        }
        return 0L;
    }

    @VisibleForTesting
    protected boolean isAlarm(Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownToAlarmConditionId(condition.id);
    }

    @VisibleForTesting
    protected boolean isCountdown(Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownConditionId(condition.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isForever(Condition condition) {
        return condition != null && this.mForeverId.equals(condition.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri getRealConditionId(Condition condition) {
        if (isForever(condition)) {
            return null;
        }
        return getConditionId(condition);
    }

    private String foreverSummary(Context context) {
        return context.getString(17041801);
    }

    private static void setToMidnight(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }

    @VisibleForTesting
    protected Condition getTimeUntilNextAlarmCondition() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        setToMidnight(gregorianCalendar);
        gregorianCalendar.add(5, 6);
        long nextAlarm = getNextAlarm();
        if (nextAlarm > 0) {
            GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
            gregorianCalendar2.setTimeInMillis(nextAlarm);
            setToMidnight(gregorianCalendar2);
            if (gregorianCalendar.compareTo((Calendar) gregorianCalendar2) >= 0) {
                return ZenModeConfig.toNextAlarmCondition(this.mContext, nextAlarm, ActivityManager.getCurrentUser());
            }
            return null;
        }
        return null;
    }

    @VisibleForTesting
    protected void bindGenericCountdown() {
        int i = DEFAULT_BUCKET_INDEX;
        this.mBucketIndex = i;
        Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i], ActivityManager.getCurrentUser());
        if (!this.mAttached || getConditionTagAt(1).condition == null) {
            bind(timeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
        }
    }

    private void updateUi(final ConditionTag conditionTag, final View view, Condition condition, boolean z, final int i, Uri uri) {
        if (conditionTag.lines == null) {
            conditionTag.lines = view.findViewById(16908290);
        }
        if (conditionTag.line1 == null) {
            conditionTag.line1 = (TextView) view.findViewById(16908308);
        }
        if (conditionTag.line2 == null) {
            conditionTag.line2 = (TextView) view.findViewById(16908309);
        }
        String str = !TextUtils.isEmpty(condition.line1) ? condition.line1 : condition.summary;
        String str2 = condition.line2;
        conditionTag.line1.setText(str);
        if (TextUtils.isEmpty(str2)) {
            conditionTag.line2.setVisibility(8);
        } else {
            conditionTag.line2.setVisibility(0);
            conditionTag.line2.setText(str2);
        }
        conditionTag.lines.setEnabled(z);
        conditionTag.lines.setAlpha(z ? 1.0f : 0.4f);
        conditionTag.lines.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                conditionTag.rb.setChecked(true);
            }
        });
        long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
        ImageView imageView = (ImageView) view.findViewById(16908313);
        ImageView imageView2 = (ImageView) view.findViewById(16908314);
        if (i != 1 || tryParseCountdownConditionId <= 0) {
            if (imageView != null) {
                ((ViewGroup) view).removeView(imageView);
            }
            if (imageView2 != null) {
                ((ViewGroup) view).removeView(imageView2);
                return;
            }
            return;
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                EnableZenModeDialog.this.onClickTimeButton(view, conditionTag, false, i);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                EnableZenModeDialog.this.onClickTimeButton(view, conditionTag, true, i);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        int i2 = this.mBucketIndex;
        if (i2 > -1) {
            imageView.setEnabled(i2 > 0);
            imageView2.setEnabled(this.mBucketIndex < MINUTE_BUCKETS.length - 1);
        } else {
            imageView.setEnabled(tryParseCountdownConditionId - System.currentTimeMillis() > ((long) (MIN_BUCKET_MINUTES * 60000)));
            imageView2.setEnabled(!Objects.equals(condition.summary, ZenModeConfig.toTimeCondition(this.mContext, MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser()).summary));
        }
        imageView.setAlpha(imageView.isEnabled() ? 1.0f : 0.5f);
        imageView2.setAlpha(imageView2.isEnabled() ? 1.0f : 0.5f);
    }

    @VisibleForTesting
    protected void bindNextAlarm(Condition condition) {
        View childAt = this.mZenRadioGroupContent.getChildAt(2);
        ConditionTag conditionTag = (ConditionTag) childAt.getTag();
        if (condition != null && (!this.mAttached || conditionTag == null || conditionTag.condition == null)) {
            bind(condition, childAt, 2);
        }
        ConditionTag conditionTag2 = (ConditionTag) childAt.getTag();
        boolean z = (conditionTag2 == null || conditionTag2.condition == null) ? false : true;
        this.mZenRadioGroup.getChildAt(2).setVisibility(z ? 0 : 8);
        childAt.setVisibility(z ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickTimeButton(View view, ConditionTag conditionTag, boolean z, int i) {
        Condition timeCondition;
        this.mMetricsLogger.logOnClickTimeButton(z);
        int[] iArr = MINUTE_BUCKETS;
        int length = iArr.length;
        int i2 = this.mBucketIndex;
        if (i2 == -1) {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(getConditionId(conditionTag.condition));
            long currentTimeMillis = System.currentTimeMillis();
            for (int i3 = 0; i3 < length; i3++) {
                int i4 = z ? i3 : (length - 1) - i3;
                int i5 = MINUTE_BUCKETS[i4];
                long j = currentTimeMillis + (60000 * i5);
                if ((z && j > tryParseCountdownConditionId) || (!z && j < tryParseCountdownConditionId)) {
                    this.mBucketIndex = i4;
                    timeCondition = ZenModeConfig.toTimeCondition(this.mContext, j, i5, ActivityManager.getCurrentUser(), false);
                    break;
                }
            }
            timeCondition = null;
            if (timeCondition == null) {
                int i6 = DEFAULT_BUCKET_INDEX;
                this.mBucketIndex = i6;
                timeCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i6], ActivityManager.getCurrentUser());
            }
        } else {
            int max = Math.max(0, Math.min(length - 1, i2 + (z ? 1 : -1)));
            this.mBucketIndex = max;
            timeCondition = ZenModeConfig.toTimeCondition(this.mContext, iArr[max], ActivityManager.getCurrentUser());
        }
        bind(timeCondition, view, i);
        updateAlarmWarningText(conditionTag.condition);
        conditionTag.rb.setChecked(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmWarningText(Condition condition) {
        String computeAlarmWarningText = computeAlarmWarningText(condition);
        this.mZenAlarmWarning.setText(computeAlarmWarningText);
        this.mZenAlarmWarning.setVisibility(computeAlarmWarningText == null ? 8 : 0);
    }

    @VisibleForTesting
    protected String computeAlarmWarningText(Condition condition) {
        int i;
        if ((this.mNotificationManager.getNotificationPolicy().priorityCategories & 32) != 0) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long nextAlarm = getNextAlarm();
        if (nextAlarm < currentTimeMillis) {
            return null;
        }
        if (condition == null || isForever(condition)) {
            i = R$string.zen_alarm_warning_indef;
        } else {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(condition.id);
            i = (tryParseCountdownConditionId <= currentTimeMillis || nextAlarm >= tryParseCountdownConditionId) ? 0 : R$string.zen_alarm_warning;
        }
        if (i == 0) {
            return null;
        }
        return this.mContext.getResources().getString(i, getTime(nextAlarm, currentTimeMillis));
    }

    @VisibleForTesting
    protected String getTime(long j, long j2) {
        boolean z = j - j2 < 86400000;
        boolean is24HourFormat = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
        return this.mContext.getResources().getString(z ? R$string.alarm_template : R$string.alarm_template_far, DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), z ? is24HourFormat ? "Hm" : "hma" : is24HourFormat ? "EEEHm" : "EEEhma"), j));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class ConditionTag {
        public Condition condition;
        public TextView line1;
        public TextView line2;
        public View lines;
        public RadioButton rb;

        protected ConditionTag() {
        }
    }
}
