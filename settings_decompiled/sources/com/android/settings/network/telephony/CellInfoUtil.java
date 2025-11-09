package com.android.settings.network.telephony;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.android.internal.telephony.OperatorInfo;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class CellInfoUtil {
    public static String getNetworkTitle(CellIdentity cellIdentity, String str) {
        String string = getResourcesForMccMnc(str).getString(135004170);
        if (TextUtils.isEmpty(string)) {
            if (cellIdentity != null) {
                String objects = Objects.toString(cellIdentity.getOperatorAlphaLong(), "");
                if (TextUtils.isEmpty(objects)) {
                    objects = Objects.toString(cellIdentity.getOperatorAlphaShort(), "");
                }
                if (!TextUtils.isEmpty(objects)) {
                    return objects;
                }
            }
            return TextUtils.isEmpty(str) ? "" : BidiFormatter.getInstance().unicodeWrap(str, TextDirectionHeuristics.LTR);
        }
        return string;
    }

    public static CellIdentity getCellIdentity(CellInfo cellInfo) {
        if (cellInfo == null) {
            return null;
        }
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return ((CellInfoTdscdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoNr) {
            return ((CellInfoNr) cellInfo).getCellIdentity();
        }
        return null;
    }

    public static CellInfo convertOperatorInfoToCellInfo(OperatorInfo operatorInfo) {
        String str;
        String str2;
        String operatorNumeric = operatorInfo.getOperatorNumeric();
        if (operatorNumeric == null || !operatorNumeric.matches("^[0-9]{5,6}$")) {
            str = null;
            str2 = null;
        } else {
            String substring = operatorNumeric.substring(0, 3);
            str2 = operatorNumeric.substring(3);
            str = substring;
        }
        CellIdentityGsm cellIdentityGsm = new CellIdentityGsm(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, str, str2, operatorInfo.getOperatorAlphaLong(), operatorInfo.getOperatorAlphaShort(), Collections.emptyList());
        CellInfoGsm cellInfoGsm = new CellInfoGsm();
        cellInfoGsm.setCellIdentity(cellIdentityGsm);
        return cellInfoGsm;
    }

    public static String cellInfoListToString(List<CellInfo> list) {
        return (String) list.stream().map(new Function() { // from class: com.android.settings.network.telephony.CellInfoUtil$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String cellInfoToString;
                cellInfoToString = CellInfoUtil.cellInfoToString((CellInfo) obj);
                return cellInfoToString;
            }
        }).collect(Collectors.joining(", "));
    }

    public static String cellInfoToString(CellInfo cellInfo) {
        CharSequence charSequence;
        String simpleName = cellInfo.getClass().getSimpleName();
        CellIdentity cellIdentity = getCellIdentity(cellInfo);
        String cellIdentityMcc = getCellIdentityMcc(cellIdentity);
        String cellIdentityMnc = getCellIdentityMnc(cellIdentity);
        CharSequence charSequence2 = null;
        if (cellIdentity != null) {
            charSequence2 = cellIdentity.getOperatorAlphaLong();
            charSequence = cellIdentity.getOperatorAlphaShort();
        } else {
            charSequence = null;
        }
        return String.format("{CellType = %s, isRegistered = %b, mcc = %s, mnc = %s, alphaL = %s, alphaS = %s}", simpleName, Boolean.valueOf(cellInfo.isRegistered()), cellIdentityMcc, cellIdentityMnc, charSequence2, charSequence);
    }

    public static String getCellIdentityMccMnc(CellIdentity cellIdentity) {
        String cellIdentityMcc = getCellIdentityMcc(cellIdentity);
        String cellIdentityMnc = getCellIdentityMnc(cellIdentity);
        if (cellIdentityMcc == null || cellIdentityMnc == null) {
            return null;
        }
        return cellIdentityMcc + cellIdentityMnc;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getCellIdentityMcc(android.telephony.CellIdentity r2) {
        /*
            r0 = 0
            if (r2 == 0) goto L3a
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto Le
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMccString()
            goto L3b
        Le:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L19
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMccString()
            goto L3b
        L19:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L24
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMccString()
            goto L3b
        L24:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L2f
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMccString()
            goto L3b
        L2f:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L3a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r2 = r2.getMccString()
            goto L3b
        L3a:
            r2 = r0
        L3b:
            if (r2 != 0) goto L3e
            goto L3f
        L3e:
            r0 = r2
        L3f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getCellIdentityMcc(android.telephony.CellIdentity):java.lang.String");
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getCellIdentityMnc(android.telephony.CellIdentity r2) {
        /*
            r0 = 0
            if (r2 == 0) goto L3a
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto Le
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMncString()
            goto L3b
        Le:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L19
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMncString()
            goto L3b
        L19:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L24
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMncString()
            goto L3b
        L24:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L2f
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMncString()
            goto L3b
        L2f:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L3a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r2 = r2.getMncString()
            goto L3b
        L3a:
            r2 = r0
        L3b:
            if (r2 != 0) goto L3e
            goto L3f
        L3e:
            r0 = r2
        L3f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getCellIdentityMnc(android.telephony.CellIdentity):java.lang.String");
    }

    public static int getCellType(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoNr) {
            return 6;
        }
        if (cellInfo instanceof CellInfoLte) {
            return 3;
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return 4;
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return 5;
        }
        return cellInfo instanceof CellInfoGsm ? 1 : 0;
    }

    public static int checkCellType(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoNr) {
            return 5;
        }
        if (cellInfo instanceof CellInfoLte) {
            return 4;
        }
        if ((cellInfo instanceof CellInfoWcdma) || (cellInfo instanceof CellInfoTdscdma)) {
            return 3;
        }
        return cellInfo instanceof CellInfoGsm ? 2 : 0;
    }

    public static OperatorInfo getOperatorInfoFromCellInfo(CellInfo cellInfo) {
        OperatorInfo operatorInfo;
        String str;
        if (cellInfo instanceof CellInfoNr) {
            CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
            CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
            String mccString = cellIdentityNr.getMccString();
            String mncString = cellIdentityNr.getMncString();
            if (mccString == null || mncString == null) {
                str = null;
            } else {
                str = mccString + mncString;
            }
            return new OperatorInfo(getOperatorAlphaLong(6, cellInfoNr), (String) cellInfoNr.getCellIdentity().getOperatorAlphaShort(), str + " 11");
        } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            return new OperatorInfo(getOperatorAlphaLong(3, cellInfoLte), (String) cellInfoLte.getCellIdentity().getOperatorAlphaShort(), cellInfoLte.getCellIdentity().getMobileNetworkOperator() + " 7");
        } else {
            if (cellInfo instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                operatorInfo = new OperatorInfo(getOperatorAlphaLong(4, cellInfoWcdma), (String) cellInfoWcdma.getCellIdentity().getOperatorAlphaShort(), cellInfoWcdma.getCellIdentity().getMobileNetworkOperator() + " 2");
            } else if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                return new OperatorInfo(getOperatorAlphaLong(1, cellInfoGsm), (String) cellInfoGsm.getCellIdentity().getOperatorAlphaShort(), cellInfoGsm.getCellIdentity().getMobileNetworkOperator() + " 0");
            } else if (cellInfo instanceof CellInfoCdma) {
                CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
                return new OperatorInfo((String) cellInfoCdma.getCellIdentity().getOperatorAlphaLong(), (String) cellInfoCdma.getCellIdentity().getOperatorAlphaShort(), "");
            } else if (cellInfo instanceof CellInfoTdscdma) {
                CellInfoTdscdma cellInfoTdscdma = (CellInfoTdscdma) cellInfo;
                operatorInfo = new OperatorInfo(getOperatorAlphaLong(5, cellInfoTdscdma), (String) cellInfoTdscdma.getCellIdentity().getOperatorAlphaShort(), cellInfoTdscdma.getCellIdentity().getMobileNetworkOperator() + " 2");
            } else {
                return new OperatorInfo("", "", "");
            }
            return operatorInfo;
        }
    }

    public static OperatorInfo getOperatorInfoFromCellInfo(CellIdentity cellIdentity, String str) {
        if (cellIdentity instanceof CellIdentityNr) {
            return new OperatorInfo((String) cellIdentity.getOperatorAlphaLong(), (String) cellIdentity.getOperatorAlphaShort(), str + " 11");
        } else if (cellIdentity instanceof CellIdentityLte) {
            return new OperatorInfo((String) cellIdentity.getOperatorAlphaLong(), (String) cellIdentity.getOperatorAlphaShort(), str + " 7");
        } else if (cellIdentity instanceof CellIdentityWcdma) {
            return new OperatorInfo((String) cellIdentity.getOperatorAlphaLong(), (String) cellIdentity.getOperatorAlphaShort(), str + " 2");
        } else if (cellIdentity instanceof CellIdentityGsm) {
            return new OperatorInfo((String) cellIdentity.getOperatorAlphaLong(), (String) cellIdentity.getOperatorAlphaShort(), str + " 0");
        } else if (cellIdentity instanceof CellIdentityCdma) {
            return new OperatorInfo((String) cellIdentity.getOperatorAlphaLong(), (String) cellIdentity.getOperatorAlphaShort(), "");
        } else {
            if (cellIdentity instanceof CellIdentityTdscdma) {
                return new OperatorInfo((String) cellIdentity.getOperatorAlphaLong(), (String) cellIdentity.getOperatorAlphaShort(), str + " 2");
            }
            return new OperatorInfo("", "", "");
        }
    }

    public static String getOperatorAlphaLong(int i, CellInfo cellInfo) {
        String str = (String) cellInfo.getCellIdentity().getOperatorAlphaLong();
        String str2 = cellInfo.getCellIdentity().getMccString() + cellInfo.getCellIdentity().getMncString();
        if (str == null || str.equals("")) {
            str = str2;
        }
        if (str.matches(".*[2345]G$")) {
            return str;
        }
        if (i == 1) {
            return str + " 2G";
        } else if (i == 3) {
            return str + " 4G";
        } else if (i == 4 || i == 5) {
            return str + " 3G";
        } else if (i != 6) {
            return str;
        } else {
            return str + " 5G";
        }
    }

    private static Resources getResourcesForMccMnc(String str) {
        Resources system = Resources.getSystem();
        Configuration configuration = system.getConfiguration();
        Configuration configuration2 = new Configuration();
        configuration2.setTo(configuration);
        try {
            configuration2.mcc = Integer.parseInt(str.substring(0, 3));
            int parseInt = Integer.parseInt(str.substring(3));
            configuration2.mnc = parseInt;
            if (parseInt == 0) {
                configuration2.mnc = 65535;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        DisplayMetrics displayMetrics = system.getDisplayMetrics();
        DisplayMetrics displayMetrics2 = new DisplayMetrics();
        displayMetrics2.setTo(displayMetrics);
        return new Resources(system.getAssets(), displayMetrics2, configuration2);
    }
}
