package com.syu.systemupdate.aidl;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes2.dex */
public class NewVersionInfo implements Parcelable {
    public static final Parcelable.Creator<NewVersionInfo> CREATOR = new Parcelable.Creator<NewVersionInfo>() { // from class: com.syu.systemupdate.aidl.NewVersionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NewVersionInfo createFromParcel(Parcel parcel) {
            return new NewVersionInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NewVersionInfo[] newArray(int i) {
            return new NewVersionInfo[i];
        }
    };
    public String content;
    public String deltaID;
    public String deltaUrl;
    public long fileSize;
    public String md5sum;
    public String oldVersionName;
    public String publishDate;
    public String versionAlias;
    public String versionName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NewVersionInfo() {
    }

    protected NewVersionInfo(Parcel parcel) {
        this.oldVersionName = parcel.readString();
        this.versionName = parcel.readString();
        this.versionAlias = parcel.readString();
        this.md5sum = parcel.readString();
        this.deltaUrl = parcel.readString();
        this.deltaID = parcel.readString();
        this.publishDate = parcel.readString();
        this.content = parcel.readString();
        this.fileSize = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.oldVersionName);
        parcel.writeString(this.versionName);
        parcel.writeString(this.versionAlias);
        parcel.writeString(this.md5sum);
        parcel.writeString(this.deltaUrl);
        parcel.writeString(this.deltaID);
        parcel.writeString(this.publishDate);
        parcel.writeString(this.content);
        parcel.writeLong(this.fileSize);
    }
}
