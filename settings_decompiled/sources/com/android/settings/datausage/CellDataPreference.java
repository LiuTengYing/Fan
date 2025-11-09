package com.android.settings.datausage;

import android.content.Context;
import android.content.DialogInterface;
import android.net.NetworkTemplate;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.settings.R$string;
import com.android.settings.datausage.TemplatePreference;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.CustomDialogPreferenceCompat;
/* loaded from: classes.dex */
public class CellDataPreference extends CustomDialogPreferenceCompat implements TemplatePreference, MobileDataEnabledListener.Client {
    public boolean mChecked;
    private MobileDataEnabledListener mDataStateListener;
    public boolean mMultiSimDialog;
    final ProxySubscriptionManager.OnActiveSubscriptionChangedListener mOnSubscriptionsChangeListener;
    public int mSubId;

    public CellDataPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.switchPreferenceStyle, 16843629));
        this.mSubId = -1;
        this.mOnSubscriptionsChangeListener = new ProxySubscriptionManager.OnActiveSubscriptionChangedListener() { // from class: com.android.settings.datausage.CellDataPreference.1
            @Override // com.android.settings.network.ProxySubscriptionManager.OnActiveSubscriptionChangedListener
            public void onChanged() {
                CellDataPreference.this.updateEnabled();
            }
        };
        this.mDataStateListener = new MobileDataEnabledListener(context, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        CellDataState cellDataState = (CellDataState) parcelable;
        super.onRestoreInstanceState(cellDataState.getSuperState());
        this.mChecked = cellDataState.mChecked;
        this.mMultiSimDialog = cellDataState.mMultiSimDialog;
        if (this.mSubId == -1) {
            this.mSubId = cellDataState.mSubId;
            setKey(getKey() + this.mSubId);
        }
        notifyChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        CellDataState cellDataState = new CellDataState(super.onSaveInstanceState());
        cellDataState.mChecked = this.mChecked;
        cellDataState.mMultiSimDialog = this.mMultiSimDialog;
        cellDataState.mSubId = this.mSubId;
        return cellDataState;
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDataStateListener.start(this.mSubId);
        getProxySubscriptionManager().addActiveSubscriptionsListener(this.mOnSubscriptionsChangeListener);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        this.mDataStateListener.stop();
        getProxySubscriptionManager().removeActiveSubscriptionsListener(this.mOnSubscriptionsChangeListener);
        super.onDetached();
    }

    @Override // com.android.settings.datausage.TemplatePreference
    public void setTemplate(NetworkTemplate networkTemplate, int i, TemplatePreference.NetworkServices networkServices) {
        if (i == -1) {
            throw new IllegalArgumentException("CellDataPreference needs a SubscriptionInfo");
        }
        getProxySubscriptionManager().addActiveSubscriptionsListener(this.mOnSubscriptionsChangeListener);
        if (this.mSubId == -1) {
            this.mSubId = i;
            setKey(getKey() + i);
        }
        updateEnabled();
        updateChecked();
    }

    ProxySubscriptionManager getProxySubscriptionManager() {
        return ProxySubscriptionManager.getInstance(getContext());
    }

    SubscriptionInfo getActiveSubscriptionInfo(int i) {
        return getProxySubscriptionManager().getActiveSubscriptionInfo(i);
    }

    private void updateChecked() {
        setChecked(((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).getDataEnabled(this.mSubId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEnabled() {
        setEnabled(getActiveSubscriptionInfo(this.mSubId) != null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void performClick(View view) {
        Context context = getContext();
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, 178, !this.mChecked);
        SubscriptionInfo activeSubscriptionInfo = getActiveSubscriptionInfo(this.mSubId);
        SubscriptionInfo activeSubscriptionInfo2 = getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
        if (this.mChecked) {
            setMobileDataEnabled(false);
            if (activeSubscriptionInfo2 == null || activeSubscriptionInfo == null || activeSubscriptionInfo.getSubscriptionId() != activeSubscriptionInfo2.getSubscriptionId()) {
                return;
            }
            disableDataForOtherSubscriptions(this.mSubId);
            return;
        }
        setMobileDataEnabled(true);
    }

    private void setMobileDataEnabled(boolean z) {
        ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).setDataEnabled(this.mSubId, z);
        setChecked(z);
    }

    private void setChecked(boolean z) {
        if (this.mChecked == z) {
            return;
        }
        this.mChecked = z;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908352);
        findViewById.setClickable(false);
        ((Checkable) findViewById).setChecked(this.mChecked);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        if (this.mMultiSimDialog) {
            showMultiSimDialog(builder, onClickListener);
        } else {
            showDisableDialog(builder, onClickListener);
        }
    }

    private void showDisableDialog(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setTitle((CharSequence) null).setMessage(R$string.data_usage_disable_mobile).setPositiveButton(17039370, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
    }

    private void showMultiSimDialog(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        String charSequence;
        SubscriptionInfo activeSubscriptionInfo = getActiveSubscriptionInfo(this.mSubId);
        SubscriptionInfo activeSubscriptionInfo2 = getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
        if (activeSubscriptionInfo2 == null) {
            charSequence = getContext().getResources().getString(R$string.sim_selection_required_pref);
        } else {
            charSequence = activeSubscriptionInfo2.getDisplayName().toString();
        }
        builder.setTitle(R$string.sim_change_data_title);
        Context context = getContext();
        int i = R$string.sim_change_data_message;
        Object[] objArr = new Object[2];
        objArr[0] = String.valueOf(activeSubscriptionInfo != null ? activeSubscriptionInfo.getDisplayName() : null);
        objArr[1] = charSequence;
        builder.setMessage(context.getString(i, objArr));
        builder.setPositiveButton(R$string.okay, onClickListener);
        builder.setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null);
    }

    private void disableDataForOtherSubscriptions(int i) {
        if (getActiveSubscriptionInfo(i) != null) {
            ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).setDataEnabled(i, false);
        }
    }

    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        if (this.mMultiSimDialog) {
            getProxySubscriptionManager().get().setDefaultDataSubId(this.mSubId);
            setMobileDataEnabled(true);
            disableDataForOtherSubscriptions(this.mSubId);
            return;
        }
        setMobileDataEnabled(false);
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        updateChecked();
    }

    /* loaded from: classes.dex */
    public static class CellDataState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<CellDataState> CREATOR = new Parcelable.Creator<CellDataState>() { // from class: com.android.settings.datausage.CellDataPreference.CellDataState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public CellDataState createFromParcel(Parcel parcel) {
                return new CellDataState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public CellDataState[] newArray(int i) {
                return new CellDataState[i];
            }
        };
        public boolean mChecked;
        public boolean mMultiSimDialog;
        public int mSubId;

        public CellDataState(Parcelable parcelable) {
            super(parcelable);
        }

        public CellDataState(Parcel parcel) {
            super(parcel);
            this.mChecked = parcel.readByte() != 0;
            this.mMultiSimDialog = parcel.readByte() != 0;
            this.mSubId = parcel.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.mChecked ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mMultiSimDialog ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.mSubId);
        }
    }
}
