package com.android.settingslib;

import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.utils.BuildCompatUtils;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class RestrictedSwitchPreference extends SwitchPreference {
    AppOpsManager mAppOpsManager;
    RestrictedPreferenceHelper mHelper;
    private int mIconSize;
    CharSequence mRestrictedSwitchSummary;
    boolean mUseAdditionalSummary;
    private TextView textState;

    public RestrictedSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        boolean z = true;
        this.mUseAdditionalSummary = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RestrictedSwitchPreference);
            TypedValue peekValue = obtainStyledAttributes.peekValue(R$styleable.RestrictedSwitchPreference_useAdditionalSummary);
            if (peekValue != null) {
                this.mUseAdditionalSummary = (peekValue.type != 18 || peekValue.data == 0) ? false : z;
            }
            TypedValue peekValue2 = obtainStyledAttributes.peekValue(R$styleable.RestrictedSwitchPreference_restrictedSwitchSummary);
            obtainStyledAttributes.recycle();
            if (peekValue2 != null && peekValue2.type == 3) {
                int i3 = peekValue2.resourceId;
                if (i3 != 0) {
                    this.mRestrictedSwitchSummary = context.getText(i3);
                } else {
                    this.mRestrictedSwitchSummary = peekValue2.string;
                }
            }
        }
        if (this.mUseAdditionalSummary) {
            setLayoutResource(R$layout.restricted_switch_preference);
            useAdminDisabledSummary(false);
        }
    }

    public RestrictedSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RestrictedSwitchPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.switchPreferenceStyle, 16843629));
    }

    public RestrictedSwitchPreference(Context context) {
        this(context, null);
    }

    public void setAppOps(AppOpsManager appOpsManager) {
        this.mAppOpsManager = appOpsManager;
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908352);
        this.textState = (TextView) preferenceViewHolder.findViewById(R$id.state_tv);
        if (findViewById != null && (findViewById instanceof Checkable) && (findViewById instanceof Switch)) {
            Switch r1 = (Switch) findViewById;
            findViewById.getRootView().setFilterTouchesWhenObscured(true);
        }
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        CharSequence charSequence = this.mRestrictedSwitchSummary;
        if (charSequence == null) {
            if (isChecked()) {
                charSequence = getUpdatableEnterpriseString(getContext(), "Settings.ENABLED_BY_ADMIN_SWITCH_SUMMARY", R$string.enabled_by_admin);
            } else {
                charSequence = getUpdatableEnterpriseString(getContext(), "Settings.DISABLED_BY_ADMIN_SWITCH_SUMMARY", R$string.disabled_by_admin);
            }
        }
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
        if (this.mIconSize > 0) {
            int i = this.mIconSize;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(i, i));
        }
        if (this.mUseAdditionalSummary) {
            TextView textView = (TextView) preferenceViewHolder.findViewById(R$id.additional_summary);
            if (textView != null) {
                if (isDisabledByAdmin()) {
                    textView.setText(charSequence);
                    textView.setVisibility(0);
                    return;
                }
                textView.setVisibility(8);
                return;
            }
            return;
        }
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView2 == null || !isDisabledByAdmin()) {
            return;
        }
        textView2.setText(charSequence);
        textView2.setVisibility(0);
    }

    private static String getUpdatableEnterpriseString(final Context context, String str, final int i) {
        if (!BuildCompatUtils.isAtLeastT()) {
            return context.getString(i);
        }
        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString(str, new Supplier() { // from class: com.android.settingslib.RestrictedSwitchPreference$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String string;
                string = context.getString(i);
                return string;
            }
        });
    }

    public void useAdminDisabledSummary(boolean z) {
        this.mHelper.useAdminDisabledSummary(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    public void checkRestrictionAndSetDisabled(String str) {
        this.mHelper.checkRestrictionAndSetDisabled(str, UserHandle.myUserId());
    }

    public void checkRestrictionAndSetDisabled(String str, int i) {
        this.mHelper.checkRestrictionAndSetDisabled(str, i);
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (this.mHelper.performClick()) {
            return;
        }
        super.performClick();
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        boolean z2;
        boolean z3 = true;
        if (z && isDisabledByAdmin()) {
            this.mHelper.setDisabledByAdmin(null);
            z2 = true;
        } else {
            z2 = false;
        }
        if (z && isDisabledByAppOps()) {
            this.mHelper.setDisabledByAppOps(false);
        } else {
            z3 = z2;
        }
        if (z3) {
            return;
        }
        super.setEnabled(z);
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mHelper.setDisabledByAdmin(enforcedAdmin)) {
            notifyChanged();
        }
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    private void setDisabledByAppOps(boolean z) {
        if (this.mHelper.setDisabledByAppOps(z)) {
            notifyChanged();
        }
    }

    public boolean isDisabledByAppOps() {
        return this.mHelper.isDisabledByAppOps();
    }

    public void setState(String str) {
        TextView textView = this.textState;
        if (textView != null) {
            textView.setText(str);
        }
    }

    public void updateState(String str, int i, boolean z) {
        this.mHelper.updatePackageDetails(str, i);
        if (this.mAppOpsManager == null) {
            this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
        }
        boolean z2 = !getContext().getResources().getBoolean(17891657) || this.mAppOpsManager.noteOpNoThrow(119, i, str) == 0;
        if (z) {
            setEnabled(true);
        } else if (z2 && isDisabledByAppOps()) {
            setEnabled(true);
        } else if (z2) {
        } else {
            setDisabledByAppOps(true);
        }
    }
}
