package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
class AccessibilitySettingsContentObserver extends ContentObserver {
    private final Map<Uri, String> mUriToKey;
    private final Map<List<String>, ContentObserverCallback> mUrisToCallback;

    /* loaded from: classes.dex */
    public interface ContentObserverCallback {
        void onChange(String str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilitySettingsContentObserver(Handler handler) {
        super(handler);
        this.mUriToKey = new HashMap(2);
        this.mUrisToCallback = new HashMap();
        addDefaultKeysToMap();
    }

    public void register(ContentResolver contentResolver) {
        for (Uri uri : this.mUriToKey.keySet()) {
            contentResolver.registerContentObserver(uri, false, this);
        }
    }

    public void unregister(ContentResolver contentResolver) {
        contentResolver.unregisterContentObserver(this);
    }

    private void addDefaultKeysToMap() {
        addKeyToMap("accessibility_enabled");
        addKeyToMap("enabled_accessibility_services");
    }

    private boolean isDefaultKey(String str) {
        return "accessibility_enabled".equals(str) || "enabled_accessibility_services".equals(str);
    }

    private void addKeyToMap(String str) {
        this.mUriToKey.put(Settings.Secure.getUriFor(str), str);
    }

    public void registerKeysToObserverCallback(List<String> list, ContentObserverCallback contentObserverCallback) {
        for (String str : list) {
            addKeyToMap(str);
        }
        this.mUrisToCallback.put(list, contentObserverCallback);
    }

    public void registerObserverCallback(ContentObserverCallback contentObserverCallback) {
        this.mUrisToCallback.put(Collections.emptyList(), contentObserverCallback);
    }

    @Override // android.database.ContentObserver
    public final void onChange(boolean z, Uri uri) {
        String str = this.mUriToKey.get(uri);
        if (str == null) {
            Log.w("AccessibilitySettingsContentObserver", "AccessibilitySettingsContentObserver can not find the key for uri: " + uri);
            return;
        }
        for (List<String> list : this.mUrisToCallback.keySet()) {
            if (isDefaultKey(str) || list.contains(str)) {
                this.mUrisToCallback.get(list).onChange(str);
            }
        }
    }
}
