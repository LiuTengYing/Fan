package com.android.settings.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.android.settings.SubSettings;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
/* loaded from: classes.dex */
public class SubSettingLauncher {
    private final Context mContext;
    private final LaunchRequest mLaunchRequest;
    private boolean mLaunched;

    public SubSettingLauncher(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must be non-null.");
        }
        this.mContext = context;
        LaunchRequest launchRequest = new LaunchRequest();
        this.mLaunchRequest = launchRequest;
        launchRequest.mTransitionType = 0;
    }

    public SubSettingLauncher setDestination(String str) {
        this.mLaunchRequest.mDestinationName = str;
        return this;
    }

    public SubSettingLauncher setTitleRes(int i) {
        return setTitleRes(null, i);
    }

    public SubSettingLauncher setTitleRes(String str, int i) {
        LaunchRequest launchRequest = this.mLaunchRequest;
        launchRequest.mTitleResPackageName = str;
        launchRequest.mTitleResId = i;
        launchRequest.mTitle = null;
        return this;
    }

    public SubSettingLauncher setTitleText(CharSequence charSequence) {
        this.mLaunchRequest.mTitle = charSequence;
        return this;
    }

    public SubSettingLauncher setArguments(Bundle bundle) {
        this.mLaunchRequest.mArguments = bundle;
        return this;
    }

    public SubSettingLauncher setExtras(Bundle bundle) {
        this.mLaunchRequest.mExtras = bundle;
        return this;
    }

    public SubSettingLauncher setSourceMetricsCategory(int i) {
        this.mLaunchRequest.mSourceMetricsCategory = i;
        return this;
    }

    public SubSettingLauncher setResultListener(Fragment fragment, int i) {
        LaunchRequest launchRequest = this.mLaunchRequest;
        launchRequest.mRequestCode = i;
        launchRequest.mResultListener = fragment;
        return this;
    }

    public SubSettingLauncher addFlags(int i) {
        LaunchRequest launchRequest = this.mLaunchRequest;
        launchRequest.mFlags = i | launchRequest.mFlags;
        return this;
    }

    public SubSettingLauncher setUserHandle(UserHandle userHandle) {
        this.mLaunchRequest.mUserHandle = userHandle;
        return this;
    }

    public SubSettingLauncher setTransitionType(int i) {
        this.mLaunchRequest.mTransitionType = i;
        return this;
    }

    public SubSettingLauncher setIsSecondLayerPage(boolean z) {
        this.mLaunchRequest.mIsSecondLayerPage = z;
        return this;
    }

    public void launch() {
        if (this.mLaunched) {
            throw new IllegalStateException("This launcher has already been executed. Do not reuse");
        }
        this.mLaunched = true;
        Intent intent = toIntent();
        UserHandle userHandle = this.mLaunchRequest.mUserHandle;
        boolean z = (userHandle == null || userHandle.getIdentifier() == UserHandle.myUserId()) ? false : true;
        LaunchRequest launchRequest = this.mLaunchRequest;
        Fragment fragment = launchRequest.mResultListener;
        boolean z2 = fragment != null;
        if (z && z2) {
            launchForResultAsUser(intent, launchRequest.mUserHandle, fragment, launchRequest.mRequestCode);
        } else if (z && !z2) {
            launchAsUser(intent, launchRequest.mUserHandle);
        } else if (!z && z2) {
            launchForResult(fragment, intent, launchRequest.mRequestCode);
        } else {
            launch(intent);
        }
    }

    public Intent toIntent() {
        Intent intent = new Intent("android.intent.action.MAIN");
        copyExtras(intent);
        intent.setClass(this.mContext, SubSettings.class);
        if (TextUtils.isEmpty(this.mLaunchRequest.mDestinationName)) {
            throw new IllegalArgumentException("Destination fragment must be set");
        }
        intent.putExtra(":settings:show_fragment", this.mLaunchRequest.mDestinationName);
        int i = this.mLaunchRequest.mSourceMetricsCategory;
        if (i < 0) {
            throw new IllegalArgumentException("Source metrics category must be set");
        }
        intent.putExtra(":settings:source_metrics", i);
        intent.putExtra(":settings:show_fragment_args", this.mLaunchRequest.mArguments);
        intent.putExtra(":settings:show_fragment_title_res_package_name", this.mLaunchRequest.mTitleResPackageName);
        intent.putExtra(":settings:show_fragment_title_resid", this.mLaunchRequest.mTitleResId);
        intent.putExtra(":settings:show_fragment_title", this.mLaunchRequest.mTitle);
        intent.addFlags(this.mLaunchRequest.mFlags);
        intent.putExtra("page_transition_type", this.mLaunchRequest.mTransitionType);
        intent.putExtra(":settings:is_second_layer_page", this.mLaunchRequest.mIsSecondLayerPage);
        if (UniMobileNetworkUtils.isSubsidyShowing()) {
            intent.putExtra(":settings:subsidylock", true);
        }
        return intent;
    }

    void launch(Intent intent) {
        this.mContext.startActivity(intent);
    }

    void launchAsUser(Intent intent, UserHandle userHandle) {
        this.mContext.startActivityAsUser(intent, userHandle);
    }

    void launchForResultAsUser(Intent intent, UserHandle userHandle, Fragment fragment, int i) {
        fragment.getActivity().startActivityForResultAsUser(intent, i, userHandle);
    }

    void launchForResult(Fragment fragment, Intent intent, int i) {
        fragment.startActivityForResult(intent, i);
    }

    private void copyExtras(Intent intent) {
        Bundle bundle = this.mLaunchRequest.mExtras;
        if (bundle != null) {
            intent.replaceExtras(bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LaunchRequest {
        Bundle mArguments;
        String mDestinationName;
        Bundle mExtras;
        int mFlags;
        boolean mIsSecondLayerPage;
        int mRequestCode;
        Fragment mResultListener;
        int mSourceMetricsCategory = -100;
        CharSequence mTitle;
        int mTitleResId;
        String mTitleResPackageName;
        int mTransitionType;
        UserHandle mUserHandle;

        LaunchRequest() {
        }
    }
}
