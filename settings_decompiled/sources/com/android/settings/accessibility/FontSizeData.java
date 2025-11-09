package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import com.android.settings.R$array;
import com.android.settings.display.ToggleFontSizePreferenceFragment;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
final class FontSizeData extends PreviewSizeData<Float> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public FontSizeData(Context context) {
        super(context);
        Resources resources = getContext().getResources();
        ContentResolver contentResolver = getContext().getContentResolver();
        List asList = Arrays.asList(resources.getStringArray(R$array.entryvalues_font_size));
        setDefaultValue(Float.valueOf(1.0f));
        setInitialIndex(ToggleFontSizePreferenceFragment.fontSizeValueToIndex(Settings.System.getFloat(contentResolver, "font_scale", getDefaultValue().floatValue()), (String[]) asList.toArray(new String[0])));
        setValues((List) asList.stream().map(new Function() { // from class: com.android.settings.accessibility.FontSizeData$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Float.valueOf((String) obj);
            }
        }).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void commit(int i) {
        Settings.System.putFloat(getContext().getContentResolver(), "font_scale", getValues().get(i).floatValue());
    }
}
