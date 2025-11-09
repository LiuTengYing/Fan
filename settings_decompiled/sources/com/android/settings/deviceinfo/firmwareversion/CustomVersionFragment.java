package com.android.settings.deviceinfo.firmwareversion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class CustomVersionFragment extends Fragment {
    private View mRootView;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.layout_custom_version, viewGroup, false);
        this.mRootView = inflate;
        return inflate;
    }
}
