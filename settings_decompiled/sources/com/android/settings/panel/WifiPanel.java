package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.WifiSettings;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class WifiPanel implements PanelContent {
    private final Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1687;
    }

    public static WifiPanel create(Context context) {
        return new WifiPanel(context);
    }

    private WifiPanel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(R$string.wifi_settings);
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CustomSliceRegistry.WIFI_SLICE_URI);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        Intent buildSearchResultPageIntent = SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, WifiSettings.class.getName(), (String) null, this.mContext.getText(R$string.wifi_settings).toString(), 103, R$string.menu_key_network);
        buildSearchResultPageIntent.setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        buildSearchResultPageIntent.addFlags(268435456);
        return buildSearchResultPageIntent;
    }
}
