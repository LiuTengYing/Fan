package com.android.settings.inputmethod;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.textservice.SpellCheckerInfo;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.CustomListPreference;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
/* loaded from: classes.dex */
class SpellCheckerPreference extends CustomListPreference {
    private Intent mIntent;
    private final SpellCheckerInfo[] mScis;

    public SpellCheckerPreference(Context context, SpellCheckerInfo[] spellCheckerInfoArr) {
        super(context, null);
        this.mScis = spellCheckerInfoArr;
        setWidgetLayoutResource(R$layout.preference_widget_gear);
        CharSequence[] charSequenceArr = new CharSequence[spellCheckerInfoArr.length];
        CharSequence[] charSequenceArr2 = new CharSequence[spellCheckerInfoArr.length];
        for (int i = 0; i < spellCheckerInfoArr.length; i++) {
            charSequenceArr[i] = spellCheckerInfoArr[i].loadLabel(context.getPackageManager());
            charSequenceArr2[i] = String.valueOf(i);
        }
        setEntries(charSequenceArr);
        setEntryValues(charSequenceArr2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setTitle(R$string.choose_spell_checker);
        builder.setSingleChoiceItems(getEntries(), findIndexOfValue(getValue()), onClickListener);
    }

    public void setSelected(SpellCheckerInfo spellCheckerInfo) {
        if (spellCheckerInfo == null) {
            setValue(null);
            return;
        }
        int i = 0;
        while (true) {
            SpellCheckerInfo[] spellCheckerInfoArr = this.mScis;
            if (i >= spellCheckerInfoArr.length) {
                return;
            }
            if (spellCheckerInfoArr[i].getId().equals(spellCheckerInfo.getId())) {
                setValueIndex(i);
                return;
            }
            i++;
        }
    }

    @Override // androidx.preference.ListPreference
    public void setValue(String str) {
        super.setValue(str);
        int parseInt = str != null ? Integer.parseInt(str) : -1;
        if (parseInt == -1) {
            this.mIntent = null;
            return;
        }
        SpellCheckerInfo spellCheckerInfo = this.mScis[parseInt];
        String settingsActivity = spellCheckerInfo.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            this.mIntent = null;
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        this.mIntent = intent;
        intent.setClassName(spellCheckerInfo.getPackageName(), settingsActivity);
    }

    @Override // androidx.preference.Preference
    public boolean callChangeListener(Object obj) {
        return super.callChangeListener(obj != null ? this.mScis[Integer.parseInt((String) obj)] : null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.settings_button);
        findViewById.setVisibility(this.mIntent != null ? 0 : 4);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.inputmethod.SpellCheckerPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SpellCheckerPreference.this.onSettingsButtonClicked();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSettingsButtonClicked() {
        Context context = getContext();
        try {
            Intent intent = this.mIntent;
            if (intent != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException unused) {
        }
    }
}
