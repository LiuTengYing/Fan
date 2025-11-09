package com.android.settings.wifi.addappnetworks;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.Window;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils;
/* loaded from: classes.dex */
public class AddAppNetworksActivity extends FragmentActivity {
    @VisibleForTesting
    final Bundle mBundle = new Bundle();
    @VisibleForTesting
    IActivityManager mActivityManager = ActivityManager.getService();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.settings_panel);
        if (!showAddNetworksFragment()) {
            finish();
            return;
        }
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        Window window = getWindow();
        window.setGravity(80);
        window.setLayout(-1, -2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (showAddNetworksFragment()) {
            return;
        }
        finish();
    }

    @VisibleForTesting
    boolean showAddNetworksFragment() {
        if (isGuestUser(getApplicationContext())) {
            Log.e("AddAppNetworksActivity", "Guest user is not allowed to configure Wi-Fi!");
            EventLog.writeEvent(1397638484, "224772678", -1, "User is a guest");
            return false;
        } else if (!isAddWifiConfigAllow()) {
            Log.d("AddAppNetworksActivity", "Not allowed by Enterprise Restriction");
            return false;
        } else {
            String callingAppPackageName = getCallingAppPackageName();
            if (TextUtils.isEmpty(callingAppPackageName)) {
                Log.d("AddAppNetworksActivity", "Package name is null");
                return false;
            }
            this.mBundle.putString("panel_calling_package_name", callingAppPackageName);
            this.mBundle.putParcelableArrayList("android.provider.extra.WIFI_NETWORK_LIST", getIntent().getParcelableArrayListExtra("android.provider.extra.WIFI_NETWORK_LIST"));
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag("AddAppNetworksActivity");
            if (findFragmentByTag == null) {
                AddAppNetworksFragment addAppNetworksFragment = new AddAppNetworksFragment();
                addAppNetworksFragment.setArguments(this.mBundle);
                supportFragmentManager.beginTransaction().add(R$id.main_content, addAppNetworksFragment, "AddAppNetworksActivity").commit();
            } else {
                ((AddAppNetworksFragment) findFragmentByTag).createContent(this.mBundle);
            }
            return true;
        }
    }

    @VisibleForTesting
    protected String getCallingAppPackageName() {
        try {
            return this.mActivityManager.getLaunchedFromPackage(getActivityToken());
        } catch (RemoteException unused) {
            Log.e("AddAppNetworksActivity", "Can not get the package from activity manager");
            return null;
        }
    }

    @VisibleForTesting
    boolean isAddWifiConfigAllow() {
        return WifiEnterpriseRestrictionUtils.isAddWifiConfigAllowed(this);
    }

    private static boolean isGuestUser(Context context) {
        UserManager userManager;
        if (context == null || (userManager = (UserManager) context.getSystemService(UserManager.class)) == null) {
            return false;
        }
        return userManager.isGuestUser();
    }
}
