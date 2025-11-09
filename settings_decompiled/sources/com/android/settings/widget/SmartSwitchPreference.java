package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class SmartSwitchPreference extends SwitchPreference {
    private final Listener mListener;
    private OnPreferenceSwitchChangeListener mOnSwitchChangeListener;
    private OnViewClickedListener mOnViewClickedListener;

    /* loaded from: classes.dex */
    public interface OnPreferenceSwitchChangeListener {
        void onPreferenceSwitchChanged(boolean z);
    }

    /* loaded from: classes.dex */
    public interface OnViewClickedListener {
        void OnViewClicked(View view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Listener implements CompoundButton.OnCheckedChangeListener {
        private Listener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (!SmartSwitchPreference.this.callChangeListener(Boolean.valueOf(z))) {
                compoundButton.setChecked(!z);
                return;
            }
            SmartSwitchPreference.this.mOnSwitchChangeListener.onPreferenceSwitchChanged(z);
            SmartSwitchPreference.this.setChecked(z);
        }
    }

    public void setOnViewClickedListener(OnViewClickedListener onViewClickedListener) {
        this.mOnViewClickedListener = onViewClickedListener;
    }

    public void setOnPreferenceSwitchCheckedListener(OnPreferenceSwitchChangeListener onPreferenceSwitchChangeListener) {
        this.mOnSwitchChangeListener = onPreferenceSwitchChangeListener;
    }

    public SmartSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mListener = new Listener();
        setWidgetLayoutResource(R$layout.smart_switch_preference);
    }

    public SmartSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListener = new Listener();
        setWidgetLayoutResource(R$layout.smart_switch_preference);
    }

    public SmartSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListener = new Listener();
        setWidgetLayoutResource(R$layout.smart_switch_preference);
    }

    public SmartSwitchPreference(Context context) {
        super(context);
        this.mListener = new Listener();
        setWidgetLayoutResource(R$layout.smart_switch_preference);
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.SmartSwitchPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SmartSwitchPreference.this.mOnViewClickedListener.OnViewClicked(view);
            }
        });
        syncSwitchView(preferenceViewHolder.findViewById(R$id.prefrence_switch));
    }

    private void syncSwitchView(View view) {
        boolean z = view instanceof Switch;
        if (z) {
            ((Switch) view).setOnCheckedChangeListener(null);
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(this.mChecked);
        }
        if (z) {
            ((Switch) view).setOnCheckedChangeListener(this.mListener);
        }
    }
}
