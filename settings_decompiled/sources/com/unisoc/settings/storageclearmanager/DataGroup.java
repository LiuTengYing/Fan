package com.unisoc.settings.storageclearmanager;

import android.util.ArrayMap;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class DataGroup {
    private static DataGroup instance;
    public long mRubbishCategorySize;
    public long mTempCategorySize;
    public ArrayMap<String, Long> mInCacheMap = new ArrayMap<>();
    public ArrayMap<String, Long> mExCacheMap = new ArrayMap<>();
    public ArrayMap<String, Long> mInRubbishMap = new ArrayMap<>();
    public ArrayMap<String, Long> mExRubbishMap = new ArrayMap<>();
    public ArrayMap<String, Long> mInTmpMap = new ArrayMap<>();
    public ArrayMap<String, Long> mExTmpMap = new ArrayMap<>();
    public ArrayList<FileDetailModel> mRubbish_log_ext = new ArrayList<>();
    public ArrayList<FileDetailModel> mRubbish_bak_ext = new ArrayList<>();
    public ArrayList<FileDetailModel> mRubbish_tmp_prefix = new ArrayList<>();
    public ArrayList<FileDetailModel> mRubbish_tmp_ext = new ArrayList<>();
    public ArrayList<FileDetailModel> mRubbish_cach1_ext = new ArrayList<>();
    public ArrayList<FileDetailModel> mRubbish_cach2_ext = new ArrayList<>();

    public static DataGroup getInstance() {
        if (instance == null) {
            instance = new DataGroup();
        }
        return instance;
    }

    private DataGroup() {
    }
}
