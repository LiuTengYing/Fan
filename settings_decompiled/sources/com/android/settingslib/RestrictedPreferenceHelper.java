package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.utils.BuildCompatUtils;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class RestrictedPreferenceHelper {
    private String mAttrUserRestriction;
    private final Context mContext;
    private boolean mDisabledByAdmin;
    private boolean mDisabledByAppOps;
    private boolean mDisabledSummary;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private final Preference mPreference;
    String packageName;
    int uid;

    public RestrictedPreferenceHelper(Context context, Preference preference, AttributeSet attributeSet, String str, int i) {
        CharSequence charSequence;
        this.mAttrUserRestriction = null;
        boolean z = false;
        this.mDisabledSummary = false;
        this.mContext = context;
        this.mPreference = preference;
        this.packageName = str;
        this.uid = i;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RestrictedPreference);
            TypedValue peekValue = obtainStyledAttributes.peekValue(R$styleable.RestrictedPreference_userRestriction);
            if (peekValue == null || peekValue.type != 3) {
                charSequence = null;
            } else {
                int i2 = peekValue.resourceId;
                if (i2 != 0) {
                    charSequence = context.getText(i2);
                } else {
                    charSequence = peekValue.string;
                }
            }
            String charSequence2 = charSequence == null ? null : charSequence.toString();
            this.mAttrUserRestriction = charSequence2;
            if (RestrictedLockUtilsInternal.hasBaseUserRestriction(context, charSequence2, UserHandle.myUserId())) {
                this.mAttrUserRestriction = null;
                return;
            }
            TypedValue peekValue2 = obtainStyledAttributes.peekValue(R$styleable.RestrictedPreference_useAdminDisabledSummary);
            if (peekValue2 != null) {
                if (peekValue2.type == 18 && peekValue2.data != 0) {
                    z = true;
                }
                this.mDisabledSummary = z;
            }
        }
    }

    public RestrictedPreferenceHelper(Context context, Preference preference, AttributeSet attributeSet) {
        this(context, preference, attributeSet, null, -1);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        TextView textView;
        String string;
        if (this.mDisabledByAdmin || this.mDisabledByAppOps) {
            preferenceViewHolder.itemView.setEnabled(true);
        }
        if (!this.mDisabledSummary || (textView = (TextView) preferenceViewHolder.findViewById(16908304)) == null) {
            return;
        }
        if (BuildCompatUtils.isAtLeastT()) {
            string = getDisabledByAdminUpdatableString();
        } else {
            string = this.mContext.getString(R$string.disabled_by_admin_summary_text);
        }
        if (this.mDisabledByAdmin) {
            textView.setText(string);
        } else if (this.mDisabledByAppOps) {
            textView.setText(R$string.disabled_by_app_ops_text);
        } else if (TextUtils.equals(string, textView.getText())) {
            textView.setText((CharSequence) null);
        }
    }

    private String getDisabledByAdminUpdatableString() {
        return ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.CONTROLLED_BY_ADMIN_SUMMARY", new Supplier() { // from class: com.android.settingslib.RestrictedPreferenceHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getDisabledByAdminUpdatableString$0;
                lambda$getDisabledByAdminUpdatableString$0 = RestrictedPreferenceHelper.this.lambda$getDisabledByAdminUpdatableString$0();
                return lambda$getDisabledByAdminUpdatableString$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDisabledByAdminUpdatableString$0() {
        return this.mContext.getString(R$string.disabled_by_admin_summary_text);
    }

    public void useAdminDisabledSummary(boolean z) {
        this.mDisabledSummary = z;
    }

    public boolean performClick() {
        if (this.mDisabledByAdmin) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
            return true;
        } else if (this.mDisabledByAppOps) {
            RestrictedLockUtilsInternal.sendShowRestrictedSettingDialogIntent(this.mContext, this.packageName, this.uid);
            return true;
        } else {
            return false;
        }
    }

    public void onAttachedToHierarchy() {
        String str = this.mAttrUserRestriction;
        if (str != null) {
            checkRestrictionAndSetDisabled(str, UserHandle.myUserId());
        }
    }

    public void checkRestrictionAndSetDisabled(String str, int i) {
        setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, str, i));
    }

    public RestrictedLockUtils.EnforcedAdmin checkRestrictionEnforced() {
        String str = this.mAttrUserRestriction;
        if (str == null) {
            return null;
        }
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, str, UserHandle.myUserId());
    }

    public boolean setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        boolean z = enforcedAdmin != null;
        this.mEnforcedAdmin = enforcedAdmin;
        if (this.mDisabledByAdmin != z) {
            this.mDisabledByAdmin = z;
            updateDisabledState();
            return true;
        }
        return false;
    }

    public boolean setDisabledByAppOps(boolean z) {
        if (this.mDisabledByAppOps != z) {
            this.mDisabledByAppOps = z;
            updateDisabledState();
            return true;
        }
        return false;
    }

    public boolean isDisabledByAdmin() {
        return this.mDisabledByAdmin;
    }

    public boolean isDisabledByAppOps() {
        return this.mDisabledByAppOps;
    }

    public void updatePackageDetails(String str, int i) {
        this.packageName = str;
        this.uid = i;
    }

    private void updateDisabledState() {
        Preference preference = this.mPreference;
        boolean z = true;
        if (!(preference instanceof RestrictedTopLevelPreference)) {
            preference.setEnabled((this.mDisabledByAdmin || this.mDisabledByAppOps) ? false : true);
        }
        Preference preference2 = this.mPreference;
        if (preference2 instanceof PrimarySwitchPreference) {
            PrimarySwitchPreference primarySwitchPreference = (PrimarySwitchPreference) preference2;
            if (this.mDisabledByAdmin || this.mDisabledByAppOps) {
                z = false;
            }
            primarySwitchPreference.setSwitchEnabled(z);
        }
    }
}
