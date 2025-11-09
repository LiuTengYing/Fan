package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.PinResult;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.settings.EditPinPreference;
import com.android.settings.network.ProxySubscriptionManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class IccLockSettings extends SettingsPreferenceFragment implements EditPinPreference.OnPinEnteredListener {
    private String mError;
    private ListView mListView;
    private String mNewPin;
    private String mOldPin;
    private String mPin;
    private EditPinPreference mPinDialog;
    private SwitchPreference mPinToggle;
    private ProxySubscriptionManager mProxySubscriptionMgr;
    private Resources mRes;
    private int mSubId;
    private TabHost mTabHost;
    private TabWidget mTabWidget;
    private TelephonyManager mTelephonyManager;
    private boolean mToState;
    private int mDialogState = 0;
    private int mSlotId = -1;
    private Handler mHandler = new Handler() { // from class: com.android.settings.IccLockSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (IccLockSettings.this.getContext() != null && message.what == 102) {
                if (IccLockSettings.this.mSlotId == message.arg1 && IccLockSettings.this.mPinDialog != null && IccLockSettings.this.mPinDialog.getDialog() != null && IccLockSettings.this.mPinDialog.getDialog().isShowing()) {
                    IccLockSettings.this.mPinDialog.getDialog().dismiss();
                }
                IccLockSettings.this.initializeSubscriptions();
                IccLockSettings.this.updatePreferences();
            }
        }
    };
    private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.IccLockSettings.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                String stringExtra = intent.getStringExtra("ss");
                int intExtra = intent.getIntExtra("phone", Integer.MAX_VALUE);
                Log.d("IccLockSettings", "simState : " + stringExtra + " phoneId : " + intExtra);
                if ("ABSENT".equals(stringExtra)) {
                    IccLockSettings.this.mHandler.sendMessage(IccLockSettings.this.mHandler.obtainMessage(102, intExtra, 1));
                } else if ("LOADED".equals(stringExtra)) {
                    IccLockSettings.this.mHandler.sendMessage(IccLockSettings.this.mHandler.obtainMessage(102, intExtra, 10));
                }
            }
        }
    };
    private TabHost.OnTabChangeListener mTabListener = new TabHost.OnTabChangeListener() { // from class: com.android.settings.IccLockSettings.4
        @Override // android.widget.TabHost.OnTabChangeListener
        public void onTabChanged(String str) {
            IccLockSettings iccLockSettings = IccLockSettings.this;
            iccLockSettings.mSlotId = iccLockSettings.getSlotIndexFromTag(str);
            IccLockSettings.this.updatePreferences();
        }
    };
    private TabHost.TabContentFactory mEmptyTabContent = new TabHost.TabContentFactory() { // from class: com.android.settings.IccLockSettings.5
        @Override // android.widget.TabHost.TabContentFactory
        public View createTabContent(String str) {
            return new View(IccLockSettings.this.mTabHost.getContext());
        }
    };
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.IccLockSettings.6
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            Log.d("IccLockSettings", "onSubscriptionsChanged updateTabName");
            IccLockSettings.this.initializeSubscriptions();
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 56;
    }

    private boolean isIccLockEnabled() {
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        return createForSubscriptionId.isIccLockEnabled();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("need_search_icon_in_action_bar", false);
        setArguments(bundle2);
        if (Utils.isMonkeyRunning()) {
            finish();
            return;
        }
        getContext().registerReceiver(this.mSimStateReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
        ProxySubscriptionManager proxySubscriptionManager = ProxySubscriptionManager.getInstance(getContext());
        this.mProxySubscriptionMgr = proxySubscriptionManager;
        proxySubscriptionManager.setLifecycle(getLifecycle());
        this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        addPreferencesFromResource(R$xml.sim_lock_settings);
        this.mPinDialog = (EditPinPreference) findPreference("sim_pin");
        this.mPinToggle = (SwitchPreference) findPreference("sim_toggle");
        if (bundle != null) {
            if (bundle.containsKey("dialogState") && restoreDialogStates(bundle)) {
                Log.d("IccLockSettings", "onCreate: restore dialog for slotId=" + this.mSlotId + ", subId=" + this.mSubId);
            } else if (bundle.containsKey("currentTab") && restoreTabFocus(bundle)) {
                Log.d("IccLockSettings", "onCreate: restore focus on slotId=" + this.mSlotId + ", subId=" + this.mSubId);
            }
        }
        this.mPinDialog.setOnPinEnteredListener(this);
        getPreferenceScreen().setPersistent(false);
        this.mRes = getResources();
    }

    private boolean restoreDialogStates(Bundle bundle) {
        SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex;
        SubscriptionInfo activeSubscriptionInfo = this.mProxySubscriptionMgr.getActiveSubscriptionInfo(bundle.getInt("dialogSubId"));
        if (activeSubscriptionInfo == null || (visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(activeSubscriptionInfo.getSimSlotIndex())) == null || visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId() != activeSubscriptionInfo.getSubscriptionId()) {
            return false;
        }
        this.mSlotId = activeSubscriptionInfo.getSimSlotIndex();
        this.mSubId = activeSubscriptionInfo.getSubscriptionId();
        this.mDialogState = bundle.getInt("dialogState");
        this.mPin = bundle.getString("dialogPin");
        this.mError = bundle.getString("dialogError");
        this.mToState = bundle.getBoolean("enableState");
        int i = this.mDialogState;
        if (i == 3) {
            this.mOldPin = bundle.getString("oldPinCode");
            return true;
        } else if (i != 4) {
            return true;
        } else {
            this.mOldPin = bundle.getString("oldPinCode");
            this.mNewPin = bundle.getString("newPinCode");
            return true;
        }
    }

    private boolean restoreTabFocus(Bundle bundle) {
        try {
            SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(Integer.parseInt(bundle.getString("currentTab")));
            if (visibleSubscriptionInfoForSimSlotIndex == null) {
                return false;
            }
            this.mSlotId = visibleSubscriptionInfoForSimSlotIndex.getSimSlotIndex();
            this.mSubId = visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId();
            TabHost tabHost = this.mTabHost;
            if (tabHost != null) {
                tabHost.setCurrentTabByTag(getTagForSlotId(this.mSlotId));
                return true;
            }
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int activeSubscriptionInfoCountMax = this.mProxySubscriptionMgr.getActiveSubscriptionInfoCountMax();
        ArrayList<SubscriptionInfo> arrayList = new ArrayList();
        for (int i = 0; i < activeSubscriptionInfoCountMax; i++) {
            SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(i);
            if (visibleSubscriptionInfoForSimSlotIndex != null) {
                arrayList.add(visibleSubscriptionInfoForSimSlotIndex);
            }
        }
        if (arrayList.size() == 0) {
            Log.e("IccLockSettings", "onCreateView: no sim info");
            return super.onCreateView(layoutInflater, viewGroup, bundle);
        }
        if (this.mSlotId < 0) {
            this.mSlotId = ((SubscriptionInfo) arrayList.get(0)).getSimSlotIndex();
            this.mSubId = ((SubscriptionInfo) arrayList.get(0)).getSubscriptionId();
            Log.d("IccLockSettings", "onCreateView: default slotId=" + this.mSlotId + ", subId=" + this.mSubId);
        }
        View inflate = layoutInflater.inflate(R$layout.icc_lock_tabs, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R$id.prefs_container);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, viewGroup2, false);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        this.mTabHost = (TabHost) inflate.findViewById(16908306);
        this.mTabWidget = (TabWidget) inflate.findViewById(16908307);
        this.mListView = (ListView) inflate.findViewById(16908298);
        this.mTabHost.setup();
        this.mTabHost.setOnTabChangedListener(this.mTabListener);
        this.mTabHost.clearAllTabs();
        if (arrayList.size() > 1) {
            for (SubscriptionInfo subscriptionInfo : arrayList) {
                this.mTabHost.addTab(buildTabSpec(String.valueOf(subscriptionInfo.getSimSlotIndex()), String.valueOf(subscriptionInfo.getDisplayName())));
            }
            if (bundle != null && bundle.containsKey("currentTab")) {
                this.mTabHost.setCurrentTabByTag(bundle.getString("currentTab"));
            }
        }
        this.mSubId = ((SubscriptionInfo) arrayList.get(0)).getSubscriptionId();
        return inflate;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updatePreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreferences() {
        int i;
        if (Utils.isMonkeyRunning() || getContext() == null) {
            return;
        }
        SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(this.mSlotId);
        int subscriptionId = visibleSubscriptionInfoForSimSlotIndex != null ? visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId() : -1;
        if (this.mSubId != subscriptionId) {
            this.mSubId = subscriptionId;
            resetDialogState();
            EditPinPreference editPinPreference = this.mPinDialog;
            if (editPinPreference != null && editPinPreference.isDialogOpen()) {
                this.mPinDialog.getDialog().dismiss();
            }
        }
        boolean z = false;
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(this.mSubId);
            this.mTelephonyManager = createForSubscriptionId;
            i = createForSubscriptionId.getSimState();
        } else {
            i = 0;
        }
        EditPinPreference editPinPreference2 = this.mPinDialog;
        if (editPinPreference2 != null) {
            editPinPreference2.setEnabled((visibleSubscriptionInfoForSimSlotIndex == null || i == 4) ? false : true);
        }
        SwitchPreference switchPreference = this.mPinToggle;
        if (switchPreference != null) {
            if (visibleSubscriptionInfoForSimSlotIndex != null && i != 4) {
                z = true;
            }
            switchPreference.setEnabled(z);
            if (visibleSubscriptionInfoForSimSlotIndex != null) {
                this.mPinToggle.setChecked(isIccLockEnabled());
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        SubscriptionManager.from(getContext()).addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        if (this.mDialogState != 0) {
            showPinDialog();
        } else {
            resetDialogState();
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        SubscriptionManager.from(getContext()).removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (Utils.isMonkeyRunning() || this.mSimStateReceiver == null) {
            return;
        }
        getContext().unregisterReceiver(this.mSimStateReceiver);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_icc_lock;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        if (this.mPinDialog.isDialogOpen()) {
            bundle.putInt("dialogSubId", this.mSubId);
            bundle.putInt("dialogState", this.mDialogState);
            bundle.putString("dialogPin", this.mPinDialog.getEditText().getText().toString());
            bundle.putString("dialogError", this.mError);
            bundle.putBoolean("enableState", this.mToState);
            int i = this.mDialogState;
            if (i == 3) {
                bundle.putString("oldPinCode", this.mOldPin);
            } else if (i == 4) {
                bundle.putString("oldPinCode", this.mOldPin);
                bundle.putString("newPinCode", this.mNewPin);
            }
        } else {
            super.onSaveInstanceState(bundle);
        }
        TabHost tabHost = this.mTabHost;
        if (tabHost != null) {
            bundle.putString("currentTab", tabHost.getCurrentTabTag());
        }
    }

    private void showPinDialog() {
        if (this.mDialogState == 0) {
            return;
        }
        if (!hasActiveSub()) {
            Log.d("IccLockSettings", "Do not show pin dialog because no active sub.");
            return;
        }
        setDialogValues();
        this.mPinDialog.showPinDialog();
        EditText editText = this.mPinDialog.getEditText();
        if (editText != null) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
        }
        if (TextUtils.isEmpty(this.mPin) || editText == null) {
            return;
        }
        editText.setSelection(this.mPin.length());
    }

    private void setDialogValues() {
        String string;
        String string2;
        int remainTimes;
        int i;
        this.mPinDialog.setText(this.mPin);
        Log.d("IccLockSettings", "mDialogState = " + this.mDialogState);
        int i2 = this.mDialogState;
        if (i2 == 1) {
            string = this.mRes.getString(R$string.sim_enter_pin);
            EditPinPreference editPinPreference = this.mPinDialog;
            if (this.mToState) {
                string2 = this.mRes.getString(R$string.sim_enable_sim_lock);
            } else {
                string2 = this.mRes.getString(R$string.sim_disable_sim_lock);
            }
            editPinPreference.setDialogTitle(string2);
        } else if (i2 == 2) {
            string = this.mRes.getString(R$string.sim_enter_old);
            this.mPinDialog.setDialogTitle(this.mRes.getString(R$string.sim_change_pin));
        } else if (i2 == 3) {
            string = this.mRes.getString(R$string.sim_enter_new);
            this.mPinDialog.setDialogTitle(this.mRes.getString(R$string.sim_change_pin));
        } else if (i2 != 4) {
            string = "";
        } else {
            string = this.mRes.getString(R$string.sim_reenter_new);
            this.mPinDialog.setDialogTitle(this.mRes.getString(R$string.sim_change_pin));
        }
        if (getRemainTimes() >= 0 && (i = this.mDialogState) != 3 && i != 4) {
            string = string + this.mRes.getString(R$string.attempts_remaining_times, Integer.valueOf(remainTimes));
        }
        if (this.mError != null) {
            string = this.mError + "\n" + string;
            this.mError = null;
        }
        this.mPinDialog.setDialogMessage(string);
    }

    @Override // com.android.settings.EditPinPreference.OnPinEnteredListener
    public void onPinEntered(EditPinPreference editPinPreference, boolean z) {
        if (!z) {
            resetDialogState();
            return;
        }
        String text = editPinPreference.getText();
        this.mPin = text;
        if (!reasonablePin(text)) {
            this.mError = this.mRes.getString(R$string.sim_bad_pin);
            this.mPin = null;
            showPinDialog();
            return;
        }
        int i = this.mDialogState;
        if (i == 1) {
            tryChangeIccLockState();
        } else if (i == 2) {
            this.mOldPin = this.mPin;
            this.mDialogState = 3;
            this.mError = null;
            this.mPin = null;
            showPinDialog();
        } else if (i == 3) {
            this.mNewPin = this.mPin;
            this.mDialogState = 4;
            this.mPin = null;
            showPinDialog();
        } else if (i != 4) {
        } else {
            if (!this.mPin.equals(this.mNewPin)) {
                this.mError = this.mRes.getString(R$string.sim_pins_dont_match);
                this.mDialogState = 3;
                this.mPin = null;
                showPinDialog();
                return;
            }
            this.mError = null;
            tryChangePin();
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        SwitchPreference switchPreference = this.mPinToggle;
        if (preference == switchPreference) {
            boolean isChecked = switchPreference.isChecked();
            this.mToState = isChecked;
            this.mPinToggle.setChecked(!isChecked);
            if (this.mDialogState != 0) {
                Log.d("IccLockSettings", "Wait for change sim pin done.");
                return true;
            }
            this.mPin = null;
            this.mDialogState = 1;
            showPinDialog();
        } else if (preference == this.mPinDialog) {
            if (this.mDialogState != 0) {
                Log.d("IccLockSettings", "Wait for enable/disable pin lock done.");
                return false;
            }
            this.mDialogState = 2;
            this.mPin = null;
            setDialogValues();
            return false;
        }
        return true;
    }

    private void tryChangeIccLockState() {
        new SetIccLockEnabled(this.mToState, this.mPin).execute(new Void[0]);
        this.mPinToggle.setEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SetIccLockEnabled extends AsyncTask<Void, Void, PinResult> {
        private final String mPin;
        private final boolean mState;

        private SetIccLockEnabled(boolean z, String str) {
            this.mState = z;
            this.mPin = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public PinResult doInBackground(Void... voidArr) {
            IccLockSettings iccLockSettings = IccLockSettings.this;
            iccLockSettings.mTelephonyManager = iccLockSettings.mTelephonyManager.createForSubscriptionId(IccLockSettings.this.mSubId);
            return IccLockSettings.this.mTelephonyManager.setIccLockEnabled(this.mState, this.mPin);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(PinResult pinResult) {
            IccLockSettings.this.iccLockChanged(pinResult.getResult() == 0, pinResult.getAttemptsRemaining());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void iccLockChanged(boolean z, int i) {
        Log.d("IccLockSettings", "iccLockChanged: success = " + z);
        if (z) {
            this.mPinToggle.setChecked(this.mToState);
            if (getContext() != null) {
                if (this.mToState) {
                    Toast.makeText(getContext(), this.mRes.getString(R$string.icc_pin_enabled, Integer.valueOf(this.mSlotId + 1)), 1).show();
                } else {
                    Toast.makeText(getContext(), this.mRes.getString(R$string.icc_pin_disabled, Integer.valueOf(this.mSlotId + 1)), 1).show();
                }
            }
        } else if (i >= 0) {
            Toast.makeText(getContext(), getPinPasswordErrorMessage(i), 1).show();
        } else if (this.mToState) {
            Toast.makeText(getContext(), this.mRes.getString(R$string.sim_pin_enable_failed), 1).show();
        } else {
            Toast.makeText(getContext(), this.mRes.getString(R$string.sim_pin_disable_failed), 1).show();
        }
        this.mPinToggle.setEnabled(true);
        resetDialogState();
    }

    private void createCustomTextToast(CharSequence charSequence) {
        final View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(17367363, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(16908299);
        textView.setText(charSequence);
        textView.setSingleLine(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        int absoluteGravity = Gravity.getAbsoluteGravity(getContext().getResources().getInteger(17694949), inflate.getContext().getResources().getConfiguration().getLayoutDirection());
        layoutParams.gravity = absoluteGravity;
        if ((absoluteGravity & 7) == 7) {
            layoutParams.horizontalWeight = 1.0f;
        }
        if ((absoluteGravity & R.styleable.AppCompatTheme_toolbarNavigationButtonStyle) == 112) {
            layoutParams.verticalWeight = 1.0f;
        }
        layoutParams.y = getContext().getResources().getDimensionPixelSize(17105610);
        layoutParams.height = -2;
        layoutParams.width = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = 16973828;
        layoutParams.type = 2017;
        layoutParams.setFitInsetsTypes(layoutParams.getFitInsetsTypes() & (~WindowInsets.Type.statusBars()));
        layoutParams.setTitle(charSequence);
        layoutParams.flags = 152;
        final WindowManager windowManager = (WindowManager) getSystemService("window");
        windowManager.addView(inflate, layoutParams);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.IccLockSettings.3
            @Override // java.lang.Runnable
            public void run() {
                windowManager.removeViewImmediate(inflate);
            }
        }, 7000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void iccPinChanged(boolean z, int i) {
        Log.d("IccLockSettings", "iccPinChanged: success = " + z);
        if (!z) {
            createCustomTextToast(getPinPasswordErrorMessage(i));
        } else {
            Toast.makeText(getContext(), this.mRes.getString(R$string.sim_change_succeeded), 0).show();
        }
        resetDialogState();
    }

    private void tryChangePin() {
        new ChangeIccLockPin(this.mOldPin, this.mNewPin).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ChangeIccLockPin extends AsyncTask<Void, Void, PinResult> {
        private final String mNewPin;
        private final String mOldPin;

        private ChangeIccLockPin(String str, String str2) {
            this.mOldPin = str;
            this.mNewPin = str2;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public PinResult doInBackground(Void... voidArr) {
            IccLockSettings iccLockSettings = IccLockSettings.this;
            iccLockSettings.mTelephonyManager = iccLockSettings.mTelephonyManager.createForSubscriptionId(IccLockSettings.this.mSubId);
            return IccLockSettings.this.mTelephonyManager.changeIccLockPin(this.mOldPin, this.mNewPin);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(PinResult pinResult) {
            IccLockSettings.this.iccPinChanged(pinResult.getResult() == 0, pinResult.getAttemptsRemaining());
        }
    }

    private String getPinPasswordErrorMessage(int i) {
        if (i == 0) {
            return this.mRes.getString(R$string.wrong_pin_code_pukked);
        }
        if (i == 1) {
            return this.mRes.getString(R$string.wrong_pin_code_one, Integer.valueOf(i));
        }
        if (i > 1) {
            return this.mRes.getQuantityString(R$plurals.wrong_pin_code, i, Integer.valueOf(i));
        }
        return this.mRes.getString(R$string.pin_failed);
    }

    private boolean reasonablePin(String str) {
        return str != null && str.length() >= 4 && str.length() <= 8;
    }

    private void resetDialogState() {
        this.mError = null;
        this.mDialogState = 2;
        this.mPin = "";
        setDialogValues();
        this.mDialogState = 0;
    }

    private String getTagForSlotId(int i) {
        return String.valueOf(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSlotIndexFromTag(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    private SubscriptionInfo getVisibleSubscriptionInfoForSimSlotIndex(int i) {
        List<SubscriptionInfo> activeSubscriptionsInfo = this.mProxySubscriptionMgr.getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo == null) {
            return null;
        }
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getContext().getSystemService(CarrierConfigManager.class);
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionsInfo) {
            if (isSubscriptionVisible(carrierConfigManager, subscriptionInfo) && subscriptionInfo.getSimSlotIndex() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private boolean isSubscriptionVisible(CarrierConfigManager carrierConfigManager, SubscriptionInfo subscriptionInfo) {
        PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId());
        if (configForSubId == null) {
            return false;
        }
        return !configForSubId.getBoolean("hide_sim_lock_settings_bool");
    }

    private TabHost.TabSpec buildTabSpec(String str, String str2) {
        return this.mTabHost.newTabSpec(str).setIndicator(str2).setContent(this.mEmptyTabContent);
    }

    private boolean hasActiveSub() {
        if (getContext() == null) {
            return false;
        }
        boolean z = SubscriptionManager.from(getContext()).getActiveSubscriptionInfoForSimSlotIndex(this.mSlotId) != null;
        Log.d("IccLockSettings", "hasActiveSub : " + z + " currentTab : " + this.mSlotId);
        return z;
    }

    private void updateTabName(int i, String str) {
        TextView textView = (TextView) this.mTabHost.getTabWidget().getChildTabViewAt(i).findViewById(16908310);
        textView.setText(str);
        textView.setAllCaps(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeSubscriptions() {
        ProxySubscriptionManager proxySubscriptionManager;
        PersistableBundle configForSubId;
        if (getContext() == null || (proxySubscriptionManager = this.mProxySubscriptionMgr) == null) {
            return;
        }
        int activeSubscriptionInfoCountMax = proxySubscriptionManager.getActiveSubscriptionInfoCountMax();
        this.mProxySubscriptionMgr.getActiveSubscriptionsInfo();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < activeSubscriptionInfoCountMax; i++) {
            SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(i);
            if (visibleSubscriptionInfoForSimSlotIndex != null && (configForSubId = ((CarrierConfigManager) getContext().getSystemService(CarrierConfigManager.class)).getConfigForSubId(visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId())) != null && !configForSubId.getBoolean("hide_sim_lock_settings_bool")) {
                arrayList.add(visibleSubscriptionInfoForSimSlotIndex);
            }
        }
        if (arrayList.size() < 2) {
            TabHost tabHost = this.mTabHost;
            if (tabHost != null) {
                tabHost.clearAllTabs();
            }
            Log.d("IccLockSettings", "no need to add tab");
            return;
        }
        TabHost tabHost2 = (TabHost) getActivity().findViewById(16908306);
        this.mTabHost = tabHost2;
        if (tabHost2 == null) {
            Log.d("IccLockSettings", "mTabHost is null");
            return;
        }
        tabHost2.setup();
        for (int i2 = 0; i2 < activeSubscriptionInfoCountMax; i2++) {
            SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex2 = getVisibleSubscriptionInfoForSimSlotIndex(i2);
            String valueOf = visibleSubscriptionInfoForSimSlotIndex2 != null ? String.valueOf(visibleSubscriptionInfoForSimSlotIndex2.getDisplayName()) : String.valueOf(getContext().getString(R$string.sim_editor_title, Integer.valueOf(i2 + 1)));
            TabHost tabHost3 = this.mTabHost;
            if (tabHost3 != null && tabHost3.getTabWidget() != null && this.mTabHost.getTabWidget().getChildTabViewAt(i2) != null) {
                updateTabName(i2, valueOf);
            } else {
                this.mTabHost.addTab(buildTabSpec(String.valueOf(i2), valueOf));
                updateTabName(i2, valueOf);
            }
        }
        this.mTabHost.setCurrentTab(this.mSlotId);
    }

    private int getRemainTimes() {
        int i;
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            String telephonyProperty = TelephonyManager.getTelephonyProperty(SubscriptionManager.getPhoneId(this.mSubId), "vendor.sim.pin.remaintimes", "");
            if (!TextUtils.isEmpty(telephonyProperty)) {
                try {
                    i = Integer.valueOf(telephonyProperty).intValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                Log.d("IccLockSettings", "getRemainTimes : " + i);
                return i;
            }
        }
        i = -1;
        Log.d("IccLockSettings", "getRemainTimes : " + i);
        return i;
    }
}
