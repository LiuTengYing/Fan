package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AdaptiveOutlineDrawable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes2.dex */
public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice> {
    private long mConnectAttempted;
    private final Context mContext;
    BluetoothDevice mDevice;
    private int mDeviceMode;
    private int mDeviceSide;
    LruCache<String, BitmapDrawable> mDrawableCache;
    private int mGroupId;
    private long mHiSyncId;
    boolean mJustDiscovered;
    private boolean mLocalNapRoleConnected;
    private final LocalBluetoothProfileManager mProfileManager;
    short mRssi;
    private CachedBluetoothDevice mSubDevice;
    private boolean mUnpairing;
    private final Object mProfileLock = new Object();
    private final Collection<LocalBluetoothProfile> mProfiles = new CopyOnWriteArrayList();
    private final Collection<LocalBluetoothProfile> mRemovedProfiles = new CopyOnWriteArrayList();
    boolean mIsCoordinatedSetMember = false;
    private final Collection<Callback> mCallbacks = new CopyOnWriteArrayList();
    private boolean mIsActiveDeviceA2dp = false;
    private boolean mIsActiveDeviceHeadset = false;
    private boolean mIsActiveDeviceHearingAid = false;
    private boolean mIsActiveDeviceLeAudio = false;
    private boolean mIsA2dpProfileConnectedFail = false;
    private boolean mIsHeadsetProfileConnectedFail = false;
    private boolean mIsHearingAidProfileConnectedFail = false;
    private boolean mIsLeAudioProfileConnectedFail = false;
    private Set<CachedBluetoothDevice> mMemberDevices = new HashSet();
    private boolean mIsClientDevice = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                CachedBluetoothDevice.this.mIsHeadsetProfileConnectedFail = true;
            } else if (i == 2) {
                CachedBluetoothDevice.this.mIsA2dpProfileConnectedFail = true;
            } else if (i == 21) {
                CachedBluetoothDevice.this.mIsHearingAidProfileConnectedFail = true;
            } else if (i == 22) {
                CachedBluetoothDevice.this.mIsLeAudioProfileConnectedFail = true;
            } else {
                Log.w("CachedBluetoothDevice", "handleMessage(): unknown message : " + message.what);
            }
            Log.w("CachedBluetoothDevice", "Connect to profile : " + message.what + " timeout, show error message !");
            CachedBluetoothDevice.this.refresh();
        }
    };
    private final BluetoothAdapter mLocalAdapter = BluetoothAdapter.getDefaultAdapter();

    /* loaded from: classes2.dex */
    public interface Callback {
        void onDeviceAttributesChanged();
    }

    private boolean isTwsBatteryAvailable(int i, int i2) {
        return i >= 0 && i2 >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDevice(Context context, LocalBluetoothProfileManager localBluetoothProfileManager, BluetoothDevice bluetoothDevice) {
        this.mContext = context;
        this.mProfileManager = localBluetoothProfileManager;
        this.mDevice = bluetoothDevice;
        fillData();
        this.mHiSyncId = 0L;
        this.mGroupId = -1;
        initDrawableCache();
        this.mUnpairing = false;
    }

    public boolean isClientDevice() {
        return this.mIsClientDevice;
    }

    private void initDrawableCache() {
        this.mDrawableCache = new LruCache<String, BitmapDrawable>(((int) (Runtime.getRuntime().maxMemory() / 1024)) / 8) { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.util.LruCache
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount() / 1024;
            }
        };
    }

    private String describe(LocalBluetoothProfile localBluetoothProfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:");
        sb.append(this.mDevice);
        if (localBluetoothProfile != null) {
            sb.append(" Profile:");
            sb.append(localBluetoothProfile);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onProfileStateChanged(LocalBluetoothProfile localBluetoothProfile, int i) {
        Log.d("CachedBluetoothDevice", "onProfileStateChanged: profile " + localBluetoothProfile + ", device " + this.mDevice.getAnonymizedAddress() + ", newProfileState " + i);
        if (this.mLocalAdapter.getState() == 13) {
            Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        synchronized (this.mProfileLock) {
            if ((localBluetoothProfile instanceof A2dpProfile) || (localBluetoothProfile instanceof HeadsetProfile) || (localBluetoothProfile instanceof HearingAidProfile)) {
                setProfileConnectedStatus(localBluetoothProfile.getProfileId(), false);
                if (i != 0) {
                    if (i == 1) {
                        this.mHandler.sendEmptyMessageDelayed(localBluetoothProfile.getProfileId(), 60000L);
                    } else if (i == 2) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    } else if (i == 3) {
                        if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                            this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                        }
                    } else {
                        Log.w("CachedBluetoothDevice", "onProfileStateChanged(): unknown profile state : " + i);
                    }
                } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                    this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    setProfileConnectedStatus(localBluetoothProfile.getProfileId(), true);
                }
            }
            if (i == 2) {
                if (localBluetoothProfile instanceof MapProfile) {
                    localBluetoothProfile.setEnabled(this.mDevice, true);
                }
                if (Utils.isSupportedBTSink() && ((localBluetoothProfile instanceof PbapClientProfile) || (localBluetoothProfile instanceof HfpClientProfile))) {
                    Log.d("CachedBluetoothDevice", "The device is a client device");
                    this.mIsClientDevice = true;
                }
                if (!this.mProfiles.contains(localBluetoothProfile)) {
                    this.mRemovedProfiles.remove(localBluetoothProfile);
                    this.mProfiles.add(localBluetoothProfile);
                    if ((localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice)) {
                        this.mLocalNapRoleConnected = true;
                    }
                }
            } else if ((localBluetoothProfile instanceof MapProfile) && i == 0) {
                localBluetoothProfile.setEnabled(this.mDevice, false);
            } else if (this.mLocalNapRoleConnected && (localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice) && i == 0) {
                Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
                this.mLocalNapRoleConnected = false;
            }
        }
        fetchActiveDevices();
    }

    void setProfileConnectedStatus(int i, boolean z) {
        if (i == 1) {
            this.mIsHeadsetProfileConnectedFail = z;
        } else if (i == 2) {
            this.mIsA2dpProfileConnectedFail = z;
        } else if (i == 21) {
            this.mIsHearingAidProfileConnectedFail = z;
        } else if (i == 22) {
            this.mIsLeAudioProfileConnectedFail = z;
        } else {
            Log.w("CachedBluetoothDevice", "setProfileConnectedStatus(): unknown profile id : " + i);
        }
    }

    public void disconnect() {
        synchronized (this.mProfileLock) {
            if (getGroupId() != -1) {
                for (CachedBluetoothDevice cachedBluetoothDevice : getMemberDevice()) {
                    Log.d("CachedBluetoothDevice", "Disconnect the member(" + cachedBluetoothDevice.getAddress() + ")");
                    cachedBluetoothDevice.disconnect();
                }
            }
            this.mDevice.disconnect();
        }
        PbapServerProfile pbapProfile = this.mProfileManager.getPbapProfile();
        if (pbapProfile == null || !isConnectedProfile(pbapProfile)) {
            return;
        }
        pbapProfile.setEnabled(this.mDevice, false);
    }

    public boolean hasDeviceConnected() {
        for (BluetoothDevice bluetoothDevice : this.mLocalAdapter.getBondedDevices()) {
            if (!bluetoothDevice.equals(getDevice()) && bluetoothDevice.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void connect() {
        if (!(Utils.isSupportedBTSink() && hasDeviceConnected()) && ensurePaired()) {
            this.mConnectAttempted = SystemClock.elapsedRealtime();
            connectDevice();
        }
    }

    public int getDeviceSide() {
        return this.mDeviceSide;
    }

    public void setDeviceSide(int i) {
        this.mDeviceSide = i;
    }

    public int getDeviceMode() {
        return this.mDeviceMode;
    }

    public void setDeviceMode(int i) {
        this.mDeviceMode = i;
    }

    public long getHiSyncId() {
        return this.mHiSyncId;
    }

    public void setHiSyncId(long j) {
        this.mHiSyncId = j;
    }

    public boolean isHearingAidDevice() {
        return this.mHiSyncId != 0;
    }

    public void setIsCoordinatedSetMember(boolean z) {
        this.mIsCoordinatedSetMember = z;
    }

    public boolean isCoordinatedSetMemberDevice() {
        return this.mIsCoordinatedSetMember;
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public void setGroupId(int i) {
        this.mGroupId = i;
    }

    private void connectDevice() {
        synchronized (this.mProfileLock) {
            if (this.mProfiles.isEmpty()) {
                Log.d("CachedBluetoothDevice", "No profiles. Maybe we will connect later for device " + this.mDevice);
                return;
            }
            this.mDevice.connect();
            if (getGroupId() != -1) {
                for (CachedBluetoothDevice cachedBluetoothDevice : getMemberDevice()) {
                    Log.d("CachedBluetoothDevice", "connect the member(" + cachedBluetoothDevice.getAddress() + ")");
                    cachedBluetoothDevice.connect();
                }
            }
        }
    }

    public void connectProfile(LocalBluetoothProfile localBluetoothProfile) {
        this.mConnectAttempted = SystemClock.elapsedRealtime();
        connectInt(localBluetoothProfile);
        refresh();
    }

    synchronized void connectInt(LocalBluetoothProfile localBluetoothProfile) {
        if (ensurePaired()) {
            if (localBluetoothProfile.setEnabled(this.mDevice, true)) {
                Log.d("CachedBluetoothDevice", "Command sent successfully:CONNECT " + describe(localBluetoothProfile));
                return;
            }
            Log.i("CachedBluetoothDevice", "Failed to connect " + localBluetoothProfile.toString() + " to " + getName());
        }
    }

    private boolean ensurePaired() {
        if (getBondState() == 10) {
            startPairing();
            return false;
        }
        return true;
    }

    public boolean startPairing() {
        if (this.mLocalAdapter.isDiscovering()) {
            this.mLocalAdapter.cancelDiscovery();
        }
        return this.mDevice.createBond();
    }

    public void unpair() {
        BluetoothDevice bluetoothDevice;
        int bondState = getBondState();
        if (bondState == 11) {
            this.mDevice.cancelBondProcess();
        }
        if (bondState == 10 || (bluetoothDevice = this.mDevice) == null) {
            return;
        }
        this.mUnpairing = true;
        if (bluetoothDevice.removeBond()) {
            releaseLruCache();
            Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + describe(null));
        }
    }

    public int getProfileConnectionState(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile != null) {
            return localBluetoothProfile.getConnectionStatus(this.mDevice);
        }
        return 0;
    }

    private void fillData() {
        updateProfiles();
        fetchActiveDevices();
        migratePhonebookPermissionChoice();
        migrateMessagePermissionChoice();
        lambda$refresh$0();
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public String getAddress() {
        return this.mDevice.getAddress();
    }

    public String getIdentityAddress() {
        String identityAddress = this.mDevice.getIdentityAddress();
        return TextUtils.isEmpty(identityAddress) ? getAddress() : identityAddress;
    }

    public String getName() {
        String alias = this.mDevice.getAlias();
        return TextUtils.isEmpty(alias) ? getAddress() : alias;
    }

    public void setName(String str) {
        if (str == null || TextUtils.equals(str, getName())) {
            return;
        }
        this.mDevice.setAlias(str);
        lambda$refresh$0();
    }

    public boolean setActive() {
        boolean z;
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null && isConnectedProfile(a2dpProfile) && a2dpProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: A2DP active device=" + this);
            z = true;
        } else {
            z = false;
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null && isConnectedProfile(headsetProfile) && headsetProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Headset active device=" + this);
            z = true;
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null && isConnectedProfile(hearingAidProfile) && hearingAidProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Hearing Aid active device=" + this);
            z = true;
        }
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (leAudioProfile != null && isConnectedProfile(leAudioProfile) && leAudioProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: LeAudio active device=" + this);
            return true;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshName() {
        Log.d("CachedBluetoothDevice", "Device name: " + getName());
        lambda$refresh$0();
    }

    public boolean hasHumanReadableName() {
        return !TextUtils.isEmpty(this.mDevice.getAlias());
    }

    public int getBatteryLevel() {
        return this.mDevice.getBatteryLevel();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refresh() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CachedBluetoothDevice.this.lambda$refresh$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$1() {
        Uri uriMetaData;
        if (BluetoothUtils.isAdvancedDetailsHeader(this.mDevice) && (uriMetaData = BluetoothUtils.getUriMetaData(getDevice(), 5)) != null && this.mDrawableCache.get(uriMetaData.toString()) == null) {
            this.mDrawableCache.put(uriMetaData.toString(), (BitmapDrawable) BluetoothUtils.getBtDrawableWithDescription(this.mContext, this).first);
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CachedBluetoothDevice.this.lambda$refresh$0();
            }
        });
    }

    public void setJustDiscovered(boolean z) {
        if (this.mJustDiscovered != z) {
            this.mJustDiscovered = z;
            lambda$refresh$0();
        }
    }

    public int getBondState() {
        return this.mDevice.getBondState();
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0054  */
    /* JADX WARN: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onActiveDeviceChanged(boolean r4, int r5) {
        /*
            r3 = this;
            r0 = 1
            r1 = 0
            if (r5 == r0) goto L49
            r2 = 2
            if (r5 == r2) goto L40
            r2 = 21
            if (r5 == r2) goto L37
            r2 = 22
            if (r5 == r2) goto L2e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "onActiveDeviceChanged: unknown profile "
            r0.append(r2)
            r0.append(r5)
            java.lang.String r5 = " isActive "
            r0.append(r5)
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            java.lang.String r5 = "CachedBluetoothDevice"
            android.util.Log.w(r5, r4)
            goto L52
        L2e:
            boolean r5 = r3.mIsActiveDeviceLeAudio
            if (r5 == r4) goto L33
            goto L34
        L33:
            r0 = r1
        L34:
            r3.mIsActiveDeviceLeAudio = r4
            goto L51
        L37:
            boolean r5 = r3.mIsActiveDeviceHearingAid
            if (r5 == r4) goto L3c
            goto L3d
        L3c:
            r0 = r1
        L3d:
            r3.mIsActiveDeviceHearingAid = r4
            goto L51
        L40:
            boolean r5 = r3.mIsActiveDeviceA2dp
            if (r5 == r4) goto L45
            goto L46
        L45:
            r0 = r1
        L46:
            r3.mIsActiveDeviceA2dp = r4
            goto L51
        L49:
            boolean r5 = r3.mIsActiveDeviceHeadset
            if (r5 == r4) goto L4e
            goto L4f
        L4e:
            r0 = r1
        L4f:
            r3.mIsActiveDeviceHeadset = r4
        L51:
            r1 = r0
        L52:
            if (r1 == 0) goto L57
            r3.lambda$refresh$0()
        L57:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.CachedBluetoothDevice.onActiveDeviceChanged(boolean, int):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAudioModeChanged() {
        lambda$refresh$0();
    }

    public boolean isActiveDevice(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 21) {
                    if (i == 22) {
                        return this.mIsActiveDeviceLeAudio;
                    }
                    Log.w("CachedBluetoothDevice", "getActiveDevice: unknown profile " + i);
                    return false;
                }
                return this.mIsActiveDeviceHearingAid;
            }
            return this.mIsActiveDeviceA2dp;
        }
        return this.mIsActiveDeviceHeadset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRssi(short s) {
        if (this.mRssi != s) {
            this.mRssi = s;
            lambda$refresh$0();
        }
    }

    public boolean isConnected() {
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (getProfileConnectionState(localBluetoothProfile) == 2) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isConnectedProfile(LocalBluetoothProfile localBluetoothProfile) {
        return getProfileConnectionState(localBluetoothProfile) == 2;
    }

    public boolean isBusy() {
        int profileConnectionState;
        synchronized (this.mProfileLock) {
            Iterator<LocalBluetoothProfile> it = this.mProfiles.iterator();
            do {
                boolean z = true;
                if (it.hasNext()) {
                    profileConnectionState = getProfileConnectionState(it.next());
                    if (profileConnectionState == 1) {
                        break;
                    }
                } else {
                    if (getBondState() != 11) {
                        z = false;
                    }
                    return z;
                }
            } while (profileConnectionState != 3);
            return true;
        }
    }

    private boolean updateProfiles() {
        BluetoothClass bluetoothClass;
        ParcelUuid[] uuids = this.mDevice.getUuids();
        if (uuids == null) {
            return false;
        }
        List uuidsList = this.mLocalAdapter.getUuidsList();
        ParcelUuid[] parcelUuidArr = new ParcelUuid[uuidsList.size()];
        uuidsList.toArray(parcelUuidArr);
        processPhonebookAccess();
        synchronized (this.mProfileLock) {
            this.mProfileManager.updateProfiles(uuids, parcelUuidArr, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
        }
        Log.d("CachedBluetoothDevice", "updating profiles for " + this.mDevice.getAnonymizedAddress());
        if (this.mDevice.getBluetoothClass() != null) {
            Log.v("CachedBluetoothDevice", "Class: " + bluetoothClass.toString());
        }
        Log.v("CachedBluetoothDevice", "UUID:");
        for (ParcelUuid parcelUuid : uuids) {
            Log.v("CachedBluetoothDevice", "  " + parcelUuid);
        }
        return true;
    }

    private void fetchActiveDevices() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            this.mIsActiveDeviceA2dp = this.mDevice.equals(a2dpProfile.getActiveDevice());
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            this.mIsActiveDeviceHeadset = this.mDevice.equals(headsetProfile.getActiveDevice());
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            this.mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(this.mDevice);
        }
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (leAudioProfile != null) {
            this.mIsActiveDeviceLeAudio = leAudioProfile.getActiveDevices().contains(this.mDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUuidChanged() {
        updateProfiles();
        ParcelUuid[] uuids = this.mDevice.getUuids();
        long j = 30000;
        if (!ArrayUtils.contains(uuids, BluetoothUuid.HOGP)) {
            if (ArrayUtils.contains(uuids, BluetoothUuid.HEARING_AID)) {
                j = 15000;
            } else if (!ArrayUtils.contains(uuids, BluetoothUuid.LE_AUDIO)) {
                j = 5000;
            }
        }
        Log.d("CachedBluetoothDevice", "onUuidChanged: Time since last connect=" + (SystemClock.elapsedRealtime() - this.mConnectAttempted));
        if (this.mConnectAttempted + j > SystemClock.elapsedRealtime()) {
            Log.d("CachedBluetoothDevice", "onUuidChanged: triggering connectDevice");
            connectDevice();
            if (Utils.isSupportedBTSink()) {
                A2dpSinkProfile a2dpSinkProfile = this.mProfileManager.getA2dpSinkProfile();
                if (a2dpSinkProfile == null) {
                    Log.e("CachedBluetoothDevice", "disconnect a2dpsinkprofile == null");
                } else if (a2dpSinkProfile.getConnectionStatus(this.mDevice) != 2) {
                    a2dpSinkProfile.setEnabled(this.mDevice, true);
                }
            }
        }
        lambda$refresh$0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBondingStateChanged(int i) {
        if (i == 10) {
            synchronized (this.mProfileLock) {
                this.mProfiles.clear();
            }
            if (Utils.isSupportedBTSink()) {
                this.mIsClientDevice = false;
            }
            this.mDevice.setPhonebookAccessPermission(0);
            this.mDevice.setMessageAccessPermission(0);
            this.mDevice.setSimAccessPermission(0);
        }
        refresh();
        if (i == 12 && this.mDevice.isBondingInitiatedLocally()) {
            connect();
        }
    }

    public BluetoothClass getBtClass() {
        return this.mDevice.getBluetoothClass();
    }

    public List<LocalBluetoothProfile> getProfiles() {
        return new ArrayList(this.mProfiles);
    }

    public List<LocalBluetoothProfile> getConnectableProfiles() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (localBluetoothProfile.accessProfileEnabled()) {
                    arrayList.add(localBluetoothProfile);
                }
            }
        }
        return arrayList;
    }

    public List<LocalBluetoothProfile> getRemovedProfiles() {
        return new ArrayList(this.mRemovedProfiles);
    }

    public void registerCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: dispatchAttributesChanged */
    public void lambda$refresh$0() {
        for (Callback callback : this.mCallbacks) {
            callback.onDeviceAttributesChanged();
        }
    }

    public String toString() {
        return this.mDevice.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CachedBluetoothDevice)) {
            return false;
        }
        return this.mDevice.equals(((CachedBluetoothDevice) obj).mDevice);
    }

    public int hashCode() {
        return this.mDevice.getAddress().hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(CachedBluetoothDevice cachedBluetoothDevice) {
        int i = (cachedBluetoothDevice.isConnected() ? 1 : 0) - (isConnected() ? 1 : 0);
        if (i != 0) {
            return i;
        }
        int i2 = (cachedBluetoothDevice.getBondState() == 12 ? 1 : 0) - (getBondState() != 12 ? 0 : 1);
        if (i2 != 0) {
            return i2;
        }
        int i3 = (cachedBluetoothDevice.mJustDiscovered ? 1 : 0) - (this.mJustDiscovered ? 1 : 0);
        if (i3 != 0) {
            return i3;
        }
        int i4 = cachedBluetoothDevice.mRssi - this.mRssi;
        return i4 != 0 ? i4 : getName().compareTo(cachedBluetoothDevice.getName());
    }

    private void migratePhonebookPermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setPhonebookAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setPhonebookAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void migrateMessagePermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getMessageAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setMessageAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setMessageAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void processPhonebookAccess() {
        if (this.mDevice.getBondState() == 12 && BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS)) {
            BluetoothClass bluetoothClass = this.mDevice.getBluetoothClass();
            if (this.mDevice.getPhonebookAccessPermission() != 0 || bluetoothClass == null) {
                return;
            }
            if (bluetoothClass.getDeviceClass() == 1032 || bluetoothClass.getDeviceClass() == 1028) {
                EventLog.writeEvent(1397638484, "138529441", -1, "");
            }
        }
    }

    public String getConnectionSummary() {
        return getConnectionSummary(false);
    }

    public String getConnectionSummary(boolean z) {
        int i;
        int i2;
        int i3;
        if (isProfileConnectedFail() && isConnected()) {
            return this.mContext.getString(R$string.profile_connect_timeout_subtext);
        }
        synchronized (this.mProfileLock) {
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = true;
            boolean z5 = true;
            boolean z6 = true;
            boolean z7 = true;
            for (LocalBluetoothProfile localBluetoothProfile : getProfiles()) {
                int profileConnectionState = getProfileConnectionState(localBluetoothProfile);
                if (profileConnectionState != 0) {
                    if (profileConnectionState != 1) {
                        if (profileConnectionState != 2) {
                            if (profileConnectionState != 3) {
                            }
                        } else if (Utils.isSupportedBTSink() && (localBluetoothProfile instanceof A2dpProfile)) {
                            z2 = true;
                            z3 = true;
                        } else {
                            z2 = true;
                        }
                    }
                    return this.mContext.getString(BluetoothUtils.getConnectionStateSummary(profileConnectionState));
                } else if (localBluetoothProfile.isProfileReady()) {
                    if (!(localBluetoothProfile instanceof A2dpProfile) && !(localBluetoothProfile instanceof A2dpSinkProfile)) {
                        if (!(localBluetoothProfile instanceof HeadsetProfile) && !(localBluetoothProfile instanceof HfpClientProfile)) {
                            if (localBluetoothProfile instanceof HearingAidProfile) {
                                z6 = false;
                            } else if (localBluetoothProfile instanceof LeAudioProfile) {
                                z7 = false;
                            }
                        }
                        z5 = false;
                    }
                    if (!Utils.isSupportedBTSink() || !z3) {
                        z4 = false;
                    }
                }
            }
            int batteryLevel = getBatteryLevel();
            int i4 = -1;
            String formatPercentage = batteryLevel > -1 ? Utils.formatPercentage(batteryLevel) : null;
            int i5 = R$string.bluetooth_pairing;
            if (z2) {
                if (BluetoothUtils.getBooleanMetaData(this.mDevice, 6)) {
                    i4 = BluetoothUtils.getIntMetaData(this.mDevice, 10);
                    i = BluetoothUtils.getIntMetaData(this.mDevice, 11);
                } else {
                    i = -1;
                }
                if (isTwsBatteryAvailable(i4, i)) {
                    i2 = R$string.bluetooth_battery_level_untethered;
                } else {
                    i2 = formatPercentage != null ? R$string.bluetooth_battery_level : i5;
                }
                if (z4 || z5 || z6 || z7) {
                    boolean isAudioModeOngoingCall = Utils.isAudioModeOngoingCall(this.mContext);
                    if (this.mIsActiveDeviceHearingAid || ((this.mIsActiveDeviceHeadset && isAudioModeOngoingCall) || ((this.mIsActiveDeviceA2dp && !isAudioModeOngoingCall) || this.mIsActiveDeviceLeAudio))) {
                        if (isTwsBatteryAvailable(i4, i) && !z) {
                            i2 = R$string.bluetooth_active_battery_level_untethered;
                        } else if (formatPercentage != null && !z) {
                            i2 = R$string.bluetooth_active_battery_level;
                        } else {
                            i2 = R$string.bluetooth_active_no_battery_level;
                        }
                    }
                    if (this.mIsActiveDeviceHearingAid && i2 == (i3 = R$string.bluetooth_active_no_battery_level)) {
                        CachedBluetoothDevice subDevice = getSubDevice();
                        if (subDevice != null && subDevice.isConnected()) {
                            i2 = R$string.bluetooth_hearing_aid_left_and_right_active;
                        } else {
                            int i6 = this.mDeviceSide;
                            if (i6 == 0) {
                                i2 = R$string.bluetooth_hearing_aid_left_active;
                            } else {
                                i2 = i6 == 1 ? R$string.bluetooth_hearing_aid_right_active : i3;
                            }
                        }
                    }
                }
            } else {
                i = -1;
                i2 = i5;
            }
            if (i2 != i5 || getBondState() == 11) {
                return isTwsBatteryAvailable(i4, i) ? this.mContext.getString(i2, Utils.formatPercentage(i4), Utils.formatPercentage(i)) : this.mContext.getString(i2, formatPercentage);
            }
            return null;
        }
    }

    private boolean isProfileConnectedFail() {
        return this.mIsA2dpProfileConnectedFail || this.mIsHearingAidProfileConnectedFail || (!isConnectedSapDevice() && this.mIsHeadsetProfileConnectedFail) || this.mIsLeAudioProfileConnectedFail;
    }

    public boolean isConnectedA2dpDevice() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        return a2dpProfile != null && a2dpProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHfpDevice() {
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        return headsetProfile != null && headsetProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        return hearingAidProfile != null && hearingAidProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedLeAudioDevice() {
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        return leAudioProfile != null && leAudioProfile.getConnectionStatus(this.mDevice) == 2;
    }

    private boolean isConnectedSapDevice() {
        SapProfile sapProfile = this.mProfileManager.getSapProfile();
        return sapProfile != null && sapProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public CachedBluetoothDevice getSubDevice() {
        return this.mSubDevice;
    }

    public void setSubDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mSubDevice = cachedBluetoothDevice;
    }

    public void switchSubDeviceContent() {
        BluetoothDevice bluetoothDevice = this.mDevice;
        short s = this.mRssi;
        boolean z = this.mJustDiscovered;
        int i = this.mDeviceSide;
        CachedBluetoothDevice cachedBluetoothDevice = this.mSubDevice;
        this.mDevice = cachedBluetoothDevice.mDevice;
        this.mRssi = cachedBluetoothDevice.mRssi;
        this.mJustDiscovered = cachedBluetoothDevice.mJustDiscovered;
        this.mDeviceSide = cachedBluetoothDevice.mDeviceSide;
        cachedBluetoothDevice.mDevice = bluetoothDevice;
        cachedBluetoothDevice.mRssi = s;
        cachedBluetoothDevice.mJustDiscovered = z;
        cachedBluetoothDevice.mDeviceSide = i;
        fetchActiveDevices();
    }

    public Set<CachedBluetoothDevice> getMemberDevice() {
        return this.mMemberDevices;
    }

    public void addMemberDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mMemberDevices.add(cachedBluetoothDevice);
    }

    public void removeMemberDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mMemberDevices.remove(cachedBluetoothDevice);
    }

    public void switchMemberDeviceContent(CachedBluetoothDevice cachedBluetoothDevice, CachedBluetoothDevice cachedBluetoothDevice2) {
        BluetoothDevice bluetoothDevice = this.mDevice;
        short s = this.mRssi;
        boolean z = this.mJustDiscovered;
        this.mDevice = cachedBluetoothDevice2.mDevice;
        this.mRssi = cachedBluetoothDevice2.mRssi;
        this.mJustDiscovered = cachedBluetoothDevice2.mJustDiscovered;
        addMemberDevice(cachedBluetoothDevice);
        this.mMemberDevices.remove(cachedBluetoothDevice2);
        cachedBluetoothDevice2.mDevice = bluetoothDevice;
        cachedBluetoothDevice2.mRssi = s;
        cachedBluetoothDevice2.mJustDiscovered = z;
        fetchActiveDevices();
    }

    public Pair<Drawable, String> getDrawableWithDescription() {
        Uri uriMetaData = BluetoothUtils.getUriMetaData(this.mDevice, 5);
        Pair<Drawable, String> btClassDrawableWithDescription = BluetoothUtils.getBtClassDrawableWithDescription(this.mContext, this);
        if (BluetoothUtils.isAdvancedDetailsHeader(this.mDevice) && uriMetaData != null) {
            BitmapDrawable bitmapDrawable = this.mDrawableCache.get(uriMetaData.toString());
            if (bitmapDrawable != null) {
                return new Pair<>(new AdaptiveOutlineDrawable(this.mContext.getResources(), bitmapDrawable.getBitmap()), (String) btClassDrawableWithDescription.second);
            }
            refresh();
        }
        return new Pair<>(BluetoothUtils.buildBtRainbowDrawable(this.mContext, (Drawable) btClassDrawableWithDescription.first, getAddress().hashCode()), (String) btClassDrawableWithDescription.second);
    }

    void releaseLruCache() {
        this.mDrawableCache.evictAll();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUnpairing() {
        return this.mUnpairing;
    }
}
