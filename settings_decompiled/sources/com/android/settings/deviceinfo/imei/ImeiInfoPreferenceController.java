package com.android.settings.deviceinfo.imei;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.deviceinfo.PhoneNumberSummaryPreference;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ImeiInfoPreferenceController extends BasePreferenceController {
    private static final String KEY_PREFERENCE_CATEGORY = "device_detail_category";
    private Fragment mFragment;
    private final boolean mIsMultiSim;
    private final List<Preference> mPreferenceList;
    private final TelephonyManager mTelephonyManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public ImeiInfoPreferenceController(Context context, String str) {
        super(context, str);
        this.mPreferenceList = new ArrayList();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        this.mIsMultiSim = telephonyManager.getPhoneCount() > 1;
    }

    public void setHost(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY);
        this.mPreferenceList.add(findPreference);
        updatePreference(findPreference, 0);
        int order = findPreference.getOrder();
        for (int i = 1; i < this.mTelephonyManager.getPhoneCount(); i++) {
            Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
            createNewPreference.setOrder(order + i);
            createNewPreference.setKey(getPreferenceKey() + i);
            createNewPreference.setVisible(isAvailable());
            createNewPreference.setCopyingEnabled(true);
            preferenceCategory.addPreference(createNewPreference);
            this.mPreferenceList.add(createNewPreference);
            updatePreference(createNewPreference, i);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        int size = this.mPreferenceList.size();
        for (int i = 0; i < size; i++) {
            updatePreference(this.mPreferenceList.get(i), i);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getSummary(0);
    }

    private CharSequence getSummary(int i) {
        if (getPhoneType(i) == 2) {
            return this.mTelephonyManager.getMeid(i);
        }
        return this.mTelephonyManager.getImei(i);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int indexOf = this.mPreferenceList.indexOf(preference);
        if (indexOf == -1) {
            return false;
        }
        ImeiQRDialogFragment.show(this.mFragment, indexOf, preference.getTitle().toString(), this.mTelephonyManager.getImei(indexOf));
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser() || Utils.isWifiOnly(this.mContext)) ? 3 : 0;
    }

    private void updatePreference(Preference preference, int i) {
        preference.setTitle(getTitle(i));
        preference.setSummary(getSummary(i));
    }

    private CharSequence getTitleForGsmPhone(int i) {
        return this.mIsMultiSim ? this.mContext.getString(R$string.imei_multi_sim, Integer.valueOf(i + 1)) : this.mContext.getString(R$string.status_imei);
    }

    private CharSequence getTitleForCdmaPhone(int i) {
        return this.mIsMultiSim ? this.mContext.getString(R$string.meid_multi_sim, Integer.valueOf(i + 1)) : this.mContext.getString(R$string.status_meid_number);
    }

    private CharSequence getTitle(int i) {
        if (getPhoneType(i) == 2) {
            return getTitleForCdmaPhone(i);
        }
        return getTitleForGsmPhone(i);
    }

    private int getPhoneType(int i) {
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
        return this.mTelephonyManager.getCurrentPhoneType(activeSubscriptionInfoForSimSlotIndex != null ? activeSubscriptionInfoForSimSlotIndex.getSubscriptionId() : Integer.MAX_VALUE);
    }

    Preference createNewPreference(Context context) {
        return new PhoneNumberSummaryPreference(context);
    }
}
