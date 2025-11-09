package com.android.settings.deviceinfo.imei;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.PhoneNumberUtil;
import java.util.Arrays;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class ImeiInfoDialogFragment extends InstrumentedDialogFragment {
    static final String TAG = "ImeiInfoDialog";
    private static final int[] sViewIdsInDigitFormat = IntStream.of(ImeiInfoDialogController.ID_MEID_NUMBER_VALUE, ImeiInfoDialogController.ID_MIN_NUMBER_VALUE, ImeiInfoDialogController.ID_IMEI_VALUE, ImeiInfoDialogController.ID_IMEI_SV_VALUE).sorted().toArray();
    private View mRootView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1240;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        int i = arguments.getInt("arg_key_slot_id");
        String string = arguments.getString("arg_key_dialog_title");
        ImeiInfoDialogController imeiInfoDialogController = new ImeiInfoDialogController(this, i);
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setTitle(string).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        this.mRootView = LayoutInflater.from(positiveButton.getContext()).inflate(R$layout.dialog_imei_info, (ViewGroup) null);
        imeiInfoDialogController.populateImeiInfo();
        return positiveButton.setView(this.mRootView).create();
    }

    public void removeViewFromScreen(int i) {
        View findViewById = this.mRootView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
    }

    public void setText(int i, CharSequence charSequence) {
        TextView textView = (TextView) this.mRootView.findViewById(i);
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(charSequence)) {
            charSequence = getResources().getString(R$string.device_info_default);
        } else if (Arrays.binarySearch(sViewIdsInDigitFormat, i) >= 0) {
            charSequence = PhoneNumberUtil.expandByTts(charSequence);
        }
        textView.setText(charSequence);
    }
}
