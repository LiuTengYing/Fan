package com.android.settings.gps.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R$drawable;
import com.android.settings.gps.data.Gps_Data;
import com.android.settings.gps.data.SatelliteInfo;
import com.syu.ipcself.module.main.Main;
import java.util.List;
/* loaded from: classes.dex */
public class JSatelliteView extends View {
    Drawable bdSate;
    int centerx;
    int centery;
    Drawable earth;
    int earthHeight;
    int earthRadius;
    int earthWidth;
    Drawable glonassState;
    Drawable gpsSate;
    List<SatelliteInfo> mBdSatellites;
    private Context mContext;
    List<SatelliteInfo> mGLNSatellites;
    List<SatelliteInfo> mGpsSatellites;
    List<SatelliteInfo> mUNKNOWSatellites;
    Paint paint;
    Drawable unBdSate;
    Drawable unGlonassState;
    Drawable unGpsSate;
    Drawable unUnknowSate;
    Drawable unknowSate;
    public static Boolean is_New_GpsInfo = Boolean.FALSE;
    public static boolean isForeign = false;

    private double degreeToRadian(double d) {
        return (d * 3.141592653589793d) / 180.0d;
    }

    public JSatelliteView(Context context) {
        super(context);
        this.centerx = 0;
        this.centery = 0;
        this.earthRadius = 0;
        this.mContext = context;
        init(context);
    }

    public JSatelliteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.centerx = 0;
        this.centery = 0;
        this.earthRadius = 0;
        this.mContext = context;
        init(context);
    }

    public JSatelliteView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.centerx = 0;
        this.centery = 0;
        this.earthRadius = 0;
        this.mContext = context;
        init(context);
    }

    protected void init(Context context) {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setSubpixelText(true);
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.paint.setColor(-65536);
        this.paint.setTextSize(17.0f);
        this.paint.setTextAlign(Paint.Align.CENTER);
        setIconNames();
    }

    public void setIconNames() {
        this.earth = this.mContext.getResources().getDrawable(R$drawable.gps_statellite_view_bk);
        Resources resources = this.mContext.getResources();
        int i = R$drawable.set_satellite_bd_mini;
        this.bdSate = resources.getDrawable(i);
        this.unBdSate = this.mContext.getResources().getDrawable(i);
        Resources resources2 = this.mContext.getResources();
        int i2 = R$drawable.set_satellite_gln_mini;
        this.glonassState = resources2.getDrawable(i2);
        this.unGlonassState = this.mContext.getResources().getDrawable(i2);
        Resources resources3 = this.mContext.getResources();
        int i3 = R$drawable.set_satellite_gps_mini;
        this.gpsSate = resources3.getDrawable(i3);
        this.unGpsSate = this.mContext.getResources().getDrawable(i3);
        Resources resources4 = this.mContext.getResources();
        int i4 = R$drawable.set_satellite_ufo_mini;
        this.unknowSate = resources4.getDrawable(i4);
        this.unUnknowSate = this.mContext.getResources().getDrawable(i4);
        Drawable drawable = this.earth;
        if (drawable != null) {
            this.earthWidth = drawable.getIntrinsicWidth();
            this.earthHeight = this.earth.getIntrinsicHeight();
            int i5 = this.earthWidth;
            Drawable drawable2 = this.bdSate;
            this.earthRadius = (int) ((i5 - (drawable2 == null ? 0 : drawable2.getIntrinsicWidth())) / 2.0f);
        }
    }

    public void setBdSatellites(List<SatelliteInfo> list) {
        this.mBdSatellites = list;
        invalidate();
    }

    public void setGpsSatellites(List<SatelliteInfo> list) {
        this.mGpsSatellites = list;
        invalidate();
    }

    public void setGLNSatellites(List<SatelliteInfo> list) {
        this.mGLNSatellites = list;
        invalidate();
    }

    public void setUNKNOWSatellites(List<SatelliteInfo> list) {
        this.mUNKNOWSatellites = list;
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        float f = 1.0f;
        float f2 = (mode == 0 || size >= (i4 = this.earthWidth)) ? 1.0f : size / i4;
        if (mode2 != 0 && size2 < (i3 = this.earthHeight)) {
            f = size2 / i3;
        }
        float min = Math.min(f2, f);
        setMeasuredDimension(View.resolveSizeAndState((int) (this.earthWidth * min), i, 0), View.resolveSizeAndState((int) (this.earthHeight * min), i2, 0));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        boolean z;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int i = width / 2;
        this.centerx = i;
        int i2 = height / 2;
        this.centery = i2;
        int i3 = this.earthWidth;
        if (width < i3 || height < this.earthHeight) {
            float min = Math.min(width / i3, height / this.earthHeight);
            canvas.save();
            canvas.scale(min, min, i, i2);
            z = true;
        } else {
            z = false;
        }
        if (this.earth != null) {
            canvas.save();
            int intrinsicWidth = this.earth.getIntrinsicWidth();
            int intrinsicHeight = this.earth.getIntrinsicHeight();
            canvas.save();
            int i4 = intrinsicWidth / 2;
            int i5 = intrinsicHeight / 2;
            this.earth.setBounds(i - i4, i2 - i5, i + i4, i2 + i5);
            this.earth.draw(canvas);
            canvas.restore();
        }
        List<SatelliteInfo> list = this.mBdSatellites;
        if (list != null && list.size() > 0) {
            doDraw(canvas, this.mBdSatellites);
        }
        List<SatelliteInfo> list2 = this.mGpsSatellites;
        if (list2 != null && list2.size() > 0) {
            doDraw(canvas, this.mGpsSatellites);
        }
        List<SatelliteInfo> list3 = this.mGLNSatellites;
        if (list3 != null && list3.size() > 0) {
            doDraw(canvas, this.mGLNSatellites);
        }
        List<SatelliteInfo> list4 = this.mUNKNOWSatellites;
        if (list4 != null && list4.size() > 0) {
            doDraw(canvas, this.mUNKNOWSatellites);
        }
        if (z) {
            canvas.restore();
        }
    }

    private void drawSatellite(Canvas canvas, SatelliteInfo satelliteInfo, int i, int i2, int i3) {
        double elevation = i3 * ((90.0f - satelliteInfo.getElevation()) / 90.0f);
        double degreeToRadian = degreeToRadian((360.0d - satelliteInfo.getAzimuth()) + 90.0d);
        double cos = i + (Math.cos(degreeToRadian) * elevation);
        double sin = i2 + (Math.sin(degreeToRadian) * elevation);
        int isBD = satelliteInfo.getIsBD();
        int snr = satelliteInfo.getSnr();
        boolean z = (satelliteInfo.getIsUse() == 0 || Gps_Data.mGpsStatus == 0) ? false : true;
        if (!is_New_GpsInfo.booleanValue() || z) {
            if (isBD != 1) {
                if (isBD == 2) {
                    drawSatellite(canvas, !z ? this.unGlonassState : this.glonassState, (int) cos, (int) sin, snr);
                } else if (isBD == 0) {
                    drawSatellite(canvas, !z ? this.unGpsSate : this.gpsSate, (int) cos, (int) sin, snr);
                } else if (isBD == 4) {
                    drawSatellite(canvas, !z ? this.unUnknowSate : this.unknowSate, (int) cos, (int) sin, snr);
                }
            } else if (is_New_GpsInfo.booleanValue()) {
                drawSatellite(canvas, !z ? this.unBdSate : this.bdSate, (int) cos, (int) sin, snr);
            } else {
                int i4 = Main.mConf_PlatForm;
                if (i4 != 2 && i4 != 7) {
                    if ((i4 == 6 || i4 == 8) && !isForeign) {
                        drawSatellite(canvas, !z ? this.unBdSate : this.bdSate, (int) cos, (int) sin, snr);
                        return;
                    } else {
                        drawSatellite(canvas, !z ? this.unGlonassState : this.glonassState, (int) cos, (int) sin, snr);
                        return;
                    }
                }
                drawSatellite(canvas, !z ? this.unBdSate : this.bdSate, (int) cos, (int) sin, snr);
            }
        }
    }

    void drawSatellite(Canvas canvas, Drawable drawable, int i, int i2, int i3) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        canvas.save();
        int i4 = intrinsicWidth / 2;
        int i5 = intrinsicHeight / 2;
        drawable.setBounds(i - i4, i2 - i5, i4 + i, i5 + i2);
        drawable.draw(canvas);
        canvas.restore();
        canvas.drawText(String.format("%s", Integer.valueOf(i3)), i, (i2 + intrinsicHeight) - 3, this.paint);
    }

    private void doDraw(Canvas canvas, List<SatelliteInfo> list) {
        if (canvas == null || list == null) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i < list.size()) {
                drawSatellite(canvas, list.get(i), this.centerx, this.centery, this.earthRadius);
            }
        }
    }
}
