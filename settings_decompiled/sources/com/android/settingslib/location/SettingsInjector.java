package com.android.settingslib.location;

import android.R;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import androidx.preference.Preference;
import com.android.settingslib.R$string;
import com.android.settingslib.location.InjectedSetting;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class SettingsInjector {
    private final Context mContext;
    private final Handler mHandler;
    protected final Set<Setting> mSettings;

    protected Preference createPreference(Context context, InjectedSetting injectedSetting) {
        throw null;
    }

    protected void logPreferenceClick(Intent intent) {
        throw null;
    }

    public SettingsInjector(Context context) {
        this.mContext = context;
        HashSet hashSet = new HashSet();
        this.mSettings = hashSet;
        this.mHandler = new StatusLoadingHandler(hashSet);
    }

    protected List<InjectedSetting> getSettings(UserHandle userHandle) {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent("android.location.SettingInjectorService");
        int identifier = userHandle.getIdentifier();
        List<ResolveInfo> queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(intent, 128, identifier);
        if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", "Found services for profile id " + identifier + ": " + queryIntentServicesAsUser);
        }
        PackageManager packageManager2 = this.mContext.createContextAsUser(userHandle, 0).getPackageManager();
        ArrayList arrayList = new ArrayList(queryIntentServicesAsUser.size());
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            try {
                InjectedSetting parseServiceInfo = parseServiceInfo(resolveInfo, userHandle, packageManager2);
                if (parseServiceInfo == null) {
                    Log.w("SettingsInjector", "Unable to load service info " + resolveInfo);
                } else {
                    arrayList.add(parseServiceInfo);
                }
            } catch (IOException e) {
                Log.w("SettingsInjector", "Unable to load service info " + resolveInfo, e);
            } catch (XmlPullParserException e2) {
                Log.w("SettingsInjector", "Unable to load service info " + resolveInfo, e2);
            }
        }
        if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", "Loaded settings for profile id " + identifier + ": " + arrayList);
        }
        return arrayList;
    }

    private void populatePreference(Preference preference, InjectedSetting injectedSetting) {
        preference.setTitle(injectedSetting.title);
        preference.setSummary(R$string.loading_injected_setting_summary);
        preference.setOnPreferenceClickListener(new ServiceSettingClickedListener(injectedSetting));
    }

    public Map<Integer, List<Preference>> getInjectedSettings(Context context, int i) {
        List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService("user")).getUserProfiles();
        ArrayMap arrayMap = new ArrayMap();
        this.mSettings.clear();
        for (UserHandle userHandle : userProfiles) {
            if (i == -2 || i == userHandle.getIdentifier()) {
                ArrayList arrayList = new ArrayList();
                for (InjectedSetting injectedSetting : getSettings(userHandle)) {
                    Preference createPreference = createPreference(context, injectedSetting);
                    populatePreference(createPreference, injectedSetting);
                    arrayList.add(createPreference);
                    this.mSettings.add(new Setting(injectedSetting, createPreference));
                }
                if (!arrayList.isEmpty()) {
                    arrayMap.put(Integer.valueOf(userHandle.getIdentifier()), arrayList);
                }
            }
        }
        reloadStatusMessages();
        return arrayMap;
    }

    private static InjectedSetting parseServiceInfo(ResolveInfo resolveInfo, UserHandle userHandle, PackageManager packageManager) throws XmlPullParserException, IOException {
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        XmlResourceParser xmlResourceParser = null;
        if ((serviceInfo.applicationInfo.flags & 1) == 0 && Log.isLoggable("SettingsInjector", 5)) {
            Log.w("SettingsInjector", "Ignoring attempt to inject setting from app not in system image: " + resolveInfo);
            return null;
        }
        try {
            try {
                XmlResourceParser loadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, "android.location.SettingInjectorService");
                if (loadXmlMetaData == null) {
                    throw new XmlPullParserException("No android.location.SettingInjectorService meta-data for " + resolveInfo + ": " + serviceInfo);
                }
                AttributeSet asAttributeSet = Xml.asAttributeSet(loadXmlMetaData);
                while (true) {
                    int next = loadXmlMetaData.next();
                    if (next == 1 || next == 2) {
                        break;
                    }
                }
                if (!"injected-location-setting".equals(loadXmlMetaData.getName())) {
                    throw new XmlPullParserException("Meta-data does not start with injected-location-setting tag");
                }
                InjectedSetting parseAttributes = parseAttributes(serviceInfo.packageName, serviceInfo.name, userHandle, packageManager.getResourcesForApplication(serviceInfo.packageName), asAttributeSet);
                loadXmlMetaData.close();
                return parseAttributes;
            } catch (PackageManager.NameNotFoundException unused) {
                throw new XmlPullParserException("Unable to load resources for package " + serviceInfo.packageName);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    private static InjectedSetting parseAttributes(String str, String str2, UserHandle userHandle, Resources resources, AttributeSet attributeSet) {
        TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.SettingInjectorService);
        try {
            String string = obtainAttributes.getString(1);
            int resourceId = obtainAttributes.getResourceId(0, 0);
            String string2 = obtainAttributes.getString(2);
            String string3 = obtainAttributes.getString(3);
            if (Log.isLoggable("SettingsInjector", 3)) {
                Log.d("SettingsInjector", "parsed title: " + string + ", iconId: " + resourceId + ", settingsActivity: " + string2);
            }
            return new InjectedSetting.Builder().setPackageName(str).setClassName(str2).setTitle(string).setIconId(resourceId).setUserHandle(userHandle).setSettingsActivity(string2).setUserRestriction(string3).build();
        } finally {
            obtainAttributes.recycle();
        }
    }

    public void reloadStatusMessages() {
        if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", "reloadingStatusMessages: " + this.mSettings);
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class ServiceSettingClickedListener implements Preference.OnPreferenceClickListener {
        private InjectedSetting mInfo;

        public ServiceSettingClickedListener(InjectedSetting injectedSetting) {
            this.mInfo = injectedSetting;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent();
            InjectedSetting injectedSetting = this.mInfo;
            intent.setClassName(injectedSetting.packageName, injectedSetting.settingsActivity);
            SettingsInjector.this.logPreferenceClick(intent);
            SettingsInjector.this.mContext.startActivityAsUser(intent, this.mInfo.mUserHandle);
            return true;
        }
    }

    /* loaded from: classes2.dex */
    private static final class StatusLoadingHandler extends Handler {
        WeakReference<Set<Setting>> mAllSettings;
        private Set<Setting> mSettingsBeingLoaded;
        private Deque<Setting> mSettingsToLoad;

        public StatusLoadingHandler(Set<Setting> set) {
            super(Looper.getMainLooper());
            this.mSettingsToLoad = new ArrayDeque();
            this.mSettingsBeingLoaded = new ArraySet();
            this.mAllSettings = new WeakReference<>(set);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (Log.isLoggable("SettingsInjector", 3)) {
                Log.d("SettingsInjector", "handleMessage start: " + message + ", " + this);
            }
            int i = message.what;
            if (i == 1) {
                Set<Setting> set = this.mAllSettings.get();
                if (set != null) {
                    this.mSettingsToLoad.clear();
                    this.mSettingsToLoad.addAll(set);
                }
            } else if (i == 2) {
                Setting setting = (Setting) message.obj;
                setting.maybeLogElapsedTime();
                this.mSettingsBeingLoaded.remove(setting);
                removeMessages(3, setting);
            } else if (i == 3) {
                Setting setting2 = (Setting) message.obj;
                this.mSettingsBeingLoaded.remove(setting2);
                if (Log.isLoggable("SettingsInjector", 5)) {
                    Log.w("SettingsInjector", "Timed out after " + setting2.getElapsedTime() + " millis trying to get status for: " + setting2);
                }
            } else {
                Log.wtf("SettingsInjector", "Unexpected what: " + message);
            }
            if (this.mSettingsBeingLoaded.size() > 0) {
                if (Log.isLoggable("SettingsInjector", 2)) {
                    Log.v("SettingsInjector", "too many services already live for " + message + ", " + this);
                }
            } else if (this.mSettingsToLoad.isEmpty()) {
                if (Log.isLoggable("SettingsInjector", 2)) {
                    Log.v("SettingsInjector", "nothing left to do for " + message + ", " + this);
                }
            } else {
                Setting removeFirst = this.mSettingsToLoad.removeFirst();
                removeFirst.startService();
                this.mSettingsBeingLoaded.add(removeFirst);
                sendMessageDelayed(obtainMessage(3, removeFirst), 1000L);
                if (Log.isLoggable("SettingsInjector", 3)) {
                    Log.d("SettingsInjector", "handleMessage end " + message + ", " + this + ", started loading " + removeFirst);
                }
            }
        }

        @Override // android.os.Handler
        public String toString() {
            return "StatusLoadingHandler{mSettingsToLoad=" + this.mSettingsToLoad + ", mSettingsBeingLoaded=" + this.mSettingsBeingLoaded + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MessengerHandler extends Handler {
        private Handler mHandler;
        private WeakReference<Setting> mSettingRef;

        public MessengerHandler(Setting setting, Handler handler) {
            this.mSettingRef = new WeakReference<>(setting);
            this.mHandler = handler;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Setting setting = this.mSettingRef.get();
            if (setting == null) {
                return;
            }
            Bundle data = message.getData();
            if (Log.isLoggable("SettingsInjector", 3)) {
                Log.d("SettingsInjector", setting + ": received " + message + ", bundle: " + data);
            }
            boolean z = data.getBoolean("enabled", true);
            String string = data.getString("summary");
            Preference preference = setting.preference;
            if (TextUtils.isEmpty(string)) {
                preference.setSummary(R$string.summary_placeholder);
            } else {
                preference.setSummary(string);
            }
            preference.setEnabled(z);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2, setting));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public final class Setting {
        public final Preference preference;
        public final InjectedSetting setting;
        public long startMillis;

        public Setting(InjectedSetting injectedSetting, Preference preference) {
            this.setting = injectedSetting;
            this.preference = preference;
        }

        public String toString() {
            return "Setting{setting=" + this.setting + ", preference=" + this.preference + '}';
        }

        public void startService() {
            if (!((ActivityManager) SettingsInjector.this.mContext.getSystemService("activity")).isUserRunning(this.setting.mUserHandle.getIdentifier())) {
                if (Log.isLoggable("SettingsInjector", 2)) {
                    Log.v("SettingsInjector", "Cannot start service as user " + this.setting.mUserHandle.getIdentifier() + " is not running");
                    return;
                }
                return;
            }
            MessengerHandler messengerHandler = new MessengerHandler(this, SettingsInjector.this.mHandler);
            Messenger messenger = new Messenger(messengerHandler);
            Intent serviceIntent = this.setting.getServiceIntent();
            serviceIntent.putExtra("messenger", messenger);
            if (Log.isLoggable("SettingsInjector", 3)) {
                Log.d("SettingsInjector", this.setting + ": sending update intent: " + serviceIntent + ", handler: " + messengerHandler);
                this.startMillis = SystemClock.elapsedRealtime();
            } else {
                this.startMillis = 0L;
            }
            SettingsInjector.this.mContext.startServiceAsUser(serviceIntent, this.setting.mUserHandle);
        }

        public long getElapsedTime() {
            return SystemClock.elapsedRealtime() - this.startMillis;
        }

        public void maybeLogElapsedTime() {
            if (!Log.isLoggable("SettingsInjector", 3) || this.startMillis == 0) {
                return;
            }
            long elapsedTime = getElapsedTime();
            Log.d("SettingsInjector", this + " update took " + elapsedTime + " millis");
        }
    }
}
