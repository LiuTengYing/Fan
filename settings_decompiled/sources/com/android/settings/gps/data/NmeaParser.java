package com.android.settings.gps.data;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class NmeaParser {
    private static final List<SatelliteInfo> mSatellitesList = new ArrayList();
    private static final List<Integer> mPrnsList = new ArrayList();
    private static boolean mFixAvailable = false;
    public static final List<SatelliteInfo> mSList = new ArrayList();

    public static int getLocationType(int i) {
        if (i == 1) {
            return 0;
        }
        if (i == 6) {
            return 3;
        }
        if (i == 3) {
            return 2;
        }
        return i == 5 ? 1 : 4;
    }
}
