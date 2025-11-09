package com.android.settings.wifi;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;
import com.android.wifitrackerlib.WifiEntry;
/* loaded from: classes.dex */
public class LongPressWifiEntryPreference extends WifiEntryPreference {
    private final Fragment mFragment;

    public LongPressWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment) {
        super(context, wifiEntry);
        this.mFragment = fragment;
    }

    @Override // com.android.settings.wifi.WifiEntryPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            preferenceViewHolder.itemView.setOnCreateContextMenuListener(fragment);
            preferenceViewHolder.itemView.setTag(this);
            preferenceViewHolder.itemView.setLongClickable(true);
        }
    }

    @Override // com.android.settings.wifi.WifiEntryPreference
    public void refresh() {
        super.refresh();
        setEnabled(shouldEnabled());
    }

    boolean shouldEnabled() {
        WifiEntry wifiEntry = getWifiEntry();
        if (wifiEntry == null) {
            return false;
        }
        boolean canConnect = wifiEntry.canConnect();
        if (canConnect) {
            return canConnect;
        }
        if (wifiEntry.canDisconnect() || wifiEntry.isSaved()) {
            return true;
        }
        return canConnect;
    }
}
