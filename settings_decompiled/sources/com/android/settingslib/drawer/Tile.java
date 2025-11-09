package com.android.settingslib.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
/* loaded from: classes2.dex */
public abstract class Tile implements Parcelable {
    public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator<Tile>() { // from class: com.android.settingslib.drawer.Tile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tile createFromParcel(Parcel parcel) {
            boolean readBoolean = parcel.readBoolean();
            parcel.setDataPosition(0);
            return readBoolean ? new ProviderTile(parcel) : new ActivityTile(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tile[] newArray(int i) {
            return new Tile[i];
        }
    };
    public static final Comparator<Tile> TILE_COMPARATOR = new Comparator() { // from class: com.android.settingslib.drawer.Tile$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = Tile.lambda$static$0((Tile) obj, (Tile) obj2);
            return lambda$static$0;
        }
    };
    private String mCategory;
    protected ComponentInfo mComponentInfo;
    private final String mComponentName;
    private final String mComponentPackage;
    private final Intent mIntent;
    long mLastUpdateTime;
    private Bundle mMetaData;
    private CharSequence mSummaryOverride;
    public ArrayList<UserHandle> userHandle = new ArrayList<>();
    private final String SHARED_PREFERENCES_KEY = "com.android.settings.prefs";

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected abstract ComponentInfo getComponentInfo(Context context);

    protected abstract CharSequence getComponentLabel(Context context);

    public abstract String getDescription();

    public Tile(ComponentInfo componentInfo, String str, Bundle bundle) {
        this.mComponentInfo = componentInfo;
        String str2 = componentInfo.packageName;
        this.mComponentPackage = str2;
        String str3 = componentInfo.name;
        this.mComponentName = str3;
        this.mCategory = str;
        this.mMetaData = bundle;
        Intent className = new Intent().setClassName(str2, str3);
        this.mIntent = className;
        if (isNewTask()) {
            className.addFlags(268435456);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Tile(Parcel parcel) {
        String readString = parcel.readString();
        this.mComponentPackage = readString;
        String readString2 = parcel.readString();
        this.mComponentName = readString2;
        this.mIntent = new Intent().setClassName(readString, readString2);
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.userHandle.add((UserHandle) UserHandle.CREATOR.createFromParcel(parcel));
        }
        this.mCategory = parcel.readString();
        this.mMetaData = parcel.readBundle();
        if (isNewTask()) {
            this.mIntent.addFlags(268435456);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this instanceof ProviderTile);
        parcel.writeString(this.mComponentPackage);
        parcel.writeString(this.mComponentName);
        int size = this.userHandle.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.userHandle.get(i2).writeToParcel(parcel, i);
        }
        parcel.writeString(this.mCategory);
        parcel.writeBundle(this.mMetaData);
    }

    public String getPackageName() {
        return this.mComponentPackage;
    }

    public String getComponentName() {
        return this.mComponentName;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public String getCategory() {
        return this.mCategory;
    }

    public void setCategory(String str) {
        this.mCategory = str;
    }

    public int getOrder() {
        if (hasOrder()) {
            return this.mMetaData.getInt("com.android.settings.order");
        }
        return 0;
    }

    public boolean hasOrder() {
        return this.mMetaData.containsKey("com.android.settings.order") && (this.mMetaData.get("com.android.settings.order") instanceof Integer);
    }

    public boolean hasSwitch() {
        Bundle bundle = this.mMetaData;
        return bundle != null && bundle.containsKey("com.android.settings.switch_uri");
    }

    public CharSequence getTitle(Context context) {
        ensureMetadataNotStale(context);
        String str = null;
        if (this.mMetaData.containsKey("com.android.settings.title")) {
            if (this.mMetaData.containsKey("com.android.settings.title_uri")) {
                return null;
            }
            if (this.mMetaData.get("com.android.settings.title") instanceof Integer) {
                str = getApplicationString(context, this.mComponentName, this.mMetaData, "com.android.settings.title");
            } else {
                str = this.mMetaData.getString("com.android.settings.title");
            }
        }
        return str == null ? getComponentLabel(context) : str;
    }

    private SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences("com.android.settings.prefs", 0);
    }

    private String getApplicationString(Context context, String str, Bundle bundle, String str2) {
        PackageManager packageManager = context.getPackageManager();
        try {
            SharedPreferences prefs = getPrefs(context);
            String str3 = str + str2 + (Locale.getDefault().getLanguage() + Locale.getDefault().getCountry());
            String string = prefs.getString(str3, null);
            if (string == null) {
                String string2 = packageManager.getResourcesForApplication(this.mComponentPackage).getString(bundle.getInt(str2));
                prefs.edit().putString(str3, string2).apply();
                return string2;
            }
            return string;
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            Log.w("Tile", "Couldn't find info", e);
            return null;
        }
    }

    public CharSequence getSummary(Context context) {
        CharSequence charSequence = this.mSummaryOverride;
        if (charSequence != null) {
            return charSequence;
        }
        ensureMetadataNotStale(context);
        Bundle bundle = this.mMetaData;
        if (bundle == null || bundle.containsKey("com.android.settings.summary_uri") || !this.mMetaData.containsKey("com.android.settings.summary")) {
            return null;
        }
        if (this.mMetaData.get("com.android.settings.summary") instanceof Integer) {
            return getApplicationString(context, this.mComponentName, this.mMetaData, "com.android.settings.summary");
        }
        return this.mMetaData.getString("com.android.settings.summary");
    }

    public void setMetaData(Bundle bundle) {
        this.mMetaData = bundle;
    }

    public Bundle getMetaData() {
        return this.mMetaData;
    }

    public String getKey(Context context) {
        if (hasKey()) {
            ensureMetadataNotStale(context);
            if (this.mMetaData.get("com.android.settings.keyhint") instanceof Integer) {
                return context.getResources().getString(this.mMetaData.getInt("com.android.settings.keyhint"));
            }
            return this.mMetaData.getString("com.android.settings.keyhint");
        }
        return null;
    }

    public boolean hasKey() {
        Bundle bundle = this.mMetaData;
        return bundle != null && bundle.containsKey("com.android.settings.keyhint");
    }

    public Icon getIcon(Context context) {
        Icon icon = null;
        if (context != null && this.mMetaData != null) {
            ensureMetadataNotStale(context);
            ComponentInfo componentInfo = getComponentInfo(context);
            if (componentInfo == null) {
                Log.w("Tile", "Cannot find ComponentInfo for " + getDescription());
                return null;
            }
            int i = this.mMetaData.getInt("com.android.settings.icon");
            if (i != 0 && i != 17170445) {
                icon = Icon.createWithResource(componentInfo.packageName, i);
                if (isIconTintable(context)) {
                    TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843817});
                    int color = obtainStyledAttributes.getColor(0, 0);
                    obtainStyledAttributes.recycle();
                    icon.setTint(color);
                }
            }
        }
        return icon;
    }

    public boolean isIconTintable(Context context) {
        ensureMetadataNotStale(context);
        Bundle bundle = this.mMetaData;
        if (bundle == null || !bundle.containsKey("com.android.settings.icon_tintable")) {
            return false;
        }
        return this.mMetaData.getBoolean("com.android.settings.icon_tintable");
    }

    public boolean isNewTask() {
        Bundle bundle = this.mMetaData;
        if (bundle == null || !bundle.containsKey("com.android.settings.new_task")) {
            return false;
        }
        return this.mMetaData.getBoolean("com.android.settings.new_task");
    }

    private void ensureMetadataNotStale(Context context) {
        try {
            long j = context.getApplicationContext().getPackageManager().getPackageInfo(this.mComponentPackage, 128).lastUpdateTime;
            if (j == this.mLastUpdateTime) {
                return;
            }
            this.mComponentInfo = null;
            getComponentInfo(context);
            this.mLastUpdateTime = j;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("Tile", "Can't find package, probably uninstalled.");
        }
    }

    public boolean isPrimaryProfileOnly() {
        return isPrimaryProfileOnly(this.mMetaData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPrimaryProfileOnly(Bundle bundle) {
        String string = bundle != null ? bundle.getString("com.android.settings.profile") : "all_profiles";
        return TextUtils.equals(string != null ? string : "all_profiles", "primary_profile_only");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(Tile tile, Tile tile2) {
        return tile2.getOrder() - tile.getOrder();
    }
}
