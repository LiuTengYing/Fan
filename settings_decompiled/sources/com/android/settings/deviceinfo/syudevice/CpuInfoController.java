package com.android.settings.deviceinfo.syudevice;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/* loaded from: classes.dex */
public class CpuInfoController extends BasePreferenceController {
    private Context mContext;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public CpuInfoController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getCpuInfo();
    }

    private String getCpuInfo() {
        String str;
        SystemProperties.get("sys.fyt.platform");
        String string = this.mContext.getResources().getString(R$string.cpu_info_summary);
        String str2 = SystemProperties.get("sys.fyt.platform");
        if (str2.contains("6316")) {
            string = this.mContext.getResources().getString(R$string.cpu_info_summary_8581);
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpufreq/policy7/cpuinfo_max_freq"), 8192);
            double floor = Math.floor(Double.valueOf(bufferedReader.readLine()).doubleValue() / 100000.0d) / 10.0d;
            bufferedReader.close();
            String string2 = this.mContext.getResources().getString(R$string.cpu_info_1);
            if (str2.contains("6316")) {
                string2 = this.mContext.getResources().getString(R$string.cpu_info_8581);
                floor = 1.6d;
            } else if (floor > 1.8d) {
                string2 = this.mContext.getResources().getString(R$string.cpu_info_3);
            }
            string = string2 + floor + this.mContext.getResources().getString(R$string.cpu_info_2);
        } catch (Exception unused) {
        }
        String str3 = SystemProperties.get("persist.sys.private_cpuinfo", "");
        if (!"".equals(str3)) {
            string = str3;
        }
        String str4 = string + "@#";
        if (!getMemoryConfigure().isEmpty()) {
            str = "@" + getMemoryConfigure();
        } else {
            String string3 = this.mContext.getResources().getString(R$string.memory_settings_title);
            String string4 = this.mContext.getResources().getString(R$string.storage_settings);
            double totalMemory = getTotalMemory();
            String str5 = SystemProperties.get("ro.fyt.realplatform");
            if (str5.contains("817") && totalMemory == 1.0d) {
                totalMemory = 1.5d;
            }
            String totalInternalMemorySize = getTotalInternalMemorySize();
            str = "@" + (((getCustomerID() == 95 || getCustomerID() == 107) && str5.contains("812") && totalInternalMemorySize != null && "16 GB".equals(totalInternalMemorySize)) ? 1.0d : totalMemory) + " GB(" + string3 + ") + " + getTotalInternalMemorySize() + "(" + string4 + ")";
        }
        return str4.replace("@#", str);
    }

    private String getTotalInternalMemorySize() {
        return Formatter.formatShortFileSize(this.mContext, new StorageManagerVolumeProvider((StorageManager) this.mContext.getSystemService(StorageManager.class)).getPrimaryStorageSize());
    }

    private String getMemoryConfigure() {
        return SystemProperties.get("proc.meminfo.totalMemory", "");
    }

    private double getTotalMemory() {
        double d = 0.0d;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            String[] split = bufferedReader.readLine().split("\\s+");
            for (int i = 0; i < split.length; i++) {
            }
            d = Double.valueOf(split[1]).doubleValue() * 1024.0d;
            bufferedReader.close();
        } catch (IOException unused) {
        }
        return Math.ceil(((d / 1024.0d) / 1024.0d) / 1024.0d);
    }

    public static int getCustomerID() {
        return SystemProperties.getInt("ro.build.fytmanufacturer", 2);
    }
}
