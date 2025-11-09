package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.security.KeyChain;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class CredentialManagementAppHeaderController extends BasePreferenceController {
    private static final String TAG = "CredentialManagementApp";
    private String mCredentialManagerPackageName;
    private final ExecutorService mExecutor;
    private final Handler mHandler;
    private final PackageManager mPackageManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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

    public CredentialManagementAppHeaderController(Context context, String str) {
        super(context, str);
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(final PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mExecutor.execute(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppHeaderController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppHeaderController.this.lambda$displayPreference$1(preferenceScreen);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(final PreferenceScreen preferenceScreen) {
        try {
            this.mCredentialManagerPackageName = KeyChain.bind(this.mContext).getService().getCredentialManagementAppPackageName();
        } catch (RemoteException | InterruptedException unused) {
            Log.e(TAG, "Unable to display credential management app header");
        }
        this.mHandler.post(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppHeaderController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppHeaderController.this.lambda$displayPreference$0(preferenceScreen);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: displayHeader */
    public void lambda$displayPreference$0(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        ImageView imageView = (ImageView) layoutPreference.findViewById(R$id.entity_header_icon);
        TextView textView = (TextView) layoutPreference.findViewById(R$id.entity_header_title);
        TextView textView2 = (TextView) layoutPreference.findViewById(R$id.entity_header_summary);
        ((TextView) layoutPreference.findViewById(R$id.entity_header_second_summary)).setVisibility(8);
        try {
            ApplicationInfo applicationInfo = this.mPackageManager.getApplicationInfo(this.mCredentialManagerPackageName, 0);
            imageView.setImageDrawable(this.mPackageManager.getApplicationIcon(applicationInfo));
            textView.setText(applicationInfo.loadLabel(this.mPackageManager));
            textView2.setText(R$string.certificate_management_app_description);
        } catch (PackageManager.NameNotFoundException unused) {
            imageView.setImageDrawable(null);
            textView.setText(this.mCredentialManagerPackageName);
        }
    }
}
