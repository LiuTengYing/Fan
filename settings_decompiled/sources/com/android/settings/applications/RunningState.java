package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import com.android.settings.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.InterestingConfigChanges;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
/* loaded from: classes.dex */
public class RunningState {
    static Object sGlobalLock = new Object();
    static RunningState sInstance;
    final ActivityManager mAm;
    final Context mApplicationContext;
    final BackgroundHandler mBackgroundHandler;
    long mBackgroundProcessMemory;
    final HandlerThread mBackgroundThread;
    long mForegroundProcessMemory;
    boolean mHaveData;
    final boolean mHideManagedProfiles;
    final int mMyUserId;
    int mNumBackgroundProcesses;
    int mNumForegroundProcesses;
    int mNumServiceProcesses;
    final PackageManager mPm;
    OnRefreshUiListener mRefreshUiListener;
    boolean mResumed;
    long mServiceProcessMemory;
    final UserManager mUm;
    private final UserManagerBroadcastReceiver mUmBroadcastReceiver;
    boolean mWatchingBackgroundItems;
    final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
    final SparseArray<HashMap<String, ProcessItem>> mServiceProcessesByName = new SparseArray<>();
    final SparseArray<ProcessItem> mServiceProcessesByPid = new SparseArray<>();
    final ServiceProcessComparator mServiceProcessComparator = new ServiceProcessComparator();
    final ArrayList<ProcessItem> mInterestingProcesses = new ArrayList<>();
    final SparseArray<ProcessItem> mRunningProcesses = new SparseArray<>();
    final ArrayList<ProcessItem> mProcessItems = new ArrayList<>();
    final ArrayList<ProcessItem> mAllProcessItems = new ArrayList<>();
    final SparseArray<MergedItem> mOtherUserMergedItems = new SparseArray<>();
    final SparseArray<MergedItem> mOtherUserBackgroundItems = new SparseArray<>();
    final SparseArray<AppProcessInfo> mTmpAppProcesses = new SparseArray<>();
    int mSequence = 0;
    final Comparator<MergedItem> mBackgroundComparator = new Comparator<MergedItem>() { // from class: com.android.settings.applications.RunningState.1
        @Override // java.util.Comparator
        public int compare(MergedItem mergedItem, MergedItem mergedItem2) {
            int i = mergedItem.mUserId;
            int i2 = mergedItem2.mUserId;
            if (i != i2) {
                int i3 = RunningState.this.mMyUserId;
                if (i == i3) {
                    return -1;
                }
                return (i2 != i3 && i < i2) ? -1 : 1;
            }
            ProcessItem processItem = mergedItem.mProcess;
            ProcessItem processItem2 = mergedItem2.mProcess;
            if (processItem == processItem2) {
                String str = mergedItem.mLabel;
                String str2 = mergedItem2.mLabel;
                if (str == str2) {
                    return 0;
                }
                if (str != null) {
                    return str.compareTo(str2);
                }
                return -1;
            } else if (processItem == null) {
                return -1;
            } else {
                if (processItem2 == null) {
                    return 1;
                }
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = processItem.mRunningProcessInfo;
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo2 = processItem2.mRunningProcessInfo;
                boolean z = runningAppProcessInfo.importance >= 400;
                if (z != (runningAppProcessInfo2.importance >= 400)) {
                    return z ? 1 : -1;
                }
                boolean z2 = (runningAppProcessInfo.flags & 4) != 0;
                if (z2 != ((runningAppProcessInfo2.flags & 4) != 0)) {
                    return z2 ? -1 : 1;
                }
                int i4 = runningAppProcessInfo.lru;
                int i5 = runningAppProcessInfo2.lru;
                if (i4 != i5) {
                    return i4 < i5 ? -1 : 1;
                }
                String str3 = processItem.mLabel;
                String str4 = processItem2.mLabel;
                if (str3 == str4) {
                    return 0;
                }
                if (str3 == null) {
                    return 1;
                }
                if (str4 == null) {
                    return -1;
                }
                return str3.compareTo(str4);
            }
        }
    };
    final Object mLock = new Object();
    ArrayList<BaseItem> mItems = new ArrayList<>();
    ArrayList<MergedItem> mMergedItems = new ArrayList<>();
    ArrayList<MergedItem> mBackgroundItems = new ArrayList<>();
    ArrayList<MergedItem> mUserBackgroundItems = new ArrayList<>();
    final Handler mHandler = new Handler() { // from class: com.android.settings.applications.RunningState.2
        int mNextUpdate = 0;

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 3) {
                this.mNextUpdate = message.arg1 != 0 ? 2 : 1;
            } else if (i != 4) {
            } else {
                synchronized (RunningState.this.mLock) {
                    if (RunningState.this.mResumed) {
                        removeMessages(4);
                        sendMessageDelayed(obtainMessage(4), 1000L);
                        OnRefreshUiListener onRefreshUiListener = RunningState.this.mRefreshUiListener;
                        if (onRefreshUiListener != null) {
                            onRefreshUiListener.onRefreshUi(this.mNextUpdate);
                            this.mNextUpdate = 0;
                        }
                    }
                }
            }
        }
    };

    /* loaded from: classes.dex */
    interface OnRefreshUiListener {
        void onRefreshUi(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AppProcessInfo {
        boolean hasForegroundServices;
        boolean hasServices;
        final ActivityManager.RunningAppProcessInfo info;

        AppProcessInfo(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
            this.info = runningAppProcessInfo;
        }
    }

    /* loaded from: classes.dex */
    final class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                RunningState.this.reset();
            } else if (i != 2) {
            } else {
                synchronized (RunningState.this.mLock) {
                    RunningState runningState = RunningState.this;
                    if (runningState.mResumed) {
                        Message obtainMessage = runningState.mHandler.obtainMessage(3);
                        RunningState runningState2 = RunningState.this;
                        obtainMessage.arg1 = runningState2.update(runningState2.mApplicationContext, runningState2.mAm) ? 1 : 0;
                        RunningState.this.mHandler.sendMessage(obtainMessage);
                        removeMessages(2);
                        sendMessageDelayed(obtainMessage(2), 2000L);
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private final class UserManagerBroadcastReceiver extends BroadcastReceiver {
        private volatile boolean usersChanged;

        private UserManagerBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (RunningState.this.mLock) {
                RunningState runningState = RunningState.this;
                if (runningState.mResumed) {
                    runningState.mHaveData = false;
                    runningState.mBackgroundHandler.removeMessages(1);
                    RunningState.this.mBackgroundHandler.sendEmptyMessage(1);
                    RunningState.this.mBackgroundHandler.removeMessages(2);
                    RunningState.this.mBackgroundHandler.sendEmptyMessage(2);
                } else {
                    this.usersChanged = true;
                }
            }
        }

        public boolean checkUsersChangedLocked() {
            boolean z = this.usersChanged;
            this.usersChanged = false;
            return z;
        }

        void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_STOPPED");
            intentFilter.addAction("android.intent.action.USER_STARTED");
            intentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
            context.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, null, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UserState {
        Drawable mIcon;
        UserInfo mInfo;
        String mLabel;

        UserState() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class BaseItem {
        long mActiveSince;
        boolean mBackground;
        int mCurSeq;
        String mCurSizeStr;
        String mDescription;
        CharSequence mDisplayLabel;
        final boolean mIsProcess;
        String mLabel;
        boolean mNeedDivider;
        PackageItemInfo mPackageInfo;
        long mSize;
        String mSizeStr;
        final int mUserId;

        public BaseItem(boolean z, int i) {
            this.mIsProcess = z;
            this.mUserId = i;
        }

        public Drawable loadIcon(Context context, RunningState runningState) {
            PackageItemInfo packageItemInfo = this.mPackageInfo;
            if (packageItemInfo != null) {
                return runningState.mPm.getUserBadgedIcon(packageItemInfo.loadUnbadgedIcon(runningState.mPm), new UserHandle(this.mUserId));
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ServiceItem extends BaseItem {
        MergedItem mMergedItem;
        ActivityManager.RunningServiceInfo mRunningService;
        ServiceInfo mServiceInfo;
        boolean mShownAsStarted;

        public ServiceItem(int i) {
            super(false, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ProcessItem extends BaseItem {
        long mActiveSince;
        ProcessItem mClient;
        final SparseArray<ProcessItem> mDependentProcesses;
        boolean mInteresting;
        boolean mIsStarted;
        boolean mIsSystem;
        int mLastNumDependentProcesses;
        MergedItem mMergedItem;
        int mPid;
        final String mProcessName;
        ActivityManager.RunningAppProcessInfo mRunningProcessInfo;
        int mRunningSeq;
        final HashMap<ComponentName, ServiceItem> mServices;
        final int mUid;

        public ProcessItem(Context context, int i, String str) {
            super(true, UserHandle.getUserId(i));
            this.mServices = new HashMap<>();
            this.mDependentProcesses = new SparseArray<>();
            this.mDescription = context.getResources().getString(R$string.service_process_name, str);
            this.mUid = i;
            this.mProcessName = str;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void ensureLabel(PackageManager packageManager) {
            CharSequence text;
            if (this.mLabel != null) {
                return;
            }
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mProcessName, 4194304);
                if (applicationInfo.uid == this.mUid) {
                    CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                    this.mDisplayLabel = loadLabel;
                    this.mLabel = loadLabel.toString();
                    this.mPackageInfo = applicationInfo;
                    return;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            String[] packagesForUid = packageManager.getPackagesForUid(this.mUid);
            if (packagesForUid.length == 1) {
                try {
                    ApplicationInfo applicationInfo2 = packageManager.getApplicationInfo(packagesForUid[0], 4194304);
                    CharSequence loadLabel2 = applicationInfo2.loadLabel(packageManager);
                    this.mDisplayLabel = loadLabel2;
                    this.mLabel = loadLabel2.toString();
                    this.mPackageInfo = applicationInfo2;
                    return;
                } catch (PackageManager.NameNotFoundException unused2) {
                }
            }
            for (String str : packagesForUid) {
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
                    int i = packageInfo.sharedUserLabel;
                    if (i != 0 && (text = packageManager.getText(str, i, packageInfo.applicationInfo)) != null) {
                        this.mDisplayLabel = text;
                        this.mLabel = text.toString();
                        this.mPackageInfo = packageInfo.applicationInfo;
                        return;
                    }
                } catch (PackageManager.NameNotFoundException unused3) {
                }
            }
            if (this.mServices.size() > 0) {
                ApplicationInfo applicationInfo3 = this.mServices.values().iterator().next().mServiceInfo.applicationInfo;
                this.mPackageInfo = applicationInfo3;
                CharSequence loadLabel3 = applicationInfo3.loadLabel(packageManager);
                this.mDisplayLabel = loadLabel3;
                this.mLabel = loadLabel3.toString();
                return;
            }
            try {
                ApplicationInfo applicationInfo4 = packageManager.getApplicationInfo(packagesForUid[0], 4194304);
                CharSequence loadLabel4 = applicationInfo4.loadLabel(packageManager);
                this.mDisplayLabel = loadLabel4;
                this.mLabel = loadLabel4.toString();
                this.mPackageInfo = applicationInfo4;
            } catch (PackageManager.NameNotFoundException unused4) {
            }
        }

        boolean updateService(Context context, ActivityManager.RunningServiceInfo runningServiceInfo) {
            boolean z;
            PackageManager packageManager = context.getPackageManager();
            ServiceItem serviceItem = this.mServices.get(runningServiceInfo.service);
            boolean z2 = true;
            if (serviceItem == null) {
                serviceItem = new ServiceItem(this.mUserId);
                serviceItem.mRunningService = runningServiceInfo;
                try {
                    ServiceInfo serviceInfo = ActivityThread.getPackageManager().getServiceInfo(runningServiceInfo.service, 4194304L, UserHandle.getUserId(runningServiceInfo.uid));
                    serviceItem.mServiceInfo = serviceInfo;
                    if (serviceInfo == null) {
                        Log.d("RunningService", "getServiceInfo returned null for: " + runningServiceInfo.service);
                        return false;
                    }
                } catch (RemoteException unused) {
                }
                serviceItem.mDisplayLabel = RunningState.makeLabel(packageManager, serviceItem.mRunningService.service.getClassName(), serviceItem.mServiceInfo);
                CharSequence charSequence = this.mDisplayLabel;
                this.mLabel = charSequence != null ? charSequence.toString() : null;
                serviceItem.mPackageInfo = serviceItem.mServiceInfo.applicationInfo;
                this.mServices.put(runningServiceInfo.service, serviceItem);
                z = true;
            } else {
                z = false;
            }
            serviceItem.mCurSeq = this.mCurSeq;
            serviceItem.mRunningService = runningServiceInfo;
            long j = runningServiceInfo.restarting == 0 ? runningServiceInfo.activeSince : -1L;
            if (serviceItem.mActiveSince != j) {
                serviceItem.mActiveSince = j;
                z = true;
            }
            String str = runningServiceInfo.clientPackage;
            if (str != null && runningServiceInfo.clientLabel != 0) {
                if (serviceItem.mShownAsStarted) {
                    serviceItem.mShownAsStarted = false;
                    z = true;
                }
                try {
                    serviceItem.mDescription = context.getResources().getString(R$string.service_client_name, packageManager.getResourcesForApplication(str).getString(runningServiceInfo.clientLabel));
                    return z;
                } catch (PackageManager.NameNotFoundException unused2) {
                    serviceItem.mDescription = null;
                    return z;
                }
            }
            if (serviceItem.mShownAsStarted) {
                z2 = z;
            } else {
                serviceItem.mShownAsStarted = true;
            }
            serviceItem.mDescription = context.getResources().getString(R$string.service_started_by_app);
            return z2;
        }

        boolean updateSize(Context context, long j, int i) {
            long j2 = j * 1024;
            this.mSize = j2;
            if (this.mCurSeq == i) {
                String formatShortFileSize = Formatter.formatShortFileSize(context, j2);
                if (!formatShortFileSize.equals(this.mSizeStr)) {
                    this.mSizeStr = formatShortFileSize;
                }
            }
            return false;
        }

        boolean buildDependencyChain(Context context, PackageManager packageManager, int i) {
            int size = this.mDependentProcesses.size();
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                ProcessItem valueAt = this.mDependentProcesses.valueAt(i2);
                if (valueAt.mClient != this) {
                    valueAt.mClient = this;
                    z = true;
                }
                valueAt.mCurSeq = i;
                valueAt.ensureLabel(packageManager);
                z |= valueAt.buildDependencyChain(context, packageManager, i);
            }
            if (this.mLastNumDependentProcesses != this.mDependentProcesses.size()) {
                this.mLastNumDependentProcesses = this.mDependentProcesses.size();
                return true;
            }
            return z;
        }

        void addDependentProcesses(ArrayList<BaseItem> arrayList, ArrayList<ProcessItem> arrayList2) {
            int size = this.mDependentProcesses.size();
            for (int i = 0; i < size; i++) {
                ProcessItem valueAt = this.mDependentProcesses.valueAt(i);
                valueAt.addDependentProcesses(arrayList, arrayList2);
                arrayList.add(valueAt);
                if (valueAt.mPid > 0) {
                    arrayList2.add(valueAt);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MergedItem extends BaseItem {
        final ArrayList<MergedItem> mChildren;
        private int mLastNumProcesses;
        private int mLastNumServices;
        final ArrayList<ProcessItem> mOtherProcesses;
        ProcessItem mProcess;
        final ArrayList<ServiceItem> mServices;
        UserState mUser;

        MergedItem(int i) {
            super(false, i);
            this.mOtherProcesses = new ArrayList<>();
            this.mServices = new ArrayList<>();
            this.mChildren = new ArrayList<>();
            this.mLastNumProcesses = -1;
            this.mLastNumServices = -1;
        }

        private void setDescription(Context context, int i, int i2) {
            if (this.mLastNumProcesses == i && this.mLastNumServices == i2) {
                return;
            }
            this.mLastNumProcesses = i;
            this.mLastNumServices = i2;
            int i3 = R$string.running_processes_item_description_s_s;
            if (i != 1) {
                if (i2 != 1) {
                    i3 = R$string.running_processes_item_description_p_p;
                } else {
                    i3 = R$string.running_processes_item_description_p_s;
                }
            } else if (i2 != 1) {
                i3 = R$string.running_processes_item_description_s_p;
            }
            this.mDescription = context.getResources().getString(i3, Integer.valueOf(i), Integer.valueOf(i2));
        }

        boolean update(Context context, boolean z) {
            this.mBackground = z;
            if (this.mUser != null) {
                this.mPackageInfo = this.mChildren.get(0).mProcess.mPackageInfo;
                UserState userState = this.mUser;
                String str = userState != null ? userState.mLabel : null;
                this.mLabel = str;
                this.mDisplayLabel = str;
                this.mActiveSince = -1L;
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < this.mChildren.size(); i3++) {
                    MergedItem mergedItem = this.mChildren.get(i3);
                    i += mergedItem.mLastNumProcesses;
                    i2 += mergedItem.mLastNumServices;
                    long j = mergedItem.mActiveSince;
                    if (j >= 0 && this.mActiveSince < j) {
                        this.mActiveSince = j;
                    }
                }
                if (!this.mBackground) {
                    setDescription(context, i, i2);
                }
            } else {
                ProcessItem processItem = this.mProcess;
                this.mPackageInfo = processItem.mPackageInfo;
                this.mDisplayLabel = processItem.mDisplayLabel;
                this.mLabel = processItem.mLabel;
                if (!z) {
                    setDescription(context, (processItem.mPid > 0 ? 1 : 0) + this.mOtherProcesses.size(), this.mServices.size());
                }
                this.mActiveSince = -1L;
                for (int i4 = 0; i4 < this.mServices.size(); i4++) {
                    long j2 = this.mServices.get(i4).mActiveSince;
                    if (j2 >= 0 && this.mActiveSince < j2) {
                        this.mActiveSince = j2;
                    }
                }
            }
            return false;
        }

        boolean updateSize(Context context) {
            if (this.mUser != null) {
                this.mSize = 0L;
                for (int i = 0; i < this.mChildren.size(); i++) {
                    MergedItem mergedItem = this.mChildren.get(i);
                    mergedItem.updateSize(context);
                    this.mSize += mergedItem.mSize;
                }
            } else {
                this.mSize = this.mProcess.mSize;
                for (int i2 = 0; i2 < this.mOtherProcesses.size(); i2++) {
                    this.mSize += this.mOtherProcesses.get(i2).mSize;
                }
            }
            String formatShortFileSize = Formatter.formatShortFileSize(context, this.mSize);
            if (!formatShortFileSize.equals(this.mSizeStr)) {
                this.mSizeStr = formatShortFileSize;
            }
            return false;
        }

        @Override // com.android.settings.applications.RunningState.BaseItem
        public Drawable loadIcon(Context context, RunningState runningState) {
            UserState userState = this.mUser;
            if (userState == null) {
                return super.loadIcon(context, runningState);
            }
            Drawable drawable = userState.mIcon;
            if (drawable != null) {
                Drawable.ConstantState constantState = drawable.getConstantState();
                if (constantState == null) {
                    return this.mUser.mIcon;
                }
                return constantState.newDrawable();
            }
            return context.getDrawable(17302715);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ServiceProcessComparator implements Comparator<ProcessItem> {
        ServiceProcessComparator() {
        }

        @Override // java.util.Comparator
        public int compare(ProcessItem processItem, ProcessItem processItem2) {
            int i = processItem.mUserId;
            int i2 = processItem2.mUserId;
            if (i != i2) {
                int i3 = RunningState.this.mMyUserId;
                if (i == i3) {
                    return -1;
                }
                return (i2 != i3 && i < i2) ? -1 : 1;
            }
            boolean z = processItem.mIsStarted;
            if (z != processItem2.mIsStarted) {
                return z ? -1 : 1;
            }
            boolean z2 = processItem.mIsSystem;
            if (z2 != processItem2.mIsSystem) {
                return z2 ? 1 : -1;
            }
            long j = processItem.mActiveSince;
            long j2 = processItem2.mActiveSince;
            if (j != j2) {
                return j > j2 ? -1 : 1;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence makeLabel(PackageManager packageManager, String str, PackageItemInfo packageItemInfo) {
        CharSequence loadLabel;
        if (packageItemInfo == null || ((packageItemInfo.labelRes == 0 && packageItemInfo.nonLocalizedLabel == null) || (loadLabel = packageItemInfo.loadLabel(packageManager)) == null)) {
            int lastIndexOf = str.lastIndexOf(46);
            return lastIndexOf >= 0 ? str.substring(lastIndexOf + 1, str.length()) : str;
        }
        return loadLabel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RunningState getInstance(Context context) {
        RunningState runningState;
        synchronized (sGlobalLock) {
            if (sInstance == null) {
                sInstance = new RunningState(context);
            }
            runningState = sInstance;
        }
        return runningState;
    }

    private RunningState(Context context) {
        UserManagerBroadcastReceiver userManagerBroadcastReceiver = new UserManagerBroadcastReceiver();
        this.mUmBroadcastReceiver = userManagerBroadcastReceiver;
        Context applicationContext = context.getApplicationContext();
        this.mApplicationContext = applicationContext;
        this.mAm = (ActivityManager) applicationContext.getSystemService(ActivityManager.class);
        this.mPm = applicationContext.getPackageManager();
        UserManager userManager = (UserManager) applicationContext.getSystemService(UserManager.class);
        this.mUm = userManager;
        int myUserId = UserHandle.myUserId();
        this.mMyUserId = myUserId;
        UserInfo userInfo = userManager.getUserInfo(myUserId);
        this.mHideManagedProfiles = userInfo == null || !userInfo.canHaveProfile();
        this.mResumed = false;
        HandlerThread handlerThread = new HandlerThread("RunningState:Background");
        this.mBackgroundThread = handlerThread;
        handlerThread.start();
        this.mBackgroundHandler = new BackgroundHandler(handlerThread.getLooper());
        userManagerBroadcastReceiver.register(applicationContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resume(OnRefreshUiListener onRefreshUiListener) {
        synchronized (this.mLock) {
            this.mResumed = true;
            this.mRefreshUiListener = onRefreshUiListener;
            boolean checkUsersChangedLocked = this.mUmBroadcastReceiver.checkUsersChangedLocked();
            boolean applyNewConfig = this.mInterestingConfigChanges.applyNewConfig(this.mApplicationContext.getResources());
            if (checkUsersChangedLocked || applyNewConfig) {
                this.mHaveData = false;
                this.mBackgroundHandler.removeMessages(1);
                this.mBackgroundHandler.removeMessages(2);
                this.mBackgroundHandler.sendEmptyMessage(1);
            }
            if (!this.mBackgroundHandler.hasMessages(2)) {
                this.mBackgroundHandler.sendEmptyMessage(2);
            }
            this.mHandler.sendEmptyMessage(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateNow() {
        synchronized (this.mLock) {
            this.mBackgroundHandler.removeMessages(2);
            this.mBackgroundHandler.sendEmptyMessage(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasData() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mHaveData;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForData() {
        synchronized (this.mLock) {
            while (!this.mHaveData) {
                try {
                    this.mLock.wait(0L);
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pause() {
        synchronized (this.mLock) {
            this.mResumed = false;
            this.mRefreshUiListener = null;
            this.mHandler.removeMessages(4);
        }
    }

    private boolean isInterestingProcess(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
        int i;
        int i2 = runningAppProcessInfo.flags;
        if ((i2 & 1) != 0) {
            return true;
        }
        return (i2 & 2) == 0 && (i = runningAppProcessInfo.importance) >= 100 && i < 350 && runningAppProcessInfo.importanceReasonCode == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reset() {
        this.mServiceProcessesByName.clear();
        this.mServiceProcessesByPid.clear();
        this.mInterestingProcesses.clear();
        this.mRunningProcesses.clear();
        this.mProcessItems.clear();
        this.mAllProcessItems.clear();
    }

    private void addOtherUserItem(Context context, ArrayList<MergedItem> arrayList, SparseArray<MergedItem> sparseArray, MergedItem mergedItem) {
        MergedItem mergedItem2 = sparseArray.get(mergedItem.mUserId);
        if (mergedItem2 == null || mergedItem2.mCurSeq != this.mSequence) {
            UserInfo userInfo = this.mUm.getUserInfo(mergedItem.mUserId);
            if (userInfo == null) {
                return;
            }
            if (this.mHideManagedProfiles && userInfo.isManagedProfile()) {
                return;
            }
            if (mergedItem2 == null) {
                mergedItem2 = new MergedItem(mergedItem.mUserId);
                sparseArray.put(mergedItem.mUserId, mergedItem2);
            } else {
                mergedItem2.mChildren.clear();
            }
            mergedItem2.mCurSeq = this.mSequence;
            UserState userState = new UserState();
            mergedItem2.mUser = userState;
            userState.mInfo = userInfo;
            userState.mIcon = Utils.getUserIcon(context, this.mUm, userInfo);
            mergedItem2.mUser.mLabel = Utils.getUserLabel(context, userInfo);
            arrayList.add(mergedItem2);
        }
        mergedItem2.mChildren.add(mergedItem);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:329:0x066f  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x06a4  */
    /* JADX WARN: Removed duplicated region for block: B:356:0x06ee  */
    /* JADX WARN: Removed duplicated region for block: B:360:0x06f8 A[LOOP:25: B:358:0x06f0->B:360:0x06f8, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:379:0x0709 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean update(android.content.Context r26, android.app.ActivityManager r27) {
        /*
            Method dump skipped, instructions count: 1844
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningState.update(android.content.Context, android.app.ActivityManager):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWatchingBackgroundItems(boolean z) {
        synchronized (this.mLock) {
            this.mWatchingBackgroundItems = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<MergedItem> getCurrentMergedItems() {
        ArrayList<MergedItem> arrayList;
        synchronized (this.mLock) {
            arrayList = this.mMergedItems;
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<MergedItem> getCurrentBackgroundItems() {
        ArrayList<MergedItem> arrayList;
        synchronized (this.mLock) {
            arrayList = this.mUserBackgroundItems;
        }
        return arrayList;
    }
}
