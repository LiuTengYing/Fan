package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class CustomListPreference extends ListPreference {
    protected CharSequence getConfirmationMessage(String str) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAutoClosePreference() {
        return true;
    }

    protected void onDialogClosed(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogCreated(Dialog dialog) {
    }

    protected void onDialogStateRestored(Dialog dialog, Bundle bundle) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
    }

    public CustomListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setSingleLineTitle(true);
    }

    public CustomListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* loaded from: classes.dex */
    public static class CustomListPreferenceDialogFragment extends ListPreferenceDialogFragmentCompat {
        private int mClickedDialogEntryIndex;

        public static ListPreferenceDialogFragmentCompat newInstance(String str) {
            CustomListPreferenceDialogFragment customListPreferenceDialogFragment = new CustomListPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", str);
            customListPreferenceDialogFragment.setArguments(bundle);
            return customListPreferenceDialogFragment;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CustomListPreference getCustomizablePreference() {
            return (CustomListPreference) getPreference();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.preference.ListPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            this.mClickedDialogEntryIndex = getCustomizablePreference().findIndexOfValue(getCustomizablePreference().getValue());
            getCustomizablePreference().onPrepareDialogBuilder(builder, getOnItemClickListener());
            if (getCustomizablePreference().isAutoClosePreference()) {
                return;
            }
            builder.setPositiveButton(R$string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    CustomListPreferenceDialogFragment.this.onItemChosen();
                }
            });
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Dialog onCreateDialog = super.onCreateDialog(bundle);
            if (bundle != null) {
                this.mClickedDialogEntryIndex = bundle.getInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
            }
            getCustomizablePreference().onDialogCreated(onCreateDialog);
            return onCreateDialog;
        }

        @Override // androidx.preference.ListPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            getCustomizablePreference().onDialogStateRestored(getDialog(), bundle);
        }

        protected DialogInterface.OnClickListener getOnItemClickListener() {
            return new DialogInterface.OnClickListener() { // from class: com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    CustomListPreferenceDialogFragment.this.setClickedDialogEntryIndex(i);
                    if (CustomListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                        CustomListPreferenceDialogFragment.this.onItemChosen();
                    }
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void setClickedDialogEntryIndex(int i) {
            this.mClickedDialogEntryIndex = i;
        }

        private String getValue() {
            CustomListPreference customizablePreference = getCustomizablePreference();
            if (this.mClickedDialogEntryIndex < 0 || customizablePreference.getEntryValues() == null) {
                return null;
            }
            return customizablePreference.getEntryValues()[this.mClickedDialogEntryIndex].toString();
        }

        protected void onItemChosen() {
            CharSequence confirmationMessage = getCustomizablePreference().getConfirmationMessage(getValue());
            if (confirmationMessage != null) {
                ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("android.intent.extra.TEXT", confirmationMessage);
                confirmDialogFragment.setArguments(bundle);
                confirmDialogFragment.setTargetFragment(this, 0);
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.add(confirmDialogFragment, getTag() + "-Confirm");
                beginTransaction.commitAllowingStateLoss();
                return;
            }
            onItemConfirmed();
        }

        protected void onItemConfirmed() {
            onClick(getDialog(), -1);
            getDialog().dismiss();
        }

        @Override // androidx.preference.ListPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat
        public void onDialogClosed(boolean z) {
            getCustomizablePreference().onDialogClosed(z);
            CustomListPreference customizablePreference = getCustomizablePreference();
            String value = getValue();
            if (z && value != null && customizablePreference.callChangeListener(value)) {
                customizablePreference.setValue(value);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ConfirmDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 529;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setMessage(getArguments().getCharSequence("android.intent.extra.TEXT")).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.CustomListPreference.ConfirmDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Fragment targetFragment = ConfirmDialogFragment.this.getTargetFragment();
                    if (targetFragment != null) {
                        ((CustomListPreferenceDialogFragment) targetFragment).onItemConfirmed();
                    }
                }
            }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        }
    }
}
