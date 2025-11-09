package com.android.settings.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import com.android.settings.utils.AnnotationSpan;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class AnnotationSpan extends URLSpan {
    private final View.OnClickListener mClickListener;

    private AnnotationSpan(View.OnClickListener onClickListener) {
        super((String) null);
        this.mClickListener = onClickListener;
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View view) {
        View.OnClickListener onClickListener = this.mClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setUnderlineText(true);
    }

    public static CharSequence linkify(CharSequence charSequence, LinkInfo... linkInfoArr) {
        SpannableString spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
        for (Annotation annotation : annotationArr) {
            String value = annotation.getValue();
            int spanStart = spannableString.getSpanStart(annotation);
            int spanEnd = spannableString.getSpanEnd(annotation);
            AnnotationSpan annotationSpan = null;
            int length = linkInfoArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                LinkInfo linkInfo = linkInfoArr[i];
                if (linkInfo.mAnnotation.equals(value)) {
                    annotationSpan = new AnnotationSpan(linkInfo.mListener);
                    break;
                }
                i++;
            }
            if (annotationSpan != null) {
                spannableStringBuilder.setSpan(annotationSpan, spanStart, spanEnd, spannableString.getSpanFlags(annotationSpan));
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence textWithoutLink(CharSequence charSequence) {
        final SpannableString spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        if (annotationArr == null) {
            return charSequence;
        }
        Arrays.sort(annotationArr, Comparator.comparingInt(new ToIntFunction() { // from class: com.android.settings.utils.AnnotationSpan$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$textWithoutLink$0;
                lambda$textWithoutLink$0 = AnnotationSpan.lambda$textWithoutLink$0(spannableString, (Annotation) obj);
                return lambda$textWithoutLink$0;
            }
        }));
        StringBuilder sb = new StringBuilder(spannableString.toString());
        for (Annotation annotation : annotationArr) {
            sb.delete(spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation));
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$textWithoutLink$0(SpannableString spannableString, Annotation annotation) {
        return -spannableString.getSpanStart(annotation);
    }

    /* loaded from: classes.dex */
    public static class LinkInfo {
        private final Boolean mActionable;
        private final String mAnnotation;
        private final View.OnClickListener mListener;

        public LinkInfo(String str, View.OnClickListener onClickListener) {
            this.mAnnotation = str;
            this.mListener = onClickListener;
            this.mActionable = Boolean.TRUE;
        }

        public LinkInfo(Context context, String str, final Intent intent) {
            this.mAnnotation = str;
            if (intent != null) {
                this.mActionable = Boolean.valueOf(context.getPackageManager().resolveActivity(intent, 0) != null);
            } else {
                this.mActionable = Boolean.FALSE;
            }
            if (!this.mActionable.booleanValue()) {
                this.mListener = null;
            } else {
                this.mListener = new View.OnClickListener() { // from class: com.android.settings.utils.AnnotationSpan$LinkInfo$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        AnnotationSpan.LinkInfo.lambda$new$0(intent, view);
                    }
                };
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$new$0(Intent intent, View view) {
            try {
                view.startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException unused) {
                Log.w("AnnotationSpan.LinkInfo", "Activity was not found for intent, " + intent);
            }
        }

        public boolean isActionable() {
            return this.mActionable.booleanValue();
        }
    }
}
