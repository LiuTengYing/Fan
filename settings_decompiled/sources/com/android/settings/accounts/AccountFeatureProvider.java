package com.android.settings.accounts;

import android.accounts.Account;
import android.content.Context;
/* loaded from: classes.dex */
public interface AccountFeatureProvider {
    String getAccountType();

    Account[] getAccounts(Context context);
}
