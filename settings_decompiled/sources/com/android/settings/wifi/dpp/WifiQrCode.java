package com.android.settings.wifi.dpp;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public class WifiQrCode {
    private String mInformation;
    private String mPublicKey;
    private String mQrCode;
    private String mScheme;
    private WifiNetworkConfig mWifiNetworkConfig;

    public WifiQrCode(String str) throws IllegalArgumentException {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Empty QR code");
        }
        this.mQrCode = str;
        if (str.startsWith("DPP:")) {
            this.mScheme = "DPP";
            parseWifiDppQrCode(str);
        } else if (str.startsWith("WIFI:")) {
            this.mScheme = "WIFI";
            parseZxingWifiQrCode(str);
        } else {
            throw new IllegalArgumentException("Invalid scheme");
        }
    }

    private void parseWifiDppQrCode(String str) throws IllegalArgumentException {
        List<String> keyValueList = getKeyValueList(str, "DPP:", ";");
        String valueOrNull = getValueOrNull(keyValueList, "K:");
        if (TextUtils.isEmpty(valueOrNull)) {
            throw new IllegalArgumentException("Invalid format");
        }
        this.mPublicKey = valueOrNull;
        this.mInformation = getValueOrNull(keyValueList, "I:");
    }

    private void parseZxingWifiQrCode(String str) throws IllegalArgumentException {
        List<String> keyValueList = getKeyValueList(str, "WIFI:", ";");
        String valueOrNull = getValueOrNull(keyValueList, "T:");
        String valueOrNull2 = getValueOrNull(keyValueList, "S:");
        String valueOrNull3 = getValueOrNull(keyValueList, "P:");
        WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(removeBackSlash(valueOrNull), removeBackSlash(valueOrNull2), removeBackSlash(valueOrNull3), "true".equalsIgnoreCase(getValueOrNull(keyValueList, "H:")), -1, false);
        this.mWifiNetworkConfig = validConfigOrNull;
        if (validConfigOrNull == null) {
            throw new IllegalArgumentException("Invalid format");
        }
    }

    private List<String> getKeyValueList(String str, String str2, String str3) {
        String substring = str.substring(str2.length());
        return Arrays.asList(substring.split("(?<!\\\\)" + Pattern.quote(str3)));
    }

    private String getValueOrNull(List<String> list, String str) {
        for (String str2 : list) {
            if (str2.startsWith(str)) {
                return str2.substring(str.length());
            }
        }
        return null;
    }

    String removeBackSlash(String str) {
        char[] charArray;
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (char c : str.toCharArray()) {
            if (c != '\\') {
                sb.append(c);
            } else if (z) {
                sb.append(c);
            } else {
                z = true;
            }
            z = false;
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getQrCode() {
        return this.mQrCode;
    }

    public String getScheme() {
        return this.mScheme;
    }

    String getPublicKey() {
        return this.mPublicKey;
    }

    public String getInformation() {
        return this.mInformation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiNetworkConfig getWifiNetworkConfig() {
        WifiNetworkConfig wifiNetworkConfig = this.mWifiNetworkConfig;
        if (wifiNetworkConfig == null) {
            return null;
        }
        return new WifiNetworkConfig(wifiNetworkConfig);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WifiQrCode getValidWifiDppQrCodeOrNull(String str) {
        WifiQrCode wifiQrCode;
        try {
            wifiQrCode = new WifiQrCode(str);
        } catch (IllegalArgumentException unused) {
        }
        if ("DPP".equals(wifiQrCode.getScheme())) {
            return wifiQrCode;
        }
        return null;
    }
}
