package com.android.settings.enterprise;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Process;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.enterprise.ActionDisabledByAdminController;
import com.android.settingslib.enterprise.ActionDisabledByAdminControllerFactory;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public final class ActionDisabledByAdminDialogHelper {
    private final ActionDisabledByAdminController mActionDisabledByAdminController;
    private final Activity mActivity;
    private ViewGroup mDialogView;
    RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private String mRestriction;

    public ActionDisabledByAdminDialogHelper(Activity activity) {
        this(activity, null);
    }

    public ActionDisabledByAdminDialogHelper(Activity activity, String str) {
        this.mActivity = activity;
        this.mDialogView = (ViewGroup) LayoutInflater.from(activity).inflate(R$layout.support_details_dialog, (ViewGroup) null);
        this.mActionDisabledByAdminController = ActionDisabledByAdminControllerFactory.createInstance(activity, str, new DeviceAdminStringProviderImpl(activity), UserHandle.SYSTEM);
        ((TextView) this.mDialogView.findViewById(R$id.admin_support_dialog_title)).setText(((DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.DISABLED_BY_IT_ADMIN_TITLE", new Supplier() { // from class: com.android.settings.enterprise.ActionDisabledByAdminDialogHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$new$0;
                lambda$new$0 = ActionDisabledByAdminDialogHelper.this.lambda$new$0();
                return lambda$new$0;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$new$0() {
        return this.mActivity.getString(R$string.disabled_by_policy_title);
    }

    private int getEnforcementAdminUserId(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        UserHandle userHandle = enforcedAdmin.user;
        if (userHandle == null) {
            return -10000;
        }
        return userHandle.getIdentifier();
    }

    private int getEnforcementAdminUserId() {
        return getEnforcementAdminUserId(this.mEnforcedAdmin);
    }

    public AlertDialog.Builder prepareDialogBuilder(String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        AlertDialog.Builder view = new AlertDialog.Builder(this.mActivity).setPositiveButton(R$string.okay, this.mActionDisabledByAdminController.getPositiveButtonListener(this.mActivity, enforcedAdmin)).setView(this.mDialogView);
        prepareDialogBuilder(view, str, enforcedAdmin);
        return view;
    }

    void prepareDialogBuilder(AlertDialog.Builder builder, String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mActionDisabledByAdminController.initialize(new ActionDisabledLearnMoreButtonLauncherImpl(this.mActivity, builder));
        this.mEnforcedAdmin = enforcedAdmin;
        this.mRestriction = str;
        initializeDialogViews(this.mDialogView, enforcedAdmin, getEnforcementAdminUserId(), this.mRestriction);
        this.mActionDisabledByAdminController.setupLearnMoreButton(this.mActivity);
    }

    public void updateDialog(String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mEnforcedAdmin.equals(enforcedAdmin) && Objects.equals(this.mRestriction, str)) {
            return;
        }
        this.mEnforcedAdmin = enforcedAdmin;
        this.mRestriction = str;
        initializeDialogViews(this.mDialogView, enforcedAdmin, getEnforcementAdminUserId(), this.mRestriction);
    }

    private void initializeDialogViews(View view, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, int i, String str) {
        ComponentName componentName = enforcedAdmin.component;
        if (componentName == null) {
            return;
        }
        this.mActionDisabledByAdminController.updateEnforcedAdmin(enforcedAdmin, i);
        setAdminSupportIcon(view, componentName, i);
        if (isNotCurrentUserOrProfile(componentName, i)) {
            componentName = null;
        }
        setAdminSupportTitle(view, str);
        setAdminSupportDetails(this.mActivity, view, new RestrictedLockUtils.EnforcedAdmin(componentName, i != -10000 ? UserHandle.of(i) : null));
    }

    private boolean isNotCurrentUserOrProfile(ComponentName componentName, int i) {
        return (RestrictedLockUtilsInternal.isAdminInCurrentUserOrProfile(this.mActivity, componentName) && RestrictedLockUtils.isCurrentUserOrProfile(this.mActivity, i)) ? false : true;
    }

    void setAdminSupportIcon(View view, ComponentName componentName, int i) {
        ImageView imageView = (ImageView) view.requireViewById(R$id.admin_support_icon);
        imageView.setImageDrawable(this.mActivity.getDrawable(R$drawable.ic_lock_closed));
        imageView.setImageTintList(Utils.getColorAccent(this.mActivity));
    }

    void setAdminSupportTitle(View view, String str) {
        TextView textView = (TextView) view.findViewById(R$id.admin_support_dialog_title);
        if (textView == null) {
            return;
        }
        textView.setText(this.mActionDisabledByAdminController.getAdminSupportTitle(str));
    }

    void setAdminSupportDetails(Activity activity, View view, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (enforcedAdmin == null || enforcedAdmin.component == null) {
            return;
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService("device_policy");
        CharSequence charSequence = null;
        if (!RestrictedLockUtilsInternal.isAdminInCurrentUserOrProfile(activity, enforcedAdmin.component) || !RestrictedLockUtils.isCurrentUserOrProfile(activity, getEnforcementAdminUserId(enforcedAdmin))) {
            enforcedAdmin.component = null;
        } else {
            if (enforcedAdmin.user == null) {
                enforcedAdmin.user = UserHandle.of(UserHandle.myUserId());
            }
            if (UserHandle.isSameApp(Process.myUid(), AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP)) {
                charSequence = devicePolicyManager.getShortSupportMessageForUser(enforcedAdmin.component, getEnforcementAdminUserId(enforcedAdmin));
            }
        }
        CharSequence adminSupportContentString = this.mActionDisabledByAdminController.getAdminSupportContentString(this.mActivity, charSequence);
        TextView textView = (TextView) view.findViewById(R$id.admin_support_msg);
        if (adminSupportContentString != null) {
            textView.setText(adminSupportContentString);
        }
    }
}
