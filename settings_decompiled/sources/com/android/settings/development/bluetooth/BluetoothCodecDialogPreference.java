package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.R$array;
import com.android.settings.R$id;
/* loaded from: classes.dex */
public class BluetoothCodecDialogPreference extends BaseBluetoothDialogPreference {
    public BluetoothCodecDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothCodecDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothCodecDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothCodecDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    protected int getRadioButtonGroupId() {
        return R$id.bluetooth_audio_codec_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_codec_default));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_codec_sbc));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_codec_aac));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_codec_aptx));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_codec_aptx_hd));
        this.mRadioButtonIds.add(Integer.valueOf(R$id.bluetooth_audio_codec_ldac));
        for (String str : context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_titles)) {
            this.mRadioButtonStrings.add(str);
        }
        for (String str2 : context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_summaries)) {
            this.mSummaryStrings.add(str2);
        }
    }
}
