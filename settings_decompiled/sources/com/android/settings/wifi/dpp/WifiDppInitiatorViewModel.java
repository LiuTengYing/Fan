package com.android.settings.wifi.dpp;

import android.app.Application;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiManager;
import android.util.SparseArray;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
/* loaded from: classes2.dex */
public class WifiDppInitiatorViewModel extends AndroidViewModel {
    private int[] mBandArray;
    private MutableLiveData<Integer> mEnrolleeSuccessNetworkId;
    private boolean mIsWifiDppHandshaking;
    private MutableLiveData<Integer> mStatusCode;
    private SparseArray<int[]> mTriedChannels;
    private String mTriedSsid;

    public WifiDppInitiatorViewModel(Application application) {
        super(application);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MutableLiveData<Integer> getEnrolleeSuccessNetworkId() {
        if (this.mEnrolleeSuccessNetworkId == null) {
            this.mEnrolleeSuccessNetworkId = new MutableLiveData<>();
        }
        return this.mEnrolleeSuccessNetworkId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MutableLiveData<Integer> getStatusCode() {
        if (this.mStatusCode == null) {
            this.mStatusCode = new MutableLiveData<>();
        }
        return this.mStatusCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getTriedSsid() {
        return this.mTriedSsid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SparseArray<int[]> getTriedChannels() {
        return this.mTriedChannels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getBandArray() {
        return this.mBandArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isWifiDppHandshaking() {
        return this.mIsWifiDppHandshaking;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startEasyConnectAsConfiguratorInitiator(String str, int i) {
        this.mIsWifiDppHandshaking = true;
        ((WifiManager) getApplication().getSystemService(WifiManager.class)).startEasyConnectAsConfiguratorInitiator(str, i, 0, getApplication().getMainExecutor(), new EasyConnectDelegateCallback());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startEasyConnectAsEnrolleeInitiator(String str) {
        this.mIsWifiDppHandshaking = true;
        ((WifiManager) getApplication().getSystemService(WifiManager.class)).startEasyConnectAsEnrolleeInitiator(str, getApplication().getMainExecutor(), new EasyConnectDelegateCallback());
    }

    /* loaded from: classes2.dex */
    private class EasyConnectDelegateCallback extends EasyConnectStatusCallback {
        public void onProgress(int i) {
        }

        private EasyConnectDelegateCallback() {
        }

        public void onEnrolleeSuccess(int i) {
            WifiDppInitiatorViewModel.this.mIsWifiDppHandshaking = false;
            WifiDppInitiatorViewModel.this.mEnrolleeSuccessNetworkId.setValue(Integer.valueOf(i));
        }

        public void onConfiguratorSuccess(int i) {
            WifiDppInitiatorViewModel.this.mIsWifiDppHandshaking = false;
            WifiDppInitiatorViewModel.this.mStatusCode.setValue(1);
        }

        public void onFailure(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
            WifiDppInitiatorViewModel.this.mIsWifiDppHandshaking = false;
            WifiDppInitiatorViewModel.this.mTriedSsid = str;
            WifiDppInitiatorViewModel.this.mTriedChannels = sparseArray;
            WifiDppInitiatorViewModel.this.mBandArray = iArr;
            WifiDppInitiatorViewModel.this.mStatusCode.setValue(Integer.valueOf(i));
        }
    }
}
