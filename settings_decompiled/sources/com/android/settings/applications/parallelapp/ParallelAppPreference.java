package com.android.settings.applications.parallelapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.RemoteException;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AppSwitchPreference;
import com.unisoc.settings.utils.ToastManager;
/* loaded from: classes.dex */
public class ParallelAppPreference extends AppSwitchPreference {
    private final int HAS_NO_CLONEUSER;
    private final String TAG;
    private Activity mActivity;
    private final ApplicationsState mApplicationsState;
    private Drawable mCacheIcon;
    private Context mContext;
    private DialogInterface.OnClickListener mDialogClickListener;
    private final ApplicationsState.AppEntry mEntry;
    private final PackageInstaller mPackageInstaller;
    private final PackageManager mPm;
    private final UserManager mUm;

    public ParallelAppPreference(Context context, ApplicationsState.AppEntry appEntry, ApplicationsState applicationsState, UserManager userManager, PackageManager packageManager, PackageInstaller packageInstaller) {
        super(context);
        this.HAS_NO_CLONEUSER = -1;
        this.TAG = "ParallelAppPreference";
        this.mDialogClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.parallelapp.ParallelAppPreference.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -2) {
                    ParallelAppPreference.this.setChecked(true);
                    return;
                }
                ParallelAppPreference.this.mPm.deletePackageAsUser(ParallelAppPreference.this.mEntry.info.packageName, null, 0, ParallelAppPreference.this.getCloneUser());
                ParallelAppPreference.this.setChecked(false);
            }
        };
        this.mContext = context;
        this.mEntry = appEntry;
        appEntry.ensureLabel(context);
        this.mApplicationsState = applicationsState;
        this.mPm = packageManager;
        this.mPackageInstaller = packageInstaller;
        this.mUm = userManager;
        setKey(generateKey(appEntry));
        Drawable iconFromCache = AppUtils.getIconFromCache(appEntry);
        this.mCacheIcon = iconFromCache;
        if (iconFromCache != null) {
            setIcon(iconFromCache);
        } else {
            setIcon(R$drawable.empty_icon);
        }
        setTitle(appEntry.label);
        updateState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateKey(ApplicationsState.AppEntry appEntry) {
        return appEntry.info.packageName + "|" + appEntry.info.uid;
    }

    @Override // com.android.settingslib.widget.AppSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mCacheIcon == null) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.applications.parallelapp.ParallelAppPreference$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ParallelAppPreference.this.lambda$onBindViewHolder$1();
                }
            });
        }
        preferenceViewHolder.findViewById(16908312).setVisibility(0);
        super.onBindViewHolder(preferenceViewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1() {
        final Drawable icon = AppUtils.getIcon(getContext(), this.mEntry);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.parallelapp.ParallelAppPreference$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ParallelAppPreference.this.lambda$onBindViewHolder$0(icon);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(Drawable drawable) {
        setIcon(drawable);
        this.mCacheIcon = drawable;
    }

    public void updateState() {
        setChecked(isInstallApp());
    }

    public void createCloseOptionsDialog(Activity activity) {
        this.mActivity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(this.mContext.getResources().getString(R$string.parallel_app_close));
        builder.setPositiveButton(17039370, this.mDialogClickListener);
        builder.setNegativeButton(17039360, this.mDialogClickListener);
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.show();
    }

    public boolean openParApp() {
        if (getCloneUser() != -1 || createAndStartCloneUser()) {
            if (isInstallApp()) {
                return true;
            }
            try {
                int installExistingPackageAsUser = this.mPm.installExistingPackageAsUser(this.mEntry.info.packageName, getCloneUser());
                if (installExistingPackageAsUser == 1) {
                    createToast();
                    return true;
                }
                Log.d("ParallelAppPreference", "open Parallel App failed status:" + installExistingPackageAsUser);
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("ParallelAppPreference", "open Parallel App failed ," + e);
                return false;
            }
        }
        return false;
    }

    private boolean isInstallApp() {
        int cloneUser = getCloneUser();
        if (cloneUser != -1) {
            try {
                return (this.mPm.getApplicationInfoAsUser(this.mEntry.info.packageName, 128, cloneUser).flags & 8388608) != 0;
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("ParallelAppPreference", "can not foud Application " + this.mEntry.label);
                return false;
            }
        }
        return false;
    }

    private void createToast() {
        Toast.makeText(this.mContext, String.format(this.mContext.getResources().getString(R$string.parallel_app_open), this.mEntry.label), 1).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCloneUser() {
        int i = -1;
        for (UserInfo userInfo : this.mUm.getUsers()) {
            String str = userInfo.userType;
            if (str != null && str.equals("android.os.usertype.profile.CLONE")) {
                i = userInfo.id;
            }
        }
        return i;
    }

    private boolean createAndStartCloneUser() {
        String string = this.mContext.getResources().getString(R$string.clone_user);
        if (!this.mUm.canAddMoreUsers("android.os.usertype.profile.CLONE")) {
            Context context = this.mContext;
            Toast makeText = Toast.makeText(context, context.getResources().getString(R$string.add_cloneuser_max_failed), 0);
            ToastManager.setToast(makeText);
            makeText.show();
            return false;
        }
        UserInfo createProfileForUser = this.mUm.createProfileForUser(string, "android.os.usertype.profile.CLONE", 4096, 0);
        if (createProfileForUser == null && isMemoryLow()) {
            Context context2 = this.mContext;
            Toast makeText2 = Toast.makeText(context2, context2.getResources().getString(R$string.create_cloneuser_faile), 0);
            ToastManager.setToast(makeText2);
            makeText2.show();
            return false;
        }
        try {
            ActivityManager.getService().startUserInBackground(createProfileForUser.getUserHandle().getIdentifier());
            return true;
        } catch (RemoteException e) {
            Log.d("ParallelAppPreference", "create and start clone user failed");
            e.printStackTrace();
            return false;
        }
    }

    private boolean isMemoryLow() {
        return Environment.getDataDirectory().getUsableSpace() < ((StorageManager) this.mContext.getSystemService(StorageManager.class)).getStorageLowBytes(Environment.getDataDirectory());
    }
}
