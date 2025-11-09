package androidx.mediarouter.media;

import android.os.Bundle;
/* loaded from: classes.dex */
public class MediaRouterParams {
    final int mDialogType;
    final Bundle mExtras;
    final boolean mMediaTransferReceiverEnabled;
    final boolean mOutputSwitcherEnabled;
    final boolean mTransferToLocalEnabled;

    public int getDialogType() {
        return this.mDialogType;
    }

    public boolean isMediaTransferReceiverEnabled() {
        return this.mMediaTransferReceiverEnabled;
    }

    public boolean isOutputSwitcherEnabled() {
        return this.mOutputSwitcherEnabled;
    }

    public boolean isTransferToLocalEnabled() {
        return this.mTransferToLocalEnabled;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }
}
