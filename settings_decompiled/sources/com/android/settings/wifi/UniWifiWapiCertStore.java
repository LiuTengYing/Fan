package com.android.settings.wifi;

import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.security.legacykeystore.ILegacyKeystore;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/* loaded from: classes.dex */
public class UniWifiWapiCertStore {
    private static ILegacyKeystore getService() {
        return ILegacyKeystore.Stub.asInterface(ServiceManager.checkService("android.security.legacykeystore"));
    }

    public static boolean put(String str, byte[] bArr) {
        Log.d("UniWifiWapiCertStore", "start install cert : " + str + ", size : " + bArr.length);
        try {
            getService().put(str, 1010, bArr);
            Log.d("UniWifiWapiCertStore", "Successfuly installed alias : " + str + ", to uid : 1010");
            return true;
        } catch (Exception e) {
            Log.e("UniWifiWapiCertStore", "Failed to put " + str + " : " + e);
            return false;
        }
    }

    public static Collection<String> getWapiCertAlias(String str) {
        ArrayList arrayList = new ArrayList();
        try {
            String[] list = getService().list(str, 1010);
            Log.d("UniWifiWapiCertStore", "get prefix : " + str + ", alias : " + Arrays.asList(list));
            for (int i = 0; i < list.length; i++) {
                String substring = list[i].substring(str.length());
                list[i] = substring;
                arrayList.add(substring);
            }
        } catch (Exception e) {
            Log.e("UniWifiWapiCertStore", "Failed to list " + str + " : " + e);
        }
        return arrayList;
    }

    public static boolean remove(String str) {
        try {
            getService().remove(str, 1010);
            Log.d("UniWifiWapiCertStore", "Successfully remove alias : " + str);
            return true;
        } catch (ServiceSpecificException e) {
            if (e.errorCode != 7) {
                Log.e("UniWifiWapiCertStore", "Failed to remove " + str + " : " + e);
                return false;
            }
            return false;
        } catch (Exception e2) {
            Log.e("UniWifiWapiCertStore", "Failed to remove " + str + " : " + e2);
            return false;
        }
    }

    public static boolean installWapiAsCert(String str, byte[] bArr) {
        return put("WAPIAS_" + str, bArr);
    }

    public static boolean installWapiUserCert(String str, byte[] bArr) {
        return put("WAPIUSR_" + str, bArr);
    }

    public static boolean hasAnyWapiCertInstalled() {
        try {
            String[] list = getService().list("WAPIAS_", 1010);
            String[] list2 = getService().list("WAPIUSR_", 1010);
            Log.d("UniWifiWapiCertStore", "hasAnyWapiCertInstalled asAlias length : " + list.length + ", userAlias length : " + list2.length);
            if (list.length <= 0) {
                return list2.length > 0;
            }
            return true;
        } catch (Exception e) {
            Log.e("UniWifiWapiCertStore", "Failed to hasAnyWapiCertInstalled list cert : " + e);
            return false;
        }
    }
}
