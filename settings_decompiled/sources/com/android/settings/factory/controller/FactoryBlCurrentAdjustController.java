package com.android.settings.factory.controller;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.factory.dialog.FactoryBlCurrentAdjustDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class FactoryBlCurrentAdjustController extends AbstractPreferenceController implements LifecycleObserver {
    public static int PASSWORDLENGTH = 4;
    private static int brightnessPre = 0;
    private static boolean is_ToBeFYT = false;
    private ImageView clearCode;
    private ImageView mConfirm;
    private View mContentView;
    private Context mContext;
    public StringBuffer mCurPassword;
    private Fragment mFragment;
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
        return "bl_current_adjust";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryBlCurrentAdjustController(Context context, Lifecycle lifecycle, Fragment fragment) {
        super(context);
        this.mCurPassword = new StringBuffer();
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mFragment = fragment;
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
        is_ToBeFYT = SystemProperties.getBoolean("persist.fyt.setting_ui.syu", false);
        this.mText = (TextView) this.mPreference.findViewById(R$id.door_lock_password);
        initListener();
    }

    private void initListener() {
        this.mText.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryBlCurrentAdjustController.this.popupWindow != null && FactoryBlCurrentAdjustController.this.popupWindow.isShowing()) {
                    FactoryBlCurrentAdjustController.this.popupWindow.dismiss();
                } else {
                    FactoryBlCurrentAdjustController.this.showPassword();
                }
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
        this.pinCode0.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("0");
            }
        });
        this.pinCode1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("1");
            }
        });
        this.pinCode2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("2");
            }
        });
        this.pinCode3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("3");
            }
        });
        this.pinCode4.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("4");
            }
        });
        this.pinCode5.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("5");
            }
        });
        this.pinCode6.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("6");
            }
        });
        this.pinCode7.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("7");
            }
        });
        this.pinCode8.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("8");
            }
        });
        this.pinCode9.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryBlCurrentAdjustController.this.inputCode("9");
            }
        });
        this.clearCode.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryBlCurrentAdjustController.this.mCurPassword.length() > 0) {
                    StringBuffer stringBuffer = FactoryBlCurrentAdjustController.this.mCurPassword;
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                FactoryBlCurrentAdjustController.this.updatePassword();
            }
        });
        this.mConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.controller.FactoryBlCurrentAdjustController.13
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TextUtils.isEmpty(FactoryBlCurrentAdjustController.this.mCurPassword)) {
                    return;
                }
                FactoryBlCurrentAdjustController factoryBlCurrentAdjustController = FactoryBlCurrentAdjustController.this;
                if (factoryBlCurrentAdjustController.checkPassword(factoryBlCurrentAdjustController.mCurPassword.toString())) {
                    FactoryBlCurrentAdjustController.this.popupWindow.dismiss();
                    StringBuffer stringBuffer = FactoryBlCurrentAdjustController.this.mCurPassword;
                    stringBuffer.delete(0, stringBuffer.length());
                    FactoryBlCurrentAdjustController.this.updatePassword();
                    FactoryBlCurrentAdjustDialogFragment.show(FactoryBlCurrentAdjustController.this.mFragment, FactoryBlCurrentAdjustController.this.mContext.getString(R$string.factory_bl_current_adjust), FactoryBlCurrentAdjustController.brightnessPre);
                    return;
                }
                StringBuffer stringBuffer2 = FactoryBlCurrentAdjustController.this.mCurPassword;
                stringBuffer2.delete(0, stringBuffer2.length());
                FactoryBlCurrentAdjustController.this.updatePassword();
                Toast.makeText(FactoryBlCurrentAdjustController.this.mContext, R$string.lockpassword_invalid_password, 0).show();
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkPassword(String str) {
        String str2;
        if (SettingsApplication.mCustomerid == 8) {
            int i = SettingsApplication.mUi;
            str2 = i == 13 ? "6161" : i == 30 ? "3534" : "3368";
        } else {
            str2 = "5768";
        }
        return str.contains((SettingsApplication.mCustomerid == 95 || is_ToBeFYT) ? "168" : "168");
    }

    public void setBrightness(int i) {
        brightnessPre = i;
    }
}
