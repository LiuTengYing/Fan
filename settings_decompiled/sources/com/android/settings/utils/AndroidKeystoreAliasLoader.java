package com.android.settings.utils;

import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import android.util.Log;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
/* loaded from: classes.dex */
public class AndroidKeystoreAliasLoader {
    private final Collection<String> mKeyCertAliases = new ArrayList();
    private final Collection<String> mCaCertAliases = new ArrayList();

    public AndroidKeystoreAliasLoader(Integer num) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            if (num != null && num.intValue() != -1) {
                keyStore.load(new AndroidKeyStoreLoadStoreParameter(num.intValue()));
            } else {
                keyStore.load(null);
            }
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String nextElement = aliases.nextElement();
                try {
                    Key key = keyStore.getKey(nextElement, null);
                    if (key != null) {
                        if (key instanceof PrivateKey) {
                            this.mKeyCertAliases.add(nextElement);
                            Certificate[] certificateChain = keyStore.getCertificateChain(nextElement);
                            if (certificateChain != null && certificateChain.length >= 2) {
                                this.mCaCertAliases.add(nextElement);
                            }
                        }
                    } else if (keyStore.getCertificate(nextElement) != null) {
                        this.mCaCertAliases.add(nextElement);
                    }
                } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                    Log.e("SettingsKeystoreUtils", "Failed to load alias: " + nextElement + " from Android Keystore. Ignoring.", e);
                }
            }
        } catch (Exception e2) {
            Log.e("SettingsKeystoreUtils", "Failed to open Android Keystore.", e2);
        }
    }

    public Collection<String> getKeyCertAliases() {
        return this.mKeyCertAliases;
    }

    public Collection<String> getCaCertAliases() {
        return this.mCaCertAliases;
    }
}
