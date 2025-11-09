package com.android.settings.slices;

import android.app.PendingIntent;
import android.app.slice.SliceManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.KeyValueListParser;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.Utils;
import com.android.settings.bluetooth.BluetoothSliceBuilder;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.VolumeSeekBarPreferenceController;
import com.android.settings.notification.zen.ZenModeSliceBuilder;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.SliceBroadcastRelay;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SettingsSliceProvider extends SliceProvider {
    private boolean mFirstSliceBound;
    private boolean mFirstSlicePinned;
    private Boolean mNightMode;
    final Map<Uri, SliceBackgroundWorker> mPinnedWorkers;
    Map<Uri, SliceData> mSliceWeakDataCache;
    SlicesDatabaseAccessor mSlicesDatabaseAccessor;
    private static final List<Uri> PUBLICLY_SUPPORTED_CUSTOM_SLICE_URIS = Arrays.asList(CustomSliceRegistry.BLUETOOTH_URI, CustomSliceRegistry.FLASHLIGHT_SLICE_URI, CustomSliceRegistry.LOCATION_SLICE_URI, CustomSliceRegistry.MOBILE_DATA_SLICE_URI, CustomSliceRegistry.WIFI_CALLING_URI, CustomSliceRegistry.WIFI_SLICE_URI, CustomSliceRegistry.ZEN_MODE_SLICE_URI);
    private static final KeyValueListParser KEY_VALUE_LIST_PARSER = new KeyValueListParser(',');

    public SettingsSliceProvider() {
        super("android.permission.READ_SEARCH_INDEXABLES");
        this.mPinnedWorkers = new ArrayMap();
        Log.d("SettingsSliceProvider", "init");
    }

    @Override // androidx.slice.SliceProvider
    public boolean onCreateSliceProvider() {
        Log.d("SettingsSliceProvider", "onCreateSliceProvider");
        this.mSlicesDatabaseAccessor = new SlicesDatabaseAccessor(getContext());
        this.mSliceWeakDataCache = new WeakHashMap();
        return true;
    }

    @Override // androidx.slice.SliceProvider
    public void onSlicePinned(final Uri uri) {
        if (!this.mFirstSlicePinned) {
            Log.d("SettingsSliceProvider", "onSlicePinned: " + uri);
            this.mFirstSlicePinned = true;
        }
        if (CustomSliceRegistry.isValidUri(uri)) {
            Context context = getContext();
            final CustomSliceable sliceableFromUri = FeatureFactory.getFactory(context).getSlicesFeatureProvider().getSliceableFromUri(context, uri);
            IntentFilter intentFilter = sliceableFromUri.getIntentFilter();
            if (intentFilter != null) {
                registerIntentToUri(intentFilter, uri);
            }
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.slices.SettingsSliceProvider$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SettingsSliceProvider.this.lambda$onSlicePinned$0(sliceableFromUri, uri);
                }
            });
        } else if (CustomSliceRegistry.ZEN_MODE_SLICE_URI.equals(uri)) {
            registerIntentToUri(ZenModeSliceBuilder.INTENT_FILTER, uri);
        } else if (CustomSliceRegistry.BLUETOOTH_URI.equals(uri)) {
            registerIntentToUri(BluetoothSliceBuilder.INTENT_FILTER, uri);
        } else {
            loadSliceInBackground(uri);
        }
    }

    @Override // androidx.slice.SliceProvider
    public void onSliceUnpinned(final Uri uri) {
        Context context = getContext();
        if (!VolumeSliceHelper.unregisterUri(context, uri)) {
            SliceBroadcastRelay.unregisterReceivers(context, uri);
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.slices.SettingsSliceProvider$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SettingsSliceProvider.this.lambda$onSliceUnpinned$1(uri);
            }
        });
    }

    @Override // androidx.slice.SliceProvider
    public Slice onBindSlice(Uri uri) {
        if (!this.mFirstSliceBound) {
            Log.d("SettingsSliceProvider", "onBindSlice start: " + uri);
        }
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            if (!ThreadUtils.isMainThread()) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            }
            if (getBlockedKeys().contains(uri.getLastPathSegment())) {
                Log.e("SettingsSliceProvider", "Requested blocked slice with Uri: " + uri);
                return null;
            }
            boolean isNightMode = Utils.isNightMode(getContext());
            Boolean bool = this.mNightMode;
            if (bool == null) {
                this.mNightMode = Boolean.valueOf(isNightMode);
                getContext().setTheme(R$style.Theme_SettingsBase);
            } else if (bool.booleanValue() != isNightMode) {
                Log.d("SettingsSliceProvider", "Night mode changed, reload theme");
                this.mNightMode = Boolean.valueOf(isNightMode);
                getContext().getTheme().rebase();
            }
            if (CustomSliceRegistry.isValidUri(uri)) {
                Context context = getContext();
                Slice slice = FeatureFactory.getFactory(context).getSlicesFeatureProvider().getSliceableFromUri(context, uri).getSlice();
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return slice;
            } else if (CustomSliceRegistry.WIFI_CALLING_URI.equals(uri)) {
                Slice createWifiCallingSlice = FeatureFactory.getFactory(getContext()).getSlicesFeatureProvider().getNewWifiCallingSliceHelper(getContext()).createWifiCallingSlice(uri);
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return createWifiCallingSlice;
            } else if (CustomSliceRegistry.ZEN_MODE_SLICE_URI.equals(uri)) {
                Slice slice2 = ZenModeSliceBuilder.getSlice(getContext());
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return slice2;
            } else if (CustomSliceRegistry.BLUETOOTH_URI.equals(uri)) {
                Slice slice3 = BluetoothSliceBuilder.getSlice(getContext());
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return slice3;
            } else if (CustomSliceRegistry.ENHANCED_4G_SLICE_URI.equals(uri)) {
                Slice createEnhanced4gLteSlice = FeatureFactory.getFactory(getContext()).getSlicesFeatureProvider().getNewEnhanced4gLteSliceHelper(getContext()).createEnhanced4gLteSlice(uri);
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return createEnhanced4gLteSlice;
            } else if (CustomSliceRegistry.WIFI_CALLING_PREFERENCE_URI.equals(uri)) {
                Slice createWifiCallingPreferenceSlice = FeatureFactory.getFactory(getContext()).getSlicesFeatureProvider().getNewWifiCallingSliceHelper(getContext()).createWifiCallingPreferenceSlice(uri);
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return createWifiCallingPreferenceSlice;
            } else {
                SliceData sliceData = this.mSliceWeakDataCache.get(uri);
                if (sliceData == null) {
                    loadSliceInBackground(uri);
                    Slice sliceStub = getSliceStub(uri);
                    StrictMode.setThreadPolicy(threadPolicy);
                    if (!this.mFirstSliceBound) {
                        Log.v("SettingsSliceProvider", "onBindSlice end");
                        this.mFirstSliceBound = true;
                    }
                    return sliceStub;
                }
                if (!getPinnedSlices().contains(uri)) {
                    this.mSliceWeakDataCache.remove(uri);
                }
                Slice buildSlice = SliceBuilderUtils.buildSlice(getContext(), sliceData);
                StrictMode.setThreadPolicy(threadPolicy);
                if (!this.mFirstSliceBound) {
                    Log.v("SettingsSliceProvider", "onBindSlice end");
                    this.mFirstSliceBound = true;
                }
                return buildSlice;
            }
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
            if (!this.mFirstSliceBound) {
                Log.v("SettingsSliceProvider", "onBindSlice end");
                this.mFirstSliceBound = true;
            }
        }
    }

    @Override // androidx.slice.SliceProvider
    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        ArrayList arrayList = new ArrayList();
        if (isPrivateSlicesNeeded(uri)) {
            arrayList.addAll(this.mSlicesDatabaseAccessor.getSliceUris(uri.getAuthority(), false));
            Log.d("SettingsSliceProvider", "provide " + arrayList.size() + " non-public slices");
            uri = new Uri.Builder().scheme("content").authority(uri.getAuthority()).build();
        }
        if (SliceBuilderUtils.getPathData(uri) != null) {
            arrayList.add(uri);
            return arrayList;
        }
        final String authority = uri.getAuthority();
        String path = uri.getPath();
        boolean isEmpty = path.isEmpty();
        if (isEmpty || TextUtils.equals(path, "/action") || TextUtils.equals(path, "/intent")) {
            arrayList.addAll(this.mSlicesDatabaseAccessor.getSliceUris(authority, true));
            if (isEmpty && TextUtils.isEmpty(authority)) {
                arrayList.addAll(PUBLICLY_SUPPORTED_CUSTOM_SLICE_URIS);
            } else {
                arrayList.addAll((List) PUBLICLY_SUPPORTED_CUSTOM_SLICE_URIS.stream().filter(new Predicate() { // from class: com.android.settings.slices.SettingsSliceProvider$$ExternalSyntheticLambda5
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onGetSliceDescendants$2;
                        lambda$onGetSliceDescendants$2 = SettingsSliceProvider.lambda$onGetSliceDescendants$2(authority, (Uri) obj);
                        return lambda$onGetSliceDescendants$2;
                    }
                }).collect(Collectors.toList()));
            }
            grantAllowlistedPackagePermissions(getContext(), arrayList);
            return arrayList;
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onGetSliceDescendants$2(String str, Uri uri) {
        return TextUtils.equals(str, uri.getAuthority());
    }

    @Override // androidx.slice.SliceProvider
    public PendingIntent onCreatePermissionRequest(Uri uri, String str) {
        return PendingIntent.getActivity(getContext(), 0, new Intent("android.settings.SETTINGS").setPackage("com.android.settings"), 67108864);
    }

    static void grantAllowlistedPackagePermissions(Context context, List<Uri> list) {
        if (list == null) {
            Log.d("SettingsSliceProvider", "No descendants to grant permission with, skipping.");
        }
        String[] stringArray = context.getResources().getStringArray(R$array.slice_allowlist_package_names);
        if (stringArray == null || stringArray.length == 0) {
            Log.d("SettingsSliceProvider", "No packages to allowlist, skipping.");
            return;
        }
        Log.d("SettingsSliceProvider", String.format("Allowlisting %d uris to %d pkgs.", Integer.valueOf(list.size()), Integer.valueOf(stringArray.length)));
        SliceManager sliceManager = (SliceManager) context.getSystemService(SliceManager.class);
        for (Uri uri : list) {
            for (String str : stringArray) {
                sliceManager.grantSlicePermission(str, uri);
            }
        }
    }

    @Override // android.content.ContentProvider
    public void shutdown() {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.slices.SettingsSliceProvider$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SliceBackgroundWorker.shutdown();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: loadSlice */
    public void lambda$loadSliceInBackground$5(final Uri uri) {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            SliceData sliceDataFromUri = this.mSlicesDatabaseAccessor.getSliceDataFromUri(uri);
            final BasePreferenceController preferenceController = SliceBuilderUtils.getPreferenceController(getContext(), sliceDataFromUri);
            IntentFilter intentFilter = preferenceController.getIntentFilter();
            if (intentFilter != null) {
                if (preferenceController instanceof VolumeSeekBarPreferenceController) {
                    VolumeSliceHelper.registerIntentToUri(getContext(), intentFilter, uri, ((VolumeSeekBarPreferenceController) preferenceController).getAudioStream());
                } else {
                    registerIntentToUri(intentFilter, uri);
                }
            }
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.slices.SettingsSliceProvider$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SettingsSliceProvider.this.lambda$loadSlice$4(preferenceController, uri);
                }
            });
            this.mSliceWeakDataCache.put(uri, sliceDataFromUri);
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d("SettingsSliceProvider", "Built slice (" + uri + ") in: " + (System.currentTimeMillis() - currentTimeMillis));
        } catch (IllegalStateException e) {
            Log.d("SettingsSliceProvider", "Could not create slicedata for uri: " + uri, e);
        }
    }

    void loadSliceInBackground(final Uri uri) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.slices.SettingsSliceProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SettingsSliceProvider.this.lambda$loadSliceInBackground$5(uri);
            }
        });
    }

    void registerIntentToUri(IntentFilter intentFilter, Uri uri) {
        SliceBroadcastRelay.registerReceiver(getContext(), uri, SliceRelayReceiver.class, intentFilter);
    }

    Set<String> getBlockedKeys() {
        String string = Settings.Global.getString(getContext().getContentResolver(), "blocked_slices");
        ArraySet arraySet = new ArraySet();
        try {
            KEY_VALUE_LIST_PARSER.setString(string);
            Collections.addAll(arraySet, parseStringArray(string));
            return arraySet;
        } catch (IllegalArgumentException e) {
            Log.e("SettingsSliceProvider", "Bad Settings Slices Allowlist flags", e);
            return arraySet;
        }
    }

    boolean isPrivateSlicesNeeded(Uri uri) {
        String string = getContext().getString(R$string.config_non_public_slice_query_uri);
        if (TextUtils.isEmpty(string) || !TextUtils.equals(uri.toString(), string)) {
            return false;
        }
        int callingUid = Binder.getCallingUid();
        return (getContext().checkPermission("android.permission.READ_SEARCH_INDEXABLES", Binder.getCallingPid(), callingUid) == 0) && TextUtils.equals(getContext().getPackageManager().getPackagesForUid(callingUid)[0], getContext().getString(R$string.config_settingsintelligence_package_name));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: startBackgroundWorker */
    public void lambda$onSlicePinned$0(Sliceable sliceable, Uri uri) {
        if (sliceable.getBackgroundWorkerClass() == null || this.mPinnedWorkers.containsKey(uri)) {
            return;
        }
        Log.d("SettingsSliceProvider", "Starting background worker for: " + uri);
        SliceBackgroundWorker sliceBackgroundWorker = SliceBackgroundWorker.getInstance(getContext(), sliceable, uri);
        this.mPinnedWorkers.put(uri, sliceBackgroundWorker);
        sliceBackgroundWorker.pin();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: stopBackgroundWorker */
    public void lambda$onSliceUnpinned$1(Uri uri) {
        SliceBackgroundWorker sliceBackgroundWorker = this.mPinnedWorkers.get(uri);
        if (sliceBackgroundWorker != null) {
            Log.d("SettingsSliceProvider", "Stopping background worker for: " + uri);
            sliceBackgroundWorker.unpin();
            this.mPinnedWorkers.remove(uri);
        }
    }

    private static Slice getSliceStub(Uri uri) {
        return new Slice.Builder(uri).build();
    }

    private static String[] parseStringArray(String str) {
        if (str != null) {
            String[] split = str.split(":");
            if (split.length > 0) {
                return split;
            }
        }
        return new String[0];
    }
}
