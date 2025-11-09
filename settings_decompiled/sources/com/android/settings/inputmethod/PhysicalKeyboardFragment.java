package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.InputDevice;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.util.Preconditions;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Settings;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.inputmethod.KeyboardLayoutDialogFragment;
import com.android.settings.inputmethod.PhysicalKeyboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PhysicalKeyboardFragment extends SettingsPreferenceFragment implements InputManager.InputDeviceListener, KeyboardLayoutDialogFragment.OnSetupKeyboardLayoutsListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.physical_keyboard_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private InputManager mIm;
    private Intent mIntentWaitingForResult;
    private PreferenceCategory mKeyboardAssistanceCategory;
    private SwitchPreference mShowVirtualKeyboardSwitch;
    private final ArrayList<HardKeyboardDeviceInfo> mLastHardKeyboards = new ArrayList<>();
    private final Preference.OnPreferenceChangeListener mShowVirtualKeyboardSwitchPreferenceChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment$$ExternalSyntheticLambda0
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public final boolean onPreferenceChange(Preference preference, Object obj) {
            boolean lambda$new$3;
            lambda$new$3 = PhysicalKeyboardFragment.this.lambda$new$3(preference, obj);
            return lambda$new$3;
        }
    };
    private final ContentObserver mContentObserver = new ContentObserver(new Handler(true)) { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            PhysicalKeyboardFragment.this.updateShowVirtualKeyboardSwitch();
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 346;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R$xml.physical_keyboard_settings);
        this.mIm = (InputManager) Preconditions.checkNotNull((InputManager) ((Activity) Preconditions.checkNotNull(getActivity())).getSystemService(InputManager.class));
        PreferenceCategory preferenceCategory = (PreferenceCategory) Preconditions.checkNotNull((PreferenceCategory) findPreference("keyboard_assistance_category"));
        this.mKeyboardAssistanceCategory = preferenceCategory;
        this.mShowVirtualKeyboardSwitch = (SwitchPreference) Preconditions.checkNotNull((SwitchPreference) preferenceCategory.findPreference("show_virtual_keyboard_switch"));
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("keyboard_shortcuts_helper".equals(preference.getKey())) {
            writePreferenceClickMetric(preference);
            toggleKeyboardShortcutsMenu();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mLastHardKeyboards.clear();
        scheduleUpdateHardKeyboards();
        this.mIm.registerInputDeviceListener(this, null);
        this.mShowVirtualKeyboardSwitch.setOnPreferenceChangeListener(this.mShowVirtualKeyboardSwitchPreferenceChangeListener);
        registerShowVirtualKeyboardSettingsObserver();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mLastHardKeyboards.clear();
        this.mIm.unregisterInputDeviceListener(this);
        this.mShowVirtualKeyboardSwitch.setOnPreferenceChangeListener(null);
        unregisterShowVirtualKeyboardSettingsObserver();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        scheduleUpdateHardKeyboards();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        scheduleUpdateHardKeyboards();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        scheduleUpdateHardKeyboards();
    }

    private void scheduleUpdateHardKeyboards() {
        final Context context = getContext();
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PhysicalKeyboardFragment.this.lambda$scheduleUpdateHardKeyboards$1(context);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdateHardKeyboards$1(Context context) {
        final List<HardKeyboardDeviceInfo> hardKeyboards = getHardKeyboards(context);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                PhysicalKeyboardFragment.this.lambda$scheduleUpdateHardKeyboards$0(hardKeyboards);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateHardKeyboards */
    public void lambda$scheduleUpdateHardKeyboards$0(List<HardKeyboardDeviceInfo> list) {
        if (Objects.equals(this.mLastHardKeyboards, list)) {
            return;
        }
        this.mLastHardKeyboards.clear();
        this.mLastHardKeyboards.addAll(list);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setTitle(R$string.builtin_keyboard_settings_title);
        preferenceCategory.setOrder(0);
        preferenceScreen.addPreference(preferenceCategory);
        for (final HardKeyboardDeviceInfo hardKeyboardDeviceInfo : list) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(hardKeyboardDeviceInfo.mDeviceName);
            preference.setSummary(hardKeyboardDeviceInfo.mLayoutLabel);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment$$ExternalSyntheticLambda4
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    boolean lambda$updateHardKeyboards$2;
                    lambda$updateHardKeyboards$2 = PhysicalKeyboardFragment.this.lambda$updateHardKeyboards$2(hardKeyboardDeviceInfo, preference2);
                    return lambda$updateHardKeyboards$2;
                }
            });
            preferenceCategory.addPreference(preference);
        }
        this.mKeyboardAssistanceCategory.setOrder(1);
        preferenceScreen.addPreference(this.mKeyboardAssistanceCategory);
        updateShowVirtualKeyboardSwitch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateHardKeyboards$2(HardKeyboardDeviceInfo hardKeyboardDeviceInfo, Preference preference) {
        showKeyboardLayoutDialog(hardKeyboardDeviceInfo.mDeviceIdentifier);
        return true;
    }

    private void showKeyboardLayoutDialog(InputDeviceIdentifier inputDeviceIdentifier) {
        KeyboardLayoutDialogFragment keyboardLayoutDialogFragment = new KeyboardLayoutDialogFragment(inputDeviceIdentifier);
        keyboardLayoutDialogFragment.setTargetFragment(this, 0);
        keyboardLayoutDialogFragment.show(getActivity().getSupportFragmentManager(), "keyboardLayout");
    }

    private void registerShowVirtualKeyboardSettingsObserver() {
        unregisterShowVirtualKeyboardSettingsObserver();
        getActivity().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("show_ime_with_hard_keyboard"), false, this.mContentObserver, UserHandle.myUserId());
        updateShowVirtualKeyboardSwitch();
    }

    private void unregisterShowVirtualKeyboardSettingsObserver() {
        getActivity().getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateShowVirtualKeyboardSwitch() {
        this.mShowVirtualKeyboardSwitch.setChecked(Settings.Secure.getInt(getContentResolver(), "show_ime_with_hard_keyboard", 0) != 0);
    }

    private void toggleKeyboardShortcutsMenu() {
        getActivity().requestShowKeyboardShortcuts();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3(Preference preference, Object obj) {
        Settings.Secure.putInt(getContentResolver(), "show_ime_with_hard_keyboard", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    @Override // com.android.settings.inputmethod.KeyboardLayoutDialogFragment.OnSetupKeyboardLayoutsListener
    public void onSetupKeyboardLayouts(InputDeviceIdentifier inputDeviceIdentifier) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(getActivity(), Settings.KeyboardLayoutPickerActivity.class);
        intent.putExtra("input_device_identifier", (Parcelable) inputDeviceIdentifier);
        this.mIntentWaitingForResult = intent;
        startActivityForResult(intent, 0);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Intent intent2 = this.mIntentWaitingForResult;
        if (intent2 != null) {
            this.mIntentWaitingForResult = null;
            showKeyboardLayoutDialog((InputDeviceIdentifier) intent2.getParcelableExtra("input_device_identifier"));
        }
    }

    private static String getLayoutLabel(InputDevice inputDevice, Context context, InputManager inputManager) {
        String currentKeyboardLayoutForInputDevice = inputManager.getCurrentKeyboardLayoutForInputDevice(inputDevice.getIdentifier());
        if (currentKeyboardLayoutForInputDevice == null) {
            return context.getString(R$string.keyboard_layout_default_label);
        }
        KeyboardLayout keyboardLayout = inputManager.getKeyboardLayout(currentKeyboardLayoutForInputDevice);
        if (keyboardLayout == null) {
            return context.getString(R$string.keyboard_layout_default_label);
        }
        return TextUtils.emptyIfNull(keyboardLayout.getLabel());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<HardKeyboardDeviceInfo> getHardKeyboards(Context context) {
        ArrayList arrayList = new ArrayList();
        InputManager inputManager = (InputManager) context.getSystemService(InputManager.class);
        if (inputManager == null) {
            return new ArrayList();
        }
        for (int i : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(i);
            if (device != null && !device.isVirtual() && device.isFullKeyboard()) {
                arrayList.add(new HardKeyboardDeviceInfo(device.getName(), device.getIdentifier(), getLayoutLabel(device, context, inputManager)));
            }
        }
        final Collator collator = Collator.getInstance();
        arrayList.sort(new Comparator() { // from class: com.android.settings.inputmethod.PhysicalKeyboardFragment$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$getHardKeyboards$4;
                lambda$getHardKeyboards$4 = PhysicalKeyboardFragment.lambda$getHardKeyboards$4(collator, (PhysicalKeyboardFragment.HardKeyboardDeviceInfo) obj, (PhysicalKeyboardFragment.HardKeyboardDeviceInfo) obj2);
                return lambda$getHardKeyboards$4;
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getHardKeyboards$4(Collator collator, HardKeyboardDeviceInfo hardKeyboardDeviceInfo, HardKeyboardDeviceInfo hardKeyboardDeviceInfo2) {
        int compare = collator.compare(hardKeyboardDeviceInfo.mDeviceName, hardKeyboardDeviceInfo2.mDeviceName);
        if (compare != 0) {
            return compare;
        }
        int compareTo = hardKeyboardDeviceInfo.mDeviceIdentifier.getDescriptor().compareTo(hardKeyboardDeviceInfo2.mDeviceIdentifier.getDescriptor());
        return compareTo != 0 ? compareTo : collator.compare(hardKeyboardDeviceInfo.mLayoutLabel, hardKeyboardDeviceInfo2.mLayoutLabel);
    }

    /* loaded from: classes.dex */
    public static final class HardKeyboardDeviceInfo {
        public final InputDeviceIdentifier mDeviceIdentifier;
        public final String mDeviceName;
        public final String mLayoutLabel;

        public HardKeyboardDeviceInfo(String str, InputDeviceIdentifier inputDeviceIdentifier, String str2) {
            this.mDeviceName = TextUtils.emptyIfNull(str);
            this.mDeviceIdentifier = inputDeviceIdentifier;
            this.mLayoutLabel = str2;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj != null && (obj instanceof HardKeyboardDeviceInfo)) {
                HardKeyboardDeviceInfo hardKeyboardDeviceInfo = (HardKeyboardDeviceInfo) obj;
                return TextUtils.equals(this.mDeviceName, hardKeyboardDeviceInfo.mDeviceName) && Objects.equals(this.mDeviceIdentifier, hardKeyboardDeviceInfo.mDeviceIdentifier) && TextUtils.equals(this.mLayoutLabel, hardKeyboardDeviceInfo.mLayoutLabel);
            }
            return false;
        }
    }
}
