package com.android.settings.network;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.UiccSlotMapping;
import android.util.Log;
import com.android.settings.SidecarFragment;
import com.android.settings.network.telephony.EuiccOperationSidecar;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SwitchToEuiccSubscriptionSidecar extends EuiccOperationSidecar {
    private List<SubscriptionInfo> mActiveSubInfos;
    private PendingIntent mCallbackIntent;
    private boolean mIsDuringSimSlotMapping;
    private int mPort;
    private SubscriptionInfo mRemovedSubInfo;
    private int mSubId;

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar
    public String getReceiverAction() {
        return "com.android.settings.network.SWITCH_TO_SUBSCRIPTION";
    }

    public static SwitchToEuiccSubscriptionSidecar get(FragmentManager fragmentManager) {
        return (SwitchToEuiccSubscriptionSidecar) SidecarFragment.get(fragmentManager, "SwitchToEuiccSidecar", SwitchToEuiccSubscriptionSidecar.class, null);
    }

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar, com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        if (sidecarFragment == this.mSwitchSlotSidecar) {
            onSwitchSlotSidecarStateChange();
        } else {
            Log.wtf("SwitchToEuiccSidecar", "Received state change from a sidecar not expected.");
        }
    }

    public void run(int i, int i2, SubscriptionInfo subscriptionInfo) {
        setState(1, 0);
        this.mCallbackIntent = createCallbackIntent();
        this.mSubId = i;
        this.mActiveSubInfos = SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class));
        int targetSlot = getTargetSlot();
        if (targetSlot < 0) {
            Log.d("SwitchToEuiccSidecar", "There is no esim, the TargetSlot is " + targetSlot);
            setState(3, 0);
            return;
        }
        if (i2 < 0) {
            i2 = getTargetPortId(targetSlot, subscriptionInfo);
        }
        this.mPort = i2;
        this.mRemovedSubInfo = subscriptionInfo;
        Log.d("SwitchToEuiccSidecar", String.format("set esim into the SubId%d Physical Slot%d:Port%d", Integer.valueOf(this.mSubId), Integer.valueOf(targetSlot), Integer.valueOf(this.mPort)));
        if (this.mSubId == -1) {
            switchToSubscription();
        } else if ((this.mTelephonyManager.isMultiSimEnabled() && subscriptionInfo != null && subscriptionInfo.isEmbedded()) || isEsimEnabledAtTargetSlotPort(targetSlot, this.mPort)) {
            Log.d("SwitchToEuiccSidecar", "disable the enabled esim before the settings enables the target esim");
            this.mIsDuringSimSlotMapping = true;
            this.mEuiccManager.switchToSubscription(-1, this.mPort, this.mCallbackIntent);
        } else {
            this.mSwitchSlotSidecar.runSwitchToEuiccSlot(targetSlot, this.mPort, subscriptionInfo);
        }
    }

    private int getTargetPortId(final int i, SubscriptionInfo subscriptionInfo) {
        int i2 = 0;
        if (!isMultipleEnabledProfilesSupported(i)) {
            Log.d("SwitchToEuiccSidecar", "The slotId" + i + " is no MEP, port is 0");
            return 0;
        } else if (!this.mTelephonyManager.isMultiSimEnabled()) {
            Collection simSlotMapping = this.mTelephonyManager.getSimSlotMapping();
            Log.d("SwitchToEuiccSidecar", "In SS mode, the UiccSlotMapping: " + simSlotMapping);
            return simSlotMapping.stream().filter(new Predicate() { // from class: com.android.settings.network.SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getTargetPortId$0;
                    lambda$getTargetPortId$0 = SwitchToEuiccSubscriptionSidecar.lambda$getTargetPortId$0(i, (UiccSlotMapping) obj);
                    return lambda$getTargetPortId$0;
                }
            }).mapToInt(new ToIntFunction() { // from class: com.android.settings.network.SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda1
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int portIndex;
                    portIndex = ((UiccSlotMapping) obj).getPortIndex();
                    return portIndex;
                }
            }).findFirst().orElse(0);
        } else if (subscriptionInfo != null && subscriptionInfo.isEmbedded()) {
            return subscriptionInfo.getPortIndex();
        } else {
            List<SubscriptionInfo> list = this.mActiveSubInfos;
            if (list == null) {
                Log.d("SwitchToEuiccSidecar", "mActiveSubInfos is null.");
                return 0;
            }
            for (SubscriptionInfo subscriptionInfo2 : (List) list.stream().filter(new Predicate() { // from class: com.android.settings.network.SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean isEmbedded;
                    isEmbedded = ((SubscriptionInfo) obj).isEmbedded();
                    return isEmbedded;
                }
            }).sorted(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.settings.network.SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda3
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return ((SubscriptionInfo) obj).getPortIndex();
                }
            })).collect(Collectors.toList())) {
                if (subscriptionInfo2.getPortIndex() == i2) {
                    i2++;
                }
            }
            return i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getTargetPortId$0(int i, UiccSlotMapping uiccSlotMapping) {
        return uiccSlotMapping.getPhysicalSlotIndex() == i;
    }

    private int getTargetSlot() {
        return UiccSlotUtil.getEsimSlotId(getContext(), this.mSubId);
    }

    private boolean isEsimEnabledAtTargetSlotPort(int i, int i2) {
        List<SubscriptionInfo> list;
        final int logicalSlotIndex = getLogicalSlotIndex(i, i2);
        return (logicalSlotIndex == -1 || (list = this.mActiveSubInfos) == null || !list.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isEsimEnabledAtTargetSlotPort$3;
                lambda$isEsimEnabledAtTargetSlotPort$3 = SwitchToEuiccSubscriptionSidecar.lambda$isEsimEnabledAtTargetSlotPort$3(logicalSlotIndex, (SubscriptionInfo) obj);
                return lambda$isEsimEnabledAtTargetSlotPort$3;
            }
        })) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isEsimEnabledAtTargetSlotPort$3(int i, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.isEmbedded() && subscriptionInfo.getSimSlotIndex() == i;
    }

    private int getLogicalSlotIndex(int i, int i2) {
        ImmutableList<UiccSlotInfo> slotInfos = UiccSlotUtil.getSlotInfos(this.mTelephonyManager);
        if (slotInfos == null || i < 0 || i >= slotInfos.size() || slotInfos.get(i) == null) {
            return -1;
        }
        for (UiccPortInfo uiccPortInfo : slotInfos.get(i).getPorts()) {
            if (uiccPortInfo.getPortIndex() == i2) {
                return uiccPortInfo.getLogicalSlotIndex();
            }
        }
        return -1;
    }

    private void onSwitchSlotSidecarStateChange() {
        int state = this.mSwitchSlotSidecar.getState();
        if (state == 2) {
            this.mSwitchSlotSidecar.reset();
            Log.i("SwitchToEuiccSidecar", "Successfully SimSlotMapping. Start to enable/disable esim");
            switchToSubscription();
        } else if (state != 3) {
        } else {
            this.mSwitchSlotSidecar.reset();
            Log.i("SwitchToEuiccSidecar", "Failed to set SimSlotMapping");
            setState(3, 0);
        }
    }

    private boolean isMultipleEnabledProfilesSupported(final int i) {
        List<UiccCardInfo> uiccCardsInfo = this.mTelephonyManager.getUiccCardsInfo();
        if (uiccCardsInfo == null) {
            Log.w("SwitchToEuiccSidecar", "UICC cards info list is empty.");
            return false;
        }
        return uiccCardsInfo.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.SwitchToEuiccSubscriptionSidecar$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isMultipleEnabledProfilesSupported$4;
                lambda$isMultipleEnabledProfilesSupported$4 = SwitchToEuiccSubscriptionSidecar.lambda$isMultipleEnabledProfilesSupported$4(i, (UiccCardInfo) obj);
                return lambda$isMultipleEnabledProfilesSupported$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isMultipleEnabledProfilesSupported$4(int i, UiccCardInfo uiccCardInfo) {
        return uiccCardInfo.getPhysicalSlotIndex() == i && uiccCardInfo.isMultipleEnabledProfilesSupported();
    }

    private void switchToSubscription() {
        this.mEuiccManager.switchToSubscription(this.mSubId, this.mPort, this.mCallbackIntent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.network.telephony.EuiccOperationSidecar
    public void onActionReceived() {
        if (getResultCode() == 0 && this.mIsDuringSimSlotMapping) {
            this.mIsDuringSimSlotMapping = false;
            this.mSwitchSlotSidecar.runSwitchToEuiccSlot(getTargetSlot(), this.mPort, this.mRemovedSubInfo);
            return;
        }
        super.onActionReceived();
    }
}
