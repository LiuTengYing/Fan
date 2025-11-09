package com.android.settings.inputmethod;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$xml;
import com.android.settings.SettingsApplication;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtilCompat;
import com.android.settingslib.inputmethod.InputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class AvailableVirtualKeyboardFragment extends DashboardFragment implements InputMethodPreference.OnSavePreferenceListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.inputmethod.AvailableVirtualKeyboardFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.available_virtual_keyboard;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };
    @VisibleForTesting
    final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList<>();
    @VisibleForTesting
    InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private Context mUserAwareContext;
    private int mUserId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AvailableVirtualKeyboardFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 347;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R$xml.available_virtual_keyboard);
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(this.mUserAwareContext);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt("profile");
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        int myUserId = UserHandle.myUserId();
        if (i == 1) {
            UserHandle userHandle = userManager.getPrimaryUser().getUserHandle();
            myUserId = userHandle.getIdentifier();
            context = context.createContextAsUser(userHandle, 0);
        } else if (i == 2) {
            if (!userManager.isManagedProfile()) {
                myUserId = Utils.getManagedProfileId(userManager, myUserId);
            }
            context = context.createContextAsUser(UserHandle.of(myUserId), 0);
        }
        this.mUserId = myUserId;
        this.mUserAwareContext = context;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        updateInputMethodPreferenceViews();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.available_virtual_keyboard;
    }

    @Override // com.android.settingslib.inputmethod.InputMethodPreference.OnSavePreferenceListener
    public void onSaveInputMethodPreference(InputMethodPreference inputMethodPreference) {
        InputMethodAndSubtypeUtilCompat.saveInputMethodSubtypeListForUser(this, this.mUserAwareContext.getContentResolver(), ((InputMethodManager) getContext().getSystemService(InputMethodManager.class)).getInputMethodListAsUser(this.mUserId), getResources().getConfiguration().keyboard == 2, this.mUserId);
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        Iterator<InputMethodPreference> it = this.mInputMethodPreferenceList.iterator();
        while (it.hasNext()) {
            it.next().updatePreferenceViews();
        }
    }

    @VisibleForTesting
    void updateInputMethodPreferenceViews() {
        InputMethodPreference inputMethodPreference;
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        this.mInputMethodPreferenceList.clear();
        List permittedInputMethods = ((DevicePolicyManager) this.mUserAwareContext.getSystemService(DevicePolicyManager.class)).getPermittedInputMethods();
        Context prefContext = getPrefContext();
        List<InputMethodInfo> inputMethodList = this.mInputMethodSettingValues.getInputMethodList();
        List enabledInputMethodListAsUser = ((InputMethodManager) getContext().getSystemService(InputMethodManager.class)).getEnabledInputMethodListAsUser(this.mUserId);
        int size = inputMethodList == null ? 0 : inputMethodList.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo inputMethodInfo = inputMethodList.get(i);
            boolean z = permittedInputMethods == null || permittedInputMethods.contains(inputMethodInfo.getPackageName()) || enabledInputMethodListAsUser.contains(inputMethodInfo);
            if (inputMethodInfo.getPackageName().equals("com.android.inputmethod.latin")) {
                inputMethodPreference = new InputMethodPreference(SettingsApplication.mApplication, inputMethodInfo, z, this, this.mUserId);
            } else {
                inputMethodPreference = new InputMethodPreference(prefContext, inputMethodInfo, z, this, this.mUserId);
            }
            Log.d("AvailableVirtualKeyboardFragment", "updateInputMethodPreferenceViews: " + inputMethodInfo.getPackageName());
            inputMethodPreference.setIcon(inputMethodInfo.loadIcon(this.mUserAwareContext.getPackageManager()));
            this.mInputMethodPreferenceList.add(inputMethodPreference);
        }
        final Collator collator = Collator.getInstance();
        this.mInputMethodPreferenceList.sort(new Comparator() { // from class: com.android.settings.inputmethod.AvailableVirtualKeyboardFragment$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$updateInputMethodPreferenceViews$0;
                lambda$updateInputMethodPreferenceViews$0 = AvailableVirtualKeyboardFragment.lambda$updateInputMethodPreferenceViews$0(collator, (InputMethodPreference) obj, (InputMethodPreference) obj2);
                return lambda$updateInputMethodPreferenceViews$0;
            }
        });
        getPreferenceScreen().removeAll();
        for (int i2 = 0; i2 < size; i2++) {
            InputMethodPreference inputMethodPreference2 = this.mInputMethodPreferenceList.get(i2);
            inputMethodPreference2.setOrder(i2);
            getPreferenceScreen().addPreference(inputMethodPreference2);
            InputMethodAndSubtypeUtilCompat.removeUnnecessaryNonPersistentPreference(inputMethodPreference2);
            inputMethodPreference2.updatePreferenceViews();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$updateInputMethodPreferenceViews$0(Collator collator, InputMethodPreference inputMethodPreference, InputMethodPreference inputMethodPreference2) {
        return inputMethodPreference.compareTo(inputMethodPreference2, collator);
    }
}
