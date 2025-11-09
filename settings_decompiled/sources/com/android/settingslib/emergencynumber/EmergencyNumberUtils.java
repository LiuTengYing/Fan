package com.android.settingslib.emergencynumber;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.emergency.EmergencyNumber;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda5;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class EmergencyNumberUtils {
    public static final Uri EMERGENCY_NUMBER_OVERRIDE_AUTHORITY = new Uri.Builder().scheme("content").authority("com.android.emergency.gesture").build();
    static final String FALL_BACK_NUMBER = "112";
    private final CarrierConfigManager mCarrierConfigManager;
    private final Context mContext;
    private final TelephonyManager mTelephonyManager;

    public EmergencyNumberUtils(Context context) {
        this.mContext = context;
        if (context.getPackageManager().hasSystemFeature("android.hardware.telephony")) {
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
            return;
        }
        this.mTelephonyManager = null;
        this.mCarrierConfigManager = null;
    }

    public String getDefaultPoliceNumber() {
        List<String> promotedEmergencyNumbers;
        return (this.mTelephonyManager == null || (promotedEmergencyNumbers = getPromotedEmergencyNumbers(1)) == null || promotedEmergencyNumbers.isEmpty()) ? FALL_BACK_NUMBER : promotedEmergencyNumbers.get(0);
    }

    public String getPoliceNumber() {
        String emergencyNumberOverride = getEmergencyNumberOverride();
        return TextUtils.isEmpty(emergencyNumberOverride) ? getDefaultPoliceNumber() : emergencyNumberOverride;
    }

    public void setEmergencyNumberOverride(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("emergency_gesture_call_number", str);
        this.mContext.getContentResolver().call(EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, "SET_EMERGENCY_NUMBER_OVERRIDE", (String) null, bundle);
    }

    public void setEmergencyGestureEnabled(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("emergency_setting_value", z ? 1 : 0);
        this.mContext.getContentResolver().call(EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, "SET_EMERGENCY_GESTURE", (String) null, bundle);
    }

    public void setEmergencySoundEnabled(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("emergency_setting_value", z ? 1 : 0);
        this.mContext.getContentResolver().call(EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, "SET_EMERGENCY_SOUND", (String) null, bundle);
    }

    public boolean getEmergencyGestureEnabled() {
        Bundle call = this.mContext.getContentResolver().call(EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, "GET_EMERGENCY_GESTURE", (String) null, (Bundle) null);
        return call == null || call.getInt("emergency_setting_value", 1) == 1;
    }

    public boolean getEmergencyGestureSoundEnabled() {
        Bundle call = this.mContext.getContentResolver().call(EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, "GET_EMERGENCY_SOUND", (String) null, (Bundle) null);
        return call == null || call.getInt("emergency_setting_value", 0) == 1;
    }

    private String getEmergencyNumberOverride() {
        Bundle call = this.mContext.getContentResolver().call(EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, "GET_EMERGENCY_NUMBER_OVERRIDE", (String) null, (Bundle) null);
        if (call == null) {
            return null;
        }
        return call.getString("emergency_gesture_call_number");
    }

    private List<String> getPromotedEmergencyNumbers(int i) {
        Map<Integer, List<EmergencyNumber>> emergencyNumberList = this.mTelephonyManager.getEmergencyNumberList(i);
        if (emergencyNumberList == null || emergencyNumberList.isEmpty()) {
            Log.w("EmergencyNumberUtils", "Unable to retrieve emergency number lists!");
            return new ArrayList();
        }
        ArrayMap arrayMap = new ArrayMap();
        for (Map.Entry<Integer, List<EmergencyNumber>> entry : emergencyNumberList.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                int intValue = entry.getKey().intValue();
                Log.d("EmergencyNumberUtils", "Emergency numbers for subscription id " + entry.getKey());
                List<EmergencyNumber> arrayList = new ArrayList<>();
                ArrayList arrayList2 = new ArrayList();
                for (EmergencyNumber emergencyNumber : entry.getValue()) {
                    boolean contains = emergencyNumber.getEmergencyNumberSources().contains(16);
                    Log.d("EmergencyNumberUtils", String.format("Number %s, isFromPrioritizedSource %b", emergencyNumber, Boolean.valueOf(contains)));
                    if (contains) {
                        arrayList.add(emergencyNumber);
                    } else {
                        arrayList2.add(emergencyNumber);
                    }
                }
                arrayList.addAll(arrayList2);
                if (!arrayList.isEmpty()) {
                    arrayMap.put(Integer.valueOf(intValue), sanitizeEmergencyNumbers(arrayList, intValue));
                }
            }
        }
        if (arrayMap.isEmpty()) {
            Log.w("EmergencyNumberUtils", "No promoted emergency number found!");
        }
        return (List) arrayMap.get(Integer.valueOf(SubscriptionManager.getDefaultSubscriptionId()));
    }

    private List<String> sanitizeEmergencyNumbers(List<EmergencyNumber> list, int i) {
        ArrayList arrayList = new ArrayList(list);
        final String[] carrierEmergencyNumberPrefixes = getCarrierEmergencyNumberPrefixes(this.mCarrierConfigManager, i);
        return (List) arrayList.stream().map(new Function() { // from class: com.android.settingslib.emergencynumber.EmergencyNumberUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$sanitizeEmergencyNumbers$0;
                lambda$sanitizeEmergencyNumbers$0 = EmergencyNumberUtils.this.lambda$sanitizeEmergencyNumbers$0(carrierEmergencyNumberPrefixes, (EmergencyNumber) obj);
                return lambda$sanitizeEmergencyNumbers$0;
            }
        }).collect(Collectors.toCollection(new ContextualCardManager$$ExternalSyntheticLambda5()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: removePrefix */
    public String lambda$sanitizeEmergencyNumbers$0(EmergencyNumber emergencyNumber, String[] strArr) {
        String number = emergencyNumber.getNumber();
        if (strArr == null || strArr.length == 0) {
            return number;
        }
        for (String str : strArr) {
            if (number.indexOf(str) == 0) {
                Log.d("EmergencyNumberUtils", "Removing prefix " + str + " from " + number);
                return number.substring(str.length());
            }
        }
        return number;
    }

    private static String[] getCarrierEmergencyNumberPrefixes(CarrierConfigManager carrierConfigManager, int i) {
        PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(i);
        if (configForSubId == null) {
            return null;
        }
        return configForSubId.getStringArray("emergency_number_prefix_string_array");
    }
}
