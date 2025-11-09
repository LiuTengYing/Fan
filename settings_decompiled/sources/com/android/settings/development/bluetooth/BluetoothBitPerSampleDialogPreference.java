package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.R$array;
import com.android.settings.R$id;
/* loaded from: classes.dex */
public class BluetoothBitPerSampleDialogPreference extends BaseBluetoothDialogPreference {
    public BluetoothBitPerSampleDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothBitPerSampleDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothBitPerSampleDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothBitPerSampleDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    protected int getRadioButtonGroupId() {
        return R$id.bluetooth_audio_bit_per_sample_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_bit_per_sample_default));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_bit_per_sample_16));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_bit_per_sample_24));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_bit_per_sample_32));
        for (String str : context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_bits_per_sample_titles)) {
            this.mRadioButtonStrings.add(str);
        }
        for (String str2 : context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_bits_per_sample_summaries)) {
            this.mSummaryStrings.add(str2);
        }
    }
}
