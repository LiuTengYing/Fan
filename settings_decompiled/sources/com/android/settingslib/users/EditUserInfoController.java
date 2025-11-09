package com.android.settingslib.users;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import com.android.internal.util.UserIcons;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.drawable.CircleFramedDrawable;
import java.io.File;
import java.util.function.BiConsumer;
/* loaded from: classes2.dex */
public class EditUserInfoController {
    private Dialog mEditUserInfoDialog;
    private EditUserPhotoController mEditUserPhotoController;
    private final String mFileAuthority;
    private Drawable mSavedDrawable;
    private Bitmap mSavedPhoto;
    private boolean mWaitingForActivityResult = false;

    public EditUserInfoController(String str) {
        this.mFileAuthority = str;
    }

    private void clear() {
        EditUserPhotoController editUserPhotoController = this.mEditUserPhotoController;
        if (editUserPhotoController != null) {
            editUserPhotoController.removeNewUserPhotoBitmapFile();
        }
        this.mEditUserInfoDialog = null;
        this.mSavedPhoto = null;
        this.mSavedDrawable = null;
    }

    public void onRestoreInstanceState(Bundle bundle) {
        String string = bundle.getString("pending_photo");
        if (string != null) {
            this.mSavedPhoto = EditUserPhotoController.loadNewUserPhotoBitmap(new File(string));
        }
        this.mWaitingForActivityResult = bundle.getBoolean("awaiting_result", false);
    }

    public void onSaveInstanceState(Bundle bundle) {
        EditUserPhotoController editUserPhotoController;
        File saveNewUserPhotoBitmap;
        if (this.mEditUserInfoDialog != null && (editUserPhotoController = this.mEditUserPhotoController) != null && (saveNewUserPhotoBitmap = editUserPhotoController.saveNewUserPhotoBitmap()) != null) {
            bundle.putString("pending_photo", saveNewUserPhotoBitmap.getPath());
        }
        bundle.putBoolean("awaiting_result", this.mWaitingForActivityResult);
    }

    public void startingActivityForResult() {
        this.mWaitingForActivityResult = true;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        this.mWaitingForActivityResult = false;
        EditUserPhotoController editUserPhotoController = this.mEditUserPhotoController;
        if (editUserPhotoController == null || this.mEditUserInfoDialog == null) {
            return;
        }
        editUserPhotoController.onActivityResult(i, i2, intent);
    }

    public Dialog createDialog(final Activity activity, ActivityStarter activityStarter, Drawable drawable, String str, String str2, BiConsumer<String, Drawable> biConsumer, Runnable runnable) {
        View inflate = LayoutInflater.from(activity).inflate(R$layout.edit_user_info_dialog_content, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(R$id.user_name);
        editText.setText(str);
        ImageView imageView = (ImageView) inflate.findViewById(R$id.user_photo);
        imageView.setImageDrawable(getUserIcon(activity, drawable != null ? drawable : UserIcons.getDefaultUserIcon(activity.getResources(), -10000, false)));
        if (isChangePhotoRestrictedByBase(activity)) {
            inflate.findViewById(R$id.add_a_photo_icon).setVisibility(8);
        } else {
            final RestrictedLockUtils.EnforcedAdmin changePhotoAdminRestriction = getChangePhotoAdminRestriction(activity);
            if (changePhotoAdminRestriction != null) {
                imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.users.EditUserInfoController$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(activity, changePhotoAdminRestriction);
                    }
                });
            } else {
                this.mEditUserPhotoController = createEditUserPhotoController(activity, activityStarter, imageView);
            }
        }
        Dialog buildDialog = buildDialog(activity, inflate, editText, drawable, str, str2, biConsumer, runnable);
        this.mEditUserInfoDialog = buildDialog;
        buildDialog.getWindow().setSoftInputMode(4);
        return this.mEditUserInfoDialog;
    }

    private Drawable getUserIcon(Activity activity, Drawable drawable) {
        Bitmap bitmap = this.mSavedPhoto;
        if (bitmap != null) {
            CircleFramedDrawable circleFramedDrawable = CircleFramedDrawable.getInstance(activity, bitmap);
            this.mSavedDrawable = circleFramedDrawable;
            return circleFramedDrawable;
        }
        return drawable;
    }

    private Dialog buildDialog(Activity activity, View view, final EditText editText, final Drawable drawable, final String str, String str2, final BiConsumer<String, Drawable> biConsumer, final Runnable runnable) {
        return new AlertDialog.Builder(activity).setTitle(str2).setView(view).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.users.EditUserInfoController$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditUserInfoController.this.lambda$buildDialog$1(drawable, editText, str, biConsumer, dialogInterface, i);
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.users.EditUserInfoController$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditUserInfoController.this.lambda$buildDialog$2(runnable, dialogInterface, i);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settingslib.users.EditUserInfoController$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                EditUserInfoController.this.lambda$buildDialog$3(runnable, dialogInterface);
            }
        }).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildDialog$1(Drawable drawable, EditText editText, String str, BiConsumer biConsumer, DialogInterface dialogInterface, int i) {
        EditUserPhotoController editUserPhotoController = this.mEditUserPhotoController;
        Drawable newUserPhotoDrawable = editUserPhotoController != null ? editUserPhotoController.getNewUserPhotoDrawable() : null;
        if (newUserPhotoDrawable != null) {
            drawable = newUserPhotoDrawable;
        }
        String trim = editText.getText().toString().trim();
        if (!trim.isEmpty()) {
            str = trim;
        }
        clear();
        if (biConsumer != null) {
            biConsumer.accept(str, drawable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildDialog$2(Runnable runnable, DialogInterface dialogInterface, int i) {
        clear();
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildDialog$3(Runnable runnable, DialogInterface dialogInterface) {
        clear();
        if (runnable != null) {
            runnable.run();
        }
    }

    boolean isChangePhotoRestrictedByBase(Context context) {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_set_user_icon", UserHandle.myUserId());
    }

    RestrictedLockUtils.EnforcedAdmin getChangePhotoAdminRestriction(Context context) {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_set_user_icon", UserHandle.myUserId());
    }

    EditUserPhotoController createEditUserPhotoController(Activity activity, ActivityStarter activityStarter, ImageView imageView) {
        return new EditUserPhotoController(activity, activityStarter, imageView, this.mSavedPhoto, this.mSavedDrawable, this.mFileAuthority);
    }
}
