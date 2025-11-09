package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class UsbDetailsFragment extends DashboardFragment {
    private List<UsbDetailsController> mControllers;
    private UsbBackend mUsbBackend;
    private UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() { // from class: com.android.settings.connecteddevice.usb.UsbDetailsFragment$$ExternalSyntheticLambda0
        @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
        public final void onUsbConnectionChanged(boolean z, long j, int i, int i2, boolean z2) {
            UsbDetailsFragment.this.lambda$new$0(z, j, i, i2, z2);
        }
    };
    UsbConnectionBroadcastReceiver mUsbReceiver;
    private static final String TAG = UsbDetailsFragment.class.getSimpleName();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.usb_details_fragment) { // from class: com.android.settings.connecteddevice.usb.UsbDetailsFragment.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(context, UserHandle.myUserId()) == null;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return new ArrayList(UsbDetailsFragment.createControllerList(context, new UsbBackend(context), null));
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1291;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, long j, int i, int i2, boolean z2) {
        for (UsbDetailsController usbDetailsController : this.mControllers) {
            usbDetailsController.refresh(z, j, i, i2);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Utils.setActionBarShadowAnimation(getActivity(), getSettingsLifecycle(), getListView());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.usb_details_fragment;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        UsbBackend usbBackend = new UsbBackend(context);
        this.mUsbBackend = usbBackend;
        this.mControllers = createControllerList(context, usbBackend, this);
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        getSettingsLifecycle().addObserver(this.mUsbReceiver);
        return new ArrayList(this.mControllers);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<UsbDetailsController> createControllerList(Context context, UsbBackend usbBackend, UsbDetailsFragment usbDetailsFragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new UsbDetailsHeaderController(context, usbDetailsFragment, usbBackend));
        arrayList.add(new UsbDetailsDataRoleController(context, usbDetailsFragment, usbBackend));
        arrayList.add(new UsbDetailsFunctionsController(context, usbDetailsFragment, usbBackend));
        arrayList.add(new UsbDetailsPowerRoleController(context, usbDetailsFragment, usbBackend));
        arrayList.add(new UsbDetailsTranscodeMtpController(context, usbDetailsFragment, usbBackend));
        return arrayList;
    }
}
