package com.google.protobuf.nano;

import com.google.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;
/* loaded from: classes2.dex */
public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano {
    @Override // com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        return 0;
    }

    @Override // com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
    }

    @Override // com.google.protobuf.nano.MessageNano
    /* renamed from: clone */
    public M mo2542clone() throws CloneNotSupportedException {
        M m = (M) super.mo2542clone();
        InternalNano.cloneUnknownFieldData(this, m);
        return m;
    }
}
