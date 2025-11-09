package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.sysprop.VoldProperties;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.Settings;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes.dex */
public class MainClear extends InstrumentedFragment implements ViewTreeObserver.OnGlobalLayoutListener {
    static final int CREDENTIAL_CONFIRM_REQUEST = 56;
    static final int KEYGUARD_REQUEST = 55;
    private int mBatteryLevel;
    private BatteryManager mBatteryManager;
    private View mContentView;
    private Context mContext;
    CheckBox mEsimStorage;
    View mEsimStorageContainer;
    CheckBox mExternalStorage;
    private View mExternalStorageContainer;
    FooterButton mInitiateButton;
    private AlertDialog mLowBatteryPromptDialog;
    ScrollView mScrollView;
    private boolean mIsPromptDialogShowing = false;
    private boolean mLowBatteryPromptSupported = false;
    protected final View.OnClickListener mInitiateListener = new View.OnClickListener() { // from class: com.android.settings.MainClear.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Context context = view.getContext();
            if (Utils.isDemoUser(context)) {
                ComponentName deviceOwnerComponent = Utils.getDeviceOwnerComponent(context);
                if (deviceOwnerComponent != null) {
                    context.startActivity(new Intent().setPackage(deviceOwnerComponent.getPackageName()).setAction("android.intent.action.FACTORY_RESET"));
                    return;
                }
                return;
            }
            if (MainClear.this.mBatteryLevel == 0) {
                MainClear mainClear = MainClear.this;
                mainClear.mBatteryLevel = mainClear.mBatteryManager.getIntProperty(4);
            }
            if (MainClear.this.mLowBatteryPromptSupported && MainClear.this.mBatteryLevel < 30) {
                MainClear.this.mIsPromptDialogShowing = true;
                MainClear.this.showPromptDialog();
            } else if (MainClear.this.runKeyguardConfirmation(55)) {
            } else {
                Intent accountConfirmationIntent = MainClear.this.getAccountConfirmationIntent();
                if (accountConfirmationIntent != null) {
                    MainClear.this.showAccountCredentialConfirmation(accountConfirmationIntent);
                } else {
                    MainClear.this.showFinalConfirmation();
                }
            }
        }
    };
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.MainClear.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                int i = -1;
                int intExtra = intent.getIntExtra("level", -1);
                int intExtra2 = intent.getIntExtra("scale", -1);
                if (intExtra >= 0 && intExtra2 > 0) {
                    i = (intExtra * 100) / intExtra2;
                }
                MainClear.this.mBatteryLevel = i;
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 66;
    }

    boolean isValidRequestCode(int i) {
        return i == 55 || i == 56;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        this.mInitiateButton.setEnabled(hasReachedBottom(this.mScrollView));
    }

    private void setUpActionBarAndTitle() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e("MainClear", "No activity attached, skipping setUpActionBarAndTitle");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.e("MainClear", "No actionbar, skipping setUpActionBarAndTitle");
            return;
        }
        actionBar.hide();
        activity.getWindow().setStatusBarColor(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(R$string.main_clear_short_title)).show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        onActivityResultInternal(i, i2, intent);
    }

    void onActivityResultInternal(int i, int i2, Intent intent) {
        Intent accountConfirmationIntent;
        if (isValidRequestCode(i)) {
            if (i2 != -1) {
                establishInitialState();
            } else if (56 != i && (accountConfirmationIntent = getAccountConfirmationIntent()) != null) {
                showAccountCredentialConfirmation(accountConfirmationIntent);
            } else {
                showFinalConfirmation();
            }
        }
    }

    void showFinalConfirmation() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("erase_sd", this.mExternalStorage.isChecked());
        bundle.putBoolean("erase_esim", this.mEsimStorage.isChecked());
        Intent intent = new Intent();
        intent.setClass(getContext(), Settings.FactoryResetConfirmActivity.class);
        intent.putExtra(":settings:show_fragment", MainClearConfirm.class.getName());
        intent.putExtra(":settings:show_fragment_args", bundle);
        intent.putExtra(":settings:show_fragment_title_resid", R$string.main_clear_confirm_title);
        intent.putExtra(":settings:source_metrics", getMetricsCategory());
        getContext().startActivity(intent);
    }

    void showAccountCredentialConfirmation(Intent intent) {
        startActivityForResult(intent, 56);
    }

    Intent getAccountConfirmationIntent() {
        ActivityInfo activityInfo;
        FragmentActivity activity = getActivity();
        String string = activity.getString(R$string.account_type);
        String string2 = activity.getString(R$string.account_confirmation_package);
        String string3 = activity.getString(R$string.account_confirmation_class);
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2) || TextUtils.isEmpty(string3)) {
            Log.i("MainClear", "Resources not set for account confirmation.");
            return null;
        }
        Account[] accountsByType = AccountManager.get(activity).getAccountsByType(string);
        if (accountsByType != null && accountsByType.length > 0) {
            Intent component = new Intent().setPackage(string2).setComponent(new ComponentName(string2, string3));
            ResolveInfo resolveActivity = activity.getPackageManager().resolveActivity(component, 0);
            if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null && string2.equals(activityInfo.packageName)) {
                return component;
            }
            Log.i("MainClear", "Unable to resolve Activity: " + string2 + "/" + string3);
        } else {
            Log.d("MainClear", "No " + string + " accounts installed!");
        }
        return null;
    }

    void establishInitialState() {
        setUpActionBarAndTitle();
        setUpInitiateButton();
        this.mExternalStorageContainer = this.mContentView.findViewById(R$id.erase_external_container);
        this.mExternalStorage = (CheckBox) this.mContentView.findViewById(R$id.erase_external);
        this.mEsimStorageContainer = this.mContentView.findViewById(R$id.erase_esim_container);
        this.mEsimStorage = (CheckBox) this.mContentView.findViewById(R$id.erase_esim);
        ScrollView scrollView = this.mScrollView;
        if (scrollView != null) {
            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        this.mScrollView = (ScrollView) this.mContentView.findViewById(R$id.main_clear_scrollview);
        boolean isExternalStorageEmulated = Environment.isExternalStorageEmulated();
        if (isExternalStorageEmulated || (!Environment.isExternalStorageRemovable() && isExtStorageEncrypted())) {
            this.mExternalStorageContainer.setVisibility(8);
            this.mContentView.findViewById(R$id.erase_external_option_text).setVisibility(8);
            this.mContentView.findViewById(R$id.also_erases_external).setVisibility(0);
            this.mExternalStorage.setChecked(!isExternalStorageEmulated);
        } else {
            this.mExternalStorageContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MainClear.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MainClear.this.mExternalStorage.toggle();
                }
            });
        }
        if (showWipeEuicc()) {
            if (showWipeEuiccCheckbox()) {
                this.mEsimStorageContainer.setVisibility(0);
                this.mEsimStorageContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MainClear.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        MainClear.this.mEsimStorage.toggle();
                    }
                });
            } else {
                this.mContentView.findViewById(R$id.also_erases_esim).setVisibility(0);
                this.mContentView.findViewById(R$id.no_cancel_mobile_plan).setVisibility(0);
                this.mEsimStorage.setChecked(true);
            }
        } else {
            this.mEsimStorage.setChecked(false);
        }
        loadAccountList((UserManager) getActivity().getSystemService("user"));
        StringBuffer stringBuffer = new StringBuffer();
        View findViewById = this.mContentView.findViewById(R$id.main_clear_container);
        getContentDescription(findViewById, stringBuffer);
        findViewById.setContentDescription(stringBuffer);
        this.mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() { // from class: com.android.settings.MainClear.4
            @Override // android.view.View.OnScrollChangeListener
            public void onScrollChange(View view, int i, int i2, int i3, int i4) {
                if ((view instanceof ScrollView) && MainClear.this.hasReachedBottom((ScrollView) view)) {
                    MainClear.this.mInitiateButton.setEnabled(true);
                    MainClear.this.mScrollView.setOnScrollChangeListener(null);
                }
            }
        });
        this.mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    boolean showWipeEuicc() {
        Context context = getContext();
        if (isEuiccEnabled(context)) {
            return Settings.Global.getInt(context.getContentResolver(), "euicc_provisioned", 0) != 0 || DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }
        return false;
    }

    boolean showWipeEuiccCheckbox() {
        return SystemProperties.getBoolean("masterclear.allow_retain_esim_profiles_after_fdr", false);
    }

    protected boolean isEuiccEnabled(Context context) {
        return ((EuiccManager) context.getSystemService("euicc")).isEnabled();
    }

    boolean hasReachedBottom(ScrollView scrollView) {
        return scrollView.getChildCount() < 1 || scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) <= 0;
    }

    private void setUpInitiateButton() {
        if (this.mInitiateButton != null) {
            return;
        }
        FooterBarMixin footerBarMixin = (FooterBarMixin) ((GlifLayout) this.mContentView.findViewById(R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class);
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(R$string.main_clear_button_text).setListener(this.mInitiateListener).setButtonType(0).setTheme(R$style.SudGlifButton_Primary).build());
        this.mInitiateButton = footerBarMixin.getPrimaryButton();
    }

    private void getContentDescription(View view, StringBuffer stringBuffer) {
        if (view.getVisibility() != 0) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                getContentDescription(viewGroup.getChildAt(i), stringBuffer);
            }
        } else if (view instanceof TextView) {
            stringBuffer.append(((TextView) view).getText());
            stringBuffer.append(",");
        }
    }

    private boolean isExtStorageEncrypted() {
        return !"".equals((String) VoldProperties.decrypt().orElse(""));
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x017a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void loadAccountList(android.os.UserManager r23) {
        /*
            Method dump skipped, instructions count: 491
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.MainClear.loadAccountList(android.os.UserManager):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$loadAccountList$0() {
        return getString(R$string.parallel_app);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$loadAccountList$1() {
        return getString(R$string.category_work);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$loadAccountList$2() {
        return getString(R$string.category_personal);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = getContext();
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_factory_reset", UserHandle.myUserId());
        boolean z = !UserManager.get(context).isAdminUser() || RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_factory_reset", UserHandle.myUserId());
        this.mLowBatteryPromptSupported = context.getResources().getBoolean(R$bool.config_support_lowBatteryFactoryResetPrompt);
        if (!z || Utils.isDemoUser(context)) {
            if (checkIfRestrictionEnforced != null) {
                new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_factory_reset", checkIfRestrictionEnforced).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.MainClear$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        MainClear.this.lambda$onCreateView$3(dialogInterface);
                    }
                }).show();
                return new View(getContext());
            }
            this.mContext = context;
            if (bundle != null) {
                this.mIsPromptDialogShowing = bundle.getBoolean("isShowing", false);
            }
            this.mContentView = layoutInflater.inflate(R$layout.main_clear, (ViewGroup) null);
            if (this.mLowBatteryPromptSupported && this.mIsPromptDialogShowing) {
                showPromptDialog();
            }
            establishInitialState();
            return this.mContentView;
        }
        return layoutInflater.inflate(R$layout.main_clear_disallowed_screen, (ViewGroup) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$3(DialogInterface dialogInterface) {
        getActivity().finish();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("isShowing", this.mIsPromptDialogShowing);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mBatteryManager = (BatteryManager) this.mContext.getSystemService("batterymanager");
        if (this.mLowBatteryPromptSupported) {
            this.mContext.registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mLowBatteryPromptSupported) {
            this.mContext.unregisterReceiver(this.mBatteryInfoReceiver);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mLowBatteryPromptDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mLowBatteryPromptDialog.dismiss();
        }
        this.mLowBatteryPromptDialog = null;
    }

    protected void showPromptDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(R$string.master_clear_level).setTitle(R$string.factory_dialog_title).setPositiveButton(this.mContext.getString(17039370), new DialogInterface.OnClickListener() { // from class: com.android.settings.MainClear.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (MainClear.this.runKeyguardConfirmation(55)) {
                    return;
                }
                Intent accountConfirmationIntent = MainClear.this.getAccountConfirmationIntent();
                if (accountConfirmationIntent != null) {
                    MainClear.this.showAccountCredentialConfirmation(accountConfirmationIntent);
                } else {
                    MainClear.this.showFinalConfirmation();
                }
            }
        }).setNegativeButton(this.mContext.getString(17039360), (DialogInterface.OnClickListener) null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.MainClear.6
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                MainClear.this.mIsPromptDialogShowing = false;
            }
        });
        this.mLowBatteryPromptDialog = builder.show();
    }
}
