package com.android.settings.development.autofill;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.autofill.AutofillManager;
import com.android.settings.development.autofill.AutofillCategoryController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class AutofillCategoryController extends DeveloperOptionsPreferenceController implements LifecycleObserver, OnStart, OnStop {
    private ContentResolver mContentResolver;
    private final Handler mHandler;
    private ContentObserver mSettingsObserver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "debug_autofill_category";
    }

    public AutofillCategoryController(Context context, Lifecycle lifecycle) {
        super(context);
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mSettingsObserver = new AnonymousClass1(handler);
        this.mContentResolver = context.getContentResolver();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.development.autofill.AutofillCategoryController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 extends ContentObserver {
        AnonymousClass1(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            AutofillCategoryController.this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.development.autofill.AutofillCategoryController$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AutofillCategoryController.AnonymousClass1.this.lambda$onChange$0();
                }
            }, 2000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChange$0() {
            ((DeveloperOptionsPreferenceController) AutofillCategoryController.this).mPreference.notifyDependencyChange(AutofillCategoryController.this.shouldDisableDependents());
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("autofill_service"), false, this.mSettingsObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
    }

    private boolean isAutofillEnabled() {
        AutofillManager autofillManager = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
        boolean z = autofillManager != null && autofillManager.isEnabled();
        Log.v("AutofillCategoryController", "isAutofillEnabled(): " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldDisableDependents() {
        boolean z = !isAutofillEnabled();
        Log.v("AutofillCategoryController", "shouldDisableDependents(): " + z);
        return z;
    }
}
