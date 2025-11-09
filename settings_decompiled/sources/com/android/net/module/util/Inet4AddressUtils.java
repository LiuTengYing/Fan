package com.android.net.module.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
/* loaded from: classes.dex */
public class Inet4AddressUtils {
    public static Inet4Address intToInet4AddressHTH(int i) {
        try {
            return (Inet4Address) InetAddress.getByAddress(new byte[]{(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)});
        } catch (UnknownHostException unused) {
            throw new AssertionError();
        }
    }

    public static int prefixLengthToV4NetmaskIntHTH(int i) throws IllegalArgumentException {
        if (i < 0 || i > 32) {
            throw new IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)");
        }
        if (i == 0) {
            return 0;
        }
        return (-1) << (32 - i);
    }

    public static Inet4Address getPrefixMaskAsInet4Address(int i) throws IllegalArgumentException {
        return intToInet4AddressHTH(prefixLengthToV4NetmaskIntHTH(i));
    }
}
