package com.android.settings.development.autofill;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.android.settings.Utils;
import com.android.settingslib.CustomEditTextPreferenceCompat;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AbstractGlobalSettingsPreference extends CustomEditTextPreferenceCompat {
    private final int mDefaultValue;
    private final String mKey;
    private final AutofillDeveloperSettingsObserver mObserver;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGlobalSettingsPreference(Context context, AttributeSet attributeSet, String str, int i) {
        super(context, attributeSet);
        this.mKey = str;
        this.mDefaultValue = i;
        this.mObserver = new AutofillDeveloperSettingsObserver(context, new Runnable() { // from class: com.android.settings.development.autofill.AbstractGlobalSettingsPreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AbstractGlobalSettingsPreference.this.lambda$new$0();
            }
        });
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mObserver.register();
        lambda$new$0();
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        this.mObserver.unregister();
        super.onDetached();
    }

    private String getCurrentValue() {
        return Integer.toString(Settings.Global.getInt(getContext().getContentResolver(), this.mKey, this.mDefaultValue));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateSummary */
    public void lambda$new$0() {
        setSummary(getCurrentValue());
    }

    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(2);
            editText.setText(getCurrentValue());
            Utils.setEditTextCursorPosition(editText);
        }
    }

    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    protected void onDialogClosed(boolean z) {
        if (z) {
            String text = getText();
            int i = this.mDefaultValue;
            try {
                i = Integer.parseInt(text);
            } catch (Exception unused) {
                Log.e("AbstractGlobalSettingsPreference", "Error converting '" + text + "' to integer. Using " + this.mDefaultValue + " instead");
            }
            Settings.Global.putInt(getContext().getContentResolver(), this.mKey, i);
        }
    }
}
