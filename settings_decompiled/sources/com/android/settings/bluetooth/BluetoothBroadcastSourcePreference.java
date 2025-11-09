package com.android.settings.bluetooth;

import android.bluetooth.BluetoothLeAudioContentMetadata;
import android.bluetooth.BluetoothLeBroadcastMetadata;
import android.bluetooth.BluetoothLeBroadcastReceiveState;
import android.bluetooth.BluetoothLeBroadcastSubgroup;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class BluetoothBroadcastSourcePreference extends Preference {
    private BluetoothLeBroadcastMetadata mBluetoothLeBroadcastMetadata;
    private BluetoothLeBroadcastReceiveState mBluetoothLeBroadcastReceiveState;
    private ImageView mFrictionImageView;
    private boolean mIsEncrypted;
    private boolean mStatus;
    private String mTitle;
    private static final int RESOURCE_ID_UNKNOWN_PROGRAM_INFO = R$string.device_info_default;
    private static final int RESOURCE_ID_ICON = R$drawable.settings_input_antenna;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothBroadcastSourcePreference(Context context) {
        super(context);
        initUi();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(R$id.two_target_divider).setVisibility(4);
        ((ImageButton) preferenceViewHolder.findViewById(R$id.icon_button)).setVisibility(8);
        this.mFrictionImageView = (ImageView) preferenceViewHolder.findViewById(R$id.friction_icon);
        updateStatusButton();
    }

    private void initUi() {
        setLayoutResource(R$layout.preference_access_point);
        setWidgetLayoutResource(R$layout.access_point_friction_widget);
        this.mTitle = getContext().getString(RESOURCE_ID_UNKNOWN_PROGRAM_INFO);
        this.mStatus = false;
        Drawable drawable = getContext().getDrawable(RESOURCE_ID_ICON);
        if (drawable != null) {
            drawable.setTint(com.android.settingslib.Utils.getColorAttrDefaultColor(getContext(), 16843817));
            setIcon(drawable);
        }
    }

    private void updateStatusButton() {
        Drawable drawable;
        ImageView imageView = this.mFrictionImageView;
        if (imageView == null) {
            return;
        }
        boolean z = this.mStatus;
        if (z || this.mIsEncrypted) {
            if (z) {
                drawable = getContext().getDrawable(R$drawable.bluetooth_broadcast_dialog_done);
            } else {
                drawable = getContext().getDrawable(R$drawable.ic_friction_lock_closed);
            }
            if (drawable != null) {
                drawable.setTint(com.android.settingslib.Utils.getColorAttrDefaultColor(getContext(), 16843817));
                this.mFrictionImageView.setImageDrawable(drawable);
            }
            this.mFrictionImageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    public void updateMetadataAndRefreshUi(BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata, boolean z) {
        this.mBluetoothLeBroadcastMetadata = bluetoothLeBroadcastMetadata;
        this.mTitle = getProgramInfo();
        this.mIsEncrypted = this.mBluetoothLeBroadcastMetadata.isEncrypted();
        this.mStatus = z || this.mBluetoothLeBroadcastReceiveState != null;
        refresh();
    }

    public void updateReceiveStateAndRefreshUi(BluetoothLeBroadcastReceiveState bluetoothLeBroadcastReceiveState) {
        this.mBluetoothLeBroadcastReceiveState = bluetoothLeBroadcastReceiveState;
        this.mTitle = getProgramInfo();
        this.mStatus = true;
        refresh();
    }

    public BluetoothLeBroadcastMetadata getBluetoothLeBroadcastMetadata() {
        return this.mBluetoothLeBroadcastMetadata;
    }

    private void refresh() {
        setTitle(this.mTitle);
        updateStatusButton();
    }

    private String getProgramInfo() {
        BluetoothLeBroadcastReceiveState bluetoothLeBroadcastReceiveState = this.mBluetoothLeBroadcastReceiveState;
        if (bluetoothLeBroadcastReceiveState != null) {
            List subgroupMetadata = bluetoothLeBroadcastReceiveState.getSubgroupMetadata();
            if (!subgroupMetadata.isEmpty()) {
                return (String) subgroupMetadata.stream().map(new Function() { // from class: com.android.settings.bluetooth.BluetoothBroadcastSourcePreference$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        String programInfo;
                        programInfo = ((BluetoothLeAudioContentMetadata) obj).getProgramInfo();
                        return programInfo;
                    }
                }).findFirst().orElse(getContext().getString(RESOURCE_ID_UNKNOWN_PROGRAM_INFO));
            }
        }
        BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata = this.mBluetoothLeBroadcastMetadata;
        if (bluetoothLeBroadcastMetadata == null) {
            return getContext().getString(RESOURCE_ID_UNKNOWN_PROGRAM_INFO);
        }
        List subgroups = bluetoothLeBroadcastMetadata.getSubgroups();
        if (subgroups.isEmpty()) {
            return getContext().getString(RESOURCE_ID_UNKNOWN_PROGRAM_INFO);
        }
        return (String) subgroups.stream().map(new Function() { // from class: com.android.settings.bluetooth.BluetoothBroadcastSourcePreference$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$getProgramInfo$1;
                lambda$getProgramInfo$1 = BluetoothBroadcastSourcePreference.lambda$getProgramInfo$1((BluetoothLeBroadcastSubgroup) obj);
                return lambda$getProgramInfo$1;
            }
        }).filter(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothBroadcastSourcePreference$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getProgramInfo$2;
                lambda$getProgramInfo$2 = BluetoothBroadcastSourcePreference.lambda$getProgramInfo$2((String) obj);
                return lambda$getProgramInfo$2;
            }
        }).findFirst().orElse(getContext().getString(RESOURCE_ID_UNKNOWN_PROGRAM_INFO));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getProgramInfo$1(BluetoothLeBroadcastSubgroup bluetoothLeBroadcastSubgroup) {
        return bluetoothLeBroadcastSubgroup.getContentMetadata().getProgramInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getProgramInfo$2(String str) {
        return !TextUtils.isEmpty(str);
    }

    public boolean isEncrypted() {
        return this.mIsEncrypted;
    }

    public void clearReceiveState() {
        this.mBluetoothLeBroadcastReceiveState = null;
        this.mTitle = getProgramInfo();
        this.mStatus = false;
        refresh();
    }
}
