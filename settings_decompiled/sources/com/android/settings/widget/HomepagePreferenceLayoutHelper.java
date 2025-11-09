package com.android.settings.widget;

import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.SettingsApplication;
/* loaded from: classes.dex */
public class HomepagePreferenceLayoutHelper {
    private View mIcon;
    private View mText;
    private boolean mIconVisible = true;
    private int mIconPaddingStart = -1;
    private int mTextPaddingStart = -1;

    /* loaded from: classes.dex */
    public interface HomepagePreferenceLayout {
        HomepagePreferenceLayoutHelper getHelper();
    }

    public HomepagePreferenceLayoutHelper(Preference preference) {
        SettingsApplication settingsApplication = SettingsApplication.mApplication;
        int i = SettingsApplication.mWidthFix;
        SettingsApplication settingsApplication2 = SettingsApplication.mApplication;
        if (i > SettingsApplication.mHeightFix) {
            preference.setLayoutResource(R$layout.homepage_preference);
        } else {
            preference.setLayoutResource(R$layout.homepage_preference_v);
        }
    }

    public void setIconVisible(boolean z) {
        this.mIconVisible = z;
        View view = this.mIcon;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    public void setIconPaddingStart(int i) {
        this.mIconPaddingStart = i;
        View view = this.mIcon;
        if (view == null || i < 0) {
            return;
        }
        view.setPaddingRelative(i, view.getPaddingTop(), this.mIcon.getPaddingEnd(), this.mIcon.getPaddingBottom());
    }

    public void setTextPaddingStart(int i) {
        this.mTextPaddingStart = i;
        View view = this.mText;
        if (view == null || i < 0) {
            return;
        }
        view.setPaddingRelative(i, view.getPaddingTop(), this.mText.getPaddingEnd(), this.mText.getPaddingBottom());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mIcon = preferenceViewHolder.findViewById(R$id.icon_frame);
        this.mText = preferenceViewHolder.findViewById(R$id.text_frame);
        setIconVisible(this.mIconVisible);
        setIconPaddingStart(this.mIconPaddingStart);
        setTextPaddingStart(this.mTextPaddingStart);
    }
}
