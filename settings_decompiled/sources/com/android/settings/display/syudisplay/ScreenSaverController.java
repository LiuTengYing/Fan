package com.android.settings.display.syudisplay;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemProperties;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ScreenSaverController extends BasePreferenceController {
    private static int timePosition;
    private String KEY;
    private List<Drawable> imgMenu;
    private boolean isScreensaverOn;
    private View mContentView;
    private Context mContext;
    private Fragment mFragment;
    private LayoutPreference mPreference;
    private RecyclerView mScreenSaverGrid;
    private Switch mScreenSaverSwitch;
    private RelativeLayout mScreenSaverTime;
    private RelativeLayout mScreenSaverTimeLy;
    private TextView mTime120;
    private TextView mTime1800;
    private TextView mTime30;
    private TextView mTime300;
    private TextView mTime600;
    private TextView mTimeState;
    private PopupWindow popupWindow;
    private ScreenSaverAdapter screenSaverAdapter;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    private int getAutoTime(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return i != 4 ? 60 : 1200;
                    }
                    return 600;
                }
                return 300;
            }
            return 180;
        }
        return 60;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ScreenSaverController(Fragment fragment, Context context, String str) {
        super(context, str);
        this.KEY = "screensaver_preview";
        this.imgMenu = new ArrayList();
        this.isScreensaverOn = false;
        this.mContext = context;
        this.mFragment = fragment;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return SystemProperties.getBoolean("persist.syu.showScreensaver", true) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = (LayoutPreference) preferenceScreen.findPreference(this.KEY);
        initViews();
        super.displayPreference(preferenceScreen);
    }

    private void initViews() {
        LayoutPreference layoutPreference = this.mPreference;
        int i = R$id.backlight_screen_saver_time_ly;
        this.mScreenSaverTime = (RelativeLayout) layoutPreference.findViewById(i);
        this.mTimeState = (TextView) this.mPreference.findViewById(R$id.screen_saver_time_state);
        this.mScreenSaverGrid = (RecyclerView) this.mPreference.findViewById(R$id.screen_saver_gridview);
        this.mScreenSaverSwitch = (Switch) this.mPreference.findViewById(R$id.screen_saver_switch);
        this.mScreenSaverTimeLy = (RelativeLayout) this.mPreference.findViewById(i);
        this.mScreenSaverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                ScreenSaverController.this.updateScreenSaver(z);
            }
        });
        this.mScreenSaverTimeLy.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d("fangli", "mScreenSaverTimeLy onClick: ");
                if (ScreenSaverController.this.popupWindow != null && ScreenSaverController.this.popupWindow.isShowing()) {
                    ScreenSaverController.this.popupWindow.dismiss();
                } else {
                    ScreenSaverController.this.showTimeSelectPop();
                }
            }
        });
        Cursor query = this.mContext.getContentResolver().query(Uri.parse("content://com.syu.screensaver.provider"), null, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                byte[] decode = Base64.decode(query.getString(0), 0);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(SettingsApplication.mResources, BitmapFactory.decodeByteArray(decode, 0, decode.length));
                Log.d("fangli", "initViews: " + bitmapDrawable.getIntrinsicWidth() + "  " + bitmapDrawable.getIntrinsicHeight());
                this.imgMenu.add(bitmapDrawable);
            }
            query.close();
            initData();
        }
    }

    private void initData() {
        int i = SystemProperties.getInt("persist.lsec.screensaver.styleIndex", 0);
        Log.d("ScreenSaverController", "setScreensaverState222: " + this.isScreensaverOn);
        this.mScreenSaverSwitch.setChecked(this.isScreensaverOn);
        this.screenSaverAdapter = new ScreenSaverAdapter(this.mContext, this.imgMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        linearLayoutManager.setOrientation(0);
        this.mScreenSaverGrid.setLayoutManager(linearLayoutManager);
        this.mScreenSaverGrid.setAdapter(this.screenSaverAdapter);
        this.screenSaverAdapter.setItemSelect(i);
        this.mScreenSaverGrid.getParent().requestDisallowInterceptTouchEvent(true);
        this.screenSaverAdapter.setOnItemClickLIstener(new OnItemClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.3
            @Override // com.android.settings.display.syudisplay.ScreenSaverController.OnItemClickListener
            public void onItemClick(int i2) {
                SystemProperties.set("persist.lsec.screensaver.styleIndex", i2 + "");
                ScreenSaverController.this.screenSaverAdapter.setItemSelect(i2);
                ScreenSaverController.this.screenSaverAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setScreensaverState(boolean z) {
        Log.d("ScreenSaverController", "setScreensaverState111: " + z);
        this.isScreensaverOn = z;
        this.mScreenSaverSwitch.setChecked(z);
        updateScreenSaver(this.isScreensaverOn);
    }

    public void initScreensaverTime(int i) {
        Log.d("ScreenSaverController", "initScreensaverTime: " + i);
        setScreensaverTime(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTimeSelectPop() {
        this.mContentView = LayoutInflater.from(this.mContext).inflate(R$layout.screen_saver_time_select_dialog, (ViewGroup) null);
        if (this.popupWindow == null) {
            this.popupWindow = new PopupWindow(this.mContentView, 200, 320, false);
        }
        this.popupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.popupWindow.setFocusable(true);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.update();
        this.popupWindow.showAsDropDown(this.mTimeState);
        initPopClickListener();
    }

    private void initPopClickListener() {
        this.mTime30 = (TextView) this.mContentView.findViewById(R$id.screen_saver_time_30);
        this.mTime120 = (TextView) this.mContentView.findViewById(R$id.screen_saver_time_120);
        this.mTime300 = (TextView) this.mContentView.findViewById(R$id.screen_saver_time_300);
        this.mTime600 = (TextView) this.mContentView.findViewById(R$id.screen_saver_time_600);
        this.mTime1800 = (TextView) this.mContentView.findViewById(R$id.screen_saver_time_1800);
        this.mTime30.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScreenSaverController.timePosition = 0;
                ScreenSaverController.this.setTimeSelect(0);
            }
        });
        this.mTime120.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScreenSaverController.timePosition = 1;
                ScreenSaverController.this.setTimeSelect(1);
            }
        });
        this.mTime300.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScreenSaverController.timePosition = 2;
                ScreenSaverController.this.setTimeSelect(2);
            }
        });
        this.mTime600.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScreenSaverController.timePosition = 3;
                ScreenSaverController.this.setTimeSelect(3);
            }
        });
        this.mTime1800.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScreenSaverController.timePosition = 4;
                ScreenSaverController.this.setTimeSelect(4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimeSelect(int i) {
        IpcObj.getInstance().setCmd(0, 177, 1, getAutoTime(i));
        setScreensaverTime(getAutoTime(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScreenSaver(boolean z) {
        RelativeLayout relativeLayout = this.mScreenSaverTime;
        if (relativeLayout != null) {
            relativeLayout.setVisibility(z ? 0 : 8);
        }
        RecyclerView recyclerView = this.mScreenSaverGrid;
        if (recyclerView != null) {
            recyclerView.setVisibility(z ? 0 : 8);
        }
        setScreensaverTime(getAutoTime(timePosition));
        IpcObj.getInstance().setCmd(0, 177, 2, z ? 1 : 0);
    }

    private void initScreenSaver(boolean z) {
        RelativeLayout relativeLayout = this.mScreenSaverTime;
        if (relativeLayout != null) {
            relativeLayout.setVisibility(z ? 0 : 8);
        }
        RecyclerView recyclerView = this.mScreenSaverGrid;
        if (recyclerView != null) {
            recyclerView.setVisibility(z ? 0 : 8);
        }
    }

    public void setScreensaverTime(int i) {
        Resources resources = this.mContext.getResources();
        int i2 = R$string.screen_saver_time_30;
        String string = resources.getString(i2);
        if (i == 60) {
            timePosition = 0;
            string = this.mContext.getResources().getString(i2);
        } else if (i == 180) {
            timePosition = 1;
            string = this.mContext.getResources().getString(R$string.screen_saver_time_120);
        } else if (i == 300) {
            timePosition = 2;
            string = this.mContext.getResources().getString(R$string.screen_saver_time_300);
        } else if (i == 600) {
            timePosition = 3;
            string = this.mContext.getResources().getString(R$string.screen_saver_time_600);
        } else if (i == 1200) {
            timePosition = 4;
            string = this.mContext.getResources().getString(R$string.screen_saver_time_1800);
        }
        this.mTimeState.setText(string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ScreenSaverAdapter extends RecyclerView.Adapter<Holder> {
        private OnItemClickListener listener;
        private Context mContext;
        private List<Drawable> mData;
        private int mSelectItem = -1;

        public ScreenSaverAdapter(Context context, List<Drawable> list) {
            this.mContext = context;
            this.mData = list;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.item_screen_saver, viewGroup, false);
            Holder holder = new Holder(inflate);
            holder.mLayout = (RelativeLayout) inflate.findViewById(R$id.screen_item_ly);
            holder.imageView = (ImageView) inflate.findViewById(R$id.img);
            return holder;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mData.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(Holder holder, final int i) {
            RelativeLayout.LayoutParams layoutParams;
            if (i == this.mSelectItem) {
                holder.mLayout.setBackground(this.mContext.getResources().getDrawable(R$drawable.select_bk_no_corner));
            } else {
                holder.mLayout.setBackground(null);
            }
            if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 240) {
                int i2 = SettingsApplication.mWidthFix;
                layoutParams = new RelativeLayout.LayoutParams((int) (((i2 * 0.6d) - 270.0d) / 4.0d), (int) ((((i2 * 0.6d) - 270.0d) / 4.0d) * 0.6d));
            } else {
                int i3 = SettingsApplication.mWidthFix;
                layoutParams = new RelativeLayout.LayoutParams((int) (((i3 * 0.6d) - 180.0d) / 4.0d), (int) ((((i3 * 0.6d) - 180.0d) / 4.0d) * 0.6d));
            }
            layoutParams.addRule(13);
            holder.imageView.setLayoutParams(layoutParams);
            holder.imageView.setImageDrawable(this.mData.get(i));
            holder.mLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.syudisplay.ScreenSaverController.ScreenSaverAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ScreenSaverAdapter.this.listener != null) {
                        ScreenSaverAdapter.this.listener.onItemClick(i);
                    }
                }
            });
        }

        public void setOnItemClickLIstener(OnItemClickListener onItemClickListener) {
            this.listener = onItemClickListener;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Holder extends RecyclerView.ViewHolder {
            ImageView imageView;
            RelativeLayout mLayout;

            public Holder(View view) {
                super(view);
            }
        }

        public void setItemSelect(int i) {
            this.mSelectItem = i;
        }
    }
}
