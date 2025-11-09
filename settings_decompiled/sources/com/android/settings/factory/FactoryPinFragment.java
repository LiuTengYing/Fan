package com.android.settings.factory;

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
import androidx.window.R;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import com.syu.ipcself.module.main.Main;
import com.syu.util.LogScreen;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class FactoryPinFragment extends Fragment {
    private static String TAG = "FactoryPinFragment";
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
    public static String PASSWORD = "3368";
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

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        Toolbar toolbar;
        FragmentActivity activity = getActivity();
        if ((activity instanceof SettingsBaseActivity) && (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) != null) {
            toolbar.setTitle(getResources().getString(R$string.factory_dashboard_title));
        }
        super.onResume();
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

    private void initListener() {
        this.pinCode0.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("0");
            }
        });
        this.pinCode1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("1");
            }
        });
        this.pinCode2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("2");
            }
        });
        this.pinCode3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("3");
            }
        });
        this.pinCode4.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("4");
            }
        });
        this.pinCode5.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("5");
            }
        });
        this.pinCode6.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("6");
            }
        });
        this.pinCode7.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("7");
            }
        });
        this.pinCode8.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("8");
            }
        });
        this.pinCode9.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FactoryPinFragment.this.inputCode("9");
            }
        });
        this.clearCode.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FactoryPinFragment.this.mCurPassword.length() > 0) {
                    StringBuffer stringBuffer = FactoryPinFragment.this.mCurPassword;
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                FactoryPinFragment.this.updatePassword();
            }
        });
        this.mConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.FactoryPinFragment.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TextUtils.isEmpty(FactoryPinFragment.this.mCurPassword)) {
                    return;
                }
                String str = FactoryPinFragment.TAG;
                Log.d(str, "onClick: " + FactoryPinFragment.this.mCurPassword.toString() + "    password ===" + FactoryPinFragment.PASSWORD);
                FactoryPinFragment factoryPinFragment = FactoryPinFragment.this;
                if (factoryPinFragment.checkPassword(factoryPinFragment.mCurPassword.toString())) {
                    FactoryPinFragment.this.showFragment();
                    return;
                }
                StringBuffer stringBuffer = FactoryPinFragment.this.mCurPassword;
                stringBuffer.delete(0, stringBuffer.length());
                FactoryPinFragment.this.clearAllText();
                Toast.makeText(FactoryPinFragment.this.getContext(), R$string.lockpassword_invalid_password, 0).show();
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
        switch (this.mCurPassword.length()) {
            case 1:
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(0)));
                return;
            case 2:
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(1)));
                return;
            case 3:
                this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(1)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(2)));
                return;
            case 4:
                this.mTextPassword5.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(1)));
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(2)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(3)));
                return;
            case 5:
                this.mTextPassword4.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword5.setText(String.valueOf(this.mCurPassword.charAt(1)));
                this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(2)));
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(3)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(4)));
                return;
            case 6:
                this.mTextPassword3.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword4.setText(String.valueOf(this.mCurPassword.charAt(1)));
                this.mTextPassword5.setText(String.valueOf(this.mCurPassword.charAt(2)));
                this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(3)));
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(4)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(5)));
                return;
            case 7:
                this.mTextPassword2.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword3.setText(String.valueOf(this.mCurPassword.charAt(1)));
                this.mTextPassword4.setText(String.valueOf(this.mCurPassword.charAt(2)));
                this.mTextPassword5.setText(String.valueOf(this.mCurPassword.charAt(3)));
                this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(4)));
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(5)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(6)));
                return;
            case 8:
                this.mTextPassword1.setText(String.valueOf(this.mCurPassword.charAt(0)));
                this.mTextPassword2.setText(String.valueOf(this.mCurPassword.charAt(1)));
                this.mTextPassword3.setText(String.valueOf(this.mCurPassword.charAt(2)));
                this.mTextPassword4.setText(String.valueOf(this.mCurPassword.charAt(3)));
                this.mTextPassword5.setText(String.valueOf(this.mCurPassword.charAt(4)));
                this.mTextPassword6.setText(String.valueOf(this.mCurPassword.charAt(5)));
                this.mTextPassword7.setText(String.valueOf(this.mCurPassword.charAt(6)));
                this.mTextPassword8.setText(String.valueOf(this.mCurPassword.charAt(7)));
                return;
            default:
                return;
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
        if (i == 2) {
            this.mTextPassword1.setVisibility(8);
            this.mTextPassword2.setVisibility(8);
            this.mTextPassword3.setVisibility(8);
            this.mTextPassword4.setVisibility(8);
            this.mTextPassword5.setVisibility(8);
            this.mTextPassword6.setVisibility(8);
            this.mPasswordLine1.setVisibility(8);
            this.mPasswordLine2.setVisibility(8);
            this.mPasswordLine3.setVisibility(8);
            this.mPasswordLine4.setVisibility(8);
            this.mPasswordLine5.setVisibility(8);
            this.mPasswordLine6.setVisibility(8);
        } else if (i == 3) {
            this.mTextPassword1.setVisibility(8);
            this.mTextPassword2.setVisibility(8);
            this.mTextPassword3.setVisibility(8);
            this.mTextPassword4.setVisibility(8);
            this.mTextPassword5.setVisibility(8);
            this.mPasswordLine1.setVisibility(8);
            this.mPasswordLine2.setVisibility(8);
            this.mPasswordLine3.setVisibility(8);
            this.mPasswordLine4.setVisibility(8);
            this.mPasswordLine5.setVisibility(8);
        } else if (i == 4) {
            this.mTextPassword1.setVisibility(8);
            this.mTextPassword2.setVisibility(8);
            this.mTextPassword3.setVisibility(8);
            this.mTextPassword4.setVisibility(8);
            this.mPasswordLine1.setVisibility(8);
            this.mPasswordLine2.setVisibility(8);
            this.mPasswordLine3.setVisibility(8);
            this.mPasswordLine4.setVisibility(8);
        } else if (i == 6) {
            this.mTextPassword1.setVisibility(8);
            this.mTextPassword2.setVisibility(8);
            this.mTextPassword3.setVisibility(0);
            this.mTextPassword4.setVisibility(0);
            this.mPasswordLine1.setVisibility(8);
            this.mPasswordLine2.setVisibility(8);
            this.mPasswordLine3.setVisibility(0);
            this.mPasswordLine4.setVisibility(0);
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
        boolean z = SystemProperties.getBoolean("persist.fyt.setting_ui.syu", false);
        is_ToBeFYT = z;
        switch (mIdCustomer) {
            case 1:
                int i = mUi;
                if (i == 9) {
                    PASSWORD = "8888";
                } else if (i == 10 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20) {
                    PASSWORD = "1234";
                } else if (i == 1948) {
                    PASSWORD = "1948";
                } else if (i == 8888) {
                    PASSWORD = "8888";
                }
                if (z) {
                    PASSWORD = "168";
                    PASSWORDLENGTH = 3;
                    break;
                }
                break;
            case 3:
                PASSWORD = "8899";
                break;
            case 5:
            case 60:
                PASSWORD = "2016";
                LancherPASSWORD = "1947";
                break;
            case 6:
            case R.styleable.AppCompatTheme_toolbarStyle /* 113 */:
                PASSWORD = "8888";
                break;
            case 8:
                int i2 = Main.mConf_PlatForm;
                if (i2 == 5) {
                    if (mUi == 13) {
                        PASSWORD = "6161";
                    } else {
                        PASSWORD = "3368";
                    }
                    LancherPASSWORD = "6161";
                } else if (i2 == 6) {
                    if (mUi == 30) {
                        PASSWORD = "3534";
                        LancherPASSWORD = "3534";
                    }
                } else {
                    LancherPASSWORD = "0808";
                }
                if (mUi == 22) {
                    PASSWORD = "6161";
                    LancherPASSWORD = "6161";
                    break;
                }
                break;
            case 21:
                PASSWORD = "126";
                PASSWORDLENGTH = 3;
                break;
            case 25:
                PASSWORD = "333111";
                PASSWORDLENGTH = 6;
                break;
            case 31:
                if (mUi == 10) {
                    PASSWORD = "1453";
                    LancherPASSWORD = "1453";
                    break;
                }
                break;
            case 34:
                if (mUi == 269) {
                    PASSWORD = "0269";
                    break;
                } else {
                    PASSWORD = "3368";
                    break;
                }
            case 37:
            case 56:
                PASSWORD = "0000";
                break;
            case 51:
            case R.styleable.AppCompatTheme_textColorSearchUrl /* 111 */:
                PASSWORD = "123456";
                PASSWORDLENGTH = 6;
                break;
            case 54:
                PASSWORD = "3153";
                break;
            case 61:
                PASSWORD = "1234";
                break;
            case 62:
                if (mUi == 7) {
                    PASSWORD = "1314";
                    break;
                }
                break;
            case 63:
            case 94:
                if (Main.mConf_PlatForm == 8) {
                    PASSWORD = "2691";
                    LancherPASSWORD = "2691";
                    break;
                }
                break;
            case 68:
                LancherPASSWORD = "666666";
                PASSWORD = "666666";
                PASSWORDLENGTH = 6;
                break;
            case 75:
                if (mUi == 2) {
                    PASSWORD = "51243204";
                    PASSWORDLENGTH = 8;
                    break;
                }
                break;
            case 86:
            case 123:
                if (mUi == 36) {
                    PASSWORD = "3368";
                    LancherPASSWORD = "3368";
                    break;
                } else {
                    PASSWORD = "7253";
                    LancherPASSWORD = "7253";
                    break;
                }
            case 91:
                LancherPASSWORD = "1818";
                break;
            case 95:
            case 107:
                PASSWORD = "168";
                LancherPASSWORD = "168";
                PASSWORDLENGTH = 3;
                break;
            case 100:
                if (mUi == 3) {
                    PASSWORD = "3437";
                    LancherPASSWORD = "3437";
                    break;
                } else {
                    PASSWORD = "6161";
                    OtherPassword.add("3737");
                    LancherPASSWORD = "6161";
                    OtherLancherPassword.add("3737");
                    break;
                }
            case 103:
                PASSWORD = "1528";
                LancherPASSWORD = "1528";
                break;
            case 108:
                if (SystemProperties.getInt("sys.fyt.platform", 0) == 6316) {
                    LancherPASSWORD = "8181";
                    break;
                } else {
                    LancherPASSWORD = "7171";
                    break;
                }
            case 116:
            case 132:
                if (mUi == 2) {
                    PASSWORD = "0310";
                    break;
                }
                break;
            case 118:
                OtherPassword.add("8888");
                break;
            case 121:
                PASSWORD = "0755";
                break;
        }
        if (SystemProperties.getBoolean("sys.lsec.hide.home_rencents", false)) {
            PASSWORD = "6638";
            LancherPASSWORD = "6638";
            PASSWORDLENGTH = 4;
        }
        if (isCangda) {
            PASSWORD = "6688";
        }
        if (SystemProperties.getBoolean("persist.fyt.isxinke", false)) {
            PASSWORD = "1579";
        }
        String str = SystemProperties.get("persist.fyt.fytpassword", PASSWORD);
        if (isNumeric(str)) {
            PASSWORD = str;
            PASSWORDLENGTH = str.length();
        }
        LogScreen.show("password ==" + PASSWORD + "   " + PASSWORDLENGTH);
        showPasswordLength();
    }

    public boolean isNumeric(String str) {
        int length = str.length();
        do {
            length--;
            if (length < 0) {
                return true;
            }
        } while (Character.isDigit(str.charAt(length)));
        return false;
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
        ((SettingsActivity) getActivity()).switchToFragment(new FactoryDashboardFragment(), R$string.factory_dashboard_title, null);
    }
}
