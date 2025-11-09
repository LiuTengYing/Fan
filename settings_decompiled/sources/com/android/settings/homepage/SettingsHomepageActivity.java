package com.android.settings.homepage;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.window.embedding.SplitController;
import com.android.settings.R$anim;
import com.android.settings.R$color;
import com.android.settings.R$dimen;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.SettingsApplication;
import com.android.settings.accounts.AvatarViewMixin;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.activityembedding.ActivityEmbeddingUtils;
import com.android.settings.core.CategoryMixin;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.ipc.IpcObj;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.PasswordUtils;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.syu.ipcself.module.main.Main;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.function.Consumer;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SettingsHomepageActivity extends FragmentActivity implements CategoryMixin.CategoryHandler {
    static final int DEFAULT_HIGHLIGHT_MENU_KEY = R$string.menu_key_network;
    private CategoryMixin mCategoryMixin;
    private View mHomepageView;
    private boolean mIsEmbeddingActivityEnabled;
    private boolean mIsTwoPane;
    private Set<HomepageLoadedListener> mLoadedListeners;
    private TopLevelSettings mMainFragment;
    private SplitController mSplitController;
    private View mSuggestionView;
    private View mTwoPaneSuggestionView;
    private UiModeManager uiModeManager;
    private boolean mIsRegularLayout = true;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.homepage.SettingsHomepageActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("SettingsHomepageActivity", "mReceiver,the intent action = " + intent.getAction());
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                String stringExtra = intent.getStringExtra("reason");
                Log.d("SettingsHomepageActivity", "onReceive: " + stringExtra);
                if (stringExtra != null) {
                    if (stringExtra.equals("homekey")) {
                        System.exit(0);
                    } else if (stringExtra.equals("recentapps")) {
                        SettingsApplication.isRecentApp = true;
                    }
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface FragmentCreator<T extends Fragment> {
        T create();

        default void init(Fragment fragment) {
        }
    }

    /* loaded from: classes.dex */
    public interface HomepageLoadedListener {
        void onHomepageLoaded();
    }

    public boolean addHomepageLoadedListener(HomepageLoadedListener homepageLoadedListener) {
        if (this.mHomepageView == null) {
            return false;
        }
        if (this.mLoadedListeners.contains(homepageLoadedListener)) {
            return true;
        }
        this.mLoadedListeners.add(homepageLoadedListener);
        return true;
    }

    public void showHomepageWithSuggestion(boolean z) {
        if (this.mHomepageView == null) {
            return;
        }
        Log.i("SettingsHomepageActivity", "showHomepageWithSuggestion: " + z);
        View view = this.mHomepageView;
        this.mSuggestionView.setVisibility(z ? 0 : 8);
        this.mTwoPaneSuggestionView.setVisibility(z ? 0 : 8);
        this.mHomepageView = null;
        this.mLoadedListeners.forEach(new Consumer() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((SettingsHomepageActivity.HomepageLoadedListener) obj).onHomepageLoaded();
            }
        });
        this.mLoadedListeners.clear();
        view.setVisibility(0);
    }

    public TopLevelSettings getMainFragment() {
        return this.mMainFragment;
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryHandler
    public CategoryMixin getCategoryMixin() {
        return this.mCategoryMixin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean isEmbeddingActivityEnabled = ActivityEmbeddingUtils.isEmbeddingActivityEnabled(this);
        this.mIsEmbeddingActivityEnabled = isEmbeddingActivityEnabled;
        if (isEmbeddingActivityEnabled) {
            UserManager userManager = (UserManager) getSystemService(UserManager.class);
            if (userManager.getUserInfo(getUser().getIdentifier()).isManagedProfile()) {
                Intent putExtra = new Intent(getIntent()).setClass(this, DeepLinkHomepageActivityInternal.class).addFlags(33554432).putExtra("user_handle", getUser());
                putExtra.removeFlags(268435456);
                startActivityAsUser(putExtra, userManager.getPrimaryUser().getUserHandle());
                finish();
                return;
            }
        }
        setupEdgeToEdge();
        setContentView(R$layout.settings_homepage_container);
        SplitController splitController = SplitController.getInstance();
        this.mSplitController = splitController;
        this.mIsTwoPane = splitController.isActivityEmbedded(this);
        updateAppBarMinHeight();
        initHomepageContainer();
        updateHomepageAppBar();
        this.mLoadedListeners = new ArraySet();
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        this.mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(this.mCategoryMixin);
        final String highlightMenuKey = getHighlightMenuKey();
        if (!((ActivityManager) getSystemService(ActivityManager.class)).isLowRamDevice()) {
            initAvatarView();
            showSuggestionFragment(this.mIsEmbeddingActivityEnabled && !TextUtils.equals(getString(DEFAULT_HIGHLIGHT_MENU_KEY), highlightMenuKey));
            if (FeatureFlagUtils.isEnabled(this, "settings_contextual_home")) {
                showFragment(new FragmentCreator() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda0
                    @Override // com.android.settings.homepage.SettingsHomepageActivity.FragmentCreator
                    public final Fragment create() {
                        ContextualCardsFragment lambda$onCreate$1;
                        lambda$onCreate$1 = SettingsHomepageActivity.lambda$onCreate$1();
                        return lambda$onCreate$1;
                    }
                }, R$id.contextual_cards_content);
                ((FrameLayout) findViewById(R$id.main_content)).getLayoutTransition().enableTransitionType(4);
            }
        }
        this.mMainFragment = (TopLevelSettings) showFragment(new FragmentCreator() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda1
            @Override // com.android.settings.homepage.SettingsHomepageActivity.FragmentCreator
            public final Fragment create() {
                TopLevelSettings lambda$onCreate$2;
                lambda$onCreate$2 = SettingsHomepageActivity.lambda$onCreate$2(highlightMenuKey);
                return lambda$onCreate$2;
            }
        }, R$id.main_content);
        this.uiModeManager = (UiModeManager) getSystemService(UiModeManager.class);
        launchDeepLinkIntentToRight();
        updateHomepagePaddings();
        updateSplitLayout();
        Log.d("fangli", "SettingsApplication.initCanbus ===" + SettingsApplication.initCanbus);
        if (SettingsApplication.initCanbus) {
            return;
        }
        SettingsApplication.initCanbus = true;
        Main.postRunnable_Ui(true, SettingsApplication.mApplication.postProtocolUpdate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ ContextualCardsFragment lambda$onCreate$1() {
        return new ContextualCardsFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ TopLevelSettings lambda$onCreate$2(String str) {
        TopLevelSettings topLevelSettings = new TopLevelSettings();
        topLevelSettings.getArguments().putString(":settings:fragment_args_key", str);
        return topLevelSettings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        ((SettingsApplication) getApplication()).setHomeActivity(this);
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        reloadHighlightMenuKey();
        if (isFinishing()) {
            return;
        }
        launchDeepLinkIntentToRight();
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

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        boolean isActivityEmbedded = this.mSplitController.isActivityEmbedded(this);
        if (this.mIsTwoPane != isActivityEmbedded) {
            this.mIsTwoPane = isActivityEmbedded;
            updateHomepageAppBar();
            updateHomepagePaddings();
        }
        updateSplitLayout();
        int i = configuration.uiMode;
        Log.d("SettingsHomepageActivity", "onConfigurationChanged: " + i);
        if (i == 33 || i == 17) {
            recreate();
        }
    }

    private void updateSplitLayout() {
        int i;
        if (this.mIsEmbeddingActivityEnabled) {
            if (this.mIsTwoPane) {
                if (this.mIsRegularLayout == ActivityEmbeddingUtils.isRegularHomepageLayout(this)) {
                    return;
                }
            } else if (this.mIsRegularLayout) {
                return;
            }
            this.mIsRegularLayout = !this.mIsRegularLayout;
            View findViewById = findViewById(R$id.search_bar_title);
            if (findViewById != null) {
                Resources resources = getResources();
                if (this.mIsRegularLayout) {
                    i = R$dimen.search_bar_title_padding_start_regular_two_pane;
                } else {
                    i = R$dimen.search_bar_title_padding_start;
                }
                findViewById.setPaddingRelative(resources.getDimensionPixelSize(i), 0, 0, 0);
            }
            getSupportFragmentManager().getFragments().forEach(new Consumer() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SettingsHomepageActivity.this.lambda$updateSplitLayout$3((Fragment) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSplitLayout$3(Fragment fragment) {
        if (fragment instanceof SplitLayoutListener) {
            ((SplitLayoutListener) fragment).onSplitLayoutChanged(this.mIsRegularLayout);
        }
    }

    private void setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(16908290), new OnApplyWindowInsetsListener() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda2
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat lambda$setupEdgeToEdge$4;
                lambda$setupEdgeToEdge$4 = SettingsHomepageActivity.lambda$setupEdgeToEdge$4(view, windowInsetsCompat);
                return lambda$setupEdgeToEdge$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ WindowInsetsCompat lambda$setupEdgeToEdge$4(View view, WindowInsetsCompat windowInsetsCompat) {
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return WindowInsetsCompat.CONSUMED;
    }

    private void initAvatarView() {
        ImageView imageView = (ImageView) findViewById(R$id.account_avatar);
        ImageView imageView2 = (ImageView) findViewById(R$id.account_avatar_two_pane_version);
        if (AvatarViewMixin.isAvatarSupported(this)) {
            imageView.setVisibility(0);
            getLifecycle().addObserver(new AvatarViewMixin(this, imageView));
            if (this.mIsEmbeddingActivityEnabled) {
                imageView2.setVisibility(0);
                getLifecycle().addObserver(new AvatarViewMixin(this, imageView2));
            }
        }
    }

    private void showSuggestionFragment(boolean z) {
        Class<? extends Fragment> contextualSuggestionFragment = FeatureFactory.getFactory(this).getSuggestionFeatureProvider(this).getContextualSuggestionFragment();
        if (contextualSuggestionFragment == null) {
            return;
        }
        int i = R$id.suggestion_content;
        this.mSuggestionView = findViewById(i);
        int i2 = R$id.two_pane_suggestion_content;
        this.mTwoPaneSuggestionView = findViewById(i2);
        View findViewById = findViewById(R$id.settings_homepage_container);
        this.mHomepageView = findViewById;
        findViewById.setVisibility(z ? 4 : 8);
        this.mHomepageView.postDelayed(new Runnable() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SettingsHomepageActivity.this.lambda$showSuggestionFragment$5();
            }
        }, 300L);
        showFragment(new SuggestionFragCreator(contextualSuggestionFragment, false), i);
        if (this.mIsEmbeddingActivityEnabled) {
            showFragment(new SuggestionFragCreator(contextualSuggestionFragment, true), i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestionFragment$5() {
        showHomepageWithSuggestion(false);
    }

    private <T extends Fragment> T showFragment(FragmentCreator<T> fragmentCreator, int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        T t = (T) supportFragmentManager.findFragmentById(i);
        int i2 = R$anim.settings_base_fragment_fade_in;
        beginTransaction.setCustomAnimations(i2, i2);
        if (t == null) {
            t = fragmentCreator.create();
            fragmentCreator.init(t);
            beginTransaction.add(i, t);
        } else {
            fragmentCreator.init(t);
            beginTransaction.show(t);
        }
        beginTransaction.commit();
        return t;
    }

    private void launchDeepLinkIntentToRight() {
        Intent intent;
        if (this.mIsEmbeddingActivityEnabled && (intent = getIntent()) != null && TextUtils.equals(intent.getAction(), "android.settings.SETTINGS_EMBED_DEEP_LINK_ACTIVITY")) {
            if (!(this instanceof DeepLinkHomepageActivity) && !(this instanceof DeepLinkHomepageActivityInternal)) {
                Log.e("SettingsHomepageActivity", "Not a deep link component");
                finish();
                return;
            }
            String stringExtra = intent.getStringExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI");
            if (TextUtils.isEmpty(stringExtra)) {
                Log.e("SettingsHomepageActivity", "No EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI to deep link");
                finish();
                return;
            }
            try {
                Intent parseUri = Intent.parseUri(stringExtra, 1);
                ComponentName resolveActivity = parseUri.resolveActivity(getPackageManager());
                if (resolveActivity == null) {
                    Log.e("SettingsHomepageActivity", "No valid target for the deep link intent: " + parseUri);
                    finish();
                    return;
                }
                try {
                    ActivityInfo activityInfo = getPackageManager().getActivityInfo(resolveActivity, 0);
                    try {
                        int launchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivityToken());
                        if (!hasPrivilegedAccess(launchedFromUid, activityInfo)) {
                            if (!activityInfo.exported) {
                                Log.e("SettingsHomepageActivity", "Target Activity is not exported");
                                finish();
                                return;
                            } else if (!isCallingAppPermitted(activityInfo.permission)) {
                                Log.e("SettingsHomepageActivity", "Calling app must have the permission of deep link Activity");
                                finish();
                                return;
                            }
                        }
                        parseUri.setComponent(resolveActivity);
                        intent.setAction(null);
                        parseUri.removeFlags(268959744);
                        parseUri.addFlags(33554432);
                        parseUri.replaceExtras(intent);
                        parseUri.putExtra("is_from_settings_homepage", true);
                        parseUri.putExtra("is_from_slice", false);
                        parseUri.setData((Uri) intent.getParcelableExtra("settings_large_screen_deep_link_intent_data"));
                        int flags = parseUri.getFlags() & 3;
                        if (parseUri.getData() != null && flags != 0 && checkUriPermission(parseUri.getData(), -1, launchedFromUid, flags) == -1) {
                            Log.e("SettingsHomepageActivity", "Calling app must have the permission to access Uri and grant permission");
                            finish();
                            return;
                        }
                        ActivityEmbeddingRulesController.registerTwoPanePairRule(this, new ComponentName(getApplicationContext(), getClass()), resolveActivity, parseUri.getAction(), 1, 1, true);
                        ActivityEmbeddingRulesController.registerTwoPanePairRule(this, new ComponentName(getApplicationContext(), Settings.class), resolveActivity, parseUri.getAction(), 1, 1, true);
                        UserHandle userHandle = (UserHandle) intent.getParcelableExtra("user_handle", UserHandle.class);
                        if (userHandle != null) {
                            startActivityAsUser(parseUri, userHandle);
                        } else {
                            startActivity(parseUri);
                        }
                    } catch (RemoteException e) {
                        Log.e("SettingsHomepageActivity", "Not able to get callingUid: " + e);
                        finish();
                    }
                } catch (PackageManager.NameNotFoundException e2) {
                    Log.e("SettingsHomepageActivity", "Failed to get target ActivityInfo: " + e2);
                    finish();
                }
            } catch (URISyntaxException e3) {
                Log.e("SettingsHomepageActivity", "Failed to parse deep link intent: " + e3);
                finish();
            }
        }
    }

    private boolean hasPrivilegedAccess(int i, ActivityInfo activityInfo) {
        int appId;
        if (TextUtils.equals(PasswordUtils.getCallingAppPackageName(getActivityToken()), getPackageName())) {
            return true;
        }
        try {
            return UserHandle.isSameApp(i, getPackageManager().getApplicationInfo(activityInfo.packageName, 0).uid) || (appId = UserHandle.getAppId(i)) == 0 || appId == 1000;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SettingsHomepageActivity", "Not able to get targetUid: " + e);
            return false;
        }
    }

    boolean isCallingAppPermitted(String str) {
        return TextUtils.isEmpty(str) || PasswordUtils.isCallingAppPermitted(this, getActivityToken(), str);
    }

    private String getHighlightMenuKey() {
        Intent intent = getIntent();
        if (intent != null && TextUtils.equals(intent.getAction(), "android.settings.SETTINGS_EMBED_DEEP_LINK_ACTIVITY")) {
            String stringExtra = intent.getStringExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_HIGHLIGHT_MENU_KEY");
            if (!TextUtils.isEmpty(stringExtra)) {
                return stringExtra;
            }
        }
        return getString(DEFAULT_HIGHLIGHT_MENU_KEY);
    }

    private void reloadHighlightMenuKey() {
        this.mMainFragment.getArguments().putString(":settings:fragment_args_key", getHighlightMenuKey());
        this.mMainFragment.reloadHighlightMenuKey();
    }

    private void initHomepageContainer() {
        View findViewById = findViewById(R$id.homepage_container);
        findViewById.setFocusableInTouchMode(false);
        findViewById.requestFocus();
    }

    private void updateHomepageAppBar() {
        if (this.mIsEmbeddingActivityEnabled) {
            updateAppBarMinHeight();
            if (this.mIsTwoPane) {
                findViewById(R$id.homepage_app_bar_regular_phone_view).setVisibility(8);
                findViewById(R$id.homepage_app_bar_two_pane_view).setVisibility(8);
                findViewById(R$id.suggestion_container_two_pane).setVisibility(0);
                return;
            }
            findViewById(R$id.homepage_app_bar_regular_phone_view).setVisibility(0);
            findViewById(R$id.homepage_app_bar_two_pane_view).setVisibility(8);
            findViewById(R$id.suggestion_container_two_pane).setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (SettingsApplication.mApplication.getThemeMode() != 0 && this.uiModeManager != null && SettingsApplication.mApplication.getIsAutoTheme() == 0) {
            IpcObj.getInstance().setCmd(0, 178, 2);
        }
        Log.d("SettingsHomepageActivity", "getThemeMode === " + SettingsApplication.mApplication.getThemeMode());
        setHomeBackground(SettingsApplication.mApplication.getThemeMode(), true, -1);
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        SettingsApplication.isRecentApp = false;
        String str = SystemProperties.get("ro.lsec.accessibility", "");
        if (!TextUtils.isEmpty(str)) {
            setStatusBar(str);
        }
        if (SettingsApplication.mApplication.isUpdateServiceConnect()) {
            return;
        }
        SettingsApplication.mApplication.connectUpdateservice();
    }

    private void setStatusBar(String str) {
        int lastIndexOf = str.lastIndexOf("/");
        String substring = str.substring(0, lastIndexOf);
        String substring2 = str.substring(lastIndexOf + 1, str.length());
        Log.i("fangli", "packageName = " + substring + ", className = " + substring2);
        if ("".equals(substring) || "".equals(substring2)) {
            return;
        }
        ComponentName componentName = new ComponentName(substring, substring2);
        if (getAccessibilityServiceInfo(SettingsApplication.mApplication, componentName) != null) {
            try {
                AccessibilityUtils.setAccessibilityServiceState(this, componentName, true);
                return;
            } catch (Exception e) {
                Log.i("fangli", Log.getStackTraceString(e));
                return;
            }
        }
        Log.i("fangli", "getAccessibilityServiceInfo is null");
    }

    private AccessibilityServiceInfo getAccessibilityServiceInfo(Context context, ComponentName componentName) {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) context.getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if (componentName.equals(accessibilityServiceInfo.getComponentName())) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    public void setHomeBackground(int i, boolean z, int i2) {
        View findViewById = findViewById(R$id.settings_homepage_container);
        TextView textView = (TextView) findViewById(R$id.setting_top_title);
        Window window = getWindow();
        window.clearFlags(67108864);
        window.addFlags(Integer.MIN_VALUE);
        if (i == 0) {
            Resources resources = SettingsApplication.mApplication.getResources();
            int i3 = R$drawable.background_main;
            findViewById.setBackground(resources.getDrawable(i3));
            textView.setBackground(SettingsApplication.mApplication.getResources().getDrawable(i3));
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color));
        } else if (i == 1) {
            Resources resources2 = SettingsApplication.mApplication.getResources();
            int i4 = R$drawable.background_main;
            findViewById.setBackground(resources2.getDrawable(i4));
            textView.setBackground(SettingsApplication.mApplication.getResources().getDrawable(i4));
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color));
        } else if (i == 2) {
            findViewById.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_classic));
            textView.setBackground(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.background_classic_title));
            window.setStatusBarColor(ContextCompat.getColor(SettingsApplication.mApplication, R$color.status_bar_color_classic));
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
            findViewById.setBackground(gradientDrawable);
            setTitleBk(i2);
        }
    }

    private int getColor() {
        String str = SystemProperties.get("persist.syu.themecolor", "");
        Log.d("SettingsHomepageActivity", "getColor: " + str);
        if (!TextUtils.isEmpty(str)) {
            try {
                return new JSONObject(str).optInt("settings");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("SettingsHomepageActivity", "initData: " + e.toString());
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
        Log.d("SettingsHomepageActivity", "setTitleBk: " + alpha + "red ===" + i2 + "green ===" + i3 + "blue ==" + i4 + "alpha ===" + i5);
        int round = Math.round(((float) i2) * 0.1f);
        int round2 = Math.round(((float) i3) * 0.1f);
        int round3 = Math.round(((float) i4) * 0.1f);
        Log.d("SettingsHomepageActivity", "setTitleBk: " + alpha + "red1 ===" + round + "green1 ===" + round2 + "blue1 ==" + round3 + "alpha1 ===" + i5);
        int argb = Color.argb(i5, round, round2, round3);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{-16777216, argb});
        gradientDrawable.setGradientType(0);
        gradientDrawable.setSize(2000, 60);
        ((TextView) findViewById(R$id.setting_top_title)).setBackground(gradientDrawable);
    }

    private void updateHomepagePaddings() {
        if (this.mIsEmbeddingActivityEnabled) {
            if (this.mIsTwoPane) {
                this.mMainFragment.setPaddingHorizontal(getResources().getDimensionPixelSize(R$dimen.homepage_padding_horizontal_two_pane));
            } else {
                this.mMainFragment.setPaddingHorizontal(0);
            }
            this.mMainFragment.updatePreferencePadding(this.mIsTwoPane);
        }
    }

    private void updateAppBarMinHeight() {
        int i;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.search_bar_height);
        Resources resources = getResources();
        if (this.mIsEmbeddingActivityEnabled && this.mIsTwoPane) {
            i = R$dimen.homepage_app_bar_padding_two_pane;
        } else {
            i = R$dimen.search_bar_margin;
        }
        resources.getDimensionPixelSize(i);
        findViewById(R$id.app_bar_container).setMinimumHeight(dimensionPixelSize + 15);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Log.d("SettingsHomepageActivity", "onStop: ");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Log.d("SettingsHomepageActivity", "onDestroy: ");
        super.onDestroy();
        SettingsApplication.mApplication.removeAllListener();
        if (SettingsApplication.isRecentApp) {
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SuggestionFragCreator implements FragmentCreator {
        private final Class<? extends Fragment> mClass;
        private final boolean mIsTwoPaneLayout;

        SuggestionFragCreator(Class<? extends Fragment> cls, boolean z) {
            this.mClass = cls;
            this.mIsTwoPaneLayout = z;
        }

        @Override // com.android.settings.homepage.SettingsHomepageActivity.FragmentCreator
        public Fragment create() {
            try {
                return this.mClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                Log.w("SettingsHomepageActivity", "Cannot show fragment", e);
                return null;
            }
        }

        @Override // com.android.settings.homepage.SettingsHomepageActivity.FragmentCreator
        public void init(Fragment fragment) {
            if (fragment instanceof SplitLayoutListener) {
                ((SplitLayoutListener) fragment).setSplitLayoutSupported(this.mIsTwoPaneLayout);
            }
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Log.d("SettingsHomepageActivity", "onBackPressed: ");
        super.onBackPressed();
    }
}
