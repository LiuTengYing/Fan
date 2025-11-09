package com.android.settings.homepage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.core.PreferenceXmlParserUtils;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class HighlightableMenu {
    private static boolean sXmlParsed;
    private static final Map<String, String> MENU_TO_PREFERENCE_KEY_MAP = new ArrayMap();
    private static final Map<String, Integer> MENU_KEY_COMPAT_MAP = new ArrayMap();

    public static synchronized void fromXml(final Context context, int i) {
        synchronized (HighlightableMenu.class) {
            if (sXmlParsed) {
                return;
            }
            Log.d("HighlightableMenu", "parsing highlightable menu from xml");
            try {
                for (Bundle bundle : PreferenceXmlParserUtils.extractMetadata(context, i, 8194)) {
                    String string = bundle.getString("highlightable_menu_key");
                    Log.d("HighlightableMenu", "fromXml: " + string);
                    if (!TextUtils.isEmpty(string)) {
                        String string2 = bundle.getString("key");
                        if (TextUtils.isEmpty(string2)) {
                            Log.w("HighlightableMenu", "Highlightable menu requires android:key but it's missing in xml: " + string);
                        } else if (!"Digital Wellbeing & parental controls".equals(string) && !"Google".equals(string)) {
                            MENU_TO_PREFERENCE_KEY_MAP.put(string, string2);
                        }
                    }
                }
                if (MENU_TO_PREFERENCE_KEY_MAP.isEmpty()) {
                    return;
                }
                sXmlParsed = true;
                MENU_KEY_COMPAT_MAP.forEach(new BiConsumer() { // from class: com.android.settings.homepage.HighlightableMenu$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        HighlightableMenu.lambda$fromXml$0(context, (String) obj, (Integer) obj2);
                    }
                });
            } catch (IOException | XmlPullParserException e) {
                Log.e("HighlightableMenu", "Failed to parse preference xml for getting highlightable menu keys", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$fromXml$0(Context context, String str, Integer num) {
        String lookupPreferenceKey = lookupPreferenceKey(context.getString(num.intValue()));
        if (lookupPreferenceKey != null) {
            MENU_TO_PREFERENCE_KEY_MAP.put(str, lookupPreferenceKey);
        }
    }

    public static synchronized void addMenuKey(String str) {
        synchronized (HighlightableMenu.class) {
            Log.d("HighlightableMenu", "add menu key: " + str);
            MENU_TO_PREFERENCE_KEY_MAP.put(str, str);
        }
    }

    public static String lookupPreferenceKey(String str) {
        return MENU_TO_PREFERENCE_KEY_MAP.get(str);
    }
}
