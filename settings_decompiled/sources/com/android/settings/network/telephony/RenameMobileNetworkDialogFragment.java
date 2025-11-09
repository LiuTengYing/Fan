package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$array;
import com.android.settings.R$color;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.DeviceInfoUtils;
import com.google.common.collect.ImmutableMap;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class RenameMobileNetworkDialogFragment extends InstrumentedDialogFragment {
    private boolean mAllowEditPhoneNumber;
    private Spinner mColorSpinner;
    private Color[] mColors;
    private Map<Integer, Integer> mLightDarkMap;
    private EditText mNameView;
    private EditText mPhoneNumber;
    private int mSubId;
    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;
    private UniTelephonyManager mUniTelephonyManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1642;
    }

    public static RenameMobileNetworkDialogFragment newInstance(int i) {
        Bundle bundle = new Bundle(1);
        bundle.putInt("subscription_id", i);
        RenameMobileNetworkDialogFragment renameMobileNetworkDialogFragment = new RenameMobileNetworkDialogFragment();
        renameMobileNetworkDialogFragment.setArguments(bundle);
        return renameMobileNetworkDialogFragment;
    }

    protected TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    protected SubscriptionManager getSubscriptionManager(Context context) {
        return (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    protected EditText getNameView() {
        return this.mNameView;
    }

    protected Spinner getColorSpinnerView() {
        return this.mColorSpinner;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mTelephonyManager = getTelephonyManager(context);
        this.mUniTelephonyManager = UniTelephonyManager.from(context);
        this.mSubscriptionManager = getSubscriptionManager(context);
        this.mSubId = getArguments().getInt("subscription_id");
        this.mAllowEditPhoneNumber = context.getResources().getBoolean(134414339);
        Resources resources = context.getResources();
        this.mLightDarkMap = ImmutableMap.builder().put(Integer.valueOf(resources.getInteger(R$color.SIM_color_cyan)), Integer.valueOf(resources.getInteger(R$color.SIM_dark_mode_color_cyan))).put(Integer.valueOf(resources.getInteger(R$color.SIM_color_blue800)), Integer.valueOf(resources.getInteger(R$color.SIM_dark_mode_color_blue))).put(Integer.valueOf(resources.getInteger(R$color.SIM_color_green800)), Integer.valueOf(resources.getInteger(R$color.SIM_dark_mode_color_green))).put(Integer.valueOf(resources.getInteger(R$color.SIM_color_purple800)), Integer.valueOf(resources.getInteger(R$color.SIM_dark_mode_color_purple))).put(Integer.valueOf(resources.getInteger(R$color.SIM_color_pink800)), Integer.valueOf(resources.getInteger(R$color.SIM_dark_mode_color_pink))).put(Integer.valueOf(resources.getInteger(R$color.SIM_color_orange)), Integer.valueOf(resources.getInteger(R$color.SIM_dark_mode_color_orange))).build();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mColors = getColors();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inflate = ((LayoutInflater) builder.getContext().getSystemService(LayoutInflater.class)).inflate(R$layout.dialog_mobile_network_rename, (ViewGroup) null);
        populateView(inflate);
        builder.setTitle(R$string.mobile_network_sim_name).setView(inflate).setPositiveButton(R$string.mobile_network_sim_name_rename, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.telephony.RenameMobileNetworkDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RenameMobileNetworkDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        this.mSubscriptionManager.setDisplayName(this.mNameView.getText().toString(), this.mSubId, 2);
        Spinner spinner = this.mColorSpinner;
        this.mSubscriptionManager.setIconTint((spinner == null ? this.mColors[0] : this.mColors[spinner.getSelectedItemPosition()]).getColor(), this.mSubId);
        if (this.mAllowEditPhoneNumber) {
            String msisdnNumber = this.mUniTelephonyManager.getMsisdnNumber(this.mSubId);
            String obj = this.mPhoneNumber.getText().toString();
            SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(this.mSubId);
            Log.d("RenameMobileNetwork", "setMsisdnNumber:" + obj);
            if (obj == null || obj.equals(msisdnNumber)) {
                return;
            }
            this.mUniTelephonyManager.setMsisdnNumber(this.mSubId, "phoneNumber", obj, (Message) null);
            this.mTelephonyManager.setLine1NumberForDisplay(this.mSubId, "phoneNumber", obj);
            activeSubscriptionInfo.setNumber(obj);
            this.mSubscriptionManager.setDisplayNumber(obj, this.mSubId);
        }
    }

    protected void populateView(View view) {
        this.mNameView = (EditText) view.findViewById(R$id.name_edittext);
        List<SubscriptionInfo> availableSubscriptionInfoList = this.mSubscriptionManager.getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : availableSubscriptionInfoList) {
                if (subscriptionInfo.getSubscriptionId() == this.mSubId) {
                    break;
                }
            }
        }
        subscriptionInfo = null;
        if (subscriptionInfo == null) {
            Log.w("RenameMobileNetwork", "got null SubscriptionInfo for mSubId:" + this.mSubId);
            return;
        }
        CharSequence displayName = subscriptionInfo.getDisplayName();
        this.mNameView.setText(displayName);
        if (!TextUtils.isEmpty(displayName)) {
            this.mNameView.setSelection(displayName.length());
        }
        this.mColorSpinner = (Spinner) view.findViewById(R$id.color_spinner);
        this.mColorSpinner.setAdapter((SpinnerAdapter) new ColorAdapter(getContext(), R$layout.dialog_mobile_network_color_picker_item, this.mColors));
        this.mColorSpinner.setSelection(getSimColorIndex(subscriptionInfo.getIconTint()));
        this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(this.mSubId);
        ((TextView) view.findViewById(R$id.operator_name_value)).setText(subscriptionInfo.getCarrierName());
        ((TextView) view.findViewById(R$id.number_label)).setVisibility(subscriptionInfo.isOpportunistic() ? 8 : 0);
        if (this.mAllowEditPhoneNumber) {
            this.mPhoneNumber = (EditText) view.findViewById(R$id.edit_number_value);
            ((TextView) view.findViewById(R$id.number_value)).setVisibility(8);
            this.mPhoneNumber.setVisibility(0);
            String msisdnNumber = this.mUniTelephonyManager.getMsisdnNumber(this.mSubId);
            Log.d("RenameMobileNetwork", "getMsisdnNumber:" + msisdnNumber);
            this.mPhoneNumber.setText(msisdnNumber);
            return;
        }
        TextView textView = (TextView) view.findViewById(R$id.number_value);
        String bidiFormattedPhoneNumber = DeviceInfoUtils.getBidiFormattedPhoneNumber(getContext(), subscriptionInfo);
        if (TextUtils.isEmpty(bidiFormattedPhoneNumber)) {
            return;
        }
        textView.setText(bidiFormattedPhoneNumber);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ColorAdapter extends ArrayAdapter<Color> {
        private Context mContext;
        private int mItemResId;

        public ColorAdapter(Context context, int i, Color[] colorArr) {
            super(context, i, colorArr);
            this.mContext = context;
            this.mItemResId = i;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
            if (view == null) {
                view = layoutInflater.inflate(this.mItemResId, (ViewGroup) null);
            }
            ((ImageView) view.findViewById(R$id.color_icon)).setImageDrawable(getItem(i).getDrawable((RenameMobileNetworkDialogFragment.this.getResources().getConfiguration().uiMode & 48) == 32));
            ((TextView) view.findViewById(R$id.color_label)).setText(getItem(i).getLabel());
            return view;
        }

        @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            return getView(i, view, viewGroup);
        }
    }

    private Color[] getColors() {
        Resources resources = getContext().getResources();
        int[] intArray = resources.getIntArray(R$array.sim_color_light);
        String[] stringArray = resources.getStringArray(R$array.color_picker);
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.color_swatch_size);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.color_swatch_stroke_width);
        int length = intArray.length;
        Color[] colorArr = new Color[length];
        for (int i = 0; i < length; i++) {
            colorArr[i] = new Color(stringArray[i], intArray[i], dimensionPixelSize, dimensionPixelSize2);
        }
        return colorArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Color {
        private int mColor;
        private ShapeDrawable mDrawable;
        private String mLabel;

        private Color(String str, int i, int i2, int i3) {
            this.mLabel = str;
            this.mColor = i;
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            this.mDrawable = shapeDrawable;
            shapeDrawable.setIntrinsicHeight(i2);
            this.mDrawable.setIntrinsicWidth(i2);
            this.mDrawable.getPaint().setStrokeWidth(i3);
            this.mDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            this.mDrawable.getPaint().setColor(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getLabel() {
            return this.mLabel;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getColor() {
            return this.mColor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ShapeDrawable getDrawable(boolean z) {
            if (z) {
                this.mDrawable.getPaint().setColor(RenameMobileNetworkDialogFragment.this.getDarkColor(this.mColor));
            }
            return this.mDrawable;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDarkColor(int i) {
        return this.mLightDarkMap.getOrDefault(Integer.valueOf(i), Integer.valueOf(i)).intValue();
    }

    private int getSimColorIndex(int i) {
        int[] intArray = getContext().getResources().getIntArray(17236180);
        int i2 = -1;
        for (int i3 = 0; i3 < intArray.length; i3++) {
            if (intArray[i3] == i) {
                i2 = i3;
            }
        }
        if (i2 == -1) {
            int i4 = 0;
            while (true) {
                Color[] colorArr = this.mColors;
                if (i4 >= colorArr.length) {
                    break;
                }
                if (colorArr[i4].getColor() == i) {
                    i2 = i4;
                }
                i4++;
            }
        }
        if (i2 == -1) {
            return 0;
        }
        return i2;
    }
}
