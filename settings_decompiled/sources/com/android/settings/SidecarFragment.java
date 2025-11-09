package com.android.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes.dex */
public class SidecarFragment extends Fragment {
    private boolean mCreated;
    private Set<Listener> mListeners = new CopyOnWriteArraySet();
    private int mState;
    private int mSubstate;

    /* loaded from: classes.dex */
    public interface Listener {
        void onStateChange(SidecarFragment sidecarFragment);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static <T extends SidecarFragment> T get(FragmentManager fragmentManager, String str, Class<T> cls, Bundle bundle) {
        T t = (T) fragmentManager.findFragmentByTag(str);
        if (t == null) {
            try {
                t = cls.newInstance();
                if (bundle != null) {
                    t.setArguments(bundle);
                }
                fragmentManager.beginTransaction().add(t, str).commit();
                fragmentManager.executePendingTransactions();
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to create fragment", e);
            } catch (InstantiationException e2) {
                throw new Fragment.InstantiationException("Unable to create fragment", e2);
            }
        }
        return t;
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        this.mCreated = true;
        setState(0, 0);
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        this.mCreated = false;
        super.onDestroy();
    }

    public void addListener(Listener listener) {
        ThreadUtils.ensureMainThread();
        this.mListeners.add(listener);
        if (this.mCreated) {
            notifyListener(listener);
        }
    }

    public boolean removeListener(Listener listener) {
        ThreadUtils.ensureMainThread();
        return this.mListeners.remove(listener);
    }

    public int getState() {
        return this.mState;
    }

    public void reset() {
        setState(0, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setState(int i, int i2) {
        ThreadUtils.ensureMainThread();
        this.mState = i;
        this.mSubstate = i2;
        notifyAllListeners();
        printState();
    }

    private void notifyAllListeners() {
        for (Listener listener : this.mListeners) {
            notifyListener(listener);
        }
    }

    private void notifyListener(Listener listener) {
        listener.onStateChange(this);
    }

    public void printState() {
        StringBuilder sb = new StringBuilder("SidecarFragment.setState(): Sidecar Class: ");
        sb.append(getClass().getCanonicalName());
        sb.append(", State: ");
        int i = this.mState;
        if (i == 0) {
            sb.append("State.INIT");
        } else if (i == 1) {
            sb.append("State.RUNNING");
        } else if (i == 2) {
            sb.append("State.SUCCESS");
        } else if (i == 3) {
            sb.append("State.ERROR");
        } else {
            sb.append(i);
        }
        if (this.mSubstate == 0) {
            sb.append(", Substate.UNUSED");
        } else {
            sb.append(", ");
            sb.append(this.mSubstate);
        }
        Log.v("SidecarFragment", sb.toString());
    }

    @Override // android.app.Fragment
    public String toString() {
        return String.format(Locale.US, "SidecarFragment[mState=%d, mSubstate=%d]: %s", Integer.valueOf(this.mState), Integer.valueOf(this.mSubstate), super.toString());
    }
}
