package com.android.settings.dashboard.profileselector;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class ProfileSelectFragment extends DashboardFragment {
    private ViewGroup mContentView;
    private ViewPager2 mViewPager;

    protected boolean forceUpdateHeight() {
        return false;
    }

    public abstract Fragment[] getFragments();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ProfileSelectFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public int getTitleResId() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mContentView = (ViewGroup) super.onCreateView(layoutInflater, viewGroup, bundle);
        FragmentActivity activity = getActivity();
        int titleResId = getTitleResId();
        if (titleResId > 0) {
            activity.setTitle(titleResId);
        }
        int tabId = getTabId(activity, getArguments());
        View findViewById = this.mContentView.findViewById(R$id.tab_container);
        ViewPager2 viewPager2 = (ViewPager2) findViewById.findViewById(R$id.view_pager);
        this.mViewPager = viewPager2;
        viewPager2.setAdapter(new ViewPagerAdapter(this));
        TabLayout tabLayout = (TabLayout) findViewById.findViewById(R$id.tabs);
        new TabLayoutMediator(tabLayout, this.mViewPager, new TabLayoutMediator.TabConfigurationStrategy() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectFragment$$ExternalSyntheticLambda0
            @Override // com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
            public final void onConfigureTab(TabLayout.Tab tab, int i) {
                ProfileSelectFragment.this.lambda$onCreateView$0(tab, i);
            }
        }).attach();
        this.mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectFragment.1
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                ProfileSelectFragment.this.updateHeight(i);
            }
        });
        findViewById.setVisibility(0);
        tabLayout.getTabAt(tabId).select();
        ((FrameLayout) this.mContentView.findViewById(16908351)).setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        RecyclerView listView = getListView();
        listView.setOverScrollMode(2);
        Utils.setActionBarShadowAnimation(activity, getSettingsLifecycle(), listView);
        return this.mContentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(TabLayout.Tab tab, int i) {
        tab.setText(getPageTitle(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeight(int i) {
        ViewPagerAdapter viewPagerAdapter;
        View view;
        if (forceUpdateHeight() && (viewPagerAdapter = (ViewPagerAdapter) this.mViewPager.getAdapter()) != null && viewPagerAdapter.getItemCount() > i && (view = viewPagerAdapter.createFragment(i).getView()) != null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
            int i2 = this.mViewPager.getLayoutParams().height;
            int measuredHeight = view.getMeasuredHeight();
            if (measuredHeight == 0 || i2 == measuredHeight) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = this.mViewPager.getLayoutParams();
            layoutParams.height = measuredHeight;
            this.mViewPager.setLayoutParams(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.placeholder_preference_screen;
    }

    int getTabId(Activity activity, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(":settings:show_fragment_tab", -1);
            if (i != -1) {
                return i;
            }
            if (UserManager.get(activity).isManagedProfile(bundle.getInt("android.intent.extra.USER_ID", UserHandle.SYSTEM.getIdentifier()))) {
                return 1;
            }
        }
        return UserManager.get(activity).isManagedProfile(activity.getIntent().getContentUserHint()) ? 1 : 0;
    }

    private CharSequence getPageTitle(int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class);
        if (i == 1) {
            return devicePolicyManager.getResources().getString("Settings.WORK_CATEGORY_HEADER", new Supplier() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectFragment$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getPageTitle$1;
                    lambda$getPageTitle$1 = ProfileSelectFragment.this.lambda$getPageTitle$1();
                    return lambda$getPageTitle$1;
                }
            });
        }
        return devicePolicyManager.getResources().getString("Settings.PERSONAL_CATEGORY_HEADER", new Supplier() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectFragment$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getPageTitle$2;
                lambda$getPageTitle$2 = ProfileSelectFragment.this.lambda$getPageTitle$2();
                return lambda$getPageTitle$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getPageTitle$1() {
        return getContext().getString(R$string.category_work);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getPageTitle$2() {
        return getContext().getString(R$string.category_personal);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ViewPagerAdapter extends FragmentStateAdapter {
        private final Fragment[] mChildFragments;

        ViewPagerAdapter(ProfileSelectFragment profileSelectFragment) {
            super(profileSelectFragment);
            this.mChildFragments = profileSelectFragment.getFragments();
        }

        @Override // androidx.viewpager2.adapter.FragmentStateAdapter
        public Fragment createFragment(int i) {
            return this.mChildFragments[i];
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mChildFragments.length;
        }
    }
}
