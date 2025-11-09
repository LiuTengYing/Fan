package com.android.settings.system.update;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.utils.CustomToast;
import com.android.settings.widget.view.TextSeekBarCenter;
import com.syu.systemupdate.aidl.NewVersionInfo;
import com.syu.systemupdate.aidl.UpdateStatusListener;
import org.json.JSONArray;
import org.json.JSONException;
/* loaded from: classes.dex */
public class UpdateFragment extends DashboardFragment {
    static final int SHOW_MENU = 1;
    private TextView mAppUpdate;
    private LinearLayout mBtnLayout;
    private AnimationDrawable mCheckAnim;
    private ImageView mCheckImg;
    private ImageView mCheckLoadingImg;
    private TextView mCheckNewVersion;
    private TextView mCurrentVersion;
    private TextView mDownloadState;
    private TextView mLastVersionNoti;
    private AnimationDrawable mMapAnim;
    private ImageView mMapViewImg;
    private TextView mNewVersion;
    private RelativeLayout mNewVersionLayout;
    private TextView mNewVersionLog;
    private TextView mNewVersionSize;
    private TextView mNewVersionTv;
    private TextSeekBarCenter mProgress;
    private LinearLayout mProgressLayout;
    private View mRootView;
    private TextView mSystemUpdate;
    private RelativeLayout mUpdateLayout;
    private TextView mUpgradeBtn;
    private TextView mUpgradeProgress;
    private NewVersionInfo newVersionInfo;
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.system.update.UpdateFragment.7
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == UpdateContans.UPDATE_CHECK_FAILED) {
                UpdateFragment.this.stopLoadingAnim();
                UpdateFragment.this.updateErrorMsg(((Integer) message.obj).intValue());
                UpdateFragment.this.setTypeEnable(true);
            } else if (i == UpdateContans.UPDATE_CHECK_SUCCESS) {
                UpdateFragment.this.stopLoadingAnim();
                NewVersionInfo newVersionInfo = (NewVersionInfo) message.obj;
                long j = newVersionInfo.fileSize;
                UpdateFragment.this.showNewVersion(newVersionInfo);
                UpdateFragment.this.setTypeEnable(true);
                Log.d("UpdateFragment", "handleMessage: " + j);
            } else if (i == UpdateContans.UPDATE_DOWNLOAD_PROGRESS) {
                UpdateFragment.this.updateDownloadProgress(((Integer) message.obj).intValue(), true);
            } else if (i == UpdateContans.UPDATE_DOWNLOAD_SUCCESS) {
                UpdateFragment.this.showInstallBtn();
            } else if (i == UpdateContans.UPGRADE_PROGRESS) {
                UpdateFragment.this.updateDownloadProgress(((Integer) message.obj).intValue(), false);
            } else if (i == UpdateContans.UPDATE_INSTALL_SUCCESS) {
                Log.d("UpdateFragment", "UPDATE_INSTALL_SUCCESS");
                UpdateFragment.this.showRebootBtn();
            } else if (i == UpdateContans.UPDATE_DOWNLOAD_FAILED) {
                UpdateFragment.this.showDownloadBtn();
                UpdateFragment.this.updateErrorMsg(((Integer) message.obj).intValue());
            }
        }
    };
    private UpdateRebootListener updateRebootListener = new UpdateRebootListener() { // from class: com.android.settings.system.update.UpdateFragment.8
        @Override // com.android.settings.system.update.UpdateRebootListener
        public void onCancelClick() {
            UpdateFragment.this.showRebootBtn();
        }
    };
    private UpdateStatusListener updateStatusListener = new UpdateStatusListener.Stub() { // from class: com.android.settings.system.update.UpdateFragment.9
        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void basicTypes(int i, long j, boolean z, float f, double d, String str) throws RemoteException {
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void onCheckSucess(NewVersionInfo newVersionInfo) throws RemoteException {
            if (newVersionInfo != null) {
                Log.d("UpdateFragment", "onCheckSucess: " + newVersionInfo.fileSize);
                Message message = new Message();
                message.what = UpdateContans.UPDATE_CHECK_SUCCESS;
                message.obj = newVersionInfo;
                UpdateFragment.this.handler.sendMessageDelayed(message, 1500L);
                return;
            }
            Log.d("UpdateFragment", "onCheckSucess: versionInfo is null");
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void onCheckFailed(int i) throws RemoteException {
            Log.d("UpdateFragment", "onCheckFailed: " + i);
            Message message = new Message();
            message.what = UpdateContans.UPDATE_CHECK_FAILED;
            message.obj = Integer.valueOf(i);
            UpdateFragment.this.handler.sendMessageDelayed(message, 1000L);
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void updateDownloadProgress(int i) throws RemoteException {
            Log.d("UpdateFragment", "updateDownloadProgress: " + i);
            Message message = new Message();
            message.what = UpdateContans.UPDATE_DOWNLOAD_PROGRESS;
            message.obj = Integer.valueOf(i);
            UpdateFragment.this.handler.sendMessageDelayed(message, 100L);
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void onDownloadSuccess() throws RemoteException {
            Log.d("UpdateFragment", "onDownloadSuccess: ");
            Message message = new Message();
            message.what = UpdateContans.UPDATE_DOWNLOAD_SUCCESS;
            UpdateFragment.this.handler.sendMessageDelayed(message, 500L);
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void onDownloadFailed(int i) throws RemoteException {
            Log.d("UpdateFragment", "onDownloadFailed: " + i);
            Message message = new Message();
            message.obj = Integer.valueOf(i);
            message.what = UpdateContans.UPDATE_DOWNLOAD_FAILED;
            UpdateFragment.this.handler.sendMessageDelayed(message, 100L);
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void enterRecoveryFail(int i) throws RemoteException {
            Log.d("UpdateFragment", "enterRecoveryFail: " + i);
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void upgradeProgress(int i) throws RemoteException {
            Log.d("UpdateFragment", "upgradeProgress: " + i);
            Message message = new Message();
            message.what = UpdateContans.UPGRADE_PROGRESS;
            message.obj = Integer.valueOf(i);
            UpdateFragment.this.handler.sendMessageDelayed(message, 100L);
        }

        @Override // com.syu.systemupdate.aidl.UpdateStatusListener
        public void upgradeSuccess() throws RemoteException {
            Log.d("UpdateFragment", "upgradeSuccess: ");
            Message message = new Message();
            message.what = UpdateContans.UPDATE_INSTALL_SUCCESS;
            UpdateFragment.this.handler.sendMessageDelayed(message, 500L);
        }
    };
    private UpdateSettingListener listener = new UpdateSettingListener() { // from class: com.android.settings.system.update.UpdateFragment.10
        @Override // com.android.settings.system.update.UpdateSettingListener
        public void onWifiDownload(boolean z) {
            SettingsApplication.mApplication.setWifiDownload(z);
        }

        @Override // com.android.settings.system.update.UpdateSettingListener
        public void slientDownload(boolean z) {
            SettingsApplication.mApplication.setSlientDownload(z);
        }

        @Override // com.android.settings.system.update.UpdateSettingListener
        public void setCheckTime(int i) {
            SettingsApplication.mApplication.setCheckTime(i);
        }

        @Override // com.android.settings.system.update.UpdateSettingListener
        public void clearData() {
            if ("upgrading".contains(SettingsApplication.mApplication.getStatus()) || "downloading".contains(SettingsApplication.mApplication.getStatus())) {
                CustomToast.showCustomToast(UpdateFragment.this.getPrefContext(), UpdateFragment.this.getPrefContext().getResources().getString(R$string.update_clear_warning_toast));
                return;
            }
            CustomToast.showCustomToast(UpdateFragment.this.getPrefContext(), UpdateFragment.this.getPrefContext().getResources().getString(R$string.update_clear_data_complete));
            SettingsApplication.mApplication.clearData();
            UpdateFragment.this.backPress();
        }

        @Override // com.android.settings.system.update.UpdateSettingListener
        public boolean getWifiDownload() {
            return SettingsApplication.mApplication.getWifiDownload();
        }

        @Override // com.android.settings.system.update.UpdateSettingListener
        public boolean getSilentDownload() {
            return SettingsApplication.mApplication.getSilentUpdate();
        }

        @Override // com.android.settings.system.update.UpdateSettingListener
        public int getCheckTime() {
            return SettingsApplication.mApplication.getCheckTime();
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "UpdateFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R$layout.layout_fragment_update, viewGroup, false);
        initViews();
        initListener();
        initData();
        return this.mRootView;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("UpdateFragment", "onAttach: " + hashCode());
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R$string.fix_connectivity);
        add.setIcon(R$drawable.update_more_settings);
        add.setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            showMoreSettings();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showMoreSettings() {
        UpdateMoreSettingsFragment.show(this, "");
        UpdateMoreSettingsFragment.setUpdateListener(this.listener);
    }

    private void initViews() {
        this.mMapViewImg = (ImageView) this.mRootView.findViewById(R$id.update_map_img);
        this.mCurrentVersion = (TextView) this.mRootView.findViewById(R$id.update_version_tv);
        this.mCheckImg = (ImageView) this.mRootView.findViewById(R$id.update_loading);
        this.mSystemUpdate = (TextView) this.mRootView.findViewById(R$id.update_sys_btn);
        this.mAppUpdate = (TextView) this.mRootView.findViewById(R$id.update_app_btn);
        this.mCheckLoadingImg = (ImageView) this.mRootView.findViewById(R$id.update_loading_anim);
        this.mNewVersion = (TextView) this.mRootView.findViewById(R$id.update_new_version);
        this.mCheckNewVersion = (TextView) this.mRootView.findViewById(R$id.update_check_new_version_tv);
        this.mUpdateLayout = (RelativeLayout) this.mRootView.findViewById(R$id.update_layout);
        this.mNewVersionLayout = (RelativeLayout) this.mRootView.findViewById(R$id.new_version_layout);
        this.mNewVersionTv = (TextView) this.mRootView.findViewById(R$id.update_new_version_tv);
        this.mNewVersionSize = (TextView) this.mRootView.findViewById(R$id.update_new_version_size);
        this.mNewVersionLog = (TextView) this.mRootView.findViewById(R$id.update_new_version_log);
        this.mUpgradeProgress = (TextView) this.mRootView.findViewById(R$id.update_new_version_progress);
        this.mProgress = (TextSeekBarCenter) this.mRootView.findViewById(R$id.update_progress);
        this.mUpgradeBtn = (TextView) this.mRootView.findViewById(R$id.update_new_version_upgrade_btn);
        this.mProgressLayout = (LinearLayout) this.mRootView.findViewById(R$id.update_new_version_progress_layout);
        this.mDownloadState = (TextView) this.mRootView.findViewById(R$id.update_progress_state);
        this.mLastVersionNoti = (TextView) this.mRootView.findViewById(R$id.last_version_noti);
        this.mBtnLayout = (LinearLayout) this.mRootView.findViewById(R$id.update_btn_layout);
        if (TextUtils.isEmpty(SystemProperties.get("ro.lsec.fota.version", ""))) {
            this.mBtnLayout.setVisibility(8);
        }
        this.mProgress.setTouchAble(false);
        AnimationDrawable animationDrawable = (AnimationDrawable) this.mMapViewImg.getBackground();
        this.mMapAnim = animationDrawable;
        animationDrawable.start();
        this.mCheckAnim = (AnimationDrawable) this.mCheckLoadingImg.getBackground();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
    }

    private void initListener() {
        this.mCheckImg.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateFragment.this.startLoadingAnim();
                SettingsApplication.mApplication.startCheck();
                UpdateFragment.this.setTypeEnable(false);
            }
        });
        this.mCheckLoadingImg.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateFragment.this.stopLoadingAnim();
            }
        });
        this.mSystemUpdate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateFragment.this.setSystemUpdateCheck(true);
                UpdateFragment.this.resetCheckState();
                SettingsApplication.mApplication.setProductId(0);
            }
        });
        this.mAppUpdate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateFragment.this.setSystemUpdateCheck(false);
                UpdateFragment.this.resetCheckState();
                SettingsApplication.mApplication.setProductId(1);
            }
        });
        this.mNewVersion.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UpdateFragment.this.showNewVersionView();
            }
        });
        this.mUpgradeBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.system.update.UpdateFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if ("download_finish".contains(UpdateFragment.this.getStatus()) || "upgrade_failed".contains(UpdateFragment.this.getStatus())) {
                    UpdateFragment.this.startInstallSystem();
                    UpdateFragment.this.showUpgradeProgress();
                    UpdateFragment.this.setBtnEnable(false);
                } else if ("need_reboot".contains(UpdateFragment.this.getStatus())) {
                    UpdateFragment.this.showRebootDialog();
                } else if ("downloading".contains(UpdateFragment.this.getStatus())) {
                    SettingsApplication.mApplication.cancelDownload();
                    UpdateFragment.this.showDownloadBtn();
                } else {
                    UpdateFragment.this.setSystemUpdateCheck(false);
                    SettingsApplication.mApplication.startDownload();
                    UpdateFragment.this.showUpgradeProgress();
                    UpdateFragment.this.showCancelDownload();
                }
            }
        });
    }

    private void initData() {
        String str = SystemProperties.get("ro.fota.version", "");
        if (!TextUtils.isEmpty(str)) {
            TextView textView = this.mCurrentVersion;
            textView.setText(getContext().getResources().getString(R$string.update_current_version) + str);
        }
        registerUpdateListener();
        setSystemUpdateCheck(true);
        Log.d("UpdateFragment", "initData: " + getStatus());
        try {
            if (!"upgrading".contains(getStatus()) && !"downloading".contains(getStatus())) {
                if ("download_finish".contains(getStatus()) || "need_reboot".contains(getStatus()) || "upgrade_pause".contains(getStatus()) || "download_pause".contains(getStatus()) || "download_failed".contains(getStatus()) || "upgrade_failed".contains(getStatus())) {
                    showNewVersion(SettingsApplication.mApplication.getNewVersionInfo());
                    showNewVersionView();
                }
            }
            showNewVersion(SettingsApplication.mApplication.getNewVersionInfo());
            showNewVersionView();
            showUpgradeProgress();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UpdateFragment", "onServiceConnected: " + e.toString());
        }
    }

    private void registerUpdateListener() {
        SettingsApplication.mApplication.registerUpdateListener(this.updateStatusListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUpgradeProgress() {
        this.mProgressLayout.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDownloadProgress(int i, boolean z) {
        this.mDownloadState.setText(z ? getPrefContext().getResources().getString(R$string.update_downloading) : getPrefContext().getResources().getString(R$string.update_upgrading));
        TextView textView = this.mUpgradeProgress;
        textView.setText(i + "%");
        this.mProgress.setProgress(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showRebootDialog() {
        UpdateRebootConfirmFragment.show(this, "");
        UpdateRebootConfirmFragment.setBtnClickListener(this.updateRebootListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDownloadBtn() {
        setBtnEnable(true);
        this.mUpgradeBtn.setText(SettingsApplication.mResources.getString(R$string.update_download_start));
        this.mProgressLayout.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showRebootBtn() {
        setBtnEnable(true);
        this.mUpgradeBtn.setText(SettingsApplication.mResources.getString(R$string.update_need_reboot));
        this.mProgressLayout.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCancelDownload() {
        this.mUpgradeBtn.setText(SettingsApplication.mResources.getString(R$string.update_download_cancel_btn));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNewVersion(NewVersionInfo newVersionInfo) {
        this.mCheckLoadingImg.setVisibility(4);
        this.mCheckImg.setVisibility(4);
        this.mCheckNewVersion.setVisibility(4);
        this.newVersionInfo = newVersionInfo;
        this.mNewVersion.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateErrorMsg(int i) {
        if (i != -2) {
            if (i == 2101) {
                this.mLastVersionNoti.setVisibility(0);
                return;
            } else if (i != 3003) {
                Log.d("UpdateFragment", "updateErrorMsg: " + i);
                return;
            }
        }
        CustomToast.showCustomToast(getPrefContext(), getPrefContext().getResources().getString(R$string.update_error_msg_no_network));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showInstallBtn() {
        this.mUpgradeBtn.setVisibility(0);
        this.mUpgradeBtn.setText(SettingsApplication.mResources.getString(R$string.update_install_start));
        this.mProgressLayout.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getStatus() {
        String status = SettingsApplication.mApplication.getStatus();
        Log.d("UpdateFragment", "getStatus: " + status);
        return status;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startLoadingAnim() {
        this.mCheckImg.setVisibility(4);
        this.mCheckNewVersion.setVisibility(4);
        if (this.mCheckAnim != null) {
            this.mCheckLoadingImg.setVisibility(0);
            this.mCheckAnim.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInstallSystem() {
        SettingsApplication.mApplication.startUpgrade();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopLoadingAnim() {
        AnimationDrawable animationDrawable = this.mCheckAnim;
        if (animationDrawable != null) {
            animationDrawable.stop();
            this.mCheckLoadingImg.setVisibility(8);
        }
        this.mCheckImg.setVisibility(0);
        this.mCheckNewVersion.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemUpdateCheck(boolean z) {
        this.mSystemUpdate.setSelected(false);
        this.mAppUpdate.setSelected(false);
        if (z) {
            this.mSystemUpdate.setSelected(true);
            this.mAppUpdate.setSelected(false);
            this.mSystemUpdate.setClickable(false);
            this.mAppUpdate.setClickable(true);
            return;
        }
        this.mSystemUpdate.setSelected(false);
        this.mAppUpdate.setSelected(true);
        this.mSystemUpdate.setClickable(true);
        this.mAppUpdate.setClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTypeEnable(boolean z) {
        this.mAppUpdate.setClickable(z);
        this.mSystemUpdate.setClickable(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetCheckState() {
        this.mCheckLoadingImg.setVisibility(4);
        this.mCheckImg.setVisibility(0);
        this.mCheckNewVersion.setVisibility(0);
        this.mNewVersion.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNewVersionView() {
        AnimationDrawable animationDrawable = this.mMapAnim;
        if (animationDrawable != null) {
            if (animationDrawable.isRunning()) {
                this.mMapAnim.stop();
            }
            this.mMapAnim.setVisible(false, false);
        }
        this.mUpdateLayout.setVisibility(8);
        this.mNewVersionLayout.setVisibility(0);
        NewVersionInfo newVersionInfo = this.newVersionInfo;
        if (newVersionInfo != null) {
            this.mNewVersionTv.setText(newVersionInfo.versionName);
            this.mNewVersionSize.setText(Formatter.formatFileSize(getContext(), this.newVersionInfo.fileSize));
            this.mNewVersionLog.setText(getContentMsg(this.newVersionInfo.content));
        }
        if ("download_finish".contains(getStatus()) || "upgrade_pause".contains(getStatus()) || "upgrade_failed".contains(getStatus())) {
            showInstallBtn();
        } else if ("need_reboot".contains(getStatus())) {
            showRebootBtn();
        } else if ("download_pause".contains(getStatus())) {
            showDownloadBtn();
        } else if ("download_failed".contains(getStatus())) {
            showDownloadBtn();
        } else if ("downloading".contains(getStatus())) {
            showCancelDownload();
        } else if ("upgrading".contains(getStatus())) {
            showInstallBtn();
            setBtnEnable(false);
        }
    }

    private String getContentMsg(String str) {
        try {
            return new JSONArray(str).getJSONObject(0).getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        Log.d("UpdateFragment", "onStop: ");
        AnimationDrawable animationDrawable = this.mMapAnim;
        if (animationDrawable != null && animationDrawable.isRunning()) {
            this.mMapAnim.stop();
        }
        AnimationDrawable animationDrawable2 = this.mCheckAnim;
        if (animationDrawable2 != null && animationDrawable2.isRunning()) {
            this.mCheckAnim.stop();
        }
        UpdateStatusListener updateStatusListener = this.updateStatusListener;
        if (updateStatusListener != null) {
            SettingsApplication.mApplication.unregisterUpdateListener(updateStatusListener);
        }
        super.onStop();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        Toolbar toolbar;
        FragmentActivity activity = getActivity();
        if ((activity instanceof SettingsBaseActivity) && (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) != null) {
            toolbar.setTitle(getResources().getString(R$string.system_update_title));
        }
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void backPress() {
        FragmentActivity activity = getActivity();
        if (activity instanceof SettingsBaseActivity) {
            activity.onBackPressed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBtnEnable(boolean z) {
        this.mUpgradeBtn.setEnabled(z);
        this.mUpgradeBtn.setAlpha(z ? 1.0f : 0.4f);
    }
}
