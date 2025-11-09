package com.android.settings.deviceinfo.legal;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageManager;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.ArrayUtils;
import com.android.settings.core.BasePreferenceController;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes.dex */
public class ModuleLicensesPreferenceController extends BasePreferenceController {
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

    public ModuleLicensesPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        List<ModuleInfo> installedModules = this.mContext.getPackageManager().getInstalledModules(0);
        final PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        installedModules.stream().sorted(Comparator.comparing(new Function() { // from class: com.android.settings.deviceinfo.legal.ModuleLicensesPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$displayPreference$0;
                lambda$displayPreference$0 = ModuleLicensesPreferenceController.lambda$displayPreference$0((ModuleInfo) obj);
                return lambda$displayPreference$0;
            }
        })).filter(new Predicate(this.mContext)).forEach(new Consumer() { // from class: com.android.settings.deviceinfo.legal.ModuleLicensesPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ModuleLicensesPreferenceController.lambda$displayPreference$1(PreferenceGroup.this, (ModuleInfo) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$displayPreference$0(ModuleInfo moduleInfo) {
        return moduleInfo.getName().toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$displayPreference$1(PreferenceGroup preferenceGroup, ModuleInfo moduleInfo) {
        preferenceGroup.addPreference(new ModuleLicensePreference(preferenceGroup.getContext(), moduleInfo));
    }

    /* loaded from: classes.dex */
    static class Predicate implements java.util.function.Predicate<ModuleInfo> {
        private final Context mContext;

        public Predicate(Context context) {
            this.mContext = context;
        }

        @Override // java.util.function.Predicate
        public boolean test(ModuleInfo moduleInfo) {
            try {
                return ArrayUtils.contains(ModuleLicenseProvider.getPackageAssetManager(this.mContext.getPackageManager(), moduleInfo.getPackageName()).list(""), "NOTICE.html.gz");
            } catch (PackageManager.NameNotFoundException | IOException unused) {
                return false;
            }
        }
    }
}
