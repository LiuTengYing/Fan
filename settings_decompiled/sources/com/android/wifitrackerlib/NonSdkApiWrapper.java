package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.vcn.VcnTransportInfo;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.UserManager;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import com.android.settingslib.HelpUtils;
/* loaded from: classes2.dex */
class NonSdkApiWrapper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startCaptivePortalApp(ConnectivityManager connectivityManager, Network network) {
        connectivityManager.startCaptivePortalApp(network);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence linkifyAnnotation(final Context context, CharSequence charSequence, String str, final String str2) {
        Annotation[] annotationArr;
        if (TextUtils.isEmpty(str2)) {
            return charSequence;
        }
        SpannableString spannableString = new SpannableString(charSequence);
        for (Annotation annotation : (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class)) {
            if (TextUtils.equals(annotation.getValue(), str)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
                ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.android.wifitrackerlib.NonSdkApiWrapper.1
                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        view.startActivityForResult(HelpUtils.getHelpIntent(context, str2, view.getClass().getName()), 0);
                    }
                };
                spannableStringBuilder.setSpan(clickableSpan, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), spannableString.getSpanFlags(clickableSpan));
                return spannableStringBuilder;
            }
        }
        return charSequence;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isVcnOverWifi(NetworkCapabilities networkCapabilities) {
        VcnTransportInfo transportInfo = networkCapabilities.getTransportInfo();
        return (transportInfo == null || !(transportInfo instanceof VcnTransportInfo) || transportInfo.getWifiInfo() == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isDemoMode(Context context) {
        return UserManager.isDeviceInDemoMode(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerSystemDefaultNetworkCallback(ConnectivityManager connectivityManager, ConnectivityManager.NetworkCallback networkCallback, Handler handler) {
        connectivityManager.registerSystemDefaultNetworkCallback(networkCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPrimary(WifiInfo wifiInfo) {
        return wifiInfo.isPrimary();
    }
}
