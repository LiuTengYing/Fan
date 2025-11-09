package com.android.settings.password;

import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.os.UserManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ChooseLockGenericController {
    private final int mAppRequestedMinComplexity;
    private final Context mContext;
    private final boolean mDevicePasswordRequirementOnly;
    private final boolean mHideInsecureScreenLockTypes;
    private final LockPatternUtils mLockPatternUtils;
    private final ManagedLockPasswordProvider mManagedPasswordProvider;
    private final int mUnificationProfileId;
    private final int mUserId;

    public ChooseLockGenericController(Context context, int i, ManagedLockPasswordProvider managedLockPasswordProvider, LockPatternUtils lockPatternUtils, boolean z, int i2, boolean z2, int i3) {
        this.mContext = context;
        this.mUserId = i;
        this.mManagedPasswordProvider = managedLockPasswordProvider;
        this.mLockPatternUtils = lockPatternUtils;
        this.mHideInsecureScreenLockTypes = z;
        this.mAppRequestedMinComplexity = i2;
        this.mDevicePasswordRequirementOnly = z2;
        this.mUnificationProfileId = i3;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private int mAppRequestedMinComplexity;
        private final Context mContext;
        private boolean mDevicePasswordRequirementOnly;
        private boolean mHideInsecureScreenLockTypes;
        private final LockPatternUtils mLockPatternUtils;
        private final ManagedLockPasswordProvider mManagedPasswordProvider;
        private int mUnificationProfileId;
        private final int mUserId;

        public Builder(Context context, int i) {
            this(context, i, new LockPatternUtils(context));
        }

        public Builder(Context context, int i, LockPatternUtils lockPatternUtils) {
            this(context, i, ManagedLockPasswordProvider.get(context, i), lockPatternUtils);
        }

        Builder(Context context, int i, ManagedLockPasswordProvider managedLockPasswordProvider, LockPatternUtils lockPatternUtils) {
            this.mHideInsecureScreenLockTypes = false;
            this.mAppRequestedMinComplexity = 0;
            this.mDevicePasswordRequirementOnly = false;
            this.mUnificationProfileId = -10000;
            this.mContext = context;
            this.mUserId = i;
            this.mManagedPasswordProvider = managedLockPasswordProvider;
            this.mLockPatternUtils = lockPatternUtils;
        }

        public Builder setAppRequestedMinComplexity(int i) {
            this.mAppRequestedMinComplexity = i;
            return this;
        }

        public Builder setEnforceDevicePasswordRequirementOnly(boolean z) {
            this.mDevicePasswordRequirementOnly = z;
            return this;
        }

        public Builder setProfileToUnify(int i) {
            this.mUnificationProfileId = i;
            return this;
        }

        public Builder setHideInsecureScreenLockTypes(boolean z) {
            this.mHideInsecureScreenLockTypes = z;
            return this;
        }

        public ChooseLockGenericController build() {
            return new ChooseLockGenericController(this.mContext, this.mUserId, this.mManagedPasswordProvider, this.mLockPatternUtils, this.mHideInsecureScreenLockTypes, this.mAppRequestedMinComplexity, this.mDevicePasswordRequirementOnly, this.mUnificationProfileId);
        }
    }

    public boolean isScreenLockVisible(ScreenLockType screenLockType) {
        boolean isManagedProfile = ((UserManager) this.mContext.getSystemService(UserManager.class)).isManagedProfile(this.mUserId);
        switch (AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[screenLockType.ordinal()]) {
            case 1:
                return (this.mHideInsecureScreenLockTypes || this.mContext.getResources().getBoolean(R$bool.config_hide_none_security_option) || isManagedProfile) ? false : true;
            case 2:
                return (this.mHideInsecureScreenLockTypes || this.mContext.getResources().getBoolean(R$bool.config_hide_swipe_security_option) || isManagedProfile) ? false : true;
            case 3:
                return this.mManagedPasswordProvider.isManagedPasswordChoosable();
            case 4:
            case 5:
            case 6:
                return this.mLockPatternUtils.hasSecureLockScreen();
            default:
                return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockGenericController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ScreenLockType;

        static {
            int[] iArr = new int[ScreenLockType.values().length];
            $SwitchMap$com$android$settings$password$ScreenLockType = iArr;
            try {
                iArr[ScreenLockType.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.SWIPE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.MANAGED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PIN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PATTERN.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PASSWORD.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    public boolean isScreenLockEnabled(ScreenLockType screenLockType) {
        return !this.mLockPatternUtils.isCredentialsDisabledForUser(this.mUserId) && screenLockType.maxQuality >= upgradeQuality(0);
    }

    public int upgradeQuality(int i) {
        return Math.max(i, Math.max(LockPatternUtils.credentialTypeToPasswordQuality(getAggregatedPasswordMetrics().credType), PasswordMetrics.complexityLevelToMinQuality(getAggregatedPasswordComplexity())));
    }

    public CharSequence getTitle(ScreenLockType screenLockType) {
        switch (AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[screenLockType.ordinal()]) {
            case 1:
                return this.mContext.getText(R$string.unlock_set_unlock_off_title);
            case 2:
                return this.mContext.getText(R$string.unlock_set_unlock_none_title);
            case 3:
                return this.mManagedPasswordProvider.getPickerOptionTitle(false);
            case 4:
                return this.mContext.getText(R$string.unlock_set_unlock_pin_title);
            case 5:
                return this.mContext.getText(R$string.unlock_set_unlock_pattern_title);
            case 6:
                return this.mContext.getText(R$string.unlock_set_unlock_password_title);
            default:
                return null;
        }
    }

    public List<ScreenLockType> getVisibleAndEnabledScreenLockTypes() {
        ScreenLockType[] values;
        ArrayList arrayList = new ArrayList();
        for (ScreenLockType screenLockType : ScreenLockType.values()) {
            if (isScreenLockVisible(screenLockType) && isScreenLockEnabled(screenLockType)) {
                arrayList.add(screenLockType);
            }
        }
        return arrayList;
    }

    public PasswordMetrics getAggregatedPasswordMetrics() {
        PasswordMetrics requestedPasswordMetrics = this.mLockPatternUtils.getRequestedPasswordMetrics(this.mUserId, this.mDevicePasswordRequirementOnly);
        int i = this.mUnificationProfileId;
        if (i != -10000) {
            requestedPasswordMetrics.maxWith(this.mLockPatternUtils.getRequestedPasswordMetrics(i));
        }
        return requestedPasswordMetrics;
    }

    public int getAggregatedPasswordComplexity() {
        int max = Math.max(this.mAppRequestedMinComplexity, this.mLockPatternUtils.getRequestedPasswordComplexity(this.mUserId, this.mDevicePasswordRequirementOnly));
        int i = this.mUnificationProfileId;
        return i != -10000 ? Math.max(max, this.mLockPatternUtils.getRequestedPasswordComplexity(i)) : max;
    }

    public boolean isScreenLockRestrictedByAdmin() {
        return getAggregatedPasswordMetrics().credType != -1 || isComplexityProvidedByAdmin();
    }

    public boolean isComplexityProvidedByAdmin() {
        int aggregatedPasswordComplexity = getAggregatedPasswordComplexity();
        return aggregatedPasswordComplexity > this.mAppRequestedMinComplexity && aggregatedPasswordComplexity > 0;
    }
}
