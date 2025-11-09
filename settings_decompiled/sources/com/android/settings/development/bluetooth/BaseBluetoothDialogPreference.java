package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settingslib.CustomDialogPreferenceCompat;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class BaseBluetoothDialogPreference extends CustomDialogPreferenceCompat implements RadioGroup.OnCheckedChangeListener {
    private Callback mCallback;
    protected List<Integer> mRadioButtonIds;
    protected List<String> mRadioButtonStrings;
    protected List<String> mSummaryStrings;

    /* loaded from: classes.dex */
    public interface Callback {
        int getCurrentConfigIndex();

        List<Integer> getSelectableIndex();

        void onIndexUpdated(int i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getDefaultIndex() {
        return 0;
    }

    protected abstract int getRadioButtonGroupId();

    public BaseBluetoothDialogPreference(Context context) {
        super(context);
        this.mRadioButtonIds = new ArrayList();
        this.mRadioButtonStrings = new ArrayList();
        this.mSummaryStrings = new ArrayList();
    }

    public BaseBluetoothDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRadioButtonIds = new ArrayList();
        this.mRadioButtonStrings = new ArrayList();
        this.mSummaryStrings = new ArrayList();
    }

    public BaseBluetoothDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mRadioButtonIds = new ArrayList();
        this.mRadioButtonStrings = new ArrayList();
        this.mSummaryStrings = new ArrayList();
    }

    public BaseBluetoothDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mRadioButtonIds = new ArrayList();
        this.mRadioButtonStrings = new ArrayList();
        this.mSummaryStrings = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        if (this.mCallback == null) {
            Log.e("BaseBluetoothDlgPref", "Unable to show dialog by the callback is null");
        } else if (this.mRadioButtonStrings.size() != this.mRadioButtonIds.size()) {
            Log.e("BaseBluetoothDlgPref", "Unable to show dialog by the view and string size are not matched");
        } else {
            int currentConfigIndex = this.mCallback.getCurrentConfigIndex();
            if (currentConfigIndex < 0 || currentConfigIndex >= this.mRadioButtonIds.size()) {
                Log.e("BaseBluetoothDlgPref", "Unable to show dialog by the incorrect index: " + currentConfigIndex);
                return;
            }
            RadioGroup radioGroup = (RadioGroup) view.findViewById(getRadioButtonGroupId());
            if (radioGroup == null) {
                Log.e("BaseBluetoothDlgPref", "Unable to show dialog by no radio button group: " + getRadioButtonGroupId());
                return;
            }
            radioGroup.check(this.mRadioButtonIds.get(currentConfigIndex).intValue());
            radioGroup.setOnCheckedChangeListener(this);
            List<Integer> selectableIndex = this.mCallback.getSelectableIndex();
            for (int i = 0; i < this.mRadioButtonStrings.size(); i++) {
                RadioButton radioButton = (RadioButton) view.findViewById(this.mRadioButtonIds.get(i).intValue());
                if (radioButton == null) {
                    Log.e("BaseBluetoothDlgPref", "Unable to show dialog by no radio button:" + this.mRadioButtonIds.get(i));
                    return;
                }
                radioButton.setText(this.mRadioButtonStrings.get(i));
                radioButton.setEnabled(selectableIndex.contains(Integer.valueOf(i)));
            }
            TextView textView = (TextView) view.findViewById(R$id.bluetooth_audio_codec_help_info);
            if (selectableIndex.size() == this.mRadioButtonIds.size()) {
                textView.setVisibility(8);
                return;
            }
            textView.setText(R$string.bluetooth_select_a2dp_codec_type_help_info);
            textView.setVisibility(0);
        }
    }

    @Override // android.widget.RadioGroup.OnCheckedChangeListener
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        Callback callback = this.mCallback;
        if (callback == null) {
            Log.e("BaseBluetoothDlgPref", "Callback is null");
            return;
        }
        callback.onIndexUpdated(this.mRadioButtonIds.indexOf(Integer.valueOf(i)));
        getDialog().dismiss();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String generateSummary(int i) {
        if (i <= this.mSummaryStrings.size()) {
            return i == getDefaultIndex() ? this.mSummaryStrings.get(getDefaultIndex()) : String.format(getContext().getResources().getString(R$string.bluetooth_select_a2dp_codec_streaming_label), this.mSummaryStrings.get(i));
        }
        Log.e("BaseBluetoothDlgPref", "Unable to get summary of " + i + ". Size is " + this.mSummaryStrings.size());
        return null;
    }
}
