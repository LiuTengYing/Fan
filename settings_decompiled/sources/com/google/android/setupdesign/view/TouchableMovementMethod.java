package com.google.android.setupdesign.view;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;
/* loaded from: classes2.dex */
public interface TouchableMovementMethod {
    MotionEvent getLastTouchEvent();

    boolean isLastTouchEventHandled();

    /* loaded from: classes2.dex */
    public static class TouchableLinkMovementMethod extends LinkMovementMethod implements TouchableMovementMethod {
        MotionEvent lastEvent;
        boolean lastEventResult = false;

        public static TouchableLinkMovementMethod getInstance() {
            return new TouchableLinkMovementMethod();
        }

        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            this.lastEvent = motionEvent;
            boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
            if (motionEvent.getAction() == 0) {
                this.lastEventResult = Selection.getSelectionStart(spannable) != -1;
            } else {
                this.lastEventResult = onTouchEvent;
            }
            return onTouchEvent;
        }

        @Override // com.google.android.setupdesign.view.TouchableMovementMethod
        public MotionEvent getLastTouchEvent() {
            return this.lastEvent;
        }

        @Override // com.google.android.setupdesign.view.TouchableMovementMethod
        public boolean isLastTouchEventHandled() {
            return this.lastEventResult;
        }
    }
}
