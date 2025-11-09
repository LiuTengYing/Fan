package com.android.settings.wifi.calling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.network.ActiveSubscriptionsListener;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.widget.RtlCompatibleViewPager;
import com.android.settings.widget.SlidingTabLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class WifiCallingSettings extends SettingsPreferenceFragment {
    private static final int[] EMPTY_SUB_ID_LIST = new int[0];
    private int mConstructionSubId;
    private WifiCallingViewPagerAdapter mPagerAdapter;
    private List<SubscriptionInfo> mSil;
    private ActiveSubscriptionsListener mSubscriptionChangeListener;
    private SlidingTabLayout mTabLayout;
    private RtlCompatibleViewPager mViewPager;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$containsSubId$1(int i, int i2) {
        return i2 == i;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 105;
    }

    /* loaded from: classes.dex */
    private final class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        private InternalViewPagerListener() {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            WifiCallingSettings.this.updateTitleForCurrentSub();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.wifi_calling_settings_tabs, viewGroup, false);
        this.mTabLayout = (SlidingTabLayout) inflate.findViewById(R$id.sliding_tabs);
        this.mViewPager = (RtlCompatibleViewPager) inflate.findViewById(R$id.view_pager);
        WifiCallingViewPagerAdapter wifiCallingViewPagerAdapter = new WifiCallingViewPagerAdapter(getChildFragmentManager(), this.mViewPager);
        this.mPagerAdapter = wifiCallingViewPagerAdapter;
        this.mViewPager.setAdapter(wifiCallingViewPagerAdapter);
        this.mViewPager.addOnPageChangeListener(new InternalViewPagerListener());
        maybeSetViewForSubId();
        return inflate;
    }

    private int getConstructionSubId(Bundle bundle) {
        Intent intent = getActivity().getIntent();
        int intExtra = intent != null ? intent.getIntExtra("android.provider.extra.SUB_ID", -1) : -1;
        return (intExtra != -1 || bundle == null) ? intExtra : bundle.getInt("android.provider.extra.SUB_ID", -1);
    }

    private void maybeSetViewForSubId() {
        if (this.mSil == null) {
            return;
        }
        int i = this.mConstructionSubId;
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            for (SubscriptionInfo subscriptionInfo : this.mSil) {
                if (i == subscriptionInfo.getSubscriptionId()) {
                    this.mViewPager.setCurrentItem(this.mSil.indexOf(subscriptionInfo));
                    return;
                }
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        this.mConstructionSubId = getConstructionSubId(bundle);
        super.onCreate(bundle);
        Log.d("WifiCallingSettings", "SubId=" + this.mConstructionSubId);
        if (this.mConstructionSubId != -1) {
            this.mSubscriptionChangeListener = getSubscriptionChangeListener(getContext());
        }
        this.mSil = updateSubList();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        List<SubscriptionInfo> list = this.mSil;
        if (list != null && list.size() > 1) {
            this.mTabLayout.setViewPager(this.mViewPager);
        } else {
            this.mTabLayout.setVisibility(8);
        }
        updateTitleForCurrentSub();
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.start();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
        }
        super.onStop();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("android.provider.extra.SUB_ID", this.mConstructionSubId);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_wifi_calling;
    }

    /* loaded from: classes.dex */
    final class WifiCallingViewPagerAdapter extends FragmentPagerAdapter {
        private final RtlCompatibleViewPager mViewPager;

        public WifiCallingViewPagerAdapter(FragmentManager fragmentManager, RtlCompatibleViewPager rtlCompatibleViewPager) {
            super(fragmentManager);
            this.mViewPager = rtlCompatibleViewPager;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            return String.valueOf(SubscriptionUtil.getUniqueSubscriptionDisplayName((SubscriptionInfo) WifiCallingSettings.this.mSil.get(i), WifiCallingSettings.this.getContext()));
        }

        @Override // androidx.fragment.app.FragmentPagerAdapter
        public Fragment getItem(int i) {
            int subscriptionId = ((SubscriptionInfo) WifiCallingSettings.this.mSil.get(i)).getSubscriptionId();
            Log.d("WifiCallingSettings", "Adapter getItem " + i + " for subId=" + subscriptionId);
            Bundle bundle = new Bundle();
            bundle.putBoolean("need_search_icon_in_action_bar", false);
            bundle.putInt("subId", subscriptionId);
            WifiCallingSettingsForSub wifiCallingSettingsForSub = new WifiCallingSettingsForSub();
            wifiCallingSettingsForSub.setArguments(bundle);
            return wifiCallingSettingsForSub;
        }

        @Override // androidx.fragment.app.FragmentPagerAdapter, androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            Log.d("WifiCallingSettings", "Adapter instantiateItem " + i);
            return super.instantiateItem(viewGroup, this.mViewPager.getRtlAwareIndex(i));
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            if (WifiCallingSettings.this.mSil == null) {
                Log.d("WifiCallingSettings", "Adapter getCount null mSil ");
                return 0;
            }
            Log.d("WifiCallingSettings", "Adapter getCount " + WifiCallingSettings.this.mSil.size());
            return WifiCallingSettings.this.mSil.size();
        }
    }

    protected List<SubscriptionInfo> getSelectableSubscriptions(Context context) {
        return SubscriptionUtil.getSelectableSubscriptionInfoList(context);
    }

    private List<SubscriptionInfo> updateSubList() {
        List<SubscriptionInfo> selectableSubscriptions = getSelectableSubscriptions(getContext());
        if (selectableSubscriptions == null) {
            return Collections.emptyList();
        }
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return Collections.emptyList();
        }
        int intExtra = intent.getIntExtra("android.provider.extra.SUB_ID", -1);
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : selectableSubscriptions) {
            try {
                if (subscriptionInfo.getSubscriptionId() == intExtra && queryImsState(intExtra).isWifiCallingProvisioned()) {
                    arrayList.add(subscriptionInfo);
                }
            } catch (Exception unused) {
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTitleForCurrentSub() {
        if (CollectionUtils.size(this.mSil) > 0) {
            getActivity().getActionBar().setTitle(SubscriptionManager.getResourcesForSubId(getContext(), this.mSil.get(this.mViewPager.getCurrentItem()).getSubscriptionId()).getString(R$string.wifi_calling_settings_title));
        }
    }

    protected WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(getContext(), i);
    }

    protected ActiveSubscriptionsListener getSubscriptionChangeListener(final Context context) {
        return new ActiveSubscriptionsListener(context.getMainLooper(), context) { // from class: com.android.settings.wifi.calling.WifiCallingSettings.1
            @Override // com.android.settings.network.ActiveSubscriptionsListener
            public void onChanged() {
                WifiCallingSettings.this.onSubscriptionChange(context);
            }
        };
    }

    protected void onSubscriptionChange(Context context) {
        if (this.mSubscriptionChangeListener == null) {
            return;
        }
        int[] subscriptionIdList = subscriptionIdList(this.mSil);
        List<SubscriptionInfo> updateSubList = updateSubList();
        int[] subscriptionIdList2 = subscriptionIdList(updateSubList);
        if (subscriptionIdList2.length > 0) {
            if (subscriptionIdList.length == 0) {
                this.mSil = updateSubList;
                return;
            } else if (subscriptionIdList.length == subscriptionIdList2.length && (!containsSubId(subscriptionIdList, this.mConstructionSubId) || containsSubId(subscriptionIdList2, this.mConstructionSubId))) {
                this.mSil = updateSubList;
                return;
            }
        }
        Log.d("WifiCallingSettings", "Closed subId=" + this.mConstructionSubId + " due to subscription change: " + Arrays.toString(subscriptionIdList) + " -> " + Arrays.toString(subscriptionIdList2));
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mSubscriptionChangeListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
            this.mSubscriptionChangeListener = null;
        }
        finishFragment();
    }

    protected int[] subscriptionIdList(List<SubscriptionInfo> list) {
        if (list == null) {
            return EMPTY_SUB_ID_LIST;
        }
        return list.stream().mapToInt(new ToIntFunction() { // from class: com.android.settings.wifi.calling.WifiCallingSettings$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$subscriptionIdList$0;
                lambda$subscriptionIdList$0 = WifiCallingSettings.lambda$subscriptionIdList$0((SubscriptionInfo) obj);
                return lambda$subscriptionIdList$0;
            }
        }).toArray();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$subscriptionIdList$0(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return -1;
        }
        return subscriptionInfo.getSubscriptionId();
    }

    protected boolean containsSubId(int[] iArr, final int i) {
        return Arrays.stream(iArr).anyMatch(new IntPredicate() { // from class: com.android.settings.wifi.calling.WifiCallingSettings$$ExternalSyntheticLambda1
            @Override // java.util.function.IntPredicate
            public final boolean test(int i2) {
                boolean lambda$containsSubId$1;
                lambda$containsSubId$1 = WifiCallingSettings.lambda$containsSubId$1(i, i2);
                return lambda$containsSubId$1;
            }
        });
    }
}
