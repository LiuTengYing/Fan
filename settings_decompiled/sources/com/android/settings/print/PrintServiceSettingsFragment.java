package com.android.settings.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.print.PrintManager;
import android.print.PrinterDiscoverySession;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$menu;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.print.PrintServiceSettingsFragment;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class PrintServiceSettingsFragment extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, LoaderManager.LoaderCallbacks<List<PrintServiceInfo>> {
    private Intent mAddPrintersIntent;
    private ComponentName mComponentName;
    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.android.settings.print.PrintServiceSettingsFragment.1
        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            invalidateOptionsMenuIfNeeded();
            PrintServiceSettingsFragment.this.updateEmptyView();
        }

        private void invalidateOptionsMenuIfNeeded() {
            int unfilteredCount = PrintServiceSettingsFragment.this.mPrintersAdapter.getUnfilteredCount();
            if ((PrintServiceSettingsFragment.this.mLastUnfilteredItemCount <= 0 && unfilteredCount > 0) || (PrintServiceSettingsFragment.this.mLastUnfilteredItemCount > 0 && unfilteredCount <= 0)) {
                PrintServiceSettingsFragment.this.getActivity().invalidateOptionsMenu();
            }
            PrintServiceSettingsFragment.this.mLastUnfilteredItemCount = unfilteredCount;
        }
    };
    private int mLastUnfilteredItemCount;
    private String mPreferenceKey;
    private PrintersAdapter mPrintersAdapter;
    private SearchView mSearchView;
    private boolean mServiceEnabled;
    private Intent mSettingsIntent;
    private SettingsMainSwitchBar mSwitchBar;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 79;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String string = getArguments().getString("EXTRA_TITLE");
        if (TextUtils.isEmpty(string)) {
            return;
        }
        getActivity().setTitle(string);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mServiceEnabled = getArguments().getBoolean("EXTRA_CHECKED");
        return onCreateView;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        initComponents();
        updateUiForArguments();
        updateEmptyView();
        updateUiForServiceState();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
        super.onPause();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mSwitchBar.removeOnSwitchChangeListener(this);
        this.mSwitchBar.hide();
        this.mPrintersAdapter.unregisterAdapterDataObserver(this.mDataObserver);
    }

    private void onPreferenceToggled(String str, boolean z) {
        ((PrintManager) getContext().getSystemService("print")).setPrintServiceEnabled(this.mComponentName, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmptyView() {
        ViewGroup viewGroup = (ViewGroup) getListView().getParent();
        View emptyView = getEmptyView();
        if (!this.mSwitchBar.isChecked()) {
            if (emptyView != null) {
                viewGroup.removeView(emptyView);
                emptyView = null;
            }
            if (emptyView == null) {
                View inflate = getActivity().getLayoutInflater().inflate(R$layout.empty_print_state, viewGroup, false);
                ((TextView) inflate.findViewById(R$id.message)).setText(R$string.print_service_disabled);
                viewGroup.addView(inflate);
                setEmptyView(inflate);
            }
        } else if (this.mPrintersAdapter.getUnfilteredCount() <= 0) {
            if (emptyView != null) {
                viewGroup.removeView(emptyView);
                emptyView = null;
            }
            if (emptyView == null) {
                View inflate2 = getActivity().getLayoutInflater().inflate(R$layout.empty_printers_list_service_enabled, viewGroup, false);
                viewGroup.addView(inflate2);
                setEmptyView(inflate2);
            }
        } else if (this.mPrintersAdapter.getItemCount() > 0) {
            if (this.mPrintersAdapter.getItemCount() <= 0 || emptyView == null) {
                return;
            }
            viewGroup.removeView(emptyView);
        } else {
            if (emptyView != null) {
                viewGroup.removeView(emptyView);
                emptyView = null;
            }
            if (emptyView == null) {
                View inflate3 = getActivity().getLayoutInflater().inflate(R$layout.empty_print_state, viewGroup, false);
                ((TextView) inflate3.findViewById(R$id.message)).setText(R$string.print_no_printers_found);
                viewGroup.addView(inflate3);
                setEmptyView(inflate3);
            }
        }
    }

    private void updateUiForServiceState() {
        if (this.mServiceEnabled) {
            this.mSwitchBar.setCheckedInternal(true);
            this.mPrintersAdapter.enable();
        } else {
            this.mSwitchBar.setCheckedInternal(false);
            this.mPrintersAdapter.disable();
        }
        getActivity().invalidateOptionsMenu();
    }

    private void initComponents() {
        PrintersAdapter printersAdapter = new PrintersAdapter();
        this.mPrintersAdapter = printersAdapter;
        printersAdapter.registerAdapterDataObserver(this.mDataObserver);
        SettingsMainSwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R$string.default_print_service_main_switch_title));
        this.mSwitchBar.addOnSwitchChangeListener(this);
        this.mSwitchBar.show();
        this.mSwitchBar.setOnBeforeCheckedChangeListener(new SettingsMainSwitchBar.OnBeforeCheckedChangeListener() { // from class: com.android.settings.print.PrintServiceSettingsFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.widget.SettingsMainSwitchBar.OnBeforeCheckedChangeListener
            public final boolean onBeforeCheckedChanged(Switch r1, boolean z) {
                boolean lambda$initComponents$0;
                lambda$initComponents$0 = PrintServiceSettingsFragment.this.lambda$initComponents$0(r1, z);
                return lambda$initComponents$0;
            }
        });
        getListView().setAdapter(this.mPrintersAdapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initComponents$0(Switch r1, boolean z) {
        onPreferenceToggled(this.mPreferenceKey, z);
        return false;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        updateEmptyView();
    }

    private void updateUiForArguments() {
        Bundle arguments = getArguments();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("EXTRA_SERVICE_COMPONENT_NAME"));
        this.mComponentName = unflattenFromString;
        this.mPreferenceKey = unflattenFromString.flattenToString();
        this.mSwitchBar.setCheckedInternal(arguments.getBoolean("EXTRA_CHECKED"));
        getLoaderManager().initLoader(2, null, this);
        setHasOptionsMenu(true);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<List<PrintServiceInfo>> onCreateLoader(int i, Bundle bundle) {
        return new SettingsPrintServicesLoader((PrintManager) getContext().getSystemService("print"), getContext(), 3);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<PrintServiceInfo>> loader, List<PrintServiceInfo> list) {
        PrintServiceInfo printServiceInfo;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (list.get(i).getComponentName().equals(this.mComponentName)) {
                    printServiceInfo = list.get(i);
                    break;
                }
            }
        }
        printServiceInfo = null;
        if (printServiceInfo == null) {
            finishFragment();
        }
        this.mServiceEnabled = printServiceInfo.isEnabled();
        if (printServiceInfo.getSettingsActivityName() != null) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setComponent(new ComponentName(printServiceInfo.getComponentName().getPackageName(), printServiceInfo.getSettingsActivityName()));
            List<ResolveInfo> queryIntentActivities = getPackageManager().queryIntentActivities(intent, 0);
            if (!queryIntentActivities.isEmpty() && queryIntentActivities.get(0).activityInfo.exported) {
                this.mSettingsIntent = intent;
            }
        } else {
            this.mSettingsIntent = null;
        }
        if (printServiceInfo.getAddPrintersActivityName() != null) {
            Intent intent2 = new Intent("android.intent.action.MAIN");
            intent2.setComponent(new ComponentName(printServiceInfo.getComponentName().getPackageName(), printServiceInfo.getAddPrintersActivityName()));
            List<ResolveInfo> queryIntentActivities2 = getPackageManager().queryIntentActivities(intent2, 0);
            if (!queryIntentActivities2.isEmpty() && queryIntentActivities2.get(0).activityInfo.exported) {
                this.mAddPrintersIntent = intent2;
            }
        } else {
            this.mAddPrintersIntent = null;
        }
        updateUiForServiceState();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<PrintServiceInfo>> loader) {
        updateUiForServiceState();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Intent intent;
        Intent intent2;
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R$menu.print_service_settings, menu);
        int i = R$id.print_menu_item_add_printer;
        MenuItem findItem = menu.findItem(i);
        if (this.mServiceEnabled && (intent2 = this.mAddPrintersIntent) != null) {
            findItem.setIntent(intent2);
        } else {
            menu.removeItem(i);
        }
        int i2 = R$id.print_menu_item_settings;
        MenuItem findItem2 = menu.findItem(i2);
        if (this.mServiceEnabled && (intent = this.mSettingsIntent) != null) {
            findItem2.setIntent(intent);
        } else {
            menu.removeItem(i2);
        }
        int i3 = R$id.print_menu_item_search;
        MenuItem findItem3 = menu.findItem(i3);
        if (this.mServiceEnabled && this.mPrintersAdapter.getUnfilteredCount() > 0) {
            SearchView searchView = (SearchView) findItem3.getActionView();
            this.mSearchView = searchView;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.android.settings.print.PrintServiceSettingsFragment.2
                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextSubmit(String str) {
                    return true;
                }

                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextChange(String str) {
                    PrintServiceSettingsFragment.this.mPrintersAdapter.getFilter().filter(str);
                    return true;
                }
            });
            this.mSearchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.settings.print.PrintServiceSettingsFragment.3
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view) {
                    if (AccessibilityManager.getInstance(PrintServiceSettingsFragment.this.getActivity()).isEnabled()) {
                        view.announceForAccessibility(PrintServiceSettingsFragment.this.getString(R$string.print_search_box_shown_utterance));
                    }
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view) {
                    FragmentActivity activity = PrintServiceSettingsFragment.this.getActivity();
                    if (activity == null || activity.isFinishing() || !AccessibilityManager.getInstance(activity).isEnabled()) {
                        return;
                    }
                    view.announceForAccessibility(PrintServiceSettingsFragment.this.getString(R$string.print_search_box_hidden_utterance));
                }
            });
            return;
        }
        menu.removeItem(i3);
    }

    /* loaded from: classes.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class PrintersAdapter extends RecyclerView.Adapter<ViewHolder> implements LoaderManager.LoaderCallbacks<List<PrinterInfo>>, Filterable {
        private final List<PrinterInfo> mFilteredPrinters;
        private CharSequence mLastSearchString;
        private final Object mLock;
        private final List<PrinterInfo> mPrinters;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        private PrintersAdapter() {
            this.mLock = new Object();
            this.mPrinters = new ArrayList();
            this.mFilteredPrinters = new ArrayList();
        }

        public void enable() {
            PrintServiceSettingsFragment.this.getLoaderManager().initLoader(1, null, this);
        }

        public void disable() {
            PrintServiceSettingsFragment.this.getLoaderManager().destroyLoader(1);
            this.mPrinters.clear();
        }

        public int getUnfilteredCount() {
            return this.mPrinters.size();
        }

        @Override // android.widget.Filterable
        public Filter getFilter() {
            return new Filter() { // from class: com.android.settings.print.PrintServiceSettingsFragment.PrintersAdapter.1
                @Override // android.widget.Filter
                protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                    synchronized (PrintersAdapter.this.mLock) {
                        if (TextUtils.isEmpty(charSequence)) {
                            return null;
                        }
                        Filter.FilterResults filterResults = new Filter.FilterResults();
                        ArrayList arrayList = new ArrayList();
                        String lowerCase = charSequence.toString().toLowerCase();
                        int size = PrintersAdapter.this.mPrinters.size();
                        for (int i = 0; i < size; i++) {
                            PrinterInfo printerInfo = (PrinterInfo) PrintersAdapter.this.mPrinters.get(i);
                            String name = printerInfo.getName();
                            if (name != null && name.toLowerCase().contains(lowerCase)) {
                                arrayList.add(printerInfo);
                            }
                        }
                        filterResults.values = arrayList;
                        filterResults.count = arrayList.size();
                        return filterResults;
                    }
                }

                @Override // android.widget.Filter
                protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                    synchronized (PrintersAdapter.this.mLock) {
                        PrintersAdapter.this.mLastSearchString = charSequence;
                        PrintersAdapter.this.mFilteredPrinters.clear();
                        if (filterResults == null) {
                            PrintersAdapter.this.mFilteredPrinters.addAll(PrintersAdapter.this.mPrinters);
                        } else {
                            PrintersAdapter.this.mFilteredPrinters.addAll((List) filterResults.values);
                        }
                    }
                    PrintersAdapter.this.notifyDataSetChanged();
                }
            };
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int size;
            synchronized (this.mLock) {
                size = this.mFilteredPrinters.size();
            }
            return size;
        }

        private Object getItem(int i) {
            PrinterInfo printerInfo;
            synchronized (this.mLock) {
                printerInfo = this.mFilteredPrinters.get(i);
            }
            return printerInfo;
        }

        public boolean isActionable(int i) {
            return ((PrinterInfo) getItem(i)).getStatus() != 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.printer_dropdown_item, viewGroup, false));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            viewHolder.itemView.setEnabled(isActionable(i));
            final PrinterInfo printerInfo = (PrinterInfo) getItem(i);
            String name = printerInfo.getName();
            String description = printerInfo.getDescription();
            Drawable loadIcon = printerInfo.loadIcon(PrintServiceSettingsFragment.this.getActivity());
            ((TextView) viewHolder.itemView.findViewById(R$id.title)).setText(name);
            TextView textView = (TextView) viewHolder.itemView.findViewById(R$id.subtitle);
            if (!TextUtils.isEmpty(description)) {
                textView.setText(description);
                textView.setVisibility(0);
            } else {
                textView.setText((CharSequence) null);
                textView.setVisibility(8);
            }
            LinearLayout linearLayout = (LinearLayout) viewHolder.itemView.findViewById(R$id.more_info);
            if (printerInfo.getInfoIntent() != null) {
                linearLayout.setVisibility(0);
                linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.print.PrintServiceSettingsFragment.PrintersAdapter.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        try {
                            PrintServiceSettingsFragment.this.getActivity().startIntentSender(printerInfo.getInfoIntent().getIntentSender(), null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("PrintServiceSettings", "Could not execute pending info intent: %s", e);
                        }
                    }
                });
            } else {
                linearLayout.setVisibility(8);
            }
            ImageView imageView = (ImageView) viewHolder.itemView.findViewById(R$id.icon);
            if (loadIcon != null) {
                imageView.setVisibility(0);
                if (!isActionable(i)) {
                    loadIcon.mutate();
                    TypedValue typedValue = new TypedValue();
                    PrintServiceSettingsFragment.this.getActivity().getTheme().resolveAttribute(16842803, typedValue, true);
                    loadIcon.setAlpha((int) (typedValue.getFloat() * 255.0f));
                }
                imageView.setImageDrawable(loadIcon);
            } else {
                imageView.setVisibility(8);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.print.PrintServiceSettingsFragment$PrintersAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PrintServiceSettingsFragment.PrintersAdapter.this.lambda$onBindViewHolder$0(i, view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$0(int i, View view) {
            PrinterInfo printerInfo = (PrinterInfo) getItem(i);
            if (printerInfo.getInfoIntent() != null) {
                try {
                    PrintServiceSettingsFragment.this.getActivity().startIntentSender(printerInfo.getInfoIntent().getIntentSender(), null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("PrintServiceSettings", "Could not execute info intent: %s", e);
                }
            }
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<PrinterInfo>> onCreateLoader(int i, Bundle bundle) {
            if (i == 1) {
                return new PrintersLoader(PrintServiceSettingsFragment.this.getContext());
            }
            return null;
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<List<PrinterInfo>> loader, List<PrinterInfo> list) {
            synchronized (this.mLock) {
                this.mPrinters.clear();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    PrinterInfo printerInfo = list.get(i);
                    if (printerInfo.getId().getServiceName().equals(PrintServiceSettingsFragment.this.mComponentName)) {
                        this.mPrinters.add(printerInfo);
                    }
                }
                this.mFilteredPrinters.clear();
                this.mFilteredPrinters.addAll(this.mPrinters);
                if (!TextUtils.isEmpty(this.mLastSearchString)) {
                    getFilter().filter(this.mLastSearchString);
                }
            }
            notifyDataSetChanged();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<PrinterInfo>> loader) {
            synchronized (this.mLock) {
                this.mPrinters.clear();
                this.mFilteredPrinters.clear();
                this.mLastSearchString = null;
            }
            notifyDataSetChanged();
        }
    }

    /* loaded from: classes.dex */
    private static class PrintersLoader extends Loader<List<PrinterInfo>> {
        private PrinterDiscoverySession mDiscoverySession;
        private final Map<PrinterId, PrinterInfo> mPrinters;

        public PrintersLoader(Context context) {
            super(context);
            this.mPrinters = new LinkedHashMap();
        }

        public void deliverResult(List<PrinterInfo> list) {
            if (isStarted()) {
                super.deliverResult((PrintersLoader) list);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStartLoading() {
            if (!this.mPrinters.isEmpty()) {
                deliverResult((List<PrinterInfo>) new ArrayList(this.mPrinters.values()));
            }
            onForceLoad();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStopLoading() {
            onCancelLoad();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onForceLoad() {
            loadInternal();
        }

        @Override // androidx.loader.content.Loader
        protected boolean onCancelLoad() {
            return cancelInternal();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onReset() {
            onStopLoading();
            this.mPrinters.clear();
            PrinterDiscoverySession printerDiscoverySession = this.mDiscoverySession;
            if (printerDiscoverySession != null) {
                printerDiscoverySession.destroy();
                this.mDiscoverySession = null;
            }
        }

        @Override // androidx.loader.content.Loader
        protected void onAbandon() {
            onStopLoading();
        }

        private boolean cancelInternal() {
            PrinterDiscoverySession printerDiscoverySession = this.mDiscoverySession;
            if (printerDiscoverySession == null || !printerDiscoverySession.isPrinterDiscoveryStarted()) {
                return false;
            }
            this.mDiscoverySession.stopPrinterDiscovery();
            return true;
        }

        private void loadInternal() {
            if (this.mDiscoverySession == null) {
                PrinterDiscoverySession createPrinterDiscoverySession = ((PrintManager) getContext().getSystemService("print")).createPrinterDiscoverySession();
                this.mDiscoverySession = createPrinterDiscoverySession;
                createPrinterDiscoverySession.setOnPrintersChangeListener(new PrinterDiscoverySession.OnPrintersChangeListener() { // from class: com.android.settings.print.PrintServiceSettingsFragment.PrintersLoader.1
                    public void onPrintersChanged() {
                        PrintersLoader.this.deliverResult((List<PrinterInfo>) new ArrayList(PrintersLoader.this.mDiscoverySession.getPrinters()));
                    }
                });
            }
            this.mDiscoverySession.startPrinterDiscovery((List) null);
        }
    }
}
