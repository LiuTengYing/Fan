package com.android.settings.applications.defaultapps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.internal.content.PackageMonitor;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DefaultAutofillPicker extends DefaultAppPickerFragment {
    static final Intent AUTOFILL_PROBE = new Intent("android.service.autofill.AutofillService");
    private DialogInterface.OnClickListener mCancelListener;
    private final PackageMonitor mSettingsPackageMonitor = new AnonymousClass1();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 792;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean shouldShowItemNone() {
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final FragmentActivity activity = getActivity();
        if (activity != null && activity.getIntent().getStringExtra("package_name") != null) {
            this.mCancelListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.defaultapps.DefaultAutofillPicker$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DefaultAutofillPicker.lambda$onCreate$0(activity, dialogInterface, i);
                }
            };
            this.mUserId = UserHandle.myUserId();
        }
        this.mSettingsPackageMonitor.register(activity, activity.getMainLooper(), false);
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreate$0(Activity activity, DialogInterface dialogInterface, int i) {
        activity.setResult(0);
        activity.finish();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    protected DefaultAppPickerFragment.ConfirmationDialogFragment newConfirmationDialogFragment(String str, CharSequence charSequence) {
        AutofillPickerConfirmationDialogFragment autofillPickerConfirmationDialogFragment = new AutofillPickerConfirmationDialogFragment();
        autofillPickerConfirmationDialogFragment.init(this, str, charSequence);
        return autofillPickerConfirmationDialogFragment;
    }

    /* loaded from: classes.dex */
    public static class AutofillPickerConfirmationDialogFragment extends DefaultAppPickerFragment.ConfirmationDialogFragment {
        @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            setCancelListener(((DefaultAutofillPicker) getTargetFragment()).mCancelListener);
            super.onCreate(bundle);
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    protected int getPreferenceScreenResId() {
        return R$xml.default_autofill_settings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.applications.defaultapps.DefaultAutofillPicker$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 extends PackageMonitor {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPackageAdded$0() {
            DefaultAutofillPicker.this.update();
        }

        public void onPackageAdded(String str, int i) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.defaultapps.DefaultAutofillPicker$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DefaultAutofillPicker.AnonymousClass1.this.lambda$onPackageAdded$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPackageModified$1() {
            DefaultAutofillPicker.this.update();
        }

        public void onPackageModified(String str) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.defaultapps.DefaultAutofillPicker$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DefaultAutofillPicker.AnonymousClass1.this.lambda$onPackageModified$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPackageRemoved$2() {
            DefaultAutofillPicker.this.update();
        }

        public void onPackageRemoved(String str, int i) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.defaultapps.DefaultAutofillPicker$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DefaultAutofillPicker.AnonymousClass1.this.lambda$onPackageRemoved$2();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        updateCandidates();
        addAddServicePreference();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mSettingsPackageMonitor.unregister();
        super.onDestroy();
    }

    private Preference newAddServicePreferenceOrNull() {
        String stringForUser = Settings.Secure.getStringForUser(getActivity().getContentResolver(), "autofill_service_search_uri", this.mUserId);
        if (TextUtils.isEmpty(stringForUser)) {
            return null;
        }
        final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringForUser));
        final Context prefContext = getPrefContext();
        Preference preference = new Preference(prefContext);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.defaultapps.DefaultAutofillPicker$$ExternalSyntheticLambda1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$newAddServicePreferenceOrNull$1;
                lambda$newAddServicePreferenceOrNull$1 = DefaultAutofillPicker.this.lambda$newAddServicePreferenceOrNull$1(prefContext, intent, preference2);
                return lambda$newAddServicePreferenceOrNull$1;
            }
        });
        preference.setTitle(R$string.print_menu_item_add_service);
        preference.setIcon(R$drawable.ic_add_24dp);
        preference.setOrder(2147483646);
        preference.setPersistent(false);
        return preference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$newAddServicePreferenceOrNull$1(Context context, Intent intent, Preference preference) {
        context.startActivityAsUser(intent, UserHandle.of(this.mUserId));
        return true;
    }

    private void addAddServicePreference() {
        Preference newAddServicePreferenceOrNull = newAddServicePreferenceOrNull();
        if (newAddServicePreferenceOrNull != null) {
            getPreferenceScreen().addPreference(newAddServicePreferenceOrNull);
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentServicesAsUser = this.mPm.queryIntentServicesAsUser(AUTOFILL_PROBE, 128, this.mUserId);
        Context context = getContext();
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            String str = resolveInfo.serviceInfo.permission;
            if ("android.permission.BIND_AUTOFILL_SERVICE".equals(str)) {
                PackageManager packageManager = this.mPm;
                int i = this.mUserId;
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                arrayList.add(new DefaultAppInfo(context, packageManager, i, new ComponentName(serviceInfo.packageName, serviceInfo.name)));
            }
            if ("android.permission.BIND_AUTOFILL".equals(str)) {
                Log.w("DefaultAutofillPicker", "AutofillService from '" + resolveInfo.serviceInfo.packageName + "' uses unsupported permission android.permission.BIND_AUTOFILL. It works for now, but might not be supported on future releases");
                PackageManager packageManager2 = this.mPm;
                int i2 = this.mUserId;
                ServiceInfo serviceInfo2 = resolveInfo.serviceInfo;
                arrayList.add(new DefaultAppInfo(context, packageManager2, i2, new ComponentName(serviceInfo2.packageName, serviceInfo2.name)));
            }
        }
        return arrayList;
    }

    public static String getDefaultKey(Context context, int i) {
        ComponentName unflattenFromString;
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "autofill_service", i);
        if (stringForUser == null || (unflattenFromString = ComponentName.unflattenFromString(stringForUser)) == null) {
            return null;
        }
        return unflattenFromString.flattenToString();
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return getDefaultKey(getContext(), this.mUserId);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    protected CharSequence getConfirmationMessage(CandidateInfo candidateInfo) {
        if (candidateInfo == null) {
            return null;
        }
        return Html.fromHtml(getContext().getString(R$string.autofill_confirmation_message, candidateInfo.loadLabel()));
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        String stringExtra;
        Settings.Secure.putStringForUser(getContext().getContentResolver(), "autofill_service", str, this.mUserId);
        FragmentActivity activity = getActivity();
        if (activity == null || (stringExtra = activity.getIntent().getStringExtra("package_name")) == null) {
            return true;
        }
        activity.setResult((str == null || !str.startsWith(stringExtra)) ? 0 : -1);
        activity.finish();
        return true;
    }

    /* loaded from: classes.dex */
    static final class AutofillSettingIntentProvider {
        private final Context mContext;
        private final String mSelectedKey;
        private final int mUserId;

        public AutofillSettingIntentProvider(Context context, int i, String str) {
            this.mSelectedKey = str;
            this.mContext = context;
            this.mUserId = i;
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0049, code lost:
            return null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:12:0x005c, code lost:
            return new android.content.Intent("android.intent.action.MAIN").setComponent(new android.content.ComponentName(r1.packageName, r6));
         */
        /* JADX WARN: Code restructure failed: missing block: B:7:0x0038, code lost:
            r6 = new android.service.autofill.AutofillServiceInfo(r6.mContext, r1).getSettingsActivity();
         */
        /* JADX WARN: Code restructure failed: missing block: B:9:0x0047, code lost:
            if (android.text.TextUtils.isEmpty(r6) == false) goto L12;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public android.content.Intent getIntent() {
            /*
                r6 = this;
                android.content.Context r0 = r6.mContext
                android.content.pm.PackageManager r0 = r0.getPackageManager()
                android.content.Intent r1 = com.android.settings.applications.defaultapps.DefaultAutofillPicker.AUTOFILL_PROBE
                int r2 = r6.mUserId
                r3 = 128(0x80, float:1.794E-43)
                java.util.List r0 = r0.queryIntentServicesAsUser(r1, r3, r2)
                java.util.Iterator r0 = r0.iterator()
            L14:
                boolean r1 = r0.hasNext()
                r2 = 0
                if (r1 == 0) goto L7c
                java.lang.Object r1 = r0.next()
                android.content.pm.ResolveInfo r1 = (android.content.pm.ResolveInfo) r1
                android.content.pm.ServiceInfo r1 = r1.serviceInfo
                android.content.ComponentName r3 = new android.content.ComponentName
                java.lang.String r4 = r1.packageName
                java.lang.String r5 = r1.name
                r3.<init>(r4, r5)
                java.lang.String r3 = r3.flattenToString()
                java.lang.String r4 = r6.mSelectedKey
                boolean r3 = android.text.TextUtils.equals(r4, r3)
                if (r3 == 0) goto L14
                android.service.autofill.AutofillServiceInfo r0 = new android.service.autofill.AutofillServiceInfo     // Catch: java.lang.SecurityException -> L5d
                android.content.Context r6 = r6.mContext     // Catch: java.lang.SecurityException -> L5d
                r0.<init>(r6, r1)     // Catch: java.lang.SecurityException -> L5d
                java.lang.String r6 = r0.getSettingsActivity()     // Catch: java.lang.SecurityException -> L5d
                boolean r0 = android.text.TextUtils.isEmpty(r6)
                if (r0 == 0) goto L4a
                return r2
            L4a:
                android.content.Intent r0 = new android.content.Intent
                java.lang.String r2 = "android.intent.action.MAIN"
                r0.<init>(r2)
                android.content.ComponentName r2 = new android.content.ComponentName
                java.lang.String r1 = r1.packageName
                r2.<init>(r1, r6)
                android.content.Intent r6 = r0.setComponent(r2)
                return r6
            L5d:
                r6 = move-exception
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "Error getting info for "
                r0.append(r3)
                r0.append(r1)
                java.lang.String r1 = ": "
                r0.append(r1)
                r0.append(r6)
                java.lang.String r6 = r0.toString()
                java.lang.String r0 = "DefaultAutofillPicker"
                android.util.Log.w(r0, r6)
            L7c:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.defaultapps.DefaultAutofillPicker.AutofillSettingIntentProvider.getIntent():android.content.Intent");
        }
    }
}
