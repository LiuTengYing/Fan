package com.android.settings.colorpick.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.ImageView;
/* loaded from: classes.dex */
public class MyColorPickerBar extends ImageView {
    private Bitmap bitmap;
    private Context context;
    private Paint paint;
    private float radius;

    public MyColorPickerBar(Context context, float f) {
        super(context);
        this.context = context;
        this.radius = f;
        init();
    }

    public void init() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setAntiAlias(false);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeWidth(1.0f);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        RectF rectF = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        Path path = new Path();
        float f = this.radius;
        path.addRoundRect(rectF, new float[]{f, f, f, f, f, f, f, f}, Path.Direction.CW);
        canvas.clipPath(path);
        this.paint.setShader(new LinearGradient(0.0f, 0.0f, getWidth(), 0.0f, new int[]{-16776961, -16711936, -7829368, Color.rgb(202, 150, 235), -65536}, new float[]{0.0f, 0.25f, 0.5f, 0.75f, 1.0f}, Shader.TileMode.REPEAT));
        this.bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        new Canvas(this.bitmap).drawRect(rectF, this.paint);
        setImageBitmap(this.bitmap);
        super.onDraw(canvas);
    }

    public int getColorByPoint(int i) {
        if (i >= this.bitmap.getWidth()) {
            i = this.bitmap.getWidth() - 1;
        } else if (i < 0) {
            i = 0;
        }
        return this.bitmap.getPixel(i, getHeight() / 2);
    }
}
