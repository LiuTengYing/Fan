package com.android.settings.connecteddevice.usb;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.view.TextSeekBarCenter;
import java.util.Arrays;
/* loaded from: classes.dex */
public class HdmiVideoOutputFragment extends DashboardFragment {
    private static String TAG = "HdmiVideoOutputFragment";
    private static int UPDATEBOTTOM = 104;
    private static int UPDATELEFT = 102;
    private static int UPDATERIGHT = 103;
    private static int UPDATETOP = 101;
    public static String mPkgName;
    public static Resources mResources;
    private TextSeekBarCenter BarBottom;
    private TextSeekBarCenter BarLeft;
    private TextSeekBarCenter BarRight;
    private TextSeekBarCenter BarTop;
    private int mBottom;
    private TextView mDeviationH;
    private TextView mDeviationV;
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.connecteddevice.usb.HdmiVideoOutputFragment.5
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == HdmiVideoOutputFragment.UPDATETOP) {
                HdmiVideoOutputFragment.this.mTop = ((Integer) message.obj).intValue();
            } else if (message.what == HdmiVideoOutputFragment.UPDATELEFT) {
                HdmiVideoOutputFragment.this.mLeft = ((Integer) message.obj).intValue();
            } else if (message.what == HdmiVideoOutputFragment.UPDATERIGHT) {
                HdmiVideoOutputFragment.this.mRight = ((Integer) message.obj).intValue();
            } else if (message.what == HdmiVideoOutputFragment.UPDATEBOTTOM) {
                HdmiVideoOutputFragment.this.mBottom = ((Integer) message.obj).intValue();
            }
            HdmiVideoOutputFragment hdmiVideoOutputFragment = HdmiVideoOutputFragment.this;
            hdmiVideoOutputFragment.setFullScreenMargin(hdmiVideoOutputFragment.mTop, HdmiVideoOutputFragment.this.mBottom, HdmiVideoOutputFragment.this.mLeft, HdmiVideoOutputFragment.this.mRight);
        }
    };
    private int mLeft;
    private int mRight;
    private View mRootView;
    private TextView mStretchH;
    private TextView mStretchV;
    private int mTop;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R$layout.hdmi_video_output_layout, viewGroup, false);
        initViews();
        initListener();
        return this.mRootView;
    }

    private void initViews() {
        mResources = getContext().getResources();
        mPkgName = getContext().getPackageName();
        this.BarTop = (TextSeekBarCenter) this.mRootView.findViewById(R$id.hdmi_output_top);
        this.BarLeft = (TextSeekBarCenter) this.mRootView.findViewById(R$id.hdmi_output_left);
        this.BarRight = (TextSeekBarCenter) this.mRootView.findViewById(R$id.hdmi_output_right);
        this.BarBottom = (TextSeekBarCenter) this.mRootView.findViewById(R$id.hdmi_output_bottom);
        this.mDeviationV = (TextView) this.mRootView.findViewById(R$id.hdmi_output_top_value);
        this.mDeviationH = (TextView) this.mRootView.findViewById(R$id.hdmi_output_left_value);
        this.mStretchH = (TextView) this.mRootView.findViewById(R$id.hdmi_output_right_value);
        this.mStretchV = (TextView) this.mRootView.findViewById(R$id.hdmi_output_bottom_value);
        getFullScreen();
        setFullScreenConfig();
        this.BarTop.setProgress(this.mTop);
        this.BarLeft.setProgress(this.mLeft);
        this.BarRight.setProgress(this.mRight);
        this.BarBottom.setProgress(this.mBottom);
        TextView textView = this.mDeviationV;
        textView.setText(this.mTop + "");
        TextView textView2 = this.mDeviationH;
        textView2.setText(this.mLeft + "");
        TextView textView3 = this.mStretchH;
        textView3.setText(this.mRight + "");
        TextView textView4 = this.mStretchV;
        textView4.setText(this.mBottom + "");
        SystemProperties.set("sys.lsec.dp.setting", "true");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R$string.fix_connectivity);
        add.setIcon(R$drawable.ic_repair_24dp);
        add.setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            resetSettings();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void resetSettings() {
        this.mTop = 0;
        this.mLeft = 0;
        this.mRight = 0;
        this.mBottom = 0;
        this.BarTop.setProgress(0);
        this.BarLeft.setProgress(this.mLeft);
        this.BarRight.setProgress(this.mRight);
        this.BarBottom.setProgress(this.mBottom);
        setFullScreenMargin(0, 0, 0, 0);
        TextView textView = this.mDeviationV;
        textView.setText(this.mTop + "");
        TextView textView2 = this.mDeviationH;
        textView2.setText(this.mLeft + "");
        TextView textView3 = this.mStretchH;
        textView3.setText(this.mRight + "");
        TextView textView4 = this.mStretchV;
        textView4.setText(this.mBottom + "");
        SystemProperties.set("persist.lsec.dp.fullscreen", "false");
    }

    private void initListener() {
        this.BarTop.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.HdmiVideoOutputFragment.1
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                TextView textView = HdmiVideoOutputFragment.this.mDeviationV;
                textView.setText(i + "");
                if (HdmiVideoOutputFragment.this.mHandler != null) {
                    Message message = new Message();
                    message.what = HdmiVideoOutputFragment.UPDATETOP;
                    message.obj = Integer.valueOf(i);
                    HdmiVideoOutputFragment.this.mHandler.removeMessages(HdmiVideoOutputFragment.UPDATETOP);
                    HdmiVideoOutputFragment.this.mHandler.sendMessageDelayed(message, 500L);
                }
            }
        });
        this.BarLeft.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.HdmiVideoOutputFragment.2
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                TextView textView = HdmiVideoOutputFragment.this.mDeviationH;
                textView.setText(i + "");
                if (HdmiVideoOutputFragment.this.mHandler != null) {
                    Message message = new Message();
                    message.what = HdmiVideoOutputFragment.UPDATELEFT;
                    message.obj = Integer.valueOf(i);
                    HdmiVideoOutputFragment.this.mHandler.removeMessages(HdmiVideoOutputFragment.UPDATELEFT);
                    HdmiVideoOutputFragment.this.mHandler.sendMessageDelayed(message, 500L);
                }
            }
        });
        this.BarRight.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.HdmiVideoOutputFragment.3
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                TextView textView = HdmiVideoOutputFragment.this.mStretchH;
                textView.setText(i + "");
                if (HdmiVideoOutputFragment.this.mHandler != null) {
                    Message message = new Message();
                    message.what = HdmiVideoOutputFragment.UPDATERIGHT;
                    message.obj = Integer.valueOf(i);
                    HdmiVideoOutputFragment.this.mHandler.removeMessages(HdmiVideoOutputFragment.UPDATERIGHT);
                    HdmiVideoOutputFragment.this.mHandler.sendMessageDelayed(message, 500L);
                }
            }
        });
        this.BarBottom.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.connecteddevice.usb.HdmiVideoOutputFragment.4
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i, boolean z) {
                TextView textView = HdmiVideoOutputFragment.this.mStretchV;
                textView.setText(i + "");
                if (HdmiVideoOutputFragment.this.mHandler != null) {
                    Message message = new Message();
                    message.what = HdmiVideoOutputFragment.UPDATEBOTTOM;
                    message.obj = Integer.valueOf(i);
                    HdmiVideoOutputFragment.this.mHandler.removeMessages(HdmiVideoOutputFragment.UPDATEBOTTOM);
                    HdmiVideoOutputFragment.this.mHandler.sendMessageDelayed(message, 500L);
                }
            }
        });
    }

    private String getFullScreen() {
        String str = SystemProperties.get("persist.lsec.dp.margins", "0,0,0,0");
        String[] split = str.split(",");
        String str2 = TAG;
        Log.d(str2, "getFullScreen: " + Arrays.toString(split));
        this.mTop = Integer.valueOf(split[0]).intValue();
        this.mLeft = Integer.valueOf(split[1]).intValue();
        this.mRight = Integer.valueOf(split[2]).intValue();
        this.mBottom = Integer.valueOf(split[3]).intValue();
        return str;
    }

    private void setFullScreenConfig() {
        SystemProperties.set("persist.lsec.dp.fullscreen", "true");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFullScreenMargin(int i, int i2, int i3, int i4) {
        SystemProperties.set("persist.lsec.dp.margins", i + "," + i3 + "," + i4 + "," + i2);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }
}
