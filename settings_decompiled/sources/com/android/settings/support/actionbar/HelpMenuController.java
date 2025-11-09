package com.android.settings.support.actionbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu;
/* loaded from: classes.dex */
public class HelpMenuController implements LifecycleObserver, OnCreateOptionsMenu {
    private final Fragment mHost;

    @Override // com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        int helpResource;
        Bundle arguments = this.mHost.getArguments();
        if (arguments != null && arguments.containsKey("help_uri_resource")) {
            helpResource = arguments.getInt("help_uri_resource");
        } else {
            Fragment fragment = this.mHost;
            helpResource = fragment instanceof HelpResourceProvider ? ((HelpResourceProvider) fragment).getHelpResource() : 0;
        }
        String string = helpResource != 0 ? this.mHost.getContext().getString(helpResource) : null;
        FragmentActivity activity = this.mHost.getActivity();
        if (string == null || activity == null) {
            return;
        }
        HelpUtils.prepareHelpMenuItem(activity, menu, string, this.mHost.getClass().getName());
    }
}
