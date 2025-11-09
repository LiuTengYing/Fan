package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.accessibility.AccessibilityServiceWarning;
import java.util.Locale;
/* loaded from: classes.dex */
public class AccessibilityServiceWarning {
    private static final View.OnTouchListener filterTouchListener = new View.OnTouchListener() { // from class: com.android.settings.accessibility.AccessibilityServiceWarning$$ExternalSyntheticLambda1
        @Override // android.view.View.OnTouchListener
        public final boolean onTouch(View view, MotionEvent motionEvent) {
            boolean lambda$static$0;
            lambda$static$0 = AccessibilityServiceWarning.lambda$static$0(view, motionEvent);
            return lambda$static$0;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface UninstallActionPerformer {
        void uninstallPackage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$static$0(View view, MotionEvent motionEvent) {
        if ((motionEvent.getFlags() & 1) == 0 && (motionEvent.getFlags() & 2) == 0) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            Toast.makeText(view.getContext(), R$string.touch_filtered_warning, 0).show();
        }
        return true;
    }

    public static Dialog createCapabilitiesDialog(Context context, AccessibilityServiceInfo accessibilityServiceInfo, View.OnClickListener onClickListener, UninstallActionPerformer uninstallActionPerformer) {
        AlertDialog create = new AlertDialog.Builder(context).setView(createEnableDialogContentView(context, accessibilityServiceInfo, onClickListener, uninstallActionPerformer)).create();
        Window window = create.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.privateFlags |= 524288;
        window.setAttributes(attributes);
        create.create();
        create.setCanceledOnTouchOutside(true);
        return create;
    }

    private static View createEnableDialogContentView(Context context, AccessibilityServiceInfo accessibilityServiceInfo, View.OnClickListener onClickListener, final UninstallActionPerformer uninstallActionPerformer) {
        Drawable loadIcon;
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.enable_accessibility_service_dialog_content, (ViewGroup) null);
        if (accessibilityServiceInfo.getResolveInfo().getIconResource() == 0) {
            loadIcon = ContextCompat.getDrawable(context, R$drawable.ic_accessibility_generic);
        } else {
            loadIcon = accessibilityServiceInfo.getResolveInfo().loadIcon(context.getPackageManager());
        }
        ((ImageView) inflate.findViewById(R$id.permissionDialog_icon)).setImageDrawable(loadIcon);
        ((TextView) inflate.findViewById(R$id.permissionDialog_title)).setText(context.getString(R$string.enable_service_title, getServiceName(context, accessibilityServiceInfo)));
        Button button = (Button) inflate.findViewById(R$id.permission_enable_allow_button);
        button.setOnClickListener(onClickListener);
        button.setOnTouchListener(filterTouchListener);
        ((Button) inflate.findViewById(R$id.permission_enable_deny_button)).setOnClickListener(onClickListener);
        Button button2 = (Button) inflate.findViewById(R$id.permission_enable_uninstall_button);
        if (!AccessibilityUtil.isSystemApp(accessibilityServiceInfo)) {
            button2.setVisibility(0);
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityServiceWarning$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AccessibilityServiceWarning.UninstallActionPerformer.this.uninstallPackage();
                }
            });
        }
        return inflate;
    }

    public static Dialog createDisableDialog(Context context, AccessibilityServiceInfo accessibilityServiceInfo, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder title = new AlertDialog.Builder(context).setTitle(context.getString(R$string.disable_service_title, accessibilityServiceInfo.getResolveInfo().loadLabel(context.getPackageManager())));
        int i = R$string.disable_service_message;
        int i2 = R$string.accessibility_dialog_button_stop;
        return title.setMessage(context.getString(i, context.getString(i2), getServiceName(context, accessibilityServiceInfo))).setCancelable(true).setPositiveButton(i2, onClickListener).setNegativeButton(R$string.accessibility_dialog_button_cancel, onClickListener).create();
    }

    private static CharSequence getServiceName(Context context, AccessibilityServiceInfo accessibilityServiceInfo) {
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        return BidiFormatter.getInstance(locale).unicodeWrap(accessibilityServiceInfo.getResolveInfo().loadLabel(context.getPackageManager()));
    }
}
