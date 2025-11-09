package com.android.settingslib.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.util.Log;
import java.util.List;
/* loaded from: classes2.dex */
public class ActivityTile extends Tile {
    public ActivityTile(ActivityInfo activityInfo, String str) {
        super(activityInfo, str, activityInfo.metaData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityTile(Parcel parcel) {
        super(parcel);
    }

    @Override // com.android.settingslib.drawer.Tile
    public String getDescription() {
        return getPackageName() + "/" + getComponentName();
    }

    @Override // com.android.settingslib.drawer.Tile
    protected ComponentInfo getComponentInfo(Context context) {
        if (this.mComponentInfo == null) {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            Intent intent = getIntent();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 128);
            if (queryIntentActivities != null && !queryIntentActivities.isEmpty()) {
                ActivityInfo activityInfo = queryIntentActivities.get(0).activityInfo;
                this.mComponentInfo = activityInfo;
                setMetaData(((ComponentInfo) activityInfo).metaData);
            } else {
                Log.e("ActivityTile", "Cannot find package info for " + intent.getComponent().flattenToString());
            }
        }
        return this.mComponentInfo;
    }

    @Override // com.android.settingslib.drawer.Tile
    protected CharSequence getComponentLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentInfo componentInfo = getComponentInfo(context);
        if (componentInfo == null) {
            return null;
        }
        return componentInfo.loadLabel(packageManager);
    }
}
