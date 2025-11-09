package com.android.settings.datausage;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.datausage.DataSaverBackend;
/* loaded from: classes.dex */
public class DataSaverPreference extends Preference implements DataSaverBackend.Listener {
    private final DataSaverBackend mDataSaverBackend;

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    public DataSaverPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDataSaverBackend = new DataSaverBackend(context);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDataSaverBackend.addListener(this);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mDataSaverBackend.remListener(this);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        setSummary(z ? R$string.data_saver_on : R$string.data_saver_off);
    }
}
