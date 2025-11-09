package com.android.settings.factory.dialog;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.SettingsApplication;
/* loaded from: classes.dex */
public class FactoryTouchScreenDialog extends Activity {
    public static int mHeightFix = 1100;
    public static String mPkgName = null;
    public static Resources mResources = null;
    public static int mWidthFix = 2000;
    TextView H;
    TextView W;
    private FrameLayout mTestView;
    ImageView point;
    int count = 79;
    int width = 60;
    int h_temp = 34;
    ImageView[] imgs_top_bottom = new ImageView[34];
    ImageView[] imgs_left_right = new ImageView[16];
    ImageView[] imgs_bevel = new ImageView[29];
    int oldSystemUIVisible = 2;
    int text_size = 20;
    int text_height = 30;
    int text_width = 200;
    View.OnTouchListener listener = new View.OnTouchListener() { // from class: com.android.settings.factory.dialog.FactoryTouchScreenDialog.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            float x = motionEvent.getX();
            TextView textView = FactoryTouchScreenDialog.this.W;
            if (textView != null) {
                textView.setText("W: " + x);
            }
            float y = motionEvent.getY();
            TextView textView2 = FactoryTouchScreenDialog.this.H;
            if (textView2 != null) {
                textView2.setText("H: " + y);
            }
            ImageView imageView = FactoryTouchScreenDialog.this.point;
            if (imageView != null) {
                imageView.setAlpha(100);
                FactoryTouchScreenDialog.this.point.setX(x);
                FactoryTouchScreenDialog.this.point.setY(y);
            }
            int i = 0;
            boolean z = true;
            while (true) {
                ImageView[] imageViewArr = FactoryTouchScreenDialog.this.imgs_left_right;
                if (i >= imageViewArr.length) {
                    break;
                }
                if (imageViewArr[i].getX() < x) {
                    float x2 = FactoryTouchScreenDialog.this.imgs_left_right[i].getX();
                    FactoryTouchScreenDialog factoryTouchScreenDialog = FactoryTouchScreenDialog.this;
                    if (x < x2 + factoryTouchScreenDialog.width && factoryTouchScreenDialog.imgs_left_right[i].getY() < y) {
                        float y2 = FactoryTouchScreenDialog.this.imgs_left_right[i].getY();
                        FactoryTouchScreenDialog factoryTouchScreenDialog2 = FactoryTouchScreenDialog.this;
                        if (y < y2 + factoryTouchScreenDialog2.width) {
                            if (factoryTouchScreenDialog2.imgs_left_right[i].getVisibility() != 8) {
                                FactoryTouchScreenDialog.this.count--;
                            }
                            FactoryTouchScreenDialog.this.imgs_left_right[i].setVisibility(8);
                            z = false;
                        }
                    }
                }
                i++;
            }
            if (z) {
                int i2 = 0;
                while (true) {
                    ImageView[] imageViewArr2 = FactoryTouchScreenDialog.this.imgs_bevel;
                    if (i2 >= imageViewArr2.length) {
                        break;
                    }
                    if (imageViewArr2[i2].getX() < x) {
                        float x3 = FactoryTouchScreenDialog.this.imgs_bevel[i2].getX();
                        FactoryTouchScreenDialog factoryTouchScreenDialog3 = FactoryTouchScreenDialog.this;
                        if (x < x3 + factoryTouchScreenDialog3.width && factoryTouchScreenDialog3.imgs_bevel[i2].getY() < y) {
                            float y3 = FactoryTouchScreenDialog.this.imgs_bevel[i2].getY();
                            FactoryTouchScreenDialog factoryTouchScreenDialog4 = FactoryTouchScreenDialog.this;
                            if (y < y3 + factoryTouchScreenDialog4.width) {
                                if (factoryTouchScreenDialog4.imgs_bevel[i2].getVisibility() != 8) {
                                    FactoryTouchScreenDialog.this.count--;
                                }
                                FactoryTouchScreenDialog.this.imgs_bevel[i2].setVisibility(8);
                            }
                        }
                    }
                    i2++;
                }
                int i3 = 0;
                while (true) {
                    ImageView[] imageViewArr3 = FactoryTouchScreenDialog.this.imgs_top_bottom;
                    if (i3 >= imageViewArr3.length) {
                        break;
                    }
                    if (imageViewArr3[i3].getX() < x) {
                        float x4 = FactoryTouchScreenDialog.this.imgs_top_bottom[i3].getX();
                        FactoryTouchScreenDialog factoryTouchScreenDialog5 = FactoryTouchScreenDialog.this;
                        if (x < x4 + factoryTouchScreenDialog5.width && factoryTouchScreenDialog5.imgs_top_bottom[i3].getY() < y) {
                            float y4 = FactoryTouchScreenDialog.this.imgs_top_bottom[i3].getY();
                            FactoryTouchScreenDialog factoryTouchScreenDialog6 = FactoryTouchScreenDialog.this;
                            if (y < y4 + factoryTouchScreenDialog6.width) {
                                if (factoryTouchScreenDialog6.imgs_top_bottom[i3].getVisibility() != 8) {
                                    FactoryTouchScreenDialog.this.count--;
                                }
                                FactoryTouchScreenDialog.this.imgs_top_bottom[i3].setVisibility(8);
                            }
                        }
                    }
                    i3++;
                }
            }
            FactoryTouchScreenDialog factoryTouchScreenDialog7 = FactoryTouchScreenDialog.this;
            if (factoryTouchScreenDialog7.count == 0) {
                Toast.makeText(SettingsApplication.mApplication, factoryTouchScreenDialog7.getString("touch_check_success"), 0).show();
                FactoryTouchScreenDialog.this.finish();
            }
            return true;
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().getDecorView().setSystemUiVisibility(4);
        setContentView(R$layout.factory_touch_test_dialog_fragment);
        init();
    }

    @Override // android.app.Activity
    protected void onResume() {
        int i = 0;
        while (true) {
            ImageView[] imageViewArr = this.imgs_bevel;
            if (i >= imageViewArr.length) {
                break;
            }
            ImageView imageView = imageViewArr[i];
            if (imageView != null) {
                imageView.setVisibility(0);
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            ImageView[] imageViewArr2 = this.imgs_top_bottom;
            if (i2 >= imageViewArr2.length) {
                break;
            }
            ImageView imageView2 = imageViewArr2[i2];
            if (imageView2 != null) {
                imageView2.setVisibility(0);
            }
            i2++;
        }
        int i3 = 0;
        while (true) {
            ImageView[] imageViewArr3 = this.imgs_left_right;
            if (i3 >= imageViewArr3.length) {
                break;
            }
            ImageView imageView3 = imageViewArr3[i3];
            if (imageView3 != null) {
                imageView3.setVisibility(0);
            }
            i3++;
        }
        ImageView imageView4 = this.point;
        if (imageView4 != null) {
            imageView4.setAlpha(0);
            this.point.setX(-1.0f);
            this.point.setY(-1.0f);
        }
        this.count = 79;
        super.onResume();
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        int i;
        float f = SettingsApplication.mWidthFix / 1024.0f;
        this.text_height = Math.round(this.text_height * f);
        this.text_width = Math.round(this.text_width * f);
        this.width = Math.round(this.width * f);
        this.h_temp = Math.round(f * this.h_temp);
        mResources = getResources();
        mPkgName = getPackageName();
        this.mTestView = (FrameLayout) findViewById(R$id.test_main);
        mWidthFix = getWindowManeger()[0];
        AbsoluteLayout absoluteLayout = new AbsoluteLayout(this);
        absoluteLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        int i2 = 0;
        int i3 = 0;
        loop0: while (true) {
            int i4 = 2;
            while (i2 < this.imgs_top_bottom.length) {
                ImageView imageView = getImageView(i3, i4);
                this.imgs_top_bottom[i2] = imageView;
                absoluteLayout.addView(imageView);
                i = this.width;
                i4 += i;
                i2++;
                if (i2 == this.imgs_top_bottom.length / 2) {
                    break;
                }
            }
            i3 = mHeightFix - i;
        }
        int i5 = this.width;
        int i6 = 0;
        int i7 = 2;
        while (i6 < this.imgs_left_right.length) {
            ImageView imageView2 = getImageView(i5, i7);
            this.imgs_left_right[i6] = imageView2;
            absoluteLayout.addView(imageView2);
            int i8 = this.width;
            i5 += i8;
            i6++;
            if (i6 == this.imgs_left_right.length / 2) {
                i7 = (mWidthFix - i8) - 2;
                i5 = i8;
            }
        }
        int i9 = this.h_temp;
        int i10 = this.width + 2;
        for (int i11 = 0; i11 < this.imgs_bevel.length; i11++) {
            ImageView imageView3 = getImageView(i9, i10);
            this.imgs_bevel[i11] = imageView3;
            absoluteLayout.addView(imageView3);
            ImageView[] imageViewArr = this.imgs_bevel;
            if (i11 > imageViewArr.length / 2) {
                int i12 = this.h_temp;
                i9 -= i12;
                int i13 = this.width;
                i10 += i13;
                if (i10 == (mWidthFix - i13) / 2) {
                    i9 -= i12;
                    i10 += i13;
                }
            } else {
                int i14 = this.h_temp;
                i9 += i14;
                int i15 = this.width;
                i10 += i15;
                if (i11 == imageViewArr.length / 2) {
                    i9 = (mHeightFix - i14) - i15;
                    i10 = i15 + 2;
                }
            }
        }
        TextView textView = getTextView();
        this.H = textView;
        textView.setY(100.0f);
        absoluteLayout.addView(this.H);
        TextView textView2 = getTextView();
        this.W = textView2;
        textView2.setY(160.0f);
        absoluteLayout.addView(this.W);
        ImageView imageView4 = new ImageView(this);
        this.point = imageView4;
        imageView4.setX(-1.0f);
        this.point.setY(-1.0f);
        this.point.setBackgroundColor(-16776961);
        this.point.setLayoutParams(new LinearLayout.LayoutParams(3, 3));
        absoluteLayout.addView(this.point);
        absoluteLayout.setOnTouchListener(this.listener);
        this.mTestView.addView(absoluteLayout);
    }

    public ImageView getImageView(int i, int i2) {
        ImageView imageView = new ImageView(this);
        imageView.setX(i2);
        imageView.setY(i);
        int i3 = this.width;
        imageView.setLayoutParams(new LinearLayout.LayoutParams(i3, i3));
        imageView.setBackgroundResource(R$drawable.set_test_point);
        return imageView;
    }

    public TextView getTextView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.text_width, this.text_height);
        TextView textView = new TextView(this);
        textView.setX(437.0f);
        textView.setTextSize(this.text_size);
        textView.setTextColor(-1);
        textView.setGravity(17);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public String getString(String str) {
        int identifier = mResources.getIdentifier(str, "string", mPkgName);
        return identifier == 0 ? str : myString(identifier);
    }

    public static String myString(int i) {
        Resources resources = mResources;
        if (resources != null) {
            try {
                return resources.getString(i);
            } catch (Exception unused) {
                return "";
            }
        }
        return "";
    }
}
