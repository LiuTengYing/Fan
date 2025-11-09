package com.syu.remote;

import java.util.Arrays;
/* loaded from: classes2.dex */
public class Message {
    public int code;
    public float[] flts;
    public int[] ints;
    public int module;
    public String[] strs;

    public Message(int i, int i2, int[] iArr, float[] fArr, String[] strArr) {
        this.module = i;
        this.code = i2;
        this.ints = iArr;
        this.flts = fArr;
        this.strs = strArr;
    }

    public String toString() {
        return "Message{module=" + this.module + ", code=" + this.code + ", ints=" + Arrays.toString(this.ints) + ", flts=" + Arrays.toString(this.flts) + ", strs=" + Arrays.toString(this.strs) + '}';
    }
}
