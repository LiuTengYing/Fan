package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.biometrics.face.FaceStatusPreferenceController;
import com.android.settings.homepage.contextualcards.FaceReEnrollDialog;
import com.android.settings.security.SecuritySettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;
/* loaded from: classes.dex */
public class FaceSetupSlice implements CustomSliceable {
    private final Context mContext;
    private FaceManager mFaceManager;

    public FaceSetupSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        CharSequence text;
        CharSequence text2;
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(this.mContext);
        this.mFaceManager = faceManagerOrNull;
        if (faceManagerOrNull == null) {
            return new ListBuilder(this.mContext, CustomSliceRegistry.FACE_ENROLL_SLICE_URI, -1L).setIsError(true).build();
        }
        int myUserId = UserHandle.myUserId();
        boolean hasEnrolledTemplates = this.mFaceManager.hasEnrolledTemplates(myUserId);
        int reEnrollSetting = getReEnrollSetting(this.mContext, myUserId);
        if (!hasEnrolledTemplates) {
            text = this.mContext.getText(R$string.security_settings_face_settings_enroll);
            text2 = this.mContext.getText(R$string.security_settings_face_settings_context_subtitle);
        } else if (reEnrollSetting == 1) {
            text = this.mContext.getText(R$string.security_settings_face_enroll_should_re_enroll_title);
            text2 = this.mContext.getText(R$string.security_settings_face_enroll_should_re_enroll_subtitle);
        } else if (reEnrollSetting == 3) {
            text = this.mContext.getText(R$string.security_settings_face_enroll_must_re_enroll_title);
            text2 = this.mContext.getText(R$string.security_settings_face_enroll_must_re_enroll_subtitle);
        } else {
            return new ListBuilder(this.mContext, CustomSliceRegistry.FACE_ENROLL_SLICE_URI, -1L).setIsError(true).build();
        }
        return new ListBuilder(this.mContext, CustomSliceRegistry.FACE_ENROLL_SLICE_URI, -1L).setAccentColor(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext)).addRow(buildRowBuilder(text, text2, IconCompat.createWithResource(this.mContext, R$drawable.ic_face_24dp), this.mContext, getIntent())).build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.FACE_ENROLL_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        if (!this.mFaceManager.hasEnrolledTemplates(UserHandle.myUserId())) {
            return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, SecuritySettings.class.getName(), FaceStatusPreferenceController.KEY_FACE_SETTINGS, this.mContext.getText(R$string.security_settings_face_settings_enroll).toString(), 1401, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        }
        return new Intent(this.mContext, FaceReEnrollDialog.class);
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_security;
    }

    private static ListBuilder.RowBuilder buildRowBuilder(CharSequence charSequence, CharSequence charSequence2, IconCompat iconCompat, Context context, Intent intent) {
        return new ListBuilder.RowBuilder().setTitleItem(iconCompat, 0).setTitle(charSequence).setSubtitle(charSequence2).setPrimaryAction(SliceAction.createDeeplink(PendingIntent.getActivity(context, 0, intent, 67108864), iconCompat, 0, charSequence));
    }

    public static int getReEnrollSetting(Context context, int i) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "face_unlock_re_enroll", 0, i);
    }
}
