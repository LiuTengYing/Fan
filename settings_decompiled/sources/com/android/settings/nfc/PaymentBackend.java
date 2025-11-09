package com.android.settings.nfc;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.ApduServiceInfo;
import android.nfc.cardemulation.CardEmulation;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import com.android.internal.content.PackageMonitor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PaymentBackend {
    private final NfcAdapter mAdapter;
    private ArrayList<PaymentAppInfo> mAppInfos;
    private final CardEmulation mCardEmuManager;
    private final Context mContext;
    private PaymentAppInfo mDefaultAppInfo;
    private final PackageMonitor mSettingsPackageMonitor = new SettingsPackageMonitor();
    private ArrayList<Callback> mCallbacks = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface Callback {
        void onPaymentAppsChanged();
    }

    /* loaded from: classes.dex */
    public static class PaymentAppInfo {
        public ComponentName componentName;
        CharSequence description;
        public Drawable icon;
        boolean isDefault;
        public CharSequence label;
        public ComponentName settingsComponent;
        public UserHandle userHandle;
    }

    /* loaded from: classes.dex */
    public static class PaymentInfo {
        public ComponentName componentName;
        public int userId;
    }

    public PaymentBackend(Context context) {
        this.mContext = context;
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(context);
        this.mAdapter = defaultAdapter;
        this.mCardEmuManager = CardEmulation.getInstance(defaultAdapter);
        refresh();
    }

    public void onPause() {
        this.mSettingsPackageMonitor.unregister();
    }

    public void onResume() {
        PackageMonitor packageMonitor = this.mSettingsPackageMonitor;
        Context context = this.mContext;
        packageMonitor.register(context, context.getMainLooper(), false);
        refresh();
    }

    public void refresh() {
        PackageManager packageManager = this.mContext.getPackageManager();
        ArrayList<PaymentAppInfo> arrayList = new ArrayList<>();
        List<UserHandle> enabledProfiles = ((UserManager) this.mContext.createContextAsUser(UserHandle.of(ActivityManager.getCurrentUser()), 0).getSystemService(UserManager.class)).getEnabledProfiles();
        PaymentInfo defaultPaymentApp = getDefaultPaymentApp();
        PaymentAppInfo paymentAppInfo = null;
        for (UserHandle userHandle : enabledProfiles) {
            List<ApduServiceInfo> services = this.mCardEmuManager.getServices("payment", userHandle.getIdentifier());
            if (services != null) {
                ArrayList arrayList2 = new ArrayList();
                for (ApduServiceInfo apduServiceInfo : services) {
                    PaymentAppInfo paymentAppInfo2 = new PaymentAppInfo();
                    paymentAppInfo2.userHandle = userHandle;
                    CharSequence loadLabel = apduServiceInfo.loadLabel(packageManager);
                    paymentAppInfo2.label = loadLabel;
                    if (loadLabel == null) {
                        paymentAppInfo2.label = apduServiceInfo.loadAppLabel(packageManager);
                    }
                    if (defaultPaymentApp == null) {
                        paymentAppInfo2.isDefault = false;
                    } else {
                        paymentAppInfo2.isDefault = apduServiceInfo.getComponent().equals(defaultPaymentApp.componentName) && defaultPaymentApp.userId == userHandle.getIdentifier();
                    }
                    if (paymentAppInfo2.isDefault) {
                        paymentAppInfo = paymentAppInfo2;
                    }
                    paymentAppInfo2.componentName = apduServiceInfo.getComponent();
                    String settingsActivityName = apduServiceInfo.getSettingsActivityName();
                    if (settingsActivityName != null) {
                        paymentAppInfo2.settingsComponent = new ComponentName(paymentAppInfo2.componentName.getPackageName(), settingsActivityName);
                    } else {
                        paymentAppInfo2.settingsComponent = null;
                    }
                    paymentAppInfo2.description = apduServiceInfo.getDescription();
                    paymentAppInfo2.icon = packageManager.getUserBadgedIcon(apduServiceInfo.loadBanner(packageManager) != null ? apduServiceInfo.loadBanner(packageManager) : apduServiceInfo.loadIcon(packageManager), paymentAppInfo2.userHandle);
                    arrayList2.add(paymentAppInfo2);
                }
                arrayList.addAll(arrayList2);
            }
        }
        this.mAppInfos = arrayList;
        this.mDefaultAppInfo = paymentAppInfo;
        makeCallbacks();
    }

    public void registerCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    public List<PaymentAppInfo> getPaymentAppInfos() {
        return this.mAppInfos;
    }

    public PaymentAppInfo getDefaultApp() {
        return this.mDefaultAppInfo;
    }

    void makeCallbacks() {
        Iterator<Callback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onPaymentAppsChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isForegroundMode() {
        try {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "nfc_payment_foreground", UserHandle.myUserId()) != 0;
        } catch (Settings.SettingNotFoundException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setForegroundMode(boolean z) {
        for (UserHandle userHandle : ((UserManager) this.mContext.createContextAsUser(UserHandle.of(UserHandle.myUserId()), 0).getSystemService(UserManager.class)).getEnabledProfiles()) {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "nfc_payment_foreground", z ? 1 : 0, userHandle.getIdentifier());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PaymentInfo getDefaultPaymentApp() {
        for (UserHandle userHandle : ((UserManager) this.mContext.createContextAsUser(UserHandle.of(ActivityManager.getCurrentUser()), 0).getSystemService(UserManager.class)).getEnabledProfiles()) {
            ComponentName defaultPaymentApp = getDefaultPaymentApp(userHandle.getIdentifier());
            if (defaultPaymentApp != null) {
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.userId = userHandle.getIdentifier();
                paymentInfo.componentName = defaultPaymentApp;
                return paymentInfo;
            }
        }
        return null;
    }

    ComponentName getDefaultPaymentApp(int i) {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "nfc_payment_default_component", i);
        if (stringForUser != null) {
            return ComponentName.unflattenFromString(stringForUser);
        }
        return null;
    }

    public void setDefaultPaymentApp(ComponentName componentName, int i) {
        for (UserHandle userHandle : ((UserManager) this.mContext.createContextAsUser(UserHandle.of(ActivityManager.getCurrentUser()), 0).getSystemService(UserManager.class)).getEnabledProfiles()) {
            if (userHandle.getIdentifier() == i) {
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "nfc_payment_default_component", componentName != null ? componentName.flattenToString() : null, userHandle.getIdentifier());
            } else {
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "nfc_payment_default_component", null, userHandle.getIdentifier());
            }
        }
        refresh();
    }

    /* loaded from: classes.dex */
    private class SettingsPackageMonitor extends PackageMonitor {
        private Handler mHandler;

        private SettingsPackageMonitor() {
        }

        public void register(Context context, Looper looper, UserHandle userHandle, boolean z) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(looper) { // from class: com.android.settings.nfc.PaymentBackend.SettingsPackageMonitor.1
                    @Override // android.os.Handler
                    public void dispatchMessage(Message message) {
                        PaymentBackend.this.refresh();
                    }
                };
            }
            super.register(context, looper, userHandle, z);
        }

        public void onPackageAdded(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void onPackageAppeared(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void onPackageDisappeared(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void onPackageRemoved(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }
    }
}
