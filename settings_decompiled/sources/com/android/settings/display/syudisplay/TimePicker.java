package com.android.settings.display.syudisplay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import com.android.settings.R$color;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import java.lang.reflect.Field;
import java.text.DateFormatSymbols;
import java.util.Calendar;
/* loaded from: classes.dex */
public class TimePicker extends FrameLayout {
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: com.android.settings.display.syudisplay.TimePicker.1
        @Override // com.android.settings.display.syudisplay.TimePicker.OnTimeChangedListener
        public void onTimeChanged(TimePicker timePicker, int i, int i2, int i3) {
        }
    };
    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER = new NumberPicker.Formatter() { // from class: com.android.settings.display.syudisplay.TimePicker.2
        @Override // android.widget.NumberPicker.Formatter
        public String format(int i) {
            return String.format("%02d", Integer.valueOf(i));
        }
    };
    private int mCurrentHour;
    private int mCurrentMinute;
    private int mCurrentSeconds;
    private final NumberPicker mHourPicker;
    private Boolean mIs24HourView;
    private boolean mIsAm;
    private final NumberPicker mMinutePicker;
    private OnTimeChangedListener mOnTimeChangedListener;
    private final NumberPicker mSecondPicker;

    /* loaded from: classes.dex */
    public interface OnTimeChangedListener {
        void onTimeChanged(TimePicker timePicker, int i, int i2, int i3);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TimePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentHour = 0;
        this.mCurrentMinute = 0;
        this.mCurrentSeconds = 0;
        this.mIs24HourView = Boolean.FALSE;
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.time_picker_widget, (ViewGroup) this, true);
        NumberPicker numberPicker = (NumberPicker) findViewById(R$id.hour);
        this.mHourPicker = numberPicker;
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.settings.display.syudisplay.TimePicker.3
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                TimePicker.this.mCurrentHour = i3;
                TimePicker.this.onTimeChanged();
            }
        });
        NumberPicker numberPicker2 = (NumberPicker) findViewById(R$id.minute);
        this.mMinutePicker = numberPicker2;
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        NumberPicker.Formatter formatter = TWO_DIGIT_FORMATTER;
        numberPicker2.setFormatter(formatter);
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.settings.display.syudisplay.TimePicker.4
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                TimePicker.this.mCurrentMinute = i3;
                TimePicker.this.onTimeChanged();
            }
        });
        NumberPicker numberPicker3 = (NumberPicker) findViewById(R$id.seconds);
        this.mSecondPicker = numberPicker3;
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setVisibility(8);
        numberPicker3.setFormatter(formatter);
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.settings.display.syudisplay.TimePicker.5
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                TimePicker.this.mCurrentSeconds = i3;
                TimePicker.this.onTimeChanged();
            }
        });
        configurePickerRanges();
        Calendar calendar = Calendar.getInstance();
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
        setCurrentHour(Integer.valueOf(calendar.get(10)));
        setCurrentMinute(Integer.valueOf(calendar.get(12)));
        setCurrentSecond(Integer.valueOf(calendar.get(13)));
        this.mIsAm = this.mCurrentHour < 12;
        new DateFormatSymbols().getAmPmStrings();
        if (isEnabled()) {
            return;
        }
        setEnabled(false);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mMinutePicker.setEnabled(z);
        this.mHourPicker.setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.settings.display.syudisplay.TimePicker.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private final int mHour;
        private final int mMinute;

        private SavedState(Parcelable parcelable, int i, int i2) {
            super(parcelable);
            this.mHour = i;
            this.mMinute = i2;
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mHour = parcel.readInt();
            this.mMinute = parcel.readInt();
        }

        public int getHour() {
            return this.mHour;
        }

        public int getMinute() {
            return this.mMinute;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mHour);
            parcel.writeInt(this.mMinute);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.mCurrentHour, this.mCurrentMinute);
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setCurrentHour(Integer.valueOf(savedState.getHour()));
        setCurrentMinute(Integer.valueOf(savedState.getMinute()));
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mOnTimeChangedListener = onTimeChangedListener;
    }

    public Integer getCurrentHour() {
        return Integer.valueOf(this.mCurrentHour);
    }

    public void setCurrentHour(Integer num) {
        this.mCurrentHour = num.intValue();
        updateHourDisplay();
        setNumberPickerDividerColor(this.mHourPicker);
    }

    public void setIs24HourView(Boolean bool) {
        if (this.mIs24HourView != bool) {
            this.mIs24HourView = bool;
            configurePickerRanges();
            updateHourDisplay();
        }
    }

    public Integer getCurrentMinute() {
        return Integer.valueOf(this.mCurrentMinute);
    }

    public void setCurrentMinute(Integer num) {
        this.mCurrentMinute = num.intValue();
        updateMinuteDisplay();
        setNumberPickerDividerColor(this.mMinutePicker);
    }

    public Integer getCurrentSeconds() {
        return Integer.valueOf(this.mCurrentSeconds);
    }

    public void setHour(Integer num) {
        this.mCurrentHour = num.intValue();
        setHourDisplay();
        setNumberPickerDividerColor(this.mHourPicker);
    }

    private void setHourDisplay() {
        int i = this.mCurrentHour;
        if (!this.mIs24HourView.booleanValue()) {
            if (i > 12) {
                i -= 12;
            } else if (i == 0) {
                i = 12;
            }
        }
        this.mHourPicker.setValue(i);
        this.mIsAm = this.mCurrentHour < 12;
    }

    public void setMinute(Integer num) {
        this.mCurrentMinute = num.intValue();
        setMinuteDisplay();
        setNumberPickerDividerColor(this.mMinutePicker);
    }

    private void setMinuteDisplay() {
        this.mMinutePicker.setValue(this.mCurrentMinute);
    }

    public void setCurrentSecond(Integer num) {
        this.mCurrentSeconds = num.intValue();
        updateSecondsDisplay();
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mHourPicker.getBaseline();
    }

    private void updateHourDisplay() {
        int i = this.mCurrentHour;
        if (!this.mIs24HourView.booleanValue()) {
            if (i > 12) {
                i -= 12;
            } else if (i == 0) {
                i = 12;
            }
        }
        this.mHourPicker.setValue(i);
        this.mIsAm = this.mCurrentHour < 12;
        onTimeChanged();
    }

    private void configurePickerRanges() {
        if (this.mIs24HourView.booleanValue()) {
            this.mHourPicker.setMinValue(0);
            this.mHourPicker.setMaxValue(23);
            this.mHourPicker.setFormatter(TWO_DIGIT_FORMATTER);
            return;
        }
        this.mHourPicker.setMinValue(1);
        this.mHourPicker.setMaxValue(12);
        this.mHourPicker.setFormatter(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        this.mOnTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue(), getCurrentSeconds().intValue());
    }

    private void updateMinuteDisplay() {
        this.mMinutePicker.setValue(this.mCurrentMinute);
        this.mOnTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue(), getCurrentSeconds().intValue());
    }

    private void updateSecondsDisplay() {
        this.mSecondPicker.setValue(this.mCurrentSeconds);
        this.mOnTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue(), getCurrentSeconds().intValue());
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        Field[] declaredFields;
        for (Field field : NumberPicker.class.getDeclaredFields()) {
            if (field.getName().equals("mSelectionDivider")) {
                field.setAccessible(true);
                try {
                    field.set(numberPicker, new ColorDrawable(getResources().getColor(R$color.translucent)));
                    return;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                    return;
                } catch (IllegalArgumentException e3) {
                    e3.printStackTrace();
                    return;
                }
            }
        }
    }
}
