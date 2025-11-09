package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
/* loaded from: classes.dex */
public class BluetoothA2dpConfigStore {
    private long mCodecSpecific1Value;
    private long mCodecSpecific2Value;
    private long mCodecSpecific3Value;
    private long mCodecSpecific4Value;
    private int mCodecType = 1000000;
    private int mCodecPriority = 0;
    private int mSampleRate = 0;
    private int mBitsPerSample = 0;
    private int mChannelMode = 0;

    public void setCodecType(int i) {
        this.mCodecType = i;
    }

    public void setCodecPriority(int i) {
        this.mCodecPriority = i;
    }

    public void setSampleRate(int i) {
        this.mSampleRate = i;
    }

    public void setBitsPerSample(int i) {
        this.mBitsPerSample = i;
    }

    public void setChannelMode(int i) {
        this.mChannelMode = i;
    }

    public void setCodecSpecific1Value(long j) {
        this.mCodecSpecific1Value = j;
    }

    public BluetoothCodecConfig createCodecConfig() {
        return new BluetoothCodecConfig.Builder().setCodecType(this.mCodecType).setCodecPriority(this.mCodecPriority).setSampleRate(this.mSampleRate).setBitsPerSample(this.mBitsPerSample).setChannelMode(this.mChannelMode).setCodecSpecific1(this.mCodecSpecific1Value).setCodecSpecific2(this.mCodecSpecific2Value).setCodecSpecific3(this.mCodecSpecific3Value).setCodecSpecific4(this.mCodecSpecific4Value).build();
    }
}
