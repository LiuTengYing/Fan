package com.android.settings.deviceinfo.syudevice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.utils.EncodingHandler;
/* loaded from: classes.dex */
public class QrCodeFragment extends InstrumentedDialogFragment {
    private View mRootView;
    private ImageView qrCode;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("QrCodeFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            QrCodeFragment qrCodeFragment = new QrCodeFragment();
            qrCodeFragment.setArguments(bundle);
            qrCodeFragment.show(childFragmentManager, "QrCodeFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setTitle(getArguments().getString("arg_key_dialog_title")).setCancelable(false).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        View inflate = LayoutInflater.from(positiveButton.getContext()).inflate(R$layout.settings_dialog_qrcode_layout, (ViewGroup) null);
        this.mRootView = inflate;
        ImageView imageView = (ImageView) inflate.findViewById(R$id.qr_code_img);
        this.qrCode = imageView;
        try {
            imageView.setImageBitmap(EncodingHandler.createQRCode(getActivity(), getQrCodeStr(), 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
        AlertDialog create = positiveButton.setView(this.mRootView).create();
        create.getWindow().setFlags(8192, 8192);
        return create;
    }

    private String getQrCodeStr() {
        return SystemProperties.get("serialno").substring(0, 16);
    }
}
