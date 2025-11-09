package com.android.settingslib.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
/* loaded from: classes2.dex */
public class SettingsSpinnerAdapter<T> extends ArrayAdapter<T> {
    private static final int DEFAULT_RESOURCE = R$layout.settings_spinner_view;
    private static final int DFAULT_DROPDOWN_RESOURCE = R$layout.settings_spinner_dropdown_view;
    private final LayoutInflater mDefaultInflater;

    public SettingsSpinnerAdapter(Context context) {
        super(context, getDefaultResource());
        setDropDownViewResource(getDropdownResource());
        this.mDefaultInflater = LayoutInflater.from(context);
    }

    public View getDefaultView(int i, View view, ViewGroup viewGroup) {
        return this.mDefaultInflater.inflate(getDefaultResource(), viewGroup, false);
    }

    public View getDefaultDropDownView(int i, View view, ViewGroup viewGroup) {
        return this.mDefaultInflater.inflate(getDropdownResource(), viewGroup, false);
    }

    private static int getDefaultResource() {
        return DEFAULT_RESOURCE;
    }

    private static int getDropdownResource() {
        return DFAULT_DROPDOWN_RESOURCE;
    }
}
