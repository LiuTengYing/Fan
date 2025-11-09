package com.android.settings.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.android.settings.widget.VideoPreference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MediaAnimationController implements VideoPreference.AnimationController {
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private boolean mVideoReady;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaAnimationController(Context context, int i) {
        MediaPlayer create = MediaPlayer.create(context, new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).appendPath(String.valueOf(i)).build());
        this.mMediaPlayer = create;
        if (create != null) {
            create.seekTo(0);
            this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() { // from class: com.android.settings.widget.MediaAnimationController$$ExternalSyntheticLambda1
                @Override // android.media.MediaPlayer.OnSeekCompleteListener
                public final void onSeekComplete(MediaPlayer mediaPlayer) {
                    MediaAnimationController.this.lambda$new$0(mediaPlayer);
                }
            });
            this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.widget.MediaAnimationController$$ExternalSyntheticLambda2
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(MediaPlayer mediaPlayer) {
        this.mVideoReady = true;
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoWidth() {
        return this.mMediaPlayer.getVideoWidth();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoHeight() {
        return this.mMediaPlayer.getVideoHeight();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getDuration() {
        return this.mMediaPlayer.getDuration();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void attachView(TextureView textureView, final View view, final View view2) {
        updateViewStates(view, view2);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.android.settings.widget.MediaAnimationController.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                MediaAnimationController.this.setSurface(surfaceTexture);
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                view.setVisibility(0);
                MediaAnimationController.this.mSurface = null;
                return false;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                MediaAnimationController.this.setSurface(surfaceTexture);
                if (MediaAnimationController.this.mVideoReady) {
                    if (view.getVisibility() == 0) {
                        view.setVisibility(8);
                    }
                    if (MediaAnimationController.this.mMediaPlayer != null && !MediaAnimationController.this.mMediaPlayer.isPlaying()) {
                        MediaAnimationController.this.mMediaPlayer.start();
                        view2.setVisibility(8);
                    }
                }
                if (MediaAnimationController.this.mMediaPlayer == null || MediaAnimationController.this.mMediaPlayer.isPlaying() || view2.getVisibility() == 0) {
                    return;
                }
                view2.setVisibility(0);
            }
        });
        textureView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.MediaAnimationController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                MediaAnimationController.this.lambda$attachView$2(view, view2, view3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$attachView$2(View view, View view2, View view3) {
        updateViewStates(view, view2);
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void release() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mVideoReady = false;
        }
    }

    private void updateViewStates(View view, View view2) {
        if (this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            view2.setVisibility(0);
            view.setVisibility(0);
            return;
        }
        view.setVisibility(8);
        view2.setVisibility(8);
        this.mMediaPlayer.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSurface(SurfaceTexture surfaceTexture) {
        if (this.mMediaPlayer == null || this.mSurface != null) {
            return;
        }
        Surface surface = new Surface(surfaceTexture);
        this.mSurface = surface;
        this.mMediaPlayer.setSurface(surface);
    }
}
