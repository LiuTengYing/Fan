package com.abupdate.mqtt_libs.mqtt_service;

import android.os.Parcel;
import android.os.Parcelable;
import com.abupdate.mqtt_libs.mqttv3.MqttMessage;
/* loaded from: classes.dex */
public class ParcelableMqttMessage extends MqttMessage implements Parcelable {
    public static final Parcelable.Creator<ParcelableMqttMessage> CREATOR = new a();

    /* renamed from: a  reason: collision with root package name */
    String f3a;

    /* loaded from: classes.dex */
    class a implements Parcelable.Creator {
        a() {
        }

        @Override // android.os.Parcelable.Creator
        /* renamed from: a */
        public ParcelableMqttMessage createFromParcel(Parcel parcel) {
            return new ParcelableMqttMessage(parcel);
        }

        @Override // android.os.Parcelable.Creator
        /* renamed from: a */
        public ParcelableMqttMessage[] newArray(int i) {
            return new ParcelableMqttMessage[i];
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(getPayload());
        parcel.writeInt(getQos());
        parcel.writeBooleanArray(new boolean[]{isRetained(), isDuplicate()});
        parcel.writeString(this.f3a);
    }

    ParcelableMqttMessage(Parcel parcel) {
        super(parcel.createByteArray());
        this.f3a = null;
        setQos(parcel.readInt());
        boolean[] createBooleanArray = parcel.createBooleanArray();
        setRetained(createBooleanArray[0]);
        setDuplicate(createBooleanArray[1]);
        this.f3a = parcel.readString();
    }
}
