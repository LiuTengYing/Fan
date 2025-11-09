package com.google.android.setupcompat.logging.internal;

import android.annotation.TargetApi;
import android.os.PersistableBundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public class FooterBarMixinMetrics {
    public static final String EXTRA_PRIMARY_BUTTON_VISIBILITY = "PrimaryButtonVisibility";
    public static final String EXTRA_SECONDARY_BUTTON_VISIBILITY = "SecondaryButtonVisibility";
    String primaryButtonVisibility = "Unknown";
    String secondaryButtonVisibility = "Unknown";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface FooterButtonVisibility {
    }

    public String getInitialStateVisibility(boolean z, boolean z2) {
        return z ? z2 ? "VisibleUsingXml" : "Visible" : "Invisible";
    }

    public void logPrimaryButtonInitialStateVisibility(boolean z, boolean z2) {
        String str;
        if (this.primaryButtonVisibility.equals("Unknown")) {
            str = getInitialStateVisibility(z, z2);
        } else {
            str = this.primaryButtonVisibility;
        }
        this.primaryButtonVisibility = str;
    }

    public void logSecondaryButtonInitialStateVisibility(boolean z, boolean z2) {
        String str;
        if (this.secondaryButtonVisibility.equals("Unknown")) {
            str = getInitialStateVisibility(z, z2);
        } else {
            str = this.secondaryButtonVisibility;
        }
        this.secondaryButtonVisibility = str;
    }

    public void updateButtonVisibility(boolean z, boolean z2) {
        this.primaryButtonVisibility = updateButtonVisibilityState(this.primaryButtonVisibility, z);
        this.secondaryButtonVisibility = updateButtonVisibilityState(this.secondaryButtonVisibility, z2);
    }

    static String updateButtonVisibilityState(String str, boolean z) {
        if ("VisibleUsingXml".equals(str) || "Visible".equals(str) || "Invisible".equals(str)) {
            return (z && "Invisible".equals(str)) ? "Invisible_to_Visible" : !z ? "VisibleUsingXml".equals(str) ? "VisibleUsingXml_to_Invisible" : "Visible".equals(str) ? "Visible_to_Invisible" : str : str;
        }
        throw new IllegalStateException("Illegal visibility state: " + str);
    }

    @TargetApi(29)
    public PersistableBundle getMetrics() {
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString(EXTRA_PRIMARY_BUTTON_VISIBILITY, this.primaryButtonVisibility);
        persistableBundle.putString(EXTRA_SECONDARY_BUTTON_VISIBILITY, this.secondaryButtonVisibility);
        return persistableBundle;
    }
}
