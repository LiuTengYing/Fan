package com.android.settings;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.CustomListPreference;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.widget.HighlightablePreferenceGroupAdapter;
import com.android.settings.widget.LoadingViewController;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.android.settingslib.CustomEditTextPreferenceCompat;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.UUID;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class SettingsPreferenceFragment extends InstrumentedPreferenceFragment implements DialogCreatable, HelpResourceProvider {
    private static final int ORDER_FIRST = -1;
    private static final String SAVE_HIGHLIGHTED_KEY = "android:preference_highlighted";
    private static final String TAG = "SettingsPreferenceFragment";
    public HighlightablePreferenceGroupAdapter mAdapter;
    private boolean mAnimationAllowed;
    private AppBarLayout mAppBarLayout;
    private ContentResolver mContentResolver;
    private RecyclerView.Adapter mCurrentRootAdapter;
    protected DevicePolicyManager mDevicePolicyManager;
    private SettingsDialogFragment mDialogFragment;
    private View mEmptyView;
    private LayoutPreference mHeader;
    private LinearLayoutManager mLayoutManager;
    ViewGroup mPinnedHeaderFrameLayout;
    private ArrayMap<String, Preference> mPreferenceCache;
    private boolean mIsDataSetObserverRegistered = false;
    private RecyclerView.AdapterDataObserver mDataSetObserver = new RecyclerView.AdapterDataObserver() { // from class: com.android.settings.SettingsPreferenceFragment.1
        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2, Object obj) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }
    };
    private boolean mPreferenceHighlighted = false;

    @Override // com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 0;
    }

    public int getInitialExpandedChildCount() {
        return 0;
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        return null;
    }

    public void onDialogShowing() {
    }

    protected boolean shouldSkipForInitialSUW() {
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        if (shouldSkipForInitialSUW() && !WizardManagerHelper.isDeviceProvisioned(getContext())) {
            Log.w(TAG, "Skip " + getClass().getSimpleName() + " before SUW completed.");
            finish();
        }
        super.onAttach(context);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDevicePolicyManager = (DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class);
        if (bundle != null) {
            this.mPreferenceHighlighted = bundle.getBoolean(SAVE_HIGHLIGHTED_KEY);
        }
        HighlightablePreferenceGroupAdapter.adjustInitialExpandedChildCount(this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mPinnedHeaderFrameLayout = (ViewGroup) onCreateView.findViewById(R$id.pinned_header);
        this.mAppBarLayout = (AppBarLayout) getActivity().findViewById(R$id.app_bar);
        return onCreateView;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        checkAvailablePrefs(getPreferenceScreen());
    }

    void checkAvailablePrefs(PreferenceGroup preferenceGroup) {
        if (preferenceGroup == null) {
            return;
        }
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if ((preference instanceof SelfAvailablePreference) && !((SelfAvailablePreference) preference).isAvailable(getContext())) {
                preference.setVisible(false);
            } else if (preference instanceof PreferenceGroup) {
                checkAvailablePrefs((PreferenceGroup) preference);
            }
        }
    }

    public View setPinnedHeaderView(int i) {
        View inflate = getActivity().getLayoutInflater().inflate(i, this.mPinnedHeaderFrameLayout, false);
        setPinnedHeaderView(inflate);
        return inflate;
    }

    public void setPinnedHeaderView(View view) {
        this.mPinnedHeaderFrameLayout.addView(view);
        this.mPinnedHeaderFrameLayout.setVisibility(0);
    }

    public void showPinnedHeader(boolean z) {
        this.mPinnedHeaderFrameLayout.setVisibility(z ? 0 : 4);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter = this.mAdapter;
        if (highlightablePreferenceGroupAdapter != null) {
            bundle.putBoolean(SAVE_HIGHLIGHTED_KEY, highlightablePreferenceGroupAdapter.isHighlightRequested());
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        highlightPreferenceIfNeeded();
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    protected void onBindPreferences() {
        registerObserverIfNeeded();
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    protected void onUnbindPreferences() {
        unregisterObserverIfNeeded();
    }

    public void setLoading(boolean z, boolean z2) {
        LoadingViewController.handleLoadingContainer(getView().findViewById(R$id.loading_container), getListView(), !z, z2);
    }

    public void registerObserverIfNeeded() {
        if (this.mIsDataSetObserverRegistered) {
            return;
        }
        RecyclerView.Adapter adapter = this.mCurrentRootAdapter;
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(this.mDataSetObserver);
        }
        RecyclerView.Adapter adapter2 = getListView().getAdapter();
        this.mCurrentRootAdapter = adapter2;
        adapter2.registerAdapterDataObserver(this.mDataSetObserver);
        this.mIsDataSetObserverRegistered = true;
        onDataSetChanged();
    }

    public void unregisterObserverIfNeeded() {
        if (this.mIsDataSetObserverRegistered) {
            RecyclerView.Adapter adapter = this.mCurrentRootAdapter;
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(this.mDataSetObserver);
                this.mCurrentRootAdapter = null;
            }
            this.mIsDataSetObserverRegistered = false;
        }
    }

    public void highlightPreferenceIfNeeded() {
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter;
        if (isAdded() && (highlightablePreferenceGroupAdapter = this.mAdapter) != null) {
            highlightablePreferenceGroupAdapter.requestHighlight(getView(), getListView(), this.mAppBarLayout);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPreferenceExpanded(Preference preference) {
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter = this.mAdapter;
        return highlightablePreferenceGroupAdapter == null || highlightablePreferenceGroupAdapter.getPreferenceAdapterPosition(preference) != ORDER_FIRST;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDataSetChanged() {
        highlightPreferenceIfNeeded();
        updateEmptyView();
    }

    public LayoutPreference getHeaderView() {
        return this.mHeader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderView(int i) {
        LayoutPreference layoutPreference = new LayoutPreference(getPrefContext(), i);
        this.mHeader = layoutPreference;
        layoutPreference.setSelectable(false);
        addPreferenceToTop(this.mHeader);
    }

    protected void setHeaderView(View view) {
        LayoutPreference layoutPreference = new LayoutPreference(getPrefContext(), view);
        this.mHeader = layoutPreference;
        layoutPreference.setSelectable(false);
        addPreferenceToTop(this.mHeader);
    }

    private void addPreferenceToTop(LayoutPreference layoutPreference) {
        layoutPreference.setOrder(ORDER_FIRST);
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().addPreference(layoutPreference);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference;
        if (preferenceScreen != null && !preferenceScreen.isAttached()) {
            preferenceScreen.setShouldUseGeneratedIds(this.mAnimationAllowed);
            Log.d("fangli", "setPreferenceScreen: " + this.mAnimationAllowed);
        }
        super.setPreferenceScreen(preferenceScreen);
        if (preferenceScreen == null || (layoutPreference = this.mHeader) == null) {
            return;
        }
        preferenceScreen.addPreference(layoutPreference);
    }

    void updateEmptyView() {
        if (this.mEmptyView == null) {
            return;
        }
        if (getPreferenceScreen() != null) {
            View findViewById = getActivity().findViewById(16908351);
            boolean z = true;
            if (getPreferenceScreen().getPreferenceCount() - (this.mHeader != null ? 1 : 0) > 0 && (findViewById == null || findViewById.getVisibility() == 0)) {
                z = false;
            }
            this.mEmptyView.setVisibility(z ? 0 : 8);
            return;
        }
        this.mEmptyView.setVisibility(0);
    }

    public void setEmptyView(View view) {
        View view2 = this.mEmptyView;
        if (view2 != null) {
            view2.setVisibility(8);
        }
        this.mEmptyView = view;
        updateEmptyView();
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView.LayoutManager onCreateLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.mLayoutManager = linearLayoutManager;
        return linearLayoutManager;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        Bundle arguments = getArguments();
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter = new HighlightablePreferenceGroupAdapter(preferenceScreen, arguments == null ? null : arguments.getString(":settings:fragment_args_key"), this.mPreferenceHighlighted);
        this.mAdapter = highlightablePreferenceGroupAdapter;
        return highlightablePreferenceGroupAdapter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAnimationAllowed(boolean z) {
        this.mAnimationAllowed = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cacheRemoveAllPrefs(PreferenceGroup preferenceGroup) {
        this.mPreferenceCache = new ArrayMap<>();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Preference getCachedPreference(String str) {
        ArrayMap<String, Preference> arrayMap = this.mPreferenceCache;
        if (arrayMap != null) {
            return arrayMap.remove(str);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeCachedPrefs(PreferenceGroup preferenceGroup) {
        for (Preference preference : this.mPreferenceCache.values()) {
            preferenceGroup.removePreference(preference);
        }
        this.mPreferenceCache = null;
    }

    protected int getCachedCount() {
        ArrayMap<String, Preference> arrayMap = this.mPreferenceCache;
        if (arrayMap != null) {
            return arrayMap.size();
        }
        return 0;
    }

    public boolean removePreference(String str) {
        return removePreference(getPreferenceScreen(), str);
    }

    boolean removePreference(PreferenceGroup preferenceGroup, String str) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (TextUtils.equals(preference.getKey(), str)) {
                return preferenceGroup.removePreference(preference);
            }
            if ((preference instanceof PreferenceGroup) && removePreference((PreferenceGroup) preference, str)) {
                return true;
            }
        }
        return false;
    }

    public final void finishFragment() {
        getActivity().onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContentResolver getContentResolver() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mContentResolver = activity.getContentResolver();
        }
        return this.mContentResolver;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getSystemService(String str) {
        return getActivity().getSystemService(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T getSystemService(Class<T> cls) {
        return (T) getActivity().getSystemService(cls);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        SettingsDialogFragment settingsDialogFragment;
        if (isRemoving() && (settingsDialogFragment = this.mDialogFragment) != null) {
            settingsDialogFragment.dismiss();
            this.mDialogFragment = null;
        }
        super.onDetach();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e(TAG, "Old dialog fragment not null!");
        }
        this.mDialogFragment = SettingsDialogFragment.newInstance(this, i);
        if (getActivity() != null && isAdded() && getChildFragmentManager() != null) {
            this.mDialogFragment.show(getChildFragmentManager(), Integer.toString(i));
        } else {
            Log.e(TAG, "Fragment has not been attached yet or getChildFragmentManager is null.");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeDialog(int i) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null && settingsDialogFragment.getDialogId() == i) {
            this.mDialogFragment.dismissAllowingStateLoss();
        }
        this.mDialogFragment = null;
    }

    protected void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null) {
            settingsDialogFragment.mOnCancelListener = onCancelListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null) {
            settingsDialogFragment.mOnDismissListener = onDismissListener;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment newInstance;
        if (preference.getKey() == null) {
            preference.setKey(UUID.randomUUID().toString());
        }
        if (preference instanceof RestrictedListPreference) {
            newInstance = RestrictedListPreference.RestrictedListPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomListPreference) {
            newInstance = CustomListPreference.CustomListPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomDialogPreferenceCompat) {
            newInstance = CustomDialogPreferenceCompat.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomEditTextPreferenceCompat) {
            newInstance = CustomEditTextPreferenceCompat.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        newInstance.setTargetFragment(this, 0);
        newInstance.show(getFragmentManager(), "dialog_preference");
        onDialogShowing();
    }

    /* loaded from: classes.dex */
    public static class SettingsDialogFragment extends InstrumentedDialogFragment {
        private DialogInterface.OnCancelListener mOnCancelListener;
        private DialogInterface.OnDismissListener mOnDismissListener;
        private Fragment mParentFragment;

        public static SettingsDialogFragment newInstance(DialogCreatable dialogCreatable, int i) {
            if (!(dialogCreatable instanceof Fragment)) {
                throw new IllegalArgumentException("fragment argument must be an instance of " + Fragment.class.getName());
            }
            SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
            settingsDialogFragment.setParentFragment(dialogCreatable);
            settingsDialogFragment.setDialogId(i);
            return settingsDialogFragment;
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            Fragment fragment = this.mParentFragment;
            if (fragment == null) {
                return 0;
            }
            int dialogMetricsCategory = ((DialogCreatable) fragment).getDialogMetricsCategory(this.mDialogId);
            if (dialogMetricsCategory > 0) {
                return dialogMetricsCategory;
            }
            throw new IllegalStateException("Dialog must provide a metrics category");
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            if (this.mParentFragment != null) {
                bundle.putInt("key_dialog_id", this.mDialogId);
                bundle.putInt("key_parent_fragment_id", this.mParentFragment.getId());
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            Fragment fragment = this.mParentFragment;
            if (fragment == null || !(fragment instanceof SettingsPreferenceFragment)) {
                return;
            }
            ((SettingsPreferenceFragment) fragment).onDialogShowing();
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Object valueOf;
            if (bundle != null) {
                this.mDialogId = bundle.getInt("key_dialog_id", 0);
                this.mParentFragment = getParentFragment();
                int i = bundle.getInt("key_parent_fragment_id", SettingsPreferenceFragment.ORDER_FIRST);
                if (this.mParentFragment == null) {
                    this.mParentFragment = getFragmentManager().findFragmentById(i);
                }
                Fragment fragment = this.mParentFragment;
                if (!(fragment instanceof DialogCreatable)) {
                    StringBuilder sb = new StringBuilder();
                    Fragment fragment2 = this.mParentFragment;
                    if (fragment2 != null) {
                        valueOf = fragment2.getClass().getName();
                    } else {
                        valueOf = Integer.valueOf(i);
                    }
                    sb.append(valueOf);
                    sb.append(" must implement ");
                    sb.append(DialogCreatable.class.getName());
                    throw new IllegalArgumentException(sb.toString());
                } else if (fragment instanceof SettingsPreferenceFragment) {
                    ((SettingsPreferenceFragment) fragment).mDialogFragment = this;
                }
            }
            return ((DialogCreatable) this.mParentFragment).onCreateDialog(this.mDialogId);
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            super.onCancel(dialogInterface);
            DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
            if (onCancelListener != null) {
                onCancelListener.onCancel(dialogInterface);
            }
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(dialogInterface);
            }
        }

        public int getDialogId() {
            return this.mDialogId;
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onDetach() {
            super.onDetach();
            Fragment fragment = this.mParentFragment;
            if ((fragment instanceof SettingsPreferenceFragment) && ((SettingsPreferenceFragment) fragment).mDialogFragment == this) {
                ((SettingsPreferenceFragment) this.mParentFragment).mDialogFragment = null;
            }
        }

        private void setParentFragment(DialogCreatable dialogCreatable) {
            this.mParentFragment = (Fragment) dialogCreatable;
        }

        private void setDialogId(int i) {
            this.mDialogId = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasNextButton() {
        return ((ButtonBarHandler) getActivity()).hasNextButton();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Button getNextButton() {
        return ((ButtonBarHandler) getActivity()).getNextButton();
    }

    public void finish() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            activity.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Intent getIntent() {
        if (getActivity() == null) {
            return null;
        }
        return getActivity().getIntent();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setResult(int i, Intent intent) {
        if (getActivity() == null) {
            return;
        }
        getActivity().setResult(i, intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setResult(int i) {
        if (getActivity() == null) {
            return;
        }
        getActivity().setResult(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFinishingOrDestroyed() {
        FragmentActivity activity = getActivity();
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceEnterprisePreferenceScreenTitle(String str, final int i) {
        getActivity().setTitle(this.mDevicePolicyManager.getResources().getString(str, new Supplier() { // from class: com.android.settings.SettingsPreferenceFragment$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$replaceEnterprisePreferenceScreenTitle$0;
                lambda$replaceEnterprisePreferenceScreenTitle$0 = SettingsPreferenceFragment.this.lambda$replaceEnterprisePreferenceScreenTitle$0(i);
                return lambda$replaceEnterprisePreferenceScreenTitle$0;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterprisePreferenceScreenTitle$0(int i) {
        return getString(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceEnterpriseStringSummary(String str, String str2, final int i) {
        Preference findPreference = findPreference(str);
        if (findPreference == null) {
            Log.d(TAG, "Could not find enterprise preference " + str);
            return;
        }
        findPreference.setSummary(this.mDevicePolicyManager.getResources().getString(str2, new Supplier() { // from class: com.android.settings.SettingsPreferenceFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$replaceEnterpriseStringSummary$1;
                lambda$replaceEnterpriseStringSummary$1 = SettingsPreferenceFragment.this.lambda$replaceEnterpriseStringSummary$1(i);
                return lambda$replaceEnterpriseStringSummary$1;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterpriseStringSummary$1(int i) {
        return getString(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceEnterpriseStringTitle(String str, String str2, final int i) {
        Preference findPreference = findPreference(str);
        if (findPreference == null) {
            Log.d(TAG, "Could not find enterprise preference " + str);
            return;
        }
        findPreference.setTitle(this.mDevicePolicyManager.getResources().getString(str2, new Supplier() { // from class: com.android.settings.SettingsPreferenceFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$replaceEnterpriseStringTitle$2;
                lambda$replaceEnterpriseStringTitle$2 = SettingsPreferenceFragment.this.lambda$replaceEnterpriseStringTitle$2(i);
                return lambda$replaceEnterpriseStringTitle$2;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterpriseStringTitle$2(int i) {
        return getString(i);
    }
}
