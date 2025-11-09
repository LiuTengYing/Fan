package com.android.settings.gps.data;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class Gps_Data {
    public static int mBDStatueCnt = 0;
    public static int mBDStatus = 0;
    public static String mCurrTime = "00-00-00 00:00:00";
    public static int mGALStatus = 0;
    public static int mGALStatusCnt = 0;
    public static int mGLNStatueCnt = 0;
    public static int mGLNStatus = 0;
    public static int mGPSStatueCnt = 0;
    public static int mGpsStatus = 0;
    public static String mJingDu = "000°00′00″";
    public static int mUNKNOWStatueCnt = 0;
    public static int mUNKNOWStatus = 0;
    public static String mWeiDu = "000°00′00″";
    public static int[] mBDStatue = new int[12];
    public static int[] mGPSStatue = new int[12];
    public static int[] mGLNStatue = new int[12];
    public static int[] mUNKNOWStatue = new int[12];
    public static int[] mGALStatue = new int[12];
    public static ArrayList<SatelliteInfo> mBDSate = new ArrayList<>();
    public static ArrayList<SatelliteInfo> mGPSSate = new ArrayList<>();
    public static ArrayList<SatelliteInfo> mGLNSate = new ArrayList<>();
    public static ArrayList<SatelliteInfo> mGALSate = new ArrayList<>();
    public static ArrayList<SatelliteInfo> mUNKNOWSate = new ArrayList<>();
    public static Updatable UPDATABLE_GPS_TIME = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.1
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mCurrTime)) {
                return false;
            }
            Gps_Data.mCurrTime = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_BD_TIME = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.2
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mCurrTime)) {
                return false;
            }
            Gps_Data.mCurrTime = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_GLN_TIME = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.3
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mCurrTime)) {
                return false;
            }
            Gps_Data.mCurrTime = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_GPS_WEIDU = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.4
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mWeiDu)) {
                return false;
            }
            Gps_Data.mWeiDu = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_GPS_JINGDU = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.5
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mJingDu)) {
                return false;
            }
            Gps_Data.mJingDu = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_BD_WEIDU = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.6
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mWeiDu)) {
                return false;
            }
            Gps_Data.mWeiDu = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_BD_JINGDU = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.7
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mJingDu)) {
                return false;
            }
            Gps_Data.mJingDu = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_GLN_WEIDU = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.8
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mWeiDu)) {
                return false;
            }
            Gps_Data.mWeiDu = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_GLN_JINGDU = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.9
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            String[] strArr = this.mStrs;
            if (strArr == null || strArr.length <= 0 || strArr[0].equals(Gps_Data.mJingDu)) {
                return false;
            }
            Gps_Data.mJingDu = this.mStrs[0];
            return true;
        }
    };
    public static Updatable UPDATABLE_GPS_STATAS = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.10
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mGpsStatus;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mGpsStatus = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_UNKNOW_STATAS_CNT = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.11
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mUNKNOWStatueCnt;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mUNKNOWStatueCnt = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_UNKNOW_STATAS_CHANGE = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.12
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            return true;
        }
    };
    public static Updatable UPDATABLE_BD_STATAS_CNT = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.13
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mBDStatueCnt;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mBDStatueCnt = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GAL_STATAS_CHANGE = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.14
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            return true;
        }
    };
    public static Updatable UPDATABLE_GAL_STATAS_CNT = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.15
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mGALStatusCnt;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mGALStatusCnt = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_BD_STATAS_CHANGE = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.16
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            return true;
        }
    };
    public static Updatable UPDATABLE_BD_STATAS = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.17
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mBDStatus;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mBDStatus = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GLN_STATAS_CNT = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.18
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mGLNStatueCnt;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mGLNStatueCnt = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GLN_STATAS_CHANGE = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.19
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            return true;
        }
    };
    public static Updatable UPDATABLE_UNKNOW_STATAS = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.20
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mUNKNOWStatus;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mUNKNOWStatus = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GLN_STATAS = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.21
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mGLNStatus;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mGLNStatus = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GPS_STATAS_CNT = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.22
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mGPSStatueCnt;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mGPSStatueCnt = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GAL_STATAS = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.23
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            int[] iArr = this.mInts;
            if (iArr != null && iArr.length > 0) {
                int i = Gps_Data.mGALStatus;
                int i2 = iArr[0];
                if (i != i2) {
                    Gps_Data.mGALStatus = i2;
                    return true;
                }
            }
            return false;
        }
    };
    public static Updatable UPDATABLE_GPS_STATAS_CHANGE = new Updatable() { // from class: com.android.settings.gps.data.Gps_Data.24
        @Override // com.android.settings.gps.data.Updatable
        public boolean onUpdate() {
            return true;
        }
    };

    public static boolean isFixed() {
        return mGpsStatus == 1 || mBDStatus == 1 || mGLNStatus == 1 || mGALStatus == 1;
    }
}
