package com.android.settings.sim;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import java.util.List;
/* loaded from: classes.dex */
public class SimDialogActivity extends FragmentActivity {
    public static String DIALOG_TYPE_KEY = "dialog_type";
    public static String PREFERRED_SIM = "preferred_sim";
    public static String RESULT_SUB_ID = "result_sub_id";
    private static String TAG = "SimDialogActivity";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        showOrUpdateDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showOrUpdateDialog();
    }

    private int getProgressState() {
        return getSharedPreferences("sim_action_dialog_prefs", 0).getInt("progress_state", 0);
    }

    private void showOrUpdateDialog() {
        int intExtra = getIntent().getIntExtra(DIALOG_TYPE_KEY, -1);
        if (intExtra == 5) {
            finishAndRemoveTask();
        } else if (intExtra == 3 && getProgressState() == 1) {
            Log.d(TAG, "Finish the sim dialog since the sim action dialog is showing the progress");
            finish();
        } else {
            String num = Integer.toString(intExtra);
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            SimDialogFragment simDialogFragment = (SimDialogFragment) supportFragmentManager.findFragmentByTag(num);
            if (simDialogFragment == null) {
                createFragment(intExtra).show(supportFragmentManager, num);
            } else {
                simDialogFragment.updateDialog();
            }
        }
    }

    private SimDialogFragment createFragment(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (!getIntent().hasExtra(PREFERRED_SIM)) {
                            throw new IllegalArgumentException("Missing required extra " + PREFERRED_SIM);
                        }
                        return PreferredSimDialogFragment.newInstance();
                    } else if (i == 4) {
                        return SimListDialogFragment.newInstance(i, R$string.select_sim_for_sms, false, false);
                    } else {
                        throw new IllegalArgumentException("Invalid dialog type " + i + " sent.");
                    }
                }
                return SimListDialogFragment.newInstance(i, R$string.select_sim_for_sms, true, false);
            }
            return SimListDialogFragment.newInstance(i, R$string.select_sim_for_calls, true, false);
        }
        return getDataPickDialogFramgent();
    }

    private SimDialogFragment getDataPickDialogFramgent() {
        if (SubscriptionManager.getDefaultDataSubscriptionId() == -1) {
            return SimListDialogFragment.newInstance(0, R$string.select_sim_for_data, false, true);
        }
        return SelectSpecificDataSimDialogFragment.newInstance();
    }

    public void onSubscriptionSelected(int i, int i2) {
        if (getSupportFragmentManager().findFragmentByTag(Integer.toString(i)) == null) {
            Log.w(TAG, "onSubscriptionSelected ignored because stored fragment was null");
        } else if (i == 0) {
            setDefaultDataSubId(i2);
        } else if (i == 1) {
            setDefaultCallsSubId(i2);
        } else if (i == 2) {
            setDefaultSmsSubId(i2);
        } else if (i == 3) {
            setPreferredSim(i2);
        } else if (i == 4) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_SUB_ID, i2);
            setResult(-1, intent);
        } else {
            throw new IllegalArgumentException("Invalid dialog type " + i + " sent.");
        }
    }

    public void onFragmentDismissed(SimDialogFragment simDialogFragment) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 1 && fragments.get(0) == simDialogFragment) {
            finishAndRemoveTask();
        }
    }

    private void setDefaultDataSubId(int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        ((SubscriptionManager) getSystemService(SubscriptionManager.class)).setDefaultDataSubId(i);
        if (i != -1) {
            createForSubscriptionId.setDataEnabled(true);
            Toast.makeText(this, R$string.data_switch_started, 1).show();
        }
    }

    private void setDefaultCallsSubId(int i) {
        ((TelecomManager) getSystemService(TelecomManager.class)).setUserSelectedOutgoingPhoneAccount(subscriptionIdToPhoneAccountHandle(i));
    }

    private void setDefaultSmsSubId(int i) {
        ((SubscriptionManager) getSystemService(SubscriptionManager.class)).setDefaultSmsSubId(i);
    }

    private void setPreferredSim(int i) {
        setDefaultDataSubId(i);
        setDefaultSmsSubId(i);
        setDefaultCallsSubId(i);
    }

    private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(int i) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TelephonyManager.class);
        for (PhoneAccountHandle phoneAccountHandle : ((TelecomManager) getSystemService(TelecomManager.class)).getCallCapablePhoneAccounts()) {
            if (i == telephonyManager.getSubscriptionId(phoneAccountHandle)) {
                return phoneAccountHandle;
            }
        }
        return null;
    }
}
