package com.android.settings.sim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SidecarFragment;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SwitchToEuiccSubscriptionSidecar;
import com.android.settings.network.SwitchToRemovableSlotSidecar;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.GlifRecyclerLayout;
import com.google.android.setupdesign.items.Dividable;
import com.google.android.setupdesign.items.IItem;
import com.google.android.setupdesign.items.Item;
import com.google.android.setupdesign.items.ItemGroup;
import com.google.android.setupdesign.items.RecyclerItemAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class ChooseSimActivity extends Activity implements RecyclerItemAdapter.OnItemSelectedListener, SidecarFragment.Listener {
    private boolean mHasPsim;
    private boolean mIsSwitching;
    private ItemGroup mItemGroup;
    private boolean mNoPsimContinueToSettings;
    private int mSelectedItemIndex;
    private SwitchToEuiccSubscriptionSidecar mSwitchToEuiccSubscriptionSidecar;
    private SwitchToRemovableSlotSidecar mSwitchToRemovableSlotSidecar;
    private ArrayList<SubscriptionInfo> mEmbeddedSubscriptions = new ArrayList<>();
    private SubscriptionInfo mRemovableSubscription = null;

    public static Intent getIntent(Context context) {
        return new Intent(context, ChooseSimActivity.class);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.choose_sim_activity);
        Intent intent = getIntent();
        this.mHasPsim = intent.getBooleanExtra("has_psim", false);
        this.mNoPsimContinueToSettings = intent.getBooleanExtra("no_psim_continue_to_settings", false);
        updateSubscriptions();
        if (this.mEmbeddedSubscriptions.size() == 0) {
            Log.e("ChooseSimActivity", "Unable to find available eSIM subscriptions.");
            finish();
            return;
        }
        if (bundle != null) {
            this.mSelectedItemIndex = bundle.getInt("selected_index");
            this.mIsSwitching = bundle.getBoolean("is_switching");
        }
        GlifLayout glifLayout = (GlifLayout) findViewById(R$id.glif_layout);
        int size = this.mEmbeddedSubscriptions.size();
        if (this.mHasPsim) {
            size++;
        }
        glifLayout.setHeaderText(getString(R$string.choose_sim_title));
        MessageFormat messageFormat = new MessageFormat(getString(R$string.choose_sim_text), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("count", Integer.valueOf(size));
        glifLayout.setDescriptionText(messageFormat.format(hashMap));
        displaySubscriptions();
        this.mSwitchToRemovableSlotSidecar = SwitchToRemovableSlotSidecar.get(getFragmentManager());
        this.mSwitchToEuiccSubscriptionSidecar = SwitchToEuiccSubscriptionSidecar.get(getFragmentManager());
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mSwitchToRemovableSlotSidecar.addListener(this);
        this.mSwitchToEuiccSubscriptionSidecar.addListener(this);
    }

    @Override // android.app.Activity
    public void onPause() {
        this.mSwitchToEuiccSubscriptionSidecar.removeListener(this);
        this.mSwitchToRemovableSlotSidecar.removeListener(this);
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("selected_index", this.mSelectedItemIndex);
        bundle.putBoolean("is_switching", this.mIsSwitching);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.google.android.setupdesign.items.RecyclerItemAdapter.OnItemSelectedListener
    public void onItemSelected(IItem iItem) {
        if (this.mIsSwitching) {
            return;
        }
        this.mIsSwitching = true;
        Item item = (Item) iItem;
        item.setSummary(getString(R$string.choose_sim_activating));
        int id = item.getId();
        this.mSelectedItemIndex = id;
        if (id == -1) {
            Log.i("ChooseSimActivity", "Ready to switch to pSIM slot.");
            this.mSwitchToRemovableSlotSidecar.run(-1, null);
            return;
        }
        Log.i("ChooseSimActivity", "Ready to switch to eSIM subscription with index: " + this.mSelectedItemIndex);
        this.mSwitchToEuiccSubscriptionSidecar.run(this.mEmbeddedSubscriptions.get(this.mSelectedItemIndex).getSubscriptionId(), -1, null);
    }

    @Override // com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        SubscriptionInfo firstRemovableSubscription;
        SwitchToRemovableSlotSidecar switchToRemovableSlotSidecar = this.mSwitchToRemovableSlotSidecar;
        if (sidecarFragment == switchToRemovableSlotSidecar) {
            int state = switchToRemovableSlotSidecar.getState();
            if (state != 2) {
                if (state != 3) {
                    return;
                }
                this.mSwitchToRemovableSlotSidecar.reset();
                Log.e("ChooseSimActivity", "Failed to switch slot in ChooseSubscriptionsActivity.");
                handleEnableRemovableSimError();
                return;
            }
            this.mSwitchToRemovableSlotSidecar.reset();
            Log.i("ChooseSimActivity", "Switch slot successfully.");
            SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(SubscriptionManager.class);
            if (subscriptionManager.canDisablePhysicalSubscription() && (firstRemovableSubscription = SubscriptionUtil.getFirstRemovableSubscription(this)) != null) {
                subscriptionManager.setUiccApplicationsEnabled(firstRemovableSubscription.getSubscriptionId(), true);
            }
            finish();
            return;
        }
        SwitchToEuiccSubscriptionSidecar switchToEuiccSubscriptionSidecar = this.mSwitchToEuiccSubscriptionSidecar;
        if (sidecarFragment == switchToEuiccSubscriptionSidecar) {
            int state2 = switchToEuiccSubscriptionSidecar.getState();
            if (state2 == 2) {
                this.mSwitchToEuiccSubscriptionSidecar.reset();
                if (this.mNoPsimContinueToSettings) {
                    Log.e("ChooseSimActivity", "mNoPsimContinueToSettings is true which is not supported for now.");
                    return;
                }
                Log.i("ChooseSimActivity", "User finished selecting eSIM profile.");
                finish();
            } else if (state2 != 3) {
            } else {
                this.mSwitchToEuiccSubscriptionSidecar.reset();
                Log.e("ChooseSimActivity", "Failed to switch subscription in ChooseSubscriptionsActivity.");
                Item item = (Item) this.mItemGroup.getItemAt(this.mSelectedItemIndex);
                item.setEnabled(false);
                item.setSummary(getString(R$string.choose_sim_could_not_activate));
                this.mIsSwitching = false;
            }
        }
    }

    private void displaySubscriptions() {
        RecyclerItemAdapter recyclerItemAdapter = (RecyclerItemAdapter) ((GlifRecyclerLayout) findViewById(16908290).findViewById(R$id.glif_layout)).getAdapter();
        recyclerItemAdapter.setOnItemSelectedListener(this);
        this.mItemGroup = (ItemGroup) recyclerItemAdapter.getRootItemHierarchy();
        if (this.mHasPsim) {
            Item disableableItem = new DisableableItem();
            SubscriptionInfo subscriptionInfo = this.mRemovableSubscription;
            CharSequence uniqueSubscriptionDisplayName = subscriptionInfo != null ? SubscriptionUtil.getUniqueSubscriptionDisplayName(Integer.valueOf(subscriptionInfo.getSubscriptionId()), this) : null;
            if (TextUtils.isEmpty(uniqueSubscriptionDisplayName)) {
                uniqueSubscriptionDisplayName = getString(R$string.sim_card_label);
            }
            disableableItem.setTitle(uniqueSubscriptionDisplayName);
            if (this.mIsSwitching && this.mSelectedItemIndex == -1) {
                disableableItem.setSummary(getString(R$string.choose_sim_activating));
            } else {
                CharSequence formattedPhoneNumber = SubscriptionUtil.getFormattedPhoneNumber(this, this.mRemovableSubscription);
                if (TextUtils.isEmpty(formattedPhoneNumber)) {
                    formattedPhoneNumber = "";
                }
                disableableItem.setSummary(formattedPhoneNumber);
            }
            disableableItem.setId(-1);
            this.mItemGroup.addChild(disableableItem);
        }
        int i = 0;
        Iterator<SubscriptionInfo> it = this.mEmbeddedSubscriptions.iterator();
        while (it.hasNext()) {
            SubscriptionInfo next = it.next();
            DisableableItem disableableItem2 = new DisableableItem();
            CharSequence uniqueSubscriptionDisplayName2 = SubscriptionUtil.getUniqueSubscriptionDisplayName(Integer.valueOf(next.getSubscriptionId()), this);
            if (TextUtils.isEmpty(uniqueSubscriptionDisplayName2)) {
                uniqueSubscriptionDisplayName2 = next.getDisplayName();
            }
            disableableItem2.setTitle(uniqueSubscriptionDisplayName2);
            if (this.mIsSwitching && this.mSelectedItemIndex == i) {
                disableableItem2.setSummary(getString(R$string.choose_sim_activating));
            } else {
                String formattedPhoneNumber2 = SubscriptionUtil.getFormattedPhoneNumber(this, next);
                if (TextUtils.isEmpty(formattedPhoneNumber2)) {
                    formattedPhoneNumber2 = "";
                }
                disableableItem2.setSummary(formattedPhoneNumber2);
            }
            disableableItem2.setId(i);
            this.mItemGroup.addChild(disableableItem2);
            i++;
        }
    }

    private void updateSubscriptions() {
        List<SubscriptionInfo> selectableSubscriptionInfoList = SubscriptionUtil.getSelectableSubscriptionInfoList(this);
        if (selectableSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : selectableSubscriptionInfoList) {
                if (subscriptionInfo != null) {
                    if (subscriptionInfo.isEmbedded()) {
                        this.mEmbeddedSubscriptions.add(subscriptionInfo);
                    } else {
                        this.mRemovableSubscription = subscriptionInfo;
                    }
                }
            }
        }
    }

    private void handleEnableRemovableSimError() {
        int i = this.mSelectedItemIndex;
        if (i == -1) {
            i = 0;
        }
        Item item = (Item) this.mItemGroup.getItemAt(i);
        item.setEnabled(false);
        item.setSummary(getString(R$string.choose_sim_could_not_activate));
        this.mIsSwitching = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class DisableableItem extends Item implements Dividable {
        @Override // com.google.android.setupdesign.items.Dividable
        public boolean isDividerAllowedAbove() {
            return true;
        }

        @Override // com.google.android.setupdesign.items.Dividable
        public boolean isDividerAllowedBelow() {
            return true;
        }

        DisableableItem() {
        }

        @Override // com.google.android.setupdesign.items.Item, com.google.android.setupdesign.items.IItem
        public void onBindView(View view) {
            super.onBindView(view);
            ((TextView) view.findViewById(R$id.sud_items_title)).setEnabled(isEnabled());
            ((TextView) view.findViewById(R$id.sud_items_summary)).setEnabled(isEnabled());
        }
    }
}
