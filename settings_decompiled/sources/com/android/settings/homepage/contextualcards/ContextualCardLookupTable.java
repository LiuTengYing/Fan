package com.android.settings.homepage.contextualcards;

import android.util.Log;
import com.android.settings.homepage.contextualcards.ContextualCardLookupTable;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardController;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.conditional.ConditionFooterContextualCardRenderer;
import com.android.settings.homepage.contextualcards.conditional.ConditionHeaderContextualCardRenderer;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardController;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardController;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ContextualCardLookupTable {
    static final Set<ControllerRendererMapping> LOOKUP_TABLE = new TreeSet<ControllerRendererMapping>() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLookupTable.1
        {
            add(new ControllerRendererMapping(3, ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH, ConditionContextualCardController.class, ConditionContextualCardRenderer.class));
            add(new ControllerRendererMapping(3, ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH, ConditionContextualCardController.class, ConditionContextualCardRenderer.class));
            add(new ControllerRendererMapping(2, LegacySuggestionContextualCardRenderer.VIEW_TYPE, LegacySuggestionContextualCardController.class, LegacySuggestionContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, SliceContextualCardRenderer.VIEW_TYPE_FULL_WIDTH, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, SliceContextualCardRenderer.VIEW_TYPE_STICKY, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(5, ConditionFooterContextualCardRenderer.VIEW_TYPE, ConditionContextualCardController.class, ConditionFooterContextualCardRenderer.class));
            add(new ControllerRendererMapping(4, ConditionHeaderContextualCardRenderer.VIEW_TYPE, ConditionContextualCardController.class, ConditionHeaderContextualCardRenderer.class));
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ControllerRendererMapping implements Comparable<ControllerRendererMapping> {
        final int mCardType;
        final Class<? extends ContextualCardController> mControllerClass;
        final Class<? extends ContextualCardRenderer> mRendererClass;
        final int mViewType;

        ControllerRendererMapping(int i, int i2, Class<? extends ContextualCardController> cls, Class<? extends ContextualCardRenderer> cls2) {
            this.mCardType = i;
            this.mViewType = i2;
            this.mControllerClass = cls;
            this.mRendererClass = cls2;
        }

        @Override // java.lang.Comparable
        public int compareTo(ControllerRendererMapping controllerRendererMapping) {
            return Comparator.comparingInt(new ToIntFunction() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLookupTable$ControllerRendererMapping$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int i;
                    i = ((ContextualCardLookupTable.ControllerRendererMapping) obj).mCardType;
                    return i;
                }
            }).thenComparingInt(new ToIntFunction() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLookupTable$ControllerRendererMapping$$ExternalSyntheticLambda1
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int i;
                    i = ((ContextualCardLookupTable.ControllerRendererMapping) obj).mViewType;
                    return i;
                }
            }).compare(this, controllerRendererMapping);
        }
    }

    public static Class<? extends ContextualCardController> getCardControllerClass(int i) {
        for (ControllerRendererMapping controllerRendererMapping : LOOKUP_TABLE) {
            if (controllerRendererMapping.mCardType == i) {
                return controllerRendererMapping.mControllerClass;
            }
        }
        return null;
    }

    public static Class<? extends ContextualCardRenderer> getCardRendererClassByViewType(final int i) throws IllegalStateException {
        List list = (List) LOOKUP_TABLE.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLookupTable$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getCardRendererClassByViewType$0;
                lambda$getCardRendererClassByViewType$0 = ContextualCardLookupTable.lambda$getCardRendererClassByViewType$0(i, (ContextualCardLookupTable.ControllerRendererMapping) obj);
                return lambda$getCardRendererClassByViewType$0;
            }
        }).collect(Collectors.toList());
        if (list == null || list.isEmpty()) {
            Log.w("ContextualCardLookup", "No matching mapping");
            return null;
        } else if (list.size() != 1) {
            throw new IllegalStateException("Have duplicate VIEW_TYPE in lookup table.");
        } else {
            return ((ControllerRendererMapping) list.get(0)).mRendererClass;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getCardRendererClassByViewType$0(int i, ControllerRendererMapping controllerRendererMapping) {
        return controllerRendererMapping.mViewType == i;
    }
}
