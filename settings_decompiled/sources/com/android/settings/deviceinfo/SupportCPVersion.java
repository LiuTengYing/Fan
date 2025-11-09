package com.android.settings.deviceinfo;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$string;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vendor.sprd.hardware.connmgr.V1_0.IConnmgr;
/* loaded from: classes.dex */
public class SupportCPVersion {
    private static SupportCPVersion mInstance;
    private static int mLengthOfCp2;
    private String mBasebandVersion;
    private IConnmgr mIConnmgr;

    public static SupportCPVersion getInstance() {
        if (mInstance == null) {
            mInstance = new SupportCPVersion();
        }
        return mInstance;
    }

    public String getBasedSummary(Context context, String str) {
        try {
            String str2 = SystemProperties.get(str, context.getResources().getString(R$string.device_info_default));
            this.mBasebandVersion = str2;
            String cp2Version = getCp2Version();
            Log.d("SupportCPVersion", "getCp2Version():" + cp2Version);
            String str3 = "";
            if (cp2Version != null) {
                Log.d("SupportCPVersion", " temp = " + cp2Version);
                if (cp2Version.startsWith("Platform")) {
                    Matcher matcher = Pattern.compile("PlatformVersion:(\\S+)ProjectVersion:(\\S+)HWVersion:(\\S+)").matcher(cp2Version.replaceAll("\\s+", ""));
                    if (!matcher.matches()) {
                        Log.e("SupportCPVersion", "Regex did not match on cp2 version: ");
                    } else {
                        String group = matcher.group(3);
                        String substring = group.substring(group.indexOf("modem") + 5);
                        String substring2 = substring.substring(10);
                        str3 = matcher.group(1) + "|" + matcher.group(2) + "|" + substring.substring(0, 10) + " " + substring2;
                    }
                } else if (cp2Version.startsWith("WCN_VER")) {
                    Matcher matcher2 = Pattern.compile("WCN_VER:(.*)~(.*)~(.*)~(.*)").matcher(cp2Version);
                    Log.d("SupportCPVersion", "temp length=" + cp2Version.length());
                    str3 = getExactResultForCp2(matcher2, cp2Version);
                } else {
                    Log.e("SupportCPVersion", "cp2 version is error");
                }
            }
            if (!TextUtils.isEmpty(str3)) {
                this.mBasebandVersion = str2 + "\n" + str3;
            }
            Log.d("SupportCPVersion", "pro = " + str2 + " cp2 = " + str3);
        } catch (Exception e) {
            Log.d("SupportCPVersion", "Exceptipon:" + e.toString());
        }
        return this.mBasebandVersion;
    }

    private String getExactResultForCp2(Matcher matcher, String str) {
        StringBuilder sb = new StringBuilder("");
        if (matcher.matches()) {
            int i = 0;
            while (i < matcher.groupCount() - 1) {
                int i2 = i + 1;
                String group = matcher.group(i2);
                if (group.contains("Version:")) {
                    Log.d("SupportCPVersion", "index of : = " + group.indexOf(58));
                    sb.append(group.substring(group.indexOf(58) + 1, group.length()));
                } else {
                    sb.append(group);
                }
                if (i != matcher.groupCount() - 2) {
                    sb.append("|");
                }
                i = i2;
            }
            Log.d("SupportCPVersion", " base wcn_ver cp2 info : " + sb.toString());
            Log.d("SupportCPVersion", " group 4  info :" + matcher.group(4) + "length () =" + matcher.group(4).length());
            if (!TextUtils.isEmpty(matcher.group(matcher.groupCount()).replaceAll("\\s+", "")) && str.charAt(mLengthOfCp2 - 2) != '~') {
                sb.append("|");
                sb.append(matcher.group(matcher.groupCount()));
            }
            Log.d("SupportCPVersion", " wcn_ver cp2 info : " + sb.toString());
        }
        return sb.toString();
    }

    public IConnmgr getConnmgrMockable() throws RemoteException {
        return IConnmgr.getService();
    }

    private String SendStringCommand(String str) {
        try {
            return this.mIConnmgr.SendStringCommand(str);
        } catch (RemoteException unused) {
            return "";
        }
    }

    public String getCp2Version() {
        Log.d("SupportCPVersion", "start getting the cp2 info by hidl");
        try {
            IConnmgr connmgrMockable = getConnmgrMockable();
            this.mIConnmgr = connmgrMockable;
            if (connmgrMockable == null) {
                Log.d("SupportCPVersion", "mIConnmgr == null");
                return "";
            }
            String SendStringCommand = SendStringCommand("wcn at+spatgetcp2info ");
            mLengthOfCp2 = SendStringCommand.length();
            Log.d("SupportCPVersion", " has got the cp2 info ");
            return SendStringCommand;
        } catch (RemoteException unused) {
            Log.d("SupportCPVersion", "remoteexception");
            return "";
        } catch (NoSuchElementException unused2) {
            return "";
        }
    }
}
