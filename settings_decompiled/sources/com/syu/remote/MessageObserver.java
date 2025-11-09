package com.syu.remote;

import java.util.BitSet;
/* loaded from: classes2.dex */
public abstract class MessageObserver {
    BitSet bitSet = new BitSet();

    public abstract void onReceiver(Message message);

    public MessageObserver(int... iArr) {
        if (iArr.length > 0) {
            for (int i : iArr) {
                addModule(i);
            }
        }
    }

    public void addModule(int i) {
        this.bitSet.set(i);
    }

    public boolean isListening(int i) {
        return this.bitSet.get(i);
    }
}
