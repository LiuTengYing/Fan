package androidx.fragment.app.strictmode;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@SuppressLint({"SyntheticAccessor"})
/* loaded from: classes.dex */
public final class FragmentStrictMode {
    private static Policy defaultPolicy = Policy.LAX;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Flag {
        PENALTY_LOG,
        PENALTY_DEATH,
        DETECT_FRAGMENT_REUSE,
        DETECT_FRAGMENT_TAG_USAGE,
        DETECT_RETAIN_INSTANCE_USAGE,
        DETECT_SET_USER_VISIBLE_HINT,
        DETECT_TARGET_FRAGMENT_USAGE,
        DETECT_WRONG_FRAGMENT_CONTAINER
    }

    /* loaded from: classes.dex */
    public interface OnViolationListener {
        void onViolation(Violation violation);
    }

    /* loaded from: classes.dex */
    public static final class Policy {
        public static final Policy LAX = new Policy(new HashSet(), null, new HashMap());
        private final Map<Class<? extends Fragment>, Set<Class<? extends Violation>>> mAllowedViolations;
        private final Set<Flag> mFlags;
        private final OnViolationListener mListener;

        private Policy(Set<Flag> set, OnViolationListener onViolationListener, Map<Class<? extends Fragment>, Set<Class<? extends Violation>>> map) {
            this.mFlags = new HashSet(set);
            this.mListener = onViolationListener;
            HashMap hashMap = new HashMap();
            for (Map.Entry<Class<? extends Fragment>, Set<Class<? extends Violation>>> entry : map.entrySet()) {
                hashMap.put(entry.getKey(), new HashSet(entry.getValue()));
            }
            this.mAllowedViolations = hashMap;
        }
    }

    private static Policy getNearestPolicy(Fragment fragment) {
        while (fragment != null) {
            if (fragment.isAdded()) {
                FragmentManager parentFragmentManager = fragment.getParentFragmentManager();
                if (parentFragmentManager.getStrictModePolicy() != null) {
                    return parentFragmentManager.getStrictModePolicy();
                }
            }
            fragment = fragment.getParentFragment();
        }
        return defaultPolicy;
    }

    public static void onFragmentReuse(Fragment fragment, String str) {
        FragmentReuseViolation fragmentReuseViolation = new FragmentReuseViolation(fragment, str);
        logIfDebuggingEnabled(fragmentReuseViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_FRAGMENT_REUSE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), FragmentReuseViolation.class)) {
            handlePolicyViolation(nearestPolicy, fragmentReuseViolation);
        }
    }

    public static void onFragmentTagUsage(Fragment fragment, ViewGroup viewGroup) {
        FragmentTagUsageViolation fragmentTagUsageViolation = new FragmentTagUsageViolation(fragment, viewGroup);
        logIfDebuggingEnabled(fragmentTagUsageViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_FRAGMENT_TAG_USAGE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), FragmentTagUsageViolation.class)) {
            handlePolicyViolation(nearestPolicy, fragmentTagUsageViolation);
        }
    }

    public static void onSetRetainInstanceUsage(Fragment fragment) {
        SetRetainInstanceUsageViolation setRetainInstanceUsageViolation = new SetRetainInstanceUsageViolation(fragment);
        logIfDebuggingEnabled(setRetainInstanceUsageViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_RETAIN_INSTANCE_USAGE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), SetRetainInstanceUsageViolation.class)) {
            handlePolicyViolation(nearestPolicy, setRetainInstanceUsageViolation);
        }
    }

    public static void onGetRetainInstanceUsage(Fragment fragment) {
        GetRetainInstanceUsageViolation getRetainInstanceUsageViolation = new GetRetainInstanceUsageViolation(fragment);
        logIfDebuggingEnabled(getRetainInstanceUsageViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_RETAIN_INSTANCE_USAGE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), GetRetainInstanceUsageViolation.class)) {
            handlePolicyViolation(nearestPolicy, getRetainInstanceUsageViolation);
        }
    }

    public static void onSetUserVisibleHint(Fragment fragment, boolean z) {
        SetUserVisibleHintViolation setUserVisibleHintViolation = new SetUserVisibleHintViolation(fragment, z);
        logIfDebuggingEnabled(setUserVisibleHintViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_SET_USER_VISIBLE_HINT) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), SetUserVisibleHintViolation.class)) {
            handlePolicyViolation(nearestPolicy, setUserVisibleHintViolation);
        }
    }

    public static void onSetTargetFragmentUsage(Fragment fragment, Fragment fragment2, int i) {
        SetTargetFragmentUsageViolation setTargetFragmentUsageViolation = new SetTargetFragmentUsageViolation(fragment, fragment2, i);
        logIfDebuggingEnabled(setTargetFragmentUsageViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_TARGET_FRAGMENT_USAGE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), SetTargetFragmentUsageViolation.class)) {
            handlePolicyViolation(nearestPolicy, setTargetFragmentUsageViolation);
        }
    }

    public static void onGetTargetFragmentUsage(Fragment fragment) {
        GetTargetFragmentUsageViolation getTargetFragmentUsageViolation = new GetTargetFragmentUsageViolation(fragment);
        logIfDebuggingEnabled(getTargetFragmentUsageViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_TARGET_FRAGMENT_USAGE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), GetTargetFragmentUsageViolation.class)) {
            handlePolicyViolation(nearestPolicy, getTargetFragmentUsageViolation);
        }
    }

    public static void onGetTargetFragmentRequestCodeUsage(Fragment fragment) {
        GetTargetFragmentRequestCodeUsageViolation getTargetFragmentRequestCodeUsageViolation = new GetTargetFragmentRequestCodeUsageViolation(fragment);
        logIfDebuggingEnabled(getTargetFragmentRequestCodeUsageViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_TARGET_FRAGMENT_USAGE) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), GetTargetFragmentRequestCodeUsageViolation.class)) {
            handlePolicyViolation(nearestPolicy, getTargetFragmentRequestCodeUsageViolation);
        }
    }

    public static void onWrongFragmentContainer(Fragment fragment, ViewGroup viewGroup) {
        WrongFragmentContainerViolation wrongFragmentContainerViolation = new WrongFragmentContainerViolation(fragment, viewGroup);
        logIfDebuggingEnabled(wrongFragmentContainerViolation);
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (nearestPolicy.mFlags.contains(Flag.DETECT_WRONG_FRAGMENT_CONTAINER) && shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), WrongFragmentContainerViolation.class)) {
            handlePolicyViolation(nearestPolicy, wrongFragmentContainerViolation);
        }
    }

    static void onPolicyViolation(Violation violation) {
        logIfDebuggingEnabled(violation);
        Fragment fragment = violation.getFragment();
        Policy nearestPolicy = getNearestPolicy(fragment);
        if (shouldHandlePolicyViolation(nearestPolicy, fragment.getClass(), violation.getClass())) {
            handlePolicyViolation(nearestPolicy, violation);
        }
    }

    private static void logIfDebuggingEnabled(Violation violation) {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "StrictMode violation in " + violation.getFragment().getClass().getName(), violation);
        }
    }

    private static boolean shouldHandlePolicyViolation(Policy policy, Class<? extends Fragment> cls, Class<? extends Violation> cls2) {
        Set set = (Set) policy.mAllowedViolations.get(cls);
        if (set == null) {
            return true;
        }
        if (cls2.getSuperclass() == Violation.class || !set.contains(cls2.getSuperclass())) {
            return !set.contains(cls2);
        }
        return false;
    }

    private static void handlePolicyViolation(final Policy policy, final Violation violation) {
        Fragment fragment = violation.getFragment();
        final String name = fragment.getClass().getName();
        if (policy.mFlags.contains(Flag.PENALTY_LOG)) {
            Log.d("FragmentStrictMode", "Policy violation in " + name, violation);
        }
        if (policy.mListener != null) {
            runOnHostThread(fragment, new Runnable() { // from class: androidx.fragment.app.strictmode.FragmentStrictMode.1
                @Override // java.lang.Runnable
                public void run() {
                    Policy.this.mListener.onViolation(violation);
                }
            });
        }
        if (policy.mFlags.contains(Flag.PENALTY_DEATH)) {
            runOnHostThread(fragment, new Runnable() { // from class: androidx.fragment.app.strictmode.FragmentStrictMode.2
                @Override // java.lang.Runnable
                public void run() {
                    Log.e("FragmentStrictMode", "Policy violation with PENALTY_DEATH in " + name, violation);
                    throw violation;
                }
            });
        }
    }

    private static void runOnHostThread(Fragment fragment, Runnable runnable) {
        if (fragment.isAdded()) {
            Handler handler = fragment.getParentFragmentManager().getHost().getHandler();
            if (handler.getLooper() == Looper.myLooper()) {
                runnable.run();
                return;
            } else {
                handler.post(runnable);
                return;
            }
        }
        runnable.run();
    }
}
