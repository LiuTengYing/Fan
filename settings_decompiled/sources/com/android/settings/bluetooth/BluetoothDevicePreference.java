package com.android.settings.bluetooth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public final class BluetoothDevicePreference extends GearPreference {
    private static int sDimAlpha = Integer.MIN_VALUE;
    private String contentDescription;
    private final CachedBluetoothDevice mCachedDevice;
    final BluetoothDevicePreferenceCallback mCallback;
    private final long mCurrentTime;
    private AlertDialog mDisconnectDialog;
    private boolean mHideSecondTarget;
    private boolean mIsCallbackRemoved;
    boolean mNeedNotifyHierarchyChanged;
    Resources mResources;
    private final boolean mShowDevicesWithoutNames;
    private final int mType;
    private final UserManager mUserManager;

    /* loaded from: classes.dex */
    private class BluetoothDevicePreferenceCallback implements CachedBluetoothDevice.Callback {
        private BluetoothDevicePreferenceCallback() {
        }

        @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
        public void onDeviceAttributesChanged() {
            BluetoothDevicePreference.this.onPreferenceAttributesChanged();
        }
    }

    public BluetoothDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z, int i) {
        super(context, null);
        this.contentDescription = null;
        this.mHideSecondTarget = false;
        this.mIsCallbackRemoved = false;
        this.mNeedNotifyHierarchyChanged = false;
        this.mResources = getContext().getResources();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mShowDevicesWithoutNames = z;
        if (sDimAlpha == Integer.MIN_VALUE) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16842803, typedValue, true);
            sDimAlpha = (int) (typedValue.getFloat() * 255.0f);
        }
        this.mCachedDevice = cachedBluetoothDevice;
        BluetoothDevicePreferenceCallback bluetoothDevicePreferenceCallback = new BluetoothDevicePreferenceCallback();
        this.mCallback = bluetoothDevicePreferenceCallback;
        cachedBluetoothDevice.registerCallback(bluetoothDevicePreferenceCallback);
        this.mCurrentTime = System.currentTimeMillis();
        this.mType = i;
        onPreferenceAttributesChanged();
    }

    public void setNeedNotifyHierarchyChanged(boolean z) {
        this.mNeedNotifyHierarchyChanged = z;
    }

    @Override // com.android.settings.widget.GearPreference, com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        return cachedBluetoothDevice == null || cachedBluetoothDevice.getBondState() != 12 || this.mUserManager.hasUserRestriction("no_config_bluetooth") || this.mHideSecondTarget;
    }

    @Override // com.android.settings.widget.GearPreference, com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R$layout.preference_widget_gear;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDevice getCachedDevice() {
        return this.mCachedDevice;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        if (!this.mIsCallbackRemoved) {
            this.mCachedDevice.unregisterCallback(this.mCallback);
            this.mIsCallbackRemoved = true;
        }
        AlertDialog alertDialog = this.mDisconnectDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDisconnectDialog = null;
        }
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        if (this.mIsCallbackRemoved) {
            this.mCachedDevice.registerCallback(this.mCallback);
            this.mIsCallbackRemoved = false;
        }
        onPreferenceAttributesChanged();
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        if (this.mIsCallbackRemoved) {
            return;
        }
        this.mCachedDevice.unregisterCallback(this.mCallback);
        this.mIsCallbackRemoved = true;
    }

    public CachedBluetoothDevice getBluetoothDevice() {
        return this.mCachedDevice;
    }

    public void hideSecondTarget(boolean z) {
        this.mHideSecondTarget = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPreferenceAttributesChanged() {
        Pair<Drawable, String> drawableWithDescription = this.mCachedDevice.getDrawableWithDescription();
        setIcon((Drawable) drawableWithDescription.first);
        this.contentDescription = (String) drawableWithDescription.second;
        setTitle(this.mCachedDevice.getName());
        setSummary(this.mCachedDevice.getConnectionSummary());
        boolean z = true;
        setEnabled(!this.mCachedDevice.isBusy());
        if (!this.mShowDevicesWithoutNames && !this.mCachedDevice.hasHumanReadableName()) {
            z = false;
        }
        setVisible(z);
        if (this.mNeedNotifyHierarchyChanged) {
            notifyHierarchyChanged();
        }
    }

    @Override // com.android.settings.widget.GearPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ImageView imageView;
        if (findPreferenceInHierarchy("bt_checkbox") != null) {
            setDependency("bt_checkbox");
        }
        if (this.mCachedDevice.getBondState() == 12 && (imageView = (ImageView) preferenceViewHolder.findViewById(R$id.settings_button)) != null) {
            imageView.setOnClickListener(this);
        }
        ImageView imageView2 = (ImageView) preferenceViewHolder.findViewById(16908294);
        if (imageView2 != null) {
            imageView2.setContentDescription(this.contentDescription);
            imageView2.setImportantForAccessibility(2);
            imageView2.setElevation(getContext().getResources().getDimension(R$dimen.bt_icon_elevation));
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BluetoothDevicePreference)) {
            return false;
        }
        return this.mCachedDevice.equals(((BluetoothDevicePreference) obj).mCachedDevice);
    }

    public int hashCode() {
        return this.mCachedDevice.hashCode();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference, java.lang.Comparable
    public int compareTo(Preference preference) {
        if (!(preference instanceof BluetoothDevicePreference)) {
            return super.compareTo(preference);
        }
        int i = this.mType;
        if (i != 1) {
            if (i != 2) {
                return super.compareTo(preference);
            }
            return this.mCurrentTime > ((BluetoothDevicePreference) preference).mCurrentTime ? 1 : -1;
        }
        return this.mCachedDevice.compareTo(((BluetoothDevicePreference) preference).mCachedDevice);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onClicked() {
        Context context = getContext();
        int bondState = this.mCachedDevice.getBondState();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        if (this.mCachedDevice.isConnected()) {
            metricsFeatureProvider.action(context, 868, new Pair[0]);
            askDisconnect();
        } else if (bondState == 12) {
            metricsFeatureProvider.action(context, 867, new Pair[0]);
            this.mCachedDevice.connect();
        } else if (bondState == 10) {
            metricsFeatureProvider.action(context, 866, new Pair[0]);
            if (!this.mCachedDevice.hasHumanReadableName()) {
                metricsFeatureProvider.action(context, 1096, new Pair[0]);
            }
            pair();
        }
    }

    private void askDisconnect() {
        Context context = getContext();
        String name = this.mCachedDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R$string.bluetooth_device);
        }
        String string = context.getString(R$string.bluetooth_disconnect_all_profiles, name);
        this.mDisconnectDialog = Utils.showDisconnectDialog(context, this.mDisconnectDialog, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BluetoothDevicePreference.this.mCachedDevice.disconnect();
            }
        }, context.getString(R$string.bluetooth_disconnect_title), Html.fromHtml(string));
    }

    private void pair() {
        if (this.mCachedDevice.startPairing()) {
            return;
        }
        Utils.showError(getContext(), this.mCachedDevice.getName(), R$string.bluetooth_pairing_error_message);
    }
}
