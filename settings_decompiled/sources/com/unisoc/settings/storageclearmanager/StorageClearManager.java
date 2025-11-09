package com.unisoc.settings.storageclearmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R$color;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes2.dex */
public class StorageClearManager extends Activity implements View.OnClickListener {
    private boolean callBack;
    private ImageView mClearDoneView;
    private Button mClearGarbageBtn;
    private View mEnterFilesManagerBtn;
    private long mInCacheSize;
    private long mInRubbishSize;
    private long mInTmpSize;
    private PackageManager mPackageManager;
    private TextView mScanPathView;
    private TextView mSizeTextView;
    private long mSizeTotal;
    private TextView mSizeUnitView;
    private ImageView mWarningIconView;
    private TextView mWarningTextView;
    private final int[] mFileDetailTypes = {0, 1, 3, 4, 5};
    private DataGroup mData = DataGroup.getInstance();
    private boolean mIsScanEnd = false;
    Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.unisoc.settings.storageclearmanager.StorageClearManager.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.i("StorageClearManager", "msg.what:" + message.what);
            int i = message.what;
            if (i == 0) {
                removeMessages(0);
                Log.i("StorageClearManager", "update......" + StorageClearManager.this.mInCacheSize + StorageClearManager.this.mInRubbishSize + StorageClearManager.this.mInTmpSize);
                String[] convertTotalSize = StorageClearManager.this.convertTotalSize(StorageClearManager.this.mInCacheSize + StorageClearManager.this.mInRubbishSize + StorageClearManager.this.mInTmpSize);
                if (convertTotalSize == null || convertTotalSize.length <= 1) {
                    return;
                }
                StorageClearManager.this.mSizeTextView.setText(convertTotalSize[0]);
                StorageClearManager.this.mSizeUnitView.setText(convertTotalSize[1]);
            } else if (i == 1) {
                removeMessages(1);
                StorageClearManager storageClearManager = StorageClearManager.this;
                String[] convertTotalSize2 = storageClearManager.convertTotalSize(storageClearManager.mSizeTotal);
                if (convertTotalSize2 == null || convertTotalSize2.length <= 1) {
                    return;
                }
                StorageClearManager.this.mSizeTextView.setText(convertTotalSize2[0]);
                StorageClearManager.this.mSizeUnitView.setText(convertTotalSize2[1]);
            } else if (i == 2) {
                removeMessages(2);
                StorageClearManager storageClearManager2 = StorageClearManager.this;
                storageClearManager2.mSizeTotal = storageClearManager2.mInCacheSize + StorageClearManager.this.mInRubbishSize + StorageClearManager.this.mInTmpSize;
                if (StorageClearManager.this.mSizeTotal <= 0) {
                    StorageClearManager.this.mScanPathView.setText(StorageClearManager.this.getString(R$string.scan_completed));
                    StorageClearManager.this.mClearGarbageBtn.setEnabled(false);
                    return;
                }
                StorageClearManager.this.mScanPathView.setText(StorageClearManager.this.getString(R$string.scan_completed));
                StorageClearManager.this.mClearGarbageBtn.setEnabled(true);
            } else if (i != 3) {
            } else {
                removeMessages(3);
                if (StorageClearManager.this.mSizeTotal < 0) {
                    StorageClearManager.this.mSizeTotal = 0L;
                }
                StorageClearManager storageClearManager3 = StorageClearManager.this;
                String[] convertTotalSize3 = storageClearManager3.convertTotalSize(storageClearManager3.mSizeTotal);
                if (convertTotalSize3 != null && convertTotalSize3.length > 1) {
                    StorageClearManager.this.mSizeTextView.setText(convertTotalSize3[0]);
                    StorageClearManager.this.mSizeUnitView.setText(convertTotalSize3[1]);
                }
                StorageClearManager.this.mScanPathView.setText(StorageClearManager.this.getString(R$string.remove_completed));
                StorageClearManager.this.mClearGarbageBtn.setVisibility(8);
                StorageClearManager.this.mClearDoneView.setVisibility(0);
                StorageClearManager.this.setupWarningText();
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.storage_clear_manage);
        this.mPackageManager = getPackageManager();
        initViews();
        performGarbageScanning();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        setupWarningText();
    }

    private void initViews() {
        this.mClearGarbageBtn = (Button) findViewById(R$id.garbage_clear_btn);
        this.mWarningTextView = (TextView) findViewById(R$id.storage_warning);
        this.mSizeTextView = (TextView) findViewById(R$id.garbage_size);
        this.mSizeUnitView = (TextView) findViewById(R$id.garbage_size_unit);
        this.mScanPathView = (TextView) findViewById(R$id.scan_path);
        this.mClearDoneView = (ImageView) findViewById(R$id.clear_done);
        this.mWarningIconView = (ImageView) findViewById(R$id.storage_warning_icon);
        this.mEnterFilesManagerBtn = findViewById(R$id.app_clear_btn);
        this.mClearGarbageBtn.setOnClickListener(this);
        this.mClearGarbageBtn.setEnabled(false);
        this.mEnterFilesManagerBtn.setOnClickListener(this);
        setupWarningText();
    }

    private boolean isRTL() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupWarningText() {
        int storageLevel = getStorageLevel();
        if (storageLevel == 0) {
            if (isRTL()) {
                TextView textView = this.mWarningTextView;
                textView.setText("\u200e" + getString(R$string.storage_warning_level0) + "\u200e");
            } else {
                this.mWarningTextView.setText(getString(R$string.storage_warning_level0));
            }
            this.mWarningIconView.setImageResource(R$drawable.baseline_info_24px);
            this.mWarningTextView.setTextColor(getResources().getColor(R$color.warning_green));
        } else if (storageLevel == 1) {
            if (isRTL()) {
                TextView textView2 = this.mWarningTextView;
                textView2.setText("\u200e" + getString(R$string.storage_warning_level1) + "\u200e");
            } else {
                this.mWarningTextView.setText(getString(R$string.storage_warning_level1));
            }
            this.mWarningIconView.setImageResource(R$drawable.baseline_disc_24px);
            this.mWarningTextView.setTextColor(getResources().getColor(R$color.warning_orange));
        } else if (storageLevel != 2) {
        } else {
            if (isRTL()) {
                TextView textView3 = this.mWarningTextView;
                textView3.setText("\u200e" + getString(R$string.storage_warning_level2) + "\u200e");
            } else {
                this.mWarningTextView.setText(getString(R$string.storage_warning_level2));
            }
            this.mWarningIconView.setImageResource(R$drawable.baseline_disc_full_24px);
            this.mWarningTextView.setTextColor(getResources().getColor(R$color.warning_red));
        }
    }

    private void performGarbageScanning() {
        resetData();
        new Thread() { // from class: com.unisoc.settings.storageclearmanager.StorageClearManager.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    try {
                        Log.i("StorageClearManager", "Scanning start...");
                        for (StorageVolume storageVolume : ((StorageManager) StorageClearManager.this.getSystemService(StorageManager.class)).getStorageVolumes()) {
                            File directory = storageVolume.getDirectory();
                            if (storageVolume.isEmulated() && storageVolume.isPrimary()) {
                                StorageClearManager.this.scanFiles(directory.listFiles());
                            }
                        }
                        SystemClock.sleep(2000L);
                        StorageClearManager.this.mIsScanEnd = true;
                        if (!StorageClearManager.this.mIsScanEnd) {
                            return;
                        }
                    } catch (Exception unused) {
                        Log.i("StorageClearManager", "InterruptedException 1");
                        if (!StorageClearManager.this.mIsScanEnd) {
                            return;
                        }
                    }
                    StorageClearManager.this.mHandler.sendEmptyMessage(2);
                    StorageClearManager.this.mIsScanEnd = false;
                } catch (Throwable th) {
                    if (StorageClearManager.this.mIsScanEnd) {
                        StorageClearManager.this.mHandler.sendEmptyMessage(2);
                        StorageClearManager.this.mIsScanEnd = false;
                    }
                    throw th;
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanFiles(File[] fileArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("Enter scanFiles, files == null:");
        sb.append(fileArr == null);
        Log.i("StorageClearManager", sb.toString());
        if (fileArr != null) {
            for (File file : fileArr) {
                if (file.isDirectory()) {
                    Log.i("StorageClearManager", "fileName:" + file.getName());
                    if (file.getName().equals("cache") || file.getName().equals("code_cache")) {
                        long scanDirSize = scanDirSize(file);
                        Log.i("StorageClearManager", "size:" + scanDirSize);
                        if (scanDirSize > 0) {
                            this.mData.mInCacheMap.put(file.getAbsolutePath(), Long.valueOf(scanDirSize));
                            this.mInCacheSize += scanDirSize;
                            Log.i("StorageClearManager", "mInCacheSize:" + this.mInCacheSize);
                            this.mData.mRubbish_cach2_ext.add(new FileDetailModel(file.getAbsolutePath(), scanDirSize));
                            this.mHandler.sendEmptyMessage(0);
                        }
                    } else {
                        scanFiles(file.listFiles());
                    }
                } else {
                    Log.i("StorageClearManager", "name:" + file.getName());
                    String name = file.getName();
                    if (name.endsWith(".log")) {
                        this.mInRubbishSize += file.length();
                        Log.i("StorageClearManager", "mInRubbishSize:" + this.mInRubbishSize);
                        DataGroup dataGroup = this.mData;
                        dataGroup.mRubbishCategorySize = this.mInRubbishSize;
                        dataGroup.mInRubbishMap.put(file.getAbsolutePath(), Long.valueOf(file.length()));
                        this.mData.mRubbish_log_ext.add(new FileDetailModel(file.getAbsolutePath(), file.length()));
                    } else if (name.endsWith(".bak")) {
                        this.mInRubbishSize += file.length();
                        Log.i("StorageClearManager", "mInRubbishSize:" + this.mInRubbishSize);
                        DataGroup dataGroup2 = this.mData;
                        dataGroup2.mRubbishCategorySize = this.mInRubbishSize;
                        dataGroup2.mInRubbishMap.put(file.getAbsolutePath(), Long.valueOf(file.length()));
                        this.mData.mRubbish_bak_ext.add(new FileDetailModel(file.getAbsolutePath(), file.length()));
                    } else if (name.endsWith(".tmp") || name.startsWith("~")) {
                        this.mInTmpSize += file.length();
                        Log.i("StorageClearManager", "mInTmpSize:" + this.mInTmpSize);
                        DataGroup dataGroup3 = this.mData;
                        dataGroup3.mTempCategorySize = this.mInTmpSize;
                        dataGroup3.mInTmpMap.put(file.getAbsolutePath(), Long.valueOf(file.length()));
                        this.mData.mRubbish_tmp_ext.add(new FileDetailModel(file.getAbsolutePath(), file.length()));
                    }
                    this.mHandler.sendEmptyMessage(0);
                }
            }
        }
    }

    private long scanDirSize(File file) {
        long j;
        long length;
        File[] listFiles = file.listFiles();
        int i = 0;
        if (listFiles != null) {
            int length2 = listFiles.length;
            int i2 = 0;
            while (i < length2) {
                File file2 = listFiles[i];
                if (file2.isDirectory()) {
                    j = i2;
                    length = scanDirSize(file2);
                } else {
                    j = i2;
                    length = file2.length();
                }
                i2 = (int) (j + length);
                i++;
            }
            i = i2;
        }
        return i;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mIsScanEnd = true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R$id.garbage_clear_btn) {
            this.mClearGarbageBtn.setEnabled(false);
            new FileDeleteTask().execute(new Void[0]);
        } else if (id == R$id.app_clear_btn) {
            startActivity(new Intent("android.os.storage.action.MANAGE_STORAGE"));
        }
    }

    private int getStorageLevel() {
        long usableSpace = Environment.getDataDirectory().getUsableSpace();
        if (usableSpace < 104857600) {
            return 2;
        }
        return usableSpace < 209715200 ? 1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] convertTotalSize(long j) {
        String formatFileSize = Formatter.formatFileSize(this, j);
        if (formatFileSize.contains(" ")) {
            return formatFileSize.split(" ");
        }
        StringBuffer stringBuffer = new StringBuffer(formatFileSize);
        int i = 0;
        while (true) {
            if (i >= formatFileSize.length()) {
                break;
            }
            if ((formatFileSize.charAt(i) + "").getBytes(Charset.defaultCharset()).length > 1) {
                stringBuffer.insert(i, " ");
                break;
            }
            i++;
        }
        return stringBuffer.toString().split(" ");
    }

    private void resetData() {
        this.mData.mInCacheMap.clear();
        this.mData.mInRubbishMap.clear();
        this.mData.mInTmpMap.clear();
        this.mData.mExCacheMap.clear();
        this.mData.mExRubbishMap.clear();
        this.mData.mExTmpMap.clear();
        this.mData.mRubbish_bak_ext.clear();
        this.mData.mRubbish_log_ext.clear();
        this.mData.mRubbish_tmp_prefix.clear();
        this.mData.mRubbish_tmp_ext.clear();
        this.mData.mRubbish_cach1_ext.clear();
        this.mData.mRubbish_cach2_ext.clear();
        this.mInCacheSize = 0L;
        this.mInRubbishSize = 0L;
        this.mInTmpSize = 0L;
        this.callBack = false;
    }

    public void deleteFiles(FileDetailModel fileDetailModel) {
        File file = new File(fileDetailModel.getFilePath());
        if (dirDelete(file) || !file.exists()) {
            this.mSizeTotal -= fileDetailModel.getFileSize();
        }
    }

    private boolean dirDelete(File file) {
        File[] listFiles;
        if (file.isDirectory() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
                if (!dirDelete(file2)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /* loaded from: classes2.dex */
    class FileDeleteTask extends AsyncTask<Void, Void, Void> {
        FileDeleteTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            onStart();
        }

        private void onStart() {
            StorageClearManager.this.mScanPathView.setText(StorageClearManager.this.getString(R$string.garbage_removing));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Iterator<FileDetailModel> it = StorageClearManager.this.mData.mRubbish_bak_ext.iterator();
            while (it.hasNext()) {
                StorageClearManager.this.deleteFiles(it.next());
            }
            StorageClearManager.this.mData.mRubbish_bak_ext.clear();
            Iterator<FileDetailModel> it2 = StorageClearManager.this.mData.mRubbish_log_ext.iterator();
            while (it2.hasNext()) {
                StorageClearManager.this.deleteFiles(it2.next());
            }
            StorageClearManager.this.mData.mRubbish_log_ext.clear();
            Iterator<FileDetailModel> it3 = StorageClearManager.this.mData.mRubbish_tmp_ext.iterator();
            while (it3.hasNext()) {
                StorageClearManager.this.deleteFiles(it3.next());
            }
            StorageClearManager.this.mData.mRubbish_tmp_ext.clear();
            Iterator<FileDetailModel> it4 = StorageClearManager.this.mData.mRubbish_cach2_ext.iterator();
            while (it4.hasNext()) {
                StorageClearManager.this.deleteFiles(it4.next());
            }
            StorageClearManager.this.mData.mRubbish_cach2_ext.clear();
            try {
                Thread.sleep(1500L);
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((FileDeleteTask) r1);
            StorageClearManager.this.mHandler.sendEmptyMessage(3);
        }
    }
}
