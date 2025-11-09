package com.android.settings.gps.data;
/* loaded from: classes.dex */
public class SatelliteInfo {
    private int azimuth;
    private int elevation;
    private int isBD;
    private int isUse;
    private int prn;
    private int snr;

    public SatelliteInfo() {
    }

    public SatelliteInfo(int i, int i2, int i3, int i4, int i5, int i6) {
        this.snr = i;
        this.elevation = i2;
        this.azimuth = i3;
        this.prn = i4;
        this.isUse = i5;
        this.isBD = i6;
    }

    public int getSnr() {
        return this.snr;
    }

    public int getElevation() {
        return this.elevation;
    }

    public int getAzimuth() {
        return this.azimuth;
    }

    public int getIsUse() {
        return this.isUse;
    }

    public int getPrn() {
        return this.prn;
    }

    public int getIsBD() {
        return this.isBD;
    }

    public String toString() {
        return "SatelliteInfo{snr=" + this.snr + ", elevation=" + this.elevation + ", azimuth=" + this.azimuth + ", prn=" + this.prn + ", isUse=" + this.isUse + ", isBD=" + this.isBD + '}';
    }
}
