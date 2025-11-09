package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.sprdpower.IPowerManagerEx;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.preference.PreferenceFrameLayout;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.internal.R;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.dashboard.profileselector.ProfileFragmentBridge;
import com.android.settingslib.widget.ActionBarShadowController;
import com.android.settingslib.widget.AdaptiveIcon;
import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes.dex */
public final class Utils extends com.android.settingslib.Utils {
    public static final boolean SUPPORT_ULTRA_POWER_SAVING;
    private static final StringBuilder sBuilder;
    private static final Formatter sFormatter;

    static {
        SUPPORT_ULTRA_POWER_SAVING = 1 == SystemProperties.getInt("ro.sys.pwctl.ultrasaving", 0);
        StringBuilder sb = new StringBuilder(50);
        sBuilder = sb;
        sFormatter = new Formatter(sb, Locale.getDefault());
    }

    public static boolean updatePreferenceToSpecificActivityOrRemove(Context context, PreferenceGroup preferenceGroup, String str, int i) {
        Preference findPreference = preferenceGroup.findPreference(str);
        if (findPreference == null) {
            return false;
        }
        Intent intent = findPreference.getIntent();
        if (intent != null) {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
            int size = queryIntentActivities.size();
            for (int i2 = 0; i2 < size; i2++) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i2);
                if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                    Intent intent2 = new Intent();
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    findPreference.setIntent(intent2.setClassName(activityInfo.packageName, activityInfo.name));
                    if ((i & 1) != 0) {
                        findPreference.setTitle(resolveInfo.loadLabel(packageManager));
                    }
                    return true;
                }
            }
        }
        preferenceGroup.removePreference(findPreference);
        return false;
    }

    public static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }

    public static boolean isVoiceCapable(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        return telephonyManager != null && telephonyManager.isVoiceCapable();
    }

    public static Locale createLocaleFromString(String str) {
        if (str == null) {
            return Locale.getDefault();
        }
        String[] split = str.split("_", 3);
        if (1 == split.length) {
            return new Locale(split[0]);
        }
        if (2 == split.length) {
            return new Locale(split[0], split[1]);
        }
        return new Locale(split[0], split[1], split[2]);
    }

    public static boolean isBatteryPresent(Intent intent) {
        return intent.getBooleanExtra("present", true);
    }

    public static boolean isBatteryPresent(Context context) {
        return isBatteryPresent(context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")));
    }

    public static String getBatteryPercentage(Intent intent) {
        return com.android.settingslib.Utils.formatPercentage(com.android.settingslib.Utils.getBatteryLevel(intent));
    }

    public static void prepareCustomPreferencesList(ViewGroup viewGroup, View view, View view2, boolean z) {
        if (view2.getScrollBarStyle() == 33554432) {
            int dimensionPixelSize = view2.getResources().getDimensionPixelSize(17105460);
            if (viewGroup instanceof PreferenceFrameLayout) {
                view.getLayoutParams().removeBorders = true;
            }
            view2.setPaddingRelative(0, 0, 0, dimensionPixelSize);
        }
    }

    public static void forceCustomPadding(View view, boolean z) {
        view.setPaddingRelative(z ? view.getPaddingStart() : 0, 0, z ? view.getPaddingEnd() : 0, view.getResources().getDimensionPixelSize(17105460));
    }

    public static String getMeProfileName(Context context, boolean z) {
        if (z) {
            return getProfileDisplayName(context);
        }
        return getShorterNameIfPossible(context);
    }

    private static String getShorterNameIfPossible(Context context) {
        String localProfileGivenName = getLocalProfileGivenName(context);
        return !TextUtils.isEmpty(localProfileGivenName) ? localProfileGivenName : getProfileDisplayName(context);
    }

    private static String getLocalProfileGivenName(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, new String[]{"_id"}, "account_type IS NULL AND account_name IS NULL", null, null);
        if (query == null) {
            return null;
        }
        try {
            if (query.moveToFirst()) {
                long j = query.getLong(0);
                query.close();
                query = contentResolver.query(ContactsContract.Profile.CONTENT_URI.buildUpon().appendPath("data").build(), new String[]{"data2", "data3"}, "raw_contact_id=" + j, null, null);
                if (query == null) {
                    return null;
                }
                try {
                    if (query.moveToFirst()) {
                        String string = query.getString(0);
                        if (TextUtils.isEmpty(string)) {
                            string = query.getString(1);
                        }
                        return string;
                    }
                    return null;
                } finally {
                }
            }
            return null;
        } finally {
        }
    }

    private static final String getProfileDisplayName(Context context) {
        Cursor query = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[]{"display_name"}, null, null, null);
        if (query == null) {
            return null;
        }
        try {
            if (query.moveToFirst()) {
                return query.getString(0);
            }
            return null;
        } finally {
            query.close();
        }
    }

    public static boolean hasMultipleUsers(Context context) {
        return ((UserManager) context.getSystemService(UserManager.class)).getUsers().size() > 1;
    }

    public static UserHandle getManagedProfile(UserManager userManager) {
        for (UserHandle userHandle : userManager.getUserProfiles()) {
            if (userHandle.getIdentifier() != userManager.getProcessUserId() && userManager.getUserInfo(userHandle.getIdentifier()).isManagedProfile()) {
                return userHandle;
            }
        }
        return null;
    }

    public static UserHandle getManagedProfileWithDisabled(UserManager userManager) {
        int myUserId = UserHandle.myUserId();
        List profiles = userManager.getProfiles(myUserId);
        int size = profiles.size();
        for (int i = 0; i < size; i++) {
            UserInfo userInfo = (UserInfo) profiles.get(i);
            if (userInfo.isManagedProfile() && userInfo.getUserHandle().getIdentifier() != myUserId) {
                return userInfo.getUserHandle();
            }
        }
        return null;
    }

    public static int getManagedProfileId(UserManager userManager, int i) {
        int[] profileIdsWithDisabled;
        for (int i2 : userManager.getProfileIdsWithDisabled(i)) {
            if (i2 != i) {
                return i2;
            }
        }
        return -10000;
    }

    public static int getCurrentUserId(UserManager userManager, boolean z) throws IllegalStateException {
        if (z) {
            UserHandle managedProfile = getManagedProfile(userManager);
            if (managedProfile == null) {
                throw new IllegalStateException("Work profile user ID is not available.");
            }
            return managedProfile.getIdentifier();
        }
        return UserHandle.myUserId();
    }

    public static UserHandle getSecureTargetUser(IBinder iBinder, UserManager userManager, Bundle bundle, Bundle bundle2) {
        boolean equals;
        UserHandle userHandle;
        UserHandle userHandle2 = new UserHandle(UserHandle.myUserId());
        IActivityManager service = ActivityManager.getService();
        try {
            equals = "com.android.settings".equals(service.getLaunchedFromPackage(iBinder));
            userHandle = new UserHandle(UserHandle.getUserId(service.getLaunchedFromUid(iBinder)));
        } catch (RemoteException e) {
            Log.v("Settings", "Could not talk to activity manager.", e);
        }
        if (userHandle.equals(userHandle2) || !isProfileOf(userManager, userHandle)) {
            UserHandle userHandleFromBundle = getUserHandleFromBundle(bundle2);
            if (userHandleFromBundle == null || userHandleFromBundle.equals(userHandle2) || !equals || !isProfileOf(userManager, userHandleFromBundle)) {
                UserHandle userHandleFromBundle2 = getUserHandleFromBundle(bundle);
                if (userHandleFromBundle2 != null && !userHandleFromBundle2.equals(userHandle2) && equals) {
                    if (isProfileOf(userManager, userHandleFromBundle2)) {
                        return userHandleFromBundle2;
                    }
                }
                return userHandle2;
            }
            return userHandleFromBundle;
        }
        return userHandle;
    }

    private static UserHandle getUserHandleFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        UserHandle userHandle = (UserHandle) bundle.getParcelable("android.intent.extra.USER");
        if (userHandle != null) {
            return userHandle;
        }
        int i = bundle.getInt("android.intent.extra.USER_ID", -1);
        if (i != -1) {
            return UserHandle.of(i);
        }
        return null;
    }

    private static boolean isProfileOf(UserManager userManager, UserHandle userHandle) {
        if (userManager == null || userHandle == null) {
            return false;
        }
        return UserHandle.myUserId() == userHandle.getIdentifier() || userManager.getUserProfiles().contains(userHandle);
    }

    public static UserInfo getExistingUser(UserManager userManager, UserHandle userHandle) {
        List<UserInfo> aliveUsers = userManager.getAliveUsers();
        int identifier = userHandle.getIdentifier();
        for (UserInfo userInfo : aliveUsers) {
            if (userInfo.id == identifier) {
                return userInfo;
            }
        }
        return null;
    }

    public static View inflateCategoryHeader(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        TypedArray obtainStyledAttributes = layoutInflater.getContext().obtainStyledAttributes(null, R.styleable.Preference, 16842892, 0);
        int resourceId = obtainStyledAttributes.getResourceId(3, 0);
        obtainStyledAttributes.recycle();
        return layoutInflater.inflate(resourceId, viewGroup, false);
    }

    public static ArraySet<String> getHandledDomains(PackageManager packageManager, String str) {
        List<IntentFilterVerificationInfo> intentFilterVerifications = packageManager.getIntentFilterVerifications(str);
        List<IntentFilter> allIntentFilters = packageManager.getAllIntentFilters(str);
        ArraySet<String> arraySet = new ArraySet<>();
        if (intentFilterVerifications != null && intentFilterVerifications.size() > 0) {
            for (IntentFilterVerificationInfo intentFilterVerificationInfo : intentFilterVerifications) {
                for (String str2 : intentFilterVerificationInfo.getDomains()) {
                    arraySet.add(str2);
                }
            }
        }
        if (allIntentFilters != null && allIntentFilters.size() > 0) {
            for (IntentFilter intentFilter : allIntentFilters) {
                if (intentFilter.hasCategory("android.intent.category.BROWSABLE") && (intentFilter.hasDataScheme("http") || intentFilter.hasDataScheme("https"))) {
                    arraySet.addAll(intentFilter.getHostsList());
                }
            }
        }
        return arraySet;
    }

    public static ApplicationInfo getAdminApplicationInfo(Context context, int i) {
        ComponentName profileOwnerAsUser = ((DevicePolicyManager) context.getSystemService("device_policy")).getProfileOwnerAsUser(i);
        if (profileOwnerAsUser == null) {
            return null;
        }
        String packageName = profileOwnerAsUser.getPackageName();
        try {
            return AppGlobals.getPackageManager().getApplicationInfo(packageName, 0L, i);
        } catch (RemoteException e) {
            Log.e("Settings", "Error while retrieving application info for package " + packageName + ", userId " + i, e);
            return null;
        }
    }

    public static boolean isBandwidthControlEnabled() {
        try {
            return INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management")).isBandwidthControlEnabled();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static SpannableString createAccessibleSequence(CharSequence charSequence, String str) {
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new TtsSpan.TextBuilder(str).build(), 0, charSequence.length(), 18);
        return spannableString;
    }

    public static int getUserIdFromBundle(Context context, Bundle bundle) {
        return getUserIdFromBundle(context, bundle, false);
    }

    public static int getUserIdFromBundle(Context context, Bundle bundle, boolean z) {
        if (bundle == null) {
            return getCredentialOwnerUserId(context);
        }
        boolean z2 = false;
        if (z && bundle.getBoolean("allow_any_user", false)) {
            z2 = true;
        }
        int i = bundle.getInt("android.intent.extra.USER_ID", UserHandle.myUserId());
        return i == -9999 ? z2 ? i : enforceSystemUser(context, i) : z2 ? i : enforceSameOwner(context, i);
    }

    public static int enforceSystemUser(Context context, int i) {
        if (UserHandle.myUserId() == 0) {
            return i;
        }
        throw new SecurityException("Given user id " + i + " must only be used from USER_SYSTEM, but current user is " + UserHandle.myUserId());
    }

    public static int enforceSameOwner(Context context, int i) {
        if (ArrayUtils.contains(((UserManager) context.getSystemService(UserManager.class)).getProfileIdsWithDisabled(UserHandle.myUserId()), i)) {
            return i;
        }
        throw new SecurityException("Given user id " + i + " does not belong to user " + UserHandle.myUserId());
    }

    public static int getCredentialOwnerUserId(Context context) {
        return getCredentialOwnerUserId(context, UserHandle.myUserId());
    }

    public static int getCredentialOwnerUserId(Context context, int i) {
        return ((UserManager) context.getSystemService(UserManager.class)).getCredentialOwnerProfile(i);
    }

    public static int getCredentialType(Context context, int i) {
        return new LockPatternUtils(context).getCredentialTypeForUser(i);
    }

    public static String formatDateRange(Context context, long j, long j2) {
        String formatter;
        StringBuilder sb = sBuilder;
        synchronized (sb) {
            sb.setLength(0);
            formatter = DateUtils.formatDateRange(context, sFormatter, j, j2, 65552, null).toString();
        }
        return formatter;
    }

    public static boolean startQuietModeDialogIfNecessary(Context context, UserManager userManager, int i) {
        if (userManager.isQuietModeEnabled(UserHandle.of(i))) {
            context.startActivity(UnlaunchableAppActivity.createInQuietModeDialogIntent(i));
            return true;
        }
        return false;
    }

    public static boolean unlockWorkProfileIfNecessary(Context context, int i) {
        try {
            if (ActivityManager.getService().isUserRunning(i, 2) && new LockPatternUtils(context).isSecure(i)) {
                return confirmWorkProfileCredentials(context, i);
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    private static boolean confirmWorkProfileCredentials(Context context, int i) {
        Intent createConfirmDeviceCredentialIntent = ((KeyguardManager) context.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, i);
        if (createConfirmDeviceCredentialIntent != null) {
            context.startActivity(createConfirmDeviceCredentialIntent);
            return true;
        }
        return false;
    }

    public static CharSequence getApplicationLabel(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 4194816).loadLabel(context.getPackageManager());
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("Settings", "Unable to find info for package: " + str);
            return null;
        }
    }

    public static Context createPackageContextAsUser(Context context, int i) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.of(i));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Settings", "Failed to create user context", e);
            return null;
        }
    }

    public static FingerprintManager getFingerprintManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            return (FingerprintManager) context.getSystemService("fingerprint");
        }
        return null;
    }

    public static boolean hasFingerprintHardware(Context context) {
        FingerprintManager fingerprintManagerOrNull = getFingerprintManagerOrNull(context);
        return fingerprintManagerOrNull != null && fingerprintManagerOrNull.isHardwareDetected();
    }

    public static FaceManager getFaceManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            return (FaceManager) context.getSystemService("face");
        }
        return null;
    }

    public static boolean hasFaceHardware(Context context) {
        FaceManager faceManagerOrNull = getFaceManagerOrNull(context);
        return faceManagerOrNull != null && faceManagerOrNull.isHardwareDetected();
    }

    public static boolean isMultipleBiometricsSupported(Context context) {
        return hasFingerprintHardware(context) && hasFaceHardware(context);
    }

    public static void launchIntent(Fragment fragment, Intent intent) {
        try {
            int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", -1);
            if (intExtra == -1) {
                fragment.startActivity(intent);
            } else {
                fragment.getActivity().startActivityAsUser(intent, new UserHandle(intExtra));
            }
        } catch (ActivityNotFoundException unused) {
            Log.w("Settings", "No activity found for " + intent);
        }
    }

    public static boolean isDemoUser(Context context) {
        return UserManager.isDeviceInDemoMode(context) && ((UserManager) context.getSystemService(UserManager.class)).isDemoUser();
    }

    public static ComponentName getDeviceOwnerComponent(Context context) {
        return ((DevicePolicyManager) context.getSystemService("device_policy")).getDeviceOwnerComponentOnAnyUser();
    }

    public static boolean isProfileOf(UserInfo userInfo, UserInfo userInfo2) {
        int i;
        return userInfo.id == userInfo2.id || ((i = userInfo.profileGroupId) != -10000 && i == userInfo2.profileGroupId);
    }

    public static VolumeInfo maybeInitializeVolume(StorageManager storageManager, Bundle bundle) {
        VolumeInfo findVolumeById = storageManager.findVolumeById(bundle.getString("android.os.storage.extra.VOLUME_ID", "private"));
        if (isVolumeValid(findVolumeById)) {
            return findVolumeById;
        }
        return null;
    }

    public static boolean isProfileOrDeviceOwner(UserManager userManager, DevicePolicyManager devicePolicyManager, String str) {
        List users = userManager.getUsers();
        if (devicePolicyManager.isDeviceOwnerAppOnAnyUser(str)) {
            return true;
        }
        int size = users.size();
        for (int i = 0; i < size; i++) {
            ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(((UserInfo) users.get(i)).id);
            if (profileOwnerAsUser != null && profileOwnerAsUser.getPackageName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProfileOrDeviceOwner(DevicePolicyManager devicePolicyManager, String str, int i) {
        if (devicePolicyManager.getDeviceOwnerUserId() == i && devicePolicyManager.isDeviceOwnerApp(str)) {
            return true;
        }
        ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(i);
        return profileOwnerAsUser != null && profileOwnerAsUser.getPackageName().equals(str);
    }

    private static boolean isVolumeValid(VolumeInfo volumeInfo) {
        return volumeInfo != null && volumeInfo.getType() == 1 && volumeInfo.isMountedReadable();
    }

    public static void setEditTextCursorPosition(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    public static Drawable getAdaptiveIcon(Context context, Drawable drawable, int i) {
        Drawable safeIcon = getSafeIcon(drawable);
        if (safeIcon instanceof AdaptiveIconDrawable) {
            return safeIcon;
        }
        AdaptiveIcon adaptiveIcon = new AdaptiveIcon(context, safeIcon);
        adaptiveIcon.setBackgroundColor(i);
        return adaptiveIcon;
    }

    public static Drawable getSafeIcon(Drawable drawable) {
        return (drawable == null || (drawable instanceof VectorDrawable)) ? drawable : getSafeDrawable(drawable, 600, 600);
    }

    private static Drawable getSafeDrawable(Drawable drawable, int i, int i2) {
        Bitmap createBitmap;
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        if (minimumWidth > i || minimumHeight > i2) {
            float f = minimumWidth;
            float f2 = minimumHeight;
            float min = Math.min(i / f, i2 / f2);
            int i3 = (int) (f * min);
            int i4 = (int) (f2 * min);
            if (drawable instanceof BitmapDrawable) {
                createBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), i3, i4, false);
            } else {
                createBitmap = createBitmap(drawable, i3, i4);
            }
            return new BitmapDrawable((Resources) null, createBitmap);
        }
        return drawable;
    }

    public static IconCompat createIconWithDrawable(Drawable drawable) {
        Bitmap createBitmap;
        if (drawable instanceof BitmapDrawable) {
            createBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth <= 0) {
                intrinsicWidth = 1;
            }
            if (intrinsicHeight <= 0) {
                intrinsicHeight = 1;
            }
            createBitmap = createBitmap(drawable, intrinsicWidth, intrinsicHeight);
        }
        return IconCompat.createWithBitmap(createBitmap);
    }

    public static Bitmap createBitmap(Drawable drawable, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    public static Drawable getBadgedIcon(IconDrawableFactory iconDrawableFactory, PackageManager packageManager, String str, int i) {
        try {
            return iconDrawableFactory.getBadgedIcon(packageManager.getApplicationInfoAsUser(str, 128, i), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return packageManager.getDefaultActivityIcon();
        }
    }

    public static boolean isPackageEnabled(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).enabled;
        } catch (Exception e) {
            Log.e("Settings", "Error while retrieving application info for package " + str, e);
            return false;
        }
    }

    public static boolean isSystemAlertWindowEnabled(Context context) {
        return !((ActivityManager) context.getSystemService("activity")).isLowRamDevice();
    }

    public static void setActionBarShadowAnimation(Activity activity, Lifecycle lifecycle, View view) {
        if (activity == null) {
            Log.w("Settings", "No activity, cannot style actionbar.");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.w("Settings", "No actionbar, cannot style actionbar.");
            return;
        }
        actionBar.setElevation(0.0f);
        if (lifecycle == null || view == null) {
            return;
        }
        ActionBarShadowController.attachToView(activity, lifecycle, view);
    }

    public static Fragment getTargetFragment(Activity activity, String str, Bundle bundle) {
        boolean z = false;
        boolean z2 = bundle != null && bundle.getInt("profile") == 1;
        if (bundle != null && bundle.getInt("profile") == 2) {
            z = true;
        }
        try {
            if (((UserManager) activity.getSystemService(UserManager.class)).getUserProfiles().size() > 1) {
                Map<String, String> map = ProfileFragmentBridge.FRAGMENT_MAP;
                if (map.get(str) != null && !z && !z2 && isShowProfile(activity.getBaseContext())) {
                    return Fragment.instantiate(activity, map.get(str), bundle);
                }
            }
            return Fragment.instantiate(activity, str, bundle);
        } catch (Exception e) {
            Log.e("Settings", "Unable to get target fragment", e);
            return null;
        }
    }

    private static boolean isShowProfile(Context context) {
        for (UserInfo userInfo : ((UserManager) context.getSystemService(UserManager.class)).getUsers()) {
            if (userInfo.isManagedProfile()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSettingsIntelligence(Context context) {
        return TextUtils.equals(context.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0], context.getString(R$string.config_settingsintelligence_package_name));
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean inUltraPowerSavingMode() {
        if (SUPPORT_ULTRA_POWER_SAVING) {
            try {
                return IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex")).isUltraPowerSaveMode();
            } catch (RemoteException unused) {
                return false;
            }
        }
        return false;
    }

    public static int getHomepageIconColor(Context context) {
        return com.android.settingslib.Utils.getColorAttrDefaultColor(context, 16842806);
    }

    public static int getHomepageIconColorHighlight(Context context) {
        return context.getColor(R$color.accent_select_primary_text);
    }

    public static boolean ledFileIsExists() {
        return fileIsExists("/sys/class/leds/red/brightness") || fileIsExists("/sys/class/leds/sc27xx:red/brightness");
    }

    public static boolean fileIsExists(String str) {
        try {
            File file = new File(str);
            Log.d("Settings", "brightness fileIsExists");
            if (!file.exists()) {
                Log.d("Settings", " brightness fileIsExists false");
                return false;
            }
            Log.d("Settings", "brightness fileIsExists true");
            return true;
        } catch (Exception e) {
            Log.d("Settings", " brightness fileIsExists Exception e = " + e);
            return false;
        }
    }

    public static boolean isCloneProfile(UserHandle userHandle, UserManager userManager) {
        return userManager.getUserInfo(userHandle.getIdentifier()).isCloneProfile();
    }
}
