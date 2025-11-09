package com.android.settings.dream;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dream.DreamPickerController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.dream.DreamBackend;
import com.android.settingslib.widget.LayoutPreference;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DreamPickerController extends BasePreferenceController {
    private DreamBackend.DreamInfo mActiveDream;
    private DreamAdapter mAdapter;
    private final DreamBackend mBackend;
    private final List<DreamBackend.DreamInfo> mDreamInfos;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DreamPickerController(Context context, String str) {
        this(context, str, DreamBackend.getInstance(context));
    }

    public DreamPickerController(Context context, String str, DreamBackend dreamBackend) {
        super(context, str);
        this.mBackend = dreamBackend;
        List<DreamBackend.DreamInfo> dreamInfos = dreamBackend.getDreamInfos();
        this.mDreamInfos = dreamInfos;
        this.mActiveDream = getActiveDreamInfo(dreamInfos);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mDreamInfos.size() > 0 ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        DreamAdapter dreamAdapter = new DreamAdapter(R$layout.dream_preference_layout, (List) this.mDreamInfos.stream().map(new Function() { // from class: com.android.settings.dream.DreamPickerController$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                DreamPickerController.DreamItem lambda$displayPreference$0;
                lambda$displayPreference$0 = DreamPickerController.this.lambda$displayPreference$0((DreamBackend.DreamInfo) obj);
                return lambda$displayPreference$0;
            }
        }).collect(Collectors.toList()));
        this.mAdapter = dreamAdapter;
        dreamAdapter.setEnabled(this.mBackend.isEnabled());
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (layoutPreference == null) {
            return;
        }
        RecyclerView recyclerView = (RecyclerView) layoutPreference.findViewById(R$id.dream_list);
        recyclerView.setLayoutManager(new AutoFitGridLayoutManager(this.mContext));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(this.mContext, R$dimen.dream_preference_card_padding));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.mAdapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ DreamItem lambda$displayPreference$0(DreamBackend.DreamInfo dreamInfo) {
        return new DreamItem(dreamInfo);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        DreamAdapter dreamAdapter = this.mAdapter;
        if (dreamAdapter != null) {
            dreamAdapter.setEnabled(preference.isEnabled());
        }
    }

    private static DreamBackend.DreamInfo getActiveDreamInfo(List<DreamBackend.DreamInfo> list) {
        return list.stream().filter(new Predicate() { // from class: com.android.settings.dream.DreamPickerController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean z;
                z = ((DreamBackend.DreamInfo) obj).isActive;
                return z;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DreamItem implements IDreamItem {
        DreamBackend.DreamInfo mDreamInfo;

        DreamItem(DreamBackend.DreamInfo dreamInfo) {
            this.mDreamInfo = dreamInfo;
        }

        @Override // com.android.settings.dream.IDreamItem
        public CharSequence getTitle() {
            return this.mDreamInfo.caption;
        }

        @Override // com.android.settings.dream.IDreamItem
        public CharSequence getSummary() {
            return this.mDreamInfo.description;
        }

        @Override // com.android.settings.dream.IDreamItem
        public Drawable getIcon() {
            return this.mDreamInfo.icon;
        }

        @Override // com.android.settings.dream.IDreamItem
        public void onItemClicked() {
            DreamPickerController.this.mActiveDream = this.mDreamInfo;
            DreamPickerController.this.mBackend.setActiveDream(this.mDreamInfo.componentName);
            DreamPickerController.this.mMetricsFeatureProvider.action(0, 1788, 0, this.mDreamInfo.componentName.flattenToString(), 1);
        }

        @Override // com.android.settings.dream.IDreamItem
        public void onCustomizeClicked() {
            DreamPickerController.this.mBackend.launchSettings(((AbstractPreferenceController) DreamPickerController.this).mContext, this.mDreamInfo);
        }

        @Override // com.android.settings.dream.IDreamItem
        public Drawable getPreviewImage() {
            return this.mDreamInfo.previewImage;
        }

        @Override // com.android.settings.dream.IDreamItem
        public boolean isActive() {
            if (!DreamPickerController.this.mAdapter.getEnabled() || DreamPickerController.this.mActiveDream == null) {
                return false;
            }
            return this.mDreamInfo.componentName.equals(DreamPickerController.this.mActiveDream.componentName);
        }

        @Override // com.android.settings.dream.IDreamItem
        public boolean allowCustomization() {
            return isActive() && this.mDreamInfo.settingsComponentName != null;
        }
    }
}
