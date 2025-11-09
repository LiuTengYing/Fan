package com.android.settings.dashboard;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import java.util.List;
/* loaded from: classes.dex */
public interface DashboardFeatureProvider {
    List<DynamicDataObserver> bindPreferenceToTileAndGetObservers(FragmentActivity fragmentActivity, DashboardFragment dashboardFragment, boolean z, Preference preference, Tile tile, String str, int i);

    List<DashboardCategory> getAllCategories();

    String getDashboardKeyForTile(Tile tile);

    DashboardCategory getTilesForCategory(String str);
}
