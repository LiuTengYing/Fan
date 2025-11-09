package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class CredentialManagementAppPreferenceController extends BasePreferenceController {
    private static final String TAG = "CredentialManagementApp";
    private String mCredentialManagerPackageName;
    private final ExecutorService mExecutor;
    private final Handler mHandler;
    private boolean mHasCredentialManagerPackage;
    private final PackageManager mPackageManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CredentialManagementAppPreferenceController(Context context, String str) {
        super(context, str);
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        this.mExecutor.execute(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppPreferenceController.this.lambda$updateState$1(preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(final Preference preference) {
        try {
            IKeyChainService service = KeyChain.bind(this.mContext).getService();
            this.mHasCredentialManagerPackage = service.hasCredentialManagementApp();
            this.mCredentialManagerPackageName = service.getCredentialManagementAppPackageName();
        } catch (RemoteException | InterruptedException unused) {
            Log.e(TAG, "Unable to display credential management app preference");
        }
        this.mHandler.post(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppPreferenceController.this.lambda$updateState$0(preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* renamed from: displayPreference */
    public void lambda$updateState$0(Preference preference) {
        if (this.mHasCredentialManagerPackage) {
            preference.setEnabled(true);
            try {
                preference.setSummary(this.mPackageManager.getApplicationInfo(this.mCredentialManagerPackageName, 0).loadLabel(this.mPackageManager));
                return;
            } catch (PackageManager.NameNotFoundException unused) {
                preference.setSummary(this.mCredentialManagerPackageName);
                return;
            }
        }
        preference.setEnabled(false);
        preference.setSummary(R$string.no_certificate_management_app);
    }
}
