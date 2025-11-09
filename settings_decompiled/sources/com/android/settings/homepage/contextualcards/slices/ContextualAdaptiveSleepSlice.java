package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.display.AdaptiveSleepPreferenceController;
import com.android.settings.display.ScreenTimeoutPreferenceController;
import com.android.settings.display.ScreenTimeoutSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class ContextualAdaptiveSleepSlice implements CustomSliceable {
    static final long DEFERRED_TIME_DAYS = TimeUnit.DAYS.toMillis(14);
    static final String PREF_KEY_SETUP_TIME = "adaptive_sleep_setup_time";
    private Context mContext;

    public ContextualAdaptiveSleepSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        if (this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).getLong(PREF_KEY_SETUP_TIME, DEFERRED_TIME_DAYS) == DEFERRED_TIME_DAYS) {
            this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).edit().putLong(PREF_KEY_SETUP_TIME, System.currentTimeMillis()).apply();
            return null;
        } else if (!isSettingsAvailable() || isUserInteracted() || isRecentlySetup() || isTurnedOn()) {
            return null;
        } else {
            IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.ic_settings_adaptive_sleep);
            CharSequence text = this.mContext.getText(R$string.adaptive_sleep_contextual_slice_title);
            CharSequence text2 = this.mContext.getText(R$string.adaptive_sleep_contextual_slice_summary);
            return new ListBuilder(this.mContext, CustomSliceRegistry.CONTEXTUAL_ADAPTIVE_SLEEP_URI, -1L).setAccentColor(-1).addRow(new ListBuilder.RowBuilder().setTitleItem(createWithResource, 0).setTitle(text).setSubtitle(text2).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, text))).build();
        }
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.CONTEXTUAL_ADAPTIVE_SLEEP_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        CharSequence text = this.mContext.getText(R$string.adaptive_sleep_title);
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, ScreenTimeoutSettings.class.getName(), ScreenTimeoutPreferenceController.PREF_NAME, text.toString(), 1401, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath(ScreenTimeoutPreferenceController.PREF_NAME).build());
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864);
    }

    private boolean isTurnedOn() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "adaptive_sleep", 0) != 0;
    }

    private boolean isUserInteracted() {
        return this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).getBoolean("adaptive_sleep_interacted", false);
    }

    private boolean isRecentlySetup() {
        return this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).getLong(PREF_KEY_SETUP_TIME, DEFERRED_TIME_DAYS) > System.currentTimeMillis() - DEFERRED_TIME_DAYS;
    }

    boolean isSettingsAvailable() {
        return AdaptiveSleepPreferenceController.isControllerAvailable(this.mContext) == 1;
    }
}
