package com.android.settings.fuelgauge.batterytip;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsActivity;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.CardPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class BatteryTipPreferenceController extends BasePreferenceController {
    private static final String KEY_BATTERY_TIPS = "key_battery_tips";
    public static final String PREF_NAME = "battery_tip";
    private static final int REQUEST_ANOMALY_ACTION = 0;
    private static final String TAG = "BatteryTipPreferenceController";
    private BatteryTipListener mBatteryTipListener;
    private Map<String, BatteryTip> mBatteryTipMap;
    private List<BatteryTip> mBatteryTips;
    CardPreference mCardPreference;
    InstrumentedPreferenceFragment mFragment;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private boolean mNeedUpdate;
    Context mPrefContext;
    private SettingsActivity mSettingsActivity;

    /* loaded from: classes.dex */
    public interface BatteryTipListener {
        void onBatteryTipHandled(BatteryTip batteryTip);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

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
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BatteryTipPreferenceController(Context context, String str) {
        super(context, str);
        this.mBatteryTipMap = new HashMap();
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mNeedUpdate = true;
    }

    public void setActivity(SettingsActivity settingsActivity) {
        this.mSettingsActivity = settingsActivity;
    }

    public void setFragment(InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        this.mFragment = instrumentedPreferenceFragment;
    }

    public void setBatteryTipListener(BatteryTipListener batteryTipListener) {
        this.mBatteryTipListener = batteryTipListener;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPrefContext = preferenceScreen.getContext();
        CardPreference cardPreference = (CardPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mCardPreference = cardPreference;
        cardPreference.setVisible(false);
    }

    public void updateBatteryTips(List<BatteryTip> list) {
        if (list == null) {
            return;
        }
        if (this.mBatteryTips == null) {
            this.mBatteryTips = list;
        } else {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                this.mBatteryTips.get(i).updateState(list.get(i));
            }
        }
        this.mCardPreference.setVisible(false);
        int size2 = list.size();
        for (int i2 = 0; i2 < size2; i2++) {
            BatteryTip batteryTip = this.mBatteryTips.get(i2);
            batteryTip.validateCheck(this.mContext);
            if (batteryTip.getState() != 2) {
                this.mCardPreference.setVisible(true);
                batteryTip.updatePreference(this.mCardPreference);
                this.mBatteryTipMap.put(this.mCardPreference.getKey(), batteryTip);
                batteryTip.log(this.mContext, this.mMetricsFeatureProvider);
                this.mNeedUpdate = batteryTip.needUpdate();
                return;
            }
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        BatteryTip batteryTip = this.mBatteryTipMap.get(preference.getKey());
        if (batteryTip != null) {
            if (batteryTip.shouldShowDialog()) {
                BatteryTipDialogFragment newInstance = BatteryTipDialogFragment.newInstance(batteryTip, this.mFragment.getMetricsCategory());
                newInstance.setTargetFragment(this.mFragment, 0);
                newInstance.show(this.mFragment.getFragmentManager(), TAG);
                return true;
            }
            BatteryTipAction actionForBatteryTip = BatteryTipUtils.getActionForBatteryTip(batteryTip, this.mSettingsActivity, this.mFragment);
            if (actionForBatteryTip != null) {
                actionForBatteryTip.handlePositiveAction(this.mFragment.getMetricsCategory());
            }
            BatteryTipListener batteryTipListener = this.mBatteryTipListener;
            if (batteryTipListener != null) {
                batteryTipListener.onBatteryTipHandled(batteryTip);
                return true;
            }
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    public void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            updateBatteryTips(bundle.getParcelableArrayList(KEY_BATTERY_TIPS));
        }
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putParcelableList(KEY_BATTERY_TIPS, this.mBatteryTips);
    }

    public boolean needUpdate() {
        return this.mNeedUpdate;
    }

    public BatteryTip getCurrentBatteryTip() {
        List<BatteryTip> list = this.mBatteryTips;
        if (list != null && list.stream().anyMatch(new Predicate() { // from class: com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((BatteryTip) obj).isVisible();
            }
        })) {
            return this.mBatteryTips.stream().filter(new Predicate() { // from class: com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((BatteryTip) obj).isVisible();
                }
            }).findFirst().get();
        }
        return null;
    }
}
