package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settingslib.widget.BannerMessagePreference;
/* loaded from: classes.dex */
public class AdaptiveSleepPermissionPreferenceController {
    private final Context mContext;
    private final PackageManager mPackageManager;
    @VisibleForTesting
    BannerMessagePreference mPreference;

    public AdaptiveSleepPermissionPreferenceController(Context context) {
        this.mPackageManager = context.getPackageManager();
        this.mContext = context;
    }

    public void addToScreen(PreferenceScreen preferenceScreen) {
        initializePreference();
        if (AdaptiveSleepPreferenceController.hasSufficientPermission(this.mPackageManager)) {
            return;
        }
        preferenceScreen.addPreference(this.mPreference);
    }

    public void updateVisibility() {
        initializePreference();
        this.mPreference.setVisible(!AdaptiveSleepPreferenceController.hasSufficientPermission(this.mPackageManager));
    }

    private void initializePreference() {
        if (this.mPreference == null) {
            String attentionServicePackageName = this.mContext.getPackageManager().getAttentionServicePackageName();
            final Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + attentionServicePackageName));
            BannerMessagePreference bannerMessagePreference = new BannerMessagePreference(this.mContext);
            this.mPreference = bannerMessagePreference;
            bannerMessagePreference.setTitle(R$string.adaptive_sleep_title_no_permission);
            this.mPreference.setSummary(R$string.adaptive_sleep_summary_no_permission);
            this.mPreference.setPositiveButtonText(R$string.adaptive_sleep_manage_permission_button);
            this.mPreference.setPositiveButtonOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.AdaptiveSleepPermissionPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdaptiveSleepPermissionPreferenceController.this.lambda$initializePreference$0(intent, view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initializePreference$0(Intent intent, View view) {
        this.mContext.startActivity(intent);
    }
}
