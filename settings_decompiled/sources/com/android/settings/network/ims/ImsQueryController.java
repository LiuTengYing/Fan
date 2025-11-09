package com.android.settings.network.ims;

import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
abstract class ImsQueryController {
    private volatile int mCapability;
    private volatile int mTech;
    private volatile int mTransportType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImsQueryController(int i, int i2, int i3) {
        this.mCapability = i;
        this.mTech = i2;
        this.mTransportType = i3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTtyOnVolteEnabled(int i) {
        return new ImsQueryTtyOnVolteStat(i).query();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEnabledByPlatform(int i) throws InterruptedException, ImsException, IllegalArgumentException {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            ImsMmTelManager createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(i);
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            BooleanConsumer booleanConsumer = new BooleanConsumer();
            createForSubscriptionId.isSupported(this.mCapability, this.mTransportType, newSingleThreadExecutor, booleanConsumer);
            return booleanConsumer.get(2000L);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isProvisionedOnDevice(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return new ImsQueryProvisioningStat(i, this.mCapability, this.mTech).query();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isServiceStateReady(int i) throws InterruptedException, ImsException, IllegalArgumentException {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            ImsMmTelManager createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(i);
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            IntegerConsumer integerConsumer = new IntegerConsumer();
            createForSubscriptionId.getFeatureState(newSingleThreadExecutor, integerConsumer);
            return integerConsumer.get(2000L) == 2;
        }
        return false;
    }

    public int getProvisioningIntValue(int i, int i2) {
        return new ImsQueryProvisioningStat(i, this.mCapability, this.mTech).queryProvisioningInt(i2);
    }
}
