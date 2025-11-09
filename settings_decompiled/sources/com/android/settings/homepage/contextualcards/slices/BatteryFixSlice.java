package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.ArrayMap;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.fuelgauge.BatteryUsageStatsLoader;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.fuelgauge.batterytip.BatteryTipLoader;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.homepage.contextualcards.slices.BatteryFixSlice;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class BatteryFixSlice implements CustomSliceable {
    static final String KEY_CURRENT_TIPS_TYPE = "current_tip_type";
    static final String PREFS = "battery_fix_prefs";
    private static final Map<Integer, List<Integer>> UNIMPORTANT_BATTERY_TIPS;
    private final Context mContext;

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
    }

    static {
        ArrayMap arrayMap = new ArrayMap();
        UNIMPORTANT_BATTERY_TIPS = arrayMap;
        arrayMap.put(6, Arrays.asList(0, 1));
        arrayMap.put(2, Arrays.asList(0, 1));
        arrayMap.put(3, Arrays.asList(1));
    }

    public BatteryFixSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.BATTERY_FIX_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        ListBuilder accentColor = new ListBuilder(this.mContext, CustomSliceRegistry.BATTERY_FIX_SLICE_URI, -1L).setAccentColor(-1);
        if (!isBatteryTipAvailableFromCache(this.mContext)) {
            return buildBatteryGoodSlice(accentColor, true);
        }
        SliceBackgroundWorker sliceBackgroundWorker = SliceBackgroundWorker.getInstance(getUri());
        List results = sliceBackgroundWorker != null ? sliceBackgroundWorker.getResults() : null;
        if (results == null) {
            return buildBatteryGoodSlice(accentColor, false);
        }
        Iterator it = results.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BatteryTip batteryTip = (BatteryTip) it.next();
            if (batteryTip.getState() != 2) {
                Drawable drawable = this.mContext.getDrawable(batteryTip.getIconId());
                int iconTintColorId = batteryTip.getIconTintColorId();
                if (iconTintColorId != -1) {
                    drawable.setColorFilter(new PorterDuffColorFilter(this.mContext.getResources().getColor(iconTintColorId), PorterDuff.Mode.SRC_IN));
                }
                IconCompat createIconWithDrawable = Utils.createIconWithDrawable(drawable);
                accentColor.addRow(new ListBuilder.RowBuilder().setTitleItem(createIconWithDrawable, 0).setTitle(batteryTip.getTitle(this.mContext)).setSubtitle(batteryTip.getSummary(this.mContext)).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createIconWithDrawable, 0, batteryTip.getTitle(this.mContext))));
            }
        }
        return accentColor.build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        String charSequence = this.mContext.getText(R$string.power_usage_summary_title).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, PowerUsageSummary.class.getName(), BatteryTipPreferenceController.PREF_NAME, charSequence, 1401, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath(BatteryTipPreferenceController.PREF_NAME).build());
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_battery;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return BatteryTipWorker.class;
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864);
    }

    private Slice buildBatteryGoodSlice(ListBuilder listBuilder, boolean z) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.ic_battery_status_good_24dp);
        String string = this.mContext.getString(R$string.power_usage_summary_title);
        listBuilder.addRow(new ListBuilder.RowBuilder().setTitleItem(createWithResource, 0).setTitle(string).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, string))).setIsError(z);
        return listBuilder.build();
    }

    public static void updateBatteryTipAvailabilityCache(final Context context) {
        ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.homepage.contextualcards.slices.BatteryFixSlice$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object refreshBatteryTips;
                refreshBatteryTips = BatteryFixSlice.refreshBatteryTips(context);
                return refreshBatteryTips;
            }
        });
    }

    static boolean isBatteryTipAvailableFromCache(Context context) {
        boolean z = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, 0);
        int i = sharedPreferences.getInt(KEY_CURRENT_TIPS_TYPE, 6);
        int i2 = sharedPreferences.getInt("current_tip_state", 2);
        if (i2 == 2) {
            return false;
        }
        Map<Integer, List<Integer>> map = UNIMPORTANT_BATTERY_TIPS;
        if (map.containsKey(Integer.valueOf(i)) && map.get(Integer.valueOf(i)).contains(Integer.valueOf(i2))) {
            z = true;
        }
        return !z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<BatteryTip> refreshBatteryTips(Context context) {
        List<BatteryTip> loadInBackground = new BatteryTipLoader(context, new BatteryUsageStatsLoader(context, false).loadInBackground()).loadInBackground();
        Iterator<BatteryTip> it = loadInBackground.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BatteryTip next = it.next();
            if (next.getState() != 2) {
                context.getSharedPreferences(PREFS, 0).edit().putInt(KEY_CURRENT_TIPS_TYPE, next.getType()).putInt("current_tip_state", next.getState()).apply();
                break;
            }
        }
        return loadInBackground;
    }

    /* loaded from: classes.dex */
    public static class BatteryTipWorker extends SliceBackgroundWorker<BatteryTip> {
        private final Context mContext;

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSliceUnpinned() {
        }

        public BatteryTipWorker(Context context, Uri uri) {
            super(context, uri);
            this.mContext = context;
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSlicePinned() {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.homepage.contextualcards.slices.BatteryFixSlice$BatteryTipWorker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BatteryFixSlice.BatteryTipWorker.this.lambda$onSlicePinned$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSlicePinned$0() {
            updateResults(BatteryFixSlice.refreshBatteryTips(this.mContext));
        }
    }
}
