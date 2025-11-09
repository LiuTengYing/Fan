package com.android.settings.factory.controller;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class FactoryDoorLockController extends AbstractPreferenceController implements LifecycleObserver {
    public static int PASSWORDLENGTH = 4;
    private ImageView clearCode;
    private boolean isChecked;
    private ImageView mConfirm;
    private View mContentView;
    private Context mContext;
    public StringBuffer mCurPassword;
    private Switch mDoorLockSwitch;
    private LayoutPreference mPreference;
    private TextView mText;
    private ImageView pinCode0;
    private ImageView pinCode1;
    private ImageView pinCode2;
    private ImageView pinCode3;
    private ImageView pinCode4;
    private ImageView pinCode5;
    private ImageView pinCode6;
    private ImageView pinCode7;
    private ImageView pinCode8;
    private ImageView pinCode9;
    private PopupWindow popupWindow;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "doorlockinterference";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryDoorLockController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mCurPassword = new StringBuffer();
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = layoutPreference;
        if (layoutPreference == null) {
            return;
        }
        init();
    }

    private void init() {
        this.mText = (TextView) this.mPreference.findViewById(R$id.door_lock_password);
        this.mDoorLockSwitch = (Switch) this.mPreference.findViewById(R$id.door_lock_switch);
        initListener();
    }

    private void initListener() {
        this.mText.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryDoorLockController.this.popupWindow != null && FactoryDoorLockController.this.popupWindow.isShowing()) {
                    FactoryDoorLockController.this.popupWindow.dismiss();
                } else {
                    FactoryDoorLockController.this.showPassword();
                }
            }
        });
        this.mDoorLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                IpcObj.getInstance().setCmd(0, 42, z ? 1 : 0);
                FactoryDoorLockController.this.isChecked = z;
                StringBuffer stringBuffer = FactoryDoorLockController.this.mCurPassword;
                stringBuffer.delete(0, stringBuffer.length());
                if (!FactoryDoorLockController.this.isChecked) {
                    FactoryDoorLockController.this.mDoorLockSwitch.setClickable(false);
                }
                FactoryDoorLockController.this.updatePassword();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPassword() {
        this.mContentView = LayoutInflater.from(this.mContext).inflate(R$layout.factory_doorlock_password_layout, (ViewGroup) null);
        if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 240) {
            PopupWindow popupWindow = new PopupWindow(this.mContentView, 400, 300, false);
            this.popupWindow = popupWindow;
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            this.popupWindow.setFocusable(true);
            this.popupWindow.setOutsideTouchable(true);
            this.popupWindow.update();
            this.popupWindow.showAsDropDown(this.mText, -150, -50);
        } else {
            PopupWindow popupWindow2 = new PopupWindow(this.mContentView, 267, 200, false);
            this.popupWindow = popupWindow2;
            popupWindow2.setBackgroundDrawable(new BitmapDrawable());
            this.popupWindow.setFocusable(true);
            this.popupWindow.setOutsideTouchable(true);
            this.popupWindow.update();
            this.popupWindow.showAsDropDown(this.mText, -50, -50);
        }
        initPopClickListener();
    }

    private void initPopClickListener() {
        this.pinCode0 = (ImageView) this.mContentView.findViewById(R$id.pin_code0);
        this.pinCode1 = (ImageView) this.mContentView.findViewById(R$id.pin_code1);
        this.pinCode2 = (ImageView) this.mContentView.findViewById(R$id.pin_code2);
        this.pinCode3 = (ImageView) this.mContentView.findViewById(R$id.pin_code3);
        this.pinCode4 = (ImageView) this.mContentView.findViewById(R$id.pin_code4);
        this.pinCode5 = (ImageView) this.mContentView.findViewById(R$id.pin_code5);
        this.pinCode6 = (ImageView) this.mContentView.findViewById(R$id.pin_code6);
        this.pinCode7 = (ImageView) this.mContentView.findViewById(R$id.pin_code7);
        this.pinCode8 = (ImageView) this.mContentView.findViewById(R$id.pin_code8);
        this.pinCode9 = (ImageView) this.mContentView.findViewById(R$id.pin_code9);
        this.clearCode = (ImageView) this.mContentView.findViewById(R$id.pin_btn_delete);
        this.mConfirm = (ImageView) this.mContentView.findViewById(R$id.pin_btn_confirm);
        this.pinCode0.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("0");
            }
        });
        this.pinCode1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("1");
            }
        });
        this.pinCode2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("2");
            }
        });
        this.pinCode3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("3");
            }
        });
        this.pinCode4.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("4");
            }
        });
        this.pinCode5.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("5");
            }
        });
        this.pinCode6.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("6");
            }
        });
        this.pinCode7.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("7");
            }
        });
        this.pinCode8.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("8");
            }
        });
        this.pinCode9.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryDoorLockController.this.inputCode("9");
            }
        });
        this.clearCode.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.13
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryDoorLockController.this.mCurPassword.length() > 0) {
                    StringBuffer stringBuffer = FactoryDoorLockController.this.mCurPassword;
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                FactoryDoorLockController.this.updatePassword();
            }
        });
        this.mConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryDoorLockController.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TextUtils.isEmpty(FactoryDoorLockController.this.mCurPassword)) {
                    return;
                }
                FactoryDoorLockController factoryDoorLockController = FactoryDoorLockController.this;
                if (factoryDoorLockController.checkPassword(factoryDoorLockController.mCurPassword.toString())) {
                    FactoryDoorLockController.this.mDoorLockSwitch.setClickable(true);
                    FactoryDoorLockController.this.popupWindow.dismiss();
                    return;
                }
                FactoryDoorLockController.this.mDoorLockSwitch.setClickable(false);
                StringBuffer stringBuffer = FactoryDoorLockController.this.mCurPassword;
                stringBuffer.delete(0, stringBuffer.length());
                FactoryDoorLockController.this.updatePassword();
                Toast.makeText(FactoryDoorLockController.this.mContext, R$string.lockpassword_invalid_password, 0).show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inputCode(String str) {
        if (this.mCurPassword.length() < PASSWORDLENGTH) {
            this.mCurPassword.append(str);
        }
        updatePassword();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePassword() {
        TextView textView = this.mText;
        if (textView != null) {
            textView.setText(this.mCurPassword.toString());
        }
    }

    public void setCheced(boolean z) {
        this.isChecked = z;
        Switch r0 = this.mDoorLockSwitch;
        if (r0 != null) {
            r0.setChecked(z);
            if (z) {
                return;
            }
            this.mDoorLockSwitch.setClickable(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkPassword(String str) {
        String str2;
        if (SettingsApplication.mCustomerid == 8) {
            str2 = SettingsApplication.mUi == 13 ? "6161" : "syu";
        } else {
            str2 = "0000";
        }
        return str.contains(str2);
    }
}
