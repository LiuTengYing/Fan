package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.R$array;
import com.android.settings.R$id;
/* loaded from: classes.dex */
public class BluetoothQualityDialogPreference extends BaseBluetoothDialogPreference {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    public int getDefaultIndex() {
        return 3;
    }

    public BluetoothQualityDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothQualityDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothQualityDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothQualityDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    protected int getRadioButtonGroupId() {
        return R$id.bluetooth_audio_quality_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_quality_default));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_quality_optimized_quality));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_quality_optimized_connection));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_quality_best_effort));
        for (String str : context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_ldac_playback_quality_titles)) {
            this.mRadioButtonStrings.add(str);
        }
        for (String str2 : context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_ldac_playback_quality_summaries)) {
            this.mSummaryStrings.add(str2);
        }
    }
}
