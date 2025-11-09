package qin.x_core.client.java;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import qin.x_core.aidl.IDataObserver;
import qin.x_core.aidl.IModule;
/* loaded from: classes2.dex */
public class DataObserver extends IDataObserver.Stub {
    private Bundle bundle;
    private ArrayList<Integer> dataFlags = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataObserver(Bundle bundle) {
        this.bundle = null;
        this.bundle = bundle;
        int[] intArray = bundle.getIntArray("flags");
        if (intArray == null || intArray.length <= 0) {
            return;
        }
        for (int i : intArray) {
            if (!this.dataFlags.contains(Integer.valueOf(i))) {
                this.dataFlags.add(Integer.valueOf(i));
            }
        }
    }

    @Override // qin.x_core.aidl.IDataObserver
    public void onDataChanged(Bundle bundle) throws RemoteException {
        if (bundle != null) {
            Log.d("Cast", "update [ pkg:  " + bundle.getString("package") + " moduleid: " + bundle.getInt("module", -1) + " data : " + bundle.getInt("value") + "]");
            bundle.putString("name", "data_observer");
            EventBus.getDefault().post(bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void add(IModule iModule, int... iArr) {
        if (iArr == null || iArr.length <= 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i : iArr) {
            if (!this.dataFlags.contains(Integer.valueOf(i))) {
                this.dataFlags.add(Integer.valueOf(i));
                arrayList.add(Integer.valueOf(i));
            }
        }
        int[] iArr2 = new int[this.dataFlags.size()];
        for (int i2 = 0; i2 < this.dataFlags.size(); i2++) {
            iArr2[i2] = this.dataFlags.get(i2).intValue();
        }
        this.bundle.putIntArray("flags", iArr2);
        if (arrayList.size() > 0) {
            Bundle bundle = (Bundle) this.bundle.clone();
            int[] iArr3 = new int[arrayList.size()];
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                iArr2[i3] = ((Integer) arrayList.get(i3)).intValue();
            }
            this.bundle.putIntArray("flags", iArr3);
            try {
                iModule.register(this, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
