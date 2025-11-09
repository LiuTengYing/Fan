package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import androidx.core.content.res.TypedArrayUtils;
import com.android.settings.R$attr;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$styleable;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class TwoStateButtonPreference extends LayoutPreference implements View.OnClickListener {
    private final Button mButtonOff;
    private final Button mButtonOn;
    private boolean mIsChecked;

    public TwoStateButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.twoStateButtonPreferenceStyle, 16842894));
        if (attributeSet == null) {
            this.mButtonOn = null;
            this.mButtonOff = null;
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.TwoStateButtonPreference);
        int i = R$styleable.TwoStateButtonPreference_textOn;
        int i2 = R$string.summary_placeholder;
        int resourceId = obtainStyledAttributes.getResourceId(i, i2);
        int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.TwoStateButtonPreference_textOff, i2);
        obtainStyledAttributes.recycle();
        Button button = (Button) findViewById(R$id.state_on_button);
        this.mButtonOn = button;
        button.setText(resourceId);
        button.setOnClickListener(this);
        Button button2 = (Button) findViewById(R$id.state_off_button);
        this.mButtonOff = button2;
        button2.setText(resourceId2);
        button2.setOnClickListener(this);
        setChecked(isChecked());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        boolean z = view.getId() == R$id.state_on_button;
        setChecked(z);
        callChangeListener(Boolean.valueOf(z));
    }

    public void setChecked(boolean z) {
        this.mIsChecked = z;
        if (z) {
            this.mButtonOn.setVisibility(8);
            this.mButtonOff.setVisibility(0);
            return;
        }
        this.mButtonOn.setVisibility(0);
        this.mButtonOff.setVisibility(8);
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public Button getStateOnButton() {
        return this.mButtonOn;
    }

    public Button getStateOffButton() {
        return this.mButtonOff;
    }
}
