package com.android.settings.network.helper;

import android.content.Context;
import android.os.ParcelUuid;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.annotation.Keep;
import com.android.settings.network.SubscriptionUtil;
import java.util.List;
/* loaded from: classes.dex */
public class SubscriptionAnnotation {
    public static final ParcelUuid EMPTY_UUID = ParcelUuid.fromString("0-0-0-0-0");
    private boolean mIsActive;
    private boolean mIsAllowToDisplay;
    private boolean mIsExisted;
    private int mOrderWithinList;
    private SubscriptionInfo mSubInfo;
    private int mType;

    /* loaded from: classes.dex */
    public static class Builder {
        private int mIndexWithinList;
        private List<SubscriptionInfo> mSubInfoList;

        public Builder(List<SubscriptionInfo> list, int i) {
            this.mSubInfoList = list;
            this.mIndexWithinList = i;
        }

        public SubscriptionAnnotation build(Context context, List<Integer> list, List<Integer> list2, List<Integer> list3) {
            return new SubscriptionAnnotation(this.mSubInfoList, this.mIndexWithinList, context, list, list2, list3);
        }
    }

    @Keep
    protected SubscriptionAnnotation(List<SubscriptionInfo> list, int i, Context context, List<Integer> list2, List<Integer> list3, List<Integer> list4) {
        boolean z = false;
        this.mType = 0;
        if (i < 0 || i >= list.size()) {
            return;
        }
        SubscriptionInfo subscriptionInfo = list.get(i);
        this.mSubInfo = subscriptionInfo;
        if (subscriptionInfo == null) {
            return;
        }
        this.mOrderWithinList = i;
        int i2 = subscriptionInfo.isEmbedded() ? 2 : 1;
        this.mType = i2;
        this.mIsExisted = true;
        if (i2 == 2) {
            int cardId = this.mSubInfo.getCardId();
            this.mIsActive = list4.contains(Integer.valueOf(this.mSubInfo.getSimSlotIndex()));
            this.mIsAllowToDisplay = (cardId < 0 || isDisplayAllowed(context)) ? true : z;
            return;
        }
        if (this.mSubInfo.getSimSlotIndex() > -1 && list4.contains(Integer.valueOf(this.mSubInfo.getSimSlotIndex()))) {
            z = true;
        }
        this.mIsActive = z;
        this.mIsAllowToDisplay = isDisplayAllowed(context);
    }

    @Keep
    public int getOrderingInList() {
        return this.mOrderWithinList;
    }

    @Keep
    public int getType() {
        return this.mType;
    }

    @Keep
    public boolean isExisted() {
        return this.mIsExisted;
    }

    @Keep
    public boolean isActive() {
        return this.mIsActive;
    }

    @Keep
    public boolean isDisplayAllowed() {
        return this.mIsAllowToDisplay;
    }

    @Keep
    public int getSubscriptionId() {
        SubscriptionInfo subscriptionInfo = this.mSubInfo;
        if (subscriptionInfo == null) {
            return -1;
        }
        return subscriptionInfo.getSubscriptionId();
    }

    @Keep
    public ParcelUuid getGroupUuid() {
        SubscriptionInfo subscriptionInfo = this.mSubInfo;
        if (subscriptionInfo == null) {
            return null;
        }
        return subscriptionInfo.getGroupUuid();
    }

    @Keep
    public SubscriptionInfo getSubInfo() {
        return this.mSubInfo;
    }

    private boolean isDisplayAllowed(Context context) {
        return SubscriptionUtil.isSubscriptionVisible((SubscriptionManager) context.getSystemService(SubscriptionManager.class), context, this.mSubInfo);
    }

    public String toString() {
        return "SubscriptionAnnotation{subId=" + getSubscriptionId() + ",type=" + getType() + ",exist=" + isExisted() + ",active=" + isActive() + ",displayAllow=" + isDisplayAllowed() + "}";
    }

    public CharSequence getDisplayName() {
        SubscriptionInfo subscriptionInfo = this.mSubInfo;
        if (subscriptionInfo == null) {
            return null;
        }
        return subscriptionInfo.getDisplayName();
    }
}
