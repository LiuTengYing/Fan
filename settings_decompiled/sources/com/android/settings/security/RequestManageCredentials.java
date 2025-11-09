package com.android.settings.security;

import android.app.Activity;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserManager;
import android.security.AppUriAuthenticationPolicy;
import android.security.KeyChain;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.Map;
/* loaded from: classes.dex */
public class RequestManageCredentials extends Activity {
    private AppUriAuthenticationPolicy mAuthenticationPolicy;
    private LinearLayout mButtonPanel;
    private String mCredentialManagerPackage;
    private ExtendedFloatingActionButton mExtendedFab;
    private KeyChain.KeyChainConnection mKeyChainConnection;
    private HandlerThread mKeyChainTread;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private boolean mDisplayingButtonPanel = false;
    private boolean mIsLandscapeMode = false;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!"android.security.MANAGE_CREDENTIALS".equals(getIntent().getAction())) {
            Log.e("ManageCredentials", "Unable to start activity because intent action is not android.security.MANAGE_CREDENTIALS");
            logRequestFailure();
            finishWithResultCancelled();
        } else if (isManagedDevice()) {
            Log.e("ManageCredentials", "Credential management on managed devices should be done by the Device Policy Controller, not a credential management app");
            logRequestFailure();
            finishWithResultCancelled();
        } else {
            String launchedFromPackage = getLaunchedFromPackage();
            this.mCredentialManagerPackage = launchedFromPackage;
            if (TextUtils.isEmpty(launchedFromPackage)) {
                Log.e("ManageCredentials", "Unknown credential manager app");
                logRequestFailure();
                finishWithResultCancelled();
                return;
            }
            DevicePolicyEventLogger.createEvent(178).setStrings(new String[]{this.mCredentialManagerPackage}).write();
            setContentView(R$layout.request_manage_credentials);
            getWindow().addSystemFlags(524288);
            this.mIsLandscapeMode = getResources().getConfiguration().orientation == 2;
            HandlerThread handlerThread = new HandlerThread("KeyChainConnection");
            this.mKeyChainTread = handlerThread;
            handlerThread.start();
            this.mKeyChainConnection = getKeyChainConnection(this, this.mKeyChainTread);
            AppUriAuthenticationPolicy appUriAuthenticationPolicy = (AppUriAuthenticationPolicy) getIntent().getParcelableExtra("android.security.extra.AUTHENTICATION_POLICY");
            if (!isValidAuthenticationPolicy(appUriAuthenticationPolicy)) {
                Log.e("ManageCredentials", "Invalid authentication policy");
                logRequestFailure();
                finishWithResultCancelled();
                return;
            }
            this.mAuthenticationPolicy = appUriAuthenticationPolicy;
            DevicePolicyEventLogger.createEvent(179).setStrings(new String[]{getNumberOfAuthenticationPolicyApps(this.mAuthenticationPolicy), getNumberOfAuthenticationPolicyUris(this.mAuthenticationPolicy)}).write();
            if (this.mIsLandscapeMode) {
                loadHeader();
            }
            loadRecyclerView();
            loadButtons();
            loadExtendedFloatingActionButton();
            addOnScrollListener();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        KeyChain.KeyChainConnection keyChainConnection = this.mKeyChainConnection;
        if (keyChainConnection != null) {
            keyChainConnection.close();
            this.mKeyChainConnection = null;
            this.mKeyChainTread.quitSafely();
        }
    }

    private boolean isValidAuthenticationPolicy(AppUriAuthenticationPolicy appUriAuthenticationPolicy) {
        if (appUriAuthenticationPolicy != null && !appUriAuthenticationPolicy.getAppAndUriMappings().isEmpty()) {
            try {
                for (String str : appUriAuthenticationPolicy.getAliases()) {
                    if (this.mKeyChainConnection.getService().requestPrivateKey(str) != null) {
                        return false;
                    }
                }
                return true;
            } catch (RemoteException e) {
                Log.e("ManageCredentials", "Invalid authentication policy", e);
            }
        }
        return false;
    }

    private boolean isManagedDevice() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        return (devicePolicyManager.getDeviceOwnerUser() == null && devicePolicyManager.getProfileOwner() == null && !hasManagedProfile()) ? false : true;
    }

    private boolean hasManagedProfile() {
        for (UserInfo userInfo : ((UserManager) getSystemService(UserManager.class)).getProfiles(getUserId())) {
            if (userInfo.isManagedProfile()) {
                return true;
            }
        }
        return false;
    }

    private void loadRecyclerView() {
        this.mLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R$id.apps_list);
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(new CredentialManagementAppAdapter(this, this.mCredentialManagerPackage, this.mAuthenticationPolicy.getAppAndUriMappings(), !this.mIsLandscapeMode, false));
    }

    private void loadButtons() {
        this.mButtonPanel = (LinearLayout) findViewById(R$id.button_panel);
        Button button = (Button) findViewById(R$id.dont_allow_button);
        button.setFilterTouchesWhenObscured(true);
        Button button2 = (Button) findViewById(R$id.allow_button);
        button2.setFilterTouchesWhenObscured(true);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.security.RequestManageCredentials$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RequestManageCredentials.this.lambda$loadButtons$0(view);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.security.RequestManageCredentials$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RequestManageCredentials.this.lambda$loadButtons$1(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadButtons$0(View view) {
        DevicePolicyEventLogger.createEvent(181).write();
        finishWithResultCancelled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadButtons$1(View view) {
        setOrUpdateCredentialManagementAppAndFinish();
    }

    private void loadExtendedFloatingActionButton() {
        ExtendedFloatingActionButton extendedFloatingActionButton = (ExtendedFloatingActionButton) findViewById(R$id.extended_fab);
        this.mExtendedFab = extendedFloatingActionButton;
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.security.RequestManageCredentials$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RequestManageCredentials.this.lambda$loadExtendedFloatingActionButton$2(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadExtendedFloatingActionButton$2(View view) {
        int size;
        if (this.mIsLandscapeMode) {
            size = this.mAuthenticationPolicy.getAppAndUriMappings().size() - 1;
        } else {
            size = this.mAuthenticationPolicy.getAppAndUriMappings().size();
        }
        this.mRecyclerView.scrollToPosition(size);
        this.mExtendedFab.hide();
        showButtonPanel();
    }

    private void loadHeader() {
        ImageView imageView = (ImageView) findViewById(R$id.credential_management_app_icon);
        TextView textView = (TextView) findViewById(R$id.credential_management_app_title);
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(this.mCredentialManagerPackage, 0);
            imageView.setImageDrawable(getPackageManager().getApplicationIcon(applicationInfo));
            textView.setText(TextUtils.expandTemplate(getText(R$string.request_manage_credentials_title), applicationInfo.loadLabel(getPackageManager())));
        } catch (PackageManager.NameNotFoundException unused) {
            imageView.setImageDrawable(null);
            textView.setText(TextUtils.expandTemplate(getText(R$string.request_manage_credentials_title), this.mCredentialManagerPackage));
        }
    }

    private void setOrUpdateCredentialManagementAppAndFinish() {
        try {
            this.mKeyChainConnection.getService().setCredentialManagementApp(this.mCredentialManagerPackage, this.mAuthenticationPolicy);
            DevicePolicyEventLogger.createEvent(180).write();
            setResult(-1);
        } catch (RemoteException e) {
            Log.e("ManageCredentials", "Unable to set credential manager app", e);
            logRequestFailure();
        }
        finish();
    }

    KeyChain.KeyChainConnection getKeyChainConnection(Context context, HandlerThread handlerThread) {
        try {
            return KeyChain.bindAsUser(context, new Handler(handlerThread.getLooper()), Process.myUserHandle());
        } catch (InterruptedException e) {
            throw new RuntimeException("Faile to bind to KeyChain", e);
        }
    }

    private void addOnScrollListener() {
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.settings.security.RequestManageCredentials.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (RequestManageCredentials.this.mDisplayingButtonPanel) {
                    return;
                }
                if (i2 > 0 && RequestManageCredentials.this.mExtendedFab.getVisibility() == 0) {
                    RequestManageCredentials.this.mExtendedFab.shrink();
                }
                if (RequestManageCredentials.this.isRecyclerScrollable()) {
                    RequestManageCredentials.this.mExtendedFab.show();
                    RequestManageCredentials.this.hideButtonPanel();
                    return;
                }
                RequestManageCredentials.this.mExtendedFab.hide();
                RequestManageCredentials.this.showButtonPanel();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showButtonPanel() {
        this.mRecyclerView.setPadding(0, 0, 0, (int) ((getResources().getDisplayMetrics().density * 60.0f) + 0.5f));
        this.mButtonPanel.setVisibility(0);
        this.mDisplayingButtonPanel = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideButtonPanel() {
        this.mRecyclerView.setPadding(0, 0, 0, 0);
        this.mButtonPanel.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRecyclerScrollable() {
        return (this.mLayoutManager == null || this.mRecyclerView.getAdapter() == null || this.mLayoutManager.findLastCompletelyVisibleItemPosition() >= this.mRecyclerView.getAdapter().getItemCount() - 1) ? false : true;
    }

    private void finishWithResultCancelled() {
        setResult(0);
        finish();
    }

    private void logRequestFailure() {
        DevicePolicyEventLogger.createEvent(182).write();
    }

    private String getNumberOfAuthenticationPolicyUris(AppUriAuthenticationPolicy appUriAuthenticationPolicy) {
        int i = 0;
        for (Map.Entry<String, Map<Uri, String>> entry : appUriAuthenticationPolicy.getAppAndUriMappings().entrySet()) {
            i += entry.getValue().size();
        }
        return String.valueOf(i);
    }

    private String getNumberOfAuthenticationPolicyApps(AppUriAuthenticationPolicy appUriAuthenticationPolicy) {
        return String.valueOf(appUriAuthenticationPolicy.getAppAndUriMappings().size());
    }
}
