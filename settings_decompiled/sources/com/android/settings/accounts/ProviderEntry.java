package com.android.settings.accounts;

import com.android.internal.util.CharSequences;
/* loaded from: classes.dex */
public class ProviderEntry implements Comparable<ProviderEntry> {
    private final CharSequence name;
    private final String type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProviderEntry(CharSequence charSequence, String str) {
        this.name = charSequence;
        this.type = str;
    }

    @Override // java.lang.Comparable
    public int compareTo(ProviderEntry providerEntry) {
        CharSequence charSequence = this.name;
        if (charSequence == null) {
            return -1;
        }
        CharSequence charSequence2 = providerEntry.name;
        if (charSequence2 == null) {
            return 1;
        }
        return CharSequences.compareToIgnoreCase(charSequence, charSequence2);
    }

    public CharSequence getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
