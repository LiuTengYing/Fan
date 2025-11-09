package com.android.settings.localepicker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.SearchIndexableData;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocaleStore;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class LocaleListEditor extends RestrictedSettingsFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.localepicker.LocaleListEditor.8
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            Resources resources = context.getResources();
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw).key = "add_language";
            searchIndexableRaw.title = resources.getString(R$string.add_a_language);
            searchIndexableRaw.keywords = resources.getString(R$string.keywords_add_language);
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }
    };
    private LocaleDragAndDropAdapter mAdapter;
    private View mAddLanguage;
    private boolean mIsUiRestricted;
    private Menu mMenu;
    private View mRemoveMenu;
    private boolean mRemoveMode;
    private boolean mShowingRemoveDialog;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 344;
    }

    public LocaleListEditor() {
        super("no_config_locale");
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        LocaleStore.fillCache(getContext());
        this.mAdapter = new LocaleDragAndDropAdapter(getContext(), getUserLocaleList());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        configureDragAndDrop(layoutInflater.inflate(R$layout.locale_order_list, (ViewGroup) onCreateView));
        return onCreateView;
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        boolean z = this.mIsUiRestricted;
        this.mIsUiRestricted = isUiRestricted();
        TextView emptyTextView = getEmptyTextView();
        boolean z2 = this.mIsUiRestricted;
        if (z2 && !z) {
            emptyTextView.setText(R$string.language_empty_list_user_restricted);
            emptyTextView.setVisibility(0);
            updateVisibilityOfRemoveMenu();
        } else if (z2 || !z) {
        } else {
            emptyTextView.setVisibility(8);
            updateVisibilityOfRemoveMenu();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        if (bundle != null) {
            this.mRemoveMode = bundle.getBoolean("localeRemoveMode", false);
            this.mShowingRemoveDialog = bundle.getBoolean("showingLocaleRemoveDialog", false);
        }
        setRemoveMode(this.mRemoveMode);
        this.mAdapter.restoreState(bundle);
        if (this.mShowingRemoveDialog) {
            showRemoveLocaleWarningDialog();
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("localeRemoveMode", this.mRemoveMode);
        bundle.putBoolean("showingLocaleRemoveDialog", this.mShowingRemoveDialog);
        this.mAdapter.saveState(bundle);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            if (this.mRemoveMode) {
                showRemoveLocaleWarningDialog();
            } else {
                setRemoveMode(true);
            }
            return true;
        } else if (itemId == 16908332 && this.mRemoveMode) {
            setRemoveMode(false);
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 0 && i2 == -1 && intent != null) {
            this.mAdapter.addLocale(intent.getSerializableExtra("localeInfo"));
            updateVisibilityOfRemoveMenu();
        }
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRemoveMode(boolean z) {
        this.mRemoveMode = z;
        this.mAdapter.setRemoveMode(z);
        this.mAddLanguage.setVisibility(z ? 4 : 0);
        updateVisibilityOfRemoveMenu();
    }

    void showRemoveLocaleWarningDialog() {
        int checkedCount = this.mAdapter.getCheckedCount();
        if (checkedCount == 0) {
            setRemoveMode(!this.mRemoveMode);
        } else if (checkedCount == this.mAdapter.getItemCount()) {
            this.mShowingRemoveDialog = true;
            new AlertDialog.Builder(getActivity()).setTitle(R$string.dlg_remove_locales_error_title).setMessage(R$string.dlg_remove_locales_error_message).setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.android.settings.localepicker.LocaleListEditor.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.localepicker.LocaleListEditor.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    LocaleListEditor.this.mShowingRemoveDialog = false;
                }
            }).create().show();
        } else {
            String quantityString = getResources().getQuantityString(R$plurals.dlg_remove_locales_title, checkedCount);
            this.mShowingRemoveDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (this.mAdapter.isFirstLocaleChecked()) {
                builder.setMessage(R$string.dlg_remove_locales_message);
            }
            builder.setTitle(quantityString).setNegativeButton(17039369, new DialogInterface.OnClickListener() { // from class: com.android.settings.localepicker.LocaleListEditor.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    LocaleListEditor.this.setRemoveMode(false);
                }
            }).setPositiveButton(R$string.locale_remove_menu, new DialogInterface.OnClickListener() { // from class: com.android.settings.localepicker.LocaleListEditor.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    LocaleListEditor.this.mRemoveMode = false;
                    LocaleListEditor.this.mShowingRemoveDialog = false;
                    LocaleListEditor.this.mAdapter.removeChecked();
                    LocaleListEditor.this.setRemoveMode(false);
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.localepicker.LocaleListEditor.3
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    LocaleListEditor.this.mShowingRemoveDialog = false;
                }
            }).create().show();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 2, 0, R$string.locale_remove_menu);
        add.setShowAsAction(4);
        add.setIcon(R$drawable.ic_delete);
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mMenu = menu;
        updateVisibilityOfRemoveMenu();
    }

    private List<LocaleStore.LocaleInfo> getUserLocaleList() {
        ArrayList arrayList = new ArrayList();
        LocaleList locales = LocalePicker.getLocales();
        for (int i = 0; i < locales.size(); i++) {
            arrayList.add(LocaleStore.getLocaleInfo(locales.get(i)));
        }
        return arrayList;
    }

    private void configureDragAndDrop(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R$id.dragList);
        LocaleLinearLayoutManager localeLinearLayoutManager = new LocaleLinearLayoutManager(getContext(), this.mAdapter);
        localeLinearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(localeLinearLayoutManager);
        recyclerView.setHasFixedSize(true);
        this.mAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(this.mAdapter);
        View findViewById = view.findViewById(R$id.add_language);
        this.mAddLanguage = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.localepicker.LocaleListEditor.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                FeatureFactory.getFactory(LocaleListEditor.this.getContext()).getMetricsFeatureProvider().logSettingsTileClick("add_language", LocaleListEditor.this.getMetricsCategory());
                LocaleListEditor.this.startActivityForResult(new Intent(LocaleListEditor.this.getActivity(), LocalePickerWithRegionActivity.class), 0);
            }
        });
        View findViewById2 = view.findViewById(R$id.remove_language);
        this.mRemoveMenu = findViewById2;
        findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.localepicker.LocaleListEditor.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (LocaleListEditor.this.mRemoveMode) {
                    LocaleListEditor.this.showRemoveLocaleWarningDialog();
                } else {
                    LocaleListEditor.this.setRemoveMode(true);
                }
            }
        });
    }

    private void updateVisibilityOfRemoveMenu() {
        Menu menu = this.mMenu;
        if (menu == null) {
            return;
        }
        MenuItem findItem = menu.findItem(2);
        if (findItem != null) {
            boolean z = false;
            findItem.setShowAsAction(this.mRemoveMode ? 2 : 0);
            if ((this.mAdapter.getItemCount() > 1) && !this.mIsUiRestricted) {
                z = true;
            }
            findItem.setVisible(z);
        }
    }
}
