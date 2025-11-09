package com.android.net.module.util;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
/* loaded from: classes.dex */
public class DnsSdTxtRecord implements Parcelable {
    public static final Parcelable.Creator<DnsSdTxtRecord> CREATOR = new Parcelable.Creator<DnsSdTxtRecord>() { // from class: com.android.net.module.util.DnsSdTxtRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DnsSdTxtRecord createFromParcel(Parcel parcel) {
            DnsSdTxtRecord dnsSdTxtRecord = new DnsSdTxtRecord();
            parcel.readByteArray(dnsSdTxtRecord.mData);
            return dnsSdTxtRecord;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DnsSdTxtRecord[] newArray(int i) {
            return new DnsSdTxtRecord[i];
        }
    };
    private byte[] mData = new byte[0];

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private String getKey(int i) {
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < i; i4++) {
            byte[] bArr = this.mData;
            if (i3 >= bArr.length) {
                break;
            }
            i3 += bArr[i3] + 1;
        }
        byte[] bArr2 = this.mData;
        if (i3 < bArr2.length) {
            byte b = bArr2[i3];
            while (i2 < b && this.mData[i3 + i2 + 1] != 61) {
                i2++;
            }
            return new String(this.mData, i3 + 1, i2);
        }
        return null;
    }

    private byte[] getValue(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            byte[] bArr = this.mData;
            if (i2 >= bArr.length) {
                break;
            }
            i2 += bArr[i2] + 1;
        }
        byte[] bArr2 = this.mData;
        if (i2 < bArr2.length) {
            int i4 = bArr2[i2];
            for (int i5 = 0; i5 < i4; i5++) {
                byte[] bArr3 = this.mData;
                int i6 = i2 + i5;
                if (bArr3[i6 + 1] == 61) {
                    int i7 = (i4 - i5) - 1;
                    byte[] bArr4 = new byte[i7];
                    System.arraycopy(bArr3, i6 + 2, bArr4, 0, i7);
                    return bArr4;
                }
            }
        }
        return null;
    }

    private String getValueAsString(int i) {
        byte[] value = getValue(i);
        if (value != null) {
            return new String(value);
        }
        return null;
    }

    public String toString() {
        String str;
        String str2 = null;
        int i = 0;
        while (true) {
            String key = getKey(i);
            if (key == null) {
                break;
            }
            String str3 = "{" + key;
            String valueAsString = getValueAsString(i);
            if (valueAsString != null) {
                str = str3 + "=" + valueAsString + "}";
            } else {
                str = str3 + "}";
            }
            if (str2 == null) {
                str2 = str;
            } else {
                str2 = str2 + ", " + str;
            }
            i++;
        }
        return str2 != null ? str2 : "";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DnsSdTxtRecord) {
            return Arrays.equals(((DnsSdTxtRecord) obj).mData, this.mData);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.mData);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(this.mData);
    }
}
