package com.android.settings.vpn2;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.Preference;
import com.android.internal.net.VpnProfile;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
/* loaded from: classes.dex */
public class LegacyVpnPreference extends ManageablePreference {
    private VpnProfile mProfile;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LegacyVpnPreference(Context context) {
        super(context, null);
        setIcon(R$drawable.ic_vpn_key);
        setIconSize(2);
    }

    public VpnProfile getProfile() {
        return this.mProfile;
    }

    public void setProfile(VpnProfile vpnProfile) {
        VpnProfile vpnProfile2 = this.mProfile;
        String str = vpnProfile2 != null ? vpnProfile2.name : null;
        String str2 = vpnProfile != null ? vpnProfile.name : null;
        if (!TextUtils.equals(str, str2)) {
            setTitle(str2);
            notifyHierarchyChanged();
        }
        this.mProfile = vpnProfile;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference, java.lang.Comparable
    public int compareTo(Preference preference) {
        if (preference instanceof LegacyVpnPreference) {
            LegacyVpnPreference legacyVpnPreference = (LegacyVpnPreference) preference;
            int i = legacyVpnPreference.mState - this.mState;
            if (i == 0) {
                int compareToIgnoreCase = this.mProfile.name.compareToIgnoreCase(legacyVpnPreference.mProfile.name);
                if (compareToIgnoreCase == 0) {
                    VpnProfile vpnProfile = this.mProfile;
                    int i2 = vpnProfile.type;
                    VpnProfile vpnProfile2 = legacyVpnPreference.mProfile;
                    int i3 = i2 - vpnProfile2.type;
                    return i3 == 0 ? vpnProfile.key.compareTo(vpnProfile2.key) : i3;
                }
                return compareToIgnoreCase;
            }
            return i;
        } else if (preference instanceof AppPreference) {
            return (this.mState == 3 || ((AppPreference) preference).getState() != 3) ? -1 : 1;
        } else {
            return super.compareTo(preference);
        }
    }

    @Override // com.android.settings.widget.GearPreference, android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R$id.settings_button && isDisabledByAdmin()) {
            performClick();
        } else {
            super.onClick(view);
        }
    }
}
