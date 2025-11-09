package com.syu.util;

import java.util.HashMap;
import java.util.List;
/* loaded from: classes2.dex */
public class Markup {
    public XmlItem mItemCur;
    public List<XmlItem> mItems;
    public int mLenData;
    public boolean mNoError;
    public String mStrData;

    /* loaded from: classes2.dex */
    public class XmlItem {
        public HashMap<String, String> mAttrs;
        public XmlItem mChild;
        public XmlItem mNext;
        public XmlItem mParent;
        public int mPosEnd;
        public int mPosStart;
        public String mStrBuff;

        public XmlItem() {
        }
    }

    public void ReadXML(String str) {
        List<XmlItem> list;
        if (str == null || str.length() <= 0) {
            return;
        }
        String str2 = new String(str);
        this.mStrData = str2;
        this.mLenData = str2.length();
        this.mNoError = true;
        ParseItem(0, null);
        if (!this.mNoError || (list = this.mItems) == null || list.size() <= 0) {
            return;
        }
        this.mItemCur = this.mItems.get(0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x00b1, code lost:
        r2.mNext = r8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int ParseItem(int r12, com.syu.util.Markup.XmlItem r13) {
        /*
            Method dump skipped, instructions count: 221
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.syu.util.Markup.ParseItem(int, com.syu.util.Markup$XmlItem):int");
    }

    public boolean NextItem() {
        XmlItem xmlItem;
        XmlItem xmlItem2 = this.mItemCur;
        if (xmlItem2 == null || (xmlItem = xmlItem2.mNext) == null) {
            return false;
        }
        this.mItemCur = xmlItem;
        return true;
    }

    public boolean IntoItem() {
        XmlItem xmlItem;
        XmlItem xmlItem2 = this.mItemCur;
        if (xmlItem2 == null || (xmlItem = xmlItem2.mChild) == null) {
            return false;
        }
        this.mItemCur = xmlItem;
        return true;
    }

    public void ExitItem() {
        XmlItem xmlItem = this.mItemCur;
        if (xmlItem != null) {
            this.mItemCur = xmlItem.mParent;
        }
    }

    public String GetAttr(String str) {
        XmlItem xmlItem;
        if (str == null || (xmlItem = this.mItemCur) == null || !xmlItem.mAttrs.containsKey(str)) {
            return null;
        }
        return this.mItemCur.mAttrs.get(str);
    }

    /* JADX WARN: Code restructure failed: missing block: B:48:0x0070, code lost:
        r0 = r10.substring(r12, r8);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String FindToken(java.lang.String r10, int r11, int r12, int[] r13) {
        /*
            r9 = this;
            r9 = 0
            if (r13 == 0) goto L6
            r0 = -1
            r13[r9] = r0
        L6:
            r0 = 0
            if (r12 < 0) goto L83
            if (r12 >= r11) goto L83
            if (r10 == 0) goto L83
            char r1 = r10.charAt(r12)
        L11:
            r2 = 60
            r3 = 61
            r4 = 10
            r5 = 13
            r6 = 9
            r7 = 32
            if (r1 == r7) goto L2a
            if (r1 == r6) goto L2a
            if (r1 == r5) goto L2a
            if (r1 == r4) goto L2a
            if (r1 == r3) goto L2a
            if (r1 == r2) goto L2a
            goto L2e
        L2a:
            int r8 = r11 + (-1)
            if (r12 < r8) goto L7c
        L2e:
            r8 = 39
            if (r1 == r8) goto L55
            r8 = 34
            if (r1 != r8) goto L37
            goto L55
        L37:
            char r1 = r10.charAt(r12)
            r8 = r12
        L3c:
            if (r1 == r7) goto L6e
            if (r1 == r6) goto L6e
            if (r1 == r5) goto L6e
            if (r1 == r4) goto L6e
            if (r1 == r3) goto L6e
            if (r1 != r2) goto L49
            goto L6e
        L49:
            int r1 = r11 + (-1)
            if (r8 < r1) goto L4e
            goto L6e
        L4e:
            int r8 = r8 + 1
            char r1 = r10.charAt(r8)
            goto L3c
        L55:
            int r12 = r12 + 1
            if (r12 >= r11) goto L6d
            char r2 = r10.charAt(r12)
            r8 = r12
        L5e:
            if (r2 != r1) goto L61
            goto L6e
        L61:
            int r2 = r11 + (-1)
            if (r8 < r2) goto L66
            goto L6e
        L66:
            int r8 = r8 + 1
            char r2 = r10.charAt(r8)
            goto L5e
        L6d:
            r8 = r12
        L6e:
            if (r8 <= r12) goto L75
            java.lang.String r10 = r10.substring(r12, r8)
            r0 = r10
        L75:
            if (r13 == 0) goto L83
            int r8 = r8 + 1
            r13[r9] = r8
            goto L83
        L7c:
            int r12 = r12 + 1
            char r1 = r10.charAt(r12)
            goto L11
        L83:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.syu.util.Markup.FindToken(java.lang.String, int, int, int[]):java.lang.String");
    }
}
