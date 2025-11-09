package com.android.settings.core;

import android.R;
import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$anim;
import com.android.settings.R$color;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.SetupWizardUtils;
import com.android.settings.SubSettings;
import com.android.settings.core.CategoryMixin;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.resources.TextAppearanceConfig;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SettingsBaseActivity extends FragmentActivity implements CategoryMixin.CategoryHandler {
    protected AppBarLayout mAppBarLayout;
    protected CategoryMixin mCategoryMixin;
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.core.SettingsBaseActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("fangli", "mReceiver,the intent action = " + intent.getAction());
            if ("FOURCAMERA2_BROADCAST_RECV".equals(intent.getAction())) {
                intent.getBooleanExtra("Show", true);
            }
        }
    };
    private TextView mTitle;
    private RelativeLayout mTitleLy;
    private Toolbar mToolbar;
    private UiModeManager uiModeManager;

    private void disableCollapsingToolbarLayoutScrollingBehavior() {
    }

    protected boolean isToolbarEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        ((SettingsApplication) getApplication()).setBaseActivity(this);
        super.onStart();
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryHandler
    public CategoryMixin getCategoryMixin() {
        return this.mCategoryMixin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isFinishing()) {
            return;
        }
        if (isLockTaskModePinned() && !isSettingsRunOnTop()) {
            Log.w("SettingsBaseActivity", "Devices lock task mode pinned.");
            finish();
        }
        System.currentTimeMillis();
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        TextAppearanceConfig.setShouldLoadFontSynchronously(true);
        this.mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(this.mCategoryMixin);
        if (!getTheme().obtainStyledAttributes(R.styleable.Theme).getBoolean(38, false)) {
            requestWindowFeature(1);
        }
        try {
            boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
            if (isAnySetupWizard && (this instanceof SubSettings)) {
                setTheme(SetupWizardUtils.getTheme(this, getIntent()));
                setTheme(R$style.SettingsPreferenceTheme_SetupWizard);
                ThemeHelper.trySetDynamicColor(this);
            }
            if (isToolbarEnabled() && !isAnySetupWizard) {
                super.setContentView(R$layout.collapsing_toolbar_base_layout);
                this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R$id.collapsing_toolbar);
                this.mAppBarLayout = (AppBarLayout) findViewById(R$id.app_bar);
                findViewById(R$id.content_parent).setFitsSystemWindows(false);
                this.mTitle = (TextView) findViewById(R$id.collapsing_title);
                this.mTitleLy = (RelativeLayout) findViewById(R$id.collapsing_title_ly);
                CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
                if (collapsingToolbarLayout != null) {
                    collapsingToolbarLayout.setVisibility(8);
                    this.mCollapsingToolbarLayout.setLineSpacingMultiplier(1.1f);
                    this.mCollapsingToolbarLayout.setHyphenationFrequency(3);
                }
                disableCollapsingToolbarLayoutScrollingBehavior();
            } else {
                super.setContentView(R$layout.settings_base_layout);
            }
            Toolbar toolbar = (Toolbar) findViewById(R$id.action_bar);
            if (!isToolbarEnabled() || isAnySetupWizard) {
                Log.d("fangli", "565656");
                toolbar.setVisibility(8);
                return;
            }
            toolbar.setVisibility(0);
            this.uiModeManager = (UiModeManager) getSystemService(UiModeManager.class);
            setActionBar(toolbar);
        } catch (Exception unused) {
            finish();
        }
    }

    @Override // android.app.Activity
    public void setActionBar(Toolbar toolbar) {
        super.setActionBar(toolbar);
        this.mToolbar = toolbar;
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Resources resources = super.getResources();
        int i = SettingsApplication.mWidthFix;
        if (i == 2000 || ((i == 1920 && SettingsApplication.mHeightFix > 1000) || (SettingsApplication.mWidthFix == 2880 && SettingsApplication.mHeightFix == 1080))) {
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            if (displayMetrics.densityDpi != 240 || displayMetrics.density != 1.5f || displayMetrics.scaledDensity != 1.75f) {
                displayMetrics.density = 1.5f;
                displayMetrics.scaledDensity = 1.75f;
                displayMetrics.densityDpi = 240;
            }
            return resources;
        } else if (SettingsApplication.mWidthFix <= 768) {
            DisplayMetrics displayMetrics2 = resources.getDisplayMetrics();
            Log.d("SettingsApplication", "getResources: " + displayMetrics2.density + " " + displayMetrics2.scaledDensity);
            if (displayMetrics2.densityDpi != 120 || displayMetrics2.density != 0.75f || displayMetrics2.scaledDensity != 0.95f) {
                Log.d("SettingsApplication", "getResources: " + SettingsApplication.mWidthFix);
                displayMetrics2.density = 0.75f;
                displayMetrics2.scaledDensity = 0.95f;
                displayMetrics2.densityDpi = 120;
            }
            return resources;
        } else {
            DisplayMetrics displayMetrics3 = resources.getDisplayMetrics();
            if (displayMetrics3.densityDpi != 160 || displayMetrics3.density != 1.0f || displayMetrics3.scaledDensity != 1.25f) {
                Log.d("SettingsApplication", "getResources: " + SettingsApplication.mWidthFix);
                displayMetrics3.density = 1.0f;
                displayMetrics3.scaledDensity = 1.25f;
                displayMetrics3.densityDpi = 160;
            }
            return resources;
        }
    }

    public void setHomeBackground(int i, boolean z, int i2) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.content_parent);
        Window window = getWindow();
        window.clearFlags(67108864);
        window.addFlags(Integer.MIN_VALUE);
        if (i == 0) {
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color));
            if (viewGroup != null) {
                viewGroup.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_main));
            }
            RelativeLayout relativeLayout = this.mTitleLy;
            if (relativeLayout != null) {
                relativeLayout.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_main));
            }
        } else if (i == 1) {
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color));
            if (viewGroup != null) {
                viewGroup.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_main));
            }
            RelativeLayout relativeLayout2 = this.mTitleLy;
            if (relativeLayout2 != null) {
                relativeLayout2.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_main));
            }
        } else if (i == 2) {
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color_classic));
            if (viewGroup != null) {
                viewGroup.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_classic));
            }
            RelativeLayout relativeLayout3 = this.mTitleLy;
            if (relativeLayout3 != null) {
                relativeLayout3.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_classic_title));
            }
        } else if (i != 3) {
        } else {
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color_classic));
            if (z) {
                i2 = getColor();
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColors(new int[]{-16777216, i2});
            gradientDrawable.setGradientType(0);
            gradientDrawable.setSize(2000, 1100);
            if (viewGroup != null) {
                viewGroup.setBackground(gradientDrawable);
            }
            if (this.mTitleLy != null) {
                setTitleBk(i2);
            }
        }
    }

    private int getColor() {
        String str = SystemProperties.get("persist.syu.themecolor", "");
        if (!TextUtils.isEmpty(str)) {
            try {
                return new JSONObject(str).optInt("settings");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("SettingsBaseActivity", "initData: " + e.toString());
            }
        }
        return -16777216;
    }

    private void setTitleBk(int i) {
        int alpha = Color.alpha(i);
        int i2 = (i >> 16) & 255;
        int i3 = (i >> 8) & 255;
        int i4 = i & 255;
        int i5 = (i >> 24) & 255;
        Log.d("SettingsBaseActivity", "setTitleBk: " + alpha + "red ===" + i2 + "green ===" + i3 + "blue ==" + i4 + "alpha ===" + i5);
        int round = Math.round(((float) i2) * 0.1f);
        int round2 = Math.round(((float) i3) * 0.1f);
        int round3 = Math.round(((float) i4) * 0.1f);
        Log.d("SettingsBaseActivity", "setTitleBk: " + alpha + "red1 ===" + round + "green1 ===" + round2 + "blue1 ==" + round3 + "alpha1 ===" + i5);
        int argb = Color.argb(i5, round, round2, round3);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{-16777216, argb});
        gradientDrawable.setGradientType(0);
        gradientDrawable.setSize(2000, 60);
        this.mTitleLy.setBackground(gradientDrawable);
    }

    @Override // android.app.Activity
    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finishAfterTransition();
        return true;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        int transitionType = getTransitionType(intent);
        super.startActivityForResult(intent, i, bundle);
        if (transitionType == 1) {
            overridePendingTransition(R$anim.sud_slide_next_in, R$anim.sud_slide_next_out);
        } else if (transitionType == 2) {
            overridePendingTransition(17432576, R$anim.sud_stay);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        if (getTransitionType(getIntent()) == 2) {
            overridePendingTransition(R$anim.sud_stay, 17432577);
        }
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Log.d("SettingsBaseActivity", "onDestroy: ");
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(int i) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.content_frame);
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        LayoutInflater.from(this).inflate(i, viewGroup);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view) {
        ((ViewGroup) findViewById(R$id.content_frame)).addView(view);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ((ViewGroup) findViewById(R$id.content_frame)).addView(view, layoutParams);
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(charSequence);
        }
        TextView textView = this.mTitle;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    @Override // android.app.Activity
    public void setTitle(int i) {
        super.setTitle(getText(i));
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(getText(i));
        }
        TextView textView = this.mTitle;
        if (textView != null) {
            textView.setText(getText(i));
        }
    }

    private boolean isLockTaskModePinned() {
        return ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getLockTaskModeState() == 2;
    }

    private boolean isSettingsRunOnTop() {
        return TextUtils.equals(getPackageName(), ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getRunningTasks(1).get(0).baseActivity.getPackageName());
    }

    public boolean setTileEnabled(ComponentName componentName, boolean z) {
        PackageManager packageManager = getPackageManager();
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);
        if ((componentEnabledSetting == 1) != z || componentEnabledSetting == 0) {
            if (z) {
                this.mCategoryMixin.removeFromDenylist(componentName);
            } else {
                this.mCategoryMixin.addToDenylist(componentName);
            }
            packageManager.setComponentEnabledSetting(componentName, z ? 1 : 2, 1);
            return true;
        }
        return false;
    }

    private int getTransitionType(Intent intent) {
        if (intent == null) {
            return -1;
        }
        return intent.getIntExtra("page_transition_type", 2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        Log.d("SettingsBaseActivity", "onResume: ");
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.uiMode;
        Log.d("SettingsBaseActivity", "onConfigurationChanged: " + i);
        if (i == 33 || i == 17) {
            recreate();
        }
    }
}
