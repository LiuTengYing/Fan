package com.android.settings.development.graphicsdriver;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import com.android.settings.development.graphicsdriver.GraphicsDriverContentObserver;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
/* loaded from: classes.dex */
public class GraphicsDriverGlobalSwitchBarController implements SwitchWidgetController.OnSwitchChangeListener, GraphicsDriverContentObserver.OnGraphicsDriverContentChangedListener, LifecycleObserver, OnStart, OnStop {
    private final ContentResolver mContentResolver;
    private final Context mContext;
    GraphicsDriverContentObserver mGraphicsDriverContentObserver;
    SwitchWidgetController mSwitchWidgetController;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GraphicsDriverGlobalSwitchBarController(Context context, SwitchWidgetController switchWidgetController) {
        this.mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentResolver = contentResolver;
        this.mGraphicsDriverContentObserver = new GraphicsDriverContentObserver(new Handler(Looper.getMainLooper()), this);
        this.mSwitchWidgetController = switchWidgetController;
        switchWidgetController.setEnabled(DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context));
        this.mSwitchWidgetController.setChecked(Settings.Global.getInt(contentResolver, "updatable_driver_all_apps", 0) != 3);
        this.mSwitchWidgetController.setListener(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mSwitchWidgetController.startListening();
        this.mGraphicsDriverContentObserver.register(this.mContentResolver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mSwitchWidgetController.stopListening();
        this.mGraphicsDriverContentObserver.unregister(this.mContentResolver);
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        int i = Settings.Global.getInt(this.mContentResolver, "updatable_driver_all_apps", 0);
        if (z && (i == 0 || i == 1 || i == 2)) {
            return true;
        }
        if (z || i != 3) {
            Settings.Global.putInt(this.mContentResolver, "updatable_driver_all_apps", z ? 0 : 3);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.development.graphicsdriver.GraphicsDriverContentObserver.OnGraphicsDriverContentChangedListener
    public void onGraphicsDriverContentChanged() {
        this.mSwitchWidgetController.setChecked(Settings.Global.getInt(this.mContentResolver, "updatable_driver_all_apps", 0) != 3);
    }
}
