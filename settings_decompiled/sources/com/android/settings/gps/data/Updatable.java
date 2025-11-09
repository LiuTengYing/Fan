package com.android.settings.gps.data;

import com.syu.ipcself.module.main.Main;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class Updatable implements Runnable {
    protected float[] mFlts;
    protected int[] mInts;
    protected String[] mStrs;
    private List<IUpdater> mUpdaters = new ArrayList();

    /* loaded from: classes.dex */
    public interface IUpdater {
        void onUpdate();
    }

    public abstract boolean onUpdate();

    public void addUpdater(IUpdater iUpdater, boolean z) {
        if (iUpdater == null) {
            throw new NullPointerException("updater == null");
        }
        if (this.mUpdaters.contains(iUpdater)) {
            return;
        }
        this.mUpdaters.add(iUpdater);
        if (z) {
            iUpdater.onUpdate();
        }
    }

    public void removeUpdater(IUpdater iUpdater) {
        this.mUpdaters.remove(iUpdater);
    }

    public void notifyUpdater() {
        for (IUpdater iUpdater : this.mUpdaters) {
            iUpdater.onUpdate();
        }
    }

    public void set(int[] iArr, float[] fArr, String[] strArr) {
        this.mInts = iArr;
        this.mFlts = fArr;
        this.mStrs = strArr;
        if (onUpdate()) {
            Main.postRunnable_Ui(true, this);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        notifyUpdater();
    }
}
