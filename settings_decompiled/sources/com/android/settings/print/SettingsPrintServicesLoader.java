package com.android.settings.print;

import android.content.Context;
import android.print.PrintManager;
import android.print.PrintServicesLoader;
import android.printservice.PrintServiceInfo;
import androidx.loader.content.Loader;
import com.android.internal.util.Preconditions;
import java.util.List;
/* loaded from: classes.dex */
public class SettingsPrintServicesLoader extends Loader<List<PrintServiceInfo>> {
    private PrintServicesLoader mLoader;

    public SettingsPrintServicesLoader(PrintManager printManager, Context context, int i) {
        super((Context) Preconditions.checkNotNull(context));
        this.mLoader = new PrintServicesLoader(printManager, context, i) { // from class: com.android.settings.print.SettingsPrintServicesLoader.1
            public void deliverResult(List<PrintServiceInfo> list) {
                super.deliverResult(list);
                SettingsPrintServicesLoader.this.deliverResult(list);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onForceLoad() {
        this.mLoader.forceLoad();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStartLoading() {
        this.mLoader.startLoading();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStopLoading() {
        this.mLoader.stopLoading();
    }

    @Override // androidx.loader.content.Loader
    protected boolean onCancelLoad() {
        return this.mLoader.cancelLoad();
    }

    @Override // androidx.loader.content.Loader
    protected void onAbandon() {
        this.mLoader.abandon();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onReset() {
        this.mLoader.reset();
    }
}
