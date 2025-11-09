package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.SidecarFragment;
import com.android.settings.network.EnableMultiSimSidecar;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SwitchToEuiccSubscriptionSidecar;
import com.android.settings.network.SwitchToRemovableSlotSidecar;
import com.android.settings.network.SwitchToRemovableSlotSidecar$$ExternalSyntheticLambda0;
import com.android.settings.network.UiccSlotUtil;
import com.android.settings.network.telephony.ConfirmDialogFragment;
import com.android.settings.sim.SimActivationNotifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ToggleSubscriptionDialogActivity extends SubscriptionActionDialogActivity implements SidecarFragment.Listener, ConfirmDialogFragment.OnConfirmListener {
    @VisibleForTesting
    public static final String ARG_enable = "enable";
    private boolean isRtlMode;
    private List<SubscriptionInfo> mActiveSubInfos;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String stringExtra = intent.getStringExtra("ss");
            int intExtra = intent.getIntExtra("phone", -1);
            Log.d("ToggleSubscriptionDialogActivity", "received ACTION_SIM_STATE_CHANGED,state =" + stringExtra + "phoneId" + ToggleSubscriptionDialogActivity.this.mPhoneId + "phoneIdConfirm =" + intExtra);
            if (stringExtra != null && ToggleSubscriptionDialogActivity.this.mPhoneId == intExtra && stringExtra.equals("ABSENT")) {
                ToggleSubscriptionDialogActivity.this.finish();
            }
        }
    };
    private boolean mEnable;
    private EnableMultiSimSidecar mEnableMultiSimSidecar;
    private boolean mIsEsimOperation;
    private int mPhoneId;
    private SubscriptionInfo mSubInfo;
    private SwitchToEuiccSubscriptionSidecar mSwitchToEuiccSubscriptionSidecar;
    private SwitchToRemovableSlotSidecar mSwitchToRemovableSlotSidecar;
    private TelephonyManager mTelMgr;

    public static Intent getIntent(Context context, int i, boolean z) {
        Intent intent = new Intent(context, ToggleSubscriptionDialogActivity.class);
        intent.putExtra("sub_id", i);
        intent.putExtra(ARG_enable, z);
        return intent;
    }

    @Override // com.android.settings.network.telephony.SubscriptionActionDialogActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra("sub_id", -1);
        this.mPhoneId = SubscriptionManager.getPhoneId(intExtra);
        this.mTelMgr = (TelephonyManager) getSystemService(TelephonyManager.class);
        if (!((UserManager) getSystemService(UserManager.class)).isAdminUser()) {
            Log.e("ToggleSubscriptionDialogActivity", "It is not the admin user. Unable to toggle subscription.");
            finish();
        } else if (!SubscriptionManager.isUsableSubscriptionId(intExtra)) {
            Log.e("ToggleSubscriptionDialogActivity", "The subscription id is not usable.");
            finish();
        } else {
            this.mActiveSubInfos = SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager);
            SubscriptionInfo subById = SubscriptionUtil.getSubById(this.mSubscriptionManager, intExtra);
            this.mSubInfo = subById;
            this.mIsEsimOperation = subById != null && subById.isEmbedded();
            this.mSwitchToEuiccSubscriptionSidecar = SwitchToEuiccSubscriptionSidecar.get(getFragmentManager());
            this.mSwitchToRemovableSlotSidecar = SwitchToRemovableSlotSidecar.get(getFragmentManager());
            this.mEnableMultiSimSidecar = EnableMultiSimSidecar.get(getFragmentManager());
            this.mEnable = intent.getBooleanExtra(ARG_enable, true);
            this.isRtlMode = getResources().getConfiguration().getLayoutDirection() == 1;
            Log.i("ToggleSubscriptionDialogActivity", "isMultipleEnabledProfilesSupported():" + isMultipleEnabledProfilesSupported());
            if (bundle == null) {
                if (this.mEnable) {
                    showEnableSubDialog();
                } else {
                    showDisableSimConfirmDialog();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        getApplicationContext().registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mSwitchToEuiccSubscriptionSidecar.addListener(this);
        this.mSwitchToRemovableSlotSidecar.addListener(this);
        this.mEnableMultiSimSidecar.addListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        getApplicationContext().unregisterReceiver(this.mBroadcastReceiver);
        this.mEnableMultiSimSidecar.removeListener(this);
        this.mSwitchToRemovableSlotSidecar.removeListener(this);
        this.mSwitchToEuiccSubscriptionSidecar.removeListener(this);
        super.onPause();
    }

    @Override // com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        if (sidecarFragment == this.mSwitchToEuiccSubscriptionSidecar) {
            handleSwitchToEuiccSubscriptionSidecarStateChange();
        } else if (sidecarFragment == this.mSwitchToRemovableSlotSidecar) {
            handleSwitchToRemovableSlotSidecarStateChange();
        } else if (sidecarFragment == this.mEnableMultiSimSidecar) {
            handleEnableMultiSimSidecarStateChange();
        }
    }

    @Override // com.android.settings.network.telephony.ConfirmDialogFragment.OnConfirmListener
    public void onConfirm(int i, boolean z, int i2) {
        List<SubscriptionInfo> list;
        if (!z && i != 3 && i != 4) {
            finish();
            return;
        }
        SubscriptionInfo subscriptionInfo = null;
        if (i == 1) {
            if (this.mIsEsimOperation) {
                Log.i("ToggleSubscriptionDialogActivity", "Disabling the eSIM profile.");
                showProgressDialog(getString(R$string.privileged_action_disable_sub_dialog_progress));
                SubscriptionInfo subscriptionInfo2 = this.mSubInfo;
                this.mSwitchToEuiccSubscriptionSidecar.run(-1, subscriptionInfo2 != null ? subscriptionInfo2.getPortIndex() : 0, null);
                return;
            }
            Log.i("ToggleSubscriptionDialogActivity", "Disabling the pSIM profile.");
            handleTogglePsimAction();
            return;
        }
        if (i != 2) {
            if (i == 3) {
                if (!z) {
                    Log.i("ToggleSubscriptionDialogActivity", "User cancel the dialog to enable DSDS.");
                    showEnableSimConfirmDialog();
                    return;
                } else if (this.mTelMgr.doesSwitchMultiSimConfigTriggerReboot()) {
                    Log.i("ToggleSubscriptionDialogActivity", "Device does not support reboot free DSDS.");
                    showRebootConfirmDialog();
                    return;
                } else {
                    Log.i("ToggleSubscriptionDialogActivity", "Enabling DSDS without rebooting.");
                    showProgressDialog(getString(R$string.sim_action_enabling_sim_without_carrier_name));
                    this.mEnableMultiSimSidecar.run(2);
                    return;
                }
            } else if (i == 4) {
                if (!z) {
                    Log.i("ToggleSubscriptionDialogActivity", "User cancel the dialog to reboot to enable DSDS.");
                    showEnableSimConfirmDialog();
                    return;
                }
                Log.i("ToggleSubscriptionDialogActivity", "User confirmed reboot to enable DSDS.");
                SimActivationNotifier.setShowSimSettingsNotification(this, true);
                this.mTelMgr.switchMultiSimConfig(2);
                return;
            } else if (i != 5) {
                Log.e("ToggleSubscriptionDialogActivity", "Unrecognized confirmation dialog tag: " + i);
                return;
            } else if (i2 != -1 && (list = this.mActiveSubInfos) != null) {
                subscriptionInfo = list.get(i2);
            }
        }
        Log.i("ToggleSubscriptionDialogActivity", "User confirmed to enable the subscription.");
        showProgressDialog(getString(R$string.sim_action_switch_sub_dialog_progress, new Object[]{SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubInfo, this)}));
        if (this.mIsEsimOperation) {
            this.mSwitchToEuiccSubscriptionSidecar.run(this.mSubInfo.getSubscriptionId(), -1, subscriptionInfo);
        } else {
            this.mSwitchToRemovableSlotSidecar.run(-1, subscriptionInfo);
        }
    }

    private void handleSwitchToEuiccSubscriptionSidecarStateChange() {
        int state = this.mSwitchToEuiccSubscriptionSidecar.getState();
        String str = ARG_enable;
        if (state == 2) {
            Object[] objArr = new Object[1];
            if (!this.mEnable) {
                str = "disable";
            }
            objArr[0] = str;
            Log.i("ToggleSubscriptionDialogActivity", String.format("Successfully %s the eSIM profile.", objArr));
            this.mSwitchToEuiccSubscriptionSidecar.reset();
            dismissProgressDialog();
            finish();
        } else if (state != 3) {
        } else {
            Object[] objArr2 = new Object[1];
            if (!this.mEnable) {
                str = "disable";
            }
            objArr2[0] = str;
            Log.i("ToggleSubscriptionDialogActivity", String.format("Failed to %s the eSIM profile.", objArr2));
            this.mSwitchToEuiccSubscriptionSidecar.reset();
            dismissProgressDialog();
            showErrorDialog(getString(R$string.privileged_action_disable_fail_title), getString(R$string.privileged_action_disable_fail_text));
        }
    }

    private void handleSwitchToRemovableSlotSidecarStateChange() {
        int state = this.mSwitchToRemovableSlotSidecar.getState();
        if (state == 2) {
            Log.i("ToggleSubscriptionDialogActivity", "Successfully switched to removable slot.");
            this.mSwitchToRemovableSlotSidecar.reset();
            handleTogglePsimAction();
            dismissProgressDialog();
            finish();
        } else if (state != 3) {
        } else {
            Log.e("ToggleSubscriptionDialogActivity", "Failed switching to removable slot.");
            this.mSwitchToRemovableSlotSidecar.reset();
            dismissProgressDialog();
            showErrorDialog(getString(R$string.sim_action_enable_sim_fail_title), getString(R$string.sim_action_enable_sim_fail_text));
        }
    }

    private void handleEnableMultiSimSidecarStateChange() {
        int state = this.mEnableMultiSimSidecar.getState();
        if (state == 2) {
            this.mEnableMultiSimSidecar.reset();
            Log.i("ToggleSubscriptionDialogActivity", "Successfully switched to DSDS without reboot.");
            handleEnableSubscriptionAfterEnablingDsds();
        } else if (state != 3) {
        } else {
            this.mEnableMultiSimSidecar.reset();
            Log.i("ToggleSubscriptionDialogActivity", "Failed to switch to DSDS without rebooting.");
            dismissProgressDialog();
            showErrorDialog(getString(R$string.dsds_activation_failure_title), getString(R$string.dsds_activation_failure_body_msg2));
        }
    }

    private void handleEnableSubscriptionAfterEnablingDsds() {
        if (this.mIsEsimOperation) {
            Log.i("ToggleSubscriptionDialogActivity", "DSDS enabled, start to enable profile: " + this.mSubInfo.getSubscriptionId());
            this.mSwitchToEuiccSubscriptionSidecar.run(this.mSubInfo.getSubscriptionId(), -1, null);
            return;
        }
        Log.i("ToggleSubscriptionDialogActivity", "DSDS enabled, start to enable pSIM profile.");
        handleTogglePsimAction();
        dismissProgressDialog();
        finish();
    }

    private void handleTogglePsimAction() {
        SubscriptionInfo subscriptionInfo;
        if (this.mSubscriptionManager.canDisablePhysicalSubscription() && (subscriptionInfo = this.mSubInfo) != null) {
            this.mSubscriptionManager.setUiccApplicationsEnabled(subscriptionInfo.getSubscriptionId(), this.mEnable);
            finish();
            return;
        }
        Log.i("ToggleSubscriptionDialogActivity", "The device does not support toggling pSIM. It is enough to just enable the removable slot.");
    }

    private void showEnableSubDialog() {
        Log.d("ToggleSubscriptionDialogActivity", "Handle subscription enabling.");
        if (isDsdsConditionSatisfied()) {
            showEnableDsdsConfirmDialog();
        } else if (!this.mIsEsimOperation && this.mTelMgr.isMultiSimEnabled() && isRemovableSimEnabled()) {
            Log.i("ToggleSubscriptionDialogActivity", "Toggle on pSIM, no dialog displayed.");
            handleTogglePsimAction();
            finish();
        } else {
            showEnableSimConfirmDialog();
        }
    }

    private void showEnableDsdsConfirmDialog() {
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 3, getString(R$string.sim_action_enable_dsds_title), getString(R$string.sim_action_enable_dsds_text), getString(R$string.sim_action_yes), getString(R$string.sim_action_no_thanks));
    }

    private void showRebootConfirmDialog() {
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 4, getString(R$string.sim_action_restart_title), getString(R$string.sim_action_enable_dsds_text), getString(R$string.sim_action_reboot), getString(R$string.sim_action_cancel));
    }

    private void showDisableSimConfirmDialog() {
        String string;
        CharSequence displayName = this.mSubInfo.getDisplayName();
        if (this.mSubInfo == null || TextUtils.isEmpty(displayName)) {
            string = getString(R$string.privileged_action_disable_sub_dialog_title_without_carrier);
        } else {
            string = getString(R$string.privileged_action_disable_sub_dialog_title, new Object[]{displayName});
        }
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 1, string, null, getString(R$string.yes), getString(R$string.sim_action_cancel));
    }

    private void showEnableSimConfirmDialog() {
        SubscriptionInfo subscriptionInfo;
        List<SubscriptionInfo> list = this.mActiveSubInfos;
        if (list == null || list.isEmpty()) {
            Log.i("ToggleSubscriptionDialogActivity", "No active subscriptions available.");
            showNonSwitchSimConfirmDialog();
            return;
        }
        Log.i("ToggleSubscriptionDialogActivity", "mActiveSubInfos:" + this.mActiveSubInfos);
        boolean z = this.mIsEsimOperation && this.mActiveSubInfos.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isEmbedded;
                isEmbedded = ((SubscriptionInfo) obj).isEmbedded();
                return isEmbedded;
            }
        });
        boolean isMultiSimEnabled = this.mTelMgr.isMultiSimEnabled();
        if (isMultiSimEnabled && !isMultipleEnabledProfilesSupported() && !z) {
            showNonSwitchSimConfirmDialog();
        } else if (isMultiSimEnabled && isMultipleEnabledProfilesSupported()) {
            if (this.mActiveSubInfos.size() < 2) {
                showNonSwitchSimConfirmDialog();
            } else {
                showMepSwitchSimConfirmDialog();
            }
        } else {
            if (isMultiSimEnabled && z) {
                subscriptionInfo = this.mActiveSubInfos.stream().filter(new Predicate() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean isEmbedded;
                        isEmbedded = ((SubscriptionInfo) obj).isEmbedded();
                        return isEmbedded;
                    }
                }).findFirst().get();
            } else {
                subscriptionInfo = this.mActiveSubInfos.get(0);
            }
            ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 2, getSwitchSubscriptionTitle(), getSwitchDialogBodyMsg(subscriptionInfo, z), getSwitchDialogPosBtnText(), getString(R$string.sim_action_cancel));
        }
    }

    private void showNonSwitchSimConfirmDialog() {
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 2, getEnableSubscriptionTitle(), null, getString(R$string.yes), getString(R$string.sim_action_cancel));
    }

    private void showMepSwitchSimConfirmDialog() {
        Log.d("ToggleSubscriptionDialogActivity", "showMepSwitchSimConfirmDialog");
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubInfo, this);
        String string = getString(R$string.sim_action_switch_sub_dialog_mep_title, new Object[]{uniqueSubscriptionDisplayName});
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R$string.sim_action_switch_sub_dialog_mep_text, new Object[]{uniqueSubscriptionDisplayName}));
        if (this.isRtlMode) {
            sb.insert(0, "\u200f").insert(sb.length(), "\u200f");
        }
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 5, string, sb.toString(), null, null, getSwitchDialogBodyList());
    }

    private String getSwitchDialogPosBtnText() {
        if (this.mIsEsimOperation) {
            return getString(R$string.sim_action_switch_sub_dialog_confirm, new Object[]{SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubInfo, this)});
        }
        return getString(R$string.sim_switch_button);
    }

    private String getEnableSubscriptionTitle() {
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubInfo, this);
        return (this.mSubInfo == null || TextUtils.isEmpty(uniqueSubscriptionDisplayName)) ? getString(R$string.sim_action_enable_sub_dialog_title_without_carrier_name) : getString(R$string.sim_action_enable_sub_dialog_title, new Object[]{uniqueSubscriptionDisplayName});
    }

    private String getSwitchSubscriptionTitle() {
        if (this.mIsEsimOperation) {
            return getString(R$string.sim_action_switch_sub_dialog_title, new Object[]{SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubInfo, this)});
        }
        return getString(R$string.sim_action_switch_psim_dialog_title);
    }

    private String getSwitchDialogBodyMsg(SubscriptionInfo subscriptionInfo, boolean z) {
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubInfo, this);
        CharSequence uniqueSubscriptionDisplayName2 = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this);
        StringBuilder sb = new StringBuilder();
        if (z && this.mIsEsimOperation) {
            sb.append(getString(R$string.sim_action_switch_sub_dialog_text_downloaded, new Object[]{uniqueSubscriptionDisplayName, uniqueSubscriptionDisplayName2}));
        } else if (this.mIsEsimOperation) {
            sb.append(getString(R$string.sim_action_switch_sub_dialog_text, new Object[]{uniqueSubscriptionDisplayName, uniqueSubscriptionDisplayName2}));
        } else {
            sb.append(getString(R$string.sim_action_switch_sub_dialog_text_single_sim, new Object[]{uniqueSubscriptionDisplayName2}));
        }
        if (this.isRtlMode) {
            sb.insert(0, "\u200f").insert(sb.indexOf("\n") - 1, "\u200f").insert(sb.indexOf("\n") + 2, "\u200f").insert(sb.length(), "\u200f");
        }
        return sb.toString();
    }

    private ArrayList<String> getSwitchDialogBodyList() {
        ArrayList<String> arrayList = new ArrayList<>((Collection) this.mActiveSubInfos.stream().map(new Function() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$getSwitchDialogBodyList$2;
                lambda$getSwitchDialogBodyList$2 = ToggleSubscriptionDialogActivity.this.lambda$getSwitchDialogBodyList$2((SubscriptionInfo) obj);
                return lambda$getSwitchDialogBodyList$2;
            }
        }).collect(Collectors.toList()));
        arrayList.add(getString(R$string.sim_action_cancel));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getSwitchDialogBodyList$2(SubscriptionInfo subscriptionInfo) {
        return getString(R$string.sim_action_switch_sub_dialog_carrier_list_item_for_turning_off, new Object[]{SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this)});
    }

    private boolean isDsdsConditionSatisfied() {
        if (this.mTelMgr.isMultiSimEnabled()) {
            Log.d("ToggleSubscriptionDialogActivity", "DSDS is already enabled. Condition not satisfied.");
            return false;
        } else if (this.mTelMgr.isMultiSimSupported() != 0) {
            Log.d("ToggleSubscriptionDialogActivity", "Hardware does not support DSDS.");
            return false;
        } else {
            boolean z = SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager).size() > 0;
            if (isMultipleEnabledProfilesSupported() && z) {
                Log.d("ToggleSubscriptionDialogActivity", "Device supports MEP and eSIM operation and eSIM profile is enabled. DSDS condition satisfied.");
                return true;
            }
            boolean isRemovableSimEnabled = isRemovableSimEnabled();
            if (this.mIsEsimOperation && isRemovableSimEnabled) {
                Log.d("ToggleSubscriptionDialogActivity", "eSIM operation and removable SIM is enabled. DSDS condition satisfied.");
                return true;
            }
            boolean anyMatch = SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager).stream().anyMatch(new SwitchToRemovableSlotSidecar$$ExternalSyntheticLambda0());
            if (!this.mIsEsimOperation && anyMatch) {
                Log.d("ToggleSubscriptionDialogActivity", "Removable SIM operation and eSIM profile is enabled. DSDS condition satisfied.");
                return true;
            }
            Log.d("ToggleSubscriptionDialogActivity", "DSDS condition not satisfied.");
            return false;
        }
    }

    private boolean isRemovableSimEnabled() {
        boolean anyMatch = UiccSlotUtil.getSlotInfos(this.mTelMgr).stream().anyMatch(new Predicate() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isRemovableSimEnabled$4;
                lambda$isRemovableSimEnabled$4 = ToggleSubscriptionDialogActivity.lambda$isRemovableSimEnabled$4((UiccSlotInfo) obj);
                return lambda$isRemovableSimEnabled$4;
            }
        });
        Log.i("ToggleSubscriptionDialogActivity", "isRemovableSimEnabled: " + anyMatch);
        return anyMatch;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isRemovableSimEnabled$4(UiccSlotInfo uiccSlotInfo) {
        return uiccSlotInfo != null && uiccSlotInfo.isRemovable() && uiccSlotInfo.getPorts().stream().anyMatch(new Predicate() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isActive;
                isActive = ((UiccPortInfo) obj).isActive();
                return isActive;
            }
        }) && uiccSlotInfo.getCardStateInfo() == 2;
    }

    private boolean isMultipleEnabledProfilesSupported() {
        List<UiccCardInfo> uiccCardsInfo = this.mTelMgr.getUiccCardsInfo();
        if (uiccCardsInfo == null) {
            Log.w("ToggleSubscriptionDialogActivity", "UICC cards info list is empty.");
            return false;
        }
        return uiccCardsInfo.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.telephony.ToggleSubscriptionDialogActivity$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isMultipleEnabledProfilesSupported;
                isMultipleEnabledProfilesSupported = ((UiccCardInfo) obj).isMultipleEnabledProfilesSupported();
                return isMultipleEnabledProfilesSupported;
            }
        });
    }
}
