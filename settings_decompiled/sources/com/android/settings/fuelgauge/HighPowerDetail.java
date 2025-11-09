package com.android.settings.fuelgauge;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
/* loaded from: classes.dex */
public class HighPowerDetail extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {
    PowerAllowlistBackend mBackend;
    BatteryUtils mBatteryUtils;
    private boolean mDefaultOn;
    boolean mIsEnabled;
    private CharSequence mLabel;
    private Checkable mOptionOff;
    private Checkable mOptionOn;
    String mPackageName;
    int mPackageUid;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 540;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mBackend = PowerAllowlistBackend.getInstance(context);
        this.mPackageName = getArguments().getString("package");
        this.mPackageUid = getArguments().getInt("uid");
        PackageManager packageManager = context.getPackageManager();
        boolean z = false;
        try {
            this.mLabel = packageManager.getApplicationInfo(this.mPackageName, 0).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            this.mLabel = this.mPackageName;
        }
        boolean z2 = getArguments().getBoolean("default_on");
        this.mDefaultOn = z2;
        this.mIsEnabled = (z2 || this.mBackend.isAllowlisted(this.mPackageName)) ? true : true;
    }

    public Checkable setup(View view, boolean z) {
        ((TextView) view.findViewById(16908310)).setText(z ? R$string.ignore_optimizations_on : R$string.ignore_optimizations_off);
        ((TextView) view.findViewById(16908304)).setText(z ? R$string.ignore_optimizations_on_desc : R$string.ignore_optimizations_off_desc);
        view.setClickable(true);
        view.setOnClickListener(this);
        if (!z && this.mBackend.isSysAllowlisted(this.mPackageName)) {
            view.setEnabled(false);
        }
        return (Checkable) view;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder view = new AlertDialog.Builder(getContext()).setTitle(this.mLabel).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).setView(R$layout.ignore_optimizations_content);
        if (!this.mBackend.isSysAllowlisted(this.mPackageName)) {
            view.setPositiveButton(R$string.done, this);
        }
        return view.create();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mOptionOn = setup(getDialog().findViewById(R$id.ignore_on), true);
        this.mOptionOff = setup(getDialog().findViewById(R$id.ignore_off), false);
        updateViews();
    }

    private void updateViews() {
        this.mOptionOn.setChecked(this.mIsEnabled);
        this.mOptionOff.setChecked(!this.mIsEnabled);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mOptionOn) {
            this.mIsEnabled = true;
            updateViews();
        } else if (view == this.mOptionOff) {
            this.mIsEnabled = false;
            updateViews();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        boolean z;
        if (i != -1 || (z = this.mIsEnabled) == this.mBackend.isAllowlisted(this.mPackageName)) {
            return;
        }
        logSpecialPermissionChange(z, this.mPackageName, getContext());
        if (z) {
            this.mBatteryUtils.setForceAppStandby(this.mPackageUid, this.mPackageName, 0);
            this.mBackend.addApp(this.mPackageName);
            return;
        }
        this.mBackend.removeApp(this.mPackageName);
    }

    static void logSpecialPermissionChange(boolean z, String str, Context context) {
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, z ? 765 : 764, str);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null || targetFragment.getActivity() == null) {
            return;
        }
        targetFragment.onActivityResult(getTargetRequestCode(), 0, null);
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        return getSummary(context, appEntry.info.packageName);
    }

    public static CharSequence getSummary(Context context, String str) {
        return getSummary(context, PowerAllowlistBackend.getInstance(context), str);
    }

    static CharSequence getSummary(Context context, PowerAllowlistBackend powerAllowlistBackend, String str) {
        int i;
        if (powerAllowlistBackend.isSysAllowlisted(str) || powerAllowlistBackend.isDefaultActiveApp(str)) {
            i = R$string.high_power_system;
        } else if (powerAllowlistBackend.isAllowlisted(str)) {
            i = R$string.high_power_on;
        } else {
            i = R$string.high_power_off;
        }
        return context.getString(i);
    }

    public static void show(Fragment fragment, int i, String str, int i2) {
        HighPowerDetail highPowerDetail = new HighPowerDetail();
        Bundle bundle = new Bundle();
        bundle.putString("package", str);
        bundle.putInt("uid", i);
        highPowerDetail.setArguments(bundle);
        highPowerDetail.setTargetFragment(fragment, i2);
        highPowerDetail.show(fragment.getFragmentManager(), HighPowerDetail.class.getSimpleName());
    }
}
