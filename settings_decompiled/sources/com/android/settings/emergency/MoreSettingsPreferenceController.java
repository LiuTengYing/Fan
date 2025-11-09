package com.android.settings.emergency;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.Utils;
import com.android.settingslib.widget.LayoutPreference;
import java.util.List;
/* loaded from: classes.dex */
public class MoreSettingsPreferenceController extends BasePreferenceController implements View.OnClickListener {
    private static final String EXTRA_KEY_ATTRIBUTION = "attribution";
    private static final String TAG = "MoreSettingsPrefCtrl";
    Intent mIntent;
    private LayoutPreference mPreference;

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

    public MoreSettingsPreferenceController(Context context, String str) {
        super(context, str);
        String string = this.mContext.getResources().getString(R$string.config_emergency_package_name);
        if (TextUtils.isEmpty(string)) {
            return;
        }
        this.mIntent = new Intent("android.intent.action.MAIN").setPackage(string);
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 1048576);
        if (queryIntentActivities != null && !queryIntentActivities.isEmpty()) {
            this.mIntent.setClassName(string, queryIntentActivities.get(0).activityInfo.name);
        } else {
            this.mIntent = null;
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = layoutPreference;
        Button button = (Button) layoutPreference.findViewById(R$id.button);
        Drawable icon = getIcon();
        button.setText(getButtonText());
        if (icon != null) {
            button.setCompoundDrawablesWithIntrinsicBounds(icon, (Drawable) null, (Drawable) null, (Drawable) null);
            button.setVisibility(0);
        }
        button.setOnClickListener(this);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mIntent == null ? 3 : 0;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().logClickedPreference(this.mPreference, getMetricsCategory());
        Intent flags = new Intent(this.mIntent).addCategory("android.intent.category.LAUNCHER").setFlags(335544320);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_KEY_ATTRIBUTION, this.mContext.getPackageName());
        this.mContext.startActivity(flags, bundle);
    }

    private Drawable getIcon() {
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getResources().getString(R$string.config_emergency_package_name), 33280);
            Context context = this.mContext;
            return getScaledDrawable(context, Utils.getBadgedIcon(context, applicationInfo), 24, 24);
        } catch (Exception e) {
            Log.d(TAG, "Failed to get open app button icon", e);
            return null;
        }
    }

    private CharSequence getButtonText() {
        String string = this.mContext.getResources().getString(R$string.config_emergency_package_name);
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            return this.mContext.getString(R$string.open_app_button, packageManager.getApplicationInfo(string, 33280).loadLabel(packageManager));
        } catch (Exception unused) {
            Log.d(TAG, "Failed to get open app button text, falling back.");
            return "";
        }
    }

    private static Drawable getScaledDrawable(Context context, Drawable drawable, int i, int i2) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new BitmapDrawable(context.getResources(), convertToBitmap(drawable, (int) TypedValue.applyDimension(1, i, displayMetrics), (int) TypedValue.applyDimension(1, i2, displayMetrics)));
    }

    private static Bitmap convertToBitmap(Drawable drawable, int i, int i2) {
        if (drawable == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, i, i2);
        drawable.draw(canvas);
        return createBitmap;
    }
}
