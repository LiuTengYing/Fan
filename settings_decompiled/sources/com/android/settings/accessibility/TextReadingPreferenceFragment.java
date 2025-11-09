package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.TextReadingResetController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class TextReadingPreferenceFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accessibility_text_reading_options);
    private int mEntryPoint = 0;
    private FontWeightAdjustmentPreferenceController mFontWeightAdjustmentController;
    boolean mNeedResetSettings;
    List<TextReadingResetController.ResetStateListener> mResetStateListeners;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "TextReadingPreferenceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1912;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mNeedResetSettings = false;
        this.mResetStateListeners = getResetStateListeners();
        if (bundle == null || !bundle.getBoolean("need_reset_settings")) {
            return;
        }
        this.mResetStateListeners.forEach(new TextReadingPreferenceFragment$$ExternalSyntheticLambda2());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_text_reading_options;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        updateEntryPoint();
        ArrayList arrayList = new ArrayList();
        FontSizeData fontSizeData = new FontSizeData(context);
        DisplaySizeData createDisplaySizeData = createDisplaySizeData(context);
        TextReadingPreviewController textReadingPreviewController = new TextReadingPreviewController(context, "preview", fontSizeData, createDisplaySizeData);
        textReadingPreviewController.setEntryPoint(this.mEntryPoint);
        arrayList.add(textReadingPreviewController);
        PreviewSizeSeekBarController previewSizeSeekBarController = new PreviewSizeSeekBarController(context, "font_size", fontSizeData);
        previewSizeSeekBarController.setInteractionListener(textReadingPreviewController);
        arrayList.add(previewSizeSeekBarController);
        PreviewSizeSeekBarController previewSizeSeekBarController2 = new PreviewSizeSeekBarController(context, "display_size", createDisplaySizeData);
        previewSizeSeekBarController2.setInteractionListener(textReadingPreviewController);
        arrayList.add(previewSizeSeekBarController2);
        FontWeightAdjustmentPreferenceController fontWeightAdjustmentPreferenceController = new FontWeightAdjustmentPreferenceController(context, "toggle_force_bold_text");
        this.mFontWeightAdjustmentController = fontWeightAdjustmentPreferenceController;
        fontWeightAdjustmentPreferenceController.setEntryPoint(this.mEntryPoint);
        arrayList.add(this.mFontWeightAdjustmentController);
        HighTextContrastPreferenceController highTextContrastPreferenceController = new HighTextContrastPreferenceController(context, "toggle_high_text_contrast_preference");
        highTextContrastPreferenceController.setEntryPoint(this.mEntryPoint);
        arrayList.add(highTextContrastPreferenceController);
        TextReadingResetController textReadingResetController = new TextReadingResetController(context, "reset", new View.OnClickListener() { // from class: com.android.settings.accessibility.TextReadingPreferenceFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TextReadingPreferenceFragment.this.lambda$createPreferenceControllers$0(view);
            }
        });
        textReadingResetController.setEntryPoint(this.mEntryPoint);
        arrayList.add(textReadingResetController);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createPreferenceControllers$0(View view) {
        showDialog(1009);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1009) {
            return new AlertDialog.Builder(getPrefContext()).setTitle(R$string.accessibility_text_reading_confirm_dialog_title).setMessage(R$string.accessibility_text_reading_confirm_dialog_message).setPositiveButton(R$string.accessibility_text_reading_confirm_dialog_reset_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.TextReadingPreferenceFragment$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    TextReadingPreferenceFragment.this.onPositiveButtonClicked(dialogInterface, i2);
                }
            }).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
        }
        throw new IllegalArgumentException("Unsupported dialogId " + i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1009) {
            return 1924;
        }
        return super.getDialogMetricsCategory(i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        if (this.mNeedResetSettings) {
            bundle.putBoolean("need_reset_settings", true);
        }
    }

    DisplaySizeData createDisplaySizeData(Context context) {
        return new DisplaySizeData(context);
    }

    private void updateEntryPoint() {
        Bundle arguments = getArguments();
        int i = 0;
        if (arguments != null && arguments.containsKey("launched_from")) {
            this.mEntryPoint = arguments.getInt("launched_from", 0);
            return;
        }
        Intent intent = getIntent();
        if (intent == null) {
            this.mEntryPoint = 0;
            return;
        }
        Set<String> categories = intent.getCategories();
        if (categories != null && categories.contains("com.android.settings.suggested.category.DISPLAY_SETTINGS")) {
            i = 2;
        }
        this.mEntryPoint = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPositiveButtonClicked(DialogInterface dialogInterface, int i) {
        removeDialog(1009);
        if (this.mFontWeightAdjustmentController.isChecked()) {
            this.mNeedResetSettings = true;
            this.mFontWeightAdjustmentController.resetState();
        } else {
            this.mResetStateListeners.forEach(new TextReadingPreferenceFragment$$ExternalSyntheticLambda2());
        }
        Toast.makeText(getPrefContext(), R$string.accessibility_text_reading_reset_message, 0).show();
    }

    private List<TextReadingResetController.ResetStateListener> getResetStateListeners() {
        final ArrayList arrayList = new ArrayList();
        getPreferenceControllers().forEach(new Consumer() { // from class: com.android.settings.accessibility.TextReadingPreferenceFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                arrayList.addAll((List) obj);
            }
        });
        return (List) arrayList.stream().filter(new Predicate() { // from class: com.android.settings.accessibility.TextReadingPreferenceFragment$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getResetStateListeners$1;
                lambda$getResetStateListeners$1 = TextReadingPreferenceFragment.lambda$getResetStateListeners$1((AbstractPreferenceController) obj);
                return lambda$getResetStateListeners$1;
            }
        }).map(new Function() { // from class: com.android.settings.accessibility.TextReadingPreferenceFragment$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                TextReadingResetController.ResetStateListener lambda$getResetStateListeners$2;
                lambda$getResetStateListeners$2 = TextReadingPreferenceFragment.lambda$getResetStateListeners$2((AbstractPreferenceController) obj);
                return lambda$getResetStateListeners$2;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getResetStateListeners$1(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof TextReadingResetController.ResetStateListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ TextReadingResetController.ResetStateListener lambda$getResetStateListeners$2(AbstractPreferenceController abstractPreferenceController) {
        return (TextReadingResetController.ResetStateListener) abstractPreferenceController;
    }
}
