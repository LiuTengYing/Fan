package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothLeAudioCodecConfigMetadata;
import android.bluetooth.BluetoothLeAudioContentMetadata;
import android.bluetooth.BluetoothLeBroadcastChannel;
import android.bluetooth.BluetoothLeBroadcastMetadata;
import android.bluetooth.BluetoothLeBroadcastSubgroup;
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public class LocalBluetoothLeBroadcastMetadata {
    private long mAudioLocation;
    private byte[] mBroadcastCode;
    private int mBroadcastId;
    private BluetoothLeBroadcastChannel mChannel;
    private int mChannelIndex;
    private long mCodecId;
    private BluetoothLeAudioCodecConfigMetadata mConfigMetadata;
    private BluetoothLeAudioContentMetadata mContentMetadata;
    private boolean mIsEncrypted;
    private boolean mIsSelected;
    private String mLanguage;
    private int mPaSyncInterval;
    private int mPresentationDelayMicros;
    private String mProgramInfo;
    private int mSourceAddressType;
    private int mSourceAdvertisingSid;
    private BluetoothDevice mSourceDevice;
    private BluetoothLeBroadcastSubgroup mSubgroup;

    public BluetoothLeBroadcastMetadata convertToBroadcastMetadata(String str) {
        Log.d("LocalBluetoothLeBroadcastMetadata", "Convert " + str + "to BluetoothLeBroadcastMetadata");
        Matcher matcher = Pattern.compile("<(.*?)>").matcher(str);
        if (matcher.find()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(matcher.group(1));
            this.mSourceAddressType = Integer.parseInt((String) arrayList.get(0));
            this.mSourceDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice((String) arrayList.get(1));
            this.mSourceAdvertisingSid = Integer.parseInt((String) arrayList.get(2));
            this.mBroadcastId = Integer.parseInt((String) arrayList.get(3));
            this.mPaSyncInterval = Integer.parseInt((String) arrayList.get(4));
            this.mIsEncrypted = Boolean.valueOf((String) arrayList.get(5)).booleanValue();
            this.mBroadcastCode = ((String) arrayList.get(6)).getBytes();
            this.mPresentationDelayMicros = Integer.parseInt((String) arrayList.get(7));
            this.mSubgroup = convertToSubgroup((String) arrayList.get(8));
            Log.d("LocalBluetoothLeBroadcastMetadata", "Converted qrCodeString result: " + matcher.group());
            return new BluetoothLeBroadcastMetadata.Builder().setSourceDevice(this.mSourceDevice, this.mSourceAddressType).setSourceAdvertisingSid(this.mSourceAdvertisingSid).setBroadcastId(this.mBroadcastId).setPaSyncInterval(this.mPaSyncInterval).setEncrypted(this.mIsEncrypted).setBroadcastCode(this.mBroadcastCode).setPresentationDelayMicros(this.mPresentationDelayMicros).addSubgroup(this.mSubgroup).build();
        }
        Log.d("LocalBluetoothLeBroadcastMetadata", "The match fail, can not convert it to BluetoothLeBroadcastMetadata.");
        return null;
    }

    private BluetoothLeBroadcastSubgroup convertToSubgroup(String str) {
        Log.d("LocalBluetoothLeBroadcastMetadata", "Convert " + str + "to BluetoothLeBroadcastSubgroup");
        Matcher matcher = Pattern.compile("<(.*?)>").matcher(str);
        if (matcher.find()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(matcher.group(1));
            this.mCodecId = Long.getLong((String) arrayList.get(0)).longValue();
            this.mConfigMetadata = convertToConfigMetadata((String) arrayList.get(1));
            this.mContentMetadata = convertToContentMetadata((String) arrayList.get(2));
            this.mChannel = convertToChannel((String) arrayList.get(3), this.mConfigMetadata);
            Log.d("LocalBluetoothLeBroadcastMetadata", "Converted subgroupString result: " + matcher.group());
            return new BluetoothLeBroadcastSubgroup.Builder().setCodecId(this.mCodecId).setCodecSpecificConfig(this.mConfigMetadata).setContentMetadata(this.mContentMetadata).addChannel(this.mChannel).build();
        }
        Log.d("LocalBluetoothLeBroadcastMetadata", "The match fail, can not convert it to BluetoothLeBroadcastSubgroup.");
        return null;
    }

    private BluetoothLeAudioCodecConfigMetadata convertToConfigMetadata(String str) {
        Log.d("LocalBluetoothLeBroadcastMetadata", "Convert " + str + "to BluetoothLeAudioCodecConfigMetadata");
        Matcher matcher = Pattern.compile("<(.*?)>").matcher(str);
        if (matcher.find()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(matcher.group(1));
            this.mAudioLocation = Long.getLong((String) arrayList.get(0)).longValue();
            Log.d("LocalBluetoothLeBroadcastMetadata", "Converted configMetadataString result: " + matcher.group());
            return new BluetoothLeAudioCodecConfigMetadata.Builder().setAudioLocation(this.mAudioLocation).build();
        }
        Log.d("LocalBluetoothLeBroadcastMetadata", "The match fail, can not convert it to BluetoothLeAudioCodecConfigMetadata.");
        return null;
    }

    private BluetoothLeAudioContentMetadata convertToContentMetadata(String str) {
        Log.d("LocalBluetoothLeBroadcastMetadata", "Convert " + str + "to BluetoothLeAudioContentMetadata");
        Matcher matcher = Pattern.compile("<(.*?)>").matcher(str);
        if (matcher.find()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(matcher.group(1));
            this.mProgramInfo = (String) arrayList.get(0);
            this.mLanguage = (String) arrayList.get(1);
            Log.d("LocalBluetoothLeBroadcastMetadata", "Converted contentMetadataString result: " + matcher.group());
            return new BluetoothLeAudioContentMetadata.Builder().setProgramInfo(this.mProgramInfo).setLanguage(this.mLanguage).build();
        }
        Log.d("LocalBluetoothLeBroadcastMetadata", "The match fail, can not convert it to BluetoothLeAudioContentMetadata.");
        return null;
    }

    private BluetoothLeBroadcastChannel convertToChannel(String str, BluetoothLeAudioCodecConfigMetadata bluetoothLeAudioCodecConfigMetadata) {
        Log.d("LocalBluetoothLeBroadcastMetadata", "Convert " + str + "to BluetoothLeBroadcastChannel");
        Matcher matcher = Pattern.compile("<(.*?)>").matcher(str);
        if (matcher.find()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(matcher.group(1));
            this.mIsSelected = Boolean.valueOf((String) arrayList.get(0)).booleanValue();
            this.mChannelIndex = Integer.parseInt((String) arrayList.get(1));
            Log.d("LocalBluetoothLeBroadcastMetadata", "Converted channelString result: " + matcher.group());
            return new BluetoothLeBroadcastChannel.Builder().setSelected(this.mIsSelected).setChannelIndex(this.mChannelIndex).setCodecMetadata(bluetoothLeAudioCodecConfigMetadata).build();
        }
        Log.d("LocalBluetoothLeBroadcastMetadata", "The match fail, can not convert it to BluetoothLeBroadcastChannel.");
        return null;
    }
}
