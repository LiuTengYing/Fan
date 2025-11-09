package com.android.settings.development;

import android.content.Context;
/* loaded from: classes.dex */
public interface AdbWirelessDialogUiBase {
    Context getContext();

    void setCancelButton(CharSequence charSequence);

    void setCanceledOnTouchOutside(boolean z);

    void setSubmitButton(CharSequence charSequence);

    void setTitle(int i);

    void setTitle(CharSequence charSequence);
}
