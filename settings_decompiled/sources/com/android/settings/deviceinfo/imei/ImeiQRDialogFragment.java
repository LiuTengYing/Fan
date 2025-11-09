package com.android.settings.deviceinfo.imei;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.utils.EncodingHandler;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class ImeiQRDialogFragment extends InstrumentedDialogFragment {
    static final String TAG = "ImeiQRDialogFragment";
    private static final int[] sViewIdsInDigitFormat = IntStream.of(ImeiInfoDialogController.ID_MEID_NUMBER_VALUE, ImeiInfoDialogController.ID_MIN_NUMBER_VALUE, ImeiInfoDialogController.ID_IMEI_VALUE, ImeiInfoDialogController.ID_IMEI_SV_VALUE).sorted().toArray();
    private View mRootView;
    private ImageView qrImg;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1240;
    }

    public static void show(Fragment fragment, int i, String str, String str2) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("arg_key_slot_id", i);
            bundle.putString("arg_key_dialog_title", str);
            bundle.putString("imei_number", str2);
            ImeiQRDialogFragment imeiQRDialogFragment = new ImeiQRDialogFragment();
            imeiQRDialogFragment.setArguments(bundle);
            imeiQRDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        int i = arguments.getInt("arg_key_slot_id");
        String string = arguments.getString("arg_key_dialog_title");
        String string2 = arguments.getString("imei_number");
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setTitle(string).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        this.mRootView = LayoutInflater.from(positiveButton.getContext()).inflate(R$layout.dialog_imei_qr, (ViewGroup) null);
        init(i, string2);
        return positiveButton.setView(this.mRootView).create();
    }

    private void init(int i, String str) {
        ImageView imageView = (ImageView) this.mRootView.findViewById(R$id.imei_qr_img);
        this.qrImg = imageView;
        try {
            imageView.setImageBitmap(EncodingHandler.CreateOneDCode(str));
        } catch (Exception e) {
            Log.d(TAG, "init: " + e.toString());
        }
    }
}
