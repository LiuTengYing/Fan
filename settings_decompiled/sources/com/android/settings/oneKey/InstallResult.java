package com.android.settings.oneKey;
/* loaded from: classes.dex */
public class InstallResult {
    private static InstallResult mInstance;
    private InstallListener listener;

    public static InstallResult getmInstance() {
        if (mInstance == null) {
            mInstance = new InstallResult();
        }
        return mInstance;
    }

    public void setListener(InstallListener installListener) {
        this.listener = installListener;
    }

    public void updateSuccess() {
        InstallListener installListener = this.listener;
        if (installListener != null) {
            installListener.success();
        }
    }

    public void updateFailed(String str) {
        InstallListener installListener = this.listener;
        if (installListener != null) {
            installListener.failed(str);
        }
    }
}
