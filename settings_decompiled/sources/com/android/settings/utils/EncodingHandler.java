package com.android.settings.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemProperties;
import com.android.settings.R$drawable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.Hashtable;
/* loaded from: classes.dex */
public class EncodingHandler {
    private static Bitmap[] bitmaps = new Bitmap[2];
    private static Bitmap logo;

    public static Bitmap createQRCode(Context context, String str, int i) throws WriterException {
        Hashtable hashtable = new Hashtable();
        logo = BitmapFactory.decodeResource(context.getResources(), R$drawable.logo);
        hashtable.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hashtable.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        int i2 = i / 2;
        bitmaps[1] = logo;
        BitMatrix encode = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, i, i, hashtable);
        int width = encode.getWidth();
        int height = encode.getHeight();
        int[] iArr = new int[width * height];
        int i3 = 0;
        for (int i4 = 0; i4 < height; i4++) {
            for (int i5 = 0; i5 < width; i5++) {
                if (i5 < i2 || i5 >= i2 || i4 <= i2 || i4 >= i2) {
                    iArr[(i4 * width) + i5] = encode.get(i5, i4) ? -16777216 : -1;
                }
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        bitmaps[0] = createBitmap;
        Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap.getWidth(), bitmaps[0].getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap2);
        while (true) {
            Bitmap[] bitmapArr = bitmaps;
            if (i3 >= bitmapArr.length) {
                return createBitmap2;
            }
            if (i3 == 0) {
                canvas.drawBitmap(createBitmap, 0.0f, 0.0f, (Paint) null);
            } else {
                float f = i2;
                canvas.drawBitmap(bitmapArr[i3], f, f, (Paint) null);
            }
            i3++;
        }
    }

    public static Bitmap CreateOneDCode(String str) throws WriterException {
        BitMatrix encode = new MultiFormatWriter().encode(str, BarcodeFormat.CODE_128, 400, 100);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        int width = encode.getWidth();
        int height = encode.getHeight();
        int[] iArr = new int[width * height];
        if (i == 0) {
            for (int i2 = 0; i2 < height; i2++) {
                for (int i3 = 0; i3 < width; i3++) {
                    if (encode.get(i3, i2)) {
                        iArr[(i2 * width) + i3] = -16777216;
                    }
                }
            }
        } else {
            for (int i4 = 0; i4 < height; i4++) {
                for (int i5 = 0; i5 < width; i5++) {
                    if (encode.get(i5, i4)) {
                        iArr[(i4 * width) + i5] = -1;
                    }
                }
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        return createBitmap;
    }
}
