package com.android.settings.enterprise;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd;
import com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ActionDisabledLearnMoreButtonLauncherImpl extends ActionDisabledLearnMoreButtonLauncher {
    private final Activity mActivity;
    private final AlertDialog.Builder mBuilder;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionDisabledLearnMoreButtonLauncherImpl(Activity activity, AlertDialog.Builder builder) {
        Objects.requireNonNull(activity, "activity cannot be null");
        this.mActivity = activity;
        Objects.requireNonNull(builder, "builder cannot be null");
        this.mBuilder = builder;
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher
    public void setLearnMoreButton(final Runnable runnable) {
        Objects.requireNonNull(runnable, "action cannot be null");
        this.mBuilder.setNeutralButton(R$string.learn_more, new DialogInterface.OnClickListener() { // from class: com.android.settings.enterprise.ActionDisabledLearnMoreButtonLauncherImpl$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                runnable.run();
            }
        });
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher
    protected void launchShowAdminPolicies(Context context, UserHandle userHandle, ComponentName componentName) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(userHandle, "user cannot be null");
        Objects.requireNonNull(componentName, "admin cannot be null");
        this.mActivity.startActivityAsUser(new Intent().setClass(this.mActivity, DeviceAdminAdd.class).putExtra("android.app.extra.DEVICE_ADMIN", componentName).putExtra("android.app.extra.CALLED_FROM_SUPPORT_DIALOG", true), userHandle);
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher
    protected void launchShowAdminSettings(Context context) {
        Objects.requireNonNull(context, "context cannot be null");
        this.mActivity.startActivity(new Intent().setClass(this.mActivity, Settings.DeviceAdminSettingsActivity.class).addFlags(268435456));
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher
    protected void finishSelf() {
        this.mActivity.finish();
    }
}
