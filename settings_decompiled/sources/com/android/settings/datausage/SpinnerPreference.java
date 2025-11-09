package com.android.settings.datausage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.datausage.CycleAdapter;
/* loaded from: classes.dex */
public class SpinnerPreference extends Preference implements CycleAdapter.SpinnerInterface {
    private CycleAdapter mAdapter;
    private Object mCurrentObject;
    private View mItemView;
    private boolean mItemViewVisible;
    private AdapterView.OnItemSelectedListener mListener;
    private final AdapterView.OnItemSelectedListener mOnSelectedListener;
    private int mPosition;

    public SpinnerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mItemViewVisible = false;
        this.mOnSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.datausage.SpinnerPreference.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (SpinnerPreference.this.mPosition == i) {
                    return;
                }
                SpinnerPreference.this.mPosition = i;
                SpinnerPreference spinnerPreference = SpinnerPreference.this;
                spinnerPreference.mCurrentObject = spinnerPreference.mAdapter.getItem(i);
                if (SpinnerPreference.this.mListener != null) {
                    SpinnerPreference.this.mListener.onItemSelected(adapterView, view, i, j);
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (SpinnerPreference.this.mListener != null) {
                    SpinnerPreference.this.mListener.onNothingSelected(adapterView);
                }
            }
        };
        setLayoutResource(R$layout.data_usage_cycles);
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public void setAdapter(CycleAdapter cycleAdapter) {
        this.mAdapter = cycleAdapter;
        notifyChanged();
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mListener = onItemSelectedListener;
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public Object getSelectedItem() {
        return this.mCurrentObject;
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public void setSelection(int i) {
        this.mPosition = i;
        this.mCurrentObject = this.mAdapter.getItem(i);
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mItemView = view;
        view.setVisibility(this.mItemViewVisible ? 0 : 4);
        Spinner spinner = (Spinner) preferenceViewHolder.findViewById(R$id.cycles_spinner);
        spinner.setAdapter((SpinnerAdapter) this.mAdapter);
        spinner.setSelection(this.mPosition);
        spinner.setOnItemSelectedListener(this.mOnSelectedListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHasCycles(boolean z) {
        setVisible(z);
        if (z) {
            this.mItemViewVisible = true;
            View view = this.mItemView;
            if (view != null) {
                view.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void performClick(View view) {
        view.findViewById(R$id.cycles_spinner).performClick();
    }
}
