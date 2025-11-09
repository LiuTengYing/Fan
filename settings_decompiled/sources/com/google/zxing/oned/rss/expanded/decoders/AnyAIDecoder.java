package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class AnyAIDecoder extends AbstractExpandedDecoder {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AnyAIDecoder(BitArray bitArray) {
        super(bitArray);
    }

    @Override // com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder
    public String parseInformation() throws NotFoundException {
        return getGeneralDecoder().decodeAllCodes(new StringBuilder(), 5);
    }
}
