package com.android.settings.applications.intentpicker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainOwner;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$string;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SupportedLinkWrapper implements Comparable {
    private String mHost;
    private SortedSet<DomainOwner> mOwnerSet;
    private boolean mIsEnabled = true;
    private String mLastOwnerName = "";
    private boolean mIsChecked = false;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getLastPackageLabel$3(String str) {
        return str != null;
    }

    public SupportedLinkWrapper(Context context, String str, SortedSet<DomainOwner> sortedSet) {
        this.mHost = str;
        this.mOwnerSet = sortedSet;
        init(context);
    }

    private void init(Context context) {
        if (this.mOwnerSet.size() > 0) {
            int i = (this.mOwnerSet.stream().filter(new Predicate() { // from class: com.android.settings.applications.intentpicker.SupportedLinkWrapper$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$init$0;
                    lambda$init$0 = SupportedLinkWrapper.lambda$init$0((DomainOwner) obj);
                    return lambda$init$0;
                }
            }).count() > 0L ? 1 : (this.mOwnerSet.stream().filter(new Predicate() { // from class: com.android.settings.applications.intentpicker.SupportedLinkWrapper$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$init$0;
                    lambda$init$0 = SupportedLinkWrapper.lambda$init$0((DomainOwner) obj);
                    return lambda$init$0;
                }
            }).count() == 0L ? 0 : -1));
            this.mIsEnabled = i == 0;
            if (i > 0) {
                this.mLastOwnerName = getLastPackageLabel(context, false);
            } else {
                this.mLastOwnerName = getLastPackageLabel(context, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$init$0(DomainOwner domainOwner) {
        return !domainOwner.isOverrideable();
    }

    private String getLastPackageLabel(final Context context, final boolean z) {
        List list = (List) this.mOwnerSet.stream().filter(new Predicate() { // from class: com.android.settings.applications.intentpicker.SupportedLinkWrapper$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getLastPackageLabel$1;
                lambda$getLastPackageLabel$1 = SupportedLinkWrapper.lambda$getLastPackageLabel$1(z, (DomainOwner) obj);
                return lambda$getLastPackageLabel$1;
            }
        }).map(new Function() { // from class: com.android.settings.applications.intentpicker.SupportedLinkWrapper$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$getLastPackageLabel$2;
                lambda$getLastPackageLabel$2 = SupportedLinkWrapper.this.lambda$getLastPackageLabel$2(context, (DomainOwner) obj);
                return lambda$getLastPackageLabel$2;
            }
        }).filter(new Predicate() { // from class: com.android.settings.applications.intentpicker.SupportedLinkWrapper$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getLastPackageLabel$3;
                lambda$getLastPackageLabel$3 = SupportedLinkWrapper.lambda$getLastPackageLabel$3((String) obj);
                return lambda$getLastPackageLabel$3;
            }
        }).collect(Collectors.toList());
        return (String) list.get(list.size() - 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getLastPackageLabel$1(boolean z, DomainOwner domainOwner) {
        return domainOwner.isOverrideable() == z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getLastPackageLabel$2(Context context, DomainOwner domainOwner) {
        return getLabel(context, domainOwner.getPackageName());
    }

    private String getLabel(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationInfo(str, 0).loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("SupportedLinkWrapper", "getLabel error : " + e.getMessage());
            return null;
        }
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public String getDisplayTitle(Context context) {
        if (TextUtils.isEmpty(this.mLastOwnerName) || context == null) {
            return this.mHost;
        }
        return this.mHost + System.lineSeparator() + context.getString(R$string.app_launch_supported_links_subtext, this.mLastOwnerName);
    }

    public String getHost() {
        return this.mHost;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public void setChecked(boolean z) {
        this.mIsChecked = z;
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        SupportedLinkWrapper supportedLinkWrapper = (SupportedLinkWrapper) obj;
        boolean z = this.mIsEnabled;
        if (z != supportedLinkWrapper.mIsEnabled) {
            return z ? -1 : 1;
        } else if (TextUtils.isEmpty(this.mLastOwnerName) != TextUtils.isEmpty(supportedLinkWrapper.mLastOwnerName)) {
            return TextUtils.isEmpty(this.mLastOwnerName) ? -1 : 1;
        } else {
            return 0;
        }
    }
}
