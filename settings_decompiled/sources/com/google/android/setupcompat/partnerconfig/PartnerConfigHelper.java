package com.google.android.setupcompat.partnerconfig;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.util.BuildCompatUtils;
import java.util.EnumMap;
/* loaded from: classes2.dex */
public class PartnerConfigHelper {
    public static final String IS_DYNAMIC_COLOR_ENABLED_METHOD = "isDynamicColorEnabled";
    public static final String IS_EXTENDED_PARTNER_CONFIG_ENABLED_METHOD = "isExtendedPartnerConfigEnabled";
    public static final String IS_MATERIAL_YOU_STYLE_ENABLED_METHOD = "IsMaterialYouStyleEnabled";
    public static final String IS_NEUTRAL_BUTTON_STYLE_ENABLED_METHOD = "isNeutralButtonStyleEnabled";
    public static final String IS_SUW_DAY_NIGHT_ENABLED_METHOD = "isSuwDayNightEnabled";
    public static final String KEY_FALLBACK_CONFIG = "fallbackConfig";
    public static final String MATERIAL_YOU_RESOURCE_SUFFIX = "_material_you";
    public static final String SUW_AUTHORITY = "com.google.android.setupwizard.partner";
    public static final String SUW_GET_PARTNER_CONFIG_METHOD = "getOverlayConfig";
    public static final String SUW_PACKAGE_NAME = "com.google.android.setupwizard";
    private static final String TAG = "PartnerConfigHelper";
    public static Bundle applyDynamicColorBundle = null;
    public static Bundle applyExtendedPartnerConfigBundle = null;
    public static Bundle applyMaterialYouConfigBundle = null;
    public static Bundle applyNeutralButtonStyleBundle = null;
    private static ContentObserver contentObserver = null;
    private static PartnerConfigHelper instance = null;
    private static int savedConfigUiMode = 0;
    public static int savedOrientation = 1;
    public static int savedScreenHeight;
    public static int savedScreenWidth;
    static Bundle suwDayNightEnabledBundle;
    Bundle resultBundle = null;
    final EnumMap<PartnerConfig, Object> partnerResourceCache = new EnumMap<>(PartnerConfig.class);

    public static synchronized PartnerConfigHelper get(Context context) {
        PartnerConfigHelper partnerConfigHelper;
        synchronized (PartnerConfigHelper.class) {
            if (!isValidInstance(context)) {
                instance = new PartnerConfigHelper(context);
            }
            partnerConfigHelper = instance;
        }
        return partnerConfigHelper;
    }

    private static boolean isValidInstance(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        if (instance == null) {
            savedConfigUiMode = configuration.uiMode & 48;
            savedOrientation = configuration.orientation;
            savedScreenWidth = configuration.screenWidthDp;
            savedScreenHeight = configuration.screenHeightDp;
            return false;
        }
        if (!(isSetupWizardDayNightEnabled(context) && (configuration.uiMode & 48) != savedConfigUiMode) && configuration.orientation == savedOrientation && configuration.screenWidthDp == savedScreenWidth && configuration.screenHeightDp == savedScreenHeight) {
            return true;
        }
        savedConfigUiMode = configuration.uiMode & 48;
        savedOrientation = configuration.orientation;
        savedScreenHeight = configuration.screenHeightDp;
        savedScreenWidth = configuration.screenWidthDp;
        resetInstance();
        return false;
    }

    private PartnerConfigHelper(Context context) {
        getPartnerConfigBundle(context);
        registerContentObserver(context);
    }

    public boolean isAvailable() {
        Bundle bundle = this.resultBundle;
        return (bundle == null || bundle.isEmpty()) ? false : true;
    }

    public boolean isPartnerConfigAvailable(PartnerConfig partnerConfig) {
        return isAvailable() && this.resultBundle.containsKey(partnerConfig.getResourceName());
    }

    public int getColor(Context context, PartnerConfig partnerConfig) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.COLOR) {
            throw new IllegalArgumentException("Not a color resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Integer) this.partnerResourceCache.get(partnerConfig)).intValue();
        }
        int i = 0;
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            Resources resources = resourceEntryFromKey.getResources();
            int resourceId = resourceEntryFromKey.getResourceId();
            TypedValue typedValue = new TypedValue();
            resources.getValue(resourceId, typedValue, true);
            if (typedValue.type == 1 && typedValue.data == 0) {
                return 0;
            }
            i = resources.getColor(resourceId, null);
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) Integer.valueOf(i));
            return i;
        } catch (NullPointerException unused) {
            return i;
        }
    }

    public Drawable getDrawable(Context context, PartnerConfig partnerConfig) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.DRAWABLE) {
            throw new IllegalArgumentException("Not a drawable resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return (Drawable) this.partnerResourceCache.get(partnerConfig);
        }
        Drawable drawable = null;
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            Resources resources = resourceEntryFromKey.getResources();
            int resourceId = resourceEntryFromKey.getResourceId();
            TypedValue typedValue = new TypedValue();
            resources.getValue(resourceId, typedValue, true);
            if (typedValue.type == 1 && typedValue.data == 0) {
                return null;
            }
            drawable = resources.getDrawable(resourceId, null);
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) drawable);
            return drawable;
        } catch (Resources.NotFoundException | NullPointerException unused) {
            return drawable;
        }
    }

    public String getString(Context context, PartnerConfig partnerConfig) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.STRING) {
            throw new IllegalArgumentException("Not a string resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return (String) this.partnerResourceCache.get(partnerConfig);
        }
        String str = null;
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            str = resourceEntryFromKey.getResources().getString(resourceEntryFromKey.getResourceId());
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) str);
            return str;
        } catch (NullPointerException unused) {
            return str;
        }
    }

    public boolean getBoolean(Context context, PartnerConfig partnerConfig, boolean z) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.BOOL) {
            throw new IllegalArgumentException("Not a bool resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Boolean) this.partnerResourceCache.get(partnerConfig)).booleanValue();
        }
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            z = resourceEntryFromKey.getResources().getBoolean(resourceEntryFromKey.getResourceId());
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) Boolean.valueOf(z));
            return z;
        } catch (Resources.NotFoundException | NullPointerException unused) {
            return z;
        }
    }

    public float getDimension(Context context, PartnerConfig partnerConfig) {
        return getDimension(context, partnerConfig, 0.0f);
    }

    public float getDimension(Context context, PartnerConfig partnerConfig, float f) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.DIMENSION) {
            throw new IllegalArgumentException("Not a dimension resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return getDimensionFromTypedValue(context, (TypedValue) this.partnerResourceCache.get(partnerConfig));
        }
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            Resources resources = resourceEntryFromKey.getResources();
            int resourceId = resourceEntryFromKey.getResourceId();
            f = resources.getDimension(resourceId);
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) getTypedValueFromResource(resources, resourceId, 5));
            return getDimensionFromTypedValue(context, (TypedValue) this.partnerResourceCache.get(partnerConfig));
        } catch (Resources.NotFoundException | NullPointerException unused) {
            return f;
        }
    }

    public float getFraction(Context context, PartnerConfig partnerConfig) {
        return getFraction(context, partnerConfig, 0.0f);
    }

    public float getFraction(Context context, PartnerConfig partnerConfig, float f) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.FRACTION) {
            throw new IllegalArgumentException("Not a fraction resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Float) this.partnerResourceCache.get(partnerConfig)).floatValue();
        }
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            f = resourceEntryFromKey.getResources().getFraction(resourceEntryFromKey.getResourceId(), 1, 1);
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) Float.valueOf(f));
            return f;
        } catch (Resources.NotFoundException | NullPointerException unused) {
            return f;
        }
    }

    public int getInteger(Context context, PartnerConfig partnerConfig, int i) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.INTEGER) {
            throw new IllegalArgumentException("Not a integer resource");
        }
        if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Integer) this.partnerResourceCache.get(partnerConfig)).intValue();
        }
        try {
            ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
            i = resourceEntryFromKey.getResources().getInteger(resourceEntryFromKey.getResourceId());
            this.partnerResourceCache.put((EnumMap<PartnerConfig, Object>) partnerConfig, (PartnerConfig) Integer.valueOf(i));
            return i;
        } catch (Resources.NotFoundException | NullPointerException unused) {
            return i;
        }
    }

    private void getPartnerConfigBundle(Context context) {
        Bundle bundle = this.resultBundle;
        if (bundle == null || bundle.isEmpty()) {
            try {
                this.resultBundle = context.getContentResolver().call(getContentUri(), SUW_GET_PARTNER_CONFIG_METHOD, (String) null, (Bundle) null);
                this.partnerResourceCache.clear();
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("PartnerConfigsBundle=");
                Bundle bundle2 = this.resultBundle;
                sb.append(bundle2 != null ? Integer.valueOf(bundle2.size()) : "(null)");
                Log.i(str, sb.toString());
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "Fail to get config from suw provider");
            }
        }
    }

    ResourceEntry getResourceEntryFromKey(Context context, String str) {
        Bundle bundle = this.resultBundle.getBundle(str);
        Bundle bundle2 = this.resultBundle.getBundle(KEY_FALLBACK_CONFIG);
        if (bundle2 != null) {
            bundle.putBundle(KEY_FALLBACK_CONFIG, bundle2.getBundle(str));
        }
        return adjustResourceEntryDayNightMode(context, adjustResourceEntryDefaultValue(context, ResourceEntry.fromBundle(context, bundle)));
    }

    private static ResourceEntry adjustResourceEntryDayNightMode(Context context, ResourceEntry resourceEntry) {
        Resources resources = resourceEntry.getResources();
        Configuration configuration = resources.getConfiguration();
        if (!isSetupWizardDayNightEnabled(context) && Util.isNightMode(configuration)) {
            configuration.uiMode = (configuration.uiMode & (-49)) | 16;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resourceEntry;
    }

    ResourceEntry adjustResourceEntryDefaultValue(Context context, ResourceEntry resourceEntry) {
        if (BuildCompatUtils.isAtLeastT() && shouldApplyMaterialYouStyle(context)) {
            try {
                if (SUW_PACKAGE_NAME.equals(resourceEntry.getPackageName())) {
                    String resourceTypeName = resourceEntry.getResources().getResourceTypeName(resourceEntry.getResourceId());
                    String concat = resourceEntry.getResourceName().concat(MATERIAL_YOU_RESOURCE_SUFFIX);
                    int identifier = resourceEntry.getResources().getIdentifier(concat, resourceTypeName, resourceEntry.getPackageName());
                    if (identifier != 0) {
                        String str = TAG;
                        Log.i(str, "use material you resource:" + concat);
                        return new ResourceEntry(resourceEntry.getPackageName(), concat, identifier, resourceEntry.getResources());
                    }
                }
            } catch (Resources.NotFoundException unused) {
            }
        }
        return resourceEntry;
    }

    public static synchronized void resetInstance() {
        synchronized (PartnerConfigHelper.class) {
            instance = null;
            suwDayNightEnabledBundle = null;
            applyExtendedPartnerConfigBundle = null;
            applyMaterialYouConfigBundle = null;
            applyDynamicColorBundle = null;
            applyNeutralButtonStyleBundle = null;
        }
    }

    public static boolean isSetupWizardDayNightEnabled(Context context) {
        if (suwDayNightEnabledBundle == null) {
            try {
                suwDayNightEnabledBundle = context.getContentResolver().call(getContentUri(), IS_SUW_DAY_NIGHT_ENABLED_METHOD, (String) null, (Bundle) null);
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "SetupWizard DayNight supporting status unknown; return as false.");
                suwDayNightEnabledBundle = null;
                return false;
            }
        }
        Bundle bundle = suwDayNightEnabledBundle;
        return bundle != null && bundle.getBoolean(IS_SUW_DAY_NIGHT_ENABLED_METHOD, false);
    }

    public static boolean shouldApplyExtendedPartnerConfig(Context context) {
        if (applyExtendedPartnerConfigBundle == null) {
            try {
                applyExtendedPartnerConfigBundle = context.getContentResolver().call(getContentUri(), IS_EXTENDED_PARTNER_CONFIG_ENABLED_METHOD, (String) null, (Bundle) null);
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "SetupWizard extended partner configs supporting status unknown; return as false.");
                applyExtendedPartnerConfigBundle = null;
                return false;
            }
        }
        Bundle bundle = applyExtendedPartnerConfigBundle;
        return bundle != null && bundle.getBoolean(IS_EXTENDED_PARTNER_CONFIG_ENABLED_METHOD, false);
    }

    public static boolean shouldApplyMaterialYouStyle(Context context) {
        Bundle bundle = applyMaterialYouConfigBundle;
        if (bundle == null || bundle.isEmpty()) {
            try {
                Bundle call = context.getContentResolver().call(getContentUri(), IS_MATERIAL_YOU_STYLE_ENABLED_METHOD, (String) null, (Bundle) null);
                applyMaterialYouConfigBundle = call;
                if (call != null && call.isEmpty() && !BuildCompatUtils.isAtLeastT()) {
                    return shouldApplyExtendedPartnerConfig(context);
                }
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "SetupWizard Material You configs supporting status unknown; return as false.");
                applyMaterialYouConfigBundle = null;
                return false;
            }
        }
        Bundle bundle2 = applyMaterialYouConfigBundle;
        return bundle2 != null && bundle2.getBoolean(IS_MATERIAL_YOU_STYLE_ENABLED_METHOD, false);
    }

    public static boolean isSetupWizardDynamicColorEnabled(Context context) {
        if (applyDynamicColorBundle == null) {
            try {
                applyDynamicColorBundle = context.getContentResolver().call(getContentUri(), IS_DYNAMIC_COLOR_ENABLED_METHOD, (String) null, (Bundle) null);
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "SetupWizard dynamic color supporting status unknown; return as false.");
                applyDynamicColorBundle = null;
                return false;
            }
        }
        Bundle bundle = applyDynamicColorBundle;
        return bundle != null && bundle.getBoolean(IS_DYNAMIC_COLOR_ENABLED_METHOD, false);
    }

    public static boolean isNeutralButtonStyleEnabled(Context context) {
        if (applyNeutralButtonStyleBundle == null) {
            try {
                applyNeutralButtonStyleBundle = context.getContentResolver().call(getContentUri(), IS_NEUTRAL_BUTTON_STYLE_ENABLED_METHOD, (String) null, (Bundle) null);
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "Neutral button style supporting status unknown; return as false.");
                applyNeutralButtonStyleBundle = null;
                return false;
            }
        }
        Bundle bundle = applyNeutralButtonStyleBundle;
        return bundle != null && bundle.getBoolean(IS_NEUTRAL_BUTTON_STYLE_ENABLED_METHOD, false);
    }

    static Uri getContentUri() {
        return new Uri.Builder().scheme("content").authority(SUW_AUTHORITY).build();
    }

    private static TypedValue getTypedValueFromResource(Resources resources, int i, int i2) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i, typedValue, true);
        if (typedValue.type == i2) {
            return typedValue;
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
    }

    private static float getDimensionFromTypedValue(Context context, TypedValue typedValue) {
        return typedValue.getDimension(context.getResources().getDisplayMetrics());
    }

    private static void registerContentObserver(Context context) {
        if (isSetupWizardDayNightEnabled(context)) {
            if (contentObserver != null) {
                unregisterContentObserver(context);
            }
            Uri contentUri = getContentUri();
            try {
                contentObserver = new ContentObserver(null) { // from class: com.google.android.setupcompat.partnerconfig.PartnerConfigHelper.1
                    @Override // android.database.ContentObserver
                    public void onChange(boolean z) {
                        super.onChange(z);
                        PartnerConfigHelper.resetInstance();
                    }
                };
                context.getContentResolver().registerContentObserver(contentUri, true, contentObserver);
            } catch (IllegalArgumentException | NullPointerException | SecurityException e) {
                String str = TAG;
                Log.w(str, "Failed to register content observer for " + contentUri + ": " + e);
            }
        }
    }

    private static void unregisterContentObserver(Context context) {
        try {
            context.getContentResolver().unregisterContentObserver(contentObserver);
            contentObserver = null;
        } catch (IllegalArgumentException | NullPointerException | SecurityException e) {
            String str = TAG;
            Log.w(str, "Failed to unregister content observer: " + e);
        }
    }
}
