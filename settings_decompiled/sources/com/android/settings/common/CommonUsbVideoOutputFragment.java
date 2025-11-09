package com.android.settings.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class CommonUsbVideoOutputFragment extends Fragment {
    private View mRootView;

    private void initViews() {
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R$layout.common_usb_video_output_layout, viewGroup, false);
        initViews();
        return this.mRootView;
    }
}
