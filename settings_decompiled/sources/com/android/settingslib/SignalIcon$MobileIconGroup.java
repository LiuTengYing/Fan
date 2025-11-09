package com.android.settingslib;
/* loaded from: classes2.dex */
public class SignalIcon$MobileIconGroup extends SignalIcon$IconGroup {
    public final int dataContentDescription;
    public final int dataType;

    public SignalIcon$MobileIconGroup(final String str, final int[][] iArr, final int[][] iArr2, final int[] iArr3, final int i, final int i2, final int i3, final int i4, final int i5, int i6, int i7) {
        new Object(str, iArr, iArr2, iArr3, i, i2, i3, i4, i5) { // from class: com.android.settingslib.SignalIcon$IconGroup
            public final int[] contentDesc;
            public final int discContentDesc;
            public final String name;
            public final int qsDiscState;
            public final int[][] qsIcons;
            public final int qsNullState;
            public final int sbDiscState;
            public final int[][] sbIcons;
            public final int sbNullState;

            {
                this.name = str;
                this.sbIcons = iArr;
                this.qsIcons = iArr2;
                this.contentDesc = iArr3;
                this.sbNullState = i;
                this.qsNullState = i2;
                this.sbDiscState = i3;
                this.qsDiscState = i4;
                this.discContentDesc = i5;
            }

            public String toString() {
                return "IconGroup(" + this.name + ")";
            }
        };
        this.dataContentDescription = i6;
        this.dataType = i7;
    }
}
