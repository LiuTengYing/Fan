package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public abstract class AppCounter extends AsyncTask<Void, Void, Integer> {
    protected final List<String> mApps;
    protected final PackageManager mPm;
    protected final UserManager mUm;

    protected abstract boolean includeInCount(ApplicationInfo applicationInfo);

    protected abstract void onCountComplete(int i);

    public AppCounter(Context context, PackageManager packageManager) {
        this.mPm = packageManager;
        this.mUm = (UserManager) context.getSystemService("user");
        this.mApps = Arrays.asList(context.getResources().getStringArray(134283315));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Integer doInBackground(Void... voidArr) {
        int i = 0;
        for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
            for (ApplicationInfo applicationInfo : this.mPm.getInstalledApplicationsAsUser(33280 | (userInfo.isAdmin() ? 4194304 : 0), userInfo.id)) {
                if (includeInCount(applicationInfo)) {
                    if (userInfo.isCloneProfile()) {
                        if (this.mApps.contains(applicationInfo.packageName)) {
                        }
                    }
                    i++;
                }
            }
        }
        return Integer.valueOf(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Integer num) {
        onCountComplete(num.intValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void executeInForeground() {
        onPostExecute(doInBackground(new Void[0]));
    }
}
