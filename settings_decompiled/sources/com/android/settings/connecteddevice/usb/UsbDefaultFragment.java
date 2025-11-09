package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.TetheringManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class UsbDefaultFragment extends RadioButtonPickerFragment {
    long mCurrentFunctions;
    Handler mHandler;
    long mPreviousFunctions;
    TetheringManager mTetheringManager;
    UsbBackend mUsbBackend;
    private UsbConnectionBroadcastReceiver mUsbReceiver;
    OnStartTetheringCallback mOnStartTetheringCallback = new OnStartTetheringCallback();
    boolean mIsStartTethering = false;
    private boolean mIsConnected = false;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() { // from class: com.android.settings.connecteddevice.usb.UsbDefaultFragment$$ExternalSyntheticLambda0
        @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
        public final void onUsbConnectionChanged(boolean z, long j, int i, int i2, boolean z2) {
            UsbDefaultFragment.this.lambda$new$0(z, j, i, i2, z2);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1312;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, long j, int i, int i2, boolean z2) {
        long defaultUsbFunctions = this.mUsbBackend.getDefaultUsbFunctions();
        Log.d("UsbDefaultFragment", "UsbConnectionListener() connected : " + z + ", functions : " + j + ", defaultFunctions : " + defaultUsbFunctions + ", mIsStartTethering : " + this.mIsStartTethering + ", isUsbConfigured : " + z2);
        if (z && !this.mIsConnected && ((defaultUsbFunctions == 32 || defaultUsbFunctions == 1024) && defaultUsbFunctions == j && !this.mIsStartTethering)) {
            this.mCurrentFunctions = defaultUsbFunctions;
            startTethering();
        }
        if ((this.mIsStartTethering || z2) && z) {
            this.mCurrentFunctions = j;
            refresh(j);
            this.mIsStartTethering = false;
        }
        this.mIsConnected = z;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUsbBackend = new UsbBackend(context);
        this.mTetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        this.mHandler = new Handler(context.getMainLooper());
        getSettingsLifecycle().addObserver(this.mUsbReceiver);
        this.mCurrentFunctions = this.mUsbBackend.getDefaultUsbFunctions();
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        getPreferenceScreen().addPreference(new FooterPreference.Builder(getActivity()).setTitle(R$string.usb_default_info).build());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.usb_default_fragment;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList newArrayList = Lists.newArrayList();
        for (Long l : UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            final String string = getContext().getString(UsbDetailsFunctionsController.FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue());
            final String usbFunctionsToString = UsbBackend.usbFunctionsToString(longValue);
            if (this.mUsbBackend.areFunctionsSupported(longValue)) {
                newArrayList.add(new CandidateInfo(true) { // from class: com.android.settings.connecteddevice.usb.UsbDefaultFragment.1
                    @Override // com.android.settingslib.widget.CandidateInfo
                    public Drawable loadIcon() {
                        return null;
                    }

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public CharSequence loadLabel() {
                        return string;
                    }

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public String getKey() {
                        return usbFunctionsToString;
                    }
                });
            }
        }
        return newArrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        long defaultUsbFunctions = this.mUsbBackend.getDefaultUsbFunctions();
        if (defaultUsbFunctions == 1024) {
            defaultUsbFunctions = 32;
        }
        return UsbBackend.usbFunctionsToString(defaultUsbFunctions);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(str);
        this.mPreviousFunctions = this.mUsbBackend.getCurrentFunctions();
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        if (usbFunctionsFromString == 32 || usbFunctionsFromString == 1024) {
            this.mCurrentFunctions = usbFunctionsFromString;
            startTethering();
            return true;
        }
        this.mIsStartTethering = false;
        this.mCurrentFunctions = usbFunctionsFromString;
        this.mUsbBackend.setDefaultUsbFunctions(usbFunctionsFromString);
        return true;
    }

    private void startTethering() {
        Log.d("UsbDefaultFragment", "startTethering()");
        this.mIsStartTethering = true;
        this.mTetheringManager.startTethering(1, new HandlerExecutor(this.mHandler), this.mOnStartTetheringCallback);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mCurrentFunctions = this.mUsbBackend.getCurrentFunctions();
        Log.d("UsbDefaultFragment", "onPause() : current functions : " + this.mCurrentFunctions);
        this.mUsbBackend.setDefaultUsbFunctions(this.mCurrentFunctions);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class OnStartTetheringCallback implements TetheringManager.StartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringStarted() {
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mCurrentFunctions = usbDefaultFragment.mUsbBackend.getCurrentFunctions();
            Log.d("UsbDefaultFragment", "onTetheringStarted() : mCurrentFunctions " + UsbDefaultFragment.this.mCurrentFunctions);
            UsbDefaultFragment usbDefaultFragment2 = UsbDefaultFragment.this;
            usbDefaultFragment2.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment2.mCurrentFunctions);
        }

        public void onTetheringFailed(int i) {
            Log.w("UsbDefaultFragment", "onTetheringFailed() error : " + i);
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment.mPreviousFunctions);
            UsbDefaultFragment.this.updateCandidates();
        }
    }

    private void refresh(long j) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (Long l : UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            SelectorWithWidgetPreference selectorWithWidgetPreference = (SelectorWithWidgetPreference) preferenceScreen.findPreference(UsbBackend.usbFunctionsToString(longValue));
            if (selectorWithWidgetPreference != null) {
                boolean areFunctionsSupported = this.mUsbBackend.areFunctionsSupported(longValue);
                selectorWithWidgetPreference.setEnabled(areFunctionsSupported);
                if (areFunctionsSupported) {
                    if (j == 1024) {
                        selectorWithWidgetPreference.setChecked(32 == longValue);
                    } else {
                        selectorWithWidgetPreference.setChecked(j == longValue);
                    }
                }
            }
        }
    }
}
