package com.abupdate.iot_libs.data.local;

import com.abupdate.iot_libs.data.remote.RegisterInfo;
import com.abupdate.iot_libs.data.remote.VersionInfo;
import com.abupdate.iot_libs.engine.policy.PolicyManager;
/* loaded from: classes.dex */
public class OtaEntity {
    private String filePath;
    private VersionInfo versionInfo;
    private boolean isMainProduct = false;
    private int downloadStatus = 0;

    public PolicyManager getPolicyManager() {
        return null;
    }

    public ProductInfo getProductInfo() {
        return null;
    }

    public RegisterInfo getRegisterInfo() {
        return null;
    }

    public boolean isMainProduct() {
        return this.isMainProduct;
    }

    public VersionInfo getVersionInfo() {
        return this.versionInfo;
    }

    public int getDownloadStatus() {
        return this.downloadStatus;
    }

    public String toString() {
        return "OtaEntity{isMainProduct=" + this.isMainProduct + ", downloadStatus=" + this.downloadStatus + ", filePath='" + this.filePath + "', deviceInfo=" + ((Object) null) + ", productInfo=" + ((Object) null) + ", registerInfo=" + ((Object) null) + ", versionInfo=" + this.versionInfo + ", policyManager=" + ((Object) null) + ", updateInter=" + ((Object) null) + '}';
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof OtaEntity) {
            ((OtaEntity) obj).getProductInfo();
            return false;
        }
        return false;
    }

    public int hashCode() {
        throw null;
    }
}
