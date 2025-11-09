package com.abupdate.iot_libs.engine;

import com.abupdate.iot_libs.MqttAgentPolicy;
import com.abupdate.iot_libs.data.local.OtaEntity;
import com.abupdate.iot_libs.data.remote.RegisterInfo;
import com.abupdate.iot_libs.engine.security.FotaException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class DataManager {
    private static DataManager mInstance;
    private OtaEntity mainOtaEntity;
    private List<OtaEntity> otaEntities = new ArrayList();

    public static DataManager getInstance() {
        if (mInstance == null) {
            synchronized (DataManager.class) {
                if (mInstance == null) {
                    mInstance = new DataManager();
                }
            }
        }
        return mInstance;
    }

    private DataManager() {
    }

    public List<OtaEntity> getOtaEntities() {
        return this.otaEntities;
    }

    public OtaEntity getMainEntity() throws FotaException {
        OtaEntity otaEntity = this.mainOtaEntity;
        if (otaEntity != null) {
            return otaEntity;
        }
        for (OtaEntity otaEntity2 : getOtaEntities()) {
            if (otaEntity2.isMainProduct()) {
                this.mainOtaEntity = otaEntity2;
                return otaEntity2;
            }
        }
        throw new FotaException(206);
    }

    public OtaEntity getEntityByProduct(String str) throws FotaException {
        Iterator<OtaEntity> it = getOtaEntities().iterator();
        if (it.hasNext()) {
            it.next().getProductInfo();
            throw null;
        }
        throw new FotaException(205);
    }

    public RegisterInfo getRegisterInfoByProduct(String str) throws FotaException {
        getEntityByProduct(str).getRegisterInfo();
        return null;
    }

    public List<OtaEntity> getNeedNotifyNewVersionEntity() {
        ArrayList arrayList = new ArrayList();
        for (OtaEntity otaEntity : getOtaEntities()) {
            if (otaEntity.getDownloadStatus() == 0 || 2 == otaEntity.getDownloadStatus() || 3 == otaEntity.getDownloadStatus() || 4 == otaEntity.getDownloadStatus()) {
                arrayList.add(otaEntity);
            } else {
                otaEntity.getProductInfo();
                throw null;
            }
        }
        return arrayList;
    }

    public boolean mqttSwitchIsOpen() {
        try {
            getInstance().getMainEntity().getProductInfo();
            throw null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean shouldUsePush() {
        if (mqttSwitchIsOpen()) {
            return MqttAgentPolicy.isMqttTime();
        }
        return false;
    }
}
