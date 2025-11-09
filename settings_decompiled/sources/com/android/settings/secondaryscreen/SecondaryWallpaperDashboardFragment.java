package com.android.settings.secondaryscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Switch;
import androidx.fragment.app.Fragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.SettingsApplication;
import com.syu.util.MySharePreference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class SecondaryWallpaperDashboardFragment extends Fragment {
    private static String SETPIC = "com.syu.gallery.setpic";
    private static String TAG = "SecondaryWallpaperDashboardFragment";
    private Switch mAutoSwitch;
    private View mRootView;
    private GridView mWallpaperList;
    private WallpaperAdapter wallpaperAdapter;
    private ArrayList<Drawable> wallpapers = new ArrayList<>();
    private BroadcastReceiver WallpaperReceiver = new BroadcastReceiver() { // from class: com.android.settings.secondaryscreen.SecondaryWallpaperDashboardFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SecondaryWallpaperDashboardFragment.SETPIC)) {
                SecondaryWallpaperDashboardFragment.this.updateCustomWallpaper(intent.getStringExtra("pic_path"));
            }
        }
    };

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R$layout.secondary_screen_wallpaper_layout, viewGroup, false);
        initViews();
        return this.mRootView;
    }

    private void initViews() {
        this.mAutoSwitch = (Switch) this.mRootView.findViewById(R$id.wallpaper_auto_switch);
        this.mWallpaperList = (GridView) this.mRootView.findViewById(R$id.wallpaper_list);
        this.mAutoSwitch.setChecked(SystemProperties.getBoolean("persist.sys.autowallpaper", false));
        initData();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SETPIC);
        getContext().registerReceiver(this.WallpaperReceiver, intentFilter);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        getContext().unregisterReceiver(this.WallpaperReceiver);
        super.onDestroy();
    }

    private void initData() {
        this.wallpapers.clear();
        Drawable drawable = SettingsApplication.mApplication.getResources().getDrawable(R$drawable.wallpaper1);
        Drawable drawable2 = SettingsApplication.mApplication.getResources().getDrawable(R$drawable.wallpaper2);
        Drawable drawable3 = SettingsApplication.mApplication.getResources().getDrawable(R$drawable.wallpaper3);
        Drawable drawable4 = SettingsApplication.mApplication.getResources().getDrawable(R$drawable.wallpaper4);
        Drawable drawable5 = SettingsApplication.mApplication.getResources().getDrawable(R$drawable.wallpaper5);
        this.wallpapers.add(drawable);
        this.wallpapers.add(drawable2);
        this.wallpapers.add(drawable3);
        this.wallpapers.add(drawable4);
        this.wallpapers.add(drawable5);
        String stringValue = MySharePreference.getStringValue("wallpaper");
        String str = TAG;
        Log.d(str, "initData: " + stringValue);
        if (TextUtils.isEmpty(stringValue)) {
            this.wallpapers.add(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.add_wallpaper));
        } else {
            this.wallpapers.add(new BitmapDrawable(SettingsApplication.mApplication.getResources(), BitmapFactory.decodeFile(stringValue)));
        }
        WallpaperAdapter wallpaperAdapter = new WallpaperAdapter(getContext(), this.wallpapers);
        this.wallpaperAdapter = wallpaperAdapter;
        this.mWallpaperList.setAdapter((ListAdapter) wallpaperAdapter);
        this.mWallpaperList.requestDisallowInterceptTouchEvent(true);
        this.mWallpaperList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.secondaryscreen.SecondaryWallpaperDashboardFragment.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                SecondaryWallpaperDashboardFragment.this.setWallpaperToSecondaryScreen(i);
            }
        });
        this.mWallpaperList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.android.settings.secondaryscreen.SecondaryWallpaperDashboardFragment.3
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (i < 5) {
                    Log.d(SecondaryWallpaperDashboardFragment.TAG, "onItemLongClick: regular wallpaper can not remove");
                    return false;
                }
                String str2 = SecondaryWallpaperDashboardFragment.TAG;
                Log.d(str2, "onItemLongClick: " + i);
                return false;
            }
        });
        this.mAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.secondaryscreen.SecondaryWallpaperDashboardFragment.4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SystemProperties.set("persist.sys.autowallpaper", z + "");
                Intent intent = new Intent();
                intent.setAction("com.syu.secondaryscreen_auto_wallpaper");
                intent.putExtra("auto", z);
                SettingsApplication.mApplication.sendBroadcast(intent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWallpaperToSecondaryScreen(int i) {
        if (i == this.wallpapers.size() - 1) {
            Intent intent = new Intent();
            intent.putExtra("folder", "/mnt/sdcard/portrait_carlogo");
            intent.setPackage("com.syu.gallery");
            intent.setAction("com.syu.gallery.jumpfolder");
            intent.addFlags(268435456);
            intent.addFlags(2097152);
            getContext().startActivity(intent);
            return;
        }
        Intent intent2 = new Intent();
        intent2.setAction("com.syu.secondaryscreen_wallpaper");
        intent2.putExtra("choose", i + "");
        SettingsApplication.mApplication.sendBroadcast(intent2);
        if (this.mAutoSwitch.isChecked()) {
            this.mAutoSwitch.setChecked(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCustomWallpaper(String str) {
        String str2 = TAG;
        Log.d(str2, "updateCustomWallpaper: " + str);
        if (TextUtils.isEmpty(str)) {
            return;
        }
        MySharePreference.saveStringValue("wallpaper", str);
        if (this.mAutoSwitch.isChecked()) {
            this.mAutoSwitch.setChecked(false);
        }
        Intent intent = new Intent();
        intent.setAction("com.syu.secondaryscreen_wallpaper");
        intent.putExtra("choose", str);
        SettingsApplication.mApplication.sendBroadcast(intent);
        initData();
    }
}
