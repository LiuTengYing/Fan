package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.ActivityPicker;
import com.android.settings.AppWidgetLoader;
import java.util.List;
/* loaded from: classes.dex */
public class AppWidgetPickActivity extends ActivityPicker implements AppWidgetLoader.ItemConstructor<ActivityPicker.PickAdapter.Item> {
    private int mAppWidgetId;
    private AppWidgetLoader<ActivityPicker.PickAdapter.Item> mAppWidgetLoader;
    private AppWidgetManager mAppWidgetManager;
    List<ActivityPicker.PickAdapter.Item> mItems;
    private PackageManager mPackageManager;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.settings.ActivityPicker
    public void onCreate(Bundle bundle) {
        this.mPackageManager = getPackageManager();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        this.mAppWidgetManager = appWidgetManager;
        this.mAppWidgetLoader = new AppWidgetLoader<>(this, appWidgetManager, this);
        super.onCreate(bundle);
        setResultData(0, null);
        Intent intent = getIntent();
        if (intent.hasExtra("appWidgetId")) {
            this.mAppWidgetId = intent.getIntExtra("appWidgetId", 0);
        } else {
            finish();
        }
    }

    @Override // com.android.settings.ActivityPicker
    protected List<ActivityPicker.PickAdapter.Item> getItems() {
        List<ActivityPicker.PickAdapter.Item> items = this.mAppWidgetLoader.getItems(getIntent());
        this.mItems = items;
        return items;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.settings.AppWidgetLoader.ItemConstructor
    public ActivityPicker.PickAdapter.Item createItem(Context context, AppWidgetProviderInfo appWidgetProviderInfo, Bundle bundle) {
        String str = appWidgetProviderInfo.label;
        Drawable drawable = null;
        if (appWidgetProviderInfo.icon != 0) {
            try {
                int i = context.getResources().getDisplayMetrics().densityDpi;
                if (i == 160 || i == 213 || i == 240 || i != 320) {
                }
                drawable = this.mPackageManager.getResourcesForApplication(appWidgetProviderInfo.provider.getPackageName()).getDrawableForDensity(appWidgetProviderInfo.icon, (int) ((i * 0.75f) + 0.5f));
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AppWidgetPickActivity", "Can't load icon drawable 0x" + Integer.toHexString(appWidgetProviderInfo.icon) + " for provider: " + appWidgetProviderInfo.provider);
            }
            if (drawable == null) {
                Log.w("AppWidgetPickActivity", "Can't load icon drawable 0x" + Integer.toHexString(appWidgetProviderInfo.icon) + " for provider: " + appWidgetProviderInfo.provider);
            }
        }
        ActivityPicker.PickAdapter.Item item = new ActivityPicker.PickAdapter.Item(context, str, drawable);
        item.packageName = appWidgetProviderInfo.provider.getPackageName();
        item.className = appWidgetProviderInfo.provider.getClassName();
        item.extras = bundle;
        return item;
    }

    @Override // com.android.settings.ActivityPicker, android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intentForPosition = getIntentForPosition(i);
        int i2 = -1;
        if (this.mItems.get(i).extras != null) {
            setResultData(-1, intentForPosition);
        } else {
            try {
                this.mAppWidgetManager.bindAppWidgetId(this.mAppWidgetId, intentForPosition.getComponent(), intentForPosition.getExtras() != null ? intentForPosition.getExtras().getBundle("appWidgetOptions") : null);
            } catch (IllegalArgumentException unused) {
                i2 = 0;
            }
            setResultData(i2, null);
        }
        finish();
    }

    void setResultData(int i, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra("appWidgetId", this.mAppWidgetId);
        setResult(i, intent);
    }
}
