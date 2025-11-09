package com.android.settings.applications.managedomainurls;

import android.content.Context;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.graphics.drawable.Drawable;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.applications.intentpicker.IntentPickerUtils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AppPreference;
/* loaded from: classes.dex */
public class DomainAppPreference extends AppPreference {
    private Drawable mCacheIcon;
    private final DomainVerificationManager mDomainVerificationManager;
    private final ApplicationsState.AppEntry mEntry;

    public DomainAppPreference(Context context, ApplicationsState.AppEntry appEntry) {
        super(context);
        this.mDomainVerificationManager = (DomainVerificationManager) context.getSystemService(DomainVerificationManager.class);
        this.mEntry = appEntry;
        appEntry.ensureLabel(getContext());
        this.mCacheIcon = AppUtils.getIconFromCache(appEntry);
        setState();
    }

    public void reuse() {
        setState();
        notifyChanged();
    }

    public ApplicationsState.AppEntry getEntry() {
        return this.mEntry;
    }

    private void setState() {
        setTitle(this.mEntry.label);
        Drawable drawable = this.mCacheIcon;
        if (drawable != null) {
            setIcon(drawable);
        } else {
            setIcon(R$drawable.empty_icon);
        }
        setSummary(getDomainsSummary(this.mEntry.info.packageName));
    }

    private CharSequence getDomainsSummary(String str) {
        return getContext().getText(isLinkHandlingAllowed(str) ? R$string.app_link_open_always : R$string.app_link_open_never);
    }

    private boolean isLinkHandlingAllowed(String str) {
        DomainVerificationUserState domainVerificationUserState = IntentPickerUtils.getDomainVerificationUserState(this.mDomainVerificationManager, str);
        if (domainVerificationUserState == null) {
            return false;
        }
        return domainVerificationUserState.isLinkHandlingAllowed();
    }

    @Override // com.android.settingslib.widget.AppPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mCacheIcon == null) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.applications.managedomainurls.DomainAppPreference$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DomainAppPreference.this.lambda$onBindViewHolder$1();
                }
            });
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1() {
        final Drawable icon = AppUtils.getIcon(getContext(), this.mEntry);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.managedomainurls.DomainAppPreference$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DomainAppPreference.this.lambda$onBindViewHolder$0(icon);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(Drawable drawable) {
        setIcon(drawable);
        this.mCacheIcon = drawable;
    }
}
