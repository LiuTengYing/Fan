package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$plurals;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class ToggleAutoclickCustomSeekbarController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, SharedPreferences.OnSharedPreferenceChangeListener {
    static final int AUTOCLICK_DELAY_STEP = 100;
    private static final String CONTROL_AUTOCLICK_DELAY_SECURE = "accessibility_autoclick_delay";
    static final String KEY_CUSTOM_DELAY_VALUE = "custom_delay_value";
    static final int MAX_AUTOCLICK_DELAY_MS = 1000;
    static final int MIN_AUTOCLICK_DELAY_MS = 200;
    private final ContentResolver mContentResolver;
    private TextView mDelayLabel;
    private ImageView mLonger;
    private SeekBar mSeekBar;
    final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private final SharedPreferences mSharedPreferences;
    private ImageView mShorter;

    /* JADX INFO: Access modifiers changed from: private */
    public int seekBarProgressToDelay(int i) {
        return (i * 100) + MIN_AUTOCLICK_DELAY_MS;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ToggleAutoclickCustomSeekbarController(Context context, String str) {
        super(context, str);
        this.mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.accessibility.ToggleAutoclickCustomSeekbarController.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                ToggleAutoclickCustomSeekbarController toggleAutoclickCustomSeekbarController = ToggleAutoclickCustomSeekbarController.this;
                toggleAutoclickCustomSeekbarController.updateCustomDelayValue(toggleAutoclickCustomSeekbarController.seekBarProgressToDelay(i));
            }
        };
        this.mSharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);
        this.mContentResolver = context.getContentResolver();
    }

    public ToggleAutoclickCustomSeekbarController(Context context, Lifecycle lifecycle, String str) {
        this(context, str);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (isAvailable()) {
            int sharedPreferenceForDelayValue = getSharedPreferenceForDelayValue();
            SeekBar seekBar = (SeekBar) layoutPreference.findViewById(R$id.autoclick_delay);
            this.mSeekBar = seekBar;
            seekBar.setMax(delayToSeekBarProgress(1000));
            this.mSeekBar.setProgress(delayToSeekBarProgress(sharedPreferenceForDelayValue));
            this.mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
            TextView textView = (TextView) layoutPreference.findViewById(R$id.current_label);
            this.mDelayLabel = textView;
            textView.setText(delayTimeToString(sharedPreferenceForDelayValue));
            ImageView imageView = (ImageView) layoutPreference.findViewById(R$id.shorter);
            this.mShorter = imageView;
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAutoclickCustomSeekbarController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ToggleAutoclickCustomSeekbarController.this.lambda$displayPreference$0(view);
                }
            });
            ImageView imageView2 = (ImageView) layoutPreference.findViewById(R$id.longer);
            this.mLonger = imageView2;
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAutoclickCustomSeekbarController$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ToggleAutoclickCustomSeekbarController.this.lambda$displayPreference$1(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        minusDelayByImageView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(View view) {
        plusDelayByImageView();
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if ("delay_mode".equals(str)) {
            updateCustomDelayValue(getSharedPreferenceForDelayValue());
        }
    }

    private int delayToSeekBarProgress(int i) {
        return (i - 200) / 100;
    }

    private int getSharedPreferenceForDelayValue() {
        return this.mSharedPreferences.getInt(KEY_CUSTOM_DELAY_VALUE, Settings.Secure.getInt(this.mContentResolver, CONTROL_AUTOCLICK_DELAY_SECURE, 600));
    }

    private void putSecureInt(String str, int i) {
        Settings.Secure.putInt(this.mContentResolver, str, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCustomDelayValue(int i) {
        putSecureInt(CONTROL_AUTOCLICK_DELAY_SECURE, i);
        this.mSharedPreferences.edit().putInt(KEY_CUSTOM_DELAY_VALUE, i).apply();
        this.mSeekBar.setProgress(delayToSeekBarProgress(i));
        this.mDelayLabel.setText(delayTimeToString(i));
    }

    private void minusDelayByImageView() {
        int sharedPreferenceForDelayValue = getSharedPreferenceForDelayValue();
        if (sharedPreferenceForDelayValue > MIN_AUTOCLICK_DELAY_MS) {
            updateCustomDelayValue(sharedPreferenceForDelayValue - 100);
        }
    }

    private void plusDelayByImageView() {
        int sharedPreferenceForDelayValue = getSharedPreferenceForDelayValue();
        if (sharedPreferenceForDelayValue < 1000) {
            updateCustomDelayValue(sharedPreferenceForDelayValue + 100);
        }
    }

    private CharSequence delayTimeToString(int i) {
        int i2 = i == 1000 ? 1 : 3;
        float f = i / 1000.0f;
        return this.mContext.getResources().getQuantityString(R$plurals.accessibilty_autoclick_delay_unit_second, i2, String.format(f == 1.0f ? "%.0f" : "%.1f", Float.valueOf(f)));
    }
}
