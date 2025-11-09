package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.security.IKeyChainService;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.android.settings.dashboard.DashboardFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.common.collect.ImmutableList;
import java.util.List;
/* loaded from: classes.dex */
public class TrustedCredentialsSettings extends DashboardFragment {
    static final ImmutableList<Tab> TABS = ImmutableList.of(Tab.SYSTEM, Tab.USER);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "TrustedCredentialsSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 92;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R$string.trusted_credentials);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.placeholder_preference_screen;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        View findViewById = view.findViewById(R$id.tab_container);
        findViewById.setVisibility(0);
        ViewPager2 viewPager2 = (ViewPager2) findViewById.findViewById(R$id.view_pager);
        viewPager2.setAdapter(new FragmentAdapter(this));
        viewPager2.setUserInputEnabled(false);
        Intent intent = getActivity().getIntent();
        if (intent != null && "com.android.settings.TRUSTED_CREDENTIALS_USER".equals(intent.getAction())) {
            viewPager2.setCurrentItem(TABS.indexOf(Tab.USER), false);
        }
        new TabLayoutMediator((TabLayout) findViewById.findViewById(R$id.tabs), viewPager2, false, false, new TabLayoutMediator.TabConfigurationStrategy() { // from class: com.android.settings.TrustedCredentialsSettings$$ExternalSyntheticLambda0
            @Override // com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
            public final void onConfigureTab(TabLayout.Tab tab, int i) {
                TrustedCredentialsSettings.lambda$onViewCreated$0(tab, i);
            }
        }).attach();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onViewCreated$0(TabLayout.Tab tab, int i) {
        tab.setText(TABS.get(i).mLabel);
    }

    /* loaded from: classes.dex */
    private static class FragmentAdapter extends FragmentStateAdapter {
        FragmentAdapter(Fragment fragment) {
            super(fragment);
        }

        @Override // androidx.viewpager2.adapter.FragmentStateAdapter
        public Fragment createFragment(int i) {
            TrustedCredentialsFragment trustedCredentialsFragment = new TrustedCredentialsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("tab", i);
            trustedCredentialsFragment.setArguments(bundle);
            return trustedCredentialsFragment;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return TrustedCredentialsSettings.TABS.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Tab {
        SYSTEM(R$string.trusted_credentials_system_tab, true),
        USER(R$string.trusted_credentials_user_tab, false);
        
        private final int mLabel;
        final boolean mSwitch;

        Tab(int i, boolean z) {
            this.mLabel = i;
            this.mSwitch = z;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public List<String> getAliases(IKeyChainService iKeyChainService) throws RemoteException {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[ordinal()];
            if (i != 1) {
                if (i == 2) {
                    return iKeyChainService.getUserCaAliases().getList();
                }
                throw new AssertionError();
            }
            return iKeyChainService.getSystemCaAliases().getList();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean deleted(IKeyChainService iKeyChainService, String str) throws RemoteException {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[ordinal()];
            if (i != 1) {
                if (i == 2) {
                    return false;
                }
                throw new AssertionError();
            }
            return !iKeyChainService.containsCaAlias(str);
        }
    }

    /* renamed from: com.android.settings.TrustedCredentialsSettings$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab;

        static {
            int[] iArr = new int[Tab.values().length];
            $SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab = iArr;
            try {
                iArr[Tab.SYSTEM.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$TrustedCredentialsSettings$Tab[Tab.USER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }
}
