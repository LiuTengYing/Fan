package com.android.settings.development;

import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class DevelopPinFragment extends Fragment {
    private static String TAG = "DevelopPinFragment";
    private ImageView clearCode;
    private ImageView mConfirm;
    public StringBuffer mCurPassword = new StringBuffer();
    private ImageView mPasswordLine1;
    private ImageView mPasswordLine2;
    private ImageView mPasswordLine3;
    private ImageView mPasswordLine4;
    private ImageView mPasswordLine5;
    private ImageView mPasswordLine6;
    private ImageView mPasswordLine7;
    private ImageView mPasswordLine8;
    private View mRootView;
    private TextView mTextPassword1;
    private TextView mTextPassword2;
    private TextView mTextPassword3;
    private TextView mTextPassword4;
    private TextView mTextPassword5;
    private TextView mTextPassword6;
    private TextView mTextPassword7;
    private TextView mTextPassword8;
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
    public static ArrayList<String> OtherLancherPassword = new ArrayList<>();
    public static ArrayList<String> OtherPassword = new ArrayList<>();
    public static String PASSWORD = "7870";
    public static String LancherPASSWORD = "8086";
    private static int mUi = -1;
    public static int PASSWORDLENGTH = 4;
    public static boolean isCangda = false;
    public static boolean is_ToBeFYT = false;
    private static int mIdCustomer = 2;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (SystemProperties.getInt("sys.lsec.navi_bar_height", 0) > 200 && SystemProperties.getBoolean("sys.lsec.showNavi", false)) {
            this.mRootView = layoutInflater.inflate(R$xml.factory_pin_password_layout_navi_bar, viewGroup, false);
        } else if (SettingsApplication.mWidthFix > SettingsApplication.mHeightFix) {
            this.mRootView = layoutInflater.inflate(R$xml.factory_pin_password_layout, viewGroup, false);
        } else {
            this.mRootView = layoutInflater.inflate(R$xml.factory_pin_password_layout_h, viewGroup, false);
        }
        initViews();
        return this.mRootView;
    }

    private void initViews() {
        this.pinCode0 = (ImageView) this.mRootView.findViewById(R$id.pin_code0);
        this.pinCode1 = (ImageView) this.mRootView.findViewById(R$id.pin_code1);
        this.pinCode2 = (ImageView) this.mRootView.findViewById(R$id.pin_code2);
        this.pinCode3 = (ImageView) this.mRootView.findViewById(R$id.pin_code3);
        this.pinCode4 = (ImageView) this.mRootView.findViewById(R$id.pin_code4);
        this.pinCode5 = (ImageView) this.mRootView.findViewById(R$id.pin_code5);
        this.pinCode6 = (ImageView) this.mRootView.findViewById(R$id.pin_code6);
        this.pinCode7 = (ImageView) this.mRootView.findViewById(R$id.pin_code7);
        this.pinCode8 = (ImageView) this.mRootView.findViewById(R$id.pin_code8);
        this.pinCode9 = (ImageView) this.mRootView.findViewById(R$id.pin_code9);
        this.clearCode = (ImageView) this.mRootView.findViewById(R$id.pin_btn_delete);
        this.mConfirm = (ImageView) this.mRootView.findViewById(R$id.pin_btn_confirm);
        this.mTextPassword1 = (TextView) this.mRootView.findViewById(R$id.text_password_1);
        this.mTextPassword2 = (TextView) this.mRootView.findViewById(R$id.text_password_2);
        this.mTextPassword3 = (TextView) this.mRootView.findViewById(R$id.text_password_3);
        this.mTextPassword4 = (TextView) this.mRootView.findViewById(R$id.text_password_4);
        this.mTextPassword5 = (TextView) this.mRootView.findViewById(R$id.text_password_5);
        this.mTextPassword6 = (TextView) this.mRootView.findViewById(R$id.text_password_6);
        this.mTextPassword7 = (TextView) this.mRootView.findViewById(R$id.text_password_7);
        this.mTextPassword8 = (TextView) this.mRootView.findViewById(R$id.text_password_8);
        this.mPasswordLine1 = (ImageView) this.mRootView.findViewById(R$id.password_line_1);
        this.mPasswordLine2 = (ImageView) this.mRootView.findViewById(R$id.password_line_2);
        this.mPasswordLine3 = (ImageView) this.mRootView.findViewById(R$id.password_line_3);
        this.mPasswordLine4 = (ImageView) this.mRootView.findViewById(R$id.password_line_4);
        this.mPasswordLine5 = (ImageView) this.mRootView.findViewById(R$id.password_line_5);
        this.mPasswordLine6 = (ImageView) this.mRootView.findViewById(R$id.password_line_6);
        this.mPasswordLine7 = (ImageView) this.mRootView.findViewById(R$id.password_line_7);
        this.mPasswordLine8 = (ImageView) this.mRootView.findViewById(R$id.password_line_8);
        initListener();
        initPassword();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        Toolbar toolbar;
        FragmentActivity activity = getActivity();
        if ((activity instanceof SettingsBaseActivity) && (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) != null) {
            toolbar.setTitle(getResources().getString(R$string.development_settings_title));
        }
        super.onResume();
    }

    private void initListener() {
        this.pinCode0.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("0");
            }
        });
        this.pinCode1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("1");
            }
        });
        this.pinCode2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("2");
            }
        });
        this.pinCode3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("3");
            }
        });
        this.pinCode4.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("4");
            }
        });
        this.pinCode5.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("5");
            }
        });
        this.pinCode6.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("6");
            }
        });
        this.pinCode7.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("7");
            }
        });
        this.pinCode8.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("8");
            }
        });
        this.pinCode9.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DevelopPinFragment.this.inputCode("9");
            }
        });
        this.clearCode.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DevelopPinFragment.this.mCurPassword.length() > 0) {
                    StringBuffer stringBuffer = DevelopPinFragment.this.mCurPassword;
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                DevelopPinFragment.this.updatePassword();
            }
        });
        this.mConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DevelopPinFragment.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TextUtils.isEmpty(DevelopPinFragment.this.mCurPassword)) {
                    return;
                }
                String str = DevelopPinFragment.TAG;
                Log.d(str, "onClick: " + DevelopPinFragment.this.mCurPassword.toString() + "    password ===" + DevelopPinFragment.PASSWORD);
                DevelopPinFragment developPinFragment = DevelopPinFragment.this;
                if (developPinFragment.checkPassword(developPinFragment.mCurPassword.toString())) {
                    DevelopPinFragment.this.showFragment();
                    return;
                }
                StringBuffer stringBuffer = DevelopPinFragment.this.mCurPassword;
                stringBuffer.delete(0, stringBuffer.length());
                DevelopPinFragment.this.clearAllText();
                Toast.makeText(DevelopPinFragment.this.getContext(), R$string.lockpassword_invalid_password, 0).show();
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
        clearAllText();
        int length = this.mCurPassword.length();
        if (length == 1) {
            this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(0)));
        } else if (length == 2) {
            this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(0)));
            this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(1)));
        } else if (length == 3) {
            this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(0)));
            this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(1)));
            this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(2)));
        } else if (length != 4) {
        } else {
            this.mTextPassword5.setText(String.valueOf(this.mCurPassword.charAt(0)));
            this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(1)));
            this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(2)));
            this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(3)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearAllText() {
        this.mTextPassword1.setText("");
        this.mTextPassword2.setText("");
        this.mTextPassword3.setText("");
        this.mTextPassword4.setText("");
        this.mTextPassword5.setText("");
        this.mTextPassword6.setText("");
        this.mTextPassword7.setText("");
        this.mTextPassword8.setText("");
    }

    private void showPasswordLength() {
        int i = PASSWORDLENGTH;
        if (i == 4) {
            this.mTextPassword1.setVisibility(8);
            this.mTextPassword2.setVisibility(8);
            this.mTextPassword3.setVisibility(8);
            this.mTextPassword4.setVisibility(8);
            this.mPasswordLine1.setVisibility(8);
            this.mPasswordLine2.setVisibility(8);
            this.mPasswordLine3.setVisibility(8);
            this.mPasswordLine4.setVisibility(8);
        } else if (i == 6) {
            this.mTextPassword1.setVisibility(0);
            this.mTextPassword2.setVisibility(0);
            this.mTextPassword3.setVisibility(8);
            this.mTextPassword4.setVisibility(8);
            this.mPasswordLine1.setVisibility(0);
            this.mPasswordLine2.setVisibility(0);
            this.mPasswordLine3.setVisibility(8);
            this.mPasswordLine4.setVisibility(8);
        } else if (i == 8) {
            this.mTextPassword1.setVisibility(0);
            this.mTextPassword2.setVisibility(0);
            this.mTextPassword3.setVisibility(0);
            this.mTextPassword4.setVisibility(0);
            this.mPasswordLine1.setVisibility(0);
            this.mPasswordLine2.setVisibility(0);
            this.mPasswordLine3.setVisibility(0);
            this.mPasswordLine4.setVisibility(0);
        }
    }

    private void initPassword() {
        mUi = SystemProperties.getInt("ro.fyt.uiid", 0);
        mIdCustomer = SystemProperties.getInt("ro.build.fytmanufacturer", 2);
        is_ToBeFYT = SystemProperties.getBoolean("persist.fyt.setting_ui.syu", false);
        PASSWORD = "7870";
        showPasswordLength();
    }

    public boolean checkPassword(String str) {
        if (str != null && !"".equals(str)) {
            if (PASSWORD.equals(str)) {
                return true;
            }
            ArrayList<String> arrayList = OtherPassword;
            if (arrayList != null && arrayList.size() > 0) {
                for (int i = 0; i < OtherPassword.size(); i++) {
                    if (str.equals(OtherPassword.get(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFragment() {
        ((SettingsActivity) getActivity()).switchToFragment(new DevelopmentSettingsDashboardFragment(), R$string.development_settings_title, null);
    }
}
