package com.android.settingslib.datetime;

import android.content.Context;
import android.icu.text.TimeZoneFormat;
import android.icu.text.TimeZoneNames;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import androidx.core.text.BidiFormatter;
import androidx.core.text.TextDirectionHeuristicsCompat;
import com.android.i18n.timezone.CountryTimeZones;
import com.android.i18n.timezone.TimeZoneFinder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes2.dex */
public class ZoneGetter {
    public static CharSequence getTimeZoneOffsetAndName(Context context, TimeZone timeZone, Date date) {
        Locale locale = context.getResources().getConfiguration().locale;
        CharSequence gmtOffsetText = getGmtOffsetText(TimeZoneFormat.getInstance(locale), locale, timeZone, date);
        String zoneLongName = getZoneLongName(TimeZoneNames.getInstance(locale), timeZone, date);
        return zoneLongName == null ? gmtOffsetText : TextUtils.concat(gmtOffsetText, " ", zoneLongName);
    }

    private static String getZoneLongName(TimeZoneNames timeZoneNames, TimeZone timeZone, Date date) {
        return timeZoneNames.getDisplayName(getCanonicalZoneId(timeZone), timeZone.inDaylightTime(date) ? TimeZoneNames.NameType.LONG_DAYLIGHT : TimeZoneNames.NameType.LONG_STANDARD, date.getTime());
    }

    private static String getCanonicalZoneId(TimeZone timeZone) {
        String id = timeZone.getID();
        String canonicalID = android.icu.util.TimeZone.getCanonicalID(id);
        return canonicalID != null ? canonicalID : id;
    }

    private static void appendWithTtsSpan(SpannableStringBuilder spannableStringBuilder, CharSequence charSequence, TtsSpan ttsSpan) {
        int length = spannableStringBuilder.length();
        spannableStringBuilder.append(charSequence);
        spannableStringBuilder.setSpan(ttsSpan, length, spannableStringBuilder.length(), 0);
    }

    private static String formatDigits(int i, int i2, String str) {
        int i3 = i / 10;
        int i4 = i % 10;
        StringBuilder sb = new StringBuilder(i2);
        if (i >= 10 || i2 == 2) {
            sb.append(str.charAt(i3));
        }
        sb.append(str.charAt(i4));
        return sb.toString();
    }

    public static CharSequence getGmtOffsetText(TimeZoneFormat timeZoneFormat, Locale locale, TimeZone timeZone, Date date) {
        String substring;
        String str;
        TimeZoneFormat.GMTOffsetPatternType gMTOffsetPatternType;
        int i;
        String str2;
        int i2;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String gMTPattern = timeZoneFormat.getGMTPattern();
        int indexOf = gMTPattern.indexOf("{0}");
        if (indexOf == -1) {
            str = "GMT";
            substring = "";
        } else {
            String substring2 = gMTPattern.substring(0, indexOf);
            substring = gMTPattern.substring(indexOf + 3);
            str = substring2;
        }
        if (!str.isEmpty()) {
            appendWithTtsSpan(spannableStringBuilder, str, new TtsSpan.TextBuilder(str).build());
        }
        int offset = timeZone.getOffset(date.getTime());
        if (offset < 0) {
            offset = -offset;
            gMTOffsetPatternType = TimeZoneFormat.GMTOffsetPatternType.NEGATIVE_HM;
        } else {
            gMTOffsetPatternType = TimeZoneFormat.GMTOffsetPatternType.POSITIVE_HM;
        }
        String gMTOffsetPattern = timeZoneFormat.getGMTOffsetPattern(gMTOffsetPatternType);
        String gMTOffsetDigits = timeZoneFormat.getGMTOffsetDigits();
        long j = offset;
        int i3 = (int) (j / 3600000);
        int abs = Math.abs((int) (j / 60000)) % 60;
        int i4 = 0;
        while (i4 < gMTOffsetPattern.length()) {
            char charAt = gMTOffsetPattern.charAt(i4);
            if (charAt == '+' || charAt == '-' || charAt == 8722) {
                String valueOf = String.valueOf(charAt);
                appendWithTtsSpan(spannableStringBuilder, valueOf, new TtsSpan.VerbatimBuilder(valueOf).build());
            } else if (charAt == 'H' || charAt == 'm') {
                int i5 = i4 + 1;
                if (i5 >= gMTOffsetPattern.length() || gMTOffsetPattern.charAt(i5) != charAt) {
                    i5 = i4;
                    i = 1;
                } else {
                    i = 2;
                }
                if (charAt == 'H') {
                    str2 = "hour";
                    i2 = i3;
                } else {
                    str2 = "minute";
                    i2 = abs;
                }
                appendWithTtsSpan(spannableStringBuilder, formatDigits(i2, i, gMTOffsetDigits), new TtsSpan.MeasureBuilder().setNumber(i2).setUnit(str2).build());
                i4 = i5;
            } else {
                spannableStringBuilder.append(charAt);
            }
            i4++;
        }
        if (!substring.isEmpty()) {
            appendWithTtsSpan(spannableStringBuilder, substring, new TtsSpan.TextBuilder(substring).build());
        }
        return BidiFormatter.getInstance().unicodeWrap(new SpannableString(spannableStringBuilder), TextUtils.getLayoutDirectionFromLocale(locale) == 1 ? TextDirectionHeuristicsCompat.RTL : TextDirectionHeuristicsCompat.LTR);
    }

    /* loaded from: classes2.dex */
    public static final class ZoneGetterData {
        public List<String> lookupTimeZoneIdsByCountry(String str) {
            CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str);
            if (lookupCountryTimeZones == null) {
                return null;
            }
            return extractTimeZoneIds(lookupCountryTimeZones.getTimeZoneMappings());
        }

        private static List<String> extractTimeZoneIds(List<CountryTimeZones.TimeZoneMapping> list) {
            ArrayList arrayList = new ArrayList(list.size());
            for (CountryTimeZones.TimeZoneMapping timeZoneMapping : list) {
                arrayList.add(timeZoneMapping.getTimeZoneId());
            }
            return Collections.unmodifiableList(arrayList);
        }
    }
}
