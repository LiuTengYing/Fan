package com.android.settingslib.users;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.util.EventLog;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import libcore.io.Streams;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class AvatarPhotoController {
    private final AvatarUi mAvatarUi;
    private final ContextInjector mContextInjector;
    private final Uri mCropPictureUri;
    private final File mImagesDir;
    private final int mPhotoSize;
    private final Uri mPreCropPictureUri;
    private final Uri mTakePictureUri;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface AvatarUi {
        int getPhotoSize();

        boolean isFinishing();

        void returnUriResult(Uri uri);

        void startActivityForResult(Intent intent, int i);

        boolean startSystemActivityForResult(Intent intent, int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface ContextInjector {
        Uri createTempImageUri(File file, String str, boolean z);

        File getCacheDir();

        ContentResolver getContentResolver();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AvatarPhotoController(AvatarUi avatarUi, ContextInjector contextInjector, boolean z) {
        this.mAvatarUi = avatarUi;
        this.mContextInjector = contextInjector;
        File file = new File(contextInjector.getCacheDir(), "multi_user");
        this.mImagesDir = file;
        file.mkdir();
        this.mPreCropPictureUri = contextInjector.createTempImageUri(file, "PreCropEditUserPhoto.jpg", !z);
        this.mCropPictureUri = contextInjector.createTempImageUri(file, "CropEditUserPhoto.jpg", !z);
        this.mTakePictureUri = contextInjector.createTempImageUri(file, "TakeEditUserPhoto.jpg", !z);
        this.mPhotoSize = avatarUi.getPhotoSize();
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            return false;
        }
        Uri data = (intent == null || intent.getData() == null) ? this.mTakePictureUri : intent.getData();
        if (!"content".equals(data.getScheme())) {
            Log.e("AvatarPhotoController", "Invalid pictureUri scheme: " + data.getScheme());
            EventLog.writeEvent(1397638484, "172939189", -1, data.getPath());
            return false;
        }
        switch (i) {
            case 1001:
                copyAndCropPhoto(data, true);
                return true;
            case 1002:
                if (this.mTakePictureUri.equals(data)) {
                    cropPhoto(data);
                } else {
                    copyAndCropPhoto(data, false);
                }
                return true;
            case 1003:
                this.mAvatarUi.returnUriResult(data);
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE_SECURE");
        appendOutputExtra(intent, this.mTakePictureUri);
        this.mAvatarUi.startActivityForResult(intent, 1002);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void choosePhoto() {
        Intent intent = new Intent("android.provider.action.PICK_IMAGES", (Uri) null);
        intent.setType("image/*");
        this.mAvatarUi.startActivityForResult(intent, 1001);
    }

    private void copyAndCropPhoto(final Uri uri, final boolean z) {
        try {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.users.AvatarPhotoController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AvatarPhotoController.this.lambda$copyAndCropPhoto$1(uri, z);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("AvatarPhotoController", "Error performing copy-and-crop", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$copyAndCropPhoto$1(Uri uri, boolean z) {
        ContentResolver contentResolver = this.mContextInjector.getContentResolver();
        try {
            InputStream openInputStream = contentResolver.openInputStream(uri);
            OutputStream openOutputStream = contentResolver.openOutputStream(this.mPreCropPictureUri);
            try {
                Streams.copy(openInputStream, openOutputStream);
                if (openOutputStream != null) {
                    openOutputStream.close();
                }
                if (openInputStream != null) {
                    openInputStream.close();
                }
                Runnable runnable = new Runnable() { // from class: com.android.settingslib.users.AvatarPhotoController$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AvatarPhotoController.this.lambda$copyAndCropPhoto$0();
                    }
                };
                if (z) {
                    ThreadUtils.postOnMainThreadDelayed(runnable, 150L);
                } else {
                    ThreadUtils.postOnMainThread(runnable);
                }
            } catch (Throwable th) {
                if (openOutputStream != null) {
                    try {
                        openOutputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            Log.w("AvatarPhotoController", "Failed to copy photo", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$copyAndCropPhoto$0() {
        if (this.mAvatarUi.isFinishing()) {
            return;
        }
        cropPhoto(this.mPreCropPictureUri);
    }

    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        appendOutputExtra(intent, this.mCropPictureUri);
        appendCropExtras(intent);
        try {
            StrictMode.disableDeathOnFileUriExposure();
            if (this.mAvatarUi.startSystemActivityForResult(intent, 1003)) {
                return;
            }
            StrictMode.enableDeathOnFileUriExposure();
            onPhotoNotCropped(uri);
        } finally {
            StrictMode.enableDeathOnFileUriExposure();
        }
    }

    private void appendOutputExtra(Intent intent, Uri uri) {
        intent.putExtra("output", uri);
        intent.addFlags(3);
        intent.setClipData(ClipData.newRawUri("output", uri));
    }

    private void appendCropExtras(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", this.mPhotoSize);
        intent.putExtra("outputY", this.mPhotoSize);
    }

    private void onPhotoNotCropped(final Uri uri) {
        try {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.users.AvatarPhotoController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AvatarPhotoController.this.lambda$onPhotoNotCropped$3(uri);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("AvatarPhotoController", "Error performing internal crop", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPhotoNotCropped$3(Uri uri) {
        int i = this.mPhotoSize;
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        try {
            Bitmap decodeStream = BitmapFactory.decodeStream(this.mContextInjector.getContentResolver().openInputStream(uri));
            if (decodeStream != null) {
                int rotation = getRotation(uri);
                int min = Math.min(decodeStream.getWidth(), decodeStream.getHeight());
                int width = (decodeStream.getWidth() - min) / 2;
                int height = (decodeStream.getHeight() - min) / 2;
                Matrix matrix = new Matrix();
                RectF rectF = new RectF(width, height, width + min, height + min);
                int i2 = this.mPhotoSize;
                matrix.setRectToRect(rectF, new RectF(0.0f, 0.0f, i2, i2), Matrix.ScaleToFit.CENTER);
                int i3 = this.mPhotoSize;
                matrix.postRotate(rotation, i3 / 2.0f, i3 / 2.0f);
                canvas.drawBitmap(decodeStream, matrix, new Paint());
                saveBitmapToFile(createBitmap, new File(this.mImagesDir, "CropEditUserPhoto.jpg"));
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.users.AvatarPhotoController$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        AvatarPhotoController.this.lambda$onPhotoNotCropped$2();
                    }
                });
            }
        } catch (FileNotFoundException unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPhotoNotCropped$2() {
        this.mAvatarUi.returnUriResult(this.mCropPictureUri);
    }

    private int getRotation(Uri uri) {
        int i = -1;
        try {
            i = new ExifInterface(this.mContextInjector.getContentResolver().openInputStream(uri)).getAttributeInt("Orientation", -1);
        } catch (IOException e) {
            Log.e("AvatarPhotoController", "Error while getting rotation", e);
        }
        if (i != 3) {
            if (i != 6) {
                return i != 8 ? 0 : 270;
            }
            return 90;
        }
        return 180;
    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("AvatarPhotoController", "Cannot create temp file", e);
        }
    }

    /* loaded from: classes2.dex */
    static class AvatarUiImpl implements AvatarUi {
        private final AvatarPickerActivity mActivity;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AvatarUiImpl(AvatarPickerActivity avatarPickerActivity) {
            this.mActivity = avatarPickerActivity;
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.AvatarUi
        public boolean isFinishing() {
            return this.mActivity.isFinishing() || this.mActivity.isDestroyed();
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.AvatarUi
        public void returnUriResult(Uri uri) {
            this.mActivity.returnUriResult(uri);
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.AvatarUi
        public void startActivityForResult(Intent intent, int i) {
            this.mActivity.startActivityForResult(intent, i);
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.AvatarUi
        public boolean startSystemActivityForResult(Intent intent, int i) {
            List<ResolveInfo> queryIntentActivities = this.mActivity.getPackageManager().queryIntentActivities(intent, 1048576);
            if (queryIntentActivities.isEmpty()) {
                Log.w("AvatarPhotoController", "No system package activity could be found for code " + i);
                return false;
            }
            intent.setPackage(queryIntentActivities.get(0).activityInfo.packageName);
            this.mActivity.startActivityForResult(intent, i);
            return true;
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.AvatarUi
        public int getPhotoSize() {
            return this.mActivity.getResources().getDimensionPixelSize(17105619);
        }
    }

    /* loaded from: classes2.dex */
    static class ContextInjectorImpl implements ContextInjector {
        private final Context mContext;
        private final String mFileAuthority;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ContextInjectorImpl(Context context, String str) {
            this.mContext = context;
            this.mFileAuthority = str;
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.ContextInjector
        public File getCacheDir() {
            return this.mContext.getCacheDir();
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.ContextInjector
        public Uri createTempImageUri(File file, String str, boolean z) {
            File file2 = new File(file, str);
            if (z) {
                file2.delete();
            }
            return FileProvider.getUriForFile(this.mContext, this.mFileAuthority, file2);
        }

        @Override // com.android.settingslib.users.AvatarPhotoController.ContextInjector
        public ContentResolver getContentResolver() {
            return this.mContext.getContentResolver();
        }
    }
}
