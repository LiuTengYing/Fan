package com.android.settingslib.display;

import android.content.Context;
import android.content.res.Resources;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.view.WindowManagerGlobal;
import com.android.settingslib.R$fraction;
import com.android.settingslib.R$string;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class DisplayDensityUtils {
    private final int mCurrentIndex;
    private final int mDefaultDensity;
    private final String[] mEntries;
    private final int[] mValues;
    public static final int SUMMARY_DEFAULT = R$string.screen_zoom_summary_default;
    private static final int SUMMARY_CUSTOM = R$string.screen_zoom_summary_custom;
    private static final int[] SUMMARIES_SMALLER = {R$string.screen_zoom_summary_small};
    private static final int[] SUMMARIES_LARGER = {R$string.screen_zoom_summary_large, R$string.screen_zoom_summary_very_large, R$string.screen_zoom_summary_extremely_large};

    public DisplayDensityUtils(Context context) {
        int i;
        int i2;
        int defaultDisplayDensity = getDefaultDisplayDensity(0);
        if (defaultDisplayDensity <= 0) {
            this.mEntries = null;
            this.mValues = null;
            this.mDefaultDensity = 0;
            this.mCurrentIndex = -1;
            return;
        }
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getDisplayNoVerify().getRealMetrics(displayMetrics);
        int i3 = displayMetrics.densityDpi;
        float f = defaultDisplayDensity;
        float min = Math.min(context.getResources().getFraction(R$fraction.display_density_max_scale, 1, 1), ((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) * 160) / 320) / f);
        float fraction = context.getResources().getFraction(R$fraction.display_density_min_scale, 1, 1);
        float fraction2 = context.getResources().getFraction(R$fraction.display_density_min_scale_interval, 1, 1);
        float f2 = 1.0f;
        float f3 = min - 1.0f;
        int constrain = (int) MathUtils.constrain(f3 / fraction2, 0.0f, SUMMARIES_LARGER.length);
        float f4 = 1.0f - fraction;
        int constrain2 = (int) MathUtils.constrain(f4 / fraction2, 0.0f, SUMMARIES_SMALLER.length);
        int i4 = constrain2 + 1 + constrain;
        String[] strArr = new String[i4];
        int[] iArr = new int[i4];
        if (constrain2 > 0) {
            float f5 = f4 / constrain2;
            int i5 = constrain2 - 1;
            i2 = 0;
            i = -1;
            while (i5 >= 0) {
                int i6 = ((int) ((f2 - ((i5 + 1) * f5)) * f)) & (-2);
                if (i3 == i6) {
                    i = i2;
                }
                strArr[i2] = resources.getString(SUMMARIES_SMALLER[i5]);
                iArr[i2] = i6;
                i2++;
                i5--;
                f2 = 1.0f;
            }
        } else {
            i = -1;
            i2 = 0;
        }
        i = i3 == defaultDisplayDensity ? i2 : i;
        iArr[i2] = defaultDisplayDensity;
        strArr[i2] = resources.getString(SUMMARY_DEFAULT);
        int i7 = i2 + 1;
        if (constrain > 0) {
            float f6 = f3 / constrain;
            int i8 = 0;
            while (i8 < constrain) {
                int i9 = i8 + 1;
                int i10 = ((int) (((i9 * f6) + 1.0f) * f)) & (-2);
                if (i3 == i10) {
                    i = i7;
                }
                iArr[i7] = i10;
                strArr[i7] = resources.getString(SUMMARIES_LARGER[i8]);
                i7++;
                i8 = i9;
            }
        }
        if (i < 0) {
            int i11 = i4 + 1;
            iArr = Arrays.copyOf(iArr, i11);
            iArr[i7] = i3;
            strArr = (String[]) Arrays.copyOf(strArr, i11);
            strArr[i7] = resources.getString(SUMMARY_CUSTOM, Integer.valueOf(i3));
            i = i7;
        }
        this.mDefaultDensity = defaultDisplayDensity;
        this.mCurrentIndex = i;
        this.mEntries = strArr;
        this.mValues = iArr;
    }

    public String[] getEntries() {
        return this.mEntries;
    }

    public int[] getValues() {
        return this.mValues;
    }

    public int getCurrentIndex() {
        return this.mCurrentIndex;
    }

    public int getDefaultDensity() {
        return this.mDefaultDensity;
    }

    private static int getDefaultDisplayDensity(int i) {
        try {
            return WindowManagerGlobal.getWindowManagerService().getInitialDisplayDensity(i);
        } catch (RemoteException unused) {
            return -1;
        }
    }
}
