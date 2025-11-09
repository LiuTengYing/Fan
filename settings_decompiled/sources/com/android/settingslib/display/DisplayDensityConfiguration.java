package com.android.settingslib.display;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.WindowManagerGlobal;
/* loaded from: classes2.dex */
public class DisplayDensityConfiguration {
    public static void clearForcedDisplayDensity(final int i) {
        final int myUserId = UserHandle.myUserId();
        AsyncTask.execute(new Runnable() { // from class: com.android.settingslib.display.DisplayDensityConfiguration$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DisplayDensityConfiguration.lambda$clearForcedDisplayDensity$0(i, myUserId);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$clearForcedDisplayDensity$0(int i, int i2) {
        try {
            WindowManagerGlobal.getWindowManagerService().clearForcedDisplayDensityForUser(i, i2);
        } catch (RemoteException unused) {
            Log.w("DisplayDensityConfig", "Unable to clear forced display density setting");
        }
    }

    public static void setForcedDisplayDensity(final int i, final int i2) {
        final int myUserId = UserHandle.myUserId();
        AsyncTask.execute(new Runnable() { // from class: com.android.settingslib.display.DisplayDensityConfiguration$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DisplayDensityConfiguration.lambda$setForcedDisplayDensity$1(i, i2, myUserId);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setForcedDisplayDensity$1(int i, int i2, int i3) {
        try {
            WindowManagerGlobal.getWindowManagerService().setForcedDisplayDensityForUser(i, i2, i3);
        } catch (RemoteException unused) {
            Log.w("DisplayDensityConfig", "Unable to save forced display density setting");
        }
    }
}
