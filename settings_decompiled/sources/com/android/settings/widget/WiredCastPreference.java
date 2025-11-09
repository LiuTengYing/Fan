package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class WiredCastPreference extends Preference {
    private int mAnimationId;
    private float mAspectRatio;
    private final Context mContext;
    MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private Uri mVideoPath;

    public WiredCastPreference(Context context) {
        super(context);
        this.mAspectRatio = 1.0f;
        this.mContext = context;
        initialize(context, null);
    }

    public WiredCastPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAspectRatio = 1.0f;
        this.mContext = context;
        initialize(context, attributeSet);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VideoPreference, 0, 0);
        try {
            try {
                int i = this.mAnimationId;
                if (i == 0) {
                    i = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_animation, 0);
                }
                this.mAnimationId = i;
                this.mVideoPath = new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).appendPath(String.valueOf(this.mAnimationId)).build();
            } catch (Exception unused) {
                Log.w("WiredCastPreference", "Animation resource not found. Will not show animation.");
            }
            if (this.mAnimationId == 0) {
                return;
            }
            initMediaPlayer();
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                setVisible(true);
                setLayoutResource(R$layout.wired_cast_preference);
                updateAspectRatio();
            } else {
                setVisible(false);
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((AspectRatioFrameLayout) preferenceViewHolder.findViewById(R$id.cast_video_container)).setAspectRatio(this.mAspectRatio);
        ((TextureView) preferenceViewHolder.findViewById(R$id.cast_texture_view)).setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.android.settings.widget.WiredCastPreference.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                WiredCastPreference wiredCastPreference = WiredCastPreference.this;
                if (wiredCastPreference.mMediaPlayer != null) {
                    wiredCastPreference.mSurface = new Surface(surfaceTexture);
                    WiredCastPreference wiredCastPreference2 = WiredCastPreference.this;
                    wiredCastPreference2.mMediaPlayer.setSurface(wiredCastPreference2.mSurface);
                }
            }
        });
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        releaseMediaPlayer();
        super.onDetached();
    }

    public void setVideo(int i) {
        this.mAnimationId = i;
        releaseMediaPlayer();
        initialize(this.mContext, null);
    }

    private void initMediaPlayer() {
        if (this.mMediaPlayer == null) {
            MediaPlayer create = MediaPlayer.create(this.mContext, this.mVideoPath);
            this.mMediaPlayer = create;
            if (create != null) {
                create.seekTo(0);
                Surface surface = this.mSurface;
                if (surface != null) {
                    this.mMediaPlayer.setSurface(surface);
                }
            }
        }
    }

    private void releaseMediaPlayer() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    void updateAspectRatio() {
        this.mAspectRatio = this.mMediaPlayer.getVideoWidth() / this.mMediaPlayer.getVideoHeight();
    }
}
