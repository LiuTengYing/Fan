package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Pair;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.R$string;
import com.android.settings.safetycenter.LockScreenSafetySource;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class SaveChosenLockWorkerBase extends Fragment {
    private boolean mBlocking;
    private boolean mFinished;
    private Listener mListener;
    protected boolean mRequestGatekeeperPassword;
    private Intent mResultData;
    protected LockscreenCredential mUnificationProfileCredential;
    protected int mUnificationProfileId = -10000;
    protected int mUserId;
    protected LockPatternUtils mUtils;
    protected boolean mWasSecureBefore;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Listener {
        void onChosenLockSaveFinished(boolean z, Intent intent);
    }

    protected abstract Pair<Boolean, Intent> saveAndVerifyInBackground();

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void setListener(Listener listener) {
        if (this.mListener == listener) {
            return;
        }
        this.mListener = listener;
        if (!this.mFinished || listener == null) {
            return;
        }
        listener.onChosenLockSaveFinished(this.mWasSecureBefore, this.mResultData);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepare(LockPatternUtils lockPatternUtils, boolean z, boolean z2, int i) {
        this.mUtils = lockPatternUtils;
        this.mUserId = i;
        this.mRequestGatekeeperPassword = z2;
        this.mWasSecureBefore = lockPatternUtils.isSecure(i);
        Context context = getContext();
        if (context == null || UserManager.get(context).getUserInfo(this.mUserId).isPrimary()) {
            this.mUtils.setCredentialRequiredToDecrypt(z);
        }
        this.mFinished = false;
        this.mResultData = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start() {
        if (this.mBlocking) {
            finish((Intent) saveAndVerifyInBackground().second);
        } else {
            new Task().execute(new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finish(Intent intent) {
        this.mFinished = true;
        this.mResultData = intent;
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onChosenLockSaveFinished(this.mWasSecureBefore, intent);
        }
        LockscreenCredential lockscreenCredential = this.mUnificationProfileCredential;
        if (lockscreenCredential != null) {
            lockscreenCredential.zeroize();
        }
        LockScreenSafetySource.onLockScreenChange(getContext());
    }

    public void setBlocking(boolean z) {
        this.mBlocking = z;
    }

    public void setProfileToUnify(int i, LockscreenCredential lockscreenCredential) {
        this.mUnificationProfileId = i;
        this.mUnificationProfileCredential = lockscreenCredential.duplicate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unifyProfileCredentialIfRequested() {
        int i = this.mUnificationProfileId;
        if (i != -10000) {
            this.mUtils.setSeparateProfileChallengeEnabled(i, false, this.mUnificationProfileCredential);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Task extends AsyncTask<Void, Void, Pair<Boolean, Intent>> {
        private Task() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Pair<Boolean, Intent> doInBackground(Void... voidArr) {
            return SaveChosenLockWorkerBase.this.saveAndVerifyInBackground();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Pair<Boolean, Intent> pair) {
            if (!((Boolean) pair.first).booleanValue()) {
                Toast.makeText(SaveChosenLockWorkerBase.this.getContext(), R$string.lockpassword_credential_changed, 1).show();
            }
            SaveChosenLockWorkerBase.this.finish((Intent) pair.second);
        }
    }
}
