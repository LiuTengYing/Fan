package com.android.settings.panel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.sprdpower.IPowerManagerEx;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.SliceMetadata;
import androidx.slice.widget.SliceLiveData;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$array;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.google.android.setupdesign.DividerItemDecoration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class PanelFragment extends Fragment {
    private static final boolean SUPPORT_SUPER_POWER_SAVE;
    private PanelSlicesAdapter mAdapter;
    private Button mDoneButton;
    private LinearLayout mHeaderLayout;
    private TextView mHeaderSubtitle;
    private TextView mHeaderTitle;
    @VisibleForTesting
    View mLayoutView;
    private int mMaxHeight;
    private MetricsFeatureProvider mMetricsProvider;
    private PanelContent mPanel;
    private String mPanelClosedKey;
    private boolean mPanelCreating;
    private LinearLayout mPanelHeader;
    private RecyclerView mPanelSlices;
    @VisibleForTesting
    PanelSlicesLoaderCountdownLatch mPanelSlicesLoaderCountdownLatch;
    private IPowerManagerEx mPowerManagerEx;
    private ProgressBar mProgressBar;
    private Button mSeeMoreButton;
    private LinearLayout mTitleGroup;
    private ImageView mTitleIcon;
    private TextView mTitleView;
    private final Map<Uri, LiveData<Slice>> mSliceLiveData = new LinkedHashMap();
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda0
        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public final boolean onPreDraw() {
            boolean lambda$new$0;
            lambda$new$0 = PanelFragment.lambda$new$0();
            return lambda$new$0;
        }
    };
    private final ViewTreeObserver.OnGlobalLayoutListener mPanelLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.panel.PanelFragment.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            if (PanelFragment.this.mLayoutView.getHeight() > PanelFragment.this.mMaxHeight) {
                ViewGroup.LayoutParams layoutParams = PanelFragment.this.mLayoutView.getLayoutParams();
                layoutParams.height = PanelFragment.this.mMaxHeight;
                PanelFragment.this.mLayoutView.setLayoutParams(layoutParams);
            }
        }
    };
    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.panel.PanelFragment.2
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            PanelFragment.this.animateIn();
            if (PanelFragment.this.mPanelSlices != null) {
                PanelFragment.this.mPanelSlices.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            PanelFragment.this.mPanelCreating = false;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0() {
        return false;
    }

    static {
        SUPPORT_SUPER_POWER_SAVE = 1 == SystemProperties.getInt("ro.sys.pwctl.ultrasaving", 0);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.panel_layout, viewGroup, false);
        this.mLayoutView = inflate;
        inflate.getViewTreeObserver().addOnGlobalLayoutListener(this.mPanelLayoutListener);
        this.mMaxHeight = getResources().getDimensionPixelSize(R$dimen.output_switcher_slice_max_height);
        this.mPanelCreating = true;
        createPanelContent();
        return this.mLayoutView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updatePanelWithAnimation() {
        this.mPanelCreating = true;
        AnimatorSet buildAnimatorSet = buildAnimatorSet(this.mLayoutView, 0.0f, this.mLayoutView.findViewById(R$id.panel_container).getHeight(), 1.0f, 0.0f, 200);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0.0f, 1.0f);
        buildAnimatorSet.play(valueAnimator);
        buildAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.panel.PanelFragment.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PanelFragment.this.createPanelContent();
            }
        });
        buildAnimatorSet.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPanelCreating() {
        return this.mPanelCreating;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createPanelContent() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = this.mLayoutView;
        if (view == null) {
            activity.finish();
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = -2;
        this.mLayoutView.setLayoutParams(layoutParams);
        this.mPanelSlices = (RecyclerView) this.mLayoutView.findViewById(R$id.panel_parent_layout);
        this.mSeeMoreButton = (Button) this.mLayoutView.findViewById(R$id.see_more);
        this.mDoneButton = (Button) this.mLayoutView.findViewById(R$id.done);
        this.mTitleView = (TextView) this.mLayoutView.findViewById(R$id.panel_title);
        this.mPanelHeader = (LinearLayout) this.mLayoutView.findViewById(R$id.panel_header);
        this.mTitleIcon = (ImageView) this.mLayoutView.findViewById(R$id.title_icon);
        this.mTitleGroup = (LinearLayout) this.mLayoutView.findViewById(R$id.title_group);
        this.mHeaderLayout = (LinearLayout) this.mLayoutView.findViewById(R$id.header_layout);
        this.mHeaderTitle = (TextView) this.mLayoutView.findViewById(R$id.header_title);
        this.mHeaderSubtitle = (TextView) this.mLayoutView.findViewById(R$id.header_subtitle);
        this.mProgressBar = (ProgressBar) this.mLayoutView.findViewById(R$id.progress_bar);
        this.mPanelSlices.setVisibility(8);
        Bundle arguments = getArguments();
        String string = arguments.getString("PANEL_CALLING_PACKAGE_NAME");
        PanelContent panel = FeatureFactory.getFactory(activity).getPanelFeatureProvider().getPanel(activity, arguments);
        this.mPanel = panel;
        if (panel == null) {
            activity.finish();
            return;
        }
        panel.registerCallback(new LocalPanelCallback());
        if (this.mPanel instanceof LifecycleObserver) {
            getLifecycle().addObserver((LifecycleObserver) this.mPanel);
        }
        this.mMetricsProvider = FeatureFactory.getFactory(activity).getMetricsFeatureProvider();
        updateProgressBar();
        this.mPanelSlices.setLayoutManager(new LinearLayoutManager(activity));
        this.mLayoutView.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        loadAllSlices();
        IconCompat icon = this.mPanel.getIcon();
        CharSequence title = this.mPanel.getTitle();
        CharSequence subTitle = this.mPanel.getSubTitle();
        if (icon != null || (subTitle != null && subTitle.length() > 0)) {
            enablePanelHeader(icon, title, subTitle);
        } else {
            enableTitle(title);
        }
        this.mSeeMoreButton.setOnClickListener(getSeeMoreListener());
        this.mDoneButton.setOnClickListener(getCloseListener());
        if (this.mPanel.isCustomizedButtonUsed()) {
            enableCustomizedButton();
        } else if (this.mPanel.getSeeMoreIntent() == null || isSuperPower()) {
            this.mSeeMoreButton.setVisibility(8);
        }
        this.mMetricsProvider.action(0, 1, this.mPanel.getMetricsCategory(), string, 0);
    }

    private boolean isSuperPower() {
        IPowerManagerEx asInterface = IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex"));
        this.mPowerManagerEx = asInterface;
        try {
            if (SUPPORT_SUPER_POWER_SAVE) {
                return asInterface.getPowerSaveMode() == 4;
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private void enablePanelHeader(IconCompat iconCompat, CharSequence charSequence, CharSequence charSequence2) {
        this.mTitleView.setVisibility(8);
        this.mPanelHeader.setVisibility(0);
        this.mPanelHeader.setAccessibilityPaneTitle(charSequence);
        this.mHeaderTitle.setText(charSequence);
        this.mHeaderSubtitle.setText(charSequence2);
        this.mHeaderSubtitle.setAccessibilityPaneTitle(charSequence2);
        if (iconCompat != null) {
            this.mTitleGroup.setVisibility(0);
            this.mHeaderLayout.setGravity(3);
            this.mTitleIcon.setImageIcon(iconCompat.toIcon(getContext()));
            if (this.mPanel.getHeaderIconIntent() != null) {
                this.mTitleIcon.setOnClickListener(getHeaderIconListener());
                this.mTitleIcon.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                return;
            }
            int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.output_switcher_panel_icon_size);
            this.mTitleIcon.setLayoutParams(new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize));
            return;
        }
        this.mTitleGroup.setVisibility(8);
        this.mHeaderLayout.setGravity(1);
    }

    private void enableTitle(CharSequence charSequence) {
        this.mPanelHeader.setVisibility(8);
        this.mTitleView.setVisibility(0);
        this.mTitleView.setAccessibilityPaneTitle(charSequence);
        this.mTitleView.setText(charSequence);
    }

    private void enableCustomizedButton() {
        CharSequence customizedButtonTitle = this.mPanel.getCustomizedButtonTitle();
        if (TextUtils.isEmpty(customizedButtonTitle)) {
            this.mSeeMoreButton.setVisibility(8);
            return;
        }
        this.mSeeMoreButton.setVisibility(0);
        this.mSeeMoreButton.setText(customizedButtonTitle);
    }

    private void updateProgressBar() {
        if (this.mPanel.isProgressBarVisible()) {
            this.mProgressBar.setVisibility(0);
        } else {
            this.mProgressBar.setVisibility(8);
        }
    }

    private void loadAllSlices() {
        this.mSliceLiveData.clear();
        List<Uri> slices = this.mPanel.getSlices();
        this.mPanelSlicesLoaderCountdownLatch = new PanelSlicesLoaderCountdownLatch(slices.size());
        for (final Uri uri : slices) {
            LiveData<Slice> fromUri = SliceLiveData.fromUri(getActivity(), uri, new SliceLiveData.OnErrorListener() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda4
                @Override // androidx.slice.widget.SliceLiveData.OnErrorListener
                public final void onSliceError(int i, Throwable th) {
                    PanelFragment.this.lambda$loadAllSlices$1(uri, i, th);
                }
            });
            this.mSliceLiveData.put(uri, fromUri);
            fromUri.observe(getViewLifecycleOwner(), new Observer() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda5
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    PanelFragment.this.lambda$loadAllSlices$3(uri, (Slice) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAllSlices$1(Uri uri, int i, Throwable th) {
        removeSliceLiveData(uri);
        this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAllSlices$3(final Uri uri, Slice slice) {
        if (this.mPanelSlicesLoaderCountdownLatch.isSliceLoaded(uri)) {
            return;
        }
        SliceMetadata from = SliceMetadata.from(getActivity(), slice);
        if (slice == null || from.isErrorSlice()) {
            removeSliceLiveData(uri);
            this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
        } else if (from.getLoadingState() == 2) {
            this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
        } else {
            new Handler().postDelayed(new Runnable() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    PanelFragment.this.lambda$loadAllSlices$2(uri);
                }
            }, 250L);
        }
        loadPanelWhenReady();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAllSlices$2(Uri uri) {
        this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
        loadPanelWhenReady();
    }

    private void removeSliceLiveData(Uri uri) {
        if (Arrays.asList(getResources().getStringArray(R$array.config_panel_keep_observe_uri)).contains(uri.toString())) {
            return;
        }
        this.mSliceLiveData.remove(uri);
    }

    private void loadPanelWhenReady() {
        if (this.mPanelSlicesLoaderCountdownLatch.isPanelReadyToLoad()) {
            PanelSlicesAdapter panelSlicesAdapter = new PanelSlicesAdapter(this, this.mSliceLiveData, this.mPanel.getMetricsCategory());
            this.mAdapter = panelSlicesAdapter;
            this.mPanelSlices.setAdapter(panelSlicesAdapter);
            this.mPanelSlices.getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
            this.mPanelSlices.setVisibility(0);
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity);
            dividerItemDecoration.setDividerCondition(1);
            if (this.mPanelSlices.getItemDecorationCount() == 0) {
                this.mPanelSlices.addItemDecoration(dividerItemDecoration);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateIn() {
        AnimatorSet buildAnimatorSet = buildAnimatorSet(this.mLayoutView, this.mLayoutView.findViewById(R$id.panel_container).getHeight(), 0.0f, 0.0f, 1.0f, 250);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0.0f, 1.0f);
        buildAnimatorSet.play(valueAnimator);
        buildAnimatorSet.start();
        this.mLayoutView.getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    }

    private static AnimatorSet buildAnimatorSet(View view, float f, float f2, float f3, float f4, int i) {
        View findViewById = view.findViewById(R$id.panel_container);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(i);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(ObjectAnimator.ofFloat(findViewById, View.TRANSLATION_Y, f, f2), ObjectAnimator.ofFloat(findViewById, View.ALPHA, f3, f4));
        return animatorSet;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (TextUtils.isEmpty(this.mPanelClosedKey)) {
            this.mPanelClosedKey = "others";
        }
        View view = this.mLayoutView;
        if (view != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(this.mPanelLayoutListener);
        }
        PanelContent panelContent = this.mPanel;
        if (panelContent != null) {
            this.mMetricsProvider.action(0, 2, panelContent.getMetricsCategory(), this.mPanelClosedKey, 0);
        }
    }

    @VisibleForTesting
    View.OnClickListener getSeeMoreListener() {
        return new View.OnClickListener() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PanelFragment.this.lambda$getSeeMoreListener$4(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getSeeMoreListener$4(View view) {
        this.mPanelClosedKey = "see_more";
        FragmentActivity activity = getActivity();
        if (this.mPanel.isCustomizedButtonUsed()) {
            this.mPanel.onClickCustomizedButton(activity);
            return;
        }
        activity.startActivityForResult(this.mPanel.getSeeMoreIntent(), 0);
        activity.finish();
    }

    @VisibleForTesting
    View.OnClickListener getCloseListener() {
        return new View.OnClickListener() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PanelFragment.this.lambda$getCloseListener$5(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getCloseListener$5(View view) {
        this.mPanelClosedKey = "done";
        getActivity().finish();
    }

    @VisibleForTesting
    View.OnClickListener getHeaderIconListener() {
        return new View.OnClickListener() { // from class: com.android.settings.panel.PanelFragment$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PanelFragment.this.lambda$getHeaderIconListener$6(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getHeaderIconListener$6(View view) {
        getActivity().startActivity(this.mPanel.getHeaderIconIntent());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPanelViewType() {
        return this.mPanel.getViewType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class LocalPanelCallback implements PanelContentCallback {
        LocalPanelCallback() {
        }

        @Override // com.android.settings.panel.PanelContentCallback
        public void forceClose() {
            PanelFragment.this.mPanelClosedKey = "others";
            getFragmentActivity().finish();
        }

        @VisibleForTesting
        FragmentActivity getFragmentActivity() {
            return PanelFragment.this.getActivity();
        }
    }
}
