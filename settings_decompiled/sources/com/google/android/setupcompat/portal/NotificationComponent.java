package com.google.android.setupcompat.portal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes2.dex */
public class NotificationComponent implements Parcelable {
    public static final Parcelable.Creator<NotificationComponent> CREATOR = new Parcelable.Creator<NotificationComponent>() { // from class: com.google.android.setupcompat.portal.NotificationComponent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationComponent createFromParcel(Parcel parcel) {
            return new NotificationComponent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationComponent[] newArray(int i) {
            return new NotificationComponent[i];
        }
    };
    private Bundle extraBundle;
    private final int notificationType;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private NotificationComponent(int i) {
        this.extraBundle = new Bundle();
        this.notificationType = i;
    }

    protected NotificationComponent(Parcel parcel) {
        this(parcel.readInt());
        this.extraBundle = parcel.readBundle(Bundle.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.notificationType);
        parcel.writeBundle(this.extraBundle);
    }
}
