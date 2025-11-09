package com.android.settings.network.telephony;

import android.os.SystemClock;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.network.telephony.TelephonyStatusControlSession;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AbstractMobileNetworkSettings extends RestrictedDashboardFragment {
    private List<AbstractPreferenceController> mHiddenControllerList;
    private boolean mIsRedrawRequired;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractMobileNetworkSettings(String str) {
        super(str);
        this.mHiddenControllerList = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<AbstractPreferenceController> getPreferenceControllersAsList() {
        final ArrayList arrayList = new ArrayList();
        getPreferenceControllers().forEach(new Consumer() { // from class: com.android.settings.network.telephony.AbstractMobileNetworkSettings$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                arrayList.addAll((List) obj);
            }
        });
        return arrayList;
    }

    Preference searchForPreference(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        String preferenceKey = abstractPreferenceController.getPreferenceKey();
        if (TextUtils.isEmpty(preferenceKey)) {
            return null;
        }
        return preferenceScreen.findPreference(preferenceKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TelephonyStatusControlSession setTelephonyAvailabilityStatus(Collection<AbstractPreferenceController> collection) {
        return new TelephonyStatusControlSession.Builder(collection).build();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceGroup.OnExpandButtonClickListener
    public void onExpandButtonClick() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mHiddenControllerList.stream().filter(new Predicate() { // from class: com.android.settings.network.telephony.AbstractMobileNetworkSettings$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isAvailable;
                isAvailable = ((AbstractPreferenceController) obj).isAvailable();
                return isAvailable;
            }
        }).forEach(new Consumer() { // from class: com.android.settings.network.telephony.AbstractMobileNetworkSettings$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AbstractMobileNetworkSettings.lambda$onExpandButtonClick$2(PreferenceScreen.this, (AbstractPreferenceController) obj);
            }
        });
        super.onExpandButtonClick();
        this.mMetricsFeatureProvider.action(getMetricsCategory(), getMetricsCategory(), 0, "onExpandButtonClick", (int) (SystemClock.elapsedRealtime() - elapsedRealtime));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onExpandButtonClick$2(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        abstractPreferenceController.updateState(preferenceScreen.findPreference(abstractPreferenceController.getPreferenceKey()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public void updatePreferenceStates() {
        this.mHiddenControllerList.clear();
        if (this.mIsRedrawRequired) {
            redrawPreferenceControllers();
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        getPreferenceControllersAsList().forEach(new Consumer() { // from class: com.android.settings.network.telephony.AbstractMobileNetworkSettings$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AbstractMobileNetworkSettings.this.lambda$updatePreferenceStates$3(preferenceScreen, (AbstractPreferenceController) obj);
            }
        });
        this.mMetricsFeatureProvider.action(getMetricsCategory(), getMetricsCategory(), 0, "updatePreferenceStates", (int) (SystemClock.elapsedRealtime() - elapsedRealtime));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateVisiblePreferenceControllers */
    public void lambda$updatePreferenceStates$3(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        Preference searchForPreference = searchForPreference(preferenceScreen, abstractPreferenceController);
        if (searchForPreference == null) {
            return;
        }
        if (!isPreferenceExpanded(searchForPreference)) {
            this.mHiddenControllerList.add(abstractPreferenceController);
        } else if (abstractPreferenceController.isAvailable()) {
            abstractPreferenceController.updateState(searchForPreference);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void redrawPreferenceControllers() {
        this.mHiddenControllerList.clear();
        if (!isResumed()) {
            this.mIsRedrawRequired = true;
            return;
        }
        this.mIsRedrawRequired = false;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        List<AbstractPreferenceController> preferenceControllersAsList = getPreferenceControllersAsList();
        TelephonyStatusControlSession telephonyAvailabilityStatus = setTelephonyAvailabilityStatus(preferenceControllersAsList);
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceControllersAsList.forEach(new Consumer() { // from class: com.android.settings.network.telephony.AbstractMobileNetworkSettings$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AbstractMobileNetworkSettings.this.lambda$redrawPreferenceControllers$4(preferenceScreen, (AbstractPreferenceController) obj);
            }
        });
        this.mMetricsFeatureProvider.action(getMetricsCategory(), getMetricsCategory(), 0, "redrawPreferenceControllers", (int) (SystemClock.elapsedRealtime() - elapsedRealtime));
        telephonyAvailabilityStatus.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$redrawPreferenceControllers$4(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        abstractPreferenceController.displayPreference(preferenceScreen);
        lambda$updatePreferenceStates$3(preferenceScreen, abstractPreferenceController);
    }
}
