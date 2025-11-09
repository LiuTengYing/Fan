package com.android.settings.wifi.dpp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ResolverListAdapter;
import com.android.internal.app.chooser.DisplayResolveInfo;
import com.android.internal.app.chooser.TargetInfo;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settingslib.qrcode.QrCodeGenerator;
import com.google.zxing.WriterException;
/* loaded from: classes2.dex */
public class WifiDppQrCodeGeneratorFragment extends WifiDppQrCodeBaseFragment {
    private String mQrCode;
    private ImageView mQrCodeView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment
    protected boolean isFooterAvailable() {
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        WifiNetworkConfig wifiNetworkConfigFromHostActivity = getWifiNetworkConfigFromHostActivity();
        if (getActivity() != null) {
            if (wifiNetworkConfigFromHostActivity.isHotspot()) {
                getActivity().setTitle(R$string.wifi_dpp_share_hotspot);
            } else {
                getActivity().setTitle(R$string.wifi_dpp_share_wifi);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem findItem = menu.findItem(1);
        if (findItem != null) {
            findItem.setShowAsAction(0);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R$layout.wifi_dpp_qrcode_generator_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mQrCodeView = (ImageView) view.findViewById(R$id.qrcode_view);
        final WifiNetworkConfig wifiNetworkConfigFromHostActivity = getWifiNetworkConfigFromHostActivity();
        if (wifiNetworkConfigFromHostActivity.isHotspot()) {
            setHeaderTitle(R$string.wifi_dpp_share_hotspot, new Object[0]);
        } else {
            setHeaderTitle(R$string.wifi_dpp_share_wifi, new Object[0]);
        }
        String preSharedKey = wifiNetworkConfigFromHostActivity.getPreSharedKey();
        TextView textView = (TextView) view.findViewById(R$id.password);
        if (TextUtils.isEmpty(preSharedKey)) {
            this.mSummary.setText(getString(R$string.wifi_dpp_scan_open_network_qr_code_with_another_device, wifiNetworkConfigFromHostActivity.getSsid()));
            textView.setVisibility(8);
        } else {
            this.mSummary.setText(getString(R$string.wifi_dpp_scan_qr_code_with_another_device, wifiNetworkConfigFromHostActivity.getSsid()));
            if (wifiNetworkConfigFromHostActivity.isHotspot()) {
                textView.setText(getString(R$string.wifi_dpp_hotspot_password, preSharedKey));
            } else {
                textView.setText(getString(R$string.wifi_dpp_wifi_password, preSharedKey));
            }
        }
        final Intent component = new Intent().setComponent(getNearbySharingComponent());
        addActionButton((ViewGroup) view.findViewById(R$id.wifi_dpp_layout), createNearbyButton(component, new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.WifiDppQrCodeGeneratorFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                WifiDppQrCodeGeneratorFragment.this.lambda$onViewCreated$0(component, wifiNetworkConfigFromHostActivity, view2);
            }
        }));
        this.mQrCode = wifiNetworkConfigFromHostActivity.getQrCode();
        setQrCode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$0(Intent intent, WifiNetworkConfig wifiNetworkConfig, View view) {
        intent.setAction("android.intent.action.SEND");
        intent.addFlags(268435456);
        intent.addFlags(32768);
        Bundle bundle = new Bundle();
        String removeFirstAndLastDoubleQuotes = WifiDppUtils.removeFirstAndLastDoubleQuotes(wifiNetworkConfig.getSsid());
        String preSharedKey = wifiNetworkConfig.getPreSharedKey();
        String security = wifiNetworkConfig.getSecurity();
        boolean hiddenSsid = wifiNetworkConfig.getHiddenSsid();
        bundle.putString("android.intent.extra.SSID", removeFirstAndLastDoubleQuotes);
        bundle.putString("android.intent.extra.PASSWORD", preSharedKey);
        bundle.putString("android.intent.extra.SECURITY_TYPE", security);
        bundle.putBoolean("android.intent.extra.HIDDEN_SSID", hiddenSsid);
        intent.putExtra("android.intent.extra.WIFI_CREDENTIALS_BUNDLE", bundle);
        startActivity(intent);
    }

    @VisibleForTesting
    ComponentName getNearbySharingComponent() {
        String string = Settings.Secure.getString(getContext().getContentResolver(), "nearby_sharing_component");
        if (TextUtils.isEmpty(string)) {
            string = getString(17039932);
        }
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    private TargetInfo getNearbySharingTarget(Intent intent) {
        ActivityInfo activityInfo;
        String str;
        Drawable drawable;
        ComponentName nearbySharingComponent = getNearbySharingComponent();
        Drawable drawable2 = null;
        drawable2 = null;
        CharSequence charSequence = null;
        if (nearbySharingComponent == null) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setComponent(nearbySharingComponent);
        PackageManager packageManager = getContext().getPackageManager();
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent2, 128);
        if (resolveActivity == null || (activityInfo = resolveActivity.activityInfo) == null) {
            Log.e("WifiDppQrCodeGeneratorFragment", "Device-specified nearby sharing component (" + nearbySharingComponent + ") not available");
            return null;
        }
        Bundle bundle = activityInfo.metaData;
        if (bundle != null) {
            try {
                Resources resourcesForActivity = packageManager.getResourcesForActivity(nearbySharingComponent);
                str = resourcesForActivity.getString(bundle.getInt("android.service.chooser.chip_label"));
                try {
                    drawable2 = resourcesForActivity.getDrawable(bundle.getInt("android.service.chooser.chip_icon"));
                } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused) {
                }
            } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused2) {
                str = null;
            }
            drawable = drawable2;
            charSequence = str;
        } else {
            drawable = null;
        }
        if (TextUtils.isEmpty(charSequence)) {
            charSequence = resolveActivity.loadLabel(packageManager);
        }
        CharSequence charSequence2 = charSequence;
        if (drawable == null) {
            drawable = resolveActivity.loadIcon(packageManager);
        }
        DisplayResolveInfo displayResolveInfo = new DisplayResolveInfo(intent, resolveActivity, charSequence2, "", intent2, (ResolverListAdapter.ResolveInfoPresentationGetter) null);
        displayResolveInfo.setDisplayIcon(drawable);
        return displayResolveInfo;
    }

    private Button createActionButton(Drawable drawable, CharSequence charSequence, View.OnClickListener onClickListener) {
        Button button = (Button) LayoutInflater.from(getContext()).inflate(17367128, (ViewGroup) null);
        if (drawable != null) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(17105043);
            drawable.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
            button.setCompoundDrawablesRelative(drawable, null, null, null);
        }
        button.setText(charSequence);
        button.setOnClickListener(onClickListener);
        return button;
    }

    private void addActionButton(ViewGroup viewGroup, Button button) {
        if (button == null) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int dimensionPixelSize = getResources().getDimensionPixelSize(17105496) / 2;
        marginLayoutParams.setMarginsRelative(dimensionPixelSize, 0, dimensionPixelSize, 0);
        viewGroup.addView(button, marginLayoutParams);
    }

    @VisibleForTesting
    Button createNearbyButton(Intent intent, View.OnClickListener onClickListener) {
        TargetInfo nearbySharingTarget;
        if ((((WifiManager) getContext().getSystemService("wifi")).isStaApConcurrencySupported() || !getWifiNetworkConfigFromHostActivity().isHotspot()) && (nearbySharingTarget = getNearbySharingTarget(intent)) != null) {
            Button createActionButton = createActionButton(nearbySharingTarget.getDisplayIcon(getContext()), nearbySharingTarget.getDisplayLabel(), onClickListener);
            createActionButton.setAllCaps(false);
            return createActionButton;
        }
        return null;
    }

    private void setQrCode() {
        try {
            this.mQrCodeView.setImageBitmap(QrCodeGenerator.encodeQrCode(this.mQrCode, getContext().getResources().getDimensionPixelSize(R$dimen.qrcode_size)));
        } catch (WriterException e) {
            Log.e("WifiDppQrCodeGeneratorFragment", "Error generating QR code bitmap " + e);
        }
    }

    private WifiNetworkConfig getWifiNetworkConfigFromHostActivity() {
        WifiNetworkConfig wifiNetworkConfig = ((WifiNetworkConfig.Retriever) getActivity()).getWifiNetworkConfig();
        if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
            return wifiNetworkConfig;
        }
        throw new IllegalStateException("Invalid Wi-Fi network for configuring");
    }
}
