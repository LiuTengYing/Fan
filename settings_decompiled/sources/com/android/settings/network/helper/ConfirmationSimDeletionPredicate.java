package com.android.settings.network.helper;

import android.app.KeyguardManager;
import android.content.Context;
import android.provider.Settings;
import com.android.settings.R$bool;
import com.android.settings.security.ConfirmSimDeletionPreferenceController;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class ConfirmationSimDeletionPredicate implements Predicate<Context> {
    private static final ConfirmationSimDeletionPredicate sSingleton = new ConfirmationSimDeletionPredicate();

    public static final ConfirmationSimDeletionPredicate getSingleton() {
        return sSingleton;
    }

    private static boolean getDefaultValue(Context context) {
        return context.getResources().getBoolean(R$bool.config_sim_deletion_confirmation_default_on);
    }

    @Override // java.util.function.Predicate
    public boolean test(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        return (keyguardManager == null || keyguardManager.isKeyguardSecure()) && Settings.Global.getInt(context.getContentResolver(), ConfirmSimDeletionPreferenceController.KEY_CONFIRM_SIM_DELETION, getDefaultValue(context) ? 1 : 0) == 1;
    }
}
