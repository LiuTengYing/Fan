package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.MainSwitchBar;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.android.settingslib.widget.R$id;
/* loaded from: classes.dex */
public class SettingsMainSwitchBar extends MainSwitchBar {
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mMetricsTag;
    private OnBeforeCheckedChangeListener mOnBeforeListener;
    private ImageView mRestrictedIcon;

    /* loaded from: classes.dex */
    public interface OnBeforeCheckedChangeListener {
        boolean onBeforeCheckedChanged(Switch r1, boolean z);
    }

    public SettingsMainSwitchBar(Context context) {
        this(context, null);
    }

    public SettingsMainSwitchBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SettingsMainSwitchBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SettingsMainSwitchBar(final Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        addOnSwitchChangeListener(new OnMainSwitchChangeListener() { // from class: com.android.settings.widget.SettingsMainSwitchBar$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                SettingsMainSwitchBar.this.lambda$new$0(r1, z);
            }
        });
        ImageView imageView = (ImageView) findViewById(R$id.restricted_icon);
        this.mRestrictedIcon = imageView;
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.SettingsMainSwitchBar$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsMainSwitchBar.this.lambda$new$1(context, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Switch r1, boolean z) {
        logMetrics(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Context context, View view) {
        if (this.mDisabledByAdmin) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(context, this.mEnforcedAdmin);
            onRestrictedIconClick();
        }
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mEnforcedAdmin = enforcedAdmin;
        if (enforcedAdmin != null) {
            super.setEnabled(true);
            this.mDisabledByAdmin = true;
            this.mTextView.setEnabled(false);
            this.mSwitch.setEnabled(false);
            this.mSwitch.setVisibility(8);
            this.mRestrictedIcon.setVisibility(0);
            return;
        }
        this.mDisabledByAdmin = false;
        this.mSwitch.setVisibility(0);
        this.mRestrictedIcon.setVisibility(8);
        setEnabled(isEnabled());
    }

    @Override // com.android.settingslib.widget.MainSwitchBar, android.view.View
    public void setEnabled(boolean z) {
        if (z && this.mDisabledByAdmin) {
            setDisabledByAdmin(null);
        } else {
            super.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.widget.MainSwitchBar, android.view.View
    public boolean performClick() {
        return getDelegatingView().performClick();
    }

    protected void onRestrictedIconClick() {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(0, 853, 0, this.mMetricsTag + "/switch_bar|restricted", 1);
    }

    @Override // com.android.settingslib.widget.MainSwitchBar
    public void setChecked(boolean z) {
        OnBeforeCheckedChangeListener onBeforeCheckedChangeListener = this.mOnBeforeListener;
        if (onBeforeCheckedChangeListener == null || !onBeforeCheckedChangeListener.onBeforeCheckedChanged(this.mSwitch, z)) {
            super.setChecked(z);
        }
    }

    public void setCheckedInternal(boolean z) {
        super.setChecked(z);
    }

    public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener onBeforeCheckedChangeListener) {
        this.mOnBeforeListener = onBeforeCheckedChangeListener;
    }

    public void setMetricsTag(String str) {
        this.mMetricsTag = str;
    }

    private View getDelegatingView() {
        return this.mDisabledByAdmin ? this.mRestrictedIcon : this.mSwitch;
    }

    private void logMetrics(boolean z) {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(0, 853, 0, this.mMetricsTag + "/switch_bar", z ? 1 : 0);
    }
}
