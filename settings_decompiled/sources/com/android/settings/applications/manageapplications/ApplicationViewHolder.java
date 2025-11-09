package com.android.settings.applications.manageapplications;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class ApplicationViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mAppIcon;
    final TextView mAppName;
    final TextView mDisabled;
    final TextView mSummary;
    final Switch mSwitch;
    final ViewGroup mWidgetContainer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationViewHolder(View view) {
        super(view);
        this.mAppName = (TextView) view.findViewById(16908310);
        this.mAppIcon = (ImageView) view.findViewById(16908294);
        this.mSummary = (TextView) view.findViewById(16908304);
        this.mDisabled = (TextView) view.findViewById(R$id.appendix);
        this.mSwitch = (Switch) view.findViewById(R$id.switchWidget);
        this.mWidgetContainer = (ViewGroup) view.findViewById(16908312);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static View newView(ViewGroup viewGroup, boolean z) {
        ViewGroup viewGroup2 = (ViewGroup) LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.preference_app, viewGroup, false);
        ViewGroup viewGroup3 = (ViewGroup) viewGroup2.findViewById(16908312);
        if (z) {
            if (viewGroup3 != null) {
                LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.preference_widget_primary_switch, viewGroup3, true);
                viewGroup2.addView(LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.preference_two_target_divider, viewGroup2, false), viewGroup2.getChildCount() - 1);
            }
        } else if (viewGroup3 != null) {
            viewGroup3.setVisibility(8);
        }
        return viewGroup2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static View newHeader(ViewGroup viewGroup, int i) {
        ViewGroup viewGroup2 = (ViewGroup) LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.preference_app_header, viewGroup, false);
        ((TextView) viewGroup2.findViewById(R$id.apps_top_intro_text)).setText(i);
        return viewGroup2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSummary(CharSequence charSequence) {
        this.mSummary.setText(charSequence);
        updateSummaryVisibility();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSummary(int i) {
        this.mSummary.setText(i);
        updateSummaryVisibility();
    }

    private void updateSummaryVisibility() {
        TextView textView = this.mSummary;
        textView.setVisibility(TextUtils.isEmpty(textView.getText()) ? 8 : 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnabled(boolean z) {
        this.itemView.setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTitle(CharSequence charSequence, CharSequence charSequence2) {
        if (charSequence == null) {
            return;
        }
        this.mAppName.setText(charSequence);
        if (TextUtils.isEmpty(charSequence2)) {
            return;
        }
        this.mAppName.setContentDescription(charSequence2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIcon(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        this.mAppIcon.setImageDrawable(drawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDisableView(ApplicationInfo applicationInfo) {
        if ((applicationInfo.flags & 8388608) == 0) {
            this.mDisabled.setVisibility(0);
            this.mDisabled.setText(R$string.not_installed);
        } else if (!applicationInfo.enabled || applicationInfo.enabledSetting == 4) {
            this.mDisabled.setVisibility(0);
            this.mDisabled.setText(R$string.disabled);
        } else {
            this.mDisabled.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSizeText(ApplicationsState.AppEntry appEntry, CharSequence charSequence, int i) {
        if (ManageApplications.DEBUG) {
            Log.d("ManageApplications", "updateSizeText of " + appEntry.label + " " + appEntry + ": " + appEntry.sizeStr);
        }
        String str = appEntry.sizeStr;
        if (str == null) {
            if (appEntry.size == -2) {
                setSummary(charSequence);
            }
        } else if (i == 1) {
            setSummary(appEntry.internalSizeStr);
        } else if (i == 2) {
            setSummary(appEntry.externalSizeStr);
        } else {
            setSummary(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSwitch(CompoundButton.OnCheckedChangeListener onCheckedChangeListener, boolean z, boolean z2) {
        ViewGroup viewGroup;
        if (this.mSwitch == null || (viewGroup = this.mWidgetContainer) == null) {
            return;
        }
        viewGroup.setFocusable(false);
        this.mWidgetContainer.setClickable(false);
        this.mSwitch.setFocusable(true);
        this.mSwitch.setClickable(true);
        this.mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        this.mSwitch.setChecked(z2);
        this.mSwitch.setEnabled(z);
    }
}
