package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.android.settings.R$raw;
/* loaded from: classes.dex */
public class FingerprintLocationAnimationVideoView extends TextureView implements FingerprintFindSensorAnimation {
    protected float mAspect;
    protected MediaPlayer mMediaPlayer;

    public FingerprintLocationAnimationVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAspect = 1.0f;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.round(this.mAspect * View.MeasureSpec.getSize(i)), 1073741824));
    }

    protected Uri getFingerprintLocationAnimation() {
        return resourceEntryToUri(getContext(), R$raw.fingerprint_location_animation);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationVideoView.1
            private SurfaceTexture mTextureToDestroy = null;

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                FingerprintLocationAnimationVideoView.this.setVisibility(4);
                Uri fingerprintLocationAnimation = FingerprintLocationAnimationVideoView.this.getFingerprintLocationAnimation();
                MediaPlayer mediaPlayer = FingerprintLocationAnimationVideoView.this.mMediaPlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                SurfaceTexture surfaceTexture2 = this.mTextureToDestroy;
                if (surfaceTexture2 != null) {
                    surfaceTexture2.release();
                    this.mTextureToDestroy = null;
                }
                FingerprintLocationAnimationVideoView fingerprintLocationAnimationVideoView = FingerprintLocationAnimationVideoView.this;
                fingerprintLocationAnimationVideoView.mMediaPlayer = fingerprintLocationAnimationVideoView.createMediaPlayer(((TextureView) fingerprintLocationAnimationVideoView).mContext, fingerprintLocationAnimation);
                MediaPlayer mediaPlayer2 = FingerprintLocationAnimationVideoView.this.mMediaPlayer;
                if (mediaPlayer2 == null) {
                    return;
                }
                mediaPlayer2.setSurface(new Surface(surfaceTexture));
                FingerprintLocationAnimationVideoView.this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationVideoView.1.1
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer3) {
                        mediaPlayer3.setLooping(true);
                    }
                });
                FingerprintLocationAnimationVideoView.this.mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationVideoView.1.2
                    @Override // android.media.MediaPlayer.OnInfoListener
                    public boolean onInfo(MediaPlayer mediaPlayer3, int i3, int i4) {
                        if (i3 == 3) {
                            FingerprintLocationAnimationVideoView.this.setVisibility(0);
                        }
                        return false;
                    }
                });
                FingerprintLocationAnimationVideoView fingerprintLocationAnimationVideoView2 = FingerprintLocationAnimationVideoView.this;
                fingerprintLocationAnimationVideoView2.mAspect = fingerprintLocationAnimationVideoView2.mMediaPlayer.getVideoHeight() / FingerprintLocationAnimationVideoView.this.mMediaPlayer.getVideoWidth();
                FingerprintLocationAnimationVideoView.this.requestLayout();
                FingerprintLocationAnimationVideoView.this.startAnimation();
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                this.mTextureToDestroy = surfaceTexture;
                return false;
            }
        });
    }

    MediaPlayer createMediaPlayer(Context context, Uri uri) {
        return MediaPlayer.create(((TextureView) this).mContext, uri);
    }

    protected static Uri resourceEntryToUri(Context context, int i) {
        Resources resources = context.getResources();
        return Uri.parse("android.resource://" + resources.getResourcePackageName(i) + '/' + resources.getResourceTypeName(i) + '/' + resources.getResourceEntryName(i));
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void startAnimation() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null || mediaPlayer.isPlaying()) {
            return;
        }
        this.mMediaPlayer.start();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void stopAnimation() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void pauseAnimation() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            return;
        }
        this.mMediaPlayer.pause();
    }
}
