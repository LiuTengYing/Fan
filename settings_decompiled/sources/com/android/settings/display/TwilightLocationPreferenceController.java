package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.BannerMessagePreference;
/* loaded from: classes.dex */
public class TwilightLocationPreferenceController extends BasePreferenceController {
    private final LocationManager mLocationManager;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

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

    public TwilightLocationPreferenceController(Context context, String str) {
        super(context, str);
        this.mLocationManager = (LocationManager) context.getSystemService(LocationManager.class);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        final BannerMessagePreference bannerMessagePreference = (BannerMessagePreference) preferenceScreen.findPreference(getPreferenceKey());
        bannerMessagePreference.setPositiveButtonText(R$string.twilight_mode_launch_location).setPositiveButtonOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.TwilightLocationPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwilightLocationPreferenceController.this.lambda$displayPreference$0(bannerMessagePreference, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(BannerMessagePreference bannerMessagePreference, View view) {
        this.mMetricsFeatureProvider.logClickedPreference(bannerMessagePreference, getMetricsCategory());
        launchLocationSettings();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mLocationManager.isLocationEnabled() ? 2 : 1;
    }

    private void launchLocationSettings() {
        Intent intent = new Intent();
        intent.setClass(this.mContext, Settings.LocationSettingsActivity.class);
        this.mContext.startActivity(intent);
    }
}
