package com.google.android.setupdesign.template;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.template.Mixin;
/* loaded from: classes2.dex */
public class RequireScrollMixin implements Mixin {
    private ScrollHandlingDelegate delegate;
    private OnRequireScrollStateChangedListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean requiringScrollToBottom = false;
    private boolean everScrolledToBottom = false;

    /* loaded from: classes2.dex */
    public interface OnRequireScrollStateChangedListener {
        void onRequireScrollStateChanged(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface ScrollHandlingDelegate {
        void pageScrollDown();

        void startListening();
    }

    public RequireScrollMixin(TemplateLayout templateLayout) {
    }

    public void setScrollHandlingDelegate(ScrollHandlingDelegate scrollHandlingDelegate) {
        this.delegate = scrollHandlingDelegate;
    }

    public void setOnRequireScrollStateChangedListener(OnRequireScrollStateChangedListener onRequireScrollStateChangedListener) {
        this.listener = onRequireScrollStateChangedListener;
    }

    public View.OnClickListener createOnClickListener(final View.OnClickListener onClickListener) {
        return new View.OnClickListener() { // from class: com.google.android.setupdesign.template.RequireScrollMixin.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (RequireScrollMixin.this.requiringScrollToBottom) {
                    RequireScrollMixin.this.delegate.pageScrollDown();
                    return;
                }
                View.OnClickListener onClickListener2 = onClickListener;
                if (onClickListener2 != null) {
                    onClickListener2.onClick(view);
                }
            }
        };
    }

    public void requireScrollWithButton(Context context, FooterButton footerButton, int i, View.OnClickListener onClickListener) {
        requireScrollWithButton(footerButton, context.getText(i), onClickListener);
    }

    public void requireScrollWithButton(final FooterButton footerButton, final CharSequence charSequence, View.OnClickListener onClickListener) {
        final CharSequence text = footerButton.getText();
        footerButton.setOnClickListener(createOnClickListener(onClickListener));
        setOnRequireScrollStateChangedListener(new OnRequireScrollStateChangedListener() { // from class: com.google.android.setupdesign.template.RequireScrollMixin.4
            @Override // com.google.android.setupdesign.template.RequireScrollMixin.OnRequireScrollStateChangedListener
            public void onRequireScrollStateChanged(boolean z) {
                footerButton.setText(z ? charSequence : text);
            }
        });
        requireScroll();
    }

    public void requireScroll() {
        this.delegate.startListening();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyScrollabilityChange(boolean z) {
        if (z == this.requiringScrollToBottom) {
            return;
        }
        if (z) {
            if (this.everScrolledToBottom) {
                return;
            }
            postScrollStateChange(true);
            this.requiringScrollToBottom = true;
            return;
        }
        postScrollStateChange(false);
        this.requiringScrollToBottom = false;
        this.everScrolledToBottom = true;
    }

    private void postScrollStateChange(final boolean z) {
        this.handler.post(new Runnable() { // from class: com.google.android.setupdesign.template.RequireScrollMixin.5
            @Override // java.lang.Runnable
            public void run() {
                if (RequireScrollMixin.this.listener != null) {
                    RequireScrollMixin.this.listener.onRequireScrollStateChanged(z);
                }
            }
        });
    }
}
