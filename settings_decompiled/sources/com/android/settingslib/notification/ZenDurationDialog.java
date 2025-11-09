package com.android.settingslib.notification;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
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
import androidx.appcompat.app.AlertDialog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.PhoneWindow;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class ZenDurationDialog {
    protected static final int ALWAYS_ASK_CONDITION_INDEX = 2;
    protected static final int COUNTDOWN_CONDITION_INDEX = 1;
    private static final int DEFAULT_BUCKET_INDEX;
    protected static final int FOREVER_CONDITION_INDEX = 0;
    protected static final int MAX_BUCKET_MINUTES;
    private static final int[] MINUTE_BUCKETS;
    protected static final int MIN_BUCKET_MINUTES;
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    private RadioGroup mZenRadioGroup;
    protected LinearLayout mZenRadioGroupContent;
    protected int mBucketIndex = -1;
    private int MAX_MANUAL_DND_OPTIONS = 3;

    static {
        int[] iArr = ZenModeConfig.MINUTE_BUCKETS;
        MINUTE_BUCKETS = iArr;
        MIN_BUCKET_MINUTES = iArr[0];
        MAX_BUCKET_MINUTES = iArr[iArr.length - 1];
        DEFAULT_BUCKET_INDEX = Arrays.binarySearch(iArr, 60);
    }

    public ZenDurationDialog(Context context) {
        this.mContext = context;
    }

    public Dialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        setupDialog(builder);
        return builder.create();
    }

    public void setupDialog(AlertDialog.Builder builder) {
        final int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_duration", 0);
        builder.setTitle(R$string.zen_mode_duration_settings_title).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R$string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                ZenDurationDialog.this.updateZenDuration(i);
            }
        });
        View contentView = getContentView();
        setupRadioButtons(i);
        builder.setView(contentView);
    }

    protected void updateZenDuration(int i) {
        int checkedRadioButtonId = this.mZenRadioGroup.getCheckedRadioButtonId();
        int i2 = 0;
        int i3 = Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_duration", 0);
        if (checkedRadioButtonId == 0) {
            MetricsLogger.action(this.mContext, 1343);
        } else if (checkedRadioButtonId == 1) {
            i2 = getConditionTagAt(checkedRadioButtonId).countdownZenDuration;
            MetricsLogger.action(this.mContext, 1342, i2);
        } else if (checkedRadioButtonId != 2) {
            i2 = i3;
        } else {
            i2 = -1;
            MetricsLogger.action(this.mContext, 1344);
        }
        if (i != i2) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "zen_duration", i2);
        }
    }

    protected View getContentView() {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = new PhoneWindow(this.mContext).getLayoutInflater();
        }
        View inflate = this.mLayoutInflater.inflate(R$layout.zen_mode_duration_dialog, (ViewGroup) null);
        ScrollView scrollView = (ScrollView) inflate.findViewById(R$id.zen_duration_container);
        this.mZenRadioGroup = (RadioGroup) scrollView.findViewById(R$id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout) scrollView.findViewById(R$id.zen_radio_buttons_content);
        for (int i = 0; i < this.MAX_MANUAL_DND_OPTIONS; i++) {
            View inflate2 = this.mLayoutInflater.inflate(R$layout.zen_mode_radio_button, (ViewGroup) this.mZenRadioGroup, false);
            this.mZenRadioGroup.addView(inflate2);
            inflate2.setId(i);
            View inflate3 = this.mLayoutInflater.inflate(R$layout.zen_mode_condition, (ViewGroup) this.mZenRadioGroupContent, false);
            inflate3.setId(this.MAX_MANUAL_DND_OPTIONS + i);
            this.mZenRadioGroupContent.addView(inflate3);
        }
        return inflate;
    }

    protected void setupRadioButtons(int i) {
        int i2 = i == 0 ? 0 : i > 0 ? 1 : 2;
        bindTag(i, this.mZenRadioGroupContent.getChildAt(0), 0);
        bindTag(i, this.mZenRadioGroupContent.getChildAt(1), 1);
        bindTag(i, this.mZenRadioGroupContent.getChildAt(2), 2);
        getConditionTagAt(i2).rb.setChecked(true);
    }

    private void bindTag(int i, View view, int i2) {
        final ConditionTag conditionTag = view.getTag() != null ? (ConditionTag) view.getTag() : new ConditionTag();
        view.setTag(conditionTag);
        if (conditionTag.rb == null) {
            conditionTag.rb = (RadioButton) this.mZenRadioGroup.getChildAt(i2);
        }
        if (i <= 0) {
            conditionTag.countdownZenDuration = MINUTE_BUCKETS[DEFAULT_BUCKET_INDEX];
        } else {
            conditionTag.countdownZenDuration = i;
        }
        conditionTag.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    conditionTag.rb.setChecked(true);
                }
            }
        });
        updateUi(conditionTag, view, i2);
    }

    protected ConditionTag getConditionTagAt(int i) {
        return (ConditionTag) this.mZenRadioGroupContent.getChildAt(i).getTag();
    }

    private void setupUi(final ConditionTag conditionTag, View view) {
        if (conditionTag.lines == null) {
            conditionTag.lines = view.findViewById(16908290);
        }
        if (conditionTag.line1 == null) {
            conditionTag.line1 = (TextView) view.findViewById(16908308);
        }
        view.findViewById(16908309).setVisibility(8);
        conditionTag.lines.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                conditionTag.rb.setChecked(true);
            }
        });
    }

    private void updateButtons(final ConditionTag conditionTag, final View view, final int i) {
        ImageView imageView = (ImageView) view.findViewById(16908313);
        ImageView imageView2 = (ImageView) view.findViewById(16908314);
        long j = conditionTag.countdownZenDuration;
        if (i != 1) {
            if (imageView != null) {
                ((ViewGroup) view).removeView(imageView);
            }
            if (imageView2 != null) {
                ((ViewGroup) view).removeView(imageView2);
                return;
            }
            return;
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                ZenDurationDialog.this.onClickTimeButton(view, conditionTag, false, i);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                ZenDurationDialog.this.onClickTimeButton(view, conditionTag, true, i);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        imageView.setVisibility(0);
        imageView2.setVisibility(0);
        imageView.setEnabled(j > ((long) MIN_BUCKET_MINUTES));
        imageView2.setEnabled(conditionTag.countdownZenDuration != MAX_BUCKET_MINUTES);
        imageView.setAlpha(imageView.isEnabled() ? 1.0f : 0.5f);
        imageView2.setAlpha(imageView2.isEnabled() ? 1.0f : 0.5f);
    }

    protected void updateUi(ConditionTag conditionTag, View view, int i) {
        String string;
        if (conditionTag.lines == null) {
            setupUi(conditionTag, view);
        }
        updateButtons(conditionTag, view, i);
        if (i == 0) {
            string = this.mContext.getString(R$string.zen_mode_forever);
        } else if (i == 1) {
            string = ZenModeConfig.toTimeCondition(this.mContext, conditionTag.countdownZenDuration, ActivityManager.getCurrentUser(), false).line1;
        } else {
            string = i != 2 ? "" : this.mContext.getString(R$string.zen_mode_duration_always_prompt_title);
        }
        conditionTag.line1.setText(string);
    }

    protected void onClickTimeButton(View view, ConditionTag conditionTag, boolean z, int i) {
        int i2;
        int[] iArr = MINUTE_BUCKETS;
        int length = iArr.length;
        int i3 = this.mBucketIndex;
        if (i3 == -1) {
            long j = conditionTag.countdownZenDuration;
            for (int i4 = 0; i4 < length; i4++) {
                int i5 = z ? i4 : (length - 1) - i4;
                i2 = MINUTE_BUCKETS[i5];
                if ((z && i2 > j) || (!z && i2 < j)) {
                    this.mBucketIndex = i5;
                    break;
                }
            }
            i2 = -1;
            if (i2 == -1) {
                int i6 = DEFAULT_BUCKET_INDEX;
                this.mBucketIndex = i6;
                i2 = MINUTE_BUCKETS[i6];
            }
        } else {
            int max = Math.max(0, Math.min(length - 1, i3 + (z ? 1 : -1)));
            this.mBucketIndex = max;
            i2 = iArr[max];
        }
        conditionTag.countdownZenDuration = i2;
        bindTag(i2, view, i);
        conditionTag.rb.setChecked(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public static class ConditionTag {
        public int countdownZenDuration;
        public TextView line1;
        public View lines;
        public RadioButton rb;

        protected ConditionTag() {
        }
    }
}
