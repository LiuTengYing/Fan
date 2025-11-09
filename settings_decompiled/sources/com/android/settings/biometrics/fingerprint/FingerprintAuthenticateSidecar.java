package com.android.settings.biometrics.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import com.android.settings.core.InstrumentedFragment;
/* loaded from: classes.dex */
public class FingerprintAuthenticateSidecar extends InstrumentedFragment {
    private boolean isStartAuthenticating = false;
    private FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback() { // from class: com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar.1
        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
            FingerprintAuthenticateSidecar.this.mCancellationSignal = null;
            FingerprintAuthenticateSidecar.this.isStartAuthenticating = false;
            if (FingerprintAuthenticateSidecar.this.mListener != null) {
                FingerprintAuthenticateSidecar.this.mListener.onAuthenticationSucceeded(authenticationResult);
                return;
            }
            FingerprintAuthenticateSidecar.this.mAuthenticationResult = authenticationResult;
            FingerprintAuthenticateSidecar.this.mAuthenticationError = null;
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationFailed() {
            FingerprintAuthenticateSidecar.this.isStartAuthenticating = false;
            if (FingerprintAuthenticateSidecar.this.mListener != null) {
                FingerprintAuthenticateSidecar.this.mListener.onAuthenticationFailed();
            }
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationError(int i, CharSequence charSequence) {
            FingerprintAuthenticateSidecar.this.mCancellationSignal = null;
            FingerprintAuthenticateSidecar.this.isStartAuthenticating = false;
            if (FingerprintAuthenticateSidecar.this.mListener != null) {
                FingerprintAuthenticateSidecar.this.mListener.onAuthenticationError(i, charSequence);
                return;
            }
            FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = FingerprintAuthenticateSidecar.this;
            fingerprintAuthenticateSidecar.mAuthenticationError = new AuthenticationError(i, charSequence);
            FingerprintAuthenticateSidecar.this.mAuthenticationResult = null;
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationHelp(int i, CharSequence charSequence) {
            if (FingerprintAuthenticateSidecar.this.mListener != null) {
                FingerprintAuthenticateSidecar.this.mListener.onAuthenticationHelp(i, charSequence);
            }
        }
    };
    private AuthenticationError mAuthenticationError;
    private FingerprintManager.AuthenticationResult mAuthenticationResult;
    private CancellationSignal mCancellationSignal;
    private FingerprintManager mFingerprintManager;
    private Listener mListener;

    /* loaded from: classes.dex */
    public interface Listener {
        void onAuthenticationError(int i, CharSequence charSequence);

        void onAuthenticationFailed();

        void onAuthenticationHelp(int i, CharSequence charSequence);

        void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1221;
    }

    /* loaded from: classes.dex */
    private class AuthenticationError {
        int error;
        CharSequence errorString;

        public AuthenticationError(int i, CharSequence charSequence) {
            this.error = i;
            this.errorString = charSequence;
        }
    }

    public void setFingerprintManager(FingerprintManager fingerprintManager) {
        this.mFingerprintManager = fingerprintManager;
    }

    public void startAuthentication(int i) {
        if (this.isStartAuthenticating) {
            Log.d("FingerprintAuthenticateSidecar", "startAuthentication return for isStartAuthenticating is true");
            return;
        }
        CancellationSignal cancellationSignal = new CancellationSignal();
        this.mCancellationSignal = cancellationSignal;
        this.mFingerprintManager.authenticate((FingerprintManager.CryptoObject) null, cancellationSignal, this.mAuthenticationCallback, (Handler) null, i);
        this.isStartAuthenticating = true;
    }

    public void stopAuthentication() {
        if (!this.isStartAuthenticating) {
            Log.d("FingerprintAuthenticateSidecar", "stopAuthentication return for isStartAuthenticating is false");
            return;
        }
        CancellationSignal cancellationSignal = this.mCancellationSignal;
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            this.mCancellationSignal.cancel();
        }
        this.mCancellationSignal = null;
        this.isStartAuthenticating = false;
    }

    public void setListener(Listener listener) {
        int i;
        if (this.mListener == null && listener != null) {
            FingerprintManager.AuthenticationResult authenticationResult = this.mAuthenticationResult;
            if (authenticationResult != null) {
                listener.onAuthenticationSucceeded(authenticationResult);
                this.mAuthenticationResult = null;
            }
            AuthenticationError authenticationError = this.mAuthenticationError;
            if (authenticationError != null && (i = authenticationError.error) != 5) {
                listener.onAuthenticationError(i, authenticationError.errorString);
                this.mAuthenticationError = null;
            }
        }
        this.mListener = listener;
    }
}
