package com.syu.ipcself.module.main;

import android.os.RemoteException;
import android.os.SystemProperties;
import com.syu.ipc.IModuleCallback;
import com.syu.ipcself.ModuleProxy;
import com.syu.util.IpcUtil;
import com.syu.util.UiNotifyEvent;
import com.syu.util.UiNotifyEventString;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class Main extends IModuleCallback.Stub {
    public static int[] DATA;
    public static int[] DATA_RADAR;
    public static String factorySn;
    public static int mVideoChannel;
    public static int mVideoChannelCurrent;
    public static int[] mcuError;
    public static boolean mcuMatchError;
    public static String naviPackageName;
    public static ArrayList<String> sArrayDebugBackCar;
    private static Main INSTANCE = new Main();
    public static boolean bIsHiworldObdEnable = SystemProperties.getBoolean("sys.fyt.hiworld_obd_enable", false);
    public static boolean bCanInitCamera = false;
    public static int mConf_PlatForm = 2;
    public static final UiNotifyEventString VER_MCU = new UiNotifyEventString();
    public static final UiNotifyEventString VER_DVD = new UiNotifyEventString();
    public static final UiNotifyEventString VER_BT = new UiNotifyEventString();
    public static UiNotifyEvent mUiNotifyEvent = new UiNotifyEvent();
    public static ModuleProxy PROXY = new ModuleProxy();

    static {
        int[] iArr = new int[256];
        DATA = iArr;
        iArr[1] = 1;
        DATA_RADAR = new int[16];
        for (int i = 0; i < 16; i++) {
            DATA_RADAR[i] = 10;
        }
        naviPackageName = "";
        factorySn = "";
        mcuMatchError = false;
    }

    private Main() {
    }

    public static void initPlatForm(String str) {
        if ("E7".equals(str)) {
            mConf_PlatForm = 1;
        } else if ("3188".equals(str)) {
            mConf_PlatForm = 2;
        } else if ("8700".equals(str)) {
            mConf_PlatForm = 3;
        } else if ("786".equals(str)) {
            mConf_PlatForm = 4;
        } else if ("Sophia".equals(str)) {
            mConf_PlatForm = 5;
        } else if ("6025".equals(str)) {
            mConf_PlatForm = 6;
        } else if ("PX5".equals(str)) {
            mConf_PlatForm = 7;
        } else if ("9853".equals(str)) {
            mConf_PlatForm = 8;
        }
    }

    public static void updateRadar(int i, int[] iArr, float[] fArr, String[] strArr) {
        if (IpcUtil.intsOk(iArr, 1)) {
            DATA[i] = iArr[0];
            if (i < 90) {
                int[] iArr2 = DATA_RADAR;
                int i2 = i - 14;
                int i3 = iArr2[i2];
                int i4 = iArr[0];
                if (i3 != i4) {
                    iArr2[i2] = i4;
                    mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
                }
            } else if (i <= 93) {
                int[] iArr3 = DATA_RADAR;
                int i5 = (i + 12) - 90;
                int i6 = iArr3[i5];
                int i7 = iArr[0];
                if (i6 != i7) {
                    iArr3[i5] = i7;
                    mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
                }
            } else if (i <= 97) {
                int[] iArr4 = DATA_RADAR;
                int i8 = (i + 4) - 90;
                int i9 = iArr4[i8];
                int i10 = iArr[0];
                if (i9 != i10) {
                    iArr4[i8] = i10;
                    mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
                }
            }
        }
    }

    public static void updateSteerAngle(int i, int[] iArr, float[] fArr, String[] strArr) {
        if (IpcUtil.intsOk(iArr, 1)) {
            int[] iArr2 = DATA;
            int i2 = iArr2[i];
            int i3 = iArr[0];
            if (i2 != i3) {
                iArr2[i] = i3;
                mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
            }
        }
    }

    public static void updateNaviName(int i, int[] iArr, float[] fArr, String[] strArr) {
        if (IpcUtil.strsOk(strArr, 1)) {
            naviPackageName = strArr[0];
            mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
        }
    }

    public static void updateMcuSerial(int i, int[] iArr, float[] fArr, String[] strArr) {
        if (IpcUtil.strsOk(strArr, 1)) {
            factorySn = strArr[0];
            mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
        }
    }

    public static void updateNotify(int i, int[] iArr, float[] fArr, String[] strArr) {
        if (IpcUtil.intsOk(iArr, 1)) {
            if (i == 89) {
                if (IpcUtil.intsOk(iArr, 2)) {
                    int i2 = iArr[0] > 0 ? iArr[1] : 2;
                    int[] iArr2 = DATA;
                    if (iArr2[i] != i2) {
                        iArr2[i] = i2;
                        mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
                        return;
                    }
                    return;
                }
                return;
            }
            int[] iArr3 = DATA;
            int i3 = iArr3[i];
            int i4 = iArr[0];
            if (i3 != i4) {
                iArr3[i] = i4;
                mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
            }
        }
    }

    public static void updateDirect(int i, int[] iArr, float[] fArr, String[] strArr) {
        if (IpcUtil.intsOk(iArr, 1)) {
            DATA[i] = iArr[0];
            mUiNotifyEvent.updateNotify(i, iArr, fArr, strArr);
        }
    }

    @Override // com.syu.ipc.IModuleCallback
    public void update(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException {
        postRunnable_Ui(false, new Runnable_Update(i, iArr, fArr, strArr));
    }

    public static void postRunnable_Ui(boolean z, Runnable runnable, long j) {
        if (runnable != null) {
            if (z) {
                removeRunnable_Ui(runnable);
            }
            if (j == 0) {
                UiNotifyEvent.HANDLER_UI.post(runnable);
            } else {
                UiNotifyEvent.HANDLER_UI.postDelayed(runnable, j);
            }
        }
    }

    public static void postRunnable_Ui(boolean z, Runnable runnable) {
        if (runnable != null) {
            if (z) {
                removeRunnable_Ui(runnable);
            }
            UiNotifyEvent.HANDLER_UI.post(runnable);
        }
    }

    public static void removeRunnable_Ui(Runnable runnable) {
        if (runnable != null) {
            UiNotifyEvent.HANDLER_UI.removeCallbacks(runnable);
        }
    }

    /* loaded from: classes2.dex */
    private class Runnable_Update implements Runnable {
        public float[] flts;
        public int[] ints;
        public String[] strs;
        public int updateCode;

        public Runnable_Update(int i, int[] iArr, float[] fArr, String[] strArr) {
            this.updateCode = i;
            this.ints = iArr;
            this.flts = fArr;
            this.strs = strArr;
        }

        /* JADX WARN: Removed duplicated region for block: B:42:0x0077  */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 416
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.syu.ipcself.module.main.Main.Runnable_Update.run():void");
        }
    }
}
