package com.android.settings.security;

import android.content.Context;
/* loaded from: classes.dex */
public class InstallCertificatePreferenceController extends RestrictedEncryptionPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "install_certificate";
    }

    public InstallCertificatePreferenceController(Context context) {
        super(context, "no_config_credentials");
    }
}
