package com.android.settings.display;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes.dex */
public class VideoEnhanceActivity extends CollapsingToolbarBaseActivity {
    private ActivityManager mActivityManager;
    private ApplicationAdapter mAdapter;
    private ListView mApplicationAppList;
    private List<String> mDisplayApplicationAppArray;
    private ImageView mImageView;
    private List<ApplicationInfo> mInstall;
    private final SwitchBar.OnSwitchChangeListener mOnSwitchClickListener = new SwitchBar.OnSwitchChangeListener() { // from class: com.android.settings.display.VideoEnhanceActivity$$ExternalSyntheticLambda0
        @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
        public final void onSwitchChanged(Switch r1, boolean z) {
            VideoEnhanceActivity.this.lambda$new$0(r1, z);
        }
    };
    private List<ApplicationInfo> mPackageInfoList;
    private PackageManager mPackageManager;
    private SwitchBar mSwitchBar;

    @Override // com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.video_enhance);
        setTitle(R$string.video_enhance);
        SwitchBar switchBar = (SwitchBar) findViewById(R$id.switch_bar);
        this.mSwitchBar = switchBar;
        switchBar.addOnSwitchChangeListener(this.mOnSwitchClickListener);
        this.mImageView = (ImageView) findViewById(R$id.image_view);
        int i = R$id.application_list;
        this.mApplicationAppList = (ListView) findViewById(i);
        this.mPackageManager = getPackageManager();
        this.mPackageInfoList = new ArrayList();
        this.mActivityManager = (ActivityManager) getSystemService(ActivityManager.class);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        this.mAdapter = new ApplicationAdapter(this, R$layout.app_list_info, this.mPackageInfoList);
        ListView listView = (ListView) findViewById(i);
        this.mApplicationAppList = listView;
        listView.setAdapter((ListAdapter) this.mAdapter);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mSwitchBar.setEnabled(!isInMultiWindowMode());
        this.mAdapter.clear();
        bindSwitch();
        displayApplicationApp();
        this.mAdapter.notifyDataSetChanged();
    }

    private void bindSwitch() {
        SwitchBar switchBar = this.mSwitchBar;
        if (switchBar != null) {
            switchBar.setSwitchBarText(R$string.video_enhance_on, R$string.video_enhance_off);
            this.mSwitchBar.show();
            setSwichBar(Settings.Global.getInt(getContentResolver(), "video_enhance_enable", 0) == 1);
        }
    }

    private void setSwichBar(boolean z) {
        this.mSwitchBar.setChecked(z);
        this.mImageView.setImageResource(z ? R$drawable.video_enhance_on : R$drawable.video_enhance_off);
    }

    private void displayApplicationApp() {
        this.mInstall = this.mPackageManager.getInstalledApplications(0);
        this.mDisplayApplicationAppArray = Arrays.asList(getResources().getStringArray(134283320));
        new HashSet(this.mDisplayApplicationAppArray);
        for (int i = 0; i < this.mInstall.size(); i++) {
            ApplicationInfo applicationInfo = this.mInstall.get(i);
            if (this.mDisplayApplicationAppArray.contains(applicationInfo.packageName)) {
                this.mPackageInfoList.add(applicationInfo);
            }
        }
    }

    /* loaded from: classes.dex */
    public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
        private List<ApplicationInfo> mApplicationInfoList;
        private int mLayoutResId;

        public ApplicationAdapter(Context context, int i, List<ApplicationInfo> list) {
            super(context, i, list);
            this.mLayoutResId = i;
            this.mApplicationInfoList = list;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(this.mLayoutResId, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.appImage = (ImageView) view.findViewById(R$id.image_view_on);
                viewHolder.appText = (TextView) view.findViewById(R$id.image_view_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ApplicationInfo applicationInfo = this.mApplicationInfoList.get(i);
            viewHolder.appImage.setImageDrawable(applicationInfo.loadIcon(VideoEnhanceActivity.this.mPackageManager));
            viewHolder.appText.setText(applicationInfo.loadLabel(VideoEnhanceActivity.this.mPackageManager).toString());
            return view;
        }

        /* loaded from: classes.dex */
        class ViewHolder {
            ImageView appImage;
            TextView appText;

            ViewHolder() {
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
        this.mSwitchBar.setEnabled(!z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Switch r2, boolean z) {
        Settings.Global.putInt(getContentResolver(), "video_enhance_enable", z ? 1 : 0);
        this.mImageView.setImageResource(z ? R$drawable.video_enhance_on : R$drawable.video_enhance_off);
    }
}
