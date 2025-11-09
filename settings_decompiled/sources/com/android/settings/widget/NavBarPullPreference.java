package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class NavBarPullPreference extends Preference {
    public NavBarPullPreference(Context context) {
        this(context, null);
    }

    public NavBarPullPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R$layout.navigation_bar_pull_notification);
    }
}
