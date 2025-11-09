package com.android.settings.vpn2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class VpnInfoPreference extends RestrictedPreference implements View.OnClickListener {
    private String mHelpUrl;
    private boolean mIsInsecureVpn;

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return false;
    }

    public VpnInfoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsInsecureVpn = false;
        this.mHelpUrl = context.getString(R$string.help_url_insecure_vpn);
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R$layout.preference_widget_warning;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.warning_button);
        if (this.mIsInsecureVpn && !TextUtils.isEmpty(this.mHelpUrl)) {
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(this);
            findViewById.setEnabled(true);
        } else {
            findViewById.setVisibility(8);
            findViewById.setOnClickListener(this);
            findViewById.setEnabled(false);
        }
        preferenceViewHolder.findViewById(R$id.two_target_divider).setVisibility(8);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Intent helpIntent;
        if (view.getId() != R$id.warning_button || (helpIntent = HelpUtils.getHelpIntent(getContext(), this.mHelpUrl, getClass().getName())) == null) {
            return;
        }
        ((Activity) getContext()).startActivityForResult(helpIntent, 0);
    }

    public void setInsecureVpn(boolean z) {
        if (this.mIsInsecureVpn != z) {
            this.mIsInsecureVpn = z;
            notifyChanged();
        }
    }
}
