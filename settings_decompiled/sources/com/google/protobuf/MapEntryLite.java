package com.google.protobuf;

import java.io.IOException;
/* loaded from: classes2.dex */
public class MapEntryLite<K, V> {

    /* loaded from: classes2.dex */
    static class Metadata<K, V> {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Metadata<K, V> getMetadata() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> void writeTo(CodedOutputStream codedOutputStream, Metadata<K, V> metadata, K k, V v) throws IOException {
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> int computeSerializedSize(Metadata<K, V> metadata, K k, V v) {
        throw null;
    }

    public int computeMessageSize(int i, K k, V v) {
        return CodedOutputStream.computeTagSize(i) + CodedOutputStream.computeLengthDelimitedFieldSize(computeSerializedSize(null, k, v));
    }
}
