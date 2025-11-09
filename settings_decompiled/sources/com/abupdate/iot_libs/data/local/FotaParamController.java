package com.abupdate.iot_libs.data.local;

import android.content.Context;
import com.abupdate.iot_libs.data.db.ParamsDBManager;
import com.abupdate.iot_libs.utils.FileUtil;
import com.abupdate.trace.Trace;
import java.io.File;
/* loaded from: classes.dex */
public class FotaParamController {
    private static FotaParamController mInstance;
    private FotaParams params;

    public static FotaParamController getInstance() {
        if (mInstance == null) {
            synchronized (FotaParamController.class) {
                if (mInstance == null) {
                    mInstance = new FotaParamController();
                }
            }
        }
        return mInstance;
    }

    public FotaParams getParams() {
        if (this.params == null) {
            this.params = ParamsDBManager.getInstance().getFotaParams();
        }
        return this.params;
    }

    /* loaded from: classes.dex */
    public static class FotaParams {
        public int clientDefaultTriggerCycle;
        public final Context context;
        public int downloadConnectTimeout;
        public int downloadReadTimeout;
        public int httpConnectTimeout;
        public int httpSocketTimeout;
        public String mid;
        public String tracePath;
        public boolean showTrace = true;
        public boolean reportLog = true;
        public boolean keepConnect = true;
        public int httpRetryTimes = -1;
        public int httpRedirectTimes = -1;
        public int downloadRetryTime = -1;
        public boolean useDefaultClientTrigger = true;
        public boolean rebootCheckTrigger = true;
        public boolean useDefaultClientStatusMechanism = true;
        public boolean useSota = false;

        public FotaParams(Context context) {
            this.context = context;
        }

        public void setTracePath(String str) {
            Trace.d("FotaParams", "setTracePath() path:" + str);
            if (!FileUtil.createOrExistsDir(new File(str).getParentFile().getAbsolutePath())) {
                Trace.d("FotaParams", "setTracePath() path is invalid ! set path fail");
            } else {
                this.tracePath = str;
            }
        }

        public void setShowTrace(boolean z) {
            this.showTrace = z;
        }

        public void setReportLog(boolean z) {
            this.reportLog = z;
        }

        public void setMid(String str) {
            this.mid = str;
        }

        public void setKeepConnect(boolean z) {
            this.keepConnect = z;
        }

        public void setHttpRetryTimes(int i) {
            this.httpRetryTimes = i;
        }

        public void setHttpRedirectTimes(int i) {
            this.httpRedirectTimes = i;
        }

        public void setHttpReadTimeout(int i) {
            this.httpSocketTimeout = i;
        }

        public void setHttpConnectTimeout(int i) {
            this.httpConnectTimeout = i;
        }

        public void setDownloadConnectTimeout(int i) {
            this.downloadConnectTimeout = i;
        }

        public void setDownloadReadTimeout(int i) {
            this.downloadReadTimeout = i;
        }

        public void setDownloadRetryTime(int i) {
            this.downloadRetryTime = i;
        }

        public void setClientDefaultTriggerCycle(int i) {
            this.clientDefaultTriggerCycle = i;
        }
    }
}
