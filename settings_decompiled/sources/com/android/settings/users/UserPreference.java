package com.android.settings.users;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedPreference;
import java.util.Comparator;
/* loaded from: classes.dex */
public class UserPreference extends RestrictedPreference {
    public static final Comparator<UserPreference> SERIAL_NUMBER_COMPARATOR = new Comparator() { // from class: com.android.settings.users.UserPreference$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = UserPreference.lambda$static$0((UserPreference) obj, (UserPreference) obj2);
            return lambda$static$0;
        }
    };
    private int mSerialNumber;
    private int mUserId;

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(UserPreference userPreference, UserPreference userPreference2) {
        if (userPreference == null) {
            return -1;
        }
        if (userPreference2 == null) {
            return 1;
        }
        int serialNumber = userPreference.getSerialNumber();
        int serialNumber2 = userPreference2.getSerialNumber();
        if (serialNumber < serialNumber2) {
            return -1;
        }
        return serialNumber > serialNumber2 ? 1 : 0;
    }

    public UserPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -10);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UserPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mSerialNumber = -1;
        this.mUserId = i;
        useAdminDisabledSummary(true);
    }

    private void dimIcon(boolean z) {
        Drawable icon = getIcon();
        if (icon != null) {
            icon.mutate().setAlpha(z ? 102 : 255);
            setIcon(icon);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        dimIcon(isDisabledByAdmin());
    }

    private int getSerialNumber() {
        if (this.mUserId == UserHandle.myUserId()) {
            return Integer.MIN_VALUE;
        }
        if (this.mSerialNumber < 0) {
            if (this.mUserId == -10) {
                return Integer.MAX_VALUE;
            }
            int userSerialNumber = ((UserManager) getContext().getSystemService("user")).getUserSerialNumber(this.mUserId);
            this.mSerialNumber = userSerialNumber;
            if (userSerialNumber < 0) {
                return this.mUserId;
            }
        }
        return this.mSerialNumber;
    }

    public int getUserId() {
        return this.mUserId;
    }
}
