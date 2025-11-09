package com.android.settings.location;

import android.content.Context;
import android.os.UserHandle;
import android.widget.Switch;
import com.android.settings.location.LocationEnabler;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class LocationSwitchBarController implements OnMainSwitchChangeListener, LocationEnabler.LocationModeChangeListener, LifecycleObserver, OnStart, OnStop {
    private final LocationEnabler mLocationEnabler;
    private final SettingsMainSwitchBar mSwitchBar;
    private boolean mValidListener;

    public LocationSwitchBarController(Context context, SettingsMainSwitchBar settingsMainSwitchBar, Lifecycle lifecycle) {
        this.mSwitchBar = settingsMainSwitchBar;
        this.mLocationEnabler = new LocationEnabler(context, this, lifecycle);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mValidListener) {
            return;
        }
        this.mSwitchBar.addOnSwitchChangeListener(this);
        this.mValidListener = true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mValidListener) {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mValidListener = false;
        }
    }

    @Override // com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        boolean isEnabled = this.mLocationEnabler.isEnabled(i);
        int myUserId = UserHandle.myUserId();
        RestrictedLockUtils.EnforcedAdmin shareLocationEnforcedAdmin = this.mLocationEnabler.getShareLocationEnforcedAdmin(myUserId);
        if (!this.mLocationEnabler.hasShareLocationRestriction(myUserId) && shareLocationEnforcedAdmin != null) {
            this.mSwitchBar.setDisabledByAdmin(shareLocationEnforcedAdmin);
        } else {
            this.mSwitchBar.setEnabled(!z);
        }
        if (isEnabled != this.mSwitchBar.isChecked()) {
            if (this.mValidListener) {
                this.mSwitchBar.removeOnSwitchChangeListener(this);
            }
            this.mSwitchBar.setChecked(isEnabled);
            if (this.mValidListener) {
                this.mSwitchBar.addOnSwitchChangeListener(this);
            }
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        this.mLocationEnabler.setLocationEnabled(z);
    }
}
