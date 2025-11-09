package com.android.settings.password;

import android.hardware.biometrics.BiometricPrompt;
import android.hardware.biometrics.PromptInfo;
import android.os.Bundle;
import android.os.CancellationSignal;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.password.BiometricFragment;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class BiometricFragment extends InstrumentedFragment {
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback = new AnonymousClass1();
    private BiometricPrompt mBiometricPrompt;
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt.AuthenticationCallback mClientCallback;
    private Executor mClientExecutor;
    private int mUserId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1585;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.BiometricFragment$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 extends BiometricPrompt.AuthenticationCallback {
        AnonymousClass1() {
        }

        @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
        public void onAuthenticationError(final int i, final CharSequence charSequence) {
            BiometricFragment.this.mClientExecutor.execute(new Runnable() { // from class: com.android.settings.password.BiometricFragment$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onAuthenticationError$0(i, charSequence);
                }
            });
            BiometricFragment.this.cleanup();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationError$0(int i, CharSequence charSequence) {
            BiometricFragment.this.mClientCallback.onAuthenticationError(i, charSequence);
        }

        @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
        public void onAuthenticationSucceeded(final BiometricPrompt.AuthenticationResult authenticationResult) {
            BiometricFragment.this.mClientExecutor.execute(new Runnable() { // from class: com.android.settings.password.BiometricFragment$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onAuthenticationSucceeded$1(authenticationResult);
                }
            });
            BiometricFragment.this.cleanup();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationSucceeded$1(BiometricPrompt.AuthenticationResult authenticationResult) {
            BiometricFragment.this.mClientCallback.onAuthenticationSucceeded(authenticationResult);
        }

        @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
        public void onAuthenticationFailed() {
            BiometricFragment.this.mClientExecutor.execute(new Runnable() { // from class: com.android.settings.password.BiometricFragment$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onAuthenticationFailed$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationFailed$2() {
            BiometricFragment.this.mClientCallback.onAuthenticationFailed();
        }

        public void onSystemEvent(final int i) {
            BiometricFragment.this.mClientExecutor.execute(new Runnable() { // from class: com.android.settings.password.BiometricFragment$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onSystemEvent$3(i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemEvent$3(int i) {
            BiometricFragment.this.mClientCallback.onSystemEvent(i);
        }
    }

    public static BiometricFragment newInstance(PromptInfo promptInfo) {
        BiometricFragment biometricFragment = new BiometricFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("prompt_info", promptInfo);
        biometricFragment.setArguments(bundle);
        return biometricFragment;
    }

    public void setCallbacks(Executor executor, BiometricPrompt.AuthenticationCallback authenticationCallback) {
        this.mClientExecutor = executor;
        this.mClientCallback = authenticationCallback;
    }

    public void setUser(int i) {
        this.mUserId = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanup() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        PromptInfo parcelable = getArguments().getParcelable("prompt_info");
        this.mBiometricPrompt = new BiometricPrompt.Builder(getContext()).setTitle(parcelable.getTitle()).setUseDefaultTitle().setDeviceCredentialAllowed(true).setSubtitle(parcelable.getSubtitle()).setDescription(parcelable.getDescription()).setTextForDeviceCredential(parcelable.getDeviceCredentialTitle(), parcelable.getDeviceCredentialSubtitle(), parcelable.getDeviceCredentialDescription()).setConfirmationRequired(parcelable.isConfirmationRequested()).setDisallowBiometricsIfPolicyExists(parcelable.isDisallowBiometricsIfPolicyExists()).setReceiveSystemEvents(true).build();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mCancellationSignal == null) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            this.mCancellationSignal = cancellationSignal;
            this.mBiometricPrompt.authenticateUser(cancellationSignal, this.mClientExecutor, this.mAuthenticationCallback, this.mUserId);
        }
    }
}
