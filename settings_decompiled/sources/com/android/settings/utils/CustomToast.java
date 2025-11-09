package com.android.settings.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class CustomToast {
    public static void showCustomToast(Context context, String str) {
        View inflate = LayoutInflater.from(context).inflate(R$layout.custom_toast, (ViewGroup) null);
        ((TextView) inflate.findViewById(R$id.toast_content)).setText(str);
        Toast toast = new Toast(context);
        toast.setDuration(0);
        toast.setGravity(80, 0, 100);
        toast.setView(inflate);
        toast.show();
    }
}
