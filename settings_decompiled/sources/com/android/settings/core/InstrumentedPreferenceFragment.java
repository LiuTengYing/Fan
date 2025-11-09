package com.android.settings.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.settings.R$drawable;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.survey.SurveyMixin;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.SettingsJankMonitor;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
/* loaded from: classes.dex */
public abstract class InstrumentedPreferenceFragment extends ObservablePreferenceFragment implements Instrumentable {
    private static final String TAG = "InstrumentedPrefFrag";
    protected final int PLACEHOLDER_METRIC = 10000;
    protected MetricsFeatureProvider mMetricsFeatureProvider;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private VisibilityLoggerMixin mVisibilityLoggerMixin;

    /* JADX INFO: Access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return -1;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mVisibilityLoggerMixin = new VisibilityLoggerMixin(getMetricsCategory(), this.mMetricsFeatureProvider);
        getSettingsLifecycle().addObserver(this.mVisibilityLoggerMixin);
        getSettingsLifecycle().addObserver(new SurveyMixin(this, getClass().getSimpleName()));
        super.onAttach(context);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getPreferenceManager().setOnPreferenceTreeClickListener(new PreferenceManager.OnPreferenceTreeClickListener() { // from class: com.android.settings.core.InstrumentedPreferenceFragment$$ExternalSyntheticLambda0
            @Override // androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
            public final boolean onPreferenceTreeClick(Preference preference) {
                boolean lambda$onStart$0;
                lambda$onStart$0 = InstrumentedPreferenceFragment.this.lambda$onStart$0(preference);
                return lambda$onStart$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onStart$0(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SettingsJankMonitor.detectSwitchPreferenceClickJank(getListView(), (SwitchPreference) preference);
        }
        return onPreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        this.mVisibilityLoggerMixin.setSourceMetricsCategory(getActivity());
        RecyclerView listView = getListView();
        if (listView != null) {
            OnScrollListener onScrollListener = new OnScrollListener(getClass().getName());
            this.mOnScrollListener = onScrollListener;
            listView.addOnScrollListener(onScrollListener);
            listView.setScrollBarStyle(R$drawable.nest_scroll_view_style);
        }
        super.onResume();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        RecyclerView listView = getListView();
        RecyclerView.OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            listView.removeOnScrollListener(onScrollListener);
            this.mOnScrollListener = null;
        }
        super.onPause();
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId > 0) {
            addPreferencesFromResource(preferenceScreenResId);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        updateActivityTitleWithScreenTitle(getPreferenceScreen());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.DialogPreference.TargetFragment
    public <T extends Preference> T findPreference(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return (T) super.findPreference(charSequence);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        writePreferenceClickMetric(preference);
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Context getPrefContext() {
        return getPreferenceManager().getContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeElapsedTimeMetric(int i, String str) {
        this.mVisibilityLoggerMixin.writeElapsedTimeMetric(i, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writePreferenceClickMetric(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
    }

    private void updateActivityTitleWithScreenTitle(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != null) {
            CharSequence title = preferenceScreen.getTitle();
            if (!TextUtils.isEmpty(title)) {
                getActivity().setTitle(title);
                return;
            }
            Log.w(TAG, "Screen title missing for fragment " + getClass().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class OnScrollListener extends RecyclerView.OnScrollListener {
        private final String mClassName;
        private final InteractionJankMonitor mMonitor;

        private OnScrollListener(String str) {
            this.mMonitor = InteractionJankMonitor.getInstance();
            this.mClassName = str;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0) {
                this.mMonitor.end(28);
            } else if (i != 1) {
            } else {
                this.mMonitor.begin(InteractionJankMonitor.Configuration.Builder.withView(28, recyclerView).setTag(this.mClassName));
            }
        }
    }
}
