package com.android.settings.development.tare;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class TareFactorExpandableListAdapter extends BaseExpandableListAdapter {
    private final String[][] mChildren;
    private final TareFactorController mFactorController;
    private final String[] mGroups;
    private final String[][] mKeys;
    private final LayoutInflater mLayoutInflater;

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int i) {
        return i;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TareFactorExpandableListAdapter(TareFactorController tareFactorController, LayoutInflater layoutInflater, String[] strArr, String[][] strArr2, String[][] strArr3) {
        this.mLayoutInflater = layoutInflater;
        this.mFactorController = tareFactorController;
        this.mGroups = strArr;
        this.mChildren = strArr2;
        this.mKeys = strArr3;
        validateMappings();
    }

    private void validateMappings() {
        int length = this.mGroups.length;
        String[][] strArr = this.mChildren;
        if (length != strArr.length) {
            throw new IllegalStateException("groups and children don't have the same length");
        }
        if (strArr.length != this.mKeys.length) {
            throw new IllegalStateException("children and keys don't have the same length");
        }
        int i = 0;
        while (true) {
            String[][] strArr2 = this.mChildren;
            if (i >= strArr2.length) {
                return;
            }
            if (strArr2[i].length != this.mKeys[i].length) {
                throw new IllegalStateException("children and keys don't have the same length in row " + i);
            }
            i++;
        }
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.mGroups.length;
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int i) {
        return this.mChildren[i].length;
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int i) {
        return this.mGroups[i];
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int i, int i2) {
        return this.mChildren[i][i2];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getKey(int i, int i2) {
        return this.mKeys[i][i2];
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(17367043, viewGroup, false);
        }
        ((TextView) view.findViewById(16908308)).setText(getGroup(i).toString());
        return view;
    }

    @Override // android.widget.ExpandableListAdapter
    @SuppressLint({"InflateParams"})
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(R$layout.tare_child_item, (ViewGroup) null);
        }
        ((TextView) view.findViewById(R$id.factor)).setText(getChild(i, i2).toString());
        ((TextView) view.findViewById(R$id.factor_number)).setText(cakeToString(this.mFactorController.getValue(getKey(i, i2))));
        return view;
    }

    private static String cakeToString(long j) {
        if (j == 0) {
            return "0";
        }
        long j2 = j % 1000000000;
        long j3 = (int) (j / 1000000000);
        if (j3 == 0) {
            return j2 + " c";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(j3);
        if (j2 > 0) {
            sb.append(".");
            sb.append(String.format("%03d", Long.valueOf(j2 / 1000000)));
        }
        sb.append(" A");
        return sb.toString();
    }
}
