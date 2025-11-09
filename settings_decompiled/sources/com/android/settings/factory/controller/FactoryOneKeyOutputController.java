package com.android.settings.factory.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsApplication;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.core.AbstractPreferenceController;
import com.syu.ipcself.module.main.Main;
import com.syu.jni.SyuJniNative;
import com.syu.remote.Message;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class FactoryOneKeyOutputController extends AbstractPreferenceController implements IpcNotify, LifecycleObserver {
    private static String TAG = "FactoryOneKeyOutputController";
    private JSONObject mAmpConfig;
    private JSONObject mBtConfig;
    private JSONObject mCanbusConfig;
    private Context mContext;
    private JSONObject mDvdConfig;
    private JSONObject mDvrConfig;
    private JSONObject mIpodConfig;
    private JSONObject mRadioConfig;
    private JSONObject mSoundConfig;
    private JSONObject mTvConfig;
    private JSONObject mainConfig;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "str_text_config_out";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGesture(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGsensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(Message message) {
    }

    public FactoryOneKeyOutputController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        onekeyOutput();
    }

    public void onekeyOutput() {
        this.mainConfig = new JSONObject();
        this.mSoundConfig = new JSONObject();
        this.mRadioConfig = new JSONObject();
        this.mDvrConfig = new JSONObject();
        this.mTvConfig = new JSONObject();
        this.mIpodConfig = new JSONObject();
        this.mAmpConfig = new JSONObject();
        this.mDvdConfig = new JSONObject();
        this.mCanbusConfig = new JSONObject();
        this.mBtConfig = new JSONObject();
        getBtMessage();
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(this.mContext);
        IpcObj.getInstance().setObserverMoudle(0, 109, 116, 38, 27, 23, 51, 82, 66, 67, 80, 88, 89, 65, 28, 100, 81, 83, 102, 108, 5, 76, 8, 6, 26, 24, 29, 56, 99, 121);
        IpcObj.getInstance().setObserverMoudle(4, 7, 5, 14, 4, 11, 13, 6, 23);
        IpcObj.getInstance().setObserverMoudle(9, 1);
        IpcObj.getInstance().setObserverMoudle(6, 0);
        IpcObj.getInstance().setObserverMoudle(5, 14);
        IpcObj.getInstance().setObserverMoudle(15, 104);
        IpcObj.getInstance().setObserverMoudle(3, 25);
        IpcObj.getInstance().setObserverMoudle(7, AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP, 1003, 1001, 1002);
        IpcObj.getInstance().setObserverMoudle(1, 25);
    }

    public void saveConfig() {
        File file = new File("/mnt/sdcard/SettingsConfig");
        if (file.exists()) {
            file.delete();
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("Main", this.mainConfig);
            jSONObject.put("Bt", this.mBtConfig);
            jSONObject.put("Sound", this.mSoundConfig);
            jSONObject.put("Dvr", this.mDvrConfig);
            jSONObject.put("Tv", this.mTvConfig);
            jSONObject.put("Ipod", this.mIpodConfig);
            jSONObject.put("Amp", this.mAmpConfig);
            jSONObject.put("Dvd", this.mDvdConfig);
            jSONObject.put("Canbus", this.mCanbusConfig);
            jSONObject.put("RadioM", this.mRadioConfig);
            if (Main.mConf_PlatForm == 8) {
                byte[] cmd_148_read_data = SyuJniNative.getInstance().cmd_148_read_data(1027, 13);
                jSONObject.put("ARMSave", Base64.encodeToString(cmd_148_read_data, 0));
                new JSONArray(cmd_148_read_data);
                Log.i(TAG, getStringByEnter(16, bytesToHexString(cmd_148_read_data)));
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile("/mnt/sdcard/SettingsConfig", "rw");
            randomAccessFile.write(jSONObject.toString().getBytes("utf-8"));
            randomAccessFile.close();
            if (!Locale.getDefault().toString().equalsIgnoreCase("zh_CN") && !Locale.getDefault().toString().startsWith("zh_CN")) {
                Toast.makeText(SettingsApplication.mApplication, "Successfully Exported To File /mnt/sdcard/SettingsConfig", 0).show();
                return;
            }
            Toast.makeText(SettingsApplication.mApplication, "导出成功至文件/mnt/sdcard/SettingsConfig", 0).show();
        } catch (Exception e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "saveConfig: " + e.toString());
        }
    }

    public String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public String getStringByEnter(int i, String str) throws Exception {
        for (int i2 = 1; i2 <= str.length(); i2++) {
            if (str.substring(0, i2).getBytes("GBK").length > i) {
                StringBuilder sb = new StringBuilder();
                int i3 = i2 - 1;
                sb.append(str.substring(0, i3));
                sb.append("\n");
                sb.append(getStringByEnter(i, str.substring(i3)));
                return sb.toString();
            }
        }
        return str;
    }

    private void getBtMessage() {
        try {
            this.mBtConfig.put("23", SystemProperties.getInt("persist.audio.mic.senstivity", 0));
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getBtMessage: " + e.toString());
        }
    }

    private void getMainMessage(Message message) {
        String str = TAG;
        Log.d(str, "getMainMessage: " + message.code + "  " + message.toString());
        try {
            int i = message.code;
            if (i == 5) {
                this.mainConfig.put("2", message.ints[0]);
            } else if (i == 6) {
                this.mainConfig.put("3", message.ints[0]);
            } else if (i == 8) {
                this.mainConfig.put("4", message.ints[0]);
            } else if (i == 38) {
                this.mainConfig.put("15", message.ints[0]);
            } else if (i == 51) {
                this.mainConfig.put("29", message.ints[0]);
            } else if (i == 56) {
                this.mainConfig.put("34", message.ints[0]);
            } else if (i == 76) {
                this.mainConfig.put("48", message.ints[0]);
            } else if (i == 102) {
                this.mainConfig.put("68", message.ints[0]);
            } else if (i == 116) {
                this.mainConfig.put("79", message.ints[0]);
            } else if (i == 121) {
                this.mainConfig.put("87", message.ints[0]);
            } else if (i == 23) {
                this.mainConfig.put("6", message.ints[0]);
            } else if (i == 24) {
                this.mainConfig.put("7", message.ints[0]);
            } else if (i == 88) {
                this.mainConfig.put("61", message.ints[0]);
            } else if (i == 89) {
                this.mainConfig.put("63", message.ints[0]);
            } else if (i == 99) {
                this.mainConfig.put("64", message.ints[0]);
            } else if (i == 100) {
                this.mainConfig.put("65", message.ints[0]);
            } else if (i == 108) {
                this.mainConfig.put("72", message.ints[0]);
            } else if (i == 109) {
                this.mainConfig.put("73", message.ints[0]);
            } else {
                switch (i) {
                    case 26:
                        this.mainConfig.put("8", message.ints[0]);
                        break;
                    case 27:
                        this.mainConfig.put("5", message.ints[0]);
                        break;
                    case 28:
                        this.mainConfig.put("9", message.strs[0]);
                        break;
                    case 29:
                        this.mainConfig.put("1", message.ints[0]);
                        break;
                    default:
                        switch (i) {
                            case 65:
                                this.mainConfig.put("40", message.ints[0]);
                                break;
                            case 66:
                                this.mainConfig.put("42", message.ints[0]);
                                break;
                            case 67:
                                this.mainConfig.put("43", message.ints[0]);
                                break;
                            default:
                                switch (i) {
                                    case 80:
                                        this.mainConfig.put("52", message.ints[0]);
                                        break;
                                    case 81:
                                        this.mainConfig.put("54", message.ints[0]);
                                        break;
                                    case 82:
                                        this.mainConfig.put("55", message.ints[0]);
                                        break;
                                    case 83:
                                        this.mainConfig.put("57", message.ints[0]);
                                        break;
                                }
                        }
                }
            }
        } catch (JSONException e) {
            e.fillInStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    private void getSoundMessage(Message message) {
        try {
            int i = message.code;
            if (i == 4) {
                this.mSoundConfig.put("8", message.ints[0]);
            } else if (i == 5) {
                this.mSoundConfig.put("12", message.ints[0]);
            } else if (i == 6) {
                this.mSoundConfig.put("14", message.ints[0]);
            } else if (i == 7) {
                this.mSoundConfig.put("7", message.ints[0]);
            } else if (i == 11) {
                this.mSoundConfig.put("5", message.ints[0]);
            } else if (i == 23) {
                this.mSoundConfig.put("20", message.ints[0]);
            } else if (i == 13) {
                this.mSoundConfig.put("7", message.ints[0]);
            } else if (i == 14) {
                this.mSoundConfig.put("9", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getSoundMessage: " + e.toString());
        }
    }

    private void getDvrMessage(Message message) {
        try {
            if (message.code == 1) {
                this.mDvrConfig.put("1", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getDvrMessage: " + e.toString());
        }
    }

    private void getTvMessage(Message message) {
        try {
            if (message.code == 0) {
                this.mTvConfig.put("4", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getTvMessage: " + e.toString());
        }
    }

    private void getIpodMessage(Message message) {
        try {
            if (message.code == 14) {
                this.mIpodConfig.put("3", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getIpodMessage: " + e.toString());
        }
    }

    private void getAmpMessage(Message message) {
        try {
            if (message.code == 104) {
                this.mAmpConfig.put("101", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getAmpMessage: " + e.toString());
        }
    }

    private void getDvdMessage(Message message) {
        try {
            if (message.code == 25) {
                this.mDvdConfig.put("21", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getDvdMessage: " + e.toString());
        }
    }

    private void getCanbusMessage(Message message) {
        try {
            switch (message.code) {
                case AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP /* 1000 */:
                    this.mCanbusConfig.put("1000", message.ints[0]);
                    break;
                case 1001:
                    this.mCanbusConfig.put("1009", message.ints[0]);
                    break;
                case 1002:
                    this.mCanbusConfig.put("1010", message.ints[0]);
                    break;
                case 1003:
                    this.mCanbusConfig.put("1001", message.ints[0]);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getCanbusMessage: " + e.toString());
        }
    }

    private void getRadioMessage(Message message) {
        try {
            if (message.code == 25) {
                this.mRadioConfig.put("24", message.ints[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = TAG;
            Log.d(str, "getRadioMessage: " + e.toString());
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(Message message) {
        getMainMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(Message message) {
        getSoundMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(Message message) {
        getRadioMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(Message message) {
        getDvdMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvr(Message message) {
        getDvrMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(Message message) {
        getTvMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyIpod(Message message) {
        getIpodMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(Message message) {
        getCanbusMessage(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(Message message) {
        getAmpMessage(message);
    }
}
