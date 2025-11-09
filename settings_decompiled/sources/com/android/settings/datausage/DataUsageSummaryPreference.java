package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.utils.StringUtil;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DataUsageSummaryPreference extends Preference {
    private final long CYCLE_TIME_UNINITIAL_VALUE;
    private CharSequence mCarrierName;
    private boolean mChartEnabled;
    private long mCycleEndTimeMs;
    private long mDataplanSize;
    private long mDataplanUse;
    private CharSequence mEndLabel;
    private boolean mHasMobileData;
    private Intent mLaunchIntent;
    private CharSequence mLimitInfoText;
    private int mNumPlans;
    private float mProgress;
    private boolean mSingleWifi;
    private long mSnapshotTimeMs;
    private CharSequence mStartLabel;
    private boolean mTag1;
    private boolean mTag2;
    private boolean mTag3;
    private String mUsagePeriod;
    private boolean mWifiMode;
    private static final long MILLIS_IN_A_DAY = TimeUnit.DAYS.toMillis(1);
    private static final long WARNING_AGE = TimeUnit.HOURS.toMillis(6);
    static final Typeface SANS_SERIF_MEDIUM = Typeface.create("sans-serif-medium", 0);

    public DataUsageSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mChartEnabled = true;
        this.CYCLE_TIME_UNINITIAL_VALUE = 0L;
        setLayoutResource(R$layout.data_usage_summary_preference);
    }

    public void setLimitInfo(CharSequence charSequence) {
        if (Objects.equals(charSequence, this.mLimitInfoText)) {
            return;
        }
        this.mLimitInfoText = charSequence;
        this.mTag1 = true;
        notifyChanged();
    }

    public void setProgress(float f) {
        this.mProgress = f;
        notifyChanged();
    }

    public void setUsageInfo(long j, long j2, CharSequence charSequence, int i, Intent intent) {
        this.mCycleEndTimeMs = j;
        this.mTag2 = true;
        this.mSnapshotTimeMs = j2;
        this.mCarrierName = charSequence;
        this.mNumPlans = i;
        this.mTag3 = true;
        this.mLaunchIntent = intent;
        notifyChanged();
    }

    public void setChartEnabled(boolean z) {
        if (this.mChartEnabled != z) {
            this.mChartEnabled = z;
            notifyChanged();
        }
    }

    public void setLabels(CharSequence charSequence, CharSequence charSequence2) {
        this.mStartLabel = charSequence;
        this.mEndLabel = charSequence2;
        notifyChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUsageNumbers(long j, long j2, boolean z) {
        this.mDataplanUse = j;
        this.mDataplanSize = j2;
        this.mHasMobileData = z;
        notifyChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWifiMode(boolean z, String str, boolean z2) {
        this.mWifiMode = z;
        this.mUsagePeriod = str;
        this.mSingleWifi = z2;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ProgressBar progressBar = getProgressBar(preferenceViewHolder);
        if (this.mChartEnabled && (!TextUtils.isEmpty(this.mStartLabel) || !TextUtils.isEmpty(this.mEndLabel))) {
            progressBar.setVisibility(0);
            getLabelBar(preferenceViewHolder).setVisibility(0);
            progressBar.setProgress((int) (this.mProgress * 100.0f));
            getLabel1(preferenceViewHolder).setText(this.mStartLabel);
            getLabel2(preferenceViewHolder).setText(this.mEndLabel);
        } else {
            progressBar.setVisibility(8);
            getLabelBar(preferenceViewHolder).setVisibility(8);
        }
        updateDataUsageLabels(preferenceViewHolder);
        TextView usageTitle = getUsageTitle(preferenceViewHolder);
        TextView carrierInfo = getCarrierInfo(preferenceViewHolder);
        Button launchButton = getLaunchButton(preferenceViewHolder);
        TextView dataLimits = getDataLimits(preferenceViewHolder);
        boolean z = this.mWifiMode;
        if (z && this.mSingleWifi) {
            updateCycleTimeText(preferenceViewHolder);
            usageTitle.setVisibility(8);
            launchButton.setVisibility(8);
            carrierInfo.setVisibility(8);
            dataLimits.setVisibility(TextUtils.isEmpty(this.mLimitInfoText) ? 8 : 0);
            dataLimits.setText(this.mLimitInfoText);
        } else if (z) {
            usageTitle.setText(R$string.data_usage_wifi_title);
            usageTitle.setVisibility(0);
            getCycleTime(preferenceViewHolder).setText(this.mUsagePeriod);
            carrierInfo.setVisibility(8);
            dataLimits.setVisibility(8);
            if (getHistoricalUsageLevel() > 0) {
                launchButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.datausage.DataUsageSummaryPreference$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        DataUsageSummaryPreference.this.lambda$onBindViewHolder$0(view);
                    }
                });
            } else {
                launchButton.setEnabled(false);
            }
            launchButton.setText(R$string.launch_wifi_text);
            launchButton.setVisibility(0);
        } else {
            usageTitle.setVisibility(this.mNumPlans > 1 ? 0 : 8);
            if (this.mTag2) {
                updateCycleTimeText(preferenceViewHolder);
                this.mTag2 = false;
            }
            if (this.mTag3) {
                updateCarrierInfo(carrierInfo);
                this.mTag3 = false;
            }
            if (this.mLaunchIntent != null) {
                launchButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.datausage.DataUsageSummaryPreference$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        DataUsageSummaryPreference.this.lambda$onBindViewHolder$1(view);
                    }
                });
                launchButton.setVisibility(0);
            } else {
                launchButton.setVisibility(8);
            }
            if (this.mTag1) {
                dataLimits.setVisibility(TextUtils.isEmpty(this.mLimitInfoText) ? 8 : 0);
                this.mTag1 = false;
            }
            dataLimits.setText(this.mLimitInfoText);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        launchWifiDataUsage(getContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1(View view) {
        getContext().startActivity(this.mLaunchIntent);
    }

    static void launchWifiDataUsage(Context context) {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("network_template", new NetworkTemplate.Builder(4).build());
        bundle.putInt("network_type", 1);
        SubSettingLauncher sourceMetricsCategory = new SubSettingLauncher(context).setArguments(bundle).setDestination(DataUsageList.class.getName()).setSourceMetricsCategory(0);
        sourceMetricsCategory.setTitleRes(R$string.wifi_data_usage);
        sourceMetricsCategory.launch();
    }

    private void updateDataUsageLabels(PreferenceViewHolder preferenceViewHolder) {
        TextView dataUsed = getDataUsed(preferenceViewHolder);
        Formatter.BytesResult formatBytes = Formatter.formatBytes(getContext().getResources(), this.mDataplanUse, 10);
        SpannableString spannableString = new SpannableString(formatBytes.value);
        spannableString.setSpan(new AbsoluteSizeSpan(getContext().getResources().getDimensionPixelSize(R$dimen.usage_number_text_size)), 0, spannableString.length(), 33);
        dataUsed.setText(TextUtils.expandTemplate(getContext().getText(R$string.data_used_formatted), spannableString, formatBytes.units));
        MeasurableLinearLayout layout = getLayout(preferenceViewHolder);
        if (this.mHasMobileData && this.mNumPlans >= 0 && this.mDataplanSize > 0) {
            TextView dataRemaining = getDataRemaining(preferenceViewHolder);
            long j = this.mDataplanSize - this.mDataplanUse;
            if (j >= 0) {
                dataRemaining.setText(TextUtils.expandTemplate(getContext().getText(R$string.data_remaining), DataUsageUtils.formatDataUsage(getContext(), j)));
                dataRemaining.setTextColor(Utils.getColorAttr(getContext(), 16843829));
            } else {
                dataRemaining.setText(TextUtils.expandTemplate(getContext().getText(R$string.data_overusage), DataUsageUtils.formatDataUsage(getContext(), -j)));
                dataRemaining.setTextColor(Utils.getColorAttr(getContext(), 16844099));
            }
            layout.setChildren(dataUsed, dataRemaining);
            return;
        }
        layout.setChildren(dataUsed, null);
    }

    private void updateCycleTimeText(PreferenceViewHolder preferenceViewHolder) {
        String quantityString;
        TextView cycleTime = getCycleTime(preferenceViewHolder);
        if (this.mCycleEndTimeMs == 0) {
            cycleTime.setVisibility(8);
            return;
        }
        cycleTime.setVisibility(0);
        long currentTimeMillis = this.mCycleEndTimeMs - System.currentTimeMillis();
        if (currentTimeMillis <= 0) {
            cycleTime.setText(getContext().getString(R$string.billing_cycle_none_left));
            return;
        }
        int i = (int) (currentTimeMillis / MILLIS_IN_A_DAY);
        if (i < 1) {
            quantityString = getContext().getString(R$string.billing_cycle_less_than_one_day_left);
        } else {
            quantityString = getContext().getResources().getQuantityString(R$plurals.billing_cycle_days_left, i, Integer.valueOf(i));
        }
        cycleTime.setText(quantityString);
    }

    private void updateCarrierInfo(TextView textView) {
        int i;
        int i2;
        if (this.mNumPlans > 0 && this.mSnapshotTimeMs >= 0) {
            textView.setVisibility(0);
            long calculateTruncatedUpdateAge = calculateTruncatedUpdateAge();
            CharSequence charSequence = null;
            if (calculateTruncatedUpdateAge == 0) {
                if (this.mCarrierName != null) {
                    i2 = R$string.carrier_and_update_now_text;
                } else {
                    i2 = R$string.no_carrier_update_now_text;
                }
            } else {
                if (this.mCarrierName != null) {
                    i = R$string.carrier_and_update_text;
                } else {
                    i = R$string.no_carrier_update_text;
                }
                i2 = i;
                charSequence = StringUtil.formatElapsedTime(getContext(), calculateTruncatedUpdateAge, false, false);
            }
            textView.setText(TextUtils.expandTemplate(getContext().getText(i2), this.mCarrierName, charSequence));
            if (calculateTruncatedUpdateAge <= WARNING_AGE) {
                setCarrierInfoTextStyle(textView, 16842808, Typeface.SANS_SERIF);
                return;
            } else {
                setCarrierInfoTextStyle(textView, 16844099, SANS_SERIF_MEDIUM);
                return;
            }
        }
        textView.setVisibility(8);
    }

    private long calculateTruncatedUpdateAge() {
        long millis;
        long millis2;
        long currentTimeMillis = System.currentTimeMillis() - this.mSnapshotTimeMs;
        TimeUnit timeUnit = TimeUnit.DAYS;
        if (currentTimeMillis >= timeUnit.toMillis(1L)) {
            millis = currentTimeMillis / timeUnit.toMillis(1L);
            millis2 = timeUnit.toMillis(1L);
        } else {
            TimeUnit timeUnit2 = TimeUnit.HOURS;
            if (currentTimeMillis >= timeUnit2.toMillis(1L)) {
                millis = currentTimeMillis / timeUnit2.toMillis(1L);
                millis2 = timeUnit2.toMillis(1L);
            } else {
                TimeUnit timeUnit3 = TimeUnit.MINUTES;
                if (currentTimeMillis < timeUnit3.toMillis(1L)) {
                    return 0L;
                }
                millis = currentTimeMillis / timeUnit3.toMillis(1L);
                millis2 = timeUnit3.toMillis(1L);
            }
        }
        return millis * millis2;
    }

    private void setCarrierInfoTextStyle(TextView textView, int i, Typeface typeface) {
        textView.setTextColor(Utils.getColorAttr(getContext(), i));
        textView.setTypeface(typeface);
    }

    protected long getHistoricalUsageLevel() {
        return new DataUsageController(getContext()).getHistoricalUsageLevel(new NetworkTemplate.Builder(4).build());
    }

    protected TextView getUsageTitle(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(R$id.usage_title);
    }

    protected TextView getCycleTime(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(R$id.cycle_left_time);
    }

    protected TextView getCarrierInfo(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(R$id.carrier_and_update);
    }

    protected TextView getDataLimits(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(R$id.data_limits);
    }

    protected TextView getDataUsed(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(R$id.data_usage_view);
    }

    protected TextView getDataRemaining(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(R$id.data_remaining_view);
    }

    protected Button getLaunchButton(PreferenceViewHolder preferenceViewHolder) {
        return (Button) preferenceViewHolder.findViewById(R$id.launch_mdp_app_button);
    }

    protected LinearLayout getLabelBar(PreferenceViewHolder preferenceViewHolder) {
        return (LinearLayout) preferenceViewHolder.findViewById(R$id.label_bar);
    }

    protected TextView getLabel1(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(16908308);
    }

    protected TextView getLabel2(PreferenceViewHolder preferenceViewHolder) {
        return (TextView) preferenceViewHolder.findViewById(16908309);
    }

    protected ProgressBar getProgressBar(PreferenceViewHolder preferenceViewHolder) {
        return (ProgressBar) preferenceViewHolder.findViewById(R$id.determinateBar);
    }

    protected MeasurableLinearLayout getLayout(PreferenceViewHolder preferenceViewHolder) {
        return (MeasurableLinearLayout) preferenceViewHolder.findViewById(R$id.usage_layout);
    }
}
