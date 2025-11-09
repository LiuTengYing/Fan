package com.android.settings.deviceinfo;

import android.text.SpannableStringBuilder;
import android.text.style.TtsSpan;
import java.util.function.IntPredicate;
/* loaded from: classes.dex */
public class PhoneNumberUtil {
    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isPhoneNumberDigit(int i) {
        return (i >= 48 && i <= 57) || i == 45 || i == 43 || i == 40 || i == 41;
    }

    public static CharSequence expandByTts(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() <= 0 || !isPhoneNumberDigits(charSequence)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.setSpan(new TtsSpan.DigitsBuilder(charSequence.toString()).build(), 0, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    private static boolean isPhoneNumberDigits(CharSequence charSequence) {
        return ((long) charSequence.length()) == charSequence.chars().filter(new IntPredicate() { // from class: com.android.settings.deviceinfo.PhoneNumberUtil$$ExternalSyntheticLambda0
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                boolean isPhoneNumberDigit;
                isPhoneNumberDigit = PhoneNumberUtil.isPhoneNumberDigit(i);
                return isPhoneNumberDigit;
            }
        }).count();
    }
}
