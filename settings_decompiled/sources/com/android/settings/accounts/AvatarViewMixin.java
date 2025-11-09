package com.android.settings.accounts;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.R$bool;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.utils.ThreadUtils;
import java.net.URISyntaxException;
import java.util.List;
/* loaded from: classes.dex */
public class AvatarViewMixin implements LifecycleObserver {
    static final Intent INTENT_GET_ACCOUNT_DATA = new Intent("android.content.action.SETTINGS_ACCOUNT_DATA");
    String mAccountName;
    private final MutableLiveData<Bitmap> mAvatarImage;
    private final ImageView mAvatarView;
    private final Context mContext;

    public static boolean isAvatarSupported(Context context) {
        if (context.getResources().getBoolean(R$bool.config_show_avatar_in_homepage)) {
            return true;
        }
        Log.d("AvatarViewMixin", "Feature disabled by config. Skipping");
        return false;
    }

    public AvatarViewMixin(final SettingsHomepageActivity settingsHomepageActivity, final ImageView imageView) {
        this.mContext = settingsHomepageActivity.getApplicationContext();
        this.mAvatarView = imageView;
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accounts.AvatarViewMixin$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AvatarViewMixin.this.lambda$new$0(settingsHomepageActivity, view);
            }
        });
        MutableLiveData<Bitmap> mutableLiveData = new MutableLiveData<>();
        this.mAvatarImage = mutableLiveData;
        mutableLiveData.observe(settingsHomepageActivity, new Observer() { // from class: com.android.settings.accounts.AvatarViewMixin$$ExternalSyntheticLambda1
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                imageView.setImageBitmap((Bitmap) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(SettingsHomepageActivity settingsHomepageActivity, View view) {
        try {
            Intent parseUri = Intent.parseUri(this.mContext.getResources().getString(R$string.config_account_intent_uri), 1);
            if (!TextUtils.isEmpty(this.mAccountName)) {
                parseUri.putExtra("extra.accountName", this.mAccountName);
            }
            List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(parseUri, 1048576);
            if (queryIntentActivities.isEmpty()) {
                Log.w("AvatarViewMixin", "Cannot find any matching action VIEW_ACCOUNT intent.");
                return;
            }
            parseUri.setComponent(queryIntentActivities.get(0).getComponentInfo().getComponentName());
            ActivityEmbeddingRulesController.registerTwoPanePairRuleForSettingsHome(this.mContext, parseUri.getComponent(), parseUri.getAction(), false, true, false);
            FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().logSettingsTileClick("avatar_icon", 1502);
            settingsHomepageActivity.startActivity(parseUri);
        } catch (URISyntaxException e) {
            Log.w("AvatarViewMixin", "Error parsing avatar mixin intent, skipping", e);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (hasAccount()) {
            loadAccount();
            return;
        }
        this.mAccountName = null;
        this.mAvatarView.setImageResource(R$drawable.ic_account_circle_24dp);
    }

    boolean hasAccount() {
        Account[] accounts = FeatureFactory.getFactory(this.mContext).getAccountFeatureProvider().getAccounts(this.mContext);
        return accounts != null && accounts.length > 0;
    }

    private void loadAccount() {
        final String queryProviderAuthority = queryProviderAuthority();
        if (TextUtils.isEmpty(queryProviderAuthority)) {
            return;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.accounts.AvatarViewMixin$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AvatarViewMixin.this.lambda$loadAccount$2(queryProviderAuthority);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAccount$2(String str) {
        Bundle call = this.mContext.getContentResolver().call(new Uri.Builder().scheme("content").authority(str).build(), "getAccountAvatar", (String) null, (Bundle) null);
        this.mAccountName = call.getString("account_name", "");
        this.mAvatarImage.postValue((Bitmap) call.getParcelable("account_avatar"));
    }

    String queryProviderAuthority() {
        List<ResolveInfo> queryIntentContentProviders = this.mContext.getPackageManager().queryIntentContentProviders(INTENT_GET_ACCOUNT_DATA, 1048576);
        if (queryIntentContentProviders.size() == 1) {
            return queryIntentContentProviders.get(0).providerInfo.authority;
        }
        Log.w("AvatarViewMixin", "The size of the provider is " + queryIntentContentProviders.size());
        return null;
    }
}
