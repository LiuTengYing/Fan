package com.android.settingslib.users;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.CircleFramedDrawable;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
/* loaded from: classes2.dex */
public class EditUserPhotoController {
    private final Activity mActivity;
    private final ActivityStarter mActivityStarter;
    private final String mFileAuthority;
    private final ImageView mImageView;
    private final File mImagesDir;
    private Bitmap mNewUserPhotoBitmap;
    private Drawable mNewUserPhotoDrawable;

    public EditUserPhotoController(Activity activity, ActivityStarter activityStarter, ImageView imageView, Bitmap bitmap, Drawable drawable, String str) {
        this.mActivity = activity;
        this.mActivityStarter = activityStarter;
        this.mFileAuthority = str;
        File file = new File(activity.getCacheDir(), "multi_user");
        this.mImagesDir = file;
        file.mkdir();
        this.mImageView = imageView;
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditUserPhotoController.this.lambda$new$0(view);
            }
        });
        this.mNewUserPhotoBitmap = bitmap;
        this.mNewUserPhotoDrawable = drawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        showAvatarPicker();
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 1004) {
            if (intent.hasExtra("default_icon_tint_color")) {
                onDefaultIconSelected(intent.getIntExtra("default_icon_tint_color", -1));
                return true;
            } else if (intent.getData() != null) {
                onPhotoCropped(intent.getData());
                return true;
            }
        }
        return false;
    }

    public Drawable getNewUserPhotoDrawable() {
        return this.mNewUserPhotoDrawable;
    }

    private void showAvatarPicker() {
        Intent intent = new Intent(this.mImageView.getContext(), AvatarPickerActivity.class);
        intent.putExtra("file_authority", this.mFileAuthority);
        this.mActivityStarter.startActivityForResult(intent, 1004);
    }

    private void onDefaultIconSelected(final int i) {
        try {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    EditUserPhotoController.this.lambda$onDefaultIconSelected$2(i);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("EditUserPhotoController", "Error processing default icon", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDefaultIconSelected$2(int i) {
        Resources resources = this.mActivity.getResources();
        final Bitmap convertToBitmapAtUserIconSize = UserIcons.convertToBitmapAtUserIconSize(resources, UserIcons.getDefaultUserIconInColor(resources, i));
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                EditUserPhotoController.this.lambda$onDefaultIconSelected$1(convertToBitmapAtUserIconSize);
            }
        });
    }

    private void onPhotoCropped(final Uri uri) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                EditUserPhotoController.this.lambda$onPhotoCropped$4(uri);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0030  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x003d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$onPhotoCropped$4(android.net.Uri r6) {
        /*
            r5 = this;
            java.lang.String r0 = "Cannot close image stream"
            java.lang.String r1 = "EditUserPhotoController"
            r2 = 0
            android.app.Activity r3 = r5.mActivity     // Catch: java.lang.Throwable -> L20 java.io.FileNotFoundException -> L22
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch: java.lang.Throwable -> L20 java.io.FileNotFoundException -> L22
            java.io.InputStream r6 = r3.openInputStream(r6)     // Catch: java.lang.Throwable -> L20 java.io.FileNotFoundException -> L22
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeStream(r6)     // Catch: java.io.FileNotFoundException -> L1e java.lang.Throwable -> L39
            if (r6 == 0) goto L2e
            r6.close()     // Catch: java.io.IOException -> L19
            goto L2e
        L19:
            r6 = move-exception
            android.util.Log.w(r1, r0, r6)
            goto L2e
        L1e:
            r3 = move-exception
            goto L24
        L20:
            r5 = move-exception
            goto L3b
        L22:
            r3 = move-exception
            r6 = r2
        L24:
            java.lang.String r4 = "Cannot find image file"
            android.util.Log.w(r1, r4, r3)     // Catch: java.lang.Throwable -> L39
            if (r6 == 0) goto L2e
            r6.close()     // Catch: java.io.IOException -> L19
        L2e:
            if (r2 == 0) goto L38
            com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda4 r6 = new com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda4
            r6.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r6)
        L38:
            return
        L39:
            r5 = move-exception
            r2 = r6
        L3b:
            if (r2 == 0) goto L45
            r2.close()     // Catch: java.io.IOException -> L41
            goto L45
        L41:
            r6 = move-exception
            android.util.Log.w(r1, r0, r6)
        L45:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.users.EditUserPhotoController.lambda$onPhotoCropped$4(android.net.Uri):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onPhotoProcessed */
    public void lambda$onPhotoCropped$3(Bitmap bitmap) {
        if (bitmap != null) {
            this.mNewUserPhotoBitmap = bitmap;
            CircleFramedDrawable circleFramedDrawable = CircleFramedDrawable.getInstance(this.mImageView.getContext(), this.mNewUserPhotoBitmap);
            this.mNewUserPhotoDrawable = circleFramedDrawable;
            this.mImageView.setImageDrawable(circleFramedDrawable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File saveNewUserPhotoBitmap() {
        if (this.mNewUserPhotoBitmap == null) {
            return null;
        }
        try {
            File file = new File(this.mImagesDir, "NewUserPhoto.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            this.mNewUserPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            Log.e("EditUserPhotoController", "Cannot create temp file", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bitmap loadNewUserPhotoBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeNewUserPhotoBitmapFile() {
        new File(this.mImagesDir, "NewUserPhoto.png").delete();
    }
}
