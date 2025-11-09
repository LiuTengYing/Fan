package com.android.settings.colorpick;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.window.R;
import com.android.settings.R$anim;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.colorpick.view.MyColorPickerBar;
import com.android.settings.colorpick.view.RoundRectImageDrawable;
import com.syu.jni.SyuJniNative;
import com.syu.util.MySharePreference;
/* loaded from: classes.dex */
public class PaletteActivity extends Activity {
    private RoundRectImageDrawable[] RRD_colors;
    private RoundRectImageDrawable cd_confirm;
    private RoundRectImageDrawable cd_type;
    private ColorStateList colorStateList;
    private Button confirm;
    private boolean curEnable;
    private String curType;
    private int currentColor;
    private ImageView customSelected;
    private int density;
    private int fixSelectX;
    private AnimatorSet hide;
    private ImageView[] image_colors;
    private ImageView[] image_mixs;
    private int int_color1 = Color.rgb(234, 147, 137);
    private int int_color2 = Color.rgb(218, 160, 52);
    private int int_color3 = Color.rgb(185, 174, 33);
    private int int_color4 = Color.rgb(73, 195, 32);
    private int int_color5 = Color.rgb(123, 175, 232);
    private int int_color6 = Color.rgb(27, 116, 199);
    private int int_color7;
    private int int_color_mix_1_a;
    private int int_color_mix_1_b;
    private int int_color_mix_2_a;
    private int int_color_mix_2_b;
    private int int_color_mix_3_a;
    private int int_color_mix_3_b;
    private int int_color_mix_4_a;
    private int int_color_mix_4_b;
    private int[] int_colors;
    private int[] int_colors_mix_a;
    private int[] int_colors_mix_b;
    private Boolean isColorOrMix;
    Animator.AnimatorListener mAnimatorListener;
    private int maxLabel;
    private int minLabel;
    MyColorPickerBar myColorPickerBar;
    BroadcastReceiver myReceiver;
    View.OnClickListener onClickListener;
    View.OnTouchListener onTouchListener;
    private RelativeLayout page;
    private LinearLayout pageColorLayout;
    private AnimatorSet pageColor_Left;
    private AnimatorSet pageColor_Right;
    private RelativeLayout pageCustomLayout;
    private AnimatorSet pageCustom_Left;
    private AnimatorSet pageCustom_Right;
    private LinearLayout pageMixLayout;
    private AnimatorSet pageMix_Left_From_Gone;
    private AnimatorSet pageMix_Left_To_Gone;
    private AnimatorSet pageMix_Right_From_Gone;
    private AnimatorSet pageMix_Right_To_Gone;
    private Boolean restarting;
    Handler restarting_handler;
    public WindowManager sWindowManager;
    private int saveColor_A;
    private int saveColor_B;
    private AnimatorSet show;
    private Boolean showing;
    private boolean startEnable;
    private Button switchButton;
    private View transparent;
    private Button typeColor;
    private Button typeCustom;
    private Button typeMix;
    private ImageView upColor1;
    private ImageView upColor2;
    private ImageView upImage;

    public PaletteActivity() {
        int rgb = Color.rgb(202, 150, 235);
        this.int_color7 = rgb;
        this.int_colors = new int[]{this.int_color1, this.int_color2, this.int_color3, this.int_color4, this.int_color5, this.int_color6, rgb};
        this.int_color_mix_1_a = Color.rgb(244, 147, 132);
        this.int_color_mix_1_b = Color.rgb(110, 92, 43);
        this.int_color_mix_2_a = Color.rgb(229, 158, 0);
        this.int_color_mix_2_b = Color.rgb(96, 96, 34);
        this.int_color_mix_3_a = Color.rgb(55, 198, 0);
        this.int_color_mix_3_b = Color.rgb(41, 102, 105);
        this.int_color_mix_4_a = Color.rgb((int) R.styleable.AppCompatTheme_toolbarStyle, 177, 240);
        int rgb2 = Color.rgb(139, 62, 154);
        this.int_color_mix_4_b = rgb2;
        this.int_colors_mix_a = new int[]{this.int_color_mix_1_a, this.int_color_mix_2_a, this.int_color_mix_3_a, this.int_color_mix_4_a};
        this.int_colors_mix_b = new int[]{this.int_color_mix_1_b, this.int_color_mix_2_b, this.int_color_mix_3_b, rgb2};
        this.image_colors = new ImageView[7];
        this.image_mixs = new ImageView[4];
        this.RRD_colors = new RoundRectImageDrawable[7];
        this.currentColor = Color.argb(255, 39, 103, 255);
        this.saveColor_A = -1;
        this.saveColor_B = -1;
        this.curType = "color";
        Boolean bool = Boolean.FALSE;
        this.showing = bool;
        this.restarting = bool;
        this.fixSelectX = 0;
        this.isColorOrMix = bool;
        this.minLabel = 0;
        this.maxLabel = 0;
        this.onTouchListener = new View.OnTouchListener() { // from class: com.android.settings.colorpick.PaletteActivity.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (PaletteActivity.this.curEnable) {
                    if (PaletteActivity.this.isColorOrMix.booleanValue()) {
                        for (int i = 0; i < PaletteActivity.this.image_colors.length; i++) {
                            PaletteActivity.this.image_colors[i].setImageResource(-1);
                        }
                        for (int i2 = 0; i2 < PaletteActivity.this.image_mixs.length; i2++) {
                            PaletteActivity.this.image_mixs[i2].setImageResource(-1);
                        }
                        PaletteActivity.this.isColorOrMix = Boolean.FALSE;
                        PaletteActivity.this.customSelected.setVisibility(0);
                    }
                    int round = Math.round(motionEvent.getX() - PaletteActivity.this.myColorPickerBar.getLeft());
                    int colorByPoint = PaletteActivity.this.myColorPickerBar.getColorByPoint(round);
                    PaletteActivity.this.saveColor_A = colorByPoint;
                    PaletteActivity.this.saveColor_B = colorByPoint;
                    PaletteActivity.this.upColor1.setBackgroundColor(PaletteActivity.this.saveColor_A);
                    PaletteActivity.this.upColor2.setBackgroundColor(PaletteActivity.this.saveColor_B);
                    if (round < 0) {
                        round = 0;
                    } else if (round > PaletteActivity.this.myColorPickerBar.getWidth() - 6) {
                        round = PaletteActivity.this.myColorPickerBar.getWidth() - 6;
                    }
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) PaletteActivity.this.customSelected.getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new RelativeLayout.LayoutParams(6, -1);
                    }
                    layoutParams.setMarginStart(round);
                    PaletteActivity.this.customSelected.setLayoutParams(layoutParams);
                    PaletteActivity.this.fixSelectX = round;
                    MySharePreference.saveIntValue("ColorPickerBarPosition", PaletteActivity.this.fixSelectX);
                }
                return false;
            }
        };
        this.onClickListener = new View.OnClickListener() { // from class: com.android.settings.colorpick.PaletteActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PaletteActivity.this.restarting.booleanValue()) {
                    return;
                }
                if (view == PaletteActivity.this.typeColor) {
                    if ("color".equals(PaletteActivity.this.curType)) {
                        return;
                    }
                    PaletteActivity.this.cancelAnimators();
                    PaletteActivity.this.typeColor.setTextColor(PaletteActivity.this.currentColor);
                    PaletteActivity.this.typeColor.setBackground(PaletteActivity.this.cd_type);
                    PaletteActivity.this.typeMix.setTextColor(-16777216);
                    PaletteActivity.this.typeMix.setBackground(null);
                    PaletteActivity.this.typeCustom.setTextColor(-16777216);
                    PaletteActivity.this.typeCustom.setBackground(null);
                    if (PaletteActivity.this.curType.equals("mix")) {
                        PaletteActivity.this.pageMix_Left_To_Gone.start();
                    } else if (PaletteActivity.this.curType.equals("custom")) {
                        PaletteActivity.this.pageCustom_Left.start();
                    }
                    PaletteActivity.this.pageColor_Left.start();
                    PaletteActivity.this.curType = "color";
                } else if (view == PaletteActivity.this.typeMix) {
                    if ("mix".equals(PaletteActivity.this.curType)) {
                        return;
                    }
                    PaletteActivity.this.cancelAnimators();
                    PaletteActivity.this.typeColor.setTextColor(-16777216);
                    PaletteActivity.this.typeColor.setBackground(null);
                    PaletteActivity.this.typeMix.setTextColor(PaletteActivity.this.currentColor);
                    PaletteActivity.this.typeMix.setBackground(PaletteActivity.this.cd_type);
                    PaletteActivity.this.typeCustom.setTextColor(-16777216);
                    PaletteActivity.this.typeCustom.setBackground(null);
                    if (PaletteActivity.this.curType.equals("color")) {
                        PaletteActivity.this.pageMix_Right_From_Gone.start();
                        PaletteActivity.this.pageColor_Right.start();
                    } else if (PaletteActivity.this.curType.equals("custom")) {
                        PaletteActivity.this.pageMix_Left_From_Gone.start();
                        PaletteActivity.this.pageCustom_Left.start();
                    }
                    PaletteActivity.this.curType = "mix";
                } else if (view == PaletteActivity.this.typeCustom) {
                    if ("custom".equals(PaletteActivity.this.curType)) {
                        return;
                    }
                    PaletteActivity.this.cancelAnimators();
                    PaletteActivity.this.typeColor.setTextColor(-16777216);
                    PaletteActivity.this.typeColor.setBackground(null);
                    PaletteActivity.this.typeMix.setTextColor(-16777216);
                    PaletteActivity.this.typeMix.setBackground(null);
                    PaletteActivity.this.typeCustom.setTextColor(PaletteActivity.this.currentColor);
                    PaletteActivity.this.typeCustom.setBackground(PaletteActivity.this.cd_type);
                    if (PaletteActivity.this.curType.equals("mix")) {
                        PaletteActivity.this.pageMix_Right_To_Gone.start();
                    } else if (PaletteActivity.this.curType.equals("color")) {
                        PaletteActivity.this.pageColor_Right.start();
                    }
                    PaletteActivity.this.pageCustom_Right.start();
                    PaletteActivity.this.curType = "custom";
                } else if (view == PaletteActivity.this.confirm) {
                    MySharePreference.saveBooleanValue("PaletteIsColorOrMix", PaletteActivity.this.isColorOrMix.booleanValue());
                    boolean z = ((PaletteActivity.this.saveColor_A == -1 && PaletteActivity.this.saveColor_B == -1) || (PaletteActivity.this.saveColor_A == SystemProperties.getInt("persist.fyt.systemuiiconactive", -1) && PaletteActivity.this.saveColor_B == SystemProperties.getInt("persist.fyt.systemuibrightnesscolor", -1))) ? false : true;
                    if (PaletteActivity.this.startEnable != PaletteActivity.this.curEnable) {
                        z = true;
                    }
                    if (z) {
                        if (PaletteActivity.this.curEnable) {
                            SystemProperties.set("persist.fyt.systemuiiconactive", PaletteActivity.this.saveColor_A + "");
                            SystemProperties.set("persist.fyt.systemuibrightnesscolor", PaletteActivity.this.saveColor_B + "");
                            PaletteActivity.saveProp();
                        }
                        Intent intent = new Intent("com.syu.killsystemui");
                        Bundle bundle = new Bundle();
                        bundle.putString("from_package_name", "com.android.settings");
                        intent.putExtras(bundle);
                        PaletteActivity.this.sendBroadcast(intent);
                        PaletteActivity.this.myColorPickerBar.setOnTouchListener(null);
                        PaletteActivity.this.confirm.setEnabled(false);
                        PaletteActivity.this.restarting = Boolean.TRUE;
                        PaletteActivity.this.restarting_handler.sendEmptyMessage(1);
                        return;
                    }
                    PaletteActivity.this.closeWhthAnim();
                } else if (view == PaletteActivity.this.switchButton) {
                    boolean z2 = SystemProperties.getBoolean("persist.fyt.systemuipaletteenable", true);
                    PaletteActivity.this.curEnable = !z2;
                    SystemProperties.set("persist.fyt.systemuipaletteenable", PaletteActivity.this.curEnable + "");
                    PaletteActivity.this.switchButton.setBackgroundResource(PaletteActivity.this.curEnable ? R$drawable.palette_switch_button_p : R$drawable.palette_switch_button_n);
                    PaletteActivity.this.switchButton.setSelected(PaletteActivity.this.curEnable);
                    if (z2) {
                        PaletteActivity.this.upColor1.setBackgroundColor(PaletteActivity.this.int_color6);
                        PaletteActivity.this.upColor2.setBackgroundColor(PaletteActivity.this.int_color6);
                        return;
                    }
                    PaletteActivity.this.refrashcurColor();
                } else if (PaletteActivity.this.curEnable) {
                    LinearLayout linearLayout = (LinearLayout) view;
                    if (linearLayout.getChildCount() > 0) {
                        for (int i = 0; i < PaletteActivity.this.image_colors.length; i++) {
                            if (linearLayout.getChildAt(0) == PaletteActivity.this.image_colors[i]) {
                                PaletteActivity.this.image_colors[i].setImageResource(R$drawable.palette_select);
                                PaletteActivity paletteActivity = PaletteActivity.this;
                                paletteActivity.saveColor_A = paletteActivity.int_colors[i];
                                PaletteActivity paletteActivity2 = PaletteActivity.this;
                                paletteActivity2.saveColor_B = paletteActivity2.int_colors[i];
                                PaletteActivity.this.upColor1.setBackgroundColor(PaletteActivity.this.saveColor_A);
                                PaletteActivity.this.upColor2.setBackgroundColor(PaletteActivity.this.saveColor_B);
                                PaletteActivity.this.isColorOrMix = Boolean.TRUE;
                            } else {
                                PaletteActivity.this.image_colors[i].setImageResource(-1);
                            }
                        }
                        for (int i2 = 0; i2 < PaletteActivity.this.image_mixs.length; i2++) {
                            if (linearLayout.getChildAt(0) == PaletteActivity.this.image_mixs[i2]) {
                                PaletteActivity.this.image_mixs[i2].setImageResource(R$drawable.palette_select);
                                PaletteActivity paletteActivity3 = PaletteActivity.this;
                                paletteActivity3.saveColor_A = paletteActivity3.int_colors_mix_a[i2];
                                PaletteActivity paletteActivity4 = PaletteActivity.this;
                                paletteActivity4.saveColor_B = paletteActivity4.int_colors_mix_b[i2];
                                PaletteActivity.this.upColor1.setBackgroundColor(PaletteActivity.this.saveColor_A);
                                PaletteActivity.this.upColor2.setBackgroundColor(PaletteActivity.this.saveColor_B);
                                PaletteActivity.this.isColorOrMix = Boolean.TRUE;
                            } else {
                                PaletteActivity.this.image_mixs[i2].setImageResource(-1);
                            }
                        }
                        if (PaletteActivity.this.isColorOrMix.booleanValue()) {
                            PaletteActivity.this.customSelected.setVisibility(8);
                        }
                    }
                }
            }
        };
        this.mAnimatorListener = new Animator.AnimatorListener() { // from class: com.android.settings.colorpick.PaletteActivity.5
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                if (animator == PaletteActivity.this.pageMix_Right_From_Gone || animator == PaletteActivity.this.pageMix_Left_From_Gone) {
                    PaletteActivity.this.pageMixLayout.setVisibility(0);
                } else if (animator == PaletteActivity.this.pageColor_Left) {
                    PaletteActivity.this.pageColorLayout.setVisibility(0);
                } else if (animator == PaletteActivity.this.pageCustom_Right) {
                    PaletteActivity.this.pageCustomLayout.setVisibility(0);
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (animator == PaletteActivity.this.pageMix_Right_To_Gone || animator == PaletteActivity.this.pageMix_Left_To_Gone) {
                    PaletteActivity.this.pageMixLayout.setVisibility(8);
                } else if (animator == PaletteActivity.this.pageColor_Right) {
                    PaletteActivity.this.pageColorLayout.setVisibility(8);
                } else if (animator == PaletteActivity.this.pageCustom_Left) {
                    PaletteActivity.this.pageCustomLayout.setVisibility(8);
                } else if (animator == PaletteActivity.this.hide) {
                    PaletteActivity.this.closeThisActivity();
                }
            }
        };
        this.myReceiver = new BroadcastReceiver() { // from class: com.android.settings.colorpick.PaletteActivity.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("PaletteActivity", "onReceive: " + intent.getAction());
                if (intent.getAction().equals("com.syu.systemuiready")) {
                    PaletteActivity.this.showing = Boolean.FALSE;
                    PaletteActivity.this.closeWhthAnim();
                } else if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                    PaletteActivity.this.showing = Boolean.FALSE;
                    PaletteActivity.this.closeThisActivity();
                }
            }
        };
        this.restarting_handler = new Handler() { // from class: com.android.settings.colorpick.PaletteActivity.8
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Message message2 = new Message();
                int i = message.what;
                if (i == 1) {
                    Button button = PaletteActivity.this.confirm;
                    button.setText(SettingsApplication.mApplication.getResources().getString(R$string.set_palette_applying) + ".");
                    message2.what = 2;
                } else if (i == 2) {
                    Button button2 = PaletteActivity.this.confirm;
                    button2.setText(SettingsApplication.mApplication.getResources().getString(R$string.set_palette_applying) + "..");
                    message2.what = 3;
                } else if (i == 3) {
                    Button button3 = PaletteActivity.this.confirm;
                    button3.setText(SettingsApplication.mApplication.getResources().getString(R$string.set_palette_applying) + "...");
                    message2.what = 1;
                }
                if (PaletteActivity.this.showing.booleanValue()) {
                    PaletteActivity.this.restarting_handler.sendMessageDelayed(message2, 1000L);
                }
            }
        };
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getDensity();
        int i = SettingsApplication.mWidthFix;
        int i2 = SettingsApplication.mHeightFix;
        if (i <= i2) {
            i2 = SettingsApplication.mWidthFix;
        }
        this.minLabel = i2;
        int i3 = SettingsApplication.mWidthFix;
        int i4 = SettingsApplication.mHeightFix;
        if (i3 > i4) {
            i4 = SettingsApplication.mWidthFix;
        }
        this.maxLabel = i4;
        this.sWindowManager = (WindowManager) getSystemService("window");
        boolean z = SystemProperties.getBoolean("persist.fyt.systemuipaletteenable", true);
        this.startEnable = z;
        this.curEnable = z;
        if (z) {
            this.currentColor = SystemProperties.getInt("persist.fyt.systemuiiconactive", this.currentColor);
        } else {
            this.currentColor = this.int_color6;
        }
        this.colorStateList = new ColorStateList(new int[][]{new int[]{16842913}, new int[0]}, new int[]{this.currentColor, -7829368});
        this.fixSelectX = MySharePreference.getIntValue("ColorPickerBarPosition", this.fixSelectX);
        createViews();
        readCurrentState();
        this.show.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.syu.systemuiready");
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(this.myReceiver, intentFilter);
        this.showing = Boolean.TRUE;
    }

    private void getDensity() {
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.density = displayMetrics.densityDpi;
        Log.d("PaletteActivity", "getDensity: " + this.density);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        unregisterReceiver(this.myReceiver);
        super.onDestroy();
    }

    public void createViews() {
        int i = (int) ((this.minLabel * 0.025f) / (this.density / 160.0f));
        if (SettingsApplication.mWidthFix == 2880 && SettingsApplication.mHeightFix == 1080) {
            i = 20;
        }
        Log.d("PaletteActivity", "createViews: " + i + "   " + Math.round(this.minLabel * 0.025f) + "   " + this.density);
        this.page = new RelativeLayout(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = 264;
        layoutParams.format = 1;
        layoutParams.gravity = 17;
        layoutParams.type = 2038;
        layoutParams.width = SettingsApplication.mWidthFix;
        layoutParams.height = SettingsApplication.mHeightFix;
        layoutParams.windowAnimations = R$anim.scale_in;
        this.transparent = new View(this);
        this.transparent.setLayoutParams(new LinearLayout.LayoutParams(SettingsApplication.mWidthFix, SettingsApplication.mHeightFix));
        View view = this.transparent;
        new Color();
        view.setBackground(new ColorDrawable(Color.valueOf(0.0f, 0.0f, 0.0f, 0.0f).toArgb()));
        this.transparent.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.colorpick.PaletteActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                PaletteActivity.this.closeWhthAnim();
            }
        });
        this.page.addView(this.transparent);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.98f), Math.round(this.minLabel * 0.6f));
        layoutParams2.setMargins(Math.round((SettingsApplication.mWidthFix - (this.minLabel * 0.98f)) / 2.0f), Math.round(this.minLabel * 0.2f), 0, 0);
        relativeLayout.setLayoutParams(layoutParams2);
        Bitmap createBitmap = Bitmap.createBitmap(Math.round(this.minLabel * 0.98f), Math.round(this.minLabel * 0.6f), Bitmap.Config.ARGB_4444);
        createBitmap.eraseColor(-1);
        relativeLayout.setBackground(new RoundRectImageDrawable(createBitmap, Math.round(this.minLabel * 0.025f)));
        relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.colorpick.PaletteActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
            }
        });
        this.upColor1 = new ImageView(this);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.4f), Math.round(this.minLabel * 0.11f));
        layoutParams3.setMargins(Math.round(this.minLabel * 0.09f), Math.round(this.minLabel * 0.06f), 0, 0);
        this.upColor1.setLayoutParams(layoutParams3);
        this.upColor1.setBackgroundColor(this.currentColor);
        relativeLayout.addView(this.upColor1);
        this.upColor2 = new ImageView(this);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.4f), Math.round(this.minLabel * 0.1f));
        layoutParams4.setMargins(Math.round(this.minLabel * 0.09f), Math.round(this.minLabel * 0.17f), 0, 0);
        this.upColor2.setLayoutParams(layoutParams4);
        this.upColor2.setBackgroundColor(this.currentColor);
        relativeLayout.addView(this.upColor2);
        this.upImage = new ImageView(this);
        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.44f), Math.round(this.minLabel * 0.29f));
        layoutParams5.setMargins(Math.round(this.minLabel * 0.07f), Math.round(this.minLabel * 0.0375f), 0, 0);
        this.upImage.setLayoutParams(layoutParams5);
        this.upImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.upImage.setAdjustViewBounds(true);
        this.upImage.setImageDrawable(SettingsApplication.mApplication.getResources().getDrawable(R$drawable.palette_show_transparent));
        relativeLayout.addView(this.upImage);
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.21f), Math.round(this.minLabel * 0.04f));
        layoutParams6.setMargins(Math.round(this.minLabel * 0.58f), Math.round(this.minLabel * 0.053f), 0, 0);
        textView.setLayoutParams(layoutParams6);
        textView.setTextColor(-16777216);
        float f = i;
        textView.setTextSize(f);
        textView.setText(SettingsApplication.mApplication.getResources().getString(R$string.set_palette_palette));
        relativeLayout.addView(textView);
        this.switchButton = new Button(this);
        LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(Math.round(this.minLabel / 12), Math.round(this.minLabel / 24));
        layoutParams7.setMargins(Math.round(this.minLabel * 0.8f), Math.round(this.minLabel * 0.053f), 0, 0);
        this.switchButton.setLayoutParams(layoutParams7);
        this.switchButton.setBackgroundResource(this.startEnable ? R$drawable.palette_switch_button_p : R$drawable.palette_switch_button_n);
        this.switchButton.setSelected(this.startEnable);
        this.switchButton.setBackgroundTintList(this.colorStateList);
        this.switchButton.setOnClickListener(this.onClickListener);
        relativeLayout.addView(this.switchButton);
        this.typeCustom = new Button(this);
        LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.24f), Math.round(this.minLabel * 0.0625f));
        layoutParams8.setMarginStart(Math.round(this.minLabel * 0.07f));
        layoutParams8.topMargin = Math.round(this.minLabel / 3);
        this.typeCustom.setLayoutParams(layoutParams8);
        if (SettingsApplication.mWidthFix == 2880 && SettingsApplication.mHeightFix == 1080) {
            this.typeCustom.setTextSize(18.0f);
        } else {
            this.typeCustom.setTextSize(((int) (this.minLabel * 0.02f)) / (this.density / 160.0f));
        }
        this.typeCustom.setText(SettingsApplication.mResources.getString(R$string.set_palette_gradient));
        this.typeCustom.setTextColor(-16777216);
        this.typeCustom.setAllCaps(false);
        relativeLayout.addView(this.typeCustom);
        setButtonOnClickListener(this.typeCustom);
        this.typeMix = new Button(this);
        LinearLayout.LayoutParams layoutParams9 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.24f), Math.round(this.minLabel * 0.0625f));
        layoutParams9.setMarginStart(Math.round(this.minLabel * 0.37f));
        layoutParams9.topMargin = Math.round(this.minLabel / 3);
        this.typeMix.setLayoutParams(layoutParams9);
        if (SettingsApplication.mWidthFix == 2880 && SettingsApplication.mHeightFix == 1080) {
            this.typeMix.setTextSize(18.0f);
        } else {
            this.typeMix.setTextSize(((int) (this.minLabel * 0.02f)) / (this.density / 160.0f));
        }
        this.typeMix.setText(SettingsApplication.mApplication.getResources().getString(R$string.set_palette_mix));
        this.typeMix.setTextColor(-16777216);
        this.typeMix.setAllCaps(false);
        relativeLayout.addView(this.typeMix);
        setButtonOnClickListener(this.typeMix);
        this.typeColor = new Button(this);
        LinearLayout.LayoutParams layoutParams10 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.24f), Math.round(this.minLabel * 0.0625f));
        layoutParams10.setMarginStart(Math.round(this.minLabel * 0.67f));
        layoutParams10.topMargin = Math.round(this.minLabel / 3);
        this.typeColor.setLayoutParams(layoutParams10);
        if (SettingsApplication.mWidthFix == 2880 && SettingsApplication.mHeightFix == 1080) {
            this.typeColor.setTextSize(18.0f);
        } else {
            this.typeColor.setTextSize(((int) (this.minLabel * 0.02f)) / (this.density / 160.0f));
        }
        this.typeColor.setText(SettingsApplication.mApplication.getResources().getString(R$string.set_palette_basic));
        this.typeColor.setTextColor(this.currentColor);
        this.typeColor.setAllCaps(false);
        relativeLayout.addView(this.typeColor);
        setButtonOnClickListener(this.typeColor);
        Bitmap createBitmap2 = Bitmap.createBitmap(Math.round(this.minLabel * 0.32f), Math.round(this.minLabel * 0.0625f), Bitmap.Config.ARGB_4444);
        createBitmap2.eraseColor(Color.rgb(216, 216, 214));
        this.cd_type = new RoundRectImageDrawable(createBitmap2, Math.round(this.minLabel * 0.03125f));
        this.typeCustom.setBackground(null);
        this.typeMix.setBackground(null);
        this.typeColor.setBackground(this.cd_type);
        LinearLayout.LayoutParams layoutParams11 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.84f), Math.round(this.minLabel * 0.05f));
        layoutParams11.setMargins(Math.round(this.minLabel * 0.07f), (this.minLabel * 5) / 12, 0, 0);
        RelativeLayout relativeLayout2 = new RelativeLayout(this);
        this.pageCustomLayout = relativeLayout2;
        relativeLayout2.setLayoutParams(layoutParams11);
        this.pageCustomLayout.setVisibility(8);
        LinearLayout.LayoutParams layoutParams12 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.48f), Math.round(this.minLabel * 0.05f));
        layoutParams12.setMargins(Math.round(this.minLabel * 0.25f), (this.minLabel * 5) / 12, 0, 0);
        LinearLayout linearLayout = new LinearLayout(this);
        this.pageMixLayout = linearLayout;
        linearLayout.setLayoutParams(layoutParams12);
        this.pageMixLayout.setVisibility(8);
        LinearLayout.LayoutParams layoutParams13 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.84f), Math.round(this.minLabel * 0.05f));
        layoutParams13.setMargins(Math.round(this.minLabel * 0.07f), (this.minLabel * 5) / 12, 0, 0);
        LinearLayout linearLayout2 = new LinearLayout(this);
        this.pageColorLayout = linearLayout2;
        linearLayout2.setLayoutParams(layoutParams13);
        relativeLayout.addView(this.pageColorLayout);
        relativeLayout.addView(this.pageMixLayout);
        relativeLayout.addView(this.pageCustomLayout);
        addColorButtons();
        createAnimatorSet();
        this.confirm = new Button(this);
        LinearLayout.LayoutParams layoutParams14 = new LinearLayout.LayoutParams(Math.round(this.minLabel * 0.32f), Math.round(this.minLabel * 0.07f));
        layoutParams14.setMargins(Math.round(this.minLabel * 0.33f), Math.round(this.minLabel * 0.5f), 0, 0);
        this.confirm.setLayoutParams(layoutParams14);
        this.confirm.setText(SettingsApplication.mResources.getString(R$string.set_palette_confirm));
        this.confirm.setTextColor(-1);
        this.confirm.setTextSize(f);
        this.confirm.setAllCaps(false);
        Bitmap createBitmap3 = Bitmap.createBitmap(Math.round(this.minLabel * 0.32f), Math.round(this.minLabel * 0.06f), Bitmap.Config.ARGB_4444);
        createBitmap3.eraseColor(this.currentColor);
        RoundRectImageDrawable roundRectImageDrawable = new RoundRectImageDrawable(createBitmap3, Math.round(this.minLabel * 0.035f));
        this.cd_confirm = roundRectImageDrawable;
        this.confirm.setBackground(roundRectImageDrawable);
        relativeLayout.addView(this.confirm);
        setButtonOnClickListener(this.confirm);
        this.page.addView(relativeLayout);
        this.sWindowManager.addView(this.page, layoutParams);
        AnimatorSet animatorSet = new AnimatorSet();
        this.hide = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(relativeLayout, "scaleX", 1.0f, 0.0f));
        this.hide.playTogether(ObjectAnimator.ofFloat(relativeLayout, "scaleY", 1.0f, 0.0f));
        this.hide.setDuration(800L);
        this.hide.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.show = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(relativeLayout, "scaleX", 0.0f, 1.0f));
        this.show.playTogether(ObjectAnimator.ofFloat(relativeLayout, "scaleY", 0.0f, 1.0f));
        this.show.setDuration(800L);
    }

    public void closeWhthAnim() {
        this.hide.start();
    }

    public void closeThisActivity() {
        this.page.setVisibility(8);
        try {
            this.sWindowManager.removeView(this.page);
            finish();
        } catch (Exception unused) {
        }
    }

    public void setButtonOnClickListener(Button button) {
        if (button != null) {
            button.setOnClickListener(this.onClickListener);
        }
    }

    public void setButtonOnTouchListener(ImageView imageView) {
        if (imageView != null) {
            imageView.setOnTouchListener(this.onTouchListener);
        }
    }

    public void addColorButtons() {
        int round = Math.round(this.minLabel * 0.05f);
        int round2 = Math.round(this.minLabel * 0.12f);
        int i = 0;
        for (int i2 = 0; i2 < this.RRD_colors.length; i2++) {
            Bitmap createBitmap = Bitmap.createBitmap(round, round, Bitmap.Config.ARGB_4444);
            createBitmap.eraseColor(this.int_colors[i2]);
            this.RRD_colors[i2] = new RoundRectImageDrawable(createBitmap, round / 2);
        }
        int i3 = (round2 - round) / 2;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(round, round);
        layoutParams.setMargins(i3, 0, i3, 0);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(round2, round);
        layoutParams2.gravity = 17;
        for (int i4 = 0; i4 < this.image_colors.length; i4++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
            imageView.setBackground(this.RRD_colors[i4]);
            this.image_colors[i4] = imageView;
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams2);
            linearLayout.setOnClickListener(this.onClickListener);
            linearLayout.addView(imageView);
            this.pageColorLayout.addView(linearLayout);
        }
        while (i < this.image_mixs.length) {
            ImageView imageView2 = new ImageView(this);
            imageView2.setLayoutParams(layoutParams);
            imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView2.setAdjustViewBounds(true);
            Resources resources = SettingsApplication.mResources;
            SettingsApplication settingsApplication = SettingsApplication.mApplication;
            StringBuilder sb = new StringBuilder();
            sb.append("palette_mix_");
            int i5 = i + 1;
            sb.append(i5);
            imageView2.setBackground(resources.getDrawable(settingsApplication.getIdDrawable(sb.toString())));
            this.image_mixs[i] = imageView2;
            LinearLayout linearLayout2 = new LinearLayout(this);
            linearLayout2.setLayoutParams(layoutParams2);
            linearLayout2.setOnClickListener(this.onClickListener);
            linearLayout2.addView(imageView2);
            this.pageMixLayout.addView(linearLayout2);
            i = i5;
        }
        this.myColorPickerBar = new MyColorPickerBar(this, round / 2);
        this.myColorPickerBar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.myColorPickerBar.setLongClickable(true);
        setButtonOnTouchListener(this.myColorPickerBar);
        this.pageCustomLayout.addView(this.myColorPickerBar);
        int round3 = Math.round(this.minLabel * 0.0625f);
        this.customSelected = new ImageView(this);
        Bitmap createBitmap2 = Bitmap.createBitmap(5, round3, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(createBitmap2);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(-1);
        canvas.drawLine(0.0f, 0.0f, 4.0f, 0.0f, paint);
        float f = round3 - 2;
        canvas.drawLine(4.0f, 0.0f, 4.0f, f, paint);
        canvas.drawLine(4.0f, f, 0.0f, f, paint);
        canvas.drawLine(0.0f, f, 0.0f, 0.0f, paint);
        this.customSelected.setImageBitmap(createBitmap2);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(6, -1);
        layoutParams3.setMarginStart(this.fixSelectX);
        this.customSelected.setLayoutParams(layoutParams3);
        this.pageCustomLayout.addView(this.customSelected);
        this.customSelected.setVisibility(8);
    }

    public void createAnimatorSet() {
        AnimatorSet animatorSet = new AnimatorSet();
        this.pageCustom_Left = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.pageCustomLayout, "translationX", 0.0f, -Math.round(this.minLabel * 0.98f)));
        this.pageCustom_Left.setDuration(1000L);
        this.pageCustom_Left.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.pageCustom_Right = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.pageCustomLayout, "translationX", -Math.round(this.minLabel * 0.98f), 0.0f));
        this.pageCustom_Right.setDuration(1000L);
        this.pageCustom_Right.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.pageMix_Left_From_Gone = animatorSet3;
        animatorSet3.playTogether(ObjectAnimator.ofFloat(this.pageMixLayout, "translationX", Math.round(this.minLabel * 0.98f), 0.0f));
        this.pageMix_Left_From_Gone.setDuration(1000L);
        this.pageMix_Left_From_Gone.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet4 = new AnimatorSet();
        this.pageMix_Right_From_Gone = animatorSet4;
        animatorSet4.playTogether(ObjectAnimator.ofFloat(this.pageMixLayout, "translationX", -Math.round(this.minLabel * 0.98f), 0.0f));
        this.pageMix_Right_From_Gone.setDuration(1000L);
        this.pageMix_Right_From_Gone.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet5 = new AnimatorSet();
        this.pageMix_Left_To_Gone = animatorSet5;
        animatorSet5.playTogether(ObjectAnimator.ofFloat(this.pageMixLayout, "translationX", 0.0f, -Math.round(this.minLabel * 0.98f)));
        this.pageMix_Left_To_Gone.setDuration(1000L);
        this.pageMix_Left_To_Gone.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet6 = new AnimatorSet();
        this.pageMix_Right_To_Gone = animatorSet6;
        animatorSet6.playTogether(ObjectAnimator.ofFloat(this.pageMixLayout, "translationX", 0.0f, Math.round(this.minLabel * 0.98f)));
        this.pageMix_Right_To_Gone.setDuration(1000L);
        this.pageMix_Right_To_Gone.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet7 = new AnimatorSet();
        this.pageColor_Left = animatorSet7;
        animatorSet7.playTogether(ObjectAnimator.ofFloat(this.pageColorLayout, "translationX", Math.round(this.minLabel * 0.98f), 0.0f));
        this.pageColor_Left.setDuration(1000L);
        this.pageColor_Left.addListener(this.mAnimatorListener);
        AnimatorSet animatorSet8 = new AnimatorSet();
        this.pageColor_Right = animatorSet8;
        animatorSet8.playTogether(ObjectAnimator.ofFloat(this.pageColorLayout, "translationX", 0.0f, Math.round(this.minLabel * 0.98f)));
        this.pageColor_Right.setDuration(1000L);
        this.pageColor_Right.addListener(this.mAnimatorListener);
    }

    public void cancelAnimators() {
        if (this.pageCustom_Left.isRunning()) {
            this.pageCustom_Left.cancel();
        }
        if (this.pageCustom_Right.isRunning()) {
            this.pageCustom_Right.cancel();
        }
        if (this.pageMix_Left_From_Gone.isRunning()) {
            this.pageMix_Left_From_Gone.cancel();
        }
        if (this.pageMix_Right_From_Gone.isRunning()) {
            this.pageMix_Right_From_Gone.cancel();
        }
        if (this.pageMix_Left_To_Gone.isRunning()) {
            this.pageMix_Left_To_Gone.cancel();
        }
        if (this.pageMix_Right_To_Gone.isRunning()) {
            this.pageMix_Right_To_Gone.cancel();
        }
        if (this.pageColor_Left.isRunning()) {
            this.pageColor_Left.cancel();
        }
        if (this.pageColor_Right.isRunning()) {
            this.pageColor_Right.cancel();
        }
    }

    public void refrashcurColor() {
        boolean z;
        int i = 0;
        if (!this.isColorOrMix.booleanValue()) {
            this.typeColor.setTextColor(-16777216);
            this.typeColor.setBackground(null);
            this.typeMix.setTextColor(-16777216);
            this.typeMix.setBackground(null);
            this.typeCustom.setTextColor(this.currentColor);
            this.typeCustom.setBackground(this.cd_type);
            if (!this.curType.equals("custom")) {
                this.pageCustom_Right.start();
                if (this.curType.equals("color")) {
                    this.pageColor_Right.start();
                } else {
                    this.pageMix_Right_To_Gone.start();
                }
            }
            this.customSelected.setVisibility(0);
            this.curType = "custom";
            return;
        }
        int i2 = 0;
        while (true) {
            int[] iArr = this.int_colors;
            if (i2 >= iArr.length) {
                z = false;
                break;
            }
            int i3 = iArr[i2];
            if (i3 == this.saveColor_A && i3 == this.saveColor_B) {
                this.image_colors[i2].setImageResource(R$drawable.palette_select);
                if (this.curEnable) {
                    this.upColor1.setBackgroundColor(i3);
                    this.upColor2.setBackgroundColor(i3);
                } else {
                    this.upColor1.setBackgroundColor(this.int_color6);
                    this.upColor2.setBackgroundColor(this.int_color6);
                }
                if (!this.curType.equals("color")) {
                    this.pageCustom_Left.start();
                    if (this.curType.equals("custom")) {
                        this.pageCustom_Left.start();
                    } else {
                        this.pageMix_Left_To_Gone.start();
                    }
                }
                this.curType = "color";
                z = true;
            } else {
                i2++;
            }
        }
        if (z) {
            return;
        }
        while (true) {
            int[] iArr2 = this.int_colors_mix_a;
            if (i >= iArr2.length) {
                return;
            }
            if (iArr2[i] == this.saveColor_A && this.int_colors_mix_b[i] == this.saveColor_B) {
                this.typeColor.setTextColor(-16777216);
                this.typeColor.setBackground(null);
                this.typeCustom.setTextColor(-16777216);
                this.typeCustom.setBackground(null);
                this.typeMix.setTextColor(this.currentColor);
                this.typeMix.setBackground(this.cd_type);
                if (!this.curType.equals("mix")) {
                    if (this.curType.equals("color")) {
                        this.pageColor_Right.start();
                        this.pageMix_Right_From_Gone.start();
                    } else {
                        this.pageCustom_Left.start();
                        this.pageMix_Left_From_Gone.start();
                    }
                }
                this.curType = "mix";
                this.image_mixs[i].setImageResource(R$drawable.palette_select);
                if (this.curEnable) {
                    this.upColor1.setBackgroundColor(this.saveColor_A);
                    this.upColor2.setBackgroundColor(this.saveColor_B);
                    return;
                }
                this.upColor1.setBackgroundColor(this.int_color6);
                this.upColor2.setBackgroundColor(this.int_color6);
                return;
            }
            i++;
        }
    }

    public static void saveProp() {
        new Thread(new Runnable() { // from class: com.android.settings.colorpick.PaletteActivity.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SyuJniNative.getInstance().syu_jni_command(253, null, null);
                } catch (Exception unused) {
                }
            }
        }).start();
    }

    public void readCurrentState() {
        this.saveColor_A = SystemProperties.getInt("persist.fyt.systemuiiconactive", this.int_color5);
        this.saveColor_B = SystemProperties.getInt("persist.fyt.systemuibrightnesscolor", this.int_color5);
        this.isColorOrMix = Boolean.valueOf(MySharePreference.getBooleanValue("PaletteIsColorOrMix", this.isColorOrMix.booleanValue()));
        refrashcurColor();
    }
}
