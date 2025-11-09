package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.net.TetheringManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.LinkedHashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class UsbDetailsFunctionsController extends UsbDetailsController implements SelectorWithWidgetPreference.OnClickListener, DataSaverBackend.Listener {
    private static final boolean DEBUG = Log.isLoggable("UsbFunctionsCtrl", 3);
    static final Map<Long, Integer> FUNCTIONS_MAP;
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Handler mHandler;
    OnStartTetheringCallback mOnStartTetheringCallback;
    long mPreviousFunction;
    private PreferenceCategory mProfilesContainer;
    private TetheringManager mTetheringManager;

    private boolean isAccessoryMode(long j) {
        return (j & 2) != 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_details_functions";
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        FUNCTIONS_MAP = linkedHashMap;
        linkedHashMap.put(4L, Integer.valueOf(R$string.usb_use_file_transfers));
        linkedHashMap.put(32L, Integer.valueOf(R$string.usb_use_tethering));
        linkedHashMap.put(8L, Integer.valueOf(R$string.usb_use_MIDI));
        linkedHashMap.put(16L, Integer.valueOf(R$string.usb_use_photo_transfers));
        linkedHashMap.put(0L, Integer.valueOf(R$string.usb_use_charging_only));
    }

    public UsbDetailsFunctionsController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
        this.mTetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mOnStartTetheringCallback = new OnStartTetheringCallback();
        this.mPreviousFunction = this.mUsbBackend.getCurrentFunctions();
        this.mHandler = new Handler(context.getMainLooper());
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverBackend.addListener(this);
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mProfilesContainer = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        refresh(false, this.mUsbBackend.getDefaultUsbFunctions(), 0, 0);
        for (Long l : FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            int intValue = FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue();
            if (this.mProfilesContainer != null) {
                SelectorWithWidgetPreference profilePreference = getProfilePreference(UsbBackend.usbFunctionsToString(longValue), intValue);
                if (longValue == 32) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("displayPreference, then setEnabled to ");
                    sb.append(!this.mDataSaverEnabled);
                    Log.d("UsbFunctionsCtrl", sb.toString());
                    profilePreference.setEnabled(!this.mDataSaverEnabled);
                }
            }
        }
    }

    private SelectorWithWidgetPreference getProfilePreference(String str, int i) {
        SelectorWithWidgetPreference selectorWithWidgetPreference = (SelectorWithWidgetPreference) this.mProfilesContainer.findPreference(str);
        if (selectorWithWidgetPreference == null) {
            SelectorWithWidgetPreference selectorWithWidgetPreference2 = new SelectorWithWidgetPreference(this.mProfilesContainer.getContext());
            selectorWithWidgetPreference2.setKey(str);
            selectorWithWidgetPreference2.setTitle(i);
            selectorWithWidgetPreference2.setSingleLineTitle(false);
            selectorWithWidgetPreference2.setOnClickListener(this);
            this.mProfilesContainer.addPreference(selectorWithWidgetPreference2);
            return selectorWithWidgetPreference2;
        }
        return selectorWithWidgetPreference;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController
    public void refresh(boolean z, long j, int i, int i2) {
        if (DEBUG) {
            Log.d("UsbFunctionsCtrl", "refresh() connected : " + z + ", functions : " + j + ", powerRole : " + i + ", dataRole : " + i2);
        }
        if (!z || i2 == 1) {
            this.mProfilesContainer.setEnabled(false);
        } else {
            this.mProfilesContainer.setEnabled(true);
        }
        for (Long l : FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            SelectorWithWidgetPreference profilePreference = getProfilePreference(UsbBackend.usbFunctionsToString(longValue), FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue());
            if (this.mUsbBackend.areFunctionsSupported(longValue)) {
                if (isAccessoryMode(j)) {
                    profilePreference.setChecked(4 == longValue);
                } else if (j == 1024) {
                    profilePreference.setChecked(32 == longValue);
                } else {
                    profilePreference.setChecked(j == longValue);
                }
            } else {
                this.mProfilesContainer.removePreference(profilePreference);
            }
        }
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        Log.d("UsbFunctionsCtrl", "DataSaver changed to " + z);
        this.mDataSaverEnabled = z;
        for (Long l : FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            int intValue = FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue();
            if (this.mProfilesContainer != null) {
                SelectorWithWidgetPreference profilePreference = getProfilePreference(UsbBackend.usbFunctionsToString(longValue), intValue);
                if (longValue == 32) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("DataSaver changed, then setEnabled to ");
                    sb.append(!z);
                    Log.d("UsbFunctionsCtrl", sb.toString());
                    profilePreference.setEnabled(!z);
                }
            }
        }
    }

    @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
    public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(selectorWithWidgetPreference.getKey());
        long currentFunctions = this.mUsbBackend.getCurrentFunctions();
        if (DEBUG) {
            Log.d("UsbFunctionsCtrl", "onRadioButtonClicked() function : " + usbFunctionsFromString + ", toString() : " + UsbManager.usbFunctionsToString(usbFunctionsFromString) + ", previousFunction : " + currentFunctions + ", toString() : " + UsbManager.usbFunctionsToString(currentFunctions));
        }
        if (usbFunctionsFromString == currentFunctions || Utils.isMonkeyRunning() || isClickEventIgnored(usbFunctionsFromString, currentFunctions)) {
            return;
        }
        this.mPreviousFunction = currentFunctions;
        SelectorWithWidgetPreference selectorWithWidgetPreference2 = (SelectorWithWidgetPreference) this.mProfilesContainer.findPreference(UsbBackend.usbFunctionsToString(currentFunctions));
        if (selectorWithWidgetPreference2 != null) {
            selectorWithWidgetPreference2.setChecked(false);
            selectorWithWidgetPreference.setChecked(true);
        }
        if (usbFunctionsFromString == 32 || usbFunctionsFromString == 1024) {
            this.mTetheringManager.startTethering(1, new HandlerExecutor(this.mHandler), this.mOnStartTetheringCallback);
        } else {
            this.mUsbBackend.setCurrentFunctions(usbFunctionsFromString);
        }
    }

    private boolean isClickEventIgnored(long j, long j2) {
        return isAccessoryMode(j2) && j == 4;
    }

    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Utils.isMonkeyRunning();
    }

    /* loaded from: classes.dex */
    final class OnStartTetheringCallback implements TetheringManager.StartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringFailed(int i) {
            Log.w("UsbFunctionsCtrl", "onTetheringFailed() error : " + i);
            UsbDetailsFunctionsController usbDetailsFunctionsController = UsbDetailsFunctionsController.this;
            usbDetailsFunctionsController.mUsbBackend.setCurrentFunctions(usbDetailsFunctionsController.mPreviousFunction);
        }
    }
}
