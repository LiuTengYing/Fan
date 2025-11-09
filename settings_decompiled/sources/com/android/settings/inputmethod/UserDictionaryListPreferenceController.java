package com.android.settings.inputmethod;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
/* loaded from: classes.dex */
public class UserDictionaryListPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart {
    public static final String USER_DICTIONARY_SETTINGS_INTENT_ACTION = "android.settings.USER_DICTIONARY_SETTINGS";
    private final String KEY_ALL_LANGUAGE;
    private String mLocale;
    private PreferenceScreen mScreen;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public UserDictionaryListPreferenceController(Context context, String str) {
        super(context, str);
        this.KEY_ALL_LANGUAGE = "all_languages";
    }

    public void setLocale(String str) {
        this.mLocale = str;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.setOrderingAsAdded(false);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        createUserDictSettings();
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0050  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0087  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.TreeSet<java.lang.String> getUserDictionaryLocalesSet(android.content.Context r7) {
        /*
            android.content.ContentResolver r0 = r7.getContentResolver()
            android.net.Uri r1 = android.provider.UserDictionary.Words.CONTENT_URI
            java.lang.String r6 = "locale"
            java.lang.String[] r2 = new java.lang.String[]{r6}
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5)
            java.util.TreeSet r1 = new java.util.TreeSet
            r1.<init>()
            if (r0 != 0) goto L1b
            return r1
        L1b:
            boolean r2 = r0.moveToFirst()     // Catch: java.lang.Throwable -> L93
            if (r2 == 0) goto L37
            int r2 = r0.getColumnIndex(r6)     // Catch: java.lang.Throwable -> L93
        L25:
            java.lang.String r3 = r0.getString(r2)     // Catch: java.lang.Throwable -> L93
            if (r3 == 0) goto L2c
            goto L2e
        L2c:
            java.lang.String r3 = ""
        L2e:
            r1.add(r3)     // Catch: java.lang.Throwable -> L93
            boolean r3 = r0.moveToNext()     // Catch: java.lang.Throwable -> L93
            if (r3 != 0) goto L25
        L37:
            r0.close()
            java.lang.String r0 = "input_method"
            java.lang.Object r7 = r7.getSystemService(r0)
            android.view.inputmethod.InputMethodManager r7 = (android.view.inputmethod.InputMethodManager) r7
            java.util.List r0 = r7.getEnabledInputMethodList()
            java.util.Iterator r0 = r0.iterator()
        L4a:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L79
            java.lang.Object r2 = r0.next()
            android.view.inputmethod.InputMethodInfo r2 = (android.view.inputmethod.InputMethodInfo) r2
            r3 = 1
            java.util.List r2 = r7.getEnabledInputMethodSubtypeList(r2, r3)
            java.util.Iterator r2 = r2.iterator()
        L5f:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L4a
            java.lang.Object r3 = r2.next()
            android.view.inputmethod.InputMethodSubtype r3 = (android.view.inputmethod.InputMethodSubtype) r3
            java.lang.String r3 = r3.getLocale()
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L5f
            r1.add(r3)
            goto L5f
        L79:
            java.util.Locale r7 = java.util.Locale.getDefault()
            java.lang.String r7 = r7.getLanguage()
            boolean r7 = r1.contains(r7)
            if (r7 != 0) goto L92
            java.util.Locale r7 = java.util.Locale.getDefault()
            java.lang.String r7 = r7.toString()
            r1.add(r7)
        L92:
            return r1
        L93:
            r7 = move-exception
            r0.close()
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.inputmethod.UserDictionaryListPreferenceController.getUserDictionaryLocalesSet(android.content.Context):java.util.TreeSet");
    }

    TreeSet<String> getUserDictLocalesSet(Context context) {
        return getUserDictionaryLocalesSet(context);
    }

    private void createUserDictSettings() {
        TreeSet<String> userDictLocalesSet = getUserDictLocalesSet(this.mContext);
        int preferenceCount = this.mScreen.getPreferenceCount();
        String str = this.mLocale;
        if (str != null) {
            userDictLocalesSet.add(str);
        }
        if (userDictLocalesSet.size() > 1) {
            userDictLocalesSet.add("");
        }
        if (preferenceCount > 0) {
            for (int i = preferenceCount - 1; i >= 0; i--) {
                String key = this.mScreen.getPreference(i).getKey();
                if (!TextUtils.isEmpty(key) && !TextUtils.equals("all_languages", key)) {
                    if (!userDictLocalesSet.isEmpty() && userDictLocalesSet.contains(key)) {
                        userDictLocalesSet.remove(key);
                    } else {
                        PreferenceScreen preferenceScreen = this.mScreen;
                        preferenceScreen.removePreference(preferenceScreen.findPreference(key));
                    }
                }
            }
        }
        if (userDictLocalesSet.isEmpty() && preferenceCount == 0) {
            this.mScreen.addPreference(createUserDictionaryPreference(null));
            return;
        }
        Iterator<String> it = userDictLocalesSet.iterator();
        while (it.hasNext()) {
            Preference createUserDictionaryPreference = createUserDictionaryPreference(it.next());
            if (this.mScreen.findPreference(createUserDictionaryPreference.getKey()) == null) {
                this.mScreen.addPreference(createUserDictionaryPreference);
            }
        }
    }

    private Preference createUserDictionaryPreference(String str) {
        Preference preference = new Preference(this.mScreen.getContext());
        Intent intent = new Intent(USER_DICTIONARY_SETTINGS_INTENT_ACTION);
        if (str == null) {
            preference.setTitle(Locale.getDefault().getDisplayName());
            preference.setKey(Locale.getDefault().toString());
        } else {
            if (TextUtils.isEmpty(str)) {
                preference.setTitle(this.mContext.getString(R$string.user_dict_settings_all_languages));
                preference.setKey("all_languages");
                preference.setOrder(0);
            } else {
                preference.setTitle(Utils.createLocaleFromString(str).getDisplayName());
                preference.setKey(str);
            }
            intent.putExtra("locale", str);
            preference.getExtras().putString("locale", str);
        }
        preference.setIntent(intent);
        preference.setFragment(UserDictionarySettings.class.getName());
        return preference;
    }
}
