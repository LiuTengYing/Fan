package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class ScaryWarningDialogFragment extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 557;
    }

    public ScaryWarningDialogFragment setServiceInfo(ComponentName componentName, CharSequence charSequence, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("c", componentName.flattenToString());
        bundle.putCharSequence("l", charSequence);
        setArguments(bundle);
        setTargetFragment(fragment, 0);
        return this;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        CharSequence charSequence = arguments.getCharSequence("l");
        ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("c"));
        return new AlertDialog.Builder(getContext()).setView(getDialogView(getContext(), charSequence, (NotificationAccessDetails) getTargetFragment(), unflattenFromString)).setCancelable(true).create();
    }

    private View getDialogView(Context context, CharSequence charSequence, final NotificationAccessDetails notificationAccessDetails, final ComponentName componentName) {
        Drawable drawable = null;
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.enable_nls_dialog_content, (ViewGroup) null);
        try {
            drawable = context.getPackageManager().getApplicationIcon(componentName.getPackageName());
        } catch (PackageManager.NameNotFoundException unused) {
        }
        ImageView imageView = (ImageView) inflate.findViewById(R$id.app_icon);
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setVisibility(8);
        }
        ((TextView) inflate.findViewById(R$id.title)).setText(context.getResources().getString(R$string.notification_listener_security_warning_title, charSequence));
        ((TextView) inflate.findViewById(R$id.prompt)).setText(context.getResources().getString(R$string.nls_warning_prompt, charSequence));
        ((Button) inflate.findViewById(R$id.allow_button)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.ScaryWarningDialogFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ScaryWarningDialogFragment.this.lambda$getDialogView$0(notificationAccessDetails, componentName, view);
            }
        });
        ((Button) inflate.findViewById(R$id.deny_button)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.ScaryWarningDialogFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ScaryWarningDialogFragment.this.lambda$getDialogView$1(view);
            }
        });
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogView$0(NotificationAccessDetails notificationAccessDetails, ComponentName componentName, View view) {
        notificationAccessDetails.enable(componentName);
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogView$1(View view) {
        dismiss();
    }
}
