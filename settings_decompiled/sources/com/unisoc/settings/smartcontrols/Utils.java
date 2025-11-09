package com.unisoc.settings.smartcontrols;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.widget.SmartSwitchPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class Utils {
    public static final int[] SENSORHUB_LIST = {65548, 65537, 25, 65546, 65539, 23, 65538};
    private static List<String> PACKAGES = null;

    public static boolean isSupportSensor(Context context, int i) {
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        return (sensorManager == null || sensorManager.getDefaultSensor(i) == null) ? false : true;
    }

    public static boolean isSupportSmartControl(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        Boolean bool = Boolean.FALSE;
        if (sensorManager != null) {
            int i = 0;
            while (true) {
                int[] iArr = SENSORHUB_LIST;
                boolean z = true;
                if (i >= iArr.length) {
                    break;
                }
                boolean booleanValue = bool.booleanValue();
                if (sensorManager.getDefaultSensor(iArr[i]) == null) {
                    z = false;
                }
                bool = Boolean.valueOf(booleanValue | z);
                i++;
            }
            return bool.booleanValue() && context.getResources().getBoolean(R$bool.config_support_smartControls);
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String str) {
        if (PACKAGES == null) {
            PACKAGES = new ArrayList();
            List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < installedPackages.size(); i++) {
                PACKAGES.add(installedPackages.get(i).packageName);
            }
        }
        return PACKAGES.contains(str);
    }

    public static boolean hasGalleryVideo(Context context, String str) {
        Intent dataAndType = new Intent().setAction("android.intent.action.VIEW").setDataAndType(Uri.parse("file://"), "video/*");
        dataAndType.setPackage(str);
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(dataAndType, 0);
        return (queryIntentActivities == null || queryIntentActivities.size() == 0) ? false : true;
    }

    public static Dialog createAnimationDialog(final Context context, final SmartSwitchPreference smartSwitchPreference, int i, int i2, int i3, final String str) {
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(i, (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(i2);
        imageView.setImageResource(i3);
        ((AnimationDrawable) imageView.getDrawable()).start();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(inflate);
        builder.setPositiveButton(R$string.smart_ok, new DialogInterface.OnClickListener() { // from class: com.unisoc.settings.smartcontrols.Utils.1
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r3v2 */
            /* JADX WARN: Type inference failed for: r3v3, types: [int, boolean] */
            /* JADX WARN: Type inference failed for: r3v4 */
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                ?? r3 = i4 == -1 ? 1 : 0;
                SmartSwitchPreference smartSwitchPreference2 = SmartSwitchPreference.this;
                if (smartSwitchPreference2 != 0) {
                    smartSwitchPreference2.setChecked(r3);
                    ContentResolver contentResolver = context.getContentResolver();
                    if ("doze_pulse_on_pick_up".equals(str) || "wake_gesture_enabled".equals(str)) {
                        Settings.Secure.putInt(contentResolver, str, r3);
                    } else if (SmartMotionFragment.isSmartMotionEnabled(context)) {
                        Settings.Global.putInt(contentResolver, str, r3);
                    }
                }
            }
        });
        return builder.create();
    }
}
