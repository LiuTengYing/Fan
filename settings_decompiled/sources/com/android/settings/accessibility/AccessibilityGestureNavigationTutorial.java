package com.android.settings.accessibility;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Preconditions;
import androidx.core.widget.TextViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieListener;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$style;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class AccessibilityGestureNavigationTutorial {
    private static final DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda1
        @Override // android.content.DialogInterface.OnClickListener
        public final void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

    public static void showGestureNavigationTutorialDialog(Context context, DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog create = new AlertDialog.Builder(context).setView(createTutorialDialogContentView(context, 2)).setNegativeButton(R$string.accessibility_tutorial_dialog_button, mOnClickListener).setOnDismissListener(onDismissListener).create();
        create.requestWindowFeature(1);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AlertDialog showAccessibilityGestureTutorialDialog(Context context) {
        return createDialog(context, 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AlertDialog createAccessibilityTutorialDialog(Context context, int i, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setView(createShortcutNavigationContentView(context, i)).setNegativeButton(R$string.accessibility_tutorial_dialog_button, onClickListener).create();
    }

    private static View createTutorialDialogContentView(Context context, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        if (i != 0) {
            if (i == 1) {
                View inflate = layoutInflater.inflate(R$layout.tutorial_dialog_launch_service_by_gesture_navigation, (ViewGroup) null);
                setupGestureNavigationTextWithImage(context, inflate);
                return inflate;
            } else if (i != 2) {
                return null;
            } else {
                View inflate2 = layoutInflater.inflate(R$layout.tutorial_dialog_launch_by_gesture_navigation_settings, (ViewGroup) null);
                setupGestureNavigationTextWithImage(context, inflate2);
                return inflate2;
            }
        }
        return layoutInflater.inflate(R$layout.tutorial_dialog_launch_service_by_accessibility_button, (ViewGroup) null);
    }

    private static void setupGestureNavigationTextWithImage(Context context, View view) {
        int i;
        int i2;
        boolean isTouchExploreEnabled = AccessibilityUtil.isTouchExploreEnabled(context);
        ImageView imageView = (ImageView) view.findViewById(R$id.image);
        if (isTouchExploreEnabled) {
            i = R$drawable.illustration_accessibility_gesture_three_finger;
        } else {
            i = R$drawable.illustration_accessibility_gesture_two_finger;
        }
        imageView.setImageResource(i);
        TextView textView = (TextView) view.findViewById(R$id.gesture_tutorial_message);
        if (isTouchExploreEnabled) {
            i2 = R$string.accessibility_tutorial_dialog_message_gesture_settings_talkback;
        } else {
            i2 = R$string.accessibility_tutorial_dialog_message_gesture_settings;
        }
        textView.setText(i2);
    }

    private static AlertDialog createDialog(Context context, int i) {
        AlertDialog create = new AlertDialog.Builder(context).setView(createTutorialDialogContentView(context, i)).setNegativeButton(R$string.accessibility_tutorial_dialog_button, mOnClickListener).create();
        create.requestWindowFeature(1);
        create.setCanceledOnTouchOutside(false);
        create.show();
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TutorialPagerAdapter extends PagerAdapter {
        private final List<TutorialPage> mTutorialPages;

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private TutorialPagerAdapter(List<TutorialPage> list) {
            this.mTutorialPages = list;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View illustrationView = this.mTutorialPages.get(i).getIllustrationView();
            viewGroup.addView(illustrationView);
            return illustrationView;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return this.mTutorialPages.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView(this.mTutorialPages.get(i).getIllustrationView());
        }
    }

    private static ImageView createImageView(Context context, int i) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(i);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    private static View createIllustrationView(Context context, int i) {
        View inflateAndInitIllustrationFrame = inflateAndInitIllustrationFrame(context);
        ((LottieAnimationView) inflateAndInitIllustrationFrame.findViewById(R$id.image)).setImageResource(i);
        return inflateAndInitIllustrationFrame;
    }

    private static View createIllustrationViewWithImageRawResource(Context context, final int i) {
        View inflateAndInitIllustrationFrame = inflateAndInitIllustrationFrame(context);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) inflateAndInitIllustrationFrame.findViewById(R$id.image);
        lottieAnimationView.setFailureListener(new LottieListener() { // from class: com.android.settings.accessibility.AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda0
            @Override // com.airbnb.lottie.LottieListener
            public final void onResult(Object obj) {
                AccessibilityGestureNavigationTutorial.lambda$createIllustrationViewWithImageRawResource$1(i, (Throwable) obj);
            }
        });
        lottieAnimationView.setAnimation(i);
        lottieAnimationView.setRepeatCount(-1);
        lottieAnimationView.playAnimation();
        return inflateAndInitIllustrationFrame;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$createIllustrationViewWithImageRawResource$1(int i, Throwable th) {
        Log.w("AccessibilityGestureNavigationTutorial", "Invalid image raw resource id: " + i, th);
    }

    private static View inflateAndInitIllustrationFrame(Context context) {
        return ((LayoutInflater) context.getSystemService(LayoutInflater.class)).inflate(R$layout.accessibility_lottie_animation_view, (ViewGroup) null);
    }

    private static View createShortcutNavigationContentView(final Context context, int i) {
        View inflate = ((LayoutInflater) context.getSystemService(LayoutInflater.class)).inflate(R$layout.accessibility_shortcut_tutorial_dialog, (ViewGroup) null);
        List<TutorialPage> createShortcutTutorialPages = createShortcutTutorialPages(context, i);
        Preconditions.checkArgument(!createShortcutTutorialPages.isEmpty(), "Unexpected tutorial pages size");
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R$id.indicator_container);
        linearLayout.setVisibility(createShortcutTutorialPages.size() > 1 ? 0 : 8);
        for (TutorialPage tutorialPage : createShortcutTutorialPages) {
            linearLayout.addView(tutorialPage.getIndicatorIcon());
        }
        createShortcutTutorialPages.get(0).getIndicatorIcon().setEnabled(true);
        TextSwitcher textSwitcher = (TextSwitcher) inflate.findViewById(R$id.title);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: com.android.settings.accessibility.AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda2
            @Override // android.widget.ViewSwitcher.ViewFactory
            public final View makeView() {
                View makeTitleView;
                makeTitleView = AccessibilityGestureNavigationTutorial.makeTitleView(context);
                return makeTitleView;
            }
        });
        textSwitcher.setText(createShortcutTutorialPages.get(0).getTitle());
        TextSwitcher textSwitcher2 = (TextSwitcher) inflate.findViewById(R$id.instruction);
        textSwitcher2.setFactory(new ViewSwitcher.ViewFactory() { // from class: com.android.settings.accessibility.AccessibilityGestureNavigationTutorial$$ExternalSyntheticLambda3
            @Override // android.widget.ViewSwitcher.ViewFactory
            public final View makeView() {
                View makeInstructionView;
                makeInstructionView = AccessibilityGestureNavigationTutorial.makeInstructionView(context);
                return makeInstructionView;
            }
        });
        textSwitcher2.setText(createShortcutTutorialPages.get(0).getInstruction());
        ViewPager viewPager = (ViewPager) inflate.findViewById(R$id.view_pager);
        viewPager.setAdapter(new TutorialPagerAdapter(createShortcutTutorialPages));
        viewPager.setContentDescription(context.getString(R$string.accessibility_tutorial_pager, 1, Integer.valueOf(createShortcutTutorialPages.size())));
        viewPager.setImportantForAccessibility(createShortcutTutorialPages.size() <= 1 ? 4 : 1);
        viewPager.addOnPageChangeListener(new TutorialPageChangeListener(context, viewPager, textSwitcher, textSwitcher2, createShortcutTutorialPages));
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View makeTitleView(Context context) {
        TextView textView = new TextView(context);
        TextViewCompat.setTextAppearance(textView, R$style.AccessibilityDialogTitle);
        textView.setGravity(17);
        return textView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View makeInstructionView(Context context) {
        TextView textView = new TextView(context);
        TextViewCompat.setTextAppearance(textView, R$style.AccessibilityDialogDescription);
        return textView;
    }

    private static TutorialPage createSoftwareTutorialPage(Context context) {
        CharSequence softwareTitle = getSoftwareTitle(context);
        View createSoftwareImage = createSoftwareImage(context);
        CharSequence softwareInstruction = getSoftwareInstruction(context);
        ImageView createImageView = createImageView(context, R$drawable.ic_accessibility_page_indicator);
        createImageView.setEnabled(false);
        return new TutorialPage(softwareTitle, createSoftwareImage, createImageView, softwareInstruction);
    }

    private static TutorialPage createHardwareTutorialPage(Context context) {
        CharSequence text = context.getText(R$string.accessibility_tutorial_dialog_title_volume);
        View createIllustrationView = createIllustrationView(context, R$drawable.accessibility_shortcut_type_hardware);
        ImageView createImageView = createImageView(context, R$drawable.ic_accessibility_page_indicator);
        CharSequence text2 = context.getText(R$string.accessibility_tutorial_dialog_message_volume);
        createImageView.setEnabled(false);
        return new TutorialPage(text, createIllustrationView, createImageView, text2);
    }

    private static TutorialPage createTripleTapTutorialPage(Context context) {
        CharSequence text = context.getText(R$string.accessibility_tutorial_dialog_title_triple);
        View createIllustrationViewWithImageRawResource = createIllustrationViewWithImageRawResource(context, R$raw.accessibility_shortcut_type_triple_tap);
        CharSequence text2 = context.getText(R$string.accessibility_tutorial_dialog_message_triple);
        ImageView createImageView = createImageView(context, R$drawable.ic_accessibility_page_indicator);
        createImageView.setEnabled(false);
        return new TutorialPage(text, createIllustrationViewWithImageRawResource, createImageView, text2);
    }

    static List<TutorialPage> createShortcutTutorialPages(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        if ((i & 1) == 1) {
            arrayList.add(createSoftwareTutorialPage(context));
        }
        if ((i & 2) == 2) {
            arrayList.add(createHardwareTutorialPage(context));
        }
        if ((i & 4) == 4) {
            arrayList.add(createTripleTapTutorialPage(context));
        }
        return arrayList;
    }

    private static View createSoftwareImage(Context context) {
        int i;
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            i = R$drawable.accessibility_shortcut_type_software_floating;
        } else if (AccessibilityUtil.isGestureNavigateEnabled(context)) {
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i = R$drawable.accessibility_shortcut_type_software_gesture_talkback;
            } else {
                i = R$drawable.accessibility_shortcut_type_software_gesture;
            }
        } else {
            i = R$drawable.accessibility_shortcut_type_software;
        }
        return createIllustrationView(context, i);
    }

    private static CharSequence getSoftwareTitle(Context context) {
        int i;
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            i = R$string.accessibility_tutorial_dialog_title_button;
        } else if (AccessibilityUtil.isGestureNavigateEnabled(context)) {
            i = R$string.accessibility_tutorial_dialog_title_gesture;
        } else {
            i = R$string.accessibility_tutorial_dialog_title_button;
        }
        return context.getText(i);
    }

    private static CharSequence getSoftwareInstruction(Context context) {
        int i;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (AccessibilityUtil.isFloatingMenuEnabled(context)) {
            spannableStringBuilder.append(context.getText(R$string.accessibility_tutorial_dialog_message_floating_button));
        } else if (AccessibilityUtil.isGestureNavigateEnabled(context)) {
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i = R$string.accessibility_tutorial_dialog_message_gesture_talkback;
            } else {
                i = R$string.accessibility_tutorial_dialog_message_gesture;
            }
            spannableStringBuilder.append(context.getText(i));
        } else {
            spannableStringBuilder.append(getSoftwareInstructionWithIcon(context, context.getText(R$string.accessibility_tutorial_dialog_message_button)));
        }
        return spannableStringBuilder;
    }

    private static CharSequence getSoftwareInstructionWithIcon(Context context, CharSequence charSequence) {
        String charSequence2 = charSequence.toString();
        SpannableString valueOf = SpannableString.valueOf(charSequence2);
        int indexOf = charSequence2.indexOf("%s");
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(context.getDrawable(R$drawable.ic_accessibility_new));
        Drawable mutate = imageView.getDrawable().mutate();
        ImageSpan imageSpan = new ImageSpan(mutate);
        imageSpan.setContentDescription("");
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
        valueOf.setSpan(imageSpan, indexOf, indexOf + 2, 33);
        return valueOf;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TutorialPage {
        private final View mIllustrationView;
        private final ImageView mIndicatorIcon;
        private final CharSequence mInstruction;
        private final CharSequence mTitle;

        TutorialPage(CharSequence charSequence, View view, ImageView imageView, CharSequence charSequence2) {
            this.mTitle = charSequence;
            this.mIllustrationView = view;
            this.mIndicatorIcon = imageView;
            this.mInstruction = charSequence2;
            setupIllustrationChildViewsGravity();
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public View getIllustrationView() {
            return this.mIllustrationView;
        }

        public ImageView getIndicatorIcon() {
            return this.mIndicatorIcon;
        }

        public CharSequence getInstruction() {
            return this.mInstruction;
        }

        private void setupIllustrationChildViewsGravity() {
            initViewGravity(this.mIllustrationView.findViewById(R$id.image_background));
            initViewGravity(this.mIllustrationView.findViewById(R$id.image));
        }

        private void initViewGravity(View view) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
            layoutParams.gravity = 17;
            view.setLayoutParams(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TutorialPageChangeListener implements ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TextSwitcher mInstruction;
        private int mLastTutorialPagePosition = 0;
        private final TextSwitcher mTitle;
        private final List<TutorialPage> mTutorialPages;
        private final ViewPager mViewPager;

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        TutorialPageChangeListener(Context context, ViewPager viewPager, ViewGroup viewGroup, ViewGroup viewGroup2, List<TutorialPage> list) {
            this.mContext = context;
            this.mViewPager = viewPager;
            this.mTitle = (TextSwitcher) viewGroup;
            this.mInstruction = (TextSwitcher) viewGroup2;
            this.mTutorialPages = list;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            Log.d("fangli", "onPageSelected: " + i);
            boolean z = this.mLastTutorialPagePosition > i;
            int i2 = z ? 17432578 : 17432741;
            int i3 = z ? 17432579 : 17432744;
            this.mTitle.setInAnimation(this.mContext, i2);
            this.mTitle.setOutAnimation(this.mContext, i3);
            this.mTitle.setText(this.mTutorialPages.get(i).getTitle());
            this.mInstruction.setInAnimation(this.mContext, i2);
            this.mInstruction.setOutAnimation(this.mContext, i3);
            this.mInstruction.setText(this.mTutorialPages.get(i).getInstruction());
            for (TutorialPage tutorialPage : this.mTutorialPages) {
                tutorialPage.getIndicatorIcon().setEnabled(false);
            }
            this.mTutorialPages.get(i).getIndicatorIcon().setEnabled(true);
            this.mLastTutorialPagePosition = i;
            this.mViewPager.setContentDescription(this.mContext.getString(R$string.accessibility_tutorial_pager, Integer.valueOf(i + 1), Integer.valueOf(this.mTutorialPages.size())));
        }
    }
}
