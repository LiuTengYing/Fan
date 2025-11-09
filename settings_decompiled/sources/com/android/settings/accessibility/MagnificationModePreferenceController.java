package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.DialogCreatable;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.accessibility.ItemInfoArrayAdapter;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class MagnificationModePreferenceController extends BasePreferenceController implements DialogCreatable, LifecycleObserver, OnCreate, OnResume, OnSaveInstanceState {
    private static final int DIALOG_ID_BASE = 10;
    static final int DIALOG_MAGNIFICATION_MODE = 11;
    static final int DIALOG_MAGNIFICATION_TRIPLE_TAP_WARNING = 12;
    static final String EXTRA_MODE = "mode";
    static final String PREF_KEY = "screen_magnification_mode";
    private static final String TAG = "MagnificationModePreferenceController";
    private DialogHelper mDialogHelper;
    private ShortcutPreference mLinkPreference;
    ListView mMagnificationModesListView;
    private int mModeCache;
    private final List<MagnificationModeInfo> mModeInfos;
    private Preference mModePreference;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface DialogHelper extends DialogCreatable {
        void setDialogDelegate(DialogCreatable dialogCreatable);

        void showDialog(int i);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 11) {
            return i != 12 ? 0 : 1923;
        }
        return 1816;
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

    public MagnificationModePreferenceController(Context context, String str) {
        super(context, str);
        this.mModeCache = 0;
        this.mModeInfos = new ArrayList();
        initModeInfos();
    }

    private void initModeInfos() {
        this.mModeInfos.add(new MagnificationModeInfo(this.mContext.getText(R$string.accessibility_magnification_mode_dialog_option_full_screen), null, R$drawable.ic_illustration_fullscreen, 1));
        this.mModeInfos.add(new MagnificationModeInfo(this.mContext.getText(R$string.accessibility_magnification_mode_dialog_option_window), null, R$drawable.ic_illustration_window, 2));
        this.mModeInfos.add(new MagnificationModeInfo(this.mContext.getText(R$string.accessibility_magnification_mode_dialog_option_switch), this.mContext.getText(R$string.accessibility_magnification_area_settings_mode_switch_summary), R$drawable.ic_illustration_switch, 3));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return MagnificationCapabilities.getSummary(this.mContext, MagnificationCapabilities.getCapabilities(this.mContext));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            this.mModeCache = bundle.getInt(EXTRA_MODE, 0);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mModePreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mLinkPreference = (ShortcutPreference) preferenceScreen.findPreference("shortcut_preference");
        this.mModePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accessibility.MagnificationModePreferenceController$$ExternalSyntheticLambda4
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$displayPreference$0;
                lambda$displayPreference$0 = MagnificationModePreferenceController.this.lambda$displayPreference$0(preference);
                return lambda$displayPreference$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Preference preference) {
        this.mModeCache = MagnificationCapabilities.getCapabilities(this.mContext);
        this.mDialogHelper.showDialog(11);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(EXTRA_MODE, this.mModeCache);
    }

    public void setDialogHelper(DialogHelper dialogHelper) {
        this.mDialogHelper = dialogHelper;
        dialogHelper.setDialogDelegate(this);
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 11) {
            if (i != 12) {
                return null;
            }
            return createMagnificationTripleTapWarningDialog();
        }
        return createMagnificationModeDialog();
    }

    private Dialog createMagnificationModeDialog() {
        this.mMagnificationModesListView = AccessibilityDialogUtils.createSingleChoiceListView(this.mContext, this.mModeInfos, new AdapterView.OnItemClickListener() { // from class: com.android.settings.accessibility.MagnificationModePreferenceController$$ExternalSyntheticLambda2
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                MagnificationModePreferenceController.this.onMagnificationModeSelected(adapterView, view, i, j);
            }
        });
        this.mMagnificationModesListView.addHeaderView(LayoutInflater.from(this.mContext).inflate(R$layout.accessibility_magnification_mode_header, (ViewGroup) this.mMagnificationModesListView, false), null, false);
        this.mMagnificationModesListView.setItemChecked(computeSelectionIndex(), true);
        return AccessibilityDialogUtils.createCustomDialog(this.mContext, this.mContext.getString(R$string.accessibility_magnification_mode_dialog_title), this.mMagnificationModesListView, this.mContext.getString(R$string.save), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.MagnificationModePreferenceController$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MagnificationModePreferenceController.this.onMagnificationModeDialogPositiveButtonClicked(dialogInterface, i);
            }
        }, this.mContext.getString(R$string.cancel), null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMagnificationModeDialogPositiveButtonClicked(DialogInterface dialogInterface, int i) {
        int checkedItemPosition = this.mMagnificationModesListView.getCheckedItemPosition();
        if (checkedItemPosition == -1) {
            Log.w(TAG, "invalid index");
            return;
        }
        this.mModeCache = ((MagnificationModeInfo) this.mMagnificationModesListView.getItemAtPosition(checkedItemPosition)).mMagnificationMode;
        if (isTripleTapEnabled(this.mContext) && this.mModeCache != 1) {
            this.mDialogHelper.showDialog(12);
        } else {
            updateCapabilitiesAndSummary(this.mModeCache);
        }
    }

    private void updateCapabilitiesAndSummary(int i) {
        this.mModeCache = i;
        MagnificationCapabilities.setCapabilities(this.mContext, i);
        this.mModePreference.setSummary(MagnificationCapabilities.getSummary(this.mContext, this.mModeCache));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMagnificationModeSelected(AdapterView<?> adapterView, View view, int i, long j) {
        int i2 = ((MagnificationModeInfo) this.mMagnificationModesListView.getItemAtPosition(i)).mMagnificationMode;
        if (i2 == this.mModeCache) {
            return;
        }
        this.mModeCache = i2;
    }

    private int computeSelectionIndex() {
        int size = this.mModeInfos.size();
        for (int i = 0; i < size; i++) {
            if (this.mModeInfos.get(i).mMagnificationMode == this.mModeCache) {
                return i + this.mMagnificationModesListView.getHeaderViewsCount();
            }
        }
        Log.w(TAG, "computeSelectionIndex failed");
        return 0;
    }

    static boolean isTripleTapEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0) == 1;
    }

    private Dialog createMagnificationTripleTapWarningDialog() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.magnification_triple_tap_warning_dialog, (ViewGroup) null);
        Dialog createCustomDialog = AccessibilityDialogUtils.createCustomDialog(this.mContext, this.mContext.getString(R$string.accessibility_magnification_triple_tap_warning_title), inflate, this.mContext.getString(R$string.accessibility_magnification_triple_tap_warning_positive_button), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.MagnificationModePreferenceController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MagnificationModePreferenceController.this.onMagnificationTripleTapWarningDialogPositiveButtonClicked(dialogInterface, i);
            }
        }, this.mContext.getString(R$string.accessibility_magnification_triple_tap_warning_negative_button), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.MagnificationModePreferenceController$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MagnificationModePreferenceController.this.onMagnificationTripleTapWarningDialogNegativeButtonClicked(dialogInterface, i);
            }
        });
        updateLinkInTripleTapWarningDialog(createCustomDialog, inflate);
        return createCustomDialog;
    }

    private void updateLinkInTripleTapWarningDialog(final Dialog dialog, View view) {
        TextView textView = (TextView) view.findViewById(R$id.message);
        CharSequence linkify = AnnotationSpan.linkify(this.mContext.getText(R$string.accessibility_magnification_triple_tap_warning_message), new AnnotationSpan.LinkInfo("link", new View.OnClickListener() { // from class: com.android.settings.accessibility.MagnificationModePreferenceController$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MagnificationModePreferenceController.this.lambda$updateLinkInTripleTapWarningDialog$1(dialog, view2);
            }
        }));
        if (textView != null) {
            textView.setText(linkify);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        dialog.setContentView(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateLinkInTripleTapWarningDialog$1(Dialog dialog, View view) {
        updateCapabilitiesAndSummary(this.mModeCache);
        this.mLinkPreference.performClick();
        dialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMagnificationTripleTapWarningDialogNegativeButtonClicked(DialogInterface dialogInterface, int i) {
        this.mModeCache = MagnificationCapabilities.getCapabilities(this.mContext);
        this.mDialogHelper.showDialog(11);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMagnificationTripleTapWarningDialogPositiveButtonClicked(DialogInterface dialogInterface, int i) {
        updateCapabilitiesAndSummary(this.mModeCache);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateState(this.mModePreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MagnificationModeInfo extends ItemInfoArrayAdapter.ItemInfo {
        public final int mMagnificationMode;

        MagnificationModeInfo(CharSequence charSequence, CharSequence charSequence2, int i, int i2) {
            super(charSequence, charSequence2, i);
            this.mMagnificationMode = i2;
        }
    }
}
