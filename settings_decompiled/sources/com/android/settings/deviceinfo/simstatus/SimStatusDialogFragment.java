package com.android.settings.deviceinfo.simstatus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.PhoneNumberUtil;
import java.util.Arrays;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class SimStatusDialogFragment extends InstrumentedDialogFragment {
    private static final int[] sViewIdsInDigitFormat = IntStream.of(SimStatusDialogController.ICCID_INFO_VALUE_ID, SimStatusDialogController.PHONE_NUMBER_VALUE_ID, SimStatusDialogController.EID_INFO_VALUE_ID).sorted().toArray();
    private SimStatusDialogController mController;
    private View mRootView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1246;
    }

    public static void show(Fragment fragment, int i, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("SimStatusDialog") == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("arg_key_sim_slot", i);
            bundle.putString("arg_key_dialog_title", str);
            SimStatusDialogFragment simStatusDialogFragment = new SimStatusDialogFragment();
            simStatusDialogFragment.setArguments(bundle);
            simStatusDialogFragment.show(childFragmentManager, "SimStatusDialog");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        int i = arguments.getInt("arg_key_sim_slot");
        String string = arguments.getString("arg_key_dialog_title");
        this.mController = new SimStatusDialogController(this, this.mLifecycle, i);
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setTitle(string).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        this.mRootView = LayoutInflater.from(positiveButton.getContext()).inflate(R$layout.dialog_sim_status, (ViewGroup) null);
        this.mController.initialize();
        AlertDialog create = positiveButton.setView(this.mRootView).create();
        create.getWindow().setFlags(8192, 8192);
        return create;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mController.deinitialize();
        super.onDestroy();
    }

    public void removeSettingFromScreen(int i) {
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
