package com.android.settings.media;

import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class MediaOutputUtils {
    private static final boolean DEBUG = Log.isLoggable("MediaOutputUtils", 3);

    public static MediaController getActiveLocalMediaController(MediaSessionManager mediaSessionManager) {
        PlaybackState playbackState;
        ArrayList arrayList = new ArrayList();
        MediaController mediaController = null;
        for (MediaController mediaController2 : mediaSessionManager.getActiveSessions(null)) {
            MediaController.PlaybackInfo playbackInfo = mediaController2.getPlaybackInfo();
            if (playbackInfo != null && (playbackState = mediaController2.getPlaybackState()) != null) {
                if (DEBUG) {
                    Log.d("MediaOutputUtils", "getActiveLocalMediaController() package name : " + mediaController2.getPackageName() + ", play back type : " + playbackInfo.getPlaybackType() + ", play back state : " + playbackState.getState());
                }
                if (playbackState.getState() == 3) {
                    if (playbackInfo.getPlaybackType() == 2) {
                        if (mediaController != null && TextUtils.equals(mediaController.getPackageName(), mediaController2.getPackageName())) {
                            mediaController = null;
                        }
                        if (!arrayList.contains(mediaController2.getPackageName())) {
                            arrayList.add(mediaController2.getPackageName());
                        }
                    } else if (playbackInfo.getPlaybackType() == 1 && mediaController == null && !arrayList.contains(mediaController2.getPackageName())) {
                        mediaController = mediaController2;
                    }
                }
            }
        }
        return mediaController;
    }
}
